package main.java.com.goxr3plus.xr3player.application.settings;

import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Control;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.xplayer.visualizer.fxpresenter.VisualizerWindowController.Type;

public class ApplicationSettingsLoader {
	
	/**
	 * Loads all the application settings from the property file
	 */
	public static void loadApplicationSettings() {
		try {
			
			//Start
			//System.out.println("\n\n-----App Settings--------------\n");
			
			//--------------------Now continue normally----------------------------------------------
			
			//Lock the update properties
			Main.dbManager.getPropertiesDb().setUpdatePropertiesLocked(true);
			
			//Load the Properties
			Main.dbManager.getPropertiesDb().getProperties().clear();
			Main.dbManager.getPropertiesDb().loadProperties();
			
			//Get the properties
			Properties settings = Main.dbManager.getPropertiesDb().getProperties();
			
			//Restore all to default before loading the settings
			Main.settingsWindow.restoreAll();
			
			//----------   Load all the settings from the config.properties --------------------
			
			//======================START OF Color-Pickers-Settings======================
			Optional.ofNullable(settings.getProperty("Libraries-Background-Color")).ifPresent(color -> Main.libraryMode.getColorPicker().setValue(Color.web(color)));
			
			//======================START OF KeyBindings-Settings======================
			
			Optional.ofNullable(settings.getProperty("ShortCuts-KeyBindings"))
					.ifPresent(s -> Main.settingsWindow.getNativeKeyBindings().getKeyBindingsActive().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("ShortCuts-SelectedPlayer"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(Main.settingsWindow.getNativeKeyBindings().getxPlayerSelected(), Integer.valueOf(s)));
			
			//======================START OF General-Settings======================
			
			//--SideBar side
			Optional.ofNullable(settings.getProperty("General-SideBarSide"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(Main.settingsWindow.getGeneralSettingsController().getSideBarSideGroup(), Integer.valueOf(s)));
			
			//--NotificationsPosition
			Optional.ofNullable(settings.getProperty("General-NotificationsPosition"))
					.ifPresent(s -> JavaFXTools.selectToogleWithText(Main.settingsWindow.getGeneralSettingsController().getNotificationsPosition(), s));
			
			//--LibraryMode
			Main.playListModesSplitPane.updateSplitPaneDivider();
			Main.libraryMode.updateTopSplitPaneDivider();
			Main.libraryMode.updateBottomSplitPaneDivider();
			//			Optional.ofNullable(settings.getProperty("General-LibraryModeUpsideDown"))
			//					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getLibraryModeUpsideDown(), Integer.valueOf(s)))
			//			
			//--DJMode
			Main.djMode.updateTopSplitPaneDivider();
			Main.djMode.updateBottomSplitPaneDivider();
			//			Optional.ofNullable(settings.getProperty("General-DjModeUpsideDown"))
			//					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getDjModeUpsideDown(), Integer.valueOf(s)))
			//		
			
			//======================START OF Libraries-Settings======================
			
			Optional.ofNullable(settings.getProperty("Libraries-ShowWidgets"))
					.ifPresent(s -> Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().setSelected(Boolean.parseBoolean(s)));
			
			//======================START OF Playlists-Settings======================
			
			//--Search SECTOR
			Optional.ofNullable(settings.getProperty("PlayLists-Search-InstantSearch"))
					.ifPresent(s -> Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("PlayLists-Search-FileSearchUsing"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(Main.settingsWindow.getPlayListsSettingsController().getFileSearchGroup(), Integer.valueOf(s)));
			
			//--Media Mode SECTOR
			
			Optional.ofNullable(settings.getProperty("PlayLists-General-PlayedFilesDetection"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(Main.settingsWindow.getPlayListsSettingsController().getPlayedFilesDetectionGroup(), Integer.valueOf(s)));
			
			Optional.ofNullable(settings.getProperty("PlayLists-General-TotalFilesShown"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(Main.settingsWindow.getPlayListsSettingsController().getTotalFilesShownGroup(), Integer.valueOf(s)));
			
			//--Folders Mode SECTOR
			Optional.ofNullable(settings.getProperty("PlayLists-FoldersMode-WhichFilesToShowGenerally")).ifPresent(s -> {
				
				ToggleGroup group = Main.settingsWindow.getPlayListsSettingsController().getWhichFilesToShowGenerally();
				group.getToggles().stream().filter(toggle -> ( (Control) toggle ).getTooltip().getText().equals(s)).findFirst().ifPresent(group::selectToggle);
				
			});
			
			Optional.ofNullable(settings.getProperty("PlayLists-FoldersMode-FilesToShowUnderFolders")).ifPresent(s -> {
				
				ToggleGroup group = Main.settingsWindow.getPlayListsSettingsController().getFilesToShowUnderFolders();
				group.getToggles().stream().filter(toggle -> ( (Control) toggle ).getTooltip().getText().equals(s)).findFirst().ifPresent(group::selectToggle);
				
			});
			//======================START OF XPLAYERS-Settings======================
			
			//--General
			Optional.ofNullable(settings.getProperty("XPlayers-General-StartAtOnce"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getStartImmediately().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("XPlayers-General-AskSecurityQuestion"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getAskSecurityQuestion().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("XPlayers-General-ShowPlayerNotifications"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getShowPlayerNotifications().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("XPlayers-General-SkipButtonSeconds"))
					.ifPresent(s -> Main.settingsWindow.getxPlayersSettingsController().getSkipSlider().setValue(Integer.parseInt(s)));
			
			//--Visualizer
			Optional.ofNullable(settings.getProperty("XPlayers-Visualizer-ShowFPS")).ifPresent(s -> {
				
				//Set the Value to the CheckBox
				Main.settingsWindow.getxPlayersSettingsController().getShowFPS().setSelected(Boolean.parseBoolean(s));
				
				//Update all the players
				Main.xPlayersList.getList().forEach(xPlayerController -> xPlayerController.getVisualizer().setShowFPS(Boolean.parseBoolean(s)));
				
			});
			
			//--Visualizer Images
			Main.xPlayersList.getList().forEach(xPlayerController -> {
				
				//If the key is not there add background image by default			
				if (Optional.ofNullable(settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared")).isPresent())
					xPlayerController.getVisualizerWindow().clearImage(Type.BACKGROUND);
				else
					xPlayerController.getVisualizerWindow().findAppropriateImage(Type.BACKGROUND);
				
				//Always add foreground image
				xPlayerController.getVisualizerWindow().findAppropriateImage(Type.FOREGROUND);
				
				//Determine the visualizer display mode
				Optional.ofNullable(settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-DisplayMode"))
						.ifPresent(s -> xPlayerController.getVisualizer().displayMode.set(Integer.valueOf(s)));
				
			});
			
			//----------                        --------------------
			
			//Re-enable Properties Updating
			Main.dbManager.getPropertiesDb().setUpdatePropertiesLocked(false);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//SHUT THE FUCK UP BASTARD MOTHER FUCKER CANCER !!!!!!!!!!! WTF !!!!!!!  CANCERED THE CONSOLE CANCER!!!! JAUDIOTAGGER LOGGER
		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
		Logger.getLogger("org.jaudiotagger.tag").setLevel(Level.OFF);
		Logger.getLogger("org.jaudiotagger.audio.mp3.MP3File").setLevel(Level.OFF);
		Logger.getLogger("org.jaudiotagger.tag.id3.ID3v23Tag").setLevel(Level.OFF);
		
		
	}
	
}
