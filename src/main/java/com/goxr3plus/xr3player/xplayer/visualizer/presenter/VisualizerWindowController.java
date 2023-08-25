/*
 * 
 */
package com.goxr3plus.xr3player.xplayer.visualizer.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerDrawer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
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

/**
 * The Class VisualizerWindow.
 *
 * @author GOXR3PLUS
 */
public class VisualizerWindowController extends StackPane {

	// ------------------

	@FXML
	private BorderPane visualizerPane;

	@FXML
	private StackPane centerStackPane;

	@FXML
	private MediaView mediaView;

	@FXML
	private BorderPane topBar;

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

	@FXML
	private JFXCheckBox keepTopBarVisible;

	@FXML
	private JFXButton maxOrNormalize;

	@FXML
	private StackedFontIcon sizeStackedFontIcon;

	@FXML
	private JFXButton close;

	@FXML
	private Label visualizerLabel;

	@FXML
	private FontIcon visualizerLabelFontIcon;

	@FXML
	private Label progressLabel;

	@FXML
	private ProgressBar progressBar;

	// ------------------------------------

	private BorderlessScene borderlessScene;

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
	 * @param xPlayerController xPlayerController
	 */
	public VisualizerWindowController(XPlayerController xPlayerController) {

		this.xPlayerController = xPlayerController;

		window = new Stage();
		window.setTitle("XR3Player Visualizer");
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		window.setWidth(JavaFXTool.getScreenHeight() / 2);
		window.setHeight(JavaFXTool.getScreenHeight() / 2);
		window.centerOnScreen();
		window.setFullScreenExitHint("");
		window.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		window.setOnCloseRequest(c -> removeVisualizer());

		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.VISUALIZERS_FXMLS + "VisualizerWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "VisualizerWindowController FXML can't be loaded!",
					ex);
		}

	}

	// public MediaPlayer videoPlayer;

	/**
	 * Called as soon as .fxml has been loaded
	 */
	@FXML
	private void initialize() {

		// -- Scene
		borderlessScene = new BorderlessScene(window, StageStyle.TRANSPARENT, this, 150, 150);
		borderlessScene.setMoveControl(topBar);
		borderlessScene.setFill(Color.rgb(0, 0, 0, transparencySlider.getValue()));
		borderlessScene.getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());

		// width listener
		window.widthProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() <= 250 && visualizerLabel.isVisible()) {
				visualizerLabel.setVisible(false);
				visualizerLabel.setManaged(false);
			} else if (newValue.intValue() > 250 && !visualizerLabel.isVisible()) {
				visualizerLabel.setVisible(true);
				visualizerLabel.setManaged(true);
			}
		});

		// --- MouseListeners
		addEventHandler(MouseEvent.MOUSE_MOVED, m -> restartPauseTransition());

		// -- KeyListeners
		borderlessScene.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE) {
				if (!window.isFullScreen())
					removeVisualizer();
				else
					window.setFullScreen(false);

			}
		});

		// ----------Drag && Drop Listeners
		borderlessScene.setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
		borderlessScene.setOnDragDropped(xPlayerController.audioDropEvent);
		window.setScene(borderlessScene);

		// -------------Top Bar Elements---------------

		// visualizerLabel
		visualizerLabel.setText("Visualizer");
		visualizerLabelFontIcon.setIconLiteral("gmi-filter-" + (xPlayerController.getKey() + 1));
		window.setTitle("Visualizer " + (xPlayerController.getKey() + 1));

		// progressLabel
		progressLabel.textProperty()
				.bind(Bindings.max(0, progressBar.progressProperty()).multiply(100.00).asString("%.02f %%"));

		// menuPopButton
		menuPopButton.setOnMouseReleased(a -> {
			Bounds bounds = menuPopButton.localToScreen(menuPopButton.getBoundsInLocal());
			visualizerContextMenu.show(menuPopButton, bounds.getMaxX(), bounds.getMaxY());
		});

		// ----------------------------- Minimize
		maxOrNormalize.setOnAction(a -> borderlessScene.maximizeStage());
		close.setOnAction(action -> removeVisualizer());

		// stage
		borderlessScene.maximizedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				sizeStackedFontIcon.getChildren().get(0).setVisible(true);
				sizeStackedFontIcon.getChildren().get(1).setVisible(false);
			} else {
				sizeStackedFontIcon.getChildren().get(1).setVisible(true);
				sizeStackedFontIcon.getChildren().get(0).setVisible(false);
			}
		});

		// transparencySlider
		// transparencySlider.disableProperty().bind(window.showingProperty().not());
		transparencySlider.valueProperty()
				.addListener(list -> borderlessScene.setFill(Color.rgb(0, 0, 0, transparencySlider.getValue())));

		// PauseTransition
		pauseTransition.setOnFinished(f -> {
			if (!topBar.isHover() && window.isShowing() && !visualizerContextMenu.isShowing()
					&& !keepTopBarVisible.isSelected()) {
				topBar.setVisible(false);
				setCursor(Cursor.NONE);
				xPlayerController.visualizer.setCursor(Cursor.NONE);
			}
			// System.out.println("PauseTransition Finished")
		});

		// --------------------------

		// setBackground
		setBackground.setOnAction(a -> setNewImage(Type.BACKGROUND));

		// setDefaultBackground
		setDefaultBackground.setOnAction(a -> resetToDefaultImage(Type.BACKGROUND));

		// clearBackground
		clearBackground.setOnAction(a -> clearImage(Type.BACKGROUND));

		// setForeground
		setForeground.setOnAction(a -> setNewImage(Type.FOREGROUND));

		// setDefaultForeground
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
		}
    }

	/**
	 * Replaces the background image of visualizer.
	 *
	 * @param type the type
	 */
	public void setNewImage(Type type) {

		// Check the response
		JavaFXTool
				.selectAndSaveImage("XPlayer" + this.xPlayerController.getKey() + type,
						DatabaseTool.getXPlayersImageFolderAbsolutePathPlain(), Main.specialChooser, window)
				.ifPresent(imageFile -> {
					if (type == Type.BACKGROUND)
						xPlayerController.visualizer.backgroundImage = new Image(imageFile.toURI() + "");
					else if (type == Type.FOREGROUND)
						xPlayerController.visualizer.foregroundImage = new Image(imageFile.toURI() + "");

					// Manage Settings
					Main.dbManager.getPropertiesDb().deleteProperty(
							"XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared");

				});

	}

	/**
	 * Resets the default background or foreground Image
	 *
	 * @param type the type
	 */
	public void resetToDefaultImage(Type type) {

		// Delete the background image
		JavaFXTool.deleteAnyImageWithTitle("XPlayer" + this.xPlayerController.getKey() + type,
				DatabaseTool.getXPlayersImageFolderAbsolutePathPlain());

		// Manage Settings
		Main.dbManager.getPropertiesDb()
				.deleteProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared");

		// Reset to default image
		findAppropriateImage(type);
	}

	/**
	 * Find the appropriate background or foreground Image , based on if any Images
	 * have been ever selected from the User
	 *
	 * @param type the type
	 */
	public void findAppropriateImage(Type type) {

		// Check if it returns null
		Image image = JavaFXTool.findAnyImageWithTitle("XPlayer" + this.xPlayerController.getKey() + type,
				DatabaseTool.getXPlayersImageFolderAbsolutePathPlain());

		// System.out.println("image is null?" + type + " .... " + ( image == null ))

		// Replace the Image
		if (type == Type.BACKGROUND)
			xPlayerController.visualizer.backgroundImage = (image != null ? image
					: VisualizerDrawer.DEFAULT_BACKGROUND_IMAGE);
		else if (type == Type.FOREGROUND)
			xPlayerController.visualizer.foregroundImage = (image != null ? image
					: VisualizerDrawer.DEFAULT_FOREGROUND_IMAGE);
	}

	/**
	 * Resets the default background or foreground Image
	 *
	 * @param type the type
	 */
	public void clearImage(Type type) {

		// Delete the background image
		JavaFXTool.deleteAnyImageWithTitle("XPlayer" + this.xPlayerController.getKey() + type,
				DatabaseTool.getXPlayersImageFolderAbsolutePathPlain());

		// Set the Image to null
		if (type == Type.BACKGROUND)
			xPlayerController.visualizer.backgroundImage = null;
		else if (type == Type.FOREGROUND)
			xPlayerController.visualizer.foregroundImage = null;

		// Manage Settings
		Main.dbManager.getPropertiesDb()
				.updateProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared", "true");

	}

	// Manage Settings

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

		// Restart the PauseTransition
		restartPauseTransition();
	}

	/**
	 * Restarts the PauseTransition
	 */
	private void restartPauseTransition() {
		pauseTransition.playFromStart();
		topBar.setVisible(true);
		setCursor(Cursor.HAND);
		xPlayerController.visualizer.setCursor(Cursor.HAND);
		// System.out.println("PauseTransition Restarted")
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
