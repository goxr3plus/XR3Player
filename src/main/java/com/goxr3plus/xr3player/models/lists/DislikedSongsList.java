package com.goxr3plus.xr3player.models.lists;

/**
 * This class represents a List of the Songs that the User Dislikes
 * 
 * @author GOXR3PLUS
 *
 */
public class DislikedSongsList extends DatabaseList {

	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "DislikedMediaListOriginal";

	/**
	 * Constructor
	 */ // TODO: improve the description, don't describe the obvious.
	public DislikedSongsList() {
		super(dataBaseTableName);
	}

}
