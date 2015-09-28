package aoop.asteroids.gui;

import aoop.asteroids.model.Spaceship;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *	Player is the controller class that listens to the users input (key events) 
 *	and forwards those events to the spaceship class. Where they will influence 
 *	the behaviour of the spaceship.
 *
 *	@author Yannick Stoffers
 */
public class Player implements KeyListener
{

	/** The spaceship that is being influenced. */
	private Spaceship ship;

	/** 
	 *	This method allows one to add a ship to an otherwise useless object. 
	 *
	 *	@param ship this is the spaceship that this class will influence from 
	 *				now on.
	 */
	public void addShip (Spaceship ship)
	{
		this.ship = ship;
	}

	/**
	 *	This method is invoked when a key is pressed and sets the corresponding 
	 *	fields in the spaceship to true.
	 *
	 *	@param e keyevent that triggered the method.
	 */
	@Override
	public void keyPressed (KeyEvent e)
	{
		switch (e.getKeyCode ()) 
		{
			case KeyEvent.VK_UP:
				this.ship.setUp (true);
				break;
			case KeyEvent.VK_LEFT:
				this.ship.setLeft (true);
				break;
			case KeyEvent.VK_RIGHT:
				this.ship.setRight (true);
				break;
			case KeyEvent.VK_SPACE:
				this.ship.setIsFiring (true);
		}
	}

	/**
	 *	This method is invoked when a key is released and sets the correspon-
	 *	ding fields in the spaceship to false.
	 *
	 *	@param e keyevent that triggered the method.
	 */
	@Override
	public void keyReleased (KeyEvent e)
	{
		switch (e.getKeyCode ()) 
		{
			case KeyEvent.VK_UP:
				this.ship.setUp (false);
				break;
			case KeyEvent.VK_LEFT:
				this.ship.setLeft (false);
				break;
			case KeyEvent.VK_RIGHT:
				this.ship.setRight (false);
				break;
			case KeyEvent.VK_SPACE:
				this.ship.setIsFiring (false);
		}
	}

	/**
	 *	This method doesn't do anything.
	 *	
	 *	@param e keyevent that triggered the method.
	 */
	@Override
	public void keyTyped (KeyEvent e) {}

}
