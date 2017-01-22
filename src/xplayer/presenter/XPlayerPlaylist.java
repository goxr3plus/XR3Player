/*
 * 
 */
package xplayer.presenter;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import media.Audio;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * Represents the PlayList of a Specific Player.
 *
 * @author GOXR3PLUS
 */
public class XPlayerPlaylist extends StackPane {

    /** The border pane. */
    @FXML
    private BorderPane borderPane;

    /** The play. */
    @FXML
    private Button play;

    /** The region. */
    @FXML
    private Region region;

    /** The progress spinner. */
    @FXML
    private ProgressIndicator progressSpinner;

    /** The horizontal box. */
    @FXML
    private HBox horizontalBox;

    /** The previous button. */
    @FXML
    private Button previousButton;

    /** The stop playing list. */
    @FXML
    private Button stopPlayingList;

    /** The next button. */
    @FXML
    private Button nextButton;

    /** The controller. */
    public SmartController controller;

    /** The play service. */
    private PlayService playService;

    /** The maximum items. */
    // Variables
    int maximumItems;

    /** The notification. */
    // Notification
    Notifications notification = Notifications.create().title("Maximum elements added")
	    .text("You have added the maximum number of elements allowed in the PlayList.").darkStyle();

    /** The x player UI. */
    XPlayerController xPlayerUI;

    /**
     * Constructor.
     *
     * @param maximumItems
     *            maximumItems allowed to be inserted into the playList
     * @param xPlayerUI
     *            the x player UI
     */
    public XPlayerPlaylist(final int maximumItems, final XPlayerController xPlayerUI) {
	this.maximumItems = maximumItems;
	this.xPlayerUI = xPlayerUI;

	// Initialize controller
	playService = new PlayService();
	controller = new SmartController(Genre.XPLAYLISTSONG, "", "XPPL" + xPlayerUI.getKey());
	controller.loadService.startService(false, true);

	// FXMLLoader
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "XPlayerPlaylist.fxml"));
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

//	controller.setOnDragOver(over -> {
//	    if (Main.dragOwner != Main.DragOwner.XPLAYLISTSONG)// &&
//							       // controller.totalInDataBase
//							       // <
//							       // maximumItems)
//		over.acceptTransferModes(TransferMode.LINK);
//	});
//	controller.setOnDragDropped(drop -> {
//	    // if?
//	    if (drop.getDragboard().hasFiles() && controller.isFree(true))
//		controller.inputService.start(drop.getDragboard().getFiles());
//
//	    drop.setDropCompleted(true);
//	});

	// BorderPane
	borderPane.setCenter(controller);

	// progressSpinner
	region.visibleProperty().bind(progressSpinner.visibleProperty());
	// region.visibleProperty().bind(controller.getRegion().visibleProperty());
	// progressSpinner.visibleProperty().bind(controller.getRegion().visibleProperty());

	// play
	play.setOnAction(a -> playService.startService());

	// horizontalBox
	horizontalBox.visibleProperty().bind(stopPlayingList.visibleProperty());

	// stopPlayingList
	stopPlayingList.setVisible(false);
	stopPlayingList.setOnAction(a -> playService.stopService());

	// previousButton
	previousButton.visibleProperty().bind(stopPlayingList.visibleProperty());
	previousButton.setOnAction(a -> xPlayerUI.radialMenu.goPrevious());
	previousButton.disableProperty().bind(xPlayerUI.radialMenu.previous.disabledProperty());

	// nextButton
	nextButton.visibleProperty().bind(stopPlayingList.visibleProperty());
	nextButton.setOnAction(a -> xPlayerUI.radialMenu.goNext());
	nextButton.disableProperty().bind(xPlayerUI.radialMenu.next.disabledProperty());

    }

    /**
     * Determines if the user has chosen the play list to be played.
     *
     * @return true, if is play the list active
     */
    public boolean isPlayTheListActive() {
	return stopPlayingList.isVisible();
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
    /**
     * This class implements the algorithm of playing the play list.
     *
     * @author SuperGoliath
     */
    private class PlayService extends Service<Void> {

	/** The song. */
	private Audio song;

	/** The counter. */
	private int counter;

	/**
	 * Constructor.
	 */
	public PlayService() {
	    setOnSucceeded(s -> done());
	    setOnFailed(c -> done());
	    setOnCancelled(c -> done());
	}

	/**
	 * Starts the service.
	 */
	public void startService() {
	    if (!isRunning() && !controller.observableList.isEmpty()) {
		stopPlayingList.setVisible(true);
		progressSpinner.setVisible(true);
		progressSpinner.progressProperty().bind(progressProperty());
		xPlayerUI.radialMenu.next.setDisable(false);
		xPlayerUI.radialMenu.previous.setDisable(false);
		reset();
		start();
	    }
	}

	/**
	 * Stops the service.
	 */
	public void stopService() {
	    if (isRunning()) {
		cancel();
		done();
	    }
	}

	/**
	 * Done.
	 */
	private void done() {
	    progressSpinner.progressProperty().unbind();
	    progressSpinner.setProgress(-1);
	    progressSpinner.setVisible(false);
	    stopPlayingList.setVisible(false);
	    xPlayerUI.radialMenu.next.setDisable(true);
	    xPlayerUI.radialMenu.previous.setDisable(true);
	    song = null;
	}

	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {
		@Override
		protected Void call() throws Exception {

		    counter = 1;
		    int totalItems = controller.observableList.size();

		    // loop
		    while (!isCancelled()) {

			// Play song
			// Synchronize with javaFX thread
			song = (Audio) controller.observableList.get(counter - 1);
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {

			    // Enable disable next
			    if (counter == totalItems)
				xPlayerUI.radialMenu.next.setDisable(true);
			    else
				xPlayerUI.radialMenu.next.setDisable(false);

			    // Enable disable previous
			    if (counter == 1)
				xPlayerUI.radialMenu.previous.setDisable(true);
			    else
				xPlayerUI.radialMenu.previous.setDisable(false);

			    // playSong
			    xPlayerUI.playSong(song.getFilePath());

			    latch.countDown();
			});
			latch.await();

			// Update the progress
			updateProgress(counter, totalItems);

			// Check is is playing some song or is paused
			while (xPlayerUI.xPlayer.isPausedOrPlaying() || xPlayerUI.xPlayer.isSeeking()) {

			    // Check if nextHasBeenPressed or
			    // previousHasBeenPressed
			    if (xPlayerUI.radialMenu.nextHasBeenPressed()
				    || xPlayerUI.radialMenu.previousHasBeenPressed())
				break;

			    // Get out if cancelled
			    if (isCancelled())
				break;

			    // Sleep
			    Thread.sleep(500);
			} // --------end of while

			// Get out if cancelled
			if (isCancelled())
			    break;

			// Check if next has been pressed
			if (xPlayerUI.radialMenu.nextHasBeenPressed()) {
			    ++counter;
			    xPlayerUI.radialMenu.resetPreviousAndNextIfPressed();

			    // Check if previous has been pressed
			} else if (xPlayerUI.radialMenu.previousHasBeenPressed()) {
			    --counter;
			    xPlayerUI.radialMenu.resetPreviousAndNextIfPressed();

			    // if counter<listSize
			} else if (counter < totalItems) {
			    ++counter;
			    // else stop Service
			} else if (counter == totalItems)
			    break;

		    } // -------end of while

		    return null;
		}
	    };
	}

    }

}
