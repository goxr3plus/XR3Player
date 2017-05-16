/*
 * 
 */
package windows;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

/**
 * The Class StarWindow.
 */
public class StarWindow extends GridPane {

    /** The ok. */
    @FXML
    private Button ok;

    /** The canvas. */
    @FXML
    private Canvas canvas;

    /** The close. */
    @FXML
    private Button close;

    /** The window. */
    private Stage window;

    /** The gc. */
    protected GraphicsContext gc;

    /** The stars position. */
    private int[] starsPosition = { 5, 35, 65, 95, 125 };

    /** The stars. */
    private DoubleProperty stars = new SimpleDoubleProperty();

    /** The accepted. */
    private boolean accepted;

    /** The no star. */
    protected Image noStar = InfoTool.getImageFromResourcesFolder("noStar.png");

    /** The half star. */
    protected Image halfStar = InfoTool.getImageFromResourcesFolder("halfStar.png");

    /** The star. */
    protected Image star = InfoTool.getImageFromResourcesFolder("star.png");

    /**
     * Constructor.
     */
    public StarWindow() {

	// ----------------------------------FXMLLoader
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "StarWindow.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

    }

    /**
     * Called when .fxml is initialized
     */
    @FXML
    private void initialize() {

	// Window
	window = new Stage();
	window.initStyle(StageStyle.TRANSPARENT);
	window.initModality(Modality.APPLICATION_MODAL);
	window.setAlwaysOnTop(true);

	// Graphics Context 2D
	gc = canvas.getGraphicsContext2D();

	// Root
	getStyleClass().add("starWindow");

	// Canvas
	canvas.setOnMouseDragged(m -> computeStars(m));
	canvas.setOnMouseReleased(m -> computeStars(m));

	// close
	close.setOnAction(a -> close(false));

	// OK
	ok.setOnAction(a -> close(true));

	// Scene
	window.setScene(new Scene(this, Color.TRANSPARENT));
	window.getScene().getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
	window.getScene().setOnKeyReleased(key -> {
	    if (key.getCode() == KeyCode.ESCAPE)
		close(false);
	});

	// Repaint
	repaintStars();

    }

    /**
     * Repaints the canvas with stars.
     */
    private void repaintStars() {

	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

	// System.out.println("Stars--->" + getStars());

	// paint half and whole stars
	if ((getStars() - 0.5) < (int) getStars())
	    for (int i = 0; i < getStars(); i++)
		gc.drawImage(star, starsPosition[i], 0);

	else {
	    for (int i = 0; i < getStars() - 1; i++)
		gc.drawImage(star, starsPosition[i], 0);

	    gc.drawImage(halfStar, starsPosition[(int) getStars()], 0);
	}

	// Paint unselected Stars
	if (getStars() != 5)
	    for (int i = 4; i >= getStars(); i--)
		gc.drawImage(noStar, starsPosition[i], 0);
    }

    /**
     * Stars must be one number from 0 to 10.
     *
     * @param st
     *            the new stars
     */
    private void setStars(double st) {
	if (getStars() != st) {
	    stars.set(st);
	    repaintStars();
	}
    }

    /**
     * Return the number of stars that have been selected.
     *
     * @return the stars
     */
    public double getStars() {
	return stars.get();
    }

    /**
     * Return Stars Property.
     *
     * @return the double property
     */
    public DoubleProperty starsProperty() {
	return stars;
    }

    /**
     * Was accepted.
     *
     * @return true, if successful
     */
    public boolean wasAccepted() {
	return accepted;
    }

    /**
     * Show.
     *
     * @param stars
     *            the stars
     * @param node
     *            the node
     */
    public void show(double stars, Node node) {
	// Auto Calculate the position
	Bounds bounds = node.localToScreen(node.getBoundsInLocal());
	show(stars, bounds.getMinX() + 5, bounds.getMaxY());
    }

    /**
     * Show.
     *
     * @param stars
     *            the stars
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void show(double stars, double x, double y) {
	setStars(stars);

	if (x <= -1 && y <= -1)
	    window.centerOnScreen();
	else {

	    if (x + getWidth() > InfoTool.getVisualScreenWidth())
		x = InfoTool.getVisualScreenWidth() - getWidth();
	    else if (x < 0)
		x = 0;

	    if (y + getHeight() > InfoTool.getVisualScreenHeight())
		y = InfoTool.getVisualScreenHeight() - getHeight();
	    else if (y < 0)
		y = 0;

	    window.setX(x);
	    window.setY(y);
	}
	window.show();
    }

    /**
     * Close the Window.
     *
     * @param accepted
     *            True if accepted , False if not
     */
    public void close(boolean accepted) {
	this.accepted = accepted;
	window.close();
    }

    /**
     * Computes the stars based on the mouse position on screen
     */
    private void computeStars(MouseEvent m) {
	int x = (int) m.getX();

	if (x <= 5)
	    setStars(0);
	else if (x >= 144)
	    setStars(5);
	else if (x >= 133)
	    setStars(4.5);
	else if (x >= 115)
	    setStars(4);
	else if (x >= 105)
	    setStars(3.5);
	else if (x >= 85)
	    setStars(3);
	else if (x >= 74)
	    setStars(2.5);
	else if (x >= 55)
	    setStars(2);
	else if (x >= 45)
	    setStars(1.5);
	else if (x >= 25)
	    setStars(1);
	else if (x >= 12)
	    setStars(0.5);
    }

    /**
     * @return the window
     */
    public Stage getWindow() {
	return window;
    }

}
