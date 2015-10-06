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

public class ServerThread extends BaseServerThread{
	
	Server server;
	
	public ServerThread(Server server) throws SocketException{
		super("asteroids.udp.ServerThread",Server.UDPPort, server.sendSocket);
		this.server = server;
	}

	@Override
	protected void parsePacket(String packet_string, DatagramPacket packet){
		System.out.println("parsing packet.");
		JSONObject packet_data = (JSONObject) JSONValue.parse(packet_string);
		int raw_packet_type = ((Long) packet_data.get("t")).intValue();
		if(PacketType.values().length < raw_packet_type){
			System.out.println("Unsupported Packet Type Received.");
			return;
		}
		PacketType packet_type = PacketType.values()[raw_packet_type];
		
		switch(packet_type){
			case GAMESTATE:
				//Do nothing. Server should send this; not receive it!
				System.out.println("S: Gamestate Packet Received");
				break;
			case SPECTATE_JOIN:
				System.out.println("S: Specate Join Packet Received");
				server.addSpectatorConnection(packet.getSocketAddress());
				break;
			case PLAYER_JOIN:
				System.out.println("S: Player Join Packet Received");
				server.addPlayerConnection(packet.getSocketAddress());
				break;
			case SPECTATOR_PING:
				System.out.println("S: Spectator Ping Packet Received");
				break;
			case PLAYER_UPDATE:
				System.out.println("S: Player Update Packet Received");
				server.updatePlayerShip((JSONArray)packet_data.get("d"), packet.getSocketAddress());
				break;
			case PLAYER_LOSE:
				//Do nothing. Server should send this; not receive it!
				System.out.println("S: Player Lose Packet Received");
				break;
			case ROUND_END:
				//Do nothing. Server should send this; not receive it!
				System.out.println("S: Round End Packet Received");
				break;
		}
	}
	
	
}
