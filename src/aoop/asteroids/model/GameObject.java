package aoop.asteroids.model;


import aoop.asteroids.Logging;
import aoop.asteroids.Utils;

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
	 * - change all _X and _Y to 2d vectors (for example Point2D.double)
	 * DONE:
	 * - make a function getMirrorLocations that returns a list of 1, 2 of 4 points
	 * - look critically where we actually need WrappablePoint instead of Point2D.double -> nowhere
	 */
	
	public static double worldWidth = 800;
	public static double worldHeight = 700;
	
	
	private Point2D.Double location;
	
	/** Velocity in X direction. */
	protected double velocityX;
	
	/** Velocity in Y direction. */
	protected double velocityY;
	
	/** Radius of the object. */
	protected int radius;
	
	/** Holds true if object collided with another object, false otherwise. */
	protected boolean destroyed;
	
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
		this.location = new Point2D.Double(location.getX(), location.getY());//, domain.getX(), domain.getY());
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.radius = radius;
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
	public Point2D getLocation (){
		return new Point2D.Double(this.location.getX(), this.location.getY());//this.location.clone();
	}
	
	public Point2D getWrappedLocation(double width, double height){
		return new Point2D.Double(Utils.floorMod(this.location.getX(), width), Utils.floorMod(this.location.getY(), height));
	}
	
	public void setLocation(Point2D location){
		this.location = new Point2D.Double(location.getX(), location.getY());//setLocation(location);
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
	
	
	/** An improved version of collides that will also detect collisions through edges */
	public boolean collidesThroughEdge(GameObject other, double width, double height){
		Point2D thisLocation = this.getWrappedLocation(width, height);
		Point2D closestLocation = new Point2D.Double(
			Utils.getClosestPoint(thisLocation.getX(), other.getLocation().getX(), width),
			Utils.getClosestPoint(thisLocation.getY(), other.getLocation().getY(), height)
		);
		double minDistance = this.getRadius() + other.getRadius();
		return thisLocation.distanceSq(closestLocation)<(minDistance*minDistance);
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
	
	
	public Collection<Point2D> getMirrorLocations(double width, double height){
		
		Point2D l = this.getWrappedLocation(width, height);
		double x = l.getX();
		double y = l.getY();
		double mirrorX = x;
		double mirrorY = y;
		
		if(x < (this.radius*2)){
			mirrorX += width;
		}else if (x > width - (this.radius*2)){
			mirrorX -= width;
		}
		
		if (y < (this.radius*2)){
			mirrorY += height;
		}else if (y > height - (this.radius*2)){
			mirrorY -= height;
		}
		
		Set<Point2D> mirrorLocations = new HashSet<>();
		mirrorLocations.add(new Point2D.Double(x,y));
		mirrorLocations.add(new Point2D.Double(mirrorX,y));
		mirrorLocations.add(new Point2D.Double(x,mirrorY));
		mirrorLocations.add(new Point2D.Double(mirrorX,mirrorY));
		
		return mirrorLocations;
	}
}
