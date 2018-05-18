package main.java.com.goxr3plus.xr3player.application.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class PictureWindowController extends StackPane{
	
	//--------------------------------------------------------
	

	
	//--------------------------------------------------------
	

	
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/** The Window */
	private Stage window = new Stage();
	
	
	/**
	 * Constructor
	 */
	public PictureWindowController() {
		
		// ------------------------------------FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "PictureWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
		window.setTitle("XR3Player Console");
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().setOnKeyReleased(k -> {
			if (k.getCode() == KeyCode.ESCAPE)
				window.close();
		});
	}
	
	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {
		
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	

	
	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}
	
	/**
	 * Show the Window
	 */
	public void show() {
		if (!window.isShowing())
			window.show();
		else
			window.requestFocus();
	}
	

	
	
}
