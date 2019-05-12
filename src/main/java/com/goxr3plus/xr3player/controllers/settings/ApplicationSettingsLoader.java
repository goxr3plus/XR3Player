package com.goxr3plus.xr3player.controllers.settings;

import java.util.Optional;
import java.util.Properties;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.goxr3plus.xr3player.xplayer.visualizer.presenter.VisualizerWindowController.Type;

import javafx.scene.control.Control;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

public class ApplicationSettingsLoader {

	/**
	 * Loads all the application settings from the property file
	 */
	public static void loadApplicationSettings() {
		try {

			// Start
			// System.out.println("\n\n-----App Settings--------------\n");

			// --------------------Now continue
			// normally----------------------------------------------

			// Lock the update properties
			Main.dbManager.getPropertiesDb().setUpdatePropertiesLocked(true);

			// Load the Properties
			Main.dbManager.getPropertiesDb().getProperties().clear();
			Main.dbManager.getPropertiesDb().loadProperties();

			// Get the properties
			Properties settings = Main.dbManager.getPropertiesDb().getProperties();

			// Restore all to default before loading the settings
			Main.settingsWindow.restoreAll();

			// ---------- Load all the settings from the config.properties
			// --------------------

			// ======================START OF Color-Pickers-Settings======================
			Optional.ofNullable(settings.getProperty("Libraries-Background-Color"))
					.ifPresent(color -> Main.libraryMode.getColorPicker().setValue(Color.web(color)));

			// ======================START OF KeyBindings-Settings======================

			Optional.ofNullable(settings.getProperty("ShortCuts-KeyBindings")).ifPresent(s -> Main.settingsWindow
					.getNativeKeyBindings().getKeyBindingsActive().setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("ShortCuts-SelectedPlayer"))
					.ifPresent(s -> JavaFXTool.selectToggleOnIndex(
							Main.settingsWindow.getNativeKeyBindings().getxPlayerSelected(), Integer.valueOf(s)));

			// ======================START OF General-Settings======================

			// -- High Graphics Mode
			Main.settingsWindow.getGeneralSettingsController().getHighGraphicsToggle().setSelected(true);
			Main.settingsWindow.getGeneralSettingsController().getHighGraphicsToggle().setSelected(false);

			Optional.ofNullable(settings.getProperty("General-High-Graphics")).ifPresent(s -> Main.settingsWindow
					.getGeneralSettingsController().getHighGraphicsToggle().setSelected(Boolean.valueOf(s)));

			// --SideBar side
			Optional.ofNullable(settings.getProperty("General-SideBarSide"))
					.ifPresent(s -> JavaFXTool.selectToggleOnIndex(
							Main.settingsWindow.getGeneralSettingsController().getSideBarSideGroup(),
							Integer.valueOf(s)));

			// --NotificationsPosition
			Optional.ofNullable(settings.getProperty("General-NotificationsPosition"))
					.ifPresent(s -> JavaFXTool.selectToogleWithText(
							Main.settingsWindow.getGeneralSettingsController().getNotificationsPosition(), s));

			// --LibraryMode
			Main.playListModesSplitPane.updateSplitPaneDivider();
			// Main.libraryMode.updateTopSplitPaneDivider();
			// Main.libraryMode.updateBottomSplitPaneDivider();
			// Optional.ofNullable(settings.getProperty("General-LibraryModeUpsideDown"))
			// .ifPresent(s ->
			// JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getLibraryModeUpsideDown(),
			// Integer.valueOf(s)))
			//
			// --DJMode
			// Main.djMode.updateTopSplitPaneDivider();
			// Main.djMode.updateBottomSplitPaneDivider();
			// Optional.ofNullable(settings.getProperty("General-DjModeUpsideDown"))
			// .ifPresent(s ->
			// JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getDjModeUpsideDown(),
			// Integer.valueOf(s)))
			//

			// ======================START OF Libraries-Settings======================

			Optional.ofNullable(settings.getProperty("Libraries-ShowWidgets")).ifPresent(s -> Main.settingsWindow
					.getLibrariesSettingsController().getShowWidgets().setSelected(Boolean.parseBoolean(s)));

			// ======================START OF Playlists-Settings======================

			// --Media Mode SECTOR

			Optional.ofNullable(settings.getProperty("PlayLists-General-PlayedFilesDetection"))
					.ifPresent(s -> JavaFXTool.selectToggleOnIndex(
							Main.settingsWindow.getPlayListsSettingsController().getPlayedFilesDetectionGroup(),
							Integer.valueOf(s)));

			Optional.ofNullable(settings.getProperty("PlayLists-General-TotalFilesShown"))
					.ifPresent(s -> JavaFXTool.selectToggleOnIndex(
							Main.settingsWindow.getPlayListsSettingsController().getTotalFilesShownGroup(),
							Integer.valueOf(s)));

			Optional.ofNullable(settings.getProperty("PlayLists-General-SelMatchMedViewItem"))
					.ifPresent(s -> Main.settingsWindow.getPlayListsSettingsController()
							.getSelectMatchingMediaViewItem().setSelected(Boolean.parseBoolean(s)));

			// --Media Viewer SECTOR
			Optional.ofNullable(settings.getProperty("PlayLists-MediaViewer-SelMatchPlaylistItem"))
					.ifPresent(s -> Main.settingsWindow.getPlayListsSettingsController().getSelectMatchingPlaylistItem()
							.setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("PlayLists-MediaViewer-ScrollToMatchPlaylistItem"))
					.ifPresent(s -> Main.settingsWindow.getPlayListsSettingsController()
							.getScrollToMatchingPlaylistItem().setSelected(Boolean.parseBoolean(s)));

			// --Search SECTOR
			Optional.ofNullable(settings.getProperty("PlayLists-Search-InstantSearch"))
					.ifPresent(s -> Main.settingsWindow.getPlayListsSettingsController().getInstantSearch()
							.setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("PlayLists-Search-FileSearchUsing"))
					.ifPresent(s -> JavaFXTool.selectToggleOnIndex(
							Main.settingsWindow.getPlayListsSettingsController().getFileSearchGroup(),
							Integer.valueOf(s)));

			// --Folders Mode SECTOR
			Optional.ofNullable(settings.getProperty("PlayLists-FoldersMode-WhichFilesToShowGenerally"))
					.ifPresent(s -> {

						ToggleGroup group = Main.settingsWindow.getPlayListsSettingsController()
								.getWhichFilesToShowGenerally();
						group.getToggles().stream()
								.filter(toggle -> ((Control) toggle).getTooltip().getText().equals(s)).findFirst()
								.ifPresent(group::selectToggle);

					});

			Optional.ofNullable(settings.getProperty("PlayLists-FoldersMode-FilesToShowUnderFolders")).ifPresent(s -> {

				ToggleGroup group = Main.settingsWindow.getPlayListsSettingsController().getFilesToShowUnderFolders();
				group.getToggles().stream().filter(toggle -> ((Control) toggle).getTooltip().getText().equals(s))
						.findFirst().ifPresent(group::selectToggle);

			});
			// ======================START OF XPLAYERS-Settings======================

			// --General
			Optional.ofNullable(settings.getProperty("XPlayers-General-AllowDiscRotation"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getAllowDiscRotation()
							.setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("XPlayers-General-StartAtOnce")).ifPresent(s -> Main.settingsWindow
					.getxPlayersSettingsController().getStartImmediately().setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("XPlayers-General-AskSecurityQuestion"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getAskSecurityQuestion()
							.setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("XPlayers-General-ShowPlayerNotifications"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getShowPlayerNotifications()
							.setSelected(Boolean.parseBoolean(s)));

			Optional.ofNullable(settings.getProperty("XPlayers-General-SkipButtonSeconds"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getSkipSlider()
							.setValue(Integer.parseInt(s)));

			// --Visualizer
			Optional.ofNullable(settings.getProperty("XPlayers-Visualizer-ShowFPS")).ifPresent(s -> {

				// Set the Value to the CheckBox
				Main.settingsWindow.getxPlayersSettingsController().getShowFPS().setSelected(Boolean.parseBoolean(s));

				// Update all the players
				Main.xPlayersList.getList().forEach(
						xPlayerController -> xPlayerController.visualizer.setShowFPS(Boolean.parseBoolean(s)));

			});

			// --Visualizer Images
			Main.xPlayersList.getList().forEach(xPlayerController -> {

				// If the key is not there add background image by default
				Optional.ofNullable(settings
						.getProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared"))
						.ifPresentOrElse(v -> xPlayerController.visualizerWindow.clearImage(Type.BACKGROUND),
								() -> xPlayerController.visualizerWindow.findAppropriateImage(Type.BACKGROUND));

				// Always add foreground image
				xPlayerController.visualizerWindow.findAppropriateImage(Type.FOREGROUND);

				// Determine the visualizer display mode
				Optional.ofNullable(
						settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-DisplayMode"))
						.ifPresent(s -> xPlayerController.visualizer.displayMode.set(Integer.valueOf(s)));

				// Check if it is on simple or advanced mode
				Optional.ofNullable(settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Advanced-Mode"))
						.ifPresentOrElse(s -> xPlayerController.getModeToggle().setSelected(Boolean.valueOf(s)), () -> {
							// DJ Mode players have it selected by default
							if (xPlayerController.getKey() == 1 || xPlayerController.getKey() == 2)
								xPlayerController.getModeToggle().setSelected(true);
						});

				// Check the volume bar
				// Optional.ofNullable(settings.getProperty("XPlayer" +
				// xPlayerController.getKey() + "-Volume-Bar")).ifPresent(s ->
				// xPlayerController.setVolume(Integer.parseInt(s)));

				// Check if muted
				Optional.ofNullable(settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Muted"))
						.ifPresent(s -> xPlayerController.setMute(Boolean.parseBoolean(s)));

				// Check if Visualizers Enabled on Simple Mode
				Optional.ofNullable(settings
						.getProperty("XPlayer" + xPlayerController.getKey() + "-Simple-Mode-Visualizers-Enabled"))
						.ifPresent(s -> xPlayerController.getShowVisualizer().setSelected(Boolean.parseBoolean(s)));

			});

			// ---------- --------------------

			// Re-enable Properties Updating
			Main.dbManager.getPropertiesDb().setUpdatePropertiesLocked(false);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
