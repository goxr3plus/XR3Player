package main.java.com.goxr3plus.xr3player.application.xplayer.services;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.xplayer.presenter.XPlayerController;
import main.java.com.goxr3plus.xr3player.utils.general.ActionTool;
import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;
import main.java.com.goxr3plus.xr3player.utils.general.ActionTool.FileType;
import main.java.com.goxr3plus.xr3player.utils.javafx.NotificationType;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

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
	private ConvertProgressListener listener = new ConvertProgressListener();
	private final SimpleDoubleProperty convertProgress = new SimpleDoubleProperty();
	private Encoder encoder;
	
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
		this.setOnFailed(c -> done());
	}
	
	/**
	 * Start the Service for the given file
	 * 
	 * @param <convertProgress>
	 * 
	 * @param fileAbsolutePath
	 */
	public void convert(String fileAbsolutePath) {
		
		//Set the full fileAbsolutePath
		this.fileAbsolutePath = fileAbsolutePath;
		
		//Try to abort
		if (encoder != null)
			encoder.abortEncoding();
		
		//Set Progress to 0
		convertProgress.set(-1.0);
		
		// Create Binding
		xPlayerController.getFxLabel().textProperty().bind(messageProperty());
		xPlayerController.getRegionStackPane().visibleProperty().bind(runningProperty());
		xPlayerController.getProgressIndicator().progressProperty().bind(convertProgress);
		
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
				updateMessage("Converting ( " + InfoTool.getFileExtension(fileAbsolutePath) + " ) to ( mp3 )");
				
				//Create the media folder if not existing
				String folderName = InfoTool.getAbsoluteDatabaseParentFolderPathWithSeparator() + "Media";
				if (!ActionTool.createFileOrFolder(folderName, FileType.DIRECTORY)) {
					ActionTool.showNotification("Internal Error", "Can't create Media Folder for converted files", Duration.seconds(4), NotificationType.WARNING);
					succeeded = false;
				}
				
				//Check if it is already .mp3
				if (!"mp3".equals(InfoTool.getFileExtension(fileAbsolutePath))) {
					newFileAsbolutePath = folderName + File.separator + InfoTool.getFileTitle(fileAbsolutePath) + ".mp3";
					
					//Convert any audio format to .mp3
					try {
						
						//Files
						File source = new File(fileAbsolutePath);
						File target = new File(newFileAsbolutePath);
						
						//Audio Attributes
						AudioAttributes audio = new AudioAttributes();
						audio.setCodec("libmp3lame");
						audio.setBitRate(128000);
						audio.setChannels(2);
						audio.setSamplingRate(44100);
						
						//Encoding attributes
						EncodingAttributes attrs = new EncodingAttributes();
						attrs.setFormat("mp3");
						attrs.setAudioAttributes(audio);
						
						//Encode          
						if (encoder != null)
							encoder.abortEncoding();
						encoder = encoder != null ? encoder : new Encoder();
						encoder.encode(new MultimediaObject(source), target, attrs, listener);
						
					} catch (Exception ex) {
						ex.printStackTrace();
						succeeded = false;
					}
				} else
					newFileAsbolutePath = fileAbsolutePath;
				
				//Set Message
				super.updateMessage("Convert finished...");
				
				//System.out.println("After the error")
				
				//Check if succeeded
				if (succeeded)
					Platform.runLater(() -> xPlayerController.playSong(newFileAsbolutePath));
				else
					ActionTool.showNotification("Convert failed", "Couldn't convert given media to .mp3 ", Duration.seconds(4), NotificationType.WARNING);
				
				return true;
			}
		};
	}
	
	public class ConvertProgressListener implements EncoderProgressListener {
		int current = 1;
		
		public ConvertProgressListener() {
		}
		
		public void message(String m) {
			//      if ((ConverterFrame.this.inputfiles.length > 1) && 
			//        (this.current < ConverterFrame.this.inputfiles.length)) {
			//        ConverterFrame.this.encodingMessageLabel.setText(this.current + "/" + ConverterFrame.this.inputfiles.length);
			//      }
		}
		
		public void progress(int p) {
			
			double progress = p / 1000.00;
			//System.out.println(progress);
			
			Platform.runLater(() -> convertProgress.set(progress));
			//      ConverterFrame.this.encodingProgressLabel.setText(progress + "%");
			//      if (p >= 1000) {
			//        if (ConverterFrame.this.inputfiles.length > 1)
			//        {
			//          this.current += 1;
			//          if (this.current > ConverterFrame.this.inputfiles.length)
			//          {
			//            ConverterFrame.this.encodingMessageLabel.setText("Encoding Complete!");
			//            ConverterFrame.this.convertButton.setEnabled(true);
			//          }
			//        }
			//        else if (p == 1001)
			//        {
			//          ConverterFrame.this.encodingMessageLabel.setText("Encoding Failed!");
			//          ConverterFrame.this.convertButton.setEnabled(true);
			//        }
			//        else
			//        {
			//          ConverterFrame.this.encodingMessageLabel.setText("Encoding Complete!");
			//          ConverterFrame.this.convertButton.setEnabled(true);
			//        }
		}
		
		public void sourceInfo(MultimediaInfo m) {
		}
	}
	
}
