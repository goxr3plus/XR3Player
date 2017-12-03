package main.java.com.goxr3plus.xr3player.smartcontroller.modes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;
import main.java.com.goxr3plus.xr3player.smartcontroller.services.ArtistsModeService;

public class SmartControllerArtistsMode extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private ListView<String> listView;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private VBox indicatorVBox;
	
	@FXML
	private ProgressIndicator progressIndicator;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// -------------------------------------------------------------
	
	/** A private instance of the SmartController it belongs */
	private final SmartController smartController;
	
	// -------------------------------------------------------------
	
	private final ArtistsModeService artistsService = new ArtistsModeService(this);
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public SmartControllerArtistsMode(SmartController smartController) {
		this.smartController = smartController;
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SmartControllerArtistsMode.fxml"));
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
		
		//indicatorVBox
		indicatorVBox.visibleProperty().bind(artistsService.runningProperty());
		
		//progressIndicator
		progressIndicator.progressProperty().bind(artistsService.progressProperty());
		
	}
	
	/**
	 * Refreshes the whole artists mode [ Heavy procedure for many files ]
	 */
	public void refreshArtistsMode() {
		artistsService.restart();
	}
	
	private void refreshTabledView(String artist) {
		
	}
	
	/**
	 * @return the smartController
	 */
	public SmartController getSmartController() {
		return smartController;
	}
	
	/**
	 * @return the listView
	 */
	public ListView<String> getListView() {
		return listView;
	}
	
}
