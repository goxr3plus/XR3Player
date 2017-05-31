/*
 * 
 */
package xplayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import application.Main;
import application.tools.InfoTool;

/**
 * The Class PlayedSongs.
 */
public class PlayedMediaList {
	
	/** The LinkedHashSet */
	private Set<String> set = new LinkedHashSet<>();
	
	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "PlayedMediaList";
	
	//------------Prepared Statements---------------
	private PreparedStatement insert;
	private PreparedStatement rename;
	
	/**
	 * Prepares the DataBase table (if not exists) , i do this to keep backward
	 * compatibility with previous XR3Player Versions ( Update 57<) Also it
	 * creates the PreparedStatement to insert Files Paths into the Table
	 */
	private void prepareMediaListTable() {
		
		try {
			//Check if it does already exists
			if (!Main.dbManager.doesTableExist(dataBaseTableName))
				
				Main.dbManager.getConnection().createStatement().executeUpdate(
						"CREATE TABLE '" + dataBaseTableName + "'(PATH   TEXT  PRIMARY KEY   NOT NULL ,TIMESPLAYED  INT  NOT NULL,DATE   TEXT   NOT NULL , HOUR  TEXT  NOT NULL)");
			
			//Create the PreparedStatements
			String string = "UPDATE '" + dataBaseTableName + "'";
			
			insert = Main.dbManager.getConnection().prepareStatement("INSERT OR IGNORE INTO '" + dataBaseTableName + "' (PATH,TIMESPLAYED,DATE,HOUR) VALUES (?,?,?,?)");
			
			rename = Main.dbManager.getConnection().prepareStatement(string + " SET PATH=? WHERE PATH=?");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Uploads the data from the database table to the list , i call this method
	 * when i login into a user to upload the Media that he/she has
	 * previously heard
	 */
	public void uploadFromDataBase() {
		
		//Check existence
		prepareMediaListTable();
		
		//Now Upload
		try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery("SELECT* FROM '" + dataBaseTableName + "'")) {
			
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
	 *        the item
	 * @return True if succeeded or False if not
	 */
	public boolean add(String item) {
		
		try {
			insert.setString(1, item);
			insert.setInt(2, 0);
			insert.setString(3, InfoTool.getCurrentDate());
			insert.setString(4, InfoTool.getLocalTime());
			insert.executeUpdate();
			
			//Commit
			Main.dbManager.commit();
			
			return set.add(item);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Check if a media has been already played.
	 *
	 * @param filePath
	 *        The absolute file path
	 * @return true, if successful
	 */
	public boolean containsFile(String filePath) {
		return set.contains(filePath);
	}
	
	/**
	 * Prints all the Songs that had been played.
	 */
	public void printPlayedSongs() {
		set.stream().forEach(System.out::println);
	}
	
	/**
	 * Renames the media with this name if exists [ in list ].
	 *
	 * @param oldName
	 *        the old name
	 * @param newName
	 *        the new name
	 * @return true, if successful
	 */
	public boolean renameMedia(String oldName , String newName) {
		if (!set.remove(oldName))
			return true;
		
		//Update in the database
		try {
			rename.setString(1, newName);
			rename.setString(2, oldName);
			rename.executeUpdate();
			
			//Commit
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
	public boolean clearAll() {
		
		try {
			//Clear the table
			Main.dbManager.getConnection().createStatement().executeUpdate("DELETE FROM '" + dataBaseTableName + "'");
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
	
}
