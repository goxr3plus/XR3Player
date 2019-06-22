package com.goxr3plus.xr3player.controllers.systemtree;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.smartcontroller.PlayContextMenu;
import com.goxr3plus.xr3player.controllers.smartcontroller.ShopContextMenu;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Duration;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class TreeViewContextMenu extends ContextMenu {

	// --------------------------------------------------------------

	@FXML
	private Menu getInfoBuy;

	@FXML
	private SeparatorMenuItem separator1;

	@FXML
	private MenuItem copy;

	@FXML
	private MenuItem rename;

	@FXML
	private SeparatorMenuItem separator2;

	@FXML
	private MenuItem showFile;

	@FXML
	private MenuItem editFileInfo;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	private FileTreeItem treeItem;

	/** ShopContextMenu */
	private final ShopContextMenu shopContextMenu = new ShopContextMenu();

	/** PlayContextMenu */
	private final PlayContextMenu playContextMenu = new PlayContextMenu();

	/**
	 * Constructor.
	 */
	public TreeViewContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TreeViewContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		// PlayContextMenu
		getItems().addAll(0, playContextMenu.getItems());

		// getInfoBuy
		getInfoBuy.getItems().addAll(shopContextMenu.getItems());

	}

	/**
	 * Show the ContextMenu
	 *
	 * @param treeItem
	 * @param x
	 * @param y
	 */
	public void show(final FileTreeItem treeItem, final double x, final double y) {
		this.treeItem = treeItem;

		// Set all items visible
		getItems().forEach(item -> item.setVisible(true));

		// Keep this variables
		final boolean isAudio = ExtensionTool.isAudio(treeItem.getAbsoluteFilePath());
		final boolean isVideo = ExtensionTool.isVideo(treeItem.getAbsoluteFilePath());

		// Fix The Menu
		if (isVideo) {
			editFileInfo.setVisible(false);
		} else if (!(isAudio || isVideo)) {
			playContextMenu.getStartPlayer().setVisible(false);
			playContextMenu.getStopPlayer().setVisible(false);
			editFileInfo.setVisible(false);
			separator1.setVisible(false);
			separator2.setVisible(false);
			getInfoBuy.setVisible(false);
		}

		// Disable rename
		rename.setVisible(false);

		// Update ShopContextMenu
		shopContextMenu.setMediaTitle(IOInfo.getFileTitle(treeItem.getAbsoluteFilePath()));

		// Update PlayContextMenu
		playContextMenu.setAbsoluteMediaPath(treeItem.getAbsoluteFilePath());
		playContextMenu.updateItemsImages();

		// Show it
		show(Main.window, x + 8, y - 1);

		// Y axis
		final double yIni = y - 50;
		final double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Timeline
		final Timeline timeIn = new Timeline();
		timeIn.getKeyFrames()
				.addAll(new KeyFrame(Duration.seconds(0.35), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		// new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd,
		// Interpolator.EASE_BOTH)))
		timeIn.play();
	}

	@FXML
	public void action(final ActionEvent event) {
		final Object source = event.getSource();

		// showFile
		if (source == showFile)
			IOAction.openFileInExplorer(treeItem.getAbsoluteFilePath());
		else if (source == copy)
			JavaFXTool.setClipBoard(Arrays.asList(new File(treeItem.getAbsoluteFilePath())));
		else if (source == editFileInfo)
			Main.tagWindow.openAudio(treeItem.getAbsoluteFilePath(), TagTabCategory.BASICINFO, true);
		else if (source == rename)
			treeItem.rename(Main.topBar);
	}

	/**
	 * @return the absoluteFilePath
	 */
	public String getAbsoluteFilePath() {
		return treeItem.getAbsoluteFilePath();
	}

	/**
	 * @param absoluteFilePath the absoluteFilePath to set
	 */
	public void setAbsoluteFilePath(final String absoluteFilePath) {
		treeItem.setAbsoluteFilePath(absoluteFilePath);
	}

}
