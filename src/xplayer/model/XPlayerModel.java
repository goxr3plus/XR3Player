/**
 * 
 */
package xplayer.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * The Model of the XPlayer.
 *
 * @author GOXR3PLUS
 */
public class XPlayerModel {

    /** The song object. */
    private SimpleObjectProperty<Object> songObject;

    /** The song extension. */
    private SimpleStringProperty songExtension;

    /** The song path. */
    private SimpleStringProperty songPath;

    /** The current angle time. */
    private int currentAngleTime;

    /** The current time. */
    private int currentTime;

    /** The duration. */
    private int duration;

    /** The equalizer array. */
    // ------------- Filters--------------
    private float[] equalizerArray;

    /**
     * Constructor.
     */
    public XPlayerModel() {
	songObject = new SimpleObjectProperty<>();
	songExtension = new SimpleStringProperty();
	songPath = new SimpleStringProperty();
	equalizerArray = new float[32];
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							GETTERS
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Gets the current angle time.
     *
     * @return The current angle time of the player
     */
    public int getCurrentAngleTime() {
	return currentAngleTime;
    }

    /**
     * Gets the current time.
     *
     * @return The current time of the player
     */
    public int getCurrentTime() {
	return currentTime;
    }

    /**
     * Gets the duration.
     *
     * @return The duration of the player
     */
    public int getDuration() {
	return duration;
    }

    /**
     * Gets the equalizer array.
     *
     * @return Return the array which contains the equalizer values for the
     *         player
     */
    public float[] getEqualizerArray() {
	return equalizerArray;
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							SETTERS
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Set the current angle time of the player.
     *
     * @param currentAngleTime
     *            the new current angle time
     */
    public void setCurrentAngleTime(int currentAngleTime) {
	this.currentAngleTime = currentAngleTime;
    }

    /**
     * Set the current time of the player.
     *
     * @param currentTime
     *            the new current time
     */
    public void setCurrentTime(int currentTime) {
	this.currentTime = currentTime;
    }

    /**
     * Set the duration of the player.
     *
     * @param duration
     *            the new duration
     */
    public void setDuration(int duration) {
	this.duration = duration;
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							PROPERTIES
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Song object property.
     *
     * @return The song object property
     */
    public SimpleObjectProperty<Object> songObjectProperty() {
	return songObject;
    }

    /**
     * Song path property.
     *
     * @return The song path property
     */
    public SimpleStringProperty songPathProperty() {
	return songPath;
    }

    /**
     * Song extension property.
     *
     * @return The song extension property
     */
    public SimpleStringProperty songExtensionProperty() {
	return songExtension;
    }

}
