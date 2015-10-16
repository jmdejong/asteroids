package aoop.asteroids;



import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Game;
import aoop.asteroids.udp.Server;
import aoop.asteroids.model.WrappablePoint;

import java.awt.Point;
import java.net.SocketException;

import java.util.logging.Level;

/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *
 *	@author Yannick Stoffers
 */
public class Asteroids {
	
	/* TODO: general
	 * - add/update documentation
	 * - make an interface for all object that we can serialize to json
	 * - use decorator pattern more
	 * - move global constants to this class
	 * - move sound functions to its own (singleton?) class
	 * - for all large classes, see if we can split them up across multiple domains
	 */
	
	
	
	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	
	public static void main (String [] args)	{
		
		Logging.LOGGER.setLevel(Logging.loggerLevel);
		
		
		WrappablePoint p = new WrappablePoint(10,10, 700, 700);
// 		p.setLocation(11,11);
		Logging.LOGGER.warning(p.toString());
		Logging.LOGGER.warning(Double.toString(Utils.floorMod(2.0,7.0)));
		
		
		HighScores h = HighScores.getInstance();
		
		
		if (args.length == 0){
			AsteroidsFrame frame = new AsteroidsFrame ();
		} else {
			if (args[0] == "server"){
				try {
					new Server(false);
				} catch (SocketException e){
					e.printStackTrace();
				}
			} else if (args[0] == "client"){
				if (args.length == 1){
					(new AsteroidsFrame()).startGame("localhost", false);
				} else {
					(new AsteroidsFrame()).startGame(args[1], false);
				}
			}
		}
	}
	
}
