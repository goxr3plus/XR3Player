package main.java.com.goxr3plus.xr3player.utils.general;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.utils.io.IOInfo;

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

	/**
	 * Private Constructor , we don't want instances of this class
	 */
	private InfoTool() {
	}

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
		if ("mp3".equals(IOInfo.getFileExtension(absolutePath)))
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
	 * @param s        the string
	 * @param letters  the letters
	 * @param appender
	 * @return Substring the current word and append a new given string at the end
	 */
	public static String getMinString(final String s, final int letters, final String appender) {
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
