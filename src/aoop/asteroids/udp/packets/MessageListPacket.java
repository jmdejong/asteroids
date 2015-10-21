package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import aoop.asteroids.model.Message;


public class MessageListPacket extends Packet {

	@SuppressWarnings("unchecked")
	public MessageListPacket(List<Message> messages){
		super(Packet.PacketType.MESSAGE_LIST, new JSONArray());
		
		JSONArray jsonMessages = new JSONArray();
		for(Message m : messages) jsonMessages.add(m.toJSON());
		this.data.add(jsonMessages);
		
		
	}
	
	public static List<Message> decodePacket(JSONArray data){
		
		JSONArray jsonMessages = (JSONArray) data.get(0);
		
		List <Message> messages = new ArrayList<Message>();
		for(Object m : jsonMessages) messages.add(Message.fromJSON(((JSONArray) m)));
		
		return messages;
	}
	

}
