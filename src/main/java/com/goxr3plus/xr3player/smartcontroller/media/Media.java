/*
 * 
 */
package main.java.com.goxr3plus.xr3player.smartcontroller.media;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.modes.librarymode.Library;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.application.windows.EmotionsWindow.Emotion;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public abstract class Media {
	
	/** The media type. */
	private SimpleObjectProperty<StackedFontIcon> artwork;
	
	/** The title. */
	private SimpleStringProperty title;
	
	/** The media type. */
	private SimpleIntegerProperty mediaType;
	
	/**
	 * Determines if the Media has been played or is currently playing or has not been played at all
	 */
	private SimpleIntegerProperty playStatus;
	
	/** Get Information or Buy */
	private SimpleObjectProperty<HBox> getInfoBuy;
	
	/** Liked Disliked or Neutral feelings */
	private SimpleIntegerProperty emotion;
	
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
	
	private SimpleStringProperty artist;
	
	private SimpleStringProperty mood;
	
	private SimpleStringProperty album;
	
	private SimpleStringProperty composer;
	
	private SimpleStringProperty comment;
	
	private SimpleStringProperty genre;
	
	private SimpleStringProperty tempo;
	
	private SimpleStringProperty key;
	
	private SimpleStringProperty year;
	
	//
	
	private SimpleStringProperty copyright;
	
	private SimpleStringProperty track;
	
	private SimpleStringProperty track_total;
	
	private SimpleStringProperty remixer;
	
	private SimpleStringProperty djMixer;
	
	private SimpleStringProperty rating;
	
	private SimpleStringProperty producer;
	
	private SimpleStringProperty performer;
	
	private SimpleStringProperty orchestra;
	
	private SimpleStringProperty country;
	
	private SimpleStringProperty lyricist;
	
	private SimpleStringProperty conductor;
	
	private SimpleStringProperty amazonID;
	
	private SimpleStringProperty encoder;
	
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
	
	/** The times played. */
	private SimpleIntegerProperty bpm;
	
	/** The number of the Media inside the PlayList */
	private SimpleIntegerProperty number;
	
	// ---------END OF PROPERTIES----------------------------------------------------------------------------------
	
	/** The genre. */
	private final Genre smartControllerGenre;
	
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
	 * @param smartControllerGenre
	 *            The smartControllerGenre of the Media <b> see the Genre class for more </b>
	 */
	public Media(String path, double stars, int timesPlayed, String dateImported, String hourImported, Genre smartControllerGenre, int number) {
		
		//ArtWork FontIcon
		FontIcon artWorkImage = JavaFXTools.getFontIcon("gmi-album", Color.WHITE, 30);
		
		//ArtWork ImageView
		ImageView artWorkImageView = new ImageView();
		artWorkImageView.setFitWidth(30);
		artWorkImageView.setFitHeight(30);
		artWorkImageView.visibleProperty().bind(artWorkImageView.imageProperty().isNotNull());
		
		//StackedFontIcon
		StackedFontIcon artWorkStack = new StackedFontIcon();
		artWorkStack.getChildren().addAll(artWorkImage, artWorkImageView);
		
		//ArtWork object
		artwork = new SimpleObjectProperty<>(artWorkStack);
		
		//search Button	
		Button searchButton = new Button("", JavaFXTools.getFontIcon("fab-chrome", Color.WHITE, 18));
		searchButton.getStyleClass().add("jfx-button2");
		searchButton.setPrefSize(28, 24);
		searchButton.setMinSize(28, 24);
		searchButton.setMaxSize(28, 24);
		searchButton.setStyle("-fx-cursor:hand");
		searchButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		searchButton.setOnMouseReleased(m -> {
			try {
				Main.webBrowser.createTabAndSelect("https://www.google.com/search?q=" + URLEncoder.encode(this.getTitle(), "UTF-8"));
				Main.topBar.selectTab(Main.topBar.getWebModeTab());
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		});
		
		//Youtube button		
		Button youtubeButton = new Button("", JavaFXTools.getFontIcon("fab-youtube", Color.WHITE, 18));
		youtubeButton.getStyleClass().add("jfx-button2");
		youtubeButton.setPrefSize(28, 24);
		youtubeButton.setMinSize(28, 24);
		youtubeButton.setMaxSize(28, 24);
		youtubeButton.setStyle("-fx-cursor:hand");
		youtubeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		youtubeButton.setOnMouseReleased(m -> {
			try {
				Main.webBrowser.createTabAndSelect("https://www.youtube.com/results?search_query=" + URLEncoder.encode(this.getTitle(), "UTF-8"));
				Main.topBar.selectTab(Main.topBar.getWebModeTab());
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		});
		
		//Buy button		
		Button buyButton = new Button("", JavaFXTools.getFontIcon("fas-shopping-cart", Color.WHITE, 18));
		buyButton.getStyleClass().add("jfx-button2");
		buyButton.setPrefSize(28, 24);
		buyButton.setMinSize(28, 24);
		buyButton.setMaxSize(28, 24);
		buyButton.setStyle("-fx-cursor:hand");
		buyButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		buyButton.setOnMouseReleased(m -> Main.shopContextMenu.showContextMenu(getTitle(), m.getScreenX(), m.getScreenY()));
		
		//HBox
		HBox hbox = new HBox(searchButton, youtubeButton, buyButton);
		hbox.setAlignment(Pos.CENTER);
		getInfoBuy = new SimpleObjectProperty<>(hbox);
		
		//----------
		this.emotion = new SimpleIntegerProperty(0);
		this.mediaType = new SimpleIntegerProperty(1);
		this.playStatus = new SimpleIntegerProperty(-2);
		this.title = new SimpleStringProperty(InfoTool.getFileTitle(path));
		this.drive = new SimpleStringProperty(Paths.get(path).getRoot() + "");
		this.filePath = new SimpleStringProperty(path);
		this.fileName = new SimpleStringProperty(InfoTool.getFileName(path));
		this.fileType = new SimpleStringProperty(InfoTool.getFileExtension(path));
		this.fileSize = new SimpleStringProperty();
		this.artist = new SimpleStringProperty();
		this.mood = new SimpleStringProperty();
		this.album = new SimpleStringProperty();
		this.composer = new SimpleStringProperty();
		this.comment = new SimpleStringProperty();
		this.genre = new SimpleStringProperty();
		this.tempo = new SimpleStringProperty();
		this.key = new SimpleStringProperty();
		this.year = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.track = new SimpleStringProperty();
		this.track_total = new SimpleStringProperty();
		this.remixer = new SimpleStringProperty();
		this.djMixer = new SimpleStringProperty();
		this.rating = new SimpleStringProperty();
		this.producer = new SimpleStringProperty();
		this.performer = new SimpleStringProperty();
		this.orchestra = new SimpleStringProperty();
		this.country = new SimpleStringProperty();
		this.lyricist = new SimpleStringProperty();
		this.conductor = new SimpleStringProperty();
		this.amazonID = new SimpleStringProperty();
		this.encoder = new SimpleStringProperty();
		this.bitRate = new SimpleIntegerProperty();
		this.bpm = new SimpleIntegerProperty();
		this.number = new SimpleIntegerProperty(number);
		
		//Stars
		this.stars = new SimpleDoubleProperty(stars);
		//-----------
		
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
		this.smartControllerGenre = smartControllerGenre;
		
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
		
		//Keep a reference of the File
		//File file = new File(filePath.get())
		
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
			mediaType.set(-1);
		else if (this.duration.get() != -1) // Not corrupted
			mediaType.set(1);
		else if (this.duration.get() == -1) //Corrupted
			mediaType.set(0);
		
	}
	
	// --------Property
	// Methods-----------------------------------------------------------------------------------
	
	/**
	 * Media type property.
	 *
	 * @return the simple object property
	 */
	public SimpleIntegerProperty mediaTypeProperty() {
		return mediaType;
	}
	
	public SimpleObjectProperty<StackedFontIcon> artworkProperty() {
		return artwork;
	}
	
	/**
	 * Check the play status property
	 *
	 * @return the simple object property
	 */
	public SimpleIntegerProperty playStatusProperty() {
		return playStatus;
	}
	
	/**
	 * Get Information or Buy Property
	 *
	 * @return the simple object property
	 */
	public SimpleObjectProperty<HBox> getInfoBuyProperty() {
		return getInfoBuy;
	}
	
	/**
	 * Liked Disliked or Neutral Feelings
	 *
	 * @return the simple object property
	 */
	public SimpleIntegerProperty emotionProperty() {
		return emotion;
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
	public SimpleStringProperty dateFileModifiedProperty() {
		return dateFileModified;
	}
	
	public SimpleStringProperty artistProperty() {
		return artist;
	}
	
	public SimpleStringProperty moodProperty() {
		return mood;
	}
	
	public SimpleStringProperty albumProperty() {
		return album;
	}
	
	public SimpleStringProperty composerProperty() {
		return composer;
	}
	
	public SimpleStringProperty commentProperty() {
		return comment;
	}
	
	public SimpleStringProperty genreProperty() {
		return genre;
	}
	
	public SimpleStringProperty tempoProperty() {
		return tempo;
	}
	
	public SimpleStringProperty keyProperty() {
		return key;
	}
	
	public SimpleStringProperty yearProperty() {
		return year;
	}
	
	public SimpleStringProperty copyrightProperty() {
		return copyright;
	}
	
	public SimpleStringProperty trackProperty() {
		return track;
	}
	
	public SimpleStringProperty track_totalProperty() {
		return track_total;
	}
	
	public SimpleStringProperty remixerProperty() {
		return remixer;
	}
	
	public SimpleStringProperty djMixerProperty() {
		return djMixer;
	}
	
	public SimpleStringProperty ratingProperty() {
		return rating;
	}
	
	public SimpleStringProperty producerProperty() {
		return producer;
	}
	
	public SimpleStringProperty performerProperty() {
		return performer;
	}
	
	public SimpleStringProperty orchestraProperty() {
		return orchestra;
	}
	
	public SimpleStringProperty countryProperty() {
		return country;
	}
	
	public SimpleStringProperty lyricistProperty() {
		return lyricist;
	}
	
	public SimpleStringProperty conductorProperty() {
		return conductor;
	}
	
	public SimpleStringProperty amazonIDProperty() {
		return amazonID;
	}
	
	public SimpleStringProperty encoderProperty() {
		return encoder;
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
	
	/**
	 * Beats per minute of audio
	 * 
	 * @return The bpm
	 */
	public SimpleIntegerProperty bpmProperty() {
		return bpm;
	}
	
	/**
	 * Number of Audio inside the play list
	 * 
	 * @return the number
	 */
	public SimpleIntegerProperty numberProperty() {
		return number;
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
		
		//Check if it is EmotionMedia (because if cleared directly from an Emotion Playlist , then we want also to
		//vanish it completely)
		if (controller.getGenre() == Genre.EMOTIONSMEDIA) {
			Main.emotionListsController.hatedMediaList.remove(getFilePath(), false);
			Main.emotionListsController.dislikedMediaList.remove(getFilePath(), false);
			Main.emotionListsController.likedMediaList.remove(getFilePath(), false);
			Main.emotionListsController.lovedMediaList.remove(getFilePath(), false);
		}
		
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
	public void rename(Node node) {
		
		// If !Controller is Locked
		//if (controller.isFree(true)) {
		
		// Security Variable
		//controller.renameWorking = true;
		
		// Open Window
		String extension = "." + InfoTool.getFileExtension(getFilePath());
		Main.renameWindow.show(getTitle(), node, "Media Renaming", FileCategory.FILE);
		String oldFilePath = getFilePath();
		
		// Bind
		title.bind(Main.renameWindow.getInputField().textProperty());
		fileName.bind(Main.renameWindow.getInputField().textProperty().concat(extension));
		
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
					
					String newFilePath = new File(oldFilePath).getParent() + File.separator + fileName.get();
					
					// !XPressed && // Old name != New name
					if (Main.renameWindow.wasAccepted() && !getFilePath().equals(newFilePath)) {
						
						try {
							
							// Check if that file already exists
							if (new File(newFilePath).exists()) {
								setFilePath(oldFilePath);
								ActionTool.showNotification("Rename Failed", "The action can not been completed:\nA file with that name already exists.", Duration.millis(1500),
										NotificationType.WARNING);
								//controller.renameWorking = false
								return;
							}
							
							// Check if it can be renamed
							if (!new File(getFilePath()).renameTo(new File(newFilePath))) {
								setFilePath(oldFilePath);
								ActionTool.showNotification("Rename Failed",
										"The action can not been completed(Possible Reasons):\n1) The file is opened by a program,close it and try again.\n2)It doesn't exist anymore..",
										Duration.millis(1500), NotificationType.WARNING);
								//controller.renameWorking = false
								return;
							}
							
							//Inform all Libraries SmartControllers 
							Main.libraryMode.viewer.getItemsObservableList().stream().map(library -> ( (Library) library ).getSmartController()).forEach(smartController -> {
								
								internalDataBaseRename(smartController, newFilePath, oldFilePath);
								
							});
							
							//Inform all XPlayers SmartControllers
							Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()).forEach(smartController -> {
								
								internalDataBaseRename(smartController, newFilePath, oldFilePath);
								
							});
							
							//Update Emotion Lists SmartControllers
							Main.emotionsTabPane.getTabPane().getTabs().stream().map(tab -> (SmartController) tab.getContent()).forEach(smartController -> {
								
								internalDataBaseRename(smartController, newFilePath, oldFilePath);
								
							});
							
							//Inform all XPlayers Models
							Main.xPlayersList.getList().stream().forEach(xPlayerController -> {
								if (oldFilePath.equals(xPlayerController.getxPlayerModel().songPathProperty().get())) {
									
									//filePath
									xPlayerController.getxPlayerModel().songPathProperty().set(newFilePath);
									
									//object
									xPlayerController.getPlayService().checkAudioTypeAndUpdateXPlayerModel(newFilePath);
									
									//change the text of Marquee
									xPlayerController.getMediaFileMarquee().setText(InfoTool.getFileName(newFilePath));
									
								}
							});
							
							// Inform Played Media List
							Main.playedSongs.renameMedia(oldFilePath, newFilePath, false);
							
							// Inform Hated Media List
							Main.emotionListsController.hatedMediaList.renameMedia(oldFilePath, newFilePath, false);
							// Inform Disliked Media List
							Main.emotionListsController.dislikedMediaList.renameMedia(oldFilePath, newFilePath, false);
							// Inform Liked Media List
							Main.emotionListsController.likedMediaList.renameMedia(oldFilePath, newFilePath, false);
							// Inform Loved Media List
							Main.emotionListsController.lovedMediaList.renameMedia(oldFilePath, newFilePath, false);
							
							//Update the SearchWindow
							Main.searchWindowSmartController.getItemsObservableList().forEach(media -> {
								if (media.getFilePath().equals(oldFilePath))
									media.setFilePath(newFilePath);
							});
							
							//Commit to the Database
							Main.dbManager.commit();
							
							// Exception occurred
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
							setFilePath(oldFilePath);
							ActionTool.showNotification("Error Message", "Failed to rename the File:/n" + ex.getMessage(), Duration.millis(1500), NotificationType.ERROR);
						}
					} else // X is pressed by user || // Old name == New name
						setFilePath(oldFilePath);
					
				} // RenameWindow is still showing
			}// invalidated
		});
		//}
	}
	
	/**
	 * Called to rename the SQL Table data for the SmartController
	 * 
	 * @param smartController
	 * @param newFilePath
	 * @param oldFilePath
	 */
	public static void internalDataBaseRename(SmartController smartController , String newFilePath , String oldFilePath) {
		
		//if (controller1 != controller) // we already renamed on this controller
		try (PreparedStatement dataRename = Main.dbManager.getConnection().prepareStatement("UPDATE '" + smartController.getDataBaseTableName() + "' SET PATH=? WHERE PATH=?")) {
			
			// Prepare Statement
			dataRename.setString(1, newFilePath);
			dataRename.setString(2, oldFilePath);
			if (dataRename.executeUpdate() > 0) { //Check
				smartController.getItemsObservableList().forEach(media -> {
					if (media.getFilePath().equals(oldFilePath))
						media.setFilePath(newFilePath);
				});
				smartController.getFiltersMode().getMediaTableViewer().getTableView().getItems().forEach(media -> {
					if (media.getFilePath().equals(oldFilePath))
						media.setFilePath(newFilePath);
				});
			}
			
		} catch (SQLException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
		
	}
	
	/**
	 * Evaluate the Media File using stars.
	 * 
	 * @param node
	 *            The node based on which the Rename Window will be position
	 */
	public void updateStars(Node node) {
		
		// Show the Window
		Main.starWindow.show(getFileName(), stars.get(), node);
		
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
						Main.libraryMode.viewer.getItemsObservableList().stream().map(library -> ( (Library) library ).getSmartController()).forEach(smartController -> {
							
							internalDataBaseUpdateStars(smartController);
							
						});
						
						//Inform all XPlayers SmartControllers
						Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()).forEach(smartController -> {
							
							internalDataBaseUpdateStars(smartController);
							
						});
						
						//Update Emotion Lists SmartControllers
						Main.emotionsTabPane.getTabPane().getTabs().stream().map(tab -> (SmartController) tab.getContent()).forEach(smartController -> {
							
							internalDataBaseUpdateStars(smartController);
							
						});
						
						//Update the SearchWindow
						Main.searchWindowSmartController.getItemsObservableList().forEach(media -> {
							if (media.getFilePath().equals(Media.this.getFilePath()))
								media.starsProperty().set(stars.get());
						});
						
						//Update the StarredMediaList
						Main.starredMediaList.addOrUpdateStars(Media.this.getFilePath(), stars.get(), false);
						
						//Commit
						Main.dbManager.commit();
					} else
						stars.set(previousStars);
				}
			}
		});
		
	}
	
	/**
	 * Called to update STARS on the SQL Table data for the SmartController
	 * 
	 * @param smartController
	 */
	private void internalDataBaseUpdateStars(SmartController smartController) {
		
		//Do it bro!
		try (PreparedStatement preparedUStars = Main.dbManager.getConnection()
				.prepareStatement("UPDATE '" + smartController.getDataBaseTableName() + "' SET STARS=? WHERE PATH=?")) {
			
			// Prepare Statement
			preparedUStars.setDouble(1, getStars());
			preparedUStars.setString(2, getFilePath());
			if (preparedUStars.executeUpdate() > 0) {// && controller1 != controller) //Check 
				smartController.getItemsObservableList().forEach(media -> {
					if (media.getFilePath().equals(Media.this.getFilePath()))
						media.starsProperty().set(stars.get());
				});
				smartController.getFiltersMode().getMediaTableViewer().getTableView().getItems().forEach(media -> {
					if (media.getFilePath().equals(Media.this.getFilePath()))
						media.starsProperty().set(stars.get());
				});
			}
			
		} catch (Exception ex) {
			Main.logger.log(Level.WARNING, "", ex);
			//	ActionTool.showNotification("Error Message", "Failed to update the stars:/n" + ex.getMessage(), Duration.millis(1500), NotificationType.ERROR)
		}
	}
	
	/**
	 * Keep track of previous emotion so not change the current image again and again
	 */
	private int previousEmotion = 0;
	
	/**
	 * This method is called to change the Emotion Image of the Media based on the current Emotion
	 * 
	 * @param emotion
	 */
	public void changeEmotionImage(Emotion emotion) {
		
		if (emotion == Emotion.HATE)
			this.emotion.set(1);
		else if (emotion == Emotion.DISLIKE)
			this.emotion.set(2);
		else if (emotion == Emotion.NEUTRAL)
			this.emotion.set(0);
		else if (emotion == Emotion.LIKE)
			this.emotion.set(3);
		else if (emotion == Emotion.LOVE)
			this.emotion.set(4);
		
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
	 * @return the bitRate
	 */
	public SimpleIntegerProperty getBitRate() {
		return bitRate;
	}
	
	/**
	 * @return The bpm
	 */
	public SimpleIntegerProperty getBpm() {
		return bpm;
	}
	
	/**
	 * @return the Number
	 */
	public SimpleIntegerProperty getNumber() {
		return number;
	}
	
	/**
	 * Gets smartControllerGenre
	 *
	 * @return The genre of smartControllerGenre Media
	 */
	public Genre getSmartControllerGenre() {
		return smartControllerGenre;
	}
	
	// --------SETTERS------------------------------------------------------------------------------------
	
	/**
	 * Sets the file path.
	 *
	 * @param path
	 *            the new file path
	 */
	public void setFilePath(String path) {
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
	
	//The variables below are used to the played status of the media
	//Just to be more understandable for the future programmers i added
	//the below variables with names :)
	public static final int HAS_BEEN_PLAYED = -1;
	public static final int NEVER_PLAYED = -2;
	public static final int UNKNOWN_PLAYED_STATUS = -3;
	
	/**
	 * Sets playStatus
	 * 
	 * @param playStatus
	 * 
	 *            -2-> Never Played <br>
	 *            -1-> Has been played <br>
	 *            0,1,2,..-> Number of Player is playing this media right now..
	 */
	public void setPlayedStatus(int playStatus) {
		this.playStatus.set(playStatus);
	}
	
	// ------------------ABSTRACT METHODS ----------------------------------------------------------------------
	
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
	
	public abstract Image getAlbumImageFit(int width , int height);
}
