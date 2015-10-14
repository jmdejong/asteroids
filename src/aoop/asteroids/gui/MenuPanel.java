package aoop.asteroids.gui;

import aoop.asteroids.HighScores;
import aoop.asteroids.PlayerScore;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.TitledBorder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;


public class MenuPanel extends JPanel {
	
	// why are these capitalized? they're not classes. I would use either all caps or start with a lowercase letter
	private static Color ButtonBorderColor = Color.GREEN;
	private static Color TextColor = Color.GREEN;
	private static Color BackColor = Color.BLACK;
	private static int ButtonBorderWidth = 1;
// 	private static Color TRASPARENT = new Color(0,0,0,0);
// 	private JPanel middle;
// 	private JPanel left;
// 	private JPanel right;
	
	private JTextField nameField;
	
	
	
	private Map<String,JButton> buttons = new HashMap<String,JButton>();
	
// 	private JPanel mainPanel;
// 	private JPanel addressPanel;
	
// 	private JButton connectButton;

	private JTextField addressField;
	
	public MenuPanel() {
		
		// Set color of menu panel background
		setBackground(BackColor);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		
		
		addTitle(this, "ASTEROIDS");
		
		nameField = addInput(this, generateName(), "Name:");
		
		this.add(Box.createRigidArea(new Dimension(0,50)));
		
		
		
		final JPanel switchable = new JPanel();
		switchable.setBackground(BackColor);
		final CardLayout switchableLayout = new CardLayout();
		switchable.setLayout(switchableLayout);
		
		switchable.add(makeCompositePanel(new Dimension(200, 300), BoxLayout.Y_AXIS,
				makeButton("Singleplayer"),
				makeButton("Host Multiplayer"),
				makeButton("Join Multiplayer", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
					switchableLayout.show(switchable, "address panel");
				}})
			), "main panel");
		
		switchable.add(makeCompositePanel(null, BoxLayout.Y_AXIS,
			addressField = makeInput("localhost", "address:" ),
			makeButton("Join"),
			makeButton("Spectate"),
			makeButton("Back to menu", new AbstractAction(){ public void actionPerformed(ActionEvent arg0){
				switchableLayout.show(switchable, "main panel");
			}})
		),"address panel");
		switchableLayout.show(switchable, "main panel");
		
		this.add(
			makeCompositePanel(null, BoxLayout.X_AXIS,
				addHighScores("All Time High Scores",HighScores.getInstance().getHighScores()),
				makeCompositePanel(null, BoxLayout.Y_AXIS,
					switchable,
					makeCompositePanel(new Dimension(200, 300), BoxLayout.Y_AXIS, 
							makeButton("Quit")
					)
				),
				addHighScores("Last Hour High Scores",HighScores.getInstance().getLastHourHighScores())
			)
		);
		
		
	}
	
	
	private JPanel makeBox(Dimension size, int axis){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, axis));
		if (size != null){
			panel.setPreferredSize(size);
		}
		
		//panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, this.ButtonBorderWidth));
		panel.setBackground(BackColor);
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		return panel;
	}
	
	private void addTitle(JPanel panel, String titleText){
		
		this.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JLabel title = new JLabel(titleText);
		title.setAlignmentX( Component.CENTER_ALIGNMENT );
		title.setMaximumSize(new Dimension(800,50));
		title.setHorizontalAlignment( JLabel.CENTER );
		title.setFont(new Font("sansserif", Font.PLAIN, 40));
		title.setForeground(this.TextColor);
		panel.add(title);
		
		panel.add(Box.createRigidArea(new Dimension(0, 30)));
	}
	
	private JLabel addTitle(String titleText){
		JLabel title = new JLabel(titleText);
		title.setAlignmentX( Component.CENTER_ALIGNMENT );
		title.setMaximumSize(new Dimension(800,50));
		title.setHorizontalAlignment( JLabel.CENTER );
		title.setFont(new Font("sansserif", Font.PLAIN, 40));
		title.setForeground(this.TextColor);
		return title;
	}
	
	private JTextField addInput(JPanel panel, String defaultText, String title){
		JTextField inputField = makeInput(defaultText, title, panel.getFont());
		panel.add(inputField);
		
		panel.add(Box.createRigidArea(new Dimension(0, 40)));
		
		return inputField;
	}
	
	
	private JTextField makeInput(String defaultText, String title, Font f){
		JTextField inputField = new JTextField();
// 		inputField.setAlignmentY( Component.TOP_ALIGNMENT );
		inputField.setMaximumSize( new Dimension(120,80) );
		inputField.setBackground(this.BackColor);
		inputField.setForeground(this.TextColor);
		inputField.setBorder(BorderFactory.createCompoundBorder(
			new TitledBorder(new EmptyBorder(10,10,10,10), title, TitledBorder.CENTER, TitledBorder.TOP, f, TextColor),
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(this.ButtonBorderColor, MenuPanel.ButtonBorderWidth),
				new EmptyBorder(5, 5, 5, 5)
			)
		));
		inputField.setText( defaultText);
		inputField.setCaretColor(this.TextColor);
		inputField.getCaret().setVisible(true);
		
		return inputField;
	}
	
	private JTextField makeInput(String defaultText, String title){
		return makeInput(defaultText, title, new Font(null));
	}
	
	
	
	private JButton makeButton(JPanel panel, String text){
		
		JButton button = new JButton(text);
		
		if(this.buttons.containsKey(text)){
			throw new UnsupportedOperationException();
		}
		this.buttons.put(text, button);
		
		
// 		button.setFont(Font.getFont("Courier"));
		button.setAlignmentX( Component.CENTER_ALIGNMENT );
		button.setMaximumSize( new Dimension(180,50) );
		button.setBackground(this.BackColor);
		button.setForeground(this.TextColor);
		button.setBorder(BorderFactory.createLineBorder(this.ButtonBorderColor, this.ButtonBorderWidth));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		if(panel != null){
			panel.add(button);
			panel.add(Box.createRigidArea(new Dimension(0, 20)));
		}
		
		
		return button;

	}
	private JButton makeButton(JPanel panel, String text, ActionListener action){
		JButton button = this.makeButton(panel, text);
		this.setButtonAction(text, action);
		return button;
	}
	private JButton makeButton(String text){
		return this.makeButton(null,text);
	}
	
	private JButton makeButton(String text, ActionListener action){
		return this.makeButton(null,text,action);
	}
	
	
	private void addHighScores(JPanel panel, List<PlayerScore> scores){
		
		
		Font scoreFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		for (PlayerScore score : scores){
			JLabel scoreLabel = new JLabel();
			scoreLabel.setHorizontalAlignment( JLabel.RIGHT );
			scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			scoreLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			scoreLabel.setFont(scoreFont);
			scoreLabel.setForeground(Color.WHITE);
			scoreLabel.setText(score.toString());
			panel.add(scoreLabel);
		}
		//return container;
		
		//panel.add(addHighScores(scores));
	}
	
	private JPanel addHighScores(String scoreListName, List<PlayerScore> scores){
		JPanel container = makeBox(null, BoxLayout.Y_AXIS);
		Font scoreFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		for (int i=0;i<10;i++){
			
			JLabel scoreLabel = new JLabel();
			if(i==0){
				scoreLabel.setText(scoreListName+":");
			}else if(i > scores.size()){
				scoreLabel.setText("         -     ");
			}else{
				scoreLabel.setText(scores.get(i-1).toString());
			}
			scoreLabel.setHorizontalAlignment( JLabel.RIGHT );
			scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			scoreLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			scoreLabel.setFont(scoreFont);
			scoreLabel.setForeground(Color.WHITE);
			
			
			container.add(scoreLabel);
		}
		return container;
	}
	
	public void setButtonAction(String button_name, ActionListener action){
		JButton button = buttons.get(button_name);
		if(button == null){
			return;
		}
		button.addActionListener(action);
	}
	
	public JPanel makeCompositePanel(Dimension size, int axis, JComponent... child_components){
		JPanel panel = makeBox(size, axis);
		for(JComponent c : child_components){
			panel.add(c);
			if(c instanceof JButton){
				panel.add(Box.createRigidArea(new Dimension(0, 20)));
			}else if (c instanceof JLabel){
				panel.add(Box.createRigidArea(new Dimension(0, 30)));
			}
		}
		return panel;
	}
	
	
	public String getPlayerName(){
		return this.nameField.getText();
	}
	
	public String getAddress(){
		return this.addressField.getText();
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
		String consonants = "bbcddffgghjjkkllmmmnnnppqrrrsssttttvvwxz";
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