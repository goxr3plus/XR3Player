/*
 *
 */
package com.goxr3plus.xr3player.controllers.systemtree;

import java.io.File;
import java.util.logging.Level;

import org.kordamp.ikonli.javafx.FontIcon;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.FileCategory;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * A custom TreeItem which represents a File
 */
public class FileTreeItem extends TreeItem<String> {

	/** Stores the full path to the file or directory. */
	private String absoluteFilePath;

	/** Defines if this File is a Directory */
	private boolean isDirectory;

	/**
	 * FontIcon
	 */
	private FontIcon icon = new FontIcon();

	// --------------- Colors ----------------
	public static final Color folderColor = Color.web("#ddaa33");
	public static final Color audioColor = Color.web("#ff4a4a");
	public static final Color pdfColor = Color.web("#d62641");
	public static final Color fileColor = Color.web("#d74418");

	/**
	 * Constructor.
	 *
	 * @param absoluteFilePath The absolute path of the file or folder
	 */
	public FileTreeItem(String absoluteFilePath) {
		super(absoluteFilePath);
		this.absoluteFilePath = absoluteFilePath;

		// icon
		icon.setIconSize(18);
		setGraphic(icon);

		// Is this a directory?
		File file = new File(absoluteFilePath);
		isDirectory = file.isDirectory();

		// Does it exists?
		if (file.exists()) {
			// It is directory?
			if (isDirectory)
				setFontIcon("fas-folder", folderColor);

			else {
				// Is it a music file?
				if (ExtensionTool.isAudio(absoluteFilePath))
					setFontIcon("fas-file-audio", audioColor);
				else if (ExtensionTool.isVideo(absoluteFilePath))
					setFontIcon("fas-file-video", Color.WHITE);
				else if (ExtensionTool.isImage(absoluteFilePath))
					setFontIcon("fas-file-image", Color.WHITE);
				else if (ExtensionTool.isPdf(absoluteFilePath))
					setFontIcon("fas-file-pdf", pdfColor);
				else if (ExtensionTool.isZip(absoluteFilePath))
					setFontIcon("fas-file-archive", Color.WHITE);
				else
					setFontIcon("fas-file", Color.WHITE);
			}
		} else
			setFontIcon("fas-file", fileColor);

		// set the value
		if (!absoluteFilePath.endsWith(File.separator)) {
			// set the value (which is what is displayed in the tree)
			String value = absoluteFilePath;
			int indexOf = value.lastIndexOf(File.separator);
			if (indexOf > 0)
				this.setValue(value.substring(indexOf + 1));
			else
				this.setValue(value);

		}

		// this.setValue(InfoTool.getFileName(absolutePath))
	}

	/**
	 * Rename the Media File.
	 *
	 * @param node The node based on which the Rename Window will be position
	 * [[SuppressWarningsSpartan]]
	 */
	public void rename(Node node) {

		// Open Window
		String extension = "." + IOInfo.getFileExtension(getAbsoluteFilePath());
		Main.renameWindow.show(IOInfo.getFileTitle(getAbsoluteFilePath()), node, "Media Renaming", FileCategory.FILE);
		String oldFilePath = getAbsoluteFilePath();

		// Bind
		valueProperty().bind(Main.renameWindow.getInputField().textProperty().concat(!isDirectory() ? extension : ""));

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
					valueProperty().unbind();

					String newFilePath = new File(oldFilePath).getParent() + File.separator
						+ Main.renameWindow.getInputField().getText() + (!isDirectory() ? extension : "");

					// !XPressed && // Old name != New name
					if (Main.renameWindow.wasAccepted() && !getAbsoluteFilePath().equals(newFilePath)) {

						try {

							// Check if that file already exists
							if (new File(newFilePath).exists()) {
								setAbsoluteFilePath(oldFilePath);
								AlertTool.showNotification("Rename Failed",
									"The action can not been completed:\nA file with that name already exists.",
									Duration.millis(1500), NotificationType.WARNING);
								// controller.renameWorking = false
								return;
							}

							// Check if it can be renamed
							if (!new File(getAbsoluteFilePath()).renameTo(new File(newFilePath))) {
								setAbsoluteFilePath(oldFilePath);
								AlertTool.showNotification("Rename Failed",
									"The action can not been completed(Possible Reasons):\n1) The file is opened by a program,close it and try again.\n2)It doesn't exist anymore..",
									Duration.millis(1500), NotificationType.WARNING);
								// controller.renameWorking = false
								return;
							}

							//Rename
							Media.mediaRename(oldFilePath, newFilePath);

							// Set new file path
							setAbsoluteFilePath(newFilePath);

							// Commit to the Database
							Main.dbManager.commit();

							// Show message to user
							AlertTool.showNotification("Success Message",
								"Successfully rename from :\n" + IOInfo.getFileName(oldFilePath) + " \nto\n"
									+ IOInfo.getFileName(newFilePath),
								Duration.millis(2000), NotificationType.SUCCESS);

							// Exception occurred
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
							setAbsoluteFilePath(oldFilePath);
							AlertTool.showNotification("Error Message",
								"Failed to rename the File:/n" + ex.getMessage(), Duration.millis(1500),
								NotificationType.ERROR);
						}
					} else // X is pressed by user || // Old name == New name
						setAbsoluteFilePath(oldFilePath);

				} // RenameWindow is still showing
			}// invalidated
		});
		// }
	}

	/**
	 * Set Graphic Font Icon
	 *
	 * @param iconLiteral
	 * @param color
	 */
	private void setFontIcon(String iconLiteral, Color color) {
		icon.setIconLiteral(iconLiteral);
		icon.setIconColor(color);
	}

	/**
	 * Gets the full path.
	 *
	 * @return the full path
	 */
	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}

	public void setAbsoluteFilePath(String fullPath) {
		this.absoluteFilePath = fullPath;

		// set the value
		if (!fullPath.endsWith(File.separator)) {
			// set the value (which is what is displayed in the tree)
			String value = fullPath;
			int indexOf = value.lastIndexOf(File.separator);
			if (indexOf > 0)
				this.setValue(value.substring(indexOf + 1));
			else
				this.setValue(value);

		}
	}

	/**
	 * Checks if is directory.
	 *
	 * @return true, if is directory
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * @return the icon
	 */
	public FontIcon getIcon() {
		return icon;
	}

}
