/**
 * 
 */
package application.webbrowser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;

import com.jfoenix.controls.JFXButton;

import application.presenter.custom.Marquee;
import application.tools.InfoTool;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;

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
	private Button backwardButton;
	
	@FXML
	private Button forwardButton;
	
	@FXML
	private TextField searchBar;
	
	@FXML
	private Button goButton;
	
	@FXML
	private Button reloadButton;
	
	@FXML
	private ComboBox<String> searchEngineComboBox;
	
	@FXML
	private WebView webView;
	
	@FXML
	private VBox errorPane;
	
	@FXML
	private JFXButton tryAgain;
	
	// -------------------------------------------------------------
	
	/** The engine. */
	WebEngine webEngine;
	
	/** The web history */
	private WebHistory history;
	private ObservableList<WebHistory.Entry> historyEntryList;
	
	private final Tab tab;
	private String firstWebSite;
	
	private final WebBrowserController webBrowserController;
	
	/**
	 * Constructor
	 * 
	 * @param tab
	 * @param firstWebSite
	 */
	public WebBrowserTabController(WebBrowserController webBrowserController, Tab tab, String firstWebSite) {
		this.webBrowserController = webBrowserController;
		this.tab = tab;
		this.firstWebSite = firstWebSite;
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
		
		//---------------ERROR PANE---
		//tryAgain
		tryAgain.setOnAction(a -> checkForInternetConnection());
		
		//-------------------WebView------------------------	
		// hide webview scrollbars whenever they appear.
		//	webView.getChildrenUnmodifiable().addListener((Change<? extends Node> change) -> {
		//	    Set<Node> deadSeaScrolls = webView.lookupAll(".scroll-bar");
		//	    for (Node scroll : deadSeaScrolls) {
		//		scroll.setVisible(false);
		//		scroll.setManaged(false);
		//	    }
		//	});
		
		//-------------------WebEngine------------------------
		webEngine = webView.getEngine();
		webEngine.getLoadWorker().exceptionProperty().addListener(error -> checkForInternetConnection());
		webEngine.setOnError(error -> checkForInternetConnection());
		//handle pop up windows
		webEngine.setCreatePopupHandler(l -> webBrowserController.createAndAddNewTab().getWebView().getEngine());
		
		setHistory(webEngine.getHistory());
		historyEntryList = getHistory().getEntries();
		SimpleListProperty<Entry> list = new SimpleListProperty<>(historyEntryList);
		
		//-------------------TAB------------------------
		tab.setTooltip(new Tooltip(""));
		tab.getTooltip().textProperty().bind(webEngine.titleProperty());
		
		// Graphic
		StackPane stack = new StackPane();
		
		// indicator
		ProgressBar indicator = new ProgressBar();
		indicator.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
		indicator.visibleProperty().bind(webEngine.getLoadWorker().runningProperty());
		indicator.setMaxSize(30, 11);
		
		// text
		Text text = new Text();
		text.setStyle("-fx-font-size:70%;");
		text.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100.00).asString("%.02f %%"));
		
		Marquee marquee = new Marquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		
		stack.getChildren().addAll(indicator, text);
		stack.setManaged(false);
		stack.setVisible(false);
		
		// stack
		indicator.visibleProperty().addListener(l -> {
			if (indicator.isVisible()) {
				stack.setManaged(true);
				stack.setVisible(true);
			} else {
				stack.setManaged(false);
				stack.setVisible(false);
			}
		});
		
		// HBOX
		HBox hBox = new HBox();
		hBox.getChildren().addAll(stack, marquee);
		tab.setGraphic(hBox);
		
		//-------------------Items------------------------
		
		//searchBar
		webEngine.getLoadWorker().runningProperty().addListener((observable , oldValue , newValue) -> {
			if (!newValue) // if !running
				searchBar.textProperty().unbind();
			else
				searchBar.textProperty().bind(webEngine.locationProperty());
		});
		searchBar.setOnAction(a -> loadWebSite(searchBar.getText()));
		searchBar.focusedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue)
				Platform.runLater(() -> searchBar.selectAll());
			
		});
		
		//Proposing sites
		new AutoCompleteTextField().bindAutoCompletion(searchBar, 15, true, WebBrowserController.WEBSITE_PROPOSALS);
		
		//goButton
		goButton.setOnAction(searchBar.getOnAction());
		
		//reloadButton
		reloadButton.setOnAction(a -> reloadWebSite());
		
		//backwardButton
		backwardButton.setOnAction(a -> goBack());
		backwardButton.disableProperty().bind(getHistory().currentIndexProperty().isEqualTo(0));
		backwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() - 1).getUrl()).getTab());
		});
		
		//forwardButton
		forwardButton.setOnAction(a -> goForward());
		forwardButton.disableProperty().bind(getHistory().currentIndexProperty().greaterThanOrEqualTo(list.sizeProperty().subtract(1)));
		forwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() + 1).getUrl()).getTab());
		});
		
		searchEngineComboBox.getItems().addAll("Google", "DuckDuckGo", "Bing", "Yahoo");
		searchEngineComboBox.getSelectionModel().select(1);
		
		//Load the website
		loadWebSite(firstWebSite);
	}
	
	/**
	 * Return the Search Url for the Search Provider For example for `Google` returns `https://www.google.com/search?q=`
	 * 
	 * @param searchProvider
	 * @return The Search Engine Url
	 */
	public String getSearchEngineSearchUrl(String searchProvider) {
		//Find
		switch (searchProvider.toLowerCase()) {
			case "bing":
				return "http://www.bing.com/search?q=";
			case "duckduckgo":
				return "https://duckduckgo.com/?q=";
			case "yahoo":
				return "https://search.yahoo.com/search?p=";
			default: //then google
				return "https://www.google.com/search?q=";
		}
	}
	
	/**
	 * Return the Search Url for the Search Provider For example for `Google` returns `https://www.google.com/search?q=`
	 * 
	 * @param searchProvider
	 * @return The Search Engine Url
	 */
	public String getSearchEngineHomeUrl(String searchProvider) {
		//Find
		switch (searchProvider.toLowerCase()) {
			case "bing":
				return "http://www.bing.com";
			case "duckduckgo":
				return "https://duckduckgo.com";
			case "yahoo":
				return "https://search.yahoo.com";
			default: //then google
				return "https://www.google.com";
		}
	}
	
	/**
	 * Loads the given website , either directly if the url is a valid WebSite Url or using a SearchEngine like Google
	 * 
	 * @param webSite
	 */
	public void loadWebSite(String webSite) {
		
		//Check null or empty
		//		if (webSite == null || webSite.isEmpty())
		//			return;
		
		//Search if it is a valid WebSite url
		String load = !new UrlValidator().isValid(webSite) ? null : webSite;
		
		//Load
		try {
			webEngine.load(
					load != null ? load : getSearchEngineSearchUrl(searchEngineComboBox.getSelectionModel().getSelectedItem()) + URLEncoder.encode(searchBar.getText(), "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Loads the default website
	 */
	public void loadDefaultWebSite() {
		webEngine.load(getSearchEngineHomeUrl(searchEngineComboBox.getSelectionModel().getSelectedItem()));
	}
	
	/**
	 * Loads the current website , or if none then loads the default website
	 */
	public void reloadWebSite() {
		if (!getHistory().getEntries().isEmpty())
			webEngine.reload();
		else
			loadDefaultWebSite();
	}
	
	/**
	 * Goes Backward one Page
	 * 
	 */
	public void goBack() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() > 0 ? -1 : 0);
		//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
	}
	
	/**
	 * Goes Forward one Page
	 * 
	 */
	public void goForward() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() < historyEntryList.size() - 1 ? 1 : 0);
		//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
	}
	
	/**
	 * @return the webView
	 */
	public WebView getWebView() {
		return webView;
	}
	
	/**
	 * @return the tab
	 */
	public Tab getTab() {
		return tab;
	}
	
	/**
	 * @return the errorPane
	 */
	public VBox getErrorPane() {
		return errorPane;
	}
	
	/**
	 * Checks for internet connection
	 */
	void checkForInternetConnection() {
		
		//Check for internet connection
		Thread thread = new Thread(() -> {
			boolean hasInternet = InfoTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {
				errorPane.setVisible(!hasInternet);
				if (hasInternet)
					reloadWebSite();
			});
		}, "Internet Connection Tester Thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * @return the history
	 */
	public WebHistory getHistory() {
		return history;
	}
	
	/**
	 * @param history
	 *            the history to set
	 */
	public void setHistory(WebHistory history) {
		this.history = history;
	}
	
}
