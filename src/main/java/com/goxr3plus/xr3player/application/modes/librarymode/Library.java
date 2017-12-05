/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.modes.librarymode;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.FileCategory;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

/**
 * A class which hold all the functionality of a digital library.
 *
 * @author GOXR3PLUS
 */
public class Library extends StackPane {
	
	// --------------------------------------------
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label nameLabel;
	
	@FXML
	private Label ratingLabel;
	
	@FXML
	private Label informationLabel;
	
	@FXML
	private Label descriptionLabel;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private Label totalItemsLabel;
	
	@FXML
	private StackPane progressBarStackPane;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Label progressBarLabel;
	
	@FXML
	private StackPane selectionModeStackPane;
	
	@FXML
	private JFXCheckBox selectionModeCheckBox;
	
	// --------------------------------------------
	
	// private final CopyProgress copyService = new CopyProgress()
	
	// -------------------------------------------
	
	/** The logger for this class */
	private static final Logger logger = Logger.getLogger(Library.class.getName());
	
	/** The controller. */
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
	private StringProperty description;
	
	/**
	 * // create a rotation transform starting at 0 degrees, rotating about pivot point 0, 0.
	 */
	// Rotate rotationTransform = new Rotate(0, 0, 0)
	
	/**
	 * The Save Mode of the Library.
	 *
	 * @author GOXR3PLUS
	 */
	public enum SaveMode {
		
		/**
		 * Songs are not copied into the database so if they are deleted they don't exist anymore.
		 */
		ORIGINAL_PATH,
		
		/** Songs have been copied into the database. */
		DATABASE_PATH;
	}
	
	/** The save mode. */
	private SaveMode saveMode;
	
	/** The position. */
	private int position = -1;
	
	/** The name of the database image [Example : image.jpg ] */
	private String imageName;
	
	/** Define a pseudo class. */
	private static final PseudoClass OPENED_PSEUDO_CLASS = PseudoClass.getPseudoClass("opened");
	
	/** The opened. */
	private BooleanProperty opened = new BooleanPropertyBase(false) {
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
					nameLabel.textProperty().unbind();
					
					// !XPressed && Old name !=newName
					if (Main.renameWindow.wasAccepted() && !libraryName.equals(newName)) {
						
						// duplicate?
						if (! ( duplicate = Main.libraryMode.teamViewer.getViewer().getItemsObservableList().stream()
								.anyMatch(library -> library != Library.this && library.getLibraryName().equals(newName)) )) {
							
							try (PreparedStatement libURename = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET NAME=? WHERE NAME=? ;")) {
								// Update SQL Database
								libURename.setString(1, newName);
								libURename.setString(2, oldName);
								libURename.executeUpdate();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							
							// Rename library folder
							// new File(InfoTool.ab + oldName)
							// .renameTo(new
							// File(InfoTool.user_dbPath_With_Separator +
							// newName))
							
							// set the new name
							setLibraryName(newName);
							nameLabel.getTooltip().setText(newName);
							
							// Rename the image of library
							if (imageName != null)
								updateImagePathInDB(InfoTool.getImagesFolderAbsolutePathWithSeparator() + newName + "." + InfoTool.getFileExtension(getAbsoluteImagePath()), true,
										false);
							
							//Update the UserInformation properties file
							if (isOpened())
								Main.dbManager.storeOpenedLibraries();
						} else { // duplicate
							resetTheName();
							ActionTool.showNotification("Dublicate Name", "Name->" + newName + " is already used from another Library...", Duration.millis(2000),
									NotificationType.WARNING);
						}
					} else // X is pressed by user || oldName == newName
						resetTheName();
					
				} catch (Exception ex) {
					logger.log(Level.WARNING, "", ex);
					// etc
					resetTheName();
				} finally {
					
					// Rename Tab + Unbind Tab textProperty
					if (isOpened()) {
						if (Main.renameWindow.wasAccepted() && !newName.equals(oldName) && !duplicate)
							Main.libraryMode.multipleLibs.renameTab(oldName, getLibraryName());
						
						Main.libraryMode.multipleLibs.getTab(getLibraryName()).getTooltip().textProperty().unbind();
					}
					
					// Security Variable
					controller.renameWorking = false;
					
					// commit
					if (Main.renameWindow.wasAccepted() && !newName.equals(oldName) && !duplicate)
						Main.dbManager.commit();
				}
				
			} // rename window is still showing
		} // invalidated
		
		/**
		 * Resets the name if the user cancels the rename operation
		 */
		private void resetTheName() {
			nameLabel.setText(getLibraryName());
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
	 *            The library position inside
	 * @param imageName
	 *            The image name [example: image.jpg ]
	 * @param opened
	 *            the opened
	 */
	public Library(String libraryName, String dataBaseTableName, double stars, String dateCreated, String timeCreated, String description, int saveMode, int position,
			String imageName, boolean opened) {
		
		// ----------------------------------Initialize Variables-------------------------------------
		
		// LibraryName
		this.libraryName = libraryName;
		
		// DataBase TableName
		this.dataBaseTableName = dataBaseTableName;
		
		// Stars
		setStars(stars);
		
		// Date Created
		this.dateCreated = dateCreated != null ? dateCreated : InfoTool.getCurrentDate();
		
		// Hour Created
		this.timeCreated = timeCreated != null ? timeCreated : InfoTool.getLocalTime();
		
		// Description
		this.description = new SimpleStringProperty(description == null ? "" : description);
		
		// SaveMode
		this.saveMode = saveMode == 1 ? SaveMode.ORIGINAL_PATH : SaveMode.DATABASE_PATH;
		
		// Library Position in List
		this.position = position;
		
		// LibraryImage
		this.imageName = imageName;
		
		// isOpened
		this.opened.set(opened);
		
		// ----------------------------------FXMLLoader-------------------------------------
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "Library.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		// Add the rotationTransform effect
		// setEffect(rotationTransform)
		
		// Add the rotation transform
		// rotationTransform.pivotXProperty().bind(this.widthProperty().divide(2))
		// rotationTransform.pivotYProperty().bind(this.heightProperty().divide(2))
		// rotationTransform.setAxis(Rotate.Y_AXIS)
		// getTransforms().add(rotationTransform)
		
		// Controller
		this.controller = new SmartController(Genre.LIBRARYMEDIA, libraryName, dataBaseTableName);
		
		// ----------------------------------Load FXML-------------------------------------
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
		}
		
		// ----------------------------------Evemt Listeners-------------------------------------
		
		// --Scroll Listener
		setOnScroll(scroll -> updateStars(scroll.getDeltaY() > 0 ? starsProperty().get() + 0.5 : starsProperty().get() - 0.5));
		
		// --Key Listener
		setOnKeyReleased(this::onKeyReleased);
		
		// --Mouse Listener
		setOnMouseEntered(m -> {
			if (!isFocused())
				requestFocus();
		});
		
		// --Drag Over
		super.setOnDragOver(dragOver -> {
			
			// Source has files?
			if (dragOver.getDragboard().hasFiles())
				Main.libraryMode.teamViewer.getViewer().setCenterIndex(this.getPosition());
			
			// The drag must come from source other than the owner
			if (dragOver.getGestureSource() != controller.getTableViewer())// && dragOver.getGestureSource()!= controller.foldersMode)
				dragOver.acceptTransferModes(TransferMode.LINK);
			
		});
		
		// --Drag Dropped
		super.setOnDragDropped(drop -> {
			// Has Files? + isFree()?
			if (drop.getDragboard().hasFiles() && controller.isFree(true))
				controller.getInputService().start(drop.getDragboard().getFiles());
			
			drop.setDropCompleted(true);
		});
		
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
		//this.maxWidthProperty().bind(imageView.fitWidthProperty())
		//this.maxHeightProperty().bind(imageView.fitHeightProperty())
		
		// ImageView
		// imageView.fitWidthProperty().bind(this.prefWidthProperty())
		// imageView.fitHeightProperty().bind(this.prefHeightProperty())
		
		// Clip
		//Rectangle rect = new Rectangle();
		//rect.widthProperty().bind(widthProperty());
		//rect.heightProperty().bind(heightProperty());
		//rect.setArcWidth(30);
		//rect.setArcHeight(30);
		//rect.setEffect(new Reflection())
		//setClip(rect);
		
		// StackPane -> this
		//Reflection reflection = new Reflection()
		//reflection.setInput(new DropShadow(4, Color.FIREBRICK));
		//this.setEffect(reflection);
		
		// -----ImageView
		imageView.setImage(getImage());
		
		// -----NameLabel
		setLibraryName(libraryName);
		nameLabel.setText(libraryName);
		nameLabel.getTooltip().setText(libraryName);
		nameLabel.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.PRIMARY && m.getClickCount() == 2 && Main.libraryMode.teamViewer.getViewer().centerItemProperty().get() == Library.this)//Main.libraryMode.teamViewer.getViewer().getTimeline().getStatus() != Status.RUNNING)
				renameLibrary(nameLabel);
		});
		
		// -----RatingLabel
		ratingLabel.visibleProperty().bind(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty());
		ratingLabel.textProperty().bind(starsProperty().asString());
		ratingLabel.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.PRIMARY && Main.libraryMode.teamViewer.getViewer().centerItemProperty().get() == Library.this)
				updateLibraryStars(ratingLabel);
		});
		
		// ----InformationLabel
		informationLabel.setOnMouseReleased(m -> {
			if (Main.libraryMode.teamViewer.getViewer().centerItemProperty().get() == Library.this)
				Main.libraryMode.libraryInformation.showWindow(this);
		});
		
		// ----DescriptionLabel
		descriptionLabel.visibleProperty().bind(description.isEmpty().not().and(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty()));
		descriptionLabel.setOnMouseReleased(informationLabel.getOnMouseReleased());
		
		// ----totalItemsLabel
		
		totalItemsLabel.textProperty()
				.bind(Bindings.createStringBinding(() -> InfoTool.getNumberWithDots(controller.totalInDataBaseProperty().get()), controller.totalInDataBaseProperty()));
		totalItemsLabel.textProperty().addListener((observable , oldValue , newValue) -> Main.libraryMode.calculateEmptyLibraries());
		totalItemsLabel.visibleProperty().bind(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty());
		
		//I run this Thread to calculate the total entries of this library
		//because if the library is not opened they are not calculated
		new Thread(controller::calculateTotalEntries).start();
		
		// ----ProgressBarStackPane
		progressBarStackPane.setVisible(false);
		progressBar.setProgress(-1);
		// progressBar.progressProperty().bind(copyService.progressProperty());
		// progressBarLabel.textProperty()
		// .bind(Bindings.max(0,
		// progressBar.progressProperty()).multiply(100.00).asString("%.02f
		// %%"));
		
		// ---SelectionModeStackPane
		selectedProperty().bind(selectionModeCheckBox.selectedProperty());
		
		// Label error = new Label();
		// error.setOnDragDetected(drag -> {
		//
		// // Main.libraryMode.dragDetected=true
		//
		// /* Allow copy transfer mode */
		// Dragboard db = startDragAndDrop(TransferMode.COPY,
		// TransferMode.LINK);
		//
		// /* Put a String into the dragBoard */
		// ClipboardContent content = new ClipboardContent();
		// content.putString("#library#" + getLibraryName());
		//
		// /* Set the DragView */
		// WritableImage writableImage = new WritableImage((int)
		// nameField.getWidth(), (int) nameField.getHeight());
		// SnapshotParameters params = new SnapshotParameters();
		// // params.setFill(Color.TRANSPARENT)
		// params.setFill(Color.WHITE);
		// db.setDragView(nameField.snapshot(params, writableImage),
		// writableImage.getWidth() / 2, 0);
		//
		// db.setContent(content);
		// drag.consume();
		//
		// });
		//
		// error.setOnDragOver(drag -> {
		// // Main.libraryMode.dragDetected=false
		//
		// Dragboard db = drag.getDragboard();
		// if (db.hasString() && db.getString().contains("#library#"))
		// drag.acceptTransferModes(TransferMode.COPY);
		//
		// drag.consume();
		// });
		
	}
	
	/**
	 * Change the state of the Library from Normal to Selection Mode.
	 *
	 * @param way
	 *            the way
	 */
	public void goOnSelectionMode(boolean way) {
		selectionModeStackPane.setVisible(way);
	}
	
	/**
	 * Update the Stars of the Library.
	 *
	 * @param stars1
	 *            the stars
	 */
	public void updateStars(double stars1) {
		// An acceptable value has been given
		if (setStars(stars1))
			//Try
			try (PreparedStatement libUStars = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET STARS=? WHERE NAME=?;");) {
				
				// SQLITE COMMIT
				libUStars.setDouble(1, stars1);
				libUStars.setString(2, getLibraryName());
				libUStars.executeUpdate();
				
				//Commit
				Main.dbManager.commit();
			} catch (SQLException ex) {
				logger.log(Level.WARNING, "", ex);
			}
	}
	
	/**
	 * Stores the Library description into the database.
	 */
	public void updateDescription() {
		try (PreparedStatement libUDescription = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET DESCRIPTION=?" + " WHERE NAME=?;")) {
			
			// SQLITE
			libUDescription.setString(1, description.get());
			libUDescription.setString(2, getLibraryName());
			libUDescription.executeUpdate();
			Main.dbManager.commit();
			
		} catch (SQLException ex) {
			logger.log(Level.WARNING, "", ex);
		}
	}
	
	/**
	 * Make an update only if the library is in information mode.
	 */
	//    public void updateSettingsTotalLabel() {
	//	Main.libraryMode.settings.updateTotalItemsLabel(this);
	//    }
	
	/**
	 * Updates the position variable of Library in database so the next time viewer position it correct.
	 *
	 * @param newPosition
	 *            The new position of the Library
	 */
	public void updatePosition(int newPosition) {
		try (PreparedStatement libUPosition = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET POSITION=?  WHERE NAME=?;")) {
			position = newPosition;
			
			// SQLITE
			libUPosition.setInt(1, newPosition);
			libUPosition.setString(2, getLibraryName());
			libUPosition.executeUpdate();
		} catch (SQLException ex) {
			logger.log(Level.WARNING, "", ex);
		}
	}
	
	/**
	 * Updates the Image File.
	 *
	 * @param absoluteFilePath
	 *            The absolute path of the new image to the file system
	 * @param renameOperation
	 *            Is this a rename update ?
	 * @param commit
	 *            If true commit to database
	 */
	private boolean updateImagePathInDB(String absoluteFilePath , boolean renameOperation , boolean commit) {
		boolean success = true;
		
		try (PreparedStatement libUImage = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET LIBRARYIMAGE=?  WHERE NAME=?")) {
			
			// rename the old image file
			if (renameOperation && imageName != null) {
				
				// Do the rename procedure
				success = new File(getAbsoluteImagePath()).renameTo(new File(absoluteFilePath));
				
				// Change the image name
				imageName = InfoTool.getFileName(absoluteFilePath);
				
			} else { // Create new Image
				
				//				// Delete the [[old]] image if exist
				//				if (imageName != null && !new File(getAbsoluteImagePath()).delete())
				//					logger.log(Level.WARNING, "Failed to delete image for LibraryName=[" + getLibraryName() + "]");
				//				
				// Create the new image
				String newImageName = InfoTool.getImagesFolderAbsolutePathWithSeparator() + getLibraryName() + "." + InfoTool.getFileExtension(absoluteFilePath);
				
				// Change the image name
				imageName = InfoTool.getFileName(newImageName);
				
				//				// Do the copy procedure
				//				if (!ActionTool.copy(absolutePath, newImageName))
				//					logger.log(Level.WARNING, "Failed to create image for LibraryName=[" + getLibraryName() + "]");			
			}
			
			// SQLITE
			libUImage.setString(1, imageName);
			libUImage.setString(2, getLibraryName());
			libUImage.executeUpdate();
			if (commit)
				Main.dbManager.commit();
			
		} catch (SQLException ex) {
			success = false;
			logger.log(Level.WARNING, "", ex);
		}
		
		return success;
	}
	
	/**
	 * The user has the ability to change the Library Image
	 *
	 */
	public void setNewImage() {
		JavaFXTools.selectAndSaveImage(this.getLibraryName(), InfoTool.getImagesFolderAbsolutePathWithSeparator(), Main.specialChooser, Main.window).ifPresent(imageFile -> {
			updateImagePathInDB(imageFile.getAbsolutePath(), false, true);
			imageView.setImage(new Image(imageFile.toURI() + ""));
		});
	}
	
	/**
	 * Gives to Library the default image that i had set on resources.
	 */
	public void setDefaultImage() {
		
		try (PreparedStatement libUImage = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET LIBRARYIMAGE=?  WHERE NAME=?")) {
			
			// Ask if user is sure...
			if (ActionTool.doQuestion("Reset Image", "Reset to default the image of this library?", this, Main.window)) {
				
				// Delete the [[old]] image if exist
				if (imageName != null && !new File(getAbsoluteImagePath()).delete())
					logger.log(Level.WARNING, "Failed to delete image for LibraryName=[" + getLibraryName() + "]");
				
				// Set to null
				imageName = null;
				
				// Set the default
				imageView.setImage(getImage());
				
				// SQLITE
				libUImage.setString(1, imageName);
				libUImage.setString(2, getLibraryName());
				libUImage.executeUpdate();
				Main.dbManager.commit();
			}
			
		} catch (Exception ex) {
			logger.log(Level.WARNING, "", ex);
		}
	}
	
	/**
	 * Export the Library image.
	 */
	public void exportImage() {
		//imageName ?
		if (imageName == null)
			return;
		File file = Main.specialChooser.prepareToExportImage(Main.window, imageName);
		//File ?
		if (file == null)
			return;
		progressBarStackPane.setVisible(true);
		progressBarLabel.setText("Exporting image...");
		
		//Start a Thread to copy the File
		new Thread(() -> {
			if (!ActionTool.copy(getAbsoluteImagePath(), file.getAbsolutePath()))
				Platform.runLater(() -> ActionTool.showNotification("Exporting Library Image", "Failed to export library image for \nLibrary=[" + getLibraryName() + "]",
						Duration.millis(2500), NotificationType.SIMPLE));
			Platform.runLater(() -> progressBarStackPane.setVisible(false));
		}).start();
	}
	
	/**
	 * Set or not the libraryOpened.
	 *
	 * @param way
	 *            the way
	 * @param commit
	 *            the commit
	 */
	private void setLibraryOpened(boolean way , boolean commit) {
		
		try (PreparedStatement libUStatus = Main.dbManager.getConnection().prepareStatement("UPDATE LIBRARIES SET OPENED=? WHERE NAME=? ;")) {
			opened.set(way);
			
			//commit?
			if (commit) {
				libUStatus.setBoolean(1, way);
				libUStatus.setString(2, getLibraryName());
				libUStatus.executeUpdate();
				
				//Commit
				Main.dbManager.commit();
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}
	
	/**
	 * Renames the current Library.
	 * 
	 * @param n
	 *            The node based on which the Rename Window will be position
	 */
	public void renameLibrary(Node n) {
		//Free?
		if (!controller.isFree(true))
			return;
		
		// Security Variable
		controller.renameWorking = true;
		
		// Open the Window
		Main.renameWindow.show(getLibraryName(), n, "Library Renaming", FileCategory.DIRECTORY);
		
		// Bind 1
		Tab tab = Main.libraryMode.multipleLibs.getTab(getLibraryName());
		if (tab != null)
			tab.getTooltip().textProperty().bind(nameLabel.textProperty());
		
		// Bind 2
		nameLabel.textProperty().bind(Main.renameWindow.getInputField().textProperty());
		
		//Add Invalidation Listener
		Main.renameWindow.showingProperty().addListener(renameInvalidator);
		
	}
	
	/**
	 * Updates the LibraryStars.
	 * 
	 * @param n
	 *            The node based on which the Rename Window will be position
	 */
	protected void updateLibraryStars(Node n) {
		//Free?
		if (!controller.isFree(true))
			return;
		
		// Bind
		Main.libraryMode.libraryInformation.getStarsLabel().textProperty().bind(Main.starWindow.starsProperty().asString());
		
		Main.starWindow.show(getLibraryName(), starsProperty().get(), n);
		
		//Keep a reference to the previous stars
		double previousStars = stars.get();
		
		//Bind
		stars.bind(Main.starWindow.starsProperty());
		
		/***
		 * This InvalidationListener is used when i want to change the stars of the Library
		 */
		InvalidationListener updateStarsInvalidation = new InvalidationListener() {
			@Override
			public void invalidated(Observable o) {
				
				// Remove the listener
				Main.starWindow.getWindow().showingProperty().removeListener(this);
				
				// Remove Binding from Stars
				stars.unbind();
				
				// if !showing
				if (!Main.starWindow.getWindow().isShowing()) {
					
					//Unbind
					Main.libraryMode.libraryInformation.getStarsLabel().textProperty().unbind();
					
					//Was accepted
					if (Main.starWindow.wasAccepted())
						updateStars(Main.starWindow.getStars());
					else
						setStars(previousStars);
				}
				
			}
		};
		
		//Add Invalidation Listener
		Main.starWindow.getWindow().showingProperty().addListener(updateStarsInvalidation);
		
	}
	
	/**
	 * Delete the library.
	 */
	public void deleteLibrary(Node owner) {
		if (controller.isFree(true)
				&& ActionTool.doQuestion("Delete Library", "Confirm that you want to 'delete' this library,\n Name: [" + getLibraryName() + " ]", owner, Main.window)) {
			
			try {
				
				// Drop the database table
				Main.dbManager.getConnection().createStatement().execute("DROP TABLE '" + getDataBaseTableName() + "' ");
				
				// Delete the row from Libraries table
				Main.dbManager.getConnection().createStatement().executeUpdate("DELETE FROM LIBRARIES WHERE NAME='" + getLibraryName() + "' ");
				
				// Delete the folder with library name in database
				ActionTool.deleteFile(new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + getLibraryName()));
				
				// delete library image
				if (imageName != null && !new File(getAbsoluteImagePath()).delete())
					logger.log(Level.WARNING, "Failed to delete image for LibraryName=[" + getLibraryName() + "]");
				
				// opened? Yes=remove the tab
				if (isOpened())
					Main.libraryMode.multipleLibs.removeTab(getLibraryName());
				
				// Update the libraryViewer
				Main.libraryMode.teamViewer.getViewer().deleteItem(this);
				
				// Commit
				Main.dbManager.commit();
				
				//Update the UserInformation properties file
				if (isOpened())
					Main.dbManager.storeOpenedLibraries();
				
				//Recalculate those bindings
				Main.libraryMode.calculateOpenedLibraries();
				Main.libraryMode.calculateEmptyLibraries();
				
			} catch (SQLException sql) {
				logger.log(Level.WARNING, "\n", sql);
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
	public void openLibrary(boolean open , boolean firstLoadHack) {
		if (firstLoadHack) {
			setLibraryOpened(open, false);
			Main.libraryMode.multipleLibs.insertTab(this);
		} else {
			// Open
			if (open && !isOpened()) {
				setLibraryOpened(open, true);
				Main.libraryMode.multipleLibs.insertTab(this);
			} // Close 
			else if (!open && isOpened() && controller.isFree(true)) {
				setLibraryOpened(open, true);
				Main.libraryMode.multipleLibs.removeTab(getLibraryName());
			}
			
			//Update the UserInformation properties file
			Main.dbManager.storeOpenedLibraries();
			
			//Calculate opened libraries
			Main.libraryMode.calculateOpenedLibraries();
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
	private boolean setStars(double stars) {
		if (stars < 0.0 || stars > 5.0)
			return false;
		starsProperty().set(stars);
		return true;
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
		description.set(newDescription);
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
	
	/**
	 * Opened property.
	 *
	 * @return The Opened Property
	 */
	public BooleanProperty openedProperty() {
		if (opened == null)
			opened = new SimpleBooleanProperty(this, "opened", false);
		
		return opened;
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
	public boolean isOpened() {
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
	 * @return the ratingLabel
	 */
	public Label getRatingLabel() {
		return ratingLabel;
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
	
	/**
	 * Gets the image.
	 *
	 * @return The image of the Library
	 */
	public Image getImage() {
		
		if (imageName == null) {
			// Show warning Label
			warningLabel.setVisible(false);
			return LibraryMode.defaultImage;
			// return null;
		}
		if (!new File(getAbsoluteImagePath()).exists()) {
			//Show warning Label
			warningLabel.setVisible(true);
			return null;
		}
		
		//Hide warning Label
		warningLabel.setVisible(false);
		//Return the Image
		return new Image(new File(getAbsoluteImagePath()).toURI() + "");
		
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
	 * True if the total items of Library are 0
	 * 
	 * @return True if the total items of Library are 0
	 */
	public boolean isEmpty() {
		return getTotalEntries() == 0;
	}
	
	/**
	 * @return the totalItemsLabel
	 */
	public Label getTotalItemsLabel() {
		return totalItemsLabel;
	}
	
	/**
	 * Returns the absolute path of the Library Image in the operating system
	 *
	 * @return The absolute path of the Library Image in the operating system
	 */
	public String getAbsoluteImagePath() {
		return imageName == null ? null : InfoTool.getImagesFolderAbsolutePathWithSeparator() + imageName;
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
		return description.get();
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
	 *            An event which indicates that a keystroke occurred in a javafx.scene.Node.
	 */
	public void onKeyReleased(KeyEvent key) {
		if (Main.libraryMode.libraryInformation.isShowing() || getPosition() != Main.libraryMode.teamViewer.getViewer().getCenterIndex())
			return;
		
		//Check if Control is down
		if (key.isControlDown()) {
			
			KeyCode code = key.getCode();
			if (code == KeyCode.O)
				openLibrary(true, false);
			else if (code == KeyCode.C)
				openLibrary(false, false);
			else if (code == KeyCode.R)
				renameLibrary(nameLabel);
			else if (code == KeyCode.DELETE || code == KeyCode.D)
				deleteLibrary(this);
			else if (code == KeyCode.S)
				Main.libraryMode.libraryInformation.showWindow(this);
			else if (code == KeyCode.E)
				this.exportImage();
		} else if (key.getCode() == KeyCode.ENTER)
			openLibrary(!isOpened(), false);
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
