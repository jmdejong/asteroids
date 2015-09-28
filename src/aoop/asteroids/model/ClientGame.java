package aoop.asteroids.model;

import java.util.Collection;
import java.util.Observable;

public class ClientGame extends Observable implements Runnable{
	
	private Collection <Spaceship> ships;
	/** List of bullets. */
	private Collection <Bullet> bullets;

	/** List of asteroids. */
	private Collection <Asteroid> asteroids;

	
	public ClientGame(){
		
	}
	
	private void update(){
		for (Asteroid a : this.asteroids) a.nextStep ();
		for (Bullet b : this.bullets) b.nextStep ();
		for (Spaceship s : this.ships) s.nextStep();
		
		//TODO: send player packet depending on player input.
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
}
