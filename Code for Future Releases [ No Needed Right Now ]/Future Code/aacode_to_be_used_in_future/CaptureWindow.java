/*
 * 
 */
package aacode_to_be_used_in_future;

import application.Main;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

/**
 * Is used to capture an area of the screen.
 *
 * @author GOXR3PLUS
 */
public class CaptureWindow extends Stage {

	/** The border pane. */
	// BorderPane and Canvas
	BorderPane borderPane = new BorderPane();
	
	/** The canvas. */
	Canvas canvas = new Canvas();

	/** The gc. */
	GraphicsContext gc = canvas.getGraphicsContext2D();
	
	/** The stage. */
	Stage stage;

	/** The width. */
	// Variables
	int width;
	
	/** The height. */
	int height;
	
	/** The x pressed. */
	int xPressed = 0;
	
	/** The y pressed. */
	int yPressed = 0;
	
	/** The x now. */
	int xNow = 0;
	
	/** The y now. */
	int yNow = 0;
	
	/** The foreground. */
	Color foreground = Color.rgb(255, 167, 0);
	
	/** The background. */
	Color background = Color.rgb(0, 0, 0, 0.3);

	/**
	 * Constructor.
	 *
	 * @param screenWidth the screen width
	 * @param screenHeight the screen height
	 * @param primary the primary
	 */
	public CaptureWindow(double screenWidth, double screenHeight, Stage primary) {
		stage = primary;

		setX(0);
		setY(0);
		setWidth(screenWidth);
		setHeight(screenHeight);
		getIcons().add(new Image(getClass().getResourceAsStream(InfoTool.images + "icon.png")));
		initOwner(primary);
		initStyle(StageStyle.TRANSPARENT);
		setAlwaysOnTop(true);

		// BorderPane
		borderPane.setStyle("-fx-background-color:rgb(0,0,0,0.1);");

		// Canvas
		canvas.setWidth(screenWidth);
		canvas.setHeight(screenHeight);
		canvas.setOnMousePressed(m -> {
			xPressed = (int) m.getScreenX();
			yPressed = (int) m.getScreenY();
		});
		canvas.setOnMouseDragged(m -> {
			xNow = (int) m.getScreenX();
			yNow = (int) m.getScreenY();
			repaintCanvas();
		});

		borderPane.setCenter(canvas);

		// Scene
		setScene(new Scene(borderPane, Color.TRANSPARENT));
		getScene().setCursor(Cursor.CROSSHAIR);
		getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.B) {
				close();
				Main.libraryMode.multipleLibs.getSelectedLibrary().getSmartController()
						.fireCaptureEvent(calculatedRectangle());
			}
		});

		// gc
		gc.setLineDashes(6);
		gc.setFont(Font.font("null", FontWeight.BOLD, 14));
	}

	/**
	 * Repaints the canvas *.
	 */
	protected void repaintCanvas() {

		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setStroke(foreground);
		gc.setFill(background);
		gc.setLineWidth(3);

		if (xNow > xPressed && yNow > yPressed) { // Right and Down

			calculateWidthAndHeight(xNow - xPressed, yNow - yPressed);
			gc.strokeRect(xPressed, yPressed, width, height);
			gc.fillRect(xPressed, yPressed, width, height);

		} else if (xNow < xPressed && yNow < yPressed) { // Left and Up

			calculateWidthAndHeight(xPressed - xNow, yPressed - yNow);
			gc.strokeRect(xNow, yNow, width, height);
			gc.fillRect(xNow, yNow, width, height);

		} else if (xNow > xPressed && yNow < yPressed) { // Right and Up

			calculateWidthAndHeight(xNow - xPressed, yPressed - yNow);
			gc.strokeRect(xPressed, yNow, width, height);
			gc.fillRect(xPressed, yNow, width, height);

		} else if (xNow < xPressed && yNow > yPressed) { // Left and Down

			calculateWidthAndHeight(xPressed - xNow, yNow - yPressed);
			gc.strokeRect(xNow, yPressed, width, height);
			gc.fillRect(xNow, yPressed, width, height);
		}

	}

	/**
	 * Show the window.
	 */
	public void showWindow() {
		xNow = 0;
		yNow = 0;
		xPressed = 0;
		yPressed = 0;
		repaintCanvas();
		show();
	}

	/**
	 * Calculates the width and height of the rectangle.
	 *
	 * @param w the w
	 * @param h the h
	 */
	private final void calculateWidthAndHeight(int w, int h) {
		width = w;
		height = h;
	}

	/**
	 * Selects whole Screen.
	 */
	public void selectWholeScreen() {
		xPressed = 0;
		yPressed = 0;
		xNow = (int) getWidth();
		yNow = (int) getHeight();
	}

	/**
	 * Return an array witch contains the (UPPER_LEFT) Point2D of the rectangle
	 * and the width and height of the rectangle.
	 *
	 * @return the int[]
	 */
	public int[] calculatedRectangle() {

		if (xNow > xPressed) { // Right
			if (yNow > yPressed) // and DOWN
				return new int[] { xPressed, yPressed, xNow - xPressed, yNow - yPressed };
			else if (yNow < yPressed) // and UP
				return new int[] { xPressed, yNow, xNow - xPressed, yPressed - yNow };
		} else if (xNow < xPressed) { // LEFT
			if (yNow > yPressed) // and DOWN
				return new int[] { xNow, yPressed, xPressed - xNow, yNow - yPressed };
			else if (yNow < yPressed) // and UP
				return new int[] { xNow, yNow, xPressed - xNow, yPressed - yNow };
		}

		return new int[] { xPressed, yPressed, xNow, yNow };
	}

}
