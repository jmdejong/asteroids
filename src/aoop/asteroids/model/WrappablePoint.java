package aoop.asteroids.model;

import java.awt.geom.Point2D;

/**
 * A WrappablePoint provides the same functionality as Point2d.Double,
 * but when giving WrappablePoint a domainWidth and domainHeight, values will automatically be converted to fit in the domain;<br>
 * e.g. WrappablePoint.x == Point2D.Double.x (mod) WrappablePoint.domainWidth
 * @author qqwy
 *
 */
public class WrappablePoint extends Point2D.Double {
	private static final long serialVersionUID = 1L;
	private double domainWidth, domainHeight = java.lang.Double.POSITIVE_INFINITY;
	
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
	 * As in Java, the `%` operator works towards negative infinity (-3 % 26 == -3 and not 23), we have to add the domain ourselves when working with negative coordinates.<br>
	 * In Java 8, the Math.floorMod function could be used instead of %, but we assume usage of Java 7.
	 */
	@Override
	public void setLocation(double x, double y){
		setX(x);
		setY(y);
	}
	
	public void setDomain(double domainWidth, double domainHeight){
		this.domainWidth = domainWidth;
		this.domainHeight = domainHeight;
		this.setLocation(this.getX(),this.getY());
	}
	
	public void setX(double x){
		this.x = (domainWidth + x) % domainWidth;
	}
	public void setY(double y){
		this.y = (domainHeight + y) % domainHeight;
	}
}
