package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.Packet;
import aoop.asteroids.udp.packets.Packet.PacketType;

public class ServerThread extends Thread{
	boolean stopServer = false;
	protected DatagramSocket socket = null;
	Server server;
	
	public ServerThread(Server server) throws SocketException{
		super("asteroids.udp.ServerThread");
		this.server = server;
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
				String packet_string = new String(buf).split("\0")[0];
				//System.out.print(str);

				
				parsePacket(packet_string, packet);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parsePacket(String packet_string, DatagramPacket packet){
		JSONObject packet_data = (JSONObject) JSONValue.parse(packet_string);
		System.out.println("Data: "+packet_data);
		System.out.println(packet_data.get("t"));
		
		
		int raw_packet_type = ((Long) packet_data.get("t")).intValue();
		
		if(PacketType.values().length < raw_packet_type){
			System.out.println("Unsupported Packet Type Received.");
			return;
		}
		
		PacketType packet_type = PacketType.values()[raw_packet_type];
		switch(packet_type){
			case GAMESTATE:
				//Do nothing. Server should send this; not receive it!
				System.out.println("Gamestate Packet Received");
				break;
			case SPECTATE_JOIN:
				System.out.println("Specate Join Packet Received");
				server.addSpectatorConnection(packet.getSocketAddress());
				break;
			case PLAYER_JOIN:
				System.out.println("Player Join Packet Received");
				server.addPlayerConnection(packet.getSocketAddress());
				break;
			case PLAYER_UPDATE:
				System.out.println("Player Update Packet Received");
				break;
		}
	}
	
	
}
