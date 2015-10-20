package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import aoop.asteroids.model.GameMessage;


public class MessageListPacket extends Packet {

	public MessageListPacket(List<GameMessage> messages){
		super(Packet.PacketType.MESSAGE_LIST, new JSONArray());
		
		JSONArray jsonMessages = new JSONArray();
		for(GameMessage m : messages) jsonMessages.add(m.toJSON());
		this.data.add(jsonMessages);
		
		
	}
	
	public static List<GameMessage> decodePacket(JSONArray data){
		
		JSONArray jsonMessages = (JSONArray) data.get(0);
		
		List <GameMessage> messages = new ArrayList<GameMessage>();
		for(Object m : jsonMessages) messages.add(GameMessage.fromJSON(((JSONArray) m)));
		
		return messages;
	}
	

}
