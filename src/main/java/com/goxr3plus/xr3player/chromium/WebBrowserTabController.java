/**
 * 
 */
package main.java.com.goxr3plus.xr3player.chromium;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.ContextMenuHandler;
import com.teamdev.jxbrowser.chromium.ContextMenuParams;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.NetError;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import com.teamdev.jxbrowser.chromium.javafx.DefaultPopupHandler;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.Marquee;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import net.sf.image4j.codec.ico.ICODecoder;

/**
 * This class represents a Tab from The WebBrowser
 * 
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
	private Button goFullScreen;
	
	@FXML
	private WebView webView;
	
	@FXML
	private Button backwardButton;
	
	@FXML
	private Button reloadButton;
	
	@FXML
	private Button forwardButton;
	
	@FXML
	private TextField searchBar;
	
	@FXML
	private ComboBox<String> searchEngineComboBox;
	
	@FXML
	private Button goButton;
	
	@FXML
	private MenuItem about;
	
	@FXML
	private VBox errorPane;
	
	@FXML
	private JFXButton tryAgain;
	
	@FXML
	private ProgressIndicator tryAgainIndicator;
	
	@FXML
	private JFXCheckBox movingTitleAnimation;
	
	// -------------------------------------------------------------
	
	/** The browser and it's View */
	private Browser browser;
	private BrowserView browserView;
	
	private final Tab tab;
	private final WebBrowserController webBrowserController;
	private final ImageView facIconImageView = new ImageView();
	private String firstWebSite;
	
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
		
		try {
			
			//StackPane
			setOnKeyReleased(k -> {
				if (k.getCode() == KeyCode.F11)
					webBrowserController.chromiumFullScreenController.goFullScreenMode(browserView, this);
			});
			
			//---------------ERROR PANE---
			//tryAgain
			tryAgain.setOnAction(a -> checkForInternetConnection());
			
			//-------------------Browser------------------------
			browser = new Browser();
			//		browser.setPopupHandler(new PopupHandler() {
			//		    public PopupContainer handlePopup(PopupParams params) {
			//		        return new PopupContainer() {
			//
			//					@Override
			//					public void insertBrowser(Browser browser , java.awt.Rectangle arg1) {
			//						
			//						System.out.println(browser.getURL().contains("fmovies.com"));
			//						System.out.println("PopUp occured!!!");
			//					}
			//				};
			//			}
			//		});
			
			//--Render Listener
			//			browser.addRenderListener(new RenderListener() {
			//				
			//				@Override
			//				public void onRenderCreated(RenderEvent arg0) {
			//					System.out.println("Render process is created and ready to work.");
			//				}
			//				
			//				@Override
			//				public void onRenderGone(RenderEvent arg0) {
			//					System.out.println("Render process is exited, crashed or killed.");
			//					
			//				}
			//				
			//				@Override
			//				public void onRenderResponsive(RenderEvent arg0) {
			//					System.out.println("Render process is no longer hung.");
			//				}
			//				
			//				@Override
			//				public void onRenderUnresponsive(RenderEvent arg0) {
			//					System.out.println("Render process is hung.");
			//				}
			//				
			//			});
			
			//-------------------BrowserView------------------------
			browserView = new BrowserView(browser);
			browser.setContextMenuHandler(new MyContextMenuHandler(browserView));
			browser.setPopupHandler(new DefaultPopupHandler());
			borderPane.setCenter(browserView);
			
			//Continue
			
			//browser.pro
			//		webEngine.getLoadWorker().exceptionProperty().addListener(error -> {
			//			//System.out.println("WebEngine exception occured" + error.toString())
			//			checkForInternetConnection();
			//		});
			//		com.sun.javafx.webkit.WebConsoleListener
			//				.setDefaultListener((webView , message , lineNumber , sourceId) -> System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message));
			//		
			//Add listener to the WebEngine
			//webEngine.getLoadWorker().stateProperty().addListener(new FavIconProvider());
			//webEngine.getLoadWorker().stateProperty().addListener(new DownloadDetector());
			//		webEngine.getLoadWorker().stateProperty().addListener((observable , oldState , newState) -> {
			//			if (newState == Worker.State.SUCCEEDED) {
			//				
			//				//Check for error pane
			//				errorPane.setVisible(false);
			//				
			//			} else if (newState == Worker.State.FAILED) {
			//				
			//				//Check for error pane
			//				errorPane.setVisible(true);
			//			}
			//		});
			//		
			//		webEngine.setOnError(error -> {
			//			//System.out.println("WebEngine error occured")
			//			checkForInternetConnection();
			//		});
			
			//handle pop up windows
			//webEngine.setCreatePopupHandler(l -> webBrowserController.createAndAddNewTab().getWebView().getEngine())
			
			//-------------------TAB------------------------
			tab.setTooltip(new Tooltip(""));
			
			// Graphic
			StackPane stack = new StackPane();
			stack.setPadding(new Insets(0, 5, 0, 5));
			stack.setAlignment(Pos.TOP_CENTER);
			
			// indicator
			ProgressIndicator indicator = new ProgressIndicator();
			indicator.getStyleClass().add("dropbox-progress-indicator");
			indicator.setMaxSize(20, 20);
			
			// label
			Label label = new Label();
			label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			label.setAlignment(Pos.CENTER);
			label.setStyle("-fx-font-weight:bold; -fx-text-fill: white; -fx-font-size:10; -fx-background-color: rgb(0,0,0,0.3);");
			label.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100).asString("%.00f %%"));
			
			Marquee marquee = new Marquee();
			marquee.textProperty().bind(tab.getTooltip().textProperty());
			
			stack.getChildren().addAll(indicator);
			stack.setManaged(false);
			stack.setVisible(true);
			
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
			
			//--Load Listener
			browser.addLoadListener(new LoadAdapter() {
				@Override
				public void onStartLoadingFrame(StartLoadingEvent event) {
					if (event.isMainFrame()) {
						System.out.println("Main frame has started loading");
						
						//Platform.runLater(() -> indicator.setVisible(true))
					}
				}
				
				@Override
				public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
					if (event.isMainFrame()) {
						System.out.println("Provisional load was committed for a frame");
						
						//Platform.runLater(() -> indicator.setVisible(true))
					}
				}
				
				@Override
				public void onFinishLoadingFrame(FinishLoadingEvent event) {
					if (event.isMainFrame()) {
						System.out.println("Main frame has finished loading");
						
					}
				}
				
				@Override
				public void onFailLoadingFrame(FailLoadingEvent event) {
					NetError errorCode = event.getErrorCode();
					if (event.isMainFrame()) {
						System.out.println("Main frame has failed loading: " + errorCode);
					}
				}
				
				@Override
				public void onDocumentLoadedInFrame(FrameLoadEvent event) {
					System.out.println("Frame document is loaded.");
					String currentURL = browser.getURL();
					
					//Set Search Bar Text 			
					Platform.runLater(() -> searchBar.setText(currentURL));
					
				}
				
				@Override
				public void onDocumentLoadedInMainFrame(LoadEvent event) {
					System.out.println("Main frame document is loaded.");
					
					//Backward and Forward buttons
					boolean backwardDisabled = browser.getCurrentNavigationEntry().getURL().equals(browser.getNavigationEntryAtIndex(1).getURL());
					boolean forwardDisabled = browser.getCurrentNavigationEntry().getURL()
							.equals(browser.getNavigationEntryAtIndex(browser.getNavigationEntryCount() - 1).getURL());
					
					//Strings
					String currentTitle = browser.getTitle();
					String currentURL = browser.getURL();
					
					//Run On JavaFX Thread
					Platform.runLater(() -> {
						
						backwardButton.setDisable(backwardDisabled);
						forwardButton.setDisable(forwardDisabled);
						
						//Tab Title
						tab.getTooltip().setText(currentTitle);
						
						//Set Search Bar Text 
						searchBar.setText(currentURL);
					});
					
					//Determine FavIcon
					new Thread(() -> determineFavIcon(currentURL)).start();
				}
			});
			
			//facIconImageView 
			facIconImageView.setFitWidth(25);
			facIconImageView.setFitHeight(25);
			facIconImageView.setSmooth(true);
			
			//iconLabel
			Label iconLabel = new Label();
			iconLabel.setGraphic(facIconImageView);
			iconLabel.setStyle("-fx-background-color:#202020");
			
			//X Button
			JFXButton closeButton = new JFXButton("X");
			int maxSize = 25;
			closeButton.setMinSize(maxSize, maxSize);
			closeButton.setPrefSize(maxSize, maxSize);
			closeButton.setMaxSize(maxSize, maxSize);
			closeButton.setStyle("-fx-background-radius:0; -fx-font-size:8px");
			closeButton.setOnAction(a -> this.webBrowserController.removeTab(tab));
			
			// HBOX
			HBox hBox = new HBox();
			hBox.setStyle("-fx-background-color:#000000;");
			hBox.setOnMouseClicked(m -> {
				if (m.getButton() == MouseButton.MIDDLE)
					webBrowserController.removeTab(tab);
			});
			hBox.getChildren().addAll(iconLabel, stack, marquee, closeButton);
			tab.setGraphic(hBox);
			
			//ContextMenu
			tab.setContextMenu(new WebBrowserTabContextMenu(this, webBrowserController));
			
			//-------------------Items------------------------
			
			searchBar.setOnAction(a -> loadWebSite(searchBar.getText()));
			searchBar.focusedProperty().addListener((observable , oldValue , newValue) -> {
				if (newValue)
					Platform.runLater(() -> searchBar.selectAll());
			});
			
			//Proposing sites
			//new AutoCompleteTextField().bindAutoCompletion(searchBar, 15, true, WebBrowserController.WEBSITE_PROPOSALS)
			
			//goButton
			goButton.setOnAction(searchBar.getOnAction());
			
			//reloadButton
			reloadButton.setOnAction(a -> reloadWebSite());
			
			//backwardButton
			forwardButton.setDisable(true);
			backwardButton.setOnAction(a -> goBack());
			backwardButton.setOnMouseReleased(m -> {
				if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
					webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
							webBrowserController.createNewTab(browser.getNavigationEntryAtIndex(browser.getCurrentNavigationEntryIndex() - 1).getURL()).getTab());
			});
			
			//forwardButton
			forwardButton.setDisable(true);
			forwardButton.setOnAction(a -> goForward());
			forwardButton.setOnMouseReleased(m -> {
				if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
					webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
							webBrowserController.createNewTab(browser.getNavigationEntryAtIndex(browser.getCurrentNavigationEntryIndex() + 1).getURL()).getTab());
			});
			
			//searchEngineComboBox
			searchEngineComboBox.getItems().addAll("Google", "DuckDuckGo", "Bing", "Yahoo");
			searchEngineComboBox.getSelectionModel().select(1);
			
			//movingTitleAnimation
			movingTitleAnimation.selectedProperty().addListener((observable , oldValue , newValue) -> {
				marquee.checkAnimationValidity(newValue);
			});
			movingTitleAnimation.setSelected(WebBrowserController.MOVING_TITLES_ENABLED);
			
			//showVersion
			about.setOnAction(a -> JavaFXTools.createAlert("Browser Information", null, "Browser Version :" + WebBrowserController.VERSION + "\n" + "Created by: GOXR3PLUS STUDIO",
					AlertType.INFORMATION, StageStyle.UTILITY, Main.window, null).showAndWait());
			
			//goFullScreen
			goFullScreen.setOnAction(a -> webBrowserController.chromiumFullScreenController.goFullScreenMode(browserView, this));
			
			//Finally load the firstWebSite
			loadWebSite(firstWebSite);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Returns back the main domain of the given url for example https://duckduckgo.com/?q=/favicon.ico returns <br>
	 * https://duckduckgo.com
	 * 
	 * @param urlInput
	 * @return
	 */
	private String getHostName(String urlInput) {
		try {
			URL url = new URL(urlInput);
			return url.getProtocol() + "://" + url.getHost() + "/";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
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
	 * Get the default url home page for the selected search provider
	 * 
	 * @return Get the default url home page for the selected search provider
	 */
	public String getSelectedEngineHomeUrl() {
		return getSearchEngineHomeUrl(searchEngineComboBox.getSelectionModel().getSelectedItem());
	}
	
	/**
	 * Loads the given website , either directly if the url is a valid WebSite Url or using a SearchEngine like Google
	 * 
	 * @param webSite
	 */
	private void loadWebSite(String webSite) {
		
		//Search if it is a valid WebSite url
		String load = !new UrlValidator().isValid(webSite) ? null : webSite;
		
		//Load
		try {
			
			//First Part
			String finalWebsiteFristPart = ( load != null ) ? load : getSelectedEngineHomeUrl();
			
			//Second Part
			String finalWebsiteSecondPart = "";
			if (searchBar.getText().isEmpty())
				finalWebsiteSecondPart = "";
			else {
				switch (searchEngineComboBox.getSelectionModel().getSelectedItem().toLowerCase()) {
					case "bing":
					case "duckduckgo":
						finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
					case "yahoo": //I need to find a solution for this
						finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
					default: //then google
						finalWebsiteSecondPart = "//search?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
				}
				
			}
			
			//Load it 
			browser.loadURL(finalWebsiteFristPart + finalWebsiteSecondPart);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Loads the default website
	 */
	public void loadDefaultWebSite() {
		browser.loadURL(getSelectedEngineHomeUrl());
	}
	
	/**
	 * Loads the current website , or if none then loads the default website
	 */
	public void reloadWebSite() {
		browser.reload();
	}
	
	/**
	 * Goes Backward one Page
	 * 
	 */
	public void goBack() {
		browser.goBack();
	}
	
	/**
	 * Goes Forward one Page
	 * 
	 */
	public void goForward() {
		browser.goForward();
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
		
		//tryAgainIndicator
		tryAgainIndicator.setVisible(true);
		
		//Check for internet connection
		Thread thread = new Thread(() -> {
			boolean hasInternet = InfoTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {
				
				//Visibility of error pane
				errorPane.setVisible(!hasInternet);
				
				//Visibility of Try Again Indicator
				tryAgainIndicator.setVisible(false);
				
				//Reload the website if it has internet
				if (hasInternet)
					reloadWebSite();
			});
		}, "Internet Connection Tester Thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Determines if the tab title will have a moving animation or not
	 * 
	 * @param value
	 */
	public void setMovingTitleEnabled(boolean value) {
		movingTitleAnimation.setSelected(value);
	}
	
	/**
	 * Find the favicon for the current website
	 */
	private void determineFavIcon(String url) {
		
		try {
			//Determine the full url
			String favIconFullURL = getHostName(url) + "favicon.ico";
			//System.out.println(favIconFullURL)
			
			//Create HttpURLConnection 
			HttpURLConnection httpcon = (HttpURLConnection) new URL(favIconFullURL).openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
			List<BufferedImage> image = ICODecoder.read(httpcon.getInputStream());
			
			//Set the favicon
			Platform.runLater(() -> facIconImageView.setImage(SwingFXUtils.toFXImage(image.get(0), null)));
			
		} catch (Exception ex) {
			//ex.printStackTrace();
			Platform.runLater(() -> facIconImageView.setImage(null));
		}
	}
	
	/**
	 * @return the borderPane
	 */
	public BorderPane getBorderPane() {
		return borderPane;
	}
	
	/**
	 * @return the browser
	 */
	public Browser getBrowser() {
		return browser;
	}
	
	/**
	 * @param browser
	 *            the browser to set
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}
	
	/**
	 * Right Click ContextMenuHandler
	 * 
	 * @author GOXR3PLUSSTUDIO
	 *
	 */
	private static class MyContextMenuHandler implements ContextMenuHandler {
		
		private final Pane pane;
		
		/**
		 * Constructor
		 * 
		 * @param parent
		 */
		private MyContextMenuHandler(Pane parent) {
			this.pane = parent;
		}
		
		public void showContextMenu(final ContextMenuParams params) {
			Platform.runLater(() -> createAndDisplayContextMenu(params));
		}
		
		private void createAndDisplayContextMenu(final ContextMenuParams params) {
			final ContextMenu contextMenu = new ContextMenu();
			
			// If there's link under mouse pointer, create and add
			// the "Open link in new window" menu item to our context menu
			if (!params.getLinkText().isEmpty())
				contextMenu.getItems().add(createMenuItem("Open link in new Tab", () -> Main.webBrowser.addNewTabOnTheEnd(params.getLinkURL())));
			
			// Create and add "Reload" menu item to our context menu
			contextMenu.getItems().add(createMenuItem("Reload", () -> params.getBrowser().reload()));
			
			// Display context menu at required location on screen
			java.awt.Point location = params.getLocation();
			Point2D screenLocation = pane.localToScreen(location.x, location.y);
			contextMenu.show(Main.window, screenLocation.getX(), screenLocation.getY());
		}
		
		private static MenuItem createMenuItem(String title , final Runnable action) {
			MenuItem menuItem = new MenuItem(title);
			menuItem.setOnAction(a -> action.run()
			
			);
			return menuItem;
		}
	}
	
}
