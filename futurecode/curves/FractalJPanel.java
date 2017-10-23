package curves;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class FractalJPanel extends JPanel {
	
	Color color;
	int level;
	
	int WIDTH = 400;
	int HEIGHT = 400;
	
	public FractalJPanel(int currentLevel) {
		
		color = Color.BLUE;
		level = currentLevel;
		setBackground(Color.white);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
	}
	
	public void drawFractal(int level , int xA , int yA , int xB , int yB , Graphics g) {
		
		if (level == 0)
			g.drawLine(xA, yA, xB, yB);
		else {
			
			int xC = ( xA + xB ) / 2;
			int yC = ( yA + yB ) / 2;
			
			int xD = xA + ( xC - xA ) / 2 - ( yC - yA ) / 2;
			int yD = yA + ( yC - yA ) / 2 + ( xC - xA ) / 2;
			
			drawFractal(level - 1, xD, yD, xA, yA, g);
			drawFractal(level - 1, xD, yD, xC, yC, g);
			drawFractal(level - 1, xD, yD, xB, yB, g);
		}
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(color);
		drawFractal(level, 50, 45, 350, 260, g);
	}
	
	public void setColor(Color c) {
		
		color = c;
	}
	
	public void setLevel(int currentLevel) {
		
		level = currentLevel;
	}
	
	public int getLevel() {
		return level;
	}
	
}
