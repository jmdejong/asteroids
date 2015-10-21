package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

import aoop.asteroids.model.Spaceship;

public class PlayerUpdatePacket extends Packet {
	@SuppressWarnings("unchecked")
	public PlayerUpdatePacket(boolean up, boolean left, boolean right, boolean fire){
		super(Packet.PacketType.PLAYER_UPDATE, new JSONArray());
		this.data.add(up ? 1 : 0);
		this.data.add(left ? 1 : 0);
		this.data.add(right ? 1 : 0);
		this.data.add(fire ? 1 : 0);
	}
	
	public static Spaceship decodePacket(JSONArray data, Spaceship influencedShip){
		boolean up 		= ((Long) data.get(0)) == 1;
		boolean left 	= ((Long) data.get(1)) == 1;
		boolean right 	= ((Long) data.get(2)) == 1;
		boolean fire 	= ((Long) data.get(3)) == 1;
		influencedShip.setUp(up);
		influencedShip.setLeft(left);
		influencedShip.setRight(right);
		influencedShip.setIsFiring(fire);
		
		
		return influencedShip;
	}
}
