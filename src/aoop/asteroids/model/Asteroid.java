package aoop.asteroids.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

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
	public Asteroid (WrappablePoint location, double velocityX, double velocityY, int radius)
	{
		super (location, velocityX, velocityY, radius);
	}

	/** Updates location of the asteroid with traveled distance. */
	@Override 
	public void nextStep () 
	{
		super.nextStep();
		this.stepsTilCollide = Math.max (0, this.stepsTilCollide - 1);
	}

	/**
	 *	Override this method in factory classes in order to produce offspring 
	 *	upon destruction.
	 *
	 *	@return a collection of the successors.
	 */
	public Collection <Asteroid> getSuccessors ()
	{
		Collection <Asteroid> list = new ArrayList <> ();
		Random r = new Random();
		
		if (radius > 10){
			list.add (new Asteroid (
				this.getLocation (),
				this.getVelocityX () * Math.cos (Math.PI / 2) * 1.5 - this.getVelocityY () * Math.sin (Math.PI / 2) * 1.5 + r.nextDouble(),
				this.getVelocityX () * Math.sin (Math.PI / 2) * 1.5 + this.getVelocityY () * Math.cos (Math.PI / 2) * 1.5 + r.nextDouble(),
				this.radius/2
			));
			list.add (new Asteroid (
				this.getLocation (),
				this.getVelocityX () * Math.cos (- Math.PI / 2) * 1.5 - this.getVelocityY () * Math.sin (- Math.PI / 2) * 1.5 + r.nextDouble(),
				this.getVelocityX () * Math.sin (- Math.PI / 2) * 1.5 + this.getVelocityY () * Math.cos (- Math.PI / 2) * 1.5 + r.nextDouble(),
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
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		double velocityX = (double) json.get(2);
		double velocityY = (double) json.get(3);
		int radius = ((Long) json.get(4)).intValue();
		return new Asteroid(new WrappablePoint(x,y),velocityX, velocityY, radius);
		//TODO: different types of Asteroid classes depending on radius?
	}
	
	

}
