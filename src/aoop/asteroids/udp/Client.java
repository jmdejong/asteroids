package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.model.*;
import aoop.asteroids.udp.packets.PlayerJoinPacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;
import aoop.asteroids.udp.packets.SpectatorPingPacket;

public class Client extends Base implements Observer{
	
	/* TODO:
	 * - maybe make a new class (Player, for example") that takes care of the clients on player (spaceshipController, hasLost etc...)
	 *   this player object should have a reference to game
	 *   or maybe we can just move this to spaceshipController
	 * DONE:
	 * - find out why message "connection with host lost" shows up at the beginning and fix
	 */
	
	public InetSocketAddress serverAddress;
	
	
	private SpaceshipController spaceshipController;
	
	public ClientGame game;
	
	public static int UDPPort = 8091;
	
	/** if set to true, Client is in Spectator mode, and will not send any input packets.*/
	public boolean isSpectator = false;
	
	private String playerName;
	
	private boolean hasConnected = false;
	
	private long lastPingTime = 0;
	private long lastPacketId = 0;
	private long lastConnectionCheckTime = 0;
	
	DatagramSocket sendSocket;
	
	public Client(String host, int port, boolean isSpectator, String playerName){
		super();
		
		Logging.LOGGER.fine("New Client made.");
		
		this.serverAddress = new InetSocketAddress(host, port);
		
		this.isSpectator = isSpectator;
		
		this.playerName = playerName;
		
		this.sendSocket = createSocketOnFirstUnusedPort();
		
		this.game = new ClientGame();
		game.addObserver(this);
		
		
		
		if(!isSpectator){
			this.spaceshipController = new SpaceshipController();
		}
		
		
		//sendPlayerJoinPacket();
		Thread t = new Thread (game);
		t.start();
		
		this.game.addMessage("Connecting to Host...");
		
		try {
			this.responsesThread =  new ClientReciever(this);
			this.responsesThread.start();
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
	
	private void sendPacket(String packet_string) throws IOException{
		super.sendPacket(packet_string,serverAddress.getAddress(), serverAddress.getPort(), sendSocket); 	
	}
	
	public void sendPlayerJoinPacket(){
		Logging.LOGGER.fine("sending join packet...");
		PlayerJoinPacket playerJoinPacket = new PlayerJoinPacket(this.playerName);
		try {
			this.sendPacket(playerJoinPacket.toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPlayerUpdatePacket(SpaceshipController sc){
		PlayerUpdatePacket playerUpdatePacket = new PlayerUpdatePacket(sc.isUp(), sc.isLeft(), sc.isRight(), sc.isFire());
		try {
			this.sendPacket(playerUpdatePacket.toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendSpectatorPingPacket(){
		try {
			this.sendPacket(new SpectatorPingPacket().toJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void checkIfStillConnected(){
		
	}

	public boolean hasConnected() {
		return hasConnected;
	}

	public void confirmConnectionExistance() {
		this.hasConnected = true;
	}
	
	public void stopClient(){
		this.game.abort();
		this.responsesThread.stopServer();
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
		return this.lastPingTime > System.currentTimeMillis() - Client.MaxNonRespondTime;
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
		if(!this.hasConnected() && this.lastConnectionCheckTime + 3000 < executionTime){
			this.sendPlayerJoinPacket();
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
			this.sendPlayerUpdatePacket(this.spaceshipController);
		}else{
			this.sendSpectatorPingPacket();
		}
		
	}
	
	public SpaceshipController getController(){
		return this.spaceshipController;
	}
	
	public boolean hasLost(){
		Spaceship s = game.getSpaceship(this.playerName);
		return s!=null && s.isDestroyed();
	}
}
