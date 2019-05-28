package com.goxr3plus.xr3player.controllers.xplayer;

import java.io.IOException;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class MixTabInterface extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private BorderPane borderPane;

	@FXML
	private JFXButton balanceButton;

	@FXML
	private MenuButton sync1;

	@FXML
	private MenuButton sync2;

	@FXML
	private ProgressBar volumeProgress1;

	@FXML
	private ProgressBar volumeProgress2;

	@FXML
	private Slider masterVolumeSlider;

	@FXML
	private HBox centerHBox;

	// -------------------------------------------------------------

	public MixTabInterface() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.PLAYERS_FXMLS + "MixTabInterface.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as fxml is initialized
	 */
	@FXML
	private void initialize() {

		// balanceButton
		balanceButton.setOnAction(a -> masterVolumeSlider.setValue(masterVolumeSlider.getMax() / 2));

		// masterVolumeSlider
		masterVolumeSlider.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> calculateBars());
		masterVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			calculateBars();

			// Calculate Volume
			Main.xPlayersList.getXPlayerController(1).controlVolume();
		});
	}

	/**
	 * Calculate bars positioning
	 */
	private void calculateBars() {

		// Variables
		double value = masterVolumeSlider.getValue();
		double half = masterVolumeSlider.getMax() / 2;
		double masterVolumeSliderWidth = masterVolumeSlider.getWidth();

		// Progress Max1
		volumeProgress1.setProgress(1);
		volumeProgress2.setProgress(1);

		// Below is mind tricks
		if ((int) value == (int) half) {
			volumeProgress1.setMinWidth(masterVolumeSliderWidth / 2);
			volumeProgress2.setMinWidth(masterVolumeSliderWidth / 2);
		} else if (value < half) {
			double progress = 1.0 - (value / half);
			double minimumWidth = masterVolumeSlider.getWidth() / 2 + (masterVolumeSlider.getWidth() / 2) * (progress);
			volumeProgress1.setMinWidth(masterVolumeSliderWidth - minimumWidth);
			volumeProgress1.setMaxWidth(masterVolumeSliderWidth - minimumWidth);
			volumeProgress2.setMinWidth(minimumWidth);
		} else if (value > half) {
			double progress = (value - half) / half;
			double minimumWidth = masterVolumeSlider.getWidth() / 2 + (masterVolumeSlider.getWidth() / 2) * (progress);
			volumeProgress1.setMinWidth(minimumWidth);
			volumeProgress2.setMinWidth(masterVolumeSliderWidth - minimumWidth);
			volumeProgress2.setMaxWidth(masterVolumeSliderWidth - minimumWidth);
		}

		// Syncronize Button
		prepareSynchonizeButtons(sync1, 2, 1);
		prepareSynchonizeButtons(sync2, 1, 2);
	}

	/**
	 * Fast method to avoid duplicate code
	 */
	private void prepareSynchonizeButtons(MenuButton button, int masterKey, int slaveKey) {
		button.getItems().get(0).setOnAction(a -> Main.xPlayersList.getXPlayerController(slaveKey)
				.setSpeed(Main.xPlayersList.getXPlayerController(masterKey).getSpeed()));
		button.getItems().get(1).setOnAction(a -> Main.xPlayersList.getXPlayerController(slaveKey)
				.setVolume(Main.xPlayersList.getXPlayerController(masterKey).getVolume()));
		button.getItems().get(2).setOnAction(a -> {
			Main.xPlayersList.getXPlayerController(slaveKey)
					.setVolume(Main.xPlayersList.getXPlayerController(masterKey).getVolume());
			Main.xPlayersList.getXPlayerController(slaveKey)
					.setSpeed(Main.xPlayersList.getXPlayerController(masterKey).getSpeed());
		});
	}

	/**
	 * @return the borderPane
	 */
	public BorderPane getBorderPane() {
		return borderPane;
	}

	/**
	 * @return the masterVolumeSlider
	 */
	public Slider getMasterVolumeSlider() {
		return masterVolumeSlider;
	}

	/**
	 * @return the centerHBox
	 */
	public HBox getCenterHBox() {
		return centerHBox;
	}

}
