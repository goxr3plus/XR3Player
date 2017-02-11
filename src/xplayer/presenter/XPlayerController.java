/*
 * 
 */
package xplayer.presenter;

import static application.Main.djMode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import customnodes.Marquee;
import disc.DJDisc;
import disc.DJDiscListener;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import streamplayer.StreamPlayer.Status;
import streamplayer.StreamPlayerEvent;
import streamplayer.StreamPlayerException;
import streamplayer.StreamPlayerListener;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import visualizer.view.VisualizerStackController;
import visualizer.view.VisualizerWindowController;
import visualizer.view.XPlayerVisualizer;
import xplayer.model.XPlayer;
import xplayer.model.XPlayerModel;

/**
 * Represents the graphical interface for the deck.
 *
 * @author GOXR3PLUS
 */
public class XPlayerController extends StackPane implements DJDiscListener, StreamPlayerListener {

    /**
     * The class Logger
     */
    private Logger logger = Logger.getLogger(getClass().getName());

    // -----------------------------------------------------

    /** The container. */
    @FXML
    private GridPane container;

    /** The top grid pane. */
    @FXML
    private GridPane topGridPane;

    /** The visualizer stack pane. */
    @FXML
    private StackPane visualizerStackPane;

    /**
     * Checked = Visualizer is Visible[running]
     */
    @FXML
    private ToggleButton visualizerVisible;

    /** The maximize visualizer. */
    @FXML
    private Button maximizeVisualizer;

    /**
     * This label exists to provide information when the Visualizer in invisible
     */
    @FXML
    private Label visualizerVisibleLabel;

    /**
     * This Label is visible when visualizer is maximized-ec in VisualizerWindow
     */
    @FXML
    private Label visualizerMaximizedLabel;

    /**
     * This Label is visible when the player is stopped || paused and displays that status
     */
    @FXML
    private Label playerStatusLabel;

    /** The Top right V box. */
    @FXML
    private VBox topRightVBox;

    @FXML
    private StackPane mediaFileStackPane;

    /**
     * The StackPane of the Disk
     */
    @FXML
    private StackPane diskStackPane;

    /**
     * Open the playing Media File Folder
     */
    @FXML
    private Button openMediaFileFolder;

    /** The Media name label. */
    @FXML
    private Label mediaNameLabel;

    /** The bottom grid pane. */
    @FXML
    private GridPane bottomGridPane;

    /** The settings toggle. */
    @FXML
    JFXToggleButton settingsToggle;

    /**
     * Opens the default system explorer so the user can select a file to play
     */
    @FXML
    private Button openFileButton;

    /** The fx region. */
    @FXML
    private Region fxRegion;

    @FXML
    private Label bugLabel;

    /** The fx spinner. */
    @FXML
    private JFXSpinner fxSpinner;

    /** The fx label. */
    @FXML
    private Label fxLabel;

    @FXML
    private Label topInfoLabel;

    // -----------------------------------------------------------------------------

    private static final ImageView eye = InfoTool.getImageViewFromDocuments("eye.png");
    private static final ImageView eyeDisabled = InfoTool.getImageViewFromDocuments("eyeDisabled.png");

    /** The x player settings controller. */
    public XPlayerSettingsController xPlayerSettingsController;

    /** The x player model. */
    public XPlayerModel xPlayerModel;

    /** The x player. */
    public XPlayer xPlayer;

    /** The x play list. */
    public XPlayerPlaylist xPlayList;

    /** The radial menu. */
    public XPlayerRadialMenu radialMenu;

    /** The visualizer window. */
    public VisualizerWindowController visualizerWindow;

    /**
     * This controller contains a Visualizer and a Label which describes every time (for some milliseconds) which type of visualizer is being
     * displayed (for example [ Oscilloscope , Rosette , Spectrum Bars etc...]);
     */
    public VisualizerStackController visualizerStackController = new VisualizerStackController();

    /** The visualizer. */
    public XPlayerVisualizer visualizer;

    /** The equalizer. */
    XPlayerEqualizer equalizer;

    /** The analyser box. */
    // AnalyserBox analyserBox ;

    /** The disc. */
    public DJDisc disc;

    private final Marquee mediaFileMarquee = new Marquee();

    // ---Services---------------------

    /** The seek service. */
    public SeekService seekService = new SeekService();

    /** The play service. */
    private PlayService playService = new PlayService();

    // ---Variables-----------------------

    /** The key. */
    private int key;

    /** The disc is being mouse dragged */
    private boolean discIsDragging = false;

    private static Image noSeek = InfoTool.getImageFromDocuments(
	    ImageCursor.getBestSize(64, 64).getWidth() >= 64.00 ? "Private-64.png" : "Private-32.png");
    private static ImageCursor noSeekCursor = new ImageCursor(noSeek, noSeek.getWidth() / 2, noSeek.getHeight() / 2);

    /**
     * Constructor.
     *
     * @param key
     *            The key that is identifying this player
     */
    public XPlayerController(int key) {
	this.key = key;

	// ----------------------------------- FXMLLoader
	// -------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "XPlayerController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "XPlayerController FXML can't be loaded!", ex);
	}

    }

    /** Called as soon as the .fxml has been loaded */
    @FXML
    private void initialize() {

	// -----XPlayer and XPlayerModel-------------
	xPlayerModel = new XPlayerModel();
	xPlayer = new XPlayer();
	xPlayer.addStreamPlayerListener(this);

	// -----Important-------------
	radialMenu = new XPlayerRadialMenu(key);
	xPlayList = new XPlayerPlaylist(25, this);
	visualizerWindow = new VisualizerWindowController(this);
	xPlayerSettingsController = new XPlayerSettingsController(this);

	// Styling
	setStyle("-fx-background-image:url('/image/deckBackground.jpg');  -fx-background-size:stretch;");

	// Listeners
	container.setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
	container.setOnDragDropped(drop -> dragDrop(drop, 2));

	// settingsToggle
	getChildren().add(xPlayerSettingsController);
	xPlayerSettingsController.visibleProperty().bind(settingsToggle.selectedProperty());

	// fxRegion,fxSpinner
	fxRegion.setVisible(false);
	bugLabel.visibleProperty().bind(fxRegion.visibleProperty());
	fxSpinner.visibleProperty().bind(fxRegion.visibleProperty());
	fxLabel.visibleProperty().bind(fxRegion.visibleProperty());

	// mediaFileStackPane
	mediaFileStackPane.getChildren().add(mediaFileMarquee);
	mediaFileMarquee.toBack();

	// openMediaFileFolder
	// openMediaFileFolder.visibleProperty().bind(mediaFileStackPane.hoverProperty())
	openMediaFileFolder.setOnAction(action -> ActionTool.openFileLocation(xPlayerModel.songPathProperty().get()));

	// openFileButton
	openFileButton.setOnAction(action -> {
	    File file = Main.specialChooser.selectSongFile(Main.window);
	    if (file != null)
		playSong(file.getAbsolutePath());
	});

	// topInfoLabel
	topInfoLabel.setText("Player : [ " + this.getKey() + " ]");
    }

    /**
     * Can be called from different classes to implement the dragDrop for their XPlayer.
     *
     * @param dragDrop
     *            the drag drop
     * @param number
     *            the number
     */
    public void dragDrop(DragEvent dragDrop, int number) {
	// Keeping the absolute path
	String absolutePath;

	// File?
	for (File file : dragDrop.getDragboard().getFiles()) {
	    absolutePath = file.getAbsolutePath();
	    if (file.isFile() && InfoTool.isAudioSupported(absolutePath)) {
		// Ask Question?
		if (xPlayer.isPausedOrPlaying() && xPlayerSettingsController.askSecurityQuestion.isSelected()) {
		    if (ActionTool.doQuestion(
			    "A song is already playing on this deck.\n Are you sure you want to replace it?",
			    visualizerWindow.getStage().isShowing() ? visualizerWindow : XPlayerController.this))
			playSong(absolutePath);
		} else
		    playSong(absolutePath);
		break;
	    }
	}

	// // URL?
	// if (xPlayer.isPausedOrPlaying()) {
	// // OK?
	// if (ActionTool
	// .doQuestion("A song is already playing on this deck.\n Are you
	// sure you want to replace it?"))
	// xPlayer.playSong(dragDrop.getDragboard().getUrl().toString());
	// } else
	// xPlayer.playSong(dragDrop.getDragboard().getUrl().toString());

	dragDrop.setDropCompleted(true);
    }

    /**
     * Returns the volume level of the player.
     *
     * @return the volume
     */
    public int getVolume() {
	return disc.getVolume();
    }

    /**
     * Returns the Disc Color.
     *
     * @return the disc color
     */
    public Color getDiscColor() {
	return disc.getArcColor();
    }

    /**
     * Adjust the volume to a given value.
     *
     * @param value
     *            the value
     */
    public void adjustVolume(int value) {
	disc.setVolume(disc.getVolume() + value);
    }

    /**
     * Adjust the volume to the maximum value.
     */
    public void maximizeVolume() {
	disc.setVolume(disc.getMaximumVolume());
    }

    /**
     * Adjust the volume to the minimum value.
     */
    public void minimizeVolume() {
	disc.setVolume(0);
    }

    /**
     * Set the volume to this value.
     *
     * @param value
     *            the new volume
     */
    public void setVolume(int value) {
	disc.setVolume(value);
    }

    /**
     * Returns the key of the player.
     *
     * @return The Key of the Player
     */
    public int getKey() {
	return key;
    }

    /**
     * Used by resume method.
     */
    public void resumeCode() {
	disc.stopFade();
	if (!playService.isDiscImageNull())
	    disc.resumeRotation();
	visualizer.startVisualizer();
	radialMenu.resumeOrPause.setGraphic(radialMenu.pauseImageView);
    }

    /**
     * Used by pause method.
     */
    private void pauseCode() {
	disc.playFade();
	disc.pauseRotation();
	visualizer.stopVisualizer();
	radialMenu.resumeOrPause.setGraphic(radialMenu.playImageView);
    }

    /**
     * Controls the volume of the player.
     */
    public void controlVolume() {

	try {
	    if (key == 1 || key == 2) {
		if (djMode.balancer.getVolume() < 100) { // <100

		    Main.xPlayersList.getXPlayer(1).setGain(
			    ((Main.xPlayersList.getXPlayerUI(1).getVolume() / 100.00) * (djMode.balancer.getVolume()))
				    / 100.00);
		    Main.xPlayersList.getXPlayer(2).setGain(Main.xPlayersList.getXPlayerUI(2).getVolume() / 100.00);

		} else if (djMode.balancer.getVolume() == 100) { // ==100

		    Main.xPlayersList.getXPlayer(1).setGain(Main.xPlayersList.getXPlayerUI(1).getVolume() / 100.00);
		    Main.xPlayersList.getXPlayer(2).setGain(Main.xPlayersList.getXPlayerUI(2).getVolume() / 100.00);

		} else if (djMode.balancer.getVolume() > 100) { // >100

		    Main.xPlayersList.getXPlayer(1).setGain(Main.xPlayersList.getXPlayerUI(1).getVolume() / 100.00);
		    Main.xPlayersList.getXPlayer(2).setGain(((Main.xPlayersList.getXPlayerUI(2).getVolume() / 100.00)
			    * (200 - djMode.balancer.getVolume())) / 100.00);

		}
	    } else if (key == 0) {
		xPlayer.setGain((double) disc.getVolume() / 100.00);
	    }

	} catch (Exception ex) {

	    logger.log(Level.INFO, "\n", ex);
	}

    }

    /**
     * Checks if the djDisc is being dragged by user.
     *
     * @return True if disc is being dragged
     */
    public boolean isDiscBeingDragged() {
	return discIsDragging;
    }

    /**
     * This method is Used by VisualizerWindow class.
     */
    public void reAddVisualizer() {
	visualizerStackPane.getChildren().add(0, visualizerStackController);
    }

    /**
     * This method is making the visualizer of the player.
     *
     * @param side
     *            the side
     */
    public void makeTheVisualizer(Side side) {

	// Visualizer
	visualizer = new XPlayerVisualizer(this);
	visualizer.setShowFPS(xPlayerSettingsController.showFPS.selectedProperty().get());

	// When displayMode is being updated
	visualizer.displayMode.addListener((observable, oldValue, newValue) -> {
	    visualizerWindow.visualizerTypeGroup
		    .selectToggle(visualizerWindow.visualizerTypeGroup.getToggles().get(newValue.intValue()));
	    visualizerStackController.replayLabelEffect(
		    ((RadioMenuItem) visualizerWindow.visualizerTypeGroup.getSelectedToggle()).getText());
	});

	// -----------visualizerTypeGroup
	visualizerWindow.visualizerTypeGroup.getToggles().forEach(toggle -> {
	    ((RadioMenuItem) toggle).setOnAction(a -> {
		visualizer.displayMode.set(visualizerWindow.visualizerTypeGroup.getToggles().indexOf(toggle));
	    });
	});

	// VisualizerStackController
	visualizerStackController.getChildren().add(0, visualizer);
	visualizerStackController.visibleProperty().bind(visualizerVisible.selectedProperty());

	// Add VisualizerStackController to the VisualizerStackPane
	visualizerStackPane.getChildren().add(0, visualizerStackController);

	// maximizeVisualizer
	maximizeVisualizer.disableProperty().bind(visualizerVisible.selectedProperty().not());
	maximizeVisualizer.setStyle("-fx-background-color:white; -fx-cursor:hand; -fx-background-radius:15px;");
	maximizeVisualizer.setOnAction(e -> visualizerWindow.displayVisualizer());
	maximizeVisualizer.visibleProperty()
		.bind(visualizerWindow.getStage().showingProperty().not().and(visualizerStackPane.hoverProperty()));

	// visualizerVisible
	visualizerVisible.setStyle("-fx-background-color:white; -fx-cursor:hand; -fx-background-radius:15px;");
	visualizerVisible.visibleProperty().bind(maximizeVisualizer.visibleProperty());
	visualizerVisible.selectedProperty().addListener((observable, oldValue, newValue) -> {
	    if (newValue) // true?
		visualizerVisible.setGraphic(eye);
	    else
		visualizerVisible.setGraphic(eyeDisabled);
	});

	// visualizerVisibleLabel
	visualizerVisibleLabel.visibleProperty().bind(visualizerVisible.selectedProperty().not());

	// visualizerMaximizedLabel
	visualizerMaximizedLabel.visibleProperty().bind(visualizerWindow.getStage().showingProperty());

	// playerStatusLabel
	playerStatusLabel.visibleProperty().bind(visualizer.animationService.runningProperty().not());

	buildSettings(side);
    }

    /**
     * This method is making the disc of the player.
     *
     * @param width
     *            the width
     * @param height
     *            the height
     * @param color
     *            the color
     * @param volume
     *            the volume
     * @param side
     *            the side
     */
    public void makeTheDisc(int width, int height, Color color, int volume, Side side) {

	// initialize
	disc = new DJDisc(width, height, color, volume, 125);
	disc.addDJDiscListener(this);
	// disc.set

	// radialMenu
	radialMenu.setStrokeVisible(false);
	radialMenu.setBackgroundMouseOnColor(color);
	disc.getChildren().add(radialMenu);

	// Canvas Mouse Moving
	disc.getCanvas().setOnMouseMoved(m -> {
	    // File is either corrupted or error or no File entered yet
	    if (xPlayerModel.getDuration() == 0 || xPlayerModel.getDuration() == -1)
		disc.getCanvas().setCursor(noSeekCursor);
	    // !discIsDragging
	    else if (!discIsDragging)
		disc.getCanvas().setCursor(Cursor.OPEN_HAND);
	});

	// Canvas Mouse Released
	disc.getCanvas().setOnMouseReleased(m -> {

	    // PrimaryMouseButton
	    if (m.getButton() == MouseButton.PRIMARY) {

		// discIsDragging and MouseButton==Primary
		// and duration!=0 and duration!=-1
		if (discIsDragging && xPlayerModel.getDuration() != 0 && xPlayerModel.getDuration() != -1) {

		    // Try to seek
		    seekService.startSeekService(xPlayerModel.getCurrentAngleTime()
			    * (xPlayer.getTotalBytes() / xPlayerModel.getDuration()));

		}

	    }
	});

	// Canvas Mouse Dragging
	disc.getCanvas().setOnMouseDragged(m -> {

	    // MouseButton==Primary
	    if (m.getButton() == MouseButton.PRIMARY)

		// RadialMenu!showing and duration!=0 and duration!=-1
		if (!radialMenu.isShowing() && xPlayerModel.getDuration() != 0 && xPlayerModel.getDuration() != -1) {

		    System.out.println("Entered Dragging...");

		    // Set the cursor
		    disc.getCanvas().setCursor(Cursor.CLOSED_HAND);

		    // Try to do the dragging
		    discIsDragging = true;
		    xPlayerModel.setCurrentAngleTime(disc.getValue(xPlayerModel.getDuration()));
		    disc.calculateAngleByMouse(m, xPlayerModel.getCurrentAngleTime(), xPlayerModel.getDuration());

		}
	});

	diskStackPane.getChildren().add(disc);
	disc.toBack();
	// firstLayerGridPane.add(disc, side == Side.LEFT ? 0 : 1, 0, 1, 3)

    }

    /**
     * needs to be deleted.
     *
     * @param side
     *            the side
     * @deprecated
     */
    @Deprecated
    private void buildSettings(Side side) {

	// BorderPane
	BorderPane pane = new BorderPane();
	pane.setStyle("-fx-padding:5px;");

	// AnalyserBox
	// analyserBox = new AnalyserBox(301, 100);
	// pane.setCenter(analyserBox)

	// Equalizer
	equalizer = new XPlayerEqualizer(this);
	xPlayerSettingsController.equalizerTab.setContent(new ScrollPane(equalizer));

	// PlayList
	xPlayerSettingsController.playListTab.setContent(xPlayList);

	// XplayerTabs
	/*
	 * playerTabs = new XPlayerTabs(key); Button tabs = new Button("Tabs")
	 * tabs.setId("button"); box.getChildren().add(tabs); tabs.setOnAction(e
	 * -> playerTabs.show(firstLayerGridPane))
	 */

	// pane.setBottom(box)
	// firstLayerGridPane.add(pane, side == Side.LEFT ? 0 : 1, 2)
    }

    /**
     * This Service is used to skip the Audio to a different time .
     *
     * @author GOXR3PLUS
     */
    public class SeekService extends Service<Boolean> {

	/** The bytes to be skipped */
	long bytes;

	/**
	 * Determines if the Service is locked , if yes it can't be used .
	 */
	private volatile boolean locked = false;

	/**
	 * Constructor.
	 */
	public SeekService() {
	    setOnSucceeded(s -> done());
	    setOnFailed(f -> done());
	}

	/**
	 * Start the Service.
	 *
	 * @param bytes
	 *            Bytes to skip
	 */
	public void startSeekService(long bytes) {
	    if (!locked && !isRunning()) {

		// Bytes to Skip
		this.bytes = bytes;

		// Create Binding
		fxLabel.textProperty().bind(messageProperty());
		fxRegion.visibleProperty().bind(runningProperty());

		// lock the Service
		locked = true;

		// Restart
		restart();
	    }
	}

	/**
	 * When the Service is done.
	 */
	private void done() {

	    // Remove the unidirectional binding
	    fxLabel.textProperty().unbind();
	    fxRegion.visibleProperty().unbind();
	    fxRegion.setVisible(false);

	    // Stop disc dragging!
	    discIsDragging = false;

	    // Put the appropriate Cursor
	    disc.getCanvas().setCursor(Cursor.OPEN_HAND);

	    // Resume Rotation
	    if (xPlayer.isPlaying() && !playService.isDiscImageNull())
		disc.resumeRotation();

	    // Recalculate Angle and paint again Disc
	    disc.calculateAngleByValue(xPlayerModel.getCurrentTime(), xPlayerModel.getDuration(), true);
	    disc.repaint();

	    // unlock the Service
	    locked = false;

	}

	@Override
	protected Task<Boolean> createTask() {
	    return new Task<Boolean>() {
		@Override
		protected Boolean call() throws Exception {
		    boolean succeded = true;

		    // ----------------------- Seek the Media
		    updateMessage("Skipping the Audio");

		    // GO
		    if (bytes != 0) { // and xPlayer.isPausedOrPlaying())
			Main.logger.info("Seek Service Started..");

			// CurrentTime
			xPlayerModel.setCurrentTime(xPlayerModel.getCurrentAngleTime());

			try {
			    xPlayer.seek(bytes);
			} catch (StreamPlayerException ex) {
			    logger.log(Level.WARNING, "", ex);
			    succeded = false;
			}
		    }

		    // ----------------------- Play Audio
		    if (!xPlayer.isPausedOrPlaying())
			xPlayer.play();

		    // ----------------------- Configuration
		    updateMessage("Applying Settings ...");

		    // Configure Media Settings
		    if (xPlayer.isPausedOrPlaying())
			configureMediaSettings();

		    return succeded;
		}

	    };
	}

    }

    /**
     * Implements the StreamPlayer Seek Method so it runs outside JavaFX Main Thread.
     *
     * @author GOXR3PLUS
     */
    private class PlayService extends Service<Boolean> {

	/** The album image of the audio */
	private Image image;

	/**
	 * Determines if the Service is locked , if yes it can't be used .
	 */
	private volatile boolean locked = false;

	/**
	 * Start the Service.
	 *
	 * @param path
	 *            The path of the audio
	 */
	public void startPlayService(String path) {
	    if (!locked && !isRunning() && path != null && InfoTool.isAudioSupported(path)) {

		// The path of the audio file
		xPlayerModel.songPathProperty().set(path);

		// Create Binding
		fxLabel.textProperty().bind(messageProperty());
		fxRegion.visibleProperty().bind(runningProperty());

		// Restart the Service
		restart();

		// lock the Service
		locked = true;
	    }
	}

	/**
	 * Determines if the image of the disc is the NULL_IMAGE that means that the media inserted into the player has no album image.
	 *
	 * @return true if the DiscImage==null <br>
	 *         false if the DiscImage!=null
	 */
	public boolean isDiscImageNull() {
	    return image == null;
	}

	/**
	 * When the Service is done.
	 */
	private void done() {

	    // Remove the unidirectional binding
	    fxLabel.textProperty().unbind();
	    fxRegion.visibleProperty().unbind();
	    fxRegion.setVisible(false);

	    // Set the appropriate cursor
	    if (xPlayerModel.getDuration() == 0 || xPlayerModel.getDuration() == -1)
		disc.getCanvas().setCursor(Cursor.OPEN_HAND);

	    // unlock the Service
	    locked = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
	    return new Task<Boolean>() {
		@Override
		protected Boolean call() throws Exception {

		    try {

			// Stop the previous audio
			updateMessage("Stop previous...");
			xPlayer.stop();

			// ---------------------- Load the File
			updateMessage("File Configuration ...");

			// duration
			xPlayerModel.setDuration(InfoTool.durationInSeconds(xPlayerModel.songPathProperty().get(),
				checkAudioType(xPlayerModel.songPathProperty().get())));

			// extension
			xPlayerModel.songExtensionProperty()
				.set(InfoTool.getFileExtension(xPlayerModel.songPathProperty().get()));

			// ----------------------- Load the Album Image
			image = InfoTool.getMp3AlbumImage(xPlayerModel.songPathProperty().get(), -1, -1);

			// ---------------------- Open the Audio
			updateMessage("Opening ...");
			xPlayer.open(xPlayerModel.songObjectProperty().get());

			// ----------------------- Play the Audio
			updateMessage("Starting ...");
			xPlayer.play();

			// ----------------------- Configuration
			updateMessage("Applying Settings ...");

			// Configure Media Settings
			configureMediaSettings();

			// ....well let's go
		    } catch (Exception ex) {
			logger.log(Level.WARNING, "", ex);
			Platform.runLater(() -> ActionTool.showNotification("ERROR",
				"Can't play \n[" + InfoTool.getMinString(xPlayerModel.songPathProperty().get(), 30)
					+ "]\n" + "It is corrupted or maybe unsupported",
				Duration.millis(1500), NotificationType.ERROR));
			return false;
		    } finally {

			// Print the current audio file path
			System.out.println("Current audio path is ...:" + xPlayerModel.songPathProperty().get());

		    }

		    return true;
		}

		/**
		 * Checking the audio type -> File || URL
		 * 
		 * @param path
		 *            The path of the audio File
		 * @return returns
		 * @see AudioType
		 */
		private AudioType checkAudioType(String path) {

		    // File?
		    try {
			xPlayerModel.songObjectProperty().set(new File(path));
			return AudioType.FILE;
		    } catch (Exception ex) {
			logger.log(Level.WARNING, "", ex);
		    }

		    // URL?
		    try {
			xPlayerModel.songObjectProperty().set(new URL(path));
			return AudioType.URL;
		    } catch (MalformedURLException ex) {
			logger.log(Level.WARNING, "MalformedURLException", ex);
		    }

		    // very dangerous this null here!!!!!!!!!!!
		    xPlayerModel.songObjectProperty().set(null);

		    return AudioType.UNKNOWN;
		}

	    };
	}

	@Override
	public void succeeded() {
	    super.succeeded();
	    System.out.println("XPlayer [ " + key + " ] PlayService Succeeded...");

	    // Replace the image of the disc
	    disc.replaceImage(image);

	    // add to played songs...
	    Main.playedSongs.add(xPlayerModel.songPathProperty().get());

	    done();
	}

	@Override
	public void failed() {
	    super.failed();
	    System.out.println("XPlayer [ " + key + " ] PlayService Failed...");

	    // xPlayerModel.songObjectProperty().set(null)
	    // xPlayerModel.songPathProperty().set(null)
	    // xPlayerModel.songExtensionProperty().set(null)
	    // xPlayerModel.setDuration(-1)
	    // xPlayerModel.setCurrentTime(-1)
	    // image = null
	    // disc.replaceImage(null)

	    done();
	}

	@Override
	public void cancelled() {
	    super.cancelled();
	    System.out.println("XPlayer [ " + key + " ] PlayService Cancelled...");

	}
    }

    /**
     * When the audio starts , fast configure it's settings
     */
    public void configureMediaSettings() {

	// Start immediately?
	if (!xPlayerSettingsController.startImmediately.isSelected())
	    pause();

	// Mute?
	xPlayer.setMute(radialMenu.mute.isSelected());

	// Volume
	controlVolume();

	// Sets Pan value. Line should be opened before calling this method.
	// Linear scale : -1.0 <--> +1.0
	xPlayer.setPan(equalizer.panFilter.getValue(200));

	// Represents a control for the relative balance of a stereo signal
	// between two stereo speakers. The valid range of values is -1.0 (left
	// channel only) to 1.0 (right channel only). The default is 0.0
	// (centered).
	xPlayer.setBalance(equalizer.balanceFilter.getValue(200));

	// Audio is MP3?
	if ("mp3".equals(xPlayerModel.songExtensionProperty().get())) {
	    xPlayer.setEqualizer(xPlayerModel.getEqualizerArray(), 32);
	    equalizer.setDisable(false);
	} else
	    equalizer.setDisable(true);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void opened(Object dataSource, Map<String, Object> map) {
	// some code here
    }

    float progress = 0;

    @Override
    public void progress(int nEncodedBytes, long microSecondsPosition, byte[] pcmdata, Map<String, Object> properties) {
	visualizer.writeDSP(pcmdata);

	if (!isDiscBeingDragged()) {

	    // previousTime = xPlayerUI.xPlayer.currentTime

	    // .MP3 OR .WAV
	    if ("mp3".equals(xPlayerModel.songExtensionProperty().get())
		    || "wav".equals(xPlayerModel.songExtensionProperty().get())) {

		// Calculate the progress until now
		progress = (nEncodedBytes > 0 && xPlayer.getTotalBytes() > 0)
			? (nEncodedBytes * 1.0f / xPlayer.getTotalBytes() * 1.0f)
			: -1.0f;
		// System.out.println(progress*100+"%")
		if (visualizerWindow.isVisible())
		    Platform.runLater(() -> visualizerWindow.progressBar.setProgress(progress));

		// find the current time in seconds
		xPlayerModel.setCurrentTime((int) (xPlayerModel.getDuration() * progress));
		//System.out.println((double) xPlayerModel.getDuration() * progress)

		// .WHATEVER MUSIC FILE*
	    } else
		xPlayerModel.setCurrentTime((int) (microSecondsPosition / 1000000));

	    String millisecondsFormat = InfoTool.millisecondsToTime(microSecondsPosition / 1000);
	    //System.out.println(milliFormat)

	    // Paint the Disc
	    if (!xPlayer.isStopped()) {
		//Update the disc Angle
		disc.calculateAngleByValue(xPlayerModel.getCurrentTime(), xPlayerModel.getDuration(), false);
		//Update the disc time
		disc.updateTimeDirectly(xPlayerModel.getCurrentTime(), xPlayerModel.getDuration(), millisecondsFormat);
	    }

	    if (!visualizer.isRunning())
		Platform.runLater(this::resumeCode);

	}

	// System.out.println(xPlayer.currentTime)
    }

    @Override
    public void statusUpdated(StreamPlayerEvent streamPlayerEvent) {

	// Status.OPENED
	if (streamPlayerEvent.getPlayerStatus() == Status.OPENED && xPlayer.getSourceDataLine() != null) {

	    visualizer.setupDSP(xPlayer.getSourceDataLine());
	    visualizer.startDSP(xPlayer.getSourceDataLine());

	    Platform.runLater(() -> {
		mediaFileMarquee.setText(InfoTool.getFileName(xPlayerModel.songPathProperty().get()));
		resumeCode();
	    });

	    System.out.println("XPlayer [ " + getKey() + " ] is opened!");

	    // Status.PLAYING
	} else if (streamPlayerEvent.getPlayerStatus() == Status.PLAYING) {

	    Platform.runLater(this::resumeCode);

	    System.out.println("XPlayer [ " + getKey() + " ] is PLAYING!");

	    // Status.PAUSED
	} else if (streamPlayerEvent.getPlayerStatus() == Status.PAUSED) {

	    Platform.runLater(() -> {
		playerStatusLabel.setText("Player is Paused ");
		pauseCode();
	    });
	    System.out.println("XPlayer [ " + getKey() + " ] is PAUSED!");

	    // Status.STOPPED
	} else if (streamPlayerEvent.getPlayerStatus() == Status.STOPPED) {

	    visualizer.stopDSP();

	    Platform.runLater(() -> {

		//
		mediaFileMarquee.setText("Player is Stopped");
		playerStatusLabel.setText(mediaFileMarquee.textProperty().get());

		// disc
		disc.calculateAngleByValue(0, 0, true);
		disc.repaint();

		disc.stopFade();
		disc.pauseRotation();
		visualizer.stopVisualizer();
		radialMenu.resumeOrPause.setGraphic(radialMenu.playImageView);

		ActionTool.showNotification("Player " + this.getKey(), "Player[ " + this.getKey() + " ] has stopped...",
			Duration.millis(500), NotificationType.SIMPLE);
	    });

	    System.out.println("XPlayer [ " + getKey() + " ] is STOPPED!");
	}
    }

    @Override
    public void volumeChanged(int volume) {
	controlVolume();
    }

    /**
     * Replay the current song
     */
    public void replaySong() {

	if (xPlayerModel.songExtensionProperty().get() != null)
	    playService.startPlayService(xPlayerModel.songPathProperty().get());
	else
	    ActionTool.showNotification("No Previous File", "Drag and Drop or Add a File or URL on this player.",
		    Duration.millis(1500), NotificationType.INFORMATION);

	// if (thisSong instanceof URL)
	// return playSong(((URL) thisSong).toString(), totalTime);
	// else if (thisSong instanceof File)
	// return playSong(((File) thisSong).getAbsolutePath(), totalTime);

    }

    /**
     * Play the current song.
     *
     * @param absolutePath
     *            The absolute path of the file
     */
    public void playSong(String absolutePath) {

	playService.startPlayService(absolutePath);
    }

    /**
     * Resume the player.
     */
    public void resume() {
	xPlayer.resume();
    }

    /**
     * Pause the player.
     */
    public void pause() {
	xPlayer.pause();
    }

    /**
     * Reverse Play with Pause or the opposite.
     */
    public void reversePlayAndPause() {
	if (xPlayer.isPlaying()) // playing?
	    pause();
	else if (xPlayer.isPaused()) // paused?
	    resume();
	else if (xPlayer.isStopped() || xPlayer.isUnknown())
	    replaySong();

    }

}
