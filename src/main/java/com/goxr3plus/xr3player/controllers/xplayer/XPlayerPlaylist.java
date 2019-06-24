/*
 * 
 */
package com.goxr3plus.xr3player.controllers.xplayer;

import java.io.IOException;
import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * Represents the PlayList of a Specific Player.
 *
 * @author GOXR3PLUS
 */
public class XPlayerPlaylist extends StackPane {

	// ----------------------------------------------------------

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button play;

	@FXML
	private Region region;

	@FXML
	private ProgressIndicator progressSpinner;

	@FXML
	private HBox horizontalBox;

	@FXML
	private Button previousButton;

	@FXML
	private Button stopPlayingList;

	@FXML
	private Button nextButton;

	// --------------------------------------------------------------------------------

	/** The controller. */
	private SmartController smartController;

	/** The play service. */
	// private PlayListService playService = new PlayListService();;

	/** The x player UI. */
	XPlayerController xPlayerUI;

	/**
	 * Constructor.
	 *
	 * @param xPlayerUI    the x player UI
	 */
	public XPlayerPlaylist(final XPlayerController xPlayerUI) {
		this.xPlayerUI = xPlayerUI;

		// ------------------------------FXMLLoader-----------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.PLAYERS_FXMLS + "XPlayerPlaylist.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}

	}

	/** Called as soon as the fxml has been loaded */
	@FXML
	public void initialize() {

		// progressSpinner
		region.visibleProperty().bind(progressSpinner.visibleProperty());
		// region.visibleProperty().bind(controller.getRegion().visibleProperty());
		// progressSpinner.visibleProperty().bind(controller.getRegion().visibleProperty());

		// play
		// play.setDisable(false);
		// play.setOnAction(a -> playService.startService());

		// horizontalBox
		horizontalBox.visibleProperty().bind(stopPlayingList.visibleProperty());

		// stopPlayingList
		stopPlayingList.setVisible(false);
		// stopPlayingList.setOnAction(a -> playService.stopService());

		// previousButton
		previousButton.visibleProperty().bind(stopPlayingList.visibleProperty());
		// previousButton.setOnAction(a -> xPlayerUI.getRadialMenu().goPrevious());
		// previousButton.disableProperty().bind(xPlayerUI.getRadialMenu().previous.disabledProperty());

		// nextButton
		nextButton.visibleProperty().bind(stopPlayingList.visibleProperty());
		// nextButton.setOnAction(a -> xPlayerUI.getRadialMenu().goNext());
		// nextButton.disableProperty().bind(xPlayerUI.getRadialMenu().next.disabledProperty());

		// SmartController
		smartController = new SmartController(Genre.LIBRARYMEDIA, "XPlayer " + xPlayerUI.getKey() + " PlayList",
				"XPPL" + xPlayerUI.getKey());
		borderPane.setCenter(smartController);
	}

	/**
	 * Determines if the user has chosen the play list to be played.
	 *
	 * @return true, if is play the list active
	 */
	public boolean isPlayTheListActive() {
		return stopPlayingList.isVisible();
	}

	/**
	 * @return the controller
	 */
	public SmartController getSmartController() {
		return smartController;
	}

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							PLAY SERVICE
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
//	/**
//	 * This class implements the algorithm of playing the play list. !!!NEEDS TO BE FIXED IT DOESN'T WORK AT ALL!!!
//	 *
//	 * @author GOXR3PLUS
//	 */
//	@Deprecated
//	private class PlayListService extends Service<Void> {
//		
//		/** The song. */
//		private Audio song;
//		
//		/** The counter. */
//		private int counter;
//		
//		/**
//		 * Constructor.
//		 */
//		public PlayListService() {
//			setOnSucceeded(s -> done());
//			setOnFailed(c -> done());
//			setOnCancelled(c -> done());
//		}
//		
//		/**
//		 * Starts the service.
//		 */
//		public void startService() {
//			if (!isRunning() && !getSmartController().getItemsObservableList().isEmpty()) {
//				stopPlayingList.setVisible(true);
//				progressSpinner.setVisible(true);
//				progressSpinner.progressProperty().bind(progressProperty());
//			//	xPlayerUI.getRadialMenu().next.setDisable(false);
//			//	xPlayerUI.getRadialMenu().previous.setDisable(false);
//				reset();
//				start();
//			}
//		}
//		
//		/**
//		 * Stops the service.
//		 */
//		public void stopService() {
//			if (isRunning()) {
//				cancel();
//				done();
//			}
//		}
//		
//		/**
//		 * Done.
//		 */
//		private void done() {
//			progressSpinner.progressProperty().unbind();
//			progressSpinner.setProgress(-1);
//			progressSpinner.setVisible(false);
//			stopPlayingList.setVisible(false);
//			xPlayerUI.getRadialMenu().next.setDisable(true);
//			xPlayerUI.getRadialMenu().previous.setDisable(true);
//			song = null;
//		}
//		
//		@Override
//		protected Task<Void> createTask() {
//			return new Task<Void>() {
//				@Override
//				protected Void call() throws Exception {
//					
//					counter = 1;
//					int totalItems = getSmartController().getItemsObservableList().size();
//					
//					// loop
//					while (!isCancelled()) {
//						
//						// Play song
//						// Synchronize with javaFX thread
//						song = (Audio) getSmartController().getItemsObservableList().get(counter - 1);
//						CountDownLatch latch = new CountDownLatch(1);
//						Platform.runLater(() -> {
//							
//							// Enable disable next
//							if (counter == totalItems)
//								xPlayerUI.getRadialMenu().next.setDisable(true);
//							else
//								xPlayerUI.getRadialMenu().next.setDisable(false);
//							
//							// Enable disable previous
//							if (counter == 1)
//								xPlayerUI.getRadialMenu().previous.setDisable(true);
//							else
//								xPlayerUI.getRadialMenu().previous.setDisable(false);
//							
//							// playSong
//							xPlayerUI.playSong(song.getFilePath());
//							
//							latch.countDown();
//						});
//						latch.await();
//						
//						// Update the progress
//						updateProgress(counter, totalItems);
//						
//						// Check is is playing some song or is paused
//						while (xPlayerUI.getxPlayer().isPausedOrPlaying() || xPlayerUI.getxPlayer().isSeeking()) {
//							
//							// Check if nextHasBeenPressed or
//							// previousHasBeenPressed
//							if (xPlayerUI.getRadialMenu().nextHasBeenPressed() || xPlayerUI.getRadialMenu().previousHasBeenPressed())
//								break;
//							
//							// Get out if cancelled
//							if (isCancelled())
//								break;
//							
//							// Sleep
//							Thread.sleep(500);
//						} // --------end of while
//						
//						// Get out if cancelled
//						if (isCancelled())
//							break;
//						
//						// Check if next has been pressed
//						if (xPlayerUI.getRadialMenu().nextHasBeenPressed()) {
//							++counter;
//							xPlayerUI.getRadialMenu().resetPreviousAndNextIfPressed();
//							
//							// Check if previous has been pressed
//						} else if (xPlayerUI.getRadialMenu().previousHasBeenPressed()) {
//							--counter;
//							xPlayerUI.getRadialMenu().resetPreviousAndNextIfPressed();
//							
//							// if counter<listSize
//						} else if (counter < totalItems) {
//							++counter;
//							// else stop Service
//						} else if (counter == totalItems)
//							break;
//						
//					} // -------end of while
//					
//					return null;
//				}
//			};
//		}
//		
//	}
//	
}
