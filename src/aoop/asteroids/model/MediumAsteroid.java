package aoop.asteroids.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

/** 
 *	This class is a factory class for a regular asteroid. It forwards all given 
 *	parameters to the Asteroid class and sets its radius to 20 pixels.
 *
 *	@author Yannick Stoffers
 */
public class MediumAsteroid extends Asteroid
{
	
	/** 
	 *	Constructs a new medium asteroid. I.e. the radius will be 20 pixels.
	 *
	 *	@param location location of the asteroid.
	 *	@param velocityX velocity in X direction.
	 *	@param velocityY velocity in Y direction.
	 */
	public MediumAsteroid (Point location, double velocityX, double velocityY)
	{
		super (location, velocityX, velocityY, 20);
	}

	/**
	 *	Returns the full set of successors upon destruction of the current 
	 *	object. Since the current object is a medium asteroid, two small 
	 *	asteroids will be returned with speed equal and opposite from each 
	 *	other. Speed is in both X and Y direction 50 percent higher than for 
	 *	the current object.
	 *
	 *	@return a collection of two small asteroids.
	 */
	@Override 
	public Collection <Asteroid> getSuccessors ()
	{
		ArrayList <Asteroid> list = new ArrayList <> ();
		list.add (new SmallAsteroid (this.getLocation (), this.getVelocityX () * Math.cos (Math.PI / 2) * 1.5 - this.getVelocityY () * Math.sin (Math.PI / 2) * 1.5, 
															this.getVelocityX () * Math.sin (Math.PI / 2) * 1.5 + this.getVelocityY () * Math.cos (Math.PI / 2) * 1.5));
		list.add (new SmallAsteroid (this.getLocation (), this.getVelocityX () * Math.cos (- Math.PI / 2) * 1.5 - this.getVelocityY () * Math.sin (- Math.PI / 2) * 1.5, 
															this.getVelocityX () * Math.sin (- Math.PI / 2) * 1.5 + this.getVelocityY () * Math.cos (- Math.PI / 2) * 1.5));
		return list;
	}

}
