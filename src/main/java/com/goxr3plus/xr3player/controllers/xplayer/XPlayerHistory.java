/**
 * 
 */
package com.goxr3plus.xr3player.controllers.xplayer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The Class XPlayerSettingsController.
 *
 * @author GOXR3PLUS
 */
public class XPlayerHistory extends StackPane {

	// ------------------------

	@FXML
	private BorderPane borderPane;

	// ------------------------

	private final XPlayerController xPlayerController;

	/**
	 * Constructor.
	 *
	 */
	public XPlayerHistory(XPlayerController xPlayerController) {

		this.xPlayerController = xPlayerController;

		// --------------------------FXMLLoader--------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.PLAYERS_FXMLS + "XPlayerHistory.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "XPlayerSettingsController FXML can't be loaded!",
					ex);
		}

	}

	/**
	 * As soon as fxml has been loaded then this method will be called
	 * 1)-constructor,2)-FXMLLOADER,3)-initialise();
	 */
	@FXML
	private void initialize() {

		// When this can be visible?
		this.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				xPlayerController.getHistoryToggle().setSelected(false);
		});
		/*
		 * this.visibleProperty().bind(xPlayerUI.getHistoryToggle().selectedProperty());
		 * this.visibleProperty().addListener((observable , oldValue , newValue) -> { if
		 * (newValue) // true? this.requestFocus(); });
		 */

		// ----PlayListTab
		borderPane.setCenter(xPlayerController.getxPlayerPlayList());

	}

}
