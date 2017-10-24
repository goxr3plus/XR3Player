/**
 * 
 */
package main.java.com.goxr3plus.xr3player.application.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.windows.FileAndFolderChooser;

/**
 * This class has some functions that are not there by default in JavaFX 8
 * 
 * @author GOXR3PLUS
 *
 */
public final class JavaFXTools {
	
	private JavaFXTools() {
	}
	
	/**
	 * Selects the Toogle with the given text from the toggle group or else selects nothing
	 * 
	 * @param toggleGroup
	 */
	public static void selectToogleWithText(ToggleGroup toggleGroup , String text) {
		toggleGroup.getToggles().forEach(toggle -> {
			if ( ( (Labeled) toggle ).getText().equals(text)) {
				toggle.setSelected(true);
			}
		});
	}
	
	/**
	 * Returns the Index of the Selected Toggle inside the ToggleGroup (counting from 0)
	 * 
	 * @param g
	 * @return The index of the Selected Toggle
	 */
	public static int getIndexOfSelectedToggle(ToggleGroup g) {
		return g.getToggles().indexOf(g.getSelectedToggle());
	}
	
	/**
	 * Selects the Toggle in position Index inside the toggle group (counting from 0 )
	 * 
	 * @param g
	 * @param index
	 */
	public static void selectToggleOnIndex(ToggleGroup g , int index) {
		g.selectToggle(g.getToggles().get(index));
	}
	
	/**
	 * Searches for any Image that contains the given title -> example ["background"] inside the given folder
	 * 
	 * @return The absolute path of the image file or null if not exists
	 */
	public static String getAbsoluteImagePath(String title , String folderToSearch) {
		String absolutePath = null;
		
		//If Folder not exists return null
		File searchingFolder = new File(folderToSearch);
		if (!searchingFolder.exists())
			return absolutePath;
		
		//Try to find the image
		try (Stream<Path> paths = Files.walk(Paths.get(searchingFolder.getPath()), 1)) {
			absolutePath = paths.filter(path -> {
				File file = path.toFile();
				return !file.isDirectory() && title.equals(InfoTool.getFileTitle(file.getAbsolutePath())) && InfoTool.isImage(file.getAbsolutePath());
			}).findFirst().map(path -> path.toAbsolutePath().toString()).orElse(null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return absolutePath;
	}
	
	/**
	 * Check if any image with that title exists -> for example ["background"] inside the Folder given , i don't have the extension
	 * 
	 * @param title
	 * @param folderToSearch
	 *            Absolute path of the Folder to Search
	 * @return
	 */
	public static Image findAnyImageWithTitle(String title , String folderToSearch) {
		//Check if any Image with that Title exists inside the given folder
		String imageAbsolutePath = getAbsoluteImagePath(title, folderToSearch);
		return imageAbsolutePath == null ? null : new Image(new File(imageAbsolutePath).toURI() + "");
	}
	
	/**
	 * Deletes any image which has that title , for example ["background"] searching on the given Folder
	 * 
	 * @param title
	 * @param folderToSearch
	 *            Absolute path of the Folder to Search
	 */
	public static void deleteAnyImageWithTitle(String title , String folderToSearch) {
		
		//If Folder not exists return
		File searchingFolder = new File(folderToSearch);
		if (!searchingFolder.exists())
			return;
		
		//Find and delete it
		try (Stream<Path> paths = Files.walk(Paths.get(searchingFolder.getPath()), 1)) {
			paths.forEach(path -> {
				File file = path.toFile();
				if (!file.isDirectory() && InfoTool.getFileTitle(file.getAbsolutePath()).equals(title) && InfoTool.isImage(file.getAbsolutePath()))
					file.delete(); //-> to be fixed
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		//---Something Experimental--------
		//		return title.equals(InfoTool.getFileTitle(file.getAbsolutePath())) && InfoTool.isImage(file.getAbsolutePath())
		//				&& !file.isDirectory();
		//		    }).findFirst().map(path->path.toFile().delete()).orElse(false);
		
	}
	
	/**
	 * Open's a select Window and if the user selects an image it saves it with the given title and to the given folder , the extension is
	 * automatically found from the original one Image
	 * 
	 * @param imageNameToDelete
	 *            The images containing this name will be deleted
	 * @param folderForSaving
	 *            This folder must already exist!
	 * 
	 * @return The image file which of course can be null if the user doesn't selected anything
	 */
	public static Optional<File> selectAndSaveImage(String title , String folderForSaving , FileAndFolderChooser specialChooser , Stage window) {
		
		File imageFile = specialChooser.prepareToSelectImage(window);
		if (imageFile == null)
			return Optional.ofNullable(null);
		
		//Check the given image
		Image image = new Image(imageFile.toURI() + "");
		
		//Check width and height
		if (image.getWidth() > 8000 || image.getHeight() > 8000 || image.getWidth() < 200 || image.getHeight() < 200) {
			ActionTool.showNotification("Warning", "Maximum Size Allowed 8000*8000 \nMinimum Size Allowed 200*200 \n\tCurrent is:" + image.getWidth() + "x" + image.getHeight(),
					Duration.millis(2000), NotificationType.WARNING);
			return Optional.ofNullable(null);
		}
		
		//Copy the File
		new Thread(() -> {
			
			//Delete any previous image with that title
			deleteAnyImageWithTitle(title, folderForSaving);
			
			if (!ActionTool.copy(imageFile.getAbsolutePath(), folderForSaving + File.separator + title + "." + InfoTool.getFileExtension(imageFile.getAbsolutePath())))
				Platform.runLater(() -> ActionTool.showNotification("Failed saving image", "Failed to change the image...", Duration.millis(2500), NotificationType.SIMPLE));
			
		}).start();
		
		return Optional.ofNullable(imageFile);
	}
}
