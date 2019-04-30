/*
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * The Class StarWindow.
 */
public class StarWindow extends GridPane {

	// --------------------------------

	@FXML
	private Canvas canvas;

	@FXML
	private Label starsLabel;

	@FXML
	private Label titleLabel;

	@FXML
	private JFXButton ok;

	@FXML
	private JFXButton close;

	// -------------------------------------

	/** The window. */
	private final Stage window = new Stage();;

	/** The gc. */
	private GraphicsContext gc;

	/** The stars position. */
	private int[] starsPosition = { 5, 35, 65, 95, 125 };

	/** The stars. */
	private DoubleProperty stars = new SimpleDoubleProperty();

	/** The accepted. */
	private boolean accepted;

	/** The no star. */
	private static final Image noStar = InfoTool.getImageFromResourcesFolder("noStar.png");

	/** The half star. */
	private static final Image halfStar = InfoTool.getImageFromResourcesFolder("halfStar.png");

	/** The star. */
	private static final Image star = InfoTool.getImageFromResourcesFolder("star.png");

	String[] labelText = { "No Stars", "Very Bad", "Bad", "Very Bored", "Bored", "Almost Fine", "Fine", "Good",
			"Very Good", "Amazing", "Excellent" };

	/**
	 * The timeLine which controls the animations of the Window
	 */
	private Timeline timeLine = new Timeline();

	/**
	 * Constructor.
	 */
	public StarWindow() {

		// ----------------------------------FXMLLoader----------------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.WINDOW_FXMLS + "StarWindow.fxml"));
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
		window.setTitle("Stars Window");
		window.initStyle(StageStyle.TRANSPARENT);
		window.setAlwaysOnTop(true);

		// Graphics Context 2D
		gc = canvas.getGraphicsContext2D();

		// Canvas
		canvas.setOnMouseDragged(m -> computeStars(m, false));
		canvas.setOnMouseReleased(m -> computeStars(m, false));
		canvas.setOnMouseMoved(m -> computeStars(m, true));
		canvas.setOnMouseExited(m -> repaintStars(getStars()));

		// close
		close.setOnAction(a -> close(false));

		// OK
		ok.setOnAction(a -> close(true));

		// Scene
		window.setScene(new Scene(this, Color.TRANSPARENT));
		window.getScene().getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		window.getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				close(false);
		});
		window.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && window.isShowing())// && Main.renameWindow.getTimeLine().getStatus() != Status.RUNNING &&
												// Main.emotionsWindow.getTimeLine().getStatus() != Status.RUNNING)
				close(false);
		});

		// Repaint
		repaintStars(getStars());

	}

	/**
	 * Repaints the canvas with stars.
	 */
	private void repaintStars(double stars) {

		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// System.out.println("Stars--->" + stars);

		// paint half and whole stars
		if ((stars - 0.5) < (int) stars)
			for (int i = 0; i < stars; i++)
				gc.drawImage(star, starsPosition[i], 0);

		else {
			for (int i = 0; i < stars - 1; i++)
				gc.drawImage(star, starsPosition[i], 0);

			gc.drawImage(halfStar, starsPosition[(int) stars], 0);
		}

		// Paint unselected Stars
		if (stars != 5)
			for (int i = 4; i >= stars; i--)
				gc.drawImage(noStar, starsPosition[i], 0);

		// Label Text
		starsLabel.setText(labelText[(int) Math.round(stars * 2)]);
	}

	/**
	 * Stars must be one number from 0 to 10.
	 *
	 * @param newStars the new stars
	 */
	private void setStars(double newStars) {
		if (getStars() == newStars)
			return;
		stars.set(newStars);
		repaintStars(stars.get());
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
	 * @param stars the stars
	 * @param node  the node
	 */
	public void show(String text, double stars, Node node) {

		// Stop the TimeLine
		timeLine.stop();
		window.close();

		// titleLabel
		titleLabel.setText(text);
		titleLabel.getTooltip().setText(text);

		// Auto Calculate the position
		Bounds bounds = node.localToScreen(node.getBoundsInLocal());
		show(stars, bounds.getMinX() - 202 + bounds.getWidth() / 2, bounds.getMaxY());

	}

	/**
	 * Show.
	 *
	 * @param stars the stars
	 * @param x     the x
	 * @param y     the y
	 */
	private void show(double stars, double x, double y) {
		setStars(stars);

		// Set once
		window.setX(x);
		window.setY(y);

		window.show();

		// Set it again -- NEEDS FIXING
		if (x <= -1 && y <= -1)
			window.centerOnScreen();
		else {
			if (x + getWidth() > JavaFXTool.getScreenWidth())
				x = JavaFXTool.getScreenWidth() - getWidth();
			else if (x < 0)
				x = 0;

			if (y + getHeight() > JavaFXTool.getScreenHeight())
				y = JavaFXTool.getScreenHeight() - getHeight();
			else if (y < 0)
				y = 0;

			window.setX(x);
			window.setY(y);

			// ------------Animation------------------
			// Y axis
			double yIni = y - getHeight() - 50;
			double yEnd = y - getHeight() / 2 - 10;
			window.setY(yIni);
			window.setX(x - 20);

			// Create Double Property
			final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
			yProperty.addListener((ob, n, n1) -> window.setY(n1.doubleValue()));

			// Create Time Line
			Timeline timeIn = new Timeline(
					new KeyFrame(Duration.seconds(0.15), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
			timeIn.play();
			// ------------ END of Animation------------------
		}

	}

	/**
	 * Close the Window.
	 *
	 * @param accepted True if accepted , False if not
	 */
	private void close(boolean accepted) {
		// System.out.println("Star Window Close called with accepted := " + accepted);
		this.accepted = accepted;

		// ------------Animation------------------
		// Y axis
		double yIni = window.getY();
		double yEnd = window.getY() + 50;
		window.setY(yIni);

		// Create Double Property
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> window.setY(n1.doubleValue()));

		// Create Time Line
		timeLine.getKeyFrames()
				.setAll(new KeyFrame(Duration.seconds(0.15), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		timeLine.setOnFinished(f -> window.close());
		timeLine.playFromStart();
		// ------------ END of Animation------------------

	}

	/**
	 * Computes the stars based on the mouse position on screen
	 */
	/**
	 * @param m         The MouseEvent
	 * @param fakeStars If True it just displays stars but doesn't actually changes
	 *                  them ( just repainting canvas )
	 */
	private void computeStars(MouseEvent m, boolean fakeStars) {
		int x = (int) m.getX();

		double var = 0.0;

		if (x <= 5)
			var = (0);
		else if (x >= 144) {
			var = (5);
		} else if (x >= 133)
			var = (4.5);
		else if (x >= 115)
			var = (4);
		else if (x >= 105)
			var = (3.5);
		else if (x >= 85)
			var = (3);
		else if (x >= 74)
			var = (2.5);
		else if (x >= 55)
			var = (2);
		else if (x >= 45)
			var = (1.5);
		else if (x >= 25)
			var = (1);
		else if (x >= 12)
			var = (0.5);

		if (!fakeStars)
			setStars(var);
		else
			repaintStars(var);
	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

	/**
	 * @return the timeLine
	 */
	public Timeline getTimeLine() {
		return timeLine;
	}

}
