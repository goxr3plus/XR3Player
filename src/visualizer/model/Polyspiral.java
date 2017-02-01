/**
 * 
 */
package visualizer.model;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 * 
 * 
 * Rosette and Polyspiral
 * 
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 * 
 * @author GOXR3PLUS
 *
 */
public class Polyspiral {

    Random random = new Random();

    /** The Constant TWO_PI. */
    static final float TWO_PI = (float) (Math.PI * 2);

    // --------------- PolySpiral ---------------------
    /** The pixel size. */
    float pixelSize;

    /** The r width. */
    float rWidth = 50.0F;

    /** The r height. */
    float rHeight = 50.0F;

    /** The center X. */
    int centerX;

    /** The center Y. */
    int centerY;

    // -------------- Rosette --------------------------

    /** The num of vertices. */
    int numOfVertices = 0;

    /** The radius. */
    float radius;

    /** The rosette angle. */
    float rosetteAngle;

    /** The delang. */
    float delang;

    /** The vertices. */
    Point2D[] vertices = null;

    /** The default angle. */
    float defaultAngle = 0.0F;

    /** The cd. */
    float cd;

    /** The iterator. */
    int iterator = 500;

    /** The distance. */
    float distance;

    /** The poly spiral angle. */
    float polySpiralAngle;

    /** The increment. */
    float increment;

    /** The cur X. */
    float curX;

    /** The cur Y. */
    float curY;

    // --------------------------------------------------------

    /** VisualizerDrawer instance. */
    private VisualizerDrawer visualizerDrawer;

    /**
     * Constructor
     * 
     * @param visualizerDrawer
     */
    public Polyspiral(VisualizerDrawer visualizerDrawer) {
	this.visualizerDrawer = visualizerDrawer;
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 				Rosette and Polyspiral
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Draws a Rosette and a Polyspiral.
     */
    public void drawPolySpiral() {

	// Calculations
	float[] pSample = visualizerDrawer.stereoMerge(visualizerDrawer.pLeftChannel, visualizerDrawer.pRightChannel);
	float[] array = visualizerDrawer.returnBandsArray(pSample, 3);

	int length;
	int total;

	if (array[0] < 0.25) {
	    length = 2;
	    total = 65;
	} else if (array[0] < .5) {
	    length = 3;
	    total = 200;
	} else if (array[0] < .65) {
	    length = 4;
	    total = 300;
	} else if (array[0] < 0.75) {
	    length = 5;
	    total = 500;
	} else {
	    length = 6;
	    total = 1200;
	}

	// Background image
	if (visualizerDrawer.backgroundImage != null)
	    visualizerDrawer.gc.drawImage(visualizerDrawer.backgroundImage, 0, 0, visualizerDrawer.canvasWidth,
		    visualizerDrawer.canvasHeight);

	// Set the background fill
	visualizerDrawer.gc.setFill(Color.rgb(0, 0, 0, array[0]));
	visualizerDrawer.gc.fillRect(0, 0, visualizerDrawer.canvasWidth, visualizerDrawer.canvasHeight);

	// Draw Random Ovals
	/*
	 * Exception in thread "JavaFX Application Thread"
	 * java.lang.IllegalArgumentException: bound must be positive at
	 * java.util.Random.nextInt(Random.java:388) at
	 * visualizer.model.VisualizerDrawer.drawPolySpiral(VisualizerDrawer.
	 * java:261) error(gc.fillOval(random.nextInt(width),
	 * random.nextInt(height), length + 2.00, length + 2.00);)
	 */
	visualizerDrawer.gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
	for (int i = 0; i < total; i++) {
	    visualizerDrawer.gc.fillOval(random.nextInt(visualizerDrawer.canvasWidth),
		    random.nextInt(visualizerDrawer.canvasHeight), length + 2.00, length + 2.00);
	}

	// Draw Lights
	if (visualizerDrawer.canvasWidth > VisualizerDrawer.greyLight.getWidth()
		&& visualizerDrawer.canvasHeight > VisualizerDrawer.greyLight.getHeight())
	    if (array[0] < 0.2) {
		drawLight(VisualizerDrawer.greyLight);
	    } else if (array[0] < 0.3) {
		drawLight(VisualizerDrawer.lightBlueLight);
	    } else if (array[0] < 0.4) {
		drawLight(VisualizerDrawer.blueLight);
	    } else if (array[0] < 0.6) {
		drawLight(VisualizerDrawer.yellowLight);
	    } else if (array[0] < 0.9) {
		drawLight(VisualizerDrawer.redLight);

	    }

	// Scope
	if (visualizerDrawer.canvasWidth > VisualizerDrawer.greyLight.getWidth()
		&& visualizerDrawer.canvasHeight > VisualizerDrawer.greyLight.getHeight()) {
	    visualizerDrawer.gc.setStroke(visualizerDrawer.scopeColor);
	    double coolW = VisualizerDrawer.coolEffect.getWidth();
	    double coolH = VisualizerDrawer.coolEffect.getHeight() < visualizerDrawer.canvasHeight
		    ? VisualizerDrawer.coolEffect.getHeight()
		    : visualizerDrawer.canvasHeight;

	    // ------------------------Draw Scope----------------------------

	    // Scope 1
	    int zb = (int) (50 + 100 * array[0]);
	    int yLast = (int) (pSample[0] * (float) zb) + zb;
	    int angleIncrement = 1;
	    for (int a = angleIncrement, c = (int) (coolW / 2 - 50); c < (visualizerDrawer.canvasWidth
		    - coolW / 2); a += angleIncrement, c++) {
		int yNow = (int) (pSample[a] * (float) zb) + zb;
		visualizerDrawer.gc.strokeLine(c, yLast, c + 1.00, yNow);
		yLast = yNow;
	    }

	    visualizerDrawer.gc
		    .setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1.0));

	    // Scope 2
	    int zb2 = (int) (visualizerDrawer.canvasHeight - 50 - 100 * array[0]);
	    int yLast2 = (int) (pSample[0] * (float) zb2) + zb2;
	    int angleIncrement2 = 1;
	    for (int a = angleIncrement2, c = (int) (coolW / 2 - 50); c < (visualizerDrawer.canvasWidth
		    - coolW / 2); a += angleIncrement2, c++) {
		int yNow2 = (int) (pSample[a] * (float) zb2) + zb2;
		visualizerDrawer.gc.strokeLine(c, yLast2, c + 1.00, yNow2);
		yLast2 = yNow2;
	    }

	    // Cool Effects
	    visualizerDrawer.gc.drawImage(VisualizerDrawer.coolEffect, (coolW / 2 - 50) - coolW * array[0] / 2,
		    (visualizerDrawer.canvasHeight / 2 - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
		    coolH * array[0]);
	    visualizerDrawer.gc.drawImage(VisualizerDrawer.coolEffect,
		    (visualizerDrawer.canvasWidth - coolW / 2) - coolW * array[0] / 2,
		    (visualizerDrawer.canvasHeight / 2 - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
		    coolH * array[0]);

	    visualizerDrawer.gc.drawImage(VisualizerDrawer.coolEffect, (coolW / 2 - 50) - coolW * array[0] / 2,
		    (visualizerDrawer.canvasHeight - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
		    coolH * array[0]);
	    visualizerDrawer.gc.drawImage(VisualizerDrawer.coolEffect,
		    (visualizerDrawer.canvasWidth - coolW / 2) - coolW * array[0] / 2,
		    (visualizerDrawer.canvasHeight - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
		    coolH * array[0]);
	}

	// ------------------------Draw Rosette----------------------------
	calculate();

	numOfVertices = (int) (array[0] * 100);
	vertices = new Point2D[numOfVertices];
	delang = TWO_PI / numOfVertices;

	// Calculate the angle of vertices
	for (int i = 0; i < numOfVertices; i++) {
	    rosetteAngle = i * delang + defaultAngle;
	    vertices[i] = new Point2D((float) (radius * Math.cos(rosetteAngle)),
		    (float) (radius * Math.sin(rosetteAngle)));
	}

	// Draw the vertices
	double opacity = array[0] < 0.28 ? 1.0 : 0;
	for (int i = 0; i < numOfVertices; i++)
	    for (int j = numOfVertices - 1; j >= 0; j--)
		if (j > i) {
		    visualizerDrawer.gc.setStroke(
			    Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), opacity));
		    visualizerDrawer.gc.strokeLine(deviceX(vertices[i].getX()), deviceY(vertices[i].getY()),
			    deviceX(vertices[j].getX()), deviceY(vertices[j].getY()));
		} else
		    break;

	// --------------------Draw PolySpiral ------------

	// if array[0]==1 it has an ugly effect of drawing one horizontal line
	// so i don't need this
	if (array[0] < 0.99) {

	    do {
		polySpiralAngle = (float) Math.random() * 360;
		distance = (float) Math.random();
		increment = (float) Math.random();
	    } while (polySpiralAngle == 0 || distance == 0.0F || increment == 0.0F);

	    distance = (float) Math.random() * array[0];// (float) 0.2// //
	    // array[0]*array[1]*100
	    increment = (float) 0.07; // (float) Math.random() * array[1] * 100
				      // + (float) 0.07
	    polySpiralAngle = array[1] < 0.35 ? array[0] * 360 : array[0] * 360 * (float) Math.random() * 100;

	    curX = 0.0F;
	    curY = 0.0F;
	    cd = 0.0F;

	    visualizerDrawer.gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255),
		    array[0] < 0.28 ? 0.0 : 1.0));
	    for (int i = 0; i < iterator; i++) {

		// draw Line
		float lineAngle = TWO_PI * cd / 360.0f;
		float newX = curX + distance * (float) Math.cos(lineAngle);
		float newY = curY + distance * (float) Math.sin(lineAngle);
		visualizerDrawer.gc.strokeLine(deviceX(curX), deviceY(curY), deviceX(newX), deviceY(newY));
		curX = newX;
		curY = newY;

		cd -= polySpiralAngle;
		distance = distance + increment;
	    }
	}

	// Draw the foreground images
	double imageW = visualizerDrawer.foregroundImage.getWidth();
	double imageH = visualizerDrawer.foregroundImage.getHeight();
	if (visualizerDrawer.canvasWidth < visualizerDrawer.canvasHeight)
	    imageW = imageH = visualizerDrawer.canvasWidth / 1.5;
	else if (visualizerDrawer.canvasHeight < visualizerDrawer.canvasWidth)
	    imageW = imageH = visualizerDrawer.canvasHeight / 1.5;

	else {

	    imageW = visualizerDrawer.getWidth() / 2;
	    imageH = visualizerDrawer.getHeight() / 2;
	}
	// System.out.println(imageW + ", h:" + imageH)
	visualizerDrawer.gc.drawImage(visualizerDrawer.foregroundImage,
		(visualizerDrawer.canvasWidth / 2 - imageW / 2) - imageW * array[0] / 2,
		(visualizerDrawer.canvasHeight / 2 - imageH / 2) - imageH * array[0] / 2, imageW + imageW * array[0],
		imageH + imageH * array[0]);

    }

    /**
     * Draws this Light for The Polyspiral
     * 
     * @param image
     */
    private void drawLight(Image image) {
	visualizerDrawer.gc.drawImage(image, 0, 0);
	visualizerDrawer.gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), visualizerDrawer.canvasWidth, 0,
		-image.getWidth(), image.getHeight());
    }

    /**
     * Calculate.
     */
    void calculate() {
	int maxX = visualizerDrawer.canvasWidth - 1;
	int maxY = visualizerDrawer.canvasHeight - 1;
	pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
	centerX = maxX / 2;
	centerY = maxY / 2;
	radius = Math.min(rWidth, rHeight) / 2.0F;
    }

    /**
     * Logical X coordinates to Device X coordinates.
     *
     * @param logicalX
     *            the logical X
     * @return the int
     */
    private int deviceX(double logicalX) {
	return (int) (centerX + logicalX / pixelSize);
    }

    /**
     * Logical Y coordinates to Device Y coordinates.
     *
     * @param logicalY
     *            the logical Y
     * @return the int
     */
    private int deviceY(double logicalY) {
	return (int) (centerY - logicalY / pixelSize);
    }

}
