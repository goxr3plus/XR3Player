/*
 * 
 */
package application.librarymode;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.presenter.TopBar.WindowMode;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

/**
 * Xm i must think what description to add here This shows the opened libraries and the Media Information
 *
 * @author GOXR3PLUS
 */
public class MultipleTabs extends BorderPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private SplitPane splitPane;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// Variables
	private double[] splitPaneDivider = { 0.4 , 0.6 };
	
	/**
	 * Constructor.
	 */
	public MultipleTabs() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MultipleTabs.fxml"));
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
		
		//== tabPane
		//tabPane.getSelectionModel().select(0);
		
		//== tab 1
		//tab1.getStyleClass().add("sTab")
		//tab1.setContent(Main.libraryMode.multipleLibs);
		
		//== splitPane
		splitPane.getItems().clear();
		splitPane.getItems().addAll(Main.mediaInformation, Main.libraryMode.multipleLibs);
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
	 * Reverse the position of SplitPane items using this method , it takes care of holding the position of dividers
	 */
	public void reverseSplitPaneItems() {
		//Reverse the divider positions
		double[] array = { 1.00 - splitPane.getDividerPositions()[0] , splitPane.getDividerPositions()[0] };
		
		splitPane.getItems().clear();
		if (Main.topBar.getWindowMode() == WindowMode.MAINMODE)
			splitPane.getItems().addAll(Main.libraryMode.multipleLibs, Main.mediaInformation);
		else
			splitPane.getItems().addAll(Main.mediaInformation, Main.libraryMode.multipleLibs);
		
		//Set
		splitPane.setDividerPositions(array);
		//Save
		saveSplitPaneDivider();
		
	}
	
}
