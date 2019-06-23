package com.goxr3plus.xr3player.models.lists;

/**
 * This class represents a List of the Songs that the User Dislikes
 * 
 * @author GOXR3PLUS
 *
 */
public class HatedSongsList extends DatabaseList {

	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "HatedMediaListOriginal";

	/**
	 * Constructor
	 *
	 */
	public HatedSongsList() {
		super(dataBaseTableName);
	}

}
