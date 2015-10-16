package aoop.asteroids.model;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.json.simple.JSONArray;

/**
 *	GameObject is the abstract superclass for all game objects. I.e. bullets, 
 *	asteroids and spaceships. This class provides some of the basic mechanics, 
 *	such as collision detection, and the desctruction mechanism.
 *
 *	@author Yannick Stoffers
 */
public abstract class GameObject 
{
	
	/* TODO:
	 * - move worldWidth and worldHeight somewhere else and avoid using them where possible
	 * - look critically where we actually need WrappablePoint instead of Point2D.double
	 * - change all _X and _Y to 2d vectors (for example Point2D.double)
	 * - make a function getMirrorLocations that returns a list of 1, 2 of 4 points
	 */
	
	public static double worldWidth = 800;
	public static double worldHeight = 700;

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
	protected GameObject (WrappablePoint location, double velocityX, double velocityY, int radius)
	{
		this.locationX = location.getX();
		this.locationY = location.getY();
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.radius = radius;
		this.stepsTilCollide = 3;
	}

	/** Subclasses need to specify their own behaviour. */
	public void nextStep (){
		this.setLocation(new WrappablePoint(this.locationX+this.velocityX,this.locationY+this.velocityY));
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
	 *	This method combines both location fields in a java.awt.Point object by 
	 *	casting them to integers. The point object is returned.
	 *
	 *	@return the location of the object.
	 */
	public WrappablePoint getLocation ()
	{
		return new WrappablePoint (this.locationX, this.locationY, GameObject.worldWidth, GameObject.worldHeight);
	}
	
	public void setLocation(WrappablePoint location){
		location.setDomain(GameObject.worldWidth, GameObject.worldHeight);
		this.locationX = location.getX();
		this.locationY = location.getY();
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

	public JSONArray toJSON(){
		JSONArray result = new JSONArray();
		result.add((double)this.locationX);
		result.add((double)this.locationY);
		result.add(this.velocityX);
		result.add(this.velocityY);
		
		return result;
	}
	
	public String toString(){
		return this.getClass().toString() + "destroyed?"+this.isDestroyed()+";x="+this.locationX+";y="+this.locationY+";vX="+this.velocityX+";vY="+this.velocityY;
	}
	
	public boolean isCloseToEdge(){
		return isCloseToXEdge() && isCloseToYEdge();
	}
	
	public boolean isCloseToXEdge(){
		return this.locationX < (this.radius*2) || this.locationX > GameObject.worldWidth - (this.radius*2);
	}
	
	public boolean isCloseToYEdge(){
		return this.locationY < (this.radius*2) || this.locationY > GameObject.worldHeight - (this.radius*2);
	}
	
	public Point2D getMirrorLocation(){
		double x, y = 0;
		Point2D mirrorLoc = (WrappablePoint) this.getLocation().clone();
		x = this.getLocation().x;
		y = this.getLocation().y;
		
		if(isCloseToXEdge()){
			if (this.locationX < GameObject.worldWidth/2){
				x = this.locationX + GameObject.worldWidth;
			}else{
				x = this.locationX - GameObject.worldWidth;
			}
		}
		
		if(isCloseToYEdge()){
			if (this.locationY < GameObject.worldHeight/2){
				y = this.locationY + GameObject.worldHeight;
			}else{
				y = this.locationY - GameObject.worldHeight;
			}
		}
		
		return new Point2D.Double(x,y);
	}
}
