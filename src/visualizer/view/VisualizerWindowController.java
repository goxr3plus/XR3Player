/*
 * 
 */
package visualizer.view;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import borderless.BorderlessScene;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import visualizer.model.VisualizerDrawer;
import xplayer.presenter.XPlayerController;

/**
 * The Class VisualizerWindow.
 *
 * @author GOXR3PLUS
 */
public class VisualizerWindowController extends StackPane {

    @FXML
    private BorderPane visualizerPane;

    @FXML
    private StackPane centerStackPane;

    @FXML
    private MediaView mediaView;

    @FXML
    private BorderPane topBar;

    @FXML
    private Button minimize;

    @FXML
    private StackPane progressBarStackPane;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private MenuButton menuPopButton;

    @FXML
    private ContextMenu visualizerContextMenu;

    @FXML
    private Menu spectrumMenu;

    @FXML
    private ToggleGroup visualizerTypeGroup;

    @FXML
    private MenuItem setBackground;

    @FXML
    private MenuItem clearBackground;

    @FXML
    private MenuItem setForeground;

    @FXML
    private MenuItem setDefaultForeground;

    @FXML
    private Slider transparencySlider;

    // ------------------------------------

    /** The window. */
    private Stage window;

    /** The x player UI. */
    // Controller of an XPlayer
    private XPlayerController xPlayerController;

    /** The pause transition. */
    private PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));

    /**
     * Constructor.
     *
     * @param xPlayerController
     *            xPlayerController
     */
    public VisualizerWindowController(XPlayerController xPlayerController) {

	this.xPlayerController = xPlayerController;

	window = new Stage();
	window.setTitle("XR3Player Visualizer");
	window.getIcons().add(InfoTool.getImageFromDocuments("icon.png"));
	window.setWidth(InfoTool.getScreenHeight() / 2);
	window.setHeight(InfoTool.getScreenHeight() / 2);
	window.centerOnScreen();
	window.setFullScreenExitHint("");
	window.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
	window.setOnCloseRequest(c -> removeVisualizer());

	// FXMLLOADER
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "VisualizerWindowController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "VisualizerWindowController FXML can't be loaded!", ex);
	}

    }

    //public MediaPlayer videoPlayer;

    /**
     * Called as soon as .fxml has been loaded
     */
    @FXML
    private void initialize() {

	// -- Scene
	BorderlessScene scene = new BorderlessScene(window, StageStyle.TRANSPARENT, this, 150, 150);
	scene.setMoveControl(topBar);
	scene.setFill(Color.rgb(0, 0, 0, transparencySlider.getValue()));
	scene.getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());

	// ---Size Listeners

	// width-height Listeners
	window.widthProperty().addListener((observable, oldValue, newValue) -> {
	    if (newValue.intValue() <= 200 && progressBarStackPane.isVisible()) {
		progressBarStackPane.setVisible(false);
		progressBarStackPane.setManaged(false);
	    } else if (newValue.intValue() > 200 && !progressBarStackPane.isVisible()) {
		progressBarStackPane.setVisible(true);
		progressBarStackPane.setManaged(true);
	    }
	});

	// --- MouseListeners
	addEventHandler(MouseEvent.MOUSE_MOVED, m -> {
	    pauseTransition.playFromStart();
	    topBar.setVisible(true);
	    setCursor(Cursor.HAND);
	    xPlayerController.visualizer.setCursor(Cursor.HAND);
	});

	// --- Mouse Scroll Listeners
	setOnScroll(scroll -> {
	    if (scroll.getDeltaY() > 0)
		xPlayerController.adjustVolume(1);
	    else
		xPlayerController.adjustVolume(-1);

	    xPlayerController.visualizerStackController.replayLabelEffect("Vol: " + xPlayerController.getVolume());
	});

	// -- KeyListeners
	scene.setOnKeyReleased(key -> {
	    if (key.getCode() == KeyCode.ESCAPE) {
		if (window.isFullScreen())
		    window.setFullScreen(false);
		else
		    removeVisualizer();

	    } else if (key.getCode() == KeyCode.RIGHT)
		xPlayerController.visualizerStackController.nextSpectrumAnalyzer();
	    else if (key.getCode() == KeyCode.LEFT)
		xPlayerController.visualizerStackController.previousSpectrumAnalyzer();
	});

	// ----------Drag && Drop Listeners
	scene.setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
	scene.setOnDragDropped(drop -> xPlayerController.dragDrop(drop, 2));
	window.setScene(scene);

	// -------------Top Bar Elements---------------

	// menuPopButton
	menuPopButton.textProperty().bind(Bindings.max(0, progressBar.progressProperty()).multiply(100.00).asString("[%.02f %%]")
		.concat("Deck [" + xPlayerController.getKey() + "]"));

	// ----------------------------- Minimize
	minimize.setOnAction(action ->

	removeVisualizer());

	// transparencySlider
	transparencySlider.valueProperty().addListener(list -> scene.setFill(Color.rgb(0, 0, 0, transparencySlider.getValue())));

	// PauseTransition
	pauseTransition.setOnFinished(f -> {
	    if (!topBar.isHover() && window.isShowing() && !menuPopButton.isShowing()) {
		topBar.setVisible(false);
		setCursor(Cursor.NONE);
		xPlayerController.visualizer.setCursor(Cursor.NONE);
	    }
	});

	// /** The media. */
	// Media media = new Media(new
	// File("C:\\\\Users\\\\GOXR3PLUS\\\\Desktop\\\\Twerking
	// Dog.mp4").toURI().toString());
	//
	// /** The video player. */
	// videoPlayer = new MediaPlayer(media);
	//
	// mediaView.setMediaPlayer(videoPlayer);
	// mediaView.fitHeightProperty().bind(super.widthProperty());
	// mediaView.fitHeightProperty().bind(super.heightProperty());
	// mediaView.setSmooth(true);
	//
	// if (xPlayerUI.getKey() == 0) {
	// videoPlayer.setRate(0.5);
	// videoPlayer.setCycleCount(50000);
	// videoPlayer.setMute(true);
	// videoPlayer.setAutoPlay(true);
	// }

	//--------------------------

	// setBackground
	setBackground.setOnAction(a -> changeImage(Type.background));

	// clearBackground
	clearBackground.setOnAction(a -> xPlayerController.visualizer.backgroundImage = null);

	// setForeground
	setForeground.setOnAction(a -> changeImage(Type.foreground));

	//setDefaultForeground
	setDefaultForeground.setOnAction(a -> xPlayerController.visualizer.foregroundImage = VisualizerDrawer.DEFAULT_FOREGROUND_IMAGE);

    }

    /**
     * The Enum Type.
     */
    public enum Type {

	/** The background. */
	background,
	/** The foreground. */
	foreground;
    }

    /**
     * Replaces the background image of visualizer.
     *
     * @param type
     *            the type
     */
    public void changeImage(Type type) {
	File image = Main.specialChooser.prepareToSelectImage(window);
	if (image != null) {
	    Image img = new Image(image.toURI().toString());
	    if (img.getWidth() <= 4800 && img.getHeight() <= 4800) {
		if (type == Type.background)
		    xPlayerController.visualizer.backgroundImage = img;
		else if (type == Type.foreground)
		    xPlayerController.visualizer.foregroundImage = img;
	    } else
		ActionTool.showNotification("Warning", "Maximum Size Allowed 4800*4800 \n Current is:" + img.getWidth() + "*" + img.getHeight(),
			Duration.millis(1500), NotificationType.WARNING);

	}
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
     * 							GETTERS
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
     * Gets the stage.
     *
     * @return The Actual Stage of the Window
     */
    public Stage getStage() {
	return window;
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
     * 							Methods
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
     * Adds a visualizer to the Window.
     */
    public void displayVisualizer() {

	// Add the visualizer
	centerStackPane.getChildren().add(1, xPlayerController.visualizerStackController);

	// show the window
	window.show();
    }

    /**
     * Removes the visualizer from the Window.
     */
    public void removeVisualizer() {
	pauseTransition.stop();
	xPlayerController.visualizer.setCursor(Cursor.HAND);
	xPlayerController.reAddVisualizer();
	window.close();
    }

    /**
     * @return the visualizerContextMenu
     */
    public ContextMenu getVisualizerContextMenu() {
	return visualizerContextMenu;
    }

    /**
     * @return the visualizerTypeGroup
     */
    public ToggleGroup getVisualizerTypeGroup() {
	return visualizerTypeGroup;
    }

    /**
     * @return the progressBar
     */
    public ProgressBar getProgressBar() {
	return progressBar;
    }

    // -----------------Rubbish code.......------------------------------

    // topBar.setOnMousePressed(m -> {
    // if (window.getWidth() < InfoTool.screenWidth && m.getButton() ==
    // MouseButton.PRIMARY) {
    // topBar.setCursor(Cursor.MOVE);
    // initialX = (int) ( window.getX() - m.getScreenX() );
    // initialY = (int) ( window.getY() - m.getScreenY() );
    // }
    // });
    //
    // topBar.setOnMouseDragged(m -> {
    // if (window.getWidth() < InfoTool.screenWidth && m.getButton() ==
    // MouseButton.PRIMARY) {
    // window.setX(m.getScreenX() + initialX);
    // window.setY(m.getScreenY() + initialY);
    // }
    //
    // });
    //
    // topBar.setOnMouseReleased(m -> topBar.setCursor(Cursor.DEFAULT));

}
