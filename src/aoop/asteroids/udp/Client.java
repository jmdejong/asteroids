package aoop.asteroids.udp;

import java.net.InetSocketAddress;
import java.net.SocketException;

import aoop.asteroids.model.ClientGame;

public class Client extends Base{
	
	public InetSocketAddress serverAddress;
	public ClientGame game;
	
	public static int UDPPort = 8091;
	
	public Client(String host, int port){
		super();
		
		this.serverAddress = InetSocketAddress.createUnresolved(host, port);
		
		this.game = new ClientGame();
		
		try {
			new ClientThread(this).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
