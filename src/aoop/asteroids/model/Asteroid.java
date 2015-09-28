package aoop.asteroids.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONArray;

/**
 *	An asteroid is a game object that needs to be destroyed in order to 
 *	increase the score of the player. Destroying an asteroid is done by making 
 *	it collide with another non asteroid game object, i.e. shooting it or 
 *	flying into it. Note that flying into it, also destroys the spaceship, 
 *	which will end the game.
 *
 *	@author Yannick Stoffers
 */
public class Asteroid extends GameObject
{
    
    /**
     *	Constructs a new asteroid at the specified location, with specified 
     *	velocities in both X and Y direction and the specified radius.
     *
     *	@param location the location in which to spawn an asteroid.
     *	@param velocityX the velocity in X direction.
     *	@param velocityY the velocity in Y direction.
     *	@param radius radius of the asteroid.
     */
	public Asteroid (Point location, double velocityX, double velocityY, int radius)
	{
		super (location, velocityX, velocityY, radius);
	}

	/** Updates location of the asteroid with traveled distance. */
	@Override 
	public void nextStep () 
	{
		this.stepsTilCollide = Math.max (0, this.stepsTilCollide - 1);
		this.locationX = (800 + this.locationX + this.velocityX) % 800;
		this.locationY = (800 + this.locationY + this.velocityY) % 800;
	}

	/**
	 *	Override this method in factory classes in order to produce offspring 
	 *	upon destruction.
	 *
	 *	@return a collection of the successors.
	 */
	public Collection <Asteroid> getSuccessors ()
	{
		ArrayList <Asteroid> list = new ArrayList <> ();
		if (radius > 10){
			list.add (new Asteroid (
				this.getLocation (),
				this.getVelocityX () * Math.cos (Math.PI / 2) * 1.5 - this.getVelocityY () * Math.sin (Math.PI / 2) * 1.5,
				this.getVelocityX () * Math.sin (Math.PI / 2) * 1.5 + this.getVelocityY () * Math.cos (Math.PI / 2) * 1.5,
				this.radius/2
			));
			list.add (new Asteroid (
				this.getLocation (),
				this.getVelocityX () * Math.cos (- Math.PI / 2) * 1.5 - this.getVelocityY () * Math.sin (- Math.PI / 2) * 1.5,
				this.getVelocityX () * Math.sin (- Math.PI / 2) * 1.5 + this.getVelocityY () * Math.cos (- Math.PI / 2) * 1.5,
				this.radius/2
			));
		}
		return list;
	}


	/** Creates an exact copy of the asteroid. */
	public Asteroid clone ()
	{
		return new Asteroid (this.getLocation (), this.velocityX, this.velocityY, this.radius);
	}
	
	public JSONArray toJSON(){
		JSONArray result = super.toJSON();
		result.add(this.radius);
		return result;
	}
	
	public static Asteroid fromJSON(JSONArray json){
		int x = ((Long) json.get(0)).intValue();
		int y = ((Long) json.get(1)).intValue();
		double velocityX = (double) json.get(2);
		double velocityY = (double) json.get(3);
		int radius = ((Long) json.get(4)).intValue();
		return new Asteroid(new Point(x,y),velocityX, velocityY, radius);
		//TODO: different types of Asteroid classes depending on radius?
	}
	
	

}
