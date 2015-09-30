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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddressInputPanel extends JPanel {
	
	JTextField addressField;
	JButton connectButton;
	
	public AddressInputPanel(){
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createRigidArea(new Dimension(200, 0)));
		
		
		
		addressField = new JTextField();
		addressField.setAlignmentY( Component.CENTER_ALIGNMENT );
		addressField.setMaximumSize( new Dimension(200,50) );
		addressField.setBackground(Color.GRAY);
		addressField.setForeground(Color.WHITE);
		addressField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
		this.add(addressField);
		
		connectButton = new JButton("Connect");
		connectButton.setAlignmentX( Component.CENTER_ALIGNMENT );
		connectButton.setMaximumSize( new Dimension(80,50) );
		connectButton.setBackground(Color.GRAY);
		connectButton.setForeground(Color.WHITE);
		connectButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
		this.add(connectButton);
		
	}
	
	public void addClickListener(ActionListener connectAction){
		connectButton.addActionListener(connectAction);
	}
	
	public String getAddress(){
		return addressField.getText();
	}
	
	
}
