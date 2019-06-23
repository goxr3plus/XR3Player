package com.goxr3plus.xr3player.controllers.dropbox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class DropboxFileContextMenu extends ContextMenu {

	// --------------------------------------------------------------

	@FXML
	private MenuItem download;

	@FXML
	private MenuItem rename;

	@FXML
	private MenuItem delete;

	@FXML
	private MenuItem openFolder;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	private DropboxFile dropboxFile;
	private Node node;

	/**
	 * Constructor.
	 */
	public DropboxFileContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.DROPBOX_FXMLS + "DropboxFileContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		// delete
		delete.setOnAction(a -> Main.dropBoxViewer.deleteFile(dropboxFile, false));

		// download
		download.setOnAction(a -> Main.dropBoxViewer.downloadFile(dropboxFile));

		// rename
		rename.setOnAction(a -> Main.dropBoxViewer.renameFile(dropboxFile, node));

		// openFolder
		openFolder.setOnAction(a -> Main.dropBoxViewer.recreateTableView(dropboxFile.getMetadata().getPathLower()));

	}

	/**
	 * Show the ContextMenu
	 *
	 * @param dropboxFile
	 * @param x the x position of the popup anchor in screen coordinates
	 * @param y the y position of the popup anchor in screen coordinates
	 * @param node
	 */
	public void show(DropboxFile dropboxFile, double x, double y, Node node) {
		this.dropboxFile = dropboxFile;
		this.node = node;

		// Open Folder Visibility
		if (dropboxFile.isDirectory())
			openFolder.setVisible(true);
		else
			openFolder.setVisible(false);

		// Show it
		show(Main.window, x, y - 1);

		// Y axis
		double yIni = y + 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames()
				.addAll(new KeyFrame(Duration.seconds(0.25), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		// new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd,
		// Interpolator.EASE_BOTH)))
		timeIn.play();
	}

}
