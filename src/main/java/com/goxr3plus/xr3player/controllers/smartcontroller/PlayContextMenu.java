/*
 * 
 */
package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * Play Context Menu
 *
 * @author GOXR3PLUS
 */
public class PlayContextMenu extends ContextMenu {

	// --------------------------------------------------------------

	@FXML
	private Menu startPlayer;

	@FXML
	private MenuItem startOnPlayer0;

	@FXML
	private MenuItem startOnPlayer1;

	@FXML
	private MenuItem startOnPlayer2;

	@FXML
	private Menu stopPlayer;

	@FXML
	private MenuItem stopPlayer0;

	@FXML
	private MenuItem stopPlayer1;

	@FXML
	private MenuItem stopPlayer2;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * The node based on which the Rename or Star Window will be position
	 * 
	 * 
	 * /** The media.
	 */
	private String absoluteMediaPath;

	/**
	 * Constructor.
	 */
	public PlayContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "PlayContextMenu.fxml"));
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

		// playOnDeck
		startPlayer.getItems().forEach(item -> item.setOnAction(a -> playOnDeck(startPlayer.getItems().indexOf(item))));

		// stopPlayer
		stopPlayer.getItems().forEach(item -> item
				.setOnAction(a -> Main.xPlayersList.getXPlayer(stopPlayer.getItems().indexOf(item)).stop()));
	}

	/**
	 * Updates the players images based on the status of the players
	 */
	public void updateItemsImages() {

		// Determine the image
		for (int i = 0; i <= 2; i++) {
			boolean playerEnergized = Main.xPlayersList.getXPlayer(i).isOpened()
					|| Main.xPlayersList.getXPlayer(i).isPausedOrPlaying()
					|| Main.xPlayersList.getXPlayer(i).isSeeking();

			// PlayFontIcon
			FontIcon playFontIcon = new FontIcon("fas-play-circle");
			playFontIcon.setIconSize(24);
			playFontIcon.setIconColor(Color.web("#ceff26"));

			// StopFontIcon
			FontIcon stopFontIcon = new FontIcon("fas-stop-circle");
			stopFontIcon.setIconSize(24);
			stopFontIcon.setIconColor(Color.web("#ff3c26"));

			// Set it to the items
			startPlayer.getItems().get(i).setGraphic(!playerEnergized ? null : playFontIcon);
			stopPlayer.getItems().get(i).setGraphic(!playerEnergized ? null : stopFontIcon);
		}
	}

	/**
	 * Adding the song to deck and starting it.
	 *
	 * @param deck the deck
	 */
	public void playOnDeck(int deck) {
		Main.xPlayersList.getXPlayerController(deck).playSong(absoluteMediaPath);
	}

	/**
	 * @return the absoluteMediaPath
	 */
	public String getAbsoluteMediaPath() {
		return absoluteMediaPath;
	}

	/**
	 * @param absoluteMediaPath the absoluteMediaPath to set
	 */
	public void setAbsoluteMediaPath(String absoluteMediaPath) {
		this.absoluteMediaPath = absoluteMediaPath;
	}

	/**
	 * @return the startPlayer
	 */
	public Menu getStartPlayer() {
		return startPlayer;
	}

	/**
	 * @return the stopPlayer
	 */
	public Menu getStopPlayer() {
		return stopPlayer;
	}

}
