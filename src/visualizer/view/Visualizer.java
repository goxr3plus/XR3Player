/*
 * 
 */
package visualizer.view;

import disc.DJDisc;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import visualizer.model.VisualizerDrawer;

/**
 * The Class Visualizer.
 *
 * @author GOXR3PLUS
 */
public abstract class Visualizer extends VisualizerDrawer {
	
	/**
	 * @param string
	 */
	public Visualizer(String string) {
		super(string);
		
		// if i didn't add the draw to the @Override resize(double width, double
		// height) then it must be into the below listeners
		
		// Make the magic happen when the width or height changes
		// ----------
		widthProperty().addListener((observable , oldValue , newValue) -> {
			//System.out.println("New Visualizer Width is:" + newValue)
			
			// Canvas Width
			canvasWidth = (int) widthProperty().get();
			
			// Compute the Color Scale
			computeColorScale();
			
		});
		// -------------
		heightProperty().addListener((observable , oldValue , newValue) -> {
			//System.out.println("New Visualizer Height is:" + newValue)
			
			// Canvas Height
			canvasHeight = (int) heightProperty().get();
			halfCanvasHeight = canvasHeight >> 1;
			
			// Sierpinski
			sierpinskiRootHeight = canvasHeight;
			
			// Compute the Color Scale
			computeColorScale();
		});
		
	}
	
	/** The animation service. */
	public PaintService animationService = new PaintService();
	
	/**
	 * Add a mouse listener so every time primary mouse button is clicked the
	 * visualizer is changing.
	 */
	protected void addMouseListener() {
		
		setCursor(Cursor.HAND);
		setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.PRIMARY)
				displayMode = ( displayMode + 1 > 4 ) ? 0 : ++displayMode;
		});
		
	}
	
	// /**
	// * Resizes the visualizer to the given values.
	// *
	// * @param width the width
	// * @param height the height
	// * @return The Visualizer
	// */
	// public Visualizer resizeVisualizer(double width , double height) {
	// if (width > 0 && height > 0) {
	//
	// this.canvasWidth = (int) width;
	// this.canvasHeight = (int) height;
	// halfCanvasHeight = this.canvasHeight >> 1;
	// sierpinskiRootHeight = height;
	//
	// System.out.println(this.canvasWidth);
	// System.out.println(this.canvasHeight);
	//
	// // Set the width and height of Canvas
	// setWidth(width);
	// setHeight(height);
	//
	// // Careful with contentBias here
	// prefWidth(-1);
	// prefHeight(-1);
	//
	// // autosize()
	//
	// // System.out.println("Content Bias is:"+this.getContentBias())
	//
	// computeColorScale();
	// }
	//
	// return this;
	// }
	
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
		
		/** The disc. */
		DJDisc disc;
		
		@Override
		public void start() {
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
		 * This method is used by XPLayerVisualizer to pass it's disc so it is
		 * repainted from this AnimationTimer.
		 *
		 * @param disc the disc
		 */
		public void passDJDisc(DJDisc disc) {
			this.disc = disc;
		}
		
		@Override
		public void handle(long nanos) {
			// System.out.println("Visualizer :" + disc.getArcColor() + " is
			// running..")
			
			clear();
			switch (displayMode) {
				
				case 0:
					drawScope();
					break;
				case 1:
					drawSpectrumAnalyser();
					break;
				case 2:
					drawVUMeter();
					break;
				case 3:
					drawPolySpiral();
					break;
				case 4:
					drawSierpinski();
					break;
				case 5:
					drawJuliaFractals();
				default:
					break;
			}
			
			// System.out.println("Running..");
			
			// -- Show FPS if necessary.
			if (showFPS) {
				
				framesPerSecond++;
				
				// Check for 1 second passed
				if (nanos >= nextSecond) {
					fps = framesPerSecond;
					framesPerSecond = 0;
					nextSecond = nanos + ONE_SECOND_NANOS;
				}
				
				/* if (System.currentTimeMillis() >= lfu + 1000) { lfu =
				 * System.currentTimeMillis(); fps = framesPerSecond;
				 * framesPerSecond = 0; } */
				gc.setStroke(Color.YELLOW);
				gc.strokeText("FPS: " + fps + " (FRRH: " + frameRateRatioHint + ")", 0, canvasHeight - 1.00);
			}
			
			// player.analyserBox.repaintCanvas();
			
			if (disc != null)
				disc.repaint();
		}
		
	}
	
}
