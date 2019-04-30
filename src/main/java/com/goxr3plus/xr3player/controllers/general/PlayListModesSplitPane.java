/*
 * 
 */
package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.general.TopBar.WindowMode;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * Xm i must think what description to add here This shows the opened libraries
 * and the Media Information
 *
 * @author GOXR3PLUS
 */
public class PlayListModesSplitPane extends BorderPane {

	// --------------------------------------------------------------

	@FXML
	private SplitPane splitPane;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	// Variables
	private double[] splitPaneDivider = { 0.18, 0.83 };

	/**
	 * Constructor.
	 */
	public PlayListModesSplitPane() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "PlayListModesSplitPane.fxml"));
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

		// == splitPane
		splitPane.getItems().clear();
		splitPane.getItems().addAll(Main.treeManager, Main.playListModesTabPane, Main.mediaInformation);
		SplitPane.setResizableWithParent(Main.playListModesTabPane, Boolean.FALSE);
		SplitPane.setResizableWithParent(Main.mediaInformation, Boolean.FALSE);
		SplitPane.setResizableWithParent(Main.treeManager, Boolean.FALSE);
		updateSplitPaneDivider();
	}

	/**
	 * Updates the SplitPane DividerPositions
	 */
	public void updateSplitPaneDivider() {
		splitPane.setDividerPositions(splitPaneDivider);
	}

	/**
	 * Saves current divider positions of SplitPane into an array
	 */
	public void saveSplitPaneDivider() {
		splitPaneDivider = splitPane.getDividerPositions();
	}

	/**
	 * Reverse the position of SplitPane items using this method , it takes care of
	 * holding the position of dividers
	 */
	public void reverseSplitPaneItems() {
		// Reverse the divider positions
		double[] array = { 1.00 - splitPane.getDividerPositions()[0], splitPane.getDividerPositions()[0] };

		splitPane.getItems().clear();
		if (Main.topBar.getWindowMode() == WindowMode.MAINMODE)
			splitPane.getItems().addAll(Main.libraryMode.openedLibrariesViewer, Main.mediaInformation);
		else
			splitPane.getItems().addAll(Main.mediaInformation, Main.libraryMode.openedLibrariesViewer);

		// Set
		splitPane.setDividerPositions(array);
		// Save
		saveSplitPaneDivider();

	}

}
