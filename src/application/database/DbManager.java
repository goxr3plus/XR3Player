/*
 * 
 */
package application.database;

import java.io.File;
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

import application.Main;
import application.librarymode.Library;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import application.tools.ActionTool.FileType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import smartcontroller.services.Operation;

/**
 * This class is managing the database of the application.
 *
 * @author GOXR3PLUS
 */
public class DbManager {
	
	/** The connection 1. */
	private Connection connection;
	
	/** The data loader. */
	private DataLoader dataLoader = new DataLoader();
	
	/** This executor does the commit job. */
	private final ExecutorService commitExecutor = Executors.newSingleThreadExecutor();
	
	/** If true -> The database notifications are shown */
	final boolean showNotifications = false;
	
	//----------------------
	
	/**
	 * The KeyValueDb
	 */
	private JSONDB keyValueDb = new JSONDB(this);
	
	/**
	 * The PropertiesDb
	 */
	private PropertiesDb propertiesDb;
	
	//-------------------------
	
	/** The runnable of the commit executor. */
	private Runnable commitRunnable = () -> {
		try {
			connection.commit();
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
			if (connection != null) {
				// close + open connection
				connection.commit();
				manageConnection(Operation.CLOSE);
				manageConnection(Operation.OPEN);
				
				// vacuum
				connection.createStatement().executeUpdate("VACUUM");
				
				// close connection
				manageConnection(Operation.CLOSE);
			}
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
	 * 
	 *
	 * @param userName
	 *            the user name
	 */
	public void initialize(String userName) {
		
		// Initialise
		InfoTool.setUserName(userName);
		
		//Create the propertiesDb
		File settingsFolder = new File(InfoTool.getUserFolderAbsolutePathWithSeparator() + "settings");
		ActionTool.createFileOrFolder(settingsFolder, FileType.DIRECTORY);
		propertiesDb = new PropertiesDb(settingsFolder + File.separator + "config.properties");
		propertiesDb.loadProperties();
		
		// User Folder
		File userFolder = new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + userName);
		ActionTool.createFileOrFolder(userFolder, FileType.DIRECTORY);
		
		// Images Folder
		File imagesFolder = new File(InfoTool.getImagesFolderAbsolutePathPlain());
		ActionTool.createFileOrFolder(imagesFolder, FileType.DIRECTORY);
		
		//XPlayer Images Folder
		File xPlayerImagesFolder = new File(InfoTool.getXPlayersImageFolderAbsolutePathPlain());
		ActionTool.createFileOrFolder(xPlayerImagesFolder, FileType.DIRECTORY);
		
		//Attempt DataBase connection
		try {
			String dbFileAbsolutePath = InfoTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db";
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
			connection.setAutoCommit(false);
			
			//if (!data1Exist)
			
			//This method keeps backward compatibility with previous XR3Player versions
			createDatabaseMissingTables();
			
			keyValueDb.recreateJSonDataBase();
			
		} catch (SQLException ex) {
			Main.logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	//------------------- Getters ---------------------------------------------------------------
	
	/**
	 * @return the keyValueDb
	 */
	public JSONDB getKeyValueDb() {
		return keyValueDb;
	}
	
	/**
	 * @return the propertiesDb
	 */
	public PropertiesDb getPropertiesDb() {
		return propertiesDb;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	//------------------- Setters --------------------------------------------------------------
	
	//------------------- Methods --------------------------------------------------------------
	
	/**
	 * Open or close the connection.
	 *
	 * @param action
	 *            the action
	 */
	public void manageConnection(Operation action) {
		try {
			// OPEN
			if (action == Operation.OPEN && connection.isClosed())
				connection = DriverManager.getConnection("jdbc:sqlite:" + InfoTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db");
			// CLOSE
			else if (action == Operation.CLOSE && connection != null && !connection.isClosed())
				connection.close();
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
	 * Checks if this table exists in the SQL DataBase
	 *
	 * @param tableName
	 *            the table name
	 * @return true, if successful
	 */
	public boolean doesTableExist(String tableName) {
		// SQLite table names are case insensitive, but comparison is case
		// sensitive by default. To make this work properly in all cases you
		// need to add COLLATE NOCASE
		try (ResultSet r = connection.createStatement().executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "' COLLATE NOCASE ")) {
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
	public void createDatabaseMissingTables() {
		
		Main.logger.info("1-->Checking for missing database tables");
		
		try (Statement statement = connection.createStatement()) {
			
			// ----------Libraries Table ----------------//
			statement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS LIBRARIES (NAME          TEXT    PRIMARY KEY   NOT NULL," + "TABLENAME TEXT NOT NULL," + "STARS         DOUBLE     NOT NULL,"
							+ "DATECREATED          TEXT   	NOT NULL," + "TIMECREATED          TEXT    NOT NULL," + "DESCRIPTION   TEXT    NOT NULL,"
							+ "SAVEMODE      INT     NOT NULL," + "POSITION      INT     NOT NULL," + "LIBRARYIMAGE  TEXT," + "OPENED BOOLEAN NOT NULL )");
			
			// -----------Radio Stations Table ------------//
			//  statement.executeUpdate("CREATE TABLE '" + InfoTool.RADIO_STATIONS_DATABASE_TABLE_NAME + "'(NAME TEXT PRIMARY KEY NOT NULL,"
			//	    + "STREAMURL TEXT NOT NULL," + "TAGS TEXT NOT NULL," + "DESCRIPTION TEXT," + "STARS DOUBLE NOT NULL)");
			
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
	private void createXPlayListTable(Statement statement , int key) throws SQLException {
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS `XPPL" + key + "` (PATH       TEXT    PRIMARY KEY   NOT NULL ," + "STARS       DOUBLE     NOT NULL,"
				+ "TIMESPLAYED  INT     NOT NULL," + "DATE        TEXT   	NOT NULL," + "HOUR        TEXT    NOT NULL)");
	}
	
	/**
	 * This method loads the application database.
	 */
	public void loadApplicationDataBase() {
		Main.updateScreen.setVisible(true);
		Main.updateScreen.getProgressBar().progressProperty().bind(dataLoader.progressProperty());
		dataLoader.restart();
	}
	
	/**
	 * DataLoader.
	 *
	 * @author GOXR3PLUS STUDIO
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
					Main.updateScreen.getProgressBar().progressProperty().unbind();
				});
				pause1.playFromStart();
				
			});
			
			// ---------------------if failed
			setOnFailed(fail -> {
				Main.updateScreen.getProgressBar().progressProperty().unbind();
				ActionTool.showNotification("Fatal Error!", "DataLoader failed during loading dataBase!!Application will exit...", Duration.millis(1500), NotificationType.ERROR);
				System.exit(0);
			});
		}
		
		//TODO-------------------Needs modification cause sometimes it violates JavaFX THREAD!!!!!!!!!!!!
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					int counter;
					int total = 1;
					updateProgress(0, total);
					
					// -------------------------- Load all the libraries
					try (ResultSet resultSet = getConnection().createStatement().executeQuery("SELECT* FROM LIBRARIES;");
							ResultSet dbCounter = getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM LIBRARIES;");) {
						
						total += dbCounter.getInt(1);
						Main.logger.info("Loading Libraries....");
						
						// Refresh the text
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("Loading Libraries..."));
						updateProgress(1, 2);
						
						//Kepp a List of all Libraries
						final List<Library> libraries = new ArrayList<>(total);
						
						// Load all the libraries
						while (resultSet.next()) {
							libraries.add(new Library(resultSet.getString("NAME"), resultSet.getString("TABLENAME"), resultSet.getDouble("STARS"),
									resultSet.getString("DATECREATED"), resultSet.getString("TIMECREATED"), resultSet.getString("DESCRIPTION"), resultSet.getInt("SAVEMODE"),
									resultSet.getInt("POSITION"), resultSet.getString("LIBRARYIMAGE"), resultSet.getBoolean("OPENED")));
							
							updateProgress(resultSet.getRow() - 1, total);
						}
						
						//Add all the Libraries to the Library Viewer
						Platform.runLater(() -> Main.libraryMode.teamViewer.getViewer().addMultipleItems(libraries));
						
						//Load the Opened Libraries
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("Loading Opened Libraries..."));
						keyValueDb.loadOpenedLibraries();
						
						//Calculate opened libraries
						Platform.runLater(() -> Main.libraryMode.calculateOpenedLibraries());
						
						//Load PlayerMediaList
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("Loading previous data..."));
						Main.playedSongs.uploadFromDataBase();
						
						//Refresh all the XPlayers PlayLists
						Platform.runLater(() -> Main.xPlayersList.getList().stream()
								.forEach(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController().getLoadService().startService(false, false)));
						
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
	
}
