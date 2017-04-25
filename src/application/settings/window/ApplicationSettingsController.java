/**
 * 
 */
package application.settings.window;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class ApplicationSettingsController extends BorderPane {

    /**
     * @author GOXR3PLUS
     *
     */
    public enum SettingsTab {
	LIBRARIES, PLAYLISTS, SHORTCUTS, ANYONE;
    }

    @FXML
    private Tab librariesTab;

    @FXML
    private Tab playListsTab;

    @FXML
    private Tab shortCutsTab;

    @FXML
    private JFXButton doneButton;

    //--------------------------------------------------------

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The Stage of the ApplicationSettings
     */
    private Stage window = new Stage();

    private KeyBindingsController nativeKeyBindings = new KeyBindingsController();
    private PlaylistsSettingsController playListsSettingsController = new PlaylistsSettingsController();
    private LibrariesSettingsController librariesSettingsController = new LibrariesSettingsController();

    /**
     * Constructor
     */
    public ApplicationSettingsController() {

	// ------------------------------------FXMLLOADER-------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "ApplicationSettingsController.fxml"));
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
	window.getScene().setOnKeyReleased(k -> {
	    if (k.getCode() == KeyCode.ESCAPE)
		window.close();
	});

    }

    /**
     * Shows the Window
     * 
     * @param settingsTab
     *            The default tab you want to be selected when the window is shown
     */
    public void showWindow(SettingsTab settingsTab) {

	if (settingsTab == SettingsTab.LIBRARIES)
	    librariesTab.getTabPane().getSelectionModel().select(0);
	else if (settingsTab == SettingsTab.PLAYLISTS)
	    librariesTab.getTabPane().getSelectionModel().select(1);
	else if (settingsTab == SettingsTab.SHORTCUTS)
	    librariesTab.getTabPane().getSelectionModel().select(2);

	window.show();
    }

    /**
     * Hides the Window
     */
    public void hideWindow() {
	window.hide();
    }

    /**
     * @return the window
     */
    public Stage getWindow() {
	return window;
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {
	librariesTab.setContent(librariesSettingsController);
	playListsTab.setContent(playListsSettingsController);
	shortCutsTab.setContent(nativeKeyBindings);

	//doneButton
	doneButton.setOnAction(a -> hideWindow());
    }

    /**
     * @return the playListsSettingsController
     */
    public PlaylistsSettingsController getPlayListsSettingsController() {
	return playListsSettingsController;
    }

    /**
     * @return the nativeKeyBindings
     */
    public KeyBindingsController getNativeKeyBindings() {
	return nativeKeyBindings;
    }

    /**
     * @return the librariesSettingsController
     */
    public LibrariesSettingsController getLibrariesSettingsController() {
	return librariesSettingsController;
    }

}
