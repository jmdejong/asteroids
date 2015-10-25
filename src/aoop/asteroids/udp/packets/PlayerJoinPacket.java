package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

/**
 * This packet is sent by the Client whenever a Player wants to join a game.
 * It contains the Player's name. (other metadata such as the Client's IP address can be found out by reading the actual DatagramPacket's header segment).
 * @author Wiebe-Marten Wijnja, Michiel de Jong
 *
 */
public class PlayerJoinPacket extends Packet {
	@SuppressWarnings("unchecked")
	public PlayerJoinPacket(String name){
		super(Packet.PacketType.PLAYER_JOIN, new JSONArray());
		this.data.add(name);
	}
	
	/**
	 * 
	 * @return For the given JSON data string, returns a string containing the Player's name
	 */
	public static String decodePacket(JSONArray data){
		return (String) data.get(0);
	}
}
