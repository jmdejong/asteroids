package aoop.asteroids.gui;

import aoop.asteroids.Logging;
import aoop.asteroids.Utils;
import aoop.asteroids.model.Message;
import aoop.asteroids.model.game.ClientGame;
import aoop.asteroids.model.gameobjects.Asteroid;
import aoop.asteroids.model.gameobjects.Bullet;
import aoop.asteroids.model.gameobjects.Explosion;
import aoop.asteroids.model.gameobjects.Spaceship;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 *	AsteroidsPanel extends JPanel and thus provides the actual graphical 
 *	representation of the game state.
 *
 *	@author qqwy
 */
public class AsteroidsPanel extends JPanel
{
	
	/* TODO:
	 * - Show whether hosting/joining/spectating and on what address?
	 * DONE:
	 * - Consistently use addRotatedPoint or not use it at all
	 * - Avoid using GameObject.worldWidth or GameObject.worldHeight
	 * - Make explosions go over edges
	 * 
	 */
	
	/** serialVersionUID */
	public static final long serialVersionUID = 4L;

	/** Game model. */
	private ClientGame game;

	/** 
	 *	Constructs a new game panel, based on the given model.
	 *
	 *	@param game game model.
	 */

	public void observeGame(ClientGame game)
	{
		this.game = game;
		this.game.addObserver(new Observer ()
		{
			@Override
			public void update(Observable o, Object arg)
			{
				repaint();
			}
		});
		setSize(game.getWidth(), game.getHeight());
		Logging.LOGGER.fine("Size in the Panel: "+this.getSize().toString());
	}
	
	/**
	 *	Method for refreshing the GUI.
	 *  Paints the current game state on the screen.
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
		
		
		BufferedImage bgimage = game.getBgImage();
		if (bgimage != null){
			paintBackground(g2, bgimage, (int)game.getBgPos().getX(),(int) game.getBgPos().getY());
		}
		
		
		
		g2.setColor(new Color(0,0,0,0.5f));
		g2.fillRect(0, 0, (int)this.game.getWidth(), (int)this.game.getHeight());
		
		

		this.paintBullets (g2);
		this.paintSpaceships (g2);
		this.paintAsteroids (g2);
		
		
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
	 *	Draws all bullets in the GUI as a circle.
	 *  The colour of the circle is the complement of the shooter's colour.
	 *	@param g graphics instance to use.
	 */
	private void paintBullets (Graphics2D g)
	{
		
		for (Bullet b : this.game.getBullets ()){
			Color c = new Color(b.getColour());
			g.setColor(Utils.getComplementColour(c));
			Point2D location = b.getWrappedLocation(game.getWidth(), game.getHeight());
		    g.fillOval (((int)location.getX()) - 2, ((int)location.getY()) - 2, 5, 5);
		}
	}

	/**
	 *	Draws all asteroids in the GUI as filled polygons.
	 *  Wraps around the screen edges, by drawing objects that are close at both sides of the screen.
	 *	@param g graphics instance to use.
	 */
	private void paintAsteroids (Graphics2D g)
	{
		g.setColor (Color.GRAY);

		for (Asteroid a : this.game.getAsteroids ()) {
			
			int seed = (int)(3*a.getVelocityX()+5*a.getVelocityY());
			for (Point2D location : a.getMirrorLocations(this.game.getWidth(), this.game.getHeight())){
				paintAsteroidPart(g, (int)location.getX(), (int)location.getY(), a.getRadius(), seed, a.getRotation());
			}
		}
	}
	
	/**
	 * Draws an asteroid at a certain screen location
	 * @param g reference to the graphics object
	 * @param x x-location on screen
	 * @param y y-location on screen
	 * @param radius of the asteroid
	 * @param seed Seeds the Random Number Generator, determining the actual Asteroid's shape. This is synchronized between clients.
	 * @param rotation The current rotation of the asteroid.
	 */
	private void paintAsteroidPart(Graphics2D g, int x, int y, int radius, int seed, double rotation){
		
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		Random r = new Random(seed);
		
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
		
		
		g.setColor(Color.BLACK);
		g.fill(polygon);
		
		
		for(Spaceship s : this.game.getSpaceships()){
			if(s==null || s.isDestroyed()){
				continue;
			}
			
			double spaceshipX = Utils.getClosestPoint(x, s.getLocation().getX(), this.game.getWidth());
			double spaceshipY = Utils.getClosestPoint(y, s.getLocation().getY(), this.game.getHeight());
			
			
			double distanceX = x-spaceshipX;
			double distanceY = y-spaceshipY;
			double distanceSquared = distanceX * distanceX + distanceY * distanceY;
			double maxDistance = Math.min(this.game.getWidth(), this.game.getHeight())/2;
			int intensity = (int) Math.max(255*(1-(distanceSquared/(maxDistance*maxDistance))),0);
			
			Color c = new Color(s.getColour());
			RadialGradientPaint playerLight = new RadialGradientPaint(x, y, (int) (radius*1.4), (int)spaceshipX, (int)spaceshipY, new float[]{0,1}, new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), intensity), new Color(c.getRed(),c.getGreen(),c.getBlue(),0)/*DARK_GRAY*/}, CycleMethod.NO_CYCLE);
		
			g.setPaint(playerLight);
			g.fill(polygon);
		}
	}
	


	/**
	 *  Draws all spaceships as triangles, filled with a gradient.
	 *  Wraps around the screen edges, by drawing objects that are close at both sides of the screen.
	 *	@param g graphics instance to use.
	 */
	private void paintSpaceships (Graphics2D g)
	{
		for(Spaceship s : this.game.getSpaceships()){
			if(s==null || s.isDestroyed()){
				continue;
			}
			Color c = new Color(s.getColour());
			
			for (Point2D location : s.getMirrorLocations(this.game.getWidth(), this.game.getHeight())){
				paintSpaceshipPart(g, (int)location.getX(), (int)location.getY(), s.getDirection(), s.isAccelerating(), c);
			}
			
		}
		
	}
	
	private void addRotatedPoint(Polygon polygon, double centerX, double centerY, double dx, double dy, double rotationX, double rotationY){
		polygon.addPoint((int)(centerX + Utils.imagMultI(dx,dy,rotationX,rotationY)), (int)(centerY + Utils.imagMultR(dx,dy,rotationX,rotationY)));
	}
	
	/**
	 *  Draws a spaceship at a certain screen position.
	 *  A spaceship is represented by a triangle, filled with a gradient.
	 *  If a spaceship is accelerating, it's exhaust, which has the opposite colour of the spaceship, is also drawn. 
	 * @param g reference to the graphics object
	 * @param x x-location on screen
	 * @param y y-location on screen
	 * @param direction The direction the ship is facing (in radians)
	 * @param isAccelerating if true, the exhaust will be drawn
	 * @param c the colour to use for this ship
	 */
	private void paintSpaceshipPart(Graphics2D g, int x, int y, double direction, boolean isAccelerating, Color c){
		
		//Draw spaceship glow
		Color startc = new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
		Color endc = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
		int glowradius = 50;
		RadialGradientPaint roundGradientPaint = new RadialGradientPaint(x, y, glowradius, x, y, new float[]{0, 1}, new Color[]{startc, endc}, CycleMethod.NO_CYCLE);
		g.setPaint(roundGradientPaint);
		Ellipse2D.Double ell = new Ellipse2D.Double();
		ell.setFrame (x - glowradius, y - glowradius, 2 * glowradius, 2 * glowradius);
		g.fill(ell);
		
		
		Polygon p;
		if (isAccelerating){// Spaceship accelerating -> continue, otherwise abort.
			// Draw flame at the exhaust
			p = new Polygon ();
			double exhaustLength = 10*(Math.random()*Math.random());
			p.addPoint ((int)(x - Math.sin (direction			     ) * (25+exhaustLength)), (int)(y + Math.cos (direction			       ) * (25+exhaustLength)));
			p.addPoint ((int)(x + Math.sin (direction + 0.9 * Math.PI) * 15 ), (int)(y - Math.cos (direction + 0.9 * Math.PI) * 15));
			p.addPoint ((int)(x + Math.sin (direction + 1.1 * Math.PI) * 15), (int)(y - Math.cos (direction + 1.1 * Math.PI) * 15));
			g.setColor(Utils.getComplementColour(c));
			g.fill(p);
		}
		
		double directionY = Math.cos(direction);
		double directionX = Math.sin(direction);
		
		// Draw body of the spaceship
		for(int i=11;i>0;i--){
			p = new Polygon ();
			addRotatedPoint(p, x,y, 0,20, directionX,directionY);
			addRotatedPoint(p, x,y, i,-16, directionX,directionY);
			addRotatedPoint(p, x,y, -i,-16, directionX,directionY);
			float ratio2 =.2f+1f*(1-((float)i/11));
			Color pc = c;
			int red,blue,green;
			red = Math.max(0,Math.min(255,(int)(pc.getRed()*ratio2)));
			green = Math.max(0,Math.min(255,(int)(pc.getGreen()*ratio2)));
			blue = Math.max(0,Math.min(255,(int)(pc.getBlue()*ratio2)));
			
			g.setColor (new Color(red,green,blue));
			g.fill (p);
		}
		
	}

	/**
	 * Paints GameMessage's as text-strings on top of the game.
	 * These fade in and later fade-out, depending on their opacity.
	 * @param g reference to the Graphics2D object.
	 * @param messages list of GameMessage objects to draw.
	 */
	private void paintGameMessages(Graphics2D g, List<Message> messages){
		for(int i=0;i<messages.size();i++){
			Message m = messages.get(i);
			String str = m.toString();
			Logging.LOGGER.fine("opacity:"+m.getOpacity());
			g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, 18));
			g.setColor(new Color(1,1,1,m.getOpacity()));
			FontMetrics fm = g.getFontMetrics();
			int stringWidth = fm.stringWidth(str);
			int stringHeight = fm.getHeight();
			
			
			g.drawString(str, ((int)this.game.getWidth()/2)-(stringWidth/2), ((int)this.game.getHeight()/2)+(stringHeight*i));
		}
	}
	
	/**
	 * Paints the current player names + scores in the top right.
	 * Scores have the same colour as the corresponding player (and ship).
	 * Scores are ordered high-to-low (top to bottom).
	 * @param g reference to the Graphics2D object.
	 */
	private void paintScores(Graphics2D g) {
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		FontMetrics fm = g.getFontMetrics();
		int yPos = 5;
		List<Spaceship> spaceships = new ArrayList<Spaceship>(this.game.getSpaceships());
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
			String score = String.format("%s: %5d",s.getName(), s.getScore());//s.getName() + ": " + Integer.toString(s.getScore());
			yPos += fm.getHeight();
			g.drawString(score, this.getWidth()-fm.stringWidth(score)-5, yPos);
		}
	}
	
	/**
	 * Draws an explosion on the screen.
	 * An explosion internally has a seed, a position, a color and a current time.
	 * From this, a pseudorandom list of particle positions are generated, and these are then drawn as circular gradients.
	 * @param g
	 * @param e
	 */
	private void paintExplosion(Graphics2D g, Explosion e){
		Random r = new Random(e.getSeed());
		float time = e.getTime();
		Ellipse2D.Double ell = new Ellipse2D.Double ();

		for(float i=1;i<=Explosion.particleAmount;i++){
			double d=r.nextDouble()*Math.PI*2;
			double fade=(i/128.0)*time;
			fade /= 1 - (time/(Explosion.maxTimeUntilFadeout));
			int x,y,radius, finalx, finaly;
			x =(int)  (Math.sin(d)*(time*r.nextDouble())*.1);
			y =(int)  (Math.cos(d)*(time*r.nextDouble())*.1);
			radius = (int) (20+ (time *.02));
			
			int alpha = Math.max(0,255-(int)fade);
			if(alpha > 255){
				alpha = 0;
			}
			Color oc = new Color(e.getColour());
			Color c = new Color(oc.getRed(), oc.getGreen(), oc.getBlue(),alpha);
			Color endc = new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), 0);
				        
	        for(Point2D el : e.getMirrorLocations(game.getWidth(), game.getHeight(), radius)){
	        	finalx =(int)(el.getX()) + x;
	        	finaly =(int)(el.getY()) + y;
	        	

		        RadialGradientPaint roundGradientPaint = new RadialGradientPaint(finalx, finaly, radius, finalx, finaly, new float[]{0, 1}, new Color[]{c, endc}, CycleMethod.NO_CYCLE);
				g.setPaint(roundGradientPaint);
				
				
				ell.setFrame (finalx - radius, finaly - radius, 2 * radius, 2 * radius);
				g.fill(ell);
	        }
	        
	        
	    }
	}
	   
	/**
	 * Paints the background image on the screen.
	 * This image is tiled(repeating )in both directions, and slowly moves.
	 * @param g reference to the Graphics2D object.
	 * @param i the image to draw
	 * @param xoffset the amount to move it in the x-direction
	 * @param yoffset the amount to move it in the y-direction
	 */
    private void paintBackground(Graphics2D g, BufferedImage i, int xoffset, int yoffset){
    	int iw, ih;
    	iw = i.getWidth();
    	ih = i.getHeight();
    	
    	
    	
    	Rectangle imageBounds = new Rectangle(xoffset, yoffset, iw, ih);
	    TexturePaint    tp = new TexturePaint(i, imageBounds);
	    
	    g.setPaint(tp);
	    g.fillRect(0, 0, (int) this.game.getWidth(), (int) this.game.getHeight());
	}
    

}
