package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.MessageListPacket;
import aoop.asteroids.udp.packets.MessagePacket;
import aoop.asteroids.udp.packets.Packet.PacketType;

public class ClientThread extends BaseServerThread {
	Client client;
	
	public ClientThread(Client client) throws SocketException{
		super("asteroids.udp.ClientThread",Client.UDPPort, client.sendSocket);
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
				
				if(!client.game.bgmHasStarted){
					client.game.bgmHasStarted = true;
					//client.game.playSound("background_music_bassline.wav",0/* ((Long) packet_data.get("r")).intValue() % 3600*/);
				}
				break;
			case SPECTATE_JOIN:
				//Do nothing. Client should send this; not receive it!
				Logging.LOGGER.fine("C: Specate Join Packet Received");
				break;
			case PLAYER_JOIN:
				//Do nothing. Client should send this; not receive it!
				Logging.LOGGER.fine("C: Player Join Packet Received");
				break;
			case SPECTATOR_PING:
				Logging.LOGGER.fine("C: Spectator Ping Packet Received");
				break;
			case PLAYER_UPDATE:
				//Do nothing. Client should send this; not receive it!
				Logging.LOGGER.fine("C: Player Update Packet Received");
				break;
			case PLAYER_LOSE:
				Logging.LOGGER.fine("C: Player Lose Packet Received");
				this.client.game.hasLost(); //TODO: better separation of concerns?
				break;
			case ROUND_END:
				Logging.LOGGER.fine("C: Round End Packet Received");
				if(!this.client.isSpectator){
					this.client.game.hasLost = false; //TODO: better separation of concerns?
				}
				this.client.game.freeze();
				//TODO: More sophisticated round restart logic?
				break;
			case MESSAGE:
				Logging.LOGGER.severe("C: Message Packet Received (DEPRECATED!)");
				Logging.LOGGER.fine(MessagePacket.decodePacket((JSONArray) packetData.get("d")));
				client.game.addMessage(MessagePacket.decodePacket((JSONArray) packetData.get("d")));
				break;	
			case MESSAGE_LIST:
				Logging.LOGGER.severe("C: Message List Packet Received");
				client.game.addPossiblyNewMessages(MessageListPacket.decodePacket((JSONArray) packetData.get("d")));
				break;
			default:
				Logging.LOGGER.fine("C: Unknown packet type!");
				break;
				
		}
	}

}
