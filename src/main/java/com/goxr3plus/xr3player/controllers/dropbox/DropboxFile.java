/*
 * 
 */
package com.goxr3plus.xr3player.controllers.dropbox;

import org.kordamp.ikonli.javafx.FontIcon;

import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.io.IOInfo;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public class DropboxFile {

	/** The title. */
	private SimpleStringProperty title;

	private SimpleStringProperty extension;

	private SimpleObjectProperty<Button> actionColumn;

	private SimpleObjectProperty<Button> download;

	// ---------------------------------------------------------------------

	// ----------------------------------------

	/** Defines if this File is a Directory */
	private boolean isDirectory;

	private Metadata metadata;

	private final Button actionColumnButton = new Button("");

	/**
	 * Constructor
	 */
	public DropboxFile(Metadata metadata) {
		this.metadata = metadata;
		String value = metadata.getName();

		// ---------------------Init properties------------------------------------
		title = new SimpleStringProperty(value);
		extension = new SimpleStringProperty(IOInfo.getFileExtension(value));

		// ArtWork FontIcon
		FontIcon menuFontIcon = new FontIcon("typ-th-small");
		menuFontIcon.setIconSize(30);
		menuFontIcon.setIconColor(Color.WHITE);

		// actionColumnButton
		actionColumnButton.setGraphic(menuFontIcon);
		actionColumnButton.setPrefSize(50, 25);
		actionColumnButton.setMinSize(50, 25);
		actionColumnButton.setMaxSize(50, 25);
		actionColumnButton.getStyleClass().add("jfx-button4");
		actionColumnButton.setOnMouseReleased(m -> {

			// Find the bounds
			Bounds bounds = actionColumnButton.localToScreen(actionColumnButton.getBoundsInLocal());

			// Show the contextMenu
			Main.dropBoxViewer.getFileContextMenu().show(this, bounds.getMinX() - bounds.getWidth() / 2,
					bounds.getMaxY() + 5, actionColumnButton);
		});

		this.actionColumn = new SimpleObjectProperty<>(actionColumnButton);

		// downloadButton
		FontIcon downloadIcon = new FontIcon("fas-cloud-download-alt");
		downloadIcon.setIconSize(18);
		downloadIcon.setIconColor(Color.WHITE);

		Button downloadButton = new Button("", downloadIcon);
		downloadButton.getStyleClass().add("jfx-button2");
		downloadButton.setPrefSize(28, 24);
		downloadButton.setMinSize(28, 24);
		downloadButton.setMaxSize(28, 24);
		downloadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		downloadButton.setOnMouseReleased(m -> Main.dropBoxViewer.downloadFile(this));

		download = new SimpleObjectProperty<>(downloadButton);

		// -------------------------------ETC---------------------------

		// Is this a directory?
		isDirectory = this.metadata instanceof FolderMetadata;

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

	/**
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
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

	public SimpleObjectProperty<Button> actionColumnProperty() {
		return actionColumn;
	}

	public SimpleObjectProperty<Button> downloadProperty() {
		return download;
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
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
		title.set(metadata.getName());
	}

}
