package aoop.asteroids.model;

import aoop.asteroids.HighScores;
import aoop.asteroids.Logging;
import aoop.asteroids.udp.ClientConnection;
import aoop.asteroids.udp.Server;
import aoop.asteroids.udp.packets.GameStatePacket;

import java.awt.Color;
import java.awt.Point;
import java.lang.Runnable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
 *		<li> The player dies upon colliding with either a buller or an 
 *			asteroid. </li>
 *		<li> Destroying every 5th asteroid increases the asteroid limit by 1, 
 *			increasing the difficulty. </li>
 *	</ul>
 *	<p>
 *	This class implements Runnable, so all simulations will be run in its own 
 *	thread. This class extends Observable in order to notify the view element 
 *	of the program, without keeping a reference to those objects.
 *
 *	@author Yannick Stoffers
 */
public class Game extends Observable implements Runnable
{
	
	/* TODO:
	 * - remove all references of server from this class
	 *   server should be only an observer
	 * - see if we can make this class smaller
	 * - see if we can orden the functions better
	 */
	
	/** List of spaceships. */
	private List<Spaceship> ships;

	/** List of bullets. */
	private List <Bullet> bullets;

	/** List of asteroids. */
	private List <Asteroid> asteroids;
	
	/** List of explosions. */
	private List <Explosion> explosions;
	 
	/** List of all messages */
	private List <GameMessage> messages;

	/** Random number generator. */
	private static Random rng;

	public static long waitingTime = 3000;

	/** Game tick counter for spawning random asteroids. */
	private int numberOfSpawnedAsteroids;

	/** Asteroid limit. */
	protected int asteroidsLimit;
	
	protected Server server;
	
	
	//private ClientGame cg;

	/** 
	 *	Indicates whether the a new game is about to be started. 
	 *
	 *	@see #run()
	 */
	private boolean aborted;

	private long startCountdownTime = 0;

	/** Initializes a new game from scratch. */
	public Game (Server server, int roundNumber)
	{
		Game.rng = new Random ();
		//this.ship = new Spaceship ();
		this.initGameData (roundNumber);
		//this.cg = cg;
		this.server = server;
	}

	/** Sets all game data to hold the values of a new game. */
	public void initGameData (int roundNumber)
	{
		this.aborted = false;
		this.numberOfSpawnedAsteroids = 0;
		this.asteroidsLimit = roundNumber == 0 ? 0 : Math.max(1, roundNumber / 3);
		Logging.LOGGER.fine("round number:"+roundNumber);
		this.bullets = new ArrayList <> ();
		this.asteroids = new ArrayList <> ();
		this.ships = new ArrayList<> ();
		this.explosions = new ArrayList<> ();
		this.messages = new ArrayList<>();
		//this.ship.reinit ();
		
		while(asteroids.size() < asteroidsLimit){
			this.addRandomAsteroid ();
		}
	}
	
	/** 
	 *	Returns a clone of the ships set, preserving encapsulation.
	 *
	 *	@return a clone of the asteroid set.
	 */
	public List <Spaceship> getSpaceships(){
		
		List <Spaceship> c = new ArrayList <> ();
		for (Spaceship s : this.ships) c.add (s.clone ());
		return c;
	}
	
	public void addSpaceship(String name, boolean startDestroyed){
		Spaceship s = new Spaceship(name);
		
		this.ships.add(s);
		s.reinit();
		
		if(startDestroyed){
			s.destroy();
		}
	}

	/** 
	 *	Returns a clone of the asteroid set, preserving encapsulation.
	 *
	 *	@return a clone of the asteroid set.
	 */
	public List <Asteroid> getAsteroids ()
	{
		List <Asteroid> c = new ArrayList <> ();
		for (Asteroid a : this.asteroids) c.add (a.clone ());
		return c;
	}

	/** 
	 *	Returns a clone of the bullet set, preserving encapsulation.
	 *
	 *	@return a clone of the bullet set.
	 */
	public List <Bullet> getBullets ()
	{
		List <Bullet> c = new ArrayList <> ();
		for (Bullet b : this.bullets) c.add (b.clone ());
		return c;
	}

	/**
	 *	Method invoked at every game tick. It updates all game objects first. 
	 *	Then it adds a bullet if the player is firing. Afterwards it checks all 
	 *	objects for collisions and removes the destroyed objects. Finally the 
	 *	game tick counter is updated and a new asteroid is spawn upon every 
	 *	200th game tick.
	 */
	private void update ()
	{
		for (Asteroid a : this.asteroids) a.nextStep ();
		for (Bullet b : this.bullets) b.nextStep ();
		for (Spaceship s : this.ships) {
			s.nextStep ();
			if (s.isFiring () && !s.isDestroyed())
			{
				double direction = s.getDirection ();
				this.bullets.add (new Bullet(s.getLocation (), s.getVelocityX () + Math.sin (direction) * 15, s.getVelocityY () - Math.cos (direction) * 15, s));
				s.setFired ();
			}
		}
		
		
		
		this.checkCollisions ();
		this.removeDestroyedObjects ();
		
		this.destroyAllShipsOfDisconnectedPlayers();
		
		
		this.setChanged ();
		this.notifyObservers ();
	}

	/** 
	 *	Adds a randomly sized asteroid at least 50 pixels removed from the 
	 *	player.
	 */
	private void addRandomAsteroid ()
	{
		int prob = Game.rng.nextInt (3000);
		WrappablePoint loc;
		do
		{
			loc = new WrappablePoint (Game.rng.nextInt ((int)GameObject.worldWidth), Game.rng.nextInt ((int)GameObject.worldHeight));
		}
		while (pointOverlapsCenterCircle(loc));
		
		int size;
		if (prob < 1000){
			size = 40;
		} else if (prob < 2000){
			size = 20;
		} else {
			size = 10;
		}
		
		this.asteroids.add (new Asteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3, size, Game.rng.nextDouble()*2*Math.PI- Math.PI));
	}
	
	private boolean pointOverlapsCenterCircle(WrappablePoint p){
		int radius = 100;
		double x,y;
		x = p.getX() - GameObject.worldWidth/2;
		y = p.getY() - GameObject.worldHeight/2;
		return (x*x+y*y) < radius * radius;
	}
	
	private boolean pointDoesNotOverlapAnySpaceships(WrappablePoint p, int radius){
		for(Spaceship s : this.getSpaceships()){
			double x,y;
			x = p.getX() - s.locationX;
			y = p.getY() - s.locationY;
			
			if((x*x+y*y) < radius*radius){
				return false;
			}
		}
		return true;
	}

	/** 
	 *	Checks all objects for collisions and marks them as destroyed upon col-
	 *	lision. All objects can collide with objects of a different type, but 
	 *	not with objects of the same type. I.e. bullets cannot collide with 
	 *	bullets etc.
	 */
	private void checkCollisions ()
	{ // Destroy all objects that collide.
		for (Bullet b : this.bullets)
		{ // For all bullets.
			for (Asteroid a : this.asteroids)
			{ // Check all bullet/asteroid combinations.
				if (a.collides (b))
				{ // Collision -> destroy both objects.
					b.destroy ();
					a.destroy ();
					this.explosions.add(new Explosion(new WrappablePoint(a.locationX, a.locationY), 3*a.hashCode()+5*b.hashCode(), a.getRadius(), Color.WHITE.getRGB()));
				}
			}
			for(Spaceship s : this.ships){
				if (!s.isDestroyed() && b.collides (s))
				{ // Collision with playerß -> destroy both objects
					
					//Score point if another ship was destroyed by you. (No points for killing yourself, though).
					if(/*b.getShooter() != null && */b.getShooter() != s){
						//b.getShooter().increaseScore();
						//server.sendMessagePacket(s.getName() + " was shot by " + b.getShooter().getName());
						messages.add(new GameMessage(s.getName() + " was shot by " + b.getShooter().getName()));
					}
					
					b.destroy ();
					s.destroy ();
					this.explosions.add(new Explosion(new WrappablePoint(s.locationX, s.locationY), 3*s.hashCode()+5*b.hashCode(), s.getRadius(), s.getColour()));

					
// 					server.sendPlayerLosePacket(this.ships.indexOf(s));
				}
			}

			
		}

		for (Asteroid a : this.asteroids)
		{ // For all asteroids, no cross check with bullets required.
			for(Spaceship s : this.ships){
				if (!s.isDestroyed() && a.collides (s))
				{ // Collision with player -> destroy both objects.
					a.destroy ();
					s.destroy ();
					this.explosions.add(new Explosion(new WrappablePoint(a.locationX, a.locationY), 3*a.hashCode()+5*s.hashCode(), a.getRadius(), Color.WHITE.getRGB()));
					this.explosions.add(new Explosion(new WrappablePoint(s.locationX, s.locationY), 3*s.hashCode()+5*a.hashCode(), s.getRadius(), s.getColour()));

					
					//server.sendMessagePacket(s.getName() + " was smashed by an Asteroid");
					addMessage(s.getName() + " was smashed by an Asteroid");
// 					server.sendPlayerLosePacket(this.ships.indexOf(s));
				}
			}
			
		}
		
	}
	
	public void addMessage(String message){
		messages.add(new GameMessage(message));
	}

	/**
	 *	Removes all destroyed objects. Destroyed asteroids increase the score 
	 *	and spawn two smaller asteroids if it wasn't a small asteroid. New 
	 *	asteroids are faster than their predecessor and travel in opposite 
	 *	direction.
	 */
	private void removeDestroyedObjects ()
	{
		
		List <Asteroid> newAsts = new ArrayList <> ();
		for (Asteroid a : this.asteroids)
		{
			if (a.isDestroyed ())
			{
				newAsts.addAll (a.getSuccessors ());
			}
			else {
				newAsts.add (a);
			}
		}
		this.asteroids = newAsts;

		List <Bullet> newBuls = new ArrayList <> ();
		for (Bullet b : this.bullets) {
			if (!b.isDestroyed ()){
				newBuls.add (b);
			}
		}
		this.bullets = newBuls;
		
		for(int i=explosions.size()-1;i >= 0;i--){
			if(explosions.get(i).isDestroyed()){
				explosions.remove(i);
			}
		}
	}

	/**
	 *	Returns whether the game is over. The game is over when all spaceships are destroyed.
	 *
	 *	@return true if game is over, false otherwise.
	 */ 
	protected boolean areAllShipsDestroyed ()
	{
		if(this.ships.isEmpty()){//This situation happens before a player has joined.
			return false;
		}
		for(Spaceship s : this.ships){
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
		if(this.ships.isEmpty()){//This situation happens before a player has joined.
			return false;
		}
		int amount = 0;
		for(Spaceship s : this.ships){
			if(!s.isDestroyed()){
				++amount;
			}
		}
		Logging.LOGGER.fine("ships left:"+amount);
		return amount == 1;
	}
	
/*	protected boolean isGameOver(){
		return this.isThereOnlyOneShipLeft() || this.areAllAsteroidsDestroyed();
	}*/
	
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
		
		for(Spaceship s : this.ships){
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
			if (!this.isGameOver ())
			{
				executionTime = System.currentTimeMillis ();
				this.update ();
				executionTime -= System.currentTimeMillis ();
				sleepTime = 40 - executionTime;
			}
			else {
				
				sleepTime = 100;
				this.server.restartGame();
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

	public Spaceship getSpaceshipRef(int index) {
		if(this.ships.size() <= index){
			return null;
		}
		return this.ships.toArray(new Spaceship[this.ships.size()])[index];
	}
	
	private void destroyAllShipsOfDisconnectedPlayers(){
		List<ClientConnection> playerConnections = this.server.getPlayerConnections();
		for(int i=playerConnections.size()-1; i>=0; i--){
			Spaceship s = ships.get(i);
			if(!s.isDestroyed() && playerConnections.get(i).isDisconnected()){
				s.destroy();
			}
		}
		
	}

	/**
	 * Adds multiple spaceships at once to the game.
	 * Used when starting a new game round.
	 * In singleplayer mode, ship is rendered in the middle of the screen, pointing up.
	 * In multiplayer, ships are set in a circle, pointing outward.
	 * @param spaceships
	 */
	/* ehmm, this also removes all spaceships currently in the game*/
	public void addSpaceships(List<Spaceship> spaceships) {
		this.ships = spaceships;
		for(int i=0;i<spaceships.size();i++){
			Spaceship s = spaceships.get(i);
			s.reinit();
			if(spaceships.size()==1){
				s.setLocation(new WrappablePoint(GameObject.worldWidth/2, GameObject.worldHeight/2));
			}else{
				int amount = spaceships.size();
				double rotation = ((2*Math.PI)/amount)*i+ (.5*Math.PI) ;
				int radius = 50;
				int dx = 0;
				int dy = 0;
				s.setLocation(new WrappablePoint((GameObject.worldWidth/2) + radius*Math.sin(rotation), (GameObject.worldHeight/2) + radius*Math.cos(rotation)));
				s.setDirection(Math.PI-rotation);
			}
			
		}
		
	}

	public List <Explosion> getExplosions() {
		return explosions;
	}
	
	public List<GameMessage> getMessages(){
		checkMessages();
		// return a clone of the message list
		// GameMessage is immutable anyways so no need to clone the messages
		return messages.subList(0, messages.size());
	}
	
	/** check whether the messages should still be shown
	 * removes the messages that are no longer relevant
	 */
	public void checkMessages(){
		for(int i=messages.size()-1;i >= 0;i--){
			if(messages.get(i).isOver()){
				messages.remove(i);
			}
		}
	}

	public double timeUntilOver() {
	
		if(    ( server.isSinglePlayerMode() && ((!this.getSpaceships().isEmpty() && this.areAllAsteroidsDestroyed()) || this.areAllShipsDestroyed()))
			|| (!server.isSinglePlayerMode() && (this.getSpaceships().size() > 1) && ((this.areAllAsteroidsDestroyed()) || this.areAllShipsDestroyed() || this.isThereOnlyOneShipLeft() ))){
			
			if(this.startCountdownTime==0){
				startCountdownTime = System.currentTimeMillis();
				if(this.asteroidsLimit != 0){
					List<Spaceship> winners = getWinners();
					for(Spaceship w : winners){
						if(!server.isSinglePlayerMode() || !w.isDestroyed()){
							w.increaseScore();
							
							long highscore = HighScores.getInstance().getScore(w.getName());
							if(w.getScore() > highscore){
								HighScores.getInstance().saveScore(w.getName(), w.getScore());
								//this.server.sendMessagePacket(w.getName()+" has beat their high score!");
								this.addMessage(w.getName()+" has beat their high score!");
							}
						}
						
					}
				}
				
				if(this.areAllAsteroidsDestroyed() && this.asteroidsLimit != 0){
					//server.sendMessagePacket("Congradulations! Level Cleared.");
					this.addMessage("Congradulations! Level Cleared.");
				}
				//server.sendMessagePacket("Starting Next Round in "+(waitingTime/1000)+" seconds");
				this.addMessage("Starting Next Round in "+(waitingTime/1000)+" seconds");
			}
			double time = System.currentTimeMillis() - this.startCountdownTime;
			return Game.waitingTime - time;
		}else{
			return Double.POSITIVE_INFINITY;
		}
		
	}

	protected boolean isGameOver() {
		Logging.LOGGER.fine("Time until next level:"+this.timeUntilOver());
		return this.timeUntilOver() <= 0;
	}
    
	
}
