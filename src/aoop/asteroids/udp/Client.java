package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

import org.json.simple.JSONObject;

import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.model.*;

public class Client extends Base implements Observer{
	
	/* TODO:
	 * - maybe make a new class (Player, for example") that takes care of the clients on player (spaceshipController, hasLost etc...)
	 *   this player object should have a reference to game
	 *   or maybe we can just move this to spaceshipController
	 * DONE:
	 * - find out why message "connection with host lost" shows up at the beginning and fix
	 */
	
	//private InetSocketAddress serverAddress;
	
	
	private SpaceshipController spaceshipController;
	
	private ClientGame game;
	
	public static int UDPPort = 8091;
	
	/** if set to true, Client is in Spectator mode, and will not send any input packets.*/
	private boolean isSpectator = false;
	
	private String playerName;
	
	private boolean hasConnected = false;
	
	private long lastPingTime = 0;
	private long lastPacketId = 0;
	private long lastConnectionCheckTime = 0;
	
	protected ClientSender sender;
	
	
	public Client(String host, int port, boolean isSpectator, String playerName){
		super();
		
		Logging.LOGGER.fine("New Client made.");
		
		
		DatagramSocket connectionSocket = createSocketOnFirstUnusedPort();
		
		this.isSpectator = isSpectator;
		
		this.playerName = playerName;
		
		this.sender = new ClientSender(new InetSocketAddress(host, port), connectionSocket);
		
		this.game = new ClientGame();
		game.addObserver(this);
		
		
		
		if(!isSpectator){
			this.spaceshipController = new SpaceshipController();
		}
		
		Thread t = new Thread (game);
		t.start();
		
		this.game.addMessage("Connecting to Host...");
		
		try {
			this.reciever =  new ClientReciever(this, connectionSocket);
			this.reciever.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	private DatagramSocket createSocketOnFirstUnusedPort(){
		int port = Client.UDPPort;
		while(port < Client.UDPPort + 100){
			try{
				return new DatagramSocket(port);
			}catch(SocketException b){
				port++;
			}
		}
		return null;
		
	}
	
	public boolean hasConnected() {
		return hasConnected;
	}

	public void confirmConnectionExistance() {
		this.hasConnected = true;
	}
	
	public void stopClient(){
		this.game.abort();
		this.reciever.stopReciever();
	}
	
	
	public boolean checkIfLatestPacket(JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		
		return this.getLastPacketId() < packetId;
	}
	
	public void updateConnectionData(JSONObject packetData, DatagramPacket packet){
		
		long packetId = ((Long) packetData.get("r"));
		
		this.setLastPingTime(System.currentTimeMillis());
		this.setLastPacketId(packetId);
	}
	
	
	public boolean isConnected(){
		return this.getLastPingTime() > System.currentTimeMillis() - Client.MaxNonRespondTime;
	}
	

	private long getLastPacketId() {
		return lastPacketId;
	}

	private void setLastPacketId(long lastPacketId) {
		this.lastPacketId = lastPacketId;
	}

	private long getLastPingTime() {
		return lastPingTime;
	}

	private void setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		//Re-send join packets until joining succeeds.
		long executionTime = System.currentTimeMillis();
		if(!this.hasConnected() && this.lastConnectionCheckTime + 1000 < executionTime){
			this.sender.sendPlayerJoinPacket(this.playerName);
			this.lastConnectionCheckTime = executionTime;
		}
		
		//When no longer connected, freeze the game, and display message.
		if(!this.isConnected() && this.hasConnected && !this.game.isFrozen()){
			this.game.addMessage("Connection with Host has been lost.");
			this.game.freeze();
			return;
		}
		
		//Otherwise, send update to server.
		if(!this.isSpectator && !this.hasLost()){
			this.sender.sendPlayerUpdatePacket(this.spaceshipController);
		}else{
			this.sender.sendSpectatorPingPacket();
		}
		
	}
	
	public SpaceshipController getController(){
		return this.spaceshipController;
	}
	
	public boolean hasLost(){
		Spaceship s = game.getSpaceship(this.playerName);
		return s!=null && s.isDestroyed();
	}
	
	public ClientGame getGame(){
		return this.game;
	}
}
