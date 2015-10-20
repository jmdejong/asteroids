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
				this.client.game.unFreeze();
				GameStatePacket.decodePacket((JSONArray) packetData.get("d"), client.game);
				
				this.client.game.playBGMIfNotAlreadyStarted();
				break;
// 			case ROUND_END:
// 				Logging.LOGGER.fine("C: Round End Packet Received");
// 				if(!this.client.isSpectator){
// 					this.client.game.hasLost = false; //TODO: better separation of concerns?
// 				}
// 				this.client.game.freeze();
// 				//TODO: More sophisticated round restart logic?
// 				break;
			
			case MESSAGE_LIST:
				Logging.LOGGER.fine("C: Message List Packet Received");
				client.game.addPossiblyNewMessages(MessageListPacket.decodePacket((JSONArray) packetData.get("d")));
				break;
			default:
				Logging.LOGGER.fine("C: packet received with type: "+packet_type);
				break;
				
		}
	}

}