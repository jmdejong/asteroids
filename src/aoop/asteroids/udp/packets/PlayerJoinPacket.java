package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class PlayerJoinPacket extends Packet {
	public PlayerJoinPacket(){
		super(Packet.PacketType.PLAYER_JOIN, new JSONArray());
	}
}
