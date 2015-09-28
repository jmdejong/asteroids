package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class SpectateJoinPacket extends Packet {
	public SpectateJoinPacket(){
		super(Packet.PacketType.SPECTATE_JOIN, new JSONArray());
	}
}
