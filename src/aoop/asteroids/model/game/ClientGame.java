package aoop.asteroids.model.game;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import aoop.asteroids.Logging;
import aoop.asteroids.model.Message;
import aoop.asteroids.model.Sound;
import aoop.asteroids.model.gameobjects.Asteroid;
import aoop.asteroids.model.gameobjects.Bullet;
import aoop.asteroids.model.gameobjects.Explosion;
import aoop.asteroids.model.gameobjects.Spaceship;

/**
 * ClientGame is the representation of the game as it is seen on the Client-Side. This is rendered by {@link aoop.asteroids.gui.AsteroidsPanel} after each update step.<br>
 * ClientGame attempts to iterate the current game state by moving objects on their predicted paths, if no connection to the Server is currently available.<br>
 * This is repeated until a new GameUpdate packet arrives, and all internal lists of GameObjects are replaced with the up-to-date ones from the Server.
 * 
 * @author Wiebe-Marten Wijnja, Michiel de Jong
 *
 */
public final class ClientGame extends BaseGame implements Runnable {
	
	/* TODO:

	 * Done:
	 * - Make this class more readable
	 * - All collections of GameObjects are now lists.
	 * - Maybe do the sound stuff somewhere else.
	 *   This is the model part and the sound would be the view part
	 *   Even if this class is responsible for calling the playSound commands 
	 *   (which I don't like but don't know how to solve), the code to play
	 *   sounds could better have its own class
	 * - Is this the right place for storing the spaceshipController? -> No. moved it to Client.
	 */
	
	
	/**
	 * A reference to the current image that is used as background-image.
	 */
	private BufferedImage bgimage;
	
	/**
	 * The current offset of this background-image, as it should slowly move.
	 */
	private Point2D.Double bgPos = new Point2D.Double();
	
	/**
	 * When set to true, the ClientGame will not run its update function; the game state will remain exactly as it is and thus the graphical output on the screen will remain the same.<br/>
	 * This is used to `freeze` the game when the connection with the Server has been lost.
	 */
	private boolean frozen = false;
	
	/**
	 * A reference to the Sound class, to play sounds with.
	 */
	private Sound sound = new Sound();
	
	/**
	 * The starting constructor only needs to set the round number.
	 * The rest of the objects is filled in as soon as GameState packets are received from the Server.
	 */
	public ClientGame(){
		
		setBackgroundImage(this.roundNumber);
	}
	
	/**
	 * The background-image is chosen from a list of `tscapeN.jpg` images, where N is in the range (0..7).
	 * The actual image used is dependent on the current round number.
	 * The used images are tiling (repeating) in both the horizontal and the vertical direction.
	 * @param index
	 */
	public void setBackgroundImage(int index){
		try {
			this.bgimage = ImageIO.read(new File("images/tscape"+(index%7)+".jpg"));
		} catch (IOException e) {
			Logging.LOGGER.warning("The image `images/tscape"+(index%7)+".jpg` could not be loaded.");
		}
	}
	
	/**
	 * This function is run every game tick, and updates the game state (using the predictions from calling GameObject#nextStep() on all objects), unless {@link ClientGame#isFrozen()} is true.
	 */
	protected void update(){
		
		synchronized (this){
			
			removeDestroyedMessages();
			
			if(!this.isFrozen()){
				for (Asteroid a : this.asteroids) a.nextStep ();
				for (Bullet b : this.bullets) b.nextStep ();
				for (Spaceship s : this.spaceships) s.nextStep();
				
				
				Random r = new Random(113*this.roundNumber);
				double xVelocity = 3 * (r.nextDouble() - .5);
				double yVelocity = 3 * (r.nextDouble() - .5);
				this.bgPos = new Point2D.Double(this.bgPos.x+xVelocity, this.bgPos.y+yVelocity);
			}
			
			this.setChanged ();
			this.notifyObservers ();
		}
	}
	
	/**
	 * @param ships the new spaceships to use. Also plays a `player death` sound when a spaceship has been destroyed.
	 */
	public void setSpaceships(List<Spaceship> spaceships){
		int oldLivingShipsSize = 0;
		for(Spaceship s : this.spaceships){
			if(!s.isDestroyed()){
				++oldLivingShipsSize;
			}
		}
		int newLivingShipsSize = 0;
		for(Spaceship s : spaceships){
			if(!s.isDestroyed()){
				++newLivingShipsSize;
			}
		}
		if(newLivingShipsSize < oldLivingShipsSize){
			this.sound.playSound("PlayerDeathNew0.wav");
		}
		
		
		this.spaceships = spaceships;
	}
	
	/**
	 * @param asteroids the new asteroids to use.
	 */
	public void setAsteroids(List<Asteroid> asteroids){
		this.asteroids = asteroids;
	}
	
	
	/**
	 * @param bullets the new bullets to use. Plays a `shoot` sound when a new bullet is added.
	 */
	public void setBullets(List<Bullet> bullets){
		int bulletsSize = this.bullets.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(bullets.size() > bulletsSize){
			sound.playSound("ShootNew2.wav");
		}
		
		this.bullets = bullets;
	}
	
	
	/**
	 * @param explosions the new explosions to use. Plays an `explosion` sound when a new explosion is added.
	 */
	public void setExplosions(List<Explosion> explosions) {
		
		int explosionsSize = this.explosions.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(explosions.size() > explosionsSize){
			sound.playExplosionSound(explosions.get(explosions.size()-1));
		}
		
		
		this.explosions = explosions;
	}
	
	/**
	 * Returns a reference to the spaceship with a given name, or `null` if it does not exist in the spaceships list.
	 * @param name the name the spaceship should have
	 */
	public Spaceship getSpaceship(String name){
		for(Spaceship s : spaceships){
			if(s.getName().equals(name)){
				return s;
			}
		}
		return null;
	}
		
	/**
	 * Adds all mesages from the list newMessages, but only if they do not yet exist in the current list of messages.
	 */
	public void addPossiblyNewMessages(List<Message> newMessages){
		for(Message m : newMessages){
			if(!this.messages.contains(m)){
				this.messages.add(m);
			}
		}
	}
	
	
	/**
	 * @return the image that should currently be used as background image.
	 */
	public BufferedImage getBgImage(){
		return bgimage;
	}
	
	
	/**
	 * @return the position that should be used as offset to draw the background image.
	 */
	public Point2D getBgPos(){
		return (Point2D.Double)this.bgPos.clone();
	}
	
	/**
	 * When called, will freeze the ClientGame. All drawing and updates to GameObject positions will cease, until {@link ClientGame#unFreeze()} is called.
	 * @see this.frozen
	 */
	public void freeze(){
		this.frozen = true;
		
	}
	
	/**
	 * Unfreezes the game.
	 * @see ClientGame#freeze()
	 */
	public void unFreeze(){
		
		this.frozen = false;
	}


	/**
	 * After calling this, this Thread will quit itself.
	 */
	public void abort(){
		this.aborted = true;
	}
	
	
	/** 
	 * Check if the round has ended and update round number
	 */
	public void checkIfRoundHasEndedAndUpdateRoundNumber(int roundNumber){
		if(this.roundNumber != roundNumber){
			sound.playSound("NextLevelNew0.wav");
			this.roundNumber = roundNumber;
			setBackgroundImage(this.roundNumber);
			this.bgPos = new Point2D.Double(0,0);
		}
	}
	
	/**
	 * Starts playing the Background Music if this has not already started.
	 */
	public void playBGMIfNotAlreadyStarted(){
		
		//Do not play music while waiting for a connection, to synchronize audio between multiple PC's in close physical proximity.
		if(this.roundNumber == 0){
			return;
		}
		
		if(!this.sound.hasBgmStarted()){
			this.sound.playSound("background_music_bassline.wav", true);
		}
	}
	
	/**
	 * @return true if the ClientGame is currently frozen, i.e. {@link ClientGame#freeze()} has been called before.
	 */
	public boolean isFrozen(){
		return frozen;
	}
	
	/**
	 * Never true for this child class of BaseGame.
	 */
	public boolean hasEnded(){
		return false;
	}
}
