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
import aoop.asteroids.udp.packets.PlayerUpdatePacket;


public class Server extends Base{
	
	public static int UDPPort = 8090;
	
	ArrayList<InetSocketAddress> spectatorConnections 	= new ArrayList<InetSocketAddress>();
	ArrayList<InetSocketAddress> playerConnections		= new ArrayList<InetSocketAddress>();
	
	Game game;
	
	DatagramSocket sendSocket;
	
	public Server(){
		super();
		
		try {
			this.sendSocket = new DatagramSocket(8098);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.game = new Game(this);
		Thread t = new Thread (game);
		t.start();
		
		
		try {
			new ServerThread(this).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSpectatorConnection(SocketAddress address){
		spectatorConnections.add((InetSocketAddress)address);
		System.out.println(spectatorConnections);
	}
	public void addPlayerConnection(SocketAddress address){
		playerConnections.add((InetSocketAddress)address);
		System.out.println(playerConnections);
		this.game.addSpaceship();
	}
	
	public void sendGameStatePacket(){
		
		GameStatePacket gameStatePacket = new GameStatePacket(
				game.getSpaceships(),
				game.getBullets(),
				game.getAsteroids());
		System.out.println("Sending Game State Packet " + gameStatePacket.toJsonString());
		
		try {
			sendPacket(gameStatePacket.toJsonString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPacket(String packet_string) throws IOException{
		
		byte[] buf = packet_string.getBytes();
		for(InetSocketAddress address : playerConnections){
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address.getAddress(), Client.UDPPort);
			sendSocket.send(packet);
		}
		for(InetSocketAddress address : spectatorConnections){
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address.getAddress(), Client.UDPPort);
			sendSocket.send(packet);
		}
	}

	public void updatePlayerShip(JSONArray packet_data, SocketAddress socketAddress) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
		int index = playerConnections.indexOf(inetSocketAddress);
		System.out.println(index);
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
}
