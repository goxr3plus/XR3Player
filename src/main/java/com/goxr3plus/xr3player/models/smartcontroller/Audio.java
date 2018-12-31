/*
 * 
 */
package main.java.com.goxr3plus.xr3player.models.smartcontroller;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.enums.Genre;
import main.java.com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import main.java.com.goxr3plus.xr3player.utils.general.AudioImageTool;
import main.java.com.goxr3plus.xr3player.utils.javafx.DragViewTool;

/**
 * Representing an Audio File.
 *
 * @author GOXR3PLUS
 */
public class Audio extends Media {

	/** The album image. */
	private Image albumImage;

	/**
	 * Define a pseudo class.
	 *
	 * @param path         the path
	 * @param duration     the duration
	 * @param stars        the stars
	 * @param timesPlayed  the times played
	 * @param dateImported the date imported
	 * @param hourImported the hour imported
	 * @param genre        the genre
	 */
	/*
	 * private PseudoClass markedPseudoClass = PseudoClass.getPseudoClass("marked");
	 * BooleanProperty marked = new BooleanPropertyBase(false) {
	 * 
	 * @Override public void invalidated() {
	 * pseudoClassStateChanged(markedPseudoClass, marked.get()); }
	 * 
	 * @Override public Object getBean() { return SongButton.this; }
	 * 
	 * @Override public String getName() { return "marked"; } };
	 */

	/**
	 * Constructor
	 * 
	 * @param path         The path of the File
	 * @param stars        The quality of the Media
	 * @param timesPlayed  The times the Media has been played
	 * @param dateImported The date the Media was imported <b> if null given then
	 *                     the imported time will be the current date </b>
	 * @param hourImported The hour the Media was imported <b> if null given then
	 *                     the imported hour will be the current time </b>
	 * @param genre        The genre of the Media <b> see the Genre class for more
	 *                     </b>
	 */
	public Audio(final String path, final double stars, final int timesPlayed, final String dateImported,
			final String hourImported, final Genre genre, final int number) {
		super(path, stars, timesPlayed, dateImported, hourImported, genre, number);
	}

	/**
	 * Adding the song to deck and starting it.
	 *
	 * @param deck       the deck
	 * @param controller the controller
	 */
	public void playOnDeck(final int deck, final SmartController controller) {
		// if (Main.xPlayersList.getXPlayer(deck).playSong(this))
		// setTimesPlayed(getTimesPlayed() + 1, controller);
		Main.xPlayersList.getXPlayerController(deck).playSong(getFilePath());

	}

	/**
	 * Set the dragView based on the image of the Song.
	 *
	 * @param db the new drag view
	 */
	@Override
	public void setDragView(final Dragboard db) {
		DragViewTool.setDragView(db, this);
	}

	/**
	 * Returns some item based on the category value.
	 *
	 * @param category the category
	 * @return the category
	 */
	public int getCategory(final int category) {
		switch (category) {
		case 1:
			return getTimesPlayed();
		case 2:
			return (int) (getStars() * 2); // It converts it to maximum 10
		default:
			return -1;
		}

	}

	/**
	 * Return original Song Album Image.
	 *
	 * @return the album image
	 */
	@Override
	public Image getAlbumImage() {
		if ("mp3".equals(getFileType()) && new File(getFilePath()).exists() && albumImage == null)
			albumImage = AudioImageTool.getAudioAlbumImage(getFilePath(), -1, -1);
		return albumImage;
	}

	/**
	 * Return original Song Album Image in requested width and height.
	 *
	 * @param width  the width
	 * @param height the height
	 * @return the album image fit
	 */
	@Override
	public Image getAlbumImageFit(final int width, final int height) {
		return !"mp3".equals(getFileType()) || !new File(getFilePath()).exists() ? null
				: AudioImageTool.getAudioAlbumImage(getFilePath(), width, height);
	}

}
