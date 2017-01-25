/**
 * 
 */
package visualizer.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import tools.InfoTool;

/**
 * 
 * This class extends a StackPane which must have as a children a Visualizer
 * 
 * @author GOXR3PLUS
 *
 */
public class VisualizerStackController extends StackPane {

    @FXML
    private Label visualizerTypeLabel;

    private FadeTransition fadeTransition;

    /**
     * Constructor
     */
    public VisualizerStackController() {

	// ----------------- FXMLLoader----------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "VisualizerStackController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "VisualizerStackController FXML can't be loaded!",
		    ex);
	}
    }

    /** Called as soon as the .fxml has been loaded */
    @FXML
    private void initialize() {

	// fadeTranstion
	fadeTransition = new FadeTransition(Duration.millis(1500), visualizerTypeLabel);
	fadeTransition.setFromValue(1.0);
	fadeTransition.setToValue(0.0);

	visualizerTypeLabel.setOpacity(0);
    }

    /**
     * Replays the fade effect to show the new type of visualizer
     * @param text 
     */
    public void replayLabelEffect(String text) {
	visualizerTypeLabel.setText(text);
	fadeTransition.playFromStart();
    }

}
