package application.speciallists;

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
	 * 
	 * @param dataBaseTableName
	 */
	public DislikedSongsList() {
		super(dataBaseTableName);
	}
	
}
