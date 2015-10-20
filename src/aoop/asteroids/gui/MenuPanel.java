package aoop.asteroids.gui;

import aoop.asteroids.HighScores;
import aoop.asteroids.PlayerScore;
import aoop.asteroids.Utils;

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
import java.util.List;


public class MenuPanel extends JPanel {
	
	/* 
	 * TODO:
	 * - Give highscores a fixed width (enough for a name of 12 chars)
	 * - Make hightscores align to the top
	 * - Make a function to update the highscores
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// why are these capitalized? they're not classes. I would use either all caps or start with a lowercase letter
	private static Color BORDERCOLOR = Color.GREEN;
	private static Color TEXTCOLOR = Color.GREEN;
	private static Color BACKCOLOR = Color.BLACK;
	private static int BORDERWIDTH = 1;
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

	private JPanel allTimeHighScores;
	private JPanel lastHourHighScores;
	
	
	public MenuPanel() {
		
		// Set color of menu panel background
		setBackground(MenuPanel.BACKCOLOR);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		addTitle(this, "ASTEROIDS");
		
		nameField = addInput(this, Utils.generateName(), "Name:");
		

		this.add(Box.createRigidArea(new Dimension(0,50)));
		
		
		
		final JPanel switchable = new JPanel();
		switchable.setBackground(BACKCOLOR);
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
				allTimeHighScores = addHighScores("All Time High Scores",HighScores.getInstance().getHighScores()),
				makeCompositePanel(null, BoxLayout.Y_AXIS,
					switchable,
					makeCompositePanel(new Dimension(200, 300), BoxLayout.Y_AXIS, 
							makeButton("Quit")
					)
				),
				lastHourHighScores = addHighScores("Last Hour High Scores",HighScores.getInstance().getLastHourHighScores())
			)
		);
		
		
	}
	
	
	private JPanel makeBox(Dimension size, int axis){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, axis));
		if (size != null){
			panel.setPreferredSize(size);
		}
		
		panel.setBackground(BACKCOLOR);
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
		title.setForeground(MenuPanel.TEXTCOLOR);
		panel.add(title);
		
		panel.add(Box.createRigidArea(new Dimension(0, 30)));
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
		inputField.setBackground(MenuPanel.BACKCOLOR);
		inputField.setForeground(MenuPanel.TEXTCOLOR);
		inputField.setBorder(BorderFactory.createCompoundBorder(
			new TitledBorder(new EmptyBorder(10,10,10,10), title, TitledBorder.CENTER, TitledBorder.TOP, f, MenuPanel.TEXTCOLOR),
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(MenuPanel.BORDERCOLOR, MenuPanel.BORDERWIDTH),
				new EmptyBorder(5, 5, 5, 5)
			)
		));
		inputField.setText( defaultText);
		inputField.setCaretColor(MenuPanel.TEXTCOLOR);
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
		button.setBackground(MenuPanel.BACKCOLOR);
		button.setForeground(MenuPanel.TEXTCOLOR);
		button.setBorder(BorderFactory.createLineBorder(MenuPanel.BORDERCOLOR, MenuPanel.BORDERWIDTH));
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
	
	
	private JPanel addHighScores(String scoreListName, List<PlayerScore> scores){
		JPanel container = makeBox(null, BoxLayout.Y_AXIS);
		return addHighScores(scoreListName, scores, container);
	}
	private JPanel addHighScores(String scoreListName, List<PlayerScore> scores, JPanel container){
		
		container.setBorder(new TitledBorder(BorderFactory.createLineBorder(MenuPanel.BORDERCOLOR, MenuPanel.BORDERWIDTH), scoreListName, TitledBorder.CENTER, TitledBorder.TOP, container.getFont(), MenuPanel.TEXTCOLOR));
		container.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		
		Font scoreFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		for (int i=0;i<10;i++){
			
			JLabel scoreLabel = new JLabel();
			if(i >= scores.size()){
				scoreLabel.setText("         -     ");
			}else{
				scoreLabel.setText(scores.get(i).toString());
			}
			scoreLabel.setHorizontalAlignment( JLabel.RIGHT );
			scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			scoreLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			scoreLabel.setFont(scoreFont);
			scoreLabel.setForeground(MenuPanel.TEXTCOLOR);
			
			
			container.add(scoreLabel);
		}
		return container;
	}
	
	public void reloadHighScores(){
		this.allTimeHighScores.removeAll();
		this.lastHourHighScores.removeAll();
		addHighScores("All Time High Scores",HighScores.getInstance().getHighScores(), this.allTimeHighScores);
		addHighScores("Last Hour High Scores",HighScores.getInstance().getLastHourHighScores(), this.lastHourHighScores);
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
	
}