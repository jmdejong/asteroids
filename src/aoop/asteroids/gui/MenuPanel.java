package aoop.asteroids.gui;


import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
	
	
	public MenuPanel() {
		
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createRigidArea(new Dimension(0, 200)));
		
		
		
	}
	
	public void makeButton(String text, ActionListener action){
		
		JButton button = new JButton(text);
		button.setAlignmentX( Component.CENTER_ALIGNMENT );
		button.setMaximumSize( new Dimension(300,100) );
		button.setBackground(Color.GRAY);
		button.setForeground(Color.WHITE);
		button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
		this.add(button);
		
		button.addActionListener(action);
		
	}
}