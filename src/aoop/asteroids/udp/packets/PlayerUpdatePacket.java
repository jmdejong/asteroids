package aoop.asteroids.udp.packets;

import org.json.simple.JSONArray;

import aoop.asteroids.model.gameobjects.Spaceship;

/**
 * Sent by a Player multiple time per second. It contains keypresses of the different keys that control a spaceship.<br>
 * Each keypress is serialized as '1' or '0', as this is shorter than storing boolean 'true'/'false' values in JSON.
 * @author qqwy
 *
 */
public class PlayerUpdatePacket extends Packet {
	@SuppressWarnings("unchecked")
	public PlayerUpdatePacket(boolean up, boolean left, boolean right, boolean fire){
		super(Packet.PacketType.PLAYER_UPDATE, new JSONArray());
		this.data.add(up ? 1 : 0);
		this.data.add(left ? 1 : 0);
		this.data.add(right ? 1 : 0);
		this.data.add(fire ? 1 : 0);
	}
	
	/**
	 * For a given data and spaceship, updates the passed `influencedShip` with the input keypresses contained in `data`. A reference to this ship is then returned.
	 */
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
