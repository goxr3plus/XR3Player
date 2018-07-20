/*
 * 
 */
package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import javafx.scene.control.TreeItem;

/**
 * A custom TreeItem which represents a File
 */
public class DropboxClientTreeItem extends TreeItem<String> {
	
	private String accessToken;
	
	/**
	 * Constructor.
	 *
	 * @param accessToken
	 *            The client accessToken
	 * 
	 */
	public DropboxClientTreeItem(String value, String accessToken) {
		super(value);
		this.accessToken = accessToken;
		
	}
	
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
}
