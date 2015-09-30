package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
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
	
	ArrayList<ClientConnection> spectatorConnections 	= new ArrayList<ClientConnection>();
	ArrayList<ClientConnection> playerConnections		= new ArrayList<ClientConnection>();
	
	Game game;
	
	DatagramSocket sendSocket;
	
	public Server(){
		super();
		
		
		
		try {
			this.sendSocket = new DatagramSocket(8098);
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
	
	public void addSpectatorConnection(SocketAddress address){
		spectatorConnections.add(new ClientConnection((InetSocketAddress)address));
// 		System.out.println(spectatorConnections);
	}
	public void addPlayerConnection(SocketAddress address){
		playerConnections.add(new ClientConnection((InetSocketAddress)address));
// 		System.out.println(playerConnections);
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
		if(ship_index < 0 || ship_index > playerConnections.size()){
			return;
		}
		
		InetSocketAddress connection = playerConnections.get(ship_index).getSocketAddress();
		
		PlayerLosePacket playerLosePacket = new PlayerLosePacket();
		try {
			super.sendPacket(playerLosePacket.toJsonString(), connection.getAddress(), Client.UDPPort, sendSocket);
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
		for(ClientConnection address : playerConnections){
			super.sendPacket(packet_string, address.getAddress(), Client.UDPPort, sendSocket);
		}
		for(ClientConnection address : spectatorConnections){
			super.sendPacket(packet_string, address.getAddress(), Client.UDPPort, sendSocket);
		}
	}

	public void updatePlayerShip(JSONArray packet_data, SocketAddress socketAddress) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
		int index = playerConnections.indexOf(inetSocketAddress);
// 		System.out.println(index);
		if(index == -1){
			return;
		}
		Spaceship playerShip = this.game.getSpaceshipRef(index);
		if(playerShip==null){
			return;
		}
// 		System.out.println(playerShip);
		PlayerUpdatePacket.decodePacket(packet_data, playerShip);
	}
	
	public void sendPlayerPacket(){
		
	}
	
	public void restartGame(){
		sendRoundOverPacket();
		//this.game = new Game(this);
	}
}
