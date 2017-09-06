/*
 * 
 */
package smartcontroller.media;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import smartcontroller.Genre;
import smartcontroller.SmartController;

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
	
	@FXML
	private Menu findLyrics;
	
	@FXML
	private MenuItem lyricFinderOrg;
	
	@FXML
	private MenuItem lyricsCom;
	
	@FXML
	private MenuItem markAsPlayed;
	
	@FXML
	private MenuItem stars;
	
	@FXML
	private MenuItem copyOrMove;
	
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
	private MenuItem properties;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private final Image soundWave = InfoTool.getImageFromResourcesFolder("Audio Wave Filled-24.png");
	
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
	
	String encoding = "UTF-8";
	
	/**
	 * Constructor.
	 */
	public MediaContextMenu() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MediaContextMenu.fxml"));
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
		
		properties.setDisable(true);
	}
	
	/**
	 * Shows the context menu based on the variables below.
	 *
	 * @param media1
	 *            the media
	 * @param genre
	 *            the genre
	 * @param x
	 *            the d
	 * @param y
	 *            the e
	 * @param controller1
	 *            The smartcontroller that is calling this method
	 * @param node
	 */
	public void showContextMenu(Media media1 , Genre genre , double x , double y , SmartController controller1 , Node node) {
		
		// Don't waste resources
		if (previousGenre != genre)
			if (media1.getGenre() == Genre.LIBRARYMEDIA)
				getItems().forEach(item -> item.setVisible(true));
			else if (media1.getGenre() == Genre.SEARCHWINDOW)
				removeMedia.setVisible(false);
			
		//Determine the image
		for (int i = 0; i <= 2; i++) {
			boolean playerEnergized = Main.xPlayersList.getXPlayer(i).isOpened() || Main.xPlayersList.getXPlayer(i).isPausedOrPlaying()
					|| Main.xPlayersList.getXPlayer(i).isSeeking();
			( (ImageView) startPlayer.getItems().get(i).getGraphic() ).setImage(!playerEnergized ? null : soundWave);
			( (ImageView) stopPlayer.getItems().get(i).getGraphic() ).setImage(!playerEnergized ? null : soundWave);
		}
		
		//Mark Played/Unplayed
		this.markAsPlayed.setText("Mark as " + ( Main.playedSongs.containsFile(media1.getFilePath()) ? "Unplayed" : "Played" ) + " (CTRL+U)");
		
		this.node = node;
		this.media = media1;
		this.controller = controller1;
		
		// Show it
		show(Main.window, x - super.getWidth(), y - 1);
		previousGenre = genre;
		
		//Y axis
		double yIni = y - 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob , n , n1) -> super.setY(n1.doubleValue()));
		
		//X axis
		//	double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		//	double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		//	super.setX(xIni);
		//	final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		//	xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));
		
		//Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames().addAll(new KeyFrame(Duration.seconds(0.35), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		//new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd, Interpolator.EASE_BOTH)))
		timeIn.play();
		
	}
	
	/**
	 * Shows a popOver with informations for this Song.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void showPopOver(double x , double y) {
		// this.media = media
		// pop.show(media)
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
			if (!Main.playedSongs.containsFile(media.getFilePath()))
				System.out.println(Main.playedSongs.addIfNotExists(media.getFilePath(), true));
			else
				System.out.println(Main.playedSongs.remove(media.getFilePath(), true));
		}
		
		// remove media
		else if (source == removeMedia)
			controller.prepareDelete(false);
		
		// rename
		else if (source == rename)
			media.rename(node);
		else if (source == copy)
			controller.getTableViewer().copySelectedMediaToClipBoard();
		else if (source == paste)
			controller.getTableViewer().pasteMediaFromClipBoard();
		else if (source == stars)
			media.updateStars(node);
		else if (source == showFile) // File path
			ActionTool.openFileLocation(media.getFilePath());
		else if (e.getSource() == copyOrMove) // copyTo
			Main.exportWindow.show(controller);
		else
			try {
				
				//---------------------SEARCH ON WEB--------------------------------------------
				//Music Sites
				if (source == soundCloud)
					ActionTool.openWebSite("https://soundcloud.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == jamendo)
					ActionTool.openWebSite("https://www.jamendo.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == tuneIn)
					ActionTool.openWebSite("http://tunein.com/search/?query=" + URLEncoder.encode(media.getTitle(), encoding));
				//Amazon
				else if (source == amazonUS)
					ActionTool.openWebSite("https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonUK)
					ActionTool.openWebSite("https://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonCanada)
					ActionTool.openWebSite("https://www.amazon.ca/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonGermany)
					ActionTool.openWebSite("https://www.amazon.de/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonFrance)
					ActionTool.openWebSite("https://www.amazon.fr/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonSpain)
					ActionTool.openWebSite("https://www.amazon.es/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonItaly)
					ActionTool.openWebSite("https://www.amazon.it/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonJapan)
					ActionTool.openWebSite("https://www.amazon.co.jp/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == amazonChina)
					ActionTool.openWebSite("https://www.amazon.cn/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords=" + URLEncoder.encode(media.getTitle(), encoding));
				
				//Music Sites
				else if (source == hDTracks)
					ActionTool.openWebSite("http://www.hdtracks.com/catalogsearch/result/?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == cDUniverse)
					ActionTool.openWebSite("http://www.cduniverse.com/sresult.asp?HT_Search=ALL&HT_Search_Info=" + URLEncoder.encode(media.getTitle(), encoding) + "&style=all");
				
				//Radios
				else if (source == lastfm)
					ActionTool.openWebSite("https://www.last.fm/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == librefm)
					ActionTool.openWebSite("https://libre.fm/search.php?search_term=" + URLEncoder.encode(media.getTitle(), encoding) + "&search_type=artist");
				
				//Video WebSites
				else if (source == youtube)
					ActionTool.openWebSite("https://www.youtube.com/results?search_query=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == vimeo)
					ActionTool.openWebSite("https://vimeo.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
				
				//Search-Engines
				else if (source == google)
					ActionTool.openWebSite("https://www.google.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == duckduckgo)
					ActionTool.openWebSite("https://duckduckgo.com/?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == bing)
					ActionTool.openWebSite("http://www.bing.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == yahoo)
					ActionTool.openWebSite("https://search.yahoo.com/search?p=" + URLEncoder.encode(media.getTitle(), encoding));
				
				//Wikipedia
				else if (source == wikipedia)
					ActionTool.openWebSite("https://www.wikipedia.org/wiki/Special:Search?search=" + URLEncoder.encode(media.getTitle(), encoding));
				
				//-----------------------FIND LYRICS------------------------------------------------
				else if (source == lyricFinderOrg)
					ActionTool.openWebSite("http://search.lyricfinder.org/?query=" + URLEncoder.encode(media.getTitle(), encoding));
				else if (source == lyricsCom)
					ActionTool.openWebSite("http://www.lyrics.com/lyrics/" + URLEncoder.encode(media.getTitle(), encoding));
				
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		
	}
	
}
