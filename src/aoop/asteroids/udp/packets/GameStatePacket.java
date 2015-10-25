package aoop.asteroids.udp.packets;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;

import aoop.asteroids.model.game.ClientGame;
import aoop.asteroids.model.gameobjects.Asteroid;
import aoop.asteroids.model.gameobjects.Bullet;
import aoop.asteroids.model.gameobjects.Explosion;
import aoop.asteroids.model.gameobjects.Spaceship;

/**
 * The GameStatePacket is sent by the Server whenever the game-state has changed.
 * Besides the current roundNumber, it contains lists of all GameObjects.
 * @author Wiebe-Marten Wijnja, Michiel de Jong
 *
 */
public class GameStatePacket extends Packet {
	
	@SuppressWarnings("unchecked")
	public GameStatePacket(
			int roundNumber,
			List<Spaceship> spaceships, 
			List<Bullet> bullets, 
			List<Asteroid> asteroids,
			List<Explosion> explosions
			){
		super(Packet.PacketType.GAMESTATE);
		
		this.data.add(roundNumber);
		
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
	
	/**
	 * Decodes a given GameStatePacket, and sets the corresponding fields on the passed ClientGame to their updated value.
	 * @param data
	 * @param currentGameState
	 */
	public static void decodePacket(JSONArray data, ClientGame currentGameState){
		
		int roundNumber =((Long)( data.get(0))).intValue();
		currentGameState.checkIfRoundHasEndedAndUpdateRoundNumber(roundNumber);
		
		JSONArray jsonSpaceships = (JSONArray) data.get(1);
		JSONArray jsonBullets = (JSONArray) data.get(2);
		JSONArray jsonAsteroids = (JSONArray) data.get(3);
		JSONArray jsonExplosions = (JSONArray) data.get(4);
		
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
		
		
	}
}
