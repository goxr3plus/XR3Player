package main.java.com.goxr3plus.xr3player.utils.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.enums.FileLinkType;
import main.java.com.goxr3plus.xr3player.application.enums.NotificationType;
import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;
import main.java.com.goxr3plus.xr3player.utils.javafx.AlertTool;

public class IOTool {

	/**
	 * Returns the real Path of the given File , either it is symbolic link or hard
	 * link or Windows Shortcut File
	 * 
	 * @param absoluteFilePath
	 */
	public static FileTypeAndAbsolutePath getRealPathFromFile(String absoluteFilePath) {
		final Path path = Paths.get(absoluteFilePath);
		final File file = path.toFile();

		// Check if file exists
		// if (!file.exists())
		// return new FileTypeAndAbsolutePath(FileType.ORIGINAL_FILE, absoluteFilePath);

		// ---------------Check if it is symbolic link
		if (Files.isSymbolicLink(path))
			try {
				// If yes return the real file name
				absoluteFilePath = Files.readSymbolicLink(path).toFile().getAbsolutePath();
				return new FileTypeAndAbsolutePath(FileLinkType.SYMBOLIC_LINK, absoluteFilePath);
			} catch (final IOException e1) {
				e1.printStackTrace();
			}

		// ------------Check if it Windows ShortCut File
		else
			try {
				// If yes returns the real file name
				if ("lnk".equals(IOTool.getFileExtension(absoluteFilePath))
						&& WindowsShortcut.isPotentialValidLink(file))
					absoluteFilePath = new WindowsShortcut(file).getRealFilename();
				return new FileTypeAndAbsolutePath(FileLinkType.SHORTCUT, absoluteFilePath);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}

		return new FileTypeAndAbsolutePath(FileLinkType.ORIGINAL_FILE, absoluteFilePath);
	}

	/**
	 * Calculates the creationTime of the File.
	 *
	 * @param path the path
	 * @return FileTime
	 */
	public static FileTime getFileCreationTime(final String path) {
		try {
			return Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime();
		} catch (final IOException ex) {
			ex.printStackTrace();
			//ActionTool.logger.log(Level.INFO, "", ex);
		}

		return null;
	}

	/**
	 * Calculates the creationTime of the File.
	 *
	 * @param path the path
	 * @return A String in format <b> DD/MM/YYYY</b>
	 */
	public static String getFileDateCreated(final String path) {

		final FileTime creationTime = getFileCreationTime(path);

		// Be carefull for null pointer exception here
		if (creationTime == null)
			return "error occured";

		final String[] dateCreatedF = creationTime.toString().split("-");
		return dateCreatedF[2].substring(0, 2) + "/" + dateCreatedF[1] + "/" + dateCreatedF[0];
	}

	/**
	 * Returns the creation time. The creation time is the time that the file was
	 * created.
	 *
	 * <p>
	 * If the file system implementation does not support a time stamp to indicate
	 * the time when the file was created then this method returns an implementation
	 * specific default value, typically the {@link #lastModifiedTime()
	 * last-modified-time} or a {@code FileTime} representing the epoch
	 * (1970-01-01T00:00:00Z).
	 *
	 * @param absolutePath The File absolute path
	 * @return The File Creation Date in String Format
	 */
	public static String getFileCreationDate(final String absolutePath) {
		final File file = new File(absolutePath);
		// exists?
		if (!file.exists())
			return "file missing";

		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		} catch (final IOException ex) {
			InfoTool.logger.log(Level.WARNING, ex.getMessage(), ex);
			return "error";
		}
		return (attr.creationTime() + "").replaceAll("T|Z", " ");
	}

	/**
	 * Returns the Date the File Created in Format `dd/mm/yyyy`
	 * 
	 * @param file The File to be given
	 * @return the Date the File Created in Format `dd/mm/yyyy`
	 */
	public static String getFileCreationDate(final File file) {
		final Path path = Paths.get(file.getAbsolutePath());
		BasicFileAttributes attr;

		/* File not exists */
		if (!file.exists())
			return "file missing";

		try {
			attr = Files.readAttributes(path, BasicFileAttributes.class);

			return new SimpleDateFormat("dd/MM/yyyy").format(attr.creationTime().toMillis());

		} catch (final IOException e) {
			e.printStackTrace();
			return "oops error! ";
		}
	}

	/**
	 * Returns the Time the File Created in Format `h:mm a`
	 * 
	 * @param file The File to be given
	 * @return the Time the File Created in Format `HH:mm:ss`
	 */
	public static String getFileCreationTime(final File file) {
		final Path path = Paths.get(file.getAbsolutePath());
		BasicFileAttributes attr;

		/* File not exists */
		if (!file.exists())
			return "file missing";

		try {
			attr = Files.readAttributes(path, BasicFileAttributes.class);

			return new SimpleDateFormat("h:mm a").format(attr.creationTime().toMillis());

		} catch (final IOException e) {
			e.printStackTrace();
			return "oops error! ";
		}
	}

	/**
	 * Returns the time of last modification.
	 *
	 * <p>
	 * If the file system implementation does not support a time stamp to indicate
	 * the time of last modification then this method returns an implementation
	 * specific default value, typically a {@code FileTime} representing the epoch
	 * (1970-01-01T00:00:00Z).
	 *
	 * @param absolutePath The File absolute path
	 * @return The File Creation Date in String Format
	 */
	public static String getFileLastModifiedDate(final String absolutePath) {
		final File file = new File(absolutePath);
		// exists?
		if (!file.exists())
			return "file missing";

		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		} catch (final IOException ex) {
			InfoTool.logger.log(Level.WARNING, ex.getMessage(), ex);
			return "error";
		}
		return (attr.lastModifiedTime() + "").replaceAll("T|Z", " ");
	}

	/**
	 * Returns the extension of file(without (.)) for example <b>(ai.mp3)->(mp3)</b>
	 * and to lowercase (Mp3 -> mp3)
	 *
	 * @param absolutePath The File absolute path
	 * @return the File extension
	 */
	public static String getFileExtension(final String absolutePath) {
		return FilenameUtils.getExtension(absolutePath).toLowerCase();

		// int i = path.lastIndexOf('.'); // characters contained before (.)
		//
		// if the name is not empty
		// if (i > 0 && i < path.length() - 1)
		// return path.substring(i + 1).toLowerCase()
		//
		// return null
	}

	/**
	 * Returns the name of the file for example if file path is <b>(C:/Give me
	 * more/no no/media.ogg)</b> it returns <b>(media.ogg)</b>
	 *
	 * @param absolutePath the path
	 * @return the File title+extension
	 */
	public static String getFileName(final String absolutePath) {
		return FilenameUtils.getName(absolutePath);

	}

	/**
	 * Returns the title of the file for example if file name is <b>(club.mp3)</b>
	 * it returns <b>(club)</b>
	 *
	 * @param absolutePath The File absolute path
	 * @return the File title
	 */
	public static String getFileTitle(final String absolutePath) {
		return FilenameUtils.getBaseName(absolutePath);
	}

	/**
	 * Gets the file size edited in format "x MiB , y KiB"
	 *
	 * @param bytes File size in bytes
	 * @return <b> a String representing the file size in MB and kB </b>
	 */
	public static String getFileSizeEdited(final long bytes) {

		// Find it
		final int kilobytes = (int) (bytes / 1024), megabytes = kilobytes / 1024;
		if (kilobytes < 1024)
			return kilobytes + " KiB";
		else if (kilobytes > 1024)
			return megabytes + "." + (kilobytes - (megabytes * 1024)) + " MiB";

		return "error";

	}

	/**
	 * Gets the file size edited in format "x MiB , y KiB"
	 *
	 * @param file the file
	 * @return <b> a String representing the file size in MB and kB </b>
	 */
	public static String getFileSizeEdited(final File file) {
		return !file.exists() ? "file missing" : getFileSizeEdited(file.length());
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



}
