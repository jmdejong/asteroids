package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class PlayerLosePacket extends Packet {
	/* TODO:
	 * - This packet is obsolete, remove it
	 */
	public PlayerLosePacket(){
		super(Packet.PacketType.PLAYER_LOSE, new JSONArray());
	}
}
