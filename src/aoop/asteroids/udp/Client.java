package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.model.*;
import aoop.asteroids.udp.packets.PlayerJoinPacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;

public class Client extends Base{
	
	public InetSocketAddress serverAddress;
	public ClientGame game;
	
	public static int UDPPort = 8091;
	
	/** if set to true, Client is in Spectator mode, and will not send any input packets.*/
	public boolean isSpectator = false;
	
	
	
	DatagramSocket sendSocket;
	
	public Client(String host, int port, boolean isSpectator){
		super();
		
		this.serverAddress = new InetSocketAddress(host, port);
		
		try {
			this.sendSocket = new DatagramSocket(8099);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.game = new ClientGame(this);
		
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
		super.sendPacket(packet_string,serverAddress.getAddress(), serverAddress.getPort(), sendSocket); 
		byte[] buf = packet_string.getBytes();
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress.getAddress(), serverAddress.getPort());
		sendSocket.send(packet);
		
	}
	
	private void sendPlayerJoinPacket(){
		PlayerJoinPacket playerJoinPacket = new PlayerJoinPacket();
		try {
			this.sendPacket(playerJoinPacket.toJsonString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendPlayerUpdatePacket(SpaceshipController sc){
		PlayerUpdatePacket playerUpdatePacket = new PlayerUpdatePacket(sc.isUp(), sc.isLeft(), sc.isRight(), sc.isFire());
// 		System.out.println("Sending Player Update Packet "+ playerUpdatePacket.toJsonString());
		try {
			this.sendPacket(playerUpdatePacket.toJsonString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
