package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class PlayerJoinPacket extends Packet {
	@SuppressWarnings("unchecked")
	public PlayerJoinPacket(String name){
		super(Packet.PacketType.PLAYER_JOIN, new JSONArray());
		this.data.add(name);
	}
	
	public static String decodePacket(JSONArray data){
		return (String) data.get(0);
	}
}
