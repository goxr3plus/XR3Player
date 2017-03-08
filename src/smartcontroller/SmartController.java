/*
 * 
 */
package smartcontroller;

import java.io.File;
import java.io.IOException;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.control.Notifications;
import org.fxmisc.easybind.EasyBind;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import customnodes.FunIndicator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import media.Audio;
import media.Media;
import tools.ActionTool;
import tools.InfoTool;
import xplayer.presenter.AudioType;

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
     * If the search is instant or needs the user to press enter on the search field
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

    @FXML
    private VBox indicatorVBox;

    /** The indicator. */
    @FXML
    private ProgressBar indicator;

    /** The cancel. */
    @FXML
    private Button cancel;

    @FXML
    private HBox searchBarHBox;

    // ----------------------------------------------------------
    /** @see Genre */
    public final Genre genre;

    /**
     * The name of the database table (eg. @see ActionTool.returnRandomTableName())
     */
    private final String dataBaseTableName;

    /** The name of the SmartController . */
    private String controllerName = null;

    /** Total items in database table . */
    private IntegerProperty totalInDataBase = new SimpleIntegerProperty(0);

    /**
     * The last focus owner of the Scene , it is used by the pageField TextField
     */
    private Node focusOwner;

    /** The table viewer. */
    public final MediaTableViewer tableViewer;

    /** This list keeps all the Media Items from the TableViewer */
    public ObservableList<Media> itemsObservableList = FXCollections.observableArrayList();

    /** The current page inside the TableViewer */
    IntegerProperty currentPage = new SimpleIntegerProperty(0);

    /** Maximum items allowed per page. */
    int maximumPerPage = 50;

    // ---------Services--------------------------

    /** The search service. */
    public final SmartSearcher searchService;

    /** The load service. */
    public final LoadService loadService;

    /** The input service. */
    public final InputService inputService;

    /** CopyOrMoveService */
    public final CopyOrMoveService copyOrMoveService;

    // ---------Security---------------------------

    /** The deposit working. */
    public volatile boolean depositWorking;

    /** The un deposit working. */
    public volatile boolean undepositWorking;

    /** The rename working. */
    public volatile boolean renameWorking;

    /** The update working. */
    public volatile boolean updateWorking;

    // --------Prepared Statements------------------

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

    // --------------------------------------------------

    /**
     * Instantiates a new smart controller.
     *
     * @param genre
     *            .. @see Genre
     * @param controllerName
     *            The name of the SmartController
     * @param dataBaseTableName
     *            The name of the database table <br>
     *            ..@see ActionTool.returnRandomTableName()
     */
    public SmartController(Genre genre, String controllerName, String dataBaseTableName) {
	this.genre = genre;
	this.controllerName = controllerName;
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

	// ------ centerStackPane
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

	// ------ tableViewer
	tableViewer.setItems(itemsObservableList);
	tableViewer.setPlaceholder(new Label("Import or Drag Media Here ;) "));
	tableViewer.setOnMouseReleased(m -> {
	    if (m.getButton() == MouseButton.SECONDARY && !tableViewer.getSelectionModel().getSelectedItems().isEmpty())
		Main.songsContextMenu.showContextMenu(tableViewer.getSelectionModel().getSelectedItem(), genre,
			m.getScreenX(), m.getScreenY(), this);
	});

	centerStackPane.getChildren().add(tableViewer);
	tableViewer.toBack();

	// FunIndicator
	FunIndicator funIndicator = new FunIndicator();
	super.getChildren().add(super.getChildren().size() - 1, funIndicator);

	// ------ region
	region.setVisible(false);
	region.visibleProperty().addListener((observable, oldValue, newValue) -> {
	    if (region.isVisible()) {
		funIndicator.setFromColor(Color.RED);
		funIndicator.start();
	    } else
		funIndicator.pause();
	});

	// ------ progress indicator
	funIndicator.visibleProperty().bind(region.visibleProperty());
	indicator.visibleProperty().bind(region.visibleProperty());
	indicatorVBox.visibleProperty().bind(region.visibleProperty());

	// ------ cancel
	cancel.setStyle(
		"-fx-background-color:rgb(0,0,0,0.7); -fx-background-radius:20; -fx-text-fill:orange; -fx-font-size:18px; -fx-font-weight:bold;");
	cancel.visibleProperty().bind(region.visibleProperty());
	cancel.setDisable(true);

	// ------ searchBarHBox
	searchBarHBox.getChildren().add(0, searchService);

	// ------ previous
	previous.opacityProperty()
		.bind(Bindings.when(previous.hoverProperty().or(next.hoverProperty())).then(1.0).otherwise(0.5));
	previous.disableProperty().bind(next.disabledProperty());
	previous.visibleProperty().bind(currentPage.isNotEqualTo(0));
	previous.setOnAction(a -> goPrevious());

	// ------- next
	next.opacityProperty()
		.bind(Bindings.when(next.hoverProperty().or(previous.hoverProperty())).then(1.0).otherwise(0.5));
	next.setVisible(false);
	next.setOnAction(a -> goNext());

	// -------- pageField
	pageField.opacityProperty()
		.bind(Bindings.when(pageField.hoverProperty().or(next.hoverProperty()).or(previous.hoverProperty()))
			.then(1.0).otherwise(0.03));
	pageField.disableProperty().bind(next.disabledProperty());
	pageField.textProperty().addListener((observable, oldValue, newValue) -> {

	    if (!newValue.matches("\\d"))
		pageField.setText(newValue.replaceAll("\\D", ""));

	    if (!pageField.getText().isEmpty()) {
		// System.out.println("Setting")
		int maximumPage = maximumList();
		// System.out.println("maximum Page:"+maximumPage)
		if (Integer.parseInt(pageField.getText()) > maximumPage)
		    Platform.runLater(() -> {
			pageField.setText(Integer.toString(maximumPage));
			pageField.selectEnd();
		    });
	    }

	});
	pageField.setOnAction(ac -> { // ENTER KEY
	    if (!pageField.getText().isEmpty() && !loadService.isRunning() && !searchService.service.isRunning()
		    && totalInDataBase.get() != 0) {
		int lisN = Integer.parseInt(pageField.getText());
		if (lisN <= maximumList()) {
		    currentPage.set(lisN);
		    loadService.startService(false, true);
		} else {
		    pageField.setText(Integer.toString(lisN));
		    pageField.selectEnd();
		}
	    }

	});
	pageField.setOnScroll(scroll -> { // SCROLL
	    if (scroll.getDeltaY() > 0 && currentPage.get() + 1 <= maximumList())
		currentPage.set(currentPage.get() + 1);
	    else if (scroll.getDeltaY() < 0 && currentPage.get() - 1 >= 0)
		currentPage.set(currentPage.get() - 1);

	    // Update pageField
	    pageField.setText(String.valueOf(currentPage.get()));
	    pageField.selectEnd();
	    pageField.deselect();
	});
	pageField.hoverProperty().addListener(l -> { // HOVER
	    if (pageField.isHover()) {
		focusOwner = Main.window.getScene().getFocusOwner();
		pageField.requestFocus();
		pageField.selectEnd();
		// pageField.deselect()
	    } else {
		focusOwner.requestFocus();
	    }
	});

	// -------- totalItemsShown
	for (MenuItem item : totalItemsShown.getItems())
	    item.setOnAction(ac -> {

		// GO
		int current = Integer.parseInt(item.getText());
		if (maximumPerPage != current) {
		    currentPage.set((maximumPerPage == 50) ? currentPage.get() / 2
			    : currentPage.get() * 2 + (currentPage.get() % 2 == 0 ? 0 : 1));
		    maximumPerPage = current;
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
	exportFiles.setOnAction(a -> Main.exportWindow.show(this));

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
     *            true->storage medium + (play list)/library false->only from (play list)/library<br>
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
		    Main.libraryMode.updateLibraryTotalLabel(controllerName);

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
		Main.libraryMode.updateLibraryTotalLabel(controllerName);
	}
    }

    /**
     * Updates the label of the smart controller.
     */
    public void updateLabel() {
	if (!searchService.isActive()) {
	    titledPane.setText("[Total:<" + totalInDataBase.get() + "> CurrentPage:<" + currentPage.get()
		    + "> MaxPage:<" + maximumList() + ">] [ Selected:" + tableViewer.getSelectedCount() + "  Showing:"
		    + currentPage.get() * maximumPerPage + "-"
		    + (currentPage.get() * maximumPerPage + itemsObservableList.size() + " ]"));

	} else {
	    titledPane.setText("[Found: <" + itemsObservableList.size() + "> first matching] Selected:"
		    + tableViewer.getSelectedCount());

	}

	pageField.setText(Integer.toString(currentPage.get()));
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
	if (isFree(false) && !searchService.isActive() && totalInDataBase.get() != 0 && currentPage.get() > 0) {
	    currentPage.set(currentPage.get() - 1);
	    loadService.startService(false, true);
	}
    }

    /**
     * Goes on the Next List.
     */
    public void goNext() {
	if (isFree(false) && !searchService.isActive() && totalInDataBase.get() != 0
		&& currentPage.get() < maximumList()) {
	    currentPage.set(currentPage.get() + 1);
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
	    currentPage.set(0);

	} else {

	    next.setVisible(
		    (currentPage.isEqualTo(0).or(currentPage.lessThan(maximumList()))).get() && maximumList() != 0);
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
	itemsObservableList.clear();
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
	controllerName = newName;
    }

    /**
     * Returns the name of the smart controller
     * 
     * @return The name of the smartController
     */
    public String getName() {
	return controllerName;
    }

    @Override
    public String toString() {
	return "SmartController" + ": <" + controllerName + ">";
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
     * Return the number of the final List counting from <b>firstList->0 SecondList->1 ....</b>
     *
     * @return the int
     */
    public int maximumList() {
	if (totalInDataBase.get() == 0)
	    return 0;
	else
	    return (totalInDataBase.get() / maximumPerPage) + ((totalInDataBase.get() % maximumPerPage == 0) ? -1 : 0);
    }

    /**
     * Indicates that a capture event has been fired to this controller from the CaptureWindow.
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

	    getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) l ->
	    // Main.amazon.updateInformation((Media) newValue)
	    updateLabel());

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
		    cancel.setText("Updating...");
		    itemsObservableList.clear();
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
		Main.libraryMode.updateLibraryTotalLabel(controllerName);

	    // System.out.println("Is this JavaFX Thread: " +
	    // Platform.isFxApplicationThread())

	    // Reset the default vertical scroll position before the search
	    // happened
	    if (searchService.getVerticalScrollBarPosition() != -1.00) {
		ScrollBar verticalBar = (ScrollBar) tableViewer.lookup(".scroll-bar:vertical");
		if (verticalBar != null)
		    verticalBar.setValue(searchService.getVerticalScrollBarPosition());

		// System.out.println(
		// "Trying to set the vertical scrollPosition:" +
		// searchService.getVerticalScrollBarPosition())

		// Reset to -1
		searchService.setVerticalScrollBarPosition(-1.00);
	    }
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
		    if (getTotalInDataBase() != 0 && currentPage.get() > maximumList())
			currentPage.set(currentPage.get() - 1);

		    // Select the available Media Files
		    try (ResultSet resultSet = Main.dbManager.connection1.createStatement()
			    .executeQuery("SELECT* FROM '" + dataBaseTableName + "' LIMIT " + maximumPerPage
				    + " OFFSET " + currentPage.get() * maximumPerPage);
			    ResultSet dbCounter = Main.dbManager.connection1.createStatement()
				    .executeQuery("SELECT* FROM '" + dataBaseTableName + "' LIMIT " + maximumPerPage
					    + " OFFSET " + currentPage.get() * maximumPerPage);) {

			// Count how many items the result returned...
			int currentMaximumPerList = 0;
			while (dbCounter.next())
			    ++currentMaximumPerList;
			// System.out.println("Next:"+dbCounter.getString("PATH"))

			// System.out.println("CurrentMaximumPerList=:"
			// +currentMaximumPerList)

			if (genre == Genre.RADIOSTATION) {

			    // SongButton station = null;
			    // while (set.next()) {
			    // station = new RadioStation(set.getString("NAME"),
			    // new URL(set.getString("STREAMURL")),
			    // set.getString("TAGS"), set.getDouble("STARS"));
			    // bigList.add(station); // Update Progress
			    // updateProgress(++counter, maximumPerList);
			    // }

			} else {
			    // Fetch the items from the database
			    List<Media> array = new ArrayList<>();
			    Audio song = null;
			    while (resultSet.next()) {
				song = new Audio(resultSet.getString("PATH"),
					InfoTool.durationInSeconds(resultSet.getString("PATH"), AudioType.FILE),
					resultSet.getDouble("STARS"), resultSet.getInt("TIMESPLAYED"),
					resultSet.getString("DATE"), resultSet.getString("HOUR"), genre);
				array.add(song);

				// Update Progress
				updateProgress(++counter, currentMaximumPerList);
			    }

			    // Add the the items to the observable list
			    CountDownLatch countDown = new CountDownLatch(1);
			    Platform.runLater(() -> {
				itemsObservableList.addAll(array);
				countDown.countDown();
			    });
			    countDown.await();
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
	 * 
	 * @param directories
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
	 * 
	 * @param directories
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
			total = itemsObservableList.size();

			// Multiple Items have been selected
			if (total > 0) {
			    // Stream
			    Stream<Media> stream = itemsObservableList.stream();
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
			ActionTool.copy(filePath, destinationFolder + File.separator + media.getFileName());
		    else
			ActionTool.move(filePath, destinationFolder + File.separator + media.getFileName());
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
	private int progress;

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

		// We need only directories or media files
		this.list = list.stream()
			.filter(file -> file.isDirectory()
				|| (file.isFile() && InfoTool.isAudioSupported(file.getAbsolutePath())))
			.collect(Collectors.toList());
		depositWorking = true;
		// System.out.println(this.list)

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

	int batchcount;
	int batchSize;

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

		    progress = 0;
		    totalFiles = 0;
		    String date = InfoTool.getCurrentDate();

		    // Start the insert work
		    if ("upload from system".equals(job)) {

			// Count all the files
			for (File file : list) {
			    System.out.println("Checking [ file||folder ]:" + file.getAbsolutePath());
			    // Update to show which entry is being inserted
			    Platform.runLater(() -> getCancelButton().setText("Checking:" + file.getName()));

			    // File or Folder exists?
			    if (file.exists())
				if (!isCancelled())
				    totalFiles += countFiles(file);
				else
				    break;
			}

			// System.out.println("Total Files are->" + totalFiles)

			
			//Calculate the batch size
//			if (totalFiles < 20_000)
//			    batchSize = 1000;
//			else if (totalFiles < 100_000)
//			    batchSize = 5000;
//			else
//			    batchSize = 10_000;
//			
			
//			batchcount = 0;

			// INSERT
			Platform.runLater(() -> getCancelButton().setText("Adding: [" + totalFiles + "] entries..."));
			for (File file : list) {
			    if (file.exists() && !isCancelled()) {
				try (Stream<Path> paths = Files.walk(Paths.get(file.getPath()))) {
				    paths.forEach(path -> {

					// System.out.println("Adding...."+s.toString())

					// cancelled?
					if (isCancelled())
					    paths.close();
					// supported?
					else if (InfoTool.isAudioSupported(path.toString()))
					    insertMedia(path.toString(), 0, 0, date, InfoTool.getLocalTime());

					//For performance reasons
//					if ((++batchcount % batchSize) == 0) {
//					    try {
//						preparedInsert.executeBatch();
//					    } catch (SQLException ex) {
//						ex.printStackTrace();
//					    }
//					}

					// update progress
					updateProgress(++progress, totalFiles);
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
			    getCancelButton().setText("Saving...");
			    latch.countDown();
			});
			try {
			    latch.await();

			    //Insert the remaining
			    preparedInsert.executeBatch();

			    // Count how many items where added
			    //--Below i need to know how many entries have been successfully added [ will be implemented better soon... :) ]
			    // setTotalInDataBase((int) (getTotalInDataBase()
			    //	    + Arrays.stream(preparedInsert.executeBatch()).filter(s -> s > 0).count()))
			    setTotalInDataBase(0);
			    // Platform.runLater(() -> updateTotalLabel())
			} catch (SQLException | InterruptedException ex) {
			    Main.logger.log(Level.WARNING, "", ex);
			}
		    }
		}

		/**
		 * Count files in a directory (including files in all sub directories)
		 * 
		 * @param directory
		 *            the directory to start in
		 * @return the total number of files
		 */
		private int countFiles(File dir) {
		    if (dir.exists())
			try (Stream<Path> paths = Files.walk(Paths.get(dir.getPath()))) {
			    return (int) paths.filter(path -> {

				// System.out.println("Counting..." +
				// s.toString())

				// cancelled?
				if (isCancelled())
				    paths.close();
				else
				    return InfoTool.isAudioSupported(path.toString());

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
