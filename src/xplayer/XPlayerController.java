/*
 * 
 */
package xplayer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import application.presenter.custom.DJDisc;
import application.presenter.custom.DJDiscListener;
import application.presenter.custom.Marquee;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import application.windows.EmotionsWindow.Emotion;
import application.windows.XPlayerWindow;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import smartcontroller.Genre;
import smartcontroller.media.Audio;
import xplayer.model.XPlayer;
import xplayer.model.XPlayerModel;
import xplayer.services.XPlayerPlayService;
import xplayer.services.XPlayerSeekService;
import xplayer.streamplayer.Status;
import xplayer.streamplayer.StreamPlayerEvent;
import xplayer.streamplayer.StreamPlayerException;
import xplayer.streamplayer.StreamPlayerListener;
import xplayer.visualizer.view.VisualizerStackController;
import xplayer.visualizer.view.VisualizerWindowController;
import xplayer.visualizer.view.XPlayerVisualizer;

/**
 * Represents the graphical interface for the deck.
 *
 * @author GOXR3PLUS
 */
public class XPlayerController extends StackPane implements DJDiscListener, StreamPlayerListener {
	
	public static final Image playImage = InfoTool.getImageFromResourcesFolder("play.png");
	public static final Image pauseImage = InfoTool.getImageFromResourcesFolder("pause.png");
	
	//-----------------------------------------------
	
	@FXML
	private StackPane xPlayerStackPane;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private GridPane basicGridPane;
	
	@FXML
	private Button previousSongButton;
	
	@FXML
	private ToggleButton muteButton;
	
	@FXML
	private Button playPauseButton;
	
	@FXML
	private Button stopButton;
	
	@FXML
	private Button backwardButton;
	
	@FXML
	private Button replayButton;
	
	@FXML
	private Button forwardButton;
	
	@FXML
	private Button nextSongButton;
	
	@FXML
	private StackPane visualizerStackPane;
	
	@FXML
	private Label playerStatusLabel;
	
	@FXML
	private Label visualizerVisibleLabel;
	
	@FXML
	private HBox visualizerMaximizedHBox;
	
	@FXML
	private Label visualizerMinimize;
	
	@FXML
	private Label visualizerRequestFocus;
	
	@FXML
	private HBox visualizerSettingsHBox;
	
	@FXML
	private Button visualizerSettings;
	
	@FXML
	private ToggleButton visualizerVisible;
	
	@FXML
	private Button maximizeVisualizer;
	
	@FXML
	private BorderPane discBorderPane;
	
	@FXML
	private StackPane diskStackPane;
	
	@FXML
	private Button emotionsButton;
	
	@FXML
	private HBox mediaNameHBox;
	
	@FXML
	private Button mediaTagImageButton;
	
	@FXML
	private Label elapsedTimeLabel;
	
	@FXML
	private Label remainingTimeLabel;
	
	@FXML
	private Label totalTimeLabel;
	
	@FXML
	private Tab equalizerTab;
	
	@FXML
	private Tab padTab;
	
	@FXML
	private Label topInfoLabel;
	
	@FXML
	private JFXToggleButton settingsToggle;
	
	@FXML
	private Button extendPlayer;
	
	@FXML
	private MenuItem openFile;
	
	@FXML
	private Menu transferMedia;
	
	@FXML
	private MenuItem showEmotionLists;
	
	@FXML
	private StackPane regionStackPane;
	
	@FXML
	private Label bugLabel;
	
	@FXML
	private JFXSpinner fxSpinner;
	
	@FXML
	private Label fxLabel;
	
	@FXML
	private Label restorePlayer;
	
	@FXML
	private Label focusXPlayerWindow;
	
	// -----------------------------------------------------------------------------
	
	public final Logger logger = Logger.getLogger(getClass().getName());
	
	// ------------------------- Images/ImageViews --------------------------
	
	private final ImageView eye = InfoTool.getImageViewFromResourcesFolder("eye.png");
	private final ImageView eyeDisabled = InfoTool.getImageViewFromResourcesFolder("eyeDisabled.png");
	
	private static final Image noSeek = InfoTool.getImageFromResourcesFolder("Private-" + ( ImageCursor.getBestSize(64, 64).getWidth() < 64.00 ? "32" : "64" ) + ".png");
	private static final ImageCursor noSeekCursor = new ImageCursor(noSeek, noSeek.getWidth() / 2, noSeek.getHeight() / 2);
	
	// ------------------------- Services --------------------------
	
	/** The seek service. */
	private final XPlayerSeekService seekService = new XPlayerSeekService(this);
	
	/** The play service. */
	private final XPlayerPlayService playService = new XPlayerPlayService(this);
	
	// ------------------------- Variables --------------------------
	/** The key. */
	private final int key;
	
	/** The disc is being mouse dragged */
	public boolean discIsDragging;
	
	// -------------------------ETC --------------------------
	
	private XPlayerPlaylist xPlayerPlayList;
	
	private XPlayerWindow xPlayerWindow;
	
	/** The x player settings controller. */
	private XPlayerExtraSettings playerExtraSettings;
	
	/** The x player model. */
	private XPlayerModel xPlayerModel;
	
	/** The x player. */
	private XPlayer xPlayer;
	
	/** The radial menu. */
	private XPlayerRadialMenu radialMenu;
	
	/** The visualizer window. */
	private VisualizerWindowController visualizerWindow;
	
	/**
	 * This controller contains a Visualizer and a Label which describes every time (for some milliseconds) which type of visualizer is being
	 * displayed (for example [ Oscilloscope , Rosette , Spectrum Bars etc...]);
	 */
	private final VisualizerStackController visualizerStackController = new VisualizerStackController();
	
	/** The visualizer. */
	private XPlayerVisualizer visualizer;
	
	/** The equalizer. */
	private XPlayerEqualizer equalizer;
	
	private XPlayerPad xPlayerPad;
	
	/** The disc. */
	private DJDisc disc;
	
	private final Marquee mediaFileMarquee = new Marquee();
	
	private final FlipPanel flipPane = new FlipPanel(Orientation.HORIZONTAL);
	
	//======= Events ===========
	
	public final EventHandler<? super MouseEvent> audioDragEvent = event -> {
		String absolutePath = xPlayerModel.songPathProperty().get();
		if (absolutePath != null) {
			
			/* Allow copy transfer mode */
			Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);
			
			/* Put a String into the dragBoard */
			ClipboardContent content = new ClipboardContent();
			content.putFiles(Arrays.asList(new File(absolutePath)));
			db.setContent(content);
			
			/* Set the DragView */
			new Audio(absolutePath, 0.0, 0, "", "", Genre.SEARCHWINDOW, -1).setDragView(db);
		}
		event.consume();
	};
	
	public final EventHandler<? super DragEvent> audioDropEvent = event -> {
		
		//We don't want the player to start if the drop event is for the XPlayer PlayList
		if (!flipPane.isBackVisible()) {
			
			// Keeping the absolute path
			String absolutePath;
			
			// File?
			for (File file : event.getDragboard().getFiles()) {
				absolutePath = file.getAbsolutePath();
				if (file.isFile() && InfoTool.isAudioSupported(absolutePath)) {
					// Ask Question?
					if (xPlayer.isPausedOrPlaying() && Main.settingsWindow.getxPlayersSettingsController().getAskSecurityQuestion().isSelected()) {
						if (ActionTool.doQuestion("A song is already playing on this deck.\n Are you sure you want to replace it?",
								visualizerWindow.getStage().isShowing() && !xPlayerWindow.getWindow().isShowing() ? visualizerWindow : xPlayerStackPane, Main.window))
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
			
			event.setDropCompleted(true);
			
		}
	};
	
	//============================================================================================
	
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "XPlayerController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
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
	 * @return the settingsToggle
	 */
	public JFXToggleButton getSettingsToggle() {
		return settingsToggle;
	}
	
	/**
	 * Returns the XPlayerStackPane back to the XPlayerController if it is on XPlayer external Window
	 */
	public void restorePlayerStackPane() {
		this.getChildren().add(getXPlayerStackPane());
	}
	
	/** Called as soon as the .fxml has been loaded */
	@FXML
	private void initialize() {
		
		// -----XPlayer and XPlayerModel-------------
		xPlayerModel = new XPlayerModel();
		xPlayer = new XPlayer();
		xPlayer.addStreamPlayerListener(this);
		
		// -----Important-------------
		xPlayerWindow = new XPlayerWindow(this);
		
		//== RadialMenu
		radialMenu = new XPlayerRadialMenu(this);
		radialMenu.mute.selectedProperty().addListener(l -> {
			xPlayer.setMute(radialMenu.mute.isSelected());
			muteButton.setSelected(radialMenu.mute.isSelected());
			
			//System.out.println("Entered Radial Menu");
		});
		muteButton.selectedProperty().addListener(l -> {
			xPlayer.setMute(radialMenu.mute.isSelected());
			radialMenu.mute.setSelected(muteButton.isSelected());
			
			//System.out.println("Entered Menu Button");
		});
		
		//
		xPlayerPlayList = new XPlayerPlaylist(this);
		visualizerWindow = new VisualizerWindowController(this);
		playerExtraSettings = new XPlayerExtraSettings(this);
		
		//== borderPane
		borderPane.setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
		borderPane.setOnDragDropped(audioDropEvent);
		
		//== regionStackPane
		regionStackPane.setVisible(false);
		
		// mediaFileStackPane	
		mediaFileMarquee.setText("Drag a song on this deck to load it");
		mediaFileMarquee.setOnMouseClicked(m -> openAudioInExplorer());
		mediaFileMarquee.setCursor(Cursor.HAND);
		mediaFileMarquee.setOnDragDetected(audioDragEvent);
		mediaNameHBox.getChildren().add(0, mediaFileMarquee);
		HBox.setHgrow(mediaFileMarquee, Priority.ALWAYS);
		
		// openMediaFileFolder
		mediaTagImageButton.setOnAction(action -> openAudioInExplorer());
		mediaTagImageButton.setOnDragDetected(audioDragEvent);
		
		// openFile
		openFile.setOnAction(action -> openFileChooser());
		
		// topInfoLabel
		topInfoLabel.setText("Player {" + this.getKey() + "}");
		
		//== backwardButton
		backwardButton.setOnAction(a -> seek(-Integer.parseInt(backwardButton.getText())));
		
		//== playPauseButton
		playPauseButton.setOnAction(fire -> {
			if (xPlayer.isPlaying())
				pause();
			else
				playOrReplay();
			
			//Fix fast the image
			( (ImageView) playPauseButton.getGraphic() ).setImage(xPlayer.isPlaying() ? XPlayerController.pauseImage : XPlayerController.playImage);
		});
		
		//== replayButton
		replayButton.setOnAction(a -> replay());
		
		//== stopButton
		stopButton.setOnAction(a -> stop());
		
		//== forwardButton
		forwardButton.setOnAction(a -> seek(Integer.parseInt(forwardButton.getText())));
		
		//flipPane
		flipPane.setFlipTime(150);
		flipPane.getFront().getChildren().addAll(basicGridPane);
		flipPane.getBack().getChildren().addAll(playerExtraSettings);
		
		settingsToggle.selectedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue) // true?
				flipPane.flipToBack();
			else
				flipPane.flipToFront();
		});
		borderPane.setCenter(flipPane);
		
		//RestorePlayerVBox
		restorePlayer.getParent().visibleProperty().bind(xPlayerWindow.getWindow().showingProperty());
		
		//restorePlayer
		restorePlayer.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.PRIMARY)
				xPlayerWindow.close();
		});
		
		//focusXPlayerWindow
		focusXPlayerWindow.setOnMouseReleased(m -> xPlayerWindow.getWindow().requestFocus());
		
		//extendPlayer
		extendPlayer.textProperty().bind(Bindings.when(xPlayerWindow.getWindow().showingProperty()).then("Restore").otherwise("Extend"));
		extendPlayer.setOnAction(ac -> {
			if (xPlayerWindow.getWindow().isShowing())
				xPlayerWindow.close();
			else
				xPlayerWindow.show();
		});
		
		//transferMedia
		transferMedia.getItems().get(key).setVisible(false);
		transferMedia.getItems().forEach(item -> item.setOnAction(a -> Optional.ofNullable(getxPlayerModel().songPathProperty().getValue()).ifPresent(path -> {
			
			//Start the selected player
			Main.xPlayersList.getXPlayerController(transferMedia.getItems().indexOf(item)).playSong(getxPlayerModel().songPathProperty().get(), getxPlayerModel().getCurrentTime());
			
			//Stop the Current Player
			stop();
			
		})));
		
		//=emotionsButton
		emotionsButton.disableProperty().bind(xPlayerModel.songPathProperty().isNull());
		emotionsButton.setOnAction(a -> updateEmotion(emotionsButton));
		
		//=showEmotionLists
		showEmotionLists.setOnAction(a -> Main.playListModesTabPane.selectTab(1));
		
	}
	
	/**
	 * This method is called to change the Emotion Image of the Media based on the current Emotion
	 * 
	 * @param emotion
	 */
	public void changeEmotionImage(Emotion emotion) {
		Main.emotionsWindow.giveEmotionImageToButton(emotionsButton, emotion);
	}
	
	/**
	 * Update the emotion the user is feeling for this Media
	 */
	public void updateEmotion(Node node) {
		
		// Show the Window
		Main.emotionsWindow.show(node);
		
		// Listener
		Main.emotionsWindow.getWindow().showingProperty().addListener(new InvalidationListener() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			public void invalidated(Observable o) {
				
				// Remove the listener
				Main.emotionsWindow.getWindow().showingProperty().removeListener(this);
				
				// !showing?
				if (!Main.emotionsWindow.getWindow().isShowing()) {
					
					//Add it the one of the emotions list
					new Thread(() -> Main.emotionListsController.makeEmotionDecisition(xPlayerModel.songPathProperty().get(), Main.emotionsWindow.getEmotion())).start();
					
					//System.out.println(Main.emotionsWindow.getEmotion());
					
				}
			}
		});
		
	}
	
	/**
	 * Opens the current Media File of the player to the default system explorer
	 */
	public void openAudioInExplorer() {
		if (xPlayerModel.songPathProperty().get() != null)
			ActionTool.openFileLocation(xPlayerModel.songPathProperty().get());
	}
	
	/**
	 * Opens a FileChooser so the user can select a song File
	 */
	public void openFileChooser() {
		File file = Main.specialChooser.selectSongFile(Main.window);
		if (file != null)
			playSong(file.getAbsolutePath());
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
	 * You can use this method to add or minus from the player volume For example you can call adjustVolume(+1) or adjustVolume(-1)
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
	
	public StackPane getRegionStackPane() {
		return regionStackPane;
	}
	
	public Label getFxLabel() {
		return fxLabel;
	}
	
	/**
	 * Used by resume method.
	 */
	private void resumeCode() {
		System.out.println("RESUME code....");
		
		// Stop the fade animation
		disc.stopFade();
		
		// image !=null?
		if (!playService.isDiscImageNull())
			disc.resumeRotation();
		
		// Start the visualizer
		visualizer.startVisualizer();
		
		// Pause Image
		// radialMenu.resumeOrPause.setGraphic(radialMenu.pauseImageView);
	}
	
	/**
	 * Used by pause method.
	 */
	private void pauseCode() {
		System.out.println("PAUSE code....");
		
		// Play the fade animation
		disc.playFade();
		
		// Pause the Rotation fo disc
		disc.pauseRotation();
		
		// Stop the Visualizer
		visualizer.stopVisualizer();
		
		// Play Image
		// radialMenu.resumeOrPause.setGraphic(radialMenu.playImageView);
	}
	
	/**
	 * Controls the volume of the player.
	 */
	public void controlVolume() {
		
		try {
			// if (key == 1 || key == 2) {
			// if (djMode.balancer.getVolume() < 100) { // <100
			//
			// Main.xPlayersList.getXPlayer(1).setGain(
			// ((Main.xPlayersList.getXPlayerUI(1).getVolume() / 100.00) *
			// (djMode.balancer.getVolume()))
			// / 100.00);
			// Main.xPlayersList.getXPlayer(2).setGain(Main.xPlayersList.getXPlayerUI(2).getVolume()
			// / 100.00);
			//
			// } else if (djMode.balancer.getVolume() == 100) { // ==100
			//
			// Main.xPlayersList.getXPlayer(1).setGain(Main.xPlayersList.getXPlayerUI(1).getVolume()
			// / 100.00);
			// Main.xPlayersList.getXPlayer(2).setGain(Main.xPlayersList.getXPlayerUI(2).getVolume()
			// / 100.00);
			//
			// } else if (djMode.balancer.getVolume() > 100) { // >100
			//
			// Main.xPlayersList.getXPlayer(1).setGain(Main.xPlayersList.getXPlayerUI(1).getVolume()
			// / 100.00);
			// Main.xPlayersList.getXPlayer(2).setGain(((Main.xPlayersList.getXPlayerUI(2).getVolume()
			// / 100.00)
			// * (200 - djMode.balancer.getVolume())) / 100.00);
			//
			// }
			// } else if (key == 0) {
			xPlayer.setGain((double) disc.getVolume() / 100.00);
			// }
			
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
		visualizer.setShowFPS(Main.settingsWindow.getxPlayersSettingsController().getShowFPS().selectedProperty().get());
		
		// Select the correct toggle
		visualizerWindow.getVisualizerTypeGroup().selectToggle(visualizerWindow.getVisualizerTypeGroup().getToggles().get(visualizer.displayMode.get()));
		
		// When displayMode is being updated
		visualizer.displayMode.addListener((observable , oldValue , newValue) -> {
			
			//Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + getKey() + "-Visualizer-DisplayMode", Integer.toString(newValue.intValue()));
			
			//----------
			visualizerWindow.getVisualizerTypeGroup().selectToggle(visualizerWindow.getVisualizerTypeGroup().getToggles().get(newValue.intValue()));
			visualizerStackController.replayLabelEffect( ( (RadioMenuItem) visualizerWindow.getVisualizerTypeGroup().getSelectedToggle() ).getText());
		});
		
		// -----------visualizerTypeGroup
		visualizerWindow.getVisualizerTypeGroup().getToggles()
				.forEach(toggle -> ( (RadioMenuItem) toggle ).setOnAction(a -> visualizer.displayMode.set(visualizerWindow.getVisualizerTypeGroup().getToggles().indexOf(toggle))));
		
		// VisualizerStackController
		visualizerStackController.getChildren().add(0, visualizer);
		visualizerStackController.visibleProperty().bind(visualizerVisible.selectedProperty());
		visualizerStackController.addListenersToButtons(this);
		
		// Add VisualizerStackController to the VisualizerStackPane
		visualizerStackPane.getChildren().add(0, visualizerStackController);
		
		// visualizerSettingsHBox
		visualizerSettingsHBox.visibleProperty().bind(visualizerWindow.getStage().showingProperty().not().and(visualizerStackPane.hoverProperty()));
		
		// visualizerSettings
		visualizerSettings.setOnMouseReleased(m -> {
			Bounds bounds = visualizerSettings.localToScreen(visualizerSettings.getBoundsInLocal());
			getVisualizerWindow().getVisualizerContextMenu().show(visualizerSettings, bounds.getMinX(), bounds.getMaxY());
		});
		
		// maximizeVisualizer
		maximizeVisualizer.disableProperty().bind(visualizerVisible.selectedProperty().not());
		maximizeVisualizer.setOnAction(e -> visualizerWindow.displayVisualizer());
		
		// visualizerVisible
		visualizerVisible.selectedProperty().addListener((observable , oldValue , newValue) -> visualizerVisible.setGraphic(newValue ? eye : eyeDisabled));
		
		// visualizerVisibleLabel
		visualizerVisibleLabel.visibleProperty().bind(visualizerVisible.selectedProperty().not());
		
		// visualizerMaximizedHBox
		visualizerMaximizedHBox.visibleProperty().bind(visualizerWindow.getStage().showingProperty());
		// visualizerMinimize
		visualizerMinimize.setOnMouseReleased(m -> visualizerWindow.removeVisualizer());
		// visualizerRequestFocus
		visualizerRequestFocus.setOnMouseReleased(m -> visualizerWindow.getStage().requestFocus());
		
		// playerStatusLabel
		playerStatusLabel.visibleProperty().bind(visualizer.getAnimationService().runningProperty().not());
		
		//Equalizer
		equalizer = new XPlayerEqualizer(this);
		equalizerTab.setContent(equalizer);
		
		//Pad
		xPlayerPad = new XPlayerPad(this);
		padTab.setContent(xPlayerPad);
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
	public void makeTheDisc(Color color , int volume , Side side) {
		
		// initialize
		disc = new DJDisc(136, 136, color, volume, 125);
		disc.addDJDiscListener(this);
		
		// radialMenu
		//disc.getChildren().add(radialMenu.getRadialMenuButton());
		
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
				// SecondaryMouseButton
			} else if (m.getButton() == MouseButton.SECONDARY)
				discIsDragging = false;
		});
		
		// Canvas Mouse Dragging
		disc.getCanvas().setOnMouseDragged(m -> {
			
			// MouseButton==Primary || Secondary
			if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.SECONDARY)
				
				// RadialMenu!showing and duration!=0 and duration!=-1
				if (!radialMenu.isHidden() && xPlayerModel.getDuration() != 0 && xPlayerModel.getDuration() != -1) {
					
					// System.out.println("Entered Dragging...");
					
					// Set the cursor
					disc.getCanvas().setCursor(Cursor.CLOSED_HAND);
					
					// Try to do the dragging
					discIsDragging = true;
					xPlayerModel.setCurrentAngleTime(disc.getValue(xPlayerModel.getDuration()));
					disc.calculateAngleByMouse(m, xPlayerModel.getCurrentAngleTime(), xPlayerModel.getDuration());
					
				}
		});
		
		//
		( (HBox) discBorderPane.getTop() ).getChildren().add(0, disc.getTimeField());
		HBox.setHgrow(disc.getTimeField(), Priority.ALWAYS);
		diskStackPane.getChildren().add(disc);
		diskStackPane.layoutBoundsProperty().addListener((observable , oldValue , newValue) -> reCalculateCanvasSize());
		reCalculateCanvasSize();
		
	}
	
	/**
	 * Recalculates the Canvas size to the preffered size
	 */
	private void reCalculateCanvasSize() {
		double size = Math.min(diskStackPane.getWidth(), diskStackPane.getHeight()) / 1.5;
		
		disc.resizeDisc(size, size);
		radialMenu.getRadialMenuButton().setPrefSize(disc.getMinWidth(), disc.getMinHeight());
		//System.out.println("Redrawing canvas");
	}
	
	/**
	 * When the audio starts , fast configure it's settings
	 * 
	 * @param ignoreStartImmediately
	 */
	public void configureMediaSettings(boolean ignoreStartImmediately) {
		
		// Start immediately?
		if (!ignoreStartImmediately && !Main.settingsWindow.getxPlayersSettingsController().getStartImmediately().isSelected())
			pause();
		else {
			play();
			resume();
		}
		
		// Mute?
		xPlayer.setMute(radialMenu.mute.isSelected());
		System.out.println("Mute is Selected? " + radialMenu.mute.isSelected());
		
		// Volume
		controlVolume();
		
		// Sets Pan value. Line should be opened before calling this method.
		// Linear scale : -1.0 <--> +1.0
		xPlayer.setPan(equalizer.getPanFilter().getValue(200));
		
		// Represents a control for the relative balance of a stereo signal
		// between two stereo speakers. The valid range of values is -1.0 (left
		// channel only) to 1.0 (right channel only). The default is 0.0
		// (centered).
		xPlayer.setBalance(equalizer.getBalanceFilter().getValue(200));
		
		// Audio is MP3?
		if (!"mp3".equals(xPlayerModel.songExtensionProperty().get()))
			equalizer.setDisable(true);
		else {
			xPlayer.setEqualizer(xPlayerModel.getEqualizerArray(), 32);
			equalizer.setDisable(false);
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void opened(Object dataSource , Map<String,Object> map) {
		// some code here
	}
	
	float progress;
	
	@Override
	public void progress(int nEncodedBytes , long microSecondsPosition , byte[] pcmdata , Map<String,Object> properties) {
		visualizer.writeDSP(pcmdata);
		
		if (!isDiscBeingDragged()) {
			
			// previousTime = xPlayerUI.xPlayer.currentTime
			
			// .MP3 OR .WAV
			String extension = xPlayerModel.songExtensionProperty().get();
			if ("mp3".equals(extension) || "wav".equals(extension)) {
				
				// Calculate the progress until now
				progress = ( nEncodedBytes > 0 && xPlayer.getTotalBytes() > 0 ) ? ( nEncodedBytes * 1.0f / xPlayer.getTotalBytes() * 1.0f ) : -1.0f;
				// System.out.println(progress*100+"%")
				if (visualizerWindow.isVisible())
					Platform.runLater(() -> visualizerWindow.getProgressBar().setProgress(progress));
				
				// find the current time in seconds
				xPlayerModel.setCurrentTime((int) ( xPlayerModel.getDuration() * progress ));
				// System.out.println((double) xPlayerModel.getDuration() *
				// progress)
				
				// .WHATEVER MUSIC FILE*
			} else
				xPlayerModel.setCurrentTime((int) ( microSecondsPosition / 1000000 ));
			
			String millisecondsFormatted = InfoTool.millisecondsToTime(microSecondsPosition / 1000);
			// System.out.println(milliFormat)
			
			// Paint the Disc
			if (!xPlayer.isStopped()) {
				
				// Update the disc Angle
				disc.calculateAngleByValue(xPlayerModel.getCurrentTime(), xPlayerModel.getDuration(), false);
				// Update the disc time
				disc.updateTimeDirectly(xPlayerModel.getCurrentTime(), xPlayerModel.getDuration(), millisecondsFormatted);
				
				//Update the below labels - this might be costly in terms of cpu
				Platform.runLater(() -> {
					
					//TotalTime and CurrentTime
					int totalTime = xPlayerModel.getDuration() , currentTime = xPlayerModel.getCurrentTime();
					
					//== RemainingTimeLabel
					remainingTimeLabel.setText(InfoTool.getTimeEdited(totalTime - currentTime) + "." + ( 9 - Integer.parseInt(millisecondsFormatted.replace(".", "")) ));
					
					//== ElapsedTimeLabel
					elapsedTimeLabel.setText(InfoTool.getTimeEdited(currentTime) + millisecondsFormatted + "");
					
				});
				
			}
			
			// if (!visualizer.isRunning())
			// Platform.runLater(this::resumeCode);
			
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
				//Marquee Text
				mediaFileMarquee.setText(InfoTool.getFileName(xPlayerModel.songPathProperty().get()));
				
				//Notification
				if (Main.settingsWindow.getxPlayersSettingsController().getShowPlayerNotifications().isSelected())
					ActionTool.showNotification("Player [ " + this.getKey() + " ] Opened", InfoTool.getFileName(xPlayerModel.songPathProperty().get()), Duration.seconds(4),
							NotificationType.SIMPLE, InfoTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60));
			});
			
			// Status.RESUMED			
		} else if (streamPlayerEvent.getPlayerStatus() == Status.RESUMED) {
			
			Platform.runLater(() -> {
				playerStatusLabel.setText("Player is Resumed ");
				resumeCode();
				
				//Notification
				//ActionTool.showNotification("Player [ " + this.getKey() + " ] Resuming", InfoTool.getFileName(xPlayerModel.songPathProperty().get()), Duration.seconds(2),
				//		NotificationType.SIMPLE, InfoTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60));
			});
			
			// Status.PLAYING
		} else if (streamPlayerEvent.getPlayerStatus() == Status.PLAYING) {
			
			Platform.runLater(this::resumeCode);
			
			// Status.PAUSED
		} else if (streamPlayerEvent.getPlayerStatus() == Status.PAUSED) {
			
			Platform.runLater(() -> {
				playerStatusLabel.setText("Player is Paused ");
				pauseCode();
				
				//Notification
				//	ActionTool.showNotification("Player [ " + this.getKey() + " ] Paused", InfoTool.getFileName(xPlayerModel.songPathProperty().get()), Duration.seconds(2),
				//			NotificationType.SIMPLE, InfoTool.getAudioAlbumImage(xPlayerModel.songPathProperty().get(), 60, 60));
			});
			
			// Status.STOPPED
		} else if (streamPlayerEvent.getPlayerStatus() == Status.STOPPED) {
			
			visualizer.stopDSP();
			
			Platform.runLater(() -> {
				
				// SeekService running?
				if (seekService.isRunning()) {
					
					// oh yeah
					
				} else {
					
					// Set time to 0 to not have problems with SeekService
					xPlayerModel.setCurrentTime(0);
					
					// Change Marquee text
					//mediaFileMarquee.setText("Player is Stopped");
					playerStatusLabel.setText("Player is Stopped");
					
					disc.calculateAngleByValue(0, 0, true);
					disc.repaint();
					
					// disk
					disc.stopRotation();
					disc.stopFade();
					
					// Visualizer
					visualizer.stopVisualizer();
					
					// Play Image
					// radialMenu.resumeOrPause.setGraphic(radialMenu.playImageView);
					
					//== RemainingTimeLabel
					remainingTimeLabel.setText("00:00");
					
					//== ElapsedTimeLabel
					elapsedTimeLabel.setText("00:00");
					
					// Notification
					//ActionTool.showNotification("Player " + this.getKey(), "Player[ " + this.getKey() + " ] has stopped...", Duration.millis(500), NotificationType.SIMPLE);
					
				}
				
			});
			
			// Status.SEEKING
		} else if (streamPlayerEvent.getPlayerStatus() == Status.SEEKING) {
			
			Platform.runLater(() -> playerStatusLabel.setText("Player is Seeking "));
			
			// Status.SEEKED
		} else if (streamPlayerEvent.getPlayerStatus() == Status.SEEKED) {
			//TODO i need to add code here
		}
	}
	
	@Override
	public void volumeChanged(int volume) {
		controlVolume();
	}
	
	/**
	 * Replay the current song
	 */
	public void replay() {
		
		if (xPlayerModel.songExtensionProperty().get() != null)
			playService.startPlayService(xPlayerModel.songPathProperty().get(), 0);
		else
			ActionTool.showNotification("No Previous File", "Drag and Drop or Add a File or URL on this player.", Duration.millis(1500), NotificationType.INFORMATION);
		
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
		
		playService.startPlayService(absolutePath, 0);
		
	}
	
	/**
	 * Play the current song.
	 *
	 * @param absolutePath
	 *            The absolute path of the file
	 * @param startingSecond
	 *            From which second to start the audio , this will not be exactly accurate
	 */
	public void playSong(String absolutePath , int startingSecond) {
		
		playService.startPlayService(absolutePath, startingSecond);
		
	}
	
	//---------------------------------------------------Player Actions------------------------------------------------------------------
	
	/**
	 * Tries to skip forward or backward
	 * 
	 * @param seconds
	 *            Seconds to seek
	 */
	public void seek(int seconds) {
		boolean ok = false;
		if (seconds == 0)
			return;
		
		//
		
		if (seconds < 0 && ( seconds + xPlayerModel.getCurrentTime() >= 0 )) { //negative seek
			
			System.out.println("Skipping backwards ...[" + seconds + "] seconds");
			
			ok = true;
		} else if (seconds > 0 && ( seconds + xPlayerModel.getCurrentTime() <= xPlayerModel.getDuration() )) { //positive seek
			
			System.out.println("Skipping forward ...[" + seconds + "] seconds");
			
			ok = true;
		}
		
		//Ok/?
		if (ok) {
			
			// Add or Remove
			xPlayerModel.setCurrentAngleTime(xPlayerModel.getCurrentTime() + seconds);
			
			//	    //Seek
			//	    System.out.println("Original: "
			//		    + (xPlayerModel.getCurrentAngleTime()) * (xPlayer.getTotalBytes() / xPlayerModel.getDuration())
			//		    + " With double:" + (long) (((float) xPlayerModel.getCurrentAngleTime())
			//			    * (xPlayer.getTotalBytes() / (float) xPlayerModel.getDuration())))
			
			//Start the Service
			seekService.startSeekService((long) ( ( (float) xPlayerModel.getCurrentAngleTime() ) * ( xPlayer.getTotalBytes() / (float) xPlayerModel.getDuration() ) ), false);
		}
		
	}
	
	/**
	 * This method is used to seek to a specific time of the audio
	 * 
	 * @param seconds
	 */
	public void seekTo(int seconds) {
		
		if (seconds < 0 || seconds >= xPlayerModel.getDuration())
			return;
		
		// Set
		xPlayerModel.setCurrentAngleTime(seconds);
		
		//Seek To
		seekService.startSeekService( ( xPlayerModel.getCurrentAngleTime() ) * ( xPlayer.getTotalBytes() / xPlayerModel.getDuration() ), true);
		
	}
	
	/**
	 * Mute the player.
	 */
	public void mute() {
		radialMenu.mute.setSelected(true);
		xPlayer.setMute(true);
	}
	
	/**
	 * UnMute the player.
	 */
	public void unMute() {
		radialMenu.mute.setSelected(false);
		xPlayer.setMute(false);
	}
	
	/**
	 * Starts the player
	 */
	public void play() {
		try {
			xPlayer.play();
		} catch (StreamPlayerException ex) {
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
	 * If the player is paused it resume it else if it is stopped it replays the Media
	 */
	public void playOrReplay() {
		if (xPlayer.isPaused()) // paused?
			resume();
		else if (xPlayer.isStopped() || xPlayer.isUnknown())
			replay();
	}
	
	/**
	 * @return the xPlayerWindow
	 */
	public XPlayerWindow getxPlayerWindow() {
		return xPlayerWindow;
	}
	
	/**
	 * @return the playerExtraSettings
	 */
	public XPlayerExtraSettings getPlayerExtraSettings() {
		return playerExtraSettings;
	}
	
	/**
	 * @return the xPlayerModel
	 */
	public XPlayerModel getxPlayerModel() {
		return xPlayerModel;
	}
	
	/**
	 * @return the xPlayer
	 */
	public XPlayer getxPlayer() {
		return xPlayer;
	}
	
	/**
	 * @return the radialMenu
	 */
	public XPlayerRadialMenu getRadialMenu() {
		return radialMenu;
	}
	
	/**
	 * @return the visualizerWindow
	 */
	public VisualizerWindowController getVisualizerWindow() {
		return visualizerWindow;
	}
	
	/**
	 * @return the visualizerStackController
	 */
	public VisualizerStackController getVisualizerStackController() {
		return visualizerStackController;
	}
	
	/**
	 * @return the visualizer
	 */
	public XPlayerVisualizer getVisualizer() {
		return visualizer;
	}
	
	/**
	 * @return the equalizer
	 */
	public XPlayerEqualizer getEqualizer() {
		return equalizer;
	}
	
	/**
	 * @return the disc
	 */
	public DJDisc getDisc() {
		return disc;
	}
	
	/**
	 * @return the xPlayerPlayList
	 */
	public XPlayerPlaylist getxPlayerPlayList() {
		return xPlayerPlayList;
	}
	
	/**
	 * @return the mediaTagImageButton
	 */
	public Button getMediaTagImageButton() {
		return mediaTagImageButton;
	}
	
	/**
	 * @return the backwardButton
	 */
	public Button getBackwardButton() {
		return backwardButton;
	}
	
	/**
	 * @return the forwardButton
	 */
	public Button getForwardButton() {
		return forwardButton;
	}
	
	/**
	 * @return the totalTimeLabel
	 */
	public Label getTotalTimeLabel() {
		return totalTimeLabel;
	}
	
	/**
	 * @param totalTimeLabel
	 *            the totalTimeLabel to set
	 */
	public void setTotalTimeLabel(Label totalTimeLabel) {
		this.totalTimeLabel = totalTimeLabel;
	}
	
	/**
	 * @return the playPauseButton
	 */
	public Button getPlayPauseButton() {
		return playPauseButton;
	}
	
	/**
	 * @return the emotionsButton
	 */
	public Button getEmotionsButton() {
		return emotionsButton;
	}
	
	/**
	 * @return the playService
	 */
	public XPlayerPlayService getPlayService() {
		return playService;
	}
	
}
