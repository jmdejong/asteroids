package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.Packet.PacketType;

public class ClientThread extends BaseServerThread {
	Client client;
	
	public ClientThread(Client client) throws SocketException{
		super("asteroids.udp.ClientThread",Client.UDPPort, client.sendSocket);
		this.client = client;
	}

	@Override
	protected void parsePacket(String packet_string, DatagramPacket packet) {
		JSONObject packet_data = (JSONObject) JSONValue.parse(packet_string);
		int raw_packet_type = ((Long) packet_data.get("t")).intValue();
		if(PacketType.values().length < raw_packet_type){
			return;
		}
		PacketType packet_type = PacketType.values()[raw_packet_type];
		switch(packet_type){
			case GAMESTATE:
				Logging.LOGGER.fine("C: Gamestate Packet Received");
				Logging.LOGGER.fine(packet_data);
				this.client.game.unFreeze();
				GameStatePacket.decodePacket((JSONArray) packet_data.get("d"), client.game);
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
				this.client.game.hasLost = true; //TODO: better separation of concerns?
				break;
			case ROUND_END:
				Logging.LOGGER.fine("C: Round End Packet Received");
				if(!this.client.isSpectator){
					this.client.game.hasLost = false; //TODO: better separation of concerns?
				}
				this.client.game.freeze();
				//TODO: More sophisticated round restart logic?
				break;
		}
		
	}

}
