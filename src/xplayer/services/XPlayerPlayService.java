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
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import smartcontroller.media.AudioType;
import xplayer.XPlayerController;

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
	 * @param fileAbsolutePath
	 *            The path of the audio
	 * @param secondsToSkip
	 */
	public void startPlayService(String fileAbsolutePath , int secondsToSkip) {
		if (locked || isRunning() || fileAbsolutePath == null || !InfoTool.isAudioSupported(fileAbsolutePath))
			return;
		
		// The path of the audio file
		xPlayerController.getxPlayerModel().songPathProperty().set(fileAbsolutePath);
		
		// Create Binding
		xPlayerController.getFxLabel().textProperty().bind(messageProperty());
		xPlayerController.getRegionStackPane().visibleProperty().bind(runningProperty());
		
		// Bytes to Skip
		this.secondsToSkip = secondsToSkip;
		
		// Restart the Service
		restart();
		
		// lock the Service
		locked = true;
		
	}
	
	/**
	 * Determines if the image of the disc is the NULL_IMAGE that means that the media inserted into the player has no album image.
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
		if (xPlayerController.getxPlayerModel().getDuration() == 0 || xPlayerController.getxPlayerModel().getDuration() == -1)
			xPlayerController.getDisc().getCanvas().setCursor(Cursor.OPEN_HAND);
		
		// Configure Media Settings
		xPlayerController.configureMediaSettings(false);
		xPlayerController.getDisc().repaint();
		
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
							checkAudioTypeAndUpdateXPlayerModel(xPlayerController.getxPlayerModel().songPathProperty().get())));
					
					// extension
					xPlayerController.getxPlayerModel().songExtensionProperty().set(InfoTool.getFileExtension(xPlayerController.getxPlayerModel().songPathProperty().get()));
					
					//== TotalTimeLabel
					Platform.runLater(() -> xPlayerController.getTotalTimeLabel().setText(InfoTool.getTimeEdited(xPlayerController.getxPlayerModel().getDuration())));
					
					// ----------------------- Load the Album Image
					image = InfoTool.getAudioAlbumImage(xPlayerController.getxPlayerModel().songPathProperty().get(), -1, -1);
					
					// ---------------------- Open the Audio
					updateMessage("Opening ...");
					xPlayerController.getxPlayer().open(xPlayerController.getxPlayerModel().songObjectProperty().get());
					
					// ----------------------- Play the Audio
					updateMessage("Starting ...");
					xPlayerController.getxPlayer().play();
					xPlayerController.getxPlayer().pause();
					
					//So the user wants to start from a position better than 0
					if (secondsToSkip > 0) {
						xPlayerController.getxPlayer().seek((long) ( ( (float) secondsToSkip )
								* ( xPlayerController.getxPlayer().getTotalBytes() / (float) xPlayerController.getxPlayerModel().getDuration() ) ));
						
						//Update the below bitches!!!!!!! Fuck dat flickiro a flickira e
						// daaaaaaaaaaaaaaaaaaaaanb bitchg!!!!Fuck Fock Fock Fock i was crazy when writting this code here
						xPlayerController.getxPlayerModel().setCurrentTime(secondsToSkip);
						xPlayerController.getxPlayerModel().setCurrentAngleTime(secondsToSkip);
						// Update the disc Angle
						xPlayerController.getDisc().calculateAngleByValue(secondsToSkip, xPlayerController.getxPlayerModel().getDuration(), true);
					}
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
			
		};
	}
	
	/**
	 * Checking the audio type -> File || URL
	 * 
	 * @param path
	 *            The path of the audio File
	 * @return returns
	 * @see AudioType
	 */
	public AudioType checkAudioTypeAndUpdateXPlayerModel(String path) {
		
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
	
	@Override
	public void succeeded() {
		super.succeeded();
		System.out.println("XPlayer [ " + xPlayerController.getKey() + " ] PlayService Succeeded...");
		
		// Replace the image of the disc
		xPlayerController.getDisc().replaceImage(image);
		( (ImageView) xPlayerController.getMediaTagImageButton().getGraphic() ).setImage(xPlayerController.getDisc().getImage());
		
		// add to played songs...
		String absolutePath = xPlayerController.getxPlayerModel().songPathProperty().get();
		Main.playedSongs.addIfNotExists(absolutePath,true);
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
