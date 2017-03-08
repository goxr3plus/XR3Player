/**
 * 
 */
package application.settings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class ApplicationSettingsController extends BorderPane {

    @FXML
    private Tab shortCutsTab;

    //--------------------------------------------------------

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The Stage of the ApplicationSettings
     */
    public Stage window = new Stage();

    NativeKeysController nativeKeyBindings = new NativeKeysController();

    /**
     * Constructor
     */
    public ApplicationSettingsController() {

	// ------------------------------------FXMLLOADER-------------------------------------
	FXMLLoader loader = new FXMLLoader(
		getClass().getResource(InfoTool.fxmls + "ApplicationSettingsController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}

	window.setTitle("Application Settings");
	window.initStyle(StageStyle.UTILITY);
	window.setScene(new Scene(this));

    }

    /**
     * Shows the Window
     */
    public void showWindow() {
	window.show();
    }

    /**
     * Hides the Window
     */
    public void hideWindow() {
	window.hide();
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {
	shortCutsTab.setContent(nativeKeyBindings);
    }
}
