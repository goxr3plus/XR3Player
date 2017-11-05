package main.java.com.goxr3plus.xr3player.smartcontroller.tags.mp3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class ID3v1 extends StackPane {
	
	//--------------------------------------------------------------
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Constructor.
	 */
	public ID3v1() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "ID3v1.fxml"));
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
	
}
