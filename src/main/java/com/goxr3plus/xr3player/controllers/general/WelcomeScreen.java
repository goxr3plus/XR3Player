package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.application.MainExit;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class WelcomeScreen extends StackPane {

	// ---------------------------------------------

	@FXML
	private ImageView backgroundImage;

	@FXML
	private HBox topHBox;

	@FXML
	private CheckBox showOnStartUp;

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

		// startButton
		startButton.setOnAction(a -> {

			// Unbind
			unBindBackgroundImageView();

			// Hide Welcome Screen
			hideWelcomeScreen();

		});

		// dontShowAgain
		showOnStartUp.selectedProperty().addListener((observable, oldValue, newValue) -> Main.applicationProperties
				.updateProperty("Show-Welcome-Screen", String.valueOf(newValue.booleanValue())));

		// exit
		exit.setOnAction(a -> MainExit.terminateXR3Player(0));

		// mediaView
		// mediaView.setVisible(false)

		// backgroundImage
		backgroundImage.fitWidthProperty().bind(this.widthProperty());
		backgroundImage.fitHeightProperty().bind(this.heightProperty());

	}

	/**
	 * Un-bind backgroundImage
	 */
	private void unBindBackgroundImageView() {
		backgroundImage.fitWidthProperty().unbind();
		backgroundImage.fitHeightProperty().unbind();
	}

	/**
	 * This method should be called only once!! Show the welcome screen
	 */
	public void showWelcomeScreen() {

		// try {
		//
		// if (mediaPlayer != null && soundPlayer != null) {
		// //mediaPlayer.play();
		// soundPlayer.play();
		// } else {
		//
		// // mediaView.setFitWidth(Main.window.getWidth());
		// // mediaView.setFitHeight(Main.window.getHeight());
		// // mediaPlayer = new MediaPlayer(new
		// Media(getClass().getResource(InfoTool.VIDEOS +
		// "lights.mp4").toURI().toString()));
		// // mediaView.setMediaPlayer(mediaPlayer);
		// // mediaPlayer.setAutoPlay(true);
		// // //mediaPlayer.setRate(3.0)
		// // mediaPlayer.setStartTime(Duration.seconds(0));
		// // mediaPlayer.setStopTime(Duration.seconds(8));
		// // //mediaPlayer.setCycleCount(50)
		// // mediaPlayer.play();
		// // mediaPlayer.setAutoPlay(true);
		// // //mediaPlayer.setCycleCount(50)
		// // mediaPlayer.setOnEndOfMedia(() -> mediaView.setVisible(false));
		//
		// Media m1 = new Media(getClass().getResource(InfoTool.SOUNDS +
		// "anonymous.mp3").toURI().toString());
		// soundPlayer = new MediaPlayer(m1);
		// soundPlayer.muteProperty().bind(sound.selectedProperty().not());
		// soundPlayer.play();
		//
		// //Set the background Image
		// //setBackground(new Background(new
		// BackgroundImage(InfoTool.getImageFromResourcesFolder("application_background.jpg"),
		// BackgroundRepeat.NO_REPEAT,
		// // BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new
		// BackgroundSize(Main.window.getWidth(), Main.window.getHeight(), true, true,
		// true, true))));
		// }
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }

		setVisible(true);
	}

	/**
	 * Hide the welcome screen
	 */
	public void hideWelcomeScreen() {

		if (mediaPlayer != null) {
			mediaPlayer.dispose();
		}

		if (soundPlayer != null) {
			soundPlayer.dispose();
		}

		// Load the informations about every user
		Main.loginMode.usersLoaderService.start();

		setVisible(false);

	}

	/**
	 * @return the showOnStartUp
	 */
	public CheckBox getShowOnStartUp() {
		return showOnStartUp;
	}

	/**
	 * @return the versionLabel
	 */
	public Label getVersionLabel() {
		return versionLabel;
	}

	/**
	 * @param versionLabel the versionLabel to set
	 */
	public void setVersionLabel(Label versionLabel) {
		this.versionLabel = versionLabel;
	}

	/**
	 * @return the topHBox
	 */
	public HBox getTopHBox() {
		return topHBox;
	}

}
