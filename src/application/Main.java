/*
 * 
 */
package application;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;
import org.fxmisc.richtext.InlineCssTextArea;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import borderless.BorderlessScene;
import database.LocalDBManager;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import librarymode.LibraryMode;
import remote_communication.RemoteAppsController;
import services.VacuumProgress;
import smartcontroller.MediaContextMenu;
import smartcontroller.Operation;
import smartcontroller.PlayedSongs;
import smartcontroller.SmartSearcher.AdvancedSearch;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import treeview.TreeViewManager;
import windows.DJMode;
import windows.ExportWindowController;
import windows.RenameWindow;
import windows.SpecialChooser;
import windows.StarWindow;
import xplayer.presenter.XPlayersList;

/**
 * The Main class from which the application is starting.
 *
 * @author GOXR3PLUS
 */
public class Main extends Application {

    /** Application logger. */
    public static final Logger logger = Logger.getGlobal();

    /**
     * Indicates where the drag is coming from i use this cause for example when
     * you drag a song from a library that song can not be dragged into the same
     * library again cause is stupid that to happen.
     *
     * @author SuperGoliath
     */
    public enum DragOwner {

	/** The library song. */
	LIBRARYSONG,
	/** The xplaylist song. */
	XPLAYLISTSONG,
	/** The unknown. */
	UNKNOWN;
    }

    /** Indicates which is the owner of the drag. */
    // public static DragOwner dragOwner = DragOwner.UNKNOWN

    /** The Constant tagWindow. */
    // public static final TagWindow tagWindow = new TagWindow()

    /** The Constant dbManager. */
    // DataBaseController
    public static final LocalDBManager dbManager = new LocalDBManager("user");

    /** The speech reader. */
    // SpeechReader
    public static RemoteAppsController speechReader;// = new
						    // RemoteAppsController()

    /** The capture window. */
    // public static CaptureWindow captureWindow

    /** The rename window. */
    public static final RenameWindow renameWindow = new RenameWindow();

    /** The star window. */
    public static final StarWindow starWindow = new StarWindow();

    /**
     * This window is being used to export files from the application to the
     * outside world
     */
    public static final ExportWindowController exportWindow = new ExportWindowController();

    /** The window. */
    // window,scene,root
    public static Stage window;

    /** The scene. */
    public static BorderlessScene scene;

    /** The stack pane root. */
    private static final StackPane stackPaneRoot = new StackPane();

    /** The root. */
    public static final BorderPane root = new BorderPane();

    // ------------------------------------------------------

    /** The top bar. */
    public static final TopBar topBar = new TopBar();

    /** The Constant sideBar. */
    public static final SideBar sideBar = new SideBar();

    // ---------------------------------------------------------

    /** The Constant xPlayersList. */
    public static final XPlayersList xPlayersList = new XPlayersList();

    /** The Constant updateScreen. */
    public static final UpdateScreen updateScreen = new UpdateScreen();

    /** The Constant songsContextMenu. */
    public static final MediaContextMenu songsContextMenu = new MediaContextMenu();

    /** The Constant specialChooser. */
    public static final SpecialChooser specialChooser = new SpecialChooser();

    /** The Constant advancedSearch. */
    public static final AdvancedSearch advancedSearch = new AdvancedSearch();

    /** The Constant playedSongs. */
    public static final PlayedSongs playedSongs = new PlayedSongs();

    /** The Constant libraryMode. */
    // Modes
    public static final LibraryMode libraryMode = new LibraryMode();

    /** The Constant djMode. */
    public static final DJMode djMode = new DJMode();

    /** The Constant multipleTabs. */
    public static final MultipleTabs multipleTabs = new MultipleTabs();

    /** The Constant stationsInfostructure. */
    // public static final RadioStationsController stationsInfostructure = new
    // RadioStationsController()

    /** The Constant treeManager. */
    public static final TreeViewManager treeManager = new TreeViewManager();

    /** The can save data. */
    public static boolean canSaveData = true;

    // ------------Updates Sector------------
    /**
     * The current update of XR3Player
     */
    public final static int currentVersion = 41;

    /**
     * The Thread which is responsible for the update check
     */
    private static Thread updaterThread;

    // ---------------------------------------

    @Override
    public void start(Stage primaryStage) throws Exception {

	try {
	    logger.info("XR3Player Application Started");
	    ActionTool.initInternalJavaFXElements();

	    // rootStack
	    stackPaneRoot.getChildren().addAll(root, sideBar, updateScreen);
	    StackPane.setAlignment(sideBar, Pos.CENTER_LEFT);

	    // root
	    root.setTop(topBar);

	    // Window
	    window = primaryStage;
	    starWindow.window.initOwner(window);
	    renameWindow.window.initOwner(window);
	    exportWindow.window.initOwner(window);
	    topBar.addXR3LabelBinding();

	    // captureWindow
	    // captureWindow = new
	    // CaptureWindow(InfoTool.getScreenWidth(),InfoTool.getScreenHeight(),
	    // window)

	    window.setTitle("XR3Player V." + currentVersion);
	    // -------------------
	    // -------Due to a bug i need the width%2==0---------
	    int width = (int) (InfoTool.getVisualScreenWidth() * 0.77);
	    width = (width % 2 == 0) ? width : width + 1;
	    // -------------------
	    Main.window.setWidth(width);
	    Main.window.setHeight(InfoTool.getVisualScreenHeight() * 0.91);
	    Main.window.centerOnScreen();
	    // window.setWidth(450)
	    // window.setHeight(253)
	    window.getIcons().add(InfoTool.getImageFromDocuments("icon.png"));
	    window.centerOnScreen();
	    window.setOnCloseRequest(exit -> {
		exitQuestion();
		exit.consume();
	    });

	    // Root
	    root.setStyle(
		    "-fx-background-color:BLACK; -fx-background-color:rgb(0,0,0,0.9); -fx-background-image:url('/image/libraryModeBackground.jpg'); -fx-background-size:cover;");
	    // root.setBottom(navigationBar)

	    // Scene
	    scene = new BorderlessScene(window, StageStyle.TRANSPARENT, stackPaneRoot, 650, 500);
	    scene.setMoveControl(topBar);
	    scene.getStylesheets()
		    .add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());

	    // Register some listeners to the main window
	    libraryMode.librariesSearcher.registerListeners();

	    // Load the database
	    dbManager.loadApplicationDataBase();

	    // Scene and Show
	    window.setScene(scene);
	    window.show();

	    // throw new Exception("xd")
	    // ScenicView.show(scene);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    ActionTool.showNotification("Fatal Error", "Fatal Error happened trying to run the application... :(",
		    Duration.millis(10000), NotificationType.ERROR);
	}

    }

    @Override
    public void init() {
	System.out.println("Hello from init");
    }

    /**
     * Terminate the application.
     *
     * @param vacuum
     *            the vacuum
     */
    public static void terminate(boolean vacuum) {

	if (libraryMode.multipleLibs.isFree(true)) {

	    // Stop SpeechReader
	    if (speechReader != null)
		speechReader.stopSpeechRec(true);

	    // vacuum?
	    if (vacuum) {
		VacuumProgress vService = new VacuumProgress();
		updateScreen.label.textProperty().bind(vService.messageProperty());
		updateScreen.progressBar.setProgress(-1);
		updateScreen.progressBar.progressProperty().bind(vService.progressProperty());
		updateScreen.setVisible(true);

		vService.start(new File(InfoTool.dbPath_With_Separator + "user" + File.separator + "dbFile.db"),
			new File(InfoTool.dbPath_With_Separator + "user" + File.separator + "dbFile.db-journal"));

		// Go
		new Thread(() -> {
		    try {
			// close + open connection
			dbManager.commit();
			dbManager.shutdownCommitExecutor();
			dbManager.manageConnection(Operation.CLOSE);
			dbManager.manageConnection(Operation.OPEN);

			// vacuum
			dbManager.connection1.createStatement().executeUpdate("VACUUM");

			// close connection
			dbManager.manageConnection(Operation.OPEN);

			// exit
			System.exit(0);
		    } catch (SQLException ex) {
			logger.log(Level.WARNING, "", ex);
		    }
		}).start();
	    } else
		System.exit(0);
	}

    }

    /**
     * Exit App Confirmation.
     */
    public static void exitQuestion() {
	Alert alert = new Alert(AlertType.CONFIRMATION);
	alert.initStyle(StageStyle.UTILITY);
	alert.initOwner(window);

	alert.setContentText(
		"Doing Vacuum you clear all the junks on the dataBase so:\n1)You release memory \n2)The application is going faster\nBut:\nThe bigger the dataBase the more time it will take!");
	ButtonType justExit = new ButtonType("Exit");
	ButtonType vAndExit = new ButtonType("Vacuum + Exit");
	ButtonType cancel = new ButtonType("Cancel");

	// alert.getDialogPane()
	// .getScene()
	// .setFill(Color.TRANSPARENT);
	// ((Button) alert.getDialogPane()
	// .lookupButton(ButtonType.CANCEL)).setDefaultButton(true);
	alert.getButtonTypes().setAll(cancel, justExit, vAndExit);
	// alert.getDialogPane()
	// .getStylesheets()
	// .add(Main.class.getResource(InfoTool.styLes +
	// InfoTool.applicationCss)
	// .toExternalForm());

	alert.showAndWait().ifPresent(answer -> {
	    if (answer == vAndExit)
		terminate(true);
	    else if (answer == justExit)
		terminate(false);
	});
    }

    /**
     * Calling this method restarts the application
     * 
     * @param askUser
     *            Ask the User if he/she wants to restart the application
     */
    public static void restartTheApplication(boolean askUser) {

	// Restart XR3Player
	new Thread(() -> {
	    String path = InfoTool.getBasePathForClass(Main.class);
	    String applicationPath = new File(path + "XR3Player.jar").getAbsolutePath();

	    Platform.runLater(Notifications.create().title("Processing")
		    .text("Restarting XR3Player....\n Current directory path is:[ " + applicationPath
			    + " ] \n If this takes more than 20 seconds either the computer is slow or it has failed....")
		    .hideAfter(Duration.seconds(25))::show);

	    try {

		System.out.println("XR3PlayerPath is : " + applicationPath);

		ProcessBuilder builder = new ProcessBuilder("java", "-jar", applicationPath);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		// Wait 20 seconds
		PauseTransition pause = new PauseTransition(Duration.seconds(20));
		pause.setOnFinished(f -> {

		    // Show failed message
		    Platform.runLater(Notifications.create().title("Failed to restart!")
			    .text("Failed to restart XR3Player!\nBuilder Directory:" + applicationPath
				    + "\nTrying to start:" + path + "XR3Player.jar\nTry to do it manually...")
			    .hideAfter(Duration.seconds(10))::showError);

		    // Ask the user
		    if (askUser) {
			Platform.runLater(() -> {
			    Alert alert = new Alert(AlertType.CONFIRMATION);
			    alert.getDialogPane().getScene().setFill(Color.TRANSPARENT);

			    alert.setContentText("Restart failed.... force shutdown?");
			    ButtonType yes = new ButtonType("Yes");
			    ButtonType cancel = new ButtonType("Cancel");
			    ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);

			    alert.getButtonTypes().setAll(yes, cancel);
			    alert.initStyle(StageStyle.TRANSPARENT);
			    alert.showAndWait().ifPresent(answer -> {
				if (answer == yes)
				    terminate(false);
			    });
			});
		    } else {
			// Terminate after showing the message for a while
			PauseTransition forceTerminate = new PauseTransition(Duration.seconds(2));
			forceTerminate.setOnFinished(fn -> terminate(false));
			forceTerminate.play();
		    }

		});
		pause.play();

		// Continuously Read Output
		String line;
		while (process.isAlive())
		    while ((line = bufferedReader.readLine()) != null) {
			if (line.isEmpty())
			    break;
			if (line.contains("XR3Player Application Started"))
			    terminate(false);
		    }

		// process.waitFor()
		// i(process.exitValue() != 0)
		// else
		// Main.terminate(false)

	    } catch (IOException ex) {
		Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
		Platform.runLater(() -> {
		    Main.updateScreen.setVisible(false);

		    // Show failed message
		    Platform.runLater(Notifications.create().title("Error")
			    .text("Failed to restart XR3Player!\nBuilder Directory:" + path + "\nTrying to start:"
				    + path + "XR3ImageViewer.jar\nTry to do it manually...")
			    .hideAfter(Duration.millis(2000))::showError);
		});
	    }
	}).start();
    }

    private static int counter;

    /**
     * This method is fetching data from github to check if the is a new update
     * for XR3Player
     * 
     * @param showIfNotUpdateAvailable
     */
    public static void checkForUpdates(boolean showIfNotUpdateAvailable) {

	// Not already running
	if (updaterThread == null || !updaterThread.isAlive()) {
	    updaterThread = new Thread(() -> {
		System.out.println("Started update Thread");

		if (InfoTool.isReachableByPing("www.google.com")) {

		    try {

			Document doc = Jsoup.connect(
				"https://raw.githubusercontent.com/goxr3plus/XR3Player/master/XR3PlayerUpdatePage.html")
				.get();

			// Document doc = Jsoup.parse(new
			// File("XR3PlayerUpdatePage.html"), "UTF-8",
			// "http://example.com/");
			
			Element lastArticle = doc.getElementsByTag("article").last();

			// Not disturb the user every time the application
			// starts
			if (Integer.valueOf(lastArticle.id()) == currentVersion && !showIfNotUpdateAvailable)
			    return;

			// Update is available or not?
			Platform.runLater(() -> {
			    Alert alert = new Alert(AlertType.CONFIRMATION);
			    alert.setTitle("Update Window");
			    if (Integer.valueOf(lastArticle.id()) > currentVersion) {
				alert.setHeaderText("New Update available!!!");
				alert.setContentText("Update ->( " + lastArticle.id()
					+ " )<- is available!\n\t\t\t\t\tYour current version is: ->( " + currentVersion
					+ " )<-");
			    } else {
				alert.setHeaderText("You are up too date :)");
				alert.setContentText("Your current version is: ->( " + currentVersion + " )<-");
			    }
			    alert.initStyle(StageStyle.UTILITY);
			    alert.initOwner(Main.window);

			    // Label label = new Label("Information about the
			    // latest update :)")

			    InlineCssTextArea textArea = new InlineCssTextArea();
			    textArea.setEditable(false);
			    textArea.setFocusTraversable(false);
			    // textArea.setWrapText(true)

			    textArea.setMinSize(500, 200);
			    textArea.setMaxWidth(Double.MAX_VALUE);
			    textArea.setMaxHeight(Double.MAX_VALUE);
			    GridPane.setVgrow(textArea, Priority.ALWAYS);
			    GridPane.setHgrow(textArea, Priority.ALWAYS);

			    GridPane expContent = new GridPane();
			    expContent.setMaxWidth(Double.MAX_VALUE);
			    expContent.setMaxHeight(Double.MAX_VALUE);
			    // expContent.add(label, 0, 0)
			    expContent.add(textArea, 0, 1);

			    doc.getElementsByTag("article").forEach(element -> {
				String id = element.id();

				// Append the text to the textArea
				textArea.appendText("-------------Start of Update (" + id
					+ ")----------------------------------------------------------------------------------------------------\n");

				// Information
				textArea.appendText("->Information: ");
				textArea.appendText(element.getElementsByClass("about").text() + "\n");

				// ChangeLog
				textArea.appendText("->ChangeLog:\n");
				textArea.setStyle(textArea.getText().length() - 13, textArea.getText().length(),
					"-fx-font-weight:bold; -fx-font-size:14; -fx-fill:black;");

				counter = -1;
				Arrays.asList(element.getElementsByClass("changelog").text().split("\\*"))
					.forEach(el -> {
					    if (++counter >= 1)
						textArea.appendText((counter) + ")" + el + "\n");
					});

				textArea.appendText("\n\n");

			    });

			    textArea.moveTo(20);

			    // Set the default buttons
			    ButtonType download = new ButtonType("Download");
			    ButtonType cancel = new ButtonType("Cancel");
			    alert.getButtonTypes().setAll(download, cancel);

			    // Set expandable Exception into the dialog pane.
			    alert.getDialogPane().setExpandableContent(expContent);
			    alert.getDialogPane().setExpanded(true);
			    alert.getDialogPane().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

			    textArea.requestFocus();
			    // Show and Wait
			    alert.showAndWait().ifPresent(answer -> {
				if (answer == download) {
				    // Open the Default Browser
				    if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
					    desktop.browse(new URI("https://sourceforge.net/projects/xr3player/"));
					} catch (URISyntaxException | IOException ex) {
					    Platform.runLater(() -> ActionTool.showNotification("Problem Occured",
						    "Can't open default web browser at:\n[ https://sourceforge.net/projects/xr3player/ ]",
						    Duration.millis(2500), NotificationType.INFORMATION));
					    ex.printStackTrace();
					}
					// Error?
				    } else {
					System.out.println("Error trying to open the default web browser.");
				    }
				}
			    });

			});
		    } catch (IOException ex) {
			Platform.runLater(() -> ActionTool.showNotification("Problem Occured",
				"Trying to fetch update information i had a problem.", Duration.millis(2500),
				NotificationType.WARNING));
			ex.printStackTrace();
		    }

		} else {
		    Platform.runLater(() -> ActionTool.showNotification("Can't Connect",
			    "Can't connect to the update site :\n" + "1) Maybe there is not internet connection"
				    + "\n2)GitHub is down for maintenance",
			    Duration.millis(2500), NotificationType.ERROR));
		}

	    });

	    updaterThread.setDaemon(true);
	    updaterThread.start();
	}
    }

    /**
     * After Export or Import DataBase the Layout must be redefined again based
     * on mode.
     */
    public static void fixLayout() {
	updateScreen.setVisible(false);
    }

    /**
     * This method is hiding all the PopUp Windows of the application.
     */
    public static void hideAllPopUpWindows() {
	advancedSearch.hide();
	// navigationBar.getExtraControls().hide()
    }

    /**
     * Main Method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
}
