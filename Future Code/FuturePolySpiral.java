package application;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class FuturePolySpiral extends JPanel {
    double inc = 0;

    public FuturePolySpiral() {
	setPreferredSize(new Dimension(640, 640));
	setBackground(Color.white);

	new Timer(1, (ActionEvent e) -> {
	    inc = (inc + 0.05) % 360;
	    repaint();
	}).start();
    }

    void drawSpiral(Graphics2D g, int len, double angleIncrement) {

	double x1 = getWidth() / 2.00;
	double y1 = getHeight() / 2.00;
	double angle = angleIncrement;

	for (int i = 0; i < 150; i++) {

	    g.setColor(Color.getHSBColor(i / 150f, 1.0f, 1.0f));

	    double x2 = x1 + Math.cos(angle) * len;
	    double y2 = y1 - Math.sin(angle) * len;
	    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	    x1 = x2;
	    y1 = y2;

	    len += 3;

	    angle = (angle + angleIncrement) % (Math.PI * 2);
	}
    }

    @Override
    public void paintComponent(Graphics gg) {
	super.paintComponent(gg);
	Graphics2D g = (Graphics2D) gg;
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	drawSpiral(g, 6, Math.toRadians(inc));
    }

    public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> {
	    JFrame f = new JFrame();
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.setTitle("PolySpiral");
	    f.setResizable(true);
	    f.add(new FuturePolySpiral(), BorderLayout.CENTER);
	    f.pack();
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);
	});
    }
}