package aoop.asteroids;



import aoop.asteroids.gui.AsteroidsFrame;


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
