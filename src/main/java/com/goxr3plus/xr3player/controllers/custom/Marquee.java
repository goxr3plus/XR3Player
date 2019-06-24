package com.goxr3plus.xr3player.controllers.custom;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * When the screen element is not big enough to show the text then an animation
 * will start automatically
 * 
 * @author GOXR3PLUS
 *
 */
public class Marquee extends Pane {

	@FXML
	private Label label;

	// minimum distance to Pane bounds
	private static final double OFFSET = 5;

	private Timeline timeline = new Timeline();

	private boolean animationAllowed = true;

	/**
	 * Constructor
	 */
	public Marquee() {

		// FXMLLOADER
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "Marquee.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {

		// Clip
		Rectangle rectangle = new Rectangle(25, 25);
		rectangle.widthProperty().bind(widthProperty());
		rectangle.heightProperty().bind(heightProperty());
		setClip(rectangle);

		// Text
		// text.setManaged(false)

		startAnimation();
	}

	/**
	 * This method changes the text of the Marquee
	 * 
	 * @param value
	 * @return this
	 */
	public Marquee setText(String value) {

		// text
		label.setText(value);

		return this;
	}

	/**
	 * Defines text string that is to be displayed.
	 * 
	 * @return The TextProperty
	 */
	public StringProperty textProperty() {
		return label.textProperty();
	}

	/**
	 * The text of Marquee
	 * 
	 * @return The text of Marquee
	 */
	public String getText() {
		return label.textProperty().get();
	}

	/**
	 * This method starts the Animation of the marquee
	 */
	private void startAnimation() {

		// KeyFrame
		KeyFrame updateFrame = new KeyFrame(Duration.millis(35), new EventHandler<>() {

			private boolean rightMovement;

			@Override
			public void handle(ActionEvent event) {
				double textWidth = label.getLayoutBounds().getWidth();
				double paneWidth = getWidth();
				double layoutX = label.getLayoutX();

				if (2 * OFFSET + textWidth <= paneWidth && layoutX >= OFFSET) {
					// stop, if the pane is large enough and the position is
					// correct
					label.setLayoutX(OFFSET);
					timeline.stop();
				} else {
					if ((rightMovement && layoutX >= OFFSET)
							|| (!rightMovement && layoutX + textWidth + OFFSET <= paneWidth)) {
						// invert movement, if bounds are reached
						rightMovement = !rightMovement;
					}

					// update position
					if (rightMovement) {
						layoutX += 1;
					} else {
						layoutX -= 1;
					}
					label.setLayoutX(layoutX);
				}
			}
		});
		timeline.getKeyFrames().add(updateFrame);
		timeline.setCycleCount(Animation.INDEFINITE);

		// listen to bound changes of the elements to start/stop the
		// animation
		InvalidationListener listener = o -> checkAnimationValidity(animationAllowed);

		label.layoutBoundsProperty().addListener(listener);
		widthProperty().addListener(listener);

	}

	/**
	 * Starts or stops the animation based on the given boolean
	 */
	public void checkAnimationValidity(boolean continueAnimation) {
		animationAllowed = continueAnimation;
		if (animationAllowed) {
			double textWidth = label.getLayoutBounds().getWidth();
			double paneWidth = getWidth();
			label.setLayoutX(5);
			if (textWidth + 2 * OFFSET > paneWidth && timeline.getStatus() != Animation.Status.RUNNING)
				timeline.play();
		} else {
			label.setLayoutX(OFFSET);
			timeline.stop();
		}
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}

}
