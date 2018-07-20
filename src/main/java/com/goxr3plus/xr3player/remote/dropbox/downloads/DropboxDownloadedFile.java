/*
 * 
 */
package main.java.com.goxr3plus.xr3player.remote.dropbox.downloads;

import org.kordamp.ikonli.javafx.FontIcon;

import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DownloadService;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public class DropboxDownloadedFile {
	
	/** The title. */
	private SimpleStringProperty title;
	
	private SimpleStringProperty extension;
	
	private SimpleObjectProperty<Button> actionColumn;
	
	private SimpleObjectProperty<StackPane> progressBox;
	
	//---------------------------------------------------------------------
	
	//----------------------------------------
	
	/** Defines if this File is a Directory */
	private boolean isDirectory;
	
	private final Button actionColumnButton = new Button("");
	
	private final DownloadService downloadService;
	
	/**
	 * Constructor
	 */
	public DropboxDownloadedFile(DownloadService downloadService) {
		this.downloadService = downloadService;
		String value = InfoTool.getFileName(downloadService.getLocalFileAbsolutePath());
		System.out.println(downloadService.getLocalFileAbsolutePath());
		
		//---------------------Init properties------------------------------------
		title = new SimpleStringProperty(value);
		extension = new SimpleStringProperty(InfoTool.getFileExtension(value));
		
		//ArtWork FontIcon
		FontIcon menuFontIcon = new FontIcon("typ-th-small");
		menuFontIcon.setIconSize(30);
		menuFontIcon.setIconColor(Color.WHITE);
		
		//actionColumnButton
		actionColumnButton.setGraphic(menuFontIcon);
		actionColumnButton.setPrefSize(50, 25);
		actionColumnButton.setMinSize(50, 25);
		actionColumnButton.setMaxSize(50, 25);
		actionColumnButton.getStyleClass().add("jfx-button4");
		//		actionColumnButton.setOnMouseReleased(m -> {
		//			
		//			//Find the bounds
		//			Bounds bounds = actionColumnButton.localToScreen(actionColumnButton.getBoundsInLocal());
		//			
		//			//Show the contextMenu
		//			Main.dropBoxViewer.getFileContextMenu().show(this, bounds.getMinX() - bounds.getWidth() / 2, bounds.getMaxY() + 5, actionColumnButton);
		//		});
		
		this.actionColumn = new SimpleObjectProperty<>(actionColumnButton);
		
		//progressBox
		DownloadsProgressBox progressBoxe = new DownloadsProgressBox(this);	
		progressBox = new SimpleObjectProperty<>(progressBoxe);
		
		//-------------------------------ETC---------------------------
		
		//Is this a directory?
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
	
	public SimpleObjectProperty<Button> actionColumnProperty() {
		return actionColumn;
	}
	
	public SimpleObjectProperty<StackPane> downloadProperty() {
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
