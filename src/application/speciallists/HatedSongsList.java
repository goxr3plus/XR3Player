package application.speciallists;

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
	 * @param dataBaseTableName
	 */
	public HatedSongsList() {
		super(dataBaseTableName);
	}
	
}
