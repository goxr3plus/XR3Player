/**
 * 
 */
package browsers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import customnodes.Marquee;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class WebBrowserTabController extends StackPane {

    /** The logger. */
    private final Logger logger = Logger.getLogger(getClass().getName());

    //------------------------------------------------------------

    @FXML
    private BorderPane borderPane;

    @FXML
    private WebView webView;

    @FXML
    private JFXButton backwardButton;

    @FXML
    private JFXButton forwardButton;

    @FXML
    private JFXTextField searchBar;

    @FXML
    private JFXButton reloadButton;

    @FXML
    private ProgressBar progressBar;

    // -------------------------------------------------------------

    /** The engine. */
    WebEngine webEngine;
    /** The web history */
    WebHistory history;
    ObservableList<WebHistory.Entry> historyEntryList;

    Tab tab;

    /**
     * Constructor
     */
    public WebBrowserTabController(Tab tab) {
	this.tab = tab;
	this.tab.setContent(this);

	// ------------------------------------FXMLLOADER ----------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "WebBrowserTabController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}
    }

    /**
     * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
     */
    @FXML
    private void initialize() {

	//-------------------WebView------------------------
	// hide webview scrollbars whenever they appear.
	webView.getChildrenUnmodifiable().addListener((Change<? extends Node> change) -> {
	    Set<Node> deadSeaScrolls = webView.lookupAll(".scroll-bar");
	    for (Node scroll : deadSeaScrolls) {
		scroll.setVisible(false);
		scroll.setManaged(false);
	    }
	});

	//-------------------WebEngine------------------------
	webEngine = webView.getEngine();
	webEngine.load("https://www.duckduckgo.com/");
	webEngine.getLoadWorker().exceptionProperty().addListener(l -> {
	    Alert alert = new Alert(AlertType.ERROR, webEngine.getLoadWorker().getException().getMessage());
	    alert.initOwner(Main.window);
	    alert.initOwner(Main.window);
	    alert.showAndWait();
	});
	history = webEngine.getHistory();
	historyEntryList = history.getEntries();
	SimpleListProperty<Entry> list = new SimpleListProperty<>(historyEntryList);

	//-------------------Items------------------------
	//----tab
	tab.setTooltip(new Tooltip(""));
	tab.getTooltip().textProperty().bind(webEngine.titleProperty());
	//tab.textProperty().bind(webEngine.titleProperty());

	// Graphic
	StackPane stack = new StackPane();

	// indicator
	ProgressBar indicator = new ProgressBar();
	indicator.progressProperty().bind(progressBar.progressProperty());
	indicator.visibleProperty().bind(progressBar.visibleProperty());
	indicator.setMaxSize(30, 11);

	// text
	Text text = new Text();
	text.setStyle("-fx-font-size:70%;");
	text.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100.00).asString("%.02f %%"));
	// text.visibleProperty().bind(library.getSmartController().inputService.runningProperty())

	Marquee marquee = new Marquee();
	marquee.textProperty().bind(tab.getTooltip().textProperty());
	marquee.setStyle(
		"-fx-background-radius:0 0 15 15; -fx-background-color:rgb(255,255,255,0.7); -fx-border-color:transparent;");

	stack.getChildren().addAll(indicator, text);
	stack.setManaged(false);
	stack.setVisible(false);

	// stack
	progressBar.visibleProperty().addListener(l -> {
	    if (indicator.isVisible()) {
		stack.setManaged(true);
		stack.setVisible(true);
	    } else {
		stack.setManaged(false);
		stack.setVisible(false);
	    }
	});

	tab.setOnCloseRequest(c -> {
	    // Delete cache for navigate back
	    webEngine.load("about:blank");

	    //Experimental!!!
	    //Delete cookies 
	    //java.net.CookieHandler.setDefault(new java.net.CookieManager())
	});

	// HBOX
	HBox hBox = new HBox();
	hBox.getChildren().addAll(stack, marquee);
	tab.setGraphic(hBox);

	//----searchBar
	searchBar.focusedProperty().addListener((observable, oldValue, newValue) -> {
	    if (newValue) // if focused
		searchBar.textProperty().unbind();
	    else
		searchBar.textProperty().bind(webEngine.locationProperty());
	});
	searchBar.setOnAction(a -> {
	    // Get an UrlValidator
	    UrlValidator defaultValidator = new UrlValidator(); // default schemes
	    String load = null;
	    if (defaultValidator.isValid(searchBar.getText()))
		load = searchBar.getText();

	    //Load
	    try {
		webEngine.load(load != null ? load
			: "https://www.google.com/search?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8"));
	    } catch (UnsupportedEncodingException ex) {
		ex.printStackTrace();
	    }
	});

	//reloadButton
	reloadButton.setOnAction(a -> webEngine.reload());

	//ProgressBar	
	progressBar.visibleProperty().bind(webEngine.getLoadWorker().runningProperty());
	progressBar.managedProperty().bind(progressBar.visibleProperty());
	progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

	//backwardButton
	backwardButton.setOnAction(a -> goBack());
	backwardButton.disableProperty().bind(history.currentIndexProperty().isEqualTo(0));

	//forwardButton
	forwardButton.setOnAction(a -> goForward());
	forwardButton.disableProperty()
		.bind(history.currentIndexProperty().greaterThanOrEqualTo(list.sizeProperty().subtract(1)));

	//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
    }

    /**
     * Goes Backward one Page
     * 
     */
    public void goBack() {
	history.go(historyEntryList.size() > 1 && history.getCurrentIndex() > 0 ? -1 : 0);
	//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
    }

    /**
     * Goes Forward one Page
     * 
     */
    public void goForward() {
	history.go(historyEntryList.size() > 1 && history.getCurrentIndex() < historyEntryList.size() - 1 ? 1 : 0);
	//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
    }

}
