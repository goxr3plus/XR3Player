package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * This class is an class FXML Prototype
 *
 * @author GOXR3PLUS
 */
public class MediaDeleteWindow extends BorderPane {

	// --------------------------------------------------------------

	@FXML
	private ToggleGroup group;

	@FXML
	private Label mediaNameLabel;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Constructor.
	 */
	public MediaDeleteWindow() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "MediaDeleteWindow.fxml"));
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

	}

	/**
	 * Delete confirmation.
	 *
	 * @param permanent     Select the appropriate radio button based on if the
	 *                      delete is permanent or not
	 * @param text          Text to display on the Window
	 * @param numberOfItems The numbers of items that are going to be deleted
	 * @return an ArrayList <br>
	 *         The first argument is <b>True</b> if the user accepted to do any
	 *         delete or <b>False</b> either way<br>
	 *         The second argument <b>True</b> is if the delete (just from the list)
	 *         or <b>False</b> if the delete is permanent (from the computer)
	 * 
	 */
	public List<Boolean> doDeleteQuestion(boolean permanent, String text, int numberOfItems, Stage window) {
		final List<Boolean> arrayList = Arrays.asList(false, false);
		JavaFXTool.selectToggleOnIndex(group, permanent ? 1 : 0);

		// Create the Alert
		Alert alert = JavaFXTool.createAlert(null, "", "", AlertType.CONFIRMATION, StageStyle.UTILITY, window, null);
		alert.getDialogPane().setContent(this);
		mediaNameLabel.setText((numberOfItems <= 1 ? "[" + text : "Selected [" + text + " items") + "]");

		// LookUpButton
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setDefaultButton(false);
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);
		alert.showAndWait().ifPresent(answer -> {
			if (answer == ButtonType.OK) {
				arrayList.set(0, true);
				arrayList.set(1, JavaFXTool.getIndexOfSelectedToggle(group) == 1);
			}

		});

		return arrayList;
	}

}
