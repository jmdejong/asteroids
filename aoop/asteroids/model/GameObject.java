package aoop.asteroids.model;

import java.awt.Point;

/**
 *	GameObject is the abstract superclass for all game objects. I.e. bullets, 
 *	asteroids and spaceships. This class provides some of the basic mechanics, 
 *	such as collision detection, and the desctruction mechanism.
 *
 *	@author Yannick Stoffers
 */
public abstract class GameObject 
{

	/** Location on the X axis. */
	protected double locationX;

	/** Location on the Y axis. */
	protected double locationY;

	/** Velocity in X direction. */
	protected double velocityX;

	/** Velocity in Y direction. */
	protected double velocityY;

	/** Radius of the object. */
	protected int radius;

	/** Holds true if object collided with another object, false otherwise. */
	protected boolean destroyed;

	/** 
	 *	Counts the amount of game ticks left, until this object is allowed to 
	 *	collide. 
	 */
	protected int stepsTilCollide;

	/**
	 *	Constructs a new game object with the specified location, velocity and 
	 *	radius.
	 *	
	 *	@param location location of the game object.
	 *	@param velocityX velocity in X direction.
	 *	@param velocityY velocity in Y direction.
	 *	@param radius radius of the object.
	 */
	protected GameObject (Point location, double velocityX, double velocityY, int radius)
	{
		this.locationX = location.x;
		this.locationY = location.y;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.radius = radius;
		this.stepsTilCollide = 3;
	}

	/** Subclasses need to specify their own behaviour. */
	abstract public void nextStep ();

	/** Destroys the object by setting the destroyed value to true. */
	public void destroy ()
	{
		this.destroyed = true;
	}
	
	/** 
	 *	Returns the radius of the object. 
	 *
	 *	@return radius of the object in amount of pixels.
	 */
	public int getRadius ()
	{
		return radius;
	}

	/**
	 *	This method combines both location fields in a java.awt.Point object by 
	 *	casting them to integers. The point object is returned.
	 *
	 *	@return the location of the object.
	 */
	public Point getLocation ()
	{
		return new Point ((int)this.locationX, (int)this.locationY);
	}

	/** 
	 *	Returns the velocity in X direction.
	 *
	 *	@return the velocity in X direction.
	 */
	public double getVelocityX ()
	{
		return this.velocityX;
	}

	/** 
	 *	Returns the velocity in Y direction.
	 *
	 *	@return the velocity in Y direction.
	 */
	public double getVelocityY ()
	{
		return this.velocityY;
	}

	/**
	 *	Returns whether the object is destroyed.
	 *
	 *	@return true if the object is destroyed, false otherwise.
	 */
	public boolean isDestroyed ()
	{
		return this.destroyed;
	}

	/**
	 *	Given some other game object, this method checks whether the current 
	 *	object and the given object collide with each other. It does this by 
	 *	measuring the distance between the objects and checking whether it is 
	 *	larger than the sum of the radii. Furthermore both objects should be 
	 *	allowed to collide.
	 *
	 *	@param other the other object that it may collide with.
	 *	@return true if object collides with given object, false otherwise.
	 */
	public boolean collides (GameObject other) 
	{
		double distX = this.locationX - other.getLocation ().x;
		double distY = this.locationY - other.getLocation ().y;
		double distance = Math.sqrt(distX * distX + distY * distY);
		
		return distance < this.getRadius() + other.getRadius() && this.stepsTilCollide () == 0 && other.stepsTilCollide () == 0;
	}

	/**
	 *	Returns the amount of game ticks it takes until this object is allowed 
	 *	to collide.
	 *
	 *	@return the amount of game ticks it takes until this object is allowed 
	 *		to collide.
	 */
	public int stepsTilCollide ()
	{
		return this.stepsTilCollide;
	}

}
