package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

/**
 * Sent by the Spectator every few milliseconds, to let the Server know that the Spectator is still listening to the connection, and watching the game.<br>
 * Does not contain actual data, as only the timestamp of the Packet is needed in this case.
 * @author qqwy
 */
public class SpectatorPingPacket extends Packet {
	public SpectatorPingPacket(){
		super(Packet.PacketType.SPECTATOR_PING, new JSONArray());
	}
}
