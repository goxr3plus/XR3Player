/**
 * 
 */
package main.java.com.goxr3plus.xr3player.application.xplayer.visualizer.geometry;

import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.xplayer.visualizer.core.VisualizerDrawer;

/**
 * The Class Oscilloscope.
 * 
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 * 
 * 
 * Oscilloscope
 * 
 * -----------------------------------------------------------------------
 * 
 * -----------------------------------------------------------------------
 *
 * 
 * @author GOXR3PLUS
 */
public class Oscilloscope {

    // ---------------Oscilloscope-------------------

    /** The color size. */
    private final int colorSize = 360;

    /** The color index. */
    private int colorIndex = 0;

    /** The band width. */
    private float bandWidth;

    /** The x. */
    private int x = 0;

    /** The y. */
    private int y = 0;

    /** The x old. */
    private int xOld = 0;

    /** The y old. */
    @SuppressWarnings("unused")
    private int yOld = 0;

    /** VisualizerDrawer instance. */
    private VisualizerDrawer visualizerDrawer;

    // ---------------------------------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param visualizerDrawer
     *            the visualizer drawer
     */
    public Oscilloscope(VisualizerDrawer visualizerDrawer) {
	this.visualizerDrawer = visualizerDrawer;
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 			        Oscilloscope
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Draws an Oscilloscope.
     *
     * @param stereo
     *            The Oscilloscope with have 2 lines->stereo or 1 line->merge left and right audio
     */
    public void drawOscilloscope(boolean stereo) {
	float[] pSample1;

	// It will be stereo?
	if (stereo)
	    pSample1 = visualizerDrawer.pLeftChannel;
	else // not?Then merge the array
	    pSample1 = visualizerDrawer.stereoMerge;

	//Background
	visualizerDrawer.drawBackgroundImage();

	visualizerDrawer.gc.setStroke(visualizerDrawer.scopeColor);
	// System.out.println(pSample.length)

	int yLast1 = (int) (pSample1[0] * (float) visualizerDrawer.halfCanvasHeight)
		+ visualizerDrawer.halfCanvasHeight;
	int samIncrement1 = 1;
	for (int a = samIncrement1, c = 0; c < visualizerDrawer.canvasWidth; a += samIncrement1, c++) {
	    int yNow = (int) (pSample1[a] * (float) visualizerDrawer.halfCanvasHeight)
		    + visualizerDrawer.halfCanvasHeight;
	    visualizerDrawer.gc.strokeLine(c, yLast1, c + 1.00, yNow);
	    yLast1 = yNow;
	}
	

	// Oscilloscope will be stereo
	if (stereo) {
	    colorIndex = (colorIndex == colorSize - 1) ? 0 : colorIndex + 1;
	    visualizerDrawer.gc.setStroke(Color.hsb(colorIndex, 1.0f, 1.0f));

	    float[] pSample2 = visualizerDrawer.pRightChannel;

	    int yLast2 = (int) (pSample2[0] * (float) visualizerDrawer.halfCanvasHeight)
		    + visualizerDrawer.halfCanvasHeight;
	    int samIncrement2 = 1;
	    for (int a = samIncrement2, c = 0; c < visualizerDrawer.canvasWidth; a += samIncrement2, c++) {
		int yNow = (int) (pSample2[a] * (float) visualizerDrawer.halfCanvasHeight)
			+ visualizerDrawer.halfCanvasHeight;
		visualizerDrawer.gc.strokeLine(c, yLast2, c + 1.00, yNow);
		yLast2 = yNow;
	    }

	}

    }

    /**
     * Draws an Oscilloscope with up and down Lines.
     */
    public void drawOscilloScopeLines() {

	//Background
	visualizerDrawer.drawBackgroundImage();

	// Use HSB color model
	colorIndex = (colorIndex == colorSize - 1) ? 0 : colorIndex + 1;
	visualizerDrawer.gc.setStroke(Color.hsb(colorIndex, 1.0f, 1.0f));
	// System.out.println(colorIndex / (float) colorSize)

	int newSampleCount = (int) (visualizerDrawer.dataLine.getFormat().getFrameRate() * 0.023);
	bandWidth = (float) visualizerDrawer.canvasWidth / (float) newSampleCount;
	int halfHeight = visualizerDrawer.canvasHeight / 2;
	int quarterHeight = visualizerDrawer.canvasHeight / 4;
	xOld = 0;
	yOld = 0;
	// System.out.println(bandWidth)

	// Sum the sample values from the left and right audio channels.
	// Draw a line between the x,y coordinates of each new audio sample and
	// those of the previous sample. x = sample number; y = sample value
	for (int i = 0; i < newSampleCount; i++) {
	    x = (int) (i * bandWidth);
	    y = halfHeight
		    + (int) (quarterHeight * (visualizerDrawer.pLeftChannel[i] + visualizerDrawer.pRightChannel[i]));

	    x = Math.min(Math.max(0, x), visualizerDrawer.canvasWidth);
	    y = Math.min(Math.max(0, y), visualizerDrawer.canvasHeight);

	    visualizerDrawer.gc.strokeLine(xOld, halfHeight, x, y);
	    xOld = x;
	    yOld = y;
	}
	}
	
}
