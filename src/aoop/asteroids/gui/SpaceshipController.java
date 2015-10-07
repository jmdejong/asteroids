package aoop.asteroids.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SpaceshipController implements KeyListener{
	private boolean up,left,right,fire = false;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode ()) 
		{
			case KeyEvent.VK_UP:
				this.setUp(true);
				break;
			case KeyEvent.VK_LEFT:
				this.setLeft(true);
				break;
			case KeyEvent.VK_RIGHT:
				this.setRight(true);
				break;
			case KeyEvent.VK_SPACE:
				this.setFire(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode ()) 
		{
			case KeyEvent.VK_UP:
				this.setUp(false);
				break;
			case KeyEvent.VK_LEFT:
				this.setLeft(false);
				break;
			case KeyEvent.VK_RIGHT:
				this.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				this.setFire(false);
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isFire() {
		return fire;
	}

	public void setFire(boolean fire) {
		this.fire = fire;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

}
