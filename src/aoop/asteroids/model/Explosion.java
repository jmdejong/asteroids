package aoop.asteroids.model;

import org.json.simple.JSONArray;
import java.awt.geom.Point2D;

public class Explosion extends GameObject {
	
	//public static int radius = 50;
	public static int particleAmount = 10;
	public static int maxTimeUntilFadeout = 2000;
	
	
	private int seed = 0;
	private int color;
	private long creationTime;
	
	
	public Explosion(Point2D location, int seed, int radius, int color){
		this(location, seed, radius, color, System.currentTimeMillis());
	}
	
	public Explosion(Point2D location, int seed, int radius, int color, long creationTime){
		super(location, 0,0,radius);
		this.setSeed(seed);
		this.setColor(color);
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
		result.add(this.radius);
		result.add(this.getColor());
		result.add(this.creationTime);
		
		return result;
	}
	
	public static Explosion fromJSON(JSONArray json){
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		//double velocityX = (double) json.get(2);
		//double velocityY = (double) json.get(3);
		int seed = ((Long) json.get(4)).intValue();
		int radius = ((Long) json.get(5)).intValue();
		int color = ((Long) json.get(6)).intValue();
		long creationTime = ((Long) json.get(7));
		
		return new Explosion(new Point2D.Double(x,y), seed, radius, color, creationTime); //Client is not interested in the shooter.
	}
	
	public boolean isDestroyed(){
		return this.getTime() > Explosion.maxTimeUntilFadeout;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
