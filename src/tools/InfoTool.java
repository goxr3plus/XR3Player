/*
 * 
 */
package tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import application.Main;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import xplayer.presenter.AudioType;

/**
 * Provides useful methods for retrieving informations.
 *
 * @author SuperGoliath
 */
public final class InfoTool {

    /** Logger */
    public static final Logger logger = Logger.getLogger(InfoTool.class.getName());

    /** The song. */
    static Mp3File song;

    /** WebSite url */
    public static final String website = "http://goxr3plus.co.nf";

    /** The Constant images. */
    public static final String images = "/image/";

    /** The os name. */
    public static final String osName = System.getProperty("os.name");

    /** The Constant styLes. */
    public static final String styLes = "/style/";

    /** The Constant applicationCss. */
    public static final String applicationCss = "application.css";

    /** The Constant sounds. */
    public static final String sounds = "/sound/";

    /** The Constant fxmls. */
    public static final String fxmls = "/fxml/";

    // ----------------Important-----------------------------

    /** Database folder name <b>with out</b> separator [example:XR3DataBase] */
    public static final String DATABASE_FOLDER_NAME = "XR3DataBase";

    /** Database folder name with separator [example:XR3DataBase/] */
    public static final String DATABASE_FOLDER_NAME_WITH_SEPARATOR = DATABASE_FOLDER_NAME + File.separator;

    // --------

    /**
     * The current absolute path to the database <b>PARENT</b> folder with separator[example:C:/Users/]
     */
    public static final String ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_WITH_SEPARATOR = InfoTool
	    .getBasePathForClass(InfoTool.class);

    /**
     * The current absolute path to the database <b>PARENT</b> folder without separator[example:C:/Users]
     */
    public static final String ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_PLAIN = ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_WITH_SEPARATOR
	    .substring(0, ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_WITH_SEPARATOR.length() - 1);

    // --------

    /** The absolute path to the database folder<b>with out</b> separator [example:C:/Users/XR3DataBase] */
    public static final String ABSOLUTE_DATABASE_PATH_PLAIN = ABSOLUTE_DATABASE_PARENT_FOLDER_PATH_WITH_SEPARATOR
	    + DATABASE_FOLDER_NAME;

    /** The absolute database path with separator [example:C:/Users/XR3DataBase/] */
    public static final String ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR = ABSOLUTE_DATABASE_PATH_PLAIN + File.separator;

    // --------------------------------------------------------------------------------------------------------------

    /** The Constant radioStationsTable. */
    public static final String RADIO_STATIONS_DATABASE_TABLE_NAME = "RADIOSTATIONS";

    /** The Constant playedImage. */
    public static final Image playedImage = getImageFromDocuments("played.png");

    /** The contains. */
    // private static boolean contains

    /**
     * Instantiates a new info tool.
     */
    private InfoTool() {

    }

    /**
     * Gets the screen width.
     *
     * @return The screen <b>Width</b> based on the <b> bounds </b> of the Screen.
     */
    public static double getScreenWidth() {
	return Screen.getPrimary().getBounds().getWidth();
    }

    /**
     * Gets the screen height.
     *
     * @return The screen <b>Height</b> based on the <b> bounds </b> of the Screen.
     */
    public static double getScreenHeight() {
	return Screen.getPrimary().getBounds().getHeight();
    }

    /**
     * Gets the visual screen width.
     *
     * @return The screen <b>Width</b> based on the <b>visual bounds</b> of the Screen.These bounds account for objects in the native windowing system
     *         such as task bars and menu bars. These bounds are contained by Screen.bounds.
     */
    public static double getVisualScreenWidth() {
	return Screen.getPrimary().getVisualBounds().getWidth();
    }

    /**
     * Gets the visual screen height.
     *
     * @return The screen <b>Height</b> based on the <b>visual bounds</b> of the Screen.These bounds account for objects in the native windowing
     *         system such as task bars and menu bars. These bounds are contained by Screen.bounds.
     */
    public static double getVisualScreenHeight() {
	return Screen.getPrimary().getVisualBounds().getHeight();
    }

    /**
     * Returns the absolute path of the current directory in which the given class file is.
     * 
     * @param classs
     *            * @return The absolute path of the current directory in which the class file is. <b>[it ends with File.Separator!!]</b>
     * @author GOXR3PLUS[StackOverFlow user] + bachden [StackOverFlow user]
     */
    public static final String getBasePathForClass(Class<?> classs) {

	// Local variables
	File file;
	String basePath = "";
	boolean failed = false;

	// Let's give a first try
	try {
	    file = new File(classs.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

	    if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
		basePath = file.getParent();
	    } else {
		basePath = file.getPath();
	    }
	} catch (URISyntaxException ex) {
	    failed = true;
	    Logger.getLogger(classs.getName()).log(Level.WARNING,
		    "Cannot firgue out base path for class with way (1): ", ex);
	}

	// The above failed?
	if (failed) {
	    try {
		file = new File(classs.getClassLoader().getResource("").toURI().getPath());
		basePath = file.getAbsolutePath();

		// the below is for testing purposes...
		// starts with File.separator?
		// String l = local.replaceFirst("[" + File.separator +
		// "/\\\\]", "")
	    } catch (URISyntaxException ex) {
		Logger.getLogger(classs.getName()).log(Level.WARNING,
			"Cannot firgue out base path for class with way (2): ", ex);
	    }
	}

	// fix to run inside eclipse
	if (basePath.endsWith(File.separator + "lib") || basePath.endsWith(File.separator + "bin")
		|| basePath.endsWith("bin" + File.separator) || basePath.endsWith("lib" + File.separator)) {
	    basePath = basePath.substring(0, basePath.length() - 4);
	}
	// fix to run inside netbeans
	if (basePath.endsWith(File.separator + "build" + File.separator + "classes")) {
	    basePath = basePath.substring(0, basePath.length() - 14);
	}
	// end fix
	if (!basePath.endsWith(File.separator)) {
	    basePath = basePath + File.separator;
	}
	return basePath;
    }

    /**
     * Checks if a web site is reachable using ping command.
     *
     * @param host
     *            the host
     * @return <b> true </b> if Connected on Internet,<b> false </b> if not.
     */
    public static boolean isReachableByPing(String host) {
	try {

	    String cmd;
	    if (osName.toLowerCase().startsWith("windows")) {
		// For Windows
		cmd = "ping -n 1 " + host;
	    } else {
		// For Linux and OSX
		cmd = "ping -c 1 " + host;
	    }

	    // Start a new Process
	    Process myProcess = Runtime.getRuntime().exec(cmd);
	    myProcess.waitFor();

	    if (myProcess.exitValue() == 0)
		return true;
	    else
		return false;

	} catch (Exception ex) {
	    Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
	    return false;
	}
    }

    /**
     * Return the imageView of mp3File in requested Width and Height.
     *
     * @param path
     *            the path
     * @param width
     *            the width
     * @param height
     *            the height
     * @return an Image
     */
    public static Image getMp3AlbumImage(String path, int width, int height) {
	if ("mp3".equals(getFileExtension(path))) {
	    try {
		song = new Mp3File(path);

		if (song.hasId3v2Tag()) { // has id3v2 tag?

		    ID3v2 id3v2Tag = song.getId3v2Tag();

		    if (id3v2Tag.getAlbumImage() != null) // image?
			return (width == -1 && height == -1)
				? new Image(new ByteArrayInputStream(id3v2Tag.getAlbumImage()))
				: new Image(new ByteArrayInputStream(id3v2Tag.getAlbumImage()), width, height, false,
					true);
		}
	    } catch (UnsupportedTagException | InvalidDataException | IOException ex) {
		logger.log(Level.WARNING, "Can't get Album Image", ex);
	    }
	}

	return null;// fatal error here
    }

    /**
     * Returns the creation time. The creation time is the time that the file was created.
     *
     * <p>
     * If the file system implementation does not support a time stamp to indicate the time when the file was created then this method returns an
     * implementation specific default value, typically the {@link #lastModifiedTime() last-modified-time} or a {@code FileTime} representing the
     * epoch (1970-01-01T00:00:00Z).
     *
     * @param filePath
     * @return The File Creation Date in String Format
     */
    public static String getFileCreationDate(String filePath) {
	File file = new File(filePath);
	// exists?
	if (file.exists()) {
	    BasicFileAttributes attr;
	    try {
		attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
	    } catch (IOException ex) {
		logger.log(Level.WARNING, ex.getMessage(), ex);
		return "error";
	    }
	    return attr.creationTime().toString().replaceAll("T|Z", " ");
	}
	return "file not exists";
    }

    /**
     * Returns the time of last modification.
     *
     * <p>
     * If the file system implementation does not support a time stamp to indicate the time of last modification then this method returns an
     * implementation specific default value, typically a {@code FileTime} representing the epoch (1970-01-01T00:00:00Z).
     *
     * @param filePath
     * @return The File Creation Date in String Format
     */
    public static String getFileLastModifiedDate(String filePath) {
	File file = new File(filePath);
	// exists?
	if (file.exists()) {
	    BasicFileAttributes attr;
	    try {
		attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
	    } catch (IOException ex) {
		logger.log(Level.WARNING, ex.getMessage(), ex);
		return "error";
	    }
	    return attr.lastModifiedTime().toString().replaceAll("T|Z", " ");
	}

	else
	    return "file not exists";
    }

    /**
     * Returns the title of the file for example if file name is <b>(club.mp3)</b> it returns <b>(club)</b>
     *
     * @param path
     *            the path
     * @return the File title
     */
    public static String getFileTitle(String path) {
	return FilenameUtils.getBaseName(path);
    }

    /**
     * Returns the name of the file for example if file path is <b>(C:/Give me more/no no/media.ogg)</b> it returns <b>(media.ogg)</b>
     *
     * @param path
     *            the path
     * @return the File title+extension
     */
    public static String getFileName(String path) {
	return FilenameUtils.getName(path);

    }

    /**
     * Returns the extension of file(without (.)) for example <b>(ai.mp3)->(mp3)</b>
     *
     * @param path
     *            the path
     * @return the File extension
     */
    public static String getFileExtension(String path) {
	return FilenameUtils.getExtension(path).toLowerCase();

	// int i = path.lastIndexOf('.'); // characters contained before (.)
	//
	// if (i > 0 && i < path.length() - 1) // if the name is not empty
	// return path.substring(i + 1).toLowerCase();
	//
	// return null;
    }

    /**
     * 1)Checks if this file is <b>audio</b><br>
     * 2)If is supported by the application.
     *
     * @param name
     *            the name
     * @return true if the type is supported or else false
     */
    public static boolean isAudioSupported(String name) {
	String extension = getFileExtension(name);

	if (extension != null && ("mp3".equals(extension) || "wav".equals(extension) || "ogg".equals(extension)))
	    // extension.equals("ogg")
	    // ||
	    // extension.equals("wav")
	    // || extension.equals("au") || extension.equals("flac") ||
	    // extension.equals("aiff")
	    // || extension.equals("speex")))
	    return true;

	return false;
    }

    /**
     * 1)Checks if this file is <b>video</b><br>
     * 2)If is supported by the application.
     *
     * @param name
     *            the name
     * @return true if the type is supported or else false
     */
    public static boolean isVideoSupported(String name) {
	String extension = getFileExtension(name);

	if (extension != null && ("mp4".equals(extension) || "flv".equals(extension)))
	    return true;

	return false;
    }

    /**
     * 1)Checks if this file is <b>video</b><br>
     * 2)If is supported by the application.
     *
     * @param name
     *            the name
     * @return true if the file is an Image
     */
    public static boolean isImage(String name) {
	String extension = getFileExtension(name);

	if (extension != null && ("png".equals(extension) || "jpg".equals(extension) || "jpeg".equals(extension)))
	    return true;

	return false;
    }

    /**
     * Use this method to retrieve an image from the resources of the application.
     *
     * @param imageName
     *            the image name
     * @return Returns an image which is already into the resources folder of the application
     */
    public static Image getImageFromDocuments(String imageName) {
	return new Image(InfoTool.class.getResourceAsStream(images + imageName));
    }

    /**
     * Use this method to retrieve an ImageView from the resources of the application.
     *
     * @param imageName
     *            the image name
     * @return Returns an ImageView using method getImageFromDocumuments(String imageName);
     */
    public static ImageView getImageViewFromDocuments(String imageName) {
	return new ImageView(new Image(InfoTool.class.getResourceAsStream(images + imageName)));
    }

    /**
     * Returns the current hour in format hh:mm:ss.
     *
     * @return the LocalTime
     */
    public static String getLocalTime() {
	return LocalTime.now().toString();
    }

    /**
     * Returns the Local Date in format YYYY-MM-DD.
     *
     * @return the local date in format YYYY-MM-DD
     */
    public static String getCurrentDate() {
	return LocalDate.now().toString();

    }

    /**
     * Returns a String with a fixed number of letters.
     *
     * @param string
     *            the string
     * @param letters
     *            the letters
     * @return A substring(or the current given string) based on the letters that have to be cut
     */
    public static String getMinString(String string, int letters) {
	if (string.length() < letters)
	    return string;
	else
	    return string.substring(0, letters) + "...";
    }

    /**
     * This method determines the duration of given data.
     *
     * @param input
     *            The name of the input
     * @param type
     *            URL, FILE, INPUTSTREAM, UNKOWN;
     * @return Returns the duration of URL/FILE/INPUTSTREAM in milliseconds
     */
    public static int durationInMilliseconds(String input, AudioType type) {

	if (type == AudioType.FILE)
	    return fileDuration(new File(input));
	// else if (type == TYPE.URL)
	// return -1;
	// else if (type == TYPE.INPUTSTREAM)
	// return -1;
	// else if (type == TYPE.UNKOWN)
	// return -1;

	return -1;
    }

    /**
     * Used by method durationInMilliseconds() to get file duration.
     *
     * @param file
     *            the file
     * @return the int
     */
    private static int fileDuration(File file) {

	// exists?
	if (file.exists() && file.length() != 0) {

	    // extension?
	    String extension = InfoTool.getFileExtension(file.getName());

	    // MP3?
	    if ("mp3".equals(extension)) {
		try {
		    return (int) ((Long) AudioSystem.getAudioFileFormat(file).properties().get("duration") / 1000);
		} catch (IOException | UnsupportedAudioFileException ex) {
		    logger.log(Level.WARNING, ex.getMessage(), ex);
		}
	    }

	    // WAVE || OGG?
	    if ("ogg".equals(extension) || "wav".equals(extension)) {
		try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
		    AudioFormat format = audioInputStream.getFormat();
		    return (int) (file.length() / (format.getFrameSize() * (int) format.getFrameRate())) * 1000;
		} catch (IOException | UnsupportedAudioFileException ex) {
		    logger.log(Level.WARNING, ex.getMessage(), ex);
		}
	    }
	}

	// System.out.println("Passed with error")
	return -1;
    }

    /**
     * Επιστρέφει τον χρόνο του άσματος σε δευτερόλεπτα.
     *
     * @param name
     *            the name
     * @param type
     *            <br>
     *            1->URL <br>
     *            2->FILE <br>
     *            3->INPUTSTREAM
     * @return time in milliseconds
     */
    public static int durationInSeconds(String name, AudioType type) {

	int time = durationInMilliseconds(name, type);

	return (time == 0 || time == -1) ? time : time / 1000;

	/*
	 * παίρνω τα microseconds Long microseconds =
	 * (Long)AudioSystem.getAudioFileFormat(new
	 * File(kommati)).properties().get("duration") int mili =
	 * (int)(microseconds / 1000L); //από microseconds σε δεύτερα int sec =
	 * mili / 1000 % 60; //από δεύτερα σε δευτερόλεπτα int min = mili / 1000
	 * / 60; //από δεύτερα σε λεπτά time = min * 60 + sec
	 */

    }

    /**
     * /** Returns the time in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02d:%02d.
     *
     * @param ms
     *            The milliseconds
     * @return The Time edited in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02d:%02d.
     * 
     */
    public static String millisecondsToTime(long ms) {
	int millis = (int) ((ms % 1000) / 100);
	//	int seconds = (int) ((ms / 1000) % 60);
	//	int minutes = (int) ((ms / (1000 * 60)) % 60);
	//	int hours = (int) ((ms / (1000 * 60 * 60)) % 24);

	//	if (minutes > 60)
	//	    return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, millis);
	//	else
	//	    return String.format("%02d:%02d.%d", minutes, seconds, millis);

	return String.format(".%d", millis);

    }

    /**
     * Returns the time in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02dsec if (seconds<60) %02d:%02d.
     *
     * @param seconds
     *            the seconds
     * @return the time edited in format <b> %02d:%02d:%02d if( minutes >60 )</b> or %02d:%02d.
     */
    public static String getTimeEdited(int seconds) {

	// duration < 1 minute
	if (seconds < 60)
	    return String.format("%02ds", seconds % 60);
	// duration >= 1 hour
	else if ((seconds / 60) / 60 > 0)
	    return String.format("%02dh:%02dm:%02d", (seconds / 60) / 60, (seconds / 60) % 60, seconds % 60);
	else
	    return String.format("%02dm:%02d", (seconds / 60) % 60, seconds % 60);

    }

    /**
     * Returns the time in format %02d:%02d.
     *
     * @param seconds
     *            the seconds
     * @return the time edited on hours
     */
    public static String getTimeEditedOnHours(int seconds) {

	return String.format("%02d:%02d", seconds / 60, seconds % 60);

    }

    /**
     * Gets the file size edited.
     *
     * @param file
     *            the file
     * @return <b> a String representing the file size in MB and kB </b>
     */
    public static String getFileSizeEdited(File file) {
	double bytes = file.length();
	int kilobytes = (int) (bytes / 1024);
	int megabytes = kilobytes / 1024;
	int gigabytes = megabytes / 1024;

	if (kilobytes < 1024) {
	    return kilobytes + " KiB";
	} else if (kilobytes > 1024) {
	    return megabytes + " MiB";
	} else if (megabytes > 1024) {
	    return gigabytes + " GiB";
	}

	return "error";
    }

    // /**
    // * Checks if the list contains at least one accepted file.
    // *
    // * @param list
    // * the list
    // * @return true, if successful
    // */
    // @Deprecated
    // private static boolean containsAcceptedFiles(List<File> list) {
    // contains = false;
    // for (File file : list) {
    // try (Stream<Path> paths = Files.walk(Paths.get(file.getPath()))) {
    // paths.forEach(path -> {
    // if (Files.isRegularFile(path))
    // if (isAudioSupported(path.toString())) {
    // contains = true;
    // System.out.println(path.toString());
    // paths.close();
    // }
    // });
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }
    //
    // return contains;
    // }

}
