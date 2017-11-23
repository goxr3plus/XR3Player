package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.application.presenter.treeview.SystemRoot;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;

/**
 * A custom TreeItem which represents a File
 */
public class DropBoxFileTreeItem extends TreeItem<String> {
	
	public static final Image x = InfoTool.getImageFromResourcesFolder("x.png");
	
	/** Defines if this File is a Directory */
	private boolean isDirectory;
	
	private Metadata metadata;
	
	/**
	 * Constructor.
	 *
	 * @param value
	 *            The absolute path of the file or folder
	 * 
	 */
	public DropBoxFileTreeItem(String value, Metadata metadata) {
		super(value);
		this.metadata = metadata;
		
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
	
	/**
	 * Using this method do not write duplicate code using setGraphic(...) everywhere
	 * 
	 * @param image
	 */
	private void setImage(Image image) {
		setGraphic(new ImageView(image));
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
	
}
