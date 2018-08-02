/*
 * 
 */
package main.java.com.goxr3plus.xr3player.smartcontroller.presenter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Audio;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.tags.TagTabCategory;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class MediaContextMenu extends ContextMenu {
	
	//--------------------------------------------------------------
	
	@FXML
	private Menu startPlayer;
	
	@FXML
	private MenuItem startOnPlayer0;
	
	@FXML
	private MenuItem startOnPlayer1;
	
	@FXML
	private MenuItem startOnPlayer2;
	
	@FXML
	private Menu stopPlayer;
	
	@FXML
	private MenuItem stopPlayer0;
	
	@FXML
	private MenuItem stopPlayer1;
	
	@FXML
	private MenuItem stopPlayer2;
	
	@FXML
	private Menu getInfoBuy;
	
	@FXML
	private Menu findLyrics;
	
	@FXML
	private MenuItem lyricFinderOrg;
	
	@FXML
	private MenuItem lyricsCom;
	
	@FXML
	private MenuItem stars;
	
	@FXML
	private MenuItem exportFiles;
	
	@FXML
	private MenuItem markAsPlayed;
	
	@FXML
	private MenuItem rename;
	
	@FXML
	private MenuItem copy;
	
	@FXML
	private MenuItem paste;
	
	@FXML
	private MenuItem removeMedia;
	
	@FXML
	private MenuItem showFile;
	
	@FXML
	private MenuItem editFileInfo;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * The node based on which the Rename or Star Window will be position
	 */
	private Node node;
	
	/** The media. */
	private Media media;
	
	/** The controller. */
	private SmartController controller;
	
	/** The previous genre. */
	Genre previousGenre = Genre.UNKNOWN;
	
	private final String encoding = "UTF-8";
	
	private final ShopContextMenu shopContextMenu = new ShopContextMenu();
	
	/**
	 * Constructor.
	 */
	public MediaContextMenu() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "MediaContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		getInfoBuy.getItems().addAll(shopContextMenu.getItems());
	}
	
	/**
	 * Shows the context menu based on the variables below.
	 *
	 * @param media
	 *            Given media file
	 * @param genre
	 *            the genre
	 * @param x
	 *            Horizontal mouse position on the screen
	 * @param y
	 *            Vertical mouse position on the screen
	 * @param controller1
	 *            The SmartController that is calling this method
	 * @param node
	 */
	public void showContextMenu(Media media , Genre genre , double x , double y , SmartController controller1 , Node node) {
		
		// Don't waste resources
		if (previousGenre != genre)
			if (genre == Genre.BUYBUTTON) { //Smart trick
				getItems().forEach(item -> item.setVisible(false));
				getInfoBuy.setVisible(true);
			} else if (media.getSmartControllerGenre() == Genre.LIBRARYMEDIA)
				getItems().forEach(item -> item.setVisible(true));
			else if (media.getSmartControllerGenre() == Genre.SEARCHWINDOW) {
				getItems().forEach(item -> item.setVisible(true));
				removeMedia.setVisible(false);
			} else
				getItems().forEach(item -> item.setVisible(true));
			
		//Update ShopContextMenu
		shopContextMenu.updateMediaTitle(media.getTitle());
		
		//Determine the image
		for (int i = 0; i <= 2; i++) {
			boolean playerEnergized = Main.xPlayersList.getXPlayer(i).isOpened() || Main.xPlayersList.getXPlayer(i).isPausedOrPlaying()
					|| Main.xPlayersList.getXPlayer(i).isSeeking();
			
			//PlayFontIcon
			FontIcon playFontIcon = new FontIcon("fas-play-circle");
			playFontIcon.setIconSize(24);
			playFontIcon.setIconColor(Color.web("#ceff26"));
			
			//StopFontIcon
			FontIcon stopFontIcon = new FontIcon("fas-stop-circle");
			stopFontIcon.setIconSize(24);
			stopFontIcon.setIconColor(Color.web("#ff3c26"));
			
			//Set it to the items
			startPlayer.getItems().get(i).setGraphic(!playerEnergized ? null : playFontIcon);
			stopPlayer.getItems().get(i).setGraphic(!playerEnergized ? null : stopFontIcon);
		}
		
		//Mark Played or Not Played
		this.markAsPlayed.setText("Mark as " + ( !Main.playedSongs.containsFile(media.getFilePath()) ? "Played" : "Not Played" ) + " (CTRL+U)");
		
		this.node = node;
		this.media = media;
		this.controller = controller1;
		
		//Fix first time show problem
		if (super.getWidth() == 0) {
			show(Main.window);
			hide();
		}
		
		// Show it
		show( ( Main.mediaSearchWindow.getWindow().isShowing() && Main.mediaSearchWindow.getWindow().isFocused() ) ? Main.mediaSearchWindow.getWindow() : Main.window,
				x - super.getWidth(), y - 1);
		previousGenre = genre;
		
		//------------Animation------------------
		
		//Y axis
		double yIni = y - 50;
		double yEnd = y;
		super.setY(yIni);
		
		//X axis
		//	double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		//	double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		//	super.setX(xIni);
		//	final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		//	xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));
		
		//Create  Double Property
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob , n , n1) -> super.setY(n1.doubleValue()));
		
		//Create Time Line
		Timeline timeIn = new Timeline(new KeyFrame(Duration.seconds(0.30), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		//new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd, Interpolator.EASE_BOTH)))
		timeIn.play();
		//------------ END of Animation------------------
		
	}
	
	/**
	 * Open the given website on the build in Chromium
	 * 
	 * @param url
	 */
	private void openWebSite(String url) {
		Main.webBrowser.createTabAndSelect(url);
		Main.topBar.selectTab(Main.topBar.getWebModeTab());
	}
	
	/**
	 * @param e
	 */
	@FXML
	public void action(ActionEvent e) {
		Object source = e.getSource();
		
		// --------------------play on deck 0
		if (source == startOnPlayer0) {
			( (Audio) media ).playOnDeck(0, controller);
			
			// play on deck 1
		} else if (source == startOnPlayer1) {
			( (Audio) media ).playOnDeck(1, controller);
			
			// play on deck 2
		} else if (source == startOnPlayer2) {
			
			( (Audio) media ).playOnDeck(2, controller);
			
			// ------------------stop deck 0
		} else if (source == stopPlayer0) {
			Main.xPlayersList.getXPlayer(0).stop();
			
			// stop deck 1
		} else if (source == stopPlayer1) {
			Main.xPlayersList.getXPlayer(1).stop();
			
			// stop deck 2
		} else if (source == stopPlayer2) {
			Main.xPlayersList.getXPlayer(2).stop();
		}
		
		//markAsPlayed
		else if (source == markAsPlayed) {
			if (!Main.playedSongs.containsFile(media.getFilePath())) {
				Main.playedSongs.add(media.getFilePath(), true);
				Main.playedSongs.appendToTimesPlayed(media.getFilePath(), true);
			} else {
				if (Main.playedSongs.remove(media.getFilePath(), true))
					media.timesPlayedProperty().set(0);
			}
		}
		
		// remove media
		else if (source == removeMedia)
			controller.prepareDelete(false);
		
		// rename
		else if (source == rename)
			media.rename(node);
		else if (source == copy)
			controller.getNormalModeMediaTableViewer().copySelectedMediaToClipBoard();
		else if (source == paste)
			controller.getNormalModeMediaTableViewer().pasteMediaFromClipBoard();
		else if (source == stars)
			media.updateStars(node);
		else if (source == showFile) // File path
			ActionTool.openFileLocation(media.getFilePath());
		else if (source == editFileInfo) {
			//More than 1 selected?
			if (controller.getNormalModeMediaTableViewer().getSelectedCount() > 1)
				Main.tagWindow.openMultipleAudioFiles(
						controller.getNormalModeMediaTableViewer().getSelectionModel().getSelectedItems().stream().map(Media::getFilePath)
								.collect(Collectors.toCollection(FXCollections::observableArrayList)),
						controller.getNormalModeMediaTableViewer().getSelectionModel().getSelectedItem().getFilePath());
			//Only one file selected
			else
				Main.tagWindow.openAudio(media.getFilePath(), TagTabCategory.BASICINFO, true);
		} else if (source == exportFiles) { // copyTo
			Main.exportWindow.show(controller);
		} else {
			try {
				
				//-----------------------FIND LYRICS------------------------------------------------
				if (source == lyricFinderOrg)
					openWebSite("http://search.lyricfinder.org/?query=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == lyricsCom)
					openWebSite("http://www.lyrics.com/lyrics/" + URLEncoder.encode(media.getTitle(), encoding));
				
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
}
