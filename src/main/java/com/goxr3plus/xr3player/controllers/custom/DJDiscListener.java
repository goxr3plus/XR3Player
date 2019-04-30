/*
 * 
 */
package com.goxr3plus.xr3player.controllers.custom;

/**
 * The listener interface for receiving DJDisc events. The class that is
 * interested in processing a DJDisc event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addDJDiscListener<code> method. When the DJDisc event
 * occurs, that object's appropriate method is invoked.
 *
 */
@FunctionalInterface
public interface DJDiscListener {

	/**
	 * Volume changed.
	 *
	 * @param volume the volume
	 */
	void volumeChanged(int volume);

}
