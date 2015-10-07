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
	
// 	private AddressInputPanel aip;
	/** 
	 *	Constructs a new Frame, requires a game model.
	 *
	 *	@param game game model.
	 *	@param controller key listener that catches the users actions.
	 */

	public AsteroidsFrame (){

		
// 		this.initActions ();
		
		this.setTitle ("Asteroids");
		this.setResizable(false);
		
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		

// 		JMenuBar mb = new JMenuBar ();
// 		JMenu m = new JMenu ("Game");
// 		mb.add (m);
// 		m.add (this.quitAction);
// 		m.add (this.restartGameAction);
// 		this.setJMenuBar(mb);
		
		
// 		cardLayout = new CardLayout();
		cards = new CardContainer();
		
		
		this.setSize(cards.getSize());
		
		
		ap = new AsteroidsPanel ();
		cards.add(ap, "Game card");
		
		final AddressInputPanel aip = new AddressInputPanel();
		
		cards.add(aip, "Address input card");
		
		MenuPanel mp = new MenuPanel();
		mp.makeButton("Single player", new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
			try {
				Server server = new Server();
				startGame();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		}});
		mp.makeButton("Spectate single player", new AbstractAction (){ public void actionPerformed(ActionEvent arg0) {
			startGame();
		}});
		mp.makeButton("Spectate", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
			cards.showCard("Address input card");
			aip.addClickListener(new AbstractAction (){ public void actionPerformed(ActionEvent arg0){
				System.out.println(aip.getAddress());
				startGame(aip.getAddress());
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
	
	public void startSinglePlayerGame(){
		
		try {
			Server server = new Server();
			startGame ();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public void startGame(){
		startGame("127.0.0.1");
	}
	
	public void startGame(String address){
		
		Client client = new Client(address, Server.UDPPort, false);
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
