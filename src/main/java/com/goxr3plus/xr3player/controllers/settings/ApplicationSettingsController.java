package com.goxr3plus.xr3player.controllers.settings;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

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
	private Button restoreAllSettings;

	@FXML
	private JFXButton doneButton;

	@FXML
	private MenuButton copySettingsMenuButton;

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
	private Button restoreDefaults;
	// --------------------------------------------------------

	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * The Stage of the ApplicationSettings.
	 */
	private Stage window = new Stage();

	private GeneralSettingsController generalSettingsController = new GeneralSettingsController();
	private PlaylistsSettingsController playListsSettingsController = new PlaylistsSettingsController();
	private LibrariesSettingsController librariesSettingsController = new LibrariesSettingsController();
	private KeyBindingsController nativeKeyBindingsController = new KeyBindingsController();
	private XPlayerSettingsController xPlayersSettingsController = new XPlayerSettingsController();

	/**
	 * Constructor.
	 */
	public ApplicationSettingsController() {

		// ------------------------------------FXMLLOADER-------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SETTINGS_FXMLS + "ApplicationSettings.fxml"));
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
	 * @param settingsTab The default tab you want to be selected when the window is
	 *                    shown
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

		// doneButton
		doneButton.setOnAction(a -> hideWindow());

		// restoreDefaults
		restoreDefaults.setOnAction(a -> {

			// Ask the madafacka user :)
			if (AlertTool.doQuestion("Restore category settings",
					"Soore you want to restore defaults for the selected category of settings", restoreDefaults,
					Main.window)) {

				// Find the selected Tab
				Tab selectedTab = generalTab.getTabPane().getSelectionModel().getSelectedItem();

				// Decide which settings to restore based on the category
				if (selectedTab == generalTab)
					generalSettingsController.restoreSettings();
				else if (selectedTab == playListsTab)
					playListsSettingsController.restoreSettings();
				else if (selectedTab == librariesTab)
					librariesSettingsController.restoreSettings();
				else if (selectedTab == shortCutsTab)
					nativeKeyBindingsController.restoreSettings();
				else if (selectedTab == xPlayersTab)
					xPlayersSettingsController.restoreSettings();
			}
		});

		// restoreAllSettings
		restoreAllSettings.setOnAction(a -> {

			// Ask the user if wants to restore all the settings to default
			if (AlertTool.doQuestion("Restore all settings", "Soore you want to restore <All The Settings> to default",
					restoreAllSettings, Main.window)) {

				// Lock the update properties
				Main.dbManager.getPropertiesDb().setUpdatePropertiesLocked(true);

				// Restore all of them ma boy!
				restoreAll();

				// Delete the current settings from the User
				IOAction.deleteFile(
						new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator() + Main.userInfoMode.getUser().getName()
								+ File.separator + "settings" + File.separator + DatabaseTool.USER_SETTINGS_FILE_NAME));

				// Lock the update properties
				Main.dbManager.getPropertiesDb().setUpdatePropertiesLocked(false);

				AlertTool.showNotification("Settings Restored", "All the settings are restored to default",
						Duration.seconds(2), NotificationType.INFORMATION);
			}
		});
	}

	/**
	 * Restores all the settings of all the categories, this is usually used when
	 * the application firstly loads
	 */
	public void restoreAll() {
		generalSettingsController.restoreSettings();
		playListsSettingsController.restoreSettings();
		librariesSettingsController.restoreSettings();
		nativeKeyBindingsController.restoreSettings();
		xPlayersSettingsController.restoreSettings();
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
	public XPlayerSettingsController getxPlayersSettingsController() {
		return xPlayersSettingsController;
	}

	/**
	 * @return the copySettingsMenuButton
	 */
	public MenuButton getCopySettingsMenuButton() {
		return copySettingsMenuButton;
	}
}
