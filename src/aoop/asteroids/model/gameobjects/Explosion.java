package aoop.asteroids.model.gameobjects;

import org.json.simple.JSONArray;
import java.awt.geom.Point2D;

/**
 * The Explosion class is a special kind of GameObject that spawns a Particle cloud where it is created.
 * This effect is procedurally generated using a given integer `seed`, which is passed to the clients.
 * This means that a certain Explosion will look exactly the same between clients.
 * @author qqwy
 *
 */
public class Explosion extends GameObject {
	
	/**
	 * The amount of particles that will spawn from a certain Explosion.
	 * `10` is a good number for reasonably beautiful and still reasonably fast explosions.
	 */
	public static int particleAmount = 10;
	
	/**
	 * The amount of time it maximally takes for all particles to fade out
	 * (The actual fade-out time is randomized slightly per-particle to make the effect look more dynamic.)
	 */
	public static int maxTimeUntilFadeout = 2000;
	
	/**
	 * The random number that determines how this explosion will look.
	 */
	private int seed = 0;
	
	/**
	 * The colour this explosion should be. In the standard sRGB space.
	 * @see java.awt.Color#getRGB()
	 */
	private int colour;
	
	/**
	 * The moment (in milliseconds) at which this Explosion was created.
	 * This is used as the starting offset for the particle animation.
	 */
	private long creationTime;
	
	/**
	 * 
	 * @param location 2d-location this Explosion should appear at
	 * @param seed this number determines how the Explosion will look.
	 * @param radius Used as starting-radius for the Explosion. (Larger -> Larger looking explosion)
	 * @param colour The colour this explosion should be.
	 */
	public Explosion(Point2D location, int seed, int radius, int colour){
		this(location, seed, radius, colour, System.currentTimeMillis());
	}
	
	/**
	 * This private constructor can create Explosions that have started at a different time than `now`.<br/>
	 * Used to deserialize explosions at the client-side.
	 */
	private Explosion(Point2D location, int seed, int radius, int colour, long creationTime){
		super(location, 0,0,radius);
		this.seed = seed;
		this.colour=colour;
		this.creationTime = creationTime;
	}
	
	/** Clones the bullet into an exact copy. */
	@Override
	public Explosion clone () {
		return new Explosion (this.getLocation (), this.seed, this.radius, this.colour, this.creationTime);
	}

	
	/**
	 * @return the current seed
	 */
	public int getSeed() {
		return seed;
	}

	
	/**
	 * @return the time (in milliseconds) that have elapsed since the creationTime of this Explosion
	 * @see Explosion.creationTime
	 */
	public long getTime(){
		return System.currentTimeMillis() - creationTime;
	}
	
	/**
	 * @return a JSONArray containing the important characteristics of this Explosion
	 * (Besides the spatial information, this is the seed, radius, colour and creationTime)
	 * @see GameObject#toJSON()
	 * @see Explosion#fromJSON()
	 */
	@SuppressWarnings("unchecked")
	public JSONArray toJSON(){
		JSONArray result = super.toJSON();
		result.add(this.getSeed());
		result.add(this.radius);
		result.add(this.getColour());
		result.add(this.creationTime);
		
		return result;
	}
	
	/**
	 * Reconstructs an Explosion from the given JSONArray.
	 * @see Explosion#toJSON()
	 */
	public static Explosion fromJSON(JSONArray json){
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		int seed = ((Long) json.get(4)).intValue();
		int radius = ((Long) json.get(5)).intValue();
		int color = ((Long) json.get(6)).intValue();
		long creationTime = ((Long) json.get(7));
		
		return new Explosion(new Point2D.Double(x,y), seed, radius, color, creationTime); //Client is not interested in the shooter.
	}
	
	/**
	 * Different from other GameObjects, Explosions should be destroyed after a certain amount of time.<br/>
	 * Therefore returns true if the Explosion is around longer than the static Explosion.maxTimeUntilFadeout
	 * @see Explosion.maxTimeUntilFadeout
	 * @see GameObject#isDestroyed()
	 */
	@Override
	public boolean isDestroyed(){
		return this.getTime() > Explosion.maxTimeUntilFadeout;
	}

	/**
	 * @return this Explosion's current colour, in the standard sRGB colour space
	 * @see java.awt.Color#getRGB()
	 */
	public int getColour() {
		return colour;
	}

	
}
