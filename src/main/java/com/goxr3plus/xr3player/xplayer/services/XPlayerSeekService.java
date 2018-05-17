package main.java.com.goxr3plus.xr3player.xplayer.services;

import java.io.File;
import java.util.logging.Level;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.streamplayer.StreamPlayerException;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;

/**
 * This Service is used to skip the Audio of XPlayer to different time.
 *
 * @author GOXR3PLUS
 */
public class XPlayerSeekService extends Service<Boolean> {
	
	/** The bytes to be skipped */
	long bytesToSkip;
	
	/**
	 * I am using this variables when i want to stop the player and go to a specific time for example at 1 m and 32 seconds :)
	 */
	boolean stopPlayer;
	
	/**
	 * Determines if the Service is locked , if yes it can't be used .
	 */
	private volatile boolean locked;
	
	private final XPlayerController xPlayerController;
	
	/**
	 * Constructor.
	 */
	public XPlayerSeekService(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		setOnSucceeded(s -> done());
		setOnFailed(f -> done());
		
	}
	
	/**
	 * Start the Service.
	 *
	 * @param bytesToSkip
	 *            Bytes to skip
	 * @param stopPlayer
	 */
	public void startSeekService(long bytesToSkip , boolean stopPlayer) {
		String absoluteFilePath = xPlayerController.getxPlayerModel().songPathProperty().get();
		
		//First security check
		if (locked || isRunning() || absoluteFilePath == null)
			return;
		
		//Check if the file exists 
		if (!new File(absoluteFilePath).exists()) {
			ActionTool.showNotification("Media doesn't exist", "Current Media File doesn't exist anymore...", Duration.seconds(2), NotificationType.ERROR);
			done();
			
			//Fix the labels
			xPlayerController.fixPlayerStop();
			
			//Set Indicator Progress
			xPlayerController.getProgressIndicator().progressProperty().set(-1.0);
			
			return;
		}
		
		//Check if the bytesTheUser wants to skip are more than the total bytes of the audio
		if (bytesToSkip > xPlayerController.getxPlayer().getTotalBytes())
			return;
		
		//System.out.println(bytes)
		
		//StopPlayer
		this.stopPlayer = stopPlayer;
		
		// Bytes to Skip
		this.bytesToSkip = bytesToSkip;
		
		// Create Binding
		xPlayerController.getFxLabel().textProperty().bind(messageProperty());
		xPlayerController.getRegionStackPane().visibleProperty().bind(runningProperty());
		
		// lock the Service
		locked = true;
		
		// Restart
		restart();
	}
	
	/**
	 * When the Service is done.
	 */
	private void done() {
		
		// Remove the unidirectional binding
		xPlayerController.getFxLabel().textProperty().unbind();
		xPlayerController.getRegionStackPane().visibleProperty().unbind();
		xPlayerController.getRegionStackPane().setVisible(false);
		
		// Stop disc dragging!
		xPlayerController.discIsDragging = false;
		
		// Put the appropriate Cursor
		xPlayerController.getDisc().getCanvas().setCursor(Cursor.OPEN_HAND);
		
		// Recalculate Angle and paint again Disc
		xPlayerController.getDisc().calculateAngleByValue(xPlayerController.getxPlayerModel().getCurrentTime(), xPlayerController.getxPlayerModel().getDuration(), true);
		xPlayerController.getDisc().repaint();
		
		// unlock the Service
		locked = false;
		
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				boolean succeded = true;
				
				// ----------------------- Seek the Media
				updateMessage("Skipping the Audio");
				
				//Stop?
				if (stopPlayer)
					xPlayerController.getxPlayer().stop();
				
				// GO
				// if (bytes != 0) { // and xPlayer.isPausedOrPlaying())
				Main.logger.info("Seek Service Started..");
				
				// CurrentTime
				xPlayerController.getxPlayerModel().setCurrentTime(xPlayerController.getxPlayerModel().getCurrentAngleTime());
				
				try {
					xPlayerController.getxPlayer().seek(bytesToSkip);
				} catch (StreamPlayerException ex) {
					xPlayerController.logger.log(Level.WARNING, "", ex);
					succeded = false;
				}
				// }
				
				// ----------------------- Play Audio
				if (!xPlayerController.getxPlayer().isPausedOrPlaying()) {
					xPlayerController.getxPlayer().play();
					//xPlayer.pause();
				}
				
				// ----------------------- Configuration
				updateMessage("Applying Settings ...");
				
				// Configure Media Settings
				if (xPlayerController.getxPlayer().isPausedOrPlaying())
					xPlayerController.configureMediaSettings(true);
				
				return succeded;
			}
			
		};
	}
	
}
