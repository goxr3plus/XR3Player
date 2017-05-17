/*
 * 
 */
package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.tools.InfoTool;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * The Class UpdateScreen.
 */
public class UpdateScreen extends StackPane {

    @FXML
    private Rectangle rectangle;

    @FXML
    private Rectangle leftRectangle;

    @FXML
    private Rectangle rightRectangle;

    @FXML
    private ProgressIndicator progressBar;

    @FXML
    private Label label;

    // -------------------------------------------------------------------------------------

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UpdateScreen.class.getName());

    TranslateTransition translate1;
    TranslateTransition translate2;

    /**
     * Constructor.
     */
    public UpdateScreen() {
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "UpdateScreen.fxml"));
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

	// setStyle(
	// "-fx-background-image:url('/image/logo.jpg');
	// -fx-background-size:100% 100%; -fx-background-position: center
	// center; -fx-background-repeat: stretch;");

	// leftRectangle
	leftRectangle.widthProperty().bind(super.widthProperty().divide(2));
	leftRectangle.heightProperty().bind(super.heightProperty());

	rightRectangle.widthProperty().bind(leftRectangle.widthProperty());
	rightRectangle.heightProperty().bind(leftRectangle.heightProperty());

	translate1 = new TranslateTransition(Duration.millis(1000), leftRectangle);
	translate2 = new TranslateTransition(Duration.millis(1000), rightRectangle);

	//	FunIndicator fun  = new FunIndicator();	
	//	fun.setFromColor(Color.RED);
	//	fun.start();
	//	super.getChildren().add(fun);
    }

    /**
     * Makes the animation with rectangles when the application firstly opens
     */
    public void closeUpdateScreen() {

	// Left Rectangle
	if (translate1.getStatus() != Animation.Status.RUNNING && leftRectangle.getTranslateX() >= -leftRectangle.getWidth()) {
	    translate1.setFromX(leftRectangle.getX());
	    translate1.setToX(-leftRectangle.getWidth());
	    translate1.playFromStart();
	}

	// Right Rectangle
	if (translate2.getStatus() != Animation.Status.RUNNING && rightRectangle.getTranslateX() >= -rightRectangle.getWidth()) {
	    translate2.setFromX(rightRectangle.getX());
	    translate2.setToX(rightRectangle.getX() + rightRectangle.getWidth());
	    translate2.playFromStart();
	}
    }

    /**
     * @return the label
     */
    public Label getLabel() {
	return label;
    }

    /**
     * @return the progressBar
     */
    public ProgressIndicator getProgressBar() {
	return progressBar;
    }

}
