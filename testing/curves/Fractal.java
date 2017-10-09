package curves;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Fractal extends JFrame {
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 480;
	private static final int MIN_LEVEL = 0;
	private static final int MAX_LEVEL = 25;
	
	public Fractal() {
		
		super("Fractal");
		
		JLabel levelJLabel = new JLabel("Level: 0");
		FractalJPanel drawSpace = new FractalJPanel(0);
		
		JPanel controlJPanel = new JPanel();
		controlJPanel.setLayout(new FlowLayout());
		
		JButton changeColorJButton = new JButton("Color");
		controlJPanel.add(changeColorJButton);
		changeColorJButton.addActionListener(a -> {
			Color color = JColorChooser.showDialog(Fractal.this, "Choose a color", Color.BLUE);
			
			if (color == null)
				color = Color.BLUE;
			
			drawSpace.setColor(color);
			
		});
		
		JButton decreaseLevelButton = new JButton("Decrease Level");
		controlJPanel.add(decreaseLevelButton);
		decreaseLevelButton.addActionListener(a -> {
			int level = drawSpace.getLevel();
			--level;
			
			if ( ( level >= MIN_LEVEL ) && ( level <= MAX_LEVEL )) {
				levelJLabel.setText("Level: " + level);
				drawSpace.setLevel(level);
				repaint();
			}
			
		});
		
		JButton increaseLevelJButton = new JButton("Increase Level");
		controlJPanel.add(increaseLevelJButton);
		increaseLevelJButton.addActionListener(a -> {
			int level = drawSpace.getLevel();
			++level;
			
			if ( ( level >= MIN_LEVEL ) && ( level <= MAX_LEVEL )) {
				levelJLabel.setText("Level: " + level);
				drawSpace.setLevel(level);
				repaint();
			}
			
		});
		
		controlJPanel.add(levelJLabel);
		
		JPanel mainJPanel = new JPanel();
		mainJPanel.add(controlJPanel);
		mainJPanel.add(drawSpace);
		
		add(mainJPanel);
		
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		
	}
	
	public static void main(String[] args) {
		
		Fractal demo = new Fractal();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
