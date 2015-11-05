package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.Packet;
import aoop.asteroids.udp.packets.Packet.PacketType;



/**
 * The ServerReceiver receives packets at the Server-side.
 * It listens for PlayerJoin, SpectatorJoin, PlayerUpdate and SpectatorPing packets.
 * 
 * @author Wiebe-Marten Wijnja & Michiel de Jong
 *
 */
public class ServerReceiver extends BaseReceiver{
	
	/**
	 * Reference to the Server object. This reference is used to update state depending on the information that is contained in certain packets.
	 */
	private Server server;
	
	public ServerReceiver(Server server, DatagramSocket socket) throws SocketException{
		super("asteroids.udp.ServerThread", socket);
		this.server = server;
	}
	
	/**
	 * The Server listens to PlayerJoin, SpectatorJoin, PlayerUpdate and SpectatorPing packets. Other types of packets should not be received, and are thrown away.
	 * @see BaseReceiver#parsePacket(String, DatagramPacket)
	 */
	@Override
	protected void parsePacket(String packet_string, DatagramPacket packet){
		Logging.LOGGER.fine("parsing packet.");
		JSONObject packetData = (JSONObject) JSONValue.parse(packet_string);
		int rawPacketType = ((Long) packetData.get("t")).intValue();
		if(PacketType.values().length < rawPacketType){
			Logging.LOGGER.fine("Unsupported Packet Type Received.");
			return;
		}
		PacketType packet_type = PacketType.values()[rawPacketType];
		
		ClientConnection c = server.findConnection(packet.getSocketAddress());
		
		
		//Reject all non-join packets from unknown connections.
		if(c == null && !(packet_type == Packet.PacketType.SPECTATE_JOIN || packet_type == Packet.PacketType.PLAYER_JOIN)){
			return;
		}
		
		switch(packet_type){
			case SPECTATE_JOIN:
				Logging.LOGGER.fine("S: Specate Join Packet Received");
				server.addSpectatorConnection(packetData, packet);
				break;
			case PLAYER_JOIN:
				Logging.LOGGER.fine("S: Player Join Packet Received");
				server.addPlayerConnection(packetData, packet);
				break;
			case SPECTATOR_PING:
				Logging.LOGGER.severe("S: Spectator Ping Packet Received");
				if(!server.checkIfLatestPacket(packetData, packet)){
					return;
				}
				server.updateSpectatorConnectionData(packetData, packet);
				break;
			case PLAYER_UPDATE:
				Logging.LOGGER.severe("S: Player Update Packet Received");
				if(!server.checkIfLatestPacket(packetData, packet)){
					return;
				}
				server.updatePlayerShip((JSONArray)packetData.get("d"), packet.getSocketAddress());
				server.updatePlayerConnectionData(packetData, packet);
				break;
			default:
				Logging.LOGGER.fine("S: packet received with type: "+packet_type);
				break;
		}
	}
	
	
}
