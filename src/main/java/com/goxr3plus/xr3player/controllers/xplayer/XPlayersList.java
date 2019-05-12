/*
 * 
 */
package com.goxr3plus.xr3player.controllers.xplayer;

import java.util.ArrayList;
import java.util.List;

import com.goxr3plus.xr3player.models.xplayer.XPlayer;

/**
 * The Class XPlayersList.
 */
public class XPlayersList {

	/** The list. */
	private List<XPlayerController> list;

	/**
	 * Constructor.
	 */
	public XPlayersList() {
		list = new ArrayList<>();
	}

	/**
	 * Gets the xPlayerController
	 *
	 * @param key the key
	 * @return xPlayerController
	 */
	public XPlayerController getXPlayerController(int key) {
		if (key > list.size())
			return null;

		// Find it
		for (XPlayerController p : list)
			if (p.getKey() == key)
				return p;

		return null;
	}

	/**
	 * Gets the xPlayer
	 *
	 * @param key the key
	 * @return the xPlayer
	 */
	public XPlayer getXPlayer(int key) {
		return getXPlayerController(key).xPlayer;
	}

	/**
	 * Adds the xPlayerController
	 *
	 * @param xPlayerController xPlayerController
	 */
	public void addXPlayerController(XPlayerController xPlayerController) {
		list.add(xPlayerController);
	}

	/**
	 * @return the list
	 */
	public List<XPlayerController> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<XPlayerController> list) {
		this.list = list;
	}

}
