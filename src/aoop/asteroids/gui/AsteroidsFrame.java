package aoop.asteroids.gui;

import aoop.asteroids.Asteroids;
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
	private AbstractAction newGameAction;

	/** The game model. */
	private Game game;
	
	private CardLayout cardLayout;
	
	private JPanel cards;

	/** The panel in which the game is painted. */
	private AsteroidsPanel ap;

	private MenuPanel mp;
	
	/** 
	 *	Constructs a new Frame, requires a game model.
	 *
	 *	@param game game model.
	 *	@param controller key listener that catches the users actions.
	 */
	public AsteroidsFrame (Game game, Player controller)
	{
		this.game = game;
		
		this.initActions ();
		
		this.setTitle ("Asteroids");
		this.setSize (800, 800);
		
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		
		mp = new MenuPanel(this);
		
		ap = new AsteroidsPanel (this.game);
		
		cardLayout = new CardLayout();
		cards = new JPanel(cardLayout);
		cards.add(mp, "Menu card");
		cards.add(ap, "Game card");
		
		this.addKeyListener(controller);
		
		this.add(cards);
		
		
		showMenu();
		this.setVisible (true);
	}
	
	private void showMenu(){
		
		cardLayout.show(cards, "Menu card");
	}
	
	
	public void showGame(){
		
		cardLayout.show(cards, "Game card");
		
		Thread t = new Thread (game);
		t.start();
		this.requestFocusInWindow();
	}
	
	/** Quits the old game and starts a new one. */
	private void newGame ()
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
		this.newGameAction = new AbstractAction ("New Game") 
		{
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				AsteroidsFrame.this.newGame ();
			}
		};

	}
	
}
