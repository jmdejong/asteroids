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
	
	protected void sendPacket(String packet_string, InetAddress address, int port, DatagramSocket socket) throws IOException{
		byte[] buf = packet_string.getBytes();
		
		//System.out.println("Sending packet to"+address.toString()+":"+port);
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);
	}
}
