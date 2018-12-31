/*
 * 
 */
package main.java.com.goxr3plus.xr3player.utils.general;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.enums.NotificationType;
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



}
