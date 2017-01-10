/*
 * 
 */
package smartcontroller;

import application.Main;
import javafx.event.ActionEvent;
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
	
	/** The media. */
	private Media media;
	
	/** The controller. */
	private SmartController controller;
	
	/** The players. */
	Menu players = new Menu("Play on");
	
	/** The player 0. */
	MenuItem player0 = new MenuItem("xPlayer ~0");
	
	/** The player 1. */
	MenuItem player1 = new MenuItem("xPlayer ~1");
	
	/** The player 2. */
	MenuItem player2 = new MenuItem("xPlayer ~2");
	
	MenuItem searchOnWeb = new MenuItem("Search on Web..");
	
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
	MenuItem information = new MenuItem("Information(I)", InfoTool.getImageViewFromDocuments("tag.png"));
	
	/** The stars. */
	MenuItem stars = new MenuItem("Stars(S)", InfoTool.getImageViewFromDocuments("smallStar.png"));
	
	/** The source folder. */
	MenuItem sourceFolder = new MenuItem("PathFolder(P)", InfoTool.getImageViewFromDocuments("path.png"));
	
	/** The copy. */
	MenuItem copy = new MenuItem("copyTo(C)", InfoTool.getImageViewFromDocuments("copyFile.png"));
	
	/** The move. */
	MenuItem move = new MenuItem("moveTo(M)", InfoTool.getImageViewFromDocuments("moveFile.png"));
	
	/** The add in query. */
	MenuItem addInQuery = new MenuItem("addInQuery(Q)");
	
	/** The rename. */
	MenuItem rename = new MenuItem("Rename(R)", InfoTool.getImageViewFromDocuments("rename.png"));
	
	/** The simple delete. */
	MenuItem simpleDelete = new MenuItem("Delete(Delete)");
	
	/** The storage delete. */
	MenuItem storageDelete = new MenuItem("Delete(Shift+Delete)");
	
	
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
		
		// play on deck 0,1,2
		players.getItems().addAll(player0, player1, player2);
		
		// searchOnWeb
		searchOnWeb.setDisable(true);
		
		// add the above
		getItems().addAll(players, searchOnWeb, new SeparatorMenuItem());
		
		// add on deck play list 0,1,2
		addOn.setDisable(true);
		addOn.getItems().addAll(xPlayer0, xPlayer1, xPlayer2);
		getItems().addAll(addOn);
		
		// More
		information.setDisable(true);
		more.getItems().addAll(information, stars, sourceFolder);
		getItems().addAll(more);
		
		// Disable,Rename
		getItems().addAll(separator1, rename);
		
		// Simple Delete and Storage Delete
		getItems().addAll(simpleDelete, storageDelete);
		
		// copyTo,moveTo
		copy.setDisable(true);
		move.setDisable(true);
		addInQuery.setDisable(true);
		getItems().addAll(separator2, copy, move, addInQuery);
		
		player0.setOnAction(this::onAction);
		player1.setOnAction(this::onAction);
		player2.setOnAction(this::onAction);
		
		xPlayer0.setOnAction(this::onAction);
		xPlayer1.setOnAction(this::onAction);
		xPlayer2.setOnAction(this::onAction);
		
		information.setOnAction(this::onAction);
		sourceFolder.setOnAction(this::onAction);
		stars.setOnAction(this::onAction);
		
		copy.setOnAction(this::onAction);
		move.setOnAction(this::onAction);
		
		simpleDelete.setOnAction(this::onAction);
		storageDelete.setOnAction(this::onAction);
		rename.setOnAction(this::onAction);
		
	}
	
	/**
	 * Shows the context menu based on the variables below.
	 *
	 * @param media the media
	 * @param genre the genre
	 * @param d the d
	 * @param e the e
	 * @param controller the controller
	 */
	public void showContextMenu(Media media , Genre genre , double d , double e , SmartController controller) {
		
		// Don't waste resources
		if (previousGenre != genre) {
			if (media.getGenre() == Genre.LIBRARYSONG) {
				addOn.setVisible(true);
				stars.setVisible(true);
				copy.setVisible(true);
				move.setVisible(true);
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
				move.setVisible(false);
				rename.setVisible(false);
				simpleDelete.setVisible(true);
				storageDelete.setVisible(true);
				separator1.setVisible(false);
				separator2.setVisible(false);
			}
		}
		
		this.media = media;
		this.controller = controller;
		
		// Show it
		show(Main.window, (int) d, (int) e);
		previousGenre = genre;
	}
	
	/**
	 * Shows a popOver with informations for this Song.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void showPopOver(double x , double y) {
		// this.media = media;
		// pop.show(media);
	}
	
	/**
	 * On action.
	 *
	 * @param action the a
	 */
	public void onAction(ActionEvent action) {
		
		// play on deck 0
		if (action.getSource() == player0) {
			( (Audio) media ).playOnDeck(0, controller);
			
			// play on deck 1
		} else if (action.getSource() == player1) {
			( (Audio) media ).playOnDeck(1, controller);
			
			// play on deck 2
		} else if (action.getSource() == player2)
			( (Audio) media ).playOnDeck(2, controller);
			
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
			media.rename(controller);
		else if (action.getSource() == information) { // information
			// showPopOver(media);
		} else if (action.getSource() == stars)
			media.updateStars(controller);
		else if (action.getSource() == sourceFolder) // File path
			ActionTool.openFileLocation(media.getFilePath());
		else if (action.getSource() == copy) { // copyTo
			// if (media instanceof LibrarySong)
			// Main.libraryMode.multipleLibs.getSelectedLibrary().controller.copyOrMoveService.startCopy();
			
		} else if (action.getSource() == move) { // moveTo
			// if (media instanceof LibrarySong)
			// Main.libraryMode.multipleLibs.getSelectedLibrary().controller.copyOrMoveService.startMoving();
		}
		
	}
	
}
