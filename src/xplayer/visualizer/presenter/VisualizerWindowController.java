/*
 * 
 */
package xplayer.visualizer.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.borderless.BorderlessScene;
import application.tools.InfoTool;
import application.tools.JavaFXTools;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
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
import xplayer.presenter.XPlayerController;
import xplayer.visualizer.VisualizerDrawer;

/**
 * The Class VisualizerWindow.
 *
 * @author GOXR3PLUS
 */
public class VisualizerWindowController extends StackPane {
	
	//------------------
	
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
	private MenuItem setDefaultBackground;
	
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
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
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
		window.widthProperty().addListener((observable , oldValue , newValue) -> {
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
			xPlayerController.getVisualizer().setCursor(Cursor.HAND);
		});
		
		// -- KeyListeners
		scene.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE) {
				if (!window.isFullScreen())
					removeVisualizer();
				else
					window.setFullScreen(false);
				
			}
		});
		
		// ----------Drag && Drop Listeners
		scene.setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
		scene.setOnDragDropped(xPlayerController.audioDropEvent);
		window.setScene(scene);
		
		// -------------Top Bar Elements---------------
		
		// menuPopButton
		menuPopButton.textProperty()
				.bind(Bindings.max(0, progressBar.progressProperty()).multiply(100.00).asString("[%.02f %%]").concat("Deck [" + xPlayerController.getKey() + "]"));
		menuPopButton.setOnMouseReleased(a -> {
			Bounds bounds = menuPopButton.localToScreen(menuPopButton.getBoundsInLocal());
			visualizerContextMenu.show(menuPopButton, bounds.getMaxX(), bounds.getMinY());
		});
		
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
				xPlayerController.getVisualizer().setCursor(Cursor.NONE);
			}
		});
		
		//--------------------------
		
		// setBackground
		setBackground.setOnAction(a -> setNewImage(Type.BACKGROUND));
		
		//setDefaultBackground
		setDefaultBackground.setOnAction(a -> resetToDefaultImage(Type.BACKGROUND));
		
		// clearBackground
		clearBackground.setOnAction(a -> clearImage(Type.BACKGROUND));
		
		// setForeground
		setForeground.setOnAction(a -> setNewImage(Type.FOREGROUND));
		
		//setDefaultForeground
		setDefaultForeground.setOnAction(a -> resetToDefaultImage(Type.FOREGROUND));
		
	}
	
	/**
	 * The Enum Type.
	 */
	public enum Type {
		
		/** The background. */
		BACKGROUND {
			@Override
			public String toString() {
				return "Background";
			}
		},
		/** The foreground. */
		FOREGROUND {
			@Override
			public String toString() {
				return "Foreground";
			}
		};
	}
	
	/**
	 * Replaces the background image of visualizer.
	 *
	 * @param type
	 *            the type
	 */
	public void setNewImage(Type type) {
		
		//Check the response
		JavaFXTools.selectAndSaveImage("XPlayer" + this.xPlayerController.getKey() + type, InfoTool.getXPlayersImageFolderAbsolutePathPlain(), Main.specialChooser, window)
				.ifPresent(imageFile -> {
					if (type == Type.BACKGROUND)
						xPlayerController.getVisualizer().backgroundImage = new Image(imageFile.toURI() + "");
					else if (type == Type.FOREGROUND)
						xPlayerController.getVisualizer().foregroundImage = new Image(imageFile.toURI() + "");
					
					//Manage Settings
					Main.dbManager.getPropertiesDb().deleteProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared");
					
				});
		
	}
	
	/**
	 * Resets the default background or foreground Image
	 *
	 * @param type
	 *            the type
	 */
	public void resetToDefaultImage(Type type) {
		
		//Delete the background image
		JavaFXTools.deleteAnyImageWithTitle("XPlayer" + this.xPlayerController.getKey() + type, InfoTool.getXPlayersImageFolderAbsolutePathPlain());
		
		//Manage Settings
		Main.dbManager.getPropertiesDb().deleteProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared");
		
		//Reset to default image
		findAppropriateImage(type);
	}
	
	/**
	 * Find the appropriate background or foreground Image , based on if any Images have been ever selected from the User
	 *
	 * @param type
	 *            the type
	 */
	public void findAppropriateImage(Type type) {
		
		//Check if it returns null
		Image image = JavaFXTools.findAnyImageWithTitle("XPlayer" + this.xPlayerController.getKey() + type, InfoTool.getXPlayersImageFolderAbsolutePathPlain());
		
		//System.out.println("image is null?" + type + " .... " + ( image == null ))
		
		//Replace the Image
		if (type == Type.BACKGROUND)
			xPlayerController.getVisualizer().backgroundImage = ( image != null ? image : VisualizerDrawer.DEFAULT_BACKGROUND_IMAGE );
		else if (type == Type.FOREGROUND)
			xPlayerController.getVisualizer().foregroundImage = ( image != null ? image : VisualizerDrawer.DEFAULT_FOREGROUND_IMAGE );
	}
	
	/**
	 * Resets the default background or foreground Image
	 *
	 * @param type
	 *            the type
	 */
	public void clearImage(Type type) {
		
		//Delete the background image
		JavaFXTools.deleteAnyImageWithTitle("XPlayer" + this.xPlayerController.getKey() + type, InfoTool.getXPlayersImageFolderAbsolutePathPlain());
		
		//Set the Image to null
		if (type == Type.BACKGROUND)
			xPlayerController.getVisualizer().backgroundImage = null;
		else if (type == Type.FOREGROUND)
			xPlayerController.getVisualizer().foregroundImage = null;
		
		//Manage Settings
		Main.dbManager.getPropertiesDb().updateProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared", "true");
		
	}
	
	//Manage Settings
	
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
		centerStackPane.getChildren().add(1, xPlayerController.getVisualizerStackController());
		
		// show the window
		window.show();
	}
	
	/**
	 * Removes the visualizer from the Window.
	 */
	public void removeVisualizer() {
		pauseTransition.stop();
		xPlayerController.getVisualizer().setCursor(Cursor.HAND);
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
