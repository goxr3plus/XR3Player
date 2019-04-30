package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class MediaViewer extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private ImageView imageView;

	@FXML
	private Label nameLabel;

	// -------------------------------------------------------------

	private final Media media;

	/**
	 * Constructor.
	 */
	public MediaViewer(Media media) {
		this.media = media;

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "MediaViewer.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as fxml is initialized
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * @return the imageView
	 */
	public ImageView getImageView() {
		return imageView;
	}

	/**
	 * @return the media
	 */
	public Media getMedia() {
		return media;
	}

	/**
	 * @return the mediaName
	 */
	public Label getNameLabel() {
		return nameLabel;
	}

}
