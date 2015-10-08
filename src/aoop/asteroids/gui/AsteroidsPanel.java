package aoop.asteroids.gui;

import aoop.asteroids.Logging;
import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.Bullet;
import aoop.asteroids.model.ClientGame;
import aoop.asteroids.model.Explosion;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.GameMessage;
import aoop.asteroids.model.GameObject;
import aoop.asteroids.model.Spaceship;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import java.awt.FontMetrics;
import javax.swing.JPanel;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

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
		
		g2.drawImage((Image)game.bgimage, 0,0, null);
		g2.setColor(new Color(0,0,0,0.8f));
		g2.fillRect(0, 0, (int)GameObject.worldWidth, (int)GameObject.worldHeight);
		
		
		this.paintSun(g2);

		this.paintSpaceships (g2);
		this.paintAsteroids (g2);
		this.paintBullets (g2);
		
		for(Explosion e : game.getExplosions()){
			if(e==null){
				continue;
			}
			paintExplosion(g2, e);
		}
		
		this.paintGameMessages(g2, game.getMessages());
		this.paintScores(g2);
		
	}

	/**
	 *	Draws all bullets in the GUI as a yellow circle.
	 *
	 *	@param g graphics instance to use.
	 */
	private void paintBullets (Graphics2D g)
	{
		g.setColor(Color.YELLOW);

		for (Bullet b : this.game.getBullets ()){
		    g.fillOval (((int)b.getLocation ().x) - 2, ((int)b.getLocation ().y) - 2, 5, 5);
		}
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
			paintAsteroidPart(g,(int)a.getLocation().getX()			,(int)a.getLocation().getY()		,a.getRadius(), 3*a.getVelocityX()+5*a.getVelocityY(), a.getRotation());
			paintAsteroidPart(g,(int)a.getMirrorLocation().getX()	,(int)a.getLocation().getY()		,a.getRadius(), 3*a.getVelocityX()+5*a.getVelocityY(), a.getRotation());
			paintAsteroidPart(g,(int)a.getLocation().getX()			,(int)a.getMirrorLocation().getY()	,a.getRadius(), 3*a.getVelocityX()+5*a.getVelocityY(), a.getRotation());
			paintAsteroidPart(g,(int)a.getMirrorLocation().getX()	,(int)a.getMirrorLocation().getY()	,a.getRadius(), 3*a.getVelocityX()+5*a.getVelocityY(), a.getRotation());
		}
	}
	
	private void paintAsteroidPart(Graphics2D g, int x, int y, int radius, double seed, double rotation){
		//Ellipse2D.Double e = new Ellipse2D.Double ();
		//e.setFrame (x - radius, y - radius, 2 * radius, 2 * radius);
		RadialGradientPaint roundGradientPaint = new RadialGradientPaint(x, y, (int) (radius*1.2), (int)GameObject.worldWidth/2, (int)GameObject.worldHeight/2, new float[]{0,1}, new Color[]{Color.LIGHT_GRAY, Color.DARK_GRAY}, CycleMethod.NO_CYCLE);
		g.setPaint(roundGradientPaint);
		
		
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		Random r = new Random((int)seed);
		
		int orthagonalJitter = (int)(radius * .6);
		int amountOfPoints = 6 + r.nextInt(4);
		
		for(int i=0;i<amountOfPoints;i++){
			double px, py;
			px = x + (radius) * Math.sin(((2*Math.PI)/amountOfPoints) * i + rotation) + r.nextInt(orthagonalJitter) - (orthagonalJitter/2) ;
			py = y + (radius) * Math.cos(((2*Math.PI)/amountOfPoints) * i + rotation) + r.nextInt(orthagonalJitter) - (orthagonalJitter/2);
			if(i==0){
				polygon.moveTo(px, py);
			}else{
				polygon.lineTo(px, py);	
			}
			
		}
		
		polygon.closePath();
		g.fill(polygon);
		
		//g.fill (e);
	}
	
	private void paintSun(Graphics2D g){
		Ellipse2D.Double e = new Ellipse2D.Double ();
		int radius = 100;
		int x = (int) (GameObject.worldWidth / 2);
		int y = (int) (GameObject.worldHeight / 2);
		e.setFrame (x - radius, y - radius, 2 * radius, 2 * radius);
		RadialGradientPaint roundGradientPaint = new RadialGradientPaint(x, y, (int) (radius*1.2), (int)GameObject.worldWidth/2, (int)GameObject.worldHeight/2, new float[]{0, 0.05f, 0.2f, 1}, new Color[]{Color.WHITE, new Color(0,40,41, 191), new Color(0,10,10, 0), new Color(0,0,0,0)}, CycleMethod.NO_CYCLE);
		g.setPaint(roundGradientPaint);
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
			Color c = new Color(s.getColour());

			paintSpaceshipPart(g,(int)s.getLocation().getX()      ,(int) s.getLocation().getY()      ,s.getDirection(),s.isAccelerating(), c);
			paintSpaceshipPart(g,(int)s.getLocation().getX()      ,(int) s.getMirrorLocation().getY(),s.getDirection(),s.isAccelerating(), c);
			paintSpaceshipPart(g,(int)s.getMirrorLocation().getX(),(int) s.getLocation().getY()      ,s.getDirection(),s.isAccelerating(), c);
			paintSpaceshipPart(g,(int)s.getMirrorLocation().getX(),(int) s.getMirrorLocation().getY(),s.getDirection(),s.isAccelerating(), c);
			
		}
		
	}
	
	private void paintSpaceshipPart(Graphics2D g, int x, int y, double direction, boolean isAccelerating, Color c){
		
		// Spaceship accelerating -> continue, otherwise abort.
		Polygon p;
		if (isAccelerating){
			// Draw flame at the exhaust
			p = new Polygon ();
			p.addPoint ((int)(x - Math.sin (direction			     ) * 25), (int)(y + Math.cos (direction			       ) * 25));
			p.addPoint ((int)(x + Math.sin (direction + 0.9 * Math.PI) * 15), (int)(y - Math.cos (direction + 0.9 * Math.PI) * 15));
			p.addPoint ((int)(x + Math.sin (direction + 1.1 * Math.PI) * 15), (int)(y - Math.cos (direction + 1.1 * Math.PI) * 15));
			g.setColor(new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue()));
			//g.setColor(Color.YELLOW);
			g.fill(p);
		}
		
		// Draw body of the spaceship
		p = new Polygon ();
		p.addPoint ((int)(x + Math.sin (direction				 ) * 20), (int)(y - Math.cos (direction				   ) * 20));
		p.addPoint ((int)(x + Math.sin (direction + 0.8 * Math.PI) * 20), (int)(y - Math.cos (direction + 0.8 * Math.PI) * 20));
		p.addPoint ((int)(x + Math.sin (direction + 1.2 * Math.PI) * 20), (int)(y - Math.cos (direction + 1.2 * Math.PI) * 20));

		g.setColor (c);
		g.fill (p);
		//g.setColor (Color.GREEN);
		//g.setColor(new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue()));
		g.draw (p);

		


	}

	private void paintGameMessages(Graphics2D g, List<GameMessage> messages){
		for(int i=0;i<messages.size();i++){
			GameMessage m = messages.get(i);
			String str = m.toString();
			Logging.LOGGER.fine("opacity:"+m.getOpacity());
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, 20));
			g.setColor(new Color(1,1,1,m.getOpacity()));
			FontMetrics fm = g.getFontMetrics();
			int stringWidth = fm.stringWidth(str);
			int stringHeight = fm.getHeight();
			
			
			g.drawString(str, ((int)GameObject.worldWidth/2)-(stringWidth/2), ((int)GameObject.worldHeight/2)+(stringHeight*i));
		}
	}
	private void paintScores(Graphics2D g) {
		g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, 20));
		FontMetrics fm = g.getFontMetrics();
		int yPos = 5;
		List<Spaceship> spaceships = new ArrayList(this.game.getSpaceships());
		Collections.sort(spaceships, new Comparator<Spaceship>(){public int compare(Spaceship s0, Spaceship s1){
			return s0.getScore() - s1.getScore();
		}});
		Collections.reverse(spaceships);
		for(Spaceship s : spaceships){
			if(s==null){
				continue;
			}
			Color c0 = new Color((int) s.getColour());
			Color c = new Color(c0.getRed(), c0.getGreen(), c0.getBlue(), 191);
			g.setColor(c);
			String score = s.getName() + ": " + Integer.toString(s.getScore());
			yPos += fm.getHeight();
			g.drawString(score, this.getWidth()-fm.stringWidth(score)-5, yPos);
		}
	}
	
	
	private void paintExplosion(Graphics2D g, Explosion e){
		Random r = new Random(e.getSeed());
		float time = e.getTime();
		Ellipse2D.Double ell = new Ellipse2D.Double ();

	    for(float i=1;i<=Explosion.particleAmount;i++){
	        double d=r.nextDouble()*Math.PI*2;
	        double fade=(i/128.0)*time;
	        fade /= 1 - (time/(Explosion.maxTimeUntilFadeout));
	        int x,y,radius, finalx, finaly;
	        x =(int)  (Math.sin(d)*(time*r.nextDouble())*.1);//(r.nextInt(10) - 5);
	        y =(int)  (Math.cos(d)*(time*r.nextDouble())*.1);//(r.nextInt(10) - 5);
	        radius = (int) (e.getRadius() + (time *.02));
	        
	        int alpha = Math.max(0,255-(int)fade);
	        if(alpha > 255){
	        	alpha = 0;
	        }
	        Color oc = new Color(e.getColor());
	        Color c = new Color(oc.getRed(), oc.getGreen(), oc.getBlue(),alpha);
	        Color endc = new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), 0);

	        finalx = (int) e.getLocation().x + x;
	        finaly = (int)e.getLocation().y + y;
	        
	        
	        RadialGradientPaint roundGradientPaint = new RadialGradientPaint(finalx, finaly, radius, finalx, finaly, new float[]{0, 1}, new Color[]{c, endc}, CycleMethod.NO_CYCLE);
			g.setPaint(roundGradientPaint);
			
			
			ell.setFrame (finalx - radius, finaly - radius, 2 * radius, 2 * radius);
			g.fill(ell);
	    }
	    
		
	    
	}
}
