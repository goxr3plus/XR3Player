/**
 * 
 */
package com.goxr3plus.xr3player.controllers.chromium;

import java.awt.Rectangle;
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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.jfoenix.controls.JFXButton;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserException;
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

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.custom.Marquee;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.NetworkingTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
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

	// ------------------------------------------------------------

	@FXML
	private BorderPane borderPane;

	@FXML
	private JFXButton goFullScreen;

	@FXML
	private JFXButton openInDefaultBrowser;

	@FXML
	private JFXButton backwardButton;

	@FXML
	private JFXButton reloadButton;

	@FXML
	private JFXButton forwardButton;

	@FXML
	private JFXButton homeButton;

	@FXML
	private TextField searchBar;

	@FXML
	private JFXButton copyText;

	@FXML
	private JFXButton goButton;

	@FXML
	private ToggleGroup searchEngineGroup;

	@FXML
	private CheckMenuItem movingTitleAnimation;

	@FXML
	private MenuItem about;

	@FXML
	private VBox errorPane;

	@FXML
	private JFXButton tryAgain;

	@FXML
	private ProgressIndicator tryAgainIndicator;

	// -------------------------------------------------------------

	/** The browser and it's View */
	private Browser browser;
	private BrowserView browserView;

	private final Tab tab;
	private final WebBrowserController webBrowserController;
	private final ImageView facIconImageView = new ImageView();
	private String firstWebSite;
	private JFXButton audioButton;
	private StackPane progressIndicatorStackPane;
	private ProgressIndicator progressIndicator;

	public StackedFontIcon soundStack;
	public final FontIcon mutedImage = new FontIcon("gmi-volume-off");
	public final FontIcon unmutedImage = new FontIcon("fas-volume-up");

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

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.BROWSER_FXMLS + "WebBrowserTabController.fxml"));
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

			// StackPane
			setOnKeyReleased(k -> {
				if (k.getCode() == KeyCode.F11)
					webBrowserController.chromiumFullScreenController.goFullScreenMode(browserView, this);
			});

			// ---------------ERROR PANE---
			// tryAgain
			tryAgain.setOnAction(a -> checkForInternetConnection());

			// -------------------Browser------------------------
			browser = new Browser();
			browserView = new BrowserView(browser);
			browser.setContextMenuHandler(new MyContextMenuHandler(browserView));
			browser.setPopupHandler(params -> (Browser popUpBrowser, Rectangle initialBounds) -> {

				// Check if the site is allowed to display popups
				String browser_url = browser.getURL();

				// Check for PopUps
				System.out.println("Browser URL: " + browser_url);
				if (browser_url.contains("fmovies.to"))
					return;

				// Show the Window
				Platform.runLater(() -> {

					// Create a Stage
					Stage stage = new Stage();
					StackPane root = new StackPane();
					Scene scene = new Scene(root, 800, 600);
					root.getChildren().add(new BrowserView(popUpBrowser));
					stage.setScene(scene);
					stage.setTitle("Popup");

					// Initial Bounds
					if (!initialBounds.isEmpty()) {
						stage.setX(initialBounds.getLocation().getX());
						stage.setY(initialBounds.getLocation().getY());
						stage.setWidth(initialBounds.width);
						stage.setHeight(initialBounds.height);
					}

					// On Stage Close Request
					stage.setOnCloseRequest(c -> popUpBrowser.dispose());

					// On Browser Dispose Listener
					popUpBrowser.addDisposeListener(disp -> Platform.runLater(stage::close));

					// Show Stage
					stage.show();

				});

			});

			// -------------------BorderPane------------------------
			borderPane.setCenter(browserView);

			// Continue

			// browser.pro
			// webEngine.getLoadWorker().exceptionProperty().addListener(error -> {
			// //System.out.println("WebEngine exception occured" + error.toString())
			// checkForInternetConnection();
			// });
			// com.sun.javafx.webkit.WebConsoleListener
			// .setDefaultListener((webView , message , lineNumber , sourceId) ->
			// System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " +
			// message));
			//
			// Add listener to the WebEngine
			// webEngine.getLoadWorker().stateProperty().addListener(new FavIconProvider());
			// webEngine.getLoadWorker().stateProperty().addListener(new
			// DownloadDetector());
			// webEngine.getLoadWorker().stateProperty().addListener((observable , oldState
			// , newState) -> {
			// if (newState == Worker.State.SUCCEEDED) {
			//
			// //Check for error pane
			// errorPane.setVisible(false);
			//
			// } else if (newState == Worker.State.FAILED) {
			//
			// //Check for error pane
			// errorPane.setVisible(true);
			// }
			// });
			//
			// webEngine.setOnError(error -> {
			// //System.out.println("WebEngine error occured")
			// checkForInternetConnection();
			// });

			// handle pop up windows
			// webEngine.setCreatePopupHandler(l ->
			// webBrowserController.createAndAddNewTab().getWebView().getEngine())

			// -------------------TAB------------------------
			tab.setTooltip(new Tooltip(""));

			// Graphic
			progressIndicatorStackPane = new StackPane();
			progressIndicatorStackPane.setPadding(new Insets(0, 5, 0, 5));
			progressIndicatorStackPane.setAlignment(Pos.CENTER);

			// indicator
			progressIndicator = new ProgressIndicator();
			progressIndicator.getStyleClass().add("dropbox-progress-indicator");
			progressIndicator.setMaxSize(20, 20);
			progressIndicatorStackPane.getChildren().add(progressIndicator);

			// // label
			// Label label = new Label();
			// label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			// label.setAlignment(Pos.CENTER);
			// label.setStyle("-fx-font-weight:bold; -fx-text-fill: white; -fx-font-size:10;
			// -fx-background-color: rgb(0,0,0,0.3);");
			// label.textProperty().bind(Bindings.max(0,
			// indicator.progressProperty()).multiply(100).asString("%.00f %%"));
			//
			Marquee marquee = new Marquee();
			marquee.textProperty().bind(tab.getTooltip().textProperty());

			// --Load Listener
			browser.addLoadListener(new LoadAdapter() {

				@Override
				public void onStartLoadingFrame(StartLoadingEvent event) {
					if (event.isMainFrame()) {
						System.out.println("Main frame has started loading");

					}
				}

				@Override
				public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
					if (event.isMainFrame()) {
						System.out.println("Provisional load was committed for a frame");

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

					// Set Search Bar Text
					Platform.runLater(() -> searchBar.setText(currentURL));

				}

				@Override
				public void onDocumentLoadedInMainFrame(LoadEvent event) {
					System.out.println("Main frame document is loaded.");

					// Backward and Forward buttons
					boolean backwardDisabled = browser.getCurrentNavigationEntry().getURL()
							.equals(browser.getNavigationEntryAtIndex(1).getURL());
					boolean forwardDisabled = browser.getCurrentNavigationEntry().getURL()
							.equals(browser.getNavigationEntryAtIndex(browser.getNavigationEntryCount() - 1).getURL());

					// Strings
					String currentTitle = browser.getTitle();
					String currentURL = browser.getURL();

					// Run On JavaFX Thread
					Platform.runLater(() -> {

						backwardButton.setDisable(backwardDisabled);
						forwardButton.setDisable(forwardDisabled);

						// Tab Title
						tab.getTooltip().setText(currentTitle);

						// Set Search Bar Text
						searchBar.setText(currentURL);
					});

					// Determine FavIcon
					new Thread(() -> determineFavIcon(currentURL)).start();
				}
			});

			// facIconImageView
			facIconImageView.setFitWidth(20);
			facIconImageView.setFitHeight(20);
			facIconImageView.setSmooth(true);

			// iconLabel
			Label iconLabel = new Label();
			iconLabel.setPadding(new Insets(0, 5, 0, 5));
			iconLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			iconLabel.setAlignment(Pos.CENTER);
			iconLabel.setFocusTraversable(false);
			int maxSize = 25;
			iconLabel.setMinSize(maxSize, maxSize);
			iconLabel.setPrefSize(maxSize, maxSize);
			iconLabel.setMaxSize(maxSize, maxSize);
			iconLabel.setGraphic(facIconImageView);
			iconLabel.setStyle("-fx-background-color:#303030");

			// Loading

			// X Button
			JFXButton closeButton = new JFXButton("X");
			maxSize = 25;
			closeButton.setFocusTraversable(false);
			closeButton.setMinSize(maxSize, maxSize);
			closeButton.setPrefSize(maxSize, maxSize);
			closeButton.setMaxSize(maxSize, maxSize);
			closeButton.setStyle("-fx-background-radius:0; -fx-font-size:8px");
			closeButton.setOnAction(a -> this.webBrowserController.removeTab(tab));

			// Audio Button
			audioButton = new JFXButton("");
			audioButton.setVisible(false);
			audioButton.setFocusTraversable(false);
			audioButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			soundStack = new StackedFontIcon();
			mutedImage.setIconSize(24);
			mutedImage.setIconColor(Color.WHITE);
			unmutedImage.setIconSize(20);
			unmutedImage.setIconColor(Color.WHITE);
			soundStack.getChildren().addAll(mutedImage, unmutedImage);

			maxSize = 0;
			audioButton.setMinSize(maxSize, maxSize);
			audioButton.setPrefSize(maxSize, maxSize);
			audioButton.setMaxSize(maxSize, maxSize);
			audioButton.setStyle("-fx-background-radius:0; -fx-font-size:8px");
			audioButton.setOnAction(a -> browser.setAudioMuted(!browser.isAudioMuted()));
			audioButton.setGraphic(soundStack);

			// HBOX
			HBox hBox = new HBox();
			hBox.setStyle("-fx-background-color:#000000;");
			hBox.setOnMouseClicked(m -> {
				if (m.getButton() == MouseButton.MIDDLE)
					webBrowserController.removeTab(tab);
			});
			hBox.getChildren().addAll(progressIndicatorStackPane, iconLabel, marquee, audioButton, closeButton);
			tab.setGraphic(hBox);

			// ContextMenu
			tab.setContextMenu(new WebBrowserTabContextMenu(this, webBrowserController));

			// -------------------Items------------------------

			searchBar.setOnAction(a ->

			loadWebSite(searchBar.getText()));
			searchBar.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue)
					Platform.runLater(() -> searchBar.selectAll());
			});

			// Proposing sites
			// new AutoCompleteTextField().bindAutoCompletion(searchBar, 15, true,
			// WebBrowserController.WEBSITE_PROPOSALS)

			// goButton
			goButton.setOnAction(searchBar.getOnAction());

			// reloadButton
			reloadButton.setOnAction(a -> reloadWebSite());

			// backwardButton
			forwardButton.setDisable(true);
			backwardButton.setOnAction(a -> goBack());
			backwardButton.setOnMouseReleased(m -> {
				if (m.getButton() == MouseButton.MIDDLE) // Create and add it next to this tab
					webBrowserController.getTabPane().getTabs().add(
							webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
							webBrowserController.createNewTab(browser
									.getNavigationEntryAtIndex(browser.getCurrentNavigationEntryIndex() - 1).getURL())
									.getTab());
			});

			// forwardButton
			forwardButton.setDisable(true);
			forwardButton.setOnAction(a -> goForward());
			forwardButton.setOnMouseReleased(m -> {
				if (m.getButton() == MouseButton.MIDDLE) // Create and add it next to this tab
					webBrowserController.getTabPane().getTabs().add(
							webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
							webBrowserController.createNewTab(browser
									.getNavigationEntryAtIndex(browser.getCurrentNavigationEntryIndex() + 1).getURL())
									.getTab());
			});

			// homeButton
			homeButton.setOnAction(a -> loadDefaultWebSite());

			// movingTitleAnimation
			movingTitleAnimation.selectedProperty().addListener((observable, oldValue, newValue) -> marquee.checkAnimationValidity(newValue));
			movingTitleAnimation.setSelected(WebBrowserController.MOVING_TITLES_ENABLED);

			// showVersion
			about.setOnAction(a -> JavaFXTool.createAlert("Browser Information", null,
					"Browser Version :" + WebBrowserController.VERSION + "\n" + "Created by: GOXR3PLUS STUDIO",
					AlertType.INFORMATION, StageStyle.UTILITY, Main.window, null).showAndWait());

			// goFullScreen
			goFullScreen.setOnAction(
					a -> webBrowserController.chromiumFullScreenController.goFullScreenMode(browserView, this));

			// Finally load the firstWebSite
			loadWebSite(firstWebSite);

			// openInDefaultBrowser
			openInDefaultBrowser.setOnAction(a -> NetworkingTool.openWebSite(browser.getURL()));

			// copyText
			copyText.setOnAction(a -> {
				// Get Native System ClipBoard
				final Clipboard clipboard = Clipboard.getSystemClipboard();
				final ClipboardContent content = new ClipboardContent();

				// PutFiles
				content.putString(searchBar.getText());

				// Set the Content
				clipboard.setContent(content);

				// Notification
				AlertTool.showNotification("Copied to Clipboard",
						"Search bar text copied to clipboard,you can paste it anywhere on the your system.\nFor example in Windows with [CTRL+V], in Mac[COMMAND+V]",
						Duration.seconds(2), NotificationType.INFORMATION);
			});
		} catch (BrowserException ex) {
			System.exit(-1);
		} catch (Exception ex) {
			ex.printStackTrace();
			// The Chromium profile directory is already used/locked by another
			// BrowserContext instance or process.
		}
	}

	/**
	 * Returns back the main domain of the given url for example
	 * https://duckduckgo.com/?q=/favicon.ico returns <br>
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
	 * Return the Search Url for the Search Provider For example for `Google`
	 * returns `https://www.google.com/search?q=`
	 * 
	 * @param searchProvider
	 * @return The Search Engine Url
	 */
	public String getSearchEngineHomeUrl(String searchProvider) {
		// Find
		switch (searchProvider.toLowerCase()) {
		case "bing":
			return "http://www.bing.com";
		case "duckduckgo":
			return "https://duckduckgo.com";
		case "yahoo":
			return "https://search.yahoo.com";
		default: // then google
			return "https://www.google.com";
		}
	}

	/**
	 * Get the default url home page for the selected search provider
	 * 
	 * @return Get the default url home page for the selected search provider
	 */
	public String getSelectedEngineHomeUrl() {
		return getSearchEngineHomeUrl(((RadioMenuItem) searchEngineGroup.getSelectedToggle()).getText());
	}

	/**
	 * Loads the given website , either directly if the url is a valid WebSite Url
	 * or using a SearchEngine like Google
	 * 
	 * @param webSite
	 */
	private void loadWebSite(String webSite) {

		// Search if it is a valid WebSite url
		String load = !new UrlValidator().isValid(webSite) ? null : webSite;

		// Load
		try {

			// First Part
			String finalWebsiteFristPart = (load != null) ? load : getSelectedEngineHomeUrl();

			// Second Part
			String finalWebsiteSecondPart = "";
			if (searchBar.getText().isEmpty())
				finalWebsiteSecondPart = "";
			else {
				switch (((RadioMenuItem) searchEngineGroup.getSelectedToggle()).getText()) {
				case "bing":
				case "duckduckgo":
					finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
					break;
				case "yahoo": // I need to find a solution for this
					finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
					break;
				default: // then google
					finalWebsiteSecondPart = "//search?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
					break;
				}

			}

			// Load it
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

		// tryAgainIndicator
		tryAgainIndicator.setVisible(true);

		// Check for internet connection
		Thread thread = new Thread(() -> {
			boolean hasInternet = NetworkingTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {

				// Visibility of error pane
				errorPane.setVisible(!hasInternet);

				// Visibility of Try Again Indicator
				tryAgainIndicator.setVisible(false);

				// Reload the website if it has internet
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
			// Determine the full url
			String favIconFullURL = getHostName(url) + "favicon.ico";
			// System.out.println(favIconFullURL)

			// Create HttpURLConnection
			HttpURLConnection httpcon = (HttpURLConnection) new URL(favIconFullURL).openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
			List<BufferedImage> image = ICODecoder.read(httpcon.getInputStream());

			// Set the favicon
			Platform.runLater(() -> facIconImageView.setImage(SwingFXUtils.toFXImage(image.get(0), null)));

		} catch (Exception ex) {
			// ex.printStackTrace()
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
	 * @return the audioButton
	 */
	public JFXButton getAudioButton() {
		return audioButton;
	}

	/**
	 * @return the progressIndicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}

	/**
	 * @return the progressIndicatorStackPane
	 */
	public StackPane getProgressIndicatorStackPane() {
		return progressIndicatorStackPane;
	}

	public BrowserView getBrowserView() {
		return browserView;
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

		public void showContextMenu(final ContextMenuParams p) {
			Platform.runLater(() -> createAndDisplayContextMenu(p));
		}

		private void createAndDisplayContextMenu(final ContextMenuParams p) {
			final ContextMenu contextMenu = new ContextMenu();

			// If there's link under mouse pointer, create and add
			// the "Open link in new window" menu item to our context menu
			if (!p.getLinkText().isEmpty())
				contextMenu.getItems().add(createMenuItem("Open link in new Tab",
						() -> Main.webBrowser.addNewTabOnTheEnd(p.getLinkURL())));

			// Create and add "Reload" menu item to our context menu
			contextMenu.getItems().add(createMenuItem("Reload", () -> p.getBrowser().reload()));

			// Display context menu at required location on screen
			java.awt.Point location = p.getLocation();
			Point2D screenLocation = pane.localToScreen(location.x, location.y);
			contextMenu.show(Main.window, screenLocation.getX(), screenLocation.getY());
		}

		private static MenuItem createMenuItem(String title, final Runnable action) {
			MenuItem menuItem = new MenuItem(title);
			menuItem.setOnAction(a -> action.run()

			);
			return menuItem;
		}
	}

}
