package aoop.asteroids.udp;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientSender extends BaseSender {
	
	ClientSender(){
		super();
		this.sendSocket = createSocketOnFirstUnusedPort();
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
}
