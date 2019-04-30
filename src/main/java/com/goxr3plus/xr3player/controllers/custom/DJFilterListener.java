/*
 * GOXR3PLUS STUDIO PROFESSIONAL COMP. R
 */
package com.goxr3plus.xr3player.controllers.custom;

/**
 * The listener interface for receiving DJFilter events.
 *
 */
@FunctionalInterface
public interface DJFilterListener {

	/**
	 * The new value of the DJFilter
	 * 
	 * @param value
	 */
	void valueChanged(double value);

}
