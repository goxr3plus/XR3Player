package main.java.com.goxr3plus.xr3player.xplayer.services;

import java.io.File;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool.FileType;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;

/**
 * Used by XR3Player to convert all unsupported audio formats to .mp3
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class ConverterService extends Service<Boolean> {
	
	/**
	 * The full path of audio file
	 */
	private String fileAbsolutePath;
	private String newFileAsbolutePath;
	private boolean byPass = false;
	
	/**
	 * The XPlayerController
	 */
	private final XPlayerController xPlayerController;
	
	/**
	 * Constructor
	 */
	public ConverterService(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		
		this.setOnSucceeded(s -> done());
		this.setOnCancelled(c -> done());
		this.setOnFailed(f -> done());
	}
	
	/**
	 * Start the Service for the given file
	 * 
	 * @param fileAbsolutePath
	 */
	public void convert(String fileAbsolutePath) {
		
		//Set the full fileAbsolutePath
		this.fileAbsolutePath = fileAbsolutePath;
		
		// Create Binding
		xPlayerController.getFxLabel().textProperty().bind(messageProperty());
		xPlayerController.getRegionStackPane().visibleProperty().bind(runningProperty());
		
		//Restart the Service
		restart();
	}
	
	/**
	 * When the Service is done
	 */
	private void done() {
		
		//		// Remove the unidirectional binding
		//		xPlayerController.getFxLabel().textProperty().unbind();
		//		xPlayerController.getRegionStackPane().visibleProperty().unbind();
		//		xPlayerController.getRegionStackPane().setVisible(false);
		
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				boolean succeeded = true;
				
				try {
					
					// Stop the previous audio
					updateMessage("Stop previous...");
					xPlayerController.getxPlayer().stop();
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				//Set Message
				super.updateMessage("Converting audio...");
				
				//Create the media folder if not existing
				String folderName = InfoTool.getAbsoluteDatabaseParentFolderPathWithSeparator() + "Media";
				if (!ActionTool.createFileOrFolder(folderName, FileType.DIRECTORY)) {
					ActionTool.showNotification("Internal Error", "Can't create Media Folder for converted files", Duration.seconds(4), NotificationType.WARNING);
					succeeded = false;
				}
				
				//New File Name
				newFileAsbolutePath = folderName + File.separator + InfoTool.getFileTitle(fileAbsolutePath) + ".mp3";
				
				//Convert any audio format to .mp3
				try {
					File source = new File(fileAbsolutePath);
					
					File target = new File(newFileAsbolutePath);
					AudioAttributes audio = new AudioAttributes();
					audio.setCodec("libmp3lame");
					audio.setBitRate(128000);
					audio.setChannels(2);
					audio.setSamplingRate(44100);
					EncodingAttributes attrs = new EncodingAttributes();
					attrs.setFormat("mp3");
					attrs.setAudioAttributes(audio);
					new Encoder().encode(source, target, attrs);
				} catch (Exception ex) {
					ex.printStackTrace();
					succeeded = false;
				}
				
				//Set Message
				super.updateMessage("Convert finished...");
				
				//Check if succeeded
				if (succeeded)
					Platform.runLater(() -> xPlayerController.playSong(newFileAsbolutePath));
				else
					ActionTool.showNotification("Convert failed", "Couldn't convert given media to .mp3 ", Duration.seconds(4), NotificationType.WARNING);
				
				return true;
			}
		};
	}
	
}
