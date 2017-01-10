/**
 * RadialCheckMenuItem.java
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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

/**
 * The Class RadialCheckMenuItem.
 */
public class RadialCheckMenuItem extends RadialMenuItem {
	
	/** The selected. */
	protected SimpleBooleanProperty selected;
	
	/** The selected color. */
	protected Paint selectedColor;
	
	/** The selected mouse on color. */
	protected Paint selectedMouseOnColor;
	
	/**
	 * Instantiates a new radial check menu item.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 */
	public RadialCheckMenuItem(final double menuSize, final Node graphic) {
		super(menuSize, graphic);
	}
	
	/**
	 * Instantiates a new radial check menu item.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 * @param event the event
	 */
	public RadialCheckMenuItem(final double menuSize, final Node graphic, final EventHandler<ActionEvent> event) {
		super(menuSize, graphic, event);
	}
	
	/**
	 * Instantiates a new radial check menu item.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 * @param selected the selected
	 */
	public RadialCheckMenuItem(final double menuSize, final Node graphic, final boolean selected) {
		this(menuSize, graphic);
		this.selected.set(selected);
	}
	
	/**
	 * Instantiates a new radial check menu item.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 * @param selected the selected
	 * @param selectedColor the selected color
	 */
	public RadialCheckMenuItem(final double menuSize, final Node graphic, final boolean selected,
	        final Paint selectedColor) {
		this(menuSize, graphic, selected);
		this.selectedColor = selectedColor;
	}
	
	/**
	 * Instantiates a new radial check menu item.
	 *
	 * @param menuSize the menu size
	 * @param graphic the graphic
	 * @param selected the selected
	 * @param selectedColor the selected color
	 * @param selectedMouseOnColor the selected mouse on color
	 */
	public RadialCheckMenuItem(final double menuSize, final Node graphic, final boolean selected,
	        final Paint selectedColor, final Paint selectedMouseOnColor) {
		this(menuSize, graphic, selected);
		this.selectedColor = selectedColor;
		this.selectedMouseOnColor = selectedMouseOnColor;
	}
	
	@Override
	protected void redraw() {
		super.redraw();
		
		Paint color = null;
		if (backgroundVisible.get()) {
			if (isSelected() && selectedColor != null) {
				if (mouseOn && selectedMouseOnColor != null) {
					color = selectedMouseOnColor;
				} else {
					color = selectedColor;
				}
			} else {
				if (mouseOn && backgroundMouseOnColor != null) {
					color = backgroundMouseOnColor.get();
				} else {
					color = backgroundColor.get();
				}
			}
		}
		
		path.setFill(color);
	}
	
	/**
	 * Set the current MenuItem selected
	 * 
	 * @param selected
	 */
	@Override
	public final void setSelected(final boolean selected) {
		selectedProperty().set(selected);
		redraw();
	}
	
	/**
	 * @return selected value
	 */
	@Override
	public final boolean isSelected() {
		return selectedProperty().get();
	}
	
	/**
	 * @return The Selected Property
	 */
	public final SimpleBooleanProperty selectedProperty() {
		if (selected == null)
			selected = new SimpleBooleanProperty(false);
		return selected;
	}
	
}
