package librarysystema;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSlider.IndicatorPosition;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import database.LocalDBManager;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
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
    private Button previous;

    @FXML
    private Button next;

    @FXML
    private Button newLibrary;

    @FXML
    private GridPane topGrid;

    @FXML
    private Button createLibrary;

    @FXML
    private JFXToggleButton selectionModeToggle;

    // ------------------------------------------------

    // protected boolean dragDetected

    /**
     * The mechanism which allows you to transport items between libraries and more.
     */
    public final LibrariesSearcher librariesSearcher = new LibrariesSearcher();

    /**
     * The mechanism which allows you to view the libraries as components with image etc.
     */
    public final LibrariesViewer libraryViewer = new LibrariesViewer();

    /** The mechanism behind of opening multiple libraries. */
    public final MultipleLibraries multipleLibs = new MultipleLibraries();

    /** The insert new library. */
    PreparedStatement insertNewLibrary;

    /**
     * Default image of a library(which has not a costume one selected by the user.
     */
    public static final Image defaultImage = InfoTool.getImageFromDocuments("library.png");
    /**
     * A classic warning image to inform the user about something
     * 
     */
    public static final Image warningImage = InfoTool.getImageFromDocuments("warning.png");

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
		if (!libraryViewer.items.stream().anyMatch(lib -> lib.getLibraryName().equals(name))) {
		    String dataBaseTableName;
		    boolean validName;

		    // Until the randomName doesn't already exists
		    do {
			dataBaseTableName = ActionTool.returnRandomTableName();
			validName = !LocalDBManager.tableExists(dataBaseTableName);
		    } while (!validName);

		    try (Statement statement = Main.dbManager.connection1.createStatement()) {

			// Create the dataBase table
			statement.executeUpdate("CREATE TABLE '" + dataBaseTableName + "'"
				+ "(PATH       TEXT    PRIMARY KEY   NOT NULL ," + "STARS       DOUBLE     NOT NULL,"
				+ "TIMESPLAYED  INT     NOT NULL," + "DATE        TEXT   	NOT NULL,"
				+ "HOUR        TEXT    NOT NULL)");

			// Create the Library
			Library currentLib = new Library(name, dataBaseTableName, 0, null, null, null, 1,
				libraryViewer.items.size(), null, false);

			// Add the library
			currentLib.goOnSelectionMode(selectionModeToggle.isSelected());
			libraryViewer.addLibrary(currentLib, true);

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
		    }

		} else {
		    Notifications.create().title("Dublicate Name")
			    .text("A Library or PlayList with this name already exists!").darkStyle().showConfirm();
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
     * This method inits the appropriate prepared statements
     */
    public void initPreparedStatements() {
	// Prepared Statement
	try {
	    insertNewLibrary = Main.dbManager.connection1.prepareStatement(
		    "INSERT INTO LIBRARIES (NAME,TABLENAME,STARS,DATECREATED,TIMECREATED,DESCRIPTION,SAVEMODE,POSITION,LIBRARYIMAGE,OPENED) "
			    + "VALUES (?,?,?,?,?,?,?,?,?,?)");
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
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
	for (Library library : libraryViewer.items)
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
	newLibrary.visibleProperty().bind(Bindings.size(libraryViewer.items).isEqualTo(0));

	// selectionModeToggle
	selectionModeToggle.selectedProperty()
		.addListener((observable, oldValue, newValue) -> libraryViewer.goOnSelectionMode(newValue));

	// searchLibrary
	topGrid.add(librariesSearcher, 1, 0);

	// previous
	previous.setOnAction(a -> libraryViewer.previous());

	// next
	next.setOnAction(a -> libraryViewer.next());

	// StackPane
	librariesStackView.getChildren().addAll(libraryViewer, librariesSearcher.region,
		librariesSearcher.searchProgress);
	libraryViewer.toBack();

	// XPlayer - 0
	Main.xPlayersList.addXPlayerController(new XPlayerController(0));
	Main.xPlayersList.getXPlayerController(0).makeTheDisc(136, 136, Color.ORANGE, 45, Side.LEFT);
	Main.xPlayersList.getXPlayerController(0).makeTheVisualizer(Side.RIGHT);
	add(Main.xPlayersList.getXPlayerController(0), 1, 1);

    }

    /**
     * Used to create a new Library
     * 
     * @param owner
     */
    public void createNewLibrary(Node owner) {
	if (!Main.renameWindow.isShowing()) {

	    // Open rename window
	    Main.renameWindow.show("", owner);

	    // Add the showing listener
	    Main.renameWindow.showingProperty().addListener(creationInvalidator);
	}
    }

    /**
     * Gets the previous.
     *
     * @return the previous
     */
    protected Button getPrevious() {
	return previous;
    }

    /**
     * Gets the next.
     *
     * @return the next
     */
    protected Button getNext() {
	return next;
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
     * 						    Libraries Viewer
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
     * This class allows you to view the libraries.
     *
     * @author SuperGoliath
     */
    public class LibrariesViewer extends Region {

	/** The context menu. */
	public LibraryContextMenu contextMenu = new LibraryContextMenu();

	/** The settings. */
	public LibrarySettings settings = new LibrarySettings();

	/** The Constant WIDTH. */
	double WIDTH = 120;

	/** The Constant HEIGHT. */
	double HEIGHT = WIDTH + (WIDTH * 0.4);

	/** The duration. */
	private final Duration duration = Duration.millis(450);

	/** The interpolator. */
	private final Interpolator interpolator = Interpolator.EASE_BOTH;

	/** The Constant SPACING. */
	private double SPACING = 120;

	/** The Constant LEFT_OFFSET. */
	private double LEFT_OFFSET = -110;

	/** The Constant RIGHT_OFFSET. */
	private double RIGHT_OFFSET = 110;

	/** The Constant SCALE_SMALL. */
	private static final double SCALE_SMALL = 0.6;

	/** The items. */
	ObservableList<Library> items = FXCollections.observableArrayList();
	/**
	 * This class wraps an ObservableList
	 */
	public SimpleListProperty<Library> list = new SimpleListProperty<>(items);

	/** The centered. */
	private Group centered = new Group();

	/** The left group. */
	private Group leftGroup = new Group();

	/** The center group. */
	private Group centerGroup = new Group();

	/** The right group. */
	private Group rightGroup = new Group();

	/** The center index. */
	int centerIndex = 0;

	/** The scroll bar. */
	JFXSlider jfSlider = new JFXSlider();

	/** The time line */
	private Timeline timeline = new Timeline();

	Rectangle clip = new Rectangle();

	/**
	 * Instantiates a new libraries viewer.
	 */
	// Constructor
	public LibrariesViewer() {

	    // this.setOnMouseMoved(m -> {
	    //
	    // if (dragDetected) {
	    // System.out.println("Mouse Moving... with drag detected");
	    //
	    // try {
	    // Robot robot = new Robot();
	    // robot.mouseMove((int) m.getScreenX(),
	    // (int) this.localToScreen(this.getBoundsInLocal()).getMinY() + 2);
	    // } catch (AWTException ex) {
	    // ex.printStackTrace();
	    // }
	    // }
	    // })

	    //super.setCache(true)
	    // super.setCacheHint(CacheHint.SPEED)

	    // clip.set
	    setClip(clip);
	    setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, purple 87%);");
	    //setStyle("-fx-background-color: linear-gradient(to bottom,black 60,#141414 60.2%, purple 87%);")

	    // ScrollBar
	    jfSlider.setIndicatorPosition(IndicatorPosition.RIGHT);
	    jfSlider.setCursor(Cursor.HAND);
	    jfSlider.setMin(0);
	    jfSlider.setMax(0);
	    jfSlider.visibleProperty().bind(list.sizeProperty().greaterThan(3));
	    // scrollBar.setVisibleAmount(1)
	    // scrollBar.setUnitIncrement(1)
	    // scrollBar.setBlockIncrement(1)
	    jfSlider.setShowTickLabels(true);
	    // scrollBar.setMajorTickUnit(1)
	    jfSlider.setShowTickMarks(true);
	    jfSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
		int newVal = (int) Math.round(newValue.doubleValue());
		int oldVal = (int) Math.round(oldValue.doubleValue());
		// new!=old
		if (newVal != oldVal) {
		    setCenterIndex(newVal);
		}

		// System.out.println(scrollBar.getValue())
	    });

	    // setFocusTraversable(true)
	    // setOnKeyReleased(key -> {
	    // if (key.getCode() == KeyCode.LEFT) {
	    // if (timeline.getStatus() != Status.RUNNING)
	    // previous();
	    // } else if (key.getCode() == KeyCode.RIGHT) {
	    // if (timeline.getStatus() != Status.RUNNING)
	    // next();
	    // }
	    //
	    // })

	    // create content
	    centered.getChildren().addAll(leftGroup, rightGroup, centerGroup);

	    getChildren().addAll(centered, jfSlider);
	}

	/**
	 * The Collection that holds all the Libraries
	 * 
	 * @return The Collection that holds all the Libraries
	 */
	public ObservableList<Library> getItems() {
	    return items;
	}

	// ----About the last size of each Library
	double lastSize;

	// ----About the width and height of LibraryMode Clip
	int previousWidth;
	int previousHeight;

	int counter;
	double var = 1.5;

	@Override
	protected void layoutChildren() {

	    // update clip to our size
	    clip.setWidth(getWidth());
	    clip.setHeight(getHeight());

	    // keep centered centered

	    WIDTH = getHeight();
	    HEIGHT = WIDTH;// + (WIDTH * 0.4)

	    double variable = WIDTH / var;
	    centered.setLayoutX((getWidth() - variable) / 2);  //WIDTH/var) / 2)
	    centered.setLayoutY((getHeight() - variable) / 2); //HEIGHT / var) / 2)

	    // centered.setLayoutX((getWidth() - WIDTH) / 2)
	    // centered.setLayoutY((getHeight() - HEIGHT) / 2)

	    jfSlider.setLayoutX(getWidth() / 2 - 100);
	    jfSlider.setLayoutY(15);
	    jfSlider.resize(200, 15);

	    // AVOID DOING CALCULATIONS WHEN THE CLIP SIZE IS THE SAME
	    // if (previousWidth != (int) WIDTH ||
	    if (previousHeight != (int) HEIGHT) {
		// System.out.println("Updating Library Size")

		// Update ImageView width and height
		SPACING = HEIGHT / (var + 0.5);
		LEFT_OFFSET = -(SPACING - 10);
		RIGHT_OFFSET = -LEFT_OFFSET;
		// For-Each
		items.forEach(library -> {
		    double size = HEIGHT / var;

		    // --
		    library.imageView.setFitWidth(size);
		    library.imageView.setFitHeight(size);
		    library.setMaxWidth(size);
		    library.setMaxHeight(size);
		});

		// Dont Fuck the CPU
		double currentSize = WIDTH / var; // the current size of each
						 // library
		boolean doUpdate = Math.abs(currentSize - lastSize) > 2;
		// System.out.println("Do update?:" + doUpdate + " , " +
		// Math.abs(currentSize - lastSize) + "SSD.U2\n")
		lastSize = currentSize;
		if (doUpdate)
		    update();
	    }

	    previousWidth = (int) WIDTH;
	    previousHeight = (int) HEIGHT;
	    // System.out.println("Counter:" + (++counter) + " , " + getWidth()
	    // + "," + getHeight())

	}

	/**
	 * Go on selection mode.
	 *
	 * @param way
	 *            the way
	 */
	public void goOnSelectionMode(boolean way) {
	    for (Library library : items)
		library.goOnSelectionMode(way);
	}

	/**
	 * Add multiple libraries at once.
	 *
	 * @param libraries
	 *            the libraries
	 */
	public void addMultipleLibraries(Library[] libraries) {
	    for (int i = 0; i < libraries.length; i++)
		addLibrary(libraries[i], false);

	    // update
	    update();
	}

	/**
	 * Add the new library.
	 *
	 * @param library
	 *            the library
	 * @param update
	 *            Do the update on the list?
	 */
	public void addLibrary(Library library, boolean update) {
	    items.add(library);

	    // --
	    double size = HEIGHT / var;

	    library.imageView.setFitWidth(size);
	    library.imageView.setFitHeight(size);
	    library.setMaxWidth(size);
	    library.setMaxHeight(size);

	    // --
	    library.setOnMouseClicked(m -> {

		if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {

		    // If it isn't the same library again
		    if (((Library) centerGroup.getChildren().get(0)).getPosition() != library.getPosition()) {

			setCenterIndex(library.getPosition());
			// scrollBar.setValue(library.getPosition())
		    }

		} else if (m.getButton() == MouseButton.SECONDARY) {

		    // if isn't the same library again
		    if (((Library) centerGroup.getChildren().get(0)).getPosition() != library.getPosition()) {

			setCenterIndex(library.getPosition());
			// scrollBar.setValue(library.getPosition())

			timeline.setOnFinished(v -> {
			    // Bounds bounds = library.localToScreen(library.getBoundsInLocal());
			    //			    contextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
			    //				    bounds.getMinY() + bounds.getHeight() / 4, library);
			    contextMenu.show(Main.window, m.getScreenX(), m.getScreenY(), library);
			    timeline.setOnFinished(null);
			});

		    } else { // if is the same library again
			//			Bounds bounds = library.localToScreen(library.getBoundsInLocal());
			//			contextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
			//				bounds.getMinY() + bounds.getHeight() / 4, library);
			contextMenu.show(Main.window, m.getScreenX(), m.getScreenY(), library);
		    }
		}

	    });

	    // MAX
	    jfSlider.setMax(items.size() - 1.00);

	    //Update?
	    if (update)
		update();
	}

	/**
	 * Deletes the specific Library from the list
	 * 
	 * @param library
	 *            Library to be deleted
	 * @param commit
	 *            commit the changes to the database
	 */
	public void deleteLibrary(Library library, boolean commit) {
	    items.remove(library);

	    for (int i = 0; i < items.size(); i++)
		items.get(i).updatePosition(i);

	    calculateCenterAfterDelete();

	    if (commit)
		Main.dbManager.commit();
	}

	/**
	 * Recalculate the center index after a delete occurs.
	 */
	private void calculateCenterAfterDelete() {

	    // center index
	    if (!leftGroup.getChildren().isEmpty())
		centerIndex = leftGroup.getChildren().size() - 1;
	    else
		// if (!rightGroup.getChildren().isEmpty())	
		// centerIndex = 0	
		// else
		centerIndex = 0;

	    // Max
	    jfSlider.setMax(items.size() - 1.00);

	    update();

	}

	/**
	 * Sets the center index.
	 *
	 * @param i
	 *            the new center index
	 */
	public void setCenterIndex(int i) {
	    if (centerIndex != i) {
		centerIndex = i;
		update();

		// Update the ScrollBar Value
		jfSlider.setValue(centerIndex);
	    }
	}

	/**
	 * Goes to next Item (RIGHT).
	 */
	public void next() {
	    if (centerIndex + 1 < items.size())
		setCenterIndex(centerIndex + 1);
	}

	/**
	 * Goes to previous item(LEFT).
	 */
	public void previous() {
	    if (centerIndex > 0)
		setCenterIndex(centerIndex - 1);
	}

	/**
	 * Update the library viewer so it shows the center index correctly.
	 */
	public void update() {

	    // Reconstruct Groups
	    leftGroup.getChildren().clear();
	    centerGroup.getChildren().clear();
	    rightGroup.getChildren().clear();

	    if (!items.isEmpty()) {

		// If only on item exists
		if (items.size() == 1) {
		    centerGroup.getChildren().add(items.get(0));
		    centerIndex = 0;
		} else {

		    // LEFT,
		    for (int i = 0; i < centerIndex; i++)
			leftGroup.getChildren().add(items.get(i));

		    // CENTER,
		    if (centerIndex == items.size()) {
			centerGroup.getChildren().add(leftGroup.getChildren().get(centerIndex - 1));
		    } else
			centerGroup.getChildren().add(items.get(centerIndex));

		    // RIGHT
		    for (int i = items.size() - 1; i > centerIndex; i--)
			rightGroup.getChildren().add(items.get(i));

		}

		// stop old time line
		if (timeline.getStatus() == Status.RUNNING)
		    timeline.stop();

		// clear the old keyFrames
		timeline.getKeyFrames().clear();
		final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

		// LEFT KEYFRAMES
		for (int i = 0; i < leftGroup.getChildren().size(); i++) {

		    final Library it = items.get(i);

		    double newX = -leftGroup.getChildren().size() *

			    SPACING + SPACING * i + LEFT_OFFSET;

		    keyFrames.add(new KeyFrame(duration,

			    new KeyValue(it.translateXProperty(), newX, interpolator),

			    new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),

			    new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));

		    // new KeyValue(it.angle, 45.0, INTERPOLATOR)))

		}

		// CENTER ITEM KEYFRAME
		final Library centerItem;
		if (items.size() == 1)
		    centerItem = items.get(0);
		else
		    centerItem = (Library) centerGroup.getChildren().get(0);

		keyFrames.add(new KeyFrame(duration,

			new KeyValue(centerItem.translateXProperty(), 0, interpolator),

			new KeyValue(centerItem.scaleXProperty(), 1.0, interpolator),

			new KeyValue(centerItem.scaleYProperty(), 1.0, interpolator)));// ,

		// new KeyValue(centerItem.rotationTransform.angleProperty(),
		// 360)));

		// new KeyValue(centerItem.angle, 90, INTERPOLATOR)));

		// RIGHT KEYFRAMES
		for (int i = 0; i < rightGroup.getChildren().size(); i++) {

		    final Library it = items.get(items.size() - i - 1);

		    final double newX = rightGroup.getChildren().size() *

			    SPACING - SPACING * i + RIGHT_OFFSET;

		    keyFrames.add(new KeyFrame(duration,

			    new KeyValue(it.translateXProperty(), newX, interpolator),

			    new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),

			    // new
			    // KeyValue(it.rotationTransform.angleProperty(),
			    // -360)));

			    new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));

		    // new KeyValue(it.angle, 135.0, INTERPOLATOR)));

		}

		// play animation
		timeline.setAutoReverse(true);
		timeline.play();
	    }

	    // Previous and Next Visibility
	    if (rightGroup.getChildren().isEmpty())
		getNext().setVisible(false);
	    else
		getNext().setVisible(true);

	    if (leftGroup.getChildren().isEmpty())
		getPrevious().setVisible(false);
	    else
		getPrevious().setVisible(true);

	}

    }

}
