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
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
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

/**
 * The Menu Panel contains buttons to go to each of the game modes, and to quit the game.
 * Also, up-to-date high scores of the last hour and of all time are shown.
 * @author qqwy
 */
public class MenuPanel extends JPanel {
	
	/* 
	 * TODO:
	 * - Give highscores a fixed width (enough for a name of 12 chars)
	 * - Make hightscores align to the top
	 * - Make a function to update the highscores
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Color BORDERCOLOR = Color.GREEN;
	private static Color TEXTCOLOR = Color.GREEN;
	private static Color BACKCOLOR = Color.BLACK;
	private static int BORDERWIDTH = 1;
	
	/**
	 * The player can set their name in this field, before starting the game.
	 */
	private JTextField nameField;

	/**
	 * A map of all buttons created in this menu, the keys being their button text.
	 * Used to add EventListeners to them after they have been created, by referencing them by their button text.
	 */
	private Map<String,JButton> buttons = new HashMap<String,JButton>();

	/**
	 * The player can enter a server location to connect to in this field.
	 */
	private JTextField addressField;

	/**
	 * A reference to the high scores panel of all time, to enable refreshing the contained data.
	 */
	private JPanel allTimeHighScores;
	/**
	 * A reference to the high scores panel of the last hour, to enable refreshing the contained data.
	 */
	private JPanel lastHourHighScores;
	
	
	@SuppressWarnings("serial")
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
	
	/**
	 * Creates a rigid, empty box. Used for positioning the other elements on the panel.
	 * @param size the width and height of the box
	 * @param axis attempts to align the box. Pass in a BoxLayout enumerable.
	 * @see BoxLayout
	 */
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
	
	/**
	 * Creates a large title JLabel and adds it to the given panel.
	 * @param panel the panel to add the title to
	 * @param titleText the text to use as title
	 */
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

	/**
	 * Adds an input field to the specified panel
	 * @param panel the panel to add the input to
	 * @param defaultText the text to use until it changes
	 * @param title a title to use in small in the label.
	 * @return the created input field
	 */
	private JTextField addInput(JPanel panel, String defaultText, String title){
		JTextField inputField = makeInput(defaultText, title, panel.getFont());
		panel.add(inputField);
		
		panel.add(Box.createRigidArea(new Dimension(0, 40)));
		
		return inputField;
	}
	
	/**
	 * Adds an input field to the specified panel
	 * @param panel the panel to add the input to
	 * @param defaultText the text to use until it changes
	 * @param title a title to use in small in the label.
	 * @param f a font to use for the text in this input field
	 * @return the created input field
	 */
	private JTextField makeInput(String defaultText, String title, Font f){
		JTextField inputField = new JTextField();
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
	
	/**
	 * Creates an input field without adding it to any panel.
	 * @param defaultText the text to use until it changes
	 * @param title a title to use in small in the label.
	 * @return the created input field
	 */
	private JTextField makeInput(String defaultText, String title){
		return makeInput(defaultText, title, new Font(null));
	}
	
	
	/**
	 * Creates a button with the given text and adds it to the given panel.<br/><br/>
	 * Note that `text` can also be used to reference the button later on when setting a button action.
	 * @param panel the panel to add the button to
	 * @param text the text the button should contain.
	 * @return the created button
	 * @see #setButtonAction(String, ActionListener)
	 */
	private JButton makeButton(JPanel panel, String text){
		
		JButton button = new JButton(text);
		
		if(this.buttons.containsKey(text)){
			throw new UnsupportedOperationException();
		}
		this.buttons.put(text, button);
		
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
	
	/**
	 * Creates a button with the given text and adds it to the given panel.<br/><br/>
	 * Note that `text` can also be used to reference the button later on when setting a button action.
	 * @param panel the panel to add the button to
	 * @param text the text the button should contain.
	 * @param action an ActionListener to execute when a user interacts with the button.
	 * @return the created button
	 * @see #setButtonAction(String, ActionListener)
	 */
	private JButton makeButton(JPanel panel, String text, ActionListener action){
		JButton button = this.makeButton(panel, text);
		this.setButtonAction(text, action);
		return button;
	}
	
	/**
	 * Creates a button with the given text
	 * @return the created button
	 * @see #setButtonAction(String, ActionListener)
	 */
	private JButton makeButton(String text){
		return this.makeButton(null,text);
	}
	
	/**
	 * Creates a button with the given text, and an ActionListener to execute when a user interacts with the button.
	 * @return the created button
	 * @see #setButtonAction(String, ActionListener)
	 */
	private JButton makeButton(String text, ActionListener action){
		return this.makeButton(null,text,action);
	}
	
	/**
	 * Creates a new panel and adds the given highscores to it.
	 * These are then formatted to be a nice list.
	 * @return the created container
	 */
	private JPanel addHighScores(String scoreListName, List<PlayerScore> scores){
		JPanel container = makeBox(null, BoxLayout.Y_AXIS);
		return addHighScores(scoreListName, scores, container);
	}
	
	/**
	 * Adds the given highscores to an already existing container.
	 * These are then formatted to be a nice list.
	 * @return the edited container
	 */

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
	
	/**
	 * Reloads the high-scores and displays the new versions on this panel.
	 */
	public void reloadHighScores(){
		this.allTimeHighScores.removeAll();
		this.lastHourHighScores.removeAll();
		addHighScores("All Time High Scores",HighScores.getInstance().getHighScores(), this.allTimeHighScores);
		addHighScores("Last Hour High Scores",HighScores.getInstance().getLastHourHighScores(), this.lastHourHighScores);
	}
	
	/**
	 * Adds the given ActionListener to a button with the given button_name, that should have been created using makeButton() before.
	 * @see #makeButton(String)
	 * @see #makeButton(JPanel, String)
	 * @see #makeButton(String, ActionListener)
	 * @see #makeButton(JPanel, String, ActionListener)
	 */

	public void setButtonAction(String button_name, ActionListener action){
		JButton button = buttons.get(button_name);
		if(button == null){
			return;
		}
		button.addActionListener(action);
	}
	
	/**
	 * Creates a JPanel that might contain multiple child components
	 * @param size width and height of the panel
	 * @param axis the alignment BoxLayout enumerable.
	 * @param child_components a list of zero or more child components.
	 * @see BoxLayout
	 * @return the created composite panel
	 */
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
	
	/**
	 * @see #nameField
	 * @return the entered value in the nameField JTextField
	 */
	public String getPlayerName(){
		return this.nameField.getText();
	}
	
	/**
	 * @see #addressField
	 * @return the entered value in the addressField JTextField
	 */
	public String getAddress(){
		return this.addressField.getText();
	}
	
}