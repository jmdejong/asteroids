package aoop.asteroids.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import javax.imageio.ImageIO;

import aoop.asteroids.Asteroids;
import aoop.asteroids.Logging;
import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.udp.Client;

public class ClientGame extends Observable implements Runnable{
	
	/* TODO:
	 * - Make this class more readable
	 * Done:
	 * - All collections of GameObjects are now lists.
	 * - Maybe do the sound stuff somewhere else.
	 *   This is the model part and the sound would be the view part
	 *   Even if this class is responsible for calling the playSound commands 
	 *   (which I don't like but don't know how to solve), the code to play
	 *   sounds could better have its own class
	 * - Is this the right place for storing the spaceshipController? -> No. moved it to Client.
	 */
	
	private List <Spaceship> ships = new ArrayList<Spaceship>();
	/** List of bullets. */
	private List <Bullet> bullets = new ArrayList<Bullet>();

	/** List of asteroids. */
	private List <Asteroid> asteroids = new ArrayList<Asteroid>();
	
	/** List of explosions. */
	private List <Explosion> explosions = new ArrayList<Explosion>();
	
	/** List of game messages. */
	private List <GameMessage> messages = new ArrayList<GameMessage>();
	
	
	private int roundNumber = 1;
	
	private BufferedImage bgimage;
	
	private Point2D.Double bgPos = new Point2D.Double();
	
	private boolean frozen = false;
	
	private Sound sound = Sound.getInstance();
	
	private boolean aborted = false;
	
	
	public ClientGame(){
		
		setBackgroundImage(this.roundNumber);
	}
	
	public void setBackgroundImage(int index){
		try {
			this.bgimage = ImageIO.read(new File("images/tscape"+(index%7)+".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void update(){
		
		
		
		for(int i = messages.size()-1; i>=0; i--){
			if(messages.get(i).isDestroyed()){
				messages.remove(i);
			}
		}
		
		if(!frozen){
			for (Asteroid a : this.asteroids) a.nextStep ();
			for (Bullet b : this.bullets) b.nextStep ();
			for (Spaceship s : this.ships) s.nextStep();
			

			Random r = new Random(113*this.roundNumber);
			double xVelocity = 3 * (r.nextDouble() - .5);
			double yVelocity = 3 * (r.nextDouble() - .5);
			this.bgPos = new Point2D.Double(this.bgPos.x+xVelocity, this.bgPos.y+yVelocity);
		}
		
		this.setChanged ();
		this.notifyObservers ();
		
	}
	
	public void setSpaceships(List<Spaceship> ships){
		this.ships = ships;
	}
	
	
	public void setBullets(List<Bullet> bullets){
		int bulletsSize = this.bullets.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(bullets.size() > bulletsSize){
			sound.playSound("ShootNew2.wav");
		}
		
		this.bullets = bullets;
	}
	
	public void setAsteroids(List<Asteroid> asteroids){
		this.asteroids = asteroids;
	}

	@Override
	public void run() {
		long executionTime, sleepTime;
		
		
		
		while (!this.aborted)
		{
			if (true)
			{
				executionTime = System.currentTimeMillis ();
				
				
				this.update ();
				executionTime -= System.currentTimeMillis ();
				sleepTime = 40 - executionTime;
			}
			else sleepTime = 100;

			try
			{
				Thread.sleep (sleepTime);
			}
			catch (InterruptedException e)
			{
				System.err.println ("Could not perfrom action: Thread.sleep(...)");
				System.err.println ("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).");
				e.printStackTrace ();
			}
		}
	}
	
	public String toString(){
		return "Bullets:\n" + this.bullets.toString() + "\nShips:" + this.ships.toString()+"\nAsteroids:"+this.asteroids.toString();
	}

	public Collection<Bullet> getBullets() {
		return bullets;
	}
	public List<Spaceship> getSpaceships() {
		return ships;
	}
	
	public Spaceship getSpaceship(String name){
		for(Spaceship s : ships){
			if(s.getName().equals(name)){
				return s;
			}
		}
		return null;
	}
		
	public List<GameMessage> getMessages() {
		return messages;
	}
	
	public void addMessage(String message){
		this.messages.add(new GameMessage(message));
	}
	
	public void addPossiblyNewMessages(List<GameMessage> newMessages){
		for(GameMessage m : newMessages){
			if(!this.messages.contains(m)){
				this.messages.add(m);
			}
		}
	}
	
	public int getWidth(){
		return (int)GameObject.worldWidth;
	}
	
	public int getHeight(){
		return (int)GameObject.worldHeight;
	}

	public BufferedImage getBgImage(){
		return bgimage;
	}
	
	public Point2D getBgPos(){
		return (Point2D.Double)this.bgPos.clone();
	}
	
	public Collection<Asteroid> getAsteroids() {
		return asteroids;
	}
	
	public void freeze(){
		this.frozen = true;
		
	}
	public void unFreeze(){
		if(this.frozen){
			sound.playSound("NextLevelNew0.wav");
			setBackgroundImage(this.roundNumber);
		}
		this.frozen = false;
	}

	public Collection <Explosion> getExplosions() {
		return explosions;
	}

	public void setExplosions(List<Explosion> explosions) {
		
		int explosionsSize = this.explosions.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(explosions.size() > explosionsSize){
			sound.playExplosionSound(explosions.get(explosions.size()-1));
		}
		
		
		this.explosions = explosions;
	}
	
	public void abort(){
		this.aborted = true;
	}
	
	
	/** Check if the round has ended and update round number
	 */
	// Wow! Such name! :D
	public void checkIfRoundHasEndedAndUpdateRoundnumber(int roundnumber){
		if(this.roundNumber != roundnumber){
			sound.playSound("NextLevelNew0.wav");
			this.roundNumber = roundnumber;
// 			this.hasLost = false;
			setBackgroundImage(this.roundNumber);
			this.bgPos = new Point2D.Double(0,0);
		}
	}
	
	
// 	public void hasLost(){
// 		this.hasLost = true;
// 		sound.playSound("PlayerDeathNew0.wav");
// 	}
	
	
	public void playBGMIfNotAlreadyStarted(){
		
		//Do not play music while waiting for a connection, to synchronize audio between multiple PC's in close physical proximity.
		if(this.roundNumber == 0){
			return;
		}
		
		Sound sound = Sound.getInstance();
		if(!sound.hasBgmStarted()){
			sound.playSound("background_music_bassline.wav", true);
		}
	}
	
	public boolean isFrozen(){
		return frozen;
	}
}
