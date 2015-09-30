package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Base {
	public Base(){
		
		
	}
	
	protected void sendPacket(String packet_string, InetSocketAddress serverAddress, DatagramSocket socket) throws IOException{
		byte[] buf = packet_string.getBytes();
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress.getAddress(), serverAddress.getPort());
		socket.send(packet);
	}
}
