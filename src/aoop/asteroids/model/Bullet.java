package aoop.asteroids.model;

import java.awt.Color;
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
	/* TODO:
	 * - don't use the number of steps, but the distance traveled
	 */
	/** 
	 *	The amount of steps this bullet still is allowed to live. When this 
	 *	value drops below 0, the bullet is removed from the game model.
	 */
	private int stepsLeft;
	
	/**
	 * The Spaceship that shot this bullet.
	 * Although there exists friendly-fire (a Spaceship can destroy itself with its own Bullets), we want this information to know what player killed whom.
	 */
	private Spaceship shooter;
	
	/**
	 * The colour this bullet should have, in the standard sRGB colour space.
	 * @see Color#getRGB()
	 */
	private int colour = Color.YELLOW.getRGB();
	
	/**
	 *	Constructs a new bullet using the given location and velocity parame-
	 *	ters. The amount of steps the bullet gets to live is by default 60.
	 *
	 *	@param location location of the bullet.
	 *	@param velocityX velocity of the bullet as projected on the X-axis.
	 *	@param velocityY velocity of the bullet as projected on the Y-axis.
	 */
	public Bullet (Point2D location, double velocityX, double velocityY, Spaceship shooter)
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
	private Bullet (Point2D location, double velocityX, double velocityY, int stepsLeft, Spaceship shooter)
	{
		super (location, velocityX, velocityY, 0);
		this.stepsLeft = stepsLeft;
		this.shooter = shooter;
		this.colour = shooter.getColour();
	}
	
	/**
	 * This Private constructor is only used on the client-side.<br/>
	 * Notice that stepsLeft is not specified; Therefore, the bullet will 'live forever' until it is removed on the server-side (i.e. not passed in a future GameUpdatePacket).
	 * @see Bullet#Bullet(Point2D, double, double, int, Spaceship)
	 */
	private Bullet (Point2D location, double velocityX, double velocityY, int colour)
	{
		super (location, velocityX, velocityY, 0);
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
		this.stepsLeft--;

		if (this.stepsLeft < 0)
			this.destroy ();
	}

	/** Clones the bullet into an exact copy. */
	public Bullet clone ()
	{
		return new Bullet (this.getLocation (), this.velocityX, this.velocityY, this.stepsLeft, this.shooter);
	}
	
	
	/**
	 * @return a JSONArray containing the important characteristics of this Bullet
	 * (Besides the spatial information, this is the colour)
	 * @see GameObject#toJSON()
	 * @see Bullet#fromJSON()
	 */
	@SuppressWarnings("unchecked")
	public JSONArray toJSON(){
		JSONArray result = super.toJSON();
		result.add(this.getColour());
		return result;
	
	}
	
	
	/**
	 * Reconstructs a Bullet from the given JSONArray.
	 * @see Bullet#toJSON()
	 */
	public static Bullet fromJSON(JSONArray json){
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		double velocityX = (double) json.get(2);
		double velocityY = (double) json.get(3);
		int colour = ((Long) json.get(4)).intValue();
		return new Bullet(new Point2D.Double(x,y),velocityX, velocityY, colour); //Client is not interested in the shooter.
	}
	
	/**
	 * 
	 * @return Returns the Spaceship that shot this Bullet, or `null` if this is not known. <br/><i>(Note that since this information is not passed to the Client, this will always return `null` on the client-side)</i>
	 */
	public Spaceship getShooter(){
		return this.shooter;
	}

	/**
	 * @return the current Bullet's colour.
	 */
	public int getColour() {
		return colour;
	}

	/**
	 * @param colour A colour to set this Bullet's colour to.
	 */
	public void setColour(int colour) {
		this.colour = colour;
	}
}
