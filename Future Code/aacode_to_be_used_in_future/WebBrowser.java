/*
 * 
 */
package VideoPlayer_WebBrowser;

import application.Main;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class WebBrowser.
 */
public class WebBrowser extends GridPane {

	/** The browser. */
	// Web
	public WebView browser = new WebView();
	
	/** The engine. */
	WebEngine engine = browser.getEngine();
	
	/** The indicator. */
	ProgressIndicator indicator = new ProgressIndicator();

	/** The search. */
	// Search
	TextField search = new TextField();

	/** The home. */
	// UseFull
	Button home = new Button();

	/**
	 * Instantiates a new web browser.
	 */
	public WebBrowser() {

		setStyle("-fx-background-color:black;");
		setPadding(new Insets(2, 5, 2, 5));
		setHgap(5);
		setVgap(5);

		// Searchs
		search.setTooltip(new Tooltip("Search Engine"));
		search.setPromptText("Search Web...");
		search.textProperty().bind(engine.locationProperty());

		search.setOnAction(action -> {
			engine.load("https://www.google.com ");
		});

		// Home
		home.setGraphic(InfoTool.getImageViewFromDocuments("home.png"));
		home.setId("transparent_button");
		home.setOnAction(action -> {
			engine.load("https://www.google.com");
		});

		// Indicator
		indicator.visibleProperty().bind(engine.getLoadWorker().runningProperty());

		// setGridLinesVisible(true);
		add(home, 0, 0);
		add(indicator, 1, 0);
		add(search, 2, 0);

		// BROWSER
		add(browser, 0, 1, 3, 1);

		engine.getLoadWorker().exceptionProperty().addListener(l -> {
			Alert alert = new Alert(AlertType.ERROR, engine.getLoadWorker().getException().getMessage());
			alert.initOwner(Main.window);
			alert.showAndWait();
		});

	}

}
