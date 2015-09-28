package aoop.asteroids.model;

import java.awt.Point;

/** 
 *	This class is a factory class for a regular asteroid. It forwards all given 
 *	parameters to the Asteroid class and sets its radius to 10 pixels.
 *
 *	@author Yannick Stoffers
 */
public class SmallAsteroid extends Asteroid
{
    
    /** 
	 *	Constructs a new small asteroid. I.e. the radius will be 10 pixels.
	 *
	 *	@param location location of the asteroid.
	 *	@param velocityX velocity in X direction.
	 *	@param velocityY velocity in Y direction.
	 */
    public SmallAsteroid (Point location, double velocityX, double velocityY)
	{
		super (location, velocityX, velocityY, 10);
	}

}
