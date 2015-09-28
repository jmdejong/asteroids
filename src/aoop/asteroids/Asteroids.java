package aoop.asteroids;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.Player;
import aoop.asteroids.model.ClientGame;
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
		ClientGame cg = new ClientGame();
		Game game = new Game (cg);
		
		game.linkController (player);
		AsteroidsFrame frame = new AsteroidsFrame (game, cg, player);
	}

	/** 
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	public static void main (String [] args)
	{
		
		  
		  aoop.asteroids.udp.packets.PlayerJoinPacket testpacket = new aoop.asteroids.udp.packets.PlayerJoinPacket();
		  System.out.println(testpacket.toJsonString());
		  
		  JSONObject obj2 = (JSONObject) JSONValue.parse("{\"pt\":\"test\"}");
		  System.out.print(obj2);
		
		 aoop.asteroids.udp.Server server = new aoop.asteroids.udp.Server();
		
		new Asteroids ();
	}
	
}
