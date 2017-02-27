/*
 * 
 */
package disc;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

// TODO: Auto-generated Javadoc
/**
 * The Class Balancer.
 */
public class Balancer extends Canvas {

	/** The image 1. */
	Image image1 =  new Image(getClass().getResourceAsStream("circle.png"));
	
	/**
	 * 0=Horizontal <br>
	 * 1=Vertical.
	 */
	Orientation orientation = Orientation.HORIZONTAL;

	/** The bar width. */
	// Το πάχος είτε Horizontal είτε Vertical της μπάρας την οποία κάνω drag
	private int barWidth = 8;
	
	/** The value. */
	private int value = 0;

	/** The maximum volume. */
	// Colors
	final int maximumVolume;
	
	/** The color 1. */
	Color color1 = Color.rgb(53, 144, 255);

	/** The gc. */
	GraphicsContext gc = getGraphicsContext2D();

	/**
	 * Instantiates a new balancer.
	 *
	 * @param d the d
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param currentValue the current value
	 * @param maximum the maximum
	 */
	public Balancer(double d, int y, int width, int height, int currentValue, int maximum) {
		value = currentValue;
		maximumVolume = maximum;

		setLayoutX(d);
		setLayoutY(y);
		setWidth(width+image1.getWidth()/2);
		setHeight(height+8);

		setCursor(Cursor.NONE);
		paintBalancer();
	}

	/**
	 * Paint balancer.
	 */
	private void paintBalancer() {

		if (orientation == Orientation.HORIZONTAL) { // HORIZONTAL
			gc.clearRect(0, 0, getWidth(), getHeight());

			// Σχεδίασε το Background
			gc.setFill(Color.BLACK);
			gc.fillRect(0, 0, getWidth(), getHeight());

			// Σχεδίασε πόσο έχει φορτώσει
			gc.setFill(color1);
			gc.fillRect(0, 5, value, getHeight() - 10);
			gc.setFill(Color.RED);
			gc.fillRect(value, 5, getWidth() - getVolume(), getHeight() - 10);

			// Ζωγραφίζω την μπάρα
			gc.drawImage(image1, value, 0);
			/*if (getVolume() < 100)
				gc.drawImage(image3, value, 0);
			else if (getVolume() == 100)
				gc.drawImage(image2, value, 0);
			else if (getVolume() > 100)
				gc.drawImage(image1, value, 0);*/

			gc.setStroke(Color.BLACK);
			gc.strokeText(String.valueOf(getVolume()), getWidth() / 2, getHeight() / 2 + 5);
		}

	}

	/**
	 * On mouse dragged.
	 *
	 * @param m the m
	 */
	public void onMouseDragged(MouseEvent m) {
		// Δώσε για νέα τιμή το mo.getX
		if (orientation == Orientation.HORIZONTAL) { // Horizontal
			// Συνθήκες παραμετροποίησης ώστε μην έχω κολλήματα με το mouse
			if (m.getX() > getMaximum())
				setV(getMaximum());
			else if (m.getX() < 0)
				setV(0);
			else
				setV((int) m.getX());
		} else { // Vertical
			// Συνθήκες παραμετροποίησης ώστε μην έχω κολλήματα με το mouse
			if (m.getY() > getMaximum())
				setV(0);
			else if (m.getY() < 0)
				setV(getMaximum());
			else
				setV(getHeight() - m.getY());
		}

	}

	/**
	 * On scroll.
	 *
	 * @param sc the sc
	 */
	public void onScroll(ScrollEvent sc) {

		int rotation = sc.getDeltaY() < 1 ? 1 : -1;
		// Συνθήκη με άρτιο Rolling
		if (value - rotation * 2 > -1 && value - rotation * 2 < getMaximum() + 1)
			setV(value - rotation * 2);
		else if (value - rotation > -1 && value - rotation < getMaximum() + 1)
			setV(value - rotation);
	}

	////// Θέτουν σε κάτι καινούργιο------------------(SET)

	/**
	 * Set Current Value of Slider *.
	 *
	 * @param d the new v
	 */
	private void setV(double d) {

		// Πρέπει το value να είναι ανάμεσα στο Maximum και στο Minimum
		if (d < getMaximum() + 1 && d > -1) {
			value = (int) d;
			paintBalancer();
		}
	}

	/**
	 * Θέτει την τιμή με βάση το Σύστημα απαρύθμισης δηλαδή(%,per 75 , per
	 * 200,etc .. ) ότι έδωσε ο χρήστης
	 *
	 * @param newValue the new volume
	 */
	public void setVolume(int newValue) {

		// Εάν είναι τα άκρα τότε να γίνετε επιτόπια διόρθωση
		if (newValue == 0) {
			value = 0;
			paintBalancer();
			return;
		}
		if (newValue == maximumVolume) {
			value = getMaximum();
			paintBalancer();
			return;
		}

		// Αλλιώς
		if (newValue < maximumVolume && newValue > 0) {
			if (getMaximum() / maximumVolume == 0) // Εάν είναι πολλαπλάσιο του
													// Systemal
				value = newValue * getMaximum() / maximumVolume;
			else // Εάν δεν είναι πολλαπλάσιο του Systemal
				value = (newValue * getMaximum() / maximumVolume) + 1;

			paintBalancer();
		}

	}

	////// Επιστρέφουν μία τιμή---------------(GET)

	/**
	 * Get Maximum Value of Slider.
	 *
	 * @return the maximum
	 */
	public int getMaximum() {

		if (orientation == Orientation.HORIZONTAL)
			return (int) (getWidth() - barWidth);
		else
			return (int) (getHeight() - barWidth);
	}

	/**
	 * Γυρνάει αποτέλεσμα με βάση το Σύστημα απαρύθμισης δηλαδή(%,per 75 , per
	 * 200,etc .. ) ότι έδωσε ο χρήστης
	 *
	 * @return the volume
	 */
	public int getVolume() {
		if (getMaximum() <= maximumVolume) // Εάν συμφωνεί ακριβώς με το σύστημα
			return value;
		else // Εάν χρειάζετε μετατροπή
			return value * maximumVolume / getMaximum();
	}

}
