package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController.SettingsTab;
import com.goxr3plus.xr3player.controllers.windows.ConsoleWindowController.ConsoleTab;
import com.goxr3plus.xr3player.services.general.StoppableService;
import com.goxr3plus.xr3player.services.general.StoppableService.StoppableServiceCategory;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The BottomBar of the Application
 * 
 * @author GOXR3PLUS
 *
 */
public class BottomBar extends BorderPane {

	// --------------------------------------------------------------

	@FXML
	private ToggleButton barEnabledToggle;

	@FXML
	private FontIcon barEnabledFontIcon;

	@FXML
	private HBox hBox;

	@FXML
	private JFXToggleButton showHideSideBar;

	@FXML
	private JFXToggleButton keyBindings;

	@FXML
	private JFXToggleButton speechRecognitionToggle;

	@FXML
	private Label internetConnectionLabel;

	@FXML
	private Label internetConnectionDescriptionLabel;

	@FXML
	private Label currentTimeLabel;

	@FXML
	private Label runningTimeLabel;

	@FXML
	private JFXButton enableBar;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	public final StoppableService internetCheckerService = new StoppableService(
			StoppableServiceCategory.INTERNET_CHECKER);
	public final StoppableService timerCheckerService = new StoppableService(StoppableServiceCategory.TIMER_CHECKER);

	/**
	 * Constructor.
	 */
	public BottomBar() {

		FXMLLoader loader;
		loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "BottomBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		// keyBindingsLabel
		keyBindings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.SHORTCUTS));

		// SpeechRecognitionToggle
		speechRecognitionToggle.setOnAction(a -> Main.consoleWindow.showWindow(ConsoleTab.SPEECH_RECOGNITION));

		// showHideSideBar
		showHideSideBar.selectedProperty().addListener((observable, oldValue, newValue) -> Main.sideBar.toogleBar());

		// enableBar
		enableBar.setOnAction(a -> barEnabledToggle.setSelected(!barEnabledToggle.isSelected()));
		enableBar.visibleProperty().bind(barEnabledToggle.selectedProperty().not());

		// barEnabledToggle
		barEnabledToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				internetCheckerService.restart();
				timerCheckerService.restart();
				barEnabledFontIcon.setIconColor(Color.web("#d4ff00"));
			} else {
				internetCheckerService.cancel();
				timerCheckerService.cancel();
				barEnabledFontIcon.setIconColor(Color.web("#c95d4c"));
			}
		});
	}

	/**
	 * @return the keyBindings
	 */
	public JFXToggleButton getKeyBindings() {
		return keyBindings;
	}

	public JFXToggleButton getShowHideSideBar() {
		return showHideSideBar;
	}

	/**
	 * @return the speechRecognitionToggle
	 */
	public JFXToggleButton getSpeechRecognitionToggle() {
		return speechRecognitionToggle;
	}

	/**
	 * @return the internetConnectionLabel
	 */
	public Label getInternetConnectionLabel() {
		return internetConnectionLabel;
	}

	/**
	 * @return the currentTimeLabel
	 */
	public Label getCurrentTimeLabel() {
		return currentTimeLabel;
	}

	/**
	 * @return the runningTimeLabel
	 */
	public Label getRunningTimeLabel() {
		return runningTimeLabel;
	}

	/**
	 * @return the internetConnectionDescriptionLabel
	 */
	public Label getInternetConnectionDescriptionLabel() {
		return internetConnectionDescriptionLabel;
	}

}
