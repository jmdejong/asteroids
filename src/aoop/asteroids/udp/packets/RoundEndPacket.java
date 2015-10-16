package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class RoundEndPacket extends Packet {
	
	/* TODO:
	 * - This packet is obsolete, remove it
	 */
	
	public RoundEndPacket(){
		super(Packet.PacketType.ROUND_END, new JSONArray());
	}

}
