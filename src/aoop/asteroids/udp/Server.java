package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import aoop.asteroids.model.Game;
import aoop.asteroids.udp.packets.GameStatePacket;


public class Server extends Base{
	
	public static int UDPPort = 8090;
	
	ArrayList<InetSocketAddress> spectatorConnections 	= new ArrayList<InetSocketAddress>();
	ArrayList<InetSocketAddress> playerConnections		= new ArrayList<InetSocketAddress>();
	
	Game game;
	
	public Server(){
		super();
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
	}
	
	public void sendGameStatePacket(){
		GameStatePacket gameStatePacket = new GameStatePacket(
				game.getSpaceships(),
				game.getBullets(),
				game.getAsteroids());
		
	}
	
	private void sendPacket(String packet_string) throws IOException{
		DatagramSocket socket = new DatagramSocket(8098);
		byte[] buf = packet_string.getBytes();
		for(InetSocketAddress address : playerConnections){
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address.getAddress(), Client.UDPPort);
			socket.send(packet);
		}
		for(InetSocketAddress address : spectatorConnections){
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address.getAddress(), Client.UDPPort);
			socket.send(packet);
		}
	}
}
