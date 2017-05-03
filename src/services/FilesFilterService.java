/*
 * 
 */
package services;

import static application.Main.libraryMode;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import application.Main;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;
import smartcontroller.SmartController;
import streamplayer.ThreadFactoryWithNamePrefix;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * The Class FileFilterThread.
 */
public class FilesFilterService {
    /**
     * It is true when the Thread is running.
     */
    private volatile boolean threadIsRunning;

    private BooleanProperty threadStopped = new SimpleBooleanProperty(false);

    /**
     * This executor service is used in order the playerState events to be executed in an order
     */
    private ExecutorService executors = Executors.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix("Files Filter Service"));

    /**
     * Start the Thread.
     *
     */
    public void start() {
	Runnable runnable = () -> {
	    try {
		Platform.runLater(() -> threadStopped.set(false));
		threadIsRunning = true;
		//int counter = 0;
		// -----Continuously run
		while (threadIsRunning) {

		    // Find the correct controller
		    startFilteringControllers();

		    // Sleep some milliseconds
		    Thread.sleep(1000);
		    //		    ++counter;
		    //		    if (counter == 4)
		    //			break;
		}
	    } catch (Exception ex) {
		Main.logger.log(Level.INFO, "", ex);
	    } finally {
		Platform.runLater(
			() -> ActionTool.showNotification("Message", "FilesFilterThread ->exited", Duration.millis(1500), NotificationType.ERROR));
		System.out.println("FileFilterService Thread exited!!!");
		Platform.runLater(() -> threadStopped.set(true));
	    }
	};
	executors.execute(runnable);
	threadStopped.addListener((observable, oldValue, newValue) -> {
	    //Restart it if it has stopped
	    if (newValue)
		executors.execute(runnable);
	});
    }

    /**
     * Starts filtering all the needed application SmartControllers
     */
    private void startFilteringControllers() {

	//First filter the selected Opened Library Controller
	filterController(
		libraryMode.multipleLibs.getSelectedLibrary() == null ? null : libraryMode.multipleLibs.getSelectedLibrary().getSmartController());

	//The filter the SearchWindow Controller
	filterController(Main.searchWindow.getSmartController());

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

	//System.out.println("Checking elements.....")
	// controller is free && threadIsRunning?
	if (controllerIsFree[0] && threadIsRunning)
	    controller.getItemsObservableList().stream().forEach(media -> {
		if (!threadIsRunning)
		    return;

		// File exists?
		Platform.runLater(() -> media.fileExistsProperty().set(Paths.get(media.getFilePath()).toFile().exists()));

		// Item has been played?
		if (Main.settingsWindow.getPlayListsSettingsController().getPlayedFilesDetectionGroup().getToggles().get(0).isSelected()) {
		    // System.out.println("Entered 1")
		    //Check bases on File Absolute Path
		    if (Main.playedSongs.containsFile(media.getFilePath()))
			Platform.runLater(() -> media.setMediaPlayed(true));
		    else
			Platform.runLater(() -> media.setMediaPlayed(false));
		} else {
		    // System.out.println("Entered 2")
		    String mediaName = InfoTool.getFileName(media.getFilePath());
		    long mediaFileLength = new File(media.getFilePath()).length();
		    //Check based on FileName and FileLength -> both must be equal
		    if (Main.playedSongs.getSet().stream().filter(file -> file.contains(mediaName))
			    .anyMatch(file -> new File(file).length() == mediaFileLength))
			Platform.runLater(() -> media.setMediaPlayed(true));
		    else
			Platform.runLater(() -> media.setMediaPlayed(false));

		}
	    });

    }

}
