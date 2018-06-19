/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.systemtreeview;

import java.io.File;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * A custom TreeItem which represents a File
 */
public class FileTreeItem extends TreeItem<String> {
	
	/** Stores the full path to the file or directory. */
	private String fullPath;
	
	/** Defines if this File is a Directory */
	private boolean isDirectory;
	
	/**
	 * FontIcon
	 */
	private FontIcon icon = new FontIcon();
	
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
		
		//icon
		icon.setIconSize(18);
		
		//Is this a directory?
		File file = new File(fullPath);
		isDirectory = file.isDirectory();
		
		//Does it exists?
		if (file.exists()) {
			//It is directory?
			if (isDirectory)
				setFontIcon("fas-folder", Color.web("#ddaa33"));
			
			else {
				//Is it a music file?
				if (InfoTool.isAudio(absolutePath))
					setFontIcon("fas-file-audio", Color.web("#ff4a4a"));
				else if (InfoTool.isVideo(absolutePath))
					setFontIcon("fas-file-video", Color.WHITE);
				else if (InfoTool.isImage(absolutePath))
					setFontIcon("fas-file-image", Color.WHITE);
				else if (InfoTool.isPdf(absolutePath))
					setFontIcon("fas-file-pdf", Color.web("#d62641"));
				else if (InfoTool.isZip(absolutePath))
					setFontIcon("fas-file-archive", Color.WHITE);
				else
					setFontIcon("fas-file", Color.WHITE);
			}
		} else
			setFontIcon("fas-file", Color.web("#d74418"));
		
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
		
		//this.setValue(InfoTool.getFileName(absolutePath))
	}
	
	/**
	 * Set Graphic Font Icon
	 * 
	 * @param iconLiteral
	 * @param color
	 */
	private void setFontIcon(String iconLiteral , Color color) {
		icon.setIconLiteral(iconLiteral);
		icon.setIconColor(color);
		setGraphic(icon);
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
