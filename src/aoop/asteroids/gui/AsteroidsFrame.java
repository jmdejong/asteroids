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
	
	
	private Server server;
	private Client client;
	
	/** The container vor the menu and the game view */
	private JPanel cards;
	private CardLayout cardLayout = new CardLayout();
	
	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;
	
	/** The panel that shows the menu */
	private MenuPanel mp;
	
	/** 
	 *	Constructs a new Frame.
	 */

	public AsteroidsFrame (){
		
		
		this.setTitle ("Asteroids");
		this.setResizable(false);
		
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		
		JMenuBar mb = new JMenuBar ();
		mb.setBackground(Color.BLACK);
		mb.setForeground(Color.GREEN);
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (new AbstractAction("Quit"){ public void actionPerformed(ActionEvent arg0){
			System.exit(0);
		}});
		m.add (new AbstractAction("Back to Menu"){ public void actionPerformed(ActionEvent arg0){
			if (AsteroidsFrame.this.server != null){
				AsteroidsFrame.this.server.stopServer();
			}
			if (AsteroidsFrame.this.client != null){
				AsteroidsFrame.this.client.stopClient();
			}
			showMenu();
		}});
		this.setJMenuBar (mb);
		
		cards = new JPanel(cardLayout);
		cards.setPreferredSize(new Dimension(800,700));
		
		
		ap = new AsteroidsPanel ();
		cards.add(ap, "game card");
		
		mp = new MenuPanel();
		
		mp.setButtonAction("Singleplayer", new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				AsteroidsFrame.this.server = new Server(true);
				startGame("localhost", false);
				
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		}});
		
		mp.setButtonAction("Host Multiplayer",new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				AsteroidsFrame.this.server = new Server(false);
				startGame("localhost", false);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}});
		mp.setButtonAction("Join", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			Logging.LOGGER.fine(mp.getAddress());
			startGame(mp.getAddress(), false);
		}});
		mp.setButtonAction("Spectate",new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			Logging.LOGGER.fine(mp.getAddress());
			startGame(mp.getAddress(), true);
		}});
		
		mp.setButtonAction("Quit", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
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
	
	/** Starts a new game as a client. 
	 * @param address the address of the server
	 * @param isSpectator whether the player should be only a spectator
	 */
	public void startGame(String address, Boolean isSpectator){
		
		this.client = new Client(address, Server.UDPPort, isSpectator, mp.getPlayerName());
		addKeyListener(client.game.spaceshipController);
		
		ap.observeGame(client.game);
		cardLayout.show(cards, "game card");
		this.requestFocusInWindow();
	}
	

}
