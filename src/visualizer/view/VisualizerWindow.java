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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import xplayer.presenter.XPlayerController;

/**
 * The Class VisualizerWindow.
 *
 * @author GOXR3PLUS
 */
public class VisualizerWindow extends StackPane {
	
	/** The visualizer pane. */
	@FXML
	private BorderPane visualizerPane;
	
	/** The top bar. */
	@FXML
	private BorderPane topBar;
	
	/** The label. */
	@FXML
	private Label label;
	
	/** The transparency slider. */
	@FXML
	private Slider transparencySlider;
	
	/** The minimize. */
	@FXML
	private Button minimize;
	
	/** The set background. */
	@FXML
	private MenuItem setBackground;
	
	/** The clear background. */
	@FXML
	private MenuItem clearBackground;
	
	/** The set foreground. */
	@FXML
	private MenuItem setForeground;
	
	/**
	 * Shows the progress of the Media
	 * how much has completed from it's total duration
	 */
	@FXML
	public ProgressBar progressBar;
	
	@FXML
	private Label progressLabel;
	
	@FXML
	private ToggleButton onTop;
	
	// ------------------------------------
	
	/** The window. */
	private Stage window;
	
	/** The x player UI. */
	// Controller of an XPlayer
	private XPlayerController xPlayerUI;
	
	/** The pause transition. */
	private PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
	
	/**
	 * Constructor.
	 *
	 * @param xPlayerUI the x player UI
	 */
	public VisualizerWindow(XPlayerController xPlayerUI) {
		
		this.xPlayerUI = xPlayerUI;
		
		window = new Stage();
		window.setTitle("XR3Player Visualizer");
		window.getIcons().add(InfoTool.getImageFromDocuments("icon.png"));
		window.setWidth(InfoTool.getScreenWidth() / 2);
		window.setHeight(InfoTool.getScreenHeight() / 2);
		window.centerOnScreen();
		window.setFullScreenExitHint("");
		window.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		window.setOnCloseRequest(c -> removeVisualizer());
		
		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "VisualizerStage.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "VisualizerWindow FXML can't be loaded!", ex);
		}
		
	}
	
	/**
	 * Called as soon as .fxml has been loaded
	 */
	@FXML
	private void initialize() {
		
		// Set the scene
		BorderlessScene scene = new BorderlessScene(window, StageStyle.TRANSPARENT, this, 150, 150);
		scene.setMoveControl(topBar);
		scene.setFill(Color.rgb(0, 0, 0, transparencySlider.getValue()));
		scene.getStylesheets().add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());
		
		// ---------------------Scene Listeners
		scene.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				if (window.isFullScreen())
					window.setFullScreen(false);
				else
					removeVisualizer();
		});
		// width-height Listeners
		//window.widthProperty()
		//        .addListener(l -> xPlayerUI.visualizer.resizeVisualizer(window.getWidth(), window.getHeight()));
		
		//window.heightProperty()
		//        .addListener(l -> xPlayerUI.visualizer.resizeVisualizer(window.getWidth(), window.getHeight()));
		
		// Drag && Drop Listeners
		scene.setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
		scene.setOnDragDropped(drop -> xPlayerUI.dragDrop(drop, 2));
		window.setScene(scene);
		
		// ------progressLabel
		progressLabel.textProperty()
		        .bind(Bindings.max(0, progressBar.progressProperty()).multiply(100.00).asString("%.02f %%"));
		
		// ----------------------------- Minimize
		minimize.setOnAction(action -> removeVisualizer());
		
		// clearBackground
		clearBackground.setOnAction(a -> xPlayerUI.visualizer.backgroundImage = null);
		
		// Transparency Slider
		transparencySlider.valueProperty()
		        .addListener(list -> scene.setFill(Color.rgb(0, 0, 0, transparencySlider.getValue())));
		
		// ----------TopBar
		
		// PauseTransition
		pauseTransition.setOnFinished(f -> {
			if (!topBar.isHover() && window.isShowing()) {
				topBar.setVisible(false);
				setCursor(Cursor.NONE);
				xPlayerUI.visualizer.setCursor(Cursor.NONE);
			}
		});
		
		addEventHandler(MouseEvent.MOUSE_MOVED, m -> {
			pauseTransition.playFromStart();
			topBar.setVisible(true);
			setCursor(Cursor.HAND);
			xPlayerUI.visualizer.setCursor(Cursor.HAND);
		});
		
		// onTop
		onTop.selectedProperty().addListener((observable , oldValue , newValue) -> {
			window.setAlwaysOnTop(newValue);
		});
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
	 * @param type the type
	 */
	public void changeImage(Type type) {
		File image = Main.specialChooser.prepareToSelectImage(window);
		if (image != null) {
			Image img = new Image(image.toURI().toString());
			if (img.getWidth() <= 4800 && img.getHeight() <= 4800) {
				if (type == Type.background)
					xPlayerUI.visualizer.backgroundImage = img;
				else if (type == Type.foreground)
					xPlayerUI.visualizer.foregroundImage = img;
			} else
				ActionTool.showNotification("Warning",
				        "Maximum Size Allowed 4800*4800 \n Current is:" + img.getWidth() + "*" + img.getHeight(),
				        NotificationType.WARNING);
			
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
		
		label.setText("~Visualizer[ " + xPlayerUI.getKey() + " ]~");
		
		// setBackground
		setBackground.setOnAction(a -> changeImage(Type.background));
		// setForeground
		setForeground.setOnAction(a -> changeImage(Type.foreground));
		
		// Add the visualizer
		visualizerPane.setCenter(xPlayerUI.visualizer);
		
		// show the window
		window.show();
	}
	
	/**
	 * Removes the visualizer from the Window.
	 */
	private void removeVisualizer() {
		pauseTransition.stop();
		xPlayerUI.visualizer.setCursor(Cursor.HAND);
		xPlayerUI.reAddVisualizer();
		window.close();
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
