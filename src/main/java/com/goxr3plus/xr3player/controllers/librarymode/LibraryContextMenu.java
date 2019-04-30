package com.goxr3plus.xr3player.controllers.librarymode;

import java.io.IOException;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.librarymode.Library.LibraryStatus;
import com.goxr3plus.xr3player.utils.general.InfoTool;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * This is the Context Menu for every Library in the LibraryMode
 * 
 * @author GOXR3PLUS
 *
 */
public class LibraryContextMenu extends ContextMenu {

	// -------------------------------------------------------------

	@FXML
	private MenuItem open;

	@FXML
	private MenuItem close;

	@FXML
	private MenuItem rename;

	@FXML
	private MenuItem delete;

	@FXML
	private MenuItem changeImage;

	@FXML
	private MenuItem resetImage;

	@FXML
	private MenuItem exportImage;

	@FXML
	private MenuItem moreInfo;

	// -------------------------------------------------------------

	private Library library;

	/**
	 * Instantiates a new library context menu.
	 */
	// Constructor
	public LibraryContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.LIBRARIES_FXMLS + "LibraryContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		open.setOnAction(ac -> library.setLibraryStatus(LibraryStatus.OPENED, false));

		close.setOnAction(ac -> library.setLibraryStatus(LibraryStatus.CLOSED, false));

		rename.setOnAction(ac -> library.renameLibrary(library));

		changeImage.setOnAction(ac -> library.setNewImage());

		resetImage.setOnAction(ac -> library.setDefaultImage());

		exportImage.setOnAction(ac -> library.exportImage());

		moreInfo.setOnAction(ac -> Main.libraryMode.libraryInformation.showWindow(library));

		delete.setOnAction(ac -> Main.libraryMode.deleteLibraries(library, null));
	}

	/**
	 * Shows the LibraryContextMenu.
	 *
	 * @param window  the window
	 * @param x       the x
	 * @param y       the y
	 * @param library the library
	 */
	public void show(Window window, double x, double y, Library library) {
		this.library = library;

		// Remove dis nuts
		getItems().remove(open);
		getItems().remove(close);

		// customize the menu accordingly
		getItems().add(0, library.isOpened() ? close : open);

		exportImage.setDisable(library.getAbsoluteImagePath() == null);
		resetImage.setDisable(exportImage.isDisable());

		// Fix first time show problem
		if (super.getWidth() == 0) {
			show(Main.window);
			hide();
		}

		// Show it
		show(window, x - 35 - super.getWidth() + super.getWidth() * 14 / 100, y - 1);

		// Y axis
		double yIni = y - 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// X axis
		// double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		// double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		// super.setX(xIni);
		// final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		// xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames()
				.addAll(new KeyFrame(Duration.seconds(0.35), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		// new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd,
		// Interpolator.EASE_BOTH)))
		timeIn.play();

	}

}
