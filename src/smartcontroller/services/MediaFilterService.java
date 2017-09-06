/*
 * 
 */
package smartcontroller.services;

import static application.Main.libraryMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import application.Main;
import application.tools.InfoTool;
import application.tools.JavaFXTools;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import smartcontroller.SmartController;
import smartcontroller.media.Media;
import xplayer.streamplayer.ThreadFactoryWithNamePrefix;

/**
 * The Class FileFilterThread.
 */
public class MediaFilterService {
	
	/**
	 * If is true then the Thread has stopped , so i restart it again...
	 */
	private final BooleanProperty threadStopped = new SimpleBooleanProperty(false);
	
	/**
	 * This executor service is used in order the playerState events to be executed in an order
	 */
	private final ExecutorService executors = Executors.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix("Files Filter Service"));
	
	/**
	 * Start the Thread.
	 *
	 */
	public void start() {
		Runnable runnable = () -> {
			try {
				Platform.runLater(() -> threadStopped.set(false));
				
				//Run forever , except if i interrupt it ;)
				for (;; Thread.sleep(1000))
					startFilteringControllers();
				
			} catch (Exception ex) {
				Main.logger.log(Level.INFO, "", ex);
			} finally {
				//Platform.runLater(
				//		() -> ActionTool.showNotification("Message", "FilesFilterThread ->exited", Duration.millis(1500), NotificationType.ERROR))
				System.out.println("FileFilterService Thread exited!!!");
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
		
		//Filter Selected Opened Library SmartController
		libraryMode.multipleLibs.getSelectedLibrary().ifPresent(selectedLibrary -> filterController(selectedLibrary.getSmartController()));
		
		//Filter XPlayer PlayLists SmartControllers
		Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()).forEach(this::filterController);
		
		//Filter Emotion Lists SmartControllers
		Main.emotionsTabPane.getTabPane().getTabs().forEach(tab -> filterController((SmartController) tab.getContent()));
		
		//Filter SearchWindow SmartController
		filterController(Main.searchWindowSmartController);
		
	}
	
	/**
	 * Checks the Elements of the SmartController using some conditions.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception [[SuppressWarningsSpartan]]
	 */
	private void filterController(SmartController controller) {
		
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
			controller.getItemsObservableList().stream().forEach(media -> {
				
				// ---------File exists--------?
				Platform.runLater(() -> media.fileExistsProperty().set(Paths.get(media.getFilePath()).toFile().exists()));
				
				if (mode == 0) { // Check based on File Content [ The content must be absolutely the same ] 
					
					//Go
					File mediaPath = new File(media.getFilePath());
					setMediaPlayed(media, Main.playedSongs.getSet().stream().anyMatch(playedFileAbsolutePath -> {
						try {
							return FileUtils.contentEquals(new File(playedFileAbsolutePath), mediaPath);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						return false;
					}));
				} else if (mode == 1) { //Check based on FileName and FileLength -> both must be equal
					
					//Go
					String mediaName = InfoTool.getFileName(media.getFilePath()).toLowerCase();
					// String mediaPath = media.getFilePath().toLowerCase()
					long mediaFileLength = new File(media.getFilePath()).length();
					setMediaPlayed(media, Main.playedSongs.getSet().stream().filter(playedFileAbsolutePath -> playedFileAbsolutePath.toLowerCase().contains(mediaName)) // || mediaPath.toLowerCase().contains(InfoTool.getFileName(playedFileAbsolutePath))
							.anyMatch(playedFile -> new File(playedFile).length() == mediaFileLength));
					
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
	private static void setMediaPlayed(Media m , boolean played) {
		Platform.runLater(() -> m.setMediaPlayed(played));
	}
	
}
