package com.goxr3plus.xr3player.controllers.moviemode;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.general.TopBar.WindowMode;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class MovieModeController extends BorderPane {

	// -----------------------------------------------------

	@FXML
	private StackPane stack1;

	@FXML
	private ImageView imageView1;

	@FXML
	private Label label1;

	// -------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public MovieModeController() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MoviesMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as .fxml is initialised
	 */
	@FXML
	private void initialize() {

		imageView1.fitWidthProperty().bind(this.widthProperty());
		imageView1.fitHeightProperty().bind(this.heightProperty());
		stack1.setOnMouseReleased(m -> {
			Main.webBrowser.addNewTabOnTheEnd("https://www1.fmovies.to/movies");
			Main.topBar.goMode(WindowMode.WEBMODE);
			Main.webBrowser.getTabPane().getSelectionModel().selectLast();
		});
	}

}
