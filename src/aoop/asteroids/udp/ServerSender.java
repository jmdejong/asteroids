package aoop.asteroids.udp;

import java.util.List;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collection;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.Explosion;
import aoop.asteroids.model.GameMessage;
import aoop.asteroids.model.Spaceship;
import aoop.asteroids.udp.packets.GameStatePacket;
import aoop.asteroids.udp.packets.MessageListPacket;

public class ServerSender extends BaseSender {
	public ServerSender(DatagramSocket socket) throws SocketException{
		super(socket);
	}
	
	private void sendPacketToAll(String packet_string, Collection<ClientConnection> playerConnections, Collection<ClientConnection> spectatorConnections) throws IOException{
		for(ClientConnection connection : playerConnections){
			super.sendPacket(packet_string, connection.getAddress(), connection.getPort(), sendSocket);
		}
		for(ClientConnection connection : spectatorConnections){
			super.sendPacket(packet_string, connection.getAddress(), connection.getPort(), sendSocket);
		}
	}
	
	public void sendMessageListPacket(List<GameMessage> messages, Collection<ClientConnection> playerConnections, Collection<ClientConnection> spectatorConnections){
		try {
			this.sendPacketToAll(new MessageListPacket(messages).toJsonString(), playerConnections, spectatorConnections);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
public void sendGameStatePacket
	(
			int roundNumber,
			List<Spaceship> spaceships,
			List<Bullet> bullets,
			List<Asteroid> asteroids,
			List<Explosion> explosions,
			Collection<ClientConnection> playerConnections, 
			Collection<ClientConnection> spectatorConnections
	)
	{
		
		GameStatePacket gameStatePacket = new GameStatePacket(
				roundNumber,spaceships,bullets,asteroids,explosions);
		
		try {
			sendPacketToAll(gameStatePacket.toJsonString(),playerConnections, spectatorConnections);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
