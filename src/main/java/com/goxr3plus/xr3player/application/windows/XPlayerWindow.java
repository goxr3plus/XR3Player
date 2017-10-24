/**
 * 
 */
package main.java.com.goxr3plus.xr3player.application.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;

/**
 * @author GOXR3PLUS
 *
 */
public class XPlayerWindow extends BorderPane {
	
	// -----------------------------------------------------------------------------
	
	/**
	 * The Window
	 */
	private Stage window;
	
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
		
		//Make the window
		window = new Stage();
		getWindow().setTitle("XPlayer Window");
		getWindow().getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		getWindow().setFullScreenExitHint("");
		getWindow().setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		getWindow().setOnCloseRequest(c -> close());
		
		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "XPlayerWindow.fxml"));
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
		
		//	BorderlessScene scene = new BorderlessScene(window, StageStyle.TRANSPARENT, this, 150, 150)
		//	scene.setMoveControl(topBar)
		
		// -- Scene
		Scene scene = new Scene(this, InfoTool.getScreenWidth() / 3, InfoTool.getScreenHeight() / 3);
		scene.getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		getWindow().setScene(scene);
		
		// -- Window
		//getWindow().centerOnScreen()
		
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
