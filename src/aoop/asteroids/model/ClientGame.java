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
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import aoop.asteroids.Asteroids;
import aoop.asteroids.Logging;
import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.udp.Client;

public class ClientGame extends Observable implements Runnable{
	
	private List <Spaceship> ships = new ArrayList<Spaceship>();
	/** List of bullets. */
	private Collection <Bullet> bullets = new ArrayList<Bullet>();

	/** List of asteroids. */
	private Collection <Asteroid> asteroids = new ArrayList<Asteroid>();
	
	/** List of explosions. */
	private Collection <Explosion> explosions = new ArrayList<Explosion>();
	
	/** List of game messages. */
	private List <GameMessage> messages = new ArrayList<GameMessage>();
	
	
	public SpaceshipController spaceshipController;
	
	private int roundNumber = 1;
	
	private String playerName;
	
	
	private Client client;
	
	private BufferedImage bgimage;
	
	public Point2D.Double bgPos = new Point2D.Double();
	
	/** if set to true, this Game will not try to send any more input packets until the current round is over.*/
	public boolean hasLost = false;
	
	public boolean isFrozen = false;
	
	public boolean bgmHasStarted = false;
	
	private long lastConnectionCheckTime = 0;
	
	private boolean aborted = false;
	
	
	public ClientGame(Client client, String playerName){
		this.client = client;
		
		this.playerName = playerName;
		
		if(!this.client.isSpectator){
			this.spaceshipController = new SpaceshipController();
		}
		

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
		if(isFrozen){
			return;
		}
		for (Asteroid a : this.asteroids) a.nextStep ();
		for (Bullet b : this.bullets) b.nextStep ();
		for (Spaceship s : this.ships) s.nextStep();
		
		
		for(int i = messages.size()-1; i>=0; i--){
			if(messages.get(i).isDestroyed()){
				messages.remove(i);
			}
		}
		
		//TODO: send player packet depending on player input.
		if(!this.client.isSpectator && !this.hasLost){
			this.client.sendPlayerUpdatePacket(this.spaceshipController);
		}else{
			this.client.sendSpectatorPingPacket();
		}
		
		this.setChanged ();
		this.notifyObservers ();
				
		Random r = new Random(113*this.roundNumber);
		double xVelocity = 3 * (r.nextDouble() - .5);
		double yVelocity = 3 * (r.nextDouble() - .5);
		this.bgPos = new Point2D.Double(this.bgPos.x+xVelocity, this.bgPos.y+yVelocity);
	}
	
	public void setSpaceships(List<Spaceship> ships){
		this.ships = ships;
		if(!this.hasLost){
			for(Spaceship s : ships){
				if(s.getName().equals(this.playerName) && s.destroyed){
					this.hasLost();
				}
			}
		}
	}
	
	public void setBullets(Collection<Bullet> bullets){
		int bulletsSize = this.bullets.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(bullets.size() > bulletsSize){
			playShootSound();
		}
		
		this.bullets = bullets;
	}
	
	public void setAsteroids(Collection<Asteroid> asteroids){
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
				if(!this.client.hasConnected() && this.lastConnectionCheckTime + 3000 < executionTime){
					this.client.sendPlayerJoinPacket();
					this.lastConnectionCheckTime = executionTime;
				}
				
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
	
	public void setMessages(List<GameMessage> messages){
		this.messages = messages;
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
	
	public Collection<Asteroid> getAsteroids() {
		return asteroids;
	}
	
	public void freeze(){
		this.isFrozen = true;
		
	}
	public void unFreeze(){
		if(this.isFrozen){
			playSound("NextLevelNew0.wav");
			++roundNumber;
			setBackgroundImage(this.roundNumber);
		}
		this.isFrozen = false;
	}

	public Collection <Explosion> getExplosions() {
		return explosions;
	}

	public void setExplosions(List<Explosion> explosions) {
		
		int explosionsSize = this.explosions.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(explosions.size() > explosionsSize){
			playExplosionSound(explosions.get(explosions.size()-1));
		}
		
		
		this.explosions = explosions;
	}
	
	public void abort(){
		this.aborted = true;
	}
	
	

	private void playShootSound(){
		//int index = new Random(b.hashCode()).nextInt(2);
		//playSound("Shoot"+index+".wav");
		playSound("ShootNew2.wav");

	}
	
	private void playExplosionSound(Explosion explosion){
		int index = new Random(explosion.hashCode()).nextInt(4);
		//playSound("Explosion"+index+".wav");
		playSound("ExplosionNew"+index+".wav");
	}
	
	public void playSound(String filename){
		playSound(filename,0);
	}
	
	public void playSound(final String filename, final int startOffset){
		
		
		
		new Thread(new Runnable() {
		
			@Override
			public void run() {
				
				class AudioListener implements LineListener {
					private boolean done = false;
					@Override public synchronized void update(LineEvent event) {
					LineEvent.Type eventType = event.getType();
					if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
						done = true;
						notifyAll();
					}
					}
					public synchronized void waitUntilDone() throws InterruptedException {
					while (!done) { wait(); }
					}
				}
			
				try {
					
					InputStream stream = new BufferedInputStream(new FileInputStream("sounds/"+filename));
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
					
					
					
					DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
					
					AudioListener listener = new AudioListener();
					
					try {
						Clip clip;// = AudioSystem.getClip();
										
						clip = (Clip) AudioSystem.getLine(info);
						clip.addLineListener(listener);
						clip.open(inputStream);
						
						if(startOffset != 0){
							clip.setFramePosition(startOffset);
						}
						
						try {
						clip.start();
						listener.waitUntilDone();
						} catch (InterruptedException e) {
		
						} finally {
						clip.close();
						}
					} finally {
						inputStream.close();
					}
					
				} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
					//This happens when a file is unavailable or the sound device is busy.
					//Just don't play any sound when that happens.
				}
				
					// TODO Auto-generated method stub
					
			}
			
		
		}).start();
		
	}
	
	public void checkIfRoundHasEndedAndUpdateRoundnumber(int roundnumber){
		if(this.roundNumber < roundnumber){
			playSound("NextLevelNew0.wav");
			this.roundNumber = roundnumber;
			this.hasLost = false;
			setBackgroundImage(this.roundNumber);
		}
	}
	
	
	public void hasLost(){
		this.hasLost = true;
		playSound("PlayerDeathNew0.wav");
	}
}
