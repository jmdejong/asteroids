package aoop.asteroids.udp;

import java.net.DatagramPacket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.Packet.PacketType;

public class ClientThread extends BaseServerThread {
	Client client;
	
	public ClientThread(Client client) throws SocketException{
		super("asteroids.udp.ClientThread",Client.UDPPort);
		this.client = client;
	}

	@Override
	protected void parsePacket(String packet_string, DatagramPacket packet) {
		JSONObject packet_data = (JSONObject) JSONValue.parse(packet_string);
		int raw_packet_type = ((Long) packet_data.get("t")).intValue();
		if(PacketType.values().length < raw_packet_type){
// 			System.out.println("Unsupported Packet Type Received.");
			return;
		}
		PacketType packet_type = PacketType.values()[raw_packet_type];
		switch(packet_type){
			case GAMESTATE:
// 				System.out.println("Gamestate Packet Received");
				GameStatePacket.decodePacket((JSONArray) packet_data.get("d"), client.game);
				break;
			case SPECTATE_JOIN:
				//Do nothing. Client should send this; not receive it!
// 				System.out.println("Specate Join Packet Received");
				break;
			case PLAYER_JOIN:
				//Do nothing. Client should send this; not receive it!
// 				System.out.println("Player Join Packet Received");
				break;
			case PLAYER_UPDATE:
				//Do nothing. Client should send this; not receive it!
// 				System.out.println("Player Update Packet Received");
				break;
		}
	}

}
