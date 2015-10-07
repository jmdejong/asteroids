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
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;
import java.util.Random;


public class MenuPanel extends JPanel {
	
	private JTextField nameField;
	
	public MenuPanel() {
		
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createRigidArea(new Dimension(0, 100)));
		
		nameField = new JTextField();
		nameField.setAlignmentY( Component.LEFT_ALIGNMENT );
		nameField.setMaximumSize( new Dimension(200,50) );
		nameField.setBackground(Color.WHITE);
		nameField.setForeground(Color.BLACK);
		nameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3), new EmptyBorder(5, 5, 5, 5)));
		nameField.setText( generateName());
		
		this.add(nameField);
		
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
	
	public String getPlayerName(){
		return this.nameField.getText();
	}
	
	
	/**
	 * Function that generates a nice-looking (First or Last) name.<br>
	 * Readability is improved by alternating between a vowel and a consonant.<br>
	 * Vowels that are common in the English language have a higher chance to end up in the names.<br>
	 * Names have between 3 and 12 characters.<br>
	 * This function always terminates.<br>
	 * @return the generated name
	 */
	public static String generateName(){
		String vowels = "aaaeeeiiiooouuuy";
		String consonants = "bbcddffgghjjkkllmmnnppqrrssttvvwxz";
		String name = "";
		Random rand = new Random();
		Boolean wroteVowel = rand.nextBoolean();
		int length = (int)(rand.nextFloat()*rand.nextFloat()*10+3);
		for (int i=0; i<length; i++){
			if (!wroteVowel){
				name += vowels.charAt(rand.nextInt(vowels.length()));
			} else {
				name += consonants.charAt(rand.nextInt(consonants.length()));
			}
			if (i==0){
				name = name.toUpperCase();
			}
			wroteVowel = !wroteVowel;
		}
		return name;
	}
}