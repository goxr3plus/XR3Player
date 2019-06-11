package com.goxr3plus.xr3player.controllers.dropbox;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.view.javafx.BrowserView;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Opens a browser inside the application for DropBox Authentication Process
 * 
 * @author GOXR3PLUS
 *
 */
public class DropboxAuthanticationBrowser extends StackPane {

	@FXML
	private BorderPane borderPane;

	@FXML
	private ProgressIndicator loadingIndicator;

	// ----------------------------------------------------------------

	private Engine engine ;
	private Browser browser;

	/**
	 * AccessToken Property
	 */
	private final StringProperty accessToken = new SimpleStringProperty("");

	/**
	 * The window
	 */
	private final Stage window = new Stage();

	// Identifying information about XR3Player
	private DbxAppInfo appInfo = new DbxAppInfo("5dx1fba89qsx2l6", "z2avrmbnnrmwvqa");
	DbxRequestConfig requestConfig = new DbxRequestConfig("XR3Player");
	DbxWebAuth webAuth;
	DbxWebAuth.Request webAuthRequest;

	/**
	 * Constructor
	 */
	public DropboxAuthanticationBrowser() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.DROPBOX_FXMLS + "DropboxAuthanticationBrowser.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Create the Window
		window.setTitle("Dropbox Sign In");
		ObservableList<Image> i1;
		i1 = window.getIcons();
		Image i2;
		i2 = InfoTool.getImageFromResourcesFolder("icon.png");
		i1.add(i2);
		window.initModality(Modality.APPLICATION_MODAL);
		Scene s1;
		s1 = new Scene(this);
		window.setScene(s1);
	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		engine =  Engine.newInstance(
			EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
				// The language used on the default error pages and GUI.
				.language(Language.ENGLISH_US)
				// The absolute path to the directory where the data
				// such as cache, cookies, history, GPU cache, local
				// storage, visited links, web data, spell checking
				// dictionary files, etc. is stored.
//				.userDataDir(Paths.get("/Users/Me/JxBrowser/UserData"))
				.build());

		// Browser
		browser = engine.newBrowser();

		borderPane.setCenter(BrowserView.newInstance(browser));
		browser.navigation().on(FrameLoadFinished.class, event -> {
//				if (event.isMainFrame()) {
					String currentURL = event.url();
					if ("https://www.dropbox.com/1/oauth2/authorize_submit".equals(currentURL)) {
						String html = event.frame().html();
						new Thread(() -> {
							try {
								String code = Jsoup.parse(html).body().getElementById("auth-code")
										.getElementsByTag("input").first().attr("data-token");
								Platform.runLater(() -> produceAccessToken(code));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}).start();
					}
//				}
		});

	}

	/**
	 * Shows the window
	 */
	public void showAuthenticationWindow() {

		/* Clearing HTTP Cache */
		engine.httpCache().clearDiskCache(()->{});

		/* Clearing All Cookies */
		engine.cookieStore().deleteAll();

		// Load it
		browser.navigation().loadUrl(getAuthonticationRequestURL());

		// Show the Window
		window.show();
	}

	/**
	 * Starts authorization and returns a "authorization URL" on the Dropbox website
	 * that gives the lets the user grant your app access to their Dropbox account.
	 * 
	 * @return The "authorization URL" on the Dropbox website that gives the lets
	 *         the user grant your app access to their Dropbox account.
	 */
	public String getAuthonticationRequestURL() {

		// Run through Dropbox API authorization process
		webAuth = new DbxWebAuth(requestConfig, appInfo);
		webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();

		return webAuth.authorize(webAuthRequest);
	}

	/**
	 * Given the Authorization Code it produces the AccessToken needed to access
	 * DropBox Account
	 * 
	 * @param code The OAuth2 DropBox AuthorizationCode
	 */
	public void produceAccessToken(String code) {
		try {
			// Run through Dropbox API authorization process
			DbxAuthFinish authFinish = webAuth.finishFromCode(code);

			// Set the access token
			accessToken.set(authFinish.getAccessToken());

			// System.out.println("Browser -> [" + accessToken.get() + "]")

			// Close the window
			window.close();
		} catch (DbxException ex) {
			ex.printStackTrace();
			AlertTool.showNotification("Error", "Error during DropBox Authentication \n please try again :)",
					Duration.millis(2000), NotificationType.ERROR);
		}
	}

	/**
	 * @return the accessToken
	 */
	public StringProperty accessTokenProperty() {
		return accessToken;
	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

	/**
	 * @return the loadingIndicator
	 */
	public ProgressIndicator getLoadingIndicator() {
		return loadingIndicator;
	}

	/**
	 * @return the browser
	 */
	public Browser getBrowser() {
		return browser;
	}

}
