package main.java.com.goxr3plus.xr3player.smartcontroller.presenter;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;

public class DragViewer extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label nameLabel;
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public DragViewer() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "DragViewer.fxml"));
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
		
		//ImageView
		imageView.setFitWidth(150);
		imageView.setFitHeight(150);
		
		//NameLabel
		nameLabel.setMaxSize(150, 150);
	}
	
	public Node updateMedia(Media media) {
		
		try {
			//Image can be null , remember.
			Image image = media.getAlbumImage();
			
			//Image exists?
			if (image != null) {
				getImageView().setImage(image);
				getNameLabel().setVisible(false);
				
				return imageView;
			} else {
				getNameLabel().setText(media.getTitle());
				getNameLabel().setVisible(true);
				
				return nameLabel;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return nameLabel;
	}
	
	/**
	 * @return the imageView
	 */
	public ImageView getImageView() {
		return imageView;
	}
	
	/**
	 * @return the mediaName
	 */
	public Label getNameLabel() {
		return nameLabel;
	}
	
}
