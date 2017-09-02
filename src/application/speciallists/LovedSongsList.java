package application.speciallists;

/**
 * This class represents a List of the Songs that the User Dislikes
 * 
 * @author GOXR3PLUS
 *
 */
public class LovedSongsList extends DatabaseList {
	
	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "LovedMediaList";
	
	/**
	 * Constructor
	 * 
	 * @param dataBaseTableName
	 */
	public LovedSongsList() {
		super(dataBaseTableName);
	}
	
}
