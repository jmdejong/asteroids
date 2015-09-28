package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import aoop.asteroids.model.ClientGame;
import aoop.asteroids.udp.packets.PlayerJoinPacket;

public class Client extends Base{
	
	public InetSocketAddress serverAddress;
	public ClientGame game;
	
	public static int UDPPort = 8091;
	
	DatagramSocket sendSocket;
	
	public Client(String host, int port){
		super();
		
		this.serverAddress = new InetSocketAddress(host, port);
		
		try {
			this.sendSocket = new DatagramSocket(8099);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.game = new ClientGame();
		sendPlayerJoinPacket();
		Thread t = new Thread (game);
		t.start();
		
		try {
			new ClientThread(this).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sendPacket(String packet_string) throws IOException{
		 
		byte[] buf = packet_string.getBytes();
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress.getAddress(), serverAddress.getPort());
		sendSocket.send(packet);
		
	}
	
	private void sendPlayerJoinPacket(){
		PlayerJoinPacket packet = new PlayerJoinPacket();
		try {
			this.sendPacket(packet.toJsonString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
