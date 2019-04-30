/**
 * 
 */
package com.goxr3plus.xr3player.models.xplayer;

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
		songObject = new SimpleObjectProperty<>(XPlayerModel.this, "songObject", null);
		songExtension = new SimpleStringProperty(XPlayerModel.this, "songExtension", null);
		songPath = new SimpleStringProperty(XPlayerModel.this, "songPath", null);
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
	 * Gets the song path
	 *
	 * @return The song extension
	 */
	public String getSongPath() {
		return songPath.get();
	}

	/**
	 * Gets the song path
	 *
	 * @return The song extension
	 */
	public String getSongExtension() {
		return songExtension.get();
	}

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
	 * @return Return the array which contains the equalizer values for the player
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
	 * 
	 */

	/**
	 * Sets the song path
	 *
	 * @return The song extension
	 */
	public void setSongPath(String songPath) {
		this.songPath.set(songPath);
	}

	/**
	 * Sets the song extension
	 *
	 * @return The song extension
	 */
	public void setSongExtension(String songExtension) {
		this.songExtension.set(songExtension);
	}

	/**
	 * Set the current angle time of the player.
	 *
	 * @param currentAngleTime the new current angle time
	 */
	public void setCurrentAngleTime(int currentAngleTime) {
		this.currentAngleTime = currentAngleTime;
	}

	/**
	 * Set the current time of the player.
	 *
	 * @param currentTime the new current time
	 */
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * Set the duration of the player.
	 *
	 * @param duration the new duration
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
