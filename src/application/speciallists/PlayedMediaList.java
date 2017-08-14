/*
 * 
 */
package application.speciallists;

/**
 * The Class PlayedSongs.
 */
public class PlayedMediaList extends DatabaseList {
	
	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "PlayedMediaList";
	
	/**
	 * Constructor
	 * 
	 * @param dataBaseTableName
	 */
	public PlayedMediaList() {
		super(dataBaseTableName);
	}
	
}
