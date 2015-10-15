package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Packet {
	public enum PacketType {
		GAMESTATE, SPECTATE_JOIN, PLAYER_JOIN, SPECTATOR_PING, PLAYER_UPDATE, PLAYER_LOSE, ROUND_END, MESSAGE_LIST
	}
	
	private PacketType type;
	public JSONArray data;
	private long timestamp;
	
	public Packet(PacketType type){
		this(type, new JSONArray());
	}
	
	
	public Packet(PacketType type, JSONArray data){
		this.type = type;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}
	
	public String toJsonString(){
		JSONObject obj = new JSONObject();
		obj.put("t",type.ordinal());
		obj.put("d", data);
		obj.put("r", timestamp);
		
		return obj.toString();
	}
}
