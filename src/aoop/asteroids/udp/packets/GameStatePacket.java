package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONArray;
import aoop.asteroids.model.*;

public class GameStatePacket extends Packet {
	public GameStatePacket(
			Collection<Spaceship> spaceships, 
			Collection<Bullet> bullets, 
			Collection<Asteroid> asteroids

			){
		super(Packet.PacketType.GAMESTATE);
		
		JSONArray jsonSpaceships = new JSONArray();
		for(Spaceship spaceship : spaceships){
			jsonSpaceships.add(spaceship.toJSON());
		}
		JSONArray jsonBullets = new JSONArray();
		for(Bullet bullet : bullets){
			jsonBullets.add(bullet.toJSON());
		}
		JSONArray jsonAsteroids = new JSONArray();
		for(Asteroid asteroid : asteroids){
			jsonAsteroids.add(asteroid.toJSON());
		}
		
		
		this.data.add(jsonSpaceships);
		this.data.add(jsonBullets);
		this.data.add(jsonAsteroids);
		
	}
}
