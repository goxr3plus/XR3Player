/*
 * 
 */
package xplayer.presenter;

import java.util.ArrayList;
import java.util.List;

import xplayer.model.XPlayer;

// TODO: Auto-generated Javadoc
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
	 * Gets the x player UI.
	 *
	 * @param key the key
	 * @return the x player UI
	 */
	public XPlayerController getXPlayerUI(int key) {
		for (XPlayerController p : list)
			if (p.getKey() == key)
				return p;

		return null;
	}

	/**
	 * Gets the x player.
	 *
	 * @param key the key
	 * @return the x player
	 */
	public XPlayer getXPlayer(int key) {
		return getXPlayerUI(key).xPlayer;
	}

	/**
	 * Adds the X player UI.
	 *
	 * @param xplayerUI the xplayer UI
	 */
	public void addXPlayerUI(XPlayerController xplayerUI) {
		list.add(xplayerUI);
	}

}
