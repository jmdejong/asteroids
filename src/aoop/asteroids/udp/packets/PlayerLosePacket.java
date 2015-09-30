package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class PlayerLosePacket extends Packet {
	public PlayerLosePacket(){
		super(Packet.PacketType.PLAYER_LOSE, new JSONArray());
	}
}
