package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.MessageListPacket;
import aoop.asteroids.udp.packets.PlayerLosePacket;
import aoop.asteroids.udp.packets.PlayerJoinPacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;
import aoop.asteroids.udp.packets.RoundEndPacket;


public class Server extends Base implements Observer{
	
	public static int UDPPort = 8090;
	
	/**
	 * If a client takes longer than this to send a packet, the connection to that client will be considered disconnected.
	 */
	 
	/**
	 * Longer than what?
	 */
	
	private CopyOnWriteArrayList<ClientConnection> spectatorConnections = new CopyOnWriteArrayList<ClientConnection>();
	private CopyOnWriteArrayList<ClientConnection> playerConnections = new CopyOnWriteArrayList<ClientConnection>();
	
	
	
	private boolean singlePlayerMode = false;
	private int roundNumber = 0;
	
	private Game game;
	
	private DatagramSocket sendSocket;
	
	
	public Server(boolean isSinglePlayer) throws SocketException{
		super();
		
		this.sendSocket = new DatagramSocket(Server.UDPPort);

		try {
			this.responsesThread = new ServerReciever(this, Server.UDPPort, this.sendSocket);
			this.responsesThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.singlePlayerMode = isSinglePlayer;
		if(this.isSinglePlayerMode()){
			++roundNumber;
		}
		startFirstGame();
	}
	
	public void startFirstGame(){
		this.game = new Game(this.isSinglePlayerMode(),roundNumber);
		game.addObserver(this);
		Thread t = new Thread (game);
		t.start();
	}
	
	
	public void update(Observable o, Object arg){
		this.sendGameStatePacket();
		this.sendMessageListPacket();
		this.tagNonrespondingClients();
		this.destroyAllShipsOfDisconnectedPlayers();
		if (this.game.isGameOver()){
			this.restartGame();
		}
	}
	
	public void addSpectatorConnection(JSONObject packetData, DatagramPacket packet){
		addConnection(spectatorConnections, packetData, packet);
		//sendMessagePacket("New Spectator Connected"+spectatorConnections.get(spectatorConnections.size()-1).toString());
		this.game.addMessage("New Spectator Connected"+spectatorConnections.get(spectatorConnections.size()-1).toString());
	}
	public void addPlayerConnection(JSONObject packetData, DatagramPacket packet){
		
		//In single-player mode, reject more than one connection, and also all connections that are not from the current computer.
		if(this.isSinglePlayerMode() && (getPlayerConnections().size() > 0 /*|| packet.getAddress().getHostAddress() != "127.0.0.1"*/)){
			return;
		}
		
		
		
		String name = PlayerJoinPacket.decodePacket((JSONArray)packetData.get("d"));
		
		boolean nameExists = false;
		for(ClientConnection c : this.getPlayerConnections()){
			if(c.getName().equals(name)){
				nameExists = true;
				break;
			}
		}
		
		if(nameExists){
			this.game.addMessage("Duplicate Player `"+name+"` was added as Spectator instead.");
			this.addSpectatorConnection(packetData, packet);
		}else{
			addConnection(getPlayerConnections(), packetData, packet);
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
	
	public void addConnection(List<ClientConnection> list, JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		
		
		ClientConnection c = new ClientConnection((InetSocketAddress)packet.getSocketAddress());
		c.setName(PlayerJoinPacket.decodePacket((JSONArray)packetData.get("d")));
		c.setLastPingTime(System.currentTimeMillis());
		c.updateLastPacketId(packetId);
		
		list.add(c);
		
	}
	
	public void sendGameStatePacket(){
		
		GameStatePacket gameStatePacket = new GameStatePacket(
				this.roundNumber,
				game.getSpaceships(),
				game.getBullets(),
				game.getAsteroids(),
				game.getExplosions());
		
		try {
			sendPacketToAll(gameStatePacket.toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPlayerLosePacket(int ship_index){
		if(ship_index < 0 || ship_index > getPlayerConnections().size()){
			return;
		}
		
		InetSocketAddress connection = getPlayerConnections().get(ship_index).getSocketAddress();
		
		PlayerLosePacket playerLosePacket = new PlayerLosePacket();
		try {
			super.sendPacket(playerLosePacket.toJsonString(), connection.getAddress(), connection.getPort(), sendSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRoundOverPacket(){
		try {
			sendPacketToAll(new RoundEndPacket().toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendPacketToAll(String packet_string) throws IOException{
		for(ClientConnection connection : getPlayerConnections()){
			super.sendPacket(packet_string, connection.getAddress(), connection.getPort(), sendSocket);
		}
		for(ClientConnection connection : spectatorConnections){
			super.sendPacket(packet_string, connection.getAddress(), connection.getPort(), sendSocket);
		}
	}

	public void updatePlayerShip(JSONArray packet_data, SocketAddress socketAddress) {
		Logging.LOGGER.fine(socketAddress.toString());
		int index = getPlayerConnections().indexOf(new ClientConnection((InetSocketAddress)socketAddress));
		if(index == -1){
			return;
		}
		Spaceship playerShip = this.game.getSpaceshipRef(index);
		if(playerShip==null){
			return;
		}
 		Logging.LOGGER.fine(playerShip.toString());
		PlayerUpdatePacket.decodePacket(packet_data, playerShip);
	}
	
	public ClientConnection findClientConnection(SocketAddress socketAddress){
		int index = this.playerConnections.indexOf(new ClientConnection((InetSocketAddress)socketAddress));
		if(index == -1){
			return null;
		}
		return this.playerConnections.get(index);
	}
	

	
	public void sendMessageListPacket(){
		try {
			this.sendPacketToAll(new MessageListPacket(this.game.getMessages()).toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void restartGame(){
		++roundNumber;
		List<Spaceship> spaceships = (List<Spaceship>) this.game.getSpaceships();
		this.game.deleteObserver(this);
		this.game = new Game(this.isSinglePlayerMode(), roundNumber); //DONE: Rename Lobby
		this.game.addObserver(this);
		for(int i=this.playerConnections.size()-1;i>=0;i--){
			ClientConnection c = playerConnections.get(i);
			if(c.isDisconnected()){
				playerConnections.remove(i);
				spaceships.remove(i);
			}
		}
		this.game.addSpaceships(spaceships);
		
		Thread t = new Thread (game);
		t.start();
	}
	
	
	
	/** 
	 * Clients that did not say anything for longer than 5 seconds are considered to be `dead` and are to be removed from the game.
	 * */
	public void tagNonrespondingClients(){
		int amountOfDisconnectedClients = 0;
		
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
			//Return to main menu.
			this.game.addMessage("Connections with all other players lost. ");
			this.game.addMessage("Waiting for new players... ");
			
			/*//Find the local connection
			ClientConnection myLocalConnection = null;
			for(ClientConnection c : getPlayerConnections()){
				if(c.getAddress().toString() == "127.0.0.1"){
					myLocalConnection = c;
				}
			}
			
			this.playerConnections = new ArrayList<ClientConnection>();
			if(myLocalConnection != null){
				this.playerConnections.add(myLocalConnection);
			}
			
			this.roundNumber = 0;*/
			List<Spaceship> spaceships = (List<Spaceship>) this.game.getSpaceships();
			List<ClientConnection> pcs = this.getPlayerConnections();
			for(int i=pcs.size()-1;i>=0;i--){
				ClientConnection c = pcs.get(i);
				if(c.isDisconnected()){
					pcs.remove(i);
					spaceships.remove(i);
				}
			}
			this.roundNumber = 0;
			this.game = new Game(this.isSinglePlayerMode(),0);
			this.game.addSpaceships(spaceships);
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
	
	public void updateConnectionData(JSONObject packetData, DatagramPacket packet){
		
		long packetId = ((Long) packetData.get("r"));
		
		ClientConnection c = this.findClientConnection(packet.getSocketAddress());
		c.setLastPingTime(System.currentTimeMillis());
		c.updateLastPacketId(packetId);
	}
	
	public boolean checkIfLatestPacket(JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		ClientConnection c = this.findClientConnection(packet.getSocketAddress());
		return c.getLastPacketId() < packetId;
	}

	public List<ClientConnection> getPlayerConnections() {
		/*List <ClientConnection> pcs = new ArrayList <> ();
		for (ClientConnection pc : this.playerConnections) {
			pcs.add (pc.clone ());
		}
		return pcs;*/
		return this.playerConnections;
	}
	
	public List<ClientConnection> getSpectatorConnections() {
		/*List <ClientConnection> pcs = new ArrayList <> ();
		for (ClientConnection pc : this.spectatorConnections) {
			pcs.add (pc.clone ());
		}
		return pcs;*/
		return this.spectatorConnections;
	}
	

	public boolean isSinglePlayerMode() {
		return singlePlayerMode;
	}
	
	public void stopServer(){
		this.game.abort();
		this.responsesThread.stopServer();
// 		this.sendSocket.close();
	}
	
	private void destroyAllShipsOfDisconnectedPlayers(){
		List<ClientConnection> playerConnections = this.getPlayerConnections();
		List<Spaceship> ships = this.game.getSpaceships();
		for(int i=0; i<playerConnections.size(); i++){
			if( playerConnections.get(i).isDisconnected()){
				this.game.destroySpaceship(i);
			}
		}
		
	}


}
