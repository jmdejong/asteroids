package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.model.*;
import aoop.asteroids.udp.packets.PlayerJoinPacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;
import aoop.asteroids.udp.packets.SpectatorPingPacket;

public class Client extends Base{
	
	public InetSocketAddress serverAddress;
	public ClientGame game;
	
	public static int UDPPort = 8091;
	
	/** if set to true, Client is in Spectator mode, and will not send any input packets.*/
	public boolean isSpectator = false;
	
	private String name;
	
	private boolean hasConnected = false;
	
	
	
	DatagramSocket sendSocket;
	
	public Client(String host, int port, boolean isSpectator, String name){
		super();
		
		Logging.LOGGER.fine("New Client made.");
		
		this.serverAddress = new InetSocketAddress(host, port);
		
		this.isSpectator = isSpectator;
		
		this.name = name;
		
		//try {
			this.sendSocket = createSocketOnFirstUnusedPort();//new DatagramSocket(Client.UDPPort);
		//} catch (SocketException e1) {
		//	e1.printStackTrace();
		//}
		
		this.game = new ClientGame(this);
		
		//sendPlayerJoinPacket();
		Thread t = new Thread (game);
		t.start();
		
		
		
		try {
			new ClientThread(this).start();
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
		PlayerJoinPacket playerJoinPacket = new PlayerJoinPacket(this.name);
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

	public boolean hasConnected() {
		return hasConnected;
	}

	public void confirmConnectionExistance() {
		this.hasConnected = true;
	}
	

}
