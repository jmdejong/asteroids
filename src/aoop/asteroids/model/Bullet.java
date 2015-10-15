package aoop.asteroids.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.json.simple.JSONArray;

/**
 *	The bullet is the ultimate weapon of the player. It has the same mechanics 
 *	as an asteroid, in which it cannot divert from its trajectory. However, the 
 *	bullet has the addition that it only exists for a certain amount of game 
 *	steps.
 *
 *	@author Yannick Stoffers
 */
public class Bullet extends GameObject
{

	/** 
	 *	The amount of steps this bullet still is allowed to live. When this 
	 *	value drops below 0, the bullet is removed from the game model.
	 */
	private int stepsLeft;
	private Spaceship shooter;
	private int colour = Color.YELLOW.getRGB();
	
	/**
	 *	Constructs a new bullet using the given location and velocity parame-
	 *	ters. The amount of steps the bullet gets to live is by default 60.
	 *
	 *	@param location location of the bullet.
	 *	@param velocityX velocity of the bullet as projected on the X-axis.
	 *	@param velocityY velocity of the bullet as projected on the Y-axis.
	 */
	public Bullet (WrappablePoint location, double velocityX, double velocityY, Spaceship shooter)
	{
		this (location, velocityX, velocityY, 40, shooter);
	}

	/**
     *	Constructs a new bullet using the given location and velocity parame-
     *	ters. The amount of steps the bullet gets to live is set to the given 
     *	value. This constructor is primarily used for the clone () method.
     *
     *	@param location location of the bullet.
     *	@param velocityX velocity of the bullet as projected on the X-axis.
     *	@param velocityY velocity of the bullet as projected on the Y-axis.
     *	@param stepsLeft amount of steps the bullet is allowed to live.
     *
     *	@see #clone()
     */
	private Bullet (WrappablePoint location, double velocityX, double velocityY, int stepsLeft, Spaceship shooter)
	{
		super (location, velocityX, velocityY, 0);
		this.stepsLeft = stepsLeft;
		this.shooter = shooter;
		this.colour = shooter.getColour();
	}
	
	private Bullet (WrappablePoint location, double velocityX, double velocityY, int colour)
	{
		super (location, velocityX, velocityY, 0);
		this.stepsLeft = stepsLeft;
		this.setColour(colour);
	}

	/**
	 *	Method that determines the behaviour of the bullet. The behaviour of a 
	 *	bullet is defined by adding the velocity to its location parameters. 
	 *	The location is then restricted to values between 0 and 800 (size of 
	 *	the window).
	 */
	@Override 
	public void nextStep () 
	{
		super.nextStep();
		this.stepsTilCollide = Math.max (0, this.stepsTilCollide - 1);
		
		this.stepsLeft--;

		if (this.stepsLeft < 0)
			this.destroy ();
	}

	/** Clones the bullet into an exact copy. */
	public Bullet clone ()
	{
		return new Bullet (this.getLocation (), this.velocityX, this.velocityY, this.stepsLeft, this.shooter);
	}
	
	public JSONArray toJSON(){
		JSONArray result = super.toJSON();
		result.add(this.getColour());
		return result;
	
	}
	
	public static Bullet fromJSON(JSONArray json){
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		double velocityX = (double) json.get(2);
		double velocityY = (double) json.get(3);
		int colour = ((Long) json.get(4)).intValue();
		return new Bullet(new WrappablePoint(x,y),velocityX, velocityY, colour); //Client is not interested in the shooter.
	}
	
	public Spaceship getShooter(){
		return this.shooter;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}
}
