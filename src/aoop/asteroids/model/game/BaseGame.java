package aoop.asteroids.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import aoop.asteroids.Asteroids;
import aoop.asteroids.Utils;
import aoop.asteroids.model.Message;
import aoop.asteroids.model.gameobjects.Asteroid;
import aoop.asteroids.model.gameobjects.Bullet;
import aoop.asteroids.model.gameobjects.Explosion;
import aoop.asteroids.model.gameobjects.Spaceship;

/**
 * BaseGame contains all logic that is shared by both ClientGame and ServerGame.
 * As both contain lists of GameObjects, and both should be run on a Thread (with the {@link BaseGame#run()} function iterating many times per second), there is a lot of similar functionality between these classes.
 * 
 * @author qqwy
 *
 */
public abstract class BaseGame extends Observable{

	private double width = Asteroids.worldWidth;
	private double height = Asteroids.worldHeight;
	
	protected List <Spaceship> spaceships = new ArrayList<Spaceship>();
	/** List of bullets. */
	protected List <Bullet> bullets = new ArrayList<Bullet>();

	/** List of asteroids. */
	protected List <Asteroid> asteroids = new ArrayList<Asteroid>();
	
	/** List of explosions. */
	protected List <Explosion> explosions = new ArrayList<Explosion>();
	
	/** List of game messages. */
	protected List <Message> messages = new ArrayList<Message>();
	
	
	protected int roundNumber = 1;
	
	/** 
	 *	Indicates whether the current game is about to be terminated.
	 *
	 *	@see #run()
	 */
	protected boolean aborted;
	

	/** 
	 *	@return a clone of the asteroids  list, preserving encapsulation.
	 */
	public List <Asteroid> getAsteroids ()
	{
		return Utils.deepCloneList(this.asteroids);
	}

	/** 
	 *	@return a clone of the bullets list, preserving encapsulation.
	 */
	public List <Bullet> getBullets ()
	{
		return Utils.deepCloneList(this.bullets);
	}
	
	/**
	 * 
	 * @return all currently existing Explosions.
	 */
	public List <Explosion> getExplosions() {
		return Utils.deepCloneList(this.explosions);
	}
	
	/** 
	 *	@return a clone of the spaceships list, preserving encapsulation.
	 */
	public List <Spaceship> getSpaceships(){
		
		return Utils.deepCloneList(this.spaceships);
	}
	
	/**
	 * A cloned list is returned but as Messages are themselves immutable, this new list contains references to the actual Message objects.
	 * <br/>
	 * Note that checkMessages() is executed beforehand. As Messages are only used outside of ServerGame, this ensures that only messages that should still be used are returned, but they are only removed whenever we need the up-to-date message list.
	 * @return all currently existing Messages.
	 */
	public List<Message> getMessages(){
		removeDestroyedMessages();
		return messages.subList(0, messages.size());
	}
	
	public void addMessage(String message){
		this.messages.add(new Message(message));
	}
	
	/** 
	 * Check whether each of the currently existing messages should still be shown and
	 * removes the messages that are no longer relevant.
	 */
	public void removeDestroyedMessages(){
		for(int i=messages.size()-1;i >= 0;i--){
			if(messages.get(i).isDestroyed()){
				messages.remove(i);
			}
		}
	}
	
	public int getWidth(){
		return (int)this.width;
	}
	
	public int getHeight(){
		return (int)this.height;
	}
	
	protected abstract void update();
	
	protected abstract boolean hasEnded();
	
	/**
	 *	This method allows this object to run in its own thread, making sure 
	 *	that the same thread will not perform non essential computations for 
	 *	the game. The thread will not stop running until the program is quit. 
	 *	If the game is aborted or the player died, it will wait 100 
	 *	milliseconds before reevaluating and continuing the simulation. 
	 *	<p>
	 *	While the game is not aborted and the player is still alive, it will 
	 *	measure the time it takes the program to perform a game tick and wait 
	 *	40 minus execution time milliseconds to do it all over again. This 
	 *	allows the game to update every 40th millisecond, thus keeping a steady 
	 *	25 frames per second. 
	 *	<p>
	 *	Decrease waiting time to increase fps. Note 
	 *	however, that all game mechanics will be faster as well. I.e. asteroids 
	 *	will travel faster, bullets will travel faster and the spaceship may 
	 *	not be as easy to control.
	 */
	public void run ()
	{ // Update -> sleep -> update -> sleep -> etc...
		long executionTime, sleepTime;
		do
		{
			if (!this.hasEnded ())
			{
				executionTime = System.currentTimeMillis ();
				this.update ();
				executionTime -= System.currentTimeMillis ();
				sleepTime = 40 - executionTime;
			}
			else {
				this.setChanged();
				this.notifyObservers();
				return;
			}

			try
			{
				Thread.sleep (sleepTime);
			}
			catch (InterruptedException e)
			{
				System.err.println ("Could not perform action: Thread.sleep(...)");
				System.err.println ("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).");
				e.printStackTrace ();
			}
		}while  (!this.aborted);
	}
}
