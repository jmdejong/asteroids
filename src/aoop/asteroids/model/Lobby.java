package aoop.asteroids.model;

import aoop.asteroids.udp.Server;

public class Lobby extends Game {
	public static long waitingTime = 3000;
	
	public long startCountdownTime = 0;

	public Lobby(Server server) {
		super(server);
	}
	
	public double timeUntilOver(){
		if(this.getSpaceships().size() < 2 || !this.areAllShipsDestroyed()){
			return Double.POSITIVE_INFINITY;
		}else if(this.startCountdownTime==0){
			startCountdownTime = System.currentTimeMillis();
		}
		double time = System.currentTimeMillis() - this.startCountdownTime;
		return Lobby.waitingTime - time;
	}
	
	protected boolean isGameOver(){
		System.out.println("Time until next level:"+this.timeUntilOver());
		return this.timeUntilOver() <= 0;
	}

}
