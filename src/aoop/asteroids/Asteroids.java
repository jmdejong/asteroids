package aoop.asteroids;


import org.json.simple.JSONObject;
import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.Player;
import aoop.asteroids.model.Game;
import java.awt.Point;

/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *
 *	@author Yannick Stoffers
 */
public class Asteroids 
{

	/** Constructs a new instance of the program. */
	public Asteroids ()
	{
		Player player = new Player ();
		Game game = new Game ();
		game.linkController (player);
		AsteroidsFrame frame = new AsteroidsFrame (game, player);
	}

	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	public static void main (String [] args)
	{
		
		  
		  JSONObject obj=new JSONObject();
		  obj.put("name","foo");
		  obj.put("num",new Integer(100));
		  obj.put("balance",new Double(1000.21));
		  obj.put("is_vip",new Boolean(true));
		  obj.put("nickname",null);
		  System.out.print(obj);
		
		
		new Asteroids ();
	}
	
}
