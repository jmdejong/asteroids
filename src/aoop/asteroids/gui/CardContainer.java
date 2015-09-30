package aoop.asteroids.gui;

import javax.swing.JPanel;
import java.awt.CardLayout;

public class CardContainer extends JPanel{
	
	private CardLayout layout;
	
	public CardContainer(){
		
		layout = new CardLayout();
		this.setLayout(layout);
		
		
	}
	
	public void showCard(String name){
		layout.show(this, name);
	}
}