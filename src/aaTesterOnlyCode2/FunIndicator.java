/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aaTesterOnlyCode2;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * User: hansolo Date: 14.12.16 Time: 08:58
 */
@DefaultProperty("children")
public class FunIndicator extends Region {

    /** The Constant PREFERRED_WIDTH. */
    private static final double PREFERRED_WIDTH = 250;

    /** The Constant PREFERRED_HEIGHT. */
    private static final double PREFERRED_HEIGHT = 250;

    /** The Constant MINIMUM_WIDTH. */
    private static final double MINIMUM_WIDTH = 50;

    /** The Constant MINIMUM_HEIGHT. */
    private static final double MINIMUM_HEIGHT = 50;

    /** The Constant MAXIMUM_WIDTH. */
    private static final double MAXIMUM_WIDTH = 1024;

    /** The Constant MAXIMUM_HEIGHT. */
    private static final double MAXIMUM_HEIGHT = 1024;

    /** The size. */
    private double size;

    /** The arc 7. */
    private Arc arc1, arc2, arc3, arc4, arc5, arc6, arc7;

    /** The rot 7. */
    private Rotate rot1, rot2, rot3, rot4, rot5, rot6, rot7;

    /** The pane. */
    private Pane pane;

    /** The background paint. */
    private Paint backgroundPaint;

    /** The border paint. */
    private Paint borderPaint;

    /** The border width. */
    private double borderWidth;

    /** The from color. */
    private ObjectProperty<Color> fromColor;

    /** The to color. */
    private ObjectProperty<Color> toColor;

    /** The color. */
    private ObjectProperty<Color> color;

    /** The timeline. */
    private Timeline timeline;

    /**
     * Instantiates a new fun indicator.
     */
    // ******************** Constructors **************************************
    public FunIndicator() {
	backgroundPaint = Color.TRANSPARENT;
	borderPaint = Color.TRANSPARENT;
	borderWidth = 0d;
	fromColor = new ObjectPropertyBase<Color>(Color.rgb(255, 166, 9)) {
	    @Override
	    public Object getBean() {
		return FunIndicator.this;
	    }

	    @Override
	    public String getName() {
		return "fromColor";
	    }
	};
	toColor = new ObjectPropertyBase<Color>(Color.rgb(9, 255, 166)) {
	    @Override
	    public Object getBean() {
		return FunIndicator.this;
	    }

	    @Override
	    public String getName() {
		return "toColor";
	    }
	};
	color = new ObjectPropertyBase<Color>() {
	    @Override
	    public Object getBean() {
		return FunIndicator.this;
	    }

	    @Override
	    public String getName() {
		return "color";
	    }
	};
	timeline = new Timeline();
	initGraphics();
	initTimeline();
	registerListeners();
    }

    /**
     * Inits the graphics.
     */
    // ******************** Initialization ************************************
    private void initGraphics() {
	if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0
		|| Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
	    if (getPrefWidth() > 0 && getPrefHeight() > 0) {
		setPrefSize(getPrefWidth(), getPrefHeight());
	    } else {
		setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
	    }
	}

	rot1 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);
	rot2 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);
	rot3 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);
	rot4 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);
	rot5 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);
	rot6 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);
	rot7 = new Rotate(0, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5);

	arc1 = createArc();
	arc1.getTransforms().add(rot1);

	arc2 = createArc();
	arc2.getTransforms().add(rot2);

	arc3 = createArc();
	arc3.getTransforms().add(rot3);

	arc4 = createArc();
	arc4.getTransforms().add(rot4);

	arc5 = createArc();
	arc5.getTransforms().add(rot5);

	arc6 = createArc();
	arc6.getTransforms().add(rot6);

	arc7 = createArc();
	arc7.getTransforms().add(rot7);

	pane = new Pane(arc1, arc2, arc3, arc4, arc5, arc6, arc7);
	pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
	pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		new BorderWidths(borderWidth))));

	getChildren().setAll(pane);
    }

    /**
     * Inits the timeline.
     */
    private void initTimeline() {
	boolean wasRunning = Status.RUNNING == timeline.getStatus();
	timeline.stop();

	KeyValue colorKvBegin = new KeyValue(color, fromColor.get());
	KeyValue colorKvMid = new KeyValue(color, toColor.get());
	KeyValue colorKvEnd = new KeyValue(color, fromColor.get());

	KeyValue arc1KvBegin = new KeyValue(rot1.angleProperty(), 0);
	KeyValue arc1KvEnd = new KeyValue(rot1.angleProperty(), 360);

	KeyValue arc2KvBegin = new KeyValue(rot2.angleProperty(), 0);
	KeyValue arc2KvEnd = new KeyValue(rot2.angleProperty(), 720);

	KeyValue arc3KvBegin = new KeyValue(rot3.angleProperty(), 0);
	KeyValue arc3KvEnd = new KeyValue(rot3.angleProperty(), 1080);

	KeyValue arc4KvBegin = new KeyValue(rot4.angleProperty(), 0);
	KeyValue arc4KvEnd = new KeyValue(rot4.angleProperty(), 1440);

	KeyValue arc5KvBegin = new KeyValue(rot5.angleProperty(), 0);
	KeyValue arc5KvEnd = new KeyValue(rot5.angleProperty(), 1800);

	KeyValue arc6KvBegin = new KeyValue(rot6.angleProperty(), 0);
	KeyValue arc6KvEnd = new KeyValue(rot6.angleProperty(), 2160);

	KeyValue arc7KvBegin = new KeyValue(rot7.angleProperty(), 0);
	KeyValue arc7KvEnd = new KeyValue(rot7.angleProperty(), 2520);

	KeyFrame kf1 = new KeyFrame(Duration.ZERO, colorKvBegin, arc1KvBegin, arc2KvBegin, arc3KvBegin, arc4KvBegin,
		arc5KvBegin, arc6KvBegin, arc7KvBegin);
	KeyFrame kf2 = new KeyFrame(Duration.millis(3000), colorKvMid);
	KeyFrame kf3 = new KeyFrame(Duration.millis(6000), colorKvEnd, arc1KvEnd, arc2KvEnd, arc3KvEnd, arc4KvEnd,
		arc5KvEnd, arc6KvEnd, arc7KvEnd);

	timeline.getKeyFrames().setAll(kf1, kf2, kf3);
	timeline.setCycleCount(Animation.INDEFINITE);

	if (wasRunning)
	    timeline.play();
    }

    /**
     * Register listeners.
     */
    private void registerListeners() {
	widthProperty().addListener(o -> resize());
	heightProperty().addListener(o -> resize());
	fromColorProperty().addListener(o -> initTimeline());
	toColorProperty().addListener(o -> initTimeline());
    }

    /**
     * Gets the from color.
     *
     * @return the from color
     */
    // ******************** Methods *******************************************
    public Color getFromColor() {
	return fromColor.get();
    }

    /**
     * Sets the from color.
     *
     * @param COLOR
     *            the new from color
     */
    public void setFromColor(final Color COLOR) {
	fromColor.set(COLOR);
    }

    /**
     * From color property.
     *
     * @return the object property
     */
    public ObjectProperty<Color> fromColorProperty() {
	return fromColor;
    }

    /**
     * Gets the to color.
     *
     * @return the to color
     */
    public Color getToColor() {
	return toColor.get();
    }

    /**
     * Sets the to color.
     *
     * @param COLOR
     *            the new to color
     */
    public void setToColor(final Color COLOR) {
	toColor.set(COLOR);
    }

    /**
     * To color property.
     *
     * @return the object property
     */
    public ObjectProperty<Color> toColorProperty() {
	return toColor;
    }

    /* (non-Javadoc)
     * @see javafx.scene.Parent#layoutChildren()
     */
    @Override
    public void layoutChildren() {
	super.layoutChildren();
    }

    /* (non-Javadoc)
     * @see javafx.scene.layout.Region#computeMinWidth(double)
     */
    @Override
    protected double computeMinWidth(final double HEIGHT) {
	return MINIMUM_WIDTH;
    }

    /* (non-Javadoc)
     * @see javafx.scene.layout.Region#computeMinHeight(double)
     */
    @Override
    protected double computeMinHeight(final double WIDTH) {
	return MINIMUM_HEIGHT;
    }

    /* (non-Javadoc)
     * @see javafx.scene.layout.Region#computePrefWidth(double)
     */
    @Override
    protected double computePrefWidth(final double HEIGHT) {
	return super.computePrefWidth(HEIGHT);
    }

    /* (non-Javadoc)
     * @see javafx.scene.layout.Region#computePrefHeight(double)
     */
    @Override
    protected double computePrefHeight(final double WIDTH) {
	return super.computePrefHeight(WIDTH);
    }

    /* (non-Javadoc)
     * @see javafx.scene.layout.Region#computeMaxWidth(double)
     */
    @Override
    protected double computeMaxWidth(final double HEIGHT) {
	return MAXIMUM_WIDTH;
    }

    /* (non-Javadoc)
     * @see javafx.scene.layout.Region#computeMaxHeight(double)
     */
    @Override
    protected double computeMaxHeight(final double WIDTH) {
	return MAXIMUM_HEIGHT;
    }

    /* (non-Javadoc)
     * @see javafx.scene.Parent#getChildren()
     */
    @Override
    public ObservableList<Node> getChildren() {
	return super.getChildren();
    }

    /**
     * Start.
     */
    public void start() {
	timeline.play();
    }

    /**
     * Stop.
     */
    public void stop() {
	timeline.stop();
    }

    /**
     * Pause.
     */
    public void pause() {
	timeline.pause();
    }

    /**
     * Creates the arc.
     *
     * @return the arc
     */
    private Arc createArc() {
	Arc arc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, 0, 00, 50, 80);
	arc.setFill(null);
	arc.strokeProperty().bind(color);
	arc.setStrokeLineCap(StrokeLineCap.ROUND);
	return arc;
    }

    /**
     * Resize arc.
     *
     * @param ARC
     *            the arc
     * @param CENTER
     *            the center
     * @param RADIUS
     *            the radius
     * @param STROKE_WIDTH
     *            the stroke width
     */
    // ******************** Resizing ******************************************
    private void resizeArc(final Arc ARC, final double CENTER, final double RADIUS, final double STROKE_WIDTH) {
	ARC.setCenterX(CENTER);
	ARC.setCenterY(CENTER);
	ARC.setRadiusX(RADIUS);
	ARC.setRadiusY(RADIUS);
	ARC.setStrokeWidth(STROKE_WIDTH);
    }

    /**
     * Resize.
     */
    private void resize() {
	double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
	double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
	size = width < height ? width : height;

	if (width > 0 && height > 0) {
	    pane.setMaxSize(size, size);
	    pane.setPrefSize(size, size);
	    pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

	    double strokeWidth = size * 0.02;
	    double center = size * 0.5;

	    rot1.setPivotX(center);
	    rot1.setPivotY(center);

	    rot2.setPivotX(center);
	    rot2.setPivotY(center);

	    rot3.setPivotX(center);
	    rot3.setPivotY(center);

	    rot4.setPivotX(center);
	    rot4.setPivotY(center);

	    rot5.setPivotX(center);
	    rot5.setPivotY(center);

	    rot6.setPivotX(center);
	    rot6.setPivotY(center);

	    rot7.setPivotX(center);
	    rot7.setPivotY(center);

	    resizeArc(arc1, center, size * 0.45, strokeWidth);
	    resizeArc(arc2, center, size * 0.4125, strokeWidth);
	    resizeArc(arc3, center, size * 0.375, strokeWidth);
	    resizeArc(arc4, center, size * 0.3375, strokeWidth);
	    resizeArc(arc5, center, size * 0.3, strokeWidth);
	    resizeArc(arc6, center, size * 0.2625, strokeWidth);
	    resizeArc(arc7, center, size * 0.225, strokeWidth);

	    redraw();
	}
    }

    /**
     * Redraw.
     */
    private void redraw() {
	pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
	pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));
    }
}