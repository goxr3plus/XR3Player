/*
 * 
 */
package com.goxr3plus.xr3player.controllers.chromium;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * The Class VisualizerWindow.
 *
 * @author GOXR3PLUS
 */
public class ChromiumFullScreenController extends StackPane {

	// -------------------------------------

	@FXML
	private StackPane browserPane;

	@FXML
	private HBox topBox;

	@FXML
	private Button exitFullScreen;

	// ------------------------------------

	private Scene scene;

	/** The window. */
	private Stage window;

	private BrowserView browserView;

	private WebBrowserTabController webBrowserTabController;

	/** The pause transition. */
	private PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));

	//private final EventHandler<MouseEvent> mouseMovingEvent = m -> restartPauseTransition();

	/**
	 * Constructor.
	 *
	 */
	public ChromiumFullScreenController() {

		window = new Stage();
		window.setTitle("Chromium Full Screen");
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		window.initStyle(StageStyle.UNDECORATED);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setAlwaysOnTop(false);
		window.setFullScreenExitHint("Press F11 to exit full screen");
		// window.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F11))
		window.setOnCloseRequest(c -> removeBrowserView());

		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.BROWSER_FXMLS + "WebBrowserFullScreenController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"ChromiumFullScreenController FXML can't be loaded!", ex);
		}

	}

	/**
	 * Called as soon as .fxml has been loaded
	 */
	@FXML
	private void initialize() {

		// -- Scene
		scene = new Scene(this);
		scene.setFill(Color.rgb(0, 0, 0));
		scene.getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());

		// -- KeyListeners
		scene.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.F11)
				removeBrowserView();
		});

		// PauseTransition
		pauseTransition.setOnFinished(f -> {
			// if (!topBar.isHover() && window.isShowing())
			// topBar.setVisible(false);
		});

		// ----------Drag && Drop Listeners
		window.setScene(scene);

	}

	/**
	 * Restarts the PauseTransition
	 */
	private void restartPauseTransition() {
		pauseTransition.playFromStart();
		topBox.setVisible(true);

		System.out.println("Restarted pause transition");
	}

	/**
	 * Pass a browserView instance and from which controller it came
	 * 
	 * @param browserView
	 */
	public void goFullScreenMode(BrowserView browserView, WebBrowserTabController webBrowserTabController) {
		this.browserView = browserView;
		this.webBrowserTabController = webBrowserTabController;

		// Add Event Handler
		// browserView.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovingEvent)

		// Set the browserView
		webBrowserTabController.getBorderPane().setCenter(null);
		browserPane.getChildren().add(browserView);
		browserView.toBack();
		browserPane.toBack();

		// Show the window on full screen
		window.setFullScreen(true);
		window.show();

		AlertTool.showNotification("Hint!", "Press F11 to exit full screen ", Duration.seconds(2),
				NotificationType.INFORMATION);
	}

	/**
	 * Restore the BrowserView back to it's original tab controller
	 */
	private void removeBrowserView() {

		// Remove Event Handler
		// this.browserView.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovingEvent)

		// Restore the browserView
		browserPane.getChildren().remove(null);
		webBrowserTabController.getBorderPane().setCenter(browserView);

		// Hide Window
		window.hide();

	}

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							GETTERS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Gets the stage.
	 *
	 * @return The Actual Stage of the Window
	 */
	public Stage getStage() {
		return window;
	}

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							Methods
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */

}
