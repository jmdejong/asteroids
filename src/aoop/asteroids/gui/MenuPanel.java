package aoop.asteroids.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
	
	private AsteroidsFrame frame;
	
	public MenuPanel(AsteroidsFrame frame) {
		
		
		this.frame = frame;
		
		// Set color of menu panel background
		setBackground(Color.BLACK);
		
		// Make a box layout of vertical buttons, rigid area dimensions determine it's location on the y-axis
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createRigidArea(new Dimension(0, 200)));
		
		
		JButton singleplayer = new JButton("Single-player");
		singleplayer.setAlignmentX( Component.CENTER_ALIGNMENT );
		singleplayer.setMaximumSize( new Dimension(300,100) );
		singleplayer.setBackground(Color.GRAY);
		singleplayer.setForeground(Color.WHITE);
		singleplayer.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
		this.add(singleplayer);
		
		
		singleplayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
// 				MenuPanel.this.frame.remove(MenuPanel.this);
				MenuPanel.this.frame.showGame();
				
				System.out.println("now the game should be shown");
			}
		});
		
		
	}
}