package application.speciallists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import application.Main;
import application.tools.InfoTool;

/**
 * A special kind of list which is used to save the list to the database and also have it in RAM memory of the computer
 * 
 * @author GOXR3PLUS
 *
 */
public class DatabaseList {
	
	/** The LinkedHashSet */
	private Set<String> set = new LinkedHashSet<>();
	
	/**
	 * The name of the database table
	 */
	private final String databaseTableName;
	
	/**
	 * Constructor
	 * 
	 * @param dataBaseTableName
	 */
	public DatabaseList(String dataBaseTableName) {
		this.databaseTableName = dataBaseTableName;
	}
	
	//------------Prepared Statements---------------
	
	/**
	 * Prepares the DataBase table (if not exists) , i do this to keep backward compatibility with previous XR3Player Versions ( Update 57<) Also it
	 * creates the PreparedStatement to insert Files Paths into the Table
	 */
	private void prepareMediaListTable() {
		
		try {
			//IF DATABASE DOESN'T EXIST
			if (!Main.dbManager.doesTableExist(databaseTableName))
				Main.dbManager.getConnection().createStatement().executeUpdate("CREATE TABLE '" + databaseTableName
						+ "'(PATH   TEXT  PRIMARY KEY   NOT NULL ,STARS DOUBLE NOT NULL , TIMESPLAYED  INT  NOT NULL,DATE   TEXT   NOT NULL , HOUR  TEXT  NOT NULL)");
			
			//Update 81+ deletes the Emotions Lists from previous databases ( too bad , but wtf to do.... we have to update bro's)
			else {
				//Main.dbManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS'" + databaseTableName.replace("Original", "") + "'");
				//Main.dbManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS'" + "HateddMediaList" + "'");
				
				//Main.dbManager.getConnection().commit();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		//System.out.println("Column exists " + isColumnExists(databaseTableName, "STARS"));
	}
	
	/**
	 * Checks if the Specific column exists inside the database
	 * 
	 * @param table
	 * @param column
	 * @return
	 */
	public boolean isColumnExists(String table , String column) {
		try {
			Main.dbManager.getConnection().prepareStatement("SELECT " + column + " FROM '" + table + "'").executeQuery();
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Uploads the data from the database table to the list , i call this method when i login into a user to upload the Media that he/she has
	 * previously heard
	 */
	public void uploadFromDataBase() {
		
		//Check existence
		prepareMediaListTable();
		
		//Now Upload
		try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery("SELECT* FROM '" + databaseTableName + "'")) {
			
			//Add all
			while (resultSet.next())
				set.add(resultSet.getString("PATH"));
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Add a new item.
	 *
	 * @param item
	 *            the item
	 * @return True if succeeded or False if not
	 */
	public boolean addIfNotExists(String item , boolean commit) {
		
		if (set.add(item))
			//Try to insert into the database
			try (PreparedStatement insert = Main.dbManager.getConnection()
					.prepareStatement("INSERT OR IGNORE INTO '" + databaseTableName + "' (PATH,STARS,TIMESPLAYED,DATE,HOUR) VALUES (?,?,?,?,?)")) {
				insert.setString(1, item);
				insert.setDouble(2, 0.0);
				insert.setInt(3, 0);
				insert.setString(4, InfoTool.getCurrentDate());
				insert.setString(5, InfoTool.getLocalTime());
				insert.executeUpdate();
				
				//Commit
				if (commit)
					Main.dbManager.commit();
				
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		
		return false;
	}
	
	/**
	 * Add a new item.
	 *
	 * @param item
	 *            the item
	 * @return True if succeeded or False if not
	 */
	public boolean remove(String item , boolean commit) {
		
		if (set.remove(item))
			//Try to delete from the database
			try (PreparedStatement remove = Main.dbManager.getConnection().prepareStatement("DELETE FROM '" + databaseTableName + "' WHERE PATH=?")) {
				remove.setString(1, item);
				remove.executeUpdate();
				
				//Commit
				if (commit)
					Main.dbManager.commit();
				
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
		
		return false;
	}
	
	/**
	 * Check if a media has been already played.
	 *
	 * @param filePath
	 *            The absolute file path
	 * @return true, if successful
	 */
	public boolean containsFile(String filePath) {
		return set.contains(filePath);
	}
	
	/**
	 * Renames the media with this name if exists [ in list ].
	 *
	 * @param oldName
	 *            the old name
	 * @param newName
	 *            the new name
	 * @return true, if successful
	 */
	public boolean renameMedia(String oldName , String newName , boolean commit) {
		if (!set.remove(oldName))
			return true;
		
		//Update in the database
		try (PreparedStatement rename = Main.dbManager.getConnection().prepareStatement("UPDATE '" + databaseTableName + "' SET PATH=? WHERE PATH=?")) {
			rename.setString(1, newName);
			rename.setString(2, oldName);
			rename.executeUpdate();
			
			//Commit
			if (commit)
				Main.dbManager.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		
		//Add to the Set
		return set.add(newName);
		
	}
	
	/**
	 * Clears all the Media from the List and Database
	 * 
	 * @return True if succeeded or False if not
	 */
	public boolean clearAll(boolean commit) {
		
		try {
			//Clear the table
			Main.dbManager.getConnection().createStatement().executeUpdate("DELETE FROM '" + databaseTableName + "'");
			if (commit)
				Main.dbManager.commit();
			
			//Clear from Set
			set.clear();
		} catch (Exception ex) {
			Main.logger.log(Level.WARNING, "", ex);
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Returns the Set.
	 *
	 * @return the sets the
	 */
	public Set<String> getSet() {
		return set;
	}
	
	/**
	 * @return the databaseTableName
	 */
	public String getDatabaseTableName() {
		return databaseTableName;
	}
	
}
