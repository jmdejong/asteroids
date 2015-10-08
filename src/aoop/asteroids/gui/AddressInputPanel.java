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
	JButton backButton;
	
	public AddressInputPanel(){
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of horizontal buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createRigidArea(new Dimension(180, 0)));
		
		
		
		addressField = new JTextField();
		addressField.setAlignmentY( Component.CENTER_ALIGNMENT );
		addressField.setMaximumSize( new Dimension(200,50) );
		addressField.setBackground(AsteroidsFrame.ButtonBackColor);
		addressField.setForeground(AsteroidsFrame.TextColor);
		addressField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, AsteroidsFrame.ButtonBorderWidth), new EmptyBorder(5, 5, 5, 5)));
		addressField.setText("127.0.0.1");
		
		this.add(addressField);
		
		
		add(Box.createRigidArea(new Dimension(20, 0)));
		
		connectButton = new JButton("Connect");
		connectButton.setAlignmentX( Component.CENTER_ALIGNMENT );
		connectButton.setMaximumSize( new Dimension(120,50) );
		connectButton.setBackground(AsteroidsFrame.ButtonBackColor);
		connectButton.setForeground(AsteroidsFrame.TextColor);
		connectButton.setBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, AsteroidsFrame.ButtonBorderWidth));
		connectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.add(connectButton);
		
		
		add(Box.createRigidArea(new Dimension(20, 0)));
		
		backButton = new JButton("Back to menu");
		backButton.setAlignmentX( Component.CENTER_ALIGNMENT );
		backButton.setMaximumSize( new Dimension(120,50) );
		backButton.setBackground(AsteroidsFrame.ButtonBackColor);
		backButton.setForeground(AsteroidsFrame.TextColor);
		backButton.setBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, AsteroidsFrame.ButtonBorderWidth));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.add(backButton);
		
	}
	
	public void setClickAction(ActionListener connectAction){
		connectButton.addActionListener(connectAction);
	}
	
	public void setBackAction(ActionListener backAction){
		backButton.addActionListener(backAction);
	}
	
	public String getAddress(){
		return addressField.getText();
	}
	
	
}
