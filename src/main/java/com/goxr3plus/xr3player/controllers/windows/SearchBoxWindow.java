/*
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * The Class LibrariesSearchWindow.
 */
public class SearchBoxWindow extends BorderPane {

	// -------------------------------------------------------------

	/** The stage. */
	private Stage window;

	/** The scroll pane. */
	@FXML
	private ScrollPane scrollPane;

	/** The tile pane. */
	@FXML
	private TilePane tilePane;

	/** The results label. */
	@FXML
	private Label resultsLabel;

	/** The close. */
	@FXML
	private JFXButton close;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	// -------------------------------------------------------------

	/**
	 * Constructor
	 */
	public SearchBoxWindow() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.WINDOW_FXMLS + "SearchBoxWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	/**
	 * Called after the FXML layout is loaded.
	 */
	@FXML
	public void initialize() {

		// Window
		window = new Stage();
		window.initStyle(StageStyle.TRANSPARENT);
		window.setScene(new Scene(this, Color.TRANSPARENT));
		window.getScene().getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		window.getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				close();
		});

		// close
		close.setOnAction(a -> window.close());
	}

	/**
	 * Closes the window
	 */
	public void close() {
		window.close();
	}

	/**
	 * Shows the window
	 */
	public void show() {
		window.show();
	}

	/**
	 * Clears all the items from the TilePane.
	 */
	public void clearItems() {
		tilePane.getChildren().clear();
	}

	/**
	 * Adds the children.
	 * 
	 * @param text
	 * @param e
	 *
	 */
	public void addItem(String text, EventHandler<ActionEvent> e) {
		tilePane.getChildren().add(new ResultButton(text, e));
	}

	/**
	 * Changes the text of the Top Label of the Window.
	 *
	 * @param text the new label text
	 */
	public void setLabelText(String text) {
		resultsLabel.setText(text);
	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

	/**
	 * This method registers some listeners to the main window so when main windows
	 * changes his size or position then the Search Window recalculates it's
	 * position.
	 * 
	 * @param owner
	 * @param searchField
	 */
	public void registerListeners(Window owner, Node searchField) {
		// Care so the Search Window is recalculating it's position
		Main.window.xProperty().addListener((observable, oldValue, newValue) -> recalculateWindowPosition(searchField));
		Main.window.yProperty().addListener((observable, oldValue, newValue) -> recalculateWindowPosition(searchField));
		Main.window.widthProperty()
				.addListener((observable, oldValue, newValue) -> recalculateWindowPosition(searchField));
		Main.window.heightProperty()
				.addListener((observable, oldValue, newValue) -> recalculateWindowPosition(searchField));
		this.window.initOwner(owner);
	}

	/**
	 * Recalculate window position.
	 */
	public void recalculateWindowPosition(Node searchField) {
		if (!window.isShowing())
			return;

		Bounds bounds = searchField.localToScreen(searchField.getBoundsInLocal());
		window.setX(bounds.getMinX());
		// Check here so the window doesn't go below screen height
		window.setY(
				(window.getHeight() + bounds.getMaxY() + 10 < JavaFXTool.getVisualScreenHeight()) ? bounds.getMaxY() + 10
						: bounds.getMinY() - window.getHeight() - 10);
	}

	/**
	 * This button is added as a choice item into the LibrariesSearchWindow.
	 *
	 * @author GOXR3PLUS
	 */
	private class ResultButton extends Button {

		/**
		 * Constructor.
		 *
		 * @param text   the text
		 * @param action
		 */
		public ResultButton(String text, EventHandler<ActionEvent> action) {
			getStyleClass().add("search-box-window-item");
			setPrefSize(window.getWidth() - 25, 30);
			setText(text);
			setOnAction(action);
		}

	}

}
