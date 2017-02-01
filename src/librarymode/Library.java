/*
 * 
 */
package librarymode;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * A class which hold all the functionality of a digital library.
 *
 * @author GOXR3PLUS
 */
public class Library extends StackPane {

    /** The image view. */
    @FXML
    private ImageView imageView;

    /** The go settings. */
    @FXML
    private Label goSettings;

    /**
     * Warning if the Library Image is missing
     */
    @FXML
    private Label warningLabel;

    /** The rating label. */
    @FXML
    private JFXBadge ratingLabel;

    /** The name field. */
    @FXML
    private Label nameField;

    /** The selection region. */
    @FXML
    private Region selectionRegion;

    /** The selection box. */
    @FXML
    private JFXCheckBox selectionBox;

    // --------------------------------------------

    /** The controller. */
    // -------------------------
    private final SmartController controller;

    /** The library name. */
    private String libraryName;

    /** The data base table name. */
    private String dataBaseTableName;

    /** The stars. */
    private DoubleProperty stars;

    /** The selected. */
    private BooleanProperty selected;

    /** The date created. */
    private String dateCreated = "";

    /** The time created. */
    private String timeCreated = "";

    /** The description. */
    private String description = "";

    /**
     * // create a rotation transform starting at 0 degrees, rotating about
     * pivot point 0, 0.
     */
    // Rotate rotationTransform = new Rotate(0, 0, 0)

    /**
     * The Save Mode of the Library.
     *
     * @author GOXR3PLUS
     */
    public enum SaveMode {

	/**
	 * Songs are not copied into the database so if they are deleted they
	 * don't exist anymore.
	 */
	ORIGINAL_PATH,

	/** Songs have been copied into the database. */
	DATABASE_PATH;
    }

    /** The save mode. */
    private SaveMode saveMode;

    /** The position. */
    private int position = -89;

    /** The image path. */
    private String imagePath;

    /** * Prepared Statements *. */
    PreparedStatement libUStars;

    /** The lib U description. */
    PreparedStatement libUDescription;

    /** The lib U save mode. */
    // PreparedStatement libUSaveMode;

    /** The lib U position. */
    PreparedStatement libUPosition;

    /** The lib U image. */
    PreparedStatement libUImage;

    /** The lib U rename. */
    PreparedStatement libURename;

    /** The lib U status. */
    PreparedStatement libUStatus;

    /** Define a pseudo class. */
    private static final PseudoClass OPENED_PSEUDO_CLASS = PseudoClass.getPseudoClass("opened");

    /** The opened. */
    private final BooleanProperty opened = new BooleanPropertyBase(false) {
	@Override
	public void invalidated() {
	    pseudoClassStateChanged(OPENED_PSEUDO_CLASS, opened.get());
	}

	@Override
	public Object getBean() {
	    return Library.this;
	}

	@Override
	public String getName() {
	    return "opened";
	}

    };

    /** This variable is used during the creation of a new library. */
    private InvalidationListener renameInvalidator = new InvalidationListener() {
	@Override
	public void invalidated(Observable observable) {

	    // Remove the Listener
	    Main.renameWindow.showingProperty().removeListener(this);

	    // !Showing
	    if (!Main.renameWindow.isShowing()) {

		// old && new -> name
		String oldName = getLibraryName();
		String newName = Main.renameWindow.getUserInput();
		boolean duplicate = false;

		try {

		    // Remove Bindings
		    nameField.textProperty().unbind();

		    // !XPressed && Old name !=newName
		    if (!Main.renameWindow.isXPressed() && !libraryName.equals(newName)) {

			// duplicate?
			if (!(duplicate = Main.libraryMode.libraryViewer.items.stream().anyMatch(
				library -> library != Library.this && library.getLibraryName().equals(newName)))) {

			    // Update SQL Database
			    libURename.setString(1, newName);
			    libURename.setString(2, oldName);
			    libURename.executeUpdate();

			    // Rename library folder
			    new File(InfoTool.user_dbPath_With_Separator + oldName)
				    .renameTo(new File(InfoTool.user_dbPath_With_Separator + newName));

			    // set the new name
			    setLibraryName(newName);
			    nameField.getTooltip().setText(newName);

			    // Rename the image of library
			    if (imagePath != null)
				updateImagePathInDB(Main.dbManager.imagesFolder + File.separator + newName + "."
					+ InfoTool.getFileExtension(imagePath), true, false);

			} else { // duplicate
			    resetTheName();
			    Notifications.create().title("Dublicate Name")
				    .text("Name->" + newName + " is already used from another Library...").darkStyle()
				    .showInformation();
			}
		    } else // X is pressed by user || oldName == newName
			resetTheName();

		} catch (Exception ex) {
		    Main.logger.log(Level.WARNING, "", ex);
		    // etc
		    resetTheName();
		} finally {

		    // Rename Tab + Unbind Tab textProperty
		    if (isLibraryOpened()) {
			if (!Main.renameWindow.isXPressed() && !newName.equals(oldName) && !duplicate)
			    Main.libraryMode.multipleLibs.renameTab(oldName, getLibraryName());

			Main.libraryMode.multipleLibs.getTab(getLibraryName()).getTooltip().textProperty().unbind();
		    }

		    // Security Variable
		    controller.renameWorking = false;

		    // commit
		    if (!Main.renameWindow.isXPressed() && !newName.equals(oldName) && !duplicate)
			Main.dbManager.commit();
		}

	    } // rename window is still showing
	} // invalidated

	/**
	 * Resets the name if the user cancels the rename operation
	 */
	private void resetTheName() {
	    nameField.setText(getLibraryName());
	}
    };

    /**
     * Instantiates a new library.
     *
     * @param libraryName
     *            the library name
     * @param dataBaseTableName
     *            the data base table name
     * @param stars
     *            the stars
     * @param dateCreated
     *            the date created
     * @param timeCreated
     *            the time created
     * @param description
     *            the description
     * @param saveMode
     *            the save mode
     * @param position
     *            the position
     * @param imagePath
     *            the image path
     * @param opened
     *            the opened
     */
    public Library(String libraryName, String dataBaseTableName, double stars, String dateCreated, String timeCreated,
	    String description, int saveMode, int position, String imagePath, boolean opened) {

	// LibraryName
	this.libraryName = libraryName;

	// DataBase TableName
	this.dataBaseTableName = dataBaseTableName;

	// Stars
	setStars(stars);

	// Date Created
	if (dateCreated == null)
	    this.dateCreated = InfoTool.getCurrentDate();
	else
	    this.dateCreated = dateCreated;

	// Hour Created
	if (timeCreated == null)
	    this.timeCreated = InfoTool.getLocalTime();
	else
	    this.timeCreated = timeCreated;

	// Description
	if (description != null)
	    this.description = description;

	// SaveMode
	if (saveMode == 1)
	    this.saveMode = SaveMode.ORIGINAL_PATH;
	else if (saveMode == 2)
	    this.saveMode = SaveMode.DATABASE_PATH;

	// Library Position in List
	this.position = position;

	// LibraryImage
	this.imagePath = imagePath;

	// isOpened
	this.opened.set(opened);

	// ----------------------------------FXMLLoader
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "Library.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	// Add the rotationTransform effect
	// setEffect(rotationTransform)

	// Add the rotation transform
	// rotationTransform.pivotXProperty().bind(this.widthProperty().divide(2));
	// rotationTransform.pivotYProperty().bind(this.heightProperty().divide(2));
	// rotationTransform.setAxis(Rotate.Y_AXIS);
	// getTransforms().add(rotationTransform);

	setOnKeyReleased(this::onKeyReleased);
	setOnMouseEntered(m -> {
	    if (!isFocused())
		requestFocus();
	});

	// Controller
	controller = new SmartController(Genre.LIBRARYSONG, libraryName, dataBaseTableName);

	// --Drag Over
	super.setOnDragOver(dragOver -> {

	    // Source has files?
	    if (dragOver.getDragboard().hasFiles())
		Main.libraryMode.libraryViewer.setCenterIndex(this.getPosition());

	    // The drag must come from source other than the owner
	    if (dragOver.getGestureSource() != controller.tableViewer)
		dragOver.acceptTransferModes(TransferMode.LINK);

	});

	// --Drag Dropped
	super.setOnDragDropped(drop -> {
	    // Has Files? + isFree()?
	    if (drop.getDragboard().hasFiles() && controller.isFree(true))
		controller.inputService.start(drop.getDragboard().getFiles());

	    drop.setDropCompleted(true);
	});

	// -------------Load the FXML
	try {
	    loader.load();
	} catch (IOException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}

	try {

	    // ----------------About the Library---------------------
	    libUStars = Main.dbManager.connection1.prepareStatement("UPDATE LIBRARIES SET STARS=? WHERE NAME=?;");

	    libUDescription = Main.dbManager.connection1
		    .prepareStatement("UPDATE LIBRARIES SET DESCRIPTION=?" + " WHERE NAME=?;");

	    // libUSaveMode = Main.dbManager.connection1
	    // .prepareStatement("UPDATE LIBRARIES SET SAVEMODE=? WHERE NAME=?
	    // ;");

	    libUPosition = Main.dbManager.connection1
		    .prepareStatement("UPDATE LIBRARIES SET POSITION=?  WHERE NAME=?;");

	    libUImage = Main.dbManager.connection1
		    .prepareStatement("UPDATE LIBRARIES SET LIBRARYIMAGE=?  WHERE NAME=?");

	    libUStatus = Main.dbManager.connection1.prepareStatement("UPDATE LIBRARIES SET OPENED=? WHERE NAME=? ;");

	    libURename = Main.dbManager.connection1.prepareStatement("UPDATE LIBRARIES SET NAME=? WHERE NAME=? ;");

	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	} /********************/
    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Methods
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

	// StackView
	// this.maxHeightProperty().bind(Main.libraryMode.libraryViewer.heightProperty().divide(2));
	// this.maxWidthProperty().bind(this.maxHeightProperty());

	// ImageView
	// imageView.fitWidthProperty().bind(this.maxWidthProperty());
	// imageView.fitHeightProperty().bind(this.maxHeightProperty());

	// Clip
	Rectangle rect = new Rectangle();
	rect.widthProperty().bind(this.widthProperty());
	rect.heightProperty().bind(this.heightProperty());
	rect.setArcHeight(30);
	rect.setArcWidth(30);
	rect.setEffect(new Reflection());

	// StackPane -> this
	this.setClip(rect);
	Reflection reflection = new Reflection();
	reflection.setInput(new DropShadow(4, Color.WHITE));
	this.setEffect(reflection);

	// LibraryName
	setLibraryName(libraryName);
	nameField.setText(libraryName);
	nameField.getTooltip().setText(libraryName);

	// Update the Image
	// updateStarLabelImage();

	// Image
	imageView.setImage(getImage());

	// goSettings
	goSettings.setOnMouseReleased(m -> Main.libraryMode.libraryViewer.settings.showWindow(this));

	// Rating Label
	ratingLabel.textProperty().bind(starsProperty().asString());
	ratingLabel.visibleProperty().bind(starsProperty().greaterThan(0));
	this.setOnScroll(scroll -> {
	    if (scroll.getDeltaY() > 0)
		updateStars(starsProperty().get() + 0.5);
	    else
		updateStars(starsProperty().get() - 0.5);
	});

	// selectionRegion
	selectionBox.visibleProperty().bind(selectionRegion.visibleProperty());
	selectedProperty().bind(selectionBox.selectedProperty());

	Label error = new Label();
	error.setOnDragDetected(drag -> {

	    // Main.libraryMode.dragDetected=true;

	    /* Allow copy transfer mode */
	    Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);

	    /* Put a String into the dragBoard */
	    ClipboardContent content = new ClipboardContent();
	    content.putString("#library#" + getLibraryName());

	    /* Set the DragView */
	    WritableImage writableImage = new WritableImage((int) nameField.getWidth(), (int) nameField.getHeight());
	    SnapshotParameters params = new SnapshotParameters();
	    // params.setFill(Color.TRANSPARENT);
	    params.setFill(Color.WHITE);
	    db.setDragView(nameField.snapshot(params, writableImage), writableImage.getWidth() / 2, 0);

	    db.setContent(content);
	    drag.consume();

	});

	error.setOnDragOver(drag -> {
	    // Main.libraryMode.dragDetected=false;

	    Dragboard db = drag.getDragboard();
	    if (db.hasString() && db.getString().contains("#library#"))
		drag.acceptTransferModes(TransferMode.COPY);

	    drag.consume();
	});

    }

    /**
     * Change the state of the Library from Normal to Selection Mode.
     *
     * @param way
     *            the way
     */
    public void goOnSelectionMode(boolean way) {
	if (way)
	    selectionRegion.setVisible(true);
	else
	    selectionRegion.setVisible(false);
    }

    /**
     * Update the Stars of the Library.
     *
     * @param stars
     *            the stars
     */
    public void updateStars(double stars) {
	try {
	    // An acceptable value has been given
	    if (setStars(stars)) {
		// SQLITE COMMIT
		libUStars.setDouble(1, stars);
		libUStars.setString(2, getLibraryName());
		libUStars.executeUpdate();
		Main.dbManager.commit();
	    }
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Stores the Library description into the database.
     */
    public void updateDescription() {
	try {

	    // SQLITE
	    libUDescription.setString(1, description);
	    libUDescription.setString(2, getLibraryName());
	    libUDescription.executeUpdate();
	    Main.dbManager.commit();

	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Make an update only if the library is in information mode.
     */
    public void updateSettingsTotalLabel() {
	Main.libraryMode.libraryViewer.settings.updateTotalItemsLabel(this);
    }

    /**
     * Updates the position variable of Library in database so the next time
     * viewer position it correct.
     *
     * @param newPosition
     *            The new position of the Library
     */
    public void updatePosition(int newPosition) {
	try {
	    position = newPosition;

	    // SQLITE
	    libUPosition.setInt(1, newPosition);
	    libUPosition.setString(2, getLibraryName());
	    libUPosition.executeUpdate();
	} catch (SQLException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Updates the Image File.
     *
     * @param newPath
     *            the new path
     * @param renameUpdate
     *            the rename update
     * @param commit
     *            the commit
     */
    private boolean updateImagePathInDB(String newPath, boolean renameUpdate, boolean commit) {
	boolean success = true;

	try {

	    if (renameUpdate && imagePath != null) { // rename the old image

		success = new File(imagePath).renameTo(new File(newPath));
		imagePath = newPath;

	    } else { // Create new Image

		// Delete the [[old]] image if exist
		if (imagePath != null)
		    success = new File(imagePath).delete();

		// Create the new image
		imagePath = Main.dbManager.imagesFolder + File.separator + getLibraryName() + "."
			+ InfoTool.getFileExtension(newPath);

		ActionTool.copy(newPath, imagePath);

	    }

	    // SQLITE
	    libUImage.setString(1, imagePath);
	    libUImage.setString(2, getLibraryName());
	    libUImage.executeUpdate();
	    if (commit)
		Main.dbManager.commit();

	} catch (SQLException ex) {
	    success = false;
	    Main.logger.log(Level.WARNING, "", ex);
	}

	return success;
    }

    /**
     * Gives to Library the default image that i had set on resources.
     */
    public void setDefaultImage() {

	try {

	    // Delete the old image if exist
	    if (imagePath != null)
		new File(imagePath).delete();
	    imagePath = null;

	    // Set the default
	    imageView.setImage(getImage());

	    // SQLITE
	    libUImage.setString(1, imagePath);
	    libUImage.setString(2, getLibraryName());
	    libUImage.executeUpdate();
	    Main.dbManager.commit();

	} catch (Exception ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * The user has the ability to change the Library Image
     *
     */
    public void setNewImage() {

	File file = Main.specialChooser.prepareToSelectImage(Main.window);
	if (file != null) {
	    Image image = new Image(file.toURI().toString());
	    if (image.getWidth() <= 4800 && image.getHeight() <= 4800) {
		updateImagePathInDB(file.getAbsolutePath(), false, true);
		imageView.setImage(getImage());
	    } else
		ActionTool.showNotification("Warning",
			"Maximum Size Allowed 4800*4800 \n Current is:" + image.getWidth() + "*" + image.getHeight(),
			Duration.millis(1500), NotificationType.WARNING);
	}
    }

    /**
     * Export the Library image.
     */
    public void exportImage() {
	if (imagePath != null) {
	    File file = Main.specialChooser.prepareToExportImage(Main.window, imagePath);
	    if (file != null)
		new Thread(() -> ActionTool.copy(imagePath, file.getAbsolutePath())).start();
	}
    }

    /**
     * Set or not the libraryOpened.
     *
     * @param way
     *            the way
     * @param commit
     *            the commit
     */
    private void setLibraryOpened(boolean way, boolean commit) {

	try {
	    opened.set(way);
	    libUStatus.setBoolean(1, way);
	    libUStatus.setString(2, getLibraryName());
	    libUStatus.executeUpdate();
	    if (commit)
		Main.dbManager.commit();
	} catch (SQLException sql) {
	    sql.printStackTrace();
	}
    }

    /**
     * Renames the current Library.
     */
    public void renameLibrary() {
	if (controller.isFree(true)) {

	    // Security Variable
	    controller.renameWorking = true;

	    // Open the Window
	    Main.renameWindow.show(getLibraryName(), this);

	    // Bind 1
	    Tab tab = Main.libraryMode.multipleLibs.getTab(getLibraryName());
	    if (tab != null)
		tab.getTooltip().textProperty().bind(nameField.textProperty());

	    // Bind 2
	    nameField.textProperty().bind(Main.renameWindow.inputField.textProperty());

	    Main.renameWindow.showingProperty().addListener(renameInvalidator);
	}
    }

    /**
     * Updates the LibraryStars.
     */
    protected void updateLibraryStars() {
	if (controller.isFree(true)) {

	    // Bind
	    Main.libraryMode.libraryViewer.settings.getStarsLabel().textProperty()
		    .bind(Main.starWindow.starsProperty().asString());

	    Main.starWindow.show(starsProperty().get(), Main.libraryMode.libraryViewer.settings.getStarsLabel());
	    stars.bind(Main.starWindow.starsProperty());

	    Main.starWindow.window.showingProperty().addListener(new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {

		    // Remove the listener
		    Main.starWindow.window.showingProperty().removeListener(this);

		    // Remove Binding from Stars
		    stars.unbind();

		    // if !showing
		    if (!Main.starWindow.window.isShowing()) {
			if (Main.starWindow.wasAccepted())
			    updateStars(Main.starWindow.getStars());
			Main.libraryMode.libraryViewer.settings.getStarsLabel().textProperty().unbind();
		    }

		}
	    });
	}

    }

    /**
     * Delete the library.
     */
    public void deleteLibrary() {
	if (controller.isFree(true) && ActionTool
		.doQuestion("Confirm that you want to 'delete' this library,\n Name: " + getLibraryName())) {

	    try {

		// Drop the database table
		Main.dbManager.connection1.createStatement().execute("DROP TABLE '" + getDataBaseTableName() + "' ");

		// Delete the row from Libraries table
		Main.dbManager.connection1.createStatement()
			.executeUpdate("DELETE FROM LIBRARIES WHERE NAME='" + getLibraryName() + "' ");

		// Delete the folder with library name in database
		ActionTool.deleteFile(new File(InfoTool.dbPath_With_Separator + getLibraryName()));

		// delete library image
		if (imagePath != null)
		    new File(imagePath).delete();

		// opened? Yes=remove the tab
		if (isLibraryOpened())
		    Main.libraryMode.multipleLibs.removeTab(getLibraryName());

		// Update the libraryViewer
		Main.libraryMode.libraryViewer.items.remove(this);
		Main.libraryMode.libraryViewer.calculateCenterAfterDelete();

		// Update the libraries positions
		Main.libraryMode.libraryViewer.updateLibrariesPositions(false);

		// Commit
		Main.dbManager.commit();

	    } catch (SQLException sql) {
		Main.logger.log(Level.WARNING, "\n", sql);
	    }

	}
    }

    /**
     * Opens the Library.
     *
     * @param open
     *            the open
     * @param firstLoadHack
     *            the first load hack
     */
    public void libraryOpenClose(boolean open, boolean firstLoadHack) {
	if (!firstLoadHack) {
	    // Open
	    if (open && !isLibraryOpened()) {
		setLibraryOpened(open, true);
		Main.libraryMode.multipleLibs.insertTab(this);
		// Close
	    } else if (!open && isLibraryOpened() && controller.isFree(true)) {
		setLibraryOpened(open, true);
		Main.libraryMode.multipleLibs.removeTab(getLibraryName());
	    }
	    // Open Hacked to not commit
	} else {
	    setLibraryOpened(open, false);
	    Main.libraryMode.multipleLibs.insertTab(this);
	}
    }

    /*------------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Setters
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Set the Library new name.
     *
     * @param newName
     *            the new library name
     */
    private void setLibraryName(String newName) {
	libraryName = newName;
	controller.setName(newName);
    }

    /**
     * Set the stars of the library.
     *
     * @param stars
     *            the new stars
     */
    private final boolean setStars(double stars) {
	if (stars >= 0.0 && stars <= 5.0) {
	    starsProperty().set(stars);
	    return true;
	} else
	    return false;
    }

    /**
     * Set if the library is selected or not.
     *
     * @param selected
     *            the new selected
     */
    public void setSelected(boolean selected) {
	selectedProperty().set(selected);
    }

    /**
     * Set the new Description of the Library.
     *
     * @param newDescription
     *            the new description
     */
    public void setDescription(String newDescription) {
	description = newDescription;
    }

    /*------------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Properties
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Stars property.
     *
     * @return The Stars Property
     */
    public DoubleProperty starsProperty() {
	if (stars == null)
	    stars = new SimpleDoubleProperty(this, "stars", 0);

	return stars;
    }

    /**
     * Selected property.
     *
     * @return The Selected Property
     */
    public BooleanProperty selectedProperty() {
	if (selected == null)
	    selected = new SimpleBooleanProperty(this, "selected", false);

	return selected;
    }

    /*------------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Getters
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Checks if is library opened.
     *
     * @return <b> True </b> If the Library is Opened or <b> False </b> if not.
     */
    public boolean isLibraryOpened() {
	return opened.get();
    }

    /**
     * Gets the library name.
     *
     * @return The full Library Name
     */
    public String getLibraryName() {
	return libraryName;
    }

    /**
     * Gets the stars.
     *
     * @return The stars of the library
     */
    public double getStars() {
	return starsProperty().get();
    }

    /**
     * Returns <b>DATABASE TABLE NAME</b> of the List.
     *
     * @return The DataBase table name of this Library
     */
    public String getDataBaseTableName() {
	return dataBaseTableName;
    }

    /**
     * The position of library into the viewer.
     *
     * @return the position of library
     */
    public int getPosition() {
	return position;
    }

    /**
     * Look SaveMode enum description.
     *
     * @return the library SaveMode
     */
    public SaveMode getSaveMode() {
	return saveMode;
    }

    // ---------!!!!!!!!!!!!!Θέλει διόρθωση για να προηδοποιεί τον χρήστη εάν
    // μία εικόνα λείπει!!!!!!!!!!!! -----------------
    /**
     * Gets the image.
     *
     * @return The image of the Library
     */
    public Image getImage() {
	if (imagePath != null) {
	    if (new File(imagePath).exists()) {

		// Hide warning Label
		warningLabel.setVisible(false);

		// return the image
		return new Image(new File(imagePath).toURI().toString());

	    } else {

		// Show warning Label
		warningLabel.setVisible(true);

		return null;
	    }
	} else {

	    // Show warning Label
	    warningLabel.setVisible(false);

	    return LibraryMode.defaultImage;
	}
    }

    /**
     * Returns the imageView of the Library
     * 
     * @return The imageView of the Library
     */
    public ImageView getImageView() {
	return imageView;
    }

    /**
     * Gets the total entries.
     *
     * @return The total entries in the database table
     */
    public int getTotalEntries() {
	return controller.getTotalInDataBase();
    }

    /**
     * Gets the image path.
     *
     * @return The absolute path of the Library Image in the operating system
     */
    public String getImagePath() {
	return imagePath;
    }

    /**
     * Gets the date created.
     *
     * @return The data that Library was created
     */
    public String getDateCreated() {
	return dateCreated;
    }

    /**
     * Gets the time created.
     *
     * @return The time that Library was created
     */
    public String getTimeCreated() {
	return timeCreated;
    }

    /**
     * Gets the description.
     *
     * @return The description of the Library
     */
    public String getDescription() {
	return description;
    }

    /**
     * Gets the smart controller.
     *
     * @return The smart controller of the Library
     */
    public SmartController getSmartController() {
	return controller;
    }

    /*------------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							Events
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * This method is called when a key is released.
     *
     * @param key
     *            An event which indicates that a keystroke occurred in a
     *            javafx.scene.Node.
     */
    public void onKeyReleased(KeyEvent key) {
	if (!Main.libraryMode.libraryViewer.settings.isCommentsAreaFocused()
		&& getPosition() == Main.libraryMode.libraryViewer.centerIndex) {

	    KeyCode code = key.getCode();
	    if (code == KeyCode.O)
		libraryOpenClose(true, false);
	    else if (code == KeyCode.C)
		libraryOpenClose(false, false);
	    else if (code == KeyCode.R)
		renameLibrary();
	    else if (code == KeyCode.DELETE || code == KeyCode.D)
		deleteLibrary();
	    else if (code == KeyCode.S)
		Main.libraryMode.libraryViewer.settings.showWindow(this);
	    else if (code == KeyCode.E)
		this.exportImage();

	}
    }

    /*------------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							RUBBISH CODE
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    // private final double RADIUS_H = LibraryViewer.WIDTH / 2;

    // private final double BACK = LibraryViewer.WIDTH / 10;

    // protected PerspectiveTransform transform = new PerspectiveTransform();

    // /** Angle Property */
    // protected final DoubleProperty angle = new SimpleDoubleProperty(45) {
    //
    // @Override
    // protected void invalidated() {
    //
    // // when angle changes calculate new transform
    //
    // double lx = (RADIUS_H - Math.sin(Math.toRadians(angle.get())) * RADIUS_H
    // - 1);
    //
    // double rx = (RADIUS_H + Math.sin(Math.toRadians(angle.get())) * RADIUS_H
    // + 1);
    //
    // double uly = (-Math.cos(Math.toRadians(angle.get())) * BACK);
    //
    // double ury = -uly;
    //
    // // Upper Left corner
    // transform.setUlx(lx);
    // transform.setUly(uly);
    //
    // // Upper Right corner
    // transform.setUrx(rx);
    // transform.setUry(ury);
    //
    // // Lower Right corner
    // transform.setLrx(rx);
    // transform.setLry(LibraryViewer.HEIGHT + uly);
    //
    // // Lower Left corner
    // transform.setLlx(lx);
    // transform.setLly(LibraryViewer.HEIGHT + ury);
    //
    // }
    //
    // };

}
