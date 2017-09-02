/*
 * 
 */
package application.presenter.treeview;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * The Class TreeItemFile.
 */
public class TreeItemFile extends TreeItem<String> {

    /** Stores the full path to the file or directory. */
    private String fullPath;

    /** The is directory. */
    private boolean isDirectory;

    /**
     * Constructor.
     *
     * @param path
     *            the path
     */
    public TreeItemFile(String path) {
	super(path);
	this.fullPath = path;

	// test if this is a directory and set the icon
	if (new File(fullPath).isDirectory()) {
	    isDirectory = true;
	    setGraphic(new ImageView(SystemRoot.closedFolderImage));

	} // if you want different icons for different file types this is
	  // where you'd do it
	else {
	    isDirectory = false;
	    setGraphic(new ImageView(SystemRoot.fileImage));
	}

	// set the value
	if (!fullPath.endsWith(File.separator)) {
	    // set the value (which is what is displayed in the tree)
	    String value = path;
	    int indexOf = value.lastIndexOf(File.separator);
	    if (indexOf > 0) {
		this.setValue(value.substring(indexOf + 1));
	    } else {
		this.setValue(value);
	    }
	}
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
