/*
 * 
 */
package streamplayer;

import streamplayer.StreamPlayer.Status;

/**
 * The Class StreamPlayerEvent.
 *
 * @author GOXR3PLUS
 */
public class StreamPlayerEvent {
	
	/** The status. */
	private Status playerStatus = Status.UNKNOWN;
	
	/** The stream position. */
	private int encodedStreamPosition = -1;
	
	/** The source. */
	private Object source = null;
	
	/** The description. */
	private Object description = null;
	
	/**
	 * Constructor.
	 *
	 * @param source the source
	 * @param status the status
	 * @param encodededStreamPosition the stream position
	 * @param description the description
	 */
	public StreamPlayerEvent(Object source, Status status, int encodededStreamPosition, Object description) {
		this.source = source;
		this.playerStatus = status;
		this.encodedStreamPosition = encodededStreamPosition;
		this.description = description;
	}
	
	/**
	 * Returns the Player Status
	 *
	 * @return The player Status (paused,playing,...)
	 * @see StreamPlayer.Status
	 */
	public Status getPlayerStatus() {
		return playerStatus;
	}
	
	/**
	 * Returns the encoded stream position
	 *
	 * @return EncodedStreamPosition = the position of the encoded audio stream
	 *         right now..
	 */
	public int getEncodedStreamPosition() {
		return encodedStreamPosition;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public Object getDescription() {
		return description;
	}
	
	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}
	
	@Override
	public String toString() {
		return "Source :=" + source + " , Player Status := " + playerStatus + " , EncodedStreamPosition :="
		        + encodedStreamPosition + " , Description :=" + description;
		
	}
	
}
