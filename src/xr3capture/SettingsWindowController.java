/**
 * 
 */
package xr3capture;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The Class SettingsWindowController.
 *
 * @author GOXR3PLUS
 */
public class SettingsWindowController extends Stage {
	
	/** The root. */
	@FXML
	private BorderPane root;
	
	/** The marrytts toggle. */
	@FXML
	private JFXToggleButton marryttsToggle;
	
	/** The orientation. */
	@FXML
	private JFXToggleButton orientation;
	
	/** The precision slider. */
	@FXML
	private JFXSlider precisionSlider;
	
	/** The main window controller. */
	// --------------------
	MainWindowController mainWindowController;
	
	/** The capture window controller. */
	CaptureWindowController captureWindowController;
	
	/**
	 * Will be called as soon as FXML file is loaded.
	 */
	@FXML
	private void initialize() {
		
		setTitle("Settings");
		getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		setScene(new Scene(root));
		centerOnScreen();
		
		// orientation
		orientation.selectedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue) { // selected
				mainWindowController.getRoot().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
				orientation.setText("Current : LEFT  -> TO  -> RIGHT");
			} else {
				mainWindowController.getRoot().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
				orientation.setText("Current : RIGHT  -> TO  -> LEFT");
			}
		});
		
	}
	
	/**
	 * Add the needed references from the other controllers.
	 *
	 * @param mainWindowController the main window controller
	 * @param captureWindowController the capture window controller
	 */
	@SuppressWarnings("hiding")
	public void addControllerReferences(MainWindowController mainWindowController ,
	        CaptureWindowController captureWindowController) {
		
		this.mainWindowController = mainWindowController;
		this.captureWindowController = captureWindowController;
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							     Getters
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * Gets the precision slider.
	 *
	 * @return The precisionSlider
	 */
	public JFXSlider getPrecisionSlider() {
		return precisionSlider;
	}
	
	/**
	 * @return the orientation
	 */
	public JFXToggleButton getOrientation() {
		return orientation;
	}
	
	/**
	 * Gets the marry TTS toggle.
	 *
	 * @return The toggle which is for enabling/disabling text to speech
	 *         recognition
	 */
	public JFXToggleButton getMarryTTSToggle() {
		return marryttsToggle;
	}
	
	/*-----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							     Setters
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */
	
	/**
	 * @param precisionSlider the precisionSlider to set
	 */
	public void setPrecisionSlider(JFXSlider precisionSlider) {
		this.precisionSlider = precisionSlider;
	}
	
	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(JFXToggleButton orientation) {
		this.orientation = orientation;
	}
	
	/**
	 * @param marryttsToggle the marryttsToggle to set
	 */
	public void setMarryttsToggle(JFXToggleButton marryttsToggle) {
		this.marryttsToggle = marryttsToggle;
	}
	
}
