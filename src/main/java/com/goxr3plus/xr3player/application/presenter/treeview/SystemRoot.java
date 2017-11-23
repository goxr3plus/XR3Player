/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.presenter.treeview;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.util.logging.Level;

import javax.swing.filechooser.FileSystemView;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * Manages the SystemTree.
 *
 * @author GOXR3PLUS
 */
public class SystemRoot {
	
	/** The Constant openedFolderImage. */
	public static final Image OPENED_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("openedFolder.png");
	
	/** The Constant folderImage. */
	public static final Image CLOSED_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("folder.png");
	
	/** The Constant fileImage. */
	public static final Image FILE_IMAGE = InfoTool.getImageFromResourcesFolder("file.png");
	
	public static final Image PICTURE_IMAGE = InfoTool.getImageFromResourcesFolder("picture.png");
	
	public static final Image PDF_IMAGE = InfoTool.getImageFromResourcesFolder("pdf.png");
	
	public static final Image MUSIC_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("Music Folder-20.png");
	
	public static final Image VIDEOS_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("Movies Folder-20.png");
	
	public static final Image DOCUMENTS_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("Documents Folder-20.png");
	
	public static final Image DOWNLOADS_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("Downloads Folder-20.png");
	
	public static final Image PICTURES_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("Pictures Folder-20.png");
	
	public static final Image USER_FOLDER_IMAGE = InfoTool.getImageFromResourcesFolder("User Folder-20.png");
	
	/** The host name. */
	String hostName = "computer";
	
	/** The root. */
	FileTreeItem root;
	
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
		root = new FileTreeItem(hostName + " ( Local )");
		root.setGraphic(InfoTool.getImageViewFromResourcesFolder("computer.png"));
		String userHome = System.getProperty("user.home");
		
		// User Folder
		FileTreeItem userFolder = new FileTreeItem(userHome);
		( (ImageView) userFolder.getGraphic() ).setImage(USER_FOLDER_IMAGE);
		root.getChildren().add(userFolder);
		
		//Based on the Operating System
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			
			//Documents
			FileTreeItem documentsFolder = new FileTreeItem(userHome + File.separator + "Documents");
			( (ImageView) documentsFolder.getGraphic() ).setImage(DOCUMENTS_FOLDER_IMAGE);
			root.getChildren().add(documentsFolder);
			
			//Downloads
			FileTreeItem downloadsFolder = new FileTreeItem(userHome + File.separator + "Downloads");
			( (ImageView) downloadsFolder.getGraphic() ).setImage(DOWNLOADS_FOLDER_IMAGE);
			root.getChildren().add(downloadsFolder);
			
			//Music
			FileTreeItem musicFolder = new FileTreeItem(userHome + File.separator + "Music");
			( (ImageView) musicFolder.getGraphic() ).setImage(MUSIC_FOLDER_IMAGE);
			root.getChildren().add(musicFolder);
			
			//Pictures
			FileTreeItem picturesFolder = new FileTreeItem(userHome + File.separator + "Pictures");
			( (ImageView) picturesFolder.getGraphic() ).setImage(PICTURES_FOLDER_IMAGE);
			root.getChildren().add(picturesFolder);
			
			//Videos
			FileTreeItem videosFolder = new FileTreeItem(userHome + File.separator + "Videos");
			( (ImageView) videosFolder.getGraphic() ).setImage(VIDEOS_FOLDER_IMAGE);
			root.getChildren().add(videosFolder);
			
		}
		
		//===============Hard Drives Sub Menu=================================
		FileTreeItem hardDrives = new FileTreeItem("Hard Drives");
		hardDrives.setGraphic(InfoTool.getImageViewFromResourcesFolder("SSD-20.png"));
		
		// Add the root directories to the hard drive
		FileSystems.getDefault().getRootDirectories().forEach(pathName -> {
			FileTreeItem treeNode = new FileTreeItem(pathName.toString());
			hardDrives.getChildren().add(treeNode);
		});
		
		//Hard Drives
		root.getChildren().add(hardDrives);
		
		//================================================================
		
		// Desktop
		root.getChildren().add(new FileTreeItem(fileSystemView.getHomeDirectory().getAbsolutePath()));
		
		root.setExpanded(true);
	}
	
	/**
	 * @return The root item of the FileSystem
	 */
	public FileTreeItem getRoot() {
		return root;
	}
}
