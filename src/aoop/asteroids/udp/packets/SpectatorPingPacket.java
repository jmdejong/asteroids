package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

public class SpectatorPingPacket extends Packet {
	public SpectatorPingPacket(){
		super(Packet.PacketType.SPECTATOR_PING, new JSONArray());
	}
}
