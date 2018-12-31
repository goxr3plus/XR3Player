package main.java.com.goxr3plus.xr3player.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

import main.java.com.goxr3plus.xr3player.utils.general.ActionTool;

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
			ActionTool.logger.log(Level.WARNING, "", ex);
			succeess = false;
		}

		return succeess;
	}

}
