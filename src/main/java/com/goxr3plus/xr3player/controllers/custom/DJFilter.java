/*
 * 
 */
package com.goxr3plus.xr3player.controllers.custom;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import com.goxr3plus.xr3player.xplayer.visualizer.geometry.ResizableCanvas;

/**
 * Represents a disc controller.
 *
 * @author GOXR3PLUS
 */
public class DJFilter extends StackPane {

	public enum DJFilterCategory {
		EQUALIZER_FILTER, VOLUME_FILTER;
	}

	/** The listeners. */
	private final ArrayList<DJFilterListener> listeners = new ArrayList<>();

	/** The arc color. */
	private Color arcColor;

	/** The angle. */
	private int angle;

	/** The canvas. */
	// Canvas
	private final ResizableCanvas canvas = new ResizableCanvas();

	/**
	 * The X of the Point that is in the circle circumference
	 */
	private double circlePointX;
	/**
	 * The Y of the Point that is in the circle circumference
	 */
	private double circlePointY;

	// The maximum Value
	private double maximumValue;

	// The minimum Value

	private double minimumValue;

	private final DJFilterCategory filterCategory;

	/**
	 * Constructor
	 * 
	 * @param width
	 * @param arcColor
	 * @param currentValue
	 * @param minimumValue
	 * @param maximumValue
	 */
	public DJFilter(int width, Color arcColor, double currentValue, double minimumValue,
					double maximumValue, DJFilterCategory filterCategory) {
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
		this.filterCategory = filterCategory;

		super.setPickOnBounds(true);

		// StackPane
		canvas.setPickOnBounds(false);
		canvas.setCursor(Cursor.OPEN_HAND);
		canvas.setPickOnBounds(false);
		super.setPickOnBounds(false);

		this.arcColor = arcColor;
		canvas.setEffect(new DropShadow(10, Color.BLACK));

		getChildren().addAll(canvas);

		// Event handlers
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);

		resizeDisc(width);
		setValue(currentValue, true);
	}

	/**
	 * Register a new DJDiscListener.
	 *
	 * @param listener the listener
	 */
	public void addDJDiscListener(DJFilterListener listener) {
		listeners.add(listener);
	}

	/**
	 * Resizes the disc to the given values.
	 *
	 * @param width  the width, also used for the height.
	 */
	public void resizeDisc(int width) {
		int height = width;

		if (width >= 30) {

			double halfWidth = width / 2.00;
			double halfHeight = height / 2.00;

			setMinSize(width, height);
			setMaxSize(width, height);
			setPrefSize(width, height);
			canvas.setWidth(width);
			canvas.setHeight(height);
			canvas.setClip(new Circle(halfWidth, halfHeight, halfWidth));

			repaint();
		}
	}

	/**
	 * Repaints the disc.
	 */
	public void repaint() {

		// Calculate here to use less cpu
		double prefWidth = getPrefWidth();
		double prefHeight = getPrefHeight();
		canvas.gc.setLineCap(StrokeLineCap.ROUND);

		// Clear the outer rectangle
		canvas.gc.clearRect(0, 0, prefWidth, prefHeight);
		canvas.gc.setFill(filterCategory == DJFilterCategory.EQUALIZER_FILTER ? Color.BLACK : Color.TRANSPARENT);
		canvas.gc.fillRect(0, 0, prefWidth, prefHeight);

		// Arc Background Oval
		canvas.gc.setLineWidth(7);
		canvas.gc.setStroke(Color.WHITE);
		canvas.gc.strokeArc(5, 5, prefWidth - 10, prefHeight - 10, 90, 360.00 + angle, ArcType.OPEN);

		// Foreground Arc
		canvas.gc.setStroke(arcColor);
		canvas.gc.strokeArc(5, 5, prefWidth - 10, prefHeight - 10, 90, angle, ArcType.OPEN);

		// Value Arc
		if (filterCategory == DJFilterCategory.EQUALIZER_FILTER) {
			canvas.gc.setLineCap(StrokeLineCap.SQUARE);
			canvas.gc.setLineDashes(6);
			canvas.gc.setLineWidth(3);
			canvas.gc.setStroke(arcColor);
			int value = (getValue() == 0) ? 0 : (int) (((double) getValue() / (double) maximumValue) * 180);
			// System.out.println(value + " max : " + maximumValue)
			canvas.gc.setFill(Color.BLACK);
			canvas.gc.fillArc(11, 11, prefWidth - 22, prefHeight - 22, 90, 360, ArcType.OPEN);
			canvas.gc.strokeArc(13, 13, prefWidth - 26, prefHeight - 26, -90, -value, ArcType.OPEN);
			canvas.gc.strokeArc(13, 13, prefWidth - 26, prefHeight - 26, -90, +value, ArcType.OPEN);
		}
		canvas.gc.setLineDashes(0);
		canvas.gc.setLineCap(StrokeLineCap.ROUND);

		// --------------------------Maths to find the point on the circle
		// circumference
		// draw the progress oval

		// here i add + 89 to the angle cause the Java has where i have 0
		// degrees the 90 degrees.
		// I am counting the 0 degrees from the top center of the circle and
		// Java calculates them from the right mid of the circle and it is
		// going left , i calculate them clock wise
		int angle2 = this.angle + 89;
		int minus = 8;

		// Find the point on the circle circumference
		circlePointX = Math.round(
				((int) (prefWidth - minus)) / 2 + Math.cos(Math.toRadians(-angle2)) * ((int) (prefWidth - minus) / 2));
		circlePointY = Math.round(((int) (prefHeight - minus)) / 2
				+ Math.sin(Math.toRadians(-angle2)) * ((int) (prefHeight - minus) / 2));

		// System.out.println("Width:" + canvas.getWidth() + " , Height:" +
		// canvas.getHeight() + " , Angle: " + this.angle)
		// System.out.println(circlePointX + "," + circlePointY)

		int ovalWidth = 7;
		int ovalHeight = 7;

		// fix the circle position
		if (-angle >= 0 && -angle <= 90) {
			circlePointX = circlePointX - ovalWidth / 2 + 2;
			circlePointY = circlePointY + 1;
		} else if (-angle > 90 && -angle <= 180) {
			circlePointX = circlePointX - ovalWidth / 2 + 3;
			circlePointY = circlePointY - ovalWidth / 2 + 2;
		} else if (-angle > 180 && -angle <= 270) {
			circlePointX = circlePointX + 2;
			circlePointY = circlePointY - ovalWidth / 2 + 2;
		} else if (-angle > 270) {
			circlePointX = circlePointX + 2;
			// previousY = previousY - 7
		}

		canvas.gc.setLineWidth(5);
		canvas.gc.setStroke(Color.BLACK);
		canvas.gc.strokeOval(circlePointX, circlePointY, ovalWidth, ovalHeight);

		canvas.gc.setFill(arcColor);
		canvas.gc.fillOval(circlePointX, circlePointY, ovalWidth, ovalHeight);

		// System.out.println("Angle is:" + ( -this.angle ))
		// System.out.println("FormatedX is: " + previousX + ", FormatedY is: "
		// + previousY)
		// System.out.println("Relative Mouse X: " + m.getX() + " , Relative
		// Mouse Y: " + m.getY())

		// Draw the drag able rectangle
		// gc.setFill(Color.WHITE)
		// gc.fillOval(getWidth() / 2, 0, 20, 20)

		// ----------------------------------------------------------------------------------------------

	}

	/**
	 * Calculates the angle based on the given value and the maximum value allowed.
	 *
	 */
	public void setValue(double newValue, boolean notifyListeners) {

		// Find the current angle based on the new value given + the maximum value
		angle = (int) ((maximumValue == 0.0 || newValue == 0.0) ? 0.0
				: newValue == maximumValue ? 360.0 : -((360.0 * newValue) / maximumValue));

		// Notify all the Listeners
		if (notifyListeners)
			listeners.forEach(listener -> listener.valueChanged(getValue()));

		// Repaint
		repaint();
	}

	/**
	 * THIS NEEDS TO BE FIXED IT COMPLETELY IGNORES MINIMUM VALUE
	 * 
	 * Returns a Value based on the angle of the disc and the maximum value allowed.
	 *
	 * @return Returns a Value based on the angle of the disc and the maximum value
	 *         allowed
	 */

	@Deprecated
	public double getValue() {
		double holder1 = (maximumValue * -angle) / 360;
		// double holder2 = ( minimumValue * -angle ) / 360;
		// System.out.println("Holder : " + holder1);

		// double transformer = holder1 + holder2;

		return holder1;
	}

	/**
	 * Returns the color of the arc.
	 *
	 * @return The Color of the Disc Arc
	 */
	public Color getArcColor() {

		return arcColor;
	}

	/**
	 * Returns the Canvas of the disc.
	 *
	 * @return The Canvas of the Disc
	 */
	public ResizableCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Set the color of the arc to the given one.
	 *
	 * @param color the new arc color
	 */
	public void setArcColor(Color color) {
		arcColor = color;
	}

	/**
	 * Calculate the disc angle based on mouse position.
	 *
	 * @param m       the event
	 */
	public void setAngleUsingMouseEvent(MouseEvent m) {
		// Define mouseX , mouseY
		double mouseX, mouseY;

		// Go find it
		if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.SECONDARY
				|| m.getButton() == MouseButton.MIDDLE) {
			mouseX = m.getX();
			mouseY = m.getY();
			if (mouseX > getWidth() / 2)
				angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, getHeight() / 2 - mouseY));
			else {
				angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, getHeight() / 2 - mouseY));
				angle = -(360 - angle); // make it minus cause i turn it
				// on the right

				// System.out.println(getValue())

			}
			Platform.runLater(this::repaint);

		}
	}

	/**
	 * On mouse dragged.
	 *
	 * @param m the m
	 */
	private void onMouseDragged(MouseEvent m) {
		setAngleUsingMouseEvent(m);
		listeners.forEach(listener -> listener.valueChanged(getValue()));
	}

	/**
	 * @return the maximumValue
	 */
	public double getMaximumValue() {
		return maximumValue;
	}

}
