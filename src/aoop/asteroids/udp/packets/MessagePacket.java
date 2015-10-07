package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class MessagePacket extends Packet {
	public MessagePacket(String message){
		super(Packet.PacketType.MESSAGE, new JSONArray());
		this.data.add(message);
	}
	
	public static String decodePacket(JSONArray data){
		return (String) data.get(0);
	}
}
