package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class RoundEndPacket extends Packet {

	public RoundEndPacket(){
		super(Packet.PacketType.ROUND_END, new JSONArray());
	}

}
