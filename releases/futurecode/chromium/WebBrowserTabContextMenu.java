package com.goxr3plus.xr3player.controllers.chromium;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class WebBrowserTabContextMenu extends ContextMenu {

	// --------------------------------------------------------------

	@FXML
	private MenuItem newTab;

	@FXML
	private MenuItem reloadTab;

	@FXML
	private MenuItem closeOtherTabs;

	@FXML
	private MenuItem closeTabsRight;

	@FXML
	private MenuItem closeTabsLeft;

	@FXML
	private MenuItem closeTab;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final WebBrowserTabController webBrowserTabController;

	private final WebBrowserController webBrowserController;

	/**
	 * Constructor
	 * 
	 * @param webBrowserTabController
	 * @param webBrowserController
	 */
	public WebBrowserTabContextMenu(WebBrowserTabController webBrowserTabController,
			WebBrowserController webBrowserController) {
		this.webBrowserTabController = webBrowserTabController;
		this.webBrowserController = webBrowserController;

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.BROWSER_FXMLS + "WebBrowserTabContextMenu.fxml"));
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

		// newTab
		newTab.setOnAction(a -> webBrowserController.createAndAddNewTab());

		// reloadTab
		reloadTab.setOnAction(a -> webBrowserTabController.reloadWebSite());

		// closeTabsRight
		closeTabsRight.setOnAction(a -> webBrowserController.closeTabsToTheRight(webBrowserTabController.getTab()));

		// closeTabsLeft
		closeTabsLeft.setOnAction(a -> webBrowserController.closeTabsToTheLeft(webBrowserTabController.getTab()));

		// closeOtherTabs
		closeOtherTabs.setOnAction(a -> {
			webBrowserController.closeTabsToTheLeft(webBrowserTabController.getTab());
			webBrowserController.closeTabsToTheRight(webBrowserTabController.getTab());
		});

		// closeTab
		closeTab.setOnAction(a -> webBrowserController.removeTab(webBrowserTabController.getTab()));

	}
}
