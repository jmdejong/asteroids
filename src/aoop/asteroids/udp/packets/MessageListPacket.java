package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import aoop.asteroids.model.Message;

/**
 * The MessageListPacket only contains a list of messages that all Clients should see.
 * This packet is also sent multiple times(to ensure that it arrives at all Clients at least once); Clients are in charge of not re-displaying messages they saw before.
 * @author qqwy
 *
 */
public class MessageListPacket extends Packet {

	@SuppressWarnings("unchecked")
	public MessageListPacket(List<Message> messages){
		super(Packet.PacketType.MESSAGE_LIST, new JSONArray());
		
		JSONArray jsonMessages = new JSONArray();
		for(Message m : messages) jsonMessages.add(m.toJSON());
		this.data.add(jsonMessages);
		
		
	}
	
	/**
	 * @param data
	 * @return the List of messages contained in the passed data.
	 */
	public static List<Message> decodePacket(JSONArray data){
		
		JSONArray jsonMessages = (JSONArray) data.get(0);
		
		List <Message> messages = new ArrayList<Message>();
		for(Object m : jsonMessages) messages.add(Message.fromJSON(((JSONArray) m)));
		
		return messages;
	}
	

}
