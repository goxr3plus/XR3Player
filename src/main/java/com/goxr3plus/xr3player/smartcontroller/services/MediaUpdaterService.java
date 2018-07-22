/*
 * 
 */
package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import static main.java.com.goxr3plus.xr3player.application.Main.libraryMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;
import main.java.com.goxr3plus.xr3player.streamplayer.ThreadFactoryWithNamePrefix;

/**
 * This Service tries to keep all the playlists updated based on the database. For example let's say that you just opened a new library , the stars ,
 * emotions etc of each media needs to be checked and updated every so for changes.
 */
public class MediaUpdaterService {
	
	/**
	 * If is true then the Thread has stopped , so i restart it again...
	 */
	private final BooleanProperty threadStopped = new SimpleBooleanProperty(false);
	
	/**
	 * This executor service is used in order the playerState events to be executed in an order
	 */
	private final ExecutorService executors = Executors.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix("Media Updater Service "));
	
	/**
	 * Start the Thread.
	 *
	 */
	public void start() {
		Runnable runnable = () -> {
			try {
				Platform.runLater(() -> threadStopped.set(false));
				
				//Run forever , except if i interrupt it ;)
				for (;; Thread.sleep(1500))
					startFilteringControllers();
				
			} catch (Exception ex) {
				Main.logger.log(Level.INFO, "", ex);
			} finally {
				//Platform.runLater(
				//		() -> ActionTool.showNotification("Message", "FilesFilterThread ->exited", Duration.millis(1500), NotificationType.ERROR))
				System.out.println("Media Update Service Thread exited!!!");
				Platform.runLater(() -> threadStopped.set(true));
			}
		};
		executors.execute(runnable);
		
		//---Add this listener in case something bad happens to the thread above
		threadStopped.addListener((observable , oldValue , newValue) -> {
			//Restart it if it has stopped
			if (newValue)
				executors.execute(runnable);
		});
	}
	
	/**
	 * Starts filtering all the needed application SmartControllers
	 */
	private void startFilteringControllers() {
		
		try {
			
			//Don't enter in case of 
			if (Main.topBar.isTabSelected(Main.topBar.getMainModeTab()) || Main.libraryMode.getDjModeStackPane().isVisible()) {
				
				//Selected + Opened Library
				if (Main.playListModesTabPane.getOpenedLibrariesTab().isSelected())
					libraryMode.openedLibrariesViewer.getSelectedLibrary().ifPresent(selectedLibrary -> {
						
						//Find the controller
						SmartController controller = (SmartController) Main.libraryMode.openedLibrariesViewer.getTab(selectedLibrary.getLibraryName()).getContent();
						
						//Normal Mode
						if (controller.getNormalModeTab().isSelected())
							filterController(selectedLibrary.getSmartController(), selectedLibrary.getSmartController().getItemsObservableList());
						
						//Filters Mode 
						else if (controller.getFiltersModeTab().isSelected())
							filterController(selectedLibrary.getSmartController(),
									selectedLibrary.getSmartController().getFiltersMode().getMediaTableViewer().getTableView().getItems());
						
					});
				
				//-------------
				
				//Search Window
				if (Main.mediaSearchWindow.getWindow().isShowing()) {
					
					//Find the controller
					SmartController controller = Main.searchWindowSmartController;
					
					//Normal Mode
					if (controller.getNormalModeTab().isSelected())
						filterController(Main.searchWindowSmartController, Main.searchWindowSmartController.getItemsObservableList());
					
					//Filter mode
					else if (controller.getFiltersModeTab().isSelected())
						filterController(Main.searchWindowSmartController, Main.searchWindowSmartController.getFiltersMode().getMediaTableViewer().getTableView().getItems());
				}
			}
			
			//XPlayers
			Main.xPlayersList.getList().stream()
					//Extra filtering
					.filter(xPlayerController -> {
						//If extended pass
						if (xPlayerController.isExtended())
							return true;
						//Or else check more through
						else {
							//For player 0
							if (xPlayerController.getKey() == 0 && Main.topBar.isTabSelected(Main.topBar.getMainModeTab()))
								return true;
							//For other players
							else if (xPlayerController.getKey() != 0 && Main.libraryMode.getDjModeStackPane().isVisible())
								return true;
						}
						return false;
					})
					
					//For each
					.forEach(xPlayerController -> {
						
						//Fix the emotion image
						xPlayerController.changeEmotionImage(Main.emotionListsController.getEmotionForMedia(xPlayerController.getxPlayerModel().songPathProperty().get()));
						
						//Only if the Settings Mode is selected
						if (xPlayerController.getHistoryToggle().isSelected()) {
							//Find the smartController
							SmartController controller = xPlayerController.getxPlayerPlayList().getSmartController();
							
							//Normal Mode  
							if (controller.getNormalModeTab().isSelected())
								filterController(controller, controller.getItemsObservableList());
							
							//Filters Mode
							else if (controller.getFiltersModeTab().isSelected())
								filterController(controller, controller.getFiltersMode().getMediaTableViewer().getTableView().getItems());
						}
					});
			
			//--
			
			//Don't enter in case of 
			if ( ( Main.topBar.isTabSelected(Main.topBar.getMainModeTab()) || Main.libraryMode.getDjModeStackPane().isVisible() )
					&& Main.playListModesTabPane.getEmotionListsTab().isSelected())
				//Filter Emotion Lists Normal Mode TableViews   
				Main.emotionsTabPane.getTabPane().getTabs().stream().filter(Tab::isSelected).findFirst().ifPresent(tab -> {
					
					//Normal Mode
					filterController((SmartController) tab.getContent(), ( (SmartController) tab.getContent() ).getItemsObservableList());
					
					//Filters Mode
					filterController((SmartController) tab.getContent(), ( (SmartController) tab.getContent() ).getFiltersMode().getMediaTableViewer().getTableView().getItems());
					
				});
			
			//--
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Checks the Elements of the SmartController using some conditions.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception [[SuppressWarningsSpartan]]
	 */
	private void filterController(SmartController controller , ObservableList<Media> observableList) {
		
		// Don't enter if controller is null
		if (controller == null || observableList.isEmpty())
			return;
		
		boolean[] controllerIsFree = { false };
		
		// Synchronize with javaFX thread
		CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {
			controllerIsFree[0] = controller.isFree(false);
			latch.countDown();
		});
		try {
			latch.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
		// isFree?
		if (controllerIsFree[0]) {
			
			//Check the settings
			int mode = JavaFXTools.getIndexOfSelectedToggle(Main.settingsWindow.getPlayListsSettingsController().getPlayedFilesDetectionGroup());
			
			//For each media File of the Controller
			observableList.stream().forEach(media -> {
				
				// ---------File exists--------?
				Platform.runLater(() -> media.fileExistsProperty().set(Paths.get(media.getFilePath()).toFile().exists()));
				
				//Set Played Status
				determinePlayedStatus(media, mode);
				
				//Set timesPlayed
				Main.playedSongs.getSet().stream().filter(playedFile -> media.getFilePath().equals(playedFile.getPath())).findFirst().ifPresent(playedFile -> {
					media.timesPlayedProperty().set(playedFile.getTimesPlayed());
				});
				
				//Set stars
				//The is a problem with this , what if the file is the same but ... in different path
				//I should use FileUtils.contentEquals in the future but i don't want to take high the CPU usage...
				Main.starredMediaList.getSet().stream()
						//Filter
						.filter(starredFile -> ( media.getFilePath().equals(starredFile.getPath()) ) && media.getStars() != starredFile.getStars())
						//Find first matching
						.findFirst().ifPresent(starredFile -> {
							
							//Run of JavaFX Thread
							Platform.runLater(() -> {
								if (!media.starsProperty().isBound())
									media.starsProperty().set(starredFile.getStars());
							});
						});
						
				// ---------Liked or disliked--------?
				media.changeEmotionImage(Main.emotionListsController.getEmotionForMedia(media.getFilePath()));
				
			});
		}
		
	}
	
	/**
	 * Set's the Media Played or Not [ Using JavaFX Thread ]
	 * 
	 * @param played
	 */
	private static void determinePlayedStatus(Media media , int mode) {
		
		//Check if this media is already playing in some player
		//cause we want to set different image if so...
		int[] playedStatus = { Media.UNKNOWN_PLAYED_STATUS };
		Main.xPlayersList.getList().stream().forEach(xPlayerController -> {
			String path = xPlayerController.getxPlayerModel().songPathProperty().get();
			if (path != null && path.equals(media.getFilePath()))
				playedStatus[0] = xPlayerController.getKey();
			
		});
		
		//Pass this if the media is currently being played by some player
		if (playedStatus[0] == Media.UNKNOWN_PLAYED_STATUS) {
			
			playedStatus[0] = Main.playedSongs.getSet().stream().anyMatch(playedFile -> {
				
				if (mode == 0) // Check based on File Content [ The content must be absolutely the same ] 
					
					//Check if the contents match exactly with FileUtils library
					try {
						return FileUtils.contentEquals(new File(playedFile.getPath()), new File(media.getFilePath()));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				
				else if (mode == 1) //Check based on FileName
					return playedFile.getPath().contains(media.getFileName());
				
				// .anyMatch(
				//			playedFile -> new File(playedFile.getPath()).length() == new File(media.getFilePath()).length()) ? Media.HAS_BEEN_PLAYED : Media.NEVER_PLAYED ;
				
				return false;
			}) ? Media.HAS_BEEN_PLAYED : Media.NEVER_PLAYED;
			
		}
		
		media.setPlayedStatus(playedStatus[0]);
	}
	
}
