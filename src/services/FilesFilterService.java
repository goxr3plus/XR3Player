/*
 * 
 */
package services;

import static application.Main.libraryMode;

import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import smartcontroller.SmartController;

/**
 * The Class FileFilterThread.
 */
public class FilesFilterService {
	
	/** The thread. */
	Thread thread = new Thread();
	
	/** The controller. */
	SmartController controller = null;
	
	/** The controller is free. */
	boolean controllerIsFree;
	
	/**
	 * It is true when the Thread is running.
	 */
	private volatile boolean threadIsRunning = false;
	
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
	 * @param filterMode the filter mode
	 */
	public void start(FilterMode filterMode) {
		if (thread.isAlive())
			return;
		
		// Create a new Thread
		thread = new Thread(() -> {
			try {
				threadIsRunning = true;
				// -----Continuously run
				while (threadIsRunning) {
					
					// Find the correct controller
					getCorrectController(filterMode);
					
					// Check all the elements of the Controller
					checkElements();
					
					// Sleep some milliseconds
					Thread.sleep(800);
				}
			} catch (Exception ex) {
				Main.logger.log(Level.INFO, "", ex);
			} finally {
				Platform.runLater(Notifications.create().text("FileFilterThread Successfully ->exited")::showError);
			}
			
		});
		
		thread.start();
		
	}
	
	/**
	 * Calling this method will make the Thread stop after a while
	 */
	public void stop() {
		threadIsRunning = false;
	}
	
	/**
	 * Get the correct SmartController based on the FilterMode.
	 *
	 * @param filterMode the filter mode
	 */
	private void getCorrectController(FilterMode filterMode) {
		switch (filterMode) {
			case MULTIPLELIBS:
				controller = ( libraryMode.multipleLibs.getSelectedLibrary() != null )
				        ? libraryMode.multipleLibs.getSelectedLibrary().getSmartController()
				        : null;
				break;
			case XPLAYER0:
				controller = Main.xPlayersList.getXPlayerUI(0).xPlayList.controller;
				break;
			case XPLAYER1:
				controller = Main.xPlayersList.getXPlayerUI(1).xPlayList.controller;
				break;
			case XPLAYER2:
				controller = Main.xPlayersList.getXPlayerUI(2).xPlayList.controller;
				break;
			default:
				controller = null;
		}
	}
	
	/**
	 * Checks the Elements of the SmartController using some conditions.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	private void checkElements() throws InterruptedException {
		
		// Don't enter if controller is null
		if (controller != null) {
			
			// Synchronize with javaFX thread
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				controllerIsFree = controller != null && controller.isFree(false);
				latch.countDown();
			});
			latch.await();
			
			// controller is free && threadIsRunning?
			if (controllerIsFree && threadIsRunning)
				controller.observableList.stream().forEach(media -> {
					if (!threadIsRunning)
						return;
					
					// File exists?
					Platform.runLater(
					        () -> media.fileExistsProperty().set(Paths.get(media.getFilePath()).toFile().exists()));
					
					// Item has been played?
					if (Main.playedSongs.contains(media.getFilePath()))
						Platform.runLater(media::setMediaPlayed);
				});
		}
	}
	
}
