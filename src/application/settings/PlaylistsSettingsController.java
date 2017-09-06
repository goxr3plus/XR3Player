package application.settings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.JavaFXTools;
import application.tools.NotificationType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * 
 * .
 * 
 * @author GOXR3PLUS
 */
public class PlaylistsSettingsController extends BorderPane {
	
	/** -----------------------------------------------------. */
	
	@FXML
	private Accordion accordion;
	
	@FXML
	private JFXCheckBox instantSearch;
	
	@FXML
	private ToggleGroup fileSearchGroup;
	
	@FXML
	private ToggleGroup playedFilesDetectionGroup;
	
	@FXML
	private ToggleGroup totalFilesShownGroup;
	
	@FXML
	private JFXButton clearPlayedFilesHistory;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Constructor.
	 */
	public PlaylistsSettingsController() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "PlayListsSettingsController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {
		
		//accordion
		accordion.setExpandedPane(accordion.getPanes().get(1));
		
		//--Playlists-Settings-Search--------------
		
		//instantSearch
		instantSearch.selectedProperty().addListener(l -> {
			
			Main.dbManager.getPropertiesDb().updateProperty("PlayLists-Search-InstantSearch", String.valueOf(instantSearch.isSelected()));
			System.out.println("Instant Search Updated...");
			
		});
		
		//fileSearchGroup
		fileSearchGroup.selectedToggleProperty().addListener(listener -> Main.dbManager.getPropertiesDb().updateProperty("PlayLists-Search-FileSearchUsing",
				Integer.toString(JavaFXTools.getIndexOfSelectedToggle(fileSearchGroup))));
		
		//--Playlists-Settings-General--------------
		
		//playedFilesDetectionGroup
		playedFilesDetectionGroup.selectedToggleProperty().addListener(listener -> Main.dbManager.getPropertiesDb().updateProperty("PlayLists-General-PlayedFilesDetection",
				Integer.toString(JavaFXTools.getIndexOfSelectedToggle(playedFilesDetectionGroup))));
		
		//totalFilesShownGroup
		totalFilesShownGroup.selectedToggleProperty().addListener(listener -> {
			
			//Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("PlayLists-General-TotalFilesShown", Integer.toString(JavaFXTools.getIndexOfSelectedToggle(totalFilesShownGroup)));
			
			//First Update all the Libraries
			Main.libraryMode.teamViewer.getViewer().getItemsObservableList().forEach(
					library -> library.getSmartController().setNewMaximumPerPage(Integer.parseInt( ( (Labeled) totalFilesShownGroup.getSelectedToggle() ).getText()), true));
			
			//Secondly Update the Search Window PlayList
			Main.searchWindowSmartController.setNewMaximumPerPage(Integer.parseInt( ( (Labeled) totalFilesShownGroup.getSelectedToggle() ).getText()), true);
			
			//Thirdly Update all the XPlayers SmartController
			Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController())
					.forEach(controller -> controller.setNewMaximumPerPage(Integer.parseInt( ( (Labeled) totalFilesShownGroup.getSelectedToggle() ).getText()), true));
			
		});
		
		//clearPlayedFilesHistory
		clearPlayedFilesHistory.setOnAction(a -> {
			if (Main.playedSongs.clearAll(true))
				ActionTool.showNotification("Message", "Successfully cleared played files from database", Duration.millis(1500), NotificationType.INFORMATION);
			else
				ActionTool.showNotification("Message", "Problem occured trying to clear played files from database", Duration.millis(1500), NotificationType.ERROR);
		});
		
	}
	
	/**
	 * Restores all the settings that have to do with the category of the class
	 */
	public void restoreSettings() {
		
		//instantSearch
		instantSearch.setSelected(true);
		
		//fileSearchGroup
		JavaFXTools.selectToggleOnIndex(fileSearchGroup, 1);
		
		//totalFilesShownGroup
		JavaFXTools.selectToggleOnIndex(totalFilesShownGroup, 0);
		
		//playedFilesDetectionGroup
		JavaFXTools.selectToggleOnIndex(playedFilesDetectionGroup, 1);
	}
	
	/**
	 * @return the instantSearch
	 */
	public JFXCheckBox getInstantSearch() {
		return instantSearch;
	}
	
	/**
	 * @return the playedFilesDetectionGroup
	 */
	public ToggleGroup getPlayedFilesDetectionGroup() {
		return playedFilesDetectionGroup;
	}
	
	/**
	 * @return the totalFilesShownGroup
	 */
	public ToggleGroup getTotalFilesShownGroup() {
		return totalFilesShownGroup;
	}
	
	/**
	 * @return the fileSearchGroup
	 */
	public ToggleGroup getFileSearchGroup() {
		return fileSearchGroup;
	}
	
}
