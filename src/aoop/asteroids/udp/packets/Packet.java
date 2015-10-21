package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The default Packet class, where all packet types inherit from.<br>
 * These packets are supposed to be sent over UDP.<br>
 * Internally, they are made up of a String, which is built from JSON.<br>
 * <br>
 * All inherited classes should define a Constructor, which takes zero or more objects and constructs a packet containing that objects's important data<br>
 * And a public static decodePacket() method, that should take a JSONArray as input, and return the important information from this packet.
 * 
 * @author qqwy
 *
 */
public abstract class Packet {
	public enum PacketType {
		GAMESTATE, SPECTATE_JOIN, PLAYER_JOIN, SPECTATOR_PING, PLAYER_UPDATE, MESSAGE_LIST
	}
	
	private PacketType type;
	public JSONArray data;
	private long timestamp;
	
	public Packet(PacketType type){
		this(type, new JSONArray());
	}
	
	/**
	 * Creating a Packet sets its type, its data (which might be appended to later, in some cases), as well as the current time (in milliseconds) as timestamp.
	 * @param type
	 * @param data
	 */
	public Packet(PacketType type, JSONArray data){
		this.type = type;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Returns a string-representation of the internal data, as a JSON-object.
	 * the 't' field contains the packet type {@link Packet#type}<br>
	 * the 'd' field contains the actual data,<br>
	 * and the 'r' contains the set creation timestamp. This is used as the ID of the packet, as this ensures that every packet this computer sends later will have a higher ID.
	 * 
	 * @return a String-representation of the generated JSON-object.
	 */
	@SuppressWarnings("unchecked")
	public String toJsonString(){
		JSONObject obj = new JSONObject();
		obj.put("t",type.ordinal());
		obj.put("d", data);
		obj.put("r", timestamp);
		
		return obj.toString();
	}
	
	
}
