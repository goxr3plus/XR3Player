/*
 * 
 */
package application.presenter.treeview;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
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
	public static final Image closedFolderImage = InfoTool.getImageFromResourcesFolder("folder.png");
	
	/** The Constant openedFolderImage. */
	public static final Image openedFolderImage = InfoTool.getImageFromResourcesFolder("openedFolder.png");
	
	/** The Constant fileImage. */
	public static final Image fileImage = InfoTool.getImageFromResourcesFolder("file.png");
	
	public static final Image musicFolderImage = InfoTool.getImageFromResourcesFolder("Music Folder-20.png");
	
	public static final Image videosFolderImage = InfoTool.getImageFromResourcesFolder("Movies Folder-20.png");
	
	public static final Image documentsFolderImage = InfoTool.getImageFromResourcesFolder("Documents Folder-20.png");
	
	public static final Image downloadsFolderImage = InfoTool.getImageFromResourcesFolder("Downloads Folder-20.png");
	
	public static final Image picturesFolderImage = InfoTool.getImageFromResourcesFolder("Pictures Folder-20.png");
	
	public static final Image userFolderImage = InfoTool.getImageFromResourcesFolder("User Folder-20.png");
	
	/** The host name. */
	String hostName = "computer";
	
	/** The root. */
	TreeItemFile root;
	
	/** File System View */
	FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	
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
		
		//Local-Host
		root = new TreeItemFile(hostName + " ( Local )");
		root.setGraphic(InfoTool.getImageViewFromResourcesFolder("computer.png"));
		String userHome = System.getProperty("user.home");
		
		// User Folder
		TreeItemFile userFolder = new TreeItemFile(userHome);
		( (ImageView) userFolder.getGraphic() ).setImage(userFolderImage);
		root.getChildren().add(userFolder);
		
		//Based on the Operating System
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			
			//Documents
			TreeItemFile documentsFolder = new TreeItemFile(userHome + File.separator + "Documents");
			( (ImageView) documentsFolder.getGraphic() ).setImage(documentsFolderImage);
			root.getChildren().add(documentsFolder);
			
			//Downloads
			TreeItemFile downloadsFolder = new TreeItemFile(userHome + File.separator + "Downloads");
			( (ImageView) downloadsFolder.getGraphic() ).setImage(downloadsFolderImage);
			root.getChildren().add(downloadsFolder);
			
			//Music
			TreeItemFile musicFolder = new TreeItemFile(userHome + File.separator + "Music");
			( (ImageView) musicFolder.getGraphic() ).setImage(musicFolderImage);
			root.getChildren().add(musicFolder);
			
			//Pictures
			TreeItemFile picturesFolder = new TreeItemFile(userHome + File.separator + "Pictures");
			( (ImageView) picturesFolder.getGraphic() ).setImage(picturesFolderImage);
			root.getChildren().add(picturesFolder);
			
			//Videos
			TreeItemFile videosFolder = new TreeItemFile(userHome + File.separator + "Videos");
			( (ImageView) videosFolder.getGraphic() ).setImage(videosFolderImage);
			root.getChildren().add(videosFolder);
			
		}
		
		//===============Hard Drives Sub Menu=================================
		TreeItemFile hardDrives = new TreeItemFile("Hard Drives");
		hardDrives.setGraphic(InfoTool.getImageViewFromResourcesFolder("SSD-20.png"));
		
		// Add the root directories to the hard drive
		FileSystems.getDefault().getRootDirectories().forEach(pathName -> {
			TreeItemFile treeNode = new TreeItemFile(pathName.toString());
			hardDrives.getChildren().add(treeNode);
		});
		
		//Hard Drives
		root.getChildren().add(hardDrives);
		
		//================================================================
		
		// Desktop
		root.getChildren().add(new TreeItemFile(fileSystemView.getHomeDirectory().getAbsolutePath()));
		
		root.setExpanded(true);
	}
	
	/**
	 * @return The root item of the FileSystem
	 */
	public TreeItemFile getRoot() {
		return root;
	}
}
