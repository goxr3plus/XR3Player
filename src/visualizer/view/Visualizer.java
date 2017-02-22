/*
 * 
 */
package visualizer.view;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import visualizer.model.VisualizerDrawer;
import xplayer.presenter.XPlayerController;

/**
 * The Class Visualizer.
 *
 * @author GOXR3PLUS
 */
abstract class Visualizer extends VisualizerDrawer {

    /** The animation service. */
    public PaintService animationService = new PaintService();

    /**
     * Constructor
     * 
     * @param text
     */
    public Visualizer(String text) {
	System.out.println("Visualizer Constructor called...{" + text + "}");

	// if i didn't add the draw to the @Override resize(double width, double
	// height) then it must be into the below listeners

	// Make the magic happen when the width or height changes
	// ----------
	widthProperty().addListener((observable, oldValue, newValue) -> {
	    // System.out.println("New Visualizer Width is:" + newValue)

	    // Canvas Width
	    canvasWidth = (int) widthProperty().get();

	    // Compute the Color Scale
	    computeColorScale();

	});
	// -------------
	heightProperty().addListener((observable, oldValue, newValue) -> {
	    // System.out.println("New Visualizer Height is:" + newValue)

	    // Canvas Height
	    canvasHeight = (int) heightProperty().get();
	    halfCanvasHeight = canvasHeight >> 1;

	    // Sierpinski
	    sierpinski.sierpinskiRootHeight = canvasHeight;

	    // Compute the Color Scale
	    computeColorScale();
	});

    }

    /**
     * Stars the visualizer.
     */
    public void startVisualizer() {
	animationService.start();
    }

    /**
     * Stops the visualizer.
     */
    public void stopVisualizer() {
	animationService.stop();
	clear();
    }

    /**
     * @return True if AnimationTimer of Visualizer is Running
     */
    public boolean isRunning() {
	return animationService.isRunning();
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							      Paint Service
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */
    /**
     * This Service is updating the visualizer.
     *
     * @author GOXR3PLUS
     */
    public class PaintService extends AnimationTimer {

	/** The next second. */
	long nextSecond = 0L;

	/** The Constant ONE_SECOND_NANOS. */
	private static final long ONE_SECOND_NANOS = 1_000_000_000L;
	/**
	 * When this property is <b>true</b> the AnimationTimer is running
	 */
	private volatile SimpleBooleanProperty running = new SimpleBooleanProperty(false);

	/**
	 * XPlayerController reference
	 */
	XPlayerController xPlayerController;

	/**
	 * The animationService can draw
	 */
	private boolean draw = true;

	@Override
	public void start() {
	    // Values must be >0
	    if (canvasWidth <= 0 || canvasHeight <= 0) {
		canvasWidth = 1;
		canvasHeight = 1;
	    }

	    nextSecond = 0L;
	    super.start();
	    running.set(true);
	}

	@Override
	public void stop() {
	    super.stop();
	    running.set(false);
	}

	/**
	 * @return True if AnimationTimer is running
	 */
	public boolean isRunning() {
	    return running.get();
	}

	/**
	 * @return Running Property
	 */
	public SimpleBooleanProperty runningProperty() {
	    return running;
	}

	/**
	 * This method is used by XPlayerController to pass a reference to the AnimationTimer
	 *
	 * @param xPlayerController
	 *            The XPlayerController Reference
	 */
	public void passXPlayer(XPlayerController xPlayerController) {
	    this.xPlayerController = xPlayerController;
	}

	@Override
	public void handle(long nanos) {

	    //XPlayer controlls this animationTimer?
	    if (xPlayerController != null && !xPlayerController.visualizerStackController.isVisible()) {
		clear();
		draw = false;
	    } else
		draw = true;

	    //Can draw?
	    if (draw) {
		clear();
		switch (displayMode.get()) {

		case 0:
		    drawOscilloscope(false);
		    break;
		case 1:
		    drawOscilloscope(true);
		    break;
		case 2:
		    drawOscilloScopeLines();
		    break;
		case 3:
		    drawSpectrumBars();
		    break;
		case 4:
		    drawVUMeter();
		    break;
		case 5:
		    drawPolySpiral();
		    break;
		case 6:
		    drawSierpinski();
		    break;
		case 7:
		    drawJuliaSet();
		    break;
		default:
		    break;
		}

		// -- Show FPS if necessary.
		if (showFPS) {

		    framesPerSecond++;

		    // Check for 1 second passed
		    if (nanos >= nextSecond) {
			fps = framesPerSecond;
			framesPerSecond = 0;
			nextSecond = nanos + ONE_SECOND_NANOS;
		    }
		    gc.setStroke(Color.YELLOW);
		    gc.strokeText("FPS: " + fps + " (FRRH: " + frameRateRatioHint + ")", 0, canvasHeight - 1.00);
		}

	    } //END: if draw == TRUE

	    //--XRPlayer controller?
	    if (xPlayerController != null) {
		//Repaint the disc
		if (xPlayerController.disc != null)
		    xPlayerController.disc.repaint();
	    }

	    //--------------------------------------------------------------------------------------RUBBISH CODE
	    /*
	     * if (System.currentTimeMillis() >= lfu + 1000) { lfu =
	     * System.currentTimeMillis(); fps = framesPerSecond;
	     * framesPerSecond = 0; }
	     */

	    // System.out.println("Canvas Width is:" + canvasWidth + " , Canvas
	    // Height is:" + canvasHeight)

	    // System.out.println("Running..")
	}

    }

}
