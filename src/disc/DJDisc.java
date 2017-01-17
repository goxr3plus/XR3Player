/*
 * 
 */
package disc;

import java.util.ArrayList;

import application.Main;
import customNodes.DragAdjustableLabel;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Represents a disc controller.
 *
 * @author GOXR3PLUS
 */
public class DJDisc extends StackPane {
	
	/** The Constant NULL_IMAGE. */
	private static final Image NULL_IMAGE = new Image(DJDisc.class.getResourceAsStream("noImage.png"));
	
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
	// variables
	private int angle;
	
	/** The Constant MAXIMUM_VOLUME. */
	// About the Volume
	private static final int MAXIMUM_VOLUME = 100;
	
	/** The time. */
	// About the Time
	private String time = "00:00";
	
	/** The time mode. */
	private TimeMode timeMode = TimeMode.NORMAL;
	
	/** The time field. */
	private final Label timeField = new Label(time);
	
	/** The canvas. */
	// Canvas
	private final Canvas canvas = new Canvas();
	
	/** The gc. */
	private final GraphicsContext gc = canvas.getGraphicsContext2D();
	
	/** The rotation animation. */
	// Animation
	private final Timeline rotationAnimation = new Timeline();
	
	/** The fade. */
	private FadeTransition fade;
	
	/** The image view. */
	private final ImageView imageView = new ImageView();
	
	/** The volume label. */
	DragAdjustableLabel volumeLabel;
	
	/**
	 * Constructor.
	 *
	 * @param width the width
	 * @param height the height
	 * @param arcColor the arc color
	 * @param volume the volume
	 */
	public DJDisc(int width, int height, Color arcColor, int volume) {
		
		// StackPane
		canvas.setCursor(Cursor.OPEN_HAND);
		// setStyle("-fx-background-color:green;");
		
		this.arcColor = arcColor;
		canvas.setEffect(new DropShadow(10, Color.WHITE));
		
		// imageView
		replaceImage(null);
		
		// timeField
		timeField.setStyle(
		        "-fx-background-color:white; -fx-padding:-2 8 -2 8; -fx-background-radius: 15; -fx-font-weight:bold; -fx-font-size:15; -fx-text-fill:black; -fx-cursor:hand;");
		timeField.setOnMouseClicked(c -> {
			if (timeMode == TimeMode.NORMAL)
				timeMode = TimeMode.REVERSED;
			else
				timeMode = TimeMode.NORMAL;
		});
		
		// volumeLabel
		volumeLabel = new DragAdjustableLabel(volume, 0, MAXIMUM_VOLUME);
		// volumeLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/style/Younger
		// than me Bold.ttf"), 18))
		volumeLabel.currentValueProperty().addListener(
		        (observable , oldValue , newValue) -> listeners.forEach(l -> l.volumeChanged(newValue.intValue())));
		
		// Fade animation for centerDisc
		fade = new FadeTransition(new Duration(1000), canvas);
		fade.setFromValue(1.0);
		fade.setToValue(0.2);
		fade.setAutoReverse(true);
		fade.setCycleCount(Animation.INDEFINITE);
		// fade.play()
		
		// rotation transform starting at 0 degrees, rotating about pivot point
		Rotate rotationTransf = new Rotate(0, width / 2.00 - 10, height / 2.00 - 10);
		imageView.getTransforms().add(rotationTransf);
		
		// rotate a square using time line attached to the rotation transform's
		// angle property.
		rotationAnimation.getKeyFrames()
		        .add(new KeyFrame(Duration.millis(2100), new KeyValue(rotationTransf.angleProperty(), 360)));
		rotationAnimation.setCycleCount(Animation.INDEFINITE);
		// rotationAnimation.play()
		
		// When no album image exists this Label is shown
		Label noAlbumImageLabel = new Label("No Album Image");
		noAlbumImageLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");
		noAlbumImageLabel.visibleProperty().bind(imageView.imageProperty().isEqualTo(NULL_IMAGE));
		
		// Rectangle rect = new Rectangle(0,0, 10, 10);
		// rect.setArcHeight(50);
		// rect.setArcWidth(50);
		// rect.setFill(Color.VIOLET);
		//
		// Path path = new Path();
		//
		// MoveTo moveTo = new MoveTo();
		// moveTo.setX(0.0);
		// moveTo.setY(0.0);
		//
		// ArcTo arcTo = new ArcTo();
		// arcTo.setX(50);
		// arcTo.setY(50);
		// arcTo.setRadiusX(360);
		// arcTo.setRadiusY(360);
		//
		// path.getElements().add(moveTo);
		// path.getElements().add(arcTo);
		//
		// PathTransition pathT = new PathTransition();
		// pathT.setNode(rect);
		// pathT.setPath(path);
		// pathT.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
		// pathT.setDuration(Duration.millis(500));
		// pathT.setAutoReverse(true);
		// pathT.setCycleCount(Animation.INDEFINITE);
		// pathT.play();
		// rect.setTranslateY(-height/2);
		
		getChildren().addAll(canvas, imageView, noAlbumImageLabel, volumeLabel, timeField);
		
		// MouseListeners
		canvas.setOnMousePressed(m -> canvas.setCursor(Cursor.CLOSED_HAND));
		canvas.setOnMouseDragged(this::onMouseDragged);
		setOnScroll(this::onScroll);
		
		resizeDisc(width, height);
		repaint();
		repaint();
		repaint();
		repaint();
	}
	
	/**
	 * Register a new DJDiscListener.
	 *
	 * @param listener the listener
	 */
	public void addDJDiscListener(DJDiscListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Resizes the disc to the given values.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void resizeDisc(int width , int height) {
		if (width == height)
			if ( ( width >= 80 && height >= 80 ) && ( width % 2 == 0 && height % 2 == 0 )) {
				
				double halfWidth = width / 2.00;
				double halfHeight = height / 2.00;
				
				// {Maximum,Preferred} Size
				setMaxSize(width, height);
				setPrefSize(width, height);
				canvas.setWidth(width);
				canvas.setHeight(height);
				canvas.setClip(new Circle(halfWidth, halfHeight, halfWidth));
				
				// ImageView
				imageView.setTranslateX(5);
				imageView.setTranslateY(5);
				imageView.setFitWidth(width - 10.00);
				imageView.setFitHeight(height - 10.00);
				imageView.setSmooth(true);
				imageView.setPreserveRatio(false);
				imageView.setClip(new Circle(halfWidth - 10, halfHeight - 10.00, halfWidth - 10.00));
				
				// timeField
				timeField.setTranslateY(-height * 26 / 100.00);
				
				// volumeField
				volumeLabel.setTranslateY(+height * 26 / 100.00);
				
				repaint();
			} else {
				Main.logger.info("DJDisc resizing failed..");
			}
	}
	
	/**
	 * Repaints the disc.
	 */
	public void repaint() {
		
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		// Arc Background Oval
		gc.setLineWidth(7);
		gc.setStroke(Color.WHITE);
		gc.strokeArc(5, 5, getWidth() - 10, getHeight() - 10, 90, 360.00 + angle, ArcType.OPEN);
		
		// Foreground Arc
		gc.setStroke(arcColor);
		gc.strokeArc(5, 5, getWidth() - 10, getHeight() - 10, 90, angle, ArcType.OPEN);
		
		// --------------------------Maths to find the point on the circle
		// circumference
		// draw the progress oval
		// if (m != null) {
		
		// here i add + 89 to the angle cause the Java has where i have 0
		// degrees the 90 degrees.
		// I am counting the 0 degrees from the top center of the circle and
		// Java calculates them from the right mid of the circle and it is
		// going left , i calculate them clock wise
		int angle2 = this.angle + 89;
		int minus = 8;
		
		// Find the point on the circle circumference
		previousX = Math.round( ( (int) ( getWidth() - minus ) ) / 2
		        + Math.cos(Math.toRadians(-angle2)) * ( (int) ( getWidth() - minus ) / 2 ));
		previousY = Math.round( ( (int) ( getHeight() - minus ) ) / 2
		        + Math.sin(Math.toRadians(-angle2)) * ( (int) ( getHeight() - minus ) / 2 ));
		
		int ovalWidth = 11;
		int ovalHeight = 11;
		
		// fix the circle position
		if (-angle > 0 && -angle <= 90) {
			previousX = previousX - ovalWidth / 2 + 1;
			previousY = previousY - 1;
		} else if (-angle > 90 && -angle <= 180) {
			previousX = previousX - ovalWidth / 2 + 4;
			previousY = previousY - ovalWidth / 2 + 2;
		} else if (-angle > 180 && -angle < 270) {
			previousX = previousX - 2;
			previousY = previousY - ovalWidth / 2 + 2;
		} else if (-angle >= 270) {
			//previousX = previousX
			// previousY = previousY - 7
		}
		
		gc.setLineWidth(2);
		gc.setStroke(Color.BLACK);
		gc.strokeOval(previousX, previousY, ovalWidth, ovalHeight);
		
		gc.setFill(Color.MEDIUMVIOLETRED);
		gc.fillOval(previousX, previousY, ovalWidth, ovalHeight);
		
		// System.out.println("Angle is:" + ( -this.angle ))
		// System.out.println("FormatedX is: " + previousX + ", FormatedY is: "
		// + previousY)
		// System.out.println("Relative Mouse X: " + m.getX() + " , Relative
		// Mouse Y: " + m.getY())
		// }
		// Draw the drag able rectangle
		// gc.setFill(Color.WHITE)
		// gc.fillOval(getWidth() / 2, 0, 20, 20)
		
		// ----------------------------------------------------------------------------------------------
		
		// Repaint the TimeField
		// System.out.println(time)
		timeField.setText(time);
		
	}
	
	/**
	 * Returns a Value based on the angle of the disc and the maximum value
	 * allowed.
	 *
	 * @param maximum the maximum
	 * @return Returns a Value based on the angle of the disc and the maximum
	 *         value
	 *         allowed
	 */
	public int getValue(int maximum) {
		
		return ( maximum * -angle ) / 360;
	}
	
	/**
	 * Returns the volume of the disc.
	 *
	 * @return The Current Volume Value
	 */
	public int getVolume() {
		
		return volumeLabel.getCurrentValue();
	}
	
	/**
	 * Returns the maximum volume allowed.
	 *
	 * @return The Maximum Volume allowed in the Disc
	 */
	public int getMaximumVolume() {
		
		return MAXIMUM_VOLUME;
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
	 * Returns the canvas of the disc.
	 *
	 * @return The Canvas of the Disc
	 */
	public Canvas getCanvas() {
		return canvas;
	}
	
	/**
	 * Calculates the angle based on the given value and the maximum value
	 * allowed.
	 *
	 * @param value the value
	 * @param maximum the maximum
	 */
	public void calculateAngleByValue(int value , int maximum) {
		
		// or else i get
		// java.lang.ArithmeticException: / by zero
		if (maximum != 0) {
			// threshold values
			if (value == 0)
				angle = 0;
			else if (value == maximum)
				angle = 360;
			else// calculate
				angle = - ( ( value * 360 ) / maximum );
		} else
			angle = 0;
		
		calculateTheTime(value, maximum);
	}
	
	/**
	 * Calculates the time of the disc.
	 *
	 * @param current the current
	 * @param total the total
	 */
	private void calculateTheTime(int current , int total) {
		if (current == 0 && total == 0)
			time = "";
		else if (timeMode == TimeMode.NORMAL)
			time = getTimeEdited(current);
		else
			time = "-" + getTimeEdited(total - current);
	}
	
	/**
	 * * Return seconds in format HH:MM:SS or MM:SS *.
	 *
	 * @param seconds the seconds
	 * @return the time edited
	 */
	protected String getTimeEdited(int seconds) {
		
		// Is more than one hour>60
		if (seconds / 60 > 60)
			return String.format("%02d:%02d:%02d", ( seconds / 60 ) / 60, ( seconds / 60 ) % 60, seconds % 60);
		// Is less than one hour<60
		else
			return String.format("%02d:%02d", ( seconds / 60 ) % 60, seconds % 60);
		
	}
	
	/**
	 * Change the volume.
	 *
	 * @param volume the new volume
	 */
	public void setVolume(int volume) {
		if (volume > -1 && volume < getMaximumVolume() + 1)
			Platform.runLater(() -> volumeLabel.setCurrentValue(volume));
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
			imageView.setImage(NULL_IMAGE);
		
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
	 * @param m the m
	 * @param current the current
	 * @param total the total
	 */
	public void calculateAngleByMouse(MouseEvent m , int current , int total) {
		// pauseRotation()
		double mouseX;
		double mouseY;
		this.m = m;
		
		if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.SECONDARY) {
			mouseX = m.getX();
			mouseY = m.getY();
			if (mouseX > getWidth() / 2)
				angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, getHeight() / 2 - mouseY));
			else {
				angle = (int) Math.toDegrees(Math.atan2(getWidth() / 2 - mouseX, getHeight() / 2 - mouseY));
				angle = - ( 360 - angle ); // make it minus cause i turn it
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
	private boolean isContainedInCircle(double mouseX , double mouseY) {
		// Check if it is contained into the circle
		if (Math.sqrt(Math.pow( ( (int) ( getWidth() - 5 ) / 2 ) - (int) mouseX, 2)
		        + Math.pow( ( (int) ( getHeight() - 5 ) / 2 ) - (int) mouseY, 2)) <= Math
		                .floorDiv((int) ( getWidth() - 5 ), 2)) {
			System.out.println("The point is contained in the circle.");
			return true;
		} else {
			System.out.println("The point is not contained in the circle.");
			return false;
			
		}
	}
	
	MouseEvent m;
	double previousX;
	double previousY;
	
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
	private void onScroll(ScrollEvent m) {
		int rotation = m.getDeltaY() < 1 ? 1 : -1;
		setVolume(getVolume() - rotation);
	}
	
}
