package aoop.asteroids.gui;


import aoop.asteroids.Logging;
import aoop.asteroids.Asteroids;


import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Color;
import java.net.SocketException;

import aoop.asteroids.udp.Client;
import aoop.asteroids.udp.Server;

/**
 *	AsteroidsFrame is a class that extends JFrame and thus provides a game 
 *	window for the Asteroids game.
 *
 *	@author Yannick Stoffers, Wiebe-Marten Wijnja, Michiel de Jong
 */
public class AsteroidsFrame extends JFrame {
	
	/** serialVersionUID */
	public static final long serialVersionUID = 1L;
	
	
	private Server server;
	private Client client;
	
	/** The container for the menu and the game view */
	private JPanel cards;
	private CardLayout cardLayout = new CardLayout();
	
	private SpaceshipController controller;
	
	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;
	
	/** The panel that shows the menu */
	private MenuPanel mp;
	
	/** 
	 *	Constructs a new Frame.
	 */

	public AsteroidsFrame(){
		
		
		this.setTitle("Asteroids");
		this.setResizable(false);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JMenuBar mb = new JMenuBar();
		mb.setBackground(Color.BLACK);
		mb.setForeground(Color.GREEN);
		JMenu m = new JMenu("Game");
		m.setBackground(Color.BLACK);
		m.setForeground(Color.GREEN);
		
		mb.add(m);
		m.add(new AbstractAction("Quit"){ 
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent arg0){
				System.exit(0);
			}
		});
		m.add (new AbstractAction("Back to Menu"){ 
			private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent arg0){
			if (AsteroidsFrame.this.server != null){
				AsteroidsFrame.this.server.stopServer();
			}
			if (AsteroidsFrame.this.client != null){
				AsteroidsFrame.this.client.stopClient();
			}
			AsteroidsFrame.this.mp.reloadHighScores();
			showMenu();
		}});
		this.setJMenuBar (mb);
		
		this.cards = new JPanel(cardLayout);
		this.cards.setPreferredSize(new Dimension(Asteroids.worldWidth,Asteroids.worldHeight));
		
		
		this.ap = new AsteroidsPanel ();
		this.cards.add(this.ap, "game card");
		
		this.mp = new MenuPanel();
		
		this.mp.setButtonAction("Singleplayer", new AbstractAction (){ 
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent arg0){
				try {
					AsteroidsFrame.this.server = new Server(true);
					startGame("localhost", false);
					
				} catch (SocketException e) {
					Logging.LOGGER.severe("Could not start server: "+e.getMessage());
				}
			}
		});
		
		this.mp.setButtonAction("Host Multiplayer", new AbstractAction(){ 
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent arg0){
				try {
					AsteroidsFrame.this.server = new Server(false);
					startGame("localhost", false);
				} catch (SocketException e) {
					Logging.LOGGER.severe("Could not start server: "+e.getMessage());
				}
			}
		});
		this.mp.setButtonAction("Join", new AbstractAction(){ 
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0){
				Logging.LOGGER.fine(AsteroidsFrame.this.mp.getAddress());
				startGame(AsteroidsFrame.this.mp.getAddress(), false);
			}
		});
		this.mp.setButtonAction("Spectate",new AbstractAction(){ 
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0){
				Logging.LOGGER.fine(AsteroidsFrame.this.mp.getAddress());
				startGame(AsteroidsFrame.this.mp.getAddress(), true);
			}
		});
		
		this.mp.setButtonAction("Quit", new AbstractAction(){ 
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0){
				System.exit(0);
			}
		});
		
		this.cards.add(this.mp, "menu card");
		
		add(this.cards);
		
		
		showMenu();
		setVisible (true);
		requestFocusInWindow();
		
		this.controller = new SpaceshipController();
		addKeyListener(this.controller);
	}
	
	/**
	 * Shows the menu (and hides the game).
	 */
	private void showMenu(){
		
		this.cardLayout.show(cards, "menu card");
		this.pack();
	}
	
	/** Starts a new game as a client, and shows it in the layout.
	 * @param address the address of the server
	 * @param isSpectator whether the player should be only a spectator(true) or an actual player(false)
	 */
	public void startGame(String address, Boolean isSpectator){
		
		this.client = new Client(address, Server.UDPPort, isSpectator, this.mp.getPlayerName(), this.controller);
		
		this.ap.observeGame(this.client.getGame());
		this.cardLayout.show(this.cards, "game card");
		this.requestFocusInWindow();
	}
	

}
