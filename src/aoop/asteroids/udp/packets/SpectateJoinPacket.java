package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

/**
 * This packet is sent by the Client whenever a Spectator wants to join a game.
 * It does not contain any actual extra data. Metadata such as the Client's IP address can be found out by reading the actual DatagramPacket's header segment.
 * @author qqwy
 *
 */
public class SpectateJoinPacket extends Packet {
	public SpectateJoinPacket(){
		super(Packet.PacketType.SPECTATE_JOIN, new JSONArray());
	}
}
