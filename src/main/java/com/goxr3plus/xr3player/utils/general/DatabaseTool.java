package main.java.com.goxr3plus.xr3player.utils.general;

import java.io.File;
import java.util.Random;

import main.java.com.goxr3plus.xr3player.utils.io.IOInfo;

public final class DatabaseTool {

	public static final Random random = new Random();

	/** Database folder name <b>with out</b> separator [example:XR3DataBase] */
	private static final String DATABASE_FOLDER_NAME = "XR3DataBase";

	/** The name of the application user [example:Alexander] */
	private static String USERNAME;
	public static final String USER_SETTINGS_FILE_NAME = "config.properties";
	public static final String USER_INFORMATION_FILE_NAME = "userInformation.properties";

	private DatabaseTool() {
	}

	// ----

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
		return DatabaseTool.getUserFolderAbsolutePathWithSeparator() + "Images";
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
		return IOInfo.getBasePathForClass(InfoTool.class);
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

	/**
	 * Return random table name.
	 *
	 * @return Returns a RandomTableName for the database in format
	 *         ("_"+randomNumber)
	 */
	public static String returnRandomTableName() {
		return "_" + DatabaseTool.returnRandom(80000);
	}

	/**
	 * Returns a Random Number from 0 to ...what i have choosen in method see the
	 * doc
	 *
	 * @return A random integer
	 */
	public static int returnRandom(final int max) {
		return random.nextInt(max);
	}

	// ----

}
