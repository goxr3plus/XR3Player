/*
 * 
 */
package customNodes;

import java.time.LocalTime;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

// TODO: Auto-generated Javadoc
/**
 * The Class DigitalClock.
 */
public class DigitalClock extends StackPane {

	/** The canvas. */
	Canvas canvas = new Canvas();
	
	/** The gc. */
	GraphicsContext gc = canvas.getGraphicsContext2D();

	/** The text. */
	Text text = new Text("");
	
	/** The time now. */
	StringProperty timeNow = new SimpleStringProperty();

	/**
	 * Instantiates a new digital clock.
	 *
	 * @param x the x
	 * @param d the d
	 * @param width the width
	 * @param height the height
	 */
	// Constructor
	public DigitalClock(int x, double d, int width, int height) {

		// Ρυθμίσεις αρχικοποίησης
		setLayoutX(x);
		setLayoutY(d);
		setWidth(width);
		setHeight(height);

		canvas.setWidth(width);
		canvas.setHeight(height);
		text.setFill(Color.WHITE);
		text.setFont(Font.font("null", FontWeight.BOLD, 15));
		text.textProperty().bind(timeNow);

		getChildren().addAll(canvas, text);

		paintClock();
		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				LocalTime time = LocalTime.now();
				while (true) {
					time = LocalTime.now();
					timeNow.setValue(
							zero(time.getHour()) + ":" + zero(time.getMinute()) + ":" + zero(time.getSecond()));
					Thread.sleep(1000);
				}
			}
		}).start();
		;
	}

	/**
	 * Paint clock.
	 */
	private void paintClock() {

		// Clear Rect
		gc.clearRect(0, 0, getWidth(), getHeight());

		gc.setFill(Color.FIREBRICK);
		gc.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
	}

	/**
	 * Zero.
	 *
	 * @param number the number
	 * @return the string
	 */
	// Προσθέτει μηδενικά εάν κάποιο είναι <10
	private String zero(int number) {
		return (number < 10) ? "0" + number : "" + number;
	}

}
