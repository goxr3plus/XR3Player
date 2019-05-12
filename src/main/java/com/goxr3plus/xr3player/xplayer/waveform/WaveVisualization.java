/*
 * 
 */
package com.goxr3plus.xr3player.xplayer.waveform;

import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.xplayer.waveform.WaveFormService.WaveFormJob;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;

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

	private boolean recalculateWaveData = true;

	private final XPlayerController xPlayerController;

	/**
	 * Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public WaveVisualization(final XPlayerController xPlayerController, final int width, final int height) {
		super(width, height);
		super.setWaveVisualization(this);
		this.xPlayerController = xPlayerController;
		waveService = new WaveFormService(xPlayerController);
		animationService = new PaintService();

		// ----------
		widthProperty().addListener((observable, oldValue, newValue) -> {

			// Canvas Width
			this.width = Math.round(newValue.floatValue());

			// Draw single line :)
			recalculateWaveData = true;
			clear();

		});
		// -------------
		heightProperty().addListener((observable, oldValue, newValue) -> {

			// Canvas Height
			this.height = Math.round(newValue.floatValue());

			// Draw single line :)
			recalculateWaveData = true;
			clear();
		});

		// Tricky mouse events
		setOnMouseMoved(m -> setMouseXPosition((int) m.getX()));
		setOnMouseDragged(m -> this.setMouseXPosition((int) m.getX()));
		setOnMouseExited(m -> setMouseXPosition(-1));
	}
	// --------------------------------------------------------------------------------------//

	/**
	 * @return the animationService
	 */
	public PaintService getAnimationService() {
		return animationService;
	}

	public WaveFormService getWaveService() {
		return waveService;
	}

	// --------------------------------------------------------------------------------------//

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

		@Override
		public void start() {
			// Values must be >0
			if (width <= 0 || height <= 0)
				width = height = 1;

			super.start();
			running.set(true);
		}

		@Override
		public void handle(final long nanos) {

			// Speed improvement
			if (!xPlayerController.getModeToggle().isSelected() && xPlayerController.getModesStackPane().isVisible()) {
				return;
			}

			// If the player is stopped , stop the animation timer
			if (!xPlayerController.xPlayer.isPlaying())
				super.stop();

			// Set Timer X Position
			final double percent = xPlayerController.xPlayerModel.getCurrentTime()
					/ (double) xPlayerController.xPlayerModel.getDuration();
			setTimerXPosition((int) (percent * width));

			// If resulting wave is not calculated
			if (getWaveService().getResultingWaveform() == null || recalculateWaveData) {

				// Start the Service
				getWaveService().startService(getWaveService().getFileAbsolutePath(), WaveFormJob.WAVEFORM);
				recalculateWaveData = false;

				return;
			} else if (getWaveService().getWavAmplitudes() == null) {

				// Start the Service
				getWaveService().startService(getWaveService().getFileAbsolutePath(),
						WaveFormJob.AMPLITUDES_AND_WAVEFORM);
				recalculateWaveData = false;

				return;
			}

			// Draw wave
			paintWaveForm();

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
