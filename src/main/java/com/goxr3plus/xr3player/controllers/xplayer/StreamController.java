package com.goxr3plus.xr3player.controllers.xplayer;

import java.util.Map;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.AudioImageTool;
import com.goxr3plus.xr3player.utils.general.TimeTool;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.goxr3plus.xr3player.xplayer.waveform.WaveFormService;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class StreamController implements StreamPlayerListener {

	private final XPlayerController controller;

	public StreamController(XPlayerController xPlayerController) {
		this.controller = xPlayerController;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void opened(final Object dataSource, final Map<String, Object> map) {
		// some code here
	}

	float progress;

	@Override
	public void progress(final int nEncodedBytes, final long microSecondsPosition, final byte[] pcmdata,
		final Map<String, Object> properties) {

		// Return immediately
		if (controller.historyToggle.isSelected())
			return;

		// System.out.println("Entered Progress...")

		// Allow DSP ?
		if (Main.settingsWindow.getGeneralSettingsController().getHighGraphicsToggle().isSelected()
			&& controller.visualizerVisibility.get()) {
			controller.visualizer.writeDSP(pcmdata);
		}

		// DjVisualizer
		if (controller.djVisualizer != null)
			controller.djVisualizer.writeDSP(pcmdata);

		// Disc is being draggged?
		if (!controller.discIsDragging) {

			// previousTime = xPlayerUI.xPlayer.currentTime

			// .MP3 OR .WAV
			final String extension = controller.xPlayerModel.songExtensionProperty().get();
			if ("mp3".equals(extension) || "wav".equals(extension)) {

				// Calculate the progress until now
				progress = (nEncodedBytes > 0 && controller.xPlayer.getTotalBytes() > 0)
					? (nEncodedBytes * 1.0f / controller.xPlayer.getTotalBytes() * 1.0f)
					: -1.0f;
				// System.out.println(progress*100+"%")
				if (controller.visualizerWindow.isVisible())
					Platform.runLater(() -> controller.visualizerWindow.getProgressBar().setProgress(progress));

				// find the current time in seconds
				controller.xPlayerModel.setCurrentTime((int) (controller.xPlayerModel.getDuration() * progress));
				// System.out.println((double) xPlayerModel.getDuration() *
				// progress)

				// .WHATEVER MUSIC FILE*
			} else
				controller.xPlayerModel.setCurrentTime((int) (microSecondsPosition / 1000000));

			final String millisecondsFormatted = TimeTool.millisecondsToTime(microSecondsPosition / 1000);
			// System.out.println(milliFormat)

			// Paint the Modes
			if (!controller.xPlayer.isStopped() && !controller.waveFormVisualization.isHover()) {

				// TotalTime and CurrentTime
				final int totalTime = controller.xPlayerModel.getDuration();
				final int currentTime = controller.xPlayerModel.getCurrentTime();

				if (!controller.modeToggle.isSelected()) // Simple Mode for most of Users

					// Run on JavaFX Thread
					Platform.runLater(() -> {

						// Simple Mode
						controller.smTimeSlider.setMin(0);
						controller.smTimeSlider.setMax(totalTime);
						controller.smTimeSlider.setValue(currentTime);

						// smTimeSliderLabel
						controller.smTimeSliderLabel.setText(
							TimeTool.getTimeEdited(currentTime)
								+ "."
								+ (9 - Integer.parseInt(millisecondsFormatted.replace(".", "")))
								+ "  / "
								+ TimeTool.getTimeEdited(totalTime));

						// smTimeSliderProgress
						controller.smTimeSliderProgress.setProgress(controller.smTimeSlider.getValue() / controller.smTimeSlider.getMax());
					});

				else { // Advanced DJ Disc Mode

					// Update the disc Angle
					controller.disc.calculateAngleByValue(controller.xPlayerModel.getCurrentTime(), controller.xPlayerModel.getDuration(), false);

					// Update the disc time
					controller.disc.updateTimeDirectly(controller.xPlayerModel.getCurrentTime(), controller.xPlayerModel.getDuration(), millisecondsFormatted);

					// Run on JavaFX Thread
					Platform.runLater(() -> {

						// == RemainingTimeLabel
						controller.remainingTimeLabel.setText(
							TimeTool.getTimeEdited(totalTime - currentTime)
								+ "."
								+ (9 - Integer.parseInt(millisecondsFormatted.replace(".", ""))));

						// == ElapsedTimeLabel
						controller.elapsedTimeLabel.setText(TimeTool.getTimeEdited(currentTime) + millisecondsFormatted);

						// == Repaint the Disc
						controller.disc.repaint();

					});
				}

			}

		}
	}

	@Override
	public void statusUpdated(final StreamPlayerEvent streamPlayerEvent) {

		// Player status
		final Status status = streamPlayerEvent.getPlayerStatus();

		// Status.OPENED
		if (status == Status.OPENED && controller.xPlayer.getSourceDataLine() != null) {

			// Visualizer
			controller.visualizer.setupDSP(controller.xPlayer.getSourceDataLine());
			controller.visualizer.startDSP(controller.xPlayer.getSourceDataLine());

			// DjVisualizer
			if (controller.djVisualizer != null) {
				controller.djVisualizer.setupDSP(controller.xPlayer.getSourceDataLine());
				controller.djVisualizer.startDSP(controller.xPlayer.getSourceDataLine());
			}

			Platform.runLater(() -> {

				// WaveForm
				// if (!seekService.isRunning())
				// waveFormVisualization.getWaveService().startService(getxPlayerModel().getSongPath(),
				// WaveFormJob.AMPLITUDES_AND_WAVEFORM);

				// Marquee Text
				controller.mediaFileMarquee.setText(IOInfo.getFileName(controller.xPlayerModel.songPathProperty().get()));

				// Notification
				if (Main.settingsWindow.getxPlayersSettingsController().getShowPlayerNotifications().isSelected()) {

					// Check if it has Album Image
					final Image image = AudioImageTool.getAudioAlbumImage(controller.xPlayerModel.songPathProperty().get(), 60, 60);

					// Show Notification
					if (!controller.discIsDragging)
						AlertTool.showNotification("Playing on deck " + (controller.getKey() + 1),
							IOInfo.getFileName(controller.xPlayerModel.songPathProperty().get()), Duration.seconds(4),
							NotificationType.SIMPLE, image != null ? JavaFXTool.getImageView(image, 60, 60)
								: JavaFXTool.getFontIcon("gmi-album", Color.WHITE, 60));
				}
			});

			// STATUS OPENING
		} else if (status == Status.OPENING) {

			// Run on JavaFX Thread
			Platform.runLater(() -> {

				// Wave Form Service
				if (!controller.seekService.isRunning())
					controller.waveFormVisualization.getWaveService().startService(controller.xPlayerModel.getSongPath(),
						WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);

			});

			// Status.RESUMED
		} else if (status == Status.RESUMED) {

			Platform.runLater(() -> {
				// playerStatusLabel.setText("Resuming");
				controller.resumeCode();

				// WaveForm
				if (!controller.waveFormVisualization.getWaveService().isRunning())
					controller.waveFormVisualization.startPainterService();

				// Notification
				// ActionTool.showNotification("Player [ " + this.getKey() + " ] Resuming",
				// InfoTool.getFileName(xPlayerModel.songPathProperty().get()),
				// Duration.seconds(2),
				// NotificationType.SIMPLE,
				// InfoTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60));
			});

			// Status.PLAYING
		} else if (status == Status.PLAYING) {

			Platform.runLater(() -> {
				controller.resumeCode();

				// WaveForm
				controller.waveFormVisualization.startPainterService();
			});

			// Status.PAUSED
		} else if (streamPlayerEvent.getPlayerStatus() == Status.PAUSED) {

			Platform.runLater(() -> {
				controller.playerStatusLabel.setText("Status : " + " Paused");
				controller.pauseCode();

				// WaveForm
				controller.waveFormVisualization.stopPainterService();

				// Notification
				// ActionTool.showNotification("Player [ " + this.getKey() + " ] Paused",
				// InfoTool.getFileName(xPlayerModel.songPathProperty().get()),
				// Duration.seconds(2),
				// NotificationType.SIMPLE,
				// InfoTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60));
			});

			// Status.STOPPED
		} else if (status == Status.STOPPED) {

			// Visualizer
			controller.visualizer.stopDSP();

			// DJVisualizer
			if (controller.djVisualizer != null)
				controller.djVisualizer.stopDSP();

			Platform.runLater(() -> {

				// SeekService running?
				if (controller.seekService.isRunning()) {

					// oh yeah
				} else {

					// Change Marquee text
					// mediaFileMarquee.setText("Player is Stopped");
					controller.playerStatusLabel.setText("Status : " + " Stopped");

					// Set time to 0 to not have problems with SeekService
					controller.xPlayerModel.setCurrentTime(0);

					// disk
					controller.disc.stopRotation();
					controller.disc.stopFade();

					// Visualizer
					controller.visualizer.stopVisualizer();

					// DJVisualizer
					if (controller.djVisualizer != null)
						controller.djVisualizer.stopVisualizer();

					// Recalculate disc
					controller.disc.calculateAngleByValue(0, 0, true);
					controller.disc.repaint();

					// Reset
					controller.fixPlayerStop();

					// smTimeSliderProgress
					controller.smTimeSliderProgress.setProgress(controller.smTimeSlider.getValue() / controller.smTimeSlider.getMax());

					// WaveForm
					controller.waveFormVisualization.stopPainterService();
				}

			});

			// Status.SEEKING
		} else if (status == Status.SEEKING) {

			// Platform.runLater(() -> playerStatusLabel.setText("Status : "+" Seeking"));

			// Status.SEEKED
		} else if (status == Status.SEEKED) {
			// TODO i need to add code here
		}

		// Fix the images
		if (status == Status.STOPPED || status == Status.RESUMED || status == Status.PLAYING || status == Status.PAUSED)
			Platform.runLater(() -> {
				// Advanced Mode
				controller.playPauseButton.setGraphic(controller.xPlayer.isPlaying() ? controller.pauseIcon : controller.playIcon);

				// Simple Mode
				controller.smPlayPauseButton.setGraphic(controller.xPlayer.isPlaying() ? controller.smPauseIcon : controller.smPlayIcon);

				// Micro Mode
				controller.microPlayPauseButton.setGraphic(controller.xPlayer.isPlaying() ? controller.microPauseIcon : controller.microPlayIcon);
			});
	}

}
