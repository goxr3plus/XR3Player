package main.java.com.goxr3plus.xr3player.utils.general;

import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

	/** The Constant fxmls. */
	public static final String FXMLS = COMMON + "/fxml/";

	/** The Constant fxmls. */
	public static final String PLAYERS_FXMLS = FXMLS + "players/";

	public static final String VISUALIZERS_FXMLS = FXMLS + "visualizer/";

	public static final String BROWSER_FXMLS = FXMLS + "browser/";

	public static final String SETTINGS_FXMLS = FXMLS + "settings/";

	public static final String SMARTCONTROLLER_FXMLS = FXMLS + "smartcontroller/";

	public static final String DROPBOX_FXMLS = FXMLS + "dropbox/";

	public static final String LIBRARIES_FXMLS = FXMLS + "library/";

	public static final String USER_FXMLS = FXMLS + "user/";

	public static final String XR3CAPTURE_FXMLS = FXMLS + "xr3capture/";

	public static final String TAGS_FXMLS = FXMLS + "tags/";

	public static final String WINDOW_FXMLS = FXMLS + "windows/";

	/** The Constant radioStationsTable. */
	public static final String RADIO_STATIONS_DATABASE_TABLE_NAME = "RADIOSTATIONS";

	private InfoTool() {
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

}
