package main.java.com.goxr3plus.xr3player.xplayer.microphone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerModel.DisplayMode;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.presenter.MicrophoneVisualizer;

/**
 * A basic example to analyze the pcm data of microphone and visualize it
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class MicrophonePCMAnalyzerTest extends Application {
	
	private final Microphone microphone = new Microphone(Type.WAVE);
	private AudioInputStream audio = null;
	private boolean stopSpeechRecognition;
	MicrophoneVisualizer visualizer = new MicrophoneVisualizer();
	
	BorderPane borderPane = new BorderPane();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//BorderPane
		borderPane.setCenter(visualizer);
		
		//Stage
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		primaryStage.setTitle("GOXR3PLUS STUDIO ( DE BRUDA )");
		
		//Scene
		primaryStage.setScene(new Scene(borderPane));
		primaryStage.setOnCloseRequest(c -> {
			System.exit(-1);
		});
		
		primaryStage.show();
		
		//Check if Microphone is Supported
		checkMicrophoneAvailability();
		
		//Print available mixers
		//printAvailableMixers();
		
		//Capture Microphone Audio Data	
		microphone.initTargetDataLine();
		microphone.open();
		
		//Audio Input Stream
		audio = new AudioInputStream(microphone.getTargetDataLine());
		
		//Infinity loop from microphone
		new Thread(() -> {
			int nBytesRead = 0;
			byte[] trimBuffer;
			int audioDataLength = 1024;
			ByteBuffer audioDataBuffer = ByteBuffer.allocate(audioDataLength);
			audioDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			//Go inside loop
			while (!stopSpeechRecognition) {
				//byte[] data = new byte[1024];
				
				try {
					
					// System.out.println("Inside Stream Player Run method")
					int toRead = audioDataLength;
					int totalRead = 0;
					
					// Reads up a specified maximum number of bytes from audio stream 	
					//wtf i have written here xaxaxoaxoao omg //to fix! cause it is complicated
					for (; toRead > 0 && ( nBytesRead = audio.read(audioDataBuffer.array(), totalRead, toRead) ) != -1; toRead -= nBytesRead, totalRead += nBytesRead) {
					}
					
					// Check for under run
					//if (audio.available() >= audio.getBufferSize())
					//	logger.info(() -> "Underrun> Available=" + sourceDataLine.available() + " , SourceDataLineBuffer=" + sourceDataLine.getBufferSize());
					
					//Check if anything has been read
//					if (totalRead > 0) {
//						trimBuffer = audioDataBuffer.array();
//						if (totalRead < trimBuffer.length) {
//							trimBuffer = new byte[totalRead];
//							//Copies an array from the specified source array, beginning at the specified position, to the specified position of the destination array
//							// The number of components copied is equal to the length argument. 
//							System.arraycopy(audioDataBuffer.array(), 0, trimBuffer, 0, totalRead);
//						}
						
						//Write PCM data
						visualizer.writeDSP(audioDataBuffer.array());
						System.err.println(totalRead);
				//	}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
		
		//Visualizer
		visualizer.setDisplayMode(Integer.parseInt(DisplayMode.CIRCLE_WITH_LINES.toString()));
		visualizer.setupDSP(microphone.getTargetDataLine());
		visualizer.startDSP(microphone.getTargetDataLine());
		visualizer.startVisualizer();
	}
	
	/**
	 * Checks if the Microphone is available
	 */
	public static void checkMicrophoneAvailability() {
		System.out.println("Available Microphones : [ " + enumerateMicrophones().size() + " ]");
		
		enumerateMicrophones().forEach((mixer , info) -> System.out
				.println("\nName : " + mixer.getName() + " , Description : " + mixer.getDescription() + " , Vendor : " + mixer.getVendor() + " , Version : " + mixer.getVersion()));
	}
	
	/**
	 * Generates a hashmap to simplify the microphone selection process. The keyset is the name of the audio device's Mixer The value is the first
	 * lineInfo from that Mixer.
	 * 
	 * @return The generated hashmap
	 */
	public static HashMap<Mixer.Info,Line.Info> enumerateMicrophones() {
		HashMap<Mixer.Info,Line.Info> out = new HashMap<>();
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos) {
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] lineInfos = m.getTargetLineInfo();
			if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class))//Only adds to hashmap if it is audio input device
				out.put(info, lineInfos[0]);//Please enjoy my pun
		}
		return out;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
