package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServerThread extends Thread{
	boolean stopServer = false;
	protected DatagramSocket socket = null;
	
	public ServerThread() throws SocketException{
		super("asteroids.udp.ServerThread");
		socket = new DatagramSocket(8090);
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
				String str = new String(buf).split("\0")[0];;
				//System.out.print(str);
				JSONObject packet_data = (JSONObject) JSONValue.parse(str);
				System.out.println("Data: "+packet_data);
				System.out.println(packet_data.get("t"));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}
