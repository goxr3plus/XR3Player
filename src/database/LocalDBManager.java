/*
 * 
 */
package database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import application.Main;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import javafx.util.Duration;
import librarysystema.Library;
import smartcontroller.Operation;
import smartcontroller.SmartController;
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
    public Connection connection1;

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
    /** This executor does the commit job. */
    private static final ExecutorService jSONUpdateExecutor = Executors.newSingleThreadExecutor();

    /** If true -> The database notifications are shown */
    private final boolean showNotifications = false;

    /** The runnable of the commit executor. */
    private Runnable commitRunnable = () -> {
	try {
	    connection1.commit();
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, ex.getMessage(), ex);
	} finally {
	    if (showNotifications)
		ActionTool.showNotification("Commited", "Changes saved successfully", Duration.millis(150), NotificationType.INFORMATION);
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

    //-----------------------------------------------------------

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

	    // user folder
	    File userFolder = new File(InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName);
	    if (!userFolder.exists())
		userFolder.mkdir();

	    //--images folder
	    imagesFolderRelativePath = InfoTool.DATABASE_FOLDER_NAME_WITH_SEPARATOR + userName + File.separator + "Images";
	    imagesFolderAbsolutePath = InfoTool.ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_WITH_SEPARATOR + imagesFolderRelativePath;
	    //--
	    File imagesFolder = new File(imagesFolderAbsolutePath);
	    if (!imagesFolder.exists())
		imagesFolder.mkdir();

	    // database file(.db)
	    dbFileAbsolutePath = InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName + File.separator + "dbFile.db";
	    boolean data1Exist = new File(dbFileAbsolutePath).exists();

	    // connection1
	    connection1 = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
	    connection1.setAutoCommit(false);

	    if (!data1Exist)
		recreateDataBase();

	    recreateJSonDataBase();

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
     * Using this methods to control commits across the application so not to have unexpected lags.
     */
    public void commit() {
	commitExecutor.execute(commitRunnable);
    }

    /**
     * Using this methods to control commit + vacuum across the application so not to have unexpected lags.
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
	try (ResultSet r = Main.dbManager.connection1.createStatement()
		.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "' COLLATE NOCASE ")) {
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
	    statement.executeUpdate("CREATE TABLE LIBRARIES(NAME          TEXT    PRIMARY KEY   NOT NULL," + "TABLENAME TEXT NOT NULL,"
		    + "STARS         DOUBLE     NOT NULL," + "DATECREATED          TEXT   	NOT NULL," + "TIMECREATED          TEXT    NOT NULL,"
		    + "DESCRIPTION   TEXT    NOT NULL," + "SAVEMODE      INT     NOT NULL," + "POSITION      INT     NOT NULL,"
		    + "LIBRARYIMAGE  TEXT," + "OPENED BOOLEAN NOT NULL )");

	    // -----------Radio Stations Table ------------//
	    statement.executeUpdate("CREATE TABLE '" + InfoTool.RADIO_STATIONS_DATABASE_TABLE_NAME + "'(NAME TEXT PRIMARY KEY NOT NULL,"
		    + "STREAMURL TEXT NOT NULL," + "TAGS TEXT NOT NULL," + "DESCRIPTION TEXT," + "STARS DOUBLE NOT NULL)");

	    // ----------XPlayers PlayLists Tables ----------//
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
	statement.executeUpdate("CREATE TABLE XPPL" + key + "(PATH       TEXT    PRIMARY KEY   NOT NULL ," + "STARS       DOUBLE     NOT NULL,"
		+ "TIMESPLAYED  INT     NOT NULL," + "DATE        TEXT   	NOT NULL," + "HOUR        TEXT    NOT NULL)");
    }

    /**
     * This method loads the application database.
     */
    public void loadApplicationDataBase() {
	Main.updateScreen.setVisible(true);
	Main.updateScreen.progressBar.progressProperty().bind(dataLoader.progressProperty());
	dataLoader.restart();
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

	    // -------------------if succeeded
	    setOnSucceeded(s -> {

		//----------------Do the animation with rectangles---------------------
		Main.updateScreen.closeUpdateScreen();

		//----------------Finall Settings---------------------
		// update library viewer
		Main.libraryMode.teamViewer.getViewer().update();
		//Main.libraryMode.libraryViewer.goOnSelectionMode(false)

		// set libraries tree expanded
		//Main.treeManager.librariesTree.setExpanded(true)

		//---------------Set the update Screen invisible---------------------
		PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
		pause1.setOnFinished(f -> {
		    Main.updateScreen.setVisible(false);
		    Main.updateScreen.progressBar.progressProperty().unbind();
		});
		pause1.playFromStart();

	    });

	    // ---------------------if failed
	    setOnFailed(fail -> {
		Main.updateScreen.progressBar.progressProperty().unbind();
		ActionTool.showNotification("Fatal Error!", "DataLoader failed during loading dataBase!!Application will exit...",
			Duration.millis(1500), NotificationType.ERROR);
		System.exit(0);
	    });
	}

	//-------------------Needs modification cause it violates JavaFX THREAD!!!!!!!!!!!!
	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {
		@Override
		protected Void call() throws Exception {
		    int counter;
		    int total = 1;
		    updateProgress(0, total);

		    // -------------------------- Load all the libraries
		    try (ResultSet resultSet = connection1.createStatement().executeQuery("SELECT* FROM LIBRARIES;");
			    ResultSet dbCounter = connection1.createStatement().executeQuery("SELECT COUNT(*) FROM LIBRARIES;");) {

			total += dbCounter.getInt(1);
			Main.logger.info("Loading Libraries....");

			// Refresh the text
			Platform.runLater(() -> Main.updateScreen.label.setText("Loading Libraries..."));
			updateProgress(1, 2);

			//Kepp a List of all Libraries
			final List<Library> libraries = new ArrayList<>(total);

			// Load all the libraries
			while (resultSet.next()) {
			    libraries.add(new Library(resultSet.getString("NAME"), resultSet.getString("TABLENAME"), resultSet.getDouble("STARS"),
				    resultSet.getString("DATECREATED"), resultSet.getString("TIMECREATED"), resultSet.getString("DESCRIPTION"),
				    resultSet.getInt("SAVEMODE"), resultSet.getInt("POSITION"), resultSet.getString("LIBRARYIMAGE"),
				    resultSet.getBoolean("OPENED")));

			    updateProgress(resultSet.getRow() - 1, total);
			}

			//Add all the Libraries to the Library Viewer
			Platform.runLater(() -> Main.libraryMode.teamViewer.getViewer().addMultipleLibraries(libraries));

			//Load the Opened Libraries
			Platform.runLater(() -> Main.updateScreen.label.setText("Loading Opened Libraries..."));
			loadOpenedLibraries();

			//Load PlayerMediaList
			Platform.runLater(() -> Main.updateScreen.label.setText("Loading previous data..."));
			Main.playedSongs.uploadFromDataBase();

			//--FINISH
			updateProgress(total, total);

		    } catch (Exception ex) {
			Main.logger.log(Level.SEVERE, "", ex);
		    }

		    return null;
		}
	    };
	}

    }

    //-------------------------------------JSON----------------------------------------

    /**
     * Loads the Libraries information into the application
     * 
     * @return True if succedeed or False if not
     */
    public boolean loadOpenedLibraries() {

	String jsonFilePath = InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName + File.separator + "settings.json";

	//Check if the file exists
	if (!new File(jsonFilePath).exists())
	    return false;

	//Read the JSON File
	try (FileReader fileReader = new FileReader(jsonFilePath)) {

	    //JSON Array [ROOT]
	    JsonObject json = (JsonObject) Jsoner.deserialize(fileReader);

	    //Opened Libraries Array
	    JsonArray openedLibraries = (JsonArray) ((JsonObject) json.get("librariesSystem")).get("openedLibraries");

	    //For each Library
	    openedLibraries.forEach(libraryObject -> Platform.runLater(() ->

	    //Get the Library and Open it!
	    Main.libraryMode.getLibraryWithName(((JsonObject) libraryObject).get("name").toString()).libraryOpenClose(true, true)

	    //Print its name
	    //System.out.println(((JsonObject) libraryObject).get("name"))
	    ));

	    //Last selected library Array
	    JsonObject lastSelectedLibrary = (JsonObject) ((JsonObject) json.get("librariesSystem")).get("lastSelectedLibrary");

	    //Add the Listener to multipleLibs
	    Platform.runLater(() -> {
		Main.libraryMode.multipleLibs.getTabPane().getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {

		    // Give a refresh to the newly selected ,!! ONLY IF IT HAS NO ITEMS !! 
		    if (!Main.libraryMode.multipleLibs.getTabPane().getTabs().isEmpty() && ((SmartController) newTab.getContent()).isFree(false)
			    && ((SmartController) newTab.getContent()).itemsObservableList.isEmpty()) {

			((SmartController) newTab.getContent()).loadService.startService(false, true);

			Main.dbManager.updateLibrariesInformation(Main.libraryMode.multipleLibs.getTabPane().getTabs(), false);

		    }

		    //			    //Do an animation
		    //			    if (oldTab != null && newTab != null) {
		    //				Node oldContent = oldTab.getContent(); //tabContent.get(oldTab)
		    //				Node newContent = newTab.getContent(); //tabContent.get(newTab)
		    //
		    //				newTab.setContent(oldContent);
		    //				ScaleTransition fadeOut = new ScaleTransition(Duration.millis(50), oldContent);
		    //				fadeOut.setFromX(1);
		    //				fadeOut.setFromY(1);
		    //				fadeOut.setToX(0);
		    //				fadeOut.setToY(0);
		    //
		    //				ScaleTransition fadeIn = new ScaleTransition(Duration.millis(50), newContent);
		    //				fadeIn.setFromX(0);
		    //				fadeIn.setFromY(0);
		    //				fadeIn.setToX(1);
		    //				fadeIn.setToY(1);
		    //
		    //				fadeOut.setOnFinished(event -> newTab.setContent(newContent));
		    //
		    //				SequentialTransition crossFade = new SequentialTransition(fadeOut, fadeIn);
		    //				crossFade.play();
		    //			    }
		});
	    });

	    //If not empty...
	    if (!lastSelectedLibrary.isEmpty()) {
		Platform.runLater(() -> {

		    //Select the correct library inside the TabPane
		    Main.libraryMode.multipleLibs.getTabPane().getSelectionModel()
			    .select(Main.libraryMode.multipleLibs.getTab(lastSelectedLibrary.get("name").toString()));

		    //This will change in future update when user can change the default position of Libraries
		    Main.libraryMode.teamViewer.getViewer().setCenterIndex(Main.libraryMode.multipleLibs.getSelectedLibrary().getPosition());

		    //System.out.println("Entered !lastSelectedLibrary.isEmpty()")
		});
	    }

	    //Do an Update on the selected Library SmartController
	    Platform.runLater(() -> {
		//Check if empty and if not update the selected library
		if (!Main.libraryMode.multipleLibs.getTabs().isEmpty()
			&& Main.libraryMode.multipleLibs.getSelectedLibrary().getSmartController().isFree(false))
		    Main.libraryMode.multipleLibs.getSelectedLibrary().getSmartController().loadService.startService(false, true);
	    });

	} catch (IOException | DeserializationException e) {
	    e.printStackTrace();
	    //  logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
	    return false;
	}

	return true;
    }

    /**
     * Stores the informations about the opened libraries , in the order they are opened
     * 
     * @param observableList
     * @param updateOpenedLibraries
     * 
     * @return True if succedeed or False if not
     */
    public boolean updateLibrariesInformation(ObservableList<Tab> observableList, boolean updateOpenedLibraries) {
	String jsonFilePath = InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName + File.separator + "settings.json";

	if (!new File(jsonFilePath).exists())
	    return false;

	//Update the JSON File on an external Thread
	jSONUpdateExecutor.execute(() -> {
	    try (FileReader fileReader = new FileReader(jsonFilePath)) {
		Object obj = Jsoner.deserialize(fileReader);

		//JSON Array [ROOT]
		JsonObject json = (JsonObject) obj;

		//Last selected library Array
		JsonObject lastSelectedLibrary = (JsonObject) ((JsonObject) json.get("librariesSystem")).get("lastSelectedLibrary");

		if (observableList.isEmpty())
		    lastSelectedLibrary.clear();
		else
		    observableList.forEach(tab -> {
			if (tab.isSelected())
			    lastSelectedLibrary.put("name", tab.getTooltip().getText());
		    });

		//Update the opened libraries?
		if (updateOpenedLibraries) {

		    //Opened Libraries Array
		    JsonArray openedLibraries = (JsonArray) ((JsonObject) json.get("librariesSystem")).get("openedLibraries");
		    openedLibraries.clear();

		    //Add the Libraries to the Libraries Array
		    //System.out.println()
		    observableList.forEach(tab -> {

			//Add it to opened libraries
			JsonObject object = new JsonObject();
			object.put("name", tab.getTooltip().getText());
			openedLibraries.add(object);

			//System.out.println(tab.getTooltip().getText())
		    });

		}

		//Write to File
		try (FileWriter file = new FileWriter(jsonFilePath)) {
		    file.write(Jsoner.prettyPrint(json.toJson()));
		    file.flush();
		} catch (IOException e) {
		    e.printStackTrace();
		    //logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
		    //return false
		}

	    } catch (IOException | DeserializationException e) {
		e.printStackTrace();
		//  logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
		// return false
	    } finally {
		if (showNotifications)
		    ActionTool.showNotification("JSON Updated", "JSON File Updated...", Duration.millis(150), NotificationType.INFORMATION);
	    }
	});

	//Returns always true needs to be fixed!!!
	return true;
    }

    /**
     * Creates the JSONDatabase if it doesn't exitst
     * 
     * @return True if succedeed or False if not
     */
    public boolean recreateJSonDataBase() {
	String jsonFilePath = InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + userName + File.separator + "settings.json";

	//File already exists?
	if (new File(jsonFilePath).exists())
	    return true;

	//JSON Array [ROOT]
	JsonObject json = new JsonObject();

	//-----------Libraries Array------------------
	JsonObject librariesSystem = new JsonObject();

	//Latest Library that was selected + Opened
	JsonObject lastSelectedLibrary = new JsonObject();

	//Libraries that where opened
	JsonArray openedLibraries = new JsonArray();
	//	for (int i = 0; i < 2; i++) {
	//	    JsonObject object = new JsonObject();
	//	    object.put("name", "library->" + i);
	//	    openedLibraries.add(object);
	//	}

	librariesSystem.put("openedLibraries", openedLibraries);
	librariesSystem.put("lastSelectedLibrary", lastSelectedLibrary);

	//--------------XPlayers Array--------------

	JsonArray xPlayers = new JsonArray();
	for (int i = 0; i < 3; i++) {
	    JsonObject object = new JsonObject();
	    object.put("name", "xPlayer" + i);
	    xPlayers.add(object);
	}

	json.put("librariesSystem", librariesSystem);
	json.put("xPlayers", xPlayers);

	//Write to File
	try (FileWriter file = new FileWriter(jsonFilePath)) {
	    file.write(Jsoner.prettyPrint(json.toJson()));
	    file.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	    //logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
	    return false;
	}

	return true;
    }
}
