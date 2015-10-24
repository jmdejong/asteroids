package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

import org.json.simple.JSONObject;

import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.model.game.ClientGame;
import aoop.asteroids.model.gameobjects.Spaceship;

public class Client extends Base implements Observer{
	
	/* TODO:
	 * DONE:
	 * - find out why message "connection with host lost" shows up at the beginning and fix
	 * not needed:
	 * - maybe make a new class (Player, for example") that takes care of the clients on player (spaceshipController, hasLost etc...)
	 *   this player object should have a reference to game
	 *   or maybe we can just move this to spaceshipController
	 */
	
	//private InetSocketAddress serverAddress;
	
	/**
	 * Reference to the SpaceshipController, that implements KeyListener so we know how the player want to move their spaceship.
	 */
	private SpaceshipController spaceshipController;
	
	/**
	 * Reference to the ClientGame, which contains the last game state we recieved from the server, and after this attempts to move this game state visually forward by predicting the movement of the game objects (assuming no change in ship controlling or collisions).
	 */
	private ClientGame game;
	
	/**
	 * The port that the Client will try to create a Socket on, by default.
	 * If this port is taken, (For instance by another running copy of this application), consecutive ports after this one are tried.
	 * @see Client#createSocketOnFirstUnusedPort()
	 */
	public static int UDPPort = 8091;
	
	/** if set to true, Client is in Spectator mode, and will not send any PlayerInput packets.
	 * Instead, SpectatorPing packets are sent, to let the server know that this client is still connected and watching.
	 * */
	private boolean isSpectator = false;

	
	/**
	 * The name the player chose for themselves. This is used for determining the high scores.
	 */
	private String playerName;
	
	/**
	 * True if there has been a connection with the server in the past, since the creation of this Client instance.
	 */
	private boolean hasConnected = false;
	
	/**
	 * The last time a packet from the server was received.
	 */
	private long lastPingTime = 0;
	
	/**
	 * The highest ID of all packets received so far.
	 * Packets with lower ID's are rejected, to enforce packet order.
	 */
	private long lastPacketId = 0;
	
	/**
	 * Used for throttling the amount of sent PlayerJoin or SpectatorJoin packets when attempting to connect to the server.
	 */
	private long lastConnectionCheckTime = 0;
	
	/**
	 * A reference to the ClientSender that is in charge of creating and sending the actual packets of the data we provide it with.
	 */
	protected ClientSender sender;
	
	/**
	 * This constructor creates a new ClientSender with the specified host and port, as well as starting a ClientReceiver thread that listens for packets on the same socket.<br>
	 * After this, a ClientGame is started; this is the graphical representation of the game on the client-side.
	 * @param host the host address to connect to
	 * @param port the port to connect to on the host address
	 * @param isSpectator if true, the Client will run in Spectator mode. Otherwise, runs in Player mode.
	 * @param playerName the name that the player wants to use in the game. (only used if isSpectator is false)
	 */
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
	
	/**
	 * Iterates in the range ({@link Client#UDPPort} - {@link Client#UDPPort}+100) until a free socket is found.
	 * @return the first socket on a port that is not already taken.
	 */
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
	
	/**
	 * @return True if there has been a connection with the server at some point after creation of this instance.
	 */
	public boolean hasConnected() {
		return hasConnected;
	}
	
	/**
	 * If there is a connection, set hasConnected to true.
	 */
	public void confirmConnectionExistance() {
		this.hasConnected = true;
	}
	
	/**
	 * Tells all threads that have been started by this object to stop running.
	 * @see ClientGame#abort()
	 * @see ClientReciever#stopReciever()
	 */
	public void stopClient(){
		this.game.abort();
		this.reciever.stopReciever();
	}
	
	/**
	 * @return True if the given packet's ID (stored in the 'r' JSON field) is higher than the one stored previously.
	 */
	public boolean checkIfLatestPacket(JSONObject packetData){
		long packetId = ((Long) packetData.get("r"));
		
		
		return this.getLastPacketId() < packetId;
	}
	
	/**
	 * Will save the last packet ID to be the ID of the passed packetData, and the last Ping Time to be now.
	 */
	public void updateConnectionData(JSONObject packetData){
		
		long packetId = ((Long) packetData.get("r"));
		
		this.lastPingTime = System.currentTimeMillis();
		this.lastPacketId = packetId;
	}
	
	/**
	 * @return true if it has been less long than Base.MaxNonRespondTime since the last time a packet was received.
	 */
	public boolean isConnected(){
		return this.getLastPingTime() > System.currentTimeMillis() - Base.MaxNonRespondTime;
	}
	
	/**
	 * @return the ID of the last packet.
	 */
	private long getLastPacketId() {
		return lastPacketId;
	}
	

	/**
	 * @return the last time a packet was received.
	 */
	private long getLastPingTime() {
		return lastPingTime;
	}

	/**
	 * Runs every time the ClientGame's state has changed.<br/>
	 * -> Sends a PlayerJoin or SpectatorJoin packet until the Client is connected.<br>
	 * -> If no longer connected, freezes the game if the time since the last connection was longer than the timeout.<br>
	 * -> Otherwise, send an update (Either a PlayerUpdate packet or SpectatorPing packet, depending on {@link Client#isSpectator}
	 */
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
	
	/**
	 * @return a reference to the SpaceshipController object.
	 */
	public SpaceshipController getController(){
		return this.spaceshipController;
	}
	
	/**
	 * @return true if the spaceship with this player's name has been destroyed.
	 */
	public boolean hasLost(){
		Spaceship s = game.getSpaceship(this.playerName);
		return s!=null && s.isDestroyed();
	}
	
	/**
	 * @return a reference to the ClientGame object.
	 */
	public ClientGame getGame(){
		return this.game;
	}
}
