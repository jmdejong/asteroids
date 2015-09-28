package aoop.asteroids.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import aoop.asteroids.model.Game;


public class Server extends Base{
	
	public static int UDPPort = 8090;
	
	ArrayList<InetSocketAddress> spectatorConnections 	= new ArrayList<InetSocketAddress>();
	ArrayList<InetSocketAddress> playerConnections		= new ArrayList<InetSocketAddress>();
	
	Game game;
	
	public Server(){
		super();
		this.game = game;
		
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
}
