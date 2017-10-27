/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.presenter.treeview;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.application.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * A custom TreeItem which represents a File
 */
public class FileTreeItem extends TreeItem<String> {
	
	public static final Image x = InfoTool.getImageFromResourcesFolder("x.png");
	
	/** Stores the full path to the file or directory. */
	private String fullPath;
	
	/** Defines if this File is a Directory */
	private boolean isDirectory;
	
	/**
	 * Constructor.
	 *
	 * @param absolutePath
	 *            The absolute path of the file or folder
	 * 
	 */
	public FileTreeItem(String absolutePath) {
		super(absolutePath);
		this.fullPath = absolutePath;
		
		//Is this a directory?
		File file = new File(fullPath);
		isDirectory = file.isDirectory();
		//Does it exists?
		if (file.exists()) {
			//It is directory?
			if (isDirectory())
				setImage(SystemRoot.closedFolderImage);
			
			else {
				//Is it a music file?
				if (InfoTool.isAudioSupported(absolutePath))
					setImage(Media.SONG_IMAGE);
				else
					setImage(SystemRoot.fileImage);
			}
		} else
			setImage(x);
		
		// set the value
		if (!fullPath.endsWith(File.separator)) {
			// set the value (which is what is displayed in the tree)
			String value = absolutePath;
			int indexOf = value.lastIndexOf(File.separator);
			if (indexOf > 0)
				this.setValue(value.substring(indexOf + 1));
			else
				this.setValue(value);
			
		}
		
		//this.setValue(InfoTool.getFileName(absolutePath));
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
	 * Gets the full path.
	 *
	 * @return the full path
	 */
	public String getFullPath() {
		return fullPath;
	}
	
	/**
	 * Checks if is directory.
	 *
	 * @return true, if is directory
	 */
	public boolean isDirectory() {
		return isDirectory;
	}
}
