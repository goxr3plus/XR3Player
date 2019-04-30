/**
 * 
 */
package com.goxr3plus.xr3player.utils.javafx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.windows.FileAndFolderChooser;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;

/**
 * This class has some functions that are not there by default in JavaFX 8
 * 
 * @author GOXR3PLUS
 *
 */
public final class JavaFXTool {

	private JavaFXTool() {
	}

	/**
	 * Selects the Toogle with the given text from the toggle group or else selects
	 * nothing
	 * 
	 * @param toggleGroup
	 */
	public static void selectToogleWithText(final ToggleGroup toggleGroup, final String text) {
		toggleGroup.getToggles().forEach(toggle -> {
			if (((Labeled) toggle).getText().equals(text)) {
				toggle.setSelected(true);
			}
		});
	}

	/**
	 * Returns the Index of the Selected Toggle inside the ToggleGroup (counting
	 * from 0)
	 * 
	 * @param g
	 * @return The index of the Selected Toggle
	 */
	public static int getIndexOfSelectedToggle(final ToggleGroup g) {
		return g.getToggles().indexOf(g.getSelectedToggle());
	}

	/**
	 * Selects the Toggle in position Index inside the toggle group (counting from 0
	 * )
	 * 
	 * @param g
	 * @param index
	 */
	public static void selectToggleOnIndex(final ToggleGroup g, final int index) {
		g.selectToggle(g.getToggles().get(index));
	}

	/**
	 * Searches for any Image that contains the given title -> example
	 * ["background"] inside the given folder
	 * 
	 * @return The absolute path of the image file or null if not exists
	 */
	public static String getAbsoluteImagePath(final String title, final String folderToSearch) {
		String absolutePath = null;

		// If Folder not exists return null
		final File searchingFolder = new File(folderToSearch);
		if (!searchingFolder.exists())
			return absolutePath;

		// Try to find the image
		try (Stream<Path> paths = Files.walk(Paths.get(searchingFolder.getPath()), 1)) {
			absolutePath = paths.filter(path -> {
				final File file = path.toFile();
				return !file.isDirectory() && title.equals(IOInfo.getFileTitle(file.getAbsolutePath()))
						&& ExtensionTool.isImageSupported(file.getAbsolutePath());
			}).findFirst().map(path -> path.toAbsolutePath().toString()).orElse(null);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

		return absolutePath;
	}

	/**
	 * Check if any image with that title exists -> for example ["background"]
	 * inside the Folder given , i don't have the extension
	 * 
	 * @param title
	 * @param folderToSearch Absolute path of the Folder to Search
	 * @return
	 */
	public static Image findAnyImageWithTitle(final String title, final String folderToSearch) {
		// Check if any Image with that Title exists inside the given folder
		final String imageAbsolutePath = getAbsoluteImagePath(title, folderToSearch);
		return imageAbsolutePath == null ? null : new Image(new File(imageAbsolutePath).toURI() + "");
	}

	/**
	 * Deletes any image which has that title , for example ["background"] searching
	 * on the given Folder
	 * 
	 * @param title
	 * @param folderToSearch Absolute path of the Folder to Search
	 */
	public static void deleteAnyImageWithTitle(final String title, final String folderToSearch) {

		// If Folder not exists return
		final File searchingFolder = new File(folderToSearch);
		if (!searchingFolder.exists())
			return;

		// Find and delete it
		try (Stream<Path> paths = Files.walk(Paths.get(searchingFolder.getPath()), 1)) {
			paths.forEach(path -> {
				final File file = path.toFile();
				if (!file.isDirectory() && IOInfo.getFileTitle(file.getAbsolutePath()).equals(title)
						&& ExtensionTool.isImageSupported(file.getAbsolutePath()))
					file.delete(); // -> to be fixed
			});
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

		// ---Something Experimental--------
		// return title.equals(InfoTool.getFileTitle(file.getAbsolutePath())) &&
		// InfoTool.isImage(file.getAbsolutePath())
		// && !file.isDirectory();
		// }).findFirst().map(path->path.toFile().delete()).orElse(false);

	}

	private static final int maximumImageWidth = 4096 * 2;
	private static final int maximumImageHeight = maximumImageWidth;
	private static final int minimumImageWidth = 60;
	private static final int minimumImageHeight = 60;

	/**
	 * Open's a select Window and if the user selects an image it saves it with the
	 * given title and to the given folder , the extension is automatically found
	 * from the original one Image
	 * 
	 * @param imageNameToDelete The images containing this name will be deleted
	 * @param folderForSaving   This folder must already exist!
	 * 
	 * @return The image file which of course can be null if the user doesn't
	 *         selected anything
	 */
	public static Optional<File> selectAndSaveImage(final String title, final String folderForSaving,
			final FileAndFolderChooser specialChooser, final Stage window) {

		final File imageFile = specialChooser.prepareToSelectImage(window);
		if (imageFile == null)
			return Optional.ofNullable(null);

		// Check the given image
		final Image image = new Image(imageFile.toURI() + "");

		// Check width and height
		if (image.getWidth() > maximumImageWidth || image.getHeight() > maximumImageHeight
				|| image.getWidth() < minimumImageWidth || image.getHeight() < minimumImageHeight) {
			AlertTool.showNotification("Warning",
					"Maximum Size Allowed " + maximumImageWidth + "*" + maximumImageHeight + "\nMinimum Size Allowed "
							+ minimumImageWidth + "*" + minimumImageHeight + " \n\tCurrent is:" + image.getWidth() + "x"
							+ image.getHeight(),
					Duration.millis(2000), NotificationType.WARNING);
			return Optional.ofNullable(null);
		}

		// Copy the File
		new Thread(() -> {

			// Delete any previous image with that title
			deleteAnyImageWithTitle(title, folderForSaving);

			if (!IOAction.copy(imageFile.getAbsolutePath(), folderForSaving + File.separator + title + "."
					+ IOInfo.getFileExtension(imageFile.getAbsolutePath())))
				Platform.runLater(() -> AlertTool.showNotification("Failed saving image",
						"Failed to change the image...", Duration.millis(2500), NotificationType.SIMPLE));

		}).start();

		return Optional.ofNullable(imageFile);
	}

	/**
	 * Return the hex web string from the given color for example (#302015)
	 * 
	 * @param color The given color
	 * @return The hex web string from the given color for example (#302015)
	 */
	public static String colorToWebColor(final Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));

	}

	// -----------------------------------------------------------------------------------------------------------

	/**
	 * Creates an Alert with the given parameters
	 * 
	 * @param title
	 * @param headerText
	 * @param contentText
	 * @param alertType
	 * @param stageStyle
	 * @param owner
	 * @param graphic
	 * @return The created Alert based on the given parameters
	 */
	public static Alert createAlert(final String title, final String headerText, final String contentText,
			final AlertType alertType, final StageStyle stageStyle, final Stage owner, final Node graphic) {

		// Show Alert
		final Alert alert = new Alert(alertType);
		if (title != null)
			alert.setTitle(title);
		if (headerText != null)
			alert.setHeaderText(headerText);
		if (contentText != null)
			alert.setContentText(contentText);
		if (stageStyle != null)
			alert.initStyle(stageStyle);
		if (owner != null)
			alert.initOwner(owner);
		if (graphic != null)
			alert.setGraphic(graphic);

		// Make sure alert is not outside the screen so app becomes unresponsible
		alert.heightProperty().addListener(l -> {

			// Width and Height of the Alert
			final double alertWidth = alert.getWidth();
			final double alertHeight = alert.getHeight();
			double alertScreenX = alert.getX();
			double alertScreenY = alert.getY();

			// Check if Alert goes out of the Screen on X Axis
			if (alertScreenX + alertWidth > JavaFXTool.getVisualScreenWidth())
				alertScreenX = (int) (JavaFXTool.getVisualScreenWidth() - alertWidth);
			else if (alertScreenX < 0)
				alertScreenX = 0;

			// Check if Alert goes out of the Screen on Y AXIS
			if (alertScreenY + alertHeight > JavaFXTool.getVisualScreenHeight())
				alertScreenY = (int) (JavaFXTool.getVisualScreenHeight() - alertHeight);
			else if (alertScreenY < 0)
				alertScreenY = 0;

			// Set the X and Y of the Alert
			alert.setX(alertScreenX);
			alert.setY(alertScreenY);
		});

		return alert;
	}

	// -----------------------------------------------------------------------------------------------------------

	/**
	 * Use this method to retrieve an ImageView from the resources of the
	 * application.
	 *
	 * @param imageName the image name
	 * @return Returns an ImageView using method getImageFromResourcesFolder(String
	 *         imageName);
	 */
	public static ImageView getImageViewFromResourcesFolder(final String imageName, final double width,
			final double height) {
		final ImageView imageView = new ImageView(InfoTool.getImageFromResourcesFolder(imageName));
		if (width == -1 || height == -1 || width == 0 || height == 0)
			return imageView;
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		return imageView;
	}

	/**
	 * Returns an ImageView containing the given image fitting on the given size
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageView getImageView(final Image image, final double width, final double height) {
		final ImageView imageView = new ImageView(image);
		if (width == -1 || height == -1 || width == 0 || height == 0)
			return imageView;
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		return imageView;
	}

	/**
	 * Set Graphic Font Icon
	 * 
	 * @param icon
	 * @param iconLiteral
	 * @param color
	 */
	public static void setFontIcon(final Labeled node, final FontIcon icon, final String iconLiteral,
			final Color color) {
		icon.setIconLiteral(iconLiteral);
		icon.setIconColor(color);
		if (node != null)
			node.setGraphic(icon);
	}

	/**
	 * Get the requested Font Icon
	 * 
	 * @param iconLiteral
	 * @param color
	 * @param size
	 * @return
	 */
	public static FontIcon getFontIcon(final String iconLiteral, final Color color, final int size) {

		// Create the Icon
		final FontIcon icon = new FontIcon(iconLiteral);

		// Set Icon Color
		icon.setIconColor(color);

		// Set Size
		if (size != 0)
			icon.setIconSize(size);

		return icon;
	}

	/**
	 * Set System Clipboard
	 * 
	 * @param items
	 */
	public static void setClipBoard(final List<File> items) {
		// Get Native System ClipBoard
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();

		// PutFiles
		content.putFiles(items);

		// Set the Content
		clipboard.setContent(content);

		AlertTool.showNotification("Copied to Clipboard",
				"Files copied to clipboard,you can paste them anywhere on the your system.\nFor example in Windows with [CTRL+V], in Mac[COMMAND+V]",
				Duration.seconds(3.5), NotificationType.INFORMATION);
	}

	/**
	 * Gets the visual screen height.
	 *
	 * @return The screen <b>Height</b> based on the <b>visual bounds</b> of the
	 *         Screen.These bounds account for objects in the native windowing
	 *         system such as task bars and menu bars. These bounds are contained by
	 *         Screen.bounds.
	 */
	public static double getVisualScreenHeight() {
		return Screen.getPrimary().getVisualBounds().getHeight();
	}

	/**
	 * Gets the visual screen width.
	 *
	 * @return The screen <b>Width</b> based on the <b>visual bounds</b> of the
	 *         Screen.These bounds account for objects in the native windowing
	 *         system such as task bars and menu bars. These bounds are contained by
	 *         Screen.bounds.
	 */
	public static double getVisualScreenWidth() {
		return Screen.getPrimary().getVisualBounds().getWidth();
	}

	/**
	 * Gets the screen height.
	 *
	 * @return The screen <b>Height</b> based on the <b> bounds </b> of the Screen.
	 */
	public static double getScreenHeight() {
		return Screen.getPrimary().getBounds().getHeight();
	}

	/**
	 * Gets the screen width.
	 *
	 * @return The screen <b>Width</b> based on the <b> bounds </b> of the Screen.
	 */
	public static double getScreenWidth() {
		return Screen.getPrimary().getBounds().getWidth();
	}

}
