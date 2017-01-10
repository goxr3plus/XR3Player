package windows;

import java.io.File;
import java.util.List;

import application.Main;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tools.InfoTool;

/**
 * An implementation which combines FileChooser and DirectoryChooser.
 *
 * @author GOXR3PLUS
 */
public class SpecialChooser {
	
	/** The file chooser. */
	private FileChooser fileChooser = new FileChooser();
	
	/** The folder chooser. */
	private DirectoryChooser folderChooser = new DirectoryChooser();
	
	// -----------------------------------------------------------------------------------------------------------------/
	/**
	 * Show the dialog to connectedUser to select the dataBase that wants to
	 * import.
	 *
	 * @param window the window
	 * @return the file
	 */
	public File prepareToImportDataBase(Stage window) {
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
		fileChooser.setTitle("Select the (XR3DataBase).zip");
		fileChooser.setInitialFileName("XR3DataBase");
		return fileChooser.showOpenDialog(window);
	}
	
	/**
	 * Prepare to export dbManager.
	 *
	 * @param window the window
	 * @return the file
	 */
	public File prepareForExportDataBase(Stage window) {
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
		fileChooser.setTitle("Export dbManager");
		fileChooser.setInitialFileName("XR3DataBase");
		return fileChooser.showSaveDialog(window);
	}
	
	// -----------------------------------------------------------------------------------------------------------------/
	
	/**
	 * Prepares to save a file.
	 *
	 * @param window the window
	 * @param imagePath 
	 * @return the file
	 */
	public File prepareToExportImage(Stage window , String imagePath) {
		fileChooser.getExtensionFilters().clear();
		fileChooser.setTitle("Export Image");
		fileChooser.setInitialFileName(InfoTool.getFileTitle(imagePath));
		fileChooser.getExtensionFilters()
		        .add(new FileChooser.ExtensionFilter("Extension","*." + InfoTool.getFileExtension(imagePath)));
		return fileChooser.showSaveDialog(window);
	}
	
	/**
	 * Prepares to select an image.
	 *
	 * @param window the window
	 * @return The Selected Image or Null if nothing is selected
	 */
	public File prepareToSelectImage(Stage window) {
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(
		        new FileChooser.ExtensionFilter("All Images", new String[]{ "*.png" , "*.jpg" , "*.jpeg" , "*.gif" }));
		fileChooser.setTitle("Select Cover Image");
		return fileChooser.showOpenDialog(window);
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------/
	
	/**
	 * Prepares to import multiple Song Files and Folders.
	 *
	 * @param window the window
	 * @return the list
	 */
	public List<File> prepareToImportSongFiles(Stage window) {
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*.mp3", "*.wav"));
		fileChooser.setTitle("Select Folders and Files");
		return fileChooser.showOpenMultipleDialog(window);
		
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
		return fileChooser.showOpenDialog(window);
	}
	
	/**
	 * Showing the save Dialog.
	 *
	 * @return the file
	 */
	public File showSaveDialog(String initialFileName) {
		fileChooser.getExtensionFilters().clear();
		fileChooser.setInitialFileName(initialFileName);
		return fileChooser.showSaveDialog(Main.window);
	}
	
	/**
	 * Showing the open Dialog.
	 *
	 * @return the file
	 */
	public File showOpenDialog() {
		fileChooser.getExtensionFilters().clear();
		return fileChooser.showOpenDialog(Main.window);
	}
	
	/**
	 * Open a dialog that allows user to select a directory.
	 *
	 * @param window the window
	 * @return the file
	 */
	public File chooseDirectory(Stage window) {
		return folderChooser.showDialog(window);
	}
	
}
