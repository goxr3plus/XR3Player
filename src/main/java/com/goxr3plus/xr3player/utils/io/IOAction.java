package main.java.com.goxr3plus.xr3player.utils.io;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;

import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.enums.FileType;
import main.java.com.goxr3plus.xr3player.application.enums.NotificationType;
import main.java.com.goxr3plus.xr3player.utils.javafx.AlertTool;

public final class IOAction {

	private IOAction() {
	}

	/**
	 * Copy a file from source to destination.
	 *
	 * @param source      the source
	 * @param destination the destination
	 * @return True if succeeded , False if not
	 */
	public static boolean copy(final String source, final String destination) {
		boolean succeess = true;

		// System.out.println("Copying ->" + source + "\n\tto ->" + destination)

		try {
			Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException ex) {
			ex.printStackTrace();
			// logger.log(Level.WARNING, "", ex);
			succeess = false;
		}

		return succeess;

	}

	/**
	 * Copy a file from source to destination.
	 *
	 * @param source      the source
	 * @param destination the destination
	 * @return True if succeeded , False if not
	 */
	public static boolean copy(final InputStream source, final String destination) {
		boolean succeess = true;

		// System.out.println("Copying ->" + source + "\n\tto ->" + destination)

		try {
			System.out.println(Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING));
		} catch (final IOException ex) {
			ex.printStackTrace();
			// logger.log(Level.WARNING, "", ex);
			succeess = false;
		}

		return succeess;
	}

	/**
	 * Moves a file to a different location.
	 *
	 * @param source      the source
	 * @param destination the dest
	 * @return true, if successful
	 */
	public static boolean move(final String source, final String destination) {
		boolean succeess = true;

		// System.out.println("Moving ->" + source + "\n\tto ->" + destination)

		try {
			Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException ex) {
			ex.printStackTrace();
			//ActionTool.logger.log(Level.WARNING, "", ex);
			succeess = false;
		}

		return succeess;
	}

	/**
	 * Opens the file with the System default file explorer.
	 *
	 * @param path the path
	 */
	public static void openFileInExplorer(String path) {
	
		// Open the Default Browser
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			AlertTool.showNotification("Message", "Opening in File Explorer:\n" + IOInfo.getFileName(path),
					Duration.millis(1500), NotificationType.INFORMATION);
	
			// START: --NEEDS TO BE FIXED!!!!!!----------------NOT WORKING WELL-----
	
			path = path.trim().replaceAll(" +", " ");
			final String selectPath = "/select," + path;
	
			// START: Strip one SPACE among consecutive spaces
			final LinkedList<String> list = new LinkedList<>();
			final StringBuilder sb = new StringBuilder();
			boolean flag = true;
	
			for (int i = 0; i < selectPath.length(); i++) {
				if (i == 0) {
					sb.append(selectPath.charAt(i));
					continue;
				}
	
				if (selectPath.charAt(i) == ' ' && flag) {
					list.add(sb.toString());
					sb.setLength(0);
					flag = false;
					continue;
				}
	
				if (!flag && selectPath.charAt(i) != ' ')
					flag = true;
	
				sb.append(selectPath.charAt(i));
			}
	
			list.add(sb.toString());
	
			list.addFirst("explorer.exe");
			// END: Strip one SPACE among consecutive spaces
	
			// END: --NEEDS TO BE FIXED!!!!!!----------------NOT WORKING WELL-----
	
			try {
				// Open in Explorer and Highlight
				new ProcessBuilder(list).start();
			} catch (final IOException ex) {
				ex.printStackTrace();
				//ActionTool.logger.log(Level.WARNING, ex.getMessage(), ex);
				AlertTool.showNotification("Folder Explorer Fail", "Failed to open file explorer.", Duration.millis(1500),
						NotificationType.WARNING);
			}
		} else { // For MacOS and Linux
			try {
				Desktop.getDesktop().browseFileDirectory(new File(path));
			} catch (final Exception ex) {
				ex.printStackTrace();
				AlertTool.showNotification("Not Supported",
						"This function is only supported in Windows \n I am trying my best to implement it and on other operating systems :)",
						Duration.millis(1500), NotificationType.WARNING);
			}
		}
	
	}

	/**
	 * Tries to open this File with the default system program
	 * 
	 * @param absolutePath The absolute path of the File
	 * @return <b>True</b> if succeeded , <b>False</b> if not
	 */
	public static boolean openFileInEditor(final String absolutePath) {
	
		try {
			// Check if Desktop is supported
			if (!Desktop.isDesktopSupported()) {
				AlertTool.showNotification("Problem Occured", "Can't open default File at:\n[" + absolutePath + " ]",
						Duration.millis(2500), NotificationType.INFORMATION);
				return false;
			}
	
			// Check File existance
			final File file = new File(absolutePath);
			if (file.exists()) {
				AlertTool.showNotification("Opening file", "Opening in File Explorer :\n" + absolutePath,
						Duration.millis(1500), NotificationType.INFORMATION);
				Desktop.getDesktop().open(file);
			} else
				AlertTool.showNotification("Can't open file",
						"Can't open in File Explorer :\n" + absolutePath + " \n because it doesn't exists !",
						Duration.millis(1500), NotificationType.INFORMATION);
		} catch (final IOException ex) {
			AlertTool.showNotification("Problem Occured", "Can't open default File at:\n[" + absolutePath + " ]",
					Duration.millis(2500), NotificationType.INFORMATION);
			ex.printStackTrace();
			//ActionTool.logger.log(Level.INFO, "", ex);
			return false;
		}
		return true;
	}

	/**
	 * Deletes Directory of File.
	 *
	 * @param source The File to be deleted | either if it is directory or File
	 * @return true, if successful
	 */
	public static boolean deleteFile(final File source) {
	
		if (source.isDirectory()) // Directory
			try {
				FileUtils.deleteDirectory(source);
			} catch (final IOException ex) {
				ex.printStackTrace();
				//ActionTool.logger.log(Level.INFO, "", ex);
			}
		else if (source.isFile() && !source.delete()) { // File
			AlertTool.showNotification("Message", "Can't delete file:\n(" + source.getName() + ") cause is in use by a program.",
					Duration.millis(2000), NotificationType.WARNING);
			return false;
		}
	
		return true;
	}

	/**
	 * Creates the given File or Folder if not exists and returns the result
	 * 
	 * @param absoluteFilePath The absolute path of the File|Folder
	 * @param fileType         Create DIRECTORY OR FILE ?
	 * @return True if exists or have been successfully created , otherwise false
	 */
	public static boolean createFileOrFolder(final File file, final FileType fileType) {
		// Already exists?
		if (file.exists())
			return true;
		// Directory?
		if (fileType == FileType.DIRECTORY)
			return file.mkdir();
		// File?
		try {
			return file.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates the given File or Folder if not exists and returns the result
	 * 
	 * @param absoluteFilePath The absolute path of the File|Folder
	 * @param fileType         Create DIRECTORY OR FILE ?
	 * @return True if exists or have been successfully created , otherwise false
	 */
	public static boolean createFileOrFolder(final String absoluteFilePath, final FileType fileType) {
		return createFileOrFolder(new File(absoluteFilePath), fileType);
	}

}
