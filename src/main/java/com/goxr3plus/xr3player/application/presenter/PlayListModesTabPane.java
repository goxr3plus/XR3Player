package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * 
 *
 * @author GOXR3PLUS
 */
public class PlayListModesTabPane extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private Tab onlineMusicTab;
	
	@FXML
	private Tab openedLibrariesTab;
	
	@FXML
	private Tab searchEverythingTab;
	
	@FXML
	private Tab dropBoxTab;
	
	@FXML
	private Tab emotionListsTab;
	
	@FXML
	private Tab downloadsTab;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Constructor.
	 */
	public PlayListModesTabPane() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "PlayListModesTabPane.fxml"));
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
		
		//
		onlineMusicTab.setContent(Main.onlineMusicController);
		//
		openedLibrariesTab.setContent(Main.libraryMode.openedLibrariesViewer);
		//
		emotionListsTab.setContent(new BorderPane(Main.emotionsTabPane));
		//
		searchEverythingTab.setContent(new BorderPane(Main.searchWindowSmartController));
		
		//Select the second Tab
		onlineMusicTab.getTabPane().getSelectionModel().select(1);
	}
	
	/**
	 * Select the tab with the given index in the TabPane
	 * 
	 * @param index
	 */
	public void selectTab(int index) {
		openedLibrariesTab.getTabPane().getSelectionModel().select(index);
		
		//In case of Search Window Tab  Request focus of Search Field
		//if (index == 2)
		//	Main.searchWindowSmartController.getSearchService().getSearchField().requestFocus();
	}
	
	/**
	 * @return the emotionListsTab
	 */
	public Tab getEmotionListsTab() {
		return emotionListsTab;
	}
	
	/**
	 * @return the searchEverythingTab
	 */
	public Tab getSearchEverythingTab() {
		return searchEverythingTab;
	}

	/**
	 * @return the openedLibrariesTab
	 */
	public Tab getOpenedLibrariesTab() {
		return openedLibrariesTab;
	}

	/**
	 * @return the dropBoxTab
	 */
	public Tab getDropBoxTab() {
		return dropBoxTab;
	}


	
}
