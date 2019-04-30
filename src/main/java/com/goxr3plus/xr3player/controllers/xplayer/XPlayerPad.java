package com.goxr3plus.xr3player.controllers.xplayer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * This class is an class FXML Prototype
 *
 * @author GOXR3PLUS
 */
public class XPlayerPad extends BorderPane {

	// --------------------------------------------------------------

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final XPlayerController xPlayerController;

	/**
	 * Constructor.
	 */
	public XPlayerPad(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.PLAYERS_FXMLS + "XPlayerPad.fxml"));
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

	}

}
