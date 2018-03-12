package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3capture.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class OnlineMusicBoxController extends StackPane {
	
	// -------------------------------------------------------------
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label descriptionLabel;
	
	@FXML
	private Label stackLabel;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private final String url;
	private final String description;
	
	//Images
	private final Image recommendedImage = InfoTool.getImageFromResourcesFolder("online-music-recommendation.jpg");
	private final Image genresImage = InfoTool.getImageFromResourcesFolder("online-music-genres.jpg");
	private final Image activitiesImage = InfoTool.getImageFromResourcesFolder("online-music-activites.jpg");
	private final Image moodImage = InfoTool.getImageFromResourcesFolder("online-music-mood.jpg");
	private final Image editorsPickImage = InfoTool.getImageFromResourcesFolder("online-music-editor-pick.jpg");
	private final Image chartsImage = InfoTool.getImageFromResourcesFolder("online-music-charts.jpg");
	
	//Online-Music-Categories
	public enum OnlineMusicCategory {
		RECOMMENDED, GENRES, ACTIVITIES, MOOD, EDITORSPICK, CHARTS;
	}
	
	private final OnlineMusicCategory category;
	
	/**
	 * Constructor.
	 */
	public OnlineMusicBoxController(String url, String description, OnlineMusicCategory category) {
		this.url = url;
		this.description = description;
		this.category = category;
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "OnlineMusicBox.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .fxml is initialised
	 */
	@FXML
	private void initialize() {
		
		//imageView
		switch (category) {
			case RECOMMENDED:
				imageView.setImage(recommendedImage);
				break;
			case GENRES:
				imageView.setImage(genresImage);
				break;
			case ACTIVITIES:
				imageView.setImage(activitiesImage);
				break;
			case MOOD:
				imageView.setImage(moodImage);
				break;
			case EDITORSPICK:
				imageView.setImage(editorsPickImage);
				break;
			case CHARTS:
				imageView.setImage(chartsImage);
				break;
		}
		
		//descriptionLabel
		descriptionLabel.setText("'" + description + "'");
		
		//
		this.setOnMouseClicked(m -> ActionTool.openWebSite(url));
		
		stackLabel.visibleProperty().bind(this.hoverProperty());
		
	}
}
