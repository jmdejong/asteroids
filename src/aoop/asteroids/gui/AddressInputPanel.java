package aoop.asteroids.gui;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;

public class AddressInputPanel extends JPanel {
	
	public AddressInputPanel(){
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createRigidArea(new Dimension(0, 200)));
		
		
		
		JTextField addressField = new JTextField();
		addressField.setAlignmentX( Component.CENTER_ALIGNMENT );
		addressField.setMaximumSize( new Dimension(200,50) );
		addressField.setBackground(Color.GRAY);
		addressField.setForeground(Color.WHITE);
		addressField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
		this.add(addressField);
		
	}
	
	
}
