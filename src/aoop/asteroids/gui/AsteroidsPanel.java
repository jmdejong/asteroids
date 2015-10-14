package aoop.asteroids.gui;

import aoop.asteroids.Logging;
import aoop.asteroids.Utils;
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
import java.awt.Point;
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
		this.setSize(game.getWidth(), game.getHeight());
		Logging.LOGGER.info("Size in the Panel: "+this.getSize().toString());
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
		
		
		BufferedImage bgimage = game.getBgImage();
		if (bgimage != null){
			paintBackground(g2, bgimage, (int)game.bgPos.x,(int) game.bgPos.y);
		}
		
		
		
		g2.setColor(new Color(0,0,0,0.5f));
		g2.fillRect(0, 0, (int)GameObject.worldWidth, (int)GameObject.worldHeight);
		
		
		//this.paintSun(g2);

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
		
		
		RadialGradientPaint sunlight = new RadialGradientPaint(x, y, (int) (radius*1.2), (int)GameObject.worldWidth/2, (int)GameObject.worldHeight/2, new float[]{0,1}, new Color[]{new Color(191,191,191,255), new Color(191,191,191,32)/*DARK_GRAY*/}, CycleMethod.NO_CYCLE);
		
		
		
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
		
		
		g.setColor(Color.BLACK);
		g.fill(polygon);
		
		
		for(Spaceship s : this.game.getSpaceships()){
			if(s==null || s.isDestroyed()){
				continue;
			}
			
			// get the closest spaceship location
			double mx = x - GameObject.worldWidth/2;
			double spaceshipX = Utils.floorMod(s.getLocation().getX()-mx, GameObject.worldWidth)+mx;
			double my = y - GameObject.worldHeight/2;
			double spaceshipY = Utils.floorMod(s.getLocation().getY()-my, GameObject.worldHeight)+my;
			
			
			double distanceX = x-spaceshipX;
			double distanceY = y-spaceshipY;
			double distanceSquared = distanceX * distanceX + distanceY * distanceY;
			double maxDistance = Math.min(GameObject.worldWidth, GameObject.worldHeight)/2;
			int intensity = (int) Math.max(255*(1-(distanceSquared/(maxDistance*maxDistance))),0);
			
			Color c = new Color(s.getColour());
			RadialGradientPaint playerLight = new RadialGradientPaint(x, y, (int) (radius*1.2), (int)spaceshipX, (int)spaceshipY, new float[]{0,1}, new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), intensity), new Color(c.getRed(),c.getGreen(),c.getBlue(),0)/*DARK_GRAY*/}, CycleMethod.NO_CYCLE);
		
			g.setPaint(playerLight);
			g.fill(polygon);
		}
		
// 		g.setPaint(sunlight);
// 		g.fill(polygon);
	}
	
// 	private void paintAsteroidShape(Graphics2D g, int x, int y, int radius, double seed, double rotation){
// 	
// 	
// 	}
	
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
			int xa,xb,ya,yb;
			xa = (int)s.getLocation().getX();
			xb = (int)s.getMirrorLocation().getX();
			ya = (int) s.getLocation().getY();
			yb = (int) s.getMirrorLocation().getY();

			paintSpaceshipPart(g,xa,ya,s.getDirection(),s.isAccelerating(), c);
			if(yb != ya){
				paintSpaceshipPart(g,xa,yb,s.getDirection(),s.isAccelerating(), c);
			}
			if(xb != xa){
				paintSpaceshipPart(g,xb,ya,s.getDirection(),s.isAccelerating(), c);
				if(yb != ya){
					paintSpaceshipPart(g,xb,yb,s.getDirection(),s.isAccelerating(), c);
				}
				
			}
			
			
			
		}
		
	}
	
	private void addRotatedPoint(Polygon polygon, double centerX, double centerY, double dx, double dy, double rotationX, double rotationY){
		polygon.addPoint((int)(centerX + Utils.imagMultI(dx,dy,rotationX,rotationY)), (int)(centerY + Utils.imagMultR(dx,dy,rotationX,rotationY)));
	}
	
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
			p.addPoint ((int)(x - Math.sin (direction			     ) * 25), (int)(y + Math.cos (direction			       ) * 25));
			p.addPoint ((int)(x + Math.sin (direction + 0.9 * Math.PI) * 15), (int)(y - Math.cos (direction + 0.9 * Math.PI) * 15));
			p.addPoint ((int)(x + Math.sin (direction + 1.1 * Math.PI) * 15), (int)(y - Math.cos (direction + 1.1 * Math.PI) * 15));
			g.setColor(new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue()));
			//g.setColor(Color.YELLOW);
			g.fill(p);
		}
		
		double directionY = Math.cos(direction);
		double directionX = Math.sin(direction);
		
		// Draw body of the spaceship
		p = new Polygon ();
		addRotatedPoint(p, x,y, 0,20, directionX,directionY);
		addRotatedPoint(p, x,y, 11,-16, directionX,directionY);
		addRotatedPoint(p, x,y, -11,-16, directionX,directionY);
		/*p.addPoint ((int)(x + Utils.imagMultI(0,20,direction_x,direction_y)), (int)(y + Utils.imagMultR(0,20,direction_x,direction_y)));
		p.addPoint ((int)(x + Utils.imagMultI(11,-16,direction_x,direction_y)), (int)(y + Utils.imagMultR(11,-16,direction_x,direction_y)));
		p.addPoint ((int)(x + Utils.imagMultI(-11,-16,direction_x,direction_y)), (int)(y + Utils.imagMultR(-11,-16,direction_x,direction_y)))*/;

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
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
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
			String score = String.format("%s: %5d",s.getName(), s.getScore());//s.getName() + ": " + Integer.toString(s.getScore());
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
	    
    private void paintBackground(Graphics2D g, BufferedImage i, int xoffset, int yoffset){
    	int iw, ih;
    	iw = i.getWidth();
    	ih = i.getHeight();
    	
    	
    	
    	Rectangle imageBounds = new Rectangle(xoffset, yoffset, iw, ih);
	    TexturePaint    tp = new TexturePaint(i, imageBounds);
	    
	    g.setPaint(tp);
	    g.fillRect(0, 0, (int) GameObject.worldWidth, (int) GameObject.worldHeight);
	}
}
