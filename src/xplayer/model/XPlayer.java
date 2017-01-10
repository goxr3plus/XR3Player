/*
 * 
 */
package xplayer.model;

import streamplayer.StreamPlayer;

/**
 * This class in controlling the player internally.
 *
 * @author SuperGoliath
 */
public class XPlayer extends StreamPlayer {
	
	/**
	 * Constructor.
	 */
	public XPlayer() {
		super();
	}
	
	/**
	 * Stops the play back.<br>
	 *
	 * Player Status = STOPPED.<br>
	 * Thread should free Audio resources.
	 */
	@Override
	public void stop() {
		new Thread(super::stop).start();
	}
	
}
