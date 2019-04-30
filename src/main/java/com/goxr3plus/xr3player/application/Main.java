package com.goxr3plus.xr3player.application;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.goxr3plus.xr3player.controllers.chromium.WebBrowserController;
import com.goxr3plus.xr3player.controllers.djmode.DJMode;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxDownloadsTableViewer;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxViewer;
import com.goxr3plus.xr3player.controllers.librarymode.LibraryMode;
import com.goxr3plus.xr3player.controllers.loginmode.LoginMode;
import com.goxr3plus.xr3player.controllers.loginmode.UserInformation;
import com.goxr3plus.xr3player.controllers.moviemode.MovieModeController;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController;
import com.goxr3plus.xr3player.controllers.systemtree.TreeViewContextMenu;
import com.goxr3plus.xr3player.controllers.systemtree.TreeViewManager;
import com.goxr3plus.xr3player.controllers.tagging.TagWindow;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayersList;
import com.goxr3plus.xr3player.database.DatabaseManager;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.database.PropertiesDb;
import com.goxr3plus.xr3player.models.lists.EmotionListsController;
import com.goxr3plus.xr3player.models.lists.PlayedMediaList;
import com.goxr3plus.xr3player.models.lists.StarredMediaList;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.com.goxr3plus.xr3capture.application.CaptureWindow;

/**
 * The Main JavaFX Application
 *
 * @author GOXR3PLUS STUDIO
 */
public class Main extends Application {

	public static void main(final String[] args) {

		// Launch JavaFX Application
		launch(args);
	}

	// ------------------------------------------------------------------------
	/**
	 * Global Logger
	 */
	public static final Logger logger = Logger.getGlobal();

	/**
	 * Holds global application properties
	 */
	public static final PropertiesDb applicationProperties = new PropertiesDb(
		DatabaseTool.getAbsoluteDatabasePathWithSeparator() + "ApplicationProperties.properties", true);

	// Internal Information
	public static final int APPLICATION_VERSION = 129;
	public static final String RELEASE_DATE = "Check updates window";

	private static final Logger[] pin;

	static {

		// Chromium Extract Location Dir
		System.setProperty("jxbrowser.chromium.dir",
			DatabaseTool.getAbsoluteDatabaseParentFolderPathWithSeparator() + "Chrome" + APPLICATION_VERSION);

		// Disable loggers
		pin = new Logger[]{Logger.getLogger("org.jaudiotagger"), Logger.getLogger("it.sauronsoftware.jave")};
		for (final Logger l : pin)
			l.setLevel(Level.OFF);
	}

	// ------ START: The below have not dependencies on classes ------//

	public static com.goxr3plus.xr3player.controllers.general.WelcomeScreen welcomeScreen;

	public static com.goxr3plus.xr3player.controllers.windows.MediaDeleteWindow mediaDeleteWindow;

	/**
	 * The star window.
	 */
	public static com.goxr3plus.xr3player.controllers.windows.StarWindow starWindow;

	/**
	 * The rename window.
	 */
	public static com.goxr3plus.xr3player.controllers.windows.RenameWindow renameWindow;

	/**
	 * The rename window.
	 */
	public static com.goxr3plus.xr3player.controllers.windows.EmotionsWindow emotionsWindow;

	/**
	 * Audio Tagging Window
	 */
	public static TagWindow tagWindow;

	public static com.goxr3plus.xr3player.controllers.windows.MediaSearchWindow mediaSearchWindow;

	/**
	 * This window is being used to export files from the application to the outside
	 * world
	 */
	public static com.goxr3plus.xr3player.controllers.windows.ExportWindowController exportWindow;

	/**
	 * The About Window of the Application
	 */
	public static com.goxr3plus.xr3player.controllers.windows.AboutWindow aboutWindow;

	/**
	 * The console Window of the Application
	 */
	public static com.goxr3plus.xr3player.controllers.windows.ConsoleWindowController consoleWindow;

	/**
	 * This Window contains the settings for the whole application
	 */
	public static ApplicationSettingsController settingsWindow;

	/**
	 * This class is used to capture the computer Screen or a part of it [ Check
	 * XR3Capture package]
	 */
	public static CaptureWindow captureWindow;

	public static com.goxr3plus.xr3player.controllers.windows.UpdateWindow updateWindow;

	//

	/**
	 * The Top Bar of the Application
	 */
	public static com.goxr3plus.xr3player.controllers.general.TopBar topBar;

	/**
	 * The Bottom Bar of the Application
	 */
	public static com.goxr3plus.xr3player.controllers.general.BottomBar bottomBar;

	/**
	 * The Side Bar of The Application
	 */
	public static com.goxr3plus.xr3player.controllers.general.SideBar sideBar;

	/**
	 * Application Update Screen
	 */
	public static com.goxr3plus.xr3player.controllers.general.MainLoadingScreen updateScreen;

	/**
	 * The TreeView of DJMode
	 */
	public static TreeViewManager treeManager;

	public static com.goxr3plus.xr3player.controllers.smartcontroller.MediaInformation mediaInformation;
	//

	public static TreeViewContextMenu treeViewContextMenu;

	/**
	 * The Constant songsContextMenu.
	 */
	public static com.goxr3plus.xr3player.controllers.smartcontroller.MediaContextMenu songsContextMenu;

	/**
	 * The Constant songsContextMenu.
	 */
	public static com.goxr3plus.xr3player.controllers.smartcontroller.ShopContextMenu shopContextMenu;

	/**
	 * The Constant EmotionListsController.
	 */
	public static EmotionListsController emotionListsController;

	//

	/**
	 * The WebBrowser of the Application
	 */
	public static WebBrowserController webBrowser;

	//

	/**
	 * The Constant specialChooser.
	 */
	public static com.goxr3plus.xr3player.controllers.windows.FileAndFolderChooser specialChooser = new com.goxr3plus.xr3player.controllers.windows.FileAndFolderChooser();

	/**
	 * XPlayList holds the instances of XPlayerControllers
	 */
	public static XPlayersList xPlayersList = new XPlayersList();

	/**
	 * The Constant .
	 */
	public static PlayedMediaList playedSongs = new PlayedMediaList();

	/**
	 * Used to provide ui for drag and view
	 */
	public static com.goxr3plus.xr3player.controllers.smartcontroller.DragViewer dragViewer;

	// ------ END: The above have not dependencies on other classes ------

	// ------ START: Vary basic for the application ------

	/**
	 * The window.
	 */
	public static Stage window;

	/**
	 * The scene.
	 */
	public static BorderlessScene borderlessScene;

	/**
	 * The stack pane root.
	 */
	public static final StackPane applicationStackPane = new StackPane();

	/**
	 * The root.
	 */
	public static final BorderPane root = new BorderPane();

	public static final StackPane rootStackPane = new StackPane();

	/**
	 * The can save data.
	 */
	public static boolean canSaveData = true;

	// ----- END:Vary basic for the application -----//

	// ----- START: The below have dependencies on others -----

	/**
	 * The Constant dbManager.
	 */
	public static DatabaseManager dbManager = new DatabaseManager();

	/**
	 * The Constant libraryMode.
	 */
	public static LibraryMode libraryMode;

	/**
	 * The Constant djMode.
	 */
	public static DJMode djMode;

	public static com.goxr3plus.xr3player.controllers.general.OnlineMusicController onlineMusicController;

	public static DropboxViewer dropBoxViewer;

	public static com.goxr3plus.xr3player.controllers.general.EmotionsTabPane emotionsTabPane;

	public static StarredMediaList starredMediaList;

	public static DropboxDownloadsTableViewer dropboxDownloadsTableViewer;

	/**
	 * The Search Window Smart Controller of the application
	 */
	public static com.goxr3plus.xr3player.controllers.smartcontroller.SmartController searchWindowSmartController;

	public static com.goxr3plus.xr3player.controllers.general.PlayListModesTabPane playListModesTabPane;

	/**
	 * The Constant multipleTabs.
	 */
	public static com.goxr3plus.xr3player.controllers.general.PlayListModesSplitPane playListModesSplitPane;

	/**
	 * The Login Mode where the user of the applications has to choose an account to
	 * login
	 */
	public static LoginMode loginMode;

	/**
	 * Entering in this mode you can change the user settings and other things that
	 * have to do with the user....
	 */
	public static UserInformation userInfoMode;

	public static MovieModeController movieModeController;

	// ----- END: The below have dependencies on  others -----

	@Override
	public void start(final Stage primaryStage) {
		System.out.println("Entered JavaFX Application Start Method");

		// --------Window---------
		window = primaryStage;
		com.goxr3plus.xr3player.application.MainLoader.startPart0();

		System.out.println("XR3Player ready to rock!");
	}

}
