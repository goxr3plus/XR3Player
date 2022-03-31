/*
 * 
 */
package com.goxr3plus.xr3player.controllers.xplayer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.controllers.general.TopBar.WindowMode;
import com.goxr3plus.xr3player.controllers.smartcontroller.PlayContextMenu;
import com.goxr3plus.xr3player.controllers.smartcontroller.ShopContextMenu;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class XPlayerControllerContextMenu extends ContextMenu {

	// --------------------------------------------------------------

	@FXML
	private Menu getInfoBuy;

	@FXML
	private Menu findLyrics;

	@FXML
	private MenuItem lyricFinderOrg;

	@FXML
	private MenuItem lyricsCom;

	@FXML
	private MenuItem copy;

	@FXML
	private MenuItem showFile;

	@FXML
	private MenuItem editFileInfo;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * The node based on which the Rename or Star Window will be position
	 */
	private Node node;

	/** The media. */
	private String absoluteFilePath;

	/** ShopContextMenu */
	private final ShopContextMenu shopContextMenu = new ShopContextMenu();

	/** PlayContextMenu */
	private final PlayContextMenu playContextMenu = new PlayContextMenu();

	/**
	 * Constructor.
	 */
	public XPlayerControllerContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.PLAYERS_FXMLS + "XPlayerControllerContextMenu.fxml"));
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

		// PlayContextMenu
		getItems().addAll(0, playContextMenu.getItems());

		// getInfoBuy
		getInfoBuy.getItems().addAll(shopContextMenu.getItems());
	}

	/**
	 * Shows the context menu based on the variables below.
	 *
	 * @param x    Horizontal mouse position on the screen
	 * @param y    Vertical mouse position on the screen
	 * 
	 * @param node
	 */
	public void showContextMenu(String absoluteFilePath, double x, double y, Node node) {

		this.node = node;
		this.absoluteFilePath = absoluteFilePath;

		// Update ShopContextMenu
		shopContextMenu.setMediaTitle(IOInfo.getFileTitle(absoluteFilePath));

		// Update PlayContextMenu
		playContextMenu.setAbsoluteMediaPath(absoluteFilePath);
		playContextMenu.updateItemsImages();

		// Fix first time show problem
		if (super.getWidth() == 0) {
			show(Main.window);
			hide();
		}

		// Show it
		show(Main.window, x - super.getWidth(), y - 1);

		// ------------Animation------------------

		// Y axis
		double yIni = y - 50;
		double yEnd = y;
		super.setY(yIni);

		// X axis
		// double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		// double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		// super.setX(xIni);
		// final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		// xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Create Double Property
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Create Time Line
		Timeline timeIn = new Timeline(
				new KeyFrame(Duration.seconds(0.30), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		// new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd,
		// Interpolator.EASE_BOTH)))
		timeIn.play();
		// ------------ END of Animation------------------

	}

	/**
	 * Open the given website on the build in Chromium
	 * 
	 * @param url
	 */
	private void openWebSite(String url) {
//		Main.webBrowser.createTabAndSelect(url);
//		Main.topBar.goMode(WindowMode.WEBMODE);
	}

	/**
	 * @param e
	 */
	@FXML
	public void action(ActionEvent e) {
		Object source = e.getSource();

		if (source == copy) {
			JavaFXTool.setClipBoard(Arrays.asList(new File(absoluteFilePath)));
		} else if (source == showFile) {
			IOAction.openFileInExplorer(absoluteFilePath);
		} else if (source == editFileInfo)
			Main.tagWindow.openAudio(absoluteFilePath, TagTabCategory.BASICINFO, true);

	}

}
