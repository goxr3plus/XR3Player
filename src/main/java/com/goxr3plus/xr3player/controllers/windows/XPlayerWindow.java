/**
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author GOXR3PLUS
 *
 */
public class XPlayerWindow extends BorderPane {

	// -----------------------------------------------------------------------------

	@FXML
	private BorderPane topBar;

	@FXML
	private Label topLabel;

	@FXML
	private JFXButton maxOrNormalize;

	@FXML
	private StackedFontIcon sizeStackedFontIcon;

	@FXML
	private JFXButton closeWindow;

	// -----------------------------------------------------------------------------

	/**
	 * The Window
	 */
	private Stage window;

	private BorderlessScene borderlessScene;

	/**
	 * The XPlayer that the window is holding :)
	 */
	XPlayerController xPlayerController;

	/**
	 * Constructor
	 * 
	 * @param xPlayerController
	 */
	public XPlayerWindow(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;

		// Make the window
		window = new Stage();
		getWindow().setTitle("XPlayer Window");
		getWindow().getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		getWindow().setFullScreenExitHint("");
		getWindow().setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		getWindow().setOnCloseRequest(c -> close());

		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.PLAYERS_FXMLS + "XPlayerWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "XPlayerWindow FXML can't be loaded!", ex);
		}

	}

	/**
	 * Called as soon as .fxml has been loaded
	 */
	@FXML
	private void initialize() {

		// -- Scene
		borderlessScene = new BorderlessScene(window, StageStyle.TRANSPARENT, this, JavaFXTool.getScreenWidth() / 3,
				JavaFXTool.getScreenHeight() / 3);
		borderlessScene.setMoveControl(topBar);
		borderlessScene.getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		getWindow().setScene(borderlessScene);

		// -- Label
		topLabel.setText("Player " + (xPlayerController.getKey() + 1));

		// -- closeWindow
		closeWindow.setOnAction(a -> close());

		// -- maxOrNormalize
		maxOrNormalize.setOnAction(a -> borderlessScene.maximizeStage());
	}

	/**
	 * Shows the window with the appropriate XPlayer inside
	 */
	public void show() {
		setCenter(xPlayerController.getXPlayerStackPane());
		window.setIconified(false);
		window.show();

	}

	/**
	 * Closes the window and restores the XPlayer to it's originall position
	 */
	public void close() {
		window.setIconified(false);
		window.close();
		xPlayerController.restorePlayerStackPane();
	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

}
