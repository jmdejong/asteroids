package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class BaseServerThread extends Thread {
	boolean stopServer = false;
	protected DatagramSocket socket = null;
	
	public BaseServerThread(String name,int port, DatagramSocket socket) throws SocketException{
		super(name);
		this.socket = socket;//new DatagramSocket(port);
		
		Logging.LOGGER.fine(name+" thread started.");
	}
	
	public void run(){
		
		while(stopServer == false){
			try {
				byte[] buf = new byte[65507];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				//Code below is run as soon as a packet is received.
				String packet_string = new String(buf).split("\0")[0];

				
				parsePacket(packet_string, packet);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void parsePacket(String packet_string, DatagramPacket packet);
	
	public void stopServer(){
		stopServer = true;
	}
	
}
