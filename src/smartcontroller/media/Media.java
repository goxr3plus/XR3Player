/*
 * 
 */
package smartcontroller.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import application.Main;
import application.librarymode.Library;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.util.Duration;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import xplayer.model.AudioType;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public abstract class Media {
	
	/** The title. */
	private SimpleStringProperty title;
	
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
	
	/** The times played. */
	private SimpleIntegerProperty bitRate;
	
	// ---------END OF PROPERTIES----------------------------------------------------------------------------------
	
	/** The image to be displayed if the Media is Song + NO ERRORS */
	
	private static final Image SONG_IMAGE = InfoTool.getImageFromResourcesFolder("song.png");
	
	/** The image to be displayed if the Media is Song + MISSING */
	private static final Image SONG_MISSING_IMAGE = InfoTool.getImageFromResourcesFolder("songMissing.png");
	
	/** The image to be displayed if the Media is Song + CORRUPTED */
	private static final Image SONG_CORRUPTED_IMAGE = InfoTool.getImageFromResourcesFolder("songCorrupted.png");
	
	/** The video image. */
	private static final Image VIDEO_IMAGE = InfoTool.getImageFromResourcesFolder("video.png");
	
	/** The genre. */
	private Genre genre;
	
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
		mediaType = new SimpleObjectProperty<>(new ImageView(InfoTool.isAudioSupported(path) ? SONG_IMAGE : VIDEO_IMAGE));
		hasBeenPlayed = new SimpleObjectProperty<>(new ImageView());
		
		this.title = new SimpleStringProperty(InfoTool.getFileTitle(path));
		this.drive = new SimpleStringProperty(Paths.get(path).getRoot() + "");
		this.filePath = new SimpleStringProperty(path);
		this.fileName = new SimpleStringProperty(InfoTool.getFileName(path));
		this.fileType = new SimpleStringProperty(InfoTool.getFileExtension(path));
		this.fileSize = new SimpleStringProperty();
		this.bitRate = new SimpleIntegerProperty();
		try {
			File file = new File(path);
			if (file.exists())
				//It is mp3?
				if ("mp3".equals(this.fileType.get()))
					this.bitRate.set((int) new MP3File(new File(path)).getMP3AudioHeader().getBitRateAsNumber());
		} catch (IOException | TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException e) {
			e.printStackTrace();
		}
		
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
		fileExists.addListener((observable , oldValue , newValue) -> fixTheInformations(true));
		
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
		
		durationEdited.set(!fileExists.get() ? "file missing" : localDuration == -1 ? "corrupted" : localDuration == 0 ? "error" : InfoTool.getTimeEditedOnHours(localDuration));
		
		//Image
		if (!fileExists.get()) //File is missing ?
			mediaType.get().setImage(SONG_MISSING_IMAGE);
		else if (this.duration.get() != -1) // Not corrupted
			mediaType.get().setImage(SONG_IMAGE);
		else if (this.duration.get() == -1) //Corrupted
			mediaType.get().setImage(SONG_CORRUPTED_IMAGE);
		
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
	
	/**
	 * Bit Rate of Audio
	 * 
	 * @return the bitRate
	 */
	public SimpleIntegerProperty bitRateProperty() {
		return bitRate;
	}
	
	// --------ORDINARY
	// METHODS----------------------------------------------------------------------
	
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
	 * @param c
	 *            the controller
	 * @param deleteStatement
	 *            The prepared Statement which will delete the items from the SQL DataBase
	 */
	public void delete(boolean permanent , boolean doQuestion , boolean commit , SmartController c , PreparedStatement deleteStatement) {
		
		if (c.isFree(true)) {
			boolean hasBeenDeleted = false;
			
			// Do question?
			if (!doQuestion)
				hasBeenDeleted = removeItem(permanent, c);
			else if (Main.mediaDeleteWindow.doDeleteQuestion(permanent, fileName.get(), 1, Main.window).get(0))
				hasBeenDeleted = removeItem(permanent, c);
			
			if (hasBeenDeleted && deleteStatement != null) {
				// Delete from database
				try {
					deleteStatement.setString(1, getFilePath());
					deleteStatement.executeUpdate();
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
	private boolean removeItem(boolean permanent , SmartController controller) {
		
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
	 *            The node based on which the Rename Window will be position [[SuppressWarningsSpartan]]
	 */
	public void rename(SmartController controller , Node node) {
		
		// If !Controller is Locked
		if (controller.isFree(true)) {
			
			// Security Variable
			controller.renameWorking = true;
			
			// Open Window
			String extension = "." + InfoTool.getFileExtension(getFilePath());
			Main.renameWindow.show(getTitle(), node, "Media Renaming");
			
			// Bind
			title.bind(Main.renameWindow.inputField.textProperty());
			fileName.bind(Main.renameWindow.inputField.textProperty().concat(extension));
			
			// When the Rename Window is closed do the rename
			Main.renameWindow.showingProperty().addListener(new InvalidationListener() {
				/**
				 * [[SuppressWarningsSpartan]]
				 */
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
							
							//			    try (PreparedStatement elementsMatchingThisWord = Main.dbManager.connection1
							//				    .prepareStatement("SELECT COUNT(*) FROM '" + controller.getDataBaseTableName() + "' WHERE PATH=?");) {
							//
							//				// No duplicates allowed
							//				boolean canPass = true;
							//
							//				elementsMatchingThisWord.setString(1, newName);
							//				ResultSet set = elementsMatchingThisWord.executeQuery(); //needs to be fixed
							//				int total = set.getInt(1);
							//				if (total > 0)
							//				    canPass = false;
							//				set.close();
							//				// System.out.println("Total is->:" + total)
							//
							//			    } catch (SQLException ex) {
							//				Main.logger.log(Level.WARNING, "", ex);
							//				setFilePath(filePath.get());
							//				ActionTool.showNotification("Error Message", "Failed to rename the File:/n" + ex.getMessage(), Duration.millis(1500),
							//					NotificationType.ERROR);
							//			    }
							
							try {
								// if can pass
								//if (canPass) {
								
								// Check if that file already exists
								if (new File(newName).exists()) {
									setFilePath(filePath.get());
									ActionTool.showNotification("Rename Failed", "The action can not been completed:\nA file with that name already exists.", Duration.millis(1500),
											NotificationType.WARNING);
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
								
								//Inform all Libraries SmartControllers 
								Main.libraryMode.teamViewer.getViewer().getItemsObservableList().stream().map(Library::getSmartController).forEach(controller1 -> {
									
									//if (controller1 != controller) // we already renamed on this controller
									try (PreparedStatement dataRename = Main.dbManager.getConnection()
											.prepareStatement("UPDATE '" + controller1.getDataBaseTableName() + "' SET PATH=? WHERE PATH=?")) {
										
										// Prepare Statement
										dataRename.setString(1, newName);
										dataRename.setString(2, getFilePath());
										int i = dataRename.executeUpdate();
										
										if (i > 0 && controller1 != controller) //Check 
											controller1.getLoadService().startService(false, false, true);
										
									} catch (SQLException ex) {
										Main.logger.log(Level.WARNING, "", ex);
									}
								});
								
								//Inform all XPlayers SmartControllers
								Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()).forEach(controller1 -> {
									
									//if (controller1 != controller) // we already renamed on this controller
									try (PreparedStatement dataRename = Main.dbManager.getConnection()
											.prepareStatement("UPDATE '" + controller1.getDataBaseTableName() + "' SET PATH=? WHERE PATH=?")) {
										
										// Prepare Statement
										dataRename.setString(1, newName);
										dataRename.setString(2, getFilePath());
										int i = dataRename.executeUpdate();
										
										if (i > 0 && controller1 != controller) //Check 
											controller1.getLoadService().startService(false, false, true);
										
									} catch (SQLException ex) {
										Main.logger.log(Level.WARNING, "", ex);
									}
								});
								
								Main.dbManager.commit();
								
								// Rename it in playedSong if...
								Main.playedSongs.renameMedia(getFilePath(), newName);
								
								// change the file path
								setFilePath(newName);
								
								//				} else { // canPass==false
								//				    setFilePath(filePath.get());
								//				    ActionTool.showNotification("Dublicate Name",
								//					    "The action can not been completed because :\nA file with that name already exists.",
								//					    Duration.millis(1500), NotificationType.INFORMATION);
								//				}
								
								// Exception occurred
							} catch (Exception ex) {
								Main.logger.log(Level.WARNING, "", ex);
								setFilePath(filePath.get());
								ActionTool.showNotification("Error Message", "Failed to rename the File:/n" + ex.getMessage(), Duration.millis(1500), NotificationType.ERROR);
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
	public void updateStars(SmartController controller , Node node) {
		
		// Show the Window
		Main.starWindow.show(stars.get(), node);
		
		// Keep in memory stars ...
		final double previousStars = stars.get();
		stars.bind(Main.starWindow.starsProperty());
		
		// Listener
		Main.starWindow.getWindow().showingProperty().addListener(new InvalidationListener() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			public void invalidated(Observable o) {
				
				// Remove the listener
				Main.starWindow.getWindow().showingProperty().removeListener(this);
				
				// !showing?
				if (!Main.starWindow.getWindow().isShowing()) {
					
					// unbind stars property
					stars.unbind();
					
					// Accepted?
					if (Main.starWindow.wasAccepted()) {
						
						//Inform all Libraries SmartControllers
						Main.libraryMode.teamViewer.getViewer().getItemsObservableList().stream().map(Library::getSmartController).forEach(controller1 -> {
							
							//Do it bro!
							try (PreparedStatement preparedUStars = Main.dbManager.getConnection()
									.prepareStatement("UPDATE '" + controller1.getDataBaseTableName() + "' SET STARS=? WHERE PATH=?")) {
								
								// Prepare Statement
								preparedUStars.setDouble(1, getStars());
								preparedUStars.setString(2, getFilePath());
								int i = preparedUStars.executeUpdate();
								
								if (i > 0 && controller1 != controller) //Check 
									controller1.getLoadService().startService(false, false, true);
								
							} catch (Exception ex) {
								Main.logger.log(Level.WARNING, "", ex);
								//	ActionTool.showNotification("Error Message", "Failed to update the stars:/n" + ex.getMessage(), Duration.millis(1500), NotificationType.ERROR);
							}
						});
						
						//Inform all XPlayers SmartControllers
						Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()).forEach(controller1 -> {
							
							//Do it bro!
							try (PreparedStatement preparedUStars = Main.dbManager.getConnection()
									.prepareStatement("UPDATE '" + controller1.getDataBaseTableName() + "' SET STARS=? WHERE PATH=?")) {
								
								// Prepare Statement
								preparedUStars.setDouble(1, getStars());
								preparedUStars.setString(2, getFilePath());
								int i = preparedUStars.executeUpdate();
								
								if (i > 0 && controller1 != controller) //Check 
									controller1.getLoadService().startService(false, false, true);
								
							} catch (Exception ex) {
								Main.logger.log(Level.WARNING, "", ex);
								//	ActionTool.showNotification("Error Message", "Failed to update the stars:/n" + ex.getMessage(), Duration.millis(1500), NotificationType.ERROR);
							}
						});
						
						//Commit
						Main.dbManager.commit();
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
	 * @return the bitRate
	 */
	public SimpleIntegerProperty getBitRate() {
		return bitRate;
	}
	
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
	//    protected void setTimesPlayed(int timesPlayed, SmartController controller) {
	//	this.timesPlayed.set(timesPlayed);
	//
	//	// Update the dataBase
	//	try (PreparedStatement preparedUTimesPlayed = Main.dbManager.connection1.prepareStatement("UPDATE '" + controller.getDataBaseTableName() + "' SET TIMESPLAYED=? WHERE PATH=?")) {
	//
	//	    preparedUTimesPlayed.setInt(1, getTimesPlayed());
	//	    preparedUTimesPlayed.setString(2, getFilePath());
	//	    preparedUTimesPlayed.executeUpdate();
	//	    Main.dbManager.commit();
	//
	//	} catch (Exception ex) {
	//	    Main.logger.log(Level.WARNING, "", ex);
	//	}
	//
	//    }
	
	/**
	 * Sets the media played.
	 * 
	 * @param played
	 */
	public void setMediaPlayed(boolean played) {
		hasBeenPlayed.get().setImage(!played ? null : InfoTool.playedImage);
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
