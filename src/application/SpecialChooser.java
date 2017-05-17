package application;

import java.io.File;
import java.util.List;

import application.tools.InfoTool;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * An implementation which combines FileChooser and DirectoryChooser.
 *
 * @author GOXR3PLUS
 */
public class SpecialChooser {

    /**
     * Last known directory for the FileChooser
     */
    private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<File> lastKnownMediaDirectoryProperty = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<File> lastKnownImageDirectoryProperty = new SimpleObjectProperty<>();

    /** The file chooser. */
    private FileChooser fileChooser = new FileChooser();
    private FileChooser mediaFileChooser = new FileChooser();
    private FileChooser imageFileChooser = new FileChooser();

    /** The folder chooser. */
    // private DirectoryChooser folderChooser = new DirectoryChooser()

    /**
     * Constructor
     */
    public SpecialChooser() {
	fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
	mediaFileChooser.initialDirectoryProperty().bindBidirectional(lastKnownMediaDirectoryProperty);
	imageFileChooser.initialDirectoryProperty().bindBidirectional(lastKnownImageDirectoryProperty);
    }

    // -----------------------------------------------------------------------------------------------------------------/
    /**
     * Show the dialog to connectedUser to select the dataBase that wants to import.
     *
     * @param window
     *            the window
     * @return the file
     */
    public File prepareToImportDataBase(Stage window) {
	fileChooser.getExtensionFilters().clear();
	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
	fileChooser.setTitle("Select the database zip folder(it must contains xr3sign.txt in order to be a valid)");
	fileChooser.setInitialFileName("example name (XR3Database.zip)");
	File file = fileChooser.showOpenDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownDirectoryProperty.setValue(file.getParentFile());
	}
	return file;
    }

    /**
     * Prepare to export dbManager.
     *
     * @param window
     *            the window
     * @return the file
     */
    public File prepareForExportDataBase(Stage window) {
	fileChooser.getExtensionFilters().clear();
	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
	fileChooser.setTitle("Export XR3Player database as a zip folder");
	fileChooser.setInitialFileName("XR3DataBase");
	File file = fileChooser.showSaveDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownDirectoryProperty.setValue(file.getParentFile());
	}
	return file;
    }

    // -----------------------------------------------------------------------------------------------------------------/

    /**
     * Prepares to save a file.
     *
     * @param window
     *            the window
     * @param imagePath
     * @return the file
     */
    public File prepareToExportImage(Stage window, String imagePath) {
	imageFileChooser.getExtensionFilters().clear();
	imageFileChooser.setTitle("Type a File Name and press save");
	imageFileChooser.setInitialFileName(InfoTool.getFileTitle(imagePath));
	imageFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Extension", "*." + InfoTool.getFileExtension(imagePath)));
	File file = imageFileChooser.showSaveDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownImageDirectoryProperty.setValue(file.getParentFile());
	}
	return file;
    }

    /**
     * Prepares to select an image.
     *
     * @param window
     *            the window
     * @return The Selected Image or Null if nothing is selected
     */
    public File prepareToSelectImage(Stage window) {
	imageFileChooser.getExtensionFilters().clear();
	imageFileChooser.getExtensionFilters()
		.addAll(new FileChooser.ExtensionFilter("All Images", new String[] { "*.png", "*.jpg", "*.jpeg", "*.gif" }));
	imageFileChooser.setTitle("Choose an Image");
	File file = imageFileChooser.showOpenDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownImageDirectoryProperty.setValue(file.getParentFile());
	}
	return file;
    }

    // -----------------------------------------------------------------------------------------------------------------/

    /**
     * Prepares to import multiple Song Files and Folders.
     *
     * @param window
     *            the window
     * @return the list
     */
    public List<File> prepareToImportSongFiles(Stage window) {
	mediaFileChooser.getExtensionFilters().clear();
	mediaFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*.mp3", "*.wav"));
	mediaFileChooser.setTitle("Select or Drag and Drop Files || Folders into the PlayList");
	List<File> files = mediaFileChooser.showOpenMultipleDialog(window);
	if (files != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownMediaDirectoryProperty.setValue(files.get(0).getParentFile());
	}
	return files;

    }

    /**
     * Shows the default FileExplorer so the user can select a song File.
     * 
     * @param window
     * @return The Selected file from the User
     */
    public File selectSongFile(Stage window) {
	mediaFileChooser.getExtensionFilters().clear();
	mediaFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav"));
	mediaFileChooser.setTitle("Choose Media File");
	File file = mediaFileChooser.showOpenDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownMediaDirectoryProperty.setValue(file.getParentFile());
	}
	return file;
    }

    /**
     * Showing the save Dialog.
     * 
     * @param initialFileName
     *
     * @return the file
     */
    public File showSaveDialog(String initialFileName) {
	fileChooser.getExtensionFilters().clear();
	fileChooser.setInitialFileName(initialFileName);
	return fileChooser.showSaveDialog(Main.window);
    }

    /**
     * Open a dialog that allows user to select a directory.
     *
     * @param window
     *            the window
     * @return the file
     */
    //    public File chooseDirectory(Stage window) {
    //	return folderChooser.showDialog(window)
    //    }

}
