package aoop.asteroids.model;

import org.json.simple.JSONArray;

public class Explosion extends GameObject {
	
	public static int radius = 20;
	public static int particleAmount = 50;
	public static int maxTimeUntilFadeout = 2000;
	
	
	private int seed = 0;
	private long creationTime;
	
	public Explosion(WrappablePoint location, int seed){
		this(location, seed, System.currentTimeMillis());
	}
	
	public Explosion(WrappablePoint location, int seed, long creationTime){
		super(location, 0,0,Explosion.radius);
		this.setSeed(seed);
		this.creationTime = creationTime;
	}



	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	public long getTime(){
		return System.currentTimeMillis() - creationTime;
	}
	
	public JSONArray toJSON(){
		JSONArray result = super.toJSON();
		result.add(this.getSeed());
		result.add(this.creationTime);
		return result;
	}
	
	public static Explosion fromJSON(JSONArray json){
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		//double velocityX = (double) json.get(2);
		//double velocityY = (double) json.get(3);
		int seed = ((Long) json.get(4)).intValue();
		long creationTime = ((Long) json.get(5));
		return new Explosion(new WrappablePoint(x,y), seed, creationTime); //Client is not interested in the shooter.
	}
	
	public boolean isDestroyed(){
		return this.getTime() > Explosion.maxTimeUntilFadeout;
	}
}
