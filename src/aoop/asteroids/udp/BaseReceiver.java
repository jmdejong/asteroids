package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * the BaseReceiver contains core functionality that is the same between ServerReceiver and ClientReceiver.
 * It is run as a thread and listens on the specified socket for incoming packets.
 * 
 * Then, it parses the packet and depending on the type of packet, something might change in the program state.
 * Classes that inherit this class should therefore specify an implementation of parsePacket().
 * 
 * @author Wiebe-Marten Wijnja & Michiel de Jong
 *
 */
public abstract class BaseReceiver extends Thread {
	
	/**
	 * The socket + port to listen on for incoming packets.
	 */
	protected DatagramSocket socket = null;
	
	/**
	 * Constructs a new receiver.
	 * @param name
	 * @param socket the socket to listen to
	 * @throws SocketException when the socket port is already in use or unavailable for another reason.
	 */
	public BaseReceiver(String name, DatagramSocket socket) throws SocketException{
		super(name);
		this.socket = socket;
		
		Logging.LOGGER.fine(name+" thread started.");
	}
	
	/**
	 * This function is run on a separate thread.
	 * It will continually listen for packets and pass them to the parsePacket() function
	 * call stopReceiver() to make the thread stop running.
	 * @see BaseReceiver#parsePacket(String, DatagramPacket)
	 * @see BaseReceiver#stopReciever()
	 */
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
	
	/**
	 * Child classes should implement this method, to make things happen with the packets that are received.
	 * @param packet_string the JSON-encoded packet string data.
	 * @param packet the DatagramPacket. Can be used to obtain metadata such as information about the sender.
	 */
	protected abstract void parsePacket(String packet_string, DatagramPacket packet);
	
	/**
	 * Call this function to stop the Receiver's Thread from running
	 */
	public void stopReciever(){
		socket.close();
	}
	
}
