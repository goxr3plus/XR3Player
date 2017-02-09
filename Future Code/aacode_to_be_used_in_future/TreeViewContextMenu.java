/*
 * 
 */
package treeview;

import application.Main;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import smartcontroller.Genre;

/**
 * The Class TreeViewContextMenu.
 */
public class TreeViewContextMenu extends ContextMenu {

    /** The open menu. */
    Menu openMenu = new Menu("Open in ");

    /** The open current. */
    MenuItem open_Current = new MenuItem("Current Playlist");

    /** The open new. */
    MenuItem open_New = new MenuItem("New Playlist");

    /** The add current. */
    MenuItem add_Current = new MenuItem("Add in Current");

    /** The full path. */
    String fullPath = null;

    /** The category. */
    Genre category;

    /**
     * Instantiates a new tree view context menu.
     */
    // Constructor
    public TreeViewContextMenu() {

	/*
	 * add_Current.setOnAction(ac -> { if
	 * (Main.simpleMode.multiplePlaylists.getSelectedPlayList().controller.
	 * isFree(true)) doTheInsert();
	 * 
	 * });
	 * 
	 * open_New.setOnAction(ac -> {
	 * Main.simpleMode.multiplePlaylists.createNewPlayList(-1, -1, true);
	 * });
	 * 
	 * open_Current.setOnAction(ac -> { if
	 * (Main.simpleMode.multiplePlaylists.getSelectedPlayList().controller.
	 * isFree(true)) { // Προηποθέτει και μία συνολική εκάθαρση πριν την
	 * εισαγωγή των // στοιχείων
	 * Main.simpleMode.multiplePlaylists.getSelectedPlayList().controller.
	 * clearAllItems(); doTheInsert(); } });
	 */

	openMenu.getItems().addAll(open_Current, open_New);
	getItems().addAll(add_Current, openMenu);
    }

    /**
     * Does the job of instertNewLibrary From TREE MANAGER.
     *
     * @param category
     *            the category
     * @param path
     *            the path
     * @param screenX
     *            the screen X
     * @param screenY
     *            the screen Y
     */
    /*
     * public void doTheInsert() { // Κανονικά if (category == Genre.SYSTEMFILE)
     * Main.simpleMode.multiplePlaylists.getSelectedPlayList().ioService.start(
     * Arrays.asList(new File(fullPath))); else if (category == Genre.LIBRARY &&
     * Main.libraryMode.getLibraryWithName(fullPath).controller.isFree(true)) {
     * Main.simpleMode.multiplePlaylists.getSelectedPlayList().ioService.start(
     * fullPath); } }
     */

    /**
     * Shows the contextMenu in the postion screenX,screenY
     * 
     * @param path
     * @param screenX
     * @param screenY
     */
    public void showMenu(Genre category, String path, double screenX, double screenY) {

	// Να ξέρω εάν είναι βιβλιοθήκη ή
	this.category = category;
	fullPath = path;
	show(Main.window, screenX, screenY);
    }
}
