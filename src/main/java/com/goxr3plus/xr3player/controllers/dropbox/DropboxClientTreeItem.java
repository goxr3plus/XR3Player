/*
 * 
 */
package com.goxr3plus.xr3player.controllers.dropbox;

import javafx.scene.control.TreeItem;

/**
 * A custom TreeItem which represents a File
 */
public class DropboxClientTreeItem extends TreeItem<String> {

	private final String accessToken;
	private final String email;

	/**
	 * Constructor.
	 *
	 * @param accessToken The client accessToken
	 * 
	 */
	public DropboxClientTreeItem(String value, String accessToken, String email) {
		super(value);
		this.accessToken = accessToken;
		this.email = email;

	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

}
