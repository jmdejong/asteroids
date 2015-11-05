package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import aoop.asteroids.model.game.ServerGame;
import aoop.asteroids.model.gameobjects.Spaceship;

import aoop.asteroids.udp.packets.PlayerJoinPacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;


public class Server extends Base implements Observer{
	
	public static int UDPPort = 8800;
	

	
	private CopyOnWriteArrayList<ClientConnection> spectatorConnections = new CopyOnWriteArrayList<ClientConnection>();
	private CopyOnWriteArrayList<ClientConnection> playerConnections = new CopyOnWriteArrayList<ClientConnection>();
	
	
	
	private boolean singlePlayerMode = false;
	private int roundNumber = 0;
	
	private ServerGame game;
	
	private ServerSender sender;
	
	
	
	public Server(boolean isSinglePlayer) throws SocketException{
		super();
		
		
		DatagramSocket connectionSocket = new DatagramSocket(Server.UDPPort);
		this.sender = new ServerSender(connectionSocket);

		try {
			this.reciever = new ServerReceiver(this, connectionSocket);
			this.reciever.start();
		} catch (SocketException e) {
			Logging.LOGGER.severe("Unable to use server socket. This port is possibly in use already. "+e.getMessage());
		}
		this.singlePlayerMode = isSinglePlayer;
		if(this.isSinglePlayerMode()){
			++roundNumber;
		}
		startFirstGame();
	}
	
	public void startFirstGame(){
		this.game = new ServerGame(isSinglePlayerMode(),roundNumber);
		game.addObserver(this);
		Thread t = new Thread (game);
		t.start();
	}
	
	
	public void update(Observable o, Object arg){
		
		
		Logging.LOGGER.warning(this.playerConnections.toString());
		Logging.LOGGER.warning(this.spectatorConnections.toString());
		
		synchronized(this.game){
			this.sender.sendGameStatePacket(this.roundNumber,
					this.game.getSpaceships(),
					this.game.getBullets(),
					this.game.getAsteroids(),
					this.game.getExplosions(),
					this.getPlayerConnections(),
					this.getSpectatorConnections());
			this.sender.sendMessageListPacket(this.game.getMessages(),this.getPlayerConnections(), this.getSpectatorConnections());
			this.tagNonrespondingClients();
			this.destroyAllShipsOfDisconnectedPlayers();
			if (this.game.hasEnded()){
				this.restartGame();
			}
		}
	}
	
	public synchronized void addSpectatorConnection(JSONObject packetData, DatagramPacket packet){
		
		ClientConnection c = new ClientConnection((InetSocketAddress)packet.getSocketAddress());
		
		if(this.spectatorConnections.contains(c)){
			return;
		}else{
			c = addConnection(this.spectatorConnections, packetData, packet);
			
			
			synchronized (this.game){
				this.game.addMessage("New Spectator Connected: "+c.toString());
			}
		}
		
	}
	public synchronized void addPlayerConnection(JSONObject packetData, DatagramPacket packet){
		
		//In single-player mode, reject more than one connection.
		if(this.isSinglePlayerMode() && (getPlayerConnections().size() > 0)){
			return;
		}
		
		
		InetSocketAddress address = (InetSocketAddress)packet.getSocketAddress();
		for(ClientConnection c : this.getPlayerConnections()){
			if(c.getSocketAddress().equals(address)){
				return;
			}
		}
		
		String name = PlayerJoinPacket.decodePacket((JSONArray)packetData.get("d"));
		
		boolean nameExists = false;
		for(ClientConnection c : this.getPlayerConnections()){
			if(c.getName().equals(name)){
				nameExists = true;
				break;
			}
		}
		
		
		synchronized(this.game){
			if(nameExists){
				this.game.addMessage("Duplicate Player `"+name+"` was added as Spectator instead.");
				this.addSpectatorConnection(packetData, packet);
			}else{
				ClientConnection c = addConnection(getPlayerConnections(), packetData, packet);
				c.setName(PlayerJoinPacket.decodePacket((JSONArray)packetData.get("d")));
				this.game.addSpaceship(name, !isSinglePlayerMode());
			}
				
			if(getPlayerConnections().size() - countDisconnectedPlayers() == 1){
				if(isSinglePlayerMode()){
					this.game.addMessage("Singleplayer Game Started");
				}else{
					this.game.addMessage("Local Client⟷Host connection made.");

					this.game.addMessage("Waiting for at least one more Player");
				}
			}else{
				this.game.addMessage("New Player Connected: "+name);
			}
		}
		

	}
	
	public synchronized ClientConnection addConnection(Collection<ClientConnection> list, JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		ClientConnection c = new ClientConnection((InetSocketAddress)packet.getSocketAddress());
		c.setLastPingTime(System.currentTimeMillis());
		c.updateLastPacketId(packetId);
		
		list.add(c);
		return c;
	}
	
	
	public void updatePlayerShip(JSONArray packet_data, SocketAddress socketAddress) {
		Logging.LOGGER.fine(socketAddress.toString());
		int index = getPlayerConnections().indexOf(new ClientConnection((InetSocketAddress)socketAddress));
		if(index == -1){
			return;
		}
		synchronized(this.game) {
			Spaceship playerShip = this.game.getSpaceshipRef(index);
			if(playerShip==null){
				return;
			}
			Logging.LOGGER.fine(playerShip.toString());
			PlayerUpdatePacket.decodePacket(packet_data, playerShip);
		}
	}
	
	public ClientConnection findConnection(SocketAddress socketAddress){
		int index = this.playerConnections.indexOf(new ClientConnection((InetSocketAddress)socketAddress));
		if(index != -1){
			return this.playerConnections.get(index);
		}else{
			index = this.spectatorConnections.indexOf(new ClientConnection((InetSocketAddress)socketAddress));
			if(index != -1){
				return this.spectatorConnections.get(index);
			}else{
				return null;
			}
		}
		
	}
	
	

	
	
	
	public void restartGame(){
		if(this.isSinglePlayerMode() && game.areAllShipsDestroyed()){
			//Restart game at round 1 if single player crashed their ship.
			roundNumber = 1;
		}else{
			++roundNumber;
		}
		List<Spaceship> spaceships = (List<Spaceship>) this.game.getSpaceships();
		this.game.deleteObserver(this);
		this.game = new ServerGame(this.isSinglePlayerMode(), roundNumber); //DONE: Rename Lobby
		this.game.addObserver(this);
		for(int i=this.playerConnections.size()-1;i>=0;i--){
			ClientConnection c = playerConnections.get(i);
			if(c.isDisconnected()){
				playerConnections.remove(i);
				spaceships.remove(i);
			}
		}
		this.game.setSpaceships(spaceships, true);
		
		this.game.addMessage("Go!",2000);
		Thread t = new Thread (game);
		t.start();
	}
	
	
	
	/** 
	 * Clients that did not say anything for longer than 5 seconds are considered to be `dead` and are to be removed from the game.
	 * */
	public void tagNonrespondingClients(){
		int amountOfDisconnectedClients = 0;
		
		for(ClientConnection c : getSpectatorConnections()){
			c.tagAsDisconnectedIfNotResponding();
			if(c.isDisconnected()){
				this.spectatorConnections.remove(c);
				this.game.addMessage("Spectator "+c.toString()+" Left");
			}
		}
		
		for(ClientConnection c : getPlayerConnections()){
			if(c.isDisconnected()){
				amountOfDisconnectedClients+=1;
				continue;
			}
			c.tagAsDisconnectedIfNotResponding();
			Logging.LOGGER.fine(c.toDebugString());
			if(c.isDisconnected()){
				this.game.addMessage("Connection Lost with: "+c.getName());
			}
		}
		
		if(getPlayerConnections().size() > 1 &&  getPlayerConnections().size() - amountOfDisconnectedClients <= 1){
			
			List<Spaceship> spaceships = (List<Spaceship>) this.game.getSpaceships();
			List<ClientConnection> pcs = this.getPlayerConnections();
			for(int i=pcs.size()-1;i>=0;i--){
				ClientConnection c = pcs.get(i);
				if(c.isDisconnected()){
					pcs.remove(i);
					spaceships.remove(i);
				}else{
					spaceships.get(i).destroy();
				}
			}
			this.roundNumber = 0;
			this.game.deleteObserver(this);
			this.game = new ServerGame(this.isSinglePlayerMode(),0);
			this.game.setSpaceships(spaceships, false);
			this.game.addObserver(this);
			this.game.addMessage("Connections with all other players lost. ");
			this.game.addMessage("Waiting for new players... ");
			Logging.LOGGER.fine("Restarting Game in lobby mode. Waiting for more players");
			Thread t = new Thread (game);
			t.start();
			
		}
	}
	
	public int countDisconnectedPlayers(){
	
		int amountOfDisconnectedClients = 0;
		for(ClientConnection c : getPlayerConnections()){
			if(c.isDisconnected()){
				amountOfDisconnectedClients+=1;
			}
		}
		return amountOfDisconnectedClients;
	}
	
	public void updatePlayerConnectionData(JSONObject packetData, DatagramPacket packet){
		
		
		ClientConnection c = this.findConnection(packet.getSocketAddress());
		updateConnectionData(c, packetData, packet);
	}
	
	public void updateSpectatorConnectionData(JSONObject packetData, DatagramPacket packet){
		
		ClientConnection c = this.findConnection(packet.getSocketAddress());
		
		updateConnectionData(c, packetData, packet);
	}
	
	public void updateConnectionData(ClientConnection c, JSONObject packetData, DatagramPacket packet){
		if(c == null){
			return;
		}
		
		long packetId = ((Long) packetData.get("r"));
		
		c.setLastPingTime(System.currentTimeMillis());
		c.updateLastPacketId(packetId);
	}
	
	public boolean checkIfLatestPacket(JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		ClientConnection c = this.findConnection(packet.getSocketAddress());
		return c.getLastPacketId() < packetId;
	}

	public List<ClientConnection> getPlayerConnections() {
		return this.playerConnections;
	}
	
	public List<ClientConnection> getSpectatorConnections() {
		return this.spectatorConnections;
	}
	

	public boolean isSinglePlayerMode() {
		return singlePlayerMode;
	}
	
	public void stopServer(){
		this.game.abort();
		this.reciever.stopReciever();
	}
	
	private void destroyAllShipsOfDisconnectedPlayers(){
		List<ClientConnection> playerConnections = this.getPlayerConnections();
		for(int i=0; i<playerConnections.size(); i++){
			if( playerConnections.get(i).isDisconnected()){
				this.game.destroySpaceship(i);
			}
		}
		
	}


}
