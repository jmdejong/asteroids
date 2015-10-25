package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class BaseReciever extends Thread {
	boolean stopServer = false;
	protected DatagramSocket socket = null;
	
	public BaseReciever(String name, int port, DatagramSocket socket) throws SocketException{
		super(name);
		this.socket = socket;
		
		Logging.LOGGER.fine(name+" thread started.");
	}
	
	public void run(){
		
		while(true){
			try {
				byte[] buf = new byte[65507];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				//Code below is run as soon as a packet is received.
				String packet_string = new String(buf).split("\0")[0];

				
				parsePacket(packet_string, packet);
				
			} catch (SocketException e){
				// end this thread when the socket is closed
				break;
			} catch (IOException e) {
				Logging.LOGGER.severe("Reciever thread could not run: "+e.getMessage());
			}
		}

	}
	
	protected abstract void parsePacket(String packet_string, DatagramPacket packet);
	
	public void stopReciever(){
		socket.close();
	}
	
}
