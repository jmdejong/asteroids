package aoop.asteroids.udp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import aoop.asteroids.Logging;
import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.udp.packets.PlayerJoinPacket;
import aoop.asteroids.udp.packets.PlayerUpdatePacket;
import aoop.asteroids.udp.packets.SpectateJoinPacket;
import aoop.asteroids.udp.packets.SpectatorPingPacket;

public class ClientSender extends BaseSender {
	
	public InetSocketAddress serverAddress;
	
	
	ClientSender(InetSocketAddress serverAddress, DatagramSocket socket){
		super(socket);
		this.serverAddress = serverAddress;
	}
	
	
	private void sendPacket(String packet_string) throws IOException{
		super.sendPacket(packet_string,serverAddress.getAddress(), serverAddress.getPort(), sendSocket); 	
	}
	

	public void sendPlayerJoinPacket(String playerName){
		Logging.LOGGER.fine("sending join packet...");
		PlayerJoinPacket playerJoinPacket = new PlayerJoinPacket(playerName);
		try {
			this.sendPacket(playerJoinPacket.toJsonString());
		} catch (IOException e) {
			Logging.LOGGER.severe("Could not send player join packet: "+e.getMessage());
		}
	}
	
	public void sendPlayerUpdatePacket(SpaceshipController sc){
		PlayerUpdatePacket playerUpdatePacket = new PlayerUpdatePacket(sc.isUp(), sc.isLeft(), sc.isRight(), sc.isFire());
		try {
			this.sendPacket(playerUpdatePacket.toJsonString());
		} catch (IOException e) {
			Logging.LOGGER.severe("Could not send player update packet: "+e.getMessage());
		}
	}
	
	public void sendSpectatorJoinPacket(){
		try {
			this.sendPacket(new SpectateJoinPacket().toJsonString());
		} catch (IOException e) {
			Logging.LOGGER.severe("Could not send spectator join packet: "+e.getMessage());
		}
	}
	
	public void sendSpectatorPingPacket(){
		try {
			this.sendPacket(new SpectatorPingPacket().toJsonString());
		} catch (IOException e) {
			Logging.LOGGER.severe("Could not send spectator ping packet: "+e.getMessage());
		}
	}
	
	
}
