package main.java.com.goxr3plus.xr3player.utils.general;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.utils.io.IOTool;

/**
 * Provides useful methods for retrieving informations.
 *
 * @author GOXR3PLUS
 */
public final class InfoTool {

	/** The random. */
	public static final Random random = new Random();

	/** Logger */
	public static final Logger logger = Logger.getLogger(InfoTool.class.getName());

	/** WebSite url */
	public static final String WEBSITE_URL = "http://goxr3plus.co.nf";

	/** WebSite url */
	public static final String GITHUB_URL = "https://github.com/goxr3plus/XR3Player";

	/** XR3Player Tutorials */
	public static final String TUTORIALS = "https://www.youtube.com/playlist?list=PL-xqaiRUr_iRKDkpFWPfSRFmJvHSr1VJI";

	private static final String COMMON = "";

	/** The Constant images. */
	public static final String IMAGES = COMMON + "/image/";

	/** The Constant videos. */
	public static final String VIDEOS = COMMON + "/video/";

	public static final String SOUNDS = COMMON + "/sound/";

	/** The Constant styLes. */
	public static final String STYLES = COMMON + "/style/";

	/** The Constant applicationCss. */
	public static final String APPLICATIONCSS = "application.css";

	/** The Constant sounds. */
	// public static final String sounds = "/sound/"

	/** The Constant fxmls. */
	public static final String FXMLS = COMMON + "/fxml/";

	/** The Constant fxmls. */
	public static final String PLAYERS_FXMLS = COMMON + "/fxml/players/";

	public static final String VISUALIZERS_FXMLS = COMMON + "/fxml/visualizer/";

	public static final String BROWSER_FXMLS = COMMON + "/fxml/browser/";

	public static final String SETTINGS_FXMLS = COMMON + "/fxml/settings/";

	public static final String SMARTCONTROLLER_FXMLS = COMMON + "/fxml/smartcontroller/";

	public static final String DROPBOX_FXMLS = COMMON + "/fxml/dropbox/";

	public static final String LIBRARIES_FXMLS = COMMON + "/fxml/library/";

	public static final String USER_FXMLS = COMMON + "/fxml/user/";

	public static final String XR3CAPTURE_FXMLS = COMMON + "/fxml/xr3capture/";

	public static final String TAGS_FXMLS = COMMON + "/fxml/tags/";

	public static final String WINDOW_FXMLS = COMMON + "/fxml/windows/";

	/** The Constant radioStationsTable. */
	public static final String RADIO_STATIONS_DATABASE_TABLE_NAME = "RADIOSTATIONS";

	/** Database folder name <b>with out</b> separator [example:XR3DataBase] */
	private static final String DATABASE_FOLDER_NAME = "XR3DataBase";

	/** The name of the application user [example:Alexander] */
	private static String USERNAME;

	public static final String USER_SETTINGS_FILE_NAME = "config.properties";

	public static final String USER_INFORMATION_FILE_NAME = "userInformation.properties";

	// -----------Lists of accepted extensions
	// Java 8 Way
	// private static final Set<String> ACCEPTED_AUDIO_EXTENSIONS = Stream.of("mp3",
	// "wav", "ogg")
	// .collect(Collectors.toCollection(HashSet::new))
	// private static final Set<String> ACCEPTED_VIDEO_EXTENSIONS = Stream.of("mp4",
	// "flv")
	// .collect(Collectors.toCollection(HashSet::new))
	// private static final Set<String> ACCEPTED_IMAGE_EXTENSIONS = Stream.of("png",
	// "jpg", "jpeg")
	// .collect(Collectors.toCollection(HashSet::new))

	// ------------------------------------Important-------------------------------------------------------------------

	/** The name of the application user [example:Alexander] */
	public static String getUserName() {
		return USERNAME;
	}

	/** The name of the application user [example:Alexander] */
	public static void setUserName(final String uSERNAME) {
		USERNAME = uSERNAME;
	}

	// ----

	public static String getImagesFolderAbsolutePathPlain() {
		return getUserFolderAbsolutePathWithSeparator() + "Images";
	}

	public static String getImagesFolderAbsolutePathWithSeparator() {
		return getImagesFolderAbsolutePathPlain() + File.separator;
	}

	// ----

	public static String getXPlayersImageFolderAbsolutePathPlain() {
		return getImagesFolderAbsolutePathWithSeparator() + "XPlayersImages";
	}

	public static String getXPlayersImageFolderAbsolutePathWithSeparator() {
		return getImagesFolderAbsolutePathPlain() + File.separator;
	}

	// ----

	/**
	 * @return Database folder name <b>with out</b> separator [example:XR3DataBase]
	 */
	public static String getDatabaseFolderName() {
		return DATABASE_FOLDER_NAME;
	}

	/** @return Database folder name with separator [example:XR3DataBase/] */
	public static String getDatabaseFolderNameWithSeparator() {
		return getDatabaseFolderName() + File.separator;
	}

	// ----

	/**
	 * @return The current absolute path to the database <b>PARENT</b> folder with
	 *         separator[example:C:/Users/]
	 */
	public static String getAbsoluteDatabaseParentFolderPathWithSeparator() {
		return InfoTool.getBasePathForClass(InfoTool.class);
	}

	/**
	 * @return The current absolute path to the database <b>PARENT</b> folder
	 *         without separator[example:C:/Users]
	 */
	public static String getAbsoluteDatabaseParentFolderPathPlain() {
		final String parentName = getAbsoluteDatabaseParentFolderPathWithSeparator();
		return parentName.substring(0, parentName.length() - 1);
	}

	// ----

	/**
	 * @return The absolute path to the database folder<b>with out</b> separator
	 *         [example:C:/Users/XR3DataBase]
	 */
	public static String getAbsoluteDatabasePathPlain() {
		return getAbsoluteDatabaseParentFolderPathWithSeparator() + getDatabaseFolderName();
	}

	/**
	 * @return The absolute database path with separator
	 *         [example:C:/Users/XR3DataBase/]
	 */
	public static String getAbsoluteDatabasePathWithSeparator() {
		return getAbsoluteDatabasePathPlain() + File.separator;
	}

	// ----

	public static String getUserFolderAbsolutePathPlain() {
		return getAbsoluteDatabasePathWithSeparator() + getUserName();
	}

	public static String getUserFolderAbsolutePathWithSeparator() {
		return getUserFolderAbsolutePathPlain() + File.separator;
	}

	// ----

	/**
	 * @return XR3Database signature File , i am using this so the user can use any
	 *         name for the exported xr3database zip and has not too worry
	 */
	public static File getDatabaseSignatureFile() {
		return new File(getAbsoluteDatabasePathWithSeparator() + "xr3Original.sig");
	}

	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Private Constructor , we don't want instances of this class
	 */
	private InfoTool() {
	}

	/**
	 * Returns the absolute path of the current directory in which the given class
	 * file is.
	 * 
	 * @param classs * @return The absolute path of the current directory in which
	 *               the class file is. <b>[it ends with File.Separator!!]</b>
	 * @author GOXR3PLUS[StackOverFlow user] + bachden [StackOverFlow user]
	 */
	public static final String getBasePathForClass(final Class<?> classs) {

		// Local variables
		File file;
		String basePath = "";
		boolean failed = false;

		// Let's give a first try
		try {
			file = new File(classs.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			basePath = (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip"))
					? file.getParent()
					: file.getPath();
		} catch (final URISyntaxException ex) {
			failed = true;
			Logger.getLogger(classs.getName()).log(Level.WARNING,
					"Cannot firgue out base path for class with way (1): ", ex);
		}

		// The above failed?
		if (failed)
			try {
				file = new File(classs.getClassLoader().getResource("").toURI().getPath());
				basePath = file.getAbsolutePath();

				// the below is for testing purposes...
				// starts with File.separator?
				// String l = local.replaceFirst("[" + File.separator +
				// "/\\\\]", "")
			} catch (final URISyntaxException ex) {
				Logger.getLogger(classs.getName()).log(Level.WARNING,
						"Cannot firgue out base path for class with way (2): ", ex);
			}

		// fix to run inside Eclipse
		if (basePath.endsWith(File.separator + "lib") || basePath.endsWith(File.separator + "bin")
				|| basePath.endsWith("bin" + File.separator) || basePath.endsWith("lib" + File.separator)) {
			basePath = basePath.substring(0, basePath.length() - 4);
		}
		// fix to run inside NetBeans
		if (basePath.endsWith(File.separator + "build" + File.separator + "classes")) {
			basePath = basePath.substring(0, basePath.length() - 14);
		}
		// end fix
		if (!basePath.endsWith(File.separator))
			basePath += File.separator;

		return basePath;
	}

	// public Image getDragViewImage(Image image,int width,int height) {
	// if(image!=null) {
	// return image;
	// }else {
	//
	// WritableImage image = new WritableImage(100, 100);
	// Canvas canvas = new Canvas();
	// canvas.setWidth(100);
	// canvas.setHeight(100);
	// ActionTool.paintCanvas(canvas.getGraphicsContext2D(), getFileName(), 100,
	// 100);
	// SnapshotParameters params = new SnapshotParameters();
	// params.setFill(Color.TRANSPARENT);
	// return
	// canvas.snapshot(params, image), 50, 0);
	//
	// }
	// }

	/**
	 * Return the imageView of mp3File in requested Width and Height.
	 *
	 * @param absolutePath The File absolute path
	 * @param width        the width
	 * @param height       the height
	 * @return an Image
	 */
	public static Image getAudioAlbumImage(final String absolutePath, final int width, final int height) {
		final ByteArrayInputStream arrayInputStream = getAudioAlbumImageRaw(absolutePath, width, height);

		// Does it contain an image
		if (arrayInputStream != null)
			return (width == -1 && height == -1) ? new Image(arrayInputStream)
					: new Image(arrayInputStream, width, height, false, true);

		return null;
	}

	/**
	 * Return the imageView of mp3File in requested Width and Height.
	 *
	 * @param absolutePath The File absolute path
	 * @param width        the width
	 * @param height       the height
	 * @return ByteArrayInputStream containing the image as binary data
	 */
	public static ByteArrayInputStream getAudioAlbumImageRaw(final String absolutePath, final int width,
			final int height) {
		// Is it mp3?
		if ("mp3".equals(IOTool.getFileExtension(absolutePath)))
			try {
				final Mp3File song = new Mp3File(absolutePath);

				if (song.hasId3v2Tag()) { // has id3v2 tag?

					final ID3v2 id3v2Tag = song.getId3v2Tag();

					if (id3v2Tag.getAlbumImage() != null) // image?
						return new ByteArrayInputStream(id3v2Tag.getAlbumImage());
				}
			} catch (final Exception ex) {
				// logger.log(Level.WARNING, "Can't get Album Image", ex);
			}

		return null;// fatal error here
	}

	// ------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------

	/**
	 * Use this method to retrieve an image from the resources of the application.
	 *
	 * @param imageName the image name
	 * @return Returns an image which is already into the resources folder of the
	 *         application
	 */
	public static Image getImageFromResourcesFolder(final String imageName) {
		return new Image(InfoTool.class.getResourceAsStream(IMAGES + imageName));
	}

	/**
	 * Use this method to retrieve an image from the resources of the application.
	 *
	 * @param imageName the image name
	 * @return Returns an image which is already into the resources folder of the
	 *         application
	 */
	// public static Image getImageFromCurrentFolder(String folderName , String
	// imageName) {
	// return new Image(InfoTool.class.getResourceAsStream("/" + folderName + "/" +
	// imageName));
	// }

	/**
	 * Use this method to retrieve an ImageView from the resources of the
	 * application.
	 *
	 * @param imageName the image name
	 * @return Returns an ImageView using method getImageFromResourcesFolder(String
	 *         imageName);
	 */
	public static ImageView getImageViewFromResourcesFolder(final String imageName) {
		return new ImageView(getImageFromResourcesFolder(imageName));
	}

	/**
	 * Returns the current hour in format h:mm a
	 *
	 * @return the Returns the current hour in format h:mm a
	 */
	public static String getLocalTime() {
		return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
	}

	/**
	 * Returns the Local Date in format dd/MM/yyyy
	 *
	 * @return the local date in format dd/MM/yyyy
	 */
	public static String getCurrentDate() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

	}

	/**
	 * Returns a String with a fixed number of letters.
	 *
	 * @param s       the string
	 * @param letters the letters
	 * @param appender 
	 * @return Substring the current word and append a new given string at the end
	 */
	public static String getMinString(final String s, final int letters,final String appender) {
		return s.length() < letters ? s : s.substring(0, letters) + appender;
	}

	/**
	 * Returns a number with more than 3 digits [ Example 1000 as 1.000] with dots
	 * every 3 digits
	 * 
	 * @param number
	 * @return A number with more than 3 digits [ Example 1000 as 1.000] with dots
	 *         every 3 digits
	 */
	public static String getNumberWithDots(final int number) {
		return String.format(Locale.US, "%,d", number).replace(",", ".");
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
