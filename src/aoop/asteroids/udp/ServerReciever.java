package aoop.asteroids.udp;

import aoop.asteroids.Logging;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.udp.packets.Packet;
import aoop.asteroids.udp.packets.Packet.PacketType;

public class ServerReciever extends BaseReciever{
	
	Server server;
	
	public ServerReciever(Server server, int port, DatagramSocket socket) throws SocketException{
		super("asteroids.udp.ServerThread",port, socket);
		this.server = server;
	}

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
		
		ClientConnection c = server.findClientConnection(packet.getSocketAddress());
		
		//Reject all packets from connectiosn that have been considered disconnected.
		if(c != null && c.isDisconnected()){
			//return;
		}
		
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
				Logging.LOGGER.fine("S: Spectator Ping Packet Received");
				if(!server.checkIfLatestPacket(packetData, packet)){
					return;
				}
				server.updateConnectionData(packetData, packet);
				break;
			case PLAYER_UPDATE:
				Logging.LOGGER.fine("S: Player Update Packet Received");
				if(!server.checkIfLatestPacket(packetData, packet)){
					return;
				}
				server.updatePlayerShip((JSONArray)packetData.get("d"), packet.getSocketAddress());
				server.updateConnectionData(packetData, packet);
				break;
			default:
				Logging.LOGGER.fine("C: packet received with type: "+packet_type);
				break;
		}
		//System.out.println(server.getPlayerConnections());
		//server.tagNonrespondingClients();
	}
	
	
}
