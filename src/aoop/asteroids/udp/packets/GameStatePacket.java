package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class GameStatePacket extends Packet {
	public GameStatePacket(){
		super(Packet.PacketType.GAMESTATE, new JSONArray());
	}
}
