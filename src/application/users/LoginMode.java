/**
 * 
 */
package application.users;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * @author GOXR3PLUS
 *
 */
public class LoginMode extends BorderPane {

    @FXML
    private BorderPane borderPane;

    @FXML
    private GridPane topGrid;

    @FXML
    private JFXToggleButton selectionModeToggle;

    @FXML
    private JFXButton previous;

    @FXML
    private JFXButton createUser;

    @FXML
    private JFXButton next;

    @FXML
    private HBox botttomHBox;

    @FXML
    private ToolBar userToolBar;

    @FXML
    private Button deleteUser;

    @FXML
    private Button renameUser;

    @FXML
    private Button openUserContextMenu;

    @FXML
    private StackPane usersStackView;

    @FXML
    private Button newUser;

    @FXML
    private Label usersInfoLabel;

    @FXML
    private Button loginButton;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label createdByLabel;

    @FXML
    private Label checkTutorialsLabel;

    @FXML
    private Label downloadsLabel;

    @FXML
    private Label xr3PlayerLabel;

    @FXML
    private Button minimize;

    @FXML
    private Button maxOrNormalize;

    @FXML
    private Button exitApplication;

    @FXML
    private Button changeBackground;

    // --------------------------------------------

    /** The logger for this class */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Allows to see the users in a beatiful way
     */
    public UsersViewer teamViewer = new UsersViewer();

    /**
     * The Search Box of the LoginMode
     */
    public UserSearchBox userSearchBox = new UserSearchBox();

    /** The context menu of the users */
    public UserContextMenu userContextMenu = new UserContextMenu(this);

    /**
     * Loads all the information about each user
     */
    public UsersInfoLoader usersInfoLoader = new UsersInfoLoader();

    //-----

    /** This InvalidationListener is used during the creation of a new user. */
    private final InvalidationListener creationInvalidator = new InvalidationListener() {
	@Override
	public void invalidated(Observable observable) {

	    // Remove the Listener
	    Main.renameWindow.showingProperty().removeListener(this);

	    // !Showing && !XPressed
	    if (!Main.renameWindow.isShowing() && Main.renameWindow.wasAccepted()) {

		Main.window.requestFocus();

		// Check if this name already exists
		String newName = Main.renameWindow.getUserInput();

		// if can pass
		if (!teamViewer.itemsObservableList.stream().anyMatch(user -> user.getUserName().equalsIgnoreCase(newName))) {

		    if (new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + newName).mkdir())
			teamViewer.addUser(new User(newName, teamViewer.itemsObservableList.size(), LoginMode.this), true);
		    else
			ActionTool.showNotification("Error", "An error occured trying to create a new user", Duration.seconds(2),
				NotificationType.ERROR);

		    // update the positions
		    //updateUsersPosition()
		} else {
		    ActionTool.showNotification("Dublicate User", "Name->" + newName + " is already used from another User...", Duration.millis(2000),
			    NotificationType.INFORMATION);
		}
	    }
	}
    };

    /**
     * Constructor
     */
    public LoginMode() {

	// ----------------------------------FXMLLoader-------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "LoginScreenController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	// -------------Load the FXML-------------------------------
	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.WARNING, "", ex);
	}

    }

    /**
     * Called as soon as FXML file has been loaded
     */
    @FXML
    private void initialize() {

	//Set PieChartData
	pieChart.setData(pieChartData);

	//Below is some coded i used with javafxsvg Library to Display SVG images although i changed it 
	//to using WebView due to the cost of many external libraries
	//	HttpURLConnection httpcon = (HttpURLConnection) new URL(
	//		    "https://img.shields.io/sourceforge/dt/xr3player.svg").openConnection();
	//	    httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
	//	    ImageView imageView = new ImageView(new Image(httpcon.getInputStream()));

	//downloadsLabel
	((WebView) downloadsLabel.getGraphic()).getEngine().getLoadWorker().exceptionProperty().addListener(error -> {
	    downloadsLabel.setPrefSize(0, 0);
	    downloadsLabel.setMaxSize(0, 0);
	    downloadsLabel.setMinSize(0, 0);
	    ((WebView) downloadsLabel.getGraphic()).setPrefHeight(0);
	});
	((WebView) downloadsLabel.getGraphic()).getEngine().load("https://img.shields.io/sourceforge/dt/xr3player.svg");

	//super
	//setStyle(
	//	"-fx-background-color:rgb(0,0,0,0.9); -fx-background-size:100% 100%; -fx-background-image:url('file:C://Users//GOXR3PLUS//Desktop//sea.jpg'); -fx-background-position: center center; -fx-background-repeat:stretch;");

	// -- libraryToolBar
	userToolBar.disableProperty().bind(teamViewer.centerItemProperty().isNull());

	// -- botttomHBox
	botttomHBox.getChildren().add(userSearchBox);

	// createLibrary
	createUser.setOnAction(a -> createNewUser(createUser));

	//newUser
	newUser.setOnAction(a -> createNewUser(createUser));
	newUser.visibleProperty().bind(Bindings.size(teamViewer.itemsObservableList).isEqualTo(0));

	//loginButton
	loginButton.setOnAction(a -> Main.startAppWithUser(teamViewer.getSelectedItem()));
	loginButton.disableProperty().bind(userToolBar.disabledProperty());

	//openUserContextMenu
	openUserContextMenu.setOnAction(a -> {
	    User user = teamViewer.getSelectedItem();
	    Bounds bounds = user.localToScreen(user.getBoundsInLocal());
	    userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3, bounds.getMinY() + bounds.getHeight() / 4, user);
	});

	//renameUser
	//renameUser.disableProperty().bind(deleteUser.disabledProperty())
	renameUser.setOnAction(a -> teamViewer.getSelectedItem().renameUser(renameUser));

	//deleteUser
	//deleteUser.disableProperty().bind(newUser.visibleProperty())
	deleteUser.setOnAction(a -> deleteUser(deleteUser));

	// minimize
	minimize.setOnAction(ac -> Main.window.setIconified(true));

	// maximize_normalize
	maxOrNormalize.setOnAction(ac -> Main.scene.maximizeStage());

	//exitButton
	exitApplication.setOnAction(a -> Main.exitQuestion());

	//changeBackground
	changeBackground.setOnAction(a -> Main.changeBackgroundImage());

	// previous
	previous.setOnAction(a -> teamViewer.previous());

	// next
	next.setOnAction(a -> teamViewer.next());

	//Continue
	usersStackView.getChildren().add(teamViewer);
	teamViewer.toBack();

	//createdByLabel
	createdByLabel.setOnMouseReleased(r -> ActionTool.openWebSite(InfoTool.WEBSITE));

	//checkTutorialsLabel
	checkTutorialsLabel.setOnMouseReleased(r -> ActionTool.openWebSite(InfoTool.TUTORIALS));

	//----usersInfoLabel
	usersInfoLabel.textProperty().bind(Bindings.concat("[ ", teamViewer.itemsWrapperProperty().sizeProperty(), " ] Users"));
    }

    /**
     * Used to create a new User
     * 
     * @param owner
     */
    public void createNewUser(Node owner) {
	if (Main.renameWindow.isShowing())
	    return;

	// Open rename window
	Main.renameWindow.show("", owner, "Creating new User");

	// Add the showing listener
	Main.renameWindow.showingProperty().addListener(creationInvalidator);

    }

    /**
     * Used to delete a User
     */
    public void deleteUser(Node owner) {
	//Ask
	if (ActionTool.doQuestion("Confirm that you want to 'delete' this user ,\n Name: [ " + teamViewer.getSelectedItem().getUserName() + " ]",
		owner)) {

	    //Try to delete it
	    if (ActionTool.deleteFile(new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + teamViewer.getSelectedItem().getUserName())))
		teamViewer.deleteUser(teamViewer.getSelectedItem());
	    else
		ActionTool.showNotification("Error", "An error occured trying to delete the user", Duration.seconds(2), NotificationType.ERROR);
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

    /**
     * @return the xr3PlayerLabel
     */
    public Label getXr3PlayerLabel() {
	return xr3PlayerLabel;
    }

    /**
     * @param xr3PlayerLabel
     *            the xr3PlayerLabel to set
     */
    public void setXr3PlayerLabel(Label xr3PlayerLabel) {
	this.xr3PlayerLabel = xr3PlayerLabel;
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
     * This class allows you to view items
     *
     * @author GOXR3PLUS
     */
    public class UsersViewer extends Region {

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
	private ObservableList<User> itemsObservableList = FXCollections.observableArrayList();
	/**
	 * This class wraps an ObservableList
	 */
	private SimpleListProperty<User> itemsWrapperProperty = new SimpleListProperty<>(itemsObservableList);

	/**
	 * Holds the center item of TeamViewer
	 */
	private ObjectProperty<User> centerItemProperty = new SimpleObjectProperty<>(null);

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
	private ScrollBar jfSlider = new ScrollBar();

	/** The time line */
	private Timeline timeline = new Timeline();

	Rectangle clip = new Rectangle();

	/**
	 * Instantiates a new libraries viewer.
	 */
	// Constructor
	public UsersViewer() {

	    setOnScroll(scroll -> {
		if (scroll.getDeltaX() < 0)
		    next();
		else if (scroll.getDeltaX() > 0)
		    previous();
	    });

	    // --- Mouse Listeners
	    setOnMouseEntered(m -> {
		if (!isFocused())
		    requestFocus();
	    });

	    // -- KeyListeners
	    //	    setOnKeyReleased(key -> {
	    //		if (key.getCode() == KeyCode.RIGHT)
	    //		    next();
	    //		else if (key.getCode() == KeyCode.LEFT)
	    //		    previous();
	    //	    });

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
	    // jfSlider.setStyle("-fx-text-fill:white; -fx-background-color:black; -fx-border-color:red");
	    // jfSlider.setIndicatorPosition(IndicatorPosition.LEFT);
	    jfSlider.setCursor(Cursor.HAND);
	    jfSlider.setMin(0);
	    jfSlider.setMax(0);
	    jfSlider.visibleProperty().bind(itemsWrapperProperty.sizeProperty().greaterThan(2));
	    // scrollBar.setVisibleAmount(1)
	    // scrollBar.setUnitIncrement(1)
	    // scrollBar.setBlockIncrement(1)
	    // jfSlider.setShowTickLabels(true);
	    // scrollBar.setMajorTickUnit(1)
	    // jfSlider.setShowTickMarks(true);
	    jfSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
		int newVal = (int) Math.round(newValue.doubleValue());
		int oldVal = (int) Math.round(oldValue.doubleValue());
		// new!=old
		if (newVal != oldVal) {
		    setCenterIndex(newVal);
		}

		// System.out.println(scrollBar.getValue())
	    });

	    jfSlider.setOrientation(Orientation.HORIZONTAL);

	    // create content
	    centered.getChildren().addAll(leftGroup, rightGroup, centerGroup);

	    getChildren().addAll(centered, jfSlider);
	}

	/**
	 * The Collection that holds all the Library Viewer Items
	 * 
	 * @return The Collection that holds all the Libraries
	 */
	public ObservableList<User> getItemsObservableList() {
	    return itemsObservableList;
	}

	/**
	 * This class wraps an ObservableList
	 *
	 * @return the itemsWrapperProperty
	 */
	public SimpleListProperty<User> itemsWrapperProperty() {
	    return itemsWrapperProperty;
	}

	/**
	 * @return the centerItem
	 */
	public ObjectProperty<User> centerItemProperty() {
	    return centerItemProperty;
	}

	/**
	 * Returns the Index of the List center Item
	 * 
	 * @return Returns the Index of the List center Item
	 */
	public int getCenterIndex() {
	    return centerIndex;
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

	    jfSlider.setLayoutX(getWidth() / 2 - 150);
	    //jfSlider.setLayoutX(0);
	    //jfSlider.setLayoutY(double g snoopy dogg);
	    //jfSlider.resize(getWidth(), 15);
	    jfSlider.resize(300, 15);
	    jfSlider.setLayoutY(getHeight() - jfSlider.getHeight());

	    // AVOID DOING CALCULATIONS WHEN THE CLIP SIZE IS THE SAME
	    // if (previousWidth != (int) WIDTH ||
	    if (previousHeight != (int) HEIGHT) {
		// System.out.println("Updating Library Size")

		// Update ImageView width and height
		SPACING = HEIGHT / (var + 0.5);
		LEFT_OFFSET = -(SPACING - 10);
		RIGHT_OFFSET = -LEFT_OFFSET;
		// For-Each
		itemsObservableList.forEach(user -> {
		    double size = HEIGHT / var;

		    // --
		    user.getImageView().setFitWidth(size);
		    user.getImageView().setFitHeight(size);
		    user.setMaxWidth(size);
		    user.setMaxHeight(size);
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
	 * @return The selected item from the List (That means the center index)
	 */
	public User getSelectedItem() {
	    return itemsObservableList.get(centerIndex);
	}

	//	/**
	//	 * Go on selection mode.
	//	 *
	//	 * @param way
	//	 *            the way
	//	 */
	//	public void goOnSelectionMode(boolean way) {
	//	    for (Library library : items)
	//		library.goOnSelectionMode(way);
	//	}

	/**
	 * Add multiple users at once.
	 *
	 * @param list
	 *            The List with the users to be added
	 */
	public void addMultipleUsers(List<User> list) {
	    list.forEach(user -> this.addUser(user, false));

	    // update
	    update();
	}

	/**
	 * Add the new library.
	 *
	 * @param user
	 *            The User to be added
	 * @param update
	 *            Do the update on the list?
	 */
	public void addUser(User user, boolean update) {
	    itemsObservableList.add(user);

	    // --
	    double size = HEIGHT / var;

	    user.getImageView().setFitWidth(size);
	    user.getImageView().setFitHeight(size);
	    user.setMaxWidth(size);
	    user.setMaxHeight(size);

	    // --
	    user.setOnMouseClicked(m -> {

		if (m.getButton() == MouseButton.PRIMARY || m.getButton() == MouseButton.MIDDLE) {

		    // If it isn't the same User again
		    if (((User) centerGroup.getChildren().get(0)).getPosition() != user.getPosition()) {

			setCenterIndex(user.getPosition());
			// scrollBar.setValue(library.getPosition())
		    }

		} else if (m.getButton() == MouseButton.SECONDARY) {

		    // if isn't the same User again
		    if (((User) centerGroup.getChildren().get(0)).getPosition() != user.getPosition()) {

			setCenterIndex(user.getPosition());
			// scrollBar.setValue(library.getPosition())

			timeline.setOnFinished(v -> {
			    Bounds bounds = user.localToScreen(user.getBoundsInLocal());
			    userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3, bounds.getMinY() + bounds.getHeight() / 4,
				    user);
			    timeline.setOnFinished(null);
			});

		    } else { // if is the same User again
			Bounds bounds = user.localToScreen(user.getBoundsInLocal());
			userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3, bounds.getMinY() + bounds.getHeight() / 4, user);
		    }
		}

	    });

	    // MAX
	    jfSlider.setMax(itemsObservableList.size() - 1.00);

	    //Update?
	    if (update)
		update();
	}

	//	/**
	//	 * Recalculate the position of all the libraries.
	//	 *
	//	 * @param commit
	//	 *            the commit
	//	 */
	//	public void updateLibrariesPositions(boolean commit) {
	//
	//	    for (int i = 0; i < items.size(); i++)
	//		items.get(i).updatePosition(i);
	//
	//	    if (commit)
	//		Main.dbManager.commit();
	//	}

	/**
	 * Deletes the specific user from the list
	 * 
	 * @param user
	 *            User to be deleted
	 */
	public void deleteUser(User user) {
	    itemsObservableList.remove(user);

	    for (int i = 0; i < itemsObservableList.size(); i++)
		itemsObservableList.get(i).setPosition(i);

	    calculateCenterAfterDelete();
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
	    jfSlider.setMax(itemsObservableList.size() - 1.00);

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
	    if (centerIndex + 1 < itemsObservableList.size())
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

	    if (!itemsObservableList.isEmpty()) {

		// If only on item exists
		if (itemsObservableList.size() == 1) {
		    centerGroup.getChildren().add(itemsObservableList.get(0));
		    centerIndex = 0;
		} else {

		    // LEFT,
		    for (int i = 0; i < centerIndex; i++)
			leftGroup.getChildren().add(itemsObservableList.get(i));

		    // CENTER,
		    if (centerIndex == itemsObservableList.size()) {
			centerGroup.getChildren().add(leftGroup.getChildren().get(centerIndex - 1));
		    } else
			centerGroup.getChildren().add(itemsObservableList.get(centerIndex));

		    // RIGHT
		    for (int i = itemsObservableList.size() - 1; i > centerIndex; i--)
			rightGroup.getChildren().add(itemsObservableList.get(i));

		}

		// stop old time line
		if (timeline.getStatus() == Status.RUNNING)
		    timeline.stop();

		// clear the old keyFrames
		timeline.getKeyFrames().clear();
		final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

		// LEFT KEYFRAMES
		for (int i = 0; i < leftGroup.getChildren().size(); i++) {

		    final User it = itemsObservableList.get(i);

		    double newX = -leftGroup.getChildren().size() *

			    SPACING + SPACING * i + LEFT_OFFSET;

		    keyFrames.add(new KeyFrame(duration,

			    new KeyValue(it.translateXProperty(), newX, interpolator),

			    new KeyValue(it.scaleXProperty(), SCALE_SMALL, interpolator),

			    new KeyValue(it.scaleYProperty(), SCALE_SMALL, interpolator)));

		    // new KeyValue(it.angle, 45.0, INTERPOLATOR)))

		}

		// CENTER ITEM KEYFRAME
		final User centerItem;
		if (itemsObservableList.size() == 1)
		    centerItem = itemsObservableList.get(0);
		else
		    centerItem = (User) centerGroup.getChildren().get(0);

		//The Property Center Item
		this.centerItemProperty.set(centerItem);

		keyFrames.add(new KeyFrame(duration,

			new KeyValue(centerItem.translateXProperty(), 0, interpolator),

			new KeyValue(centerItem.scaleXProperty(), 1.0, interpolator),

			new KeyValue(centerItem.scaleYProperty(), 1.0, interpolator)));// ,

		// new KeyValue(centerItem.rotationTransform.angleProperty(),
		// 360)));

		// new KeyValue(centerItem.angle, 90, INTERPOLATOR)));

		// RIGHT KEYFRAMES
		for (int i = 0; i < rightGroup.getChildren().size(); i++) {

		    final User it = itemsObservableList.get(itemsObservableList.size() - i - 1);

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
	    } else
		//The Property Center Item
		this.centerItemProperty.set(null);

	    // Previous and Next Visibility
	    getNext().setVisible(!rightGroup.getChildren().isEmpty());
	    getPrevious().setVisible(!leftGroup.getChildren().isEmpty());

	}

	/**
	 * @return The Timeline
	 */
	public Animation getTimeline() {
	    return timeline;
	}

    }

    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    /**
     * @author GOXR3PLUS
     *
     */
    public class UsersInfoLoader extends Service<Boolean> {

	/**
	 * Constructor
	 */
	public UsersInfoLoader() {
	    this.setOnSucceeded(s -> done());
	    this.setOnFailed(f -> done());
	    this.setOnCancelled(c -> done());
	}

	@Override
	public void start() {
	    if (isRunning())
		return;

	    //Bindings
	    Main.updateScreen.setVisible(true);
	    Main.updateScreen.getProgressBar().progressProperty().bind(progressProperty());
	    Main.updateScreen.getLabel().setText("---Loadings Users Information---");

	    //Start
	    super.start();
	}

	/**
	 * Called when Service is done
	 */
	private void done() {
	    //Bindings
	    Main.updateScreen.getProgressBar().progressProperty().unbind();
	    Main.updateScreen.setVisible(false);
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Boolean> createTask() {
	    return new Task<Boolean>() {
		@Override
		protected Boolean call() throws Exception {
		    //Variables
		    int[] counter = { 0 };
		    int totalUsers = LoginMode.this.teamViewer.getItemsObservableList().size();

		    // -- For every user
		    LoginMode.this.teamViewer.getItemsObservableList().forEach(user -> {

			//Check if the database of this user exists
			String dbFileAbsolutePath = InfoTool.getAbsoluteDatabasePathWithSeparator() + user.getUserName() + File.separator
				+ "dbFile.db";
			if (new File(dbFileAbsolutePath).exists()) {

			    // --Create connection and load user information
			    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileAbsolutePath);
				    ResultSet dbCounter = connection.createStatement().executeQuery("SELECT COUNT(*) FROM LIBRARIES;");) {

				int[] totalLibraries = { 0 };
				totalLibraries[0] += dbCounter.getInt(1);
				Thread.sleep(500);

				// Refresh the text
				Platform.runLater(() -> {

				    //Add Pie Chart Data
				    pieChartData.add(new PieChart.Data(InfoTool.getMinString(user.getUserName(), 4), totalLibraries[0]));

				    //Update User Label
				    user.getTotalLibrariesLabel().setText(Integer.toString(totalLibraries[0]));

				});
				//System.out.println("User:" + user.getUserName() + " contains : " + totalLibraries + " Libraries");

				updateProgress(++counter[0], totalUsers);
			    } catch (Exception ex) {
				Main.logger.log(Level.SEVERE, "", ex);
			    }
			}

		    });

		    return true;
		}
	    };
	}

    }

}
