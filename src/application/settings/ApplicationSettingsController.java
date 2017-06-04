package application.settings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
		GENERERAL, LIBRARIES, PLAYLISTS, SHORTCUTS, XPLAYERS, ANYONE;
	}
	
	@FXML
	private Tab generalTab;
	
	@FXML
	private Tab playListsTab;
	
	@FXML
	private Tab librariesTab;
	
	@FXML
	private Tab shortCutsTab;
	
	@FXML
	private Tab xPlayersTab;
	
	@FXML
	private Tab webBrowserTab;
	
	@FXML
	private Button doneButton;
	
	//--------------------------------------------------------
	
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * The Stage of the ApplicationSettings.
	 */
	private Stage window = new Stage();
	
	private GeneralSettingsController generalSettingsController = new GeneralSettingsController();
	private KeyBindingsController nativeKeyBindingsController = new KeyBindingsController();
	private PlaylistsSettingsController playListsSettingsController = new PlaylistsSettingsController();
	private LibrariesSettingsController librariesSettingsController = new LibrariesSettingsController();
	private XPlayersSettingsController xPlayersSettingsController = new XPlayersSettingsController();
	
	/**
	 * Constructor.
	 */
	public ApplicationSettingsController() {
		
		// ------------------------------------FXMLLOADER-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "ApplicationSettings.fxml"));
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
	 * Shows the Window.
	 * 
	 * @param settingsTab
	 *            The default tab you want to be selected when the window is shown
	 */
	public void showWindow(SettingsTab settingsTab) {
		
		if (settingsTab == SettingsTab.GENERERAL) {
			librariesTab.getTabPane().getSelectionModel().select(0);
		} else if (settingsTab == SettingsTab.PLAYLISTS) {
			librariesTab.getTabPane().getSelectionModel().select(1);
		} else if (settingsTab == SettingsTab.LIBRARIES) {
			librariesTab.getTabPane().getSelectionModel().select(2);
		} else if (settingsTab == SettingsTab.SHORTCUTS) {
			librariesTab.getTabPane().getSelectionModel().select(3);
		} else if (settingsTab == SettingsTab.XPLAYERS) {
			librariesTab.getTabPane().getSelectionModel().select(4);
		}
		
		window.show();
	}
	
	/**
	 * Hides the Window.
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
		
		generalTab.setContent(generalSettingsController);
		librariesTab.setContent(librariesSettingsController);
		playListsTab.setContent(playListsSettingsController);
		shortCutsTab.setContent(nativeKeyBindingsController);
		xPlayersTab.setContent(xPlayersSettingsController);
		
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
		return nativeKeyBindingsController;
	}
	
	/**
	 * @return the librariesSettingsController
	 */
	public LibrariesSettingsController getLibrariesSettingsController() {
		return librariesSettingsController;
	}
	
	/**
	 * @return the generalSettingsController
	 */
	public GeneralSettingsController getGeneralSettingsController() {
		return generalSettingsController;
	}
	
	/**
	 * @return the xPlayersSettingsController
	 */
	public XPlayersSettingsController getxPlayersSettingsController() {
		return xPlayersSettingsController;
	}
	
}
