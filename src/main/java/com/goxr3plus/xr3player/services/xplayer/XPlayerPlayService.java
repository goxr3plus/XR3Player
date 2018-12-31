package main.java.com.goxr3plus.xr3player.services.xplayer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.enums.AudioType;
import main.java.com.goxr3plus.xr3player.application.enums.NotificationType;
import main.java.com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import main.java.com.goxr3plus.xr3player.utils.general.ExtensionTool;
import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;
import main.java.com.goxr3plus.xr3player.utils.general.TimeTool;
import main.java.com.goxr3plus.xr3player.utils.io.IOTool;
import main.java.com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * This Service is used to start the Audio of XR3Player
 *
 * @author GOXR3PLUS
 */
public class XPlayerPlayService extends Service<Boolean> {

	/** The seconds to be skipped */
	private int secondsToSkip;

	/** The album image of the audio */
	private Image image;

	/**
	 * Determines if the Service is locked , if yes it can't be used .
	 */
	private volatile boolean locked;

	private final XPlayerController xPlayerController;
	private final ConverterService converterService;

	/**
	 * Constructor
	 * 
	 * @param xPlayerController
	 */
	public XPlayerPlayService(final XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		this.converterService = new ConverterService(xPlayerController);
	}

	/**
	 * Start the Service.
	 *
	 * @param fileAbsolutePath The path of the audio
	 * @param secondsToSkip
	 */
	public void startPlayService(final String fileAbsolutePath, final int secondsToSkip) {

		// First Security Check
		if (locked || isRunning() || fileAbsolutePath == null)
			return;

		// Check if converter is running
		// if (converterService.isRunning()) {
		// ActionTool.showNotification("Converter is running", "Converter is already
		// running on current player\n give it some seconds to finish",
		// Duration.seconds(4),
		// NotificationType.INFORMATION);
		// return;
		// }

		// Test if the audioFile needs to be converted
		if (!ExtensionTool.isAudioSupported(fileAbsolutePath)) {
			if (ExtensionTool.isAudio(fileAbsolutePath)) { // Check if we have Audio

				// Show information to the user
				AlertTool.showNotification("File is converting",
						"Current audio file format is not supported:\n so it will automatically be converted into .mp3.",
						Duration.seconds(4), NotificationType.INFORMATION);

				// Give it a convert
				converterService.convert(fileAbsolutePath);

				return;

			} else if (ExtensionTool.isVideo(fileAbsolutePath)) { // Check if we have Video
				// Show information to the user
				AlertTool.showNotification("File is converting",
						"Current Video file format is not supported:\n so it will automatically be converted into .mp3.",
						Duration.seconds(4), NotificationType.INFORMATION);

				// Give it a convert
				converterService.convert(fileAbsolutePath);

				return;
			} else {
				// Show information to the user
				AlertTool.showNotification("No Audio File", "Can't play this file format", Duration.seconds(4),
						NotificationType.INFORMATION);
			}

			return;
		}

		// The path of the audio file
		xPlayerController.getxPlayerModel().songPathProperty().set(fileAbsolutePath);

		// Create Binding
		xPlayerController.getFxLabel().textProperty().bind(messageProperty());
		xPlayerController.getRegionStackPane().visibleProperty().bind(runningProperty());
		xPlayerController.getProgressIndicator().progressProperty().unbind();
		xPlayerController.getProgressIndicator().progressProperty().set(-1.0);

		// Bytes to Skip
		this.secondsToSkip = secondsToSkip;

		// Restart the Service
		restart();

		// lock the Service
		locked = true;

	}

	/**
	 * Determines if the image of the disc is the NULL_IMAGE that means that the
	 * media inserted into the player has no album image.
	 *
	 * @return true if the DiscImage==null <br>
	 *         false if the DiscImage!=null
	 */
	public boolean isDiscImageNull() {
		return image == null;
	}

	/**
	 * When the Service is done.
	 */
	private void done() {

		// Remove the unidirectional binding
		xPlayerController.getFxLabel().textProperty().unbind();
		xPlayerController.getRegionStackPane().visibleProperty().unbind();
		xPlayerController.getRegionStackPane().setVisible(false);

		// Set the appropriate cursor
		if (xPlayerController.getxPlayerModel().getDuration() == 0
				|| xPlayerController.getxPlayerModel().getDuration() == -1)
			xPlayerController.getDisc().getCanvas().setCursor(Cursor.OPEN_HAND);

		// Configure Media Settings
		xPlayerController.configureMediaSettings(false);
		xPlayerController.getDisc().repaint();

		// unlock the Service
		locked = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {

				final AudioType[] audioType = { null };
				String audioFullPath = null;

				try {

					// Stop the previous audio
					updateMessage("Stop previous...");
					xPlayerController.getxPlayer().stop();

					// ---------------------- Load the File
					updateMessage("File Configuration ...");

					// duration
					audioFullPath = xPlayerController.getxPlayerModel().songPathProperty().get();
					audioType[0] = checkAudioTypeAndUpdateXPlayerModel(audioFullPath);
					xPlayerController.getxPlayerModel()
							.setDuration(TimeTool.durationInSeconds(audioFullPath, audioType[0]));

					// extension
					xPlayerController.getxPlayerModel().songExtensionProperty()
							.set(IOTool.getFileExtension(audioFullPath));

					// == TotalTimeLabel
					Platform.runLater(() -> xPlayerController.getTotalTimeLabel()
							.setText(TimeTool.getTimeEdited(xPlayerController.getxPlayerModel().getDuration())));

					// ----------------------- Load the Album Image
					image = InfoTool.getAudioAlbumImage(audioFullPath, -1, -1);

					// ---------------------- Open the Audio
					updateMessage("Opening ...");
					xPlayerController.getxPlayer().open(xPlayerController.getxPlayerModel().songObjectProperty().get());

					// ----------------------- Play the Audio
					updateMessage("Starting ...");
					xPlayerController.getxPlayer().play();
					xPlayerController.getxPlayer().pause();

					// So the user wants to start from a position better than 0
					if (secondsToSkip > 0) {
						xPlayerController.getxPlayer()
								.seek((long) ((secondsToSkip) * (xPlayerController.getxPlayer().getTotalBytes()
										/ (float) xPlayerController.getxPlayerModel().getDuration())));

						// Update XPlayer Model
						xPlayerController.getxPlayerModel().setCurrentTime(secondsToSkip);
						xPlayerController.getxPlayerModel().setCurrentAngleTime(secondsToSkip);

						// Update the disc Angle
						xPlayerController.getDisc().calculateAngleByValue(secondsToSkip,
								xPlayerController.getxPlayerModel().getDuration(), true);
					}

					// ....well let's go
				} catch (final Exception ex) {
					xPlayerController.logger.log(Level.WARNING, "", ex);
					Platform.runLater(() -> {
						final String audioPath = xPlayerController.getxPlayerModel().songPathProperty().get();

						// Media not existing any more?
						if (audioType[0] != null && audioPath != null && !new File(audioPath).exists())
							AlertTool.showNotification("Media doesn't exist",
									"Current Media File doesn't exist anymore...", Duration.seconds(2),
									NotificationType.ERROR);

						// Not available Audio Devices?
						else if (xPlayerController.getxPlayer().getMixers().isEmpty())
							AlertTool.showNotification("No Audio Devices",
									"We can’t find an audio device.\nMake sure that headphones or speakers are connected.\n For more info, search your device for “Manage audio devices”",
									Duration.millis(10000), NotificationType.ERROR);

						// Audio Corrupted?
						else
							AlertTool.showNotification("Can't play current Audio",
									"Can't play \n["
											+ InfoTool.getMinString(
													xPlayerController.getxPlayerModel().songPathProperty().get(), 30,"...")
											+ "]\nIt is corrupted or maybe unsupported",
									Duration.millis(1500), NotificationType.ERROR);

					});
					return false;
				} finally {

					// Print the current audio file path
					System.out.println("Current audio path is ...:"
							+ xPlayerController.getxPlayerModel().songPathProperty().get());

				}

				return true;
			}

		};
	}

	/**
	 * Checking the audio type -> File || URL
	 * 
	 * @param path The path of the audio File
	 * @return returns
	 * @see AudioType
	 */
	public AudioType checkAudioTypeAndUpdateXPlayerModel(final String path) {

		// File?
		try {
			xPlayerController.getxPlayerModel().songObjectProperty().set(new File(path));
			return AudioType.FILE;
		} catch (final Exception ex) {
			xPlayerController.logger.log(Level.WARNING, "", ex);
		}

		// URL?
		try {
			xPlayerController.getxPlayerModel().songObjectProperty().set(new URL(path));
			return AudioType.URL;
		} catch (final MalformedURLException ex) {
			xPlayerController.logger.log(Level.WARNING, "MalformedURLException", ex);
		}

		// very dangerous this null here!!!!!!!!!!!
		xPlayerController.getxPlayerModel().songObjectProperty().set(null);

		return AudioType.UNKNOWN;
	}

	@Override
	public void succeeded() {
		super.succeeded();
		System.out.println("XPlayer [ " + xPlayerController.getKey() + " ] PlayService Succeeded...");

		// Replace the image of the disc
		xPlayerController.getDisc().replaceImage(image);
		xPlayerController.getMediaTagImageView().setImage(xPlayerController.getDisc().getImage());

		// add to played songs...
		final String absolutePath = xPlayerController.getxPlayerModel().songPathProperty().get();

		// Run this on new Thread for performance reasons
		new Thread(() -> {

			// Add to played songs
			Main.playedSongs.add(absolutePath, true);
			Main.playedSongs.appendToTimesPlayed(absolutePath, true);

			// Check if file already in XPlayer Database History Playlist before trying to
			// add it
			try (PreparedStatement statement = Main.dbManager.getConnection()
					.prepareStatement("SELECT PATH FROM '"
							+ xPlayerController.getxPlayerPlayList().getSmartController().getDataBaseTableName()
							+ "' WHERE PATH=?")) {

				// Set the string to the prepared statement
				statement.setString(1, absolutePath);

				// Now check if at least one exists
				try (ResultSet resultSet = statement.executeQuery()) {

					boolean exists = false;
					// For each
					while (resultSet.next()) {
						exists = true;
						break;
					}

					// Insert into database if it doesn't exist
					if (!exists)
						Platform.runLater(() -> xPlayerController.getxPlayerPlayList().getSmartController()
								.getInputService().start(Arrays.asList(new File(absolutePath))));

				} catch (final Exception ex) {
					Main.logger.log(Level.WARNING, "", ex);
				}

				//
			} catch (final Exception ex) {
				Main.logger.log(Level.WARNING, "", ex);
			}

		}).start();

		done();
	}

	@Override
	public void failed() {
		super.failed();
		System.out.println("XPlayer [ " + xPlayerController.getKey() + " ] PlayService Failed...");

		// xPlayerModel.songObjectProperty().set(null)
		// xPlayerModel.songPathProperty().set(null)
		// xPlayerModel.songExtensionProperty().set(null)
		// xPlayerModel.setDuration(-1)
		// xPlayerModel.setCurrentTime(-1)
		// image = null
		// disc.replaceImage(null)

		done();
	}

	@Override
	public void cancelled() {
		super.cancelled();
		System.out.println("XPlayer [ " + xPlayerController.getKey() + " ] PlayService Cancelled...");

	}
}
