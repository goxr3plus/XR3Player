package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * 
 *
 * @author GOXR3PLUS
 */
public class PlayListModesTabPane extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private Tab onlineMusicTab;

	@FXML
	private Tab openedLibrariesTab;

	@FXML
	private Tab dropBoxTab;

	@FXML
	private Tab emotionListsTab;

	@FXML
	private Tab downloadsTab;

	@FXML
	private Tab dropBoxDownloadsTab;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Constructor.
	 */
	public PlayListModesTabPane() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
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

		// Select openedLibrariesTab
		onlineMusicTab.getTabPane().getSelectionModel().select(openedLibrariesTab);
	}

	/**
	 * Select the tab with the given index in the TabPane
	 * 
	 * @param index
	 */
	public void selectTab(int index) {
		openedLibrariesTab.getTabPane().getSelectionModel().select(index);

	}

	/**
	 * @return the emotionListsTab
	 */
	public Tab getEmotionListsTab() {
		return emotionListsTab;
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

	/**
	 * @return the dropBoxDownloadsTab
	 */
	public Tab getDropBoxDownloadsTab() {
		return dropBoxDownloadsTab;
	}

}
