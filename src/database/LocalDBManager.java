/*
 * 
 */
package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import libraries_system.Library;
import smartcontroller.Operation;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * This class is managing the database of the application.
 *
 * @author SuperGoliath
 */
public class LocalDBManager {

    /** The connection 1. */
    public Connection connection1 = null;

    /** The zipper. */
    public final ExportDataBase zipper = new ExportDataBase();

    /** The un zipper. */
    public final ImportDataBase unZipper = new ImportDataBase();

    /** The data loader. */
    // Important Tasks
    private DataLoader dataLoader = new DataLoader();

    /** The database file. */
    // -Constructor-
    private String dbFileAbsolutePath;

    /** The images folder relative path */
    private String imagesFolderRelativePath;

    /** The images folder absolute path */
    public String imagesFolderAbsolutePath;

    /** The name of the logged in user */
    public String userName;

    /** This executor does the commit job. */
    private static final ExecutorService commitExecutor = Executors.newSingleThreadExecutor();

    /** The runnable of the commit executor. */
    private Runnable commitRunnable = () -> {
	try {
	    connection1.commit();
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, ex.getMessage(), ex);
	} finally {
	    Platform.runLater(
		    Notifications.create().text("Successfully saved changes.").hideAfter(Duration.millis(100))::show);
	}

    };

    /** The runnable of the commit executor. */
    private Runnable vacuumRunnable = () -> {
	try {
	    // close + open connection
	    connection1.commit();
	    manageConnection(Operation.CLOSE);
	    manageConnection(Operation.OPEN);

	    // vacuum
	    connection1.createStatement().executeUpdate("VACUUM");

	    // close connection
	    manageConnection(Operation.CLOSE);

	    // exit
	    System.exit(0);
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, ex.getMessage(), ex);
	    System.exit(-1);
	} finally {
	    System.exit(0);
	}

    };

    /**
     * Constructor.
     *
     * @param userName
     *            the user name
     */
    public LocalDBManager(String userName) {

	try {

	    // !!! Not needed in Java8 !!!!
	    // load the sqlite-JDBC driver using the current class loader
	    // Class.forName("org.sqlite.JDBC")

	    // the userName
	    this.userName = userName;

	    // database folder
	    if (!new File(InfoTool.ABSOLUTE_DATABASE_PATH_PLAIN).exists())
		new File(InfoTool.ABSOLUTE_DATABASE_PATH_PLAIN).mkdir();

	    // user folder
	    if (!new File(InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName).exists())
		new File(InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName).mkdir();

	    // images folder
	    imagesFolderRelativePath = InfoTool.DATABASE_FOLDER_NAME_WITH_SEPARATOR + userName + File.separator
		    + "Images";
	    imagesFolderAbsolutePath = InfoTool.ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_WITH_SEPARATOR
		    + imagesFolderRelativePath;
	    if (!new File(imagesFolderAbsolutePath).exists())
		new File(imagesFolderAbsolutePath).mkdir();

	    // database file(.db)
	    dbFileAbsolutePath = InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName + File.separator
		    + "dbFile.db";
	    boolean data1Exist = new File(dbFileAbsolutePath).exists();

	    // connection1
	    connection1 = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
	    connection1.setAutoCommit(false);

	    if (!data1Exist)
		recreateDataBase();

	} catch (SQLException ex) {
	    Main.logger.log(Level.SEVERE, "", ex);
	}
    }

    /**
     * Open or close the connection.
     *
     * @param action
     *            the action
     */
    public void manageConnection(Operation action) {
	try {
	    // OPEN
	    if (action == Operation.OPEN && connection1.isClosed())
		connection1 = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
	    // CLOSE
	    else if (action == Operation.CLOSE && !connection1.isClosed())
		connection1.close();
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Using this methods to control commits across the application so not to
     * have unexpected lags.
     */
    public void commit() {
	commitExecutor.execute(commitRunnable);
    }

    /**
     * Using this methods to control commit + vacuum across the application so
     * not to have unexpected lags.
     * 
     */
    public void commitAndVacuum() {
	commitExecutor.execute(vacuumRunnable);
    }

    /**
     * Stops the executorService.
     */
    public void shutdownCommitExecutor() {
	commitExecutor.shutdown();
    }

    /**
     * Checks if this table exists in dataBase.
     *
     * @param tableName
     *            the table name
     * @return true, if successful
     */
    public static boolean tableExists(String tableName) {
	// SQLite table names are case insensitive, but comparison is case
	// sensitive by default. To make this work properly in all cases you
	// need to add COLLATE NOCASE
	try (ResultSet r = Main.dbManager.connection1.createStatement().executeQuery(
		"SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "' COLLATE NOCASE ")) {
	    int total = r.getInt(1);
	    return total == 0 ? false : true;
	} catch (SQLException ex) {
	    Main.logger.log(Level.INFO, "", ex);
	}

	return false;
    }

    /**
     * Recreates the database if it doesn't exist.
     */
    public void recreateDataBase() {

	Main.logger.info("1-->Recreating the database..");

	try (Statement statement = connection1.createStatement()) {

	    // ----------Libraries Table ----------------//
	    statement.executeUpdate("CREATE TABLE LIBRARIES(NAME          TEXT    PRIMARY KEY   NOT NULL,"
		    + "TABLENAME TEXT NOT NULL," + "STARS         DOUBLE     NOT NULL,"
		    + "DATECREATED          TEXT   	NOT NULL," + "TIMECREATED          TEXT    NOT NULL,"
		    + "DESCRIPTION   TEXT    NOT NULL," + "SAVEMODE      INT     NOT NULL,"
		    + "POSITION      INT     NOT NULL," + "LIBRARYIMAGE  TEXT," + "OPENED BOOLEAN NOT NULL )");

	    // -----------Radio Stations Table ------------//
	    statement.executeUpdate("CREATE TABLE '" + InfoTool.RADIO_STATIONS_DATABASE_TABLE_NAME
		    + "'(NAME TEXT PRIMARY KEY NOT NULL," + "STREAMURL TEXT NOT NULL," + "TAGS TEXT NOT NULL,"
		    + "DESCRIPTION TEXT," + "STARS DOUBLE NOT NULL)");

	    // ----------XPlayers PlayLists Table ----------//
	    for (int i = 0; i < 3; i++)
		createXPlayListTable(statement, i);

	    commit();
	} catch (SQLException ex) {
	    Main.logger.log(Level.SEVERE, "", ex);
	}
    }

    /**
     * Create a database table for the specific XPlayer.
     *
     * @param statement
     *            the statement
     * @param key
     *            the key
     * @throws SQLException
     *             the SQL exception
     */
    private void createXPlayListTable(Statement statement, int key) throws SQLException {
	statement.executeUpdate("CREATE TABLE XPPL" + key + "(PATH       TEXT    PRIMARY KEY   NOT NULL ,"
		+ "STARS       DOUBLE     NOT NULL," + "TIMESPLAYED  INT     NOT NULL,"
		+ "DATE        TEXT   	NOT NULL," + "HOUR        TEXT    NOT NULL)");
    }

    /**
     * This method loads the application database.
     */
    public void loadApplicationDataBase() {
	Main.updateScreen.setVisible(true);
	Main.updateScreen.progressBar.progressProperty().bind(dataLoader.progressProperty());
	dataLoader.reset();
	dataLoader.start();
    }

    /**
     * DataLoader.
     *
     * @author SuperGoliath
     */
    public class DataLoader extends Service<Void> {

	/** The total. */
	int total;

	/**
	 * Constructor.
	 */
	public DataLoader() {

	    // if succeeded
	    setOnSucceeded(s -> {

		// Do the animation with rectangles
		Main.updateScreen.closeUpdateScreen();

		// Wait until the animation is finished and then fix the layout
		new Thread(() -> {
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException ex) {
			ex.printStackTrace();
		    }

		    // JavaFX Thread
		    Platform.runLater(() -> {
			Main.libraryMode.add(Main.multipleTabs, 0, 1);
			Main.root.setCenter(Main.libraryMode);
			Main.updateScreen.setVisible(false);
			Main.updateScreen.progressBar.progressProperty().unbind();
			//Main.sideBar.showBar();

			// Check for updates on start
			new Thread(() -> {
			    if (InfoTool.isReachableByPing("www.google.com"))
				Main.checkForUpdates(false);
			}).start();
		    });
		}).start();
	    });

	    // if failed
	    setOnFailed(fail -> {
		Main.updateScreen.progressBar.progressProperty().unbind();
		ActionTool.showNotification("Fatal Error!",
			"DataLoader failed during loading dataBase!!Application will exit...", Duration.millis(1500),
			NotificationType.ERROR);
		System.exit(0);
	    });
	}

	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {
		@Override
		protected Void call() throws Exception {

		    // -------------------------- Load all the libraries
		    try (ResultSet resultSet = connection1.createStatement().executeQuery("SELECT* FROM LIBRARIES;");
			    ResultSet dbCounter = connection1.createStatement()
				    .executeQuery("SELECT COUNT(*) FROM LIBRARIES;");) {

			total = dbCounter.getInt(1);
			Main.logger.info("Loading libraries....");

			// Refresh the text
			Platform.runLater(() -> Main.updateScreen.label.setText("Uploading Libraries..."));
			updateProgress(1, 2);

			// Load all the libraries
			while (resultSet.next()) {

			    Library library = new Library(resultSet.getString("NAME"), resultSet.getString("TABLENAME"),
				    resultSet.getDouble("STARS"), resultSet.getString("DATECREATED"),
				    resultSet.getString("TIMECREATED"), resultSet.getString("DESCRIPTION"),
				    resultSet.getInt("SAVEMODE"), resultSet.getInt("POSITION"),
				    resultSet.getString("LIBRARYIMAGE"), resultSet.getBoolean("OPENED"));

			    Main.libraryMode.libraryViewer.addLibrary(library);

			    // opened?
			    if (resultSet.getBoolean("OPENED"))
				library.libraryOpenClose(true, true);

			    updateProgress(resultSet.getRow(), total);
			}

			Main.libraryMode.libraryViewer.getItems().stream().filter(Library::isLibraryOpened).findFirst()
				.ifPresent(library -> {
				    if (library.isLibraryOpened())
					Main.libraryMode.libraryViewer.setCenterIndex(library.getPosition());
				});

			// update library viewer
			Main.libraryMode.libraryViewer.update();
			Main.libraryMode.libraryViewer.goOnSelectionMode(false);

			// set libraries tree expanded
			Main.treeManager.librariesTree.setExpanded(true);

		    } catch (Exception ex) {
			Main.logger.log(Level.SEVERE, "", ex);
		    }

		    return null;
		}
	    };
	}

    }
}
