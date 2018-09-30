package main.java.com.goxr3plus.xr3player.airobot;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.jfoenix.controls.JFXToggleButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.general.InfoTool;
import net.sourceforge.javaflacencoder.FLACFileWriter;

public class SpeechRecognition extends StackPane implements GSpeechResponseListener {
	
	//--------------------------------------------------------------
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private JFXToggleButton activateSpeechRecognition;
	
	@FXML
	private Button close;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Inline CSS Text Area which will contain the output of SpeechRecognition
	 */
	private final InlineCssTextArea cssTextArea = new InlineCssTextArea();
	
	//----------------------------------------------------------
	
	//Microphone
	private Microphone mic;
	
	//GSpeechDuplex for Speech Recognition
	GSpeechDuplex duplex;
	
	private final String style = "-fx-font-weight:bold; -fx-font-size:14; -fx-fill:white;";
	
	//----------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public SpeechRecognition() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SpeechRecognition.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
		//Find a microphone
		detectAvailableMicrophone();
		
	}
	
	/**
	 * Retries to find a Microphone to start Speech Recognition
	 */
	public void detectAvailableMicrophone() {
		try {
			//Microphone
			if (mic != null)
				mic.close();
			
			mic = new Microphone(FLACFileWriter.FLAC);
			
			//Duplex
			if (duplex != null)
				duplex.stopSpeechRecognition();
			
			duplex = new GSpeechDuplex("AIzaSyDIGjsPzpRkx2iHW04K_DJpDakyt_vniEE");
			duplex.setLanguage("en");
			duplex.addResponseListener(this);
			
		} catch (Exception ex) {
			//ex.printStackTrace();
			System.err.println("No Microphone available");
			this.setDisable(true);
		}
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//-- cssTextArea	
		cssTextArea.setEditable(false);
		cssTextArea.setFocusTraversable(false);
		cssTextArea.getStyleClass().add("inline-css-text-area");
		
		//-- VirtualizedScrollPane
		VirtualizedScrollPane<InlineCssTextArea> vsPane2 = new VirtualizedScrollPane<>(cssTextArea);
		vsPane2.setMaxWidth(Double.MAX_VALUE);
		vsPane2.setMaxHeight(Double.MAX_VALUE);
		
		//Set Center
		borderPane.setCenter(vsPane2);
		
		//close
		close.setOnAction(a -> Main.consoleWindow.getWindow().close());
		
		//activateSpeechRecognition
		activateSpeechRecognition.selectedProperty().addListener(l -> {
			
			//Check the Microphone
			if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
				
				//Selected?
				if (activateSpeechRecognition.isSelected()) {
					activateSpeechRecognition.setText("Stop Speech Recognition");
					
					//Start the Thread
					new Thread(() -> {
						try {
							duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
					}).start();
					
					String text = "Starting Speech Recognition , wait 2 seconds... \n";
					cssTextArea.appendText(text);
					cssTextArea.setStyle(cssTextArea.getText().length() - text.length(), cssTextArea.getLength() - 1, style.replace("white", "#329CFF"));
					
					//Follow the Caret
					cssTextArea.moveTo(cssTextArea.getLength());
					cssTextArea.requestFollowCaret();
					
					//VERY OBSOLETE WAY TO NOTIFY USER AFTER 2 SECONDS THAT HE CAN START SPEAKING...
					//THIS WILL BE CHANGED SOON!
					new Thread(() -> {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						//Run it on JavaFX Thread
						Platform.runLater(() -> {
							if (activateSpeechRecognition.isSelected()) {
								String text2 = "Hearing you ..... \n";
								cssTextArea.appendText(text2);
								cssTextArea.setStyle(cssTextArea.getText().length() - text2.length(), cssTextArea.getLength() - 1, style.replace("white", "firebrick"));
								
								//Follow the Caret
								cssTextArea.moveTo(cssTextArea.getLength());
								cssTextArea.requestFollowCaret();
							}
						});
					}).start();
					
					//Turn off speech Recognition
				} else {
					mic.close();
					duplex.stopSpeechRecognition();
					
					String text = "Speech Recognition stopped \n";
					cssTextArea.appendText(text);
					cssTextArea.setStyle(cssTextArea.getText().length() - text.length(), cssTextArea.getLength() - 1, style.replace("white", "#329CFF"));
					
					//Follow the Caret
					cssTextArea.moveTo(cssTextArea.getLength());
					cssTextArea.requestFollowCaret();
					
					activateSpeechRecognition.setText("Start Speech Recognition");
				}
			}
		});
		
		//Append this help text
		String text = "--Google Speech Recognition Demostration only ....--\n ";
		cssTextArea.appendText(text);
		cssTextArea.setStyle(cssTextArea.getText().length() - text.length(), cssTextArea.getLength() - 1, style.replace("white", "orange"));
		
	}
	
	@Override
	public void onResponse(GoogleResponse googleResponse) {
		String[] output = { "" };
		
		//Get the response from Google Cloud
		output[0] = googleResponse.getResponse();
		
		//Check the output
		if (output != null) {
			
			//Run of JavaFX Thread
			Platform.runLater(() -> {
				
				String text = "Heard -> ";
				cssTextArea.appendText(text);
				cssTextArea.setStyle(cssTextArea.getText().length() - text.length(), cssTextArea.getLength() - 1, style.replace("white", "FFA500"));
				
				cssTextArea.appendText(output[0] + "\n");
				cssTextArea.setStyle(cssTextArea.getText().length() - output[0].length() - 1, cssTextArea.getLength() - 1, style);
				
				//Follow the Caret
				cssTextArea.moveTo(cssTextArea.getLength());
				cssTextArea.requestFollowCaret();
			});
			
			//Print to Console
			System.out.println(output[0]);
			
		}
	}
	
	/**
	 * @return the activateSpeechRecognition
	 */
	public JFXToggleButton getActivateSpeechRecognition() {
		return activateSpeechRecognition;
	}
	
}
