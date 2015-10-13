package aoop.asteroids.gui;


import aoop.asteroids.Logging;
import aoop.asteroids.Asteroids;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Game;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
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
	
	private JPanel cards;
	private CardLayout cardLayout = new CardLayout();

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
		
		
		
		
		
		
		
		JMenuBar mb = new JMenuBar ();
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (new AbstractAction("Quit"){ public void actionPerformed(ActionEvent arg0){
			System.exit(0);
		}});
		this.setJMenuBar (mb);
		
		
		
		
		
		
		cards = new JPanel(cardLayout);
		cards.setPreferredSize(new Dimension(800,700));
		
// 		this.setSize(new Dimension(800,700));//cards.getPreferredSize());
		
		
		ap = new AsteroidsPanel ();
		cards.add(ap, "game card");
		
		mp = new MenuPanel();
		
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
			Logging.LOGGER.fine(mp.getAddress());
			startGame(mp.getAddress(), false);
		}});
		mp.setSpectateAction(new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			Logging.LOGGER.fine(mp.getAddress());
			startGame(mp.getAddress(), true);
		}});
		
		mp.setQuitAction(new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			System.exit(0);
		}});
		
		cards.add(mp, "menu card");
		
		this.add(cards);
		
		
		showMenu();
		this.setVisible (true);
		this.requestFocusInWindow();
	}
	
	private void showMenu(){
		
		cardLayout.show(cards, "menu card");
		this.pack();
	}
	
	public void startGame(String address, Boolean isSpectator){
		
		Client client = new Client(address, Server.UDPPort, isSpectator, mp.getPlayerName());
		addKeyListener(client.game.spaceshipController);
		
		ap.observeGame(client.game);
// 		Logging.LOGGER.info(ap.getSize().toString());
		
// 		cards.setSize(ap.getSize());
		cardLayout.show(cards, "game card");
// 		Logging.LOGGER.info(cards.getSize().toString());
// 		Logging.LOGGER.info(ap.getSize().toString());
		
		
// 		this.pack();
		this.requestFocusInWindow();
	}
	
// 	/** Quits the old game and starts a new one. */
// 	private void restartGame ()
// 	{
// 		this.game.abort ();
// 		try
// 		{
// 			Thread.sleep(50);
// 		}
// 		catch (InterruptedException e)
// 		{
// 			System.err.println ("Could not sleep before initializing a new game.");
// 			e.printStackTrace ();
// 		}
// 		this.game.initGameData (0);
// 	}
	

}
