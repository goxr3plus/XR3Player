/*
 * 
 */
package smartcontroller.media;

import java.io.File;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.paint.Color;
import smartcontroller.Genre;
import smartcontroller.SmartController;

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
	 * @param path
	 *            the path
	 * @param duration
	 *            the duration
	 * @param stars
	 *            the stars
	 * @param timesPlayed
	 *            the times played
	 * @param dateImported
	 *            the date imported
	 * @param hourImported
	 *            the hour imported
	 * @param genre
	 *            the genre
	 */
	/*
	 * private PseudoClass markedPseudoClass = PseudoClass.getPseudoClass("marked"); BooleanProperty marked = new BooleanPropertyBase(false) {
	 * @Override public void invalidated() { pseudoClassStateChanged(markedPseudoClass, marked.get()); }
	 * @Override public Object getBean() { return SongButton.this; }
	 * @Override public String getName() { return "marked"; } };
	 */
	
	/**
	 * Constructor
	 * 
	 * @param path
	 *            The path of the File
	 * @param stars
	 *            The quality of the Media
	 * @param timesPlayed
	 *            The times the Media has been played
	 * @param dateImported
	 *            The date the Media was imported <b> if null given then the imported time will be the current date </b>
	 * @param hourImported
	 *            The hour the Media was imported <b> if null given then the imported hour will be the current time </b>
	 * @param genre
	 *            The genre of the Media <b> see the Genre class for more </b>
	 */
	public Audio(String path, double stars, int timesPlayed, String dateImported, String hourImported, Genre genre) {
		super(path, stars, timesPlayed, dateImported, hourImported, genre);
	}
	
	/**
	 * Adding the song to deck and starting it.
	 *
	 * @param deck
	 *            the deck
	 * @param controller
	 *            the controller
	 */
	public void playOnDeck(int deck , SmartController controller) {
		// if (Main.xPlayersList.getXPlayer(deck).playSong(this))
		// setTimesPlayed(getTimesPlayed() + 1, controller);
		Main.xPlayersList.getXPlayerController(deck).playSong(getFilePath());
		
	}
	
	/**
	 * Set the dragView based on the image of the Song.
	 *
	 * @param db
	 *            the new drag view
	 */
	@Override
	public void setDragView(Dragboard db) {
		// System.out.println("AlbumIamge=["+getAlbumImage()+"]")
		if (getAlbumImage() != null)
			db.setDragView(getAlbumImageFit(100, 100), 50, 0);
		else {
			WritableImage image = new WritableImage(100, 100);
			Canvas canvas = new Canvas();
			canvas.setWidth(100);
			canvas.setHeight(100);
			ActionTool.paintCanvas(canvas.getGraphicsContext2D(), getFileName(), 100, 100);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			db.setDragView(canvas.snapshot(params, image), 50, 0);
		}
	}
	
	/**
	 * Returns some item based on the category value.
	 *
	 * @param category
	 *            the category
	 * @return the category
	 */
	public int getCategory(int category) {
		switch (category) {
			case 1:
				return getTimesPlayed();
			case 2:
				return (int) ( getStars() * 2 ); //It converts it to maximum 10
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
			albumImage = InfoTool.getMp3AlbumImage(getFilePath(), -1, -1);
		return albumImage;
	}
	
	/**
	 * Return original Song Album Image in requested width and height.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the album image fit
	 */
	public Image getAlbumImageFit(int width , int height) {
		return !"mp3".equals(getFileType()) || !new File(getFilePath()).exists() ? null : InfoTool.getMp3AlbumImage(getFilePath(), width, height);
	}
	
}
