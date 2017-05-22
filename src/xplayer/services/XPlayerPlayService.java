package xplayer.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.util.Duration;
import xplayer.model.AudioType;
import xplayer.presenter.XPlayerController;

/**
 * This Service is used to start the Audio of XR3Player
 *
 * @author GOXR3PLUS
 */
public class XPlayerPlayService extends Service<Boolean> {
	
	/** The album image of the audio */
	private Image image;
	
	/**
	 * Determines if the Service is locked , if yes it can't be used .
	 */
	private volatile boolean locked;
	
	private XPlayerController xPlayerController;
	
	/**
	 * Constructor
	 * 
	 * @param xPlayerController
	 */
	public XPlayerPlayService(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
	}
	
	/**
	 * Start the Service.
	 *
	 * @param path
	 *        The path of the audio
	 */
	public void startPlayService(String path) {
		if (locked || isRunning() || path == null || !InfoTool.isAudioSupported(path))
			return;
		
		// The path of the audio file
		xPlayerController.getxPlayerModel().songPathProperty().set(path);
		
		// Create Binding
		xPlayerController.getFxLabel().textProperty().bind(messageProperty());
		xPlayerController.getFxRegion().visibleProperty().bind(runningProperty());
		
		// Restart the Service
		restart();
		
		// lock the Service
		locked = true;
		
	}
	
	/**
	 * Determines if the image of the disc is the NULL_IMAGE that means that
	 * the media inserted into the player has no album image.
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
		xPlayerController.getFxRegion().visibleProperty().unbind();
		xPlayerController.getFxRegion().setVisible(false);
		
		// Set the appropriate cursor
		if (xPlayerController.getxPlayerModel().getDuration() == 0 || xPlayerController.getxPlayerModel().getDuration() == -1)
			xPlayerController.getDisc().getCanvas().setCursor(Cursor.OPEN_HAND);
		
		// Configure Media Settings
		xPlayerController.configureMediaSettings(false);
		
		// unlock the Service
		locked = false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				try {
					
					// Stop the previous audio
					updateMessage("Stop previous...");
					xPlayerController.getxPlayer().stop();
					
					// ---------------------- Load the File
					updateMessage("File Configuration ...");
					
					// duration
					xPlayerController.getxPlayerModel().setDuration(InfoTool.durationInSeconds(xPlayerController.getxPlayerModel().songPathProperty().get(),
							checkAudioType(xPlayerController.getxPlayerModel().songPathProperty().get())));
					
					// extension
					xPlayerController.getxPlayerModel().songExtensionProperty().set(InfoTool.getFileExtension(xPlayerController.getxPlayerModel().songPathProperty().get()));
					
					// ----------------------- Load the Album Image
					image = InfoTool.getMp3AlbumImage(xPlayerController.getxPlayerModel().songPathProperty().get(), -1, -1);
					
					// ---------------------- Open the Audio
					updateMessage("Opening ...");
					xPlayerController.getxPlayer().open(xPlayerController.getxPlayerModel().songObjectProperty().get());
					
					// ----------------------- Play the Audio
					updateMessage("Starting ...");
					xPlayerController.getxPlayer().play();
					xPlayerController.getxPlayer().pause();
					
					// ----------------------- Configuration
					//			updateMessage("Applying Settings ...");
					//
					//			// Configure Media Settings
					//			configureMediaSettings(false);
					
					// ....well let's go
				} catch (Exception ex) {
					xPlayerController.logger.log(Level.WARNING, "", ex);
					Platform.runLater(
							() -> ActionTool.showNotification("ERROR", "Can't play \n[" + InfoTool.getMinString(xPlayerController.getxPlayerModel().songPathProperty().get(), 30)
									+ "]\n" + "It is corrupted or maybe unsupported", Duration.millis(1500), NotificationType.ERROR));
					return false;
				} finally {
					
					// Print the current audio file path
					System.out.println("Current audio path is ...:" + xPlayerController.getxPlayerModel().songPathProperty().get());
					
				}
				
				return true;
			}
			
			/**
			 * Checking the audio type -> File || URL
			 * 
			 * @param path
			 *        The path of the audio File
			 * @return returns
			 * @see AudioType
			 */
			AudioType checkAudioType(String path) {
				
				// File?
				try {
					xPlayerController.getxPlayerModel().songObjectProperty().set(new File(path));
					return AudioType.FILE;
				} catch (Exception ex) {
					xPlayerController.logger.log(Level.WARNING, "", ex);
				}
				
				// URL?
				try {
					xPlayerController.getxPlayerModel().songObjectProperty().set(new URL(path));
					return AudioType.URL;
				} catch (MalformedURLException ex) {
					xPlayerController.logger.log(Level.WARNING, "MalformedURLException", ex);
				}
				
				// very dangerous this null here!!!!!!!!!!!
				xPlayerController.getxPlayerModel().songObjectProperty().set(null);
				
				return AudioType.UNKNOWN;
			}
			
		};
	}
	
	@Override
	public void succeeded() {
		super.succeeded();
		System.out.println("XPlayer [ " + xPlayerController.getKey() + " ] PlayService Succeeded...");
		
		// Replace the image of the disc
		xPlayerController.getDisc().replaceImage(image);
		
		// add to played songs...
		String absolutePath = xPlayerController.getxPlayerModel().songPathProperty().get();
		Main.playedSongs.add(absolutePath);
		xPlayerController.getxPlayerPlayList().getSmartController().getInputService().start(Arrays.asList(new File(absolutePath)));
		
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
