package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class BasicFXMLController {
	
	//--------------------------------------------------------------
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public BasicFXMLController() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "name.fxml"));
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
		
	}
	
}
