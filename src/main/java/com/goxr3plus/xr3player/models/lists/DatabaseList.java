package com.goxr3plus.xr3player.models.lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.DateTimeTool;

/**
 * A special kind of list which is used to save the list to the database and
 * also have it in RAM memory of the computer
 *
 * @author GOXR3PLUS
 *
 */
public class DatabaseList {

	/** The LinkedHashSet */
	private final Set<FakeMedia> set = new LinkedHashSet<>();

	/**
	 * The name of the database table
	 */
	private final String databaseTableName;

	/**
	 * Constructor
	 *
	 * @param dataBaseTableName The name of the database table
	 */
	public DatabaseList(String dataBaseTableName) {
		this.databaseTableName = dataBaseTableName;
	}

	// ------------Prepared Statements---------------

	/**
	 * Uploads the data from the database table to the list , i call this method
	 * when i login into a user to upload the Media that he/she has previously heard
	 */
	public void uploadFromDataBase() {

		// Check existence
		prepareMediaListTable();

		// Now Upload
		try (ResultSet resultSet = Main.dbManager.getConnection().createStatement()
				.executeQuery("SELECT* FROM '" + databaseTableName + "'")) {

			// Add all
			while (resultSet.next()) {
				FakeMedia fakeMedia = new FakeMedia(
						resultSet.getString("PATH"),
						resultSet.getDouble("STARS"),
						resultSet.getInt("TIMESPLAYED"));
				set.add(fakeMedia);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Prepares the DataBase table (if not exists) , i do this to keep backward
	 * compatibility with previous XR3Player Versions ( Update 57<) Also it creates
	 * the PreparedStatement to insert Files Paths into the Table
	 */
	private void prepareMediaListTable() {

		try {
			// IF DATABASE DOESN'T EXIST
			if (!Main.dbManager.doesTableExist(databaseTableName)) {
				try (Connection connection = Main.dbManager.getConnection()) {
					try (Statement statement = connection.createStatement()) {
						String sql = "CREATE TABLE '" + databaseTableName
								+ "'(PATH   TEXT  PRIMARY KEY   NOT NULL ,STARS DOUBLE NOT NULL , TIMESPLAYED  INT  NOT NULL,DATE   TEXT   NOT NULL , HOUR  TEXT  NOT NULL)";
						statement.executeUpdate(sql);
						Main.dbManager.getConnection().commit();
					}
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Add a new item.
	 *
	 * @param path the item
	 * @return True if succeeded or False if not
	 */
	public boolean add(String path, boolean commit) {
		boolean[] answer = { false };

		// If it doesn't exists inside the Set
		if (!containsFile(path))

			// Try to insert into the database
			try (PreparedStatement insert = Main.dbManager
					.getConnection()
					.prepareStatement("INSERT OR IGNORE INTO '"
					+ databaseTableName + "' (PATH,STARS,TIMESPLAYED,DATE,HOUR) VALUES (?,?,?,?,?)")) {
				insert.setString(1, path);
				insert.setDouble(2, 0.0);
				insert.setInt(3, 0);
				insert.setString(4, DateTimeTool.getCurrentDate());
				insert.setString(5, DateTimeTool.getLocalTime());
				insert.executeUpdate();

				// Append
				set.add(new FakeMedia(path, 0, 0));

				// Commit
				if (commit)
					Main.dbManager.commit();

				answer[0] = true;
			} catch (SQLException ex) {
				ex.printStackTrace();
				answer[0] = false;
			}

		return answer[0];
	}

	/**
	 * Renames the media with this name if exists [ in list ].
	 *
	 * @param oldName the old name
	 * @param newName the new name
	 * @return true, if successful
	 */
	public boolean renameMedia(String oldName, String newName, boolean commit) {
		boolean[] answer = {false};

		// Check if it already exists
		set.stream()
				.filter(fakeMedia -> fakeMedia.getPath().equals(oldName))
				.findFirst()
				.ifPresent(fakeMedia -> {
					fakeMedia.setPath(newName);
					answer[0] = true;
				});

		// Update in the database
		try (PreparedStatement rename = Main.dbManager.getConnection()
				.prepareStatement("UPDATE '" + databaseTableName + "' SET PATH=? WHERE PATH=?")) {
			rename.setString(1, newName);
			rename.setString(2, oldName);
			rename.executeUpdate();

			// Commit
			if (commit)
				Main.dbManager.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
			answer[0] = false;
		}

		return answer[0];
	}

	/**
	 * Add a new item.
	 *
	 * @param path the item
	 * @return True if succeeded or False if not
	 */
	public boolean remove(String path, boolean commit) {
		boolean[] answer = { false };
		FakeMedia[] fakeM = { null };

		set.stream()
				.filter(fakeMedia -> fakeMedia.getPath().equals(path))
				.findFirst()
				.ifPresent(fakeMedia -> {
					fakeM[0] = fakeMedia;

					// Try to delete from the database
					try (PreparedStatement remove = Main.dbManager.getConnection()
							.prepareStatement("DELETE FROM '" + databaseTableName + "' WHERE PATH=?")) {
						remove.setString(1, path);
						remove.executeUpdate();

						// Commit
						if (commit)
							Main.dbManager.commit();

						answer[0] = true;
					} catch (SQLException ex) {
						ex.printStackTrace();
						answer[0] = false;
					}
				});

		// Check if it was found
		if (fakeM[0] != null)
			set.remove(fakeM[0]);

		return answer[0];
	}

	/**
	 * Check if a media has been already played.
	 *
	 * @param path The absolute file path
	 * @return true, if successful
	 */
    public boolean containsFile(String path) {
        boolean[] answer = {false};

        set.stream()
                .filter(fakeMedia -> fakeMedia.getPath().equals(path))
                .findFirst()
                .ifPresent(fakeMedia -> answer[0] = true);
        return answer[0];
    }

	/**
	 * Clears all the Media from the List and Database
	 *
	 * @return True if succeeded or False if not
	 */
	public boolean clearAll(boolean commit) {

		try (Statement statement = Main.dbManager.getConnection().createStatement()) {

			// Clear the table
			statement.executeUpdate("DELETE FROM '" + databaseTableName + "'");
			if (commit)
				Main.dbManager.commit();

			// Clear from Set
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
	public Set<FakeMedia> getSet() {
		return set;
	}

	/**
	 * @return the databaseTableName
	 */
	public String getDatabaseTableName() {
		return databaseTableName;
	}

}
