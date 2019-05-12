package com.goxr3plus.xr3player.controllers.settings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.jfoenix.controls.JFXCheckBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

/**
 * @author GOXR3PLUS
 *
 */
public class XPlayerSettingsController extends BorderPane {

	/** -----------------------------------------------------. */

	@FXML
	private JFXCheckBox startImmediately;

	@FXML
	private JFXCheckBox askSecurityQuestion;

	@FXML
	private Slider secondsToSkipSlider;

	@FXML
	private JFXCheckBox showPlayerNotifications;

	@FXML
	private JFXCheckBox allowDiscRotation;

	@FXML
	private JFXCheckBox showFPS;

	@FXML
	private Slider maxVisualizerFPSSlider;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Constructor.
	 */
	public XPlayerSettingsController() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SETTINGS_FXMLS + "XPlayerSettingsController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {

		// ShowFPS
		showFPS.setOnAction(a -> {

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayers-Visualizer-ShowFPS",
					String.valueOf(showFPS.isSelected()));

			// Update all the players
			Main.xPlayersList.getList()
					.forEach(xPlayerController -> xPlayerController.visualizer.setShowFPS(showFPS.isSelected()));

		});

		// StartImmediately
		startImmediately.selectedProperty().addListener(l -> Main.dbManager.getPropertiesDb()
				.updateProperty("XPlayers-General-StartAtOnce", String.valueOf(startImmediately.isSelected())));

		// AskSecurityQuestion
		askSecurityQuestion.selectedProperty().addListener(l -> Main.dbManager.getPropertiesDb().updateProperty(
				"XPlayers-General-AskSecurityQuestion", String.valueOf(askSecurityQuestion.isSelected())));

		// ShowPlayerNotifications
		showPlayerNotifications.selectedProperty().addListener(l -> Main.dbManager.getPropertiesDb().updateProperty(
				"XPlayers-General-ShowPlayerNotifications", String.valueOf(showPlayerNotifications.isSelected())));

		// SkipSlider
		secondsToSkipSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			// Change the values of skip buttons from each player
			Main.xPlayersList.getList().forEach(xPlayerController -> {
				xPlayerController.getBackwardButton().setText(Integer.toString(newValue.intValue()));
				xPlayerController.getForwardButton().setText(Integer.toString(newValue.intValue()));
			});
		});

		secondsToSkipSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayers-General-SkipButtonSeconds",
					Integer.toString((int) secondsToSkipSlider.getValue()));
		});

		// allowDiscRotation
		allowDiscRotation.selectedProperty().addListener(l -> {

			// Notify all the Players
			Main.xPlayersList.getList().forEach(XPlayerController::checkDiscRotation);

			//// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayers-General-AllowDiscRotation",
					String.valueOf(allowDiscRotation.isSelected()));
		});

	}

	/**
	 * Restores all the settings that have to do with the category of the class
	 */
	public void restoreSettings() {

		// ShowFPS
		showFPS.setSelected(false);

		// StartImmediately
		startImmediately.setSelected(true);

		// AskSecurityQuestion
		askSecurityQuestion.setSelected(true);

		// ShowPlayerNotifications
		showPlayerNotifications.setSelected(false);

		// SkipSlider
		secondsToSkipSlider.setValue(15);
	}

	/**
	 * @return the showFPS
	 */
	public JFXCheckBox getShowFPS() {
		return showFPS;
	}

	/**
	 * @return the startImmediately
	 */
	public JFXCheckBox getStartImmediately() {
		return startImmediately;
	}

	/**
	 * @return the askSecurityQuestion
	 */
	public JFXCheckBox getAskSecurityQuestion() {
		return askSecurityQuestion;
	}

	/**
	 * @return the skipSlider
	 */
	public Slider getSkipSlider() {
		return secondsToSkipSlider;
	}

	/**
	 * @return the showPlayerNotifications
	 */
	public JFXCheckBox getShowPlayerNotifications() {
		return showPlayerNotifications;
	}

	/**
	 * @return the allowDiscRotation
	 */
	public JFXCheckBox getAllowDiscRotation() {
		return allowDiscRotation;
	}

}
