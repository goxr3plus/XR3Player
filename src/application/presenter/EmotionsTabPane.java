package application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;

public class EmotionsTabPane extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private Tab hateTab;
	
	@FXML
	private Tab dislikeTab;
	
	@FXML
	private Tab likeTab;
	
	@FXML
	private Tab loveTab;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Constructor.
	 */
	public EmotionsTabPane() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "EmotionsTabPane.fxml"));
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
	 * Select the tab with the given index in the TabPane
	 * 
	 * @param index
	 */
	public void selectTab(int index) {
		hateTab.getTabPane().getSelectionModel().select(index);
	}
	
}
