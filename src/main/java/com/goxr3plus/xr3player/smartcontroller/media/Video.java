/*
 * 
 */
package main.java.com.goxr3plus.xr3player.smartcontroller.media;

import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;

// TODO: Auto-generated Javadoc
/**
 * Representing an Video File.
 *
 * @author GOXR3PLUS
 */
public class Video extends Media {

    /**
     * Constructor.
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
    public Video(String path, double stars, int timesPlayed, String dateImported, String hourImported, Genre genre,int number) {
	super(path, stars, timesPlayed, dateImported, hourImported, genre,number);
    }

    /* (non-Javadoc)
     * @see media.Media#setDragView(javafx.scene.input.Dragboard)
     */
    @Override
    public void setDragView(Dragboard db) {

    }

    /* (non-Javadoc)
     * @see media.Media#getAlbumImage()
     */
    @Override
    public Image getAlbumImage() {
	return null;
    }

}
