package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.Util;

/**
 * @author GOXR3PLUS
 *
 */
public class WelcomeScreen extends StackPane {
	
	//---------------------------------------------
	
	@FXML
	private MediaView mediaView;
	
	@FXML
	private JFXCheckBox showOnStartUp;
	
	@FXML
	private JFXToggleButton sound;
	
	@FXML
	private Label versionLabel;
	
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
		exit.setOnAction(a -> Util.terminateXR3Player(0));
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
				//mediaPlayer.setRate(3.0)
				mediaPlayer.setStartTime(Duration.seconds(0));
				mediaPlayer.setStopTime(Duration.seconds(8));
				//mediaPlayer.setCycleCount(50)
				mediaPlayer.play();
				mediaPlayer.setAutoPlay(true);
				//mediaPlayer.setCycleCount(50)
				mediaPlayer.setOnEndOfMedia(() -> mediaView.setVisible(false));
				
				//Start the stream player
				soundPlayer = new MediaPlayer(new Media(LOADING_SCREEN_SOUND));
				soundPlayer.muteProperty().bind(sound.selectedProperty().not());
				soundPlayer.play();
				
				
				//Set the background Image
				setBackground(new Background(new BackgroundImage(InfoTool.getImageFromResourcesFolder("application_background.jpg"), BackgroundRepeat.NO_REPEAT,
						BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(Main.window.getWidth(), Main.window.getHeight(), true, true, true, true))));
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
	
	/**
	 * @return the versionLabel
	 */
	public Label getVersionLabel() {
		return versionLabel;
	}
	
	/**
	 * @param versionLabel
	 *            the versionLabel to set
	 */
	public void setVersionLabel(Label versionLabel) {
		this.versionLabel = versionLabel;
	}
	
}
