package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class PlayerUpdatePacket extends Packet {
	public PlayerUpdatePacket(){
		super(Packet.PacketType.PLAYER_UPDATE, new JSONArray());
	}
}
