/*
 * 
 */
package aaeffects_to_be_used_in_future;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class GPolySpiral.
 */
@SuppressWarnings("serial")
class GPolySpiral extends Canvas {
	
	/** The radius. */
	float pixelSize, radius = 0.0F;
	
	/** The r height. */
	final float rWidth = 50.0F, rHeight = 50.0F;
	
	/** The center Y. */
	int centerX, centerY;

	/** The twopi. */
	final float TWOPI = 6.283185308F;
	
	/** The cd. */
	float CD;
	
	/** The iters. */
	int iters = 500;
	
	/** The cur Y. */
	float dist, angle, incr, curX, curY;

	/** The black. */
	Color black = new Color(0, 0, 0);

	/**
	 * The main method.
	 *
	 * @param srgs the arguments
	 */
	public static void main(String[] srgs) {
		Frame myf = new Frame("Test");
		myf.setSize(400, 300);
		Canvas canvas = new GPolySpiral();
		myf.add(canvas);
		myf.setVisible(true);
	}

	/**
	 * Instantiates a new g poly spiral.
	 */
	GPolySpiral() {
		initgr();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				repaint();
			}
		});
	}

	/**
	 * Initgr.
	 */
	void initgr() {
		Dimension d = getSize();
		int maxX = d.width - 1, maxY = d.height - 1;
		pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
		centerX = maxX / 2;
		centerY = maxY / 2;
	}

	/**
	 * I X.
	 *
	 * @param x the x
	 * @return the int
	 */
	int iX(float x) {
		return Math.round(centerX + x / pixelSize);
	}

	/**
	 * I Y.
	 *
	 * @param y the y
	 * @return the int
	 */
	int iY(float y) {
		return Math.round(centerY - y / pixelSize);
	}

	/**
	 * Fx.
	 *
	 * @param X the x
	 * @return the float
	 */
	float fx(int X) {
		return (X - centerX) * pixelSize;
	}

	/**
	 * Fy.
	 *
	 * @param Y the y
	 * @return the float
	 */
	float fy(int Y) {
		return (centerY - Y) * pixelSize;
	}

	/**
	 * Line forward.
	 *
	 * @param g the g
	 * @param dist the dist
	 */
	public void lineForward(Graphics g, float dist) {
		float angle = TWOPI * CD / 360.0F;
		float newX = curX + dist * (float) Math.cos(angle);
		float newY = curY + dist * (float) Math.sin(angle);
		// g.setColor(new Color(rand(), rand(), rand()));
		g.drawLine(iX(curX), iY(curY), iX(newX), iY(newY));
		curX = newX;
		curY = newY;
	}

	/**
	 * Right.
	 *
	 * @param angle the angle
	 */
	public void right(float angle) {
		CD -= angle;
	}

	/**
	 * Rand.
	 *
	 * @return the int
	 */
	int rand() {
		return (int) (Math.random() * 256);
	}

	/* (non-Javadoc)
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		setBackground(black);
		initgr();
		// int left = iX(-rWidth/2), right = iX(rWidth/2), bottom =
		// iY(-rHeight/2), top = iY(rHeight/2);
		// Draw clipping rectangle
		// g.drawRect(left, top, right-left, bottom-top);
		do {
			angle = (float) Math.random() * 360;
			dist = (float) Math.random();
			incr = (float) Math.random();
		} while (angle == 0 || dist == 0.0F || incr == 0.0F);

		dist = (float) 0.2;
		incr = (float) 0.1;
		// angle=190;
		// System.out.println(dist);
		System.out.println(angle + " " + dist + " " + incr);

		// curX = 0.0F; curY = 0.0F; CD = 0.0F;

		curX = 0.0F;
		curY = 0.0F;
		CD = 0.0F;

		for (int i = 0; i < iters; i++) {
			g.setColor(new Color(rand(), rand(), rand()));
			lineForward(g, dist);
			right(angle);
			dist = dist + incr;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(700, 500);
	}
}
