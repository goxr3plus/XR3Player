/*
 * 
 */
package com.goxr3plus.xr3player.controllers.dropbox;

import com.dropbox.core.v2.files.FolderMetadata;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.services.dropbox.DownloadService;
import com.goxr3plus.xr3player.utils.io.IOInfo;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public class DropboxDownloadedFile {

	/** The title. */
	private SimpleStringProperty title;

	private SimpleStringProperty extension;

	private SimpleObjectProperty<StackPane> progressBox;

	// ---------------------------------------------------------------------

	// ----------------------------------------

	/** Defines if this File is a Directory */
	private boolean isDirectory;

	private final Button actionColumnButton = new Button("");

	private final DownloadService downloadService;

	/**
	 * Constructor
	 */
	public DropboxDownloadedFile(DownloadService downloadService) {
		this.downloadService = downloadService;
		String value = IOInfo.getFileName(downloadService.getLocalFileAbsolutePath());

		// ---------------------Init properties------------------------------------
		title = new SimpleStringProperty(value);
		extension = new SimpleStringProperty(IOInfo.getFileExtension(value));

		// progressBox
		DownloadsProgressBox progressBoxe = new DownloadsProgressBox(this);
		progressBox = new SimpleObjectProperty<>(progressBoxe);

		// -------------------------------ETC---------------------------

		// Is this a directory?
		isDirectory = downloadService.getDropboxFile().getMetadata() instanceof FolderMetadata;

	}

	// --------Methods------------------------------------------------------------------------------------

	/**
	 * Checks if is directory.
	 *
	 * @return true, if is directory
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * Checks if is FILE.
	 *
	 * @return true, if is FILE
	 */
	public boolean isFile() {
		return !isDirectory;
	}

	// --------Properties------------------------------------------------------------------------------------

	/**
	 * Title property.
	 *
	 * @return the simple string property
	 */
	public SimpleStringProperty titleProperty() {
		return title;
	}

	public SimpleStringProperty extensionProperty() {
		return extension;
	}

	public SimpleObjectProperty<StackPane> progressBoxProperty() {
		return progressBox;
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
	 * @return the actionColumnButton
	 */
	public Button getActionColumnButton() {
		return actionColumnButton;
	}

	/**
	 * @return the downloadService
	 */
	public DownloadService getDownloadService() {
		return downloadService;
	}

}
