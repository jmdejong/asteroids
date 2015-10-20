package aoop.asteroids.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class SpaceshipController implements KeyListener{
	private Map<Integer,Boolean> keyStates;
	
	public SpaceshipController(){
		this.keyStates = new HashMap<Integer,Boolean>();
		this.keyStates.put(KeyEvent.VK_UP,    false);
		this.keyStates.put(KeyEvent.VK_LEFT,  false);
		this.keyStates.put(KeyEvent.VK_RIGHT, false);
		this.keyStates.put(KeyEvent.VK_SPACE, false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(this.keyStates.containsKey(e.getKeyCode())){
			this.keyStates.remove(e.getKeyCode());
			this.keyStates.put(e.getKeyCode(),true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(this.keyStates.containsKey(e.getKeyCode())){
			this.keyStates.remove(e.getKeyCode());
			this.keyStates.put(e.getKeyCode(),false);
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	public boolean isUp() {
		return keyStates.get(KeyEvent.VK_UP);
	}
	
	public boolean isRight() {
		return keyStates.get(KeyEvent.VK_RIGHT);
	}

	public boolean isFire() {
		return keyStates.get(KeyEvent.VK_SPACE);
	}

	public boolean isLeft() {
		return keyStates.get(KeyEvent.VK_LEFT);
	}

}
