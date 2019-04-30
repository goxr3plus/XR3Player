package com.goxr3plus.xr3player.controllers.windows;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;
import com.goxr3plus.xr3player.utils.io.IOInfo;

/**
 * An implementation which combines FileChooser and DirectoryChooser.
 *
 * @author GOXR3PLUS
 */
public class FileAndFolderChooser {

	private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();
	private static SimpleObjectProperty<File> lastKnownDBDirectoryProperty = new SimpleObjectProperty<>();
	private static SimpleObjectProperty<File> lastKnownMediaDirectoryProperty = new SimpleObjectProperty<>();
	private static SimpleObjectProperty<File> lastKnownImageDirectoryProperty = new SimpleObjectProperty<>();

	private final DirectoryChooser directoryChooser = new DirectoryChooser();
	private final FileChooser databaseFolderChooser = new FileChooser();
	private final FileChooser mediaFileChooser = new FileChooser();
	private final FileChooser imageFileChooser = new FileChooser();

	private final ExtensionFilter audioFilter;

	/**
	 * Constructor
	 */
	public FileAndFolderChooser() {
		directoryChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
		databaseFolderChooser.initialDirectoryProperty().bindBidirectional(lastKnownDBDirectoryProperty);
		mediaFileChooser.initialDirectoryProperty().bindBidirectional(lastKnownMediaDirectoryProperty);
		imageFileChooser.initialDirectoryProperty().bindBidirectional(lastKnownImageDirectoryProperty);

		// Special Audio Files Filter
		audioFilter = new FileChooser.ExtensionFilter("Audio Files",
				Stream.of(ExtensionTool.POPULAR_AUDIO_EXTENSIONS_LIST, ExtensionTool.POPULAR_VIDEO_EXTENSIONS_LIST)
						.flatMap(List::stream).map(m -> "*." + m).collect(Collectors.toList()));
	}

	// -----------------------------------------------------------------------------------------------------------------/
	/**
	 * Show the dialog to connectedUser to select the dataBase that wants to import.
	 *
	 * @param window the window
	 * @return the file
	 */
	public File selectDBFile(final Stage window) {
		databaseFolderChooser.getExtensionFilters().clear();
		databaseFolderChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
		databaseFolderChooser.setTitle("Select the database zip folder");
		databaseFolderChooser.setInitialFileName("example name (XR3Database.zip)");
		final File file = databaseFolderChooser.showOpenDialog(window);
		if (file != null) {
			// Set the property to the directory of the chosenFile so the
			// fileChooser will open here next
			lastKnownDBDirectoryProperty.setValue(file.getParentFile());
		}
		return file;
	}

	/**
	 * Prepare to export dbManager.
	 *
	 * @param window the window
	 * @return the file
	 */
	public File exportDBFile(final Stage window) {
		databaseFolderChooser.getExtensionFilters().clear();
		databaseFolderChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
		databaseFolderChooser.setTitle("Export XR3Player database as a zip folder");
		databaseFolderChooser.setInitialFileName("XR3DataBase");
		final File file = databaseFolderChooser.showSaveDialog(window);
		if (file != null) {
			// Set the property to the directory of the chosenFile so the
			// fileChooser will open here next
			lastKnownDBDirectoryProperty.setValue(file.getParentFile());
		}
		return file;
	}

	/**
	 * Show's a dialog that allows user to select any directory from the operating
	 * system
	 *
	 * @param window the window
	 * @return the file
	 */
	public File selectFolder(final Stage window) {
		directoryChooser.setTitle("Select a Folder");
		final File file = directoryChooser.showDialog(window);
		if (file != null) {
			// Set the property to the directory of the chosenFile so the
			// fileChooser will open here next
			lastKnownDirectoryProperty.setValue(file.getParentFile());
		}
		return file;
	}

	// -----------------------------------------------------------------------------------------------------------------/

	/**
	 * Prepares to save an ImageFile
	 *
	 * @param window    the window
	 * @param imagePath
	 * @return the file
	 */
	public File prepareToExportImage(final Stage window, final String imagePath) {
		imageFileChooser.getExtensionFilters().clear();
		imageFileChooser.setTitle("Type a File Name and press save");
		imageFileChooser.setInitialFileName(IOInfo.getFileTitle(imagePath));
		imageFileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Extension", "*." + IOInfo.getFileExtension(imagePath)));
		final File file = imageFileChooser.showSaveDialog(window);
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
	 * @param window the window
	 * @return The Selected Image or Null if nothing is selected
	 */
	public File prepareToSelectImage(final Stage window) {
		imageFileChooser.getExtensionFilters().clear();
		imageFileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", new String[] { "*.png", "*.jpg", "*.jpeg", "*.gif" }));
		imageFileChooser.setTitle("Choose an Image");
		final File file = imageFileChooser.showOpenDialog(window);
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
	 * @param window the window
	 * @return the list
	 */
	public List<File> prepareToImportSongFiles(final Stage window) {
		mediaFileChooser.getExtensionFilters().clear();
		mediaFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio", "*.mp3"));
		mediaFileChooser.setTitle("Select or Drag and Drop Files || Folders into the PlayList");
		final List<File> files = mediaFileChooser.showOpenMultipleDialog(window);
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
	public File selectSongFile(final Stage window) {

		mediaFileChooser.getExtensionFilters().clear();
		mediaFileChooser.getExtensionFilters().addAll(audioFilter);
		mediaFileChooser.setTitle("Choose Media File");
		final File file = mediaFileChooser.showOpenDialog(window);
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
	public File showSaveDialog(final String initialFileName, final FileType fileType) {
		databaseFolderChooser.getExtensionFilters().clear();
		if (fileType == FileType.ZIP) {
			databaseFolderChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip", "*.zip"));
		}
		databaseFolderChooser.setInitialFileName(initialFileName);
		final File file = databaseFolderChooser.showSaveDialog(Main.window);
		if (file != null) {
			// Set the property to the directory of the chosenFile so the
			// fileChooser will open here next
			lastKnownDBDirectoryProperty.setValue(file.getParentFile());
		}
		return file;
	}

	/**
	 * Open a dialog that allows user to select a directory.
	 *
	 * @param window the window
	 * @return the file
	 */
	// public File chooseDirectory(Stage window) {
	// return folderChooser.showDialog(window)
	// }

}
