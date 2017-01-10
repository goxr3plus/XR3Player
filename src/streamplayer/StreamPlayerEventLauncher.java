/*
 * 
 */
package streamplayer;

import java.util.List;

import streamplayer.StreamPlayer.Status;

/**
 * The Class StreamPlayerEventLauncher.
 *
 * @author GOXR3PLUS
 */
public class StreamPlayerEventLauncher extends Thread {
	
	/** The player state. */
	private Status playerState = Status.UNKNOWN;
	
	/** The stream position. */
	private int encodedStreamPosition = -1;
	
	/** The description. */
	private Object description = null;
	
	/** The listeners. */
	private List<StreamPlayerListener> listeners = null;
	
	/** The source. */
	private Object source = null;
	
	/**
	 * Instantiates a new stream player event launcher.
	 * 
	 * @param source the source
	 * @param playerStatus the play state
	 * @param encodedStreamPosition the stream position
	 * @param description the description
	 * @param listeners the listeners
	 */
	public StreamPlayerEventLauncher(Object source, Status playerStatus, int encodedStreamPosition, Object description,
	        List<StreamPlayerListener> listeners) {
		this.source = source;
		this.playerState = playerStatus;
		this.encodedStreamPosition = encodedStreamPosition;
		this.description = description;
		this.listeners = listeners;
	}
	
	@Override
	public void run() {
		// Notify all the listeners that the state has been updated
		if (listeners != null) {
			listeners.forEach(listener -> listener
			        .statusUpdated(new StreamPlayerEvent(source, playerState, encodedStreamPosition, description)));
		}
	}
}
