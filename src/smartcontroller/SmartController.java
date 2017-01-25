/*
 * 
 */
package smartcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.controlsfx.control.Notifications;
import org.fxmisc.easybind.EasyBind;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import media.Audio;
import media.Media;
import smartcontroller.Genre.TYPE;
import tools.ActionTool;
import tools.InfoTool;

/**
 * Used to control big amounts of data in user interface.
 *
 * @author GOXR3PLUS
 */
public class SmartController extends StackPane {

    /** The main border. */
    @FXML
    private BorderPane mainBorder;

    /** The titled pane. */
    @FXML
    private TitledPane titledPane;

    /** The top grid. */
    @FXML
    private GridPane topGrid;

    /** The page field. */
    @FXML
    private TextField pageField;

    /** The settings button. */
    @FXML
    private MenuButton settingsButton;

    /** The total items shown. */
    @FXML
    private Menu totalItemsShown;

    /** The yolo. */
    @FXML
    private ToggleGroup yolo;

    /** The import files. */
    @FXML
    private MenuItem importFiles;

    /** The export files. */
    @FXML
    private MenuItem exportFiles;

    /** The clear all. */
    @FXML
    private MenuItem clearAll;

    /**
     * If the search is instant or needs the user to press enter on the search
     * field
     */
    @FXML
    public JFXCheckBox instantSearch;

    /** The center stack pane. */
    @FXML
    private StackPane centerStackPane;

    /** The previous. */
    @FXML
    private Button previous;

    /** The next. */
    @FXML
    private Button next;

    /** The region. */
    @FXML
    private Region region;

    /** The indicator. */
    @FXML
    private ProgressIndicator indicator;

    /** The cancel. */
    @FXML
    private Button cancel;

    @FXML
    private HBox searchBarHBox;

    // ----------------------------------------------------------

    /** The observable list. */
    // Keeping all the items of controller
    public ObservableList<Media> observableList = FXCollections.observableArrayList();

    /** The total in data base. */
    private IntegerProperty totalInDataBase = new SimpleIntegerProperty(0);

    /** The maximum per list. */
    int maximumPerList = 50;

    /** The list number. */
    public IntegerProperty listNumber = new SimpleIntegerProperty(0);

    /** The data base table name. */
    private final String dataBaseTableName;

    /** The name. */
    private String smartName = null;

    /** The search service. */
    public final SmartSearcher searchService;

    /** The load service. */
    public final LoadService loadService;

    /** The input service. */
    public final InputService inputService;

    /**
     * CopyOrMoveService
     */
    public CopyOrMoveService copyOrMoveService;

    /** The table viewer. */
    public final MediaTableViewer tableViewer;

    /** The genre. */
    public final Genre genre;

    /** The deposit working. */
    public volatile boolean depositWorking;

    /** The un deposit working. */
    public volatile boolean undepositWorking;

    /** The rename working. */
    public volatile boolean renameWorking;

    /** The update working. */
    public volatile boolean updateWorking;

    /** The prepared insert. */
    public PreparedStatement preparedInsert;

    /** The prepared delete. */
    public PreparedStatement preparedDelete;

    /** The prepared rename. */
    public PreparedStatement preparedRename;

    /** The prepared U stars. */
    public PreparedStatement preparedUStars;

    /** The prepared U times played. */
    public PreparedStatement preparedUTimesPlayed;

    /** The prepared count elements with string. */
    public PreparedStatement preparedCountElementsWithString;

    /**
     * Instantiates a new smart controller.
     *
     * @param genre
     *            the genre
     * @param name
     *            the name
     * @param dataBaseTableName
     *            the data base table name
     */
    public SmartController(Genre genre, String name, String dataBaseTableName) {
	this.genre = genre;
	this.smartName = name;
	this.dataBaseTableName = dataBaseTableName;

	// Initialize
	tableViewer = new MediaTableViewer();
	searchService = new SmartSearcher(this);
	loadService = new LoadService();
	inputService = new InputService();
	copyOrMoveService = new CopyOrMoveService();

	// FXMLLoader
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "SmartController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}

	// Initialize the prepared statements
	if (genre != Genre.RADIOSTATION)
	    prepareStatements();
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 							Initialize Method
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Called as soon as FXML file has been loaded
     */
    @FXML
    private void initialize() {

	// centerStackPane
	centerStackPane.setOnKeyReleased(key -> {
	    KeyCode code = key.getCode();

	    if (code == KeyCode.LEFT) {
		goPrevious();
	    } else if (code == KeyCode.RIGHT) {
		goNext();
	    } else if (tableViewer.getSelectedCount() > 0) { // TableViewer

		if (code == KeyCode.R)
		    tableViewer.getSelectionModel().getSelectedItem().rename(SmartController.this);
		else if (code == KeyCode.S)
		    tableViewer.getSelectionModel().getSelectedItem().updateStars(SmartController.this);
		else if (code == KeyCode.P)
		    ActionTool.openFileLocation(tableViewer.getSelectionModel().getSelectedItem().getFilePath());
		else if (code == KeyCode.DELETE)
		    tableViewer.getSelectionModel().getSelectedItem().prepareDelete(key.isShiftDown(),
			    SmartController.this);

	    }

	});

	// TableViewer
	tableViewer.setItems(observableList);
	tableViewer.setPlaceholder(new Label("Import or Drag Media Here ;) "));
	tableViewer.setOnMouseReleased(m -> {
	    if (m.getButton() == MouseButton.SECONDARY && !tableViewer.getSelectionModel().getSelectedItems().isEmpty())
		Main.songsContextMenu.showContextMenu(tableViewer.getSelectionModel().getSelectedItem(), genre,
			m.getScreenX(), m.getScreenY(), this);
	});

	/*
	 * ObjectProperty<TableRow<Media>> lastSelectedRow = new
	 * SimpleObjectProperty<>(); tableViewer.setRowFactory(tableView -> {
	 * TableRow<Media> row = new TableRow<>();
	 * row.selectedProperty().addListener((obs, wasSelected, isNowSelected)
	 * -> { if (isNowSelected) lastSelectedRow.set(row); }); return row; });
	 */

	centerStackPane.getChildren().add(tableViewer);
	tableViewer.toBack();

	// Region
	region.setStyle("-fx-background-color:rgb(0,0,0,0.7)");
	region.setVisible(false);

	// Indicator
	indicator.visibleProperty().bind(region.visibleProperty());

	// Cancel
	cancel.setStyle(
		"-fx-background-color:rgb(0,0,0,0.7); -fx-background-radius:20; -fx-text-fill:rgb(40,140,255); -fx-font-size:18px; -fx-font-weight:bold;");
	cancel.visibleProperty().bind(region.visibleProperty());
	cancel.setDisable(true);

	// searchBarHBox
	searchBarHBox.getChildren().add(0, searchService);

	// Previous Button
	previous.disableProperty().bind(next.disabledProperty());
	previous.visibleProperty().bind(listNumber.isNotEqualTo(0));
	previous.setOnAction(a -> goPrevious());

	// Next Button
	next.setVisible(false);
	next.setOnAction(a -> goNext());

	// PageField
	pageField.disableProperty().bind(next.disabledProperty());
	pageField.textProperty().addListener((observable, oldValue, newValue) -> {

	    if (!newValue.matches("\\d"))
		pageField.setText(newValue.replaceAll("\\D", ""));

	    if (!pageField.getText().isEmpty()) {
		// System.out.println("Setting")
		int maximumPage = maximumList();
		// System.out.println("maximum Page:"+maximumPage)
		if (Integer.parseInt(pageField.getText()) > maximumPage)
		    Platform.runLater(() -> pageField.setText(Integer.toString(maximumPage)));
	    }

	});
	pageField.setOnAction(ac -> {
	    if (!pageField.getText().isEmpty() && !loadService.isRunning() && !searchService.service.isRunning()
		    && totalInDataBase.get() != 0) {
		int lisN = Integer.parseInt(pageField.getText());
		if (lisN <= maximumList()) {
		    listNumber.set(lisN);
		    loadService.startService(false, true);
		} else
		    pageField.setText(Integer.toString(lisN));
	    }

	});

	// totalItemsShown
	for (MenuItem item : totalItemsShown.getItems())
	    item.setOnAction(ac -> {

		// GO
		int current = Integer.parseInt(item.getText());
		if (maximumPerList != current) {
		    listNumber.set((maximumPerList == 50) ? listNumber.get() / 2
			    : listNumber.get() * 2 + (listNumber.get() % 2 == 0 ? 0 : 1));
		    maximumPerList = current;
		    loadService.startService(false, true);
		}

	    });

	// importFiles
	importFiles.setOnAction(a -> {
	    List<File> list = Main.specialChooser.prepareToImportSongFiles(Main.window);
	    if (list != null && !list.isEmpty()) {
		inputService.start(list);
	    }
	});

	// exportFiles
	exportFiles.setOnAction(a -> {
	    Main.exportWindow.show(this);
	});

	// Export
	// ...

	// clearAll
	clearAll.setOnAction(ac -> {
	    if (ActionTool.doQuestion("All the songs from <<" + this + ">> will be cleared\n  Soore?"))
		clearDataBaseTable();
	});

	// Update
	updateLabel();

    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							METHODS
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Prepares the statements for this PlayList.
     */
    private void prepareStatements() {
	// ------------------Prepared Statements
	try {
	    String string = "UPDATE '" + dataBaseTableName + "'";

	    preparedInsert = Main.dbManager.connection1.prepareStatement("INSERT OR IGNORE INTO '" + dataBaseTableName
		    + "' (PATH,STARS,TIMESPLAYED,DATE,HOUR) " + "VALUES (?,?,?,?,?)");

	    preparedRename = Main.dbManager.connection1.prepareStatement(string + " SET PATH=? WHERE PATH=?");

	    preparedDelete = Main.dbManager.connection1
		    .prepareStatement("DELETE FROM '" + dataBaseTableName + "' WHERE PATH=?");

	    preparedUStars = Main.dbManager.connection1.prepareStatement(string + " SET STARS=? WHERE PATH=?");

	    preparedUTimesPlayed = Main.dbManager.connection1
		    .prepareStatement(string + " SET TIMESPLAYED=? WHERE PATH=?");

	    preparedCountElementsWithString = Main.dbManager.connection1
		    .prepareStatement("SELECT COUNT(*) FROM '" + dataBaseTableName + "' WHERE PATH=?");

	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Removes the selected songs.
     *
     * @param permanent
     *            <br>
     *            true->storage medium + (play list)/library false->only from
     *            (play list)/library<br>
     */
    public void removeSelected(boolean permanent) {
	// Free? && How many items are selected?+Question
	if (isFree(true) && tableViewer.getSelectedCount() == 1
		? ActionTool.doDeleteQuestion(permanent,
			tableViewer.getSelectionModel().getSelectedItem().getFileName(), tableViewer.getSelectedCount())
		: ActionTool.doDeleteQuestion(permanent, Integer.toString(tableViewer.getSelectedCount()),
			tableViewer.getSelectedCount())) {

	    // Remove selected items
	    try {
		tableViewer.getSelectionModel().getSelectedItems().iterator()
			.forEachRemaining(r -> r.delete(permanent, false, false, this));

		// Library?
		if (genre == Genre.LIBRARYSONG)
		    Main.libraryMode.updateLibraryTotalLabel(smartName);

	    } catch (Exception ex) {
		Main.logger.log(Level.WARNING, "", ex);
	    }

	}
    }

    /**
     * Clears all the items from this library *.
     */
    private void clearDataBaseTable() {
	if (isFree(true)) {

	    // Security Value
	    undepositWorking = true;

	    // Controller
	    getIndicator().setProgress(-1);
	    getCancelButton().setText("Clearing...");
	    getRegion().setVisible(true);

	    // New Thread
	    new Thread(() -> {
		try {
		    Main.dbManager.connection1.createStatement()
			    .executeUpdate("DELETE FROM '" + dataBaseTableName + "'");
		    Main.dbManager.commit();
		    Platform.runLater(() -> {
			getRegion().setVisible(false);
			getCancelButton().setText("Cancel");
		    });
		} catch (Exception ex) {
		    Main.logger.log(Level.WARNING, "", ex);
		} finally {
		    undepositWorking = false;
		}
	    }).start();

	    simpleClear();

	    // Library?
	    if (genre == Genre.LIBRARYSONG)
		Main.libraryMode.updateLibraryTotalLabel(smartName);
	}
    }

    /**
     * Updates the label of the smart controller.
     */
    public void updateLabel() {
	if (!searchService.isActive()) {
	    titledPane.setText("[Total:<" + totalInDataBase.get() + "> ListNumber:<" + listNumber.get()
		    + "> MaximumList:<" + maximumList() + ">] Selected:" + tableViewer.getSelectedCount() + "  Showing:"
		    + listNumber.get() * maximumPerList + "-"
		    + (listNumber.get() * maximumPerList + observableList.size()));

	} else {
	    titledPane.setText("[Found: <" + observableList.size() + "> first matching] Selected:"
		    + tableViewer.getSelectedCount());

	}

	pageField.setText(Integer.toString(listNumber.get()));
    }

    /**
     * Checks if any updates are on progress in the controller.
     *
     * @param showMessage
     *            the show message
     * @return true->if yes<br>
     *         false->if not
     */
    public boolean isFree(boolean showMessage) {
	boolean isFree = true;
	String message = null;

	if (depositWorking) {
	    isFree = false;
	    message = "Depositing";
	} else if (undepositWorking) {
	    isFree = false;
	    message = "Undepositing";
	} else if (searchService.service.isRunning()) {
	    isFree = false;
	    message = "Searching";
	} else if (renameWorking) {
	    isFree = false;
	    message = "Renaming";
	} else if (updateWorking) {
	    isFree = false;
	    message = "Updating";
	} else if (copyOrMoveService.isRunning()) {
	    isFree = false;
	    message = "Copy-Move Service";
	}

	if (!isFree && showMessage)
	    showMessage(message);

	return isFree;
    }

    /**
     * Gets the region.
     *
     * @return the region
     */
    public Region getRegion() {
	return region;
    }

    /**
     * Gets the indicator.
     *
     * @return the indicator
     */
    public ProgressIndicator getIndicator() {
	return indicator;
    }

    /**
     * Gets the cancel button.
     *
     * @return the cancel button
     */
    public Button getCancelButton() {
	return cancel;
    }

    /**
     * Gets the next button.
     *
     * @return the next button
     */
    public Button getNextButton() {
	return next;
    }

    /**
     * Gets the previous button.
     *
     * @return the previous button
     */
    public Button getPreviousButton() {
	return previous;
    }

    /**
     * Gets the data base table name.
     *
     * @return the data base table name
     */
    public String getDataBaseTableName() {
	return dataBaseTableName;
    }

    /**
     * Show message.
     *
     * @param reason
     *            the reason
     */
    private void showMessage(String reason) {
	Notifications.create().title("Easy Bro")
		.text("[" + reason + "] is working on:\n " + toString() + "\n\t retry as soon as it finish.")
		.darkStyle().showInformation();
    }

    /**
     * Unbind.
     */
    public void unbind() {
	region.visibleProperty().unbind();
	region.setVisible(false);
	indicator.progressProperty().unbind();
    }

    /**
     * Goes on the Previous List.
     */
    public void goPrevious() {
	if (isFree(false) && !searchService.isActive() && totalInDataBase.get() != 0 && listNumber.get() > 0) {
	    listNumber.set(listNumber.get() - 1);
	    loadService.startService(false, true);
	}
    }

    /**
     * Goes on the Next List.
     */
    public void goNext() {
	if (isFree(false) && !searchService.isActive() && totalInDataBase.get() != 0
		&& listNumber.get() < maximumList()) {
	    listNumber.set(listNumber.get() + 1);
	    loadService.startService(false, true);
	}
    }

    /**
     * Updates the List.
     */
    protected void updateList() {

	// totalInDataBase==0?
	if (totalInDataBase.get() == 0) {

	    next.setVisible(false);
	    listNumber.set(0);

	} else {

	    next.setVisible(
		    (listNumber.isEqualTo(0).or(listNumber.lessThan(maximumList()))).get() && maximumList() != 0);
	}

	// update the label
	updateLabel();
	// refresh the tableViewer
	tableViewer.refresh();

    }

    /**
     * Clear all the items from list.
     */
    private void simpleClear() {
	observableList.clear();
	totalInDataBase.set(0);
	updateList();
    }

    /**
     * Sets the name.
     *
     * @param newName
     *            the new name
     */
    public void setName(String newName) {
	smartName = newName;
    }

    /**
     * Returns the name of the smart controller
     * 
     * @return The name of the smartController
     */
    public String getName() {
	return smartName;
    }

    @Override
    public String toString() {
	return "SmartController" + ": <" + smartName + ">";
    }

    /**
     * Returns Marked Songs.
     *
     * @return the selected items
     */
    public ObservableList<Media> getSelectedItems() {
	return tableViewer.getSelectionModel().getSelectedItems();
    }

    /**
     * Return the number of the final List counting from <b>firstList->0
     * SecondList->1 ....</b>
     *
     * @return the int
     */
    private int maximumList() {
	if (totalInDataBase.get() == 0)
	    return 0;
	else
	    return (totalInDataBase.get() / maximumPerList) + ((totalInDataBase.get() % maximumPerList == 0) ? -1 : 0);
    }

    /**
     * Indicates that a capture event has been fired to this controller from the
     * CaptureWindow.
     *
     * @param array
     *            the array
     */
    public void fireCaptureEvent(int[] array) {
	// Bounds bounds
	/*
	 * for (ButtonBase song : bigList) { bounds =
	 * song.localToScreen(song.getBoundsInLocal()); // button rectangle
	 * overlaps capture rectangle ? if (bounds.getMinX() <= array[0] +
	 * array[2] && bounds.getMaxX() >= array[0] && bounds.getMinY() <=
	 * array[1] + array[3] && bounds.getMaxY() >= array[1]) ((Audio)
	 * song).setMarked(true, false); else ((Audio) song).setMarked(false,
	 * false); }
	 */

	updateLabel();
    }

    /**
     * Gets the total in data base.
     *
     * @return the total in data base
     */
    public int getTotalInDataBase() {
	return totalInDataBase.get();
    }

    /**
     * Sets the total in data base.
     *
     * @param totalInDataBase
     *            the new total in data base
     */
    public void setTotalInDataBase(int totalInDataBase) {
	this.totalInDataBase.set(totalInDataBase);
    }

    /**
     * Total in data base property.
     *
     * @return the integer property
     */
    public IntegerProperty totalInDataBaseProperty() {
	return totalInDataBase;
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Table   Viewer
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Representing the data of SmartController.
     *
     * @author GOXR3PLUS
     */
    public class MediaTableViewer extends TableView<Media> {

	/** The has been played. */
	@FXML
	private TableColumn<Media, SimpleObjectProperty<ImageView>> hasBeenPlayed;

	/** The media type. */
	@FXML
	private TableColumn<Media, SimpleObjectProperty<ImageView>> mediaType;

	/** The title. */
	@FXML
	private TableColumn<Media, String> title;

	/** The duration. */
	@FXML
	private TableColumn<Media, String> duration;

	/** The times played. */
	@FXML
	private TableColumn<Media, Integer> timesPlayed;

	/** The stars. */
	@FXML
	private TableColumn<Media, Double> stars;

	/** The hour imported. */
	@FXML
	private TableColumn<Media, String> hourImported;

	/** The date imported. */
	@FXML
	private TableColumn<Media, String> dateImported;

	/** The date that the file was created */
	@FXML
	private TableColumn<Media, String> dateFileCreated;

	/** The date that the file was last modified */
	@FXML
	private TableColumn<Media, String> dateFileModified;

	/** It is a remix? */
	@FXML
	private TableColumn<?, ?> remix;

	/** The album. */
	@FXML
	private TableColumn<?, ?> album;

	/** The composer. */
	@FXML
	private TableColumn<?, ?> composer;

	/** The comment. */
	@FXML
	private TableColumn<?, ?> comment;

	/** The genre. */
	@FXML
	private TableColumn<?, ?> genre;

	/** The bpm. */
	@FXML
	private TableColumn<?, ?> bpm;

	/** The key. */
	@FXML
	private TableColumn<?, ?> key;

	/** The harmonic. */
	@FXML
	private TableColumn<?, ?> harmonic;

	/** The bit rate. */
	@FXML
	private TableColumn<?, ?> bitRate;

	/** The year. */
	@FXML
	private TableColumn<?, ?> year;

	/** The drive. */
	@FXML
	private TableColumn<Media, String> drive;

	/** The file path. */
	@FXML
	private TableColumn<Media, String> filePath;

	/** The file name. */
	@FXML
	private TableColumn<Media, String> fileName;

	/** The file type. */
	@FXML
	private TableColumn<Media, String> fileType;

	/** The file size. */
	@FXML
	private TableColumn<?, ?> fileSize;

	/** The album art. */
	@FXML
	private TableColumn<?, ?> albumArt;

	/** The singer. */
	@FXML
	private TableColumn<?, ?> singer;

	/** The image. */
	WritableImage image = new WritableImage(100, 100);

	/** The canvas. */
	Canvas canvas = new Canvas();

	/**
	 * Constructor.
	 */
	public MediaTableViewer() {

	    canvas.setWidth(100);
	    canvas.setHeight(100);

	    // FXMLLOADRE
	    FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "MediaTableViewer.fxml"));
	    loader.setController(this);
	    loader.setRoot(this);

	    try {
		loader.load();
	    } catch (IOException ex) {
		Main.logger.log(Level.WARNING, "MediaTableViewer falied to initialize fxml..", ex);
	    }

	}

	/**
	 * Called as soon as .fxml has been initialized
	 */
	@FXML
	private void initialize() {
	    String center = "-fx-alignment:CENTER-LEFT;";

	    // hasBeenPlayed
	    hasBeenPlayed.setCellValueFactory(new PropertyValueFactory<>("hasBeenPlayed"));

	    // hasBeenPlayed
	    mediaType.setCellValueFactory(new PropertyValueFactory<>("mediaType"));

	    // title
	    title.setStyle(center);
	    title.setCellValueFactory(new PropertyValueFactory<>("title"));

	    // hourImported
	    hourImported.setCellValueFactory(new PropertyValueFactory<>("hourImported"));

	    // dateImported
	    dateImported.setCellValueFactory(new PropertyValueFactory<>("dateImported"));

	    // dateFileCreated
	    dateFileCreated.setCellValueFactory(new PropertyValueFactory<>("dateFileCreated"));

	    // dateFileCreated
	    dateFileModified.setCellValueFactory(new PropertyValueFactory<>("dateFileModified"));

	    // stars
	    stars.setCellValueFactory(new PropertyValueFactory<>("stars"));

	    // timesHeard
	    timesPlayed.setCellValueFactory(new PropertyValueFactory<>("timesPlayed"));

	    // duration
	    duration.setCellValueFactory(new PropertyValueFactory<>("durationEdited"));

	    // drive
	    drive.setCellValueFactory(new PropertyValueFactory<>("drive"));

	    // filePath
	    filePath.setStyle(center);
	    filePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));

	    // fileName
	    fileName.setStyle(center);
	    fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));

	    // fileType
	    fileType.setCellValueFactory(new PropertyValueFactory<>("fileType"));

	    setRowFactory(tv -> {
		TableRow<Media> row = new TableRow<>();

		// use EasyBind to access the valueProperty of the itemProperty
		// of the cell:
		row.disableProperty().bind(
			// start at itemProperty of row
			EasyBind.select(row.itemProperty())
				// map to fileExistsProperty[a boolean] of item,
				// if item non-null
				.selectObject(Media::fileExistsProperty)
				// map to BooleanBinding checking if false
				.map(x -> !x.booleanValue())
				// value to use if item was null
				.orElse(false));

		// it's also possible to do this with the standard API, but
		// there are lots of
		// superfluous warnings sent to standard out:
		// row.setStyle("-fx-background-color:red");
		// row.disableProperty().bind(
		// Bindings.selectBoolean(row.itemProperty(),
		// "fileExists").not());

		return row;
	    });

	    // Selection Model
	    getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	    // --Drag Detected
	    setOnDragDetected(event -> {
		if (getSelectedCount() != 0 && event.getScreenY() > localToScreen(getBoundsInLocal()).getMinY() + 30) {

		    /* allow copy transfer mode */
		    Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);

		    /* put a string on drag board */
		    ClipboardContent content = new ClipboardContent();

		    // Single DND
		    if (getSelectedCount() == 1) {
			content.putFiles(Arrays.asList(new File(getSelectionModel().getSelectedItem().getFilePath())));
			getSelectionModel().getSelectedItem().setDragView(db);
			// MultipleDND
		    } else {
			// Array with all the selected
			ArrayList<File> files = new ArrayList<>();
			getSelectionModel().getSelectedItems().stream()
				.forEach(s -> files.add(new File(s.getFilePath())));

			// PutFiles
			content.putFiles(files);
			ActionTool.paintCanvas(canvas.getGraphicsContext2D(), "(" + files.size() + ")Items", 100, 100);
			db.setDragView(canvas.snapshot(null, image), 50, 0);
		    }

		    db.setContent(content);
		}
		event.consume();
	    });

	    // --Drag Over
	    setOnDragOver(dragOver -> {
		// System.out.println(over.getGestureSource() + "," +
		// controller.tableViewer)

		// // Check if the drag come from the same source
		// String gestureSourceString
		// if (over.getGestureSource() != null)
		// gestureSourceString = over.getGestureSource()
		// .toString()
		// else
		// gestureSourceString = "null"

		// The drag must come from source other than the owner
		if (dragOver.getDragboard().hasFiles() && dragOver.getGestureSource() != tableViewer) {
		    dragOver.acceptTransferModes(TransferMode.LINK);
		}
	    });

	    // --Drag Dropped
	    setOnDragDropped(drop -> {
		// Has Files? + isFree()?
		if (drop.getDragboard().hasFiles() && isFree(true))
		    inputService.start(drop.getDragboard().getFiles());

		drop.setDropCompleted(true);
	    });

	    // setOnDragDone(d -> {
	    // System.out.println(
	    // "Drag Done,is drop completed?" + d.isDropCompleted() + " , is
	    // accepted?" + d.isAccepted());
	    // System.out.println("Accepted Mode:" +
	    // d.getAcceptedTransferMode());
	    // System.out.println(" Target:" + d.getTarget() + " Gestoure
	    // Target:" + d.getGestureTarget());
	    // });

	    getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
		// Main.amazon.updateInformation((Media) newValue);
	    });

	}

	/**
	 * Calculates the selected items in the table.
	 *
	 * @return An int representing the total selected items in the table
	 */
	public int getSelectedCount() {
	    return tableViewer.getSelectionModel().getSelectedItems().size();
	}

    }

    /**
     * The Class LoadService.
     */
    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Load Service
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */
    public class LoadService extends Service<Void> {

	/** The commit. */
	private boolean commit;

	/** The request focus. */
	private boolean requestFocus;

	/**
	 * Constructor.
	 */
	public LoadService() {
	    setOnSucceeded(s -> done());
	    setOnFailed(f -> done());
	    setOnCancelled(c -> done());
	}

	/**
	 * Stars the reload Service.
	 *
	 * @param commit
	 *            the commit
	 * @param requestFocus
	 *            the request focus
	 */
	public void startService(boolean commit, boolean requestFocus) {
	    // Variables
	    this.requestFocus = requestFocus;
	    this.commit = commit;

	    // Hide ContextMenu
	    Main.songsContextMenu.hide();

	    // Start
	    try {

		// Search
		if (searchService.isActive())
		    searchService.service.search();
		// Reload
		else {
		    updateWorking = true;
		    region.visibleProperty().bind(runningProperty());
		    indicator.progressProperty().bind(progressProperty());
		    observableList.clear();
		    cancel.setText("Updating...");
		    super.reset();
		    super.start();
		}

	    } catch (Exception ex) {
		Main.logger.log(Level.WARNING, "", ex);
	    }
	}

	/**
	 * Done.
	 */
	// Work done
	private void done() {
	    commit = false;
	    updateList();
	    unbind();
	    updateWorking = false;
	    if (requestFocus)
		centerStackPane.requestFocus();

	    // Library?
	    if (genre == Genre.LIBRARYSONG)
		Main.libraryMode.updateLibraryTotalLabel(smartName);
	}

	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {

		@Override
		protected Void call() throws Exception {

		    // counter
		    int counter = 0;

		    // total entries in database
		    if (getTotalInDataBase() == 0) {
			ResultSet s = Main.dbManager.connection1.createStatement()
				.executeQuery("SELECT COUNT(*) FROM '" + dataBaseTableName + "';");
			setTotalInDataBase(s.getInt(1));
			s.close();
		    }

		    // when the final list is deleted then the controller
		    // has to go to the previous list automatically
		    if (getTotalInDataBase() != 0 && listNumber.get() > maximumList())
			listNumber.set(listNumber.get() - 1);

		    // Select the available Media Files
		    try (ResultSet resultSet = Main.dbManager.connection1.createStatement()
			    .executeQuery("SELECT* FROM '" + dataBaseTableName + "' LIMIT " + maximumPerList
				    + " OFFSET " + listNumber.get() * maximumPerList);
			    ResultSet dbCounter = Main.dbManager.connection1.createStatement()
				    .executeQuery("SELECT* FROM '" + dataBaseTableName + "' LIMIT " + maximumPerList
					    + " OFFSET " + listNumber.get() * maximumPerList);) {

			// Count how many items the result returned...
			int currentMaximumPerList = 0;
			while (dbCounter.next())
			    ++currentMaximumPerList;
			// System.out.println("Next:"+dbCounter.getString("PATH"));

			// System.out.println("CurrentMaximumPerList=:" +
			// currentMaximumPerList);

			if (genre == Genre.RADIOSTATION) {
			    /*
			     * SongButton station = null; while (set.next()) {
			     * station = new RadioStation(set.getString("NAME"),
			     * new URL(set.getString("STREAMURL")),
			     * set.getString("TAGS"), set.getDouble("STARS"));
			     * bigList.add(station); // Update Progress
			     * updateProgress(++counter, maximumPerList); }
			     */
			} else {
			    Audio song = null;
			    while (resultSet.next()) {
				song = new Audio(resultSet.getString("PATH"),
					InfoTool.durationInSeconds(resultSet.getString("PATH"), TYPE.FILE),
					resultSet.getDouble("STARS"), resultSet.getInt("TIMESPLAYED"),
					resultSet.getString("DATE"), resultSet.getString("HOUR"), genre);
				observableList.add(song);

				// Update Progress
				updateProgress(++counter, currentMaximumPerList);
			    }
			}

			// commit?
			if (commit)
			    Main.dbManager.commit();
		    } catch (Exception ex) {
			Main.logger.log(Level.WARNING, "", ex);
		    }

		    return null;

		}
	    };
	}
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Copy or Move Service
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */
    /**
     * Copy or Move items
     *
     * @author GOXR3PLUS
     *
     */
    public class CopyOrMoveService extends Service<Boolean> {

	File destinationFolder;
	int count;
	int total;
	String filePath;
	Operation operation;
	List<File> directories;

	/**
	 * Constructor
	 */
	public CopyOrMoveService() {

	    setOnSucceeded(s -> {
		done();
		Notifications.create().title("Message")
			.text(operation + " successfully done for:\n\t" + SmartController.this).show();
	    });

	    setOnFailed(f -> {
		done();
		Notifications.create().title("Message").text(operation + " failed for:\n\t" + SmartController.this)
			.darkStyle().showError();
	    });

	    setOnCancelled(c -> done());
	}

	/**
	 * Copying process
	 */
	public void startCopy(List<File> directories) {
	    if (!isRunning() && isFree(true)) {
		this.directories = directories;
		commonOperations(Operation.COPY);
	    } else
		Notifications.create().title("Warning").text("Copy can't start!!").showInformation();
	}

	/**
	 * Moving process
	 */
	public void startMoving(List<File> directories) {
	    if (!isRunning() && isFree(true)) {
		this.directories = directories;
		commonOperations(Operation.MOVE);
	    } else
		Notifications.create().title("Warning").text("Moving can't start!!").showInformation();
	}

	/**
	 * Common operations on (move and copy) processes
	 */
	private void commonOperations(Operation operation) {
	    this.operation = operation;

	    // The choosen directories
	    destinationFolder = directories.get(0);
	    directories.forEach(directory -> directory.mkdir());

	    // Bindings
	    region.visibleProperty().bind(runningProperty());
	    indicator.progressProperty().bind(progressProperty());
	    cancel.setText("Exporting...");
	    cancel.setDisable(false);
	    cancel.setOnAction(e -> {
		super.cancel();
		cancel.setDisable(true);
	    });

	    // start
	    this.reset();
	    this.start();
	}

	/**
	 * Process has been done
	 */
	private void done() {
	    cancel.setDisable(true);
	    unbind();
	}

	@Override
	protected Task<Boolean> createTask() {
	    return new Task<Boolean>() {

		@Override
		protected Boolean call() throws Exception {

		    try {
			count = 0;
			// total = (int) observableList.stream().filter(button
			// -> ( (Audio) button ).isMarked()).count();
			total = observableList.size();

			// Multiple Items have been selected
			if (total > 0) {
			    // Stream
			    Stream<Media> stream = observableList.stream();
			    stream.forEach(media -> {
				if (!isCancelled()) {
				    // if (button.isMarked()) {

				    passItem(media);

				    // updateProgress
				    updateProgress(++count, total);
				    // }
				} else
				    stream.close();
			    });

			    // User has pressed right click or a shortcut so one
			    // item has passed
			}
			// } else {
			// passItem(Main.songsContextMenu.getM);
			//
			// // updateProgress
			// updateProgress(1, 1);
			// }
		    } catch (Exception ex) {
			ex.printStackTrace();
			return false;
		    }
		    return true;
		}

		/**
		 * Pass the media to the controller
		 *
		 * @param media
		 */
		private void passItem(Media media) {

		    filePath = ((Audio) media).getFilePath();

		    if (!new File(filePath).exists())
			return;

		    if (operation == Operation.COPY)
			copy(filePath, destinationFolder + File.separator + media.getFileName());
		    else
			move(filePath, destinationFolder + File.separator + media.getFileName());
		}

		/**
		 * Copy a file from source to destination
		 *
		 * @param source
		 * @param destination
		 */
		public void copy(String source, String destination) {

		    // Use bytes stream to support all file types
		    try (InputStream in = new FileInputStream(source);
			    OutputStream out = new FileOutputStream(destination)) {

			byte[] buffer = new byte[1024];

			int length;

			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0)
			    out.write(buffer, 0, length);

		    } catch (Exception ex) {
			ex.printStackTrace();
		    }

		    System.out.println("Copying ->" + filePath + "\n\tto ->" + destinationFolder);

		}

		/**
		 * Moves a file from source to destination
		 *
		 * @param source
		 * @param destination
		 * @return
		 */
		public boolean move(String source, String destination) {
		    copy(source, destination);
		    boolean b = new File(source).delete();
		    System.out.println("Moving [" + b + "] ->" + filePath + "\n\tto ->" + destinationFolder);
		    return b;
		}

	    };
	}
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							InputService
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    // TODO When importing files from external disk they are not added properlly
    /**
     * Manages the input operations of the SmartController.
     *
     * @author GOXR3PLUS
     */
    public class InputService extends Service<Void> {

	/** The list. */
	private List<File> list;

	/** The job. */
	private String job;

	/** The counter. */
	private int counter;

	/** The total files. */
	private int totalFiles;

	/**
	 * Constructor.
	 */
	public InputService() {

	    setOnSucceeded(s -> done());
	    setOnCancelled(c -> {
		// Clear all Batches
		try {
		    preparedInsert.clearBatch();
		} catch (SQLException ex) {
		    Main.logger.log(Level.WARNING, "", ex);
		}
		done();
	    });
	    setOnFailed(c -> done());
	}

	/**
	 * Start the Service.
	 *
	 * @param list
	 *            the list
	 */
	public void start(List<File> list) {
	    if (isFree(true)) {

		// Security
		job = "upload from system";
		this.list = list;
		depositWorking = true;

		// Binds
		getRegion().visibleProperty().bind(runningProperty());
		getIndicator().progressProperty().bind(progressProperty());
		getCancelButton().setDisable(false);
		getCancelButton().setText("Calculating entries...");
		getCancelButton().setOnAction(e -> {
		    super.cancel();
		    getCancelButton().setDisable(true);
		});

		// ....
		reset();
		start();
	    }
	}

	/**
	 * When the work is done.
	 */
	private void done() {
	    list = null;
	    unbind();
	    getCancelButton().setDisable(true);
	    depositWorking = false;
	    loadService.startService(true, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {
		@Override
		protected Void call() throws Exception {

		    counter = 0;
		    totalFiles = 0;
		    String date = InfoTool.getCurrentDate();

		    // Start the insert work
		    if ("upload from system".equals(job)) {

			// Count all the files
			for (File file : list) {
			    System.out.println("Checking file:" + file.getAbsolutePath());
			    if (file.exists())
				if (!isCancelled())
				    totalFiles += countFiles(file);
				else
				    break;
			}

			// System.out.println("Total Files are->" + totalFiles)

			Platform.runLater(() -> getCancelButton().setText("Adding [" + totalFiles + "] entries"));

			// insert in database
			for (File file : list) {
			    if (file.exists() && !isCancelled()) {
				try (Stream<Path> paths = Files.walk(Paths.get(file.getPath()))) {
				    paths.forEach(s -> {

					// System.out.println("Adding...."+s.toString())

					// cancelled?
					if (isCancelled())
					    paths.close();
					// supported?
					else if (InfoTool.isAudioSupported(s.toString()))
					    insertMedia(s.toString(), 0, 0, date, InfoTool.getLocalTime());

					// update progress
					updateProgress(++counter, totalFiles);
				    });
				} catch (IOException ex) {
				    Main.logger.log(Level.WARNING, "", ex);
				}
			    }
			}

		    }

		    saveInDataBase();

		    return null;
		}

		/**
		 * Save everything in database
		 */
		private void saveInDataBase() {
		    // ...
		    if (!isCancelled()) {
			updateProgress(-1, 0);
			final CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
			    getCancelButton().setDisable(true);
			    getCancelButton().setText("Hold on(saving)...");
			    latch.countDown();
			});
			try {
			    latch.await();
			    // Count how many items where added
			    setTotalInDataBase((int) (getTotalInDataBase()
				    + Arrays.stream(preparedInsert.executeBatch()).filter(s -> s > 0).count()));
			    // Platform.runLater(() -> updateTotalLabel());
			} catch (SQLException ex) {
			    Main.logger.log(Level.WARNING, "", ex);
			} catch (InterruptedException ex) {
			    Main.logger.log(Level.WARNING, "", ex);
			}
		    }
		}

		/**
		 * Count files in a directory (including files in all sub
		 * directories)
		 * 
		 * @param directory
		 *            the directory to start in
		 * @return the total number of files
		 */
		private int countFiles(File dir) {
		    if (dir.exists())
			try (Stream<Path> paths = Files.walk(Paths.get(dir.getPath()))) {
			    return (int) paths.filter(s -> {

				// System.out.println("Counting..." +
				// s.toString());

				// cancelled?
				if (isCancelled())
				    paths.close();
				else
				    return InfoTool.isAudioSupported(s.toString());

				return false;
			    }).count();
			} catch (IOException ex) {
			    Main.logger.log(Level.WARNING, "", ex);
			}

		    return 0;
		}

		/**
		 * Insert this song into the dataBase table
		 * 
		 * @param path
		 * @param stars
		 * @param timesPlayed
		 * @param dateCreated
		 * @param hourCreated
		 */
		private void insertMedia(String path, double stars, int timesPlayed, String dateCreated,
			String hourCreated) {

		    try {

			if (dateCreated == null || hourCreated == null) {
			    try {
				throw new Exception("DATE OR HOUR CREATED ARE NULL [LIBRARY INSERT SONG!]");
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}

			// Save the Song in the appropriate database table
			preparedInsert.setString(1, path);
			preparedInsert.setDouble(2, stars);
			preparedInsert.setInt(3, timesPlayed);
			preparedInsert.setString(4, dateCreated);
			preparedInsert.setString(5, hourCreated);
			preparedInsert.addBatch();
			// if (uInsertIntoLib.executeUpdate() > 0 ? true :
			// false)
			// updateTotalSongs(++totalSongs, false, false);
			// if (sInsert.executeUpdate() > 0)
			// ++controller.totalInDataBase;
		    } catch (SQLException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		    }

		}

	    };
	}

    }

}
