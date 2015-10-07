package aoop.asteroids.model;

import aoop.asteroids.Logging;
import aoop.asteroids.udp.Server;

public class Lobby extends Game {
	public static long waitingTime = 3000;
	
	public long startCountdownTime = 0;

	public Lobby(Server server, int roundNumber) {
		super(server, roundNumber);
	}
	
	public double timeUntilOver(){
		if(this.getSpaceships().size() < 2 || !this.areAllShipsDestroyed()){
			return Double.POSITIVE_INFINITY;
		}else if(this.startCountdownTime==0){
			startCountdownTime = System.currentTimeMillis();
			server.sendMessagePacket("Starting Next Round in "+(waitingTime/1000)+" seconds");
		}
		double time = System.currentTimeMillis() - this.startCountdownTime;
		return Lobby.waitingTime - time;
	}
	
	protected boolean isGameOver(){
		Logging.LOGGER.fine("Time until next level:"+this.timeUntilOver());
		return this.timeUntilOver() <= 0;
	}

}
