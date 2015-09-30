package aoop.asteroids.gui;

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

	/** Quit action. */
	private AbstractAction quitAction;

	/** New game action. */
	private AbstractAction restartGameAction;
	
	private AbstractAction startSinglePlayerAction;
	
	private AbstractAction startSpectatorAction;

	/** The game model. */
	private Game game;
	
	private CardContainer cards;

	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;
	
	/** 
	 *	Constructs a new Frame, requires a game model.
	 *
	 *	@param game game model.
	 *	@param controller key listener that catches the users actions.
	 */

	public AsteroidsFrame (){

		
		this.initActions ();
		
		this.setTitle ("Asteroids");
		this.setSize (800, 800);
		
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		

		JMenuBar mb = new JMenuBar ();
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (this.quitAction);
		m.add (this.restartGameAction);
		this.setJMenuBar(mb);
		
		
// 		cardLayout = new CardLayout();
		cards = new CardContainer();
		
		
		
		MenuPanel mp = new MenuPanel();
		mp.makeButton("Single player", this.startSinglePlayerAction);
		mp.makeButton("Spectate single player", this.startSpectatorAction);
		cards.add(mp, "Menu card");

		ap = new AsteroidsPanel ();
		cards.add(ap, "Game card");
		
		
		
		
		this.add(cards);
		
		
		showMenu();
		this.setVisible (true);
		this.requestFocusInWindow();
	}
	
	private void showMenu(){
		
		cards.showCard("Menu card");
	}
	
	public void startSinglePlayerGame(){
		
		Server server = new Server();
		
		
		startGame ();
	}
	
	public void startGame(){
		
		Client client = new Client("127.0.0.1", Server.UDPPort, false);
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

	/** Initializes the quit- and new game action. */
	private void initActions() 
	{
		// Quits the application
		this.quitAction = new AbstractAction ("Quit") 
		{
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed (ActionEvent arg0) 
			{
				System.exit(0);
			}
		};
		
		// Creates a new model
		this.restartGameAction = new AbstractAction ("Restart Game") 
		{
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				AsteroidsFrame.this.restartGame ();
			}
		};
		
		
		this.startSinglePlayerAction = new AbstractAction () 
		{
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				AsteroidsFrame.this.startSinglePlayerGame();
			}
		};
		
		this.startSpectatorAction = new AbstractAction () 
		{
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//AsteroidsFrame.this.cg = new ClientGame();
				//new Client("127.0.0.1", Server.UDPPort, true);
				//AsteroidsFrame.this.game = new Game(AsteroidsFrame.this.cg);
				AsteroidsFrame.this.startGame ();
			}
		};
		
	}
	
}
