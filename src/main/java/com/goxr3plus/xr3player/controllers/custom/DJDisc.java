/*
 * 
 */
package com.goxr3plus.xr3player.controllers.custom;

import java.util.ArrayList;

import org.kordamp.ikonli.javafx.FontIcon;

import com.goxr3plus.xr3player.utils.general.TimeTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.goxr3plus.xr3player.xplayer.visualizer.geometry.ResizableCanvas;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Represents a disc controller.
 *
 * @author GOXR3PLUS
 */
public class DJDisc extends StackPane {

	/** The listeners. */
	private final ArrayList<DJDiscListener> listeners = new ArrayList<>();

	/** The arc color. */
	private Color arcColor;

	/**
	 * The Time will be Reversed or Normal.
	 *
	 * @author GOXR3PLUS
	 */
	private enum TimeMode {

		/** The normal. */
		NORMAL,
		/** The reversed. */
		REVERSED;
	}

	/** The angle. */
	private int angle;

	/** The Constant MAXIMUM_VOLUME. */
	private final int maximumVolume;
	private final SimpleIntegerProperty currentVolume;

	/** The time. */
	// About the Time
	private String time = "00:00";

	/** The time mode. */
	private TimeMode timeMode = TimeMode.NORMAL;

	/** The time field. */
	private final Label timeField = new Label(time);

	/** The canvas. */
	// Canvas
	private final ResizableCanvas canvas = new ResizableCanvas();

	/** The rotation animation. */
	// Animation
	private final Timeline rotationAnimation = new Timeline();

	/** The fade. */
	private final FadeTransition fade;

	/**
	 * Default Font Icon
	 */
	private final FontIcon noAlbumImageFontIcon = JavaFXTool.getFontIcon("gmi-album", Color.WHITE, 16);

	/** The image view. */
	private final ImageView imageView = new ImageView();

	/** The volume label. */
	// private final DragAdjustableLabel volumeLabel;

	/**
	 * The X of the Point that is in the circle circumference
	 */
	private double circlePointX;
	/**
	 * The Y of the Point that is in the circle circumference
	 */
	private double circlePointY;

	/**
	 * The rotation transformation
	 */
	private final Rotate rotationTransf;

	/**
	 * Constructor.
	 * 
	 * @param perimeter     The perimeter of the disc.
	 * @param arcColor      The color of the disc arc
	 * @param volume        The current volume of the disc
	 * @param maximumVolume The maximum volume of the disc
	 *                      [[SuppressWarningsSpartan]]
	 */
	public DJDisc(int perimeter, Color arcColor, int volume, int maximumVolume) {
		this.maximumVolume = maximumVolume;
		this.currentVolume = new SimpleIntegerProperty(volume);

		super.setPickOnBounds(true);

		// StackPane
		canvas.setPickOnBounds(false);
		canvas.setCursor(Cursor.OPEN_HAND);
		canvas.setPickOnBounds(false);
		super.setPickOnBounds(false);
		// setStyle("-fx-background-color:rgb(255,255,255,0.6)");

		this.arcColor = arcColor;
		canvas.setEffect(new DropShadow(15, Color.BLACK));

		// imageView
		replaceImage(null);

		// timeField
		timeField.setAlignment(Pos.CENTER);
		timeField.setMaxWidth(Double.MAX_VALUE);
		timeField.setId("time-field-normal");
		timeField.setOnMouseClicked(c -> {
			if (timeMode == TimeMode.NORMAL) {
				timeMode = TimeMode.REVERSED;
				timeField.setId("time-field-reversed");
			} else {
				timeMode = TimeMode.NORMAL;
				timeField.setId("time-field-normal");
			}
		});
		// timeField.setStyle("-fx-background-color:white;");

		// volumeLabel
		// volumeLabel = new DragAdjustableLabel(volume, 0, maximumVolume);
		// //
		// volumeLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/style/Younger
		// // than me Bold.ttf"), 18))
		// volumeLabel.currentValueProperty().addListener((observable , oldValue ,
		// newValue) -> {
		// listeners.forEach(l -> l.volumeChanged(newValue.intValue()));
		// repaint();
		// });
		/*
		 * ImageView graphic = new ImageView(VOLUME_IMAGE); imageView.setFitWidth(15);
		 * imageView.setFitHeight(15); imageView.setSmooth(true);
		 * volumeLabel.setGraphic(graphic); volumeLabel.setGraphicTextGap(1);
		 */

		// Fade animation for centerDisc
		fade = new FadeTransition(new Duration(1000), canvas);
		fade.setFromValue(1.0);
		fade.setToValue(0.2);
		fade.setAutoReverse(true);
		fade.setCycleCount(Animation.INDEFINITE);
		// fade.play()

		// rotation transform starting at 0 degrees, rotating about pivot point
		rotationTransf = new Rotate(0, perimeter / 2.00 - 10, perimeter / 2.00 - 10);
		imageView.getTransforms().add(rotationTransf);
		imageView.setMouseTransparent(true);
		imageView.visibleProperty().bind(imageView.imageProperty().isNotNull());
		imageView.visibleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue)
				resizeDisc(localPerimeter);
		});

		// rotate a square using time line attached to the rotation transform's
		// angle property.
		rotationAnimation.getKeyFrames()
				.add(new KeyFrame(Duration.millis(10000), new KeyValue(rotationTransf.angleProperty(), 360)));
		rotationAnimation.setCycleCount(Animation.INDEFINITE);
		// rotationAnimation.play()

		// When no album image exists
		noAlbumImageFontIcon.setIconColor(arcColor);
		noAlbumImageFontIcon.visibleProperty().bind(imageView.imageProperty().isNull());
		noAlbumImageFontIcon.setMouseTransparent(true);
		noAlbumImageFontIcon.visibleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue)
				resizeDisc(localPerimeter);
		});

		getChildren().addAll(canvas, imageView, noAlbumImageFontIcon);

		// MouseListeners
		// canvas.setOnMousePressed(m -> canvas.setCursor(Cursor.CLOSED_HAND))
		canvas.setOnMouseDragged(this::onMouseDragged);
		// setOnScroll(this::onScroll);

		resizeDisc(perimeter);
	}

	/**
	 * Register a new DJDiscListener.
	 *
	 * @param listener the listener
	 */
	public void addDJDiscListener(DJDiscListener listener) {
		listeners.add(listener);
	}

	private int localPerimeter;

	/**
	 * Resizes the disc to the given values.
	 *
	 */
	public void resizeDisc(double perimeterr) {
		// Avoid divide by zero
		if (perimeterr < 1)
			return;

		int perimeter = (int) Math.round(perimeterr);
		localPerimeter = perimeter;

		// Disc radius
		double radius = perimeter / 2.00;

		// {Maximum,Preferred} Size
		setMinSize(perimeter, perimeter);
		setMaxSize(perimeter, perimeter);
		setPrefSize(perimeter, perimeter);
		canvas.setWidth(perimeter);
		canvas.setHeight(perimeter);
		canvas.setClip(new Circle(radius, radius, radius));

		// Fix the small hover disc
		changeHoverCircleRadius(perimeter / 10, true, perimeter, radius);

		// timeField
		// timeField.setTranslateY(-height * 26 / 100.00)

		// volumeField
		// volumeLabel.setTranslateY(+height * 26 / 100.00)

		// Repaint
		// repaint()

	}

	// private static final Color webGrey = Color.web("#353535")

	/**
	 * Change the size of small circle
	 * 
	 * @param newPerimeter
	 * @param repaint
	 */
	private void changeHoverCircleRadius(int newPerimeter, boolean repaint, int discPerimeter, double discRadius) {
		smallCirclePerimeter = newPerimeter;
		minus = smallCirclePerimeter - 1;
		minus2 = smallCirclePerimeter + 3;
		int val = smallCirclePerimeter;
		int width = discPerimeter - 2 * val;

		// ImageView
		if (imageView.isVisible()) {
			imageView.setTranslateX(val);
			imageView.setTranslateY(val);
			imageView.setFitWidth(width);
			imageView.setFitHeight(discPerimeter - 2 * val);
			imageView.setClip(new Circle(discRadius - 2 * val, discRadius - 2 * val, discRadius - 2 * val));
		}

		// Font Icon
		if (noAlbumImageFontIcon.isVisible()) {
			int size = (int) (width / 1.05);
			if (size > 0)
				noAlbumImageFontIcon.setIconSize(size);
			else
				noAlbumImageFontIcon.setIconSize(1);
		}

		// rotationTransformation
		rotationTransf.setPivotX(discPerimeter / 2.00 - 2 * val);
		rotationTransf.setPivotY(discPerimeter / 2.00 - 2 * val);

		// repaint?
		if (repaint)
			repaint();
	}

	// draw the progress oval circumference
	int smallCirclePerimeter = 35;
	int minus = smallCirclePerimeter - 1;
	int minus2 = smallCirclePerimeter + 3;
	private final Color darkGrey = Color.web("#202020");

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
		canvas.gc.setFill(Color.TRANSPARENT);
		canvas.gc.fillRect(0, 0, prefWidth, prefHeight);

		// Volume Arc
		// canvas.gc.setLineCap(StrokeLineCap.SQUARE);
		// canvas.gc.setLineDashes(6);
		// canvas.gc.setLineWidth(3);
		// canvas.gc.setStroke(arcColor);
		// int value = this.getVolume() == 0 ? 0 : (int) ( ( (double) this.getVolume() /
		// (double) this.maximumVolume ) * 180 );
		// //System.out.println(value)
		// canvas.gc.setFill(webGrey);
		// canvas.gc.fillArc(11, 11, prefWidth - 22, prefHeight - 22, 90, 360,
		// ArcType.OPEN);
		// canvas.gc.strokeArc(13, 13, prefWidth - 26, prefHeight - 26, -90, -value,
		// ArcType.OPEN);
		// canvas.gc.strokeArc(13, 13, prefWidth - 26, prefHeight - 26, -90, +value,
		// ArcType.OPEN);
		// canvas.gc.setLineDashes(0);
		// canvas.gc.setLineCap(StrokeLineCap.ROUND);

		// --------------------------Maths to find the point on the
		// circle-----------------------

		// Arc Background Oval
		canvas.gc.setLineWidth(smallCirclePerimeter);
		canvas.gc.setStroke(Color.WHITE);
		canvas.gc.strokeArc(smallCirclePerimeter, smallCirclePerimeter, prefWidth - 2 * smallCirclePerimeter,
				prefHeight - 2 * smallCirclePerimeter, 90, angle + 360.00, ArcType.OPEN);

		// Arc Foreground Oval
		canvas.gc.setStroke(arcColor);
		canvas.gc.strokeArc(smallCirclePerimeter, smallCirclePerimeter, prefWidth - 2 * smallCirclePerimeter,
				prefHeight - smallCirclePerimeter * 2, 90, angle, ArcType.OPEN);

		// Here i add + 89 to the angle cause the Java has where i have 0 degrees the 90
		// degrees.
		// I am counting the 0 degrees from the top center of the circle and
		// Java calculates them from the right mid of the circle and it is
		// going left , i calculate them clock wise
		int angle2 = this.angle + 89;

		// Find the point on the circle circumference
		circlePointX = Math
				.round((prefWidth - minus) / 2.00 + (prefWidth - minus) / 2.00 * Math.cos(Math.toRadians(-angle2)));
		circlePointY = Math
				.round((prefHeight - minus) / 2.00 + (prefHeight - minus) / 2.00 * Math.sin(Math.toRadians(-angle2)));

		// System.out.println("Width:" + canvas.getWidth() + " , Height:" +
		// canvas.getHeight() + " , Angle: " + this.angle)
		// System.out.println(circlePointX + "," + circlePointY)

		// fix the circle position
		int positiveAngle = -angle;
		if (positiveAngle >= 0 && positiveAngle <= 90)
			if (positiveAngle < 50) {
				circlePointX -= 1.2 * smallCirclePerimeter;
				circlePointY -= smallCirclePerimeter / 2.5;
			} else {
				circlePointX -= 1.5 * smallCirclePerimeter;
				circlePointY -= smallCirclePerimeter / 1.8;
			}
		else if (positiveAngle > 90 && positiveAngle <= 180) {
			circlePointX -= 1.5 * smallCirclePerimeter;
			circlePointY -= 1.5 * smallCirclePerimeter;
		} else if (positiveAngle > 180 && positiveAngle <= 270) {
			circlePointX -= smallCirclePerimeter / 1.6;
			circlePointY -= 1.35 * smallCirclePerimeter;
		} else if (positiveAngle > 270) {
			circlePointX -= smallCirclePerimeter / 1.6;
			circlePointY -= smallCirclePerimeter / 2.0;
		}

		canvas.gc.setLineWidth(smallCirclePerimeter / 2);
		canvas.gc.setStroke(darkGrey);
		canvas.gc.strokeOval(circlePointX + smallCirclePerimeter, circlePointY + smallCirclePerimeter,
				smallCirclePerimeter, smallCirclePerimeter);

		canvas.gc.setFill(arcColor);
		canvas.gc.fillOval(circlePointX + smallCirclePerimeter, circlePointY + smallCirclePerimeter,
				smallCirclePerimeter, smallCirclePerimeter);

		// System.out.println(-angle)
		// System.out.println("Angle is:" + ( -this.angle ))
		// System.out.println("FormatedX is: " + previousX + ", FormatedY is: "
		// + previousY)
		// System.out.println("Relative Mouse X: " + m.getX() + " , Relative
		// Mouse Y: " + m.getY())

		// Draw the drag able rectangle
		// gc.setFill(Color.WHITE)
		// gc.fillOval(getWidth() / 2, 0, 20, 20)

		// ----------------------------------------------------------------------------------------------

		// Refresh the timeField
		// timeField.setText(time)

	}

	/**
	 * @return The TimeMode of the TimeLabel
	 */
	public TimeMode getTimeMode() {
		return timeMode;
	}

	/**
	 * Returns a Value based on the angle of the disc and the maximum value allowed.
	 *
	 * @param maximum the maximum
	 * @return Returns a Value based on the angle of the disc and the maximum value
	 *         allowed
	 */
	public int getValue(int maximum) {

		return (-maximum * angle) / 360;
	}

	/**
	 * Returns the maximum volume allowed.
	 *
	 * @return The Maximum Volume allowed in the Disc
	 */
	public int getMaximumVolume() {

		return maximumVolume;
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
	 * @return the timeField
	 */
	public Label getTimeField() {
		return timeField;
	}

	/**
	 * Calculates the angle based on the given value and the maximum value allowed.
	 *
	 * @param value         The current moment of the Audio
	 * @param maximum       The maximum duration of the Audio
	 * @param updateTheTime True if you want to update the Time Label
	 */
	public void calculateAngleByValue(int value, int maximum, boolean updateTheTime) {

		// or else i get
		// java.lang.ArithmeticException: / by zero
		if (maximum != 0) {
			// threshold values
			if (value == 0)
				angle = 0;
			else if (value == maximum)
				angle = 360;
			else// calculate
				angle = -((value * 360) / maximum);
		} else
			angle = 0;

		// Update the Time?
		if (updateTheTime)
			calculateTheTime(value, maximum);
	}

	/**
	 * Calculates the time of the disc.
	 *
	 * @param current the current
	 * @param total   the total
	 */
	private void calculateTheTime(int current, int total) {
		if (current == 0 && total == 0)
			time = "00:00";
		else if (timeMode == TimeMode.NORMAL)
			time = TimeTool.getTimeEdited(current);
		else
			time = "-" + TimeTool.getTimeEdited(total - current);
	}

	/**
	 * Update the time of the disc directly using this method
	 * 
	 * @param current
	 * @param total
	 * @param millisecondsFormatted
	 */
	public void updateTimeDirectly(int current, int total, String millisecondsFormatted) {
		calculateTheTime(current, total);
		if (!this.timeField.isHover()) { // Is being hovered
			if (timeMode == TimeMode.REVERSED)
				this.time = time + "." + (9 - Integer.parseInt(millisecondsFormatted.replace(".", "")));
			else
				this.time = time + millisecondsFormatted;
		} else
			this.time = TimeTool.getTimeEdited(total);

		// Final
		// this.time = time + "\n," + InfoTool.getTimeEdited(total)
	}

	/**
	 * Change the volume.
	 *
	 * @param volume the new volume
	 */
	public void setVolume(double volume) {
		if (volume > -1 && volume < maximumVolume)
			this.currentVolume.set((int) Math.ceil(volume));
		else if (volume < 0)
			this.currentVolume.set(0);
		else if (volume >= maximumVolume)
			this.currentVolume.set(maximumVolume);
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
	 * Replace the image of the disc with the given one.
	 *
	 * @param image the image
	 */
	public void replaceImage(Image image) {
		if (image != null)
			imageView.setImage(image);
		else
			imageView.setImage(null);

	}

	/**
	 * The image of the disc
	 * 
	 * @return The image of the disc
	 */
	public Image getImage() {
		return imageView.getImage();
	}

	/**
	 * Resume the rotation of the disc.
	 */
	public void resumeRotation() {
		rotationAnimation.play();
	}

	/**
	 * Pause the rotation of the disc.
	 */
	public void pauseRotation() {
		rotationAnimation.pause();
	}

	/**
	 * Stops the Rotation Animation
	 */
	public void stopRotation() {
		rotationAnimation.jumpTo(Duration.ZERO);
		rotationAnimation.pause();
	}

	/**
	 * Start the fade effect of the disc.
	 */
	public void playFade() {
		fade.play();
	}

	/**
	 * Stop fade effect of the disc.
	 */
	public void stopFade() {
		fade.jumpTo(Duration.ZERO);
		fade.pause();
	}

	/**
	 * Calculate the disc angle based on mouse position.
	 *
	 * @param m       the m
	 * @param current the current
	 * @param total   the total
	 */
	public void calculateAngleByMouse(MouseEvent m, int current, int total) {
		// pauseRotation()
		double mouseX;
		double mouseY;

		if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.SECONDARY) {
			mouseX = m.getX();
			mouseY = m.getY();
			if (mouseX > getWidth() / 2)
				angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, getHeight() / 2 - mouseY));
			else {
				angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, getHeight() / 2 - mouseY));
				angle = -(360 - angle); // make it minus cause i turn it
				// on the right
			}

			// System.out.println(-angle)
			calculateTheTime(current, total);
			// rotationTransform.setAngle(-angle)

			Platform.runLater(this::repaint);
		}
	}

	/**
	 * Check if this point is contained into the circle
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isContainedInCircle(double mouseX, double mouseY) {
		// Check if it is contained into the circle
		if (Math.sqrt(Math.pow(((int) (getWidth() - 5) / 2) - (int) mouseX, 2)
				+ Math.pow(((int) (getHeight() - 5) / 2) - (int) mouseY, 2)) <= Math.floorDiv((int) (getWidth() - 5),
						2)) {
			System.out.println("The point is contained in the circle.");
			return true;
		} else {
			System.out.println("The point is not contained in the circle.");
			return false;

		}
	}

	/**
	 * On mouse dragged.
	 *
	 * @param m the m
	 */
	private void onMouseDragged(MouseEvent m) {
		calculateAngleByMouse(m, 0, 0);
	}

	/**
	 * On scroll.
	 *
	 * @param m the m
	 */
	// private void onScroll(ScrollEvent m) {
	// int rotation = m.getDeltaY() < 1 ? 1 : -1;
	// setVolume(getVolume() - rotation);
	// }
	//
	/**
	 * @return the imageView
	 */
	public ImageView getImageView() {
		return imageView;
	}

	/**
	 * @return the currentVolume
	 */
	public int getCurrentVolume() {
		return currentVolume.get();
	}

	public SimpleIntegerProperty currentVolumeProperty() {
		return currentVolume;
	}

	// /**
	// * @return the volumeLabel
	// */
	// public DragAdjustableLabel getVolumeLabel() {
	// return volumeLabel;
	// }

}
