package aoop.asteroids.gui;


import aoop.asteroids.Logging;
import aoop.asteroids.Asteroids;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Game;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	public static Color ButtonBorderColor = Color.GREEN;
	public static Color TextColor = Color.GREEN;
	public static Color ButtonBackColor = Color.BLACK;
	public static int ButtonBorderWidth = 1;
	

	/** The game model. */
	private Game game;
	
	private CardContainer cards;

	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;
	
	private MenuPanel mp;
	
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
		
		aip.setBackAction(new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			cards.showCard("Menu card");
		}});
		
		
		cards.add(aip, "Address input card");
		mp = new MenuPanel("ASTEROIDS");
		
		mp.setPlayAction(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				new Server(true);
				startGame("localhost", false);
				
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		}});
		
		mp.setHostAction(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				new Server(false);
				startGame("localhost", false);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}});
		mp.setJoinAction(new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			cards.showCard("Address input card");
			aip.setClickAction(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
				Logging.LOGGER.fine(aip.getAddress());
				startGame(aip.getAddress(), false);
			}});
		}});
		mp.setSpectateAction(new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			cards.showCard("Address input card");
			aip.setClickAction(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
				Logging.LOGGER.fine(aip.getAddress());
				startGame(aip.getAddress(), true);
			}});
		}});
		
		mp.setQuitAction(new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			System.exit(0);
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
		
		Client client = new Client(address, Server.UDPPort, isSpectator, mp.getPlayerName());
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
			System.err.println ("Could not sleep before initializing a new game.");
			e.printStackTrace ();
		}
		this.game.initGameData (0);
	}
	

}
