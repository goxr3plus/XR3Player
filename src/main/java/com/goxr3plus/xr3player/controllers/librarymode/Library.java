/*
 *
 */
package com.goxr3plus.xr3player.controllers.librarymode;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.FileCategory;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController.WorkOnProgress;
import com.goxr3plus.xr3player.utils.general.DateTimeTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

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
    private CheckBox selectedCheckBox;

    @FXML
    private Label nameLabel;

    @FXML
    private Label dragAndDropLabel;

    // --------------------------------------------

    // private final CopyProgress copyService = new CopyProgress()

    // -------------------------------------------

    /**
     * The logger for this class
     */
    private static final Logger logger = Logger.getLogger(Library.class.getName());

    /**
     * The controller.
     */
    private final SmartController controller;

    /**
     * The library name.
     */
    private String libraryName;

    /**
     * The data base table name.
     */
    private final String dataBaseTableName;

    /**
     * The stars.
     */
    private DoubleProperty stars;


    /**
     * The date created.
     */
    private String dateCreated = "";

    /**
     * The time created.
     */
    private String timeCreated = "";

    /**
     * The description.
     */
    private final StringProperty description;

    /**
     * The Save Mode of the Library.
     *
     * @author GOXR3PLUS
     */
    public enum SaveMode {

        /**
         * Songs are not copied into the database so if they are deleted they don't
         * exist anymore.
         */
        ORIGINAL_PATH,

        /**
         * Songs have been copied into the database.
         */
        DATABASE_PATH;
    }

    /**
     * The save mode.
     */
    private final SaveMode saveMode;

    /**
     * The name of the database image [Example : image.jpg ]
     */
    private String imageName;

    /**
     * Define a pseudo class.
     */
    private static final PseudoClass OPENED_PSEUDO_CLASS = PseudoClass.getPseudoClass("opened");

    /**
     * The opened.
     */
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

    /**
     * This variable is used during the creation of a new library.
     */
    private final InvalidationListener renameInvalidator = new InvalidationListener() {
        @Override
        public void invalidated(final Observable observable) {

            ReadOnlyBooleanProperty p1;
            p1 = Main.renameWindow.showingProperty();
            p1.removeListener(this);
            // !Showing
            if (!Main.renameWindow.isShowing()) {

                final String oldName = getLibraryName(), newName = Main.renameWindow.getUserInput();
                boolean duplicate = false;

                try {

                    // Remove Bindings
                    nameLabel.textProperty().unbind();

                    // !XPressed && Old name !=newName
                    if (Main.renameWindow.wasAccepted() && !libraryName.equals(newName)) {

                        // duplicate?
                        if (!(duplicate = Main.libraryMode.viewer.getItemsObservableList().stream()
                                .anyMatch(library -> library != Library.this
                                        && ((Library) library).getLibraryName().equals(newName)))) {

                            try (PreparedStatement libURename = Main.dbManager.getConnection()
                                    .prepareStatement("UPDATE LIBRARIES SET NAME=? WHERE NAME=? ;")) {
                                // Update SQL Database
                                libURename.setString(1, newName);
                                libURename.setString(2, oldName);
                                libURename.executeUpdate();
                            } catch (final Exception param) {
                                param.printStackTrace();
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
                                updateImagePathInDB(DatabaseTool.getImagesFolderAbsolutePathWithSeparator() + newName + "."
                                        + IOInfo.getFileExtension(getAbsoluteImagePath()), true, false);

                            // Update the UserInformation properties file
                            if (isOpened())
                                Main.libraryMode.storeOpenedLibraries();
                        } else { // duplicate
                            resetTheName();
                            AlertTool.showNotification("Dublicate Name",
                                    "Name->" + newName + " is already used from another Library...",
                                    Duration.millis(2000), NotificationType.WARNING);
                        }
                    } else // X is pressed by user || oldName == newName
                        resetTheName();

                } catch (final Exception ex) {
                    logger.log(Level.WARNING, "", ex);
                    // etc
                    resetTheName();
                } finally {

                    // Rename Tab + Unbind Tab textProperty
                    if (isOpened()) {
                        if (Main.renameWindow.wasAccepted() && !newName.equals(oldName) && !duplicate)
                            Main.libraryMode.openedLibrariesViewer.renameTab(oldName, getLibraryName());

                        Main.libraryMode.openedLibrariesViewer.getTab(getLibraryName()).getTooltip().textProperty()
                                .unbind();
                    }

                    // Security Variable
                    controller.workOnProgress = WorkOnProgress.NONE;

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
     * @param libraryName       the library name
     * @param dataBaseTableName the data base table name
     * @param stars             the stars
     * @param dateCreated       the date created
     * @param timeCreated       the time created
     * @param description       the description
     * @param saveMode          the save mode
     * @param position          The library position inside
     * @param imageName         The image name [example: image.jpg ]
     * @param opened            the opened
     */
    public Library(final String libraryName, final String dataBaseTableName, final double stars,
                   final String dateCreated, final String timeCreated, final String description, final int saveMode,
                   final int position, final String imageName, final boolean opened) {

        // ----------------------------------Initialize
        // Variables-------------------------------------

        // LibraryName
        this.libraryName = libraryName;

        // DataBase TableName
        this.dataBaseTableName = dataBaseTableName;

        // Stars
        setStars(stars);

        // Date Created
        this.dateCreated = dateCreated != null ? dateCreated : DateTimeTool.getCurrentDate();

        // Hour Created
        this.timeCreated = timeCreated != null ? timeCreated : DateTimeTool.getLocalTime();

        // Description
        this.description = new SimpleStringProperty(description == null ? "" : description);

        // SaveMode
        this.saveMode = saveMode == 1 ? SaveMode.ORIGINAL_PATH : SaveMode.DATABASE_PATH;

        // Library Position in List
        // this.position = position;

        // LibraryImage
        this.imageName = imageName;

        // isOpened
        this.opened.set(opened);

        // ----------------------------------FXMLLoader-------------------------------------

        final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.LIBRARIES_FXMLS + "Library.fxml"));
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

        // ----------------------------------Load
        // FXML-------------------------------------

        try {
            loader.load();
        } catch (final IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }

        // ----------------------------------Evemt
        // Listeners-------------------------------------

        // --Scroll Listener
        // setOnScroll(scroll -> updateStars(scroll.getDeltaY() > 0 ?
        // starsProperty().get() + 0.5 : starsProperty().get() - 0.5))

        // --Key Listener
        setOnKeyReleased(this::onKeyReleased);

        // --Mouse Listener
        setOnMouseEntered(m -> {
            if (!isFocused())
                requestFocus();
        });

        // --Drag Over
        setOnDragOver(event -> {
            // Source has files?
            if (event.getDragboard().hasFiles())
                Main.libraryMode.viewer.setCenterItem(this);

            // The drag must come from source other than the owner
            if (event.getGestureSource() != controller.getNormalModeMediaTableViewer().getTableView()
                    && event.getGestureSource() != controller.getFoldersMode()
                    && event.getGestureSource() != controller.getFiltersMode().getMediaTableViewer().getTableView())
                dragAndDropLabel.setVisible(true);

        });

        // dragAndDropLabel
        dragAndDropLabel.setVisible(false);
        dragAndDropLabel.setOnDragOver(event -> {

            // Source has files?
            if (event.getDragboard().hasFiles())
                Main.libraryMode.viewer.setCenterItem(this);

            // The drag must come from source other than the owner
            if (event.getGestureSource() != controller.getNormalModeMediaTableViewer().getTableView())// &&
                // dragOver.getGestureSource()!=
                // controller.foldersMode)
                event.acceptTransferModes(TransferMode.LINK);

        });
        dragAndDropLabel.setOnDragDropped(event -> {
            // Has Files? + isFree()?
            if (event.getDragboard().hasFiles() && controller.isFree(true))
                controller.getInputService().start(event.getDragboard().getFiles());

            event.setDropCompleted(true);
        });
        dragAndDropLabel.setOnDragExited(event -> {
            dragAndDropLabel.setVisible(false);
            event.consume();
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
        // this.maxWidthProperty().bind(imageView.fitWidthProperty())
        // this.maxHeightProperty().bind(imageView.fitHeightProperty())

        // ImageView
        // imageView.fitWidthProperty().bind(this.prefWidthProperty())
        // imageView.fitHeightProperty().bind(this.prefHeightProperty())

        // Clip
        // Rectangle rect = new Rectangle();
        // rect.widthProperty().bind(widthProperty());
        // rect.heightProperty().bind(heightProperty());
        // rect.setArcWidth(30);
        // rect.setArcHeight(30);
        // rect.setEffect(new Reflection())
        // setClip(rect);

        // StackPane -> this
        // Reflection reflection = new Reflection()
        // reflection.setInput(new DropShadow(4, Color.FIREBRICK));
        // this.setEffect(reflection);

        // Selected Property
        goOnSelectionMode(false);
        setSelected(false);
        // selected.addListener((observable , oldValue , newValue) -> {
        // if (newValue)
        // ( (DropShadow) super.getEffect() ).setColor(Color.web("#1bb2d7"));
        // else
        // ( (DropShadow) super.getEffect() ).setColor(Color.BLACK);
        // });

        // -----ImageView
        imageView.setImage(getImage());

        // -----NameLabel
        setLibraryName(libraryName);
        nameLabel.setText(libraryName);
        nameLabel.getTooltip().setText(libraryName);
        nameLabel.setOnMouseReleased(m -> {
            if (m.getButton() == MouseButton.PRIMARY && m.getClickCount() == 2
                    && Main.libraryMode.viewer.centerItemProperty().get() == Library.this)// Main.libraryMode.teamViewer.getTimeline().getStatus()
                // != Status.RUNNING)
                renameLibrary(nameLabel);
        });

        // -----RatingLabel
        ratingLabel.visibleProperty()
                .bind(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty());
        ratingLabel.textProperty().bind(starsProperty().asString());
        ratingLabel.setOnMouseReleased(m -> {
            if (m.getButton() == MouseButton.PRIMARY
                    && Main.libraryMode.viewer.centerItemProperty().get() == Library.this)
                updateLibraryStars(ratingLabel);
        });

        // ----InformationLabel
        informationLabel.setOnMouseReleased(m -> {
            if (Main.libraryMode.viewer.centerItemProperty().get() == Library.this)
                Main.libraryMode.libraryInformation.showWindow(this);
        });

        // ----DescriptionLabel
        descriptionLabel.visibleProperty().bind(description.isEmpty().not()
                .and(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty()));
        descriptionLabel.setOnMouseReleased(informationLabel.getOnMouseReleased());

        // ----totalItemsLabel

        totalItemsLabel.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> InfoTool.getNumberWithDots(controller.totalInDataBaseProperty().get()),
                        controller.totalInDataBaseProperty()));
        totalItemsLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> Main.libraryMode.calculateEmptyLibraries());
        totalItemsLabel.visibleProperty()
                .bind(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty());

        // I run this Thread to calculate the total entries of this library
        // because if the library is not opened they are not calculated
        new Thread(controller::calculateTotalEntries).start();

        // ----ProgressBarStackPane
        // progressBarStackPane.setVisible(false);
        // progressBar.setProgress(-1);
        // progressBar.progressProperty().bind(copyService.progressProperty());
        // progressBarLabel.textProperty()
        // .bind(Bindings.max(0,
        // progressBar.progressProperty()).multiply(100.00).asString("%.02f
        // %%"));

        // ---SelectionModeStackPane
        // selectedProperty().bind(selectionModeCheckBox.selectedProperty());

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
     * @param way the way
     */
    public void goOnSelectionMode(final boolean way) {
        selectedCheckBox.setVisible(way);
    }

    /**
     * Update the Stars of the Library.
     *
     * @param stars the stars
     */
    public void updateStars(final double stars) {
        // An acceptable value has been given
        if (setStars(stars)) {
            // Try
            try (PreparedStatement libUStars = Main.dbManager.getConnection()
                    .prepareStatement("UPDATE LIBRARIES SET STARS=? WHERE NAME=?;");) {

                // SQLITE COMMIT
                libUStars.setDouble(1, stars);
                libUStars.setString(2, getLibraryName());
                libUStars.executeUpdate();

                // Commit
                Main.dbManager.commit();

                // Sort if comparator is selected
                if (Main.libraryMode.getSelectedSortToggleText().contains("Stars"))
                    Main.libraryMode.viewer.sortByComparator(Main.libraryMode.getSortComparator());

            } catch (final SQLException ex) {
                logger.log(Level.WARNING, "", ex);
            }
        }
    }

    /**
     * Stores the Library description into the database.
     */
    public void updateDescription() {
        try (PreparedStatement libUDescription = Main.dbManager.getConnection()
                .prepareStatement("UPDATE LIBRARIES SET DESCRIPTION=?" + " WHERE NAME=?;")) {

            // SQLITE
            libUDescription.setString(1, description.get());
            libUDescription.setString(2, getLibraryName());
            libUDescription.executeUpdate();
            Main.dbManager.commit();

        } catch (final SQLException ex) {
            logger.log(Level.WARNING, "", ex);
        }
    }

    /**
     * Updates the Image File.
     *
     * @param absoluteFilePath The absolute path of the new image to the file system
     * @param renameOperation  Is this a rename update ?
     * @param commit           If true commit to database
     */
    private boolean updateImagePathInDB(final String absoluteFilePath, final boolean renameOperation,
                                        final boolean commit) {
        boolean success = true;

        try (PreparedStatement libUImage = Main.dbManager.getConnection()
                .prepareStatement("UPDATE LIBRARIES SET LIBRARYIMAGE=?  WHERE NAME=?")) {

            // rename the old image file
            if (renameOperation && imageName != null) {

                // Do the rename procedure
                success = new File(getAbsoluteImagePath()).renameTo(new File(absoluteFilePath));

                // Change the image name
                imageName = IOInfo.getFileName(absoluteFilePath);

            } else { // Create new Image

                // Create the new image
                final String newImageName = DatabaseTool.getImagesFolderAbsolutePathWithSeparator() + getLibraryName() + "."
                        + IOInfo.getFileExtension(absoluteFilePath);

                // Change the image name
                imageName = IOInfo.getFileName(newImageName);

            }

            // SQLITE
            libUImage.setString(1, imageName);
            libUImage.setString(2, getLibraryName());
            libUImage.executeUpdate();
            if (commit)
                Main.dbManager.commit();

        } catch (final SQLException ex) {
            success = false;
            logger.log(Level.WARNING, "", ex);
        }

        return success;
    }

    /**
     * The user has the ability to change the Library Image
     */
    public void setNewImage() {
        JavaFXTool.selectAndSaveImage(this.getLibraryName(), DatabaseTool.getImagesFolderAbsolutePathWithSeparator(),
                Main.specialChooser, Main.window).ifPresent(imageFile -> {
            updateImagePathInDB(imageFile.getAbsolutePath(), false, true);
            imageView.setImage(new Image(imageFile.toURI() + ""));
        });
    }

    /**
     * Gives to Library the default image that i had set on resources.
     */
    public void setDefaultImage() {

        try (PreparedStatement libUImage = Main.dbManager.getConnection()
                .prepareStatement("UPDATE LIBRARIES SET LIBRARYIMAGE=?  WHERE NAME=?")) {

            // Ask if user is sure...
            if (AlertTool.doQuestion("Reset Image", "Reset to default the image of this library?", this, Main.window)) {

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

        } catch (final Exception ex) {
            logger.log(Level.WARNING, "", ex);
        }
    }

    /**
     * Export the Library image.
     */
    public void exportImage() {
        // imageName ?
        if (imageName == null)
            return;
        final File file = Main.specialChooser.prepareToExportImage(Main.window, imageName);
        // File ?
        if (file == null)
            return;

        // Start a Thread to copy the File
        new Thread(() -> {
            if (!IOAction.copy(getAbsoluteImagePath(), file.getAbsolutePath())) {
                Platform.runLater(() -> AlertTool.showNotification("Exporting Library Image",
                        "Failed to export library image for \nLibrary=[" + getLibraryName() + "]",
                        Duration.millis(2500), NotificationType.SIMPLE));
            } else
                Platform.runLater(() -> AlertTool.showNotification("Exported Image",
                        "Successfully exported library image", Duration.millis(2500), NotificationType.SUCCESS));
        }).start();
    }

    /**
     * Set or not the libraryOpened.
     *
     * @param way    the way
     * @param commit the commit
     */
    private void setLibraryOpened(final boolean way, final boolean commit) {

        try (PreparedStatement libUStatus = Main.dbManager.getConnection()
                .prepareStatement("UPDATE LIBRARIES SET OPENED=? WHERE NAME=? ;")) {
            opened.set(way);

            // commit?
            if (commit) {
                libUStatus.setBoolean(1, way);
                libUStatus.setString(2, getLibraryName());
                libUStatus.executeUpdate();

                // Commit
                Main.dbManager.commit();
            }
        } catch (final SQLException sql) {
            sql.printStackTrace();
        }
    }

    /**
     * Renames the current Library.
     *
     * @param n The node based on which the Rename Window will be position
     */
    public void renameLibrary(final Node n) {
        // Free?
        if (!controller.isFree(true))
            return;

        // Security Variable
        controller.workOnProgress = WorkOnProgress.RENAMING_LIBRARY;

        // Open the Window
        Main.renameWindow.show(getLibraryName(), n, "Library Renaming", FileCategory.DIRECTORY);

        // Bind 1
        final Tab tab = Main.libraryMode.openedLibrariesViewer.getTab(getLibraryName());
        if (tab != null)
            tab.getTooltip().textProperty().bind(nameLabel.textProperty());

        // Bind 2
        nameLabel.textProperty().bind(Main.renameWindow.getInputField().textProperty());

        // Add Invalidation Listener
        Main.renameWindow.showingProperty().addListener(renameInvalidator);

    }

    /**
     * Updates the LibraryStars.
     *
     * @param n The node based on which the Rename Window will be position
     */
    protected void updateLibraryStars(final Node n) {
        // Free?
        if (!controller.isFree(true))
            return;

        // Bind
        Main.libraryMode.libraryInformation.getStarsLabel().textProperty()
                .bind(Main.starWindow.starsProperty().asString());

        Main.starWindow.show(getLibraryName(), starsProperty().get(), n);

        // Keep a reference to the previous stars
        final double previousStars = stars.get();

        // Bind
        stars.bind(Main.starWindow.starsProperty());

        /***
         * This InvalidationListener is used when i want to change the stars of the
         * Library
         */
        final InvalidationListener updateStarsInvalidation = new InvalidationListener() {
            @Override
            public void invalidated(final Observable o) {

                // Remove the listener
                Main.starWindow.getWindow().showingProperty().removeListener(this);

                // Remove Binding from Stars
                stars.unbind();

                // if !showing
                if (!Main.starWindow.getWindow().isShowing()) {

                    // Unbind
                    Main.libraryMode.libraryInformation.getStarsLabel().textProperty().unbind();

                    // Was accepted
                    if (Main.starWindow.wasAccepted())
                        updateStars(Main.starWindow.getStars());
                    else
                        setStars(previousStars);
                }

            }
        };

        // Add Invalidation Listener
        Main.starWindow.getWindow().showingProperty().addListener(updateStarsInvalidation);

    }

    /**
     * Delete the library.
     */
    public void deleteLibrary(final Node node, final boolean byPassQuestion) {
        boolean questionPass = true;

        if (!byPassQuestion)
            questionPass = AlertTool.doQuestion("Delete Library",
                    "Confirm that you want to 'delete' this library,\n Name: [" + getLibraryName() + " ]", node,
                    Main.window);

        if (controller.isFree(true) && questionPass) {

            try {

                // Drop the database table
                Main.dbManager.getConnection().createStatement()
                        .execute("DROP TABLE '" + getDataBaseTableName() + "' ");

                // Delete the row from Libraries table
                Main.dbManager.getConnection().createStatement()
                        .executeUpdate("DELETE FROM LIBRARIES WHERE NAME='" + getLibraryName() + "' ");

                // Delete the folder with library name in database
                IOAction.deleteFile(new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator() + getLibraryName()));

                // delete library image
                if (imageName != null && !new File(getAbsoluteImagePath()).delete())
                    logger.log(Level.WARNING, "Failed to delete image for LibraryName=[" + getLibraryName() + "]");

                // opened? Yes=remove the tab
                if (isOpened())
                    Main.libraryMode.openedLibrariesViewer.removeTab(getLibraryName());

                // This must happen only one time when multiple libraries are being deleted
                // Or else the application will look garbage
                if (!byPassQuestion)
                    finalizeLibraryDelete(null);

            } catch (final SQLException sql) {
                logger.log(Level.WARNING, "\n", sql);
            }

        }
    }

    /**
     * Helper method which is used for deleting mutliple libraries
     */
    public void finalizeLibraryDelete(final List<Node> list) {

        // Update the libraryViewer
        if (list == null)
            Main.libraryMode.viewer.deleteItem(this);
        else
            Main.libraryMode.viewer.deleteItems(list);

        // Commit
        Main.dbManager.commit();

        // Update the UserInformation properties file
        Main.libraryMode.storeOpenedLibraries();

        // Recalculate those bindings
        Main.libraryMode.calculateOpenedLibraries();
        Main.libraryMode.calculateEmptyLibraries();
    }

    public enum LibraryStatus {
        OPENED, CLOSED;
    }

    /**
     * Opens the Library.
     *
     * @param status
     * @param firstLoadHack the first load hack
     */
    public void setLibraryStatus(final LibraryStatus status, final boolean firstLoadHack) {
        final boolean open = (status == LibraryStatus.OPENED);

        if (firstLoadHack) {
            setLibraryOpened(open, false);
            Main.libraryMode.openedLibrariesViewer.insertTab(this);
        } else {
            // Open
            if (open && !isOpened()) {
                setLibraryOpened(true, true);
                Main.libraryMode.openedLibrariesViewer.insertTab(this);
            } // Close
            else if (!open && isOpened() && controller.isFree(true)) {
                setLibraryOpened(false, true);
                Main.libraryMode.openedLibrariesViewer.removeTab(getLibraryName());
            }

            // Update the UserInformation properties file
            Main.libraryMode.storeOpenedLibraries();

            // Calculate opened libraries
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
     * @param newName the new library name
     */
    private void setLibraryName(final String newName) {
        libraryName = newName;
        controller.setName(newName);

        // Sort if comparator is selected
        if (Main.libraryMode.getSelectedSortToggleText().contains("Name"))
            Main.libraryMode.viewer.sortByComparator(Main.libraryMode.getSortComparator());
    }

    /**
     * Set the stars of the library.
     *
     * @param stars the new stars
     */
    private boolean setStars(final double stars) {
        if (stars < 0.0 || stars > 5.0)
            return false;
        starsProperty().set(stars);
        return true;
    }

    /**
     * Set if the library is selected or not.
     *
     * @param selected the new selected
     */
    public void setSelected(final boolean selected) {
        selectedCheckBox.setSelected(selected);
    }

    public boolean isSelected() {
        return selectedCheckBox.isSelected();
    }

    /**
     * Set the new Description of the Library.
     *
     * @param newDescription the new description
     */
    public void setDescription(final String newDescription) {
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
        }
        if (!new File(getAbsoluteImagePath()).exists()) {
            // Show warning Label
            warningLabel.setVisible(true);
            return null;
        }

        // Hide warning Label
        warningLabel.setVisible(false);
        // Return the Image
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
        return imageName == null ? null : DatabaseTool.getImagesFolderAbsolutePathWithSeparator() + imageName;
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
     * @param key An event which indicates that a keystroke occurred in a
     *            javafx.scene.Node.
     */
    public void onKeyReleased(final KeyEvent key) {
        if (Main.libraryMode.libraryInformation.isShowing() || Main.libraryMode.viewer.isCenterItem(this))
            return;

        // Check if Control is down
        if (key.isControlDown()) {

            final KeyCode code = key.getCode();
            if (code == KeyCode.O)
                setLibraryStatus(LibraryStatus.OPENED, false);
            else if (code == KeyCode.C)
                setLibraryStatus(LibraryStatus.CLOSED, false);
            else if (code == KeyCode.R)
                renameLibrary(nameLabel);
            else if (code == KeyCode.DELETE || code == KeyCode.D)
                Main.libraryMode.deleteLibraries(this, null);
            else if (code == KeyCode.I)
                Main.libraryMode.libraryInformation.showWindow(this);
            else if (code == KeyCode.E)
                exportImage();
        } else if (key.getCode() == KeyCode.ENTER) {
            setLibraryStatus(isOpened() ? LibraryStatus.CLOSED : LibraryStatus.OPENED, false);
        } else if (key.getCode() == KeyCode.DELETE) {
            Main.libraryMode.deleteLibraries(this, null);
        }
    }


}
