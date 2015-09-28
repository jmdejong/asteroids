package aoop.asteroids;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.Player;
import aoop.asteroids.model.Game;
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
public class Asteroids 
{

	/** Constructs a new instance of the program. */
	public Asteroids ()
	{
		Player player = new Player ();
		Game game = new Game ();
		game.linkController (player);
		AsteroidsFrame frame = new AsteroidsFrame (game, player);
		Thread t = new Thread (game);
		t.start ();
	}

	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	public static void main (String [] args)
	{
		
		  
		  aoop.asteroids.udp.packets.GameStatePacket testpacket = new aoop.asteroids.udp.packets.GameStatePacket();
		  System.out.println(testpacket.toJsonString());
		  
		  JSONObject obj2 = (JSONObject) JSONValue.parse("{\"pt\":\"test\"}");
		  System.out.print(obj2);
		
		  try {
			new aoop.asteroids.udp.ServerThread().start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Asteroids ();
	}
	
}
