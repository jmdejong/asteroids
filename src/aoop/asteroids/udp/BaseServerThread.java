package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class BaseServerThread extends Thread {
	boolean stopServer = false;
	protected DatagramSocket socket = null;
	
	public BaseServerThread(String name,int port) throws SocketException{
		super(name);
		socket = new DatagramSocket(port);
	}
	
	public void run(){
		System.out.println("Starting server.");
		
		while(stopServer == false){
			try {
				byte[] buf = new byte[65507];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				//Code below is run as soon as a packet is received.
				//System.out.println(new String(buf));
				String packet_string = new String(buf).split("\0")[0];
				//System.out.print(str);

				
				parsePacket(packet_string, packet);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void parsePacket(String packet_string, DatagramPacket packet);
}
