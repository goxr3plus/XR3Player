package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;

import org.controlsfx.control.textfield.TextFields;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class ExportWindowFolderHBox extends HBox {

	// --------------------------------------------------------------

	@FXML
	private Button deleteBoxButton;

	@FXML
	private Button pickFolderButton;

	// -------------------------------------------------------------

	private final TextField textField = TextFields.createClearableTextField();

	/**
	 * Constructor.
	 */
	public ExportWindowFolderHBox() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.WINDOW_FXMLS + "ExportWindowFolderHBox.fxml"));
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

		// TextField
		textField.getStyleClass().add("dark-text-field-rectangle");
		textField.setPromptText("pick folder to export...");
		textField.setFocusTraversable(false);
		textField.setEditable(false);
		textField.setDisable(true);
		textField.setMinWidth(0);
		textField.setMaxHeight(0);
		HBox.setHgrow(textField, Priority.ALWAYS);
		textField.setMaxWidth(Integer.MAX_VALUE);

		getChildren().add(0, textField);
	}

	/**
	 * @return the pickFolderButton
	 */
	public Button getPickFolderButton() {
		return pickFolderButton;
	}

	/**
	 * @param pickFolderButton the pickFolderButton to set
	 */
	public void setPickFolderButton(Button pickFolderButton) {
		this.pickFolderButton = pickFolderButton;
	}

	/**
	 * @return the textField
	 */
	public TextField getTextField() {
		return textField;
	}

	/**
	 * @return the deleteBoxButton
	 */
	public Button getDeleteBoxButton() {
		return deleteBoxButton;
	}

}
