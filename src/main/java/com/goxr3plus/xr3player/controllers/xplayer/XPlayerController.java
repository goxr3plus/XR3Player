/*
 *
 */
package com.goxr3plus.xr3player.controllers.xplayer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.custom.DJDisc;
import com.goxr3plus.xr3player.controllers.custom.FlipPanel;
import com.goxr3plus.xr3player.controllers.custom.Marquee;
import com.goxr3plus.xr3player.controllers.dropbox.DownloadsProgressBox;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxDownloadedFile;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController.SettingsTab;
import com.goxr3plus.xr3player.controllers.windows.EmotionsWindow.Emotion;
import com.goxr3plus.xr3player.controllers.windows.XPlayerWindow;
import com.goxr3plus.xr3player.enums.FileLinkType;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.models.smartcontroller.Audio;
import com.goxr3plus.xr3player.models.xplayer.XPlayer;
import com.goxr3plus.xr3player.models.xplayer.XPlayerModel;
import com.goxr3plus.xr3player.services.xplayer.XPlayerPlayService;
import com.goxr3plus.xr3player.services.xplayer.XPlayerSeekService;
import com.goxr3plus.xr3player.utils.general.AudioImageTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.TimeTool;
import com.goxr3plus.xr3player.utils.io.FileTypeAndAbsolutePath;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerModel.VisualizerType;
import com.goxr3plus.xr3player.xplayer.visualizer.presenter.VisualizerStackController;
import com.goxr3plus.xr3player.xplayer.visualizer.presenter.VisualizerWindowController;
import com.goxr3plus.xr3player.xplayer.visualizer.presenter.XPlayerVisualizer;
import com.goxr3plus.xr3player.xplayer.waveform.WaveVisualization;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Represents the graphical interface for the deck.
 *
 * @author GOXR3PLUS
 */
public class XPlayerController extends StackPane {

	final FontIcon playIcon = JavaFXTool.getFontIcon("fa-play", Color.WHITE, 24);
	final FontIcon smPlayIcon = JavaFXTool.getFontIcon("fa-play", Color.WHITE, 32);
	final FontIcon microPlayIcon = JavaFXTool.getFontIcon("fa-play", Color.WHITE, 20);
	final FontIcon pauseIcon = JavaFXTool.getFontIcon("fa-pause", Color.WHITE, 24);
	final FontIcon smPauseIcon = JavaFXTool.getFontIcon("fa-pause", Color.WHITE, 32);
	final FontIcon microPauseIcon = JavaFXTool.getFontIcon("fa-pause", Color.WHITE, 20);
	private static final XPlayerControllerContextMenu contextMenu = new XPlayerControllerContextMenu();

	// -----------------------------------------------

	@FXML
	private StackPane xPlayerStackPane;

	@FXML
	private BorderPane rootBorderPane;

	@FXML
	private StackPane modesStackPane;

	@FXML
	private BorderPane borderPane;

	@FXML
	private SplitPane internalSplitPane;

	@FXML
	private BorderPane discBorderPane;

	@FXML
	private StackPane speedSliderStackPane;

	@FXML
	private Label speedControlLabel;

	@FXML
	private Slider speedSlider;

	@FXML
	private JFXButton speedControlButton;

	@FXML
	private StackPane diskStackPane;

	@FXML
	private StackPane diskStackPane1;

	@FXML
	Button playPauseButton;

	@FXML
	private Button stopButton;

	@FXML
	private Button backwardButton;

	@FXML
	private Button replayButton;

	@FXML
	private Button forwardButton;

	@FXML
	private StackPane waveStackPane;

	@FXML
	private Label waveProgressLabel;

	@FXML
	private ProgressBar waveProgressBar;

	@FXML
	private JFXTabPane visualizerTabPane;

	@FXML
	private StackPane visualizerStackTopParent;

	@FXML
	private StackPane visualizerStackPane;

	@FXML
	private Label visualizerLabel;

	@FXML
	Label playerStatusLabel;

	@FXML
	private JFXButton visualizerVisibleLabel;

	@FXML
	private FontIcon visualizerEyeIcon3;

	@FXML
	private HBox visualizerSettingsHBox;

	@FXML
	private JFXButton visualizerSettings;

	@FXML
	private JFXButton showVisualizerButton;

	@FXML
	private FontIcon visualizerEyeIcon;

	@FXML
	private JFXButton maximizeVisualizer;

	@FXML
	private VBox visualizerMaximizedBox;

	@FXML
	private JFXButton visualizerMinimize;

	@FXML
	private JFXButton visualizerRequestFocus;

	@FXML
	private Label visualizationsDisabledLabel;

	@FXML
	private Button enableHighGraphics;

	@FXML
	private Tab equalizerTab;

	@FXML
	private HBox mediaNameHBox;

	@FXML
	private HBox timersBox;

	@FXML
	Label elapsedTimeLabel;

	@FXML
	Label remainingTimeLabel;

	@FXML
	private Label totalTimeLabel;

	@FXML
	private MenuButton copyButton;

	@FXML
	private MenuItem copyFileTitle;

	@FXML
	private MenuItem copyFileLocation;

	@FXML
	private MenuItem copyFile;

	@FXML
	private Button mediaTagImageButton;

	@FXML
	private FontIcon albumImageFontIcon;

	@FXML
	private ImageView mediaTagImageView;

	@FXML
	private Label advModeVolumeLabel;

	@FXML
	private BorderPane smBorderPane;

	@FXML
	private StackPane smModeCenterStackPane;

	@FXML
	private ImageView smImageView;

	@FXML
	private FontIcon smAlbumFontIcon;

	@FXML
	private Label smMediaTitle;

	@FXML
	private Button smReplayButton;

	@FXML
	private Button smBackwardButton;

	@FXML
	Button smPlayPauseButton;

	@FXML
	private Button smStopButton;

	@FXML
	private Button smForwardButton;

	@FXML
	private ToggleButton showVisualizer;

	@FXML
	ProgressBar smTimeSliderProgress;

	@FXML
	Slider smTimeSlider;

	@FXML
	Label smTimeSliderLabel;

	@FXML
	private Label smVolumeSliderLabel;

	@FXML
	private VBox volumeBarBox;

	@FXML
	private Button smMaximizeVolume;

	@FXML
	private ProgressBar volumeSliderProgBar;

	@FXML
	private Slider smVolumeSlider;

	@FXML
	private Button smMinimizeVolume;

	@FXML
	private VBox topVBox;

	@FXML
	private HBox topHBox;

	@FXML
	private HBox toolsHBox;

	@FXML
	private ToggleButton muteButton;

	@FXML
	private Button showMenu;

	@FXML
	private Button openFile;

	@FXML
	private Button settings;

	@FXML
	private MenuButton transferMedia;

	@FXML
	private Label topInfoLabel;

	@FXML
	private Button emotionsButton;

	@FXML
	private Button extendPlayer;

	@FXML
	private StackedFontIcon sizeStackedFontIcon;

	@FXML
	ToggleButton modeToggle;

	@FXML
	ToggleButton historyToggle;

	@FXML
	private StackPane microStackPane;

	@FXML
	private BorderPane microBorderPane;

	@FXML
	private StackPane diskStackPane11;

	@FXML
	Button microPlayPauseButton;

	@FXML
	private Button microStopButton;

	@FXML
	private Button microReplayButton;

	@FXML
	private Label dragAndDropLabel;

	@FXML
	private StackPane regionStackPane;

	@FXML
	private Label playerLoadingLabel;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private StackPane downloadStackPane;

	@FXML
	private Label downloadingLabel;

	@FXML
	private JFXButton restorePlayer;

	@FXML
	private JFXButton focusXPlayerWindow;

	// -----------------------------------------------------------------------------

	/** A Fade Transition */
	private FadeTransition fadeTransition;

	/**
	 * This Variable Determines if the Player is extended or not ( which means it is
	 * being shown on an external window different from the main window )
	 */
	public boolean isPlayerExtended;

	public final Logger logger = Logger.getLogger(getClass().getName());

	// ------------------------- Images/ImageViews --------------------------

	private static final Image noSeek = InfoTool.getImageFromResourcesFolder(
		getImageName());

	private static String getImageName() {
		try {
			return "Private-" + (ImageCursor.getBestSize(64, 64).getWidth() < 64.00 ? "32" : "64") + ".png";
		} catch (NullPointerException e) {
			// TODO: Remove this hard-coded value when it's no longer needed. It's needed with JDK 10.0.2 on Mac.
			System.out.println("Using a hard-coded value for ImageCursor, to circumvent a bug on JDK 10.0.2 on Mac. Stacktrace follows.");
			e.printStackTrace();
			return "Private-32.png";
		}
	}

	private static final ImageCursor noSeekCursor = new ImageCursor(noSeek, noSeek.getWidth() / 2,
		noSeek.getHeight() / 2);

	// ------------------------- Services --------------------------

	/** The seek service. */
	public final XPlayerSeekService seekService = new XPlayerSeekService(this);

	/** The play service. */
	public final XPlayerPlayService playService = new XPlayerPlayService(this);

	// ------------------------- Variables --------------------------
	/** The key. */
	private final int key;

	/** The disc is being mouse dragged */
	public boolean discIsDragging;

	// -------------------------ETC --------------------------

	public XPlayerPlaylist xPlayerPlayList;

	public XPlayerWindow xPlayerWindow;

	/** The x player settings controller. */
	public XPlayerHistory history;

	/** The x player model. */
	public XPlayerModel xPlayerModel;

	/** The x player. */
	public XPlayer xPlayer;

	/** The visualizer window. */
	public VisualizerWindowController visualizerWindow;

	/**
	 * This controller contains a Visualizer and a Label which describes every time
	 * (for some milliseconds) which type of visualizer is being displayed (for
	 * example [ Oscilloscope , Rosette , Spectrum Bars etc...]);
	 */
	public final VisualizerStackController visualizerStackController = new VisualizerStackController();

	/** The visualizer. */
	public XPlayerVisualizer visualizer;
	public XPlayerVisualizer djVisualizer;

	/** The equalizer. */
	public XPlayerEqualizer equalizer;

	public XPlayerPad xPlayerPad;

	/** The disc. */
	public DJDisc disc;

	public final Marquee mediaFileMarquee = new Marquee();

	private final FlipPanel flipPane = new FlipPanel(Orientation.HORIZONTAL);

	public final SimpleBooleanProperty visualizerVisibility = new SimpleBooleanProperty(true);

	//
	public WaveVisualization waveFormVisualization;

	private StreamController streamController = new StreamController(this);

	// ======= Events ===========

	public final EventHandler<? super MouseEvent> audioDragEvent = event -> {
		final String absolutePath = xPlayerModel.songPathProperty().get();
		if (absolutePath != null) {

			/* Allow copy transfer mode */
			final Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);

			/* Put a String into the dragBoard */
			final ClipboardContent content = new ClipboardContent();
			content.putFiles(Arrays.asList(new File(absolutePath)));
			db.setContent(content);

			/* Set the DragView */
			new Audio(absolutePath, 0.0, 0, "", "", Genre.SEARCHWINDOW, -1).setDragView(db);
		}
		event.consume();
	};

	public final EventHandler<? super DragEvent> audioDropEvent = event -> {

		// We don't want the player to start if the drop event is for the XPlayer
		// PlayList
		if (!flipPane.isBackVisible()) {
			final Dragboard dragBoard = event.getDragboard();

			// File?
			if (dragBoard.hasFiles()) {
				for (final File file : dragBoard.getFiles())
					// No directories allowed
					if (!file.isDirectory()) {

						// Get it
						final FileTypeAndAbsolutePath ftaap = IOInfo.getRealPathFromFile(file.getAbsolutePath());

						// Check if File exists
						if (!new File(ftaap.getFileAbsolutePath()).exists()) {
							AlertTool.showNotification("File doesn't exist",
								(ftaap.getFileType() == FileLinkType.SYMBOLIC_LINK ? "Symbolic link"
									: "Windows Shortcut") + " points to a file that doesn't exists anymore.",
								Duration.millis(2000), NotificationType.INFORMATION);
							return;
						}

						// Check if XPlayer is already active
						if (xPlayer.isPausedOrPlaying() && Main.settingsWindow.getxPlayersSettingsController()
							.getAskSecurityQuestion().isSelected()) {
							if (AlertTool.doQuestion("Abort Current Song",
								"A song is already playing on this deck.\n Are you sure you want to replace it?",
								visualizerWindow.getStage().isShowing() && !xPlayerWindow.getWindow().isShowing()
									? visualizerWindow
									: xPlayerStackPane,
								Main.window))
								playSong(ftaap.getFileAbsolutePath());
						} else
							playSong(ftaap.getFileAbsolutePath());
						break;

					}
				// String
			} else if (dragBoard.hasString()) {
				final String stringo = dragBoard.getString();

				// Check if it is a direct drag and drop from dropbox
				if (stringo.contains("#dropbox_item#")) {

					// Keep a reference to it
					final DropboxDownloadedFile dropboxDownloadedFile = Main.dropBoxViewer.downloadFile(
						Main.dropBoxViewer.getDropboxFilesTableViewer().getSelectionModel().getSelectedItem());

					// DownloadsProgressBox
					final DownloadsProgressBox progressBox = new DownloadsProgressBox(dropboxDownloadedFile);
					downloadingLabel.setText("Downloading file from Dropbox");
					downloadingLabel.setGraphic(progressBox);

					// downloadStackPane
					downloadStackPane.visibleProperty()
						.bind(dropboxDownloadedFile.getDownloadService().runningProperty());

					// Play if finally the file has been successfully downloaded
					dropboxDownloadedFile.getDownloadService().setOnSucceeded(s -> {
						if (new File(dropboxDownloadedFile.getDownloadService().getLocalFileAbsolutePath()).exists())
							playSong(dropboxDownloadedFile.getDownloadService().getLocalFileAbsolutePath());
					});
				}

			}

			event.setDropCompleted(true);
			event.consume();
		}
	};

	// ============================================================================================

	/**
	 * Constructor.
	 *
	 * @param key The key that is identifying this player
	 */
	public XPlayerController(final int key) {
		this.key = key;

		// ----------------------------------- FXMLLoader
		// -------------------------------------
		final FXMLLoader loader = new FXMLLoader(
			getClass().getResource(InfoTool.PLAYERS_FXMLS + "XPlayerController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			logger.log(Level.SEVERE, "XPlayerController FXML can't be loaded!", ex);
		}

	}

	/**
	 * @return the xPlayerStackPane
	 */
	public StackPane getXPlayerStackPane() {
		return xPlayerStackPane;
	}

	/**
	 * @return historyToggle
	 */
	public ToggleButton getHistoryToggle() {
		return historyToggle;
	}

	/**
	 * Returns the XPlayerStackPane back to the XPlayerController if it is on
	 * XPlayer external Window
	 */
	public void restorePlayerStackPane() {
		this.getChildren().add(getXPlayerStackPane());
	}

	/** Called as soon as the .fxml has been loaded */
	@FXML
	private void initialize() {

		// root
		xPlayerStackPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() < 300) {

				// Get TopHBox
				if (!microBorderPane.getChildren().contains(topHBox)) {
					topVBox.getChildren().remove(topHBox);
					microBorderPane.setTop(topHBox);
				}

				// DiscBorderPane
				if (!microBorderPane.getChildren().contains(waveStackPane)) {
					discBorderPane.getChildren().remove(waveStackPane);
					microBorderPane.setCenter(waveStackPane);
				}

				// Visibility
				microStackPane.setVisible(true);
				modesStackPane.setVisible(false);
			} else {

				// Get TopHBox
				if (!topVBox.getChildren().contains(topHBox)) {
					microBorderPane.getChildren().remove(topHBox);
					topVBox.getChildren().add(0, topHBox);
				}

				// DiscBorderPane
				if (!discBorderPane.getChildren().contains(waveStackPane)) {
					microBorderPane.getChildren().remove(waveStackPane);
					discBorderPane.setTop(waveStackPane);
				}

				// Visibility
				modesStackPane.setVisible(true);
				microStackPane.setVisible(false);

			}
		});

		// -----XPlayer and XPlayerModel-------------
		xPlayerModel = new XPlayerModel();
		xPlayer = new XPlayer();
		xPlayer.addStreamPlayerListener(streamController);

		// -----Important-------------
		xPlayerWindow = new XPlayerWindow(this);

		// waveFormVisualization
		waveFormVisualization = new WaveVisualization(this, 500, 20);

		// == RadialMenu
		// radialMenu = new XPlayerRadialMenu(this);
		// radialMenu.mute.selectedProperty().addListener(l -> {
		// xPlayer.setMute(radialMenu.mute.isSelected());
		// muteButton.setSelected(radialMenu.mute.isSelected());
		//
		// //System.out.println("Entered Radial Menu");
		// });
		muteButton.selectedProperty().addListener(l -> {
			xPlayer.setMute(muteButton.isSelected());
			// radialMenu.mute.setSelected(muteButton.isSelected());

			// System.out.println("Entered Menu Button");

			// Update PropertiesDB
			Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + getKey() + "-Muted",
				String.valueOf(muteButton.isSelected()));

		});

		//
		xPlayerPlayList = new XPlayerPlaylist(this);
		visualizerWindow = new VisualizerWindowController(this);
		history = new XPlayerHistory(this);

		// == microStackPane
		microStackPane.setOnDragOver(event -> {
			// System.out.println(event.getGestureSource())

			// Check if FlipPane is on the front side
			// if (!flipPane.isBackVisible()) { //event.getGestureSource() !=
			// mediaFileMarquee) {
			dragAndDropLabel.setVisible(true);
			// }

			event.consume();
		});

		// == modesStackPane
		modesStackPane.setOnDragOver(event -> {
			// System.out.println(event.getGestureSource())

			// Check if FlipPane is on the front side
			if (!flipPane.isBackVisible()) { // event.getGestureSource() != mediaFileMarquee) {
				dragAndDropLabel.setVisible(true);
			}

			event.consume();
		});

		// Key Listener
		modesStackPane.setOnKeyReleased(key -> {

			// Check if any file path is pasted
			if ((key.isControlDown() || key.getCode() == KeyCode.COMMAND) && key.getCode() == KeyCode.V) {

				// Get Native System ClipBoard
				final Clipboard clipboard = Clipboard.getSystemClipboard();

				// Has Files? + isFree()?
				if (clipboard.hasFiles())
					// File?
					for (final File file : clipboard.getFiles())
						// No directories allowed
						if (!file.isDirectory()) {

							// Get it
							final FileTypeAndAbsolutePath ftaap = IOInfo.getRealPathFromFile(file.getAbsolutePath());

							// Check if File exists
							if (!new File(ftaap.getFileAbsolutePath()).exists()) {
								AlertTool.showNotification("File doesn't exist",
									(ftaap.getFileType() == FileLinkType.SYMBOLIC_LINK ? "Symbolic link"
										: "Windows Shortcut")
										+ " points to a file that doesn't exists anymore.",
									Duration.millis(2000), NotificationType.INFORMATION);
								return;
							}

							// Check if XPlayer is already active
							if (xPlayer.isPausedOrPlaying() && Main.settingsWindow.getxPlayersSettingsController()
								.getAskSecurityQuestion().isSelected()) {
								if (AlertTool.doQuestion("Abort Current Song",
									"A song is already playing on this deck.\n Are you sure you want to replace it?",
									visualizerWindow.getStage().isShowing()
										&& !xPlayerWindow.getWindow().isShowing() ? visualizerWindow
										: xPlayerStackPane,
									Main.window))
									playSong(ftaap.getFileAbsolutePath());
							} else
								playSong(ftaap.getFileAbsolutePath());
							break;
						}
			}
		});

		// == dragAndDropLabel
		dragAndDropLabel.setVisible(false);
		dragAndDropLabel.setOnDragOver(event -> {
			// Check if FlipPane is on the front side
			if (!flipPane.isBackVisible())
				event.acceptTransferModes(TransferMode.LINK);

			event.consume();
		});
		dragAndDropLabel.setOnDragExited(event -> {
			dragAndDropLabel.setVisible(false);
			event.consume();
		});
		dragAndDropLabel.setOnDragDropped(audioDropEvent);

		// == regionStackPane
		regionStackPane.setVisible(false);

		// mediaFileStackPane
		mediaFileMarquee.getLabel().setTooltip(new Tooltip(""));
		mediaFileMarquee.getLabel().getTooltip().textProperty().bind(mediaFileMarquee.getLabel().textProperty());
		mediaFileMarquee.setText("No media");
		mediaFileMarquee.setOnMouseClicked(m -> openAudioInExplorer());
		mediaFileMarquee.setCursor(Cursor.HAND);
		mediaFileMarquee.setOnDragDetected(audioDragEvent);
		mediaNameHBox.getChildren().add(1, mediaFileMarquee);
		HBox.setHgrow(mediaFileMarquee, Priority.ALWAYS);

		// smMediaTitle
		smMediaTitle.textProperty().bind(mediaFileMarquee.getLabel().textProperty());
		smMediaTitle.getTooltip().textProperty().bind(mediaFileMarquee.getLabel().textProperty());
		smMediaTitle.setCursor(Cursor.HAND);
		smMediaTitle.setOnMouseClicked(m -> openAudioInExplorer());

		// openMediaFileFolder
		mediaTagImageButton.setOnAction(action -> Main.tagWindow.openAudio(xPlayerModel.songPathProperty().get(),
			TagTabCategory.ARTWORK, true));
		mediaTagImageButton.setOnDragDetected(audioDragEvent);

		// albumImageFontIcon

		// openFile
		openFile.setOnAction(action -> openFileChooser());

		// copyFileTitle
		copyFileTitle.setOnAction(a -> {

			// If there is no Media
			if (xPlayerModel.getSongPath() == null) {
				AlertTool.showNotification("No Media", "No Media added on Player", Duration.seconds(2),
					NotificationType.INFORMATION);
				return;
			}

			// Get Native System ClipBoard
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();

			// PutFiles
			content.putString(mediaFileMarquee.getText());

			// Set the Content
			clipboard.setContent(content);

			// Check if it has Album Image
			final Image image = AudioImageTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60);

			// Notification
			AlertTool.showNotification("Copied to Clipboard",
				"Media name copied to clipboard,you can paste it anywhere on the your system.\nFor example in Windows with [CTRL+V], in Mac[COMMAND+V]",
				Duration.seconds(2), NotificationType.SIMPLE,
				image != null ? JavaFXTool.getImageView(image, 60, 60)
					: JavaFXTool.getFontIcon("gmi-album", Color.WHITE, 60));
		});

		// copyFileLocation
		copyFileLocation.setOnAction(a -> {

			// If there is no Media
			if (xPlayerModel.getSongPath() == null) {
				AlertTool.showNotification("No Media", "No Media added on Player", Duration.seconds(2),
					NotificationType.INFORMATION);
				return;
			}

			// Get Native System ClipBoard
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();

			// PutFiles
			content.putString(xPlayerModel.getSongPath());

			// Set the Content
			clipboard.setContent(content);

			// Check if it has Album Image
			final Image image = AudioImageTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60);

			// Notification
			AlertTool.showNotification("Copied to Clipboard",
				"Media File Full Path copied to clipboard,you can paste it anywhere on the your system.\nFor example in Windows with [CTRL+V], in Mac[COMMAND+V]",
				Duration.seconds(2), NotificationType.SIMPLE,
				image != null ? JavaFXTool.getImageView(image, 60, 60)
					: JavaFXTool.getFontIcon("gmi-album", Color.WHITE, 60));
		});

		// copyFile
		copyFile.setOnAction(a -> {

			// If there is no Media
			if (xPlayerModel.getSongPath() == null) {
				AlertTool.showNotification("No Media", "No Media added on Player", Duration.seconds(2),
					NotificationType.INFORMATION);
				return;
			}

			// Get Native System ClipBoard
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();

			// PutFiles
			content.putFiles(Arrays.asList(new File(xPlayerModel.getSongPath())));

			// Set the Content
			clipboard.setContent(content);

			// Check if it has Album Image
			final Image image = AudioImageTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60);

			// Notification
			AlertTool.showNotification("Copied to Clipboard",
				"Media name copied to clipboard,you can paste it anywhere on the your system.\nFor example in Windows with [CTRL+V], in Mac[COMMAND+V]",
				Duration.seconds(2), NotificationType.SIMPLE,
				image != null ? JavaFXTool.getImageView(image, 60, 60)
					: JavaFXTool.getFontIcon("gmi-album", Color.WHITE, 60));
		});

		// showMenu
		showMenu.setOnMouseReleased(m -> {

			// If there is no Media
			if (xPlayerModel.getSongPath() == null) {
				AlertTool.showNotification("No Media", "No Media added on Player", Duration.seconds(2),
					NotificationType.INFORMATION);
				return;
				// Check if Media exists
			} else if (!new File(xPlayerModel.getSongPath()).exists()) {
				AlertTool.showNotification("Media doesn't exist", "Current Media File doesn't exist anymore...",
					Duration.seconds(2), NotificationType.INFORMATION);
				return;
			}

			XPlayerController.contextMenu.showContextMenu(this.xPlayerModel.getSongPath(), m.getScreenX(),
				m.getScreenY(), showMenu);
		});

		// topInfoLabel
		topInfoLabel.setText("Player ");
		((FontIcon) topInfoLabel.getGraphic()).setIconLiteral("gmi-filter-" + (getKey() + 1));

		// == forwardButton
		forwardButton.setOnAction(a -> seek(Integer.parseInt(forwardButton.getText())));
		smForwardButton.setOnAction(forwardButton.getOnAction());
		smForwardButton.textProperty().bind(forwardButton.textProperty());

		// == backwardButton
		backwardButton.setOnAction(a -> seek(-Integer.parseInt(backwardButton.getText())));
		smBackwardButton.setOnAction(backwardButton.getOnAction());
		smBackwardButton.textProperty().bind(backwardButton.textProperty());

		// == playPauseButton
		playPauseButton.setOnAction(fire -> {
			if (xPlayer.isPlaying())
				pause();
			else
				playOrReplay();

			// Advanced Mode
			playPauseButton.setGraphic(xPlayer.isPlaying() ? pauseIcon : playIcon);

			// Simple Mode
			smPlayPauseButton.setGraphic(xPlayer.isPlaying() ? smPauseIcon : smPlayIcon);

			// Micro Mode
			microPlayPauseButton.setGraphic(xPlayer.isPlaying() ? microPauseIcon : microPlayIcon);
		});
		smPlayPauseButton.setOnAction(playPauseButton.getOnAction());
		microPlayPauseButton.setOnAction(playPauseButton.getOnAction());

		// == replayButton
		replayButton.setOnAction(a -> replay());
		smReplayButton.setOnAction(replayButton.getOnAction());
		microReplayButton.setOnAction(replayButton.getOnAction());

		// == stopButton
		stopButton.setOnAction(a -> stop());
		smStopButton.setOnAction(stopButton.getOnAction());
		microStopButton.setOnAction(stopButton.getOnAction());

		// flipPane
		flipPane.setFlipTime(150);
		flipPane.getFront().getChildren().addAll(modesStackPane);
		flipPane.getBack().getChildren().addAll(history);

		historyToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) // true?
				flipPane.flipToBack();
			else
				flipPane.flipToFront();
		});
		rootBorderPane.setCenter(flipPane);

		// modeToggle
		modeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				smBorderPane.setVisible(true);
				// modeToggleLabel.setText("Basic")

				// Fix the Visualizer
				simple_And_Advanced_Mode_Fix_Visualizer();

			} else {
				smBorderPane.setVisible(false);
				// modeToggleLabel.setText("Advanced")

				// Fix the Visualizer
				simple_And_Advanced_Mode_Fix_Visualizer();
			}

			// Go away from history
			historyToggle.setSelected(false);

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + getKey() + "-Advanced-Mode",
				String.valueOf(modeToggle.isSelected()));
		});

		// showVisualizer
		showVisualizer.selectedProperty().addListener((observable, oldValue, newValue) -> {

			// Fix the Visualizer
			simple_And_Advanced_Mode_Fix_Visualizer();

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + getKey() + "-Simple-Mode-Visualizers-Enabled",
				String.valueOf(showVisualizer.isSelected()));
		});

		// RestorePlayerVBox
		restorePlayer.getParent().visibleProperty().bind(xPlayerWindow.getWindow().showingProperty());

		// restorePlayer
		restorePlayer.setOnAction(m -> xPlayerWindow.close());

		// focusXPlayerWindow
		focusXPlayerWindow.setOnAction(m -> xPlayerWindow.getWindow().requestFocus());

		// extendPlayer
		extendPlayer.getTooltip().textProperty().bind(Bindings.when(xPlayerWindow.getWindow().showingProperty())
			.then("Restore to parent window").otherwise("Open to external window"));
		extendPlayer.setOnAction(ac -> {
			if (!xPlayerWindow.getWindow().isShowing()) {
				xPlayerWindow.show();
				isPlayerExtended = true;
				sizeStackedFontIcon.getChildren().get(0).setVisible(true);
				sizeStackedFontIcon.getChildren().get(1).setVisible(false);
			} else {
				xPlayerWindow.close();
				isPlayerExtended = false;
				sizeStackedFontIcon.getChildren().get(1).setVisible(true);
				sizeStackedFontIcon.getChildren().get(0).setVisible(false);
			}
		});

		// transferMedia
		IntStream.rangeClosed(0, 2).filter(item -> item != getKey())
			.forEach(item -> transferMedia.getItems().add(new MenuItem("->Player { " + (item + 1) + " }")));
		transferMedia.getItems()
			.forEach(
				item -> item
					.setOnAction(
						a -> Optional.ofNullable(xPlayerModel.songPathProperty().getValue())
							.ifPresent(path -> {

								// Start the selected player
								Main.xPlayersList
									.getXPlayerController(Integer.parseInt(item.getText()
										.replace("->Player { ", "").replace(" }", "")) - 1)
									.playSong(xPlayerModel.songPathProperty().get(),
										xPlayerModel.getCurrentTime());

								// Stop the Current Player
								stop();

							})));

		// =emotionsButton
		emotionsButton.disableProperty().bind(xPlayerModel.songPathProperty().isNull());
		emotionsButton.setOnAction(a -> updateEmotion(emotionsButton));

		// enableHighGraphics
		enableHighGraphics.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.GENERERAL));

		// =settings
		settings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.XPLAYERS));

		// smMinimizeVolume
		smMinimizeVolume.setOnAction(a -> minimizeVolume());
		// smMaximizeVolume
		smMaximizeVolume.setOnAction(a -> maximizeVolume());

		// fadeTranstion
		fadeTransition = new FadeTransition(Duration.millis(1500), advModeVolumeLabel);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		advModeVolumeLabel.setOpacity(0);

		// smBorderPane
		smBorderPane.setVisible(true);

		// extendPlayer
		if (getKey() == 1 || getKey() == 2)
			extendPlayer.setDisable(true);

		// ----------------------------------SIMPLE MODE
		// PLAYER------------------------------------------------

		// speedControlButton
		speedControlButton.setOnAction(a -> speedSlider.setValue(speedSlider.getMax() / 2));

		// speedSlider
		final double speedSliderHalf = (int) speedSlider.getMax() / 2.0;
		// speedSlider.disableProperty().bind(seekService.runningProperty())
		speedSlider.valueProperty().addListener(l -> {
			// int current_second = this.xPlayerModel.getCurrentTime()
			final double value = Math.round(speedSlider.getValue());
			final int intValue = (int) value;

			// Normal by default
			double speedFactor = 1;

			// Make some transformations
			if (intValue > speedSliderHalf) {
				speedFactor = value / (speedSliderHalf / 1);
			} else if (intValue < speedSliderHalf && intValue != 0) {
				speedFactor = value / speedSliderHalf;
			} else if (intValue == 0) {
				speedFactor = 0.05;
			}

			// speedControlButton
			speedControlLabel.setText((speedFactor == 1 ? "" : speedFactor > 1 ? "+" : "-")
				+ InfoTool.getMinString(String.valueOf(speedFactor), 4, ""));

			// Do it!
			if (xPlayer.getSpeedFactor() != speedFactor) {
				xPlayer.setSpeedFactor(speedFactor);

				// If the player is paused or playing
				if (xPlayer.isPausedOrPlaying()) {
					speedIncreaseWorking = true;
					seekService.cancel();
					seek(0);
				}
			}
		});
		speedSlider.setOnScroll(scroll -> speedSlider
			.setValue((int) Math.ceil(speedSlider.getValue() + (scroll.getDeltaY() > 0 ? 1 : -1))));

		// progressBar
		progressBar.getStyleClass().add("transparent-volume-progress-bar" + (key + 1));

		// downloadStackPane
		downloadStackPane.setVisible(false);
	}

	/**
	 * Check's if disc rotation is allowed or not and based on player status it
	 * start's it or stops it
	 */
	public void checkDiscRotation() {

		// Is Player Playing?
		if (!playService.isDiscImageNull() && xPlayer.isPlaying())
			// Is discRotation allowed?
			if (Main.settingsWindow.getxPlayersSettingsController().getAllowDiscRotation().isSelected())
				disc.resumeRotation();
			else
				disc.stopRotation();

	}

	/**
	 * Fixes the Visualizer StackPane when adding it on Simple Mode or Advanced Mode
	 */
	private void simple_And_Advanced_Mode_Fix_Visualizer() {

		// If we are on the simple Mode
		if (!modeToggle.isSelected()) {
			// If the ShowVisualizer on Simple Mode is Selected
			if (showVisualizer.isSelected()) {
				visualizerStackTopParent.getChildren().remove(visualizerStackPane);
				// Check for no duplicates
				if (!smModeCenterStackPane.getChildren().contains(visualizerStackPane))
					smModeCenterStackPane.getChildren().add(visualizerStackPane);
				// If it isn't
			} else {
				smModeCenterStackPane.getChildren().remove(visualizerStackPane);
				// Check for no duplicates
				if (!visualizerStackTopParent.getChildren().contains(visualizerStackPane))
					visualizerStackTopParent.getChildren().add(visualizerStackPane);
			}
			// If we are on Advanced Mode
		} else {
			smModeCenterStackPane.getChildren().remove(visualizerStackPane);
			// Check for no duplicates
			if (!visualizerStackTopParent.getChildren().contains(visualizerStackPane))
				visualizerStackTopParent.getChildren().add(visualizerStackPane);
		}
	}

	/**
	 * This method is called to change the Emotion Image of the Media based on the
	 * current Emotion
	 *
	 * @param emotion
	 */
	public void changeEmotionImage(final Emotion emotion) {
		Main.emotionsWindow.giveEmotionImageToButton(emotionsButton, emotion, 24);
	}

	/**
	 * Update the emotion the user is feeling for this Media
	 */
	public void updateEmotion(final Node node) {

		// Show the Window
		Main.emotionsWindow.show(IOInfo.getFileName(xPlayerModel.getSongPath()), node);

		// Listener
		Main.emotionsWindow.getWindow().showingProperty().addListener(new InvalidationListener() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			public void invalidated(final Observable o) {

				// Remove the listener
				Main.emotionsWindow.getWindow().showingProperty().removeListener(this);

				// !showing?
				if (!Main.emotionsWindow.getWindow().isShowing() && Main.emotionsWindow.wasAccepted()) {

					// Add it the one of the emotions list
					new Thread(() -> Main.emotionListsController.makeEmotionDecisition(
						xPlayerModel.songPathProperty().get(), Main.emotionsWindow.getEmotion())).start();

					// System.out.println(Main.emotionsWindow.getEmotion())

				}
			}
		});

	}

	/**
	 * Opens the current Media File of the player to the default system explorer
	 */
	public void openAudioInExplorer() {
		if (xPlayerModel.songPathProperty().get() != null)
			IOAction.openFileInExplorer(xPlayerModel.songPathProperty().get());
	}

	/**
	 * Opens a FileChooser so the user can select a song File
	 */
	public void openFileChooser() {
		final File file = Main.specialChooser.selectSongFile(Main.window);
		if (file != null)
			playSong(file.getAbsolutePath());
	}

	/**
	 * Returns the volume level of the player.
	 *
	 * @return the volume
	 */
	public int getVolume() {
		return disc.getCurrentVolume();
	}

	/**
	 * Returns the Disc Color.
	 *
	 * @return the disc color
	 */
	public Color getDiscArcColor() {
		return disc.getArcColor();
	}

	/**
	 * Set the volume to this value.
	 *
	 * @param value the new volume
	 */
	public void setVolume(final int value) {
		disc.setVolume(value);

	}

	/**
	 * You can use this method to add or minus from the player volume For example
	 * you can call adjustVolume(+1) or adjustVolume(-1)
	 *
	 * @param value the value
	 */
	public void adjustVolume(final int value) {
		disc.setVolume(disc.getCurrentVolume() + value);
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
	 * Set the speed of the player
	 *
	 * @param speed
	 */
	public void setSpeed(final double speed) {
		speedSlider.setValue(speed);
	}

	/**
	 * Get the speed of the player
	 *
	 * @return
	 */
	public double getSpeed() {
		return speedSlider.getValue();
	}

	/**
	 * Returns the key of the player.
	 *
	 * @return The Key of the Player
	 */
	public int getKey() {
		return key;
	}

	public StackPane getRegionStackPane() {
		return regionStackPane;
	}

	public Label getFxLabel() {
		return playerLoadingLabel;
	}

	/**
	 * Used by resume method.
	 */
	void resumeCode() {
		System.out.println("RESUME code....");

		// Stop the fade animation
		disc.stopFade();

		// image !=null?
		// if (!playService.isDiscImageNull())
		checkDiscRotation();

		// Start the visualizer
		visualizer.startVisualizer();
		if (djVisualizer != null)
			djVisualizer.startVisualizer();

		// Pause Image
		// radialMenu.resumeOrPause.setGraphic(radialMenu.pauseImageView)
	}

	/**
	 * Used by pause method.
	 */
	void pauseCode() {
		System.out.println("PAUSE code....");

		// Play the fade animation
		disc.playFade();

		// Pause the Rotation fo disc
		disc.pauseRotation();

		// Stop the Visualizer
		visualizer.stopVisualizer();
		if (djVisualizer != null)
			djVisualizer.stopVisualizer();

		// Play Image
		// radialMenu.resumeOrPause.setGraphic(radialMenu.playImageView)
	}

	/**
	 * Controls the volume of the player.
	 */
	public void controlVolume() {

		try {

			// Crazy Code here.......
			if (key == 1 || key == 2) {
				final double masterVolumeSlider = Main.djMode.getMixTabInterface().getMasterVolumeSlider().getValue();
				if (Main.djMode.getMixTabInterface().getMasterVolumeSlider().getValue() < 125) { // <100

					Main.xPlayersList.getXPlayer(1).setGain(
						((Main.xPlayersList.getXPlayerController(1).getVolume() / 100.00) * (masterVolumeSlider))
							/ 100.00);
					Main.xPlayersList.getXPlayer(2)
						.setGain(Main.xPlayersList.getXPlayerController(2).getVolume() / 100.00);

				} else if (masterVolumeSlider == 125) { // ==100

					Main.xPlayersList.getXPlayer(1)
						.setGain(Main.xPlayersList.getXPlayerController(1).getVolume() / 100.00);
					Main.xPlayersList.getXPlayer(2)
						.setGain(Main.xPlayersList.getXPlayerController(2).getVolume() / 100.00);

				} else if (masterVolumeSlider > 125) { // >100

					Main.xPlayersList.getXPlayer(1)
						.setGain(Main.xPlayersList.getXPlayerController(1).getVolume() / 100.00);
					Main.xPlayersList.getXPlayer(2)
						.setGain(((Main.xPlayersList.getXPlayerController(2).getVolume() / 100.00)
							* (250 - masterVolumeSlider)) / 100.00);

				}
			} else if (key == 0) {
				xPlayer.setGain(getVolume() / 100.00);
			}

			// //Update PropertiesDB
			// Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + getKey() +
			// "-Volume-Bar", String.valueOf(getVolume()));

			// VisualizerStackController Label
			Platform.runLater(() -> {
				visualizerStackController.replayLabelEffect("Vol: " + getVolume() + " %");

				// Advanced Mode Volume Label
				if (modeToggle.isSelected()) {
					advModeVolumeLabel.setText(getVolume() + " %");
					fadeTransition.playFromStart();
				}
			});
		} catch (final Exception ex) {

			logger.log(Level.INFO, "\n", ex);
		}

	}

	/**
	 * This method is Used by VisualizerWindow class.
	 */
	public void reAddVisualizer() {
		visualizerStackPane.getChildren().add(0, visualizerStackController);
	}

	/**
	 * This method is making the visualizer of the player.
	 */
	public void makeTheVisualizer() {

		// Visualizer
		visualizer = new XPlayerVisualizer(this, true);
		visualizer
			.setShowFPS(Main.settingsWindow.getxPlayersSettingsController().getShowFPS().selectedProperty().get());

		// DjVisualizer
		if (this.getKey() == 1 || this.getKey() == 2) {
			djVisualizer = new XPlayerVisualizer(this, false);
			djVisualizer.setDisplayMode(Integer.parseInt(VisualizerType.VERTICAL_VOLUME_METER.toString()));
		}

		// Select the correct toggle
		visualizerWindow.getVisualizerTypeGroup()
			.selectToggle(visualizerWindow.getVisualizerTypeGroup().getToggles().get(visualizer.displayMode.get()));

		// When displayMode is being updated
		visualizer.displayMode.addListener((observable, oldValue, newValue) -> {

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + getKey() + "-Visualizer-DisplayMode",
				Integer.toString(newValue.intValue()));

			// ----------
			visualizerWindow.getVisualizerTypeGroup()
				.selectToggle(visualizerWindow.getVisualizerTypeGroup().getToggles().get(newValue.intValue()));
			visualizerStackController.replayLabelEffect(
				((RadioMenuItem) visualizerWindow.getVisualizerTypeGroup().getSelectedToggle()).getText());
		});

		// -----------visualizerTypeGroup
		visualizerWindow.getVisualizerTypeGroup().getToggles()
			.forEach(toggle -> ((RadioMenuItem) toggle).setOnAction(a -> visualizer.displayMode
				.set(visualizerWindow.getVisualizerTypeGroup().getToggles().indexOf(toggle))));

		// visualizerTabPane
		visualizerTabPane.addEventFilter(KeyEvent.ANY, event -> {
			if (event.getCode().isArrowKey()) {
				// System.out.println("Is arrow key");
				event.consume();
			}
		});

		// VisualizerStackController
		visualizerStackController.getChildren().add(0, visualizer);
		visualizerStackController.visibleProperty().bind(visualizerVisibility);
		visualizerStackController.addListenersToButtons(this);

		// Add VisualizerStackController to the VisualizerStackPane
		visualizerStackPane.getChildren().add(0, visualizerStackController);

		// visualizerSettingsHBox
		visualizerSettingsHBox.visibleProperty()
			.bind(visualizerWindow.getStage().showingProperty().not().and(visualizerStackPane.hoverProperty()));

		// visualizerSettings
		visualizerSettings.setOnMouseReleased(m -> {
			final Bounds bounds = visualizerSettings.localToScreen(visualizerSettings.getBoundsInLocal());
			visualizerWindow.getVisualizerContextMenu().show(visualizerSettings, bounds.getMinX(),
				bounds.getMaxY());
		});

		// maximizeVisualizer
		maximizeVisualizer.disableProperty().bind(visualizerVisibility.not());
		maximizeVisualizer.setOnAction(e -> visualizerWindow.displayVisualizer());

		// showVisualizerButton
		showVisualizerButton.setOnAction(a -> visualizerVisibility.set(!visualizerVisibility.get()));

		// visualizerVisibility
		visualizerVisibility.addListener((observable, oldValue, newValue) -> visualizerEyeIcon
			.setFill(newValue ? Color.web("#d4ff00") : Color.FIREBRICK));

		// visualizerVisibleLabel
		visualizerVisibleLabel.visibleProperty().bind(visualizerVisibility.not());
		visualizerVisibleLabel.setOnAction(a -> visualizerVisibility.set(true));

		// visualizerMaximizedHBox
		visualizerMaximizedBox.visibleProperty().bind(visualizerWindow.getStage().showingProperty());

		// visualizerMinimize
		visualizerMinimize.setOnAction(m -> visualizerWindow.removeVisualizer());

		// visualizerRequestFocus
		visualizerRequestFocus.setOnAction(m -> visualizerWindow.getStage().requestFocus());

		// playerStatusLabel
		playerStatusLabel.visibleProperty().bind(visualizer.getAnimationService().runningProperty().not());
		visualizerLabel.visibleProperty().bind(playerStatusLabel.visibleProperty());

		// Equalizer
		equalizer = new XPlayerEqualizer(this);
		equalizerTab.setContent(equalizer);

		// Pad 8
		// xPlayerPad = new XPlayerPad(this)
		// padTab.setContent(xPlayerPad)

	}

	/**
	 * This method is making the disc of the player.
	 *
	 * @param arcColor the color of the disc
	 * @param volume the volume
	 * @param side the side
	 */
	public void makeTheDisc(final Color arcColor, final int volume, final int minimumVolume, final int maximumVolume,
		final Side side) {

		// Create DJDisc
		disc = new DJDisc(136, arcColor, volume, maximumVolume);

		// ------------waveFormVisualization-----------------------
		waveFormVisualization.setForeground(arcColor);
		waveFormVisualization.setOnMouseReleased(m -> {
			final double percentage = m.getX() / waveFormVisualization.getWidth();
			seekTo((int) (percentage * this.xPlayerModel.getDuration()));
		});
		waveFormVisualization.addEventHandler(MouseEvent.MOUSE_MOVED, m -> {
			final double percentage = m.getX() / waveFormVisualization.getWidth();
			final int timeNow = (int) (percentage * this.xPlayerModel.getDuration());

			// == RemainingTimeLabel
			remainingTimeLabel.setText(TimeTool.getTimeEdited(xPlayerModel.getDuration() - timeNow));

			// == ElapsedTimeLabel
			elapsedTimeLabel.setText(TimeTool.getTimeEdited(timeNow));

		});

		// waveProgressLabel
		waveProgressLabel.visibleProperty().bind(waveFormVisualization.getWaveService().runningProperty()
			.or(waveFormVisualization.getAnimationService().runningProperty().not()));

		// waveProgressBar
		waveProgressBar.getStyleClass().add("transparent-volume-progress-bar" + (key + 1));
		waveProgressBar.visibleProperty().bind(waveFormVisualization.getWaveService().runningProperty());

		// Add it to waveStackPane
		waveStackPane.getChildren().add(0, waveFormVisualization);

		// ----------------------------------------------------------

		// smImageView
		smImageView.imageProperty().bind(disc.getImageView().imageProperty());
		smImageView.fitWidthProperty()
			.bind(Bindings.when(smModeCenterStackPane.widthProperty().lessThan(smBorderPane.widthProperty()))
				.then(smModeCenterStackPane.widthProperty().subtract(20)).otherwise(0));
		smImageView.fitHeightProperty()
			.bind(Bindings.when(smModeCenterStackPane.heightProperty().lessThan(smBorderPane.heightProperty()))
				.then(smModeCenterStackPane.heightProperty().subtract(20)).otherwise(0));
		smImageView.visibleProperty().bind(
			smModeCenterStackPane.heightProperty().greaterThan(70).and(smImageView.imageProperty().isNotNull()));
		smModeCenterStackPane.visibleProperty().bind(smModeCenterStackPane.heightProperty().greaterThan(60));
		smModeCenterStackPane.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
			// if (smAlbumFontIcon.isVisible())
			smAlbumFontIcon.setIconSize((int) ((newValue.getHeight() + 1) / 1.4 + 1));
		});

		// smAlbumFontIcon
		smAlbumFontIcon.visibleProperty().bind(smImageView.visibleProperty().not());

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
					seek(xPlayerModel.getCurrentAngleTime() - xPlayerModel.getCurrentTime());

				}

			} else
				discIsDragging = false;

		});

		// Canvas Mouse Dragging
		disc.getCanvas().setOnMouseDragged(m -> {

			// MouseButton==Primary || Secondary
			if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.SECONDARY
				|| m.getButton() == MouseButton.MIDDLE)

				// duration!=0 and duration!=-1
				if (xPlayerModel.getDuration() != 0 && xPlayerModel.getDuration() != -1) {

					// TotalTime and CurrentTime
					final int totalTime = xPlayerModel.getDuration(), currentTime = xPlayerModel.getCurrentAngleTime();

					// Set the cursor
					disc.getCanvas().setCursor(Cursor.CLOSED_HAND);

					// Try to do the dragging
					discIsDragging = true;
					xPlayerModel.setCurrentAngleTime(disc.getValue(xPlayerModel.getDuration()));
					disc.calculateAngleByMouse(m, currentTime, totalTime);

					// == RemainingTimeLabel
					remainingTimeLabel.setText(TimeTool.getTimeEdited(totalTime - currentTime)); // + "." + ( 9 -
					// Integer.parseInt(millisecondsFormatted.replace(".",
					// "")) ))

					// == ElapsedTimeLabel
					elapsedTimeLabel.setText(TimeTool.getTimeEdited(currentTime)); // + millisecondsFormatted + "")

				}
		});
		disc.currentVolumeProperty().addListener((observable, oldValue, newValue) -> {

			// Update the Volume
			controlVolume();

			// Update the Label
			smVolumeSliderLabel.setText(disc.getCurrentVolume() + " %");

			// smVolumeSlider
			smVolumeSlider.setValue(newValue.doubleValue());

			// volumeSliderProgBar
			volumeSliderProgBar.setProgress(smVolumeSlider.getValue() / smVolumeSlider.getMax());

		});

		/// smTimeSlider
		smTimeSlider.setOnMouseMoved(m -> {
			// File is either corrupted or error or no File entered yet
			if (xPlayerModel.getDuration() == 0 || xPlayerModel.getDuration() == -1) {
				smTimeSlider.setCursor(noSeekCursor);
				// smTimeSlider.setDisable(true)
				// !discIsDragging
			} else if (!discIsDragging) {
				smTimeSlider.setCursor(Cursor.OPEN_HAND);
				// smTimeSlider.setDisable(false)
			}
		});
		smTimeSlider.setOnMouseReleased(m -> {
			// Security check
			if (!passTimerSecurityTest())
				return;

			// Check if the slider is not allowed to move
			if (smTimeSlider.getCursor() == noSeekCursor)
				smTimeSlider.setValue(0);

			// PrimaryMouseButton
			if (m.getButton() == MouseButton.PRIMARY) {

				// discIsDragging and MouseButton==Primary
				// and duration!=0 and duration!=-1
				if (discIsDragging && xPlayerModel.getDuration() != 0 && xPlayerModel.getDuration() != -1) {

					// Try to seek
					seek(xPlayerModel.getCurrentAngleTime() - xPlayerModel.getCurrentTime());

				} else if (!discIsDragging) {

					// TotalTime and CurrentTime
					final int totalTime = xPlayerModel.getDuration(), currentTime = xPlayerModel.getCurrentAngleTime();

					// Keep the disc refreshed based on time slider value
					xPlayerModel.setCurrentAngleTime((int) smTimeSlider.getValue());
					disc.calculateAngleByMouse(m, currentTime, totalTime);

					// Try to seek
					seek(xPlayerModel.getCurrentAngleTime() - xPlayerModel.getCurrentTime());

				}

				// discIsDragging = false

			} else
				discIsDragging = false;

		});
		smTimeSlider.setOnMousePressed(m -> {
			// Security check
			if (!passTimerSecurityTest())
				return;
		});
		smTimeSlider.setOnMouseDragged(m -> {
			// Security check
			if (!passTimerSecurityTest())
				return;

			// MouseButton==Primary || Secondary
			if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.SECONDARY
				|| m.getButton() == MouseButton.MIDDLE)

				// duration!=0 and duration!=-1
				if (xPlayerModel.getDuration() != 0 && xPlayerModel.getDuration() != -1) {

					// TotalTime and CurrentTime
					final int totalTime = xPlayerModel.getDuration(), currentTime = xPlayerModel.getCurrentAngleTime();

					// Set the cursor
					smTimeSlider.setCursor(Cursor.CLOSED_HAND);

					// Try to do the dragging
					discIsDragging = true;
					xPlayerModel.setCurrentAngleTime((int) smTimeSlider.getValue());
					disc.calculateAngleByMouse(m, currentTime, totalTime);

					// smTimeSliderLabel
					smTimeSliderLabel
						.setText(TimeTool.getTimeEdited(currentTime) + "  / " + TimeTool.getTimeEdited(totalTime));

					// smTimeSliderProgress
					smTimeSliderProgress.setProgress(smTimeSlider.getValue() / smTimeSlider.getMax());
				}

			System.out.println(discIsDragging);
		});

		// smTimeSliderProgress
		smTimeSliderProgress.getStyleClass().add("transparent-volume-progress-bar" + (key + 1));

		// smVolumeSlider
		smVolumeSlider.setMin(0);
		smVolumeSlider.setMax(maximumVolume - 1.00);
		smVolumeSlider.valueProperty().addListener(l -> disc.setVolume(smVolumeSlider.getValue()));
		smVolumeSlider.setValue(volume);
		smVolumeSlider.setOnScroll(scroll -> adjustVolume(scroll.getDeltaY() > 0 ? 2 : -2));
		smVolumeSlider.heightProperty().addListener((observable, oldValue, newValue) -> {

			// New Value
			final double newValuee = newValue.doubleValue();

			// Set the Height
			volumeSliderProgBar.setPrefWidth(newValuee);

			// Fix the Positioning
			// System.out.println("Player : " + this.getKey() + ": " + newValuee)

			// A cancerous bug is here , so cancerous that it makes me wanna brake the
			// fucking laptop
			// TODO FIX THIS FUCKING BUG FUCKING FUCKING FUCKING BUG
			// MAKES THE UI DANCE LIKE A STRIPPER
			// if (newValuee == previousSliderHeight || newValuee == previousSliderHeight +
			// 1 || newValuee == previousSliderHeight - 1) { //Damn bug
			// // double[] positions =
			// Main.libraryMode.getTopSplitPane().getDividerPositions();
			// // positions[0] += 0.03;
			// // Main.libraryMode.getTopSplitPane().setDividerPositions(positions);
			// // //Run Later
			// // Platform.runLater(() -> {
			// // System.out.println("FUCK!!!!!!");
			// // double[] positions2 =
			// Main.libraryMode.getTopSplitPane().getDividerPositions();
			// // positions2[0] -= 0.03;
			// // Main.libraryMode.getTopSplitPane().setDividerPositions(positions2);
			// // });
			// // System.out.println("Duplicate");
			// } else {
			// previousSliderHeight = newValuee;
			// }

			// Keep fixed the UI
			if (getKey() == 0) {
				Main.xPlayersList.getXPlayerController(1).volumeSliderProgBar.setPrefWidth(newValuee);
				Main.xPlayersList.getXPlayerController(2).volumeSliderProgBar.setPrefWidth(newValuee);
			} else {
				Main.xPlayersList.getXPlayerController(0).volumeSliderProgBar.setPrefWidth(newValuee);
			}

		});

		// volumeSliderProgBar
		volumeSliderProgBar.getStyleClass().add("transparent-volume-progress-bar" + (key + 1));
		volumeSliderProgBar.setProgress(smVolumeSlider.getValue() / smVolumeSlider.getMax());

		// DiscStackPane
		diskStackPane.setOnScroll(smVolumeSlider.getOnScroll());

		// Recalculate Volume Disc Size
		discBorderPane.boundsInLocalProperty()
			.addListener((observable, oldValue, newValue) -> reCalculateDiscStackPane());

		// Add disc and volume disc to StackPane
		diskStackPane.getChildren().addAll(disc);// , volumeDisc)

		// ---------------------SIDE------------------------------------------------
		changeSide(side);
	}

	private final int previousSliderHeight = 0;

	/**
	 * Just a small security check for the time slider
	 */
	private boolean passTimerSecurityTest() {

		// Security check
		if (xPlayerModel.getSongPath() == null) {
			smTimeSlider.setValue(0.0);
			return false;
		}

		return true;
	}

	/**
	 * Keep track on which side the Player Currently is . Give a fake value for the
	 * beginning
	 */
	Side discCurrentSide = Side.LEFT;

	/**
	 * Change the Side of XPlayerController , either to right or left MA NIGAAAA!!!
	 *
	 * @param newSide
	 */
	public void changeSide(final Side newSide) {
		// Check if already is in the given side
		if (discCurrentSide == newSide)
			return;
		else
			discCurrentSide = newSide;

		// DO YOUR JOB
		if (newSide == Side.LEFT) {

			internalSplitPane.getItems().remove(discBorderPane);
			internalSplitPane.getItems().add(0, discBorderPane);

			// Volume Slider
			rootBorderPane.getChildren().remove(volumeBarBox);
			rootBorderPane.setLeft(volumeBarBox);

			mediaNameHBox.getChildren().clear();
			mediaNameHBox.getChildren().addAll(timersBox, mediaFileMarquee, copyButton, mediaTagImageButton);

			// Speed Slider
			discBorderPane.getChildren().remove(speedSliderStackPane);
			discBorderPane.setLeft(speedSliderStackPane);

			// Modes HBox
			final HBox modesHBox = (HBox) modeToggle.getParent();
			modesHBox.getChildren().clear();
			modesHBox.getChildren().addAll(modeToggle, historyToggle);

			// Top HBox
			final HBox topHBox = (HBox) topInfoLabel.getParent();
			topHBox.getChildren().clear();
			topHBox.getChildren().addAll(toolsHBox, topInfoLabel, emotionsButton, extendPlayer);

			// JFXTabPane
			equalizerTab.getTabPane().setSide(Side.RIGHT);

			// toolsHBox
			toolsHBox.getChildren().clear();
			toolsHBox.getChildren().addAll(muteButton, showMenu, openFile, settings, transferMedia);

		} else if (newSide == Side.RIGHT) {

			internalSplitPane.getItems().remove(discBorderPane);
			internalSplitPane.getItems().add(1, discBorderPane);

			// Volume Slider
			rootBorderPane.getChildren().remove(volumeBarBox);
			rootBorderPane.setRight(volumeBarBox);

			mediaNameHBox.getChildren().clear();
			mediaNameHBox.getChildren().addAll(mediaTagImageButton, copyButton, mediaFileMarquee, timersBox);

			// Speed Slider
			discBorderPane.getChildren().remove(speedSliderStackPane);
			discBorderPane.setRight(speedSliderStackPane);

			// Modes HBox
			final HBox modesHBox = (HBox) modeToggle.getParent();
			modesHBox.getChildren().clear();
			modesHBox.getChildren().addAll(historyToggle, modeToggle);

			// Top HBox
			final HBox topHBox = (HBox) topInfoLabel.getParent();
			topHBox.getChildren().clear();
			topHBox.getChildren().addAll(extendPlayer, emotionsButton, topInfoLabel, toolsHBox);

			// JFXTabPane
			equalizerTab.getTabPane().setSide(Side.LEFT);

			// toolsHBox
			toolsHBox.getChildren().clear();
			toolsHBox.getChildren().addAll(transferMedia, settings, openFile, showMenu, muteButton);

		}
	}

	/**
	 * Recalculates the Canvas size to the preferred size
	 */
	private void reCalculateCanvasSize() {
		// double size = Math.min(diskStackPane.getWidth(), diskStackPane.getHeight()) /
		// 1.1
		final double size = Math.min(discBorderPane.getWidth() - speedSliderStackPane.getWidth(),
			discBorderPane.getHeight() - diskStackPane1.getHeight() - waveFormVisualization.getHeight());

		disc.resizeDisc(size);
		// radialMenu.getRadialMenuButton().setPrefSize(disc.getMinWidth(),
		// disc.getMinHeight())
		// System.out.println("Redrawing canvas")
	}

	/**
	 * Makes the DJDisc fit correctly into it's StackPane
	 */
	public void reCalculateDiscStackPane() {

		// Call it for the DJDisc
		reCalculateCanvasSize();

		// System.out.println(disc.getPrefWidth())

		// Find the correct size for the VolumeDisc
		double size;
		if (disc.getPrefWidth() < 80)
			size = disc.getPrefWidth() / 1.55;
		else if (disc.getPrefWidth() < 165)
			size = disc.getPrefWidth() / 1.25;
		else
			size = disc.getPrefWidth() / 1.15;

		// volumeDisc.resizeDisc(size, size)
	}

	/**
	 * When the audio starts , fast configure it's settings
	 *
	 * @param ignoreStartImmediately
	 */
	public void configureMediaSettings(final boolean ignoreStartImmediately) {

		// Start immediately?
		if (!ignoreStartImmediately
			&& !Main.settingsWindow.getxPlayersSettingsController().getStartImmediately().isSelected())
			pause();
		else {
			play();
			resume();
		}

		// Mute?
		xPlayer.setMute(muteButton.isSelected());
		// System.out.println("Mute is Selected? " + muteButton.isSelected())

		// Volume
		controlVolume();

		// Audio is MP3?
		if (!"mp3".equals(xPlayerModel.songExtensionProperty().get()))
			equalizer.setDisable(true);
		else {
			xPlayer.setEqualizer(xPlayerModel.getEqualizerArray(), 32);
			equalizer.setDisable(false);
		}

		// Sets Pan value. Line should be opened before calling this method.
		// Linear scale : -1.0 <--> +1.0
		xPlayer.setPan(equalizer.getPanFilter().getValueTransformed());
		// System.out.println("Eq Pan value :" +
		// equalizer.getPanFilter().getValueTransformed());

		// Represents a control for the relative balance of a stereo signal
		// between two stereo speakers. The valid range of values is -1.0 (left
		// channel only) to 1.0 (right channel only). The default is 0.0
		// (centered).
		// xPlayer.setBalance((float)
		// equalizer.getBalanceFilter().getValueTransformed());

	}

	/**
	 * Resets player labels etc to zero
	 */
	public void fixPlayerStop() {
		// System.out.println("Entered fixPlayerStop()");

		// == RemainingTimeLabel
		remainingTimeLabel.setText("00:00");

		// == ElapsedTimeLabel
		elapsedTimeLabel.setText("00:00");

		// == Visualizer Window
		visualizerWindow.getProgressBar().setProgress(0);

		// smTimeSlider
		smTimeSlider.setValue(0);

		// smTimeSliderLabel
		smTimeSliderLabel
			.setText(TimeTool.getTimeEdited(0) + "  / " + TimeTool.getTimeEdited(xPlayerModel.getDuration()));
	}

	// @Override
	// public void volumeChanged(int volume) {
	// controlVolume();
	// }

	/**
	 * Replay the current song
	 */
	public void replay() {

		if (xPlayerModel.songExtensionProperty().get() != null)
			playService.startPlayService(xPlayerModel.songPathProperty().get(), 0);
		else
			AlertTool.showNotification("No Previous File", "Drag and Drop or Add a File or URL on this player.",
				Duration.millis(1500), NotificationType.INFORMATION);

		// if (thisSong instanceof URL)
		// return playSong(((URL) thisSong).toString(), totalTime);
		// else if (thisSong instanceof File)
		// return playSong(((File) thisSong).getAbsolutePath(), totalTime);

	}

	/**
	 * Play the current song.
	 *
	 * @param absolutePath The absolute path of the file
	 */
	public void playSong(final String absolutePath) {

		playService.startPlayService(absolutePath, 0);

	}

	/**
	 * Play the current song.
	 *
	 * @param absolutePath The absolute path of the file
	 * @param startingSecond From which second to start the audio , this will not be
	 * exactly accurate
	 */
	public void playSong(final String absolutePath, final int startingSecond) {

		playService.startPlayService(absolutePath, startingSecond);

	}

	// ---------------------------------------------------Player
	// Actions------------------------------------------------------------------

	public boolean speedIncreaseWorking = false;

	/**
	 * Tries to skip forward or backward
	 *
	 * @param seconds Seconds to seek
	 */
	public void seek(final int seconds) {
		boolean securityPass = false;

		// If second==0
		if (seconds == 0) {
			securityPass = true;
		}

		//

		if (seconds < 0 && (seconds + xPlayerModel.getCurrentTime() >= 0)) { // negative seek

			System.out.println("Skipping backwards ...[" + seconds + "] seconds");

			securityPass = true;
		} else if (seconds > 0 && (seconds + xPlayerModel.getCurrentTime() <= xPlayerModel.getDuration())) { // positive
			// seek

			System.out.println("Skipping forward ...[" + seconds + "] seconds");

			securityPass = true;
		}

		// Ok/?
		if (securityPass) {

			// Add or Remove
			xPlayerModel.setCurrentAngleTime(xPlayerModel.getCurrentTime() + seconds);

			// //Seek
			// System.out.println("Original: "
			// + (xPlayerModel.getCurrentAngleTime()) * (xPlayer.getTotalBytes() /
			// xPlayerModel.getDuration())
			// + " With double:" + (long) (((float) xPlayerModel.getCurrentAngleTime())
			// * (xPlayer.getTotalBytes() / (float) xPlayerModel.getDuration())))

			// Start the Service
			seekService.startSeekService((long) ((xPlayerModel.getCurrentAngleTime())
				* (xPlayer.getTotalBytes() / (float) xPlayerModel.getDuration())), false);
		}

	}

	/**
	 * This method is used to seek to a specific time of the audio
	 *
	 * @param seconds
	 */
	public void seekTo(final int seconds) {

		if (seconds < 0 || seconds >= xPlayerModel.getDuration())
			return;

		// Set
		xPlayerModel.setCurrentAngleTime(seconds);

		// Seek To
		seekService.startSeekService(
			(xPlayerModel.getCurrentAngleTime()) * (xPlayer.getTotalBytes() / xPlayerModel.getDuration()), true);

	}

	/**
	 * Set the mute of the Line. Note that mute status does not affect gain.
	 *
	 * @param value True to mute the audio of False to unmute it
	 */
	public void setMute(final boolean value) {
		muteButton.setSelected(value);
		xPlayer.setMute(value);

	}

	/**
	 * Starts the player
	 */
	public void play() {
		try {
			xPlayer.play();
		} catch (final StreamPlayerException ex) {
			ex.printStackTrace();
		}
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
	 * Stop the player.
	 */
	public void stop() {
		xPlayer.stop();
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
			replay();

	}

	/**
	 * If the player is paused it resume it else if it is stopped it replays the
	 * Media
	 */
	public void playOrReplay() {
		if (xPlayer.isPaused()) // paused?
			resume();
		else if (xPlayer.isStopped() || xPlayer.isUnknown())
			replay();
	}

	public XPlayerPlaylist getxPlayerPlayList() {
		return xPlayerPlayList;
	}

	public Button getMediaTagImageButton() {
		return mediaTagImageButton;
	}

	public Button getBackwardButton() {
		return backwardButton;
	}

	public Button getForwardButton() {
		return forwardButton;
	}

	public Label getTotalTimeLabel() {
		return totalTimeLabel;
	}

	public void setTotalTimeLabel(final Label totalTimeLabel) {
		this.totalTimeLabel = totalTimeLabel;
	}

	public Button getPlayPauseButton() {
		return playPauseButton;
	}

	public Button getEmotionsButton() {
		return emotionsButton;
	}

	public StackPane getDiskStackPane() {
		return diskStackPane;
	}

	public Label getVisualizationsDisabledLabel() {
		return visualizationsDisabledLabel;
	}

	public Button getSmPlayPauseButton() {
		return smPlayPauseButton;
	}

	public ToggleButton getModeToggle() {
		return modeToggle;

	}

	public ProgressIndicator getProgressIndicator() {
		return progressBar;
	}

	public ToggleButton getShowVisualizer() {
		return showVisualizer;
	}

	public Label getRemainingTimeLabel() {
		return remainingTimeLabel;
	}

	public Label getElapsedTimeLabel() {
		return elapsedTimeLabel;
	}

	public Label getSmTimeSliderLabel() {
		return smTimeSliderLabel;
	}

	public Label getPlayerLoadingLabel() {
		return playerLoadingLabel;
	}

	public ImageView getMediaTagImageView() {
		return mediaTagImageView;
	}

	public ToggleButton getMuteButton() {
		return muteButton;
	}

	public boolean isMuteButtonSelected() {
		return muteButton.isSelected();
	}

	public ProgressBar getVolumeSliderProgBar() {
		return volumeSliderProgBar;
	}

	/**
	 * muteButton.setSelected(!muteButton.isSelected());
	 */
	public void revertMuteButton() {
		muteButton.setSelected(!muteButton.isSelected());
	}

	public VBox getVolumeBarBox() {
		return volumeBarBox;
	}

	public BorderPane getRootBorderPane() {
		return rootBorderPane;
	}

	public XPlayerVisualizer getDjVisualizer() {
		return djVisualizer;
	}

	public WaveVisualization getWaveFormVisualization() {
		return waveFormVisualization;
	}

	public Label getWaveProgressLabel() {
		return waveProgressLabel;
	}

	public StackPane getModesStackPane() {
		return modesStackPane;
	}

	public ProgressBar getWaveProgressBar() {
		return waveProgressBar;
	}

}
