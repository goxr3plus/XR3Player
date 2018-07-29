package main.java.com.goxr3plus.xr3player.smartcontroller.presenter;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;

public class MediaViewer extends StackPane {
	
	//--------------------------------------------------------------
	
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
		
		// ------------------------------------FXMLLOADER ----------------------------------------
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
