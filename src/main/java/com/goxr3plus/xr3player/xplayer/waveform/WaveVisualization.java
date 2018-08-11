/*
 * 
 */
package main.java.com.goxr3plus.xr3player.xplayer.waveform;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;

/**
 * The Class Visualizer.
 *
 * @author GOXR3PLUS
 */
public class WaveVisualization extends WaveFormPane {
	
	/*** This Service is constantly repainting the wave */
	private final PaintService animationService;
	
	/*** This Service is creating the wave data for the painter */
	private final WaveFormService waveService;
	
	private boolean recalculateWaveData;
	
	private XPlayerController xPlayerController;
	
	/**
	 * Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public WaveVisualization(XPlayerController xPlayerController, int width, int height) {
		super(width, height);
		this.xPlayerController = xPlayerController;
		waveService = new WaveFormService(xPlayerController);
		animationService = new PaintService();
		
		// ----------
		widthProperty().addListener((observable , oldValue , newValue) -> {
			//System.out.println("New Visualizer Width is:" + newValue);
			
			// Canvas Width
			this.width = newValue.intValue();
			recalculateWaveData = true;
			
		});
		// -------------
		heightProperty().addListener((observable , oldValue , newValue) -> {
			//System.out.println("New Visualizer Height is:" + newValue);
			
			// Canvas Height
			this.height = newValue.intValue();
			recalculateWaveData = true;
		});
	}
	//--------------------------------------------------------------------------------------//
	
	/**
	 * @return the animationService
	 */
	public PaintService getAnimationService() {
		return animationService;
	}
	
	public WaveFormService getWaveService() {
		return waveService;
	}
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * Stars the wave visualiser painter
	 */
	public void startPainterService() {
		animationService.start();
	}
	
	/**
	 * Stops the wave visualiser painter
	 */
	public void stopPainterService() {
		animationService.stop();
		clear();
	}
	
	/**
	 * @return True if AnimationTimer of Visualiser is Running
	 */
	public boolean isPainterServiceRunning() {
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
		
		/*** When this property is <b>true</b> the AnimationTimer is running */
		private volatile SimpleBooleanProperty running = new SimpleBooleanProperty(false);
		
		/*** The animationService can draw */
		private boolean drawEnabled = true;
		
		private long previousNanos = 0;
		
		@Override
		public void start() {
			// Values must be >0
			if (width <= 0 || height <= 0)
				width = height = 1;
			
			super.start();
			running.set(true);
		}
		
		/**
		 * If draw is false , nothing will be drawn
		 * 
		 * @param enabled
		 */
		public void setDrawEnabled(boolean enabled) {
			drawEnabled = enabled;
		}
		
		@Override
		public void handle(long nanos) {
			
			//Every 300 millis update
			//			if (nanos >= previousNanos + 100000 * 1000) { //
			//				previousNanos = nanos;
			//				WaveVisualization.this.setTimerXPosition(WaveVisualization.this.getTimerXPosition() + 1);
			//			}
			
			//If the player is stopped , stop the animation timer
			if (!WaveVisualization.this.xPlayerController.getxPlayer().isPlaying())
				super.stop();
			
			//Set Timer X Position
			double percent = WaveVisualization.this.xPlayerController.getxPlayerModel().getCurrentTime()
					/ (double) WaveVisualization.this.xPlayerController.getxPlayerModel().getDuration();
			System.out.println("percent : " + percent);
			WaveVisualization.this.setTimerXPosition((int) ( ( percent * WaveVisualization.this.width ) ));
			
			//Check if wave data needs to be recalculated
			if (recalculateWaveData) {
				WaveVisualization.this.setWaveData(processAmplitudes(WaveVisualization.this.getWaveService().getWavAmplitudes()));
				recalculateWaveData = false;
			}
			
			//Draw the wave form
			WaveVisualization.this.drawWaveForm();
			
			//Print
			System.out.println("Wave Data : " + WaveVisualization.this.getWaveData() + " ,Wave Visualization : " + WaveVisualization.this.getWaveService().getWavAmplitudes());
		}
		
		/**
		 * Process the amplitudes
		 * 
		 * @param sourcePcmData
		 * @return An array with amplitudes
		 */
		private float[] processAmplitudes(int[] sourcePcmData) {
			try {
				int width = WaveVisualization.this.width;    // the width of the resulting waveform panel
				float[] waveData = new float[width];
				int samplesPerPixel = sourcePcmData.length / width;
				
				for (int w = 0; w < width; w++) {
					float nValue = 0.0f;
					
					for (int s = 0; s < samplesPerPixel; s++) {
						nValue += ( Math.abs(sourcePcmData[w * samplesPerPixel + s]) / 65536.0f );
					}
					nValue /= samplesPerPixel;
					waveData[w] = nValue;
				}
				return waveData;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
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
		
	}
	
}
