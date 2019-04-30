package com.goxr3plus.xr3player.controllers.custom;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * User: hansolo Date: 16.04.14 Time: 22:06
 */
public class FlipPanel extends StackPane {
	private StackPane front;
	private StackPane back;
	private Rotate rotate;
	private Rotate backRotate;
	private Timeline flipToFront;
	private Timeline flipToBack;
	private double flipTime;
	private Orientation flipDirection;

	// ******************** Constructors **************************************
	public FlipPanel() {
		this(Orientation.HORIZONTAL);
	}

	public FlipPanel(final Orientation FLIP_DIRECTION) {
		rotate = new Rotate(0, Rotate.Y_AXIS);
		getTransforms().add(rotate);

		backRotate = new Rotate(180, Orientation.HORIZONTAL == FLIP_DIRECTION ? Rotate.Y_AXIS : Rotate.X_AXIS);

		front = new StackPane();
		back = new StackPane();

		back.setVisible(false);

		getChildren().setAll(back, front);

		flipToFront = new Timeline();
		flipToBack = new Timeline();
		flipTime = 700;
		flipDirection = FLIP_DIRECTION;

		registerListeners();
	}

	private void registerListeners() {
		front.widthProperty().addListener(observable -> adjustRotationAxis());
		front.heightProperty().addListener(observable -> adjustRotationAxis());
		back.widthProperty().addListener(observable -> adjustRotationAxis());
		back.heightProperty().addListener(observable -> adjustRotationAxis());
		rotate.angleProperty().addListener((ov, o, n) -> {
			if (Double.compare(o.doubleValue(), 90) < 0 && Double.compare(n.doubleValue(), 90) >= 0) {
				front.setVisible(false);
				back.setVisible(true);
			}
			if (Double.compare(o.doubleValue(), 90) > 0 && Double.compare(n.doubleValue(), 90) <= 0) {
				back.setVisible(false);
				front.setVisible(true);
			}
		});
	}

	// ******************** Methods *******************************************
	public StackPane getFront() {
		return front;
	}

	public StackPane getBack() {
		return back;
	}

	public void flipToFront() {
		if (Double.compare(rotate.getAngle(), 0) == 0)
			return;
		KeyValue kvStart = new KeyValue(rotate.angleProperty(), 180, Interpolator.EASE_IN);
		KeyValue kvStop = new KeyValue(rotate.angleProperty(), 0, Interpolator.EASE_OUT);
		KeyFrame kfStart = new KeyFrame(Duration.ZERO, kvStart);
		KeyFrame kfStop = new KeyFrame(Duration.millis(flipTime), kvStop);
		flipToFront.getKeyFrames().setAll(kfStart, kfStop);

		front.setCache(true);
		front.setCacheHint(CacheHint.ROTATE);
		back.setCache(true);
		back.setCacheHint(CacheHint.ROTATE);

		flipToFront.setOnFinished(event -> {
			front.setCache(false);
			back.setCache(false);
			fireEvent(new FlipEvent(FlipPanel.this, FlipPanel.this, FlipEvent.FLIP_TO_FRONT_FINISHED));
		});
		flipToFront.play();
	}

	public void flipToBack() {
		if (Double.compare(rotate.getAngle(), 180) == 0)
			return;
		KeyValue kvStart = new KeyValue(rotate.angleProperty(), 0, Interpolator.EASE_IN);
		KeyValue kvStop = new KeyValue(rotate.angleProperty(), 180, Interpolator.EASE_OUT);
		KeyFrame kfStart = new KeyFrame(Duration.ZERO, kvStart);
		KeyFrame kfStop = new KeyFrame(Duration.millis(flipTime), kvStop);
		flipToBack.getKeyFrames().setAll(kfStart, kfStop);

		front.setCache(true);
		front.setCacheHint(CacheHint.ROTATE);
		back.setCache(true);
		back.setCacheHint(CacheHint.ROTATE);

		flipToBack.setOnFinished(event -> {
			front.setCache(false);
			back.setCache(false);
			fireEvent(new FlipEvent(FlipPanel.this, FlipPanel.this, FlipEvent.FLIP_TO_BACK_FINISHED));
		});
		flipToBack.play();
	}

	public boolean isFrontVisible() {
		return front.isVisible();
	}

	public boolean isBackVisible() {
		return back.isVisible();
	}

	public void setFlipTime(final double FLIP_TIME) {
		flipTime = clamp(100, 2000, FLIP_TIME);
	}

	private void adjustRotationAxis() {
		if (front.getWidth() < 0 || back.getWidth() < 0 || front.getHeight() < 0 || back.getHeight() < 0)
			return;

		double width = front.getWidth() > back.getWidth() ? front.getWidth() : back.getWidth();
		double height = front.getHeight() > back.getHeight() ? front.getHeight() : back.getHeight();
		setPrefSize(width, height);

		if (Orientation.HORIZONTAL == flipDirection) {
			backRotate.setAngle(0);
			backRotate.setAxis(Rotate.Y_AXIS);
			backRotate.setPivotX(0.5 * width);
			backRotate.setAngle(180);
			back.getTransforms().setAll(backRotate);

			rotate.setAxis(Rotate.Y_AXIS);
			rotate.setPivotX(0.5 * width);
		} else {
			backRotate.setAngle(0);
			backRotate.setAxis(Rotate.X_AXIS);
			backRotate.setPivotY(0.5 * height);
			backRotate.setAngle(180);
			back.getTransforms().setAll(backRotate);

			rotate.setAxis(Rotate.X_AXIS);
			rotate.setPivotY(0.5 * height);
		}
	}

	public Orientation getFlipDirection() {
		return flipDirection;
	}

	public void setFlipDirection(final Orientation FLIP_DIRECTION) {
		if (FLIP_DIRECTION == flipDirection)
			return;
		flipDirection = FLIP_DIRECTION;
		backRotate = new Rotate(180, Orientation.HORIZONTAL == FLIP_DIRECTION ? Rotate.Y_AXIS : Rotate.X_AXIS);
		adjustRotationAxis();
	}

	private double clamp(final double MIN, final double MAX, final double VALUE) {
		if (VALUE < MIN)
			return MIN;
		if (VALUE > MAX)
			return MAX;
		return VALUE;
	}
}
