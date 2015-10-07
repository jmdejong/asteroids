package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.PlayerLosePacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;
import aoop.asteroids.udp.packets.RoundEndPacket;


public class Server extends Base{
	
	public static int UDPPort = 8090;
	
	/**
	 * If a client takes longer than this to send a packet, the connection to that client will be considered disconnected.
	 */
	public static int MaxNonRespondTime = 5000; 
	
	List<ClientConnection> spectatorConnections 	= new ArrayList<ClientConnection>();
	private List<ClientConnection> playerConnections		= new ArrayList<ClientConnection>();
	
	
	
	Game game;
	
	DatagramSocket sendSocket;
	
	public Server(){
		super();
		
		
		
		try {
			this.sendSocket = new DatagramSocket(Server.UDPPort);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		this.game = new Game(this);
		Thread t = new Thread (game);
		t.start();
		
		
		try {
			new ServerThread(this).start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void addSpectatorConnection(JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		ClientConnection c = new ClientConnection((InetSocketAddress)packet.getSocketAddress());
		c.setLastPingTime(System.currentTimeMillis());
		c.updateLastPacketId(packetId);
		
		spectatorConnections.add(c);
// 		System.out.println(spectatorConnections);
	}
	public void addPlayerConnection(JSONObject packetData, DatagramPacket packet){
		long packetId = ((Long) packetData.get("r"));
		
		ClientConnection c = new ClientConnection((InetSocketAddress)packet.getSocketAddress());
		c.setLastPingTime(System.currentTimeMillis());
		c.updateLastPacketId(packetId);
		
		getPlayerConnections().add(c);
 		//System.out.println(playerConnections);
		this.game.addSpaceship();
	}
	
	public void sendGameStatePacket(){
		
		GameStatePacket gameStatePacket = new GameStatePacket(
				game.getSpaceships(),
				game.getBullets(),
				game.getAsteroids());
// 		System.out.println("Sending Game State Packet " + gameStatePacket.toJsonString());
		
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
		System.out.println(socketAddress);
		int index = getPlayerConnections().indexOf(new ClientConnection((InetSocketAddress)socketAddress));
		if(index == -1){
			return;
		}
		Spaceship playerShip = this.game.getSpaceshipRef(index);
		if(playerShip==null){
			return;
		}
 		System.out.println(playerShip);
		PlayerUpdatePacket.decodePacket(packet_data, playerShip);
	}
	
	public ClientConnection findClientConnection(SocketAddress socketAddress){
		int index = getPlayerConnections().indexOf(new ClientConnection((InetSocketAddress)socketAddress));
		if(index == -1){
			return null;
		}
		return getPlayerConnections().get(index);
	}
	
	public void sendPlayerPacket(){
		
	}
	
	public void restartGame(){
		sendRoundOverPacket();
		//this.game = new Game(this);
	}
	
	
	
	/** 
	 * Clients that did not say anything for longer than 5 seconds are considered to be `dead` and are to be removed from the game.
	 * */
	public void tagNonrespondingClients(){
		
		for(ClientConnection c : getPlayerConnections()){
			c.tagAsDisconnectedIfNotResponding();
			System.err.println(c.toDebugString());
		}
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
		return playerConnections;
	}

}
