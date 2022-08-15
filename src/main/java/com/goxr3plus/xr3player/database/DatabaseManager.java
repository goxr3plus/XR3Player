/*
 * 
 */
package com.goxr3plus.xr3player.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Stream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.application.MainExit;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.Operation;
import com.goxr3plus.xr3player.controllers.librarymode.Library;
import com.goxr3plus.xr3player.controllers.loginmode.User;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsLoader;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.services.smartcontroller.MediaUpdaterService;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * This class is managing the database of the application.
 *
 * @author GOXR3PLUS
 */
public class DatabaseManager {

	/** The connection 1. */
	private Connection connection;

	/** The data loader. */
	private final DataLoader dataLoader = new DataLoader();

	/** This executor does the commit job. */
	private final ExecutorService commitExecutor = Executors.newSingleThreadExecutor();

	/** If true -> The database notifications are shown */
	private static final boolean SHOWNOTIFICATIONS = false;

	// ----------------------

	/**
	 * The KeyValueDb
	 */
	// private JSONDB keyValueDb = new JSONDB(this)

	/**
	 * Here are stored all the settings for the user account
	 */
	private PropertiesDb userSettingsDb;

	// -------------------------

	/** The runnable of the commit executor. */
	private final Runnable commitRunnable = () -> {
		try {
			connection.commit();
		} catch (final SQLException ex) {
			Main.logger.log(Level.WARNING, ex.getMessage(), ex);
		} finally {
			if (SHOWNOTIFICATIONS)
				AlertTool.showNotification("Commited", "Changes saved successfully", Duration.millis(150),
						NotificationType.INFORMATION);
		}

	};

	/** The runnable of the commit executor. */
	private final Runnable vacuumRunnable = () -> {
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
			MainExit.terminateXR3Player(0);
		} catch (final SQLException ex) {
			Main.logger.log(Level.WARNING, ex.getMessage(), ex);
			MainExit.terminateXR3Player(-1);
		} finally {
			MainExit.terminateXR3Player(0);
		}

	};

	// -----------------------------------------------------------

	/**
	 * 
	 *
	 * @param userName the user name
	 */
	public void initialize(final String userName) {

		// Initialize
		DatabaseTool.setUserName(userName);

		// Create the settingsFolder
		final File settingsFolder = new File(DatabaseTool.getUserFolderAbsolutePathWithSeparator() + "settings");
		IOAction.createFileOrFolder(settingsFolder, FileType.DIRECTORY);

		// Create the propertiesDb
		userSettingsDb = new PropertiesDb(settingsFolder + File.separator + DatabaseTool.USER_SETTINGS_FILE_NAME, true);

		// User Folder
		final File userFolder = new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator() + userName);
		IOAction.createFileOrFolder(userFolder, FileType.DIRECTORY);

		// Images Folder
		final File imagesFolder = new File(DatabaseTool.getImagesFolderAbsolutePathPlain());
		IOAction.createFileOrFolder(imagesFolder, FileType.DIRECTORY);

		// XPlayer Images Folder
		final File xPlayerImagesFolder = new File(DatabaseTool.getXPlayersImageFolderAbsolutePathPlain());
		IOAction.createFileOrFolder(xPlayerImagesFolder, FileType.DIRECTORY);

		// Attempt DataBase connection
		try {
			final String dbFileAbsolutePath = DatabaseTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db";
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
			connection.setAutoCommit(false);

			// This method keeps backward compatibility with previous XR3Player versions
			createDatabaseMissingTables();

		} catch (final SQLException ex) {
			Main.logger.log(Level.SEVERE, "", ex);
		}

	}

	// ------------------- Getters
	// ---------------------------------------------------------------

	/**
	 * @return the propertiesDb
	 */
	public PropertiesDb getPropertiesDb() {
		return userSettingsDb;
	}

	/**
	 * @return the showNotifications
	 */
	public boolean isShowNotifications() {
		return SHOWNOTIFICATIONS;
	}

	public Connection getConnection() {
		manageConnection(Operation.OPEN);
		return connection;
	}

	// ------------------- Setters
	// --------------------------------------------------------------

	// ------------------- Methods
	// --------------------------------------------------------------

	/**
	 * Open or close the connection.
	 *
	 * @param action the action
	 */
	public void manageConnection(final Operation action) {
		try {
			// OPEN
			if (action == Operation.OPEN && connection.isClosed()) {
				connection = DriverManager.getConnection(
						"jdbc:sqlite:" + DatabaseTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db");
				connection.setAutoCommit(false);
				// CLOSE
			} else if (action == Operation.CLOSE && connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (final SQLException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
	}

	/**
	 * Using this methods to control commits across the application so not to have
	 * unexpected lags.
	 */
	public void commit() {
		commitExecutor.execute(commitRunnable);
	}

	/**
	 * Using this methods to control commit + vacuum across the application so not
	 * to have unexpected lags.
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
	 * @param tableName the table name
	 * @return true, if successful
	 */
	public boolean doesTableExist(final String tableName) {
		// SQLite table names are case insensitive, but comparison is case sensitive by
		// default.
		// To make this work properly in all cases you need to add COLLATE NOCASE
		try (ResultSet r = connection.createStatement().executeQuery(
				"SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "' COLLATE NOCASE ")) {
			final int total = r.getInt(1);
			return total != 0;
		} catch (final SQLException ex) {
			Main.logger.log(Level.INFO, "", ex);
		}

		return false;
	}

	/**
	 * Recreates the database if it doesn't exist.
	 */
	public void createDatabaseMissingTables() {

		Main.logger.info("Creating (if any) missing database tables...");

		try (Statement statement = connection.createStatement()) {

			// ----------Libraries Table ----------------//
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS LIBRARIES (NAME          TEXT    PRIMARY KEY   NOT NULL,"
							+ "TABLENAME TEXT NOT NULL," + "STARS         DOUBLE     NOT NULL,"
							+ "DATECREATED          TEXT   	NOT NULL," + "TIMECREATED          TEXT    NOT NULL,"
							+ "DESCRIPTION   TEXT    NOT NULL," + "SAVEMODE      INT     NOT NULL,"
							+ "POSITION      INT     NOT NULL," + "LIBRARYIMAGE  TEXT," + "OPENED BOOLEAN NOT NULL )");

			// ----------XPlayers PlayLists Tables ----------//
			for (int i = 0; i < 3; i++)
				createXPlayListTable(statement, i);

			commit();
		} catch (final SQLException ex) {
			Main.logger.log(Level.SEVERE, "", ex);
		}
	}

	/**
	 * Create a database table for the specific XPlayer.
	 *
	 * @param statement the statement
	 * @param key       the key
	 * @throws SQLException the SQL exception
	 */
	private void createXPlayListTable(final Statement statement, final int key) throws SQLException {
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS `XPPL" + key
				+ "` (PATH TEXT PRIMARY KEY  NOT NULL , STARS  DOUBLE  NOT NULL , TIMESPLAYED  INT NOT NULL , DATE TEXT NOT NULL , HOUR TEXT NOT NULL);");
	}

	/**
	 * This method loads the application database.
	 */
	public void loadApplicationDataBase() {
		Main.updateScreen.setVisible(true);
		Main.updateScreen.getProgressBar().progressProperty().bind(dataLoader.progressProperty());
		dataLoader.restart();
	}

	// -------------------------------------------------------------------
	public Optional<User> getOpenedUser() {
		return Main.loginMode.viewer.getItemsObservableList().stream()
				.filter(user -> ((User) user).getName().equals(DatabaseTool.getUserName())).map(user -> (User) user)
				.findFirst();
	}

	/**
	 * DataLoader.
	 *
	 * @author GOXR3PLUS STUDIO
	 */
	public class DataLoader extends Service<Void> {

		/** The total. */
		private int totalLibraries;

		/**
		 * Constructor.
		 */
		public DataLoader() {

			// -------------------if succeeded
			setOnSucceeded(s -> {

				// ----------------Do the animation with rectangles---------------------
				Main.updateScreen.closeUpdateScreen();

				// ----------------Final Settings---------------------
				// update library viewer
				Main.libraryMode.viewer.update();

				// ---------------Set the update Screen invisible---------------------
				final PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
				pause1.setOnFinished(f -> {
					Main.updateScreen.setVisible(false);
					Main.updateScreen.getProgressBar().progressProperty().unbind();
				});
				pause1.playFromStart();

				// Start important services
				new MediaUpdaterService().start();
//				Main.webBrowser.startChromiumUpdaterService();

			});

			// ---------------------if failed
			setOnFailed(fail -> {
				Main.updateScreen.getProgressBar().progressProperty().unbind();
				AlertTool.showNotification("Fatal Error!",
						"DataLoader failed during loading dataBase!!Application will exit...", Duration.millis(1500),
						NotificationType.ERROR);
				MainExit.terminateXR3Player(0);
			});
		}

		@Override
		protected Task<Void> createTask() {
			return new Task<>() {
				@Override
				protected Void call() throws Exception {
					// int totalSteps = 4

					// -------------------------- Load all the libraries -------------------------------------------------
					try (ResultSet resultSet = getConnection().createStatement()
							.executeQuery("SELECT* FROM LIBRARIES;");
						 ResultSet dbCounter = getConnection().createStatement()
								 .executeQuery("SELECT COUNT(NAME) FROM LIBRARIES;");) {

						totalLibraries = dbCounter.getInt(1);

						// Refresh the text
						Platform.runLater(() -> Main.updateScreen.getLabel()
								.setText("Loading Libraries....[ 0 / " + totalLibraries + " ]"));

						// Kepp a List of all Libraries
						final List<Node> libraries = new ArrayList<>(totalLibraries);
						final int[] counter = {0};

						// Load all the libraries
						while (resultSet.next()) {

							// Create a CountDown Latch
							final CountDownLatch countDown = new CountDownLatch(1);

							// Run on JavaFX Thread
							Platform.runLater(() -> {

								// Add the library to the List of Libraries
								try {
									libraries.add(new Library(resultSet.getString("NAME"),
											resultSet.getString("TABLENAME"), resultSet.getDouble("STARS"),
											resultSet.getString("DATECREATED"), resultSet.getString("TIMECREATED"),
											resultSet.getString("DESCRIPTION"), resultSet.getInt("SAVEMODE"),
											resultSet.getInt("POSITION"), resultSet.getString("LIBRARYIMAGE"),
											resultSet.getBoolean("OPENED")));
								} catch (final SQLException e) {
									e.printStackTrace();
								}

								// Countdown
								countDown.countDown();
							});

							// Await for the CountDown
							countDown.await();

							// Change Label Text
							++counter[0];
							Platform.runLater(() -> Main.updateScreen.getLabel()
									.setText("Loading Libraries... [ " + counter[0] + " / " + totalLibraries + " ]"));

						}

						// Update the Progress
						// updateProgress(1, totalSteps)

						// Run of JavaFX Thread
						Platform.runLater(() -> {

							// Change Label Text
							Main.updateScreen.getLabel().setText("Adding Libraries ...");

							// Add all the Libraries to the Library Viewer
							Main.libraryMode.viewer.addMultipleItems(libraries);

						});

						// Load PlayerMediaList
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("Loading Emotion Lists..."));
						Main.playedSongs.uploadFromDataBase();
						Main.starredMediaList.uploadFromDataBase();
						Main.emotionListsController.hatedMediaList.uploadFromDataBase();
						Main.emotionListsController.dislikedMediaList.uploadFromDataBase();
						Main.emotionListsController.likedMediaList.uploadFromDataBase();
						Main.emotionListsController.lovedMediaList.uploadFromDataBase();

						// -----------------------Load the application
						// settings-------------------------------
						final CountDownLatch countDown1 = new CountDownLatch(1);
						Platform.runLater(() -> {
							// Change label text
							Main.updateScreen.getLabel().setText("Loading Settings...");

							// Load application settings
							ApplicationSettingsLoader.loadApplicationSettings();

							// Change Label Text
							Main.updateScreen.getLabel().setText("Loading Opened Libraries...");

							// Count down
							countDown1.countDown();

						});

						// Wait for the application settings to be loaded
						countDown1.await();

						// Load the Opened Libraries
						Main.libraryMode.loadOpenedLibraries();

						// Run of JavaFX Thread
						Platform.runLater(() -> {

							// Calculate Opened Libraries
							Main.libraryMode.calculateOpenedLibraries();

							// Calculate Empty Libraries
							Main.libraryMode.calculateEmptyLibraries();

						});

						//
						Platform.runLater(() -> {

							// Change Label Text
							Platform.runLater(() -> Main.updateScreen.getLabel().setText("Loading Bindings..."));

							// Update the emotion smart controller
							Main.emotionListsController
									.updateSelectedSmartController(new boolean[]{true, true, true, true});

							// Update the selected smart controller
							((SmartController) Main.emotionsTabPane.getTabPane().getSelectionModel().getSelectedItem()
									.getContent()).getLoadService().startService(false, false, true);

							// Refresh all the XPlayers PlayLists
							Main.xPlayersList.getList().stream()
									.forEach(xPlayerController -> xPlayerController.getxPlayerPlayList()
											.getSmartController().getLoadService().startService(false, false, false));

							// ------Bind Instant Search between all SmartControllers

							// For Search Window
							Main.searchWindowSmartController.getInstantSearch().selectedProperty()
									.bindBidirectional(Main.settingsWindow.getPlayListsSettingsController()
											.getInstantSearch().selectedProperty());

							// For Libraries
							Main.libraryMode.viewer.getItemsObservableList().stream()
									.map(library -> ((Library) library).getSmartController())
									.forEach(controller -> controller.getInstantSearch().selectedProperty()
											.bindBidirectional(Main.settingsWindow.getPlayListsSettingsController()
													.getInstantSearch().selectedProperty()));

							Stream<XPlayerController> cs1;
							cs1 = Main.xPlayersList.getList().stream();
							cs1.forEach(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()
									.getInstantSearch().selectedProperty().bindBidirectional(Main.settingsWindow
											.getPlayListsSettingsController().getInstantSearch().selectedProperty()));

							// Load Saved DropBox Accounts
							Main.dropBoxViewer.refreshSavedAccounts();

							// Resize Main Window
							Main.window.setWidth(Main.window.getWidth() + 1);
							Main.window.setHeight(Main.window.getHeight() + 1);
						});

						// Update the Progress
						// updateProgress(4, 4)

					} catch (final Exception param) {
						Main.logger.log(Level.SEVERE, "", param);
					}

					return null;
				}
			};
		}

	}

}
