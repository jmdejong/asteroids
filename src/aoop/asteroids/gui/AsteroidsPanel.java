package aoop.asteroids.gui;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.Spaceship;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.lang.Object;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 *	AsteroidsPanel extends JPanel and thus provides the actual graphical 
 *	representation of the game model.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsPanel extends JPanel
{

	/** serialVersionUID */
	public static final long serialVersionUID = 4L;

	/** Game model. */
	private ClientGame game;

	/** 
	 *	Constructs a new game panel, based on the given model.
	 *
	 *	@param game game model.
	 */

	public void observeGame (ClientGame game)
	{
		this.game = game;
		this.game.addObserver (new Observer ()
		{
			@Override
			public void update (Observable o, Object arg)
			{
				AsteroidsPanel.this.repaint ();
			}
		});
	}
	
	/**
	 *	Method for refreshing the GUI.
	 *
	 *	@param g graphics instance to use.
	 */
	@Override
	public void paintComponent (Graphics g)
	{
		super.paintComponent (g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.setBackground (Color.black);

		this.paintSpaceships (g2);
		this.paintAsteroids (g2);
		this.paintBullets (g2);

		g2.setColor (Color.WHITE);
		Spaceship s = this.game.getSpaceship ();
		if(s==null){
			return;
		}
		g2.drawString (String.valueOf (s.getScore ()), 20, 20);
	}

	/**
	 *	Draws all bullets in the GUI as a yellow circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintBullets (Graphics2D g)
	{
		g.setColor(Color.yellow);

		for (Bullet b : this.game.getBullets ())
		    g.drawOval (((int)b.getLocation ().x) - 2, ((int)b.getLocation ().y) - 2, 5, 5);
	}

	/**
	 *	Draws all asteroids in the GUI as a filled gray circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintAsteroids (Graphics2D g)
	{
		g.setColor (Color.GRAY);

		for (Asteroid a : this.game.getAsteroids ())
		{
			paintAsteroidPart(g,(int)a.getLocation().getX()			,(int)a.getLocation().getY()		,a.getRadius());
			paintAsteroidPart(g,(int)a.getMirrorLocation().getX()	,(int)a.getLocation().getY()		,a.getRadius());
			paintAsteroidPart(g,(int)a.getLocation().getX()			,(int)a.getMirrorLocation().getY()	,a.getRadius());
			paintAsteroidPart(g,(int)a.getMirrorLocation().getX()	,(int)a.getMirrorLocation().getY()	,a.getRadius());
		}
	}
	
	private void paintAsteroidPart(Graphics2D g, int x, int y, int radius){
		Ellipse2D.Double e = new Ellipse2D.Double ();
		e.setFrame (x - radius, y - radius, 2 * radius, 2 * radius);
		g.fill (e);
	}

	/**
	 *	Draws the player in the GUI as a see-through white triangle. If the 
	 *	player is accelerating a yellow triangle is drawn as a simple represen-
	 *	tation of flames from the exhaust.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintSpaceships (Graphics2D g)
	{
		for(Spaceship s : this.game.getSpaceships()){
			if(s==null || s.isDestroyed()){
				continue;
			}
			Color c = new Color((int) s.getColour());

			paintSpaceshipPart(g,(int)s.getLocation().getX()      ,(int) s.getLocation().getY()      ,s.getDirection(),s.isAccelerating(), c);
			paintSpaceshipPart(g,(int)s.getLocation().getX()      ,(int) s.getMirrorLocation().getY(),s.getDirection(),s.isAccelerating(), c);
			paintSpaceshipPart(g,(int)s.getMirrorLocation().getX(),(int) s.getLocation().getY()      ,s.getDirection(),s.isAccelerating(), c);
			paintSpaceshipPart(g,(int)s.getMirrorLocation().getX(),(int) s.getMirrorLocation().getY(),s.getDirection(),s.isAccelerating(), c);
			
		}
		
	}
	
	private void paintSpaceshipPart(Graphics2D g, int x, int y, double direction, boolean isAccelerating, Color c){
		// Draw body of the spaceship
		Polygon p = new Polygon ();
		p.addPoint ((int)(x + Math.sin (direction				 ) * 20), (int)(y - Math.cos (direction				   ) * 20));
		p.addPoint ((int)(x + Math.sin (direction + 0.8 * Math.PI) * 20), (int)(y - Math.cos (direction + 0.8 * Math.PI) * 20));
		p.addPoint ((int)(x + Math.sin (direction + 1.2 * Math.PI) * 20), (int)(y - Math.cos (direction + 1.2 * Math.PI) * 20));

		g.setColor (c);
		g.fill (p);
		g.setColor (Color.WHITE);
		g.draw (p);

		// Spaceship accelerating -> continue, otherwise abort.
		if (!isAccelerating) return;

		// Draw flame at the exhaust
		p = new Polygon ();
		p.addPoint ((int)(x - Math.sin (direction			     ) * 25), (int)(y + Math.cos (direction			       ) * 25));
		p.addPoint ((int)(x + Math.sin (direction + 0.9 * Math.PI) * 15), (int)(y - Math.cos (direction + 0.9 * Math.PI) * 15));
		p.addPoint ((int)(x + Math.sin (direction + 1.1 * Math.PI) * 15), (int)(y - Math.cos (direction + 1.1 * Math.PI) * 15));
		g.setColor(Color.yellow);
				g.fill(p);
	}

}
