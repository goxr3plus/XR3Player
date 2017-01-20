/*
 * 
 */
package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXProgressBar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import tools.InfoTool;

/**
 * The Class UpdateScreen.
 */
public class UpdateScreen extends StackPane {

    /** The rectangle. */
    @FXML
    private Rectangle rectangle;

    /** The progress bar. */
    @FXML
    public JFXProgressBar progressBar;

    /** The label. */
    @FXML
    public Label label;

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UpdateScreen.class.getName());

    /**
     * Constructor.
     */
    public UpdateScreen() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "UpdateScreen.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Update Screen Can't be loaded", ex);
        }
    }

    /** Called as soon as the .fxml has been loaded */
    @FXML
    public void initialize() {

        setStyle("-fx-background-image:url('/image/logo.jpg');  -fx-background-size:100% 100%; -fx-background-position: center center; -fx-background-repeat: stretch;");

    }

}
