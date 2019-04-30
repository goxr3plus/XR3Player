package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXTabPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.models.lists.EmotionListsController;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class EmotionsTabPane extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private Tab hateTab;

	@FXML
	private Tab dislikeTab;

	@FXML
	private Tab likeTab;

	@FXML
	private Tab loveTab;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final EmotionListsController emotionListsController;

	/**
	 * Constructor.
	 */
	public EmotionsTabPane(EmotionListsController emotionListsController) {
		this.emotionListsController = emotionListsController;

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "EmotionsTabPane.fxml"));
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

		// hateTab
		hateTab.setContent(emotionListsController.hatedMediaListController);

		// dislikeTab
		dislikeTab.setContent(emotionListsController.dislikedMediaListController);

		// likeTab
		likeTab.setContent(emotionListsController.likedMediaListController);

		// loveTab
		loveTab.setContent(emotionListsController.lovedMediaListController);

		// Set Tab Selected
		tabPane.getSelectionModel().select(loveTab);

	}

	/**
	 * @return the tabPane
	 */
	public JFXTabPane getTabPane() {
		return tabPane;
	}

	/**
	 * @return the hateTab
	 */
	public Tab getHateTab() {
		return hateTab;
	}

	/**
	 * @return the dislikeTab
	 */
	public Tab getDislikeTab() {
		return dislikeTab;
	}

	/**
	 * @return the likeTab
	 */
	public Tab getLikeTab() {
		return likeTab;
	}

	/**
	 * @return the loveTab
	 */
	public Tab getLoveTab() {
		return loveTab;
	}

}
