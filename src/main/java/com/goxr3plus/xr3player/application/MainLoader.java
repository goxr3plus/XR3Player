package com.goxr3plus.xr3player.application;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Properties;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
//import com.goxr3plus.xr3player.controllers.chromium.WebBrowserController;
import com.goxr3plus.xr3player.controllers.djmode.DJMode;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxDownloadsTableViewer;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxViewer;
import com.goxr3plus.xr3player.controllers.general.BottomBar;
import com.goxr3plus.xr3player.controllers.general.EmotionsTabPane;
import com.goxr3plus.xr3player.controllers.general.MainLoadingScreen;
import com.goxr3plus.xr3player.controllers.general.OnlineMusicController;
import com.goxr3plus.xr3player.controllers.general.PlayListModesSplitPane;
import com.goxr3plus.xr3player.controllers.general.PlayListModesTabPane;
import com.goxr3plus.xr3player.controllers.general.SideBar;
import com.goxr3plus.xr3player.controllers.general.TopBar;
import com.goxr3plus.xr3player.controllers.general.WelcomeScreen;
import com.goxr3plus.xr3player.controllers.librarymode.LibraryMode;
import com.goxr3plus.xr3player.controllers.loginmode.LoginMode;
import com.goxr3plus.xr3player.controllers.loginmode.User;
import com.goxr3plus.xr3player.controllers.loginmode.UserInformation;
import com.goxr3plus.xr3player.controllers.moviemode.MovieModeController;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController;
import com.goxr3plus.xr3player.controllers.smartcontroller.DragViewer;
import com.goxr3plus.xr3player.controllers.smartcontroller.MediaContextMenu;
import com.goxr3plus.xr3player.controllers.smartcontroller.MediaInformation;
import com.goxr3plus.xr3player.controllers.smartcontroller.ShopContextMenu;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.controllers.systemtree.TreeViewContextMenu;
import com.goxr3plus.xr3player.controllers.systemtree.TreeViewManager;
import com.goxr3plus.xr3player.controllers.tagging.TagWindow;
import com.goxr3plus.xr3player.controllers.windows.AboutWindow;
import com.goxr3plus.xr3player.controllers.windows.ConsoleWindowController;
import com.goxr3plus.xr3player.controllers.windows.EmotionsWindow;
import com.goxr3plus.xr3player.controllers.windows.ExportWindowController;
import com.goxr3plus.xr3player.controllers.windows.MediaDeleteWindow;
import com.goxr3plus.xr3player.controllers.windows.MediaSearchWindow;
import com.goxr3plus.xr3player.controllers.windows.RenameWindow;
import com.goxr3plus.xr3player.controllers.windows.StarWindow;
import com.goxr3plus.xr3player.controllers.windows.UpdateWindow;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.UserCategory;
import com.goxr3plus.xr3player.models.lists.EmotionListsController;
import com.goxr3plus.xr3player.models.lists.StarredMediaList;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
//import com.teamdev.jxbrowser.chromium.bb;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import static com.goxr3plus.xr3player.application.Main.borderlessScene;
import static com.goxr3plus.xr3player.application.Main.libraryMode;
import static com.goxr3plus.xr3player.application.Main.loginMode;
import static com.goxr3plus.xr3player.application.Main.root;
import static com.goxr3plus.xr3player.application.Main.welcomeScreen;
import static com.goxr3plus.xr3player.application.Main.window;

//import main.java.com.goxr3plus.xr3capture.application.CaptureWindow;

public class MainLoader {

    private static final int screenMinWidth = 800;
    private static final int screenMinHeight = 600;

    static void startPart0() {

        // Current Application Path
        System.out.println("Path :-> " + IOInfo.getBasePathForClass(Main.class));

        /* Window */
        window.setTitle("XR3Player V." + Main.APPLICATION_VERSION);
        window.setWidth(JavaFXTool.getVisualScreenWidth() * 0.95);
        window.setHeight(JavaFXTool.getVisualScreenHeight() * 0.95);
        window.centerOnScreen();
        window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
        window.centerOnScreen();
        window.setOnCloseRequest(exit -> {
            MainExit.confirmApplicationExit();
            exit.consume();
        });

        // Borderless Scene
        borderlessScene = new BorderlessScene(window, StageStyle.UNDECORATED, Main.applicationStackPane, screenMinWidth,
                screenMinHeight);
        startPart1();
        borderlessScene.getStylesheets()
                .add(MainLoader.class.getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
        borderlessScene.setTransparentWindowStyle(
                "-fx-background-color:rgb(0,0,0,0.7); -fx-border-color:firebrick; -fx-border-width:2px;");
        borderlessScene.setMoveControl(loginMode.getXr3PlayerLabel());
        borderlessScene.setMoveControl(Main.topBar.getXr3Label());
        borderlessScene.setMoveControl(Main.welcomeScreen.getTopHBox());
        window.setScene(borderlessScene);
        window.show();
        window.close();

        // Continue
        startPart2();

        // Count Downloads
        MainTools.countDownloads();

        // Delete AutoUpdate if it exists
        IOAction.deleteFile(new File(IOInfo.getBasePathForClass(Main.class) + "XR3PlayerUpdater.jar"));

        // ============= ApplicationProperties GLOBAL
        final Properties properties = Main.applicationProperties.loadProperties();

        // WelcomeScreen
        welcomeScreen.getVersionLabel().setText(window.getTitle());
        Optional.ofNullable(properties.getProperty("Show-Welcome-Screen")).ifPresentOrElse(value -> {
            welcomeScreen.getShowOnStartUp().setSelected(Boolean.valueOf(value));
            if (welcomeScreen.getShowOnStartUp().isSelected())
                welcomeScreen.showWelcomeScreen();
            else
                welcomeScreen.hideWelcomeScreen();
        }, () -> welcomeScreen.showWelcomeScreen());

        // Last Logged in user
        Optional.ofNullable(properties.getProperty("Last-LoggedIn-User")).ifPresent(userName -> {
            // Check if any user with that name exists
            Main.loginMode.viewer.getItemsObservableList().stream()
                    .filter(item -> ((User) item).getName().equals(userName))
                    // Set center item
                    .forEach(item -> {
                        Main.loginMode.viewer.setCenterItem(item);
                        // Main.loginMode.viewer.update()
                    });
        });

        // Users Color Picker
        Optional.ofNullable(properties.getProperty("Users-Background-Color"))
                .ifPresent(color -> loginMode.getColorPicker().setValue(Color.web(color)));

        Main.applicationProperties.setUpdatePropertiesLocked(false);

        // ------------------Experiments------------------
        // ScenicView.show(scene)

        // Show the Window
        window.show();
        //window.setIconified(true);

        // Check for updates
        Main.updateWindow.searchForUpdates(false);

        // XR3AutoUpdater exit message
        Platform.setImplicitExit(false);

    }

    /**
     * This method creates the intances of the needed classes in order the
     * application to run
     */
    private static void startPart1() {
        // ----------------START: The below have not dependencies on other
        // ---------------------------------//

        Main.welcomeScreen = new WelcomeScreen();

        Main.mediaDeleteWindow = new MediaDeleteWindow();

        /* The star window. */
        Main.starWindow = new StarWindow();

        /* The rename window. */
        Main.renameWindow = new RenameWindow();

        /* The rename window. */
        Main.emotionsWindow = new EmotionsWindow();

        /*
          Audio Tagging Window
         */
        Main.tagWindow = new TagWindow();

        /*
          This window is being used to export files from the application to the outside
          world
         */
        Main.exportWindow = new ExportWindowController();

        /* The About Window of the Application */
        Main.aboutWindow = new AboutWindow();

        /* The console Window of the Application */
        Main.consoleWindow = new ConsoleWindowController();

        /*
          This Window contains the settings for the whole application
         */
        Main.settingsWindow = new ApplicationSettingsController();

        /*
          This class is used to capture the computer Screen or a part of it [ Check
          XR3Capture package]
         */
//        Main.captureWindow = new CaptureWindow();

        Main.updateWindow = new UpdateWindow();

        //

        /* The Top Bar of the Application */
        Main.topBar = new TopBar();

        /* The Bottom Bar of the Application */
        Main.bottomBar = new BottomBar();

        /* The Side Bar of The Application */
        Main.sideBar = new SideBar();

        /* Application Update Screen */
        Main.updateScreen = new MainLoadingScreen();

        /** The TreeView of DJMode */
        Main.treeManager = new TreeViewManager();

        /* The Constant advancedSearch. */
        // public static final AdvancedSearch advancedSearch = new AdvancedSearch()

        Main.mediaInformation = new MediaInformation();
        //

        Main.treeViewContextMenu = new TreeViewContextMenu();

        /* The Constant songsContextMenu. */
        Main.songsContextMenu = new MediaContextMenu();
        Main.shopContextMenu = new ShopContextMenu();

        //

        /* The Constant EmotionListsController. */
        Main.emotionListsController = new EmotionListsController();

        //

        // ----------------END: The above have not dependencies on other
        // ---------------------------------//

        // --------------START: The below have dependencies on
        // others------------------------

        /* The Constant libraryMode. */
        libraryMode = new LibraryMode();

        /* The Constant djMode. */
        Main.djMode = new DJMode();

        Main.onlineMusicController = new OnlineMusicController();

        Main.emotionsTabPane = new EmotionsTabPane(Main.emotionListsController);

        Main.starredMediaList = new StarredMediaList();

        /* The Search Window Smart Controller of the application */
        Main.searchWindowSmartController = new SmartController(Genre.SEARCHWINDOW, "Searching any Media", null);

        Main.playListModesTabPane = new PlayListModesTabPane();

        /* The Constant multipleTabs. */
        Main.playListModesSplitPane = new PlayListModesSplitPane();

        /*
          The Login Mode where the user of the applications has to choose an account to
          login
         */
        loginMode = new LoginMode();

        /*
          Entering in this mode you can change the user settings and other things that
          have to do with the user....
         */
        Main.userInfoMode = new UserInformation(UserCategory.LOGGED_IN);

        /*
          This JavaFX TabPane represents a TabPane for Navigation between application
          Modes
         */
        // specialJFXTabPane = new JFXTabPane();

        Main.mediaSearchWindow = new MediaSearchWindow();

        Main.dragViewer = new DragViewer();
        // --------------END: The below have dependencies on
        // others------------w------------

        Main.movieModeController = new MovieModeController();
    }

    /**
     * This method makes further additions to secure everything will start running
     * smoothly
     */
    public static void startPart2() {

        // ---- InitOwners -------
        Main.starWindow.getWindow().initOwner(window);
        Main.renameWindow.getWindow().initOwner(window);
        Main.emotionsWindow.getWindow().initOwner(window);
        Main.exportWindow.getWindow().initOwner(window);
//        Main.consoleWindow.getWindow().initOwner(window);
        Main.settingsWindow.getWindow().initOwner(window);
        Main.aboutWindow.getWindow().initOwner(window);
        Main.updateWindow.getWindow().initOwner(window);
        Main.tagWindow.getWindow().initOwner(window);
//        Main.captureWindow.getStage().initOwner(window);
//        Main.captureWindow.settingsWindowController.getStage().initOwner(window);

        // --------- Fix the Background ------------
        MainTools.determineBackgroundImage();

        // ---------LoginMode ------------
        loginMode.getXr3PlayerLabel().setText(window.getTitle());
        loginMode.userSearchBox.registerListeners(window);
        loginMode.getBackgroundImageView().fitWidthProperty().bind(window.widthProperty());
        loginMode.getBackgroundImageView().fitHeightProperty().bind(window.heightProperty());

        // ---------mediaSearchWindow ------------
        Main.mediaSearchWindow.registerListeners(window, Main.topBar.getSearchField());
        Main.topBar.getSearchField().setOnMouseReleased(m -> Main.mediaSearchWindow.recalculateAndshow(Main.topBar.getSearchField()));

        // -------Root-----------
        Main.topBar.addXR3LabelBinding();
        root.setVisible(false);
        root.setTop(Main.topBar);
        root.setLeft(Main.sideBar);
        root.setBottom(Main.bottomBar);
        root.setCenter(Main.rootStackPane);

        // ----Create the SpecialJFXTabPane for Navigation between Modes
        Main.rootStackPane.getChildren().addAll(Main.movieModeController, Main.userInfoMode, libraryMode);
        Main.movieModeController.setVisible(false);
        Main.userInfoMode.setVisible(false);

        // Load some lol images from lol base
        final Thread browserThread = new Thread(() -> {
//            try {
//                final Field e = bb.class.getDeclaredField("e");
////                e.setAccessible(true);
//                final Field f = bb.class.getDeclaredField("f");
////                f.setAccessible(true);
//                makeNonFinal(e);
//                makeNonFinal(f);
////                final Field modifersField = Field.class.getDeclaredField("modifiers");
////                modifersField.setAccessible(true);
////                modifersField.setInt(e, ~Modifier.FINAL & e.getModifiers());
////                modifersField.setInt(f, ~Modifier.FINAL & f.getModifiers());
//                e.set(null, BigInteger.valueOf(1));
//                f.set(null, BigInteger.valueOf(1));
////                modifersField.setAccessible(false);
//            } catch (final Exception e1) {
//                e1.printStackTrace();
//            }

            // Run on JavaFX Thread
            Platform.runLater(() -> {

                // Chromium Web Browser
//                Main.webBrowser = new WebBrowserController();

                // Dropbox Viewer
                Main.dropBoxViewer = new DropboxViewer();
                Main.dropBoxViewer.getAuthenticationBrowser().getWindow().initOwner(window);
                Main.playListModesTabPane.getDropBoxTab().setContent(Main.dropBoxViewer);
                Main.dropboxDownloadsTableViewer = new DropboxDownloadsTableViewer();
                Main.playListModesTabPane.getDropBoxDownloadsTab().setContent(Main.dropboxDownloadsTableViewer);
            });

            // System.out.println("Loller Thread exited...")
        });
        browserThread.start();  // TODO: Understand why Vacuum + Exit fails if the thread isn't started.

        // ---------LibraryMode ------------

        // TopSplitPane
        libraryMode.getTopSplitPane().getItems().add(Main.playListModesSplitPane);
        SplitPane.setResizableWithParent(Main.playListModesSplitPane, Boolean.FALSE);
        libraryMode.getTopSplitPane().setDividerPositions(0.45);

        // BottomSplitPane
        libraryMode.getBottomSplitPane().getItems().add(Main.xPlayersList.getXPlayerController(0));
        SplitPane.setResizableWithParent(Main.xPlayersList.getXPlayerController(0), Boolean.FALSE);
        libraryMode.getBottomSplitPane().setDividerPositions(0.65);

        libraryMode.openedLibrariesViewer.getEmptyLabel().textProperty()
                .bind(Bindings.when(libraryMode.viewer.itemsWrapperProperty().emptyProperty()).then("Create Playlist")
                        .otherwise("Open first playlist"));
        libraryMode.librariesSearcher.registerListeners(window);

        // ----------ApplicationStackPane---------
        Main.applicationStackPane.getChildren().addAll(Main.dragViewer, root, loginMode, Main.updateScreen, Main.welcomeScreen);

        // ----------Load Application Users-------
        MainLoadUser.loadTheUsers();

        // ----------Bottom Bar----------------
        Main.bottomBar.getKeyBindings().selectedProperty()
                .bindBidirectional(Main.settingsWindow.getNativeKeyBindings().getKeyBindingsActive().selectedProperty());
        // bottomBar.getSpeechRecognitionToggle().selectedProperty().bindBidirectional(consoleWindow.getSpeechRecognition().getActivateSpeechRecognition().selectedProperty());

        // -------------User Image View----------
        Main.sideBar.getUserImageView().imageProperty().bind(Main.userInfoMode.getUserImage().imageProperty());

    }

    private static VarHandle MODIFIERS;

    private static void makeNonFinal(Field field) {
        try {
            var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }

        int mods = field.getModifiers();
        if (Modifier.isFinal(mods)) {
            MODIFIERS.set(field, mods & ~Modifier.FINAL);
        }
    }

}
