/**
 * 
 */
package xplayer.visualizer.geometryshapes;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import xplayer.visualizer.VisualizerDrawer;

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

//    /** The cool effect. */
//    final Image coolEffect = new Image(VisualizerModel.class.getResourceAsStream("anim1.gif"));
//
//    /** The yellow light. */
//    final Image yellowLight = new Image(VisualizerModel.class.getResourceAsStream("yellowLight.png"));
//
//    /** The blue light. */
//    final Image blueLight = new Image(VisualizerModel.class.getResourceAsStream("blueLight.png"));
//
//    /** The grey light. */
//    final Image greyLight = new Image(VisualizerModel.class.getResourceAsStream("greyLight.png"));
//
//    /** The light blue light. */
//    final Image lightBlueLight = new Image(VisualizerModel.class.getResourceAsStream("lightBlueLight.png"));
//
//    /** The red light. */
//    final Image redLight = new Image(VisualizerModel.class.getResourceAsStream("redLight.png"));

    // -------------------

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

    /** The color size. */
    private final int colorSize2 = 360;

    /** The color index. */
    private int colorIndex2 = 0;

    //

    /** The color size. */
    private final int colorSize3 = 360;

    /** The color index. */
    private int colorIndex3 = 0;

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

	// Background
	visualizerDrawer.drawBackgroundImage();

	// Set the background fill
	visualizerDrawer.gc.setFill(Color.rgb(0, 0, 0, array[0]));
	visualizerDrawer.gc.fillRect(0, 0, visualizerDrawer.canvasWidth, visualizerDrawer.canvasHeight);

	// Draw Random Ovals
	/*
	 * Exception in thread "JavaFX Application Thread"
	 * java.lang.IllegalArgumentException: bound must be positive at
	 * java.util.Random.nextInt(Random.java:388) at
	 * visualizer.model.drawPolySpiral( java:261)
	 * error(gc.fillOval(random.nextInt(width), random.nextInt(height),
	 * length + 2.00, length + 2.00);)
	 */
	for (int i = 0; i < total; i++) {

	    //Change the Fill
	    colorIndex2 = (colorIndex2 == colorSize2 - 1) ? 0 : colorIndex2 + 1;
	    visualizerDrawer.gc.setFill(Color.hsb(colorIndex2, 1.0f, 1.0f));

	    //Draw the Oval
	    visualizerDrawer.gc.fillOval(random.nextInt(visualizerDrawer.canvasWidth),
		    random.nextInt(visualizerDrawer.canvasHeight), length + 2.00, length + 2.00);
	}
	//
	//	// Draw Lights
	//	if (visualizerDrawer.canvasWidth > greyLight.getWidth()
	//		&& visualizerDrawer.canvasHeight > greyLight.getHeight())
	//	    if (array[0] < 0.2) {
	//		drawLight(greyLight);
	//	    } else if (array[0] < 0.3) {
	//		drawLight(lightBlueLight);
	//	    } else if (array[0] < 0.4) {
	//		drawLight(blueLight);
	//	    } else if (array[0] < 0.6) {
	//		drawLight(yellowLight);
	//	    } else if (array[0] < 0.9) {
	//		drawLight(redLight);
	//
	//	    }
	//
	//	// Scope
	//	if (visualizerDrawer.canvasWidth > greyLight.getWidth()
	//		&& visualizerDrawer.canvasHeight > greyLight.getHeight()) {
	//	    visualizerDrawer.gc.setStroke(visualizerDrawer.scopeColor);
	//	    double coolW = coolEffect.getWidth();
	//	    double coolH = coolEffect.getHeight() < visualizerDrawer.canvasHeight ? coolEffect.getHeight()
	//		    : visualizerDrawer.canvasHeight;

	// ------------------------Draw Scope----------------------------

	//	    // Scope 1
	//	    int zb = (int) (50 + 100 * array[0]);
	//	    int yLast = (int) (pSample[0] * (float) zb) + zb;
	//	    int angleIncrement = 1;
	//	    for (int a = angleIncrement, c = (int) (coolW / 2 - 50); c < (visualizerDrawer.canvasWidth
	//		    - coolW / 2); a += angleIncrement, c++) {
	//		int yNow = (int) (pSample[a] * (float) zb) + zb;
	//		visualizerDrawer.gc.strokeLine(c, yLast, c + 1.00, yNow);
	//		yLast = yNow;
	//	    }

	//	    visualizerDrawer.gc
	//		    .setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1.0));
	//
	//	    // Scope 2
	//	    int zb2 = (int) (visualizerDrawer.canvasHeight - 50 - 100 * array[0]);
	//	    int yLast2 = (int) (pSample[0] * (float) zb2) + zb2;
	//	    int angleIncrement2 = 1;
	//	    for (int a = angleIncrement2, c = (int) (coolW / 2 - 50); c < (visualizerDrawer.canvasWidth
	//		    - coolW / 2); a += angleIncrement2, c++) {
	//		int yNow2 = (int) (pSample[a] * (float) zb2) + zb2;
	//		visualizerDrawer.gc.strokeLine(c, yLast2, c + 1.00, yNow2);
	//		yLast2 = yNow2;
	//	    }

	// Cool Effects
	//	    visualizerDrawer.gc.drawImage(coolEffect, (coolW / 2 - 50) - coolW * array[0] / 2,
	//		    (visualizerDrawer.canvasHeight / 2 - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
	//		    coolH * array[0]);
	//	    visualizerDrawer.gc.drawImage(coolEffect, (visualizerDrawer.canvasWidth - coolW / 2) - coolW * array[0] / 2,
	//		    (visualizerDrawer.canvasHeight / 2 - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
	//		    coolH * array[0]);
	//
	//	    visualizerDrawer.gc.drawImage(coolEffect, (coolW / 2 - 50) - coolW * array[0] / 2,
	//		    (visualizerDrawer.canvasHeight - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
	//		    coolH * array[0]);
	//	    visualizerDrawer.gc.drawImage(coolEffect, (visualizerDrawer.canvasWidth - coolW / 2) - coolW * array[0] / 2,
	//		    (visualizerDrawer.canvasHeight - coolH / 2) - coolH * array[0] / 2, coolW * array[0],
	//		    coolH * array[0]);
	//	}

	//Foreground
	visualizerDrawer.drawForegroundImage(array);

	drawPolyspiral2();

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

		    //Set Stroke color
		    colorIndex3 = (colorIndex3 == colorSize3 - 1) ? 0 : colorIndex3 + 1;
		    visualizerDrawer.gc.setStroke(Color.hsb(colorIndex3, 1.0f, 1.0f, opacity));

		    //		    visualizerDrawer.gc.setStroke(
		    //			    Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), opacity));

		    //Draw the Line
		    visualizerDrawer.gc.strokeLine(deviceX(vertices[i].getX()), deviceY(vertices[i].getY()),
			    deviceX(vertices[j].getX()), deviceY(vertices[j].getY()));
		} else
		    break;

	// --------------------Draw PolySpiral ------------

	// if array[0]==1 it has an ugly effect of drawing one horizontal line
	// so i don't need this
	//	if (array[0] < 0.99) {
	//
	//	    do {
	//		polySpiralAngle = (float) Math.random() * 360;
	//		distance = (float) Math.random();
	//		increment = (float) Math.random();
	//	    } while (polySpiralAngle == 0 || distance == 0.0F || increment == 0.0F);
	//
	//	    distance = (float) Math.random() * array[0];// (float) 0.2// //
	//	    // array[0]*array[1]*100
	//	    increment = (float) 0.07; // (float) Math.random() * array[1] * 100
	//				      // + (float) 0.07
	//	    polySpiralAngle = array[1] < 0.35 ? array[0] * 360 : array[0] * 360 * (float) Math.random() * 100;
	//
	//	    curX = 0.0F;
	//	    curY = 0.0F;
	//	    cd = 0.0F;
	//
	//	    colorIndex3 = (colorIndex3 == colorSize3 - 1) ? 0 : colorIndex3 + 1;
	//	    visualizerDrawer.gc.setStroke(Color.hsb(colorIndex3, 1.0f, 1.0f,array[0] < 0.28 ? 0.0 : 1.0));
	//	    
	////	    visualizerDrawer.gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255),
	////		    array[0] < 0.28 ? 0.0 : 1.0));
	//	    
	//	    for (int i = 0; i < iterator; i++) {
	//
	//		// draw Line
	//		float lineAngle = TWO_PI * cd / 360.0f;
	//		float newX = curX + distance * (float) Math.cos(lineAngle);
	//		float newY = curY + distance * (float) Math.sin(lineAngle);
	//		visualizerDrawer.gc.strokeLine(deviceX(curX), deviceY(curY), deviceX(newX), deviceY(newY));
	//		curX = newX;
	//		curY = newY;
	//
	//		cd -= polySpiralAngle;
	//		distance = distance + increment;
	//	    }
	//	}

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

    // -------------------------------------------------------------------------------------------------------------------------

    private double incr = 0;
    private int colorIndex;

    /**
     * Draws a polyspiral and 4 arcs
     */
    private void drawPolyspiral2() {
	float[] pSample = visualizerDrawer.stereoMerge(visualizerDrawer.pLeftChannel, visualizerDrawer.pRightChannel);
	float[] array = visualizerDrawer.returnBandsArray(pSample, 4);

	//Background
	//visualizerDrawer.drawBackgroundImage()

	// Draw tge Oscilloscope Lines below
	// drawJuliaSet()
	// visualizerDrawer.gc.setGlobalAlpha(0.8 - Math.abs(array[0]))
	// oscilloscope.drawOscilloScopeLines()
	// visualizerDrawer.gc.setGlobalAlpha(1.0)

	incr = (incr + 0.3 + Math.abs(array[0])) % 360;
	double len = 6;
	double angleIncrement = Math.toRadians(incr);
	double x1 = visualizerDrawer.canvasWidth / 2.00;
	double y1 = visualizerDrawer.canvasHeight / 2.00;
	double angle = angleIncrement;

	// visualizerDrawer.gc.setStroke(Color.RED)
	//	visualizerDrawer.gc.setLineWidth(2);
	//	visualizerDrawer.gc.strokeArc(5, 5, visualizerDrawer.canvasWidth - 10, visualizerDrawer.canvasHeight - 10, 90,
	//		360 * Math.abs(array[0]), ArcType.OPEN);
	//	visualizerDrawer.gc.setStroke(Color.CYAN);
	//	visualizerDrawer.gc.strokeArc(15, 15, visualizerDrawer.canvasWidth - 30, visualizerDrawer.canvasHeight - 30,
	//		180, 360 * Math.abs(array[1]), ArcType.OPEN);
	//	visualizerDrawer.gc.setStroke(Color.FIREBRICK);
	//	visualizerDrawer.gc.strokeArc(25, 25, visualizerDrawer.canvasWidth - 50, visualizerDrawer.canvasHeight - 50,
	//		270, 360 * Math.abs(array[2]), ArcType.OPEN);
	//	visualizerDrawer.gc.setStroke(Color.CHARTREUSE);
	//	visualizerDrawer.gc.strokeArc(35, 35, visualizerDrawer.canvasWidth - 70, visualizerDrawer.canvasHeight - 70,
	//		360, 360 * Math.abs(array[3]), ArcType.OPEN);
	//	visualizerDrawer.gc.setLineWidth(1);

	// visualizerDrawer.gc.setLineWidth(0.5)
	visualizerDrawer.gc.setLineWidth(1.5);
	int until = (int) (x1 + y1) / 2; // (int)
					// (visualizerDrawer.canvasWidth/2 *
					// Math.abs(array[0]))
	double twoPI = Math.PI * 2;
	int lenIncrement = (int) (3 * Math.abs(array[1]));
	for (int i = 0; i < until; i++) {

	    colorIndex = (colorIndex == 360 - 1) ? 0 : colorIndex + 1;
	    visualizerDrawer.gc.setStroke(Color.hsb(colorIndex, 1.0f, 1.0f, 1));

	    double x2 = x1 + Math.cos(angle) * len;
	    double y2 = y1 - Math.sin(angle) * len;
	    visualizerDrawer.gc.strokeLine((int) x1, (int) y1, (int) x2, (int) y2);
	    x1 = x2;
	    y1 = y2;

	    len += lenIncrement;

	    angle = (angle + angleIncrement) % (twoPI);
	}
	visualizerDrawer.gc.setLineWidth(1);
    }

}
