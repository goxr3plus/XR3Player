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

    /** The thread. */
    //Thread thread = new Thread();

    /** The controller. */
    SmartController controller;

    /** The controller is free. */
    boolean controllerIsFree;

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
     * The Enum FilterMode.
     *
     * @author GOXR3PLUS
     */
    public enum FilterMode {

	/** The multiplelibs. */
	MULTIPLELIBS,
	/** The xplayer0. */
	XPLAYER0,
	/** The xplayer1. */
	XPLAYER1,
	/** The xplayer2. */
	XPLAYER2;
    }

    /**
     * Start the Thread.
     *
     * @param filterMode
     *            the filter mode
     */
    public void start(FilterMode filterMode) {
	Runnable runnable = () -> {
	    try {
		Platform.runLater(() -> threadStopped.set(false));
		threadIsRunning = true;
		//int counter = 0;
		// -----Continuously run
		while (threadIsRunning) {

		    // Find the correct controller
		    getCorrectController(filterMode);

		    // Check all the elements of the Controller
		    checkElements();

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
     * Calling this method will make the Thread stop after a while
     */
    //    public void stop() {
    //	threadIsRunning = false;
    //    }

    /**
     * Get the correct SmartController based on the FilterMode.
     *
     * @param filterMode
     *            the filter mode
     */
    private void getCorrectController(FilterMode filterMode) {
	switch (filterMode) {
	case MULTIPLELIBS:
	    controller = (libraryMode.multipleLibs.getSelectedLibrary() != null) ? libraryMode.multipleLibs.getSelectedLibrary().getSmartController()
		    : null;
	    break;
	//			case XPLAYER0:
	//				controller = Main.xPlayersList.getXPlayerController(0).xPlayList.controller;
	//				break;
	//			case XPLAYER1:
	//				controller = Main.xPlayersList.getXPlayerController(1).xPlayList.controller;
	//				break;
	//			case XPLAYER2:
	//				controller = Main.xPlayersList.getXPlayerController(2).xPlayList.controller;
	//				break;
	default:
	    controller = null;
	}
    }

    /**
     * Checks the Elements of the SmartController using some conditions.
     * 
     * @throws InterruptedException
     *             the interrupted exception [[SuppressWarningsSpartan]]
     */
    private void checkElements() throws InterruptedException {

	// Don't enter if controller is null
	if (controller == null)
	    return;

	// Synchronize with javaFX thread
	CountDownLatch latch = new CountDownLatch(1);
	Platform.runLater(() -> {
	    controllerIsFree = (controller != null) && controller.isFree(false);
	    latch.countDown();
	});
	latch.await();

	//System.out.println("Checking elements.....")
	// controller is free && threadIsRunning?
	if (controllerIsFree && threadIsRunning)
	    controller.itemsObservableList.stream().forEach(media -> {
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
