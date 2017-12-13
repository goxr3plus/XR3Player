package main.java.com.goxr3plus.xr3player.application.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class WelcomeScreen extends StackPane {
	
	//---------------------------------------------
	
	@FXML
	private MediaView mediaView;
	
	@FXML
	private VBox screen1;
	
	@FXML
	private JFXCheckBox showOnStartUp;
	
	@FXML
	private JFXToggleButton sound;
	
	@FXML
	private JFXButton startButton;
	
	@FXML
	private JFXButton exit;
	
	// -------------------------------------------------------------
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	private final String LOADING_SCREEN_PATH = getClass().getResource(InfoTool.VIDEOS + "lights.mp4").toString();
	private final String LOADING_SCREEN_SOUND = getClass().getResource(InfoTool.SOUNDS + "anonymous.mp3").toString();
	
	private MediaPlayer mediaPlayer;
	private MediaPlayer soundPlayer;
	
	/**
	 * Constructor
	 */
	public WelcomeScreen() {
		
		// ------------------------------------FXMLLOADER--------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "WelcomeScreen.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
			
		}
		
	}
	
	@FXML
	private void initialize() {
		
		//startButton
		startButton.setOnAction(a -> hideWelcomeScreen());
		
		//dontShowAgain
		showOnStartUp.selectedProperty()
				.addListener((observable , oldValue , newValue) -> Main.applicationProperties.updateProperty("Show-Welcome-Screen", String.valueOf(newValue.booleanValue())));
		
		//exit
		exit.setOnAction(a -> System.exit(0));
	}
	
	/**
	 * This method should be called only once!! Show the welcome screen
	 */
	public void showWelcomeScreen() {
		
		try {
			
			if (mediaPlayer != null) {
				mediaPlayer.play();
				soundPlayer.play();
			} else {
				
				mediaView.setFitWidth(Main.window.getWidth());
				mediaView.setFitHeight(Main.window.getHeight());
				mediaPlayer = new MediaPlayer(new Media(LOADING_SCREEN_PATH));
				mediaView.setMediaPlayer(mediaPlayer);
				mediaPlayer.setAutoPlay(true);
				mediaPlayer.setCycleCount(50);
				mediaPlayer.play();
				mediaPlayer.setAutoPlay(true);
				mediaPlayer.setCycleCount(50);
				
				//Start the stream player
				soundPlayer = new MediaPlayer(new Media(LOADING_SCREEN_SOUND));
				soundPlayer.muteProperty().bind(sound.selectedProperty().not());
				soundPlayer.play();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		setVisible(true);
	}
	
	/**
	 * Hide the welcome screen
	 */
	public void hideWelcomeScreen() {
		
		if (mediaPlayer != null) {
			mediaPlayer.dispose();
			soundPlayer.dispose();
		}
		
		setVisible(false);
	}
	
	/**
	 * @return the showOnStartUp
	 */
	public JFXCheckBox getShowOnStartUp() {
		return showOnStartUp;
	}
	
}
