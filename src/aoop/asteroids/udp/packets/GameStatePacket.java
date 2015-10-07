package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONArray;

import aoop.asteroids.Logging;
import aoop.asteroids.model.*;

public class GameStatePacket extends Packet {
	public GameStatePacket(
			Collection<Spaceship> spaceships, 
			Collection<Bullet> bullets, 
			Collection<Asteroid> asteroids,
			Collection<Explosion> explosions
			){
		super(Packet.PacketType.GAMESTATE);
		
		JSONArray jsonSpaceships = new JSONArray();
		for(Spaceship spaceship : spaceships) jsonSpaceships.add(spaceship.toJSON());
		this.data.add(jsonSpaceships);
		
		JSONArray jsonBullets = new JSONArray();
		for(Bullet bullet : bullets) jsonBullets.add(bullet.toJSON());
		this.data.add(jsonBullets);
		
		JSONArray jsonAsteroids = new JSONArray();
		for(Asteroid asteroid : asteroids) jsonAsteroids.add(asteroid.toJSON());
		this.data.add(jsonAsteroids);
		
		JSONArray jsonExplosions = new JSONArray();
		for(Explosion e : explosions) jsonExplosions.add(e.toJSON());
		this.data.add(jsonExplosions);
		
		
		
		
	}
	
	public static ClientGame decodePacket(JSONArray data, ClientGame currentGameState){
		Logging.LOGGER.severe(data.toString());
		JSONArray jsonSpaceships = (JSONArray) data.get(0);
		JSONArray jsonBullets = (JSONArray) data.get(1);
		JSONArray jsonAsteroids = (JSONArray) data.get(2);
		JSONArray jsonExplosions = (JSONArray) data.get(3);
		
		Collection <Spaceship> spaceships = new ArrayList<Spaceship>();
		for(Object s : jsonSpaceships) spaceships.add(Spaceship.fromJSON(((JSONArray) s)));
		currentGameState.setSpaceships(spaceships);
		
		Collection <Bullet> bullets = new ArrayList<Bullet>();
		for(Object b : jsonBullets) bullets.add(Bullet.fromJSON(((JSONArray) b)));
		currentGameState.setBullets(bullets);
		
		Collection <Asteroid> asteroids = new ArrayList<Asteroid>();
		for(Object a : jsonAsteroids) asteroids.add(Asteroid.fromJSON(((JSONArray) a)));
		currentGameState.setAsteroids(asteroids);

		Collection <Explosion> explosions = new ArrayList<Explosion>();
		for(Object a : jsonExplosions) explosions.add(Explosion.fromJSON(((JSONArray) a)));
		currentGameState.setExplosions(explosions);
		
		return currentGameState;
	}
}
