/*
 * 
 */
package com.goxr3plus.xr3player.models.lists;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.goxr3plus.xr3player.application.Main;

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
	 */
	public PlayedMediaList() {
		super(dataBaseTableName);
	}

	/**
	 * Append 1 to TimesPlayed on the given FakeMedia Path
	 * 
	 * @return true, if successful
	 */
	public boolean appendToTimesPlayed(String path, boolean commit) {
		boolean[] answer = { false };

		// Check if it already exists
		getSet().stream().filter(fakeMedia -> fakeMedia.getPath().equals(path)).findFirst().ifPresent(fakeMedia -> {

			// Update in the database
			try (PreparedStatement appendToTimePlayed = Main.dbManager.getConnection()
					.prepareStatement("UPDATE '" + getDatabaseTableName() + "' SET TIMESPLAYED=? WHERE PATH=?")) {
				appendToTimePlayed.setInt(1, fakeMedia.getTimesPlayed() + 1);
				appendToTimePlayed.setString(2, path);
				appendToTimePlayed.executeUpdate();

				// Update FakeMedia
				fakeMedia.setTimesPlayed(fakeMedia.getTimesPlayed() + 1);
				answer[0] = true;
				// System.out.println("Path : " + path + " , Times Played :" +
				// fakeMedia.getTimesPlayed())

				// Commit
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
