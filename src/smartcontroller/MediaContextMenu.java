/*
 * 
 */
package smartcontroller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import application.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import media.Audio;
import media.Media;
import tools.ActionTool;
import tools.InfoTool;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class MediaContextMenu extends ContextMenu {

    /**
     * The node based on which the Rename or Star Window will be position
     */
    private Node node;

    /** The media. */
    private Media media;

    /** The controller. */
    private SmartController controller;

    /** The players. */
    Menu players = new Menu("Play on", InfoTool.getImageViewFromDocuments("circledPlay24.png"));

    /** The player 0. */
    MenuItem player0 = new MenuItem("xPlayer ~0");

    /** The player 1. */
    MenuItem player1 = new MenuItem("xPlayer ~1");

    /** The player 2. */
    MenuItem player2 = new MenuItem("xPlayer ~2");

    //Start:--Search on Web
    Menu searchOnWeb = new Menu("Search on Web..", InfoTool.getImageViewFromDocuments("searchWeb24.png"));

    MenuItem soundCloud = new MenuItem("SoundCloud", InfoTool.getImageViewFromDocuments("soundcloud24.png"));
    MenuItem jamendo = new MenuItem("Jamendo", InfoTool.getImageViewFromDocuments("jamendo24.png"));
    MenuItem tunein = new MenuItem("tunein", InfoTool.getImageViewFromDocuments("tunein24.png"));
    MenuItem amazon = new MenuItem("amazon", InfoTool.getImageViewFromDocuments("amazon24.png"));

    MenuItem lastfm = new MenuItem("Last.fm", InfoTool.getImageViewFromDocuments("lastfm24.png"));
    MenuItem librefm = new MenuItem("Libre.fm", InfoTool.getImageViewFromDocuments("librefm24.png"));

    MenuItem youtube = new MenuItem("Youtube", InfoTool.getImageViewFromDocuments("youtube24.png"));
    MenuItem vimeo = new MenuItem("Vimeo", InfoTool.getImageViewFromDocuments("vimeo24.png"));

    MenuItem google = new MenuItem("Google", InfoTool.getImageViewFromDocuments("google24.png"));
    MenuItem duckduckgo = new MenuItem("DuckDuckgo", InfoTool.getImageViewFromDocuments("duckduckgo24.png"));
    MenuItem bing = new MenuItem("Bing", InfoTool.getImageViewFromDocuments("bing24.png"));
    MenuItem yahoo = new MenuItem("Yahoo", InfoTool.getImageViewFromDocuments("yahoo24.png"));

    //END:--Search on Web

    /** The add on. */
    Menu addOn = new Menu("Add on");

    /** The x player 0. */
    MenuItem xPlayer0 = new MenuItem("xPlayer ~0 PlayList");

    /** The x player 1. */
    MenuItem xPlayer1 = new MenuItem("xPlayer ~1 PlayList");

    /** The x player 2. */
    MenuItem xPlayer2 = new MenuItem("xPlayer ~2 PlayList");

    /** The more. */
    Menu more = new Menu("More...", InfoTool.getImageViewFromDocuments("more.png"));

    /** The information. */
    MenuItem information = new MenuItem("Information (I)", InfoTool.getImageViewFromDocuments("tag.png"));

    /** The stars. */
    MenuItem stars = new MenuItem("Stars (S)", InfoTool.getImageViewFromDocuments("smallStar.png"));

    /** The source folder. */
    MenuItem sourceFolder = new MenuItem("PathFolder (P)", InfoTool.getImageViewFromDocuments("path.png"));

    /** The copy. */
    MenuItem copy = new MenuItem("copy/move (C/M)", InfoTool.getImageViewFromDocuments("copyFile.png"));

    /** The move. */
    //MenuItem move = new MenuItem("moveTo(M)")

    /** The rename. */
    MenuItem rename = new MenuItem("Rename (R)", InfoTool.getImageViewFromDocuments("rename.png"));

    /** The simple delete. */
    MenuItem simpleDelete = new MenuItem("Delete (Delete)", InfoTool.getImageViewFromDocuments("delete2.png"));

    /** The storage delete. */
    MenuItem storageDelete = new MenuItem("Delete (Shift+Delete)", InfoTool.getImageViewFromDocuments("delete.png"));

    /** The separator 1. */
    SeparatorMenuItem separator1 = new SeparatorMenuItem();

    /** The separator 2. */
    SeparatorMenuItem separator2 = new SeparatorMenuItem();

    /** The previous genre. */
    Genre previousGenre = Genre.UNKNOWN;

    /**
     * Constructor.
     */
    public MediaContextMenu() {

	//Add all the items
	getItems().addAll(new TitleMenuItem("Common"), players, searchOnWeb, more, new TitleMenuItem("File Edit"),
		rename, simpleDelete, storageDelete, new TitleMenuItem("Organize"), copy);

	//---play

	players.getItems().addAll(player0, player1, player2);
	players.getItems().forEach(item -> item.setOnAction(this::onAction));

	//---searchOnWeb
	getItems().addAll();

	//Start:--Search on Web
	searchOnWeb.getItems().addAll(new TitleMenuItem("Popular"), soundCloud, jamendo, tunein,
		new TitleMenuItem("Shop"), amazon, new TitleMenuItem("Radios"), librefm, lastfm,
		new TitleMenuItem("Video Sites"), youtube, vimeo, new TitleMenuItem("Search Engines"), google,
		duckduckgo, bing, yahoo);
	searchOnWeb.getItems().forEach(item -> item.setOnAction(this::onAction2));

	//END:--Search on Web

	// add on deck play list 0,1,2
	addOn.setDisable(true);
	addOn.getItems().addAll(xPlayer0, xPlayer1, xPlayer2);
	addOn.getItems().forEach(item -> item.setOnAction(this::onAction));

	// More
	more.getItems().addAll(stars, sourceFolder);
	more.getItems().forEach(item -> item.setOnAction(this::onAction));

	copy.setOnAction(this::onAction);
	//move.setOnAction(this::onAction)
	simpleDelete.setOnAction(this::onAction);
	storageDelete.setOnAction(this::onAction);
	rename.setOnAction(this::onAction);

    }

    /**
     * Shows the context menu based on the variables below.
     *
     * @param media
     *            the media
     * @param genre
     *            the genre
     * @param d
     *            the d
     * @param e
     *            the e
     * @param controller
     *            the controller
     */
    public void showContextMenu(Media media, Genre genre, double d, double e, SmartController controller, Node node) {

	// Don't waste resources
	if (previousGenre != genre) {
	    if (media.getGenre() == Genre.LIBRARYSONG) {
		addOn.setVisible(true);
		stars.setVisible(true);
		copy.setVisible(true);
		//move.setVisible(true)
		rename.setVisible(true);
		simpleDelete.setVisible(true);
		storageDelete.setVisible(true);
		separator1.setVisible(true);
		separator2.setVisible(true);
		// } else if (button instanceof TopCategorySong) {
		// addOn.setVisible(false);
		// stars.setVisible(false);
		// copy.setVisible(false);
		// move.setVisible(false);
		// rename.setVisible(false);
		// simpleDelete.setVisible(false);
		// storageDelete.setVisible(false);
		// separator1.setVisible(false);
		// separator2.setVisible(false);
	    } else if (media.getGenre() == Genre.XPLAYLISTSONG) {
		addOn.setVisible(false);
		stars.setVisible(false);
		copy.setVisible(false);
		//move.setVisible(false)
		rename.setVisible(false);
		simpleDelete.setVisible(true);
		storageDelete.setVisible(true);
		separator1.setVisible(false);
		separator2.setVisible(false);
	    }
	}

	this.node = node;
	this.media = media;
	this.controller = controller;

	// Show it
	show(Main.window, d - super.getWidth() + super.getWidth() * 14 / 100, e - 1);
	previousGenre = genre;
    }

    /**
     * Shows a popOver with informations for this Song.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void showPopOver(double x, double y) {
	// this.media = media
	// pop.show(media)
    }

    /**
     * On action.
     *
     * @param action
     *            the a
     */
    public void onAction(ActionEvent action) {

	// play on deck 0
	if (action.getSource() == player0) {
	    ((Audio) media).playOnDeck(0, controller);

	    // play on deck 1
	} else if (action.getSource() == player1) {
	    ((Audio) media).playOnDeck(1, controller);

	    // play on deck 2
	} else if (action.getSource() == player2)
	    ((Audio) media).playOnDeck(2, controller);

	// add on xPlayList 0
	// } else if (a.getSource() == xPlayer0)
	// Main.xPlayersList.getXPlayerUI(0).xPlayList.addItem(media.getSongPath(),
	// true, true);
	//
	// // add on xPlayList 1
	// else if (a.getSource() == xPlayer1)
	// Main.xPlayersList.getXPlayerUI(1).xPlayList.addItem(media.getSongPath(),
	// true, true);
	//
	// // add on xPlayList 2
	// else if (a.getSource() == xPlayer2)
	// Main.xPlayersList.getXPlayerUI(2).xPlayList.addItem(media.getSongPath(),
	// true, true);

	// delete from list
	else if (action.getSource() == simpleDelete)
	    media.prepareDelete(false, controller);
	// delete from Storage medium
	else if (action.getSource() == storageDelete)

	    media.prepareDelete(true, controller);

	// rename
	else if (action.getSource() == rename)
	    media.rename(controller, node);
	else if (action.getSource() == information) { // information
	    // showPopOver(media);
	} else if (action.getSource() == stars)
	    media.updateStars(controller,node);
	else if (action.getSource() == sourceFolder) // File path
	    ActionTool.openFileLocation(media.getFilePath());
	else if (action.getSource() == copy) // copyTo
	    Main.exportWindow.show(controller);

    }

    /**
     * It is used for action events
     * 
     * @param action
     */
    public void onAction2(ActionEvent action) {
	Object source = action.getSource();
	String encoding = "UTF-8";

	//media!=null [warning]
	if (media != null) {
	    try {

		//Music Sites
		if (source == soundCloud)
		    ActionTool.openWebSite(
			    "https://soundcloud.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == jamendo)
		    ActionTool.openWebSite(
			    "https://www.jamendo.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == tunein)
		    ActionTool.openWebSite(
			    "http://tunein.com/search/?query=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == amazon)
		    ActionTool.openWebSite(
			    "https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dpopular&field-keywords="
				    + URLEncoder.encode(media.getTitle(), encoding));

		else if (source == lastfm)
		    ActionTool.openWebSite(
			    "https://www.last.fm/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == librefm)
		    ActionTool.openWebSite("https://libre.fm/search.php?search_term="
			    + URLEncoder.encode(media.getTitle(), encoding) + "&search_type=artist");

		//Video WebSites
		else if (source == youtube)
		    ActionTool.openWebSite("https://www.youtube.com/results?search_query="
			    + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == vimeo)
		    ActionTool
			    .openWebSite("https://vimeo.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));

		//Search-Engines
		else if (source == google)
		    ActionTool.openWebSite(
			    "https://www.google.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == duckduckgo)
		    ActionTool
			    .openWebSite("https://duckduckgo.com/?q=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == bing)
		    ActionTool.openWebSite(
			    "http://www.bing.com/search?q=" + URLEncoder.encode(media.getTitle(), encoding));
		else if (source == yahoo)
		    ActionTool.openWebSite(
			    "https://search.yahoo.com/search?p=" + URLEncoder.encode(media.getTitle(), encoding));

	    } catch (UnsupportedEncodingException ex) {
		ex.printStackTrace();
	    }
	}

    }

}
