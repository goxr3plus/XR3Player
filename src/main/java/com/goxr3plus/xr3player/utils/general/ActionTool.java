/*
 * 
 */
package main.java.com.goxr3plus.xr3player.utils.general;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.enums.NotificationType;
import main.java.com.goxr3plus.xr3player.utils.io.IOTool;
import main.java.com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * A class which has a lot of useful methods.
 *
 * @author GOXR3PLUS
 */
public final class ActionTool {

	/** The logger for this class */
	public static final Logger logger = Logger.getLogger(ActionTool.class.getName());

	/** The random. */
	private static Random random = new Random();

	/**
	 * Private Constructor.
	 */
	private ActionTool() {
	}

	/**
	 * Opens the file with the System default file explorer.
	 *
	 * @param path the path
	 */
	public static void openFileInExplorer(String path) {

		// Open the Default Browser
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			AlertTool.showNotification("Message", "Opening in File Explorer:\n" + IOTool.getFileName(path),
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
				logger.log(Level.WARNING, ex.getMessage(), ex);
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

//	/**
//	 * Copy a file from source to destination.
//	 *
//	 * @param source      the source
//	 * @param destination the destination
//	 * @return True if succeeded , False if not
//	 */
//	public static boolean copy(final String source, final String destination) {
//		boolean succeess = true;
//
//		// System.out.println("Copying ->" + source + "\n\tto ->" + destination)
//
//		try {
//			Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
//		} catch (final IOException ex) {
//			logger.log(Level.WARNING, "", ex);
//			succeess = false;
//		}
//
//		return succeess;
//
//	}
//
//	/**
//	 * Copy a file from source to destination.
//	 *
//	 * @param source      the source
//	 * @param destination the destination
//	 * @return True if succeeded , False if not
//	 */
//	public static boolean copy(final InputStream source, final String destination) {
//		boolean succeess = true;
//
//		// System.out.println("Copying ->" + source + "\n\tto ->" + destination)
//
//		try {
//			System.out.println(Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING));
//		} catch (final IOException ex) {
//			logger.log(Level.WARNING, "", ex);
//			succeess = false;
//		}
//
//		return succeess;
//	}

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
			logger.log(Level.INFO, "", ex);
			return false;
		}
		return true;
	}

	/**
	 * Tries to open that URI on the default browser
	 * 
	 * @param uri
	 * @return <b>True</b> if succeeded , <b>False</b> if not
	 */
	public static boolean openWebSite(final String uri) {

		// Check if Desktop is supported
		if (!Desktop.isDesktopSupported()) {
			AlertTool.showNotification("Problem Occured", "Can't open default web browser at:\n[" + uri + " ]",
					Duration.millis(2500), NotificationType.INFORMATION);
			return false;
		}

		AlertTool.showNotification("Opening WebSite", "Opening on default Web Browser :\n" + uri,
				Duration.millis(1500), NotificationType.INFORMATION);

		// Start it to a new Thread , don't lag the JavaFX Application Thread
		new Thread(() -> {
			try {
				Desktop.getDesktop().browse(new URI(uri));
			} catch (IOException | URISyntaxException ex) {
				AlertTool.showNotification("Problem Occured", "Can't open default web browser at:\n[" + uri + " ]",
						Duration.millis(2500), NotificationType.INFORMATION);
				logger.log(Level.INFO, "", ex);
			}
		}).start();

		return true;
	}

	/**
	 * Returns a Random Number from 0 to ...what i have choosen in method see the
	 * doc
	 *
	 * @return A random integer
	 */
	public static int returnRandom() {
		return random.nextInt(80000);
	}

	/**
	 * Return random table name.
	 *
	 * @return Returns a RandomTableName for the database in format
	 *         ("_"+randomNumber)
	 */
	public static String returnRandomTableName() {
		return "_" + returnRandom();
	}

	/**
	 * The Type of File
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public enum FileType {
		DIRECTORY, FILE, ZIP;
	}

}
