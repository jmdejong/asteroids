package aoop.asteroids.model.game;

import aoop.asteroids.HighScores;
import aoop.asteroids.Logging;
import aoop.asteroids.model.Message;
import aoop.asteroids.model.gameobjects.Asteroid;
import aoop.asteroids.model.gameobjects.Bullet;
import aoop.asteroids.model.gameobjects.Explosion;
import aoop.asteroids.model.gameobjects.GameObject;
import aoop.asteroids.model.gameobjects.Spaceship;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.lang.Runnable;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 *	The game class is the backbone of all simulations of the asteroid game. It 
 *	contains all game object and keeps track of some other required variables 
 *	in order to specify game rules.
 *	<p>
 *	The game rules are as follows:
 *	<ul>
 *		<li> All game objects are updated according to their own rules every 
 *			game tick. </li>
 *		<li> Every 200th game tick a new asteroid is spawn. An asteroid cannot 
 *			spawn within a 50 pixel radius of the player. </li>
 *		<li> There is a maximum amount of asteroids that are allowed to be 
 *			active simultaneously. Asteroids that spawn from destroying a 
 *			larger asteroid do count towards this maximum, but are allowed to 
 *			spawn if maximum is exceeded. </li>
 *		<li> Destroying an asteroid spawns two smaller asteroids. I.e. large 
 *			asteroids spawn two medium asteroids and medium asteroids spawn two 
 *			small asteroids upon destruction. </li>
 *		<li> The player dies upon colliding with either a bullet or an 
 *			asteroid. </li>
 *		<li> Destroying every 5th asteroid increases the asteroid limit by 1, 
 *			increasing the difficulty. </li>
 *	</ul>
 *	<p>
 *	This class implements Runnable, so all simulations will be run in its own 
 *	thread. This class extends Observable in order to notify the view element 
 *	of the program, without keeping a reference to those objects.
 *
 *	@author Yannick Stoffers, Wiebe-Marten Wijnja, Michiel de Jong
 */
public final class ServerGame extends BaseGame implements Runnable {
	
	public static long waitingTime = 3000;
	
	/** Asteroid limit. */
	private int asteroidsLimit;
	
	private boolean isSinglePlayer;

	private long startCountdownTime = 0;

	/** Initializes a new game from scratch. */
	public ServerGame (boolean isSinglePlayer, int roundNumber) {
		this.initGameData (roundNumber);
		this.isSinglePlayer = isSinglePlayer;
	}

	/** Sets all game data to hold the values of a new game. */
	public void initGameData (int roundNumber){
		this.aborted = false;
		this.asteroidsLimit = roundNumber == 0 ? 0 : Math.max(1, roundNumber / 3);
		Logging.LOGGER.fine("round number:"+roundNumber);
		this.bullets = new ArrayList <> ();
		this.asteroids = new ArrayList <> ();
		this.spaceships = new ArrayList<> ();
		this.explosions = new ArrayList<> ();
		this.messages = new ArrayList<>();
		//this.ship.reinit ();
		
		while(asteroids.size() < asteroidsLimit){
			this.addRandomAsteroid ();
		}
	}
	

	
	public void addSpaceship(String name, boolean startDestroyed){
		Spaceship s = new Spaceship(name, this.getWidth()/2, this.getHeight()/2);
		
		this.spaceships.add(s);
		s.reinit(this.getWidth()/2, this.getHeight()/2);
		
		if(startDestroyed){
			s.destroy();
		}
	}
	
	/**
	 * Replaces this game's spaceships with the new list of spaceships,<br/>
	 * Afterwards, these are all reset (set to 'alive') and positioned at the center of the screen.<br/>
	 * Used when starting a new game round.<br/>
	 * -> In singleplayer mode, the single ship is rendered in the middle of the screen, pointing up.<br/>
	 * -> In multiplayer, ships are set in a circle, pointing outward.<br/>
	 * @param spaceships the list to replace this game's spaceships with.
	 */
	public void setSpaceships(List<Spaceship> spaceships, boolean reinitShips) {
		this.spaceships = spaceships;
		for(int i=0;i<spaceships.size();i++){
			Spaceship s = spaceships.get(i);
			if(reinitShips){
				s.reinit(this.getWidth()/2,this.getHeight()/2);
			}
			if(spaceships.size()==1){
				s.setLocation(new Point2D.Double(this.getWidth()/2, this.getHeight()/2));
			} else {
				int amount = spaceships.size();
				double rotation = ((2*Math.PI)/amount)*i+ (.5*Math.PI) ;
				int radius = 50;
				s.setLocation(new Point2D.Double(this.getWidth()/2 + radius*Math.sin(rotation), this.getHeight()/2 + radius*Math.cos(rotation)));
				s.setDirection(Math.PI-rotation);
			}
			
		}
		
	}


	/**
	 *	Method invoked at every game tick. It updates all game objects first. 
	 *	Then it adds a bullet if the player is firing. Afterwards it checks all 
	 *	objects for collisions and removes the destroyed objects. Finally the 
	 *	game tick counter is updated and a new asteroid is spawn upon every 
	 *	200th game tick.
	 */
	public void update () {
		for (Asteroid a : this.asteroids){
			a.nextStep ();
		}
		for (Bullet b : this.bullets){
			b.nextStep ();
		}
		for (Spaceship s : this.spaceships) {
			s.nextStep ();
			Bullet bullet = s.makeBulletIfFiring();
			if (bullet!=null){
				this.bullets.add(bullet);
			}
		}
		
		
		this.checkCollisions ();
		this.removeAllDestroyedObjects ();
		
		this.setChanged ();
		this.notifyObservers ();
	}

	/** 
	 *	Adds a randomly sized asteroid at least 50 pixels removed from the 
	 *	player.
	 */
	private void addRandomAsteroid() {
		Random rng = new Random();
		int prob = rng.nextInt (3000);
		Point2D loc;
		do {
			loc = new Point2D.Double (rng.nextInt ((int)this.getWidth()), rng.nextInt ((int)this.getHeight()));
		} while (pointOverlapsCenterCircle(loc));
		
		int size;
		if (prob < 1000){
			size = 40;
		} else if (prob < 2000){
			size = 20;
		} else {
			size = 10;
		}
		
		this.asteroids.add (new Asteroid  (loc, rng.nextDouble () * 6 - 3, rng.nextDouble () * 6 - 3, size, rng.nextDouble()*2*Math.PI- Math.PI));
	}
	
	private boolean pointOverlapsCenterCircle(Point2D p){
		int radius = 100;
		double x,y;
		x = p.getX() - this.getWidth()/2;
		y = p.getY() - this.getHeight()/2;
		return (x*x+y*y) < radius * radius;
	}
	
	/** 
	 *	Checks all objects for collisions and marks them as destroyed upon col-
	 *	lision. All objects can collide with objects of a different type, but 
	 *	not with objects of the same type. I.e. bullets cannot collide with 
	 *	bullets etc.
	 */
	private void checkCollisions (){
		// Destroy all objects that collide.
		for (Bullet b : this.bullets){
			for (Asteroid a : this.asteroids){
				// Check all bullet/asteroid combinations.
				if (a.collidesThroughEdge(b, this.getWidth(), this.getHeight())){
					// Collision -> destroy both objects.
					b.destroy ();
					a.destroy ();
					this.explosions.add(new Explosion(a.getLocation(), 3*a.hashCode()+5*b.hashCode(), a.getRadius(), Color.WHITE.getRGB()));
				}
			}
			for(Spaceship s : this.spaceships){
				if (!s.isDestroyed() && b.collidesThroughEdge(s, this.getWidth(), this.getHeight())){
					// Collision with playerß -> destroy both objects
					
					//Score point if another ship was destroyed by you. (No points for killing yourself, though).
					if(b.getShooter() != s){
						messages.add(new Message(s.getName() + " was shot by " + b.getShooter().getName()));
					}
					
					b.destroy ();
					s.destroy ();
					this.explosions.add(new Explosion(s.getLocation(), 3*s.hashCode()+5*b.hashCode(), s.getRadius(), s.getColour()));
					
				}
			}
			
		}

		for (Asteroid a : this.asteroids){
			// For all asteroids, no cross check with bullets required.
			for(Spaceship s : this.spaceships){
				if (!s.isDestroyed() && a.collidesThroughEdge(s, this.getWidth(), this.getHeight())){
					// Collision with player -> destroy both objects.
					a.destroy ();
					s.destroy ();
					this.explosions.add(new Explosion(a.getLocation(), 3*a.hashCode()+5*s.hashCode(), a.getRadius(), Color.WHITE.getRGB()));
					this.explosions.add(new Explosion(s.getLocation(), 3*s.hashCode()+5*a.hashCode(), s.getRadius(), s.getColour()));
					
					addMessage(s.getName() + " was smashed by an Asteroid");
				}
			}
			
		}
		
	}
	
	
	/**
	 *	Removes all destroyed objects. Destroyed asteroids increase the score 
	 *	and spawn two smaller asteroids if it wasn't a small asteroid. New 
	 *	asteroids are faster than their predecessor and travel in opposite 
	 *	direction.
	 */
	private void removeAllDestroyedObjects() {
		this.asteroids = removeDestroyedObjects(this.asteroids);
		this.bullets = removeDestroyedObjects(this.bullets);
		this.explosions = removeDestroyedObjects(this.explosions);
	}
	
	@SuppressWarnings("unchecked")
	private <GO extends GameObject> List<GO> removeDestroyedObjects(List<GO> objects){
		List<GO> newObjects = new ArrayList<>();
		
		for(GO object : objects){
			if(object.isDestroyed()){
				newObjects.addAll((Collection<? extends GO>) object.getSuccessors());
			}else{
				newObjects.add(object);
			}
		}
		
		return newObjects;
	}

	/**
	 *	Returns whether the game is over. The game is over when all spaceships are destroyed.
	 *
	 *	@return true if game is over, false otherwise.
	 */ 
	private boolean areAllShipsDestroyed() {
		if(this.spaceships.isEmpty()){
			//This situation happens before a player has joined.
			return false;
		}
		for(Spaceship s : this.spaceships){
			if(!s.isDestroyed()){
				return false;
			}
		}
		return true;
	}
	
	protected boolean areAllAsteroidsDestroyed(){
		
		return this.asteroids.isEmpty();
		
		
	}
	
	protected boolean isThereOnlyOneShipLeft(){
		int amount = 0;
		for(Spaceship s : this.spaceships){
			if(!s.isDestroyed()){
				++amount;
			}
		}
		Logging.LOGGER.fine("ships left:"+amount);
		return amount == 1;
	}
	
	/**
	 * Returns the list of one or multiple winners of the current game round.<br>
	 * That is:<br>
	 * -> If all ships have died, the ship(s) that died the last.<br>
	 * -> If there are still some ships alive, then return those.<br>
	 * @return the winners or the round
	 */
	protected List<Spaceship> getWinners(){
		double latestDestroyTime = 0;
		List<Spaceship> result = new ArrayList<Spaceship>();
		
		for(Spaceship s : this.spaceships){
			if(s.getDestroyTime() > latestDestroyTime){
				result = new ArrayList<Spaceship>();
				result.add(s);
				latestDestroyTime = s.getDestroyTime();
			}else if(s.getDestroyTime() == latestDestroyTime){
				result.add(s);
			}
		}
		Logging.LOGGER.fine("Winners:"+result);
		return result;
	}
	

	/** 
	 *	Aborts the game. 
	 *
	 *	@see #run()
	 */
	public void abort ()
	{
		this.aborted = true;
	}
	
	
	
	public Spaceship getSpaceshipRef(int index) {
		if(this.spaceships.size() <= index){
			return null;
		}
		return this.spaceships.toArray(new Spaceship[this.spaceships.size()])[index];
	}
	
	public void destroySpaceship(int index) {
		Spaceship s = getSpaceshipRef(index);
		if(!s.isDestroyed()){
			s.destroy();
		}
	}
	
	
	
	/**
	 * In this function, all victory conditions are checked.<br/>
	 * If they are triggered, corresponding messages are added to this game.<br/>
	 * Afterwards, the time until the game is over is returned as a number.<br/>
	 * <br/>
	 * <b>Victory Conditions:</b></br>
	 * Singleplayer: All asteroids are destroyed.
	 * Multiplayer: Only one ship is left, or all asteroids are destroyed.
	 * @return the time until the game round will end, or Double.POSITIVE_INFINITY if this is not yet known.
	 * 
	 * @see ServerGame#hasEnded()
	 */
	public double checkVictoryConditionsAndReturnTimeUntilRoundEnd() {
	
		if(( this.isSinglePlayer && ((!this.getSpaceships().isEmpty() && this.areAllAsteroidsDestroyed()) || this.areAllShipsDestroyed()))
			|| (!this.isSinglePlayer && (this.getSpaceships().size() > 1) && ((this.areAllAsteroidsDestroyed()) || this.areAllShipsDestroyed() || this.isThereOnlyOneShipLeft() ))){
			
			if(this.startCountdownTime==0){
				startCountdownTime = System.currentTimeMillis();
				if(this.asteroidsLimit != 0){
					List<Spaceship> winners = getWinners();
					for(Spaceship w : winners){
						if(!this.isSinglePlayer || !w.isDestroyed()){
							w.increaseScore();
							
							long highscore = HighScores.getInstance().getScore(w.getName());
							if(w.getScore() > highscore){
								HighScores.getInstance().saveScore(w.getName(), w.getScore());
								this.addMessage(w.getName()+" has beat their high score!");
							}
						}
						
					}
				}
				
				if(this.areAllAsteroidsDestroyed() && this.asteroidsLimit != 0 && !this.areAllShipsDestroyed()){
					this.addMessage("Congradulations! Level Cleared.");
				}
				this.addMessage("Starting Next Round in "+(waitingTime/1000)+" seconds");
			}
			double time = System.currentTimeMillis() - this.startCountdownTime;
			return ServerGame.waitingTime - time;
		}else{
			return Double.POSITIVE_INFINITY;
		}
		
	}

	/**
	 * @return true if the game should restart, false otherwise.
	 */
	public boolean hasEnded() {
		double time = this.checkVictoryConditionsAndReturnTimeUntilRoundEnd();
		Logging.LOGGER.fine("Time until next level:"+time);
		return time <= 0;
	}
    
	
}
