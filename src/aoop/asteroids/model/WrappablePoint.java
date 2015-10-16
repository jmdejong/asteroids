package aoop.asteroids.model;


import java.awt.geom.Point2D;
import aoop.asteroids.Utils;

/**
 * A WrappablePoint provides the same functionality as Point2d.Double,
 * but when giving WrappablePoint a domainWidth and domainHeight, values will automatically be converted to fit in the domain;<br>
 * e.g. WrappablePoint.x == Point2D.Double.x (mod) WrappablePoint.domainWidth
 * @author qqwy, troido
 *
 */
public class WrappablePoint extends Point2D.Double {
	
	/* TODO:
	 * - Do we really need this?
	 *   It might be better to only wrap the location when we need it, not when we set it
	 *   Without this class it's probably easier to avoid a public map size
	 *   because then not all objects need the size
	 */
	private static final long serialVersionUID = 1L;
	private double domainWidth = java.lang.Double.POSITIVE_INFINITY;
	private double domainHeight = java.lang.Double.POSITIVE_INFINITY;
	
	public WrappablePoint(double x, double y){
		super(x,y);
	}
	
	public WrappablePoint(double x, double y, double domainWidth, double domainHeight){
		this.x = this.y = 0;
		this.setDomain(domainWidth, domainHeight);
		this.setLocation(x,y);
	}
	
	/**
	 * Sets the location of the WrappablePoint to the new x and y.<br>
	 * Note that these values are automatically wrapped to fit the range [0..domainWidth)<br>
	 * As in Java, the `%` operator rounds towards 0 (-3 % 26 == -3 and not 23), we have to add the domain ourselves when working with negative coordinates.<br>
	 * In Java 8, the Math.floorMod function could be used instead of %, but we assume usage of Java 7.<br>
	 * In Utils.java we made a floorMod function that does this.
	 */
	@Override
	public void setLocation(double x, double y){
		super.setLocation(Utils.floorMod(x, domainWidth), Utils.floorMod(y, domainHeight));
	}
	
	@Override
	public void setLocation(Point2D p){
		setLocation(p.getX(), p.getY());
	}
	
	public void setDomain(double domainWidth, double domainHeight){
		this.domainWidth = domainWidth;
		this.domainHeight = domainHeight;
		this.setLocation(this.getX(),this.getY());
	}
	
}
