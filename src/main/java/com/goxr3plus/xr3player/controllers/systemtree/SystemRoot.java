/*
 * 
 */
package com.goxr3plus.xr3player.controllers.systemtree;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.util.logging.Level;

import javax.swing.filechooser.FileSystemView;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.application.Main;

/**
 * Manages the SystemTree.
 *
 * @author GOXR3PLUS
 */
public class SystemRoot {

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

		// Local-Host
		FontIcon screenFontIcon = new FontIcon("fas-desktop");
		screenFontIcon.setIconSize(18);
		screenFontIcon.setIconColor(Color.WHITE);

		root = new FileTreeItem(hostName + " ( Local )");
		root.setGraphic(screenFontIcon);

		// UserHome
		String userHome = System.getProperty("user.home");

		// User Folder
		FileTreeItem userFolder = new FileTreeItem(userHome);
		root.getChildren().add(userFolder);

		// Desktop
		root.getChildren().add(new FileTreeItem(fileSystemView.getHomeDirectory().getAbsolutePath()));

		// Based on the Operating System
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {

			// Documents
			FileTreeItem documentsFolder = new FileTreeItem(userHome + File.separator + "Documents");
			root.getChildren().add(documentsFolder);

			// Downloads
			FileTreeItem downloadsFolder = new FileTreeItem(userHome + File.separator + "Downloads");
			root.getChildren().add(downloadsFolder);

			// Music
			FileTreeItem musicFolder = new FileTreeItem(userHome + File.separator + "Music");
			root.getChildren().add(musicFolder);

			// Pictures
			FileTreeItem picturesFolder = new FileTreeItem(userHome + File.separator + "Pictures");
			root.getChildren().add(picturesFolder);

			// Videos
			FileTreeItem videosFolder = new FileTreeItem(userHome + File.separator + "Videos");
			root.getChildren().add(videosFolder);

		}

		// ===============Hard Drives Sub Menu=================================
		FontIcon ssdFontIcon = new FontIcon("icm-drive");
		ssdFontIcon.setIconSize(18);
		ssdFontIcon.setIconColor(Color.WHITE);

		FileTreeItem hardDrives = new FileTreeItem("Hard Drives");
		hardDrives.setGraphic(ssdFontIcon);

		// Add the root directories to the hard drive
		FileSystems.getDefault().getRootDirectories()
				.forEach(pathName -> hardDrives.getChildren().add(new FileTreeItem(pathName.toString())));

		// Hard Drives
		root.getChildren().add(hardDrives);

		// ================================================================

		root.setExpanded(true);
	}

	/**
	 * @return The root item of the FileSystem
	 */
	public FileTreeItem getRoot() {
		return root;
	}
}
