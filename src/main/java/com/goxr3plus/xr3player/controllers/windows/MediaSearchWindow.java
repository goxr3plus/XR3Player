/*
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * The Class LibrariesSearchWindow.
 */
public class MediaSearchWindow extends BorderPane {

	// -------------------------------------------------------------

	/** The stage. */
	private Stage window;

	private BorderlessScene borderlessScene;

	// -------------------------------------------------------------

	@FXML
	private JFXButton close;

	// -------------------------------------------------------------

	/**
	 * Constructor
	 */
	public MediaSearchWindow() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.WINDOW_FXMLS + "MediaSearchWindow.fxml"));
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

		// Root
		setCenter(Main.searchWindowSmartController);

		// Window
		window = new Stage();
		borderlessScene = new BorderlessScene(window, StageStyle.UNDECORATED, this, 400, 300);
		borderlessScene.getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		borderlessScene.setTransparentWindowStyle(
				"-fx-background-color:rgb(0,0,0,0.7); -fx-border-color:firebrick; -fx-border-width:2px;");
		window.setScene(borderlessScene);
		window.setWidth(800);
		window.setHeight(450);
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
	 * Recalculates the position and shows the window
	 */
	public void recalculateAndshow(Node searchField) {
		try {
			window.show();
			recalculateWindowPosition(searchField);
			// window.requestFocus()
			// System.out.println("X: "+window.getX() + " Y: " + window.getY())
			// System.out.println("Width: "+window.getWidth() + " Y: " + window.getY())
			// System.out.println("Window Showing : " + window.isShowing())
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		window.initOwner(owner);
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

}
