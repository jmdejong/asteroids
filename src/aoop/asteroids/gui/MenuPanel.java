package aoop.asteroids.gui;

import aoop.asteroids.HighScores;
import aoop.asteroids.PlayerScore;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.List;


public class MenuPanel extends JPanel {
	
	private static Color ButtonBorderColor = Color.GREEN;
	private static Color TextColor = Color.GREEN;
	private static Color ButtonBackColor = Color.BLACK;
	private static int ButtonBorderWidth = 1;
	private static Color TRASPARENT = new Color(0,0,0,0);
	private JPanel middle;
	private JPanel left;
	private JPanel right;
	
	private JTextField nameField;
	
	private JButton playButton;
	private JButton hostButton;
	private JButton joinButton;
	private JButton spectateButton;
	private JButton quitButton;
	
	public MenuPanel(String titleText) {
		
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));
		body.setBackground(TRASPARENT);
		
		middle = new JPanel();
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		middle.setBackground(TRASPARENT);
		
		addCentralPart();
		
		
		left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setBackground(TRASPARENT);
		left.setMaximumSize(new Dimension(300,500));
// 		left.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setMaximumSize(new Dimension(300,500));
		right.setBackground(TRASPARENT);
		
		addHighScores(left, HighScores.getInstance().getHighScores());
		
		
		
		addTitle(titleText);
		body.add(left);
		body.add(Box.createRigidArea(new Dimension(100,0)));
		body.add(middle);
		body.add(Box.createRigidArea(new Dimension(100,0)));
		body.add(right);
		this.add(body);
	}
	
	private void addTitle(String titleText){
		
		this.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JLabel title = new JLabel(titleText);
		title.setAlignmentX( Component.CENTER_ALIGNMENT );
		title.setHorizontalAlignment( JLabel.CENTER );
		title.setFont(new Font("sansserif", Font.PLAIN, 40));
		title.setForeground(AsteroidsFrame.TextColor);
		this.add(title);
		
		this.add(Box.createRigidArea(new Dimension(0, 30)));
	}
	
	private void addCentralPart(){
		
		nameField = new JTextField();
		nameField.setAlignmentY( Component.TOP_ALIGNMENT );
		nameField.setMaximumSize( new Dimension(120,40) );
		nameField.setBackground(AsteroidsFrame.ButtonBackColor);
		nameField.setForeground(AsteroidsFrame.TextColor);
		nameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, MenuPanel.ButtonBorderWidth), new EmptyBorder(5, 5, 5, 5)));
		nameField.setText( generateName());
		nameField.setCaretColor(AsteroidsFrame.TextColor);
		nameField.getCaret().setVisible(true);
		middle.add(nameField);
		
		middle.add(Box.createRigidArea(new Dimension(0, 50)));
		
		
		playButton = makeButton("Singleplayer");
		hostButton = makeButton("Multiplayer");
		joinButton = makeButton("Join Multiplayer");
		spectateButton = makeButton("Spectate");
		
		middle.add(Box.createRigidArea(new Dimension(0, 40)));
		quitButton = makeButton("Quit");
		
	}
	
	
	private JButton makeButton(String text){
		
		JButton button = new JButton(text);
// 		button.setFont(Font.getFont("Courier"));
		button.setAlignmentX( Component.CENTER_ALIGNMENT );
		button.setMaximumSize( new Dimension(200,50) );
		button.setBackground(AsteroidsFrame.ButtonBackColor);
		button.setForeground(AsteroidsFrame.TextColor);
		button.setBorder(BorderFactory.createLineBorder(AsteroidsFrame.ButtonBorderColor, AsteroidsFrame.ButtonBorderWidth));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		middle.add(button);
		middle.add(Box.createRigidArea(new Dimension(0, 20)));
		
		return button;

	}
	
	
	private void addHighScores(JPanel panel, List<PlayerScore> scores){
		Font scoreFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		for (PlayerScore score : scores){
			JLabel scoreLabel = new JLabel();
			scoreLabel.setHorizontalAlignment( JLabel.RIGHT );
			scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			scoreLabel.setFont(scoreFont);
			scoreLabel.setForeground(Color.WHITE);
			scoreLabel.setText(score.toString());
			panel.add(scoreLabel);
		}
	}
	
	
	public void setPlayAction(ActionListener action){
		playButton.addActionListener(action);
	}
	
	public void setHostAction(ActionListener action){
		hostButton.addActionListener(action);
	}
	
	public void setJoinAction(ActionListener action){
		joinButton.addActionListener(action);
	}
	
	public void setSpectateAction(ActionListener action){
		spectateButton.addActionListener(action);
	}
	
	public void setQuitAction(ActionListener action){
		quitButton.addActionListener(action);
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