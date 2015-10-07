package aoop.asteroids.gui;


import aoop.asteroids.Logging;
import aoop.asteroids.Asteroids;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Game;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.net.SocketException;

import aoop.asteroids.udp.Client;
import aoop.asteroids.udp.Server;

/**
 *	AsteroidsFrame is a class that extends JFrame and thus provides a game 
 *	window for the Asteroids game.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsFrame extends JFrame
{

	/** serialVersionUID */
	public static final long serialVersionUID = 1L;

	/** The game model. */
	private Game game;
	
	private CardContainer cards;

	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;
	
// 	private AddressInputPanel aip;
	/** 
	 *	Constructs a new Frame, requires a game model.
	 *
	 *	@param game game model.
	 *	@param controller key listener that catches the users actions.
	 */

	public AsteroidsFrame (){

		
		this.setTitle ("Asteroids");
		this.setResizable(false);
		
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		cards = new CardContainer();
		
		
		this.setSize(cards.getSize());
		
		
		ap = new AsteroidsPanel ();
		cards.add(ap, "Game card");
		
		final AddressInputPanel aip = new AddressInputPanel();
		
		cards.add(aip, "Address input card");
		
		MenuPanel mp = new MenuPanel();
		
		mp.makeButton("Singleplayer", new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				new Server(true);
				startGame("localhost", false);
				
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		}});
		
		mp.makeButton("Multiplayer", new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				new Server(false);
				startGame("localhost", false);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}});
		mp.makeButton("Join", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			cards.showCard("Address input card");
			aip.addClickListener(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
				Logging.LOGGER.fine(aip.getAddress());
				startGame(aip.getAddress(), false);
			}});
		}});
		mp.makeButton("Spectate", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			cards.showCard("Address input card");
			aip.addClickListener(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
				Logging.LOGGER.fine(aip.getAddress());
				startGame(aip.getAddress(), true);
			}});
		}});
		
		cards.add(mp, "Menu card");
		
		this.add(cards);
		
		
		showMenu();
		this.setVisible (true);
		this.requestFocusInWindow();
	}
	
	private void showMenu(){
		
		cards.showCard("Menu card");
	}
	
	public void startGame(String address, Boolean isSpectator){
		
		Client client = new Client(address, Server.UDPPort, isSpectator);
		addKeyListener(client.game.spaceshipController);
		
		ap.observeGame(client.game);
		
		cards.showCard( "Game card");
		

		this.requestFocusInWindow();
	}
	
	/** Quits the old game and starts a new one. */
	private void restartGame ()
	{
		this.game.abort ();
		try
		{
			Thread.sleep(50);
		}
		catch (InterruptedException e)
		{
			System.err.println ("Could not sleep before initialing a new game.");
			e.printStackTrace ();
		}
		this.game.initGameData ();
	}
	
}
