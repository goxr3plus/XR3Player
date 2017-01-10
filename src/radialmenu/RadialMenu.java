/**
 * RadialMenu.java
 *
 * Copyright (c) 2011-2015, JFXtras
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package radialmenu;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc
/**
 * The Class RadialMenu.
 */
public class RadialMenu extends Group implements EventHandler<MouseEvent>, ChangeListener<Object> {

	/**
	 * The Enum CenterVisibility.
	 *
	 * @author SuperGoliath
	 */
	public enum CenterVisibility {
		
		/** The always. */
		ALWAYS, 
 /** The with menu. */
 WITH_MENU, 
 /** The never. */
 NEVER
	}

	/** The items. */
	protected List<RadialMenuItem> items = new ArrayList<>();
	
	/** The inner radius. */
	protected DoubleProperty innerRadius;
	
	/** The radius. */
	protected DoubleProperty radius;
	
	/** The offset. */
	protected DoubleProperty offset;
	
	/** The initial angle. */
	protected DoubleProperty initialAngle;
	
	/** The background fill. */
	protected ObjectProperty<Paint> backgroundFill;
	
	/** The background mouse on fill. */
	protected ObjectProperty<Paint> backgroundMouseOnFill;
	
	/** The stroke mouse on fill. */
	protected ObjectProperty<Paint> strokeMouseOnFill;
	
	/** The stroke fill. */
	protected ObjectProperty<Paint> strokeFill;
	
	/** The clockwise. */
	protected BooleanProperty clockwise;
	
	/** The background visible. */
	protected BooleanProperty backgroundVisible;
	
	/** The stroke visible. */
	protected BooleanProperty strokeVisible;
	
	/** The center visibility. */
	protected ObjectProperty<CenterVisibility> centerVisibility;
	
	/** The center graphic. */
	protected ObjectProperty<Node> centerGraphic;
	
	/** The center stroke shape. */
	protected Circle centerStrokeShape;
	
	/** The center group. */
	protected Group centerGroup;
	
	/** The item group. */
	protected Group itemGroup;
	
	/** The mouse on. */
	private boolean mouseOn = false;
	
	/** The last initial angle value. */
	private double lastInitialAngleValue;
	
	/** The last offset value. */
	private double lastOffsetValue;

	/**
	 * Gets the background fill.
	 *
	 * @return the background fill
	 */
	public Paint getBackgroundFill() {
		return backgroundFill.get();
	}

	/**
	 * Sets the background fill.
	 *
	 * @param backgroundFill the new background fill
	 */
	public void setBackgroundFill(final Paint backgroundFill) {
		this.backgroundFill.set(backgroundFill);
	}

	/**
	 * Background fill property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Paint> backgroundFillProperty() {
		return backgroundFill;
	}

	/**
	 * Gets the background mouse on fill.
	 *
	 * @return the background mouse on fill
	 */
	public Paint getBackgroundMouseOnFill() {
		return backgroundMouseOnFill.get();
	}

	/**
	 * Sets the background mouse on fill.
	 *
	 * @param backgroundMouseOnFill the new background mouse on fill
	 */
	public void setBackgroundMouseOnFill(final Paint backgroundMouseOnFill) {
		this.backgroundMouseOnFill.set(backgroundMouseOnFill);
	}

	/**
	 * Background mouse on fill property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Paint> backgroundMouseOnFillProperty() {
		return backgroundMouseOnFill;
	}

	/**
	 * Gets the stroke mouse on fill.
	 *
	 * @return the stroke mouse on fill
	 */
	public Paint getStrokeMouseOnFill() {
		return strokeMouseOnFill.get();
	}

	/**
	 * Sets the stroke mouse on fill.
	 *
	 * @param backgroundMouseOnFill the new stroke mouse on fill
	 */
	public void setStrokeMouseOnFill(final Paint backgroundMouseOnFill) {
		strokeMouseOnFill.set(backgroundMouseOnFill);
	}

	/**
	 * Stroke mouse on fill property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Paint> strokeMouseOnFillProperty() {
		return strokeMouseOnFill;
	}

	/**
	 * Gets the stroke fill.
	 *
	 * @return the stroke fill
	 */
	public Paint getStrokeFill() {
		return strokeFill.get();
	}

	/**
	 * Sets the stroke fill.
	 *
	 * @param strokeFill the new stroke fill
	 */
	public void setStrokeFill(final Paint strokeFill) {
		this.strokeFill.set(strokeFill);
	}

	/**
	 * Stroke fill property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Paint> strokeFillProperty() {
		return strokeFill;
	}

	/**
	 * Gets the center graphic.
	 *
	 * @return the center graphic
	 */
	public Node getCenterGraphic() {
		return centerGraphic.get();
	}

	/**
	 * Sets the center graphic.
	 *
	 * @param graphic the new center graphic
	 */
	public void setCenterGraphic(final Node graphic) {
		if (centerGraphic.get() != null) {
			centerGroup.getChildren().remove(centerGraphic.get());
		}
		if (graphic != null) {
			centerGroup.getChildren().add(graphic);
		}
		centerGraphic.set(graphic);
	}

	/**
	 * Center graphic property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Node> centerGraphicProperty() {
		return centerGraphic;
	}

	/**
	 * Gets the initial angle.
	 *
	 * @return the initial angle
	 */
	public double getInitialAngle() {
		return initialAngle.get();
	}

	/**
	 * Initial angle property.
	 *
	 * @return the double property
	 */
	public DoubleProperty initialAngleProperty() {
		return initialAngle;
	}

	/**
	 * Gets the inner radius.
	 *
	 * @return the inner radius
	 */
	public double getInnerRadius() {
		return innerRadius.get();
	}

	/**
	 * Inner radius property.
	 *
	 * @return the double property
	 */
	public DoubleProperty innerRadiusProperty() {
		return innerRadius;
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public double getRadius() {
		return radius.get();
	}

	/**
	 * Radius property.
	 *
	 * @return the double property
	 */
	public DoubleProperty radiusProperty() {
		return radius;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public double getOffset() {
		return offset.get();
	}

	/**
	 * Offset property.
	 *
	 * @return the double property
	 */
	public DoubleProperty offsetProperty() {
		return offset;
	}

	/**
	 * Checks if is clockwise.
	 *
	 * @return true, if is clockwise
	 */
	public boolean isClockwise() {
		return clockwise.get();
	}

	/**
	 * Clockwise property.
	 *
	 * @return the boolean property
	 */
	public BooleanProperty clockwiseProperty() {
		return clockwise;
	}

	/**
	 * Checks if is background visible.
	 *
	 * @return true, if is background visible
	 */
	public boolean isBackgroundVisible() {
		return backgroundVisible.get();
	}

	/**
	 * Background visible property.
	 *
	 * @return the boolean property
	 */
	public BooleanProperty backgroundVisibleProperty() {
		return backgroundVisible;
	}

	/**
	 * Stroke visible property.
	 *
	 * @return the boolean property
	 */
	public BooleanProperty strokeVisibleProperty() {
		return strokeVisible;
	}

	/**
	 * Checks if is stroke visible.
	 *
	 * @return true, if is stroke visible
	 */
	public boolean isStrokeVisible() {
		return strokeVisible.get();
	}

	/**
	 * Center visibility property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<CenterVisibility> centerVisibilityProperty() {
		return centerVisibility;
	}

	/**
	 * Gets the center visibility.
	 *
	 * @return the center visibility
	 */
	public CenterVisibility getCenterVisibility() {
		return centerVisibility.get();
	}

	/**
	 * Sets the center visibility.
	 *
	 * @param visibility the new center visibility
	 */
	public void setCenterVisibility(final CenterVisibility visibility) {
		centerVisibility.set(visibility);
	}

	/**
	 * Constructor.
	 *
	 * @param initialAngle the initial angle
	 * @param innerRadius the inner radius
	 * @param radius the radius
	 * @param offset the offset
	 * @param bgFill the bg fill
	 * @param bgMouseOnFill the bg mouse on fill
	 * @param strokeFill the stroke fill
	 * @param strokeMouseOnFill the stroke mouse on fill
	 * @param clockwise the clockwise
	 * @param centerVisibility the center visibility
	 * @param centerGraphic the center graphic
	 */
	public RadialMenu(final double initialAngle, final double innerRadius, final double radius, final double offset,
			final Paint bgFill, final Paint bgMouseOnFill, final Paint strokeFill, final Paint strokeMouseOnFill,
			final boolean clockwise, final CenterVisibility centerVisibility, final Node centerGraphic) {
		itemGroup = new Group();
		getChildren().add(itemGroup);

		this.initialAngle = new SimpleDoubleProperty(initialAngle);
		this.initialAngle
				.addListener((observable, oldValue, newVaule) -> setInitialAngle(observable.getValue().doubleValue()));

		this.innerRadius = new SimpleDoubleProperty(innerRadius);
		this.strokeFill = new SimpleObjectProperty<>(strokeFill);
		this.strokeFill.addListener(this);

		this.radius = new SimpleDoubleProperty(radius);
		this.offset = new SimpleDoubleProperty(offset);
		this.clockwise = new SimpleBooleanProperty(clockwise);
		backgroundFill = new SimpleObjectProperty<>(bgFill);
		backgroundFill.addListener(this);
		backgroundMouseOnFill = new SimpleObjectProperty<>(bgMouseOnFill);
		backgroundMouseOnFill.addListener(this);
		this.strokeMouseOnFill = new SimpleObjectProperty<>(strokeMouseOnFill);
		this.strokeMouseOnFill.addListener(this);
		strokeVisible = new SimpleBooleanProperty(true);
		backgroundVisible = new SimpleBooleanProperty(true);

		this.centerVisibility = new SimpleObjectProperty<>(centerVisibility);

		centerStrokeShape = new Circle(innerRadius, bgFill);
		centerStrokeShape.setStroke(strokeFill);
		centerStrokeShape.radiusProperty().bind(innerRadiusProperty());
		this.centerVisibility.addListener(this);

		strokeVisible.addListener(this);
		backgroundVisible.addListener(this);

		centerGroup = new Group();

		centerGroup.setOnMouseEntered(m -> {
			mouseOn = true;
			redraw();
		});

		centerGroup.setOnMouseExited(e -> {
			mouseOn = false;
			redraw();
		});

		centerGroup.setOnMouseClicked(clicked -> {
			if (itemGroup.isVisible())
				hideRadialMenu();
			else
				showRadialMenu();

			clicked.consume();
		});

		centerGroup.getChildren().add(centerStrokeShape);

		getChildren().add(centerGroup);
		this.centerGraphic = new SimpleObjectProperty<>(centerGraphic);
		setCenterGraphic(centerGraphic);

		saveStateBeforeAnimation();
	}

	/**
	 * Sets the on menu item mouse clicked.
	 *
	 * @param paramEventHandler the new on menu item mouse clicked
	 */
	public void setOnMenuItemMouseClicked(final EventHandler<? super MouseEvent> paramEventHandler) {
		for (final RadialMenuItem item : items) {
			item.setOnMouseClicked(paramEventHandler);
		}
	}

	/**
	 * Sets the initial angle.
	 *
	 * @param angle the new initial angle
	 */
	public void setInitialAngle(final double angle) {
		initialAngle.set(angle);

		double angleOffset = initialAngle.get();
		for (final RadialMenuItem item : items) {
			item.setStartAngle(angleOffset);
			angleOffset = angleOffset + item.getMenuSize();
		}
	}

	/**
	 * Sets the inner radius.
	 *
	 * @param radius the new inner radius
	 */
	public void setInnerRadius(final double radius) {
		innerRadius.set(radius);

	}

	/**
	 * Sets the radius.
	 *
	 * @param radius the new radius
	 */
	public void setRadius(final double radius) {
		this.radius.set(radius);

	}

	/**
	 * Sets the offset.
	 *
	 * @param offset the new offset
	 */
	public void setOffset(final double offset) {
		this.offset.set(offset);
	}

	/**
	 * Sets the background visible.
	 *
	 * @param visible the new background visible
	 */
	public void setBackgroundVisible(final boolean visible) {
		backgroundVisible.set(visible);

	}

	/**
	 * Sets the stroke visible.
	 *
	 * @param visible the new stroke visible
	 */
	public void setStrokeVisible(final boolean visible) {
		strokeVisible.set(visible);

	}

	/**
	 * Sets the background color.
	 *
	 * @param color the new background color
	 */
	public void setBackgroundColor(final Paint color) {
		backgroundFill.set(color);
	}

	/**
	 * Sets the background mouse on color.
	 *
	 * @param color the new background mouse on color
	 */
	public void setBackgroundMouseOnColor(final Paint color) {
		backgroundMouseOnFill.set(color);
	}

	/**
	 * Sets the stroke mouse on color.
	 *
	 * @param color the new stroke mouse on color
	 */
	public void setStrokeMouseOnColor(final Paint color) {
		strokeMouseOnFill.set(color);
	}

	/**
	 * Sets the stroke color.
	 *
	 * @param color the new stroke color
	 */
	public void setStrokeColor(final Paint color) {
		strokeFill.set(color);
	}

	/**
	 * Sets the clockwise.
	 *
	 * @param clockwise the new clockwise
	 */
	public void setClockwise(final boolean clockwise) {
		this.clockwise.set(clockwise);
	}

	/**
	 * Adds a new MenuItem to the RadialMenu.
	 *
	 * @param item the item
	 */
	public void addMenuItem(final RadialMenuItem item) {
		item.visibleProperty().bind(visibleProperty());
		item.backgroundColorProperty().bind(backgroundFill);
		item.backgroundMouseOnColorProperty().bind(backgroundMouseOnFill);
		item.strokeMouseOnColorProperty().bind(strokeMouseOnFill);
		item.innerRadiusProperty().bind(innerRadius);
		item.radiusProperty().bind(radius);
		item.offsetProperty().bind(offset);
		item.strokeColorProperty().bind(strokeFill);
		item.clockwiseProperty().bind(clockwise);
		item.backgroundVisibleProperty().bind(backgroundVisible);
		item.strokeVisibleProperty().bind(strokeVisible);
		items.add(item);
		itemGroup.getChildren().add(itemGroup.getChildren().size(), item);
		double angleOffset = initialAngle.get();
		for (final RadialMenuItem it : items) {
			it.setStartAngle(angleOffset);
			angleOffset = angleOffset + item.getMenuSize();
		}
		item.setOnMouseClicked(this);
	}

	/**
	 * Removes the menu item.
	 *
	 * @param item the item
	 */
	public void removeMenuItem(final RadialMenuItem item) {
		items.remove(item);
		itemGroup.getChildren().remove(item);
		item.visibleProperty().unbind();
		item.backgroundColorProperty().unbind();
		item.backgroundMouseOnColorProperty().unbind();
		item.innerRadiusProperty().unbind();
		item.radiusProperty().unbind();
		item.offsetProperty().unbind();
		item.strokeColorProperty().unbind();
		item.clockwiseProperty().unbind();
		item.backgroundVisibleProperty().unbind();
		item.strokeVisibleProperty().unbind();
		item.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
	}

	/**
	 * Removes the menu item.
	 *
	 * @param itemIndex the item index
	 */
	public void removeMenuItem(final int itemIndex) {
		final RadialMenuItem item = items.get(itemIndex);
		removeMenuItem(item);
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(final MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			final RadialMenuItem item = (RadialMenuItem) event.getSource();
			item.setSelected(!item.isSelected());
			for (final RadialMenuItem it : items) {
				if (it != item) {
					it.setSelected(false);
				}
			}
			if (!item.isSelected()) {
				hideRadialMenu();
			}
			event.consume();
		}
	}

	/**
	 * Hides the RadialMenu.
	 */
	public void hideRadialMenu() {
		saveStateBeforeAnimation();

		final List<Animation> anim = new ArrayList<>();

		final FadeTransition fadeItemGroup = new FadeTransition(Duration.millis(300), itemGroup);
		fadeItemGroup.setFromValue(1);
		fadeItemGroup.setToValue(0);
		fadeItemGroup.setOnFinished(f -> {
			itemGroup.setVisible(false);
		});
		anim.add(fadeItemGroup);

		if (centerVisibility.get() == CenterVisibility.WITH_MENU) {
			final FadeTransition fadeCenter = new FadeTransition(Duration.millis(300), centerGroup);
			fadeCenter.setFromValue(1);
			fadeCenter.setToValue(0);
			fadeCenter.setOnFinished(f -> {
				centerGroup.setVisible(false);
			});
			anim.add(fadeCenter);
		}

		final ParallelTransition transition = new ParallelTransition();
		transition.getChildren().addAll(anim);

		transition.play();
	}

	/**
	 * Shows the RadialMenu.
	 */
	public void showRadialMenu() {
		final List<Animation> anim = new ArrayList<>();

		final FadeTransition fade = new FadeTransition(Duration.millis(400), itemGroup);
		fade.setFromValue(0);
		fade.setToValue(1.0);
		anim.add(fade);

		final Animation offset = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(offsetProperty(), 0)),
				new KeyFrame(Duration.millis(300), new KeyValue(offsetProperty(), lastOffsetValue)));
		anim.add(offset);

		final Animation angle = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(initialAngleProperty(), lastInitialAngleValue + 20)),
				new KeyFrame(Duration.millis(300), new KeyValue(initialAngleProperty(), lastInitialAngleValue)));
		anim.add(angle);

		if (centerVisibility.get() == CenterVisibility.WITH_MENU) {
			final FadeTransition fadeCenter = new FadeTransition(Duration.millis(300), centerGroup);
			fadeCenter.setFromValue(0);
			fadeCenter.setToValue(1);
			anim.add(fadeCenter);

			// final Animation radius = new Timeline(new KeyFrame(Duration.ZERO,
			// new KeyValue(innerRadiusProperty(), 0)), new KeyFrame(
			// Duration.millis(300), new KeyValue(
			// innerRadiusProperty(),
			// lastInnerRadiusValue)));
			// anim.add(radius);

			centerGroup.setVisible(true);
		}

		final ParallelTransition transition = new ParallelTransition();
		transition.getChildren().addAll(anim);

		itemGroup.setVisible(true);
		transition.play();
	}

	/**
	 * Save state before animation.
	 */
	private void saveStateBeforeAnimation() {
		lastInitialAngleValue = initialAngle.get();
		lastOffsetValue = offset.get();
	}

	/* (non-Javadoc)
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(final ObservableValue<? extends Object> arg0, final Object arg1, final Object arg2) {
		redraw();
	}

	/**
	 * Redraws the RadialMenu.
	 */
	private void redraw() {
		if (centerVisibility.get() == CenterVisibility.NEVER) {
			centerGroup.visibleProperty().set(false);
		} else if (centerVisibility.get() == CenterVisibility.ALWAYS) {
			centerGroup.visibleProperty().set(true);
		} else {
			centerGroup.visibleProperty().set(itemGroup.isVisible());
		}

		centerStrokeShape.setFill(backgroundVisible.get()
				? (mouseOn && backgroundMouseOnFill.get() != null ? backgroundMouseOnFill.get() : backgroundFill.get())
				: Color.TRANSPARENT);
		centerStrokeShape.setStroke(strokeVisible.get()
				? (mouseOn && strokeMouseOnFill.get() != null ? strokeMouseOnFill.get() : strokeFill.get())
				: Color.TRANSPARENT);
	}
}
