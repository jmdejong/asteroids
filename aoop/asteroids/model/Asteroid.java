package aoop.asteroids.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

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
		return new ArrayList <Asteroid> ();
	}

	/** Creates an exact copy of the asteroid. */
	public Asteroid clone ()
	{
		return new Asteroid (this.getLocation (), this.velocityX, this.velocityY, this.radius);
	}

}
