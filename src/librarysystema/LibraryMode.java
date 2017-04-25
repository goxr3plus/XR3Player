package librarysystema;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Level;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import application.settings.window.ApplicationSettingsController.SettingsTab;
import database.LocalDBManager;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import xplayer.presenter.XPlayerController;

/**
 * This class contains everything needed going on LibraryMode.
 *
 * @author SuperGoliath
 */
public class LibraryMode extends GridPane {

    @FXML
    private GridPane root;

    @FXML
    private BorderPane borderPane;

    @FXML
    private StackPane librariesStackView;

    @FXML
    private Button newLibrary;

    @FXML
    private GridPane topGrid;

    @FXML
    private JFXToggleButton selectionModeToggle;

    @FXML
    private JFXButton previous;

    @FXML
    private JFXButton createLibrary;

    @FXML
    private JFXButton next;

    @FXML
    private JFXButton showSettings;

    @FXML
    private ToolBar libraryToolBar;

    @FXML
    private Button deleteLibrary;

    @FXML
    private Button renameLibrary;

    @FXML
    private Button openOrCloseLibrary;

    @FXML
    private HBox botttomHBox;

    @FXML
    private Label librariesInfoLabel;

    // ------------------------------------------------

    // protected boolean dragDetected

    /**
     * The mechanism which allows you to transport items between libraries and more.
     */
    public final LibrariesSearchBox librariesSearcher = new LibrariesSearchBox();

    /**
     * The mechanism which allows you to view the libraries as components with image etc.
     */
    public final TeamViewer teamViewer = new TeamViewer(this);

    /** The mechanism behind of opening multiple libraries. */
    public final MultipleLibraries multipleLibs = new MultipleLibraries();

    //--------Images ------------------------------

    /**
     * Default image of a library(which has not a costume one selected by the user.
     */
    public static Image defaultImage;//= InfoTool.getImageFromDocuments("visualizer.jpg");
    /**
     * A classic warning image to inform the user about something
     * 
     */
    public static final Image warningImage = InfoTool.getImageFromDocuments("warning.png");

    //----- Library Specific ------------------

    /** The settings. */
    public LibrarySettings settings = new LibrarySettings();

    /** The context menu. */
    public LibraryContextMenu contextMenu = new LibraryContextMenu();

    //----- Invalidation Listeners ------------------

    /** This variable is used during the creation of a new library. */
    private final InvalidationListener creationInvalidator = new InvalidationListener() {
	@Override
	public void invalidated(Observable observable) {

	    // Remove the Listener
	    Main.renameWindow.showingProperty().removeListener(this);

	    // !Showing && !XPressed
	    if (!Main.renameWindow.isShowing() && Main.renameWindow.wasAccepted()) {

		Main.window.requestFocus();

		// Check if this name already exists
		String name = Main.renameWindow.getUserInput();

		// if can pass
		if (!teamViewer.getViewer().getItemsObservableList().stream().anyMatch(lib -> lib.getLibraryName().equals(name))) {
		    String tableName;
		    boolean validName;

		    // Until the randomName doesn't already exists
		    do {
			tableName = ActionTool.returnRandomTableName();
			validName = !LocalDBManager.tableExists(tableName);
		    } while (!validName);
		    final String dataBaseTableName = tableName; //add it to a final variable

		    //Ok Now Go
		    try (PreparedStatement insertNewLibrary = Main.dbManager.connection1.prepareStatement(
			    "INSERT INTO LIBRARIES (NAME,TABLENAME,STARS,DATECREATED,TIMECREATED,DESCRIPTION,SAVEMODE,POSITION,LIBRARYIMAGE,OPENED) "
				    + "VALUES (?,?,?,?,?,?,?,?,?,?)");
			    Statement statement = Main.dbManager.connection1.createStatement()) {

			// Create the dataBase table
			statement.executeUpdate("CREATE TABLE '" + dataBaseTableName + "'" + "(PATH       TEXT    PRIMARY KEY   NOT NULL ,"
				+ "STARS       DOUBLE     NOT NULL," + "TIMESPLAYED  INT     NOT NULL," + "DATE        TEXT   	NOT NULL,"
				+ "HOUR        TEXT    NOT NULL)");

			// Create the Library
			Library currentLib = new Library(name, dataBaseTableName, 0, null, null, null, 1,
				teamViewer.getViewer().getItemsObservableList().size(), null, false);

			// Add the library
			currentLib.goOnSelectionMode(selectionModeToggle.isSelected());
			teamViewer.getViewer().addLibrary(currentLib, true);

			// Add a row on libraries table
			insertNewLibrary.setString(1, name);
			insertNewLibrary.setString(2, dataBaseTableName);
			insertNewLibrary.setDouble(3, currentLib.starsProperty().get());
			insertNewLibrary.setString(4, currentLib.getDateCreated());
			insertNewLibrary.setString(5, currentLib.getTimeCreated());
			insertNewLibrary.setString(6, currentLib.getDescription());
			insertNewLibrary.setInt(7, 1);
			insertNewLibrary.setInt(8, currentLib.getPosition());
			insertNewLibrary.setString(9, null);
			insertNewLibrary.setBoolean(10, false);

			insertNewLibrary.executeUpdate();

			// Commit
			Main.dbManager.commit();
		    } catch (Exception ex) {
			Main.logger.log(Level.WARNING, "", ex);
			ActionTool.showNotification("Error Creating a Library", "Library can't be created cause of:" + ex.getMessage(),
				Duration.seconds(2), NotificationType.WARNING);
		    }
		} else {
		    ActionTool.showNotification("Dublicate Name", "A Library or PlayList with this name already exists!", Duration.seconds(2),
			    NotificationType.INFORMATION);
		}
	    }
	}
    };

    /**
     * Constructor.
     */
    public LibraryMode() {

	// FXMLLOADER
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "LibraryMode.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

    }

    /**
     * Return the library with the given name.
     *
     * @param name
     *            the name
     * @return the library with name
     */
    public Library getLibraryWithName(String name) {

	// Find that
	for (Library library : teamViewer.getViewer().getItemsObservableList())
	    if (library.getLibraryName().equals(name))
		return library;

	return null;
    }

    /**
     * Update Settings Total Library only if this Library exists and it is on settings mode
     * 
     * @param name
     */
    public void updateLibraryTotalLabel(String name) {
	Library lib = getLibraryWithName(name);
	if (lib != null)
	    lib.updateSettingsTotalLabel();
    }

    /**
     * Called as soon as FXML file has been loaded
     */
    @FXML
    public void initialize() {

	// createLibrary
	createLibrary.setOnAction(a -> createNewLibrary(createLibrary));

	// newLibrary
	newLibrary.setOnAction(a -> createNewLibrary(newLibrary));
	newLibrary.visibleProperty().bind(Bindings.size(teamViewer.getViewer().getItemsObservableList()).isEqualTo(0));

	// selectionModeToggle
	selectionModeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> teamViewer.getViewer().goOnSelectionMode(newValue));

	// searchLibrary
	//topGrid.add(librariesSearcher, 1, 0)
	botttomHBox.getChildren().add(librariesSearcher);

	// previous
	previous.setOnAction(a -> teamViewer.getViewer().previous());

	// next
	next.setOnAction(a -> teamViewer.getViewer().next());

	//showSettings
	showSettings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.LIBRARIES));

	// StackPane
	librariesStackView.getChildren().addAll(teamViewer.getViewer(), librariesSearcher.region, librariesSearcher.searchProgress);
	teamViewer.getViewer().toBack();

	// XPlayer - 0
	Main.xPlayersList.addXPlayerController(new XPlayerController(0));
	Main.xPlayersList.getXPlayerController(0).makeTheDisc(150, 150, Color.FIREBRICK, 45, Side.LEFT);
	Main.xPlayersList.getXPlayerController(0).makeTheVisualizer(Side.RIGHT);
	add(Main.xPlayersList.getXPlayerController(0), 1, 1);

	//Let'z GO

	// -- libraryToolBar
	libraryToolBar.disableProperty().bind(teamViewer.getViewer().centerItemProperty().isNull());

	// -- renameLibrary
	renameLibrary.setOnAction(a -> teamViewer.getViewer().centerItemProperty().get().renameLibrary(renameLibrary));

	// -- deleteLibrary
	deleteLibrary.setOnAction(a -> teamViewer.getViewer().centerItemProperty().get().deleteLibrary());

	// -- openOrCloseLibrary 
	teamViewer.getViewer().centerItemProperty().addListener((observable, oldValue, newValue) -> {
	    if (newValue != null)
		openOrCloseLibrary.textProperty()
			.bind(Bindings.when(teamViewer.getViewer().centerItemProperty().get().openedProperty()).then("Close").otherwise("Open"));
	    else {
		openOrCloseLibrary.textProperty().unbind();
		openOrCloseLibrary.setText("...");
	    }
	});
	openOrCloseLibrary.setOnAction(a -> teamViewer.getViewer().centerItemProperty().get()
		.libraryOpenClose(!teamViewer.getViewer().centerItemProperty().get().isOpened(), false));

	//----librariesInfoLabel
	librariesInfoLabel.textProperty().bind(Bindings.concat("[ ", teamViewer.getViewer().itemsWrapperProperty().sizeProperty(), " ] Libraries"));
    }

    /**
     * Used to create a new Library
     * 
     * @param owner
     */
    public void createNewLibrary(Node owner) {
	if (Main.renameWindow.isShowing())
	    return;

	// Open rename window
	Main.renameWindow.show("", owner);

	// Add the showing listener
	Main.renameWindow.showingProperty().addListener(creationInvalidator);
    }

    /**
     * Gets the previous.
     *
     * @return the previous
     */
    public Button getPrevious() {
	return previous;
    }

    /**
     * Gets the next.
     *
     * @return the next
     */
    public Button getNext() {
	return next;
    }

}
