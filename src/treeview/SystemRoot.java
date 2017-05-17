/*
 * 
 */
package treeview;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Level;

import javax.swing.filechooser.FileSystemView;

import application.Main;
import application.tools.InfoTool;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Manages the SystemTree.
 *
 * @author GOXR3PLUS
 */
public class SystemRoot {

    /** The Constant folderImage. */
    public static final Image closedFolderImage = new Image(
	    TreeItemFile.class.getResourceAsStream(InfoTool.IMAGES + "folder.png"));

    /** The Constant openedFolderImage. */
    public static final Image openedFolderImage = new Image(
	    TreeItemFile.class.getResourceAsStream(InfoTool.IMAGES + "openedFolder.png"));

    /** The Constant fileImage. */
    public static final Image fileImage = new Image(
	    TreeItemFile.class.getResourceAsStream(InfoTool.IMAGES + "file.png"));

    /** The host name. */
    String hostName = "computer";

    /** The root. */
    TreeItemFile root;

    /** The root directories. */
    Iterable<Path> rootDirectories;

    /**
     * Constructor.
     */
    public SystemRoot() {

	// setup the file browser root
	try {
	    hostName = InetAddress.getLocalHost().getHostName();
	} catch (UnknownHostException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
	root = new TreeItemFile(hostName);
	root.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(InfoTool.IMAGES + "computer.png"))));

	// Add the user directory
	TreeItemFile userHome = new TreeItemFile(System.getProperty("user.home"));
	root.getChildren().add(userHome);

	// Add the user desktop
	FileSystemView filesys = FileSystemView.getFileSystemView();
	TreeItemFile dekstop = new TreeItemFile(filesys.getHomeDirectory().getAbsolutePath());
	root.getChildren().add(dekstop);

	// Add the root directories
	rootDirectories = FileSystems.getDefault().getRootDirectories();
	for (Path name : rootDirectories) {
	    TreeItemFile treeNode = new TreeItemFile(name.toString());
	    root.getChildren().add(treeNode);
	}

	root.setExpanded(true);
    }

    /**
     * @return The root item of the FileSystem
     */
    public TreeItemFile getRoot() {
	return root;
    }
}
