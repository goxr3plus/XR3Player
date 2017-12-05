/*
 * 
 */
package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.application.presenter.treeview.SystemRoot;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;

/**
 * This class is used as super class for Audio and Video classes.
 *
 * @author GOXR3PLUS
 */
public class DropboxFile {
	
	/** The title. */
	private SimpleStringProperty title;
	
	/** The FILE type. */
	private SimpleObjectProperty<ImageView> fileThumbnail;
	
	private SimpleObjectProperty<Button> actionColumn;
	
	//---------------------------------------------------------------------
	
	public static final Image x = InfoTool.getImageFromResourcesFolder("x.png");
	
	//----------------------------------------
	
	/** Defines if this File is a Directory */
	private boolean isDirectory;
	
	private Metadata metadata;
	
	/**
	 * Constructor
	 */
	public DropboxFile(Metadata metadata) {
		this.metadata = metadata;
		String value = metadata.getName();
		
		//---------------------Init properties------------------------------------
		title = new SimpleStringProperty(value);
		fileThumbnail = new SimpleObjectProperty<>(null);
		
		//actionColumnButton
		Button actionColumnButton = new Button("...");
		actionColumnButton.setPrefSize(50, 25);
		actionColumnButton.setMinSize(50, 25);
		actionColumnButton.setMaxSize(50, 25);
		actionColumnButton.setStyle("-fx-cursor:hand; -fx-background-color:white; -fx-text-fill:black;");
		
		this.actionColumn = new SimpleObjectProperty<>(actionColumnButton);
		//-------------------------------ETC---------------------------
		
		//Is this a directory?
		isDirectory = this.metadata == null || this.metadata instanceof FolderMetadata;
		
		//It is directory?
		if (isDirectory)
			setImage(SystemRoot.CLOSED_FOLDER_IMAGE);
		
		else {
			//Is it a music file?
			if (InfoTool.isAudio(value))
				setImage(Media.SONG_IMAGE);
			else if (InfoTool.isVideo(value))
				setImage(SystemRoot.VIDEO_IMAGE);
			else if (InfoTool.isImage(value))
				setImage(SystemRoot.PICTURE_IMAGE);
			else if (InfoTool.isPdf(value))
				setImage(SystemRoot.PDF_IMAGE);
			else if (InfoTool.isZip(value))
				setImage(SystemRoot.ZIP_IMAGE);
			else
				setImage(SystemRoot.FILE_IMAGE);
		}
		
	}
	
	// --------Methods------------------------------------------------------------------------------------
	
	/**
	 * Using this method do not write duplicate code using setGraphic(...) everywhere
	 * 
	 * @param image
	 */
	private void setImage(Image image) {
		fileThumbnail.set(new ImageView(image));
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
	
	public SimpleObjectProperty<ImageView> fileThumbnailProperty() {
		return fileThumbnail;
	}
	
	public SimpleObjectProperty<Button> actionColumnProperty() {
		return actionColumn;
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
	
}
