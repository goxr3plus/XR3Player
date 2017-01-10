/**
 * RadialContainerMenuItem.java
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

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc
/**
 * The Class RadialContainerMenuItem.
 */
public class RadialContainerMenuItem extends RadialMenuItem {
	
	/** The selected. */
	private boolean selected = false;
	
	/** The child anim group. */
	private final Group childAnimGroup = new Group();
	
	/** The fade in. */
	private FadeTransition fadeIn = null;
	
	/** The fade out. */
	private FadeTransition fadeOut = null;
	
	/** The items. */
	protected List<RadialMenuItem> items = new ArrayList<>();
	
	/** The arrow. */
	protected Polyline arrow = new Polyline(-5.0, -5.0, 5.0, 0.0, -5.0, 5.0, -5.0, -5.0);
	
	/**
	 * Instantiates a new radial container menu item.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 */
	public RadialContainerMenuItem(final double menuSize, final Node graphic) {
		super(menuSize, graphic);
		initialize();
	}
	
	/**
	 * Instantiates a new radial container menu item.
	 *
	 * @param menuSize the menu size
	 * @param text the text
	 * @param graphic the graphic
	 */
	public RadialContainerMenuItem(final double menuSize, final String text, final Node graphic) {
		super(menuSize, text, graphic);
		initialize();
	}
	
	/**
	 * Initialize.
	 */
	private void initialize() {
		arrow.setFill(Color.GRAY);
		arrow.setStroke(null);
		childAnimGroup.setVisible(false);
		visibleProperty().addListener((observable , oldValue , newValue) -> {
			if (!observable.getValue()) {
				childAnimGroup.setVisible(false);
				RadialContainerMenuItem.this.setSelected(false);
				
			}
		});
		getChildren().add(childAnimGroup);
		
		fadeIn = new FadeTransition(Duration.millis(400), childAnimGroup);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		
		fadeOut = new FadeTransition(Duration.millis(400), childAnimGroup);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setOnFinished(f -> childAnimGroup.setVisible(false));
		
		getChildren().add(arrow);
	}
	
	/**
	 * Adds the menu item.
	 *
	 * @param item the item
	 */
	public void addMenuItem(final RadialMenuItem item) {
		item.backgroundColorProperty().bind(backgroundColor);
		item.backgroundMouseOnColorProperty().bind(backgroundMouseOnColor);
		item.innerRadiusProperty().bind(radius);
		item.radiusProperty().bind(radius.multiply(2).subtract(innerRadius));
		item.offsetProperty().bind(offset.multiply(2.0));
		item.strokeColorProperty().bind(strokeColor);
		item.clockwiseProperty().bind(clockwise);
		item.backgroundVisibleProperty().bind(backgroundVisible);
		item.strokeVisibleProperty().bind(strokeVisible);
		items.add(item);
		childAnimGroup.getChildren().add(item);
		double offset = 0;
		for (final RadialMenuItem it : items) {
			it.startAngleProperty().bind(startAngleProperty().add(offset));
			offset += it.getMenuSize();
		}
	}
	
	/**
	 * Removes the menu item.
	 *
	 * @param item the item
	 */
	public void removeMenuItem(final RadialMenuItem item) {
		items.remove(item);
		childAnimGroup.getChildren().remove(item);
		item.backgroundColorProperty().unbind();
		item.backgroundMouseOnColorProperty().unbind();
		item.innerRadiusProperty().unbind();
		item.radiusProperty().unbind();
		item.offsetProperty().unbind();
		item.strokeColorProperty().unbind();
		item.clockwiseProperty().unbind();
		item.backgroundVisibleProperty().unbind();
		item.strokeVisibleProperty().unbind();
		
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
	 * @see radialmenu.RadialMenuItem#redraw() */
	@Override
	protected void redraw() {
		super.redraw();
		if (selected) {
			path.setFill(backgroundVisible.get()
			        ? ( selected && backgroundMouseOnColor.get() != null ? backgroundMouseOnColor.get()
			                : backgroundColor.get() )
			        : null);
		}
		if (arrow != null) {
			arrow.setFill(backgroundVisible.get()
			        ? ( mouseOn && strokeColor.get() != null ? strokeColor.get() : strokeColor.get() )
			        : null);
			arrow.setStroke(strokeVisible.get() ? strokeColor.get() : null);
			if (!clockwise.get()) {
				arrow.setRotate(- ( startAngle.get() + menuSize / 2.0 ));
				arrow.setTranslateX( ( radius.get() - arrow.getBoundsInLocal().getWidth() / 2.0 )
				        * Math.cos(Math.toRadians(startAngle.get() + menuSize / 2.0)) + translateX);
				arrow.setTranslateY(- ( radius.get() - arrow.getBoundsInLocal().getHeight() / 2.0 )
				        * Math.sin(Math.toRadians(startAngle.get() + menuSize / 2.0)) + translateY);
			} else {
				arrow.setRotate(startAngle.get() + menuSize / 2.0);
				arrow.setTranslateX( ( radius.get() - arrow.getBoundsInLocal().getWidth() / 2.0 )
				        * Math.cos(Math.toRadians(startAngle.get() + menuSize / 2.0)) + translateX);
				arrow.setTranslateY( ( radius.get() - arrow.getBoundsInLocal().getHeight() / 2.0 )
				        * Math.sin(Math.toRadians(startAngle.get() + menuSize / 2.0)) + translateY);
				
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see radialmenu.RadialMenuItem#setSelected(boolean) */
	@Override
	public void setSelected(final boolean selected) {
		this.selected = selected;
		if (selected) {
			double startOpacity = 0;
			if (fadeOut.getStatus() == Status.RUNNING) {
				fadeOut.stop();
				startOpacity = childAnimGroup.getOpacity();
			}
			// draw Children
			childAnimGroup.setOpacity(startOpacity);
			childAnimGroup.setVisible(true);
			fadeIn.fromValueProperty().set(startOpacity);
			fadeIn.playFromStart();
		} else {
			// draw Children
			double startOpacity = 1.0;
			if (fadeIn.getStatus() == Status.RUNNING) {
				fadeIn.stop();
				startOpacity = childAnimGroup.getOpacity();
			}
			fadeOut.fromValueProperty().set(startOpacity);
			fadeOut.playFromStart();
		}
		redraw();
	}
	
	/* (non-Javadoc)
	 * @see radialmenu.RadialMenuItem#isSelected() */
	@Override
	public boolean isSelected() {
		return selected;
	}
	
}
