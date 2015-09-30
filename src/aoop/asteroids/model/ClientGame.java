package aoop.asteroids.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

import aoop.asteroids.gui.SpaceshipController;
import aoop.asteroids.udp.Client;

public class ClientGame extends Observable implements Runnable{
	
	private Collection <Spaceship> ships = new ArrayList<Spaceship>();
	/** List of bullets. */
	private Collection <Bullet> bullets = new ArrayList<Bullet>();

	/** List of asteroids. */
	private Collection <Asteroid> asteroids = new ArrayList<Asteroid>();
	
	public SpaceshipController spaceshipController;
	
	
	private Client client;
	
	/** if set to true, this Game will not try to send any more input packets until the current round is over.*/
	public boolean hasLost = false;

	
	public boolean isFrozen = false;
	
	public ClientGame(Client client){
		this.client = client;
		
		if(!this.client.isSpectator){
			this.spaceshipController = new SpaceshipController();
		}
		
	}
	
	private void update(){
		if(isFrozen){
			return;
		}
		for (Asteroid a : this.asteroids) a.nextStep ();
		for (Bullet b : this.bullets) b.nextStep ();
		for (Spaceship s : this.ships) s.nextStep();
		
		//TODO: send player packet depending on player input.
		if(!this.client.isSpectator && !this.hasLost){
			this.client.sendPlayerUpdatePacket(this.spaceshipController);
		}
		
		this.setChanged ();
		this.notifyObservers ();
	}
	
	public void setSpaceships(Collection<Spaceship> ships){
		this.ships = ships;
	}
	
	public void setBullets(Collection<Bullet> bullets){
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
	public Spaceship getSpaceship(){
		return ships.toArray(new Spaceship[1])[0];
	}
	
	public Collection<Asteroid> getAsteroids() {
		return asteroids;
	}
	
	public void freeze(){
		this.isFrozen = true;
	}
	public void unFreeze(){
		this.isFrozen = false;
	}
}
