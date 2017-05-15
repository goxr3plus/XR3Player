/**
 * 
 */
package application.settings.window;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class XPlayersSettingsController extends BorderPane {

    //-----------------------------------------------------

    @FXML
    private JFXCheckBox showFPS;

    @FXML
    private JFXCheckBox startImmediately;

    @FXML
    private JFXCheckBox askSecurityQuestion;

    // -------------------------------------------------------------

    /** The logger. */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Constructor.
     */
    public XPlayersSettingsController() {

	// ------------------------------------FXMLLOADER ----------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "XPlayersSettingsController.fxml"));
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

	// ShowFPS
	showFPS.setOnAction(a -> {

	    //Update the properties file
	    Main.dbManager.getPropertiesDb().updateProperty("XPlayers-Visualizer-ShowFPS", String.valueOf(showFPS.isSelected()));

	    //Update all the players
	    Main.xPlayersList.getList().forEach(xPlayerController -> xPlayerController.getVisualizer().setShowFPS(showFPS.isSelected()));

	});

	// StartImmediately
	startImmediately.selectedProperty().addListener(
		l -> Main.dbManager.getPropertiesDb().updateProperty("XPlayers-General-StartAtOnce", String.valueOf(startImmediately.isSelected())));

	// AskSecurityQuestion
	askSecurityQuestion.selectedProperty().addListener(l -> Main.dbManager.getPropertiesDb()
		.updateProperty("XPlayers-General-AskSecurityQuestion", String.valueOf(askSecurityQuestion.isSelected())));

    }

    /**
     * @return the showFPS
     */
    public JFXCheckBox getShowFPS() {
	return showFPS;
    }

    /**
     * @return the startImmediately
     */
    public JFXCheckBox getStartImmediately() {
	return startImmediately;
    }

    /**
     * @return the askSecurityQuestion
     */
    public JFXCheckBox getAskSecurityQuestion() {
	return askSecurityQuestion;
    }

}
