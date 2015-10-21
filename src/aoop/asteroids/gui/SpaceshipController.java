package aoop.asteroids.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;


/**
 * This class listens to key presses, and keeps track of them after the actual key-events have happened.
 * To reduce code-duplication, a map is used to keep track of individual key states.
 * @author qqwy
 *
 */
public class SpaceshipController implements KeyListener{
	
	private Map<Integer,Boolean> keyStates;
	
	/**
	 * Initializes all key states to false.
	 */
	public SpaceshipController(){
		this.keyStates = new HashMap<Integer,Boolean>();
		this.keyStates.put(KeyEvent.VK_UP,    false);
		this.keyStates.put(KeyEvent.VK_LEFT,  false);
		this.keyStates.put(KeyEvent.VK_RIGHT, false);
		this.keyStates.put(KeyEvent.VK_SPACE, false);
	}

	/**
	 * When a key is pressed, update its state to 'true'.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(this.keyStates.containsKey(e.getKeyCode())){
			this.keyStates.put(e.getKeyCode(),true);
		}
	}

	/**
	 * When a key is released, update its state to 'false'.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if(this.keyStates.containsKey(e.getKeyCode())){
			this.keyStates.put(e.getKeyCode(),false);
		}
		
	}

	
	/**
	 * We are not interested in typed events.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * @return if the 'up' key is currently pressed, which makes the spaceship go forward.
	 */
	public boolean isUp() {
		return keyStates.get(KeyEvent.VK_UP);
	}
	
	/**
	 * @return if the 'right' key is currently pressed, which makes the spaceship rotate right.
	 */
	public boolean isRight() {
		return keyStates.get(KeyEvent.VK_RIGHT);
	}
	
	/**
	 * @return if the 'left' key is currently pressed, which makes the spaceship rotate left.
	 */
	public boolean isLeft() {
		return keyStates.get(KeyEvent.VK_LEFT);
	}
	
	/**
	 * @return if the 'space' key is currently pressed, which makes the spaceship fire a bullet.
	 */
	public boolean isFire() {
		return keyStates.get(KeyEvent.VK_SPACE);
	}

}
