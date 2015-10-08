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

import aoop.asteroids.Logging;
import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.udp.Client;

public class ClientGame extends Observable implements Runnable{
	
	private Collection <Spaceship> ships = new ArrayList<Spaceship>();
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
	
	
	private Client client;
	
	public BufferedImage bgimage;
	
	public Point2D.Double bgPos = new Point2D.Double();
	
	/** if set to true, this Game will not try to send any more input packets until the current round is over.*/
	public boolean hasLost = false;
	
	public boolean isFrozen = false;
	
	public ClientGame(Client client){
		this.client = client;
		
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
	
	public void setSpaceships(Collection<Spaceship> ships){
		this.ships = ships;
	}
	
	public void setBullets(List<Bullet> bullets){
		int bulletsSize = this.bullets.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(bullets.size() > bulletsSize){
			playShootSound(bullets.get(bullets.size()-1));
		}
		
		this.bullets = bullets;
	}
	
	public void setAsteroids(Collection<Asteroid> asteroids){
		this.asteroids = asteroids;
	}

	@Override
	public void run() {
		long executionTime, sleepTime;
		
		
		
		while (true)
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
	public Collection<Spaceship> getSpaceships() {
		return ships;
	}
	public List<GameMessage> getMessages() {
		return messages;
	}
	
	public void addMessage(String message){
		this.messages.add(new GameMessage(message));
	}
	
// 	public Spaceship getSpaceship(){
// 		return ships.toArray(new Spaceship[1])[0];
// 	}
	
	public Collection<Asteroid> getAsteroids() {
		return asteroids;
	}
	
	public void freeze(){
		this.isFrozen = true;
		
	}
	public void unFreeze(){
		if(this.isFrozen){
			playSound("NextLevel");
			++roundNumber;
			setBackgroundImage(this.roundNumber);
		}
		this.isFrozen = false;
	}

	public Collection <Explosion> getExplosions() {
		return explosions;
	}

	public void setExplosions(List <Explosion> explosions) {
		
		int explosionsSize = this.explosions.size();
		
		//Play `fire` sound whenever a new bullet appears.
		if(explosions.size() > explosionsSize){
			playExplosionSound(explosions.get(explosions.size()-1));
		}
		
		
		this.explosions = explosions;
	}

	private void playShootSound(Bullet b){
		int index = new Random(b.hashCode()).nextInt(2);
		playSound("Shoot"+index);
	}
	
	private void playExplosionSound(Explosion explosion){
		int index = new Random(explosion.hashCode()).nextInt(5);
		playSound("Explosion"+index);
	}
	
	public void playSound(final String filename){
		
		
		
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
					
					InputStream stream = new BufferedInputStream(new FileInputStream("sounds/"+filename+".wav"));
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
					
					DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
					AudioListener listener = new AudioListener();
			        
			        try {
			            Clip clip;// = AudioSystem.getClip();
			            	            
			            clip = (Clip) AudioSystem.getLine(info);
			            clip.addLineListener(listener);
				        clip.open(inputStream);
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
	
	public void hasLost(){
		this.hasLost = true;
		playSound("ShipExplosion");
	}
}
