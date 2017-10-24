/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.speciallists;

/**
 * The Class PlayedSongs.
 */
public class PlayedMediaList extends DatabaseList {
	
	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "PlayedMediaListOriginal";
	
	/**
	 * Constructor
	 * 
	 * @param dataBaseTableName
	 */
	public PlayedMediaList() {
		super(dataBaseTableName);
	}
	
}
