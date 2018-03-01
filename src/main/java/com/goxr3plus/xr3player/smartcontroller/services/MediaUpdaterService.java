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
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;
import main.java.goxr3plus.javastreamplayer.stream.ThreadFactoryWithNamePrefix;

/**
 * The Class FileFilterThread.
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
				for (;; Thread.sleep(900))
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
			
			//Filter Selected Opened Libraries Normal Mode TableViews  
			libraryMode.openedLibrariesViewer.getSelectedLibrary()
					.ifPresent(selectedLibrary -> filterController(selectedLibrary.getSmartController(), selectedLibrary.getSmartController().getItemsObservableList()));
			
//			//Do Sorting of Normal Mode TableViews 
//			Platform.runLater(() -> libraryMode.openedLibrariesViewer.getSelectedLibrary()
//					.ifPresent(selectedLibrary -> selectedLibrary.getSmartController().getNormalModeMediatTableViewer().sortTable()));
//			
			//Filter Selected Opened Libraries Filters Mode TableViews  
			libraryMode.openedLibrariesViewer.getSelectedLibrary().ifPresent(selectedLibrary -> filterController(selectedLibrary.getSmartController(),
					selectedLibrary.getSmartController().filtersMode.getMediaTableViewer().getTableView().getItems()));
			
			//--
			
			//Filter XPlayer PlayLists Normal Mode TableViews   
			Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController())
					.forEach(smartController -> filterController(smartController, smartController.getItemsObservableList()));
			
			//Filter XPlayer PlayLists Filters Mode TableViews  
			Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController())
					.forEach(smartController -> filterController(smartController, smartController.filtersMode.getMediaTableViewer().getTableView().getItems()));
			
			//--
			
			//Filter Emotion Lists Normal Mode TableViews   
			Main.emotionsTabPane.getTabPane().getTabs()
					.forEach(tab -> filterController((SmartController) tab.getContent(), ( (SmartController) tab.getContent() ).getItemsObservableList()));
			
			//Filter Emotion Lists Filters Mode TableViews  
			Main.emotionsTabPane.getTabPane().getTabs().forEach(tab -> filterController((SmartController) tab.getContent(),
					( (SmartController) tab.getContent() ).filtersMode.getMediaTableViewer().getTableView().getItems()));
			
			//--
			
			//Filter SearchWindow Normal Mode TableViews  
			filterController(Main.searchWindowSmartController, Main.searchWindowSmartController.getItemsObservableList());
			
			//Filter SearchWindow SmartController
			filterController(Main.searchWindowSmartController, Main.searchWindowSmartController.filtersMode.getMediaTableViewer().getTableView().getItems());
			
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
		if (controller == null)
			return;
		
		boolean[] controllerIsFree = { false };
		
		// Synchronise with javaFX thread
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
				
				if (mode == 0) { // Check based on File Content [ The content must be absolutely the same ] 
					
					//Go
					File file = new File(media.getFilePath());
					
					//Check if this media is already playing in some player
					//cause we want to set different image if so...
					int[] mediaIsPlaying = { -3 };
					Main.xPlayersList.getList().stream().forEach(xPlayerController -> {
						String path = xPlayerController.getxPlayerModel().songPathProperty().get();
						if (path != null && path.equals(media.getFilePath()))
							mediaIsPlaying[0] = xPlayerController.getKey();
					});
					
					//Set Played Status
					setPlayStatus(media, mediaIsPlaying[0] != -3 ? mediaIsPlaying[0] : Main.playedSongs.getSet().stream().anyMatch(playedFile -> {
						try {
							return FileUtils.contentEquals(new File(playedFile.getPath()), file);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						return false;
					}) ? -1 : -2);
					
					//Set timesPlayed
					Main.playedSongs.getSet().stream().filter(playedFile -> media.getFilePath().equals(playedFile.getPath())).findFirst().ifPresent(playedFile -> {
						media.timesPlayedProperty().set(playedFile.getTimesPlayed());
					});
					
				} else if (mode == 1) { //Check based on FileName and FileLength -> both must be equal
					
					//Go
					String mediaName = InfoTool.getFileName(media.getFilePath()).toLowerCase();
					// String mediaPath = media.getFilePath().toLowerCase()
					long mediaFileLength = new File(media.getFilePath()).length();
					
					//Check if this media is already playing in some player
					//cause we want to set different image if so...
					int[] mediaIsPlaying = { -3 };
					Main.xPlayersList.getList().stream().forEach(xPlayerController -> {
						String path = xPlayerController.getxPlayerModel().songPathProperty().get();
						if (path != null && path.equals(media.getFilePath()))
							mediaIsPlaying[0] = xPlayerController.getKey();
					});
					
					//Set Played Status
					setPlayStatus(media,
							mediaIsPlaying[0] != -3 ? mediaIsPlaying[0]
									: Main.playedSongs.getSet().stream().filter(playedFile -> playedFile.getPath().toLowerCase().contains(mediaName)) // || mediaPath.toLowerCase().contains(InfoTool.getFileName(playedFileAbsolutePath))
											.anyMatch(playedFile -> new File(playedFile.getPath()).length() == mediaFileLength) ? -1 : -2);
					
					//Set timesPlayed
					Main.playedSongs.getSet().stream().filter(playedFile -> media.getFilePath().equals(playedFile.getPath())).findFirst().ifPresent(playedFile -> {
						media.timesPlayedProperty().set(playedFile.getTimesPlayed());
					});
				}
				
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
	private static void setPlayStatus(Media m , int playStatus) {
		Platform.runLater(() -> m.setPlayedStatus(playStatus));
	}
	
}
