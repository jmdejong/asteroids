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
 *	GameObject is the abstract superclass for all spatial game objects. I.e. bullets, 
 *	asteroids and spaceships. This class provides some of the basic mechanics, 
 *	such as movement and collision detection, and the destruction mechanism.
 *
 */
public abstract class GameObject 
{
	
	/* TODO:
	 * DONE:
	 * - maybe change all _X and _Y to 2d vectors (for example Point2D.double) -> Decided against it.
	 * - move worldWidth and worldHeight somewhere else and avoid using them where possible
	 * - make a function getMirrorLocations that returns a list of 1, 2 of 4 points
	 * - look critically where we actually need WrappablePoint instead of Point2D.double -> nowhere
	 */
	
	/**
	 * Location of this GameObject in the two-dimensional plane.
	 */
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
	protected GameObject (Point2D location, double velocityX, double velocityY, int radius){
		this.location = new Point2D.Double(location.getX(), location.getY());//, domain.getX(), domain.getY());
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.radius = radius;
	}

	/** 
	 * This function determines how this GameObject moves from one frame to the next.
	 * Subclasses might want to specify their own behaviour.
	 */
	public void nextStep (){
		this.location.setLocation(this.location.getX()+this.velocityX,this.location.getY()+this.velocityY);
	}

	/** Marks the object for destruction by setting the destroyed value to true. 
	 *  Destruction happens in the Game class that has a reference to this GameObject, by removing this reference.
	 *  Afterwards it can be safely garbage collected.
	 * */
	public void destroy (){
		this.destroyed = true;
	}
	
	/** 
	 *	Returns the radius of the object. 
	 *
	 *	@return radius of the object in amount of pixels.
	 */
	public int getRadius (){
		return radius;
	}

	/**
	 *
	 *  @return the location of the object.
	 */
	public Point2D getLocation (){
		return new Point2D.Double(this.location.getX(), this.location.getY());
	}
	/**
	 * @return The location of the object, modulo the given `width` (for the x-position) and `height` (for the y-position)
	 */
	public Point2D getWrappedLocation(double width, double height){
		return new Point2D.Double(Utils.floorMod(this.location.getX(), width), Utils.floorMod(this.location.getY(), height));
	}
	
	/**
	 * 
	 * @param location The new two-dimensional location the GameObject should be at.
	 */
	public void setLocation(Point2D location){
		this.location.setLocation(location);
	}

	/** 
	 *	@return the velocity in X direction.
	 */
	public double getVelocityX ()
	{
		return this.velocityX;
	}

	/** 
	 *	@return the velocity in Y direction.
	 */
	public double getVelocityY ()
	{
		return this.velocityY;
	}

	/**
	 *	@return true if the object is marked for destruction, false otherwise.
	 */
	public boolean isDestroyed ()
	{
		return this.destroyed;
	}
	
	
	/** 
	 * An improved version of the collision detection that will also detect collisions through world-wrap edges 
	 * @param other The GameObject to check collision against.
	 * @param width The width at which the world wraps horizontally
	 * @param height The height at which the world wraps vertically
	 * */
	public boolean collidesThroughEdge(GameObject other, double width, double height){
		Point2D thisLocation = this.getWrappedLocation(width, height);
		Point2D closestLocation = new Point2D.Double(
			Utils.getClosestPoint(thisLocation.getX(), other.getLocation().getX(), width),
			Utils.getClosestPoint(thisLocation.getY(), other.getLocation().getY(), height)
		);
		double minDistance = this.getRadius() + other.getRadius();
		return thisLocation.distanceSq(closestLocation)<(minDistance*minDistance);
	}

	/**
	 * @return a JSONArray containing the important spatial information of this GameObject (x,y, velocityX, velocityY).
	 */
	@SuppressWarnings("unchecked")
	public JSONArray toJSON(){
		JSONArray result = new JSONArray();
		result.add(this.location.getX());
		result.add(this.location.getY());
		result.add(this.velocityX);
		result.add(this.velocityY);
		
		
		return result;
	}
	
	//TODO: This method was used for testing. Maybe remove now? 
	public String toString(){
		return this.getClass().toString() + "destroyed?"+this.isDestroyed()+";x="+this.location.getX()+";y="+this.location.getY()+";vX="+this.velocityX+";vY="+this.velocityY;
	}
	
	
	/**
	 * This function returns a Collection of possibly multiple locations at which a graphical representation of this GameObject should be drawn.<br/>By drawing each of these, we ensure that objects that are half across the screen edge will draw their other half at the other side of the screen.
	 * @param width The width at which the world wraps horizontally
	 * @param height The height at which the world wraps vertically
	 * @return A list of one or more locations at which the object might appear
	 */
	public Collection<Point2D> getMirrorLocations(double width, double height){
		return getMirrorLocations(width,height,this.radius);
	
	}
	/**
	 * This function returns a Collection of possibly multiple locations at which a graphical representation of this GameObject should be drawn.<br/>By drawing each of these, we ensure that objects that are half across the screen edge will draw their other half at the other side of the screen.
	 * <br/> In this case, a radius other than the radius defined inside the GameObject itself is used.
	 * @param width The width at which the world wraps horizontally
	 * @param height The height at which the world wraps vertically
	 * @param radius The radius to use.
	 * @return A list of one or more locations at which the object might appear
	 */	
	public Collection<Point2D> getMirrorLocations(double width, double height, double radius){
		
		Point2D l = this.getWrappedLocation(width, height);
		double x = l.getX();
		double y = l.getY();
		double mirrorX = x;
		double mirrorY = y;
		
		if(x < (radius*2)){
			mirrorX += width;
		}else if (x > width - (radius*2)){
			mirrorX -= width;
		}
		
		if (y < (radius*2)){
			mirrorY += height;
		}else if (y > height - (radius*2)){
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
