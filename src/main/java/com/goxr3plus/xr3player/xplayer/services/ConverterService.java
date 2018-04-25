package main.java.com.goxr3plus.xr3player.xplayer.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;

/**
 * Used by XR3Player to convert all unsupported audio formats to .mp3
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class ConverterService extends Service<Boolean> {
	
	private String audioPath;
	
	private final XPlayerController xPlayerController;
	
	/**
	 * Constructor
	 */
	public ConverterService(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
	}
	
	/**
	 * Start the Service for the given file
	 * 
	 * @param audioPath
	 */
	public void start(String audioPath) {
		
		//Set the full audioPath
		this.audioPath = audioPath;
		
		//Restart the Service
		this.restart();
	}
	
	/**
	 * When the Service is done
	 */
	private void done() {
		// succeeded
		if (this.getValue()) {
			
			//failed
		} else {
			
		}
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				//				//Convert any audio format to .mp3
				//				try {
				//					File source = new File(name + extension);
				//					File target = new File("audio.mp3");
				//					AudioAttributes audio = new AudioAttributes();
				//					audio.setCodec("libmp3lame");
				//					audio.setBitRate(128000);
				//					audio.setChannels(2);
				//					audio.setSamplingRate(44100);
				//					EncodingAttributes attrs = new EncodingAttributes();
				//					attrs.setFormat("mp3");
				//					attrs.setAudioAttributes(audio);
				//					Encoder encoder = new Encoder();
				//					encoder.encode(source, target, attrs);
				//				} catch (Exception ex) {
				//					ex.printStackTrace();
				//					return false;
				//				}
				
				Thread.sleep(2000);
				
				return true;
			}
		};
	}
	
}
