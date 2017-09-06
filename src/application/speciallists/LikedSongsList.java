package application.speciallists;

/**
 * This class represents a List of Songs that User Likes
 * 
 * @author GOXR3PLUS
 *
 */
public class LikedSongsList extends DatabaseList {
	
	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "LikedMediaListOriginal";
	
	/**
	 * Constructor
	 * 
	 * @param dataBaseTableName
	 */
	public LikedSongsList() {
		super(dataBaseTableName);
	}
	
}
