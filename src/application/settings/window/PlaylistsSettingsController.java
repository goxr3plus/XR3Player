package application.settings.window;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * This class is used as the SideBar of the application.
 *
 * @author GOXR3PLUS
 */
public class PlaylistsSettingsController extends BorderPane {

    //-----------------------------------------------------

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

	//totalFilesShownGroup
	totalFilesShownGroup.selectedToggleProperty().addListener(listener -> {

	    //First Update all the Libraries
	    Main.libraryMode.teamViewer.getViewer().getItemsObservableList().forEach(library -> library.getSmartController()
		    .setNewMaximumPerPage(Integer.parseInt(((Labeled) totalFilesShownGroup.getSelectedToggle()).getText()), true));

	    //Secondly Update the Search Window PlayList
	    Main.searchWindow.getSmartController()
		    .setNewMaximumPerPage(Integer.parseInt(((Labeled) totalFilesShownGroup.getSelectedToggle()).getText()), true);

	});

	//clearPlayedFilesHistory
	clearPlayedFilesHistory.setOnAction(a -> {
	    if (Main.playedSongs.clearAll())
		ActionTool.showNotification("Message", "Successfully cleared played files from database", Duration.millis(1500),
			NotificationType.INFORMATION);
	    else
		ActionTool.showNotification("Message", "Problem occured trying to clear played files from database", Duration.millis(1500),
			NotificationType.ERROR);
	});

	//accordion
	accordion.setExpandedPane(accordion.getPanes().get(0));
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
