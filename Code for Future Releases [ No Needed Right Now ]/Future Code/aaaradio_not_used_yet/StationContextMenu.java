/*
 * 
 */
package aaaradio_not_used_yet;

import java.net.URL;

import application.Main;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

// TODO: Auto-generated Javadoc
/**
 * The Class StationContextMenu.
 */
public class StationContextMenu extends ContextMenu {

	/** The play. */
	MenuItem play = new MenuItem("Play");
	
	/** The edit. */
	MenuItem edit = new MenuItem("Edit");
	
	/** The delete. */
	MenuItem delete = new MenuItem("Delete");

	/**
	 * Constructor.
	 */
	public StationContextMenu() {
		getItems().addAll(play, edit, delete);
	}

	/**
	 * Shows the popup at the specified location on the screen. The popup window
	 * is positioned in such way that its anchor point ({@see #anchorLocation})
	 * is displayed at the specified anchorX and anchorY coordinates.
	 *
	 * @param url the url
	 * @param x the x
	 * @param y the y
	 */
	public void show(URL url, double x, double y) {

		show(Main.window, x, y);
	}
}
