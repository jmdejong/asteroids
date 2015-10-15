package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;


import aoop.asteroids.Logging;
import aoop.asteroids.model.*;

public class GameStatePacket extends Packet {
	public GameStatePacket(
			List<Spaceship> spaceships, 
			List<Bullet> bullets, 
			List<Asteroid> asteroids,
			List<Explosion> explosions,
			List<GameMessage> messages
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
		
		
		JSONArray jsonMessages = new JSONArray();
		for(GameMessage e : messages) jsonMessages.add(e.toJSON());
		this.data.add(jsonMessages);
		
		
	}
	
	public static void decodePacket(JSONArray data, ClientGame currentGameState){
		
		JSONArray jsonSpaceships = (JSONArray) data.get(0);
		JSONArray jsonBullets = (JSONArray) data.get(1);
		JSONArray jsonAsteroids = (JSONArray) data.get(2);
		JSONArray jsonExplosions = (JSONArray) data.get(3);
		JSONArray jsonMessages = (JSONArray) data.get(4);
		
		List <Spaceship> spaceships = new ArrayList<Spaceship>();
		for(Object s : jsonSpaceships) spaceships.add(Spaceship.fromJSON(((JSONArray) s)));
		currentGameState.setSpaceships(spaceships);
		
		List <Bullet> bullets = new ArrayList<Bullet>();
		for(Object b : jsonBullets) bullets.add(Bullet.fromJSON(((JSONArray) b)));
		currentGameState.setBullets(bullets);
		
		List <Asteroid> asteroids = new ArrayList<Asteroid>();
		for(Object a : jsonAsteroids) asteroids.add(Asteroid.fromJSON(((JSONArray) a)));
		currentGameState.setAsteroids(asteroids);
		
		List <Explosion> explosions = new ArrayList<Explosion>();
		for(Object a : jsonExplosions) explosions.add(Explosion.fromJSON(((JSONArray) a)));
		currentGameState.setExplosions(explosions);
		
		List <GameMessage> messages = new ArrayList<>();
		for(Object a : jsonMessages) messages.add(GameMessage.fromJSON(((JSONArray) a)));
		currentGameState.setMessages(messages);
		
		
	}
}
