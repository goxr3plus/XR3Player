/*
 * 
 */
package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.general.TopBar.WindowMode;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class ShopContextMenu extends ContextMenu {

	// --------------------------------------------------------------

	@FXML
	private MenuItem amazonUS;

	@FXML
	private MenuItem amazonUK;

	@FXML
	private MenuItem amazonCanada;

	@FXML
	private MenuItem amazonGermany;

	@FXML
	private MenuItem amazonFrance;

	@FXML
	private MenuItem amazonSpain;

	@FXML
	private MenuItem amazonItaly;

	@FXML
	private MenuItem amazonJapan;

	@FXML
	private MenuItem amazonChina;

	@FXML
	private MenuItem soundCloud;

	@FXML
	private MenuItem jamendo;

	@FXML
	private MenuItem tuneIn;

	@FXML
	private MenuItem hDTracks;

	@FXML
	private MenuItem cDUniverse;

	@FXML
	private MenuItem lastfm;

	@FXML
	private MenuItem librefm;

	@FXML
	private MenuItem youtube;

	@FXML
	private MenuItem vimeo;

	@FXML
	private MenuItem google;

	@FXML
	private MenuItem duckduckgo;

	@FXML
	private MenuItem bing;

	@FXML
	private MenuItem yahoo;

	@FXML
	private MenuItem wikipedia;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * The node based on which the Rename or Star Window will be position
	 * 
	 * 
	 * /** The media.
	 */
	private String mediaTitle;

	private final String encoding = "UTF-8";

	/**
	 * Constructor.
	 */
	public ShopContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "ShopContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Set the mediaTitle
	 * 
	 * @param mediaTitle
	 */
	public void setMediaTitle(String mediaTitle) {
		this.mediaTitle = mediaTitle;
	}

	/**
	 * Shows the context menu based on the variables below.
	 *
	 * @param mediaTitle Given media title
	 * @param x     Horizontal mouse position on the screen
	 * @param y     Vertical mouse position on the screen
	 * 
	 */
	public void showContextMenu(String mediaTitle, double x, double y) {

		this.mediaTitle = mediaTitle;

		// Fix first time show problem
		if (super.getWidth() == 0) {
			show(Main.window);
			hide();
		}

		// Show it
		show((Main.mediaSearchWindow.getWindow().isShowing() && Main.mediaSearchWindow.getWindow().isFocused())
				? Main.mediaSearchWindow.getWindow()
				: Main.window, x - super.getWidth(), y - 1);

		// ------------Animation------------------

		// Y axis
		double yIni = y - 50;
		double yEnd = y;
		super.setY(yIni);

		// X axis
		// double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		// double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		// super.setX(xIni);
		// final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		// xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Create Double Property
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Create Time Line
		Timeline timeIn = new Timeline(
				new KeyFrame(Duration.seconds(0.30), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		// new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd,
		// Interpolator.EASE_BOTH)))
		timeIn.play();
		// ------------ END of Animation------------------

	}

	/**
	 * Open the given website on the build in Chromium
	 * 
	 * @param url
	 */
	private void openWebSite(String url) {
		Main.webBrowser.createTabAndSelect(url);
		Main.topBar.goMode(WindowMode.WEBMODE);
	}

	/**
	 * @param e
	 */
	@FXML
	public void action(ActionEvent e) {
		Object source = e.getSource();

		try {

			// ---------------------SEARCH ON
			// WEB--------------------------------------------
			// Music Sites
			if (source == soundCloud)
				openWebSite("https://soundcloud.com/search?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == jamendo)
				openWebSite("https://www.jamendo.com/search?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == tuneIn)
				openWebSite("http://tunein.com/search/?query=" + URLEncoder.encode(mediaTitle, encoding));
			// Amazon
			else if (source == amazonUS)
				openWebSite("https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonUK)
				openWebSite("https://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonCanada)
				openWebSite("https://www.amazon.ca/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonGermany)
				openWebSite("https://www.amazon.de/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonFrance)
				openWebSite("https://www.amazon.fr/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonSpain)
				openWebSite("https://www.amazon.es/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonItaly)
				openWebSite("https://www.amazon.it/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonJapan)
				openWebSite("https://www.amazon.co.jp/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));
			else if (source == amazonChina)
				openWebSite("https://www.amazon.cn/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
						+ URLEncoder.encode(mediaTitle, encoding));

			// Music Sites
			else if (source == hDTracks)
				openWebSite(
						"http://www.hdtracks.com/catalogsearch/result/?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == cDUniverse)
				openWebSite("http://www.cduniverse.com/sresult.asp?HT_Search=ALL&HT_Search_Info="
						+ URLEncoder.encode(mediaTitle, encoding) + "&style=all");

			// Radios
			else if (source == lastfm)
				openWebSite("https://www.last.fm/search?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == librefm)
				openWebSite("https://libre.fm/search.php?search_term=" + URLEncoder.encode(mediaTitle, encoding)
						+ "&search_type=artist");

			// Video WebSites
			else if (source == youtube)
				openWebSite("https://www.youtube.com/results?search_query=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == vimeo)
				openWebSite("https://vimeo.com/search?q=" + URLEncoder.encode(mediaTitle, encoding));

			// Search-Engines
			else if (source == google)
				openWebSite("https://www.google.com/search?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == duckduckgo)
				openWebSite("https://duckduckgo.com/?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == bing)
				openWebSite("http://www.bing.com/search?q=" + URLEncoder.encode(mediaTitle, encoding));
			else if (source == yahoo)
				openWebSite("https://search.yahoo.com/search?p=" + URLEncoder.encode(mediaTitle, encoding));

			// Wikipedia
			else if (source == wikipedia)
				openWebSite("https://www.wikipedia.org/wiki/Special:Search?search="
						+ URLEncoder.encode(mediaTitle, encoding));

		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}

	}

}
