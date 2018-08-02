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
	private String absoluteFilePath;
	
	/** Defines if this File is a Directory */
	private boolean isDirectory;
	
	/**
	 * FontIcon
	 */
	private FontIcon icon = new FontIcon();
	
	//--------------- Colors ----------------
	public static final Color folderColor = Color.web("#ddaa33");
	public static final Color audioColor = Color.web("#ff4a4a");
	public static final Color pdfColor = Color.web("#d62641");
	public static final Color fileColor = Color.web("#d74418");
	
	/**
	 * Constructor.
	 *
	 * @param absoluteFilePath
	 *            The absolute path of the file or folder
	 * 
	 */
	public FileTreeItem(String absoluteFilePath) {
		super(absoluteFilePath);
		this.absoluteFilePath = absoluteFilePath;
		
		//icon
		icon.setIconSize(18);
		setGraphic(icon);
		
		//Is this a directory?
		File file = new File(absoluteFilePath);
		isDirectory = file.isDirectory();
		
		//Does it exists?
		if (file.exists()) {
			//It is directory?
			if (isDirectory)
				setFontIcon("fas-folder", folderColor);
			
			else {
				//Is it a music file?
				if (InfoTool.isAudio(absoluteFilePath))
					setFontIcon("fas-file-audio", audioColor);
				else if (InfoTool.isVideo(absoluteFilePath))
					setFontIcon("fas-file-video", Color.WHITE);
				else if (InfoTool.isImage(absoluteFilePath))
					setFontIcon("fas-file-image", Color.WHITE);
				else if (InfoTool.isPdf(absoluteFilePath))
					setFontIcon("fas-file-pdf", pdfColor);
				else if (InfoTool.isZip(absoluteFilePath))
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
