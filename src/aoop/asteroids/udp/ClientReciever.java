package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.MessageListPacket;
import aoop.asteroids.udp.packets.Packet.PacketType;

public class ClientReciever extends BaseReciever {
	Client client;
	
	public ClientReciever(Client client, DatagramSocket socket) throws SocketException{
		super("asteroids.udp.ClientThread",Client.UDPPort, socket);
		this.client = client;
	}

	@Override
	protected void parsePacket(String packetString, DatagramPacket packet) {
		JSONObject packetData = (JSONObject) JSONValue.parse(packetString);
		int rawPacketType = ((Long) packetData.get("t")).intValue();
		if(PacketType.values().length < rawPacketType){
			return;
		}
		PacketType packet_type = PacketType.values()[rawPacketType];
		
		if(!this.client.checkIfLatestPacket(packetData)){
			return;
		}else{
			this.client.updateConnectionData(packetData);
		}
		
		switch(packet_type){
			case GAMESTATE:
				Logging.LOGGER.fine("C: Gamestate Packet Received");
				Logging.LOGGER.fine(packetData.toString());
				this.client.confirmConnectionExistance();
				this.client.getGame().unFreeze();
				GameStatePacket.decodePacket((JSONArray) packetData.get("d"), client.getGame());
				
				this.client.getGame().playBGMIfNotAlreadyStarted();
				break;
			
			case MESSAGE_LIST:
				Logging.LOGGER.fine("C: Message List Packet Received");
				this.client.getGame().addPossiblyNewMessages(MessageListPacket.decodePacket((JSONArray) packetData.get("d")));
				break;
			default:
				Logging.LOGGER.fine("C: packet received with type: "+packet_type);
				break;
				
		}
	}

}
