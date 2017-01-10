/**
 * RadialMenuItem.java
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

// TODO: Auto-generated Javadoc
/**
 * This class is representing a Radial Menu Item.
 *
 * @author SuperGoliath
 */
public class RadialMenuItem extends Group implements ChangeListener<Object> {

	/** The start angle. */
	// Variables
	protected DoubleProperty startAngle = new SimpleDoubleProperty();
	
	/** The inner radius. */
	protected DoubleProperty innerRadius = new SimpleDoubleProperty();
	
	/** The radius. */
	protected DoubleProperty radius = new SimpleDoubleProperty();
	
	/** The offset. */
	protected DoubleProperty offset = new SimpleDoubleProperty();

	/** The background mouse on color. */
	protected ObjectProperty<Paint> backgroundMouseOnColor = new SimpleObjectProperty<>();
	
	/** The background color. */
	protected ObjectProperty<Paint> backgroundColor = new SimpleObjectProperty<>();
	
	/** The stroke color. */
	protected ObjectProperty<Paint> strokeColor = new SimpleObjectProperty<>();
	
	/** The stroke mouse on color. */
	protected ObjectProperty<Paint> strokeMouseOnColor = new SimpleObjectProperty<>();

	/** The background visible. */
	protected BooleanProperty backgroundVisible = new SimpleBooleanProperty(true);
	
	/** The stroke visible. */
	protected BooleanProperty strokeVisible = new SimpleBooleanProperty(true);
	
	/** The clockwise. */
	protected BooleanProperty clockwise = new SimpleBooleanProperty();

	/** The menu size. */
	protected double menuSize;
	
	/** The inner start X. */
	protected double innerStartX;
	
	/** The inner start Y. */
	protected double innerStartY;
	
	/** The inner end X. */
	protected double innerEndX;
	
	/** The inner end Y. */
	protected double innerEndY;
	
	/** The start X. */
	protected double startX;
	
	/** The start Y. */
	protected double startY;
	
	/** The end X. */
	protected double endX;
	
	/** The end Y. */
	protected double endY;
	
	/** The graphic X. */
	protected double graphicX;
	
	/** The graphic Y. */
	protected double graphicY;
	
	/** The translate X. */
	protected double translateX;
	
	/** The translate Y. */
	protected double translateY;

	/** The sweep. */
	protected boolean sweep;
	
	/** The inner sweep. */
	protected boolean innerSweep;
	
	/** The mouse on. */
	protected boolean mouseOn;

	/** The move to. */
	protected MoveTo moveTo = new MoveTo();

	/** The arc to. */
	protected ArcTo arcToInner = new ArcTo(), arcTo = new ArcTo();
	
	/** The line to 2. */
	protected LineTo lineTo = new LineTo(), lineTo2 = new LineTo();

	/** The path. */
	protected Path path = new Path();

	/** The graphic. */
	protected Node graphic;

	/** The text. */
	protected String text;

	/**
	 * Default Constructor.
	 */
	public RadialMenuItem() {
		menuSize = 45;
		innerRadius.addListener(this);
		radius.addListener(this);
		offset.addListener(this);
		backgroundVisible.addListener(this);
		strokeVisible.addListener(this);
		clockwise.addListener(this);
		backgroundColor.addListener(this);
		strokeColor.addListener(this);
		backgroundMouseOnColor.addListener(this);
		strokeMouseOnColor.addListener(this);
		startAngle.addListener(this);

		path.getElements().addAll(moveTo, arcToInner, lineTo, arcTo, lineTo2);
		getChildren().add(path);

		setOnMouseEntered(m -> {
			mouseOn = true;
			redraw();
		});

		setOnMouseExited(m -> {
			mouseOn = false;
			redraw();
		});

	}

	/**
	 * Constructor.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 */
	public RadialMenuItem(final double menuSize, final Node graphic) {
		this();

		this.menuSize = menuSize;
		this.graphic = graphic;
		if (this.graphic != null)
			getChildren().add(this.graphic);

		redraw();
	}

	/**
	 * Constructor.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 * @param actionHandler the action handler
	 */
	public RadialMenuItem(final double menuSize, final Node graphic, final EventHandler<ActionEvent> actionHandler) {
		this(menuSize, graphic);
		addEventHandler(MouseEvent.MOUSE_CLICKED,
				v -> actionHandler.handle(new ActionEvent(v.getSource(), v.getTarget())));
		redraw();
	}

	/**
	 * Constructor.
	 *
	 * @param menuSize the menu size
	 * @param text the text
	 * @param graphic the graphic
	 */
	public RadialMenuItem(final double menuSize, final String text, final Node graphic) {
		this(menuSize, graphic);

		this.text = text;
		redraw();
	}

	/**
	 * Constructor.
	 *
	 * @param menuSize the menu size
	 * @param text the text
	 * @param graphic the graphic
	 * @param actionHandler the action handler
	 */
	public RadialMenuItem(final double menuSize, final String text, final Node graphic,
			final EventHandler<ActionEvent> actionHandler) {

		this(menuSize, graphic, actionHandler);

		this.text = text;
		redraw();
	}

	/**
	 * Inner radius property.
	 *
	 * @return the double property
	 */
	DoubleProperty innerRadiusProperty() {
		return innerRadius;
	}

	/**
	 * Radius property.
	 *
	 * @return the double property
	 */
	DoubleProperty radiusProperty() {
		return radius;
	}

	/**
	 * Offset property.
	 *
	 * @return the double property
	 */
	DoubleProperty offsetProperty() {
		return offset;
	}

	/**
	 * Background mouse on color property.
	 *
	 * @return the object property
	 */
	ObjectProperty<Paint> backgroundMouseOnColorProperty() {
		return backgroundMouseOnColor;
	}

	/**
	 * Stroke mouse on color property.
	 *
	 * @return the object property
	 */
	ObjectProperty<Paint> strokeMouseOnColorProperty() {
		return strokeMouseOnColor;
	}

	/**
	 * Background color property.
	 *
	 * @return the object property
	 */
	ObjectProperty<Paint> backgroundColorProperty() {
		return backgroundColor;
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
	 * Stroke color property.
	 *
	 * @return the object property
	 */
	ObjectProperty<Paint> strokeColorProperty() {
		return strokeColor;
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
	 * Background visible property.
	 *
	 * @return the boolean property
	 */
	public BooleanProperty backgroundVisibleProperty() {
		return backgroundVisible;
	}

	/**
	 * Gets the graphic.
	 *
	 * @return the graphic
	 */
	public Node getGraphic() {
		return graphic;
	}

	/**
	 * Sets the start angle.
	 *
	 * @param angle the new start angle
	 */
	public void setStartAngle(final double angle) {
		startAngle.set(angle);
	}

	/**
	 * Start angle property.
	 *
	 * @return the double property
	 */
	public DoubleProperty startAngleProperty() {
		return startAngle;
	}

	/**
	 * Sets the graphic.
	 *
	 * @param graphic the new graphic
	 */
	public void setGraphic(final Node graphic) {
		if (this.graphic != null)
			getChildren().remove(this.graphic);

		this.graphic = graphic;
		if (this.graphic != null)
			getChildren().add(graphic);

		redraw();
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(final String text) {
		this.text = text;
		redraw();
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Redraw.
	 */
	protected void redraw() {

		path.setFill(backgroundVisible.get() ? (mouseOn && backgroundMouseOnColor.get() != null
				? backgroundMouseOnColor.get() : backgroundColor.get()) : Color.TRANSPARENT);
		path.setStroke(strokeVisible.get()
				? (mouseOn && strokeMouseOnColor.get() != null ? strokeMouseOnColor.get() : strokeColor.get())
				: Color.TRANSPARENT);

		path.setFillRule(FillRule.EVEN_ODD);

		computeCoordinates();

		update();

	}

	/**
	 * Update.
	 */
	protected void update() {
		final double innerRadiusValue = innerRadius.get();
		final double radiusValue = radius.get();

		moveTo.setX(innerStartX + translateX);
		moveTo.setY(innerStartY + translateY);

		arcToInner.setX(innerEndX + translateX);
		arcToInner.setY(innerEndY + this.translateY);
		arcToInner.setSweepFlag(innerSweep);
		arcToInner.setRadiusX(innerRadiusValue);
		arcToInner.setRadiusY(innerRadiusValue);

		lineTo.setX(startX + translateX);
		lineTo.setY(startY + translateY);

		arcTo.setX(endX + translateX);
		arcTo.setY(endY + translateY);
		arcTo.setSweepFlag(sweep);

		arcTo.setRadiusX(radiusValue);
		arcTo.setRadiusY(radiusValue);

		lineTo2.setX(innerStartX + translateX);
		lineTo2.setY(innerStartY + translateY);

		if (graphic != null) {
			graphic.setTranslateX(graphicX + translateX);
			graphic.setTranslateY(graphicY + translateY);
		}

		// translateXProperty().set(translateX);
		// translateYProperty().set(this.translateY);
	}

	/**
	 * Compute coordinates.
	 */
	protected void computeCoordinates() {
		final double innerRadiusValue = this.innerRadius.get();
		final double startAngleValue = this.startAngle.get();

		final double graphicAngle = startAngleValue + (this.menuSize / 2.0);
		final double radiusValue = this.radius.get();

		final double graphicRadius = innerRadiusValue + (radiusValue - innerRadiusValue) / 2.0;

		final double offsetValue = this.offset.get();

		if (!this.clockwise.get()) {
			this.innerStartX = innerRadiusValue * Math.cos(Math.toRadians(startAngleValue));
			this.innerStartY = -innerRadiusValue * Math.sin(Math.toRadians(startAngleValue));
			this.innerEndX = innerRadiusValue * Math.cos(Math.toRadians(startAngleValue + this.menuSize));
			this.innerEndY = -innerRadiusValue * Math.sin(Math.toRadians(startAngleValue + this.menuSize));

			this.innerSweep = false;

			this.startX = radiusValue * Math.cos(Math.toRadians(startAngleValue + this.menuSize));
			this.startY = -radiusValue * Math.sin(Math.toRadians(startAngleValue + this.menuSize));
			this.endX = radiusValue * Math.cos(Math.toRadians(startAngleValue));
			this.endY = -radiusValue * Math.sin(Math.toRadians(startAngleValue));

			this.sweep = true;

			if (this.graphic != null) {
				this.graphicX = graphicRadius * Math.cos(Math.toRadians(graphicAngle))
						- this.graphic.getBoundsInParent().getWidth() / 2.0;
				this.graphicY = -graphicRadius * Math.sin(Math.toRadians(graphicAngle))
						- this.graphic.getBoundsInParent().getHeight() / 2.0;

			}
			this.translateX = offsetValue * Math.cos(Math.toRadians(startAngleValue + (this.menuSize / 2.0)));
			this.translateY = -offsetValue * Math.sin(Math.toRadians(startAngleValue + (this.menuSize / 2.0)));

		} else if (this.clockwise.get()) {
			this.innerStartX = innerRadiusValue * Math.cos(Math.toRadians(startAngleValue));
			this.innerStartY = innerRadiusValue * Math.sin(Math.toRadians(startAngleValue));
			this.innerEndX = innerRadiusValue * Math.cos(Math.toRadians(startAngleValue + this.menuSize));
			this.innerEndY = innerRadiusValue * Math.sin(Math.toRadians(startAngleValue + this.menuSize));

			this.innerSweep = true;

			this.startX = radiusValue * Math.cos(Math.toRadians(startAngleValue + this.menuSize));
			this.startY = radiusValue * Math.sin(Math.toRadians(startAngleValue + this.menuSize));
			this.endX = radiusValue * Math.cos(Math.toRadians(startAngleValue));
			this.endY = radiusValue * Math.sin(Math.toRadians(startAngleValue));

			this.sweep = false;

			if (this.graphic != null) {
				this.graphicX = graphicRadius * Math.cos(Math.toRadians(graphicAngle))
						- this.graphic.getBoundsInParent().getWidth() / 2.0;
				this.graphicY = graphicRadius * Math.sin(Math.toRadians(graphicAngle))
						- this.graphic.getBoundsInParent().getHeight() / 2.0;

			}

			this.translateX = offsetValue * Math.cos(Math.toRadians(startAngleValue + (this.menuSize / 2.0)));
			this.translateY = offsetValue * Math.sin(Math.toRadians(startAngleValue + (this.menuSize / 2.0)));
		}

	}

	/**
	 * Gets the menu size.
	 *
	 * @return the menu size
	 */
	public double getMenuSize() {
		return menuSize;
	}

	/* (non-Javadoc)
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(final ObservableValue<? extends Object> arg0, final Object arg1, final Object arg2) {
		redraw();
	}

	/**
	 * @return
	 */
	public boolean isSelected() {
		return false;
	}

	/**
	 * @param b
	 */
	public void setSelected(boolean b) {
		
	}


}
