package aoop.asteroids;



import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Game;
import aoop.asteroids.udp.Server;

import java.awt.Point;
import java.net.SocketException;

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
	
	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	
	public static void main (String [] args)	{
		
		
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
