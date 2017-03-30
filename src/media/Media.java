/*
 * 
 */
package media;

import java.io.File;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.Notifications;

import application.Main;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.util.Duration;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import xplayer.presenter.AudioType;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public abstract class Media {

    /** The title. */
    protected SimpleStringProperty title;

    /** The media type. */
    private SimpleObjectProperty<ImageView> mediaType;

    /** The has been played. */
    private SimpleObjectProperty<ImageView> hasBeenPlayed;

    /** The duration edited. */
    private SimpleStringProperty durationEdited;

    /** The duration. */
    private SimpleIntegerProperty duration;

    /** The times played. */
    private SimpleIntegerProperty timesPlayed;

    /** The stars. */
    private SimpleDoubleProperty stars;

    /** The hour imported. */
    private SimpleStringProperty hourImported;

    /** The date imported. */
    private SimpleStringProperty dateImported;

    /** The date that the File was created. */
    private SimpleStringProperty dateFileCreated;

    /** The date that the File was last modified. */
    private SimpleStringProperty dateFileModified;

    /** The drive. */
    private SimpleStringProperty drive;

    /** The file path. */
    private SimpleStringProperty filePath;

    /** The file name. */
    private SimpleStringProperty fileName;

    /** The file type. */
    private SimpleStringProperty fileType;

    /** The file type. */
    private SimpleStringProperty fileSize;

    /** Does the File exists */
    private SimpleBooleanProperty fileExists;

    // ---------END OF
    // PROPERTIES----------------------------------------------------------------------------------

    /** The image to be displayed if the Media is Song + NO ERRORS */
    public static final Image songImage = InfoTool.getImageFromDocuments("song.png");
    /** The image to be displayed if the Media is Song + MISSING */
    public static final Image songMissingImage = InfoTool.getImageFromDocuments("songMissing.png");
    /** The image to be displayed if the Media is Song + CORRUPTED */
    public static final Image songCorruptedImage = InfoTool.getImageFromDocuments("songCorrupted.png");

    /** The video image. */
    public static final Image videoImage = InfoTool.getImageFromDocuments("video.png");

    /** The genre. */
    protected Genre genre;

    /**
     * Constructor.
     *
     * @param path
     *            The path of the File
     * @param stars
     *            The quality of the Media
     * @param timesPlayed
     *            The times the Media has been played
     * @param dateImported
     *            The date the Media was imported <b> if null given then the imported time will be the current date </b>
     * @param hourImported
     *            The hour the Media was imported <b> if null given then the imported hour will be the current time </b>
     * @param genre
     *            The genre of the Media <b> see the Genre class for more </b>
     */
    public Media(String path, double stars, int timesPlayed, String dateImported, String hourImported, Genre genre) {

	// ....initialize
	mediaType = new SimpleObjectProperty<>(new ImageView(InfoTool.isAudioSupported(path) ? songImage : videoImage));
	hasBeenPlayed = new SimpleObjectProperty<>(null);

	this.title = new SimpleStringProperty(InfoTool.getFileTitle(path));
	this.drive = new SimpleStringProperty(Paths.get(path).getRoot() + "");
	this.filePath = new SimpleStringProperty(path);
	this.fileName = new SimpleStringProperty(InfoTool.getFileName(path));
	this.fileType = new SimpleStringProperty(InfoTool.getFileExtension(path));
	this.fileSize = new SimpleStringProperty();

	this.stars = new SimpleDoubleProperty(stars);
	this.timesPlayed = new SimpleIntegerProperty(timesPlayed);
	this.duration = new SimpleIntegerProperty();
	//this.duration.addListener((observable, oldValue, newValue) -> fixTheInformations(true))
	this.durationEdited = new SimpleStringProperty("");

	// Hour Created|Imported
	this.hourImported = new SimpleStringProperty(hourImported != null ? hourImported : InfoTool.getLocalTime());

	// Date Created|Imported
	this.dateImported = new SimpleStringProperty(dateImported != null ? dateImported : InfoTool.getCurrentDate());

	//Date File Created + Date File Modified
	dateFileCreated = new SimpleStringProperty();
	dateFileModified = new SimpleStringProperty();

	// File exists
	fileExists = new SimpleBooleanProperty(this, "FileExists", true);
	fileExists.addListener((observable, oldValue, newValue) -> fixTheInformations(true));

	// Media Genre
	this.genre = genre;

	// Find the correct image
	fixTheInformations(true);
    }

    //!!!!!!!!!!!!!!!!!!THIS METHOD NEEDS FIXING!!!!!!!!!!!!!!!!!

    /**
     * When a files appears or dissapears it's information like size , image etc must be fixed to represent it's current status
     */
    private void fixTheInformations(boolean doUpdate) {

	if (!doUpdate)
	    return;

	//System.out.println("Doing Update ->" + this.fileName.get())

	//I need to add code for video files etc

	//Check the fileSize 
	this.fileSize.set(InfoTool.getFileSizeEdited(new File(filePath.get())));

	//dateFileCreated
	dateFileCreated.set(InfoTool.getFileCreationDate(filePath.get()));

	//dateFileModified
	dateFileModified.set(InfoTool.getFileLastModifiedDate(filePath.get()));

	//It is Audio?
	if (!InfoTool.isAudioSupported(filePath.get()))
	    return;

	//Duration
	duration.set(InfoTool.durationInSeconds(filePath.get(), AudioType.FILE));

	//DurationEdited
	int localDuration = this.duration.get();

	durationEdited.set(!fileExists.get() ? "file missing"
		: localDuration == -1 ? "corrupted"
			: localDuration == 0 ? "error" : InfoTool.getTimeEditedOnHours(localDuration));

	//Image
	if (!fileExists.get()) //File is missing ?
	    mediaType.get().setImage(songMissingImage);
	else if (this.duration.get() != -1) // Not corrupted
	    mediaType.get().setImage(songImage);
	else if (this.duration.get() == -1) //Corrupted
	    mediaType.get().setImage(songCorruptedImage);

    }

    // --------Property
    // Methods-----------------------------------------------------------------------------------

    /**
     * Media type property.
     *
     * @return the simple object property
     */
    public SimpleObjectProperty<ImageView> mediaTypeProperty() {
	return mediaType;
    }

    /**
     * Checks for been played property.
     *
     * @return the simple object property
     */
    public SimpleObjectProperty<ImageView> hasBeenPlayedProperty() {
	return hasBeenPlayed;
    }

    /**
     * Title property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty titleProperty() {
	return title;
    }

    /**
     * Duration edited property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty durationEditedProperty() {
	return durationEdited;
    }

    /**
     * Duration property.
     *
     * @return the simple integer property
     */
    public SimpleIntegerProperty durationProperty() {
	return duration;
    }

    /**
     * Times played property.
     *
     * @return the simple integer property
     */
    public SimpleIntegerProperty timesPlayedProperty() {
	return timesPlayed;
    }

    /**
     * Stars property.
     *
     * @return the simple double property
     */
    public SimpleDoubleProperty starsProperty() {
	return stars;
    }

    /**
     * Hour imported property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty hourImportedProperty() {
	return hourImported;
    }

    /**
     * Date imported property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty dateImportedProperty() {
	return dateImported;
    }

    /**
     * Date File Created property.
     *
     * @return Date File Created property.
     */
    public SimpleStringProperty dateFileCreatedProperty() {
	return dateFileCreated;
    }

    /**
     * Date File last modified property.
     *
     * @return The Date File last modified property.
     */
    public SimpleStringProperty dateFileModified() {
	return dateFileModified;
    }

    /**
     * Drive property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty driveProperty() {
	return drive;
    }

    /**
     * File path property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty filePathProperty() {
	return filePath;
    }

    /**
     * File name property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty fileNameProperty() {
	return fileName;
    }

    /**
     * File Size property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty fileSizeProperty() {
	return fileSize;
    }

    /**
     * File type property.
     *
     * @return the simple string property
     */
    public SimpleStringProperty fileTypeProperty() {
	return fileType;
    }

    /**
     * File type property.
     *
     * @return the simple string property
     */
    public SimpleBooleanProperty fileExistsProperty() {
	return fileExists;
    }

    // --------ORDINARY
    // METHODS----------------------------------------------------------------------

    /**
     * Prepares the delete operation when more than one Media files will be deleted.
     *
     * @param permanent
     *            <br>
     *            true->storage medium + (play list)/library<br>
     *            false->only from (play list)/library
     * @param controller
     *            the controller
     */
    public void prepareDelete(boolean permanent, SmartController controller) {
	int previousTotal = controller.getTotalInDataBase();

	//RememberScrollBar Position
	ScrollBar verticalBar = (ScrollBar) controller.tableViewer.lookup(".scroll-bar:vertical");
	controller.scrollValueBeforeDeleteAction = verticalBar.getValue();

	// Remove selected items
	controller.removeSelected(permanent);

	// Update
	if (previousTotal != controller.getTotalInDataBase()) {
	    if (genre == Genre.LIBRARYSONG)
		Main.libraryMode.multipleLibs.getSelectedLibrary().updateSettingsTotalLabel();

	    controller.loadService.startService(true, true);
	}
    }

    /**
     * Delete the Media from (play list)/library or (+storage medium).
     *
     * @param permanent
     *            <br>
     *            true->storage medium + (play list)/library<br>
     *            false->only from (play list)/library
     * @param doQuestion
     *            <br>
     *            true->asks for permission</b> <br>
     *            false->not asking for permission<br>
     * @param commit
     *            <br>
     *            true-> will do commit<br>
     *            false->will not do commit
     * @param controller
     *            the controller
     */
    public void delete(boolean permanent, boolean doQuestion, boolean commit, SmartController controller) {

	if (controller.isFree(true)) {
	    boolean hasBeenDeleted = false;

	    // Do question?
	    if (doQuestion) {
		if (ActionTool.doDeleteQuestion(permanent, fileName.get(), 1))
		    hasBeenDeleted = removeItem(permanent, controller);
	    } else
		hasBeenDeleted = removeItem(permanent, controller);

	    if (hasBeenDeleted) {
		// Delete from database
		try {
		    controller.preparedDelete.setString(1, getFilePath());
		    controller.preparedDelete.executeUpdate();
		    // Commit?
		    if (commit)
			Main.dbManager.commit();
		} catch (SQLException ex) {
		    Main.logger.log(Level.WARNING, "", ex);
		}
	    }

	}

    }

    /**
     * Removes this specific Media.
     *
     * @param permanent
     *            <br>
     *            true->storage medium + (play list)/library<br>
     *            false->only from (play list)/library
     * @param controller
     *            the controller
     * @return true, if successful
     */
    private boolean removeItem(boolean permanent, SmartController controller) {

	// Delete from storage medium?
	if (permanent && !ActionTool.deleteFile(new File(getFilePath())))
	    return false;

	// --totalInDataBase
	controller.setTotalInDataBase(controller.getTotalInDataBase() - 1);

	return true;
    }

    /**
     * Rename the Media File.
     *
     * @param controller
     *            the controller
     * @param node
     *            The node based on which the Rename Window will be position
     */
    public void rename(SmartController controller, Node node) {

	// If !Controller is Locked
	if (controller.isFree(true)) {

	    // Security Variable
	    controller.renameWorking = true;

	    // Open Window
	    String extension = "." + InfoTool.getFileExtension(getFilePath());
	    Main.renameWindow.show(getTitle(), node);

	    // Bind
	    title.bind(Main.renameWindow.inputField.textProperty());
	    fileName.bind(Main.renameWindow.inputField.textProperty().concat(extension));

	    // When the Rename Window is closed do the rename
	    Main.renameWindow.showingProperty().addListener(new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {

		    // Remove the Listener
		    Main.renameWindow.showingProperty().removeListener(this);

		    // !Showing
		    if (!Main.renameWindow.isShowing()) {

			// Remove Binding
			title.unbind();
			fileName.unbind();

			String newName = new File(getFilePath()).getParent() + File.separator + fileName.get();

			// !XPressed && // Old name != New name
			if (Main.renameWindow.wasAccepted() && !getFilePath().equals(newName)) {

			    try {

				// No duplicates allowed
				boolean canPass = true;
				controller.preparedCountElementsWithString.setString(1, newName);
				ResultSet set = controller.preparedCountElementsWithString.executeQuery();
				int total = set.getInt(1);
				if (total > 0)
				    canPass = false;
				set.close();
				// System.out.println("Total is->:" + total)

				// if can pass
				if (canPass) {

				    // Check if that file already exists
				    if (new File(newName).exists()) {
					setFilePath(filePath.get());
					ActionTool.showNotification("Rename Failed",
						"The action can not been completed(Possible Reason):\nA file with that name already exists.",
						Duration.millis(1500), NotificationType.WARNING);
					controller.renameWorking = false;
					return;
				    }

				    // Check if it can be renamed
				    if (!new File(getFilePath()).renameTo(new File(newName))) {
					setFilePath(filePath.get());
					ActionTool.showNotification("Rename Failed",
						"The action can not been completed(Possible Reasons):\n1) The file is opened by a program,close it and try again.\n2)It doesn't exist anymore..",
						Duration.millis(1500), NotificationType.WARNING);
					controller.renameWorking = false;
					return;
				    }

				    // database update
				    controller.preparedRename.setString(1, newName);
				    controller.preparedRename.setString(2, getFilePath());
				    controller.preparedRename.executeUpdate();
				    Main.dbManager.commit();

				    // Rename it in playedSong if...
				    Main.playedSongs.renameMedia(getFilePath(), newName);

				    // change the file path
				    setFilePath(newName);

				} else { // canPass==false
				    setFilePath(filePath.get());
				    Notifications.create().title("Dublicate Name").text(
					    "The action can not been completed because :\nA file with that name already exists.")
					    .darkStyle().showWarning();
				}

				// Exception occurred
			    } catch (SQLException ex) {
				Main.logger.log(Level.WARNING, "", ex);
				setFilePath(filePath.get());
				ActionTool.showNotification("Error", "error during renaming the file",
					Duration.millis(1500), NotificationType.ERROR);
			    }
			} else // X is pressed by user || // Old name == New
			      // name
			    setFilePath(filePath.get());

			// Security Variable
			controller.renameWorking = false;

		    } // RenameWindow is still showing
		}// invalidated
	    });
	}
    }

    /**
     * Evaluate the Media File using stars.
     *
     * @param controller
     *            the controller
     * @param node
     *            The node based on which the Rename Window will be position
     */
    public void updateStars(SmartController controller, Node node) {

	// Show the Window
	Main.starWindow.show(stars.get(), node);

	// Keep in memory stars ...
	final double previousStars = stars.get();
	stars.bind(Main.starWindow.starsProperty());

	// Listener
	Main.starWindow.window.showingProperty().addListener(new InvalidationListener() {
	    @Override
	    public void invalidated(Observable observable) {

		// Remove the listener
		Main.starWindow.window.showingProperty().removeListener(this);

		// !showing?
		if (!Main.starWindow.window.isShowing()) {

		    // unbind stars property
		    stars.unbind();

		    // Accepted?
		    if (Main.starWindow.wasAccepted()) {
			try {

			    controller.preparedUStars.setDouble(1, getStars());
			    controller.preparedUStars.setString(2, getFilePath());
			    controller.preparedUStars.executeUpdate();
			    Main.dbManager.commit();

			} catch (Exception ex) {
			    Main.logger.log(Level.WARNING, "", ex);
			}
		    } else
			stars.set(previousStars);
		}
	    }
	});
    }

    // --------GETTERS------------------------------------------------------------------------------------

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
	return title.get();
    }

    /**
     * Gets the drive.
     *
     * @return the drive
     */
    public String getDrive() {
	return drive.get();
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
	return filePath.get();
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
	return fileName.get();
    }

    /**
     * Gets the file type.
     *
     * @return the file type
     */
    public String getFileType() {
	return fileType.get();
    }

    /**
     * Gets the file size.
     *
     * @return the file type
     */
    public String getFileSize() {
	return fileSize.get();
    }

    /**
     * Gets the duration.
     *
     * @return the duration
     */
    public int getDuration() {
	return duration.get();
    }

    /**
     * Gets the stars.
     *
     * @return the stars
     */
    public double getStars() {
	return stars.get();
    }

    /**
     * Gets the times played.
     *
     * @return the times played
     */
    public int getTimesPlayed() {
	return timesPlayed.get();
    }

    /**
     * Gets the hour imported.
     *
     * @return the hour imported
     */
    public String getHourImported() {
	return hourImported.get();
    }

    /**
     * Gets the date imported.
     *
     * @return the date imported
     */
    public String getDateImported() {
	return dateImported.get();
    }

    /**
     * Gets The date that the File was created
     *
     * @return The date that the File was created
     */
    public String getDateFileCreated() {
	return dateFileCreated.get();
    }

    /**
     * Gets The date that the File was last modified
     *
     * @return The date that the File was last modified
     */
    public String getDateFileModified() {
	return dateFileModified.get();
    }

    /**
     * Gets the genre.
     *
     * @return the genre
     */
    public Genre getGenre() {
	return genre;
    }

    // --------SETTERS------------------------------------------------------------------------------------

    /**
     * Sets the file path.
     *
     * @param path
     *            the new file path
     */
    private void setFilePath(String path) {
	this.title.set(InfoTool.getFileTitle(path));
	this.drive.set(path.substring(0, 1));
	this.filePath.set(path);
	this.fileName.set(InfoTool.getFileName(path));
	this.fileType.set(InfoTool.getFileExtension(path));

    }

    /**
     * Sets the duration.
     *
     * @param duration
     *            the new duration
     */
    public void setDuration(int duration) {
	this.duration.set(duration);
    }

    /**
     * Sets the times played.
     *
     * @param timesPlayed
     *            the times played
     * @param controller
     *            the controller
     */
    protected void setTimesPlayed(int timesPlayed, SmartController controller) {
	this.timesPlayed.set(timesPlayed);

	// Update the dataBase
	try {

	    controller.preparedUTimesPlayed.setInt(1, getTimesPlayed());
	    controller.preparedUTimesPlayed.setString(2, getFilePath());
	    controller.preparedUTimesPlayed.executeUpdate();
	    Main.dbManager.commit();

	} catch (Exception ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}

    }

    /**
     * Sets the media played.
     */
    public void setMediaPlayed() {
	// not initialize new Objects if not necessary
	if (hasBeenPlayed.get() == null)
	    hasBeenPlayed.set(new ImageView(InfoTool.playedImage));
	else
	    hasBeenPlayed.get().setImage(InfoTool.playedImage);

    }

    // ------------------ABSTRACT METHODS
    // ----------------------------------------------------------------------

    /**
     * This method is used during drag so the drag view has an image representing the album image of the media.
     *
     * @param db
     *            the new drag view
     */
    public abstract void setDragView(Dragboard db);

    /**
     * Retrieves the Album Image of the Media.
     *
     * @return the album image
     */
    public abstract Image getAlbumImage();
}
