package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.TopBar.WindowMode;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;

public class OnlineMusicBoxController extends StackPane {
	
	// -------------------------------------------------------------
	
	@FXML
	private Label descriptionLabel;
	
	@FXML
	private FontIcon fontIcon;
	
	@FXML
	private Label stackLabel;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private final String url;
	private final String description;
	
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
		
		switch (category) {
			case RECOMMENDED:
				JavaFXTools.setFontIcon(null, fontIcon, "far-hand-peace", Color.WHITE);
				break;
			case GENRES:
				JavaFXTools.setFontIcon(null, fontIcon, "far-user", Color.WHITE);
				break;
			case ACTIVITIES:
				JavaFXTools.setFontIcon(null, fontIcon, "icm-rocket", Color.WHITE);
				break;
			case MOOD:
				JavaFXTools.setFontIcon(null, fontIcon, "far-smile", Color.WHITE);
				break;
			case EDITORSPICK:
				JavaFXTools.setFontIcon(null, fontIcon, "far-gem", Color.WHITE);
				break;
			case CHARTS:
				JavaFXTools.setFontIcon(null, fontIcon, "fas-chart-bar", Color.WHITE);
				break;
		}
		
		//descriptionLabel
		descriptionLabel.setText("'" + description + "'");
		
		//Mouse Events
		setOnMouseClicked(m -> {
			Main.webBrowser.createTabAndSelect(url);
			Main.topBar.goMode(WindowMode.WEBMODE);
		});
		
		stackLabel.visibleProperty().bind(this.hoverProperty());
		
	}
}
