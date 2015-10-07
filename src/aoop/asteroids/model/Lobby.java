package aoop.asteroids.model;

import java.util.List;

import aoop.asteroids.Logging;
import aoop.asteroids.udp.Server;

public class Lobby extends Game {
	public static long waitingTime = 3000;
	
	public long startCountdownTime = 0;

	public Lobby(Server server, int roundNumber) {
		super(server, roundNumber);
	}
	
	public double timeUntilOver(){

		if(    ( server.isSinglePlayerMode() && ((!this.getSpaceships().isEmpty() && this.areAllAsteroidsDestroyed()) || this.areAllShipsDestroyed()))
			|| (!server.isSinglePlayerMode() && (this.getSpaceships().size() > 1) && ((this.areAllAsteroidsDestroyed()) || this.areAllShipsDestroyed() || this.isThereOnlyOneShipLeft() ))){
			
			if(this.startCountdownTime==0){
				startCountdownTime = System.currentTimeMillis();
				List<Spaceship> winners = getWinners();
				for(Spaceship w : winners){
					w.increaseScore();
				}
				if(this.areAllAsteroidsDestroyed()){
					server.sendMessagePacket("Congradulations! Level Cleared.");
				}
				server.sendMessagePacket("Starting Next Round in "+(waitingTime/1000)+" seconds");
			}
			double time = System.currentTimeMillis() - this.startCountdownTime;
			return Lobby.waitingTime - time;
		}else{
			return Double.POSITIVE_INFINITY;
		}
		
	}
	
	protected boolean isGameOver(){
		Logging.LOGGER.fine("Time until next level:"+this.timeUntilOver());
		return this.timeUntilOver() <= 0;
	}

}
