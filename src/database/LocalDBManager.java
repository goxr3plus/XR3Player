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
import librarymode.Library;
import smartcontroller.Operation;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

// TODO: Auto-generated Javadoc
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

    /** The db file. */
    // -Constructor-
    private String dbFile;

    /** The images folder. */
    public String imagesFolder;

    /** This executor does the commit job. */
    private static final ExecutorService commitExecutor = Executors.newSingleThreadExecutor();

    /** The runnable of the commit executor. */
    private Runnable commitRunnable = () -> {
        try {
            connection1.commit();
        } catch (SQLException ex) {
            Main.logger.log(Level.WARNING, ex.getMessage(), ex);
        } finally {
            Platform.runLater(Notifications.create()
                .text("Successfully saved changes.")
                .hideAfter(Duration.millis(800))::show);
        }

    };

    /**
     * Constructor.
     *
     * @param userName the user name
     */
    public LocalDBManager(String userName) {

        try {
            Class.forName("org.sqlite.JDBC");

            // database folder
            if (!new File(InfoTool.dbPath_Plain).exists())
                new File(InfoTool.dbPath_Plain).mkdir();

            // user folder
            if (!new File(InfoTool.dbPath_With_Separator + userName).exists())
                new File(InfoTool.dbPath_With_Separator + userName).mkdir();
            InfoTool.user_dbPath_With_Separator = InfoTool.dbPath_With_Separator + userName + File.separator;

            // images folder
            imagesFolder = InfoTool.dbPath_With_Separator + userName + File.separator + "Images";
            if (!new File(imagesFolder).exists())
                new File(imagesFolder).mkdir();

            // database file(.db)
            dbFile = InfoTool.dbPath_With_Separator + userName + File.separator + "dbFile.db";
            boolean data1Exist = new File(dbFile).exists();

            // connection1
            connection1 = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            connection1.setAutoCommit(false);

            if (!data1Exist)
                recreateDataBase();

        } catch (SQLException | ClassNotFoundException ex) {
            Main.logger.log(Level.SEVERE, "", ex);
        }
    }

    /**
     * Open or close the connection.
     *
     * @param action the action
     */
    public void manageConnection(Operation action) {
        try {
            // OPEN
            if (action == Operation.OPEN && connection1.isClosed())
                connection1 = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
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
     * Stops the executorService.
     */
    public void shutdownCommitExecutor() {
        commitExecutor.shutdown();
    }

    /**
     * Checks if this table exists in dataBase.
     *
     * @param tableName the table name
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
            statement.executeUpdate("CREATE TABLE LIBRARIES(NAME          TEXT    PRIMARY KEY   NOT NULL," + "TABLENAME TEXT NOT NULL," + "STARS         DOUBLE     NOT NULL," + "DATECREATED          TEXT   	NOT NULL,"
                + "TIMECREATED          TEXT    NOT NULL," + "DESCRIPTION   TEXT    NOT NULL," + "SAVEMODE      INT     NOT NULL," + "POSITION      INT     NOT NULL," + "LIBRARYIMAGE  TEXT," + "OPENED BOOLEAN NOT NULL )");

            // -----------Radio Stations Table ------------//
            statement.executeUpdate("CREATE TABLE '" + InfoTool.radioStationsTable + "'(NAME TEXT PRIMARY KEY NOT NULL," + "STREAMURL TEXT NOT NULL," + "TAGS TEXT NOT NULL," + "DESCRIPTION TEXT," + "STARS DOUBLE NOT NULL)");

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
     * @param statement the statement
     * @param key the key
     * @throws SQLException the SQL exception
     */
    private void createXPlayListTable(Statement statement, int key) throws SQLException {
        statement.executeUpdate("CREATE TABLE XPPL" + key + "(PATH       TEXT    PRIMARY KEY   NOT NULL ," + "STARS       DOUBLE     NOT NULL," + "TIMESPLAYED  INT     NOT NULL," + "DATE        TEXT   	NOT NULL," + "HOUR        TEXT    NOT NULL)");
    }

    /**
     * This method loads the application database.
     */
    public void loadApplicationDataBase() {
        Main.updateScreen.setVisible(true);
        Main.updateScreen.progressBar.progressProperty()
            .bind(dataLoader.progressProperty());
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
                Main.updateScreen.progressBar.progressProperty()
                    .unbind();
                Main.root.setCenter(Main.libraryMode);
                Main.libraryMode.add(Main.multipleTabs, 0, 1);
                Main.updateScreen.setVisible(false);

                // -------Due to a bug i need the width%2==0---------
                int width = (int) (InfoTool.getVisualScreenWidth() * 0.77);
                width = (width % 2 == 0) ? width : width + 1;
                // -------------------
                Main.window.setWidth(width);
                Main.window.setHeight(InfoTool.getVisualScreenHeight() * 0.91);
                Main.window.centerOnScreen();

                // Check for updates on start
                new Thread(() -> {
                    if (InfoTool.isReachableByPing("www.google.com"))
                        Main.checkForUpdates(false);
                }).start();
            });

            // if failed
            setOnFailed(fail -> {
                Main.updateScreen.progressBar.progressProperty()
                    .unbind();
                ActionTool.showNotification("Fatal Error!", "DataLoader failed during loading dataBase!!Application will exit...", Duration.millis(1500), NotificationType.ERROR);
                System.exit(0);
            });
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    // -------------------------- Load all the libraries
                    try (ResultSet resultSet = connection1.createStatement()
                        .executeQuery("SELECT* FROM LIBRARIES;");
                        ResultSet dbCounter = connection1.createStatement()
                            .executeQuery("SELECT COUNT(*) FROM LIBRARIES;");) {

                        total = dbCounter.getInt(1);
                        Main.logger.info("Uploading libraries....");

                        // Refresh the text
                        Platform.runLater(() -> Main.updateScreen.label.setText("Uploading Libraries..."));
                        updateProgress(1, 2);

                        // Load all the libraries
                        while (resultSet.next()) {

                            Library library = new Library(resultSet.getString("NAME"), resultSet.getString("TABLENAME"), resultSet.getDouble("STARS"), resultSet.getString("DATECREATED"), resultSet.getString("TIMECREATED"), resultSet.getString("DESCRIPTION"),
                                resultSet.getInt("SAVEMODE"), resultSet.getInt("POSITION"), resultSet.getString("LIBRARYIMAGE"), resultSet.getBoolean("OPENED"));

                            Main.libraryMode.libraryViewer.addLibrary(library);

                            // opened?
                            if (resultSet.getBoolean("OPENED"))
                                library.libraryOpenClose(true, true);

                            updateProgress(resultSet.getRow(), total);
                        }

                        Main.libraryMode.libraryViewer.getItems()
                            .stream()
                            .filter(Library::isLibraryOpened)
                            .findFirst()
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
