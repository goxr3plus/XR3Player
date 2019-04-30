/**
 * 
 */
package com.goxr3plus.xr3player.controllers.general;

import javafx.scene.control.MenuItem;

/**
 * @author GOXR3PLUS
 *
 */
public class TitleMenuItem extends MenuItem {

	/**
	 * Constructor
	 * 
	 * @param text The Text of The Menu Item
	 * 
	 */
	public TitleMenuItem(String text) {
		setText(text);
		setDisable(true);
		getStyleClass().clear();
		getStyleClass().add("title-menu-item");
	}

}
