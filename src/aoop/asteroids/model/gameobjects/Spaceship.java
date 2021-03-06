package aoop.asteroids.model.gameobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;

import org.json.simple.JSONArray;


/**
 *	This class represents the player in the Asteroids game. A spaceship is able 
 *	to shoot every 20 game ticks (twice per second). 
 *	<p>
 *	A massive difference with other game objects is that the spaceship has a 
 *	certain direction in which it is pointed. This influences the way it is 
 *	drawn and determines the direction of spawned bullets. Although the game 
 *	setting is in outer space, the spaceship is subject to traction and will 
 *	slowly deaccelerate.
 *
 *	@author Wiebe-Marten Wijnja, Michiel de Jong
 */
public class Spaceship extends GameObject {
	
	private static int timeUntilFireAgain = 10;

	/** Direction the spaceship is pointed in. */
	private double direction;

	/** Amount of game ticks left, until the spaceship can fire again. */
	private int stepsTilFire;

	/** Score of the player. I.e. amount of destroyed asteroids. */
	private int score;

	/** Indicates whether the fire button is pressed. */
	private boolean isFiring;

	/** Indicates whether the accelerate button is pressed. */
	private boolean up;

	/** Indicates whether the turn right button is pressed. */
	private boolean right;

	/** Indicates whether the turn left button is pressed. */
	private boolean left;
	
	private int colour;
	
	private String name;
	
	
	
	/** Set when a ship is destroyed. Used to determine the winner after all ships have been destroyed.
	 *  Obviously, for alive ships this is +Infinity.
	 * */
	private double destroyTime = Double.POSITIVE_INFINITY;

	/** Constructs a new spaceship with default values. */
	public Spaceship (String name, double xpos, double ypos) {
		this (new Point2D.Double(xpos, ypos), 0, 0, 15, 0, false, 0, false, Double.POSITIVE_INFINITY, name, 0);
		
		this.colour = Color.getHSBColor(new Random(name.hashCode()).nextFloat(), 1, .9f).getRGB();
	}

	/**
	 *	Constructs a new spaceship using all specified information. Fields that 
	 *	do not have a parameter are initialized to default values. This 
	 *	constructor is primarily used for cloning a spaceship. All parameters 
	 *	are important to access from a cloned instance.
	 *
	 *	@param location location of the spaceship.
	 *	@param velocityX velocity in X direction.
	 *	@param velocityY velocity in Y direction.
	 *	@param radius radius.
	 *	@param direction direction.
	 *	@param up indicator for accelarating button.
	 *	@param score score.
	 */
	private Spaceship (Point2D location, double velocityX, double velocityY, int radius, double direction, boolean up, int score, boolean destroyed, double destroyTime, String name, int colour) {
		super (location, velocityX, velocityY, radius);
		this.setDirection(direction);
		this.up             = up;
		this.isFiring       = false;
		this.left           = false;
		this.right          = false;
		this.stepsTilFire   = 0;
		this.score          = score;
		this.destroyed      = destroyed;
		this.destroyTime    = destroyTime;
		this.name           = name;
		this.setColour(colour);
	}



	/** 
	 *	Resets all parameters to default values, so a new game can be started. 
	 */
	public void reinit (double xpos,double ypos) {
		this.setLocation(new Point2D.Double(xpos,ypos));
		this.velocityX      = 0;
		this.velocityY      = 0;
		this.setDirection(0);
		this.up             = false;
		this.isFiring       = false;
		this.left           = false;
		this.right          = false;
		this.destroyed      = false;
		this.stepsTilFire   = 0;
		this.destroyTime    = Double.POSITIVE_INFINITY;
	}

	/**
	 *	Sets the isFiring field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setIsFiring (boolean b) {
		this.isFiring = b;
	}

	/**
	 *	Sets the left field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setLeft (boolean b) {
		this.left = b;
	}

	/**
	 *	Sets the right field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setRight (boolean b) {
		this.right = b;
	}

	/**
	 *	Sets the up field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setUp (boolean b) {
		this.up = b;
	}	

	/**
	 *	Defines the behaviour of the spaceship. In each game tick the ship 
	 *	turns when a turn button is pressed. The speed at which it turns is 2% 
	 *	of a full rotation per game tick. Afterwards the spaceships velocity 
	 *	will be updated if the player wants to accelerate. The velocity however 
	 *	is resticted to 10 pixels per game tick in both X and Y direction. 
	 *	Afterwards the location of the ship will be updated and the velocity 
	 *	decreased to account for traction.
	 */
	@Override 
	public void nextStep () {
		super.nextStep();
		
		// Update direction if turning.
		if (this.left ) this.setDirection(this.getDirection() - 0.04 * Math.PI);
		if (this.right) this.setDirection(this.getDirection() + 0.04 * Math.PI);
		if (this.up){ // Update speed if accelerating, but constrain values.
			this.velocityX = Math.max (-10, Math.min (10, this.velocityX + Math.sin (getDirection()) * 0.4));
			this.velocityY = Math.max (-10, Math.min (10, this.velocityY - Math.cos (getDirection()) * 0.4));
		}

		// Decrease speed due to traction.
		this.velocityX *= 0.99;
		this.velocityY *= 0.99;

		// Decrease firing step counter.
		if (this.stepsTilFire != 0){
			this.stepsTilFire--;
		}
		
	}
	
	public Bullet makeBulletIfFiring(){
		if (this.isFiring () && !this.isDestroyed()){
			double direction = this.getDirection ();
			double x = this.getLocation().getX() + Math.sin(direction)*(this.radius+1);
			double y = this.getLocation().getY() - Math.cos(direction)*(this.radius+1);
			Bullet bullet = new Bullet(new Point2D.Double(x,y), this.getVelocityX () + Math.sin (direction) * 15, this.getVelocityY () - Math.cos (direction) * 15, this);
			this.setFired ();
			return bullet;
		} else {
			return null;
		}
	}

	/**
	 *	Returns a copy of the spaceship. Note that only interesting fields are 
	 *	copied into the clone.
	 *
	 *	@return an exact copy of the spaceship.
	 */
	@Override
	public Spaceship clone () {
		return new Spaceship (this.getLocation (), this.velocityX, this.velocityY, this.radius, this.getDirection(), this.up, this.score, this.isDestroyed(), this.destroyTime, this.getName(), this.getColour());
	}

	/**
	 *	Returns current direction.
	 *
	 *	@return the direction.
	 */
	public double getDirection () {
		return this.direction;
	}

	/**
	 *	Returns whether the spaceship is accelerating.
	 *
	 *	@return true if up button is pressed, false otherwise.
	 */
	public boolean isAccelerating () {
		return this.up;
	}

	/**
	 *	Returns whether the spaceship is firing.
	 *
	 *	@return true if the spacehip is firing, false otherwise.
	 */
	public boolean isFiring () {
		return this.isFiring && this.stepsTilFire == 0;
	}

	/**
	 *	Sets the fire tick counter to 20. For the next 20 game ticks this 
	 *	spaceship is not allowed to fire.
	 */
	public void setFired () {
		this.stepsTilFire = Spaceship.timeUntilFireAgain;
	}

	/** Increments score field. */
	public void increaseScore () {
		this.score++;

	}

	/**
	 *	Returns current score.
	 *
	 *	@return the score.
	 */
	public int getScore () {
		return this.score;
	}
	
	/**
	 * Resets the score back to 0.
	 * Only used in singleplayer-mode.
	 */
	public void resetScore() {
		this.score = 0;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSON(){
		JSONArray result = super.toJSON();
		result.add(this.getDirection());
		result.add(this.isAccelerating() ? 1 : 0);
		result.add(this.getScore());
		result.add(this.isDestroyed() ? 1 : 0);
		result.add(this.getName());
		result.add(this.getColour());
		return result;
	}
	
	public static Spaceship fromJSON(JSONArray json){
		double x = (double) json.get(0);
		double y = (double) json.get(1);
		double velocityX = (double) json.get(2);
		double velocityY = (double) json.get(3);
		double direction = (double) json.get(4);
		boolean isAccelerating = ((long) json.get(5)) == 1;
		int score = ((Long) json.get(6)).intValue();
		boolean destroyed = ((long) json.get(7)) == 1;
		String name = (String) json.get(8);
		int colour = ((Long) json.get(9)).intValue();
		return new Spaceship(new Point2D.Double(x,y) ,velocityX, velocityY, 15, direction, isAccelerating, score, destroyed, Double.POSITIVE_INFINITY, name, colour);
	}

	public double getDestroyTime() {
		return destroyTime;
	}

	@Override
	public void destroy(){
		
		super.destroy();
		this.destroyTime = (double) System.currentTimeMillis();
	}
	
	public String getName() {
		return name;
	}

	public int getColour() {
		return colour;
	}
	
	public void setColour(int colour) {
		this.colour = colour;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}
	
}
