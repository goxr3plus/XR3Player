/*
 * 
 */
package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXTabPane;

import application.borderless.BorderlessScene;
import application.database.DbManager;
import application.database.PropertiesDb;
import application.djmode.DJMode;
import application.librarymode.LibraryMode;
import application.loginmode.LoginMode;
import application.loginmode.User;
import application.loginmode.UserMode;
import application.presenter.BottomBar;
import application.presenter.EmotionsTabPane;
import application.presenter.PlayListModesSplitPane;
import application.presenter.PlayListModesTabPane;
import application.presenter.SideBar;
import application.presenter.TopBar;
import application.presenter.UpdateScreen;
import application.presenter.treeview.TreeViewManager;
import application.services.VacuumProgressService;
import application.settings.ApplicationSettingsController;
import application.speciallists.EmotionListsController;
import application.speciallists.PlayedMediaList;
import application.tools.ActionTool;
import application.tools.ActionTool.FileType;
import application.tools.InfoTool;
import application.tools.JavaFXTools;
import application.tools.NotificationType;
import application.versionupdate.UpdateWindow;
import application.webbrowser.WebBrowserController;
import application.windows.ApplicationInformationWindow;
import application.windows.ConsoleWindowController;
import application.windows.EmotionsWindow;
import application.windows.ExportWindowController;
import application.windows.FileAndFolderChooser;
import application.windows.RenameWindow;
import application.windows.StarWindow;
import application.windows.WelcomeScreen;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import smartcontroller.SmartControllerSearcher.AdvancedSearch;
import smartcontroller.media.MediaContextMenu;
import smartcontroller.media.MediaDeleteWindow;
import smartcontroller.media.Information.MediaInformation;
import smartcontroller.services.MediaFilterService;
import xplayer.XPlayersList;
import xplayer.services.XPlayersFilterService;
import xplayer.visualizer.view.VisualizerWindowController.Type;
import xr3capture.CaptureWindow;

/**
 * The Main class from which the application is starting.
 *
 * @author GOXR3PLUS
 */
public class Main extends Application {
	
	public static Properties internalInformation = new Properties();
	static {
		//----------Properties-------------
		internalInformation.put("Version", 82);
		internalInformation.put("ReleasedDate", "08/09/2017");
		
		System.out.println("Outside of Application Start Method");
	}
	
	/**
	 * Holds global application properties
	 */
	public static PropertiesDb applicationProperties = new PropertiesDb(InfoTool.getAbsoluteDatabasePathWithSeparator() + "ApplicationProperties.properties", true);
	
	/** Application logger. */
	public static final Logger logger = Logger.getGlobal();
	
	//----------------START: The below have not depencities on other ---------------------------------//
	
	public static final WelcomeScreen welcomeScreen = new WelcomeScreen();
	
	public static final MediaDeleteWindow mediaDeleteWindow = new MediaDeleteWindow();
	
	/** The star window. */
	public static final StarWindow starWindow = new StarWindow();
	
	/** The rename window. */
	public static final RenameWindow renameWindow = new RenameWindow();
	
	/** The rename window. */
	public static final EmotionsWindow emotionsWindow = new EmotionsWindow();
	
	/**
	 * This window is being used to export files from the application to the outside world
	 */
	public static final ExportWindowController exportWindow = new ExportWindowController();
	
	/** The About Window of the Application */
	public static final ApplicationInformationWindow aboutWindow = new ApplicationInformationWindow();
	
	/** The console Window of the Application */
	public static final ConsoleWindowController consoleWindow = new ConsoleWindowController();
	
	/**
	 * This Window contains the settings for the whole application
	 */
	public static ApplicationSettingsController settingsWindow = new ApplicationSettingsController();
	
	/**
	 * This class is used to capture the computer Screen or a part of it [ Check XR3Capture package]
	 */
	public static CaptureWindow captureWindow = new CaptureWindow();
	
	public static UpdateWindow updateWindow = new UpdateWindow();
	
	//
	
	/** The Top Bar of the Application */
	public static final TopBar topBar = new TopBar();
	
	/** The Bottom Bar of the Application */
	public static final BottomBar bottomBar = new BottomBar();
	
	/** The Side Bar of The Application */
	public static final SideBar sideBar = new SideBar();
	
	/** Application Update Screen */
	public static final UpdateScreen updateScreen = new UpdateScreen();
	
	/** The TreeView of DJMode */
	public static final TreeViewManager treeManager = new TreeViewManager();
	
	/** The Constant advancedSearch. */
	public static final AdvancedSearch advancedSearch = new AdvancedSearch();
	
	public static final MediaInformation mediaInformation = new MediaInformation();
	//
	
	/** The Constant songsContextMenu. */
	public static final MediaContextMenu songsContextMenu = new MediaContextMenu();
	
	/** The Constant specialChooser. */
	public static final FileAndFolderChooser specialChooser = new FileAndFolderChooser();
	
	//
	
	/** XPlayList holds the instances of XPlayerControllers */
	public static final XPlayersList xPlayersList = new XPlayersList();
	
	/** The Constant playedSongs. */
	public static final PlayedMediaList playedSongs = new PlayedMediaList();
	
	/** The Constant EmotionListsController. */
	public static final EmotionListsController emotionListsController = new EmotionListsController();
	
	//
	
	/**
	 * The WebBrowser of the Application
	 */
	public static WebBrowserController webBrowser = new WebBrowserController();;
	
	//----------------END: The above have not depencities on other ---------------------------------//
	
	//----------------START: Vary basic for the application---------------------------------------//
	
	/** The window. */
	public static Stage window;
	
	/** The scene. */
	public static BorderlessScene scene;
	
	/** The stack pane root. */
	public static final StackPane applicationStackPane = new StackPane();
	
	/** The root. */
	public static final BorderPane root = new BorderPane();
	
	/** The can save data. */
	public static boolean canSaveData = true;
	
	//---------------END:Vary basic for the application---------------------------------//
	
	// --------------START: The below have depencities on others------------------------
	
	/** The Constant dbManager. */
	public static DbManager dbManager = new DbManager();
	
	/** The Constant libraryMode. */
	public static LibraryMode libraryMode = new LibraryMode();
	
	/** The Constant djMode. */
	public static DJMode djMode = new DJMode();
	
	public static EmotionsTabPane emotionsTabPane = new EmotionsTabPane(emotionListsController);
	
	/** The Search Window Smart Controller of the application */
	public static SmartController searchWindowSmartController = new SmartController(Genre.SEARCHWINDOW, "Searching any Media", null);
	
	public static PlayListModesTabPane playListModesTabPane = new PlayListModesTabPane();
	
	/** The Constant multipleTabs. */
	public static PlayListModesSplitPane playListModesSplitPane = new PlayListModesSplitPane();
	
	/**
	 * The Login Mode where the user of the applications has to choose an account to login
	 */
	public static LoginMode loginMode = new LoginMode();
	
	/**
	 * Entering in this mode you can change the user settings and other things that have to do with the user....
	 */
	public static UserMode userMode = new UserMode();
	
	/***
	 * This BorderPane has in the center the root , at the left the SideBar and on the Top the TopBar
	 */
	// private static BorderPane applicationBorderPane = new BorderPane();
	
	public static JFXTabPane specialJFXTabPane = new JFXTabPane();
	
	// --------------END: The below have depencities on others------------------------
	
	@Override
	public void start(Stage primaryStage) {
		System.out.println("XR3Player Application Started");
		Platform.setImplicitExit(false);
		
		// --------Window---------
		window = primaryStage;
		window.setTitle("XR3Player V." + internalInformation.get("Version"));
		double width = InfoTool.getVisualScreenWidth();
		double height = InfoTool.getVisualScreenHeight();
		//width = 1380;
		//height = 800;
		window.setWidth(width * 0.95);
		window.setHeight(height * 0.95);
		window.centerOnScreen();
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		window.centerOnScreen();
		window.setOnCloseRequest(exit -> {
			confirmApplicationExit();
			exit.consume();
		});
		
		// Create BorderlessScene 
		final int screenMinWidth = 800 , screenMinHeight = 600;
		scene = new BorderlessScene(window, StageStyle.UNDECORATED, applicationStackPane, screenMinWidth, screenMinHeight);
		scene.setMoveControl(loginMode.getXr3PlayerLabel());
		scene.getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		window.setScene(scene);
		window.show();
		window.close();
		
		//Continue
		startPart2();
		
		//Show the Window
		window.show();
		
		//---Login Mode---- It must be set after the window has been shown
		loginMode.getSplitPane().setDividerPositions(0.65, 0.35);
		
		//Load the informations about every user
		loginMode.usersInfoLoader.start();
		
		//Check Compatibility
		checkJavaCombatibility();
		
		//Check for updates
		updateWindow.searchForUpdates(false);
		
		//Delete AutoUpdate if it exists
		ActionTool.deleteFile(new File(InfoTool.getBasePathForClass(Main.class) + "XR3PlayerUpdater.jar"));
		
		//Show Welcome Screen?	
		Properties properties = applicationProperties.loadProperties();
		if (properties.getProperty("Show-Welcome-Screen") == null)
			welcomeScreen.show();
		else
			Optional.ofNullable(properties.getProperty("Show-Welcome-Screen")).ifPresent(value -> {
				welcomeScreen.getShowOnStartUp().setSelected(Boolean.valueOf(value));
				if (welcomeScreen.getShowOnStartUp().isSelected())
					welcomeScreen.show();
				else
					welcomeScreen.close();
			});
		applicationProperties.setUpdatePropertiesLocked(false);
		
		//------------------Experiments------------------
		// ScenicView.show(scene);
		// root.setStyle("-fx-background-color:rgb(0,0,0,0.9); -fx-background-size:100% 100%; -fx-background-image:url('/image/background.jpg'); -fx-background-position: center center; -fx-background-repeat:stretch;");
		
	}
	
	private void startPart2() {
		
		// ---- InitOwners -------
		starWindow.getWindow().initOwner(window);
		renameWindow.getWindow().initOwner(window);
		exportWindow.getWindow().initOwner(window);
		consoleWindow.getWindow().initOwner(window);
		settingsWindow.getWindow().initOwner(window);
		aboutWindow.getWindow().initOwner(window);
		//searchWindow.getWindow().initOwner(window);
		updateWindow.getWindow().initOwner(window);
		welcomeScreen.getWindow().initOwner(window);
		
		// --------- Fix the Background ------------
		determineBackgroundImage();
		
		// ---------LoginMode ------------
		loginMode.getXr3PlayerLabel().setText("~" + window.getTitle() + "~");
		loginMode.userSearchBox.registerListeners(window);
		loginMode.setLeft(sideBar);
		
		// -------Root-----------
		root.setVisible(false);
		topBar.addXR3LabelBinding();
		root.setTop(topBar);
		root.setBottom(bottomBar);
		
		// ----Create the SpecialJFXTabPane for Navigation between Modes
		specialJFXTabPane.getTabs().add(new Tab("tab1", libraryMode));
		specialJFXTabPane.getTabs().add(new Tab("tab2", djMode));
		specialJFXTabPane.getTabs().add(new Tab("tab3", userMode));
		specialJFXTabPane.getTabs().add(new Tab("tab4", webBrowser));
		specialJFXTabPane.setTabMaxWidth(0);
		specialJFXTabPane.setTabMaxHeight(0);
		
		//Add listeners to each tab
		final AtomicInteger counter = new AtomicInteger(-1);
		specialJFXTabPane.getTabs().forEach(tab -> {
			final int index = counter.addAndGet(1);
			tab.selectedProperty().addListener((observable , oldValue , newValue) -> {
				if (specialJFXTabPane.getTabs().get(index).isSelected() && !topBar.isTabSelected(index))
					topBar.selectTab(index);
				//System.out.println("Entered Tab " + index) //this is inside curly braces with the above if
				
			});
		});
		root.setCenter(specialJFXTabPane);
		
		//---------LibraryMode ------------			
		libraryMode.librariesContextMenu.show(window, 0, 0);
		libraryMode.librariesContextMenu.hide();
		
		//Remove this to be soore...
		libraryMode.getTopSplitPane().getItems().remove(libraryMode.getBorderPane());
		
		libraryMode.getTopSplitPane().getItems().add(playListModesSplitPane);
		libraryMode.getBottomSplitPane().getItems().add(libraryMode.getBorderPane());
		libraryMode.getBottomSplitPane().getItems().add(xPlayersList.getXPlayerController(0));
		
		libraryMode.multipleLibs.getEmptyLabel().textProperty().bind(Bindings.when(libraryMode.teamViewer.getViewer().itemsWrapperProperty().emptyProperty())
				.then("Click here to create a library...").otherwise("Click here to open the first available library..."));
		libraryMode.librariesSearcher.registerListeners(window);
		
		//----------ApplicationStackPane---------
		applicationStackPane.getChildren().addAll(root, loginMode, updateScreen);
		
		//----------Load Application Users-------
		loadTheUsers();
		
		//----------Bottom Bar----------------
		bottomBar.getKeyBindings().selectedProperty().bindBidirectional(settingsWindow.getNativeKeyBindings().getKeyBindingsActive().selectedProperty());
		
		//-------------TOP BAR--------------------
		topBar.getSearchField().textProperty().bindBidirectional(searchWindowSmartController.getSearchService().getSearchField().textProperty());
		topBar.getSearchField().disableProperty().bind(searchWindowSmartController.getRegion().visibleProperty());
		
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	
	/**
	 * This part is actually loading the application users
	 */
	private void loadTheUsers() {
		
		//Set Update Screen Visible
		updateScreen.setVisible(true);
		
		//Create Database folder if not exists
		if (!ActionTool.createFileOrFolder(InfoTool.getAbsoluteDatabasePathPlain(), FileType.DIRECTORY)) {
			//			ActionTool.showNotification("Please exit the application and change it's installation directory!",
			//					"Fatal Error Occured trying to create \n the root database folder [ XR3DataBase] \n Maybe the application has not the permission to create this folder.",
			//					Duration.seconds(45), NotificationType.ERROR);
			System.out.println("Failed to create database folder[lack of permissions],please change installation directory");
			System.exit(-1);
		} else {
			
			//Create the List with the Available Users
			AtomicInteger counter = new AtomicInteger();
			try {
				loginMode.teamViewer.addMultipleUsers(Files.walk(Paths.get(InfoTool.getAbsoluteDatabasePathPlain()), 1)
						.filter(path -> path.toFile().isDirectory() && ! ( path + "" ).equals(InfoTool.getAbsoluteDatabasePathPlain()))
						.map(path -> new User(path.getFileName() + "", counter.getAndAdd(1), loginMode)).collect(Collectors.toList()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//avoid error
			if (!loginMode.teamViewer.getItemsObservableList().isEmpty())
				loginMode.teamViewer.setCenterIndex(loginMode.teamViewer.getItemsObservableList().size() / 2);
			
			//----------------------The 5-10 below lines are being used only to support updates below <72 where the `config.properties` file has been on DataBase Root folder[XR3DataBase]------------------------------
			
			//So copy the config.properties from the DataBase Root folder to the settings folder of each user
			File configFile = new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + InfoTool.USER_SETTINGS_FILE_NAME);
			try {
				if (configFile.exists())
					Files.walk(Paths.get(InfoTool.getAbsoluteDatabasePathPlain()), 1)
							.filter(path -> path.toFile().isDirectory() && ! ( path + "" ).equals(InfoTool.getAbsoluteDatabasePathPlain())).forEach(path -> {
								
								//Create settings folder if it doesn't exist
								File settingsFolder = new File(path + File.separator + "settings");
								if (!settingsFolder.exists())
									settingsFolder.mkdir();
								
								//Now copy the it bro!
								ActionTool.copy(configFile.getAbsolutePath(), settingsFolder.getAbsolutePath() + File.separator + InfoTool.USER_SETTINGS_FILE_NAME);
							});
				configFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		//Create Original xr3database signature file	
		ActionTool.createFileOrFolder(InfoTool.getDatabaseSignatureFile().getAbsolutePath(), FileType.FILE);
		
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	
	/**
	 * Starts the application for this specific user
	 * 
	 * @param selectedUser
	 *            The user selected to be logged in the application
	 */
	public static void startAppWithUser(User selectedUser) {
		
		//Close the LoginMode
		loginMode.userSearchBox.getSearchBoxWindow().close();
		loginMode.setVisible(false);
		updateScreen.getProgressBar().setProgress(-1);
		updateScreen.getLabel().setText("--Starting--");
		updateScreen.setVisible(true);
		
		//SideBar
		root.setLeft(sideBar);
		sideBar.prepareForLoginMode(false);
		
		//Top Bar is the new Move Controls
		scene.setMoveControl(topBar.getXr3Label());
		
		//Set root visible
		root.setVisible(true);
		
		//Do a pause so the login mode disappears
		PauseTransition pause = new PauseTransition(Duration.millis(500));
		pause.setOnFinished(f -> {
			
			//Create this in a Thread
			Thread s = new Thread(() -> dbManager.initialize(selectedUser.getUserName()));
			s.start();
			
			//Do the below until the database is initialized
			userMode.setUser(selectedUser);
			
			try {
				s.join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			
			//Load the DataBase - After the DBManager has been initialized of course ;)
			dbManager.loadApplicationDataBase();
			
			//--------- Create the Menu Items of available users for Settings Window
			loginMode.teamViewer.getItemsObservableList().stream().filter(userr -> !userr.getUserName().equals(selectedUser.getUserName())).forEach(userr -> {
				
				//Create the MenuItem
				MenuItem menuItem = new MenuItem(InfoTool.getMinString(userr.getUserName(), 50));
				
				//Set Image
				ImageView imageView = new ImageView(userr.getImageView().getImage());
				imageView.setFitWidth(24);
				imageView.setFitHeight(24);
				menuItem.setGraphic(imageView);
				
				//Set Action
				menuItem.setOnAction(a -> {
					
					//Ask the user
					if (ActionTool.doQuestion("Soore you want to ovveride your current user settings with the one that you selected from the menu ?",
							settingsWindow.getCopySettingsMenuButton(), window))
						
						//Don't block the application due to IO Operations
						new Thread(() -> {
							
							//Delete the current settings from the User
							ActionTool.deleteFile(new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + selectedUser.getUserName() + File.separator + "settings"
									+ File.separator + InfoTool.USER_SETTINGS_FILE_NAME));
							
							//Transfer the settings from the other user
							ActionTool.copy(
									InfoTool.getAbsoluteDatabasePathWithSeparator() + userr.getUserName() + File.separator + "settings" + File.separator
											+ InfoTool.USER_SETTINGS_FILE_NAME,
									InfoTool.getAbsoluteDatabasePathWithSeparator() + selectedUser.getUserName() + File.separator + "settings" + File.separator
											+ InfoTool.USER_SETTINGS_FILE_NAME);
							
							//Reload the application settings now...							
							Platform.runLater(Main::loadApplicationSettings);
						}).start();
					
				});
				
				//Disable if user has no settings defined
				if (!new File(
						InfoTool.getAbsoluteDatabasePathWithSeparator() + userr.getUserName() + File.separator + "settings" + File.separator + InfoTool.USER_SETTINGS_FILE_NAME)
								.exists())
					menuItem.setDisable(true);
				
				//Finally add the Menu Item
				settingsWindow.getCopySettingsMenuButton().getItems().add(menuItem);
			});
			
			//Load the application settings
			loadApplicationSettings();
			
			//----Update the UserInformation properties file when the total libraries change
			libraryMode.teamViewer.getViewer().itemsWrapperProperty().sizeProperty()
					.addListener((observable , oldValue , newValue) -> selectedUser.getUserInformationDb().updateProperty("Total-Libraries", String.valueOf(newValue.intValue())));
			
			//Filter Thread (Inspecting the Files if existing)
			new MediaFilterService().start();
			new XPlayersFilterService().start();
			
			//---------------END:Important Work-----------------------------------------------------------
			
			//This bitch doesn't work for some reason that i will find and smash his bitchy ass 
			topBar.getSearchField().setOnAction(a -> searchWindowSmartController.getSearchService().getSearchField().getOnAction());
		});
		pause.playFromStart();
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	
	/**
	 * Checks if the Current Java Version is the appropriate for the application [[SuppressWarningsSpartan]]
	 */
	private void checkJavaCombatibility() {
		
		//String minimumJavaVersion = "1.9.0_121";
		String[] javaVersionElements = System.getProperty("java.runtime.version").split("\\.|_|-b");
		
		//String discard = javaVersionElements[0]
		String major = javaVersionElements[1];
		//String minor = javaVersionElements[2]
		String update = javaVersionElements[3];
		//String build = javaVersionElements[4]
		//System.out.println(Arrays.asList(javaVersionElements));
		
		if (Integer.parseInt(major) < 8 || ( Integer.parseInt(major) < 8 && Integer.parseInt(update) < 121 ))
			ActionTool.showNotification("Problematic Java Version Installed!", "XR3Player needs at least Java [ 1.8.0_121 ] -> Installed Java Version [ "
					+ System.getProperty("java.version") + " ]\nThe application may crash or not work at all!\n Update your Java Version :)", Duration.seconds(40),
					NotificationType.ERROR);
	}
	
	/**
	 * Terminate the application.
	 *
	 * @param vacuum
	 *            the vacuum
	 */
	private static void terminate(boolean vacuum) {
		
		//I need to check it in case no user is logged in 
		if (dbManager == null)
			System.exit(0);
		else if (libraryMode.multipleLibs.isFree(true)) {
			if (!vacuum)
				System.exit(0);
			else {
				VacuumProgressService vService = new VacuumProgressService();
				updateScreen.getLabel().textProperty().bind(vService.messageProperty());
				updateScreen.getProgressBar().setProgress(-1);
				updateScreen.getProgressBar().progressProperty().bind(vService.progressProperty());
				updateScreen.setVisible(true);
				vService.start(new File(InfoTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db"),
						new File(InfoTool.getUserFolderAbsolutePathWithSeparator() + "dbFile.db-journal"));
				dbManager.commitAndVacuum();
			}
		}
		
	}
	
	/**
	 * This method is used to exit the application
	 */
	public static void confirmApplicationExit() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.initOwner(window);
		alert.setTitle("Terminate the application?");
		
		alert.setHeaderText("Vacuum is clearing junks from database\n(In future updates it will be automatical)");
		alert.setContentText("Pros:\nThe database file may be shrinked \n\nCons:\nIt may take some seconds to be done\n");
		ButtonType exit = new ButtonType("Exit", ButtonData.OK_DONE);
		ButtonType vacuum = new ButtonType("Vacuum + Exit", ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		( (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL) ).setDefaultButton(true);
		alert.getButtonTypes().setAll(vacuum, exit, cancel);
		
		//Pick the answer
		alert.showAndWait().ifPresent(answer -> {
			if (answer == exit)
				terminate(false);
			else if (answer == vacuum)
				terminate(true);
			
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
			String[] applicationPath = { new File(path + "XR3Player.jar").getAbsolutePath() };
			
			//Show message that application is restarting
			Platform.runLater(() -> ActionTool.showNotification("Restarting Application",
					"Application Path:[ " + applicationPath[0] + " ]\n\tIf this takes more than 20 seconds either the computer is slow or it has failed....", Duration.seconds(25),
					NotificationType.INFORMATION));
			
			try {
				
				System.out.println("XR3PlayerPath is : " + applicationPath[0]);
				
				ProcessBuilder builder = new ProcessBuilder("java", "-jar", applicationPath[0]);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
				// Wait 20 seconds
				PauseTransition pause = new PauseTransition(Duration.seconds(20));
				pause.setOnFinished(f -> {
					updateScreen.setVisible(false);
					
					// Show failed message
					Platform.runLater(() -> ActionTool.showNotification("Restart Failed", "\nApplication Path: [ " + applicationPath[0] + " ]\n\tTry to do it manually...",
							Duration.seconds(10), NotificationType.ERROR));
					
					// Ask the user
					if (askUser)
						Platform.runLater(() -> {
							if (ActionTool.doQuestion("Restart failed.... force shutdown?", Main.window))
								terminate(false);
						});
					else {
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
					while ( ( line = bufferedReader.readLine() ) != null) {
						if (line.isEmpty())
							break;
						if (line.contains("XR3Player Application Started"))
							terminate(false);
					}
				
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
				Platform.runLater(() -> {
					updateScreen.setVisible(false);
					
					// Show failed message
					Platform.runLater(() -> ActionTool.showNotification("Restart Failed", "\nApplication Path: [ " + applicationPath[0] + " ]\n\tTry to do it manually...",
							Duration.seconds(10), NotificationType.ERROR));
				});
			}
		}, "Restart Application Thread").start();
	}
	
	/**
	 * Loads all the application settings from the property file
	 */
	public static void loadApplicationSettings() {
		try {
			
			//Start
			//System.out.println("\n\n-----App Settings--------------\n");
			
			//--------------------Now continue normally----------------------------------------------
			
			//Lock the update properties
			dbManager.getPropertiesDb().setUpdatePropertiesLocked(true);
			
			//Load the Properties
			dbManager.getPropertiesDb().getProperties().clear();
			dbManager.getPropertiesDb().loadProperties();
			
			//Get the properties
			Properties settings = dbManager.getPropertiesDb().getProperties();
			
			//Restore all to default before loading the settings
			settingsWindow.restoreAll();
			
			//----------   Load all the settings from the config.properties --------------------
			
			//--KeyBindings-Settings
			Optional.ofNullable(settings.getProperty("ShortCuts-KeyBindings"))
					.ifPresent(s -> settingsWindow.getNativeKeyBindings().getKeyBindingsActive().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("ShortCuts-SelectedPlayer"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getNativeKeyBindings().getxPlayerSelected(), Integer.valueOf(s)));
			
			//--General-Settings-SideBar
			Optional.ofNullable(settings.getProperty("General-SideBarSide"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getSideBarSideGroup(), Integer.valueOf(s)));
			
			//--General-Settings-SideBar
			Optional.ofNullable(settings.getProperty("General-NotificationsPosition"))
					.ifPresent(s -> JavaFXTools.selectToogleWithText(settingsWindow.getGeneralSettingsController().getNotificationsPosition(), s));
			
			//--General-Settings-LibraryMode
			playListModesSplitPane.updateSplitPaneDivider();
			libraryMode.updateTopSplitPaneDivider();
			libraryMode.updateBottomSplitPaneDivider();
			Optional.ofNullable(settings.getProperty("General-LibraryModeUpsideDown"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getLibraryModeUpsideDown(), Integer.valueOf(s)));
			
			//--General-Settings-DJMode
			djMode.updateTopSplitPaneDivider();
			djMode.updateBottomSplitPaneDivider();
			Optional.ofNullable(settings.getProperty("General-DjModeUpsideDown"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getGeneralSettingsController().getDjModeUpsideDown(), Integer.valueOf(s)));
			
			//--Libraries-Settings
			Optional.ofNullable(settings.getProperty("Libraries-ShowWidgets"))
					.ifPresent(s -> settingsWindow.getLibrariesSettingsController().getShowWidgets().setSelected(Boolean.parseBoolean(s)));
			
			//--Playlists-Settings-Search
			Optional.ofNullable(settings.getProperty("PlayLists-Search-InstantSearch"))
					.ifPresent(s -> settingsWindow.getPlayListsSettingsController().getInstantSearch().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("PlayLists-Search-FileSearchUsing"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getPlayListsSettingsController().getFileSearchGroup(), Integer.valueOf(s)));
			
			//--Playlists-Settings-General
			
			Optional.ofNullable(settings.getProperty("PlayLists-General-PlayedFilesDetection"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getPlayListsSettingsController().getPlayedFilesDetectionGroup(), Integer.valueOf(s)));
			
			Optional.ofNullable(settings.getProperty("PlayLists-General-TotalFilesShown"))
					.ifPresent(s -> JavaFXTools.selectToggleOnIndex(settingsWindow.getPlayListsSettingsController().getTotalFilesShownGroup(), Integer.valueOf(s)));
			
			//--XPlayers-Visualizer-Settings
			Optional.ofNullable(settings.getProperty("XPlayers-Visualizer-ShowFPS")).ifPresent(s -> {
				
				//Set the Value to the CheckBox
				settingsWindow.getxPlayersSettingsController().getShowFPS().setSelected(Boolean.parseBoolean(s));
				
				//Update all the players
				xPlayersList.getList().forEach(xPlayerController -> xPlayerController.getVisualizer().setShowFPS(Boolean.parseBoolean(s)));
				
			});
			
			//--XPlayers-General-Settings
			Optional.ofNullable(settings.getProperty("XPlayers-General-StartAtOnce"))
					.ifPresent(s -> settingsWindow.getxPlayersSettingsController().getStartImmediately().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("XPlayers-General-AskSecurityQuestion"))
					.ifPresent(s -> settingsWindow.getxPlayersSettingsController().getAskSecurityQuestion().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("XPlayers-General-ShowPlayerNotifications"))
					.ifPresent(s -> settingsWindow.getxPlayersSettingsController().getShowPlayerNotifications().setSelected(Boolean.parseBoolean(s)));
			
			Optional.ofNullable(settings.getProperty("XPlayers-General-SkipButtonSeconds"))
					.ifPresent(s -> settingsWindow.getxPlayersSettingsController().getSkipSlider().setValue(Integer.parseInt(s)));
			
			//----Determine the Visualizer Images
			Main.xPlayersList.getList().forEach(xPlayerController -> {
				
				//If the key is not there add background image by default			
				if (Optional.ofNullable(settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-BackgroundImageCleared")).isPresent())
					xPlayerController.getVisualizerWindow().clearImage(Type.BACKGROUND);
				else
					xPlayerController.getVisualizerWindow().findAppropriateImage(Type.BACKGROUND);
				
				//Always add foreground image
				xPlayerController.getVisualizerWindow().findAppropriateImage(Type.FOREGROUND);
				
				//Determine the visualizer display mode
				Optional.ofNullable(settings.getProperty("XPlayer" + xPlayerController.getKey() + "-Visualizer-DisplayMode"))
						.ifPresent(s -> xPlayerController.getVisualizer().displayMode.set(Integer.valueOf(s)));
				
			});
			//----------                        --------------------
			
			//Finish
			//System.out.println("\n-----App Settings Finish--------------\n\n");
			
			//Re-enable Properties Updating
			dbManager.getPropertiesDb().setUpdatePropertiesLocked(false);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//SHUT THE FUCK UP BASTARD MOTHER FUCKER CANCER !!!!!!!!!!! WTF !!!!!!!  CANCERED THE CONSOLE CANCER!!!! JAUDIOTAGGER LOGGER
		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
	}
	
	//------------------------------------- Methods not used very often--------------------------------------------------
	
	/**
	 * The user has the ability to change the Library Image
	 * 
	 */
	public static void changeBackgroundImage() {
		
		//Check the response
		JavaFXTools.selectAndSaveImage("background", InfoTool.getAbsoluteDatabasePathPlain(), specialChooser, window).ifPresent(imageFile -> {
			BackgroundImage bgImg = new BackgroundImage(new Image(imageFile.toURI() + ""), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					new BackgroundSize(window.getWidth(), window.getHeight(), true, true, true, true));
			loginMode.setBackground(new Background(bgImg));
			root.setBackground(new Background(bgImg));
		});
		
	}
	
	static boolean backgroundFound;
	
	/**
	 * Determines the background image of the application based on if a custom image exists inside the database .If not then the default image is
	 * being added :)
	 * 
	 */
	private static void determineBackgroundImage() {
		
		//Check if it returns null
		Image image = JavaFXTools.findAnyImageWithTitle("background", InfoTool.getAbsoluteDatabasePathPlain());
		
		//Find the default one for the application
		if (image == null)
			image = new Image("/image/application_background.jpg");
		
		//Set the background Image
		BackgroundImage bgImg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(window.getWidth(), window.getHeight(), true, true, true, true));
		loginMode.setBackground(new Background(bgImg));
		root.setBackground(new Background(bgImg));
		
	}
	
	/**
	 * Resets the application background image to the default one
	 * 
	 */
	public static void resetBackgroundImage() {
		
		//Delete the background image
		JavaFXTools.deleteAnyImageWithTitle("background", InfoTool.getAbsoluteDatabasePathPlain());
		
		//Set the default one
		determineBackgroundImage();
	}
	
	/**
	 * Main Method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Hello from Main Method!");
		
		//		//---------------Check for Duplicate Instance-----------------	
		//		String id = "XR3PlayerApplication";
		//		try {
		//			JUnique.acquireLock(id, null);
		//		} catch (AlreadyLockedException e) { // Application already running.
		//			System.out.println("Duplicate instance detected...exiting...");
		//			System.exit(0);
		//		}
		//		//------------END OF: Check for Duplicate Instance-------------------
		
		launch(args);
	}
}
