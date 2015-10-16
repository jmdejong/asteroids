package aoop.asteroids.model;


import aoop.asteroids.Logging;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import org.json.simple.JSONArray;

/**
 *	GameObject is the abstract superclass for all game objects. I.e. bullets, 
 *	asteroids and spaceships. This class provides some of the basic mechanics, 
 *	such as collision detection, and the desctruction mechanism.
 *
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
	

	
	public Collection<Point2D> getMirrorLocations(double width, double height){
		
		double mirrorX = this.location.getX();
		double mirrorY = this.location.getY();
		
		if(isCloseToXEdge()){
			if (this.location.getX() < width/2){
				mirrorX += width;
			}else{
				mirrorX -= width;
			}
		}
		
		if(isCloseToYEdge()){
			if (this.location.getY() < height/2){
				mirrorY += height;
			}else{
				mirrorY -= height;
			}
		}
		
		
		Set<Point2D> mirrorLocations = new HashSet<>();
		mirrorLocations.add(new Point2D.Double(this.location.getX(),this.location.getY()));
		mirrorLocations.add(new Point2D.Double(mirrorX,this.location.getY()));
		mirrorLocations.add(new Point2D.Double(this.location.getX(),mirrorY));
		mirrorLocations.add(new Point2D.Double(mirrorX,mirrorY));
		
		return mirrorLocations;
	}
}
