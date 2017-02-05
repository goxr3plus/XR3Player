package windows;

import java.io.File;
import java.util.List;

import application.Main;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tools.InfoTool;

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

    /** The file chooser. */
    private FileChooser fileChooser = new FileChooser();

    /** The folder chooser. */
   // private DirectoryChooser folderChooser = new DirectoryChooser()

    /**
     * Constructor
     */
    public SpecialChooser() {
	fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
    }

    // -----------------------------------------------------------------------------------------------------------------/
    /**
     * Show the dialog to connectedUser to select the dataBase that wants to
     * import.
     *
     * @param window
     *            the window
     * @return the file
     */
    public File prepareToImportDataBase(Stage window) {
	fileChooser.getExtensionFilters().clear();
	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
	fileChooser.setTitle("Select the (XR3DataBase).zip");
	fileChooser.setInitialFileName("XR3DataBase");
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
	fileChooser.setTitle("Export dbManager");
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
	fileChooser.getExtensionFilters().clear();
	fileChooser.setTitle("Export Image");
	fileChooser.setInitialFileName(InfoTool.getFileTitle(imagePath));
	fileChooser.getExtensionFilters()
		.add(new FileChooser.ExtensionFilter("Extension", "*." + InfoTool.getFileExtension(imagePath)));
	File file = fileChooser.showSaveDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownDirectoryProperty.setValue(file.getParentFile());
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
	fileChooser.getExtensionFilters().clear();
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("All Images", new String[] { "*.png", "*.jpg", "*.jpeg", "*.gif" }));
	fileChooser.setTitle("Select Cover Image");
	File file = fileChooser.showOpenDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownDirectoryProperty.setValue(file.getParentFile());
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
	fileChooser.getExtensionFilters().clear();
	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*.mp3", "*.wav"));
	fileChooser.setTitle("Select Folders and Files");
	List<File> files = fileChooser.showOpenMultipleDialog(window);
	if (files != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownDirectoryProperty.setValue(files.get(0).getParentFile());
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
	fileChooser.getExtensionFilters().clear();
	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav"));
	fileChooser.setTitle("Select any Music File");
	File file = fileChooser.showOpenDialog(window);
	if (file != null) {
	    // Set the property to the directory of the chosenFile so the
	    // fileChooser will open here next
	    lastKnownDirectoryProperty.setValue(file.getParentFile());
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
