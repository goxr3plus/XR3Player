package main.java.com.goxr3plus.xr3player.utils.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;

import main.java.com.goxr3plus.xr3player.application.enums.FileType;
import main.java.com.goxr3plus.xr3player.utils.general.ActionTool;
import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;

public class IOTool {

	/**
	 * Returns the real Path of the given File , either it is symbolic link or hard
	 * link or Windows Shortcut File
	 * 
	 * @param absoluteFilePath
	 */
	public static FileTypeAndAbsolutePath getRealPathFromFile(String absoluteFilePath) {
		Path path = Paths.get(absoluteFilePath);
		File file = path.toFile();

		// Check if file exists
		// if (!file.exists())
		// return new FileTypeAndAbsolutePath(FileType.ORIGINAL_FILE, absoluteFilePath);

		// ---------------Check if it is symbolic link
		if (Files.isSymbolicLink(path))
			try {
				// If yes return the real file name
				absoluteFilePath = Files.readSymbolicLink(path).toFile().getAbsolutePath();
				return new FileTypeAndAbsolutePath(FileType.SYMBOLIC_LINK, absoluteFilePath);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		// ------------Check if it Windows ShortCut File
		else
			try {
				// If yes returns the real file name
				if ("lnk".equals(IOTool.getFileExtension(absoluteFilePath))
						&& WindowsShortcut.isPotentialValidLink(file))
					absoluteFilePath = new WindowsShortcut(file).getRealFilename();
				return new FileTypeAndAbsolutePath(FileType.SHORTCUT, absoluteFilePath);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}

		return new FileTypeAndAbsolutePath(FileType.ORIGINAL_FILE, absoluteFilePath);
	}

	/**
	 * Calculates the creationTime of the File.
	 *
	 * @param path the path
	 * @return FileTime
	 */
	public static FileTime getFileCreationTime(String path) {
		try {
			return Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime();
		} catch (IOException ex) {
			ActionTool.logger.log(Level.INFO, "", ex);
		}
	
		return null;
	}

	/**
	 * Calculates the creationTime of the File.
	 *
	 * @param path the path
	 * @return A String in format <b> DD/MM/YYYY</b>
	 */
	public static String getFileDateCreated(String path) {
	
		FileTime creationTime = getFileCreationTime(path);
	
		// Be carefull for null pointer exception here
		if (creationTime == null)
			return "error occured";
	
		String[] dateCreatedF = creationTime.toString().split("-");
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

}
