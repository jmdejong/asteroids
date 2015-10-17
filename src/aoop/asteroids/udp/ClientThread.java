package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.MessageListPacket;
import aoop.asteroids.udp.packets.Packet.PacketType;
import aoop.asteroids.model.ClientGame;

public class ClientThread extends BaseServerThread {
	Client client;
	ClientGame game;
	
	
	public ClientThread(Client client, DatagramSocket socket, ClientGame game) throws SocketException{
		super("asteroids.udp.ClientThread",Client.UDPPort, socket);
		this.client = client;
		this.game = game;
	}

	@Override
	protected void parsePacket(String packetString, DatagramPacket packet) {
		JSONObject packetData = (JSONObject) JSONValue.parse(packetString);
		int rawPacketType = ((Long) packetData.get("t")).intValue();
		if(PacketType.values().length < rawPacketType){
			return;
		}
		PacketType packet_type = PacketType.values()[rawPacketType];
		
		if(!this.client.checkIfLatestPacket(packetData, packet)){
			return;
		}else{
			this.client.updateConnectionData(packetData, packet);
		}
		
		switch(packet_type){
			case GAMESTATE:
				Logging.LOGGER.fine("C: Gamestate Packet Received");
				Logging.LOGGER.fine(packetData.toString());
				this.client.confirmConnectionExistance();
				this.game.unFreeze();
				GameStatePacket.decodePacket((JSONArray) packetData.get("d"), this.game);
				
				this.game.playBGMIfNotAlreadyStarted();
				break;
			
			case MESSAGE_LIST:
				Logging.LOGGER.fine("C: Message List Packet Received");
				this.game.addPossiblyNewMessages(MessageListPacket.decodePacket((JSONArray) packetData.get("d")));
				break;
			default:
				Logging.LOGGER.fine("C: packet received with type: "+packet_type);
				break;
				
		}
	}

}
