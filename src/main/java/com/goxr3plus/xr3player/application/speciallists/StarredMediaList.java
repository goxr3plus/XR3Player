/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.speciallists;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The Class PlayedSongs.
 */
public class StarredMediaList extends DatabaseList {
	
	/**
	 * The name of the database table
	 */
	private static final String dataBaseTableName = "StarredMediaListOriginal";
	
	/**
	 * Constructor
	 * 
	 * @param dataBaseTableName
	 */
	public StarredMediaList() {
		super(dataBaseTableName);
	}
	
	/**
	 * Add a new item.
	 *
	 * @param path
	 *            the item
	 * @return True if succeeded or False if not
	 */
	public boolean addOrUpdateStars(String path , double stars , boolean commit) {
		boolean[] answer = { false };
		
		//If it doesn't exists inside the Set
		if (!containsFile(path))
			
			//Try to insert into the database
			try (PreparedStatement insert = Main.dbManager.getConnection()
					.prepareStatement("INSERT OR IGNORE INTO '" + getDatabaseTableName() + "' (PATH,STARS,TIMESPLAYED,DATE,HOUR) VALUES (?,?,?,?,?)")) {
				insert.setString(1, path);
				insert.setDouble(2, stars);
				insert.setInt(3, 0);
				insert.setString(4, InfoTool.getCurrentDate());
				insert.setString(5, InfoTool.getLocalTime());
				insert.executeUpdate();
				
				//Append
				getSet().add(new FakeMedia(path, stars, 0));
				
				//Commit
				if (commit)
					Main.dbManager.commit();
				
				answer[0] = true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				answer[0] = false;
			}
		else //else update existing one
			return changeStars(path, stars, commit);
		
		return answer[0];
	}
	
	/**
	 * Append 1 to TimesPlayed on the given FakeMedia Path
	 * 
	 * @return true, if successful
	 */
	public boolean changeStars(String path , double stars , boolean commit) {
		boolean[] answer = { false };
		
		//Check if it already exists
		getSet().stream().filter(fakeMedia -> fakeMedia.getPath().equals(path)).findFirst().ifPresent(fakeMedia -> {
			
			//Update in the database
			try (PreparedStatement appendToTimePlayed = Main.dbManager.getConnection().prepareStatement("UPDATE '" + getDatabaseTableName() + "' SET STARS=? WHERE PATH=?")) {
				appendToTimePlayed.setDouble(1, stars);
				appendToTimePlayed.setString(2, path);
				appendToTimePlayed.executeUpdate();
				
				//Update FakeMedia
				fakeMedia.setStars(stars);
				answer[0] = true;
				//System.out.println("Path : " + path + " , Times Played :" + fakeMedia.getTimesPlayed())
				
				//Commit
				if (commit)
					Main.dbManager.commit();
			} catch (SQLException ex) {
				ex.printStackTrace();
				answer[0] = false;
			}
		});
		
		return answer[0];
	}
	
}
