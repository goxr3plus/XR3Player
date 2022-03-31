package com.goxr3plus.xr3player.controllers.general;

import java.io.File;
import java.io.IOException;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.application.MainExit;
//import com.goxr3plus.xr3player.controllers.chromium.WebBrowserTabController;
import com.goxr3plus.xr3player.controllers.custom.SystemMonitor;
import com.goxr3plus.xr3player.controllers.custom.SystemMonitor.Monitor;
import com.goxr3plus.xr3player.controllers.general.TopBar.WindowMode;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController.SettingsTab;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.Operation;
import com.goxr3plus.xr3player.services.database.DatabaseExportService;
import com.goxr3plus.xr3player.services.database.DatabaseImportService;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.NetworkingTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.jfoenix.controls.JFXButton;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SideBar extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private StackedFontIcon stackedFontIcon;

	@FXML
	private ImageView userImageView;

	@FXML
	private StackedFontIcon noImageStackedFontIcon;

	@FXML
	private FontIcon userFontIconImage;

	@FXML
	private Label nameLabel;

	@FXML
	private VBox modesBox;

	@FXML
	private ToggleButton mainModeToggle;

	@FXML
	private ToggleGroup modeTeam;

	@FXML
	private JFXButton mainModeVolumeButton;

	@FXML
	private StackedFontIcon mainModeStackedFont;

	@FXML
	private ToggleButton djModeToggle;

	@FXML
	private JFXButton djModeVolumeButton;

	@FXML
	private StackedFontIcon djModeStackedFont;

	@FXML
	private ToggleButton userInfoToggle;

	@FXML
	private ToggleButton browserToggle;

	@FXML
	private JFXButton browserVolumeButton;

	@FXML
	private StackedFontIcon browserStackedFont;

	@FXML
	private HBox performanceHBox;

	@FXML
	private JFXButton applicationUpdate;

	@FXML
	private JFXButton applicationSettings;

	@FXML
	private MenuButton applicationDatabase;

	@FXML
	private MenuItem importDataBase;

	@FXML
	private MenuItem exportDataBase;

	@FXML
	private MenuItem deleteDataBase;

	@FXML
	private MenuItem showApplicationInfo;

	@FXML
	private MenuItem showManual;

	@FXML
	private MenuItem donation;

	@FXML
	private JFXButton snapshot;

	@FXML
	private JFXButton applicationConsole;

	@FXML
	private JFXButton openTaskManager;

	@FXML
	private MenuItem downloadYoutubePlaylist;

	@FXML
	private MenuItem socialMediaToMP3;

	@FXML
	private MenuItem socialMediaToAnything;

	// -------------------------------------------------------------

	/** The zipper. */
	public final DatabaseExportService zipper = new DatabaseExportService();

	/** The un zipper. */
	public final DatabaseImportService unZipper = new DatabaseImportService();

	// System Monitors for CPU + RAM
	private final SystemMonitor cpuMonitor = new SystemMonitor(Monitor.CPU);
	private final SystemMonitor ramMonitor = new SystemMonitor(Monitor.RAM);

	/**
	 * Constructor.
	 */
	public SideBar() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SideBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

	}

	// private TranslateTransition translateX = new
	// TranslateTransition(Duration.millis(200), this)
	private final ScaleTransition scaleX = new ScaleTransition(Duration.millis(100), this);
	private double preferredWidth;

	/**
	 * Shows the Bar.
	 */
	public void showBar() {
		if (scaleX.getStatus() == Animation.Status.RUNNING)
			return;

		// System.out.println("Entered Show Bar");

		// //Check the orientation
		// NodeOrientation orientation = this.getNodeOrientation();
		// if (orientation == NodeOrientation.LEFT_TO_RIGHT) {
		// System.out.println("Width : " + getWidth());
		// translateX.setFromX(-getWidth());
		// translateX.setToX(0);
		// } else if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
		// translateX.setFromX(getWidth());
		// translateX.setToX(0);
		// }

		scaleX.setFromX(0.0);
		scaleX.setToX(1.0);

		scaleX.playFromStart();
		scaleX.setOnFinished(f -> {
			setMinWidth(preferredWidth);
			setPrefWidth(preferredWidth);
		});
	}

	/**
	 * Hides the Bar.
	 */
	public void hideBar() {
		if (scaleX.getStatus() == Animation.Status.RUNNING)
			return;

		// Remember the width of the node
		if (preferredWidth == 0)
			preferredWidth = getWidth();

		// System.out.println("Entered Hide Bar");

		// //Check the orientation
		// NodeOrientation orientation = this.getNodeOrientation();
		// System.out.println(orientation);
		// if (orientation == NodeOrientation.LEFT_TO_RIGHT) {
		// System.out.println("Width : " + getWidth());
		// translateX.setFromX(0);
		// translateX.setToX(-getWidth());
		// } else if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
		// translateX.setFromX(0);
		// translateX.setToX(getWidth());
		// }

		scaleX.setFromX(1.0);
		scaleX.setToX(0.0);

		scaleX.playFromStart();
		scaleX.setOnFinished(f -> {
			setMinWidth(0);
			setPrefWidth(0);
		});
	}

	/**
	 * Shows/Hides Side Bar
	 */
	public void toogleBar() {
		if (this.getScaleX() == 0.0)
			showBar();
		else
			hideBar();
	}

	/**
	 * Changes the side of the SideBar
	 *
	 * @param orientation
	 */
	public void changeSide(final NodeOrientation orientation) {

		Main.root.getChildren().remove(this);

		// Check the orientation
		if (orientation == NodeOrientation.LEFT_TO_RIGHT)
			Main.root.setLeft(this);
		else if (orientation == NodeOrientation.RIGHT_TO_LEFT)
			Main.root.setRight(this);

		// Set the orientation
		this.setNodeOrientation(orientation);
	}

	/**
	 * Prepares the SideBar to be shown for LoginMode
	 *
	 * @param b
	 */
	public void prepareForLoginMode(final boolean b) {
		if (b) {
			applicationSettings.setDisable(true);
			applicationConsole.setDisable(true);
		} else {
			applicationSettings.setDisable(false);
			applicationConsole.setDisable(false);
		}
	}

	/**
	 * Called as soon as .fxml is initialised
	 */
	@FXML
	private void initialize() {

		// Prepare the Side Bar
		prepareForLoginMode(true);

		// ---------UPDATE ------------------------------

		// checkForUpdates
		applicationUpdate.setOnAction(a -> Main.updateWindow.searchForUpdates(true));

		// showApplicationInfo
		showApplicationInfo.setOnAction(a -> Main.aboutWindow.show());

		// showManual
		showManual.setOnAction(a -> IOAction
				.openFileInEditor(IOInfo.getBasePathForClass(SideBar.class) + "XR3Player Manual.pdf"));

		// donation
		donation.setOnAction(a -> NetworkingTool.openWebSite("https://www.paypal.me/GOXR3PLUSCOMPANY"));

		// -----------------------------------------

		// applicationSettings
		applicationSettings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.GENERERAL));

		// downloadYoutubePlaylist
		downloadYoutubePlaylist.setOnAction(a -> NetworkingTool.openWebSite("http://www.youtubecomtomp3.com"));

		// socialMediaToMP3
		socialMediaToMP3.setOnAction(downloadYoutubePlaylist.getOnAction());

		// socialMediaToAnything
		socialMediaToAnything
			.setOnAction(a -> NetworkingTool.openWebSite("https://www.onlinevideoconverter.com/en/video-converter"));

		// applicationConsole
		applicationConsole.setOnAction(a -> Main.consoleWindow.show());

		// snapShot
		snapshot.setDisable(true);
//		snapshot.setOnAction(a -> Main.captureWindow.getStage().show());

		// importDataBase
		importDataBase.setOnAction(e -> importDatabase());

		// exportDataBase
		exportDataBase.setOnAction(a -> exportDatabase());

		// deleteDataBase
		deleteDataBase.setOnAction(a -> deleteDatabase());

		// openTaskManager
		openTaskManager.setOnAction(a -> {
			new Thread(() -> {
				try {
					Runtime.getRuntime().exec("cmd /c start taskmgr");
				} catch (final IOException e) {
					e.printStackTrace();
					// Show Message to User
					AlertTool.showNotification("Failed Opening Task Manager", "Failed Opening default Task Manager",
						Duration.millis(2000), NotificationType.ERROR);
				}
			}).start();

			// Show Message to User
			AlertTool.showNotification("Opening Task Manager", "Opening default system Task Manager",
				Duration.millis(2000), NotificationType.INFORMATION);
		});

		// modeTeam
		modeTeam.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
			if (newToggle == this.mainModeToggle) {
				Main.topBar.goMode(WindowMode.MAINMODE);

				// Fix things
				Main.libraryMode.getDjModeStackPane().setVisible(false);

				/* Fix the bug with Volume Bars */
				double[] positions = Main.libraryMode.getTopSplitPane().getDividerPositions();
				positions[0] += 0.06;
				Main.libraryMode.getTopSplitPane().setDividerPositions(positions);

				positions[0] -= 0.05;
				Main.libraryMode.getTopSplitPane().setDividerPositions(positions);

			} else if (newToggle == this.djModeToggle) {
				Main.topBar.goMode(WindowMode.MAINMODE);

				// Fix things
				Main.libraryMode.getDjModeStackPane().setVisible(true);

				/* Fix the bug with Volume Bars */
				double[] positions = Main.libraryMode.getTopSplitPane().getDividerPositions();
				positions[0] += 0.05;
				Main.libraryMode.getTopSplitPane().setDividerPositions(positions);

				positions[0] -= 0.06;
				Main.libraryMode.getTopSplitPane().setDividerPositions(positions);

			} else if (newToggle == this.userInfoToggle) {
				Main.topBar.goMode(WindowMode.USERMODE);
			} else if (newToggle == this.browserToggle) {
				Main.topBar.goMode(WindowMode.WEBMODE);
//			} else if (newToggle == this.moviesToggle) {
//				Main.topBar.goMode(WindowMode.MOVIEMODE);
			}
		});

		// modesStackPane
		modesBox.addEventFilter(KeyEvent.ANY, event -> {
			if (event.getCode().isArrowKey()) {
				event.consume();
			}
		});

		// StackView
		// this.maxWidthProperty().bind(imageView.fitWidthProperty())
		// this.maxHeightProperty().bind(imageView.fitHeightProperty())

		// ImageView
		userImageView.fitWidthProperty().bind(stackedFontIcon.heightProperty());
		userImageView.fitHeightProperty().bind(stackedFontIcon.heightProperty());
		userImageView.setOnMouseReleased(m -> Main.userInfoMode.getUser().changeUserImage());

		// Clip
		final Rectangle clip = new Rectangle();
		clip.widthProperty().bind(stackedFontIcon.heightProperty());
		clip.heightProperty().bind(stackedFontIcon.heightProperty());
		clip.setArcWidth(90);
		clip.setArcHeight(90);
		userImageView.setClip(clip);

		// noImageStackedFontIcon
		noImageStackedFontIcon.visibleProperty()
				.bind(stackedFontIcon.hoverProperty().or(userImageView.imageProperty().isNull()));
		noImageStackedFontIcon.setOnMouseReleased(m -> Main.userInfoMode.getUser().changeUserImage());

		// cpuMonitor
		cpuMonitor.setOnMouseReleased(r -> {
			if (cpuMonitor.isRunning())
				cpuMonitor.stopUpdater();
			else
				cpuMonitor.restartUpdater();
		});

		// ramMonitor
		ramMonitor.setOnMouseReleased(r -> {
			if (ramMonitor.isRunning())
				ramMonitor.stopUpdater();
			else
				ramMonitor.restartUpdater();
		});

		performanceHBox.getChildren().addAll(cpuMonitor, ramMonitor);

		// MainModeVolumeButton
		mainModeVolumeButton.setOnAction(a -> {
			final boolean mute = !mainModeStackedFont.getChildren().get(0).isVisible();
			Main.xPlayersList.getXPlayerController(0).getMuteButton().setSelected(mute);
			mainModeStackedFont.getChildren().get(1).setVisible(!mute);
		});
		mainModeStackedFont.getChildren().get(0).visibleProperty()
			.bind(mainModeStackedFont.getChildren().get(1).visibleProperty().not());

		// DjModeVolumeButton
		djModeVolumeButton.setOnAction(a -> {
			final boolean mute = !djModeStackedFont.getChildren().get(0).isVisible();
			Main.xPlayersList.getXPlayerController(1).getMuteButton().setSelected(mute);
			Main.xPlayersList.getXPlayerController(2).getMuteButton().setSelected(mute);
			djModeStackedFont.getChildren().get(1).setVisible(!mute);
		});
		djModeStackedFont.getChildren().get(0).visibleProperty()
			.bind(djModeStackedFont.getChildren().get(1).visibleProperty().not());

		// BrowserVolumeButton
//		browserVolumeButton.setOnAction(a -> {
//			final boolean mute = !browserStackedFont.getChildren().get(0).isVisible();
//			// Mute or Unmute webrowser tabs
//			Main.webBrowser.getTabPane().getTabs()
//				.forEach(tab -> ((WebBrowserTabController) tab.getContent()).getBrowser().setAudioMuted(mute));
//			browserStackedFont.getChildren().get(1).setVisible(!mute);
//		});

		browserStackedFont.getChildren().get(0).visibleProperty()
			.bind(browserStackedFont.getChildren().get(1).visibleProperty().not());

	}

	/**
	 * Delete the previous and import a new Database to XR3Player
	 */
	public void importDatabase() {
		if (!zipper.isRunning() && !unZipper.isRunning()
				&& (Main.libraryMode.openedLibrariesViewer == null
						|| Main.libraryMode.openedLibrariesViewer.isFree(true))
				&& AlertTool.doQuestion(null,
						"Just to remind you : \n  After importing a new database to XR3Player \n  the old one will be permanently deleted \n  and you will continue with the fresh one :)\n\n                 ---------------------------- \n\nYou can always keep a backup of your current database if you wish ...",
						null, Main.window)) {

			final File file = Main.specialChooser.selectDBFile(Main.window);
			if (file != null) {
				// Change the Scene View
				Main.updateScreen.setVisible(true);
				Main.updateScreen.getProgressBar().progressProperty().bind(unZipper.progressProperty());

				// Import the new database
				unZipper.importDataBase(file.getAbsolutePath());
			}
		}
	}

	/**
	 * Export XR3Player Database
	 */
	public void exportDatabase() {
		if (!zipper.isRunning() && !unZipper.isRunning() && (Main.libraryMode.openedLibrariesViewer == null
			|| Main.libraryMode.openedLibrariesViewer.isFree(true))) {

			final File file = Main.specialChooser.exportDBFile(Main.window);
			if (file != null) {

				// Change the Scene View
				Main.updateScreen.setVisible(true);
				Main.updateScreen.getProgressBar().progressProperty().bind(zipper.progressProperty());

				// Export the database
				zipper.exportDataBase(file.getAbsolutePath(), DatabaseTool.getAbsoluteDatabasePathPlain());
			}
		}
	}

	/**
	 * Delete XR3Player Database
	 */
	public void deleteDatabase() {
		if (!zipper.isRunning() && !unZipper.isRunning()
			&& (Main.libraryMode.openedLibrariesViewer == null
			|| Main.libraryMode.openedLibrariesViewer.isFree(true))
			&& AlertTool.doQuestion(null,
			"ARE you sure you want to PERMANENTLY \nDELETE THE DATABASE?\n\n                 ---------------------------- \n\nYou can always keep a backup of your current database if you wish.\n\n                 ---------------------------- \n\nAfter that the application will automatically restart...",
			null, Main.window)) {

			// Close database connections
			if (Main.dbManager != null)
				Main.dbManager.manageConnection(Operation.CLOSE);

			// Clear the Previous database manager
			IOAction.deleteFile(new File(DatabaseTool.getAbsoluteDatabasePathPlain()));

			// Show Update Screen
			Main.updateScreen.setVisible(true);
			Main.updateScreen.getProgressBar().progressProperty().unbind();
			Main.updateScreen.getProgressBar().setProgress(-1);
			Main.updateScreen.getLabel().setText("Restarting....");

			// Exit the application
			Main.canSaveData = false;
			MainExit.restartTheApplication(false);

		}
	}

	/**
	 * @return the mainModeToggle
	 */
	public ToggleButton getMainModeToggle() {
		return mainModeToggle;
	}

	/**
	 * @return the djModeToggle
	 */
	public ToggleButton getDjModeToggle() {
		return djModeToggle;
	}

	/**
	 * /**
	 *
	 * @return the userInfoToggle
	 */
	public ToggleButton getUserInfoToggle() {
		return userInfoToggle;
	}

	/**
	 * @return the browserToggle
	 */
	public ToggleButton getBrowserToggle() {
		return browserToggle;
	}

//	/**
//	 * @return the moviesToggle
//	 */
//	public ToggleButton getMoviesToggle() {
//		return moviesToggle;
//	}

	/**
	 * @return the nameLabel
	 */
	public Label getNameLabel() {
		return nameLabel;
	}

	/**
	 * @return the userImageView
	 */
	public ImageView getUserImageView() {
		return userImageView;
	}

	/**
	 * @return the mainModeVolumeButton
	 */
	public JFXButton getMainModeVolumeButton() {
		return mainModeVolumeButton;
	}

	/**
	 * @return the djModeVolumeButton
	 */
	public JFXButton getDjModeVolumeButton() {
		return djModeVolumeButton;
	}

	/**
	 * @return the browserVolumeButton
	 */
	public JFXButton getBrowserVolumeButton() {
		return browserVolumeButton;
	}

	/**
	 * @return the browserStackedFont
	 */
	public StackedFontIcon getBrowserStackedFont() {
		return browserStackedFont;
	}

	/**
	 * @return the djModeStackedFont
	 */
	public StackedFontIcon getDjModeStackedFont() {
		return djModeStackedFont;
	}

	/**
	 * @return the mainModeStackedFont
	 */
	public StackedFontIcon getMainModeStackedFont() {
		return mainModeStackedFont;
	}

}
