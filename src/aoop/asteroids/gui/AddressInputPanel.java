package aoop.asteroids.gui;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;

import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
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
		addressField.setBackground(AsteroidsFrame.ButtonBackColor);
		addressField.setForeground(AsteroidsFrame.TextColor);
		addressField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, AsteroidsFrame.ButtonBorderWidth), new EmptyBorder(5, 5, 5, 5)));
		addressField.setText("127.0.0.1");
		
		this.add(addressField);
		
		
		
		connectButton = new JButton("Connect");
		connectButton.setAlignmentX( Component.CENTER_ALIGNMENT );
		connectButton.setMaximumSize( new Dimension(80,50) );
		connectButton.setBackground(AsteroidsFrame.ButtonBackColor);
		connectButton.setForeground(AsteroidsFrame.TextColor);
		connectButton.setBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, AsteroidsFrame.ButtonBorderWidth));
		connectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.add(connectButton);
		
	}
	
	public void addClickListener(ActionListener connectAction){
		connectButton.addActionListener(connectAction);
	}

	
	public String getAddress(){
		return addressField.getText();
	}
	
	
}
