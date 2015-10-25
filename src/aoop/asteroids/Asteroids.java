package aoop.asteroids;



import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.udp.Server;

import java.net.SocketException;


/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *
 *	@author Yannick Stoffers, Wiebe-Marten Wijnja, Michiel de Jong
 */
public class Asteroids {
	
	/* TODO: general
	 * - add/update documentation
	 * - make an interface for all object that we can serialize to json -> GameObject -> that's not an interface and not all we can serialize
	 * - for all large classes, see if we can split them up across multiple domains
	 * DONE:
	 * - move global constants to this class
	 * - use decorator pattern more
	 * - move sound functions to its own (singleton?) class
	 * - make use of this consequent (always for field, not for own methods)
	 */
	
	
	
	
	public static int worldWidth = 800;
	public static int worldHeight = 700;
	
	
	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	
	public static void main (String [] args)	{
		
		Logging.LOGGER.setLevel(Logging.loggerLevel);
		
		new AsteroidsFrame ();
	}
	
}
