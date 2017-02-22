package librarysystema;

import application.Main;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Window;

/**
 * This is the Context Menu for every Library in the LibraryMode
 * 
 * @author GOXR3PLUS
 *
 */
public class LibraryContextMenu extends ContextMenu {

    /** The open. */
    MenuItem open = new MenuItem("Open");

    /** The close. */
    MenuItem close = new MenuItem("Close");

    /** The rename. */
    MenuItem rename = new MenuItem("Rename(R)");

    /** The delete. */
    MenuItem delete = new MenuItem("Delete(D)");

    /** The image. */
    Menu image = new Menu("Image");

    /** The set image. */
    Menu setImage = new Menu("newImage");

    /** The local image. */
    MenuItem localImage = new MenuItem("local");

    /** The internet image. */
    MenuItem internetImage = new MenuItem("internet");

    /** The export image. */
    MenuItem exportImage = new MenuItem("exportImage(E)");

    /** The reset image. */
    MenuItem resetImage = new MenuItem("defaultImage");

    /** The settings. */
    MenuItem settings = new MenuItem("Settings(S)");

    /** The library. */
    private Library library;

    /**
     * Instantiates a new library context menu.
     */
    // Constructor
    public LibraryContextMenu() {

	open.setOnAction(ac -> library.libraryOpenClose(true, false));

	close.setOnAction(c -> library.libraryOpenClose(false, false));

	rename.setOnAction(ac -> library.renameLibrary());

	localImage.setOnAction(ac -> library.setNewImage());

	resetImage.setOnAction(ac -> library.setDefaultImage());

	exportImage.setOnAction(a -> library.exportImage());

	settings.setOnAction(ac -> Main.libraryMode.libraryViewer.settings.showWindow(library));

	delete.setOnAction(ac -> library.deleteLibrary());

	internetImage.setDisable(true);
	// exportImage.setDisable(true)

	setImage.getItems().addAll(localImage, internetImage);
	image.getItems().addAll(setImage, exportImage, resetImage);

	getItems().addAll(open, close, settings, new SeparatorMenuItem(), image, rename, delete);

    }

    /**
     * Shows the LibraryContextMenu.
     *
     * @param window
     *            the window
     * @param x
     *            the x
     * @param y
     *            the y
     * @param library
     *            the library
     */
    public void show(Window window, double x, double y, Library library) {
	this.library = library;

	// customize the menu accordingly
	if (library.isLibraryOpened()) {
	    getItems().remove(open);
	    getItems().add(0, close);
	} else {
	    getItems().remove(close);
	    getItems().add(0, open);
	}

	show(window, x, y);
    }

}
