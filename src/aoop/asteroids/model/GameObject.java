package aoop.asteroids.model;


import aoop.asteroids.Logging;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.json.simple.JSONArray;

/**
 *	GameObject is the abstract superclass for all game objects. I.e. bullets, 
 *	asteroids and spaceships. This class provides some of the basic mechanics, 
 *	such as collision detection, and the desctruction mechanism.
 *
 */
public abstract class GameObject 
{
	
	// I think we should not use these as public static variables, but pass them in somewhere
	public static double worldWidth = 800;
	public static double worldHeight = 700;

	
	private WrappablePoint location;
	
	private Point2D domain = new Point((int)GameObject.worldWidth, (int)GameObject.worldHeight);

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
	protected GameObject (Point2D location, double velocityX, double velocityY, int radius)
	{
		this.location = new WrappablePoint(location.getX(), location.getY(), domain.getX(), domain.getY());
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.radius = radius;
		this.stepsTilCollide = 3;
	}

	/** Subclasses need to specify their own behaviour. */
	public void nextStep (){
		this.location.setLocation(this.location.getX()+this.velocityX,this.location.getY()+this.velocityY);
	}

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
	 *
	 *  @return the location of the object.
	 */
	public Point2D getLocation ()
	{
		return (WrappablePoint)this.location.clone();
	}
	
	public void setLocation(Point2D location){
		this.location.setLocation(location);
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
		double distX = this.location.getX() - other.getLocation ().getX();
		double distY = this.location.getY() - other.getLocation ().getY();
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

	public JSONArray toJSON(){
		JSONArray result = new JSONArray();
		result.add(this.location.getX());
		result.add(this.location.getY());
		result.add(this.velocityX);
		result.add(this.velocityY);
		
		
		return result;
	}
	
	public String toString(){
		return this.getClass().toString() + "destroyed?"+this.isDestroyed()+";x="+this.location.getX()+";y="+this.location.getY()+";vX="+this.velocityX+";vY="+this.velocityY;
	}
	
	public boolean isCloseToEdge(){
		return isCloseToXEdge() && isCloseToYEdge();
	}
	
	public boolean isCloseToXEdge(){
		return this.location.getX() < (this.radius*2) || this.location.getX() > this.domain.getX() - (this.radius*2);
	}
	
	public boolean isCloseToYEdge(){
		return this.location.getY() < (this.radius*2) || this.location.getY() > this.domain.getY() - (this.radius*2);
	}
	
	public Point2D getMirrorLocation(){
		double x, y = 0;
		Point2D mirrorLoc = this.getLocation();
		x = this.location.getX();
		y = this.location.getY();
		
		if(isCloseToXEdge()){
			if (this.location.getX() < this.domain.getX()/2){
				x = this.location.getX() + this.domain.getX();
			}else{
				x = this.location.getX() - this.domain.getX();
			}
		}
		
		if(isCloseToYEdge()){
			if (this.location.getX() < this.domain.getY()/2){
				y = this.location.getY() + this.domain.getY();
			}else{
				y = this.location.getY() - this.domain.getY();
			}
		}
		
		return new Point2D.Double(x,y);
	}
}
