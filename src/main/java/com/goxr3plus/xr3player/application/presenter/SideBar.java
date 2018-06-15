package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.File;
import java.io.IOException;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.database.services.CreateZipService;
import main.java.com.goxr3plus.xr3player.application.database.services.ExportZipService;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.SystemMonitor;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.SystemMonitor.Monitor;
import main.java.com.goxr3plus.xr3player.application.settings.ApplicationSettingsController.SettingsTab;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.smartcontroller.services.Operation;

public class SideBar extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private Label nameLabel;
	
	@FXML
	private StackedFontIcon stackedFontIcon;
	
	@FXML
	private ImageView userImageView;
	
	@FXML
	private StackedFontIcon noImageStackedFontIcon;
	
	@FXML
	private FontIcon userFontIconImage;
	
	@FXML
	private ToggleButton mainModeToggle;
	
	@FXML
	private ToggleGroup modeTeam;
	
	@FXML
	private FontIcon mainModeVolumeIcon;
	
	@FXML
	private ToggleButton djModeToggle;
	
	@FXML
	private FontIcon djModeVolumeIcon;
	
	@FXML
	private ToggleButton userInfoToggle;
	
	@FXML
	private ToggleButton browserToggle;
	
	@FXML
	private FontIcon browserVolumeIcon;
	
	@FXML
	private ToggleButton moviesToggle;
	
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
	public final CreateZipService zipper = new CreateZipService();
	
	/** The un zipper. */
	public final ExportZipService unZipper = new ExportZipService();
	
	//System Monitors for CPU + RAM
	private final SystemMonitor cpuMonitor = new SystemMonitor(Monitor.CPU);
	private final SystemMonitor ramMonitor = new SystemMonitor(Monitor.RAM);
	
	/**
	 * Constructor.
	 */
	public SideBar() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SideBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	//private TranslateTransition translateX = new TranslateTransition(Duration.millis(200), this)
	private ScaleTransition scaleX = new ScaleTransition(Duration.millis(100), this);
	private double preferredWidth;
	
	/**
	 * Shows the Bar.
	 */
	public void showBar() {
		if (scaleX.getStatus() == Animation.Status.RUNNING)
			return;
		
		//System.out.println("Entered Show Bar");
		
		//		//Check the orientation
		//		NodeOrientation orientation = this.getNodeOrientation();
		//		if (orientation == NodeOrientation.LEFT_TO_RIGHT) {
		//			System.out.println("Width : " + getWidth());
		//			translateX.setFromX(-getWidth());
		//			translateX.setToX(0);
		//		} else if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
		//			translateX.setFromX(getWidth());
		//			translateX.setToX(0);
		//		}
		
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
		
		//Remember the width of the node
		if (preferredWidth == 0)
			preferredWidth = getWidth();
		
		//System.out.println("Entered Hide Bar");
		
		//		//Check the orientation
		//		NodeOrientation orientation = this.getNodeOrientation();
		//		System.out.println(orientation);
		//		if (orientation == NodeOrientation.LEFT_TO_RIGHT) {
		//			System.out.println("Width : " + getWidth());
		//			translateX.setFromX(0);
		//			translateX.setToX(-getWidth());
		//		} else if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
		//			translateX.setFromX(0);
		//			translateX.setToX(getWidth());
		//		}
		
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
	public void changeSide(NodeOrientation orientation) {
		
		Main.root.getChildren().remove(this);
		
		//Check the orientation
		if (orientation == NodeOrientation.LEFT_TO_RIGHT)
			Main.root.setLeft(this);
		else if (orientation == NodeOrientation.RIGHT_TO_LEFT)
			Main.root.setRight(this);
		
		//Set the orientation
		this.setNodeOrientation(orientation);
	}
	
	/**
	 * Prepares the SideBar to be shown for LoginMode
	 * 
	 * @param b
	 */
	public void prepareForLoginMode(boolean b) {
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
		
		//Prepare the Side Bar
		prepareForLoginMode(true);
		
		//---------UPDATE ------------------------------		
		
		// checkForUpdates
		applicationUpdate.setOnAction(a -> Main.updateWindow.searchForUpdates(true));
		
		// showApplicationInfo
		showApplicationInfo.setOnAction(a -> Main.aboutWindow.show());
		
		//showManual
		showManual.setOnAction(a -> ActionTool.openFile(InfoTool.getBasePathForClass(ActionTool.class) + "XR3Player Manual.pdf"));
		
		// donation
		donation.setOnAction(a -> ActionTool.openWebSite("https://www.paypal.me/GOXR3PLUSCOMPANY"));
		
		//-----------------------------------------
		
		//applicationSettings
		applicationSettings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.GENERERAL));
		
		//downloadYoutubePlaylist
		downloadYoutubePlaylist.setOnAction(a -> ActionTool.openWebSite("http://www.youtubecomtomp3.com"));
		
		//socialMediaToMP3
		socialMediaToMP3.setOnAction(downloadYoutubePlaylist.getOnAction());
		
		//socialMediaToAnything
		socialMediaToAnything.setOnAction(a -> ActionTool.openWebSite("https://www.onlinevideoconverter.com/en/video-converter"));
		
		//applicationConsole
		applicationConsole.setOnAction(a -> Main.consoleWindow.show());
		
		//snapShot
		snapshot.setOnAction(a -> Main.captureWindow.getStage().show());
		
		// importDataBase
		importDataBase.setOnAction(e -> importDatabase());
		
		// exportDataBase
		exportDataBase.setOnAction(a -> exportDatabase());
		
		// deleteDataBase
		deleteDataBase.setOnAction(a -> deleteDatabase());
		
		//openTaskManager
		openTaskManager.setOnAction(a -> {
			new Thread(() -> {
				try {
					Runtime.getRuntime().exec("cmd /c start taskmgr");
				} catch (IOException e) {
					e.printStackTrace();
					//Show Message to User
					ActionTool.showNotification("Failed Opening Task Manager", "Failed Opening default Task Manager", Duration.millis(2000), NotificationType.ERROR);
				}
			}).start();
			
			//Show Message to User
			ActionTool.showNotification("Opening Task Manager", "Opening default system Task Manager", Duration.millis(2000), NotificationType.INFORMATION);
		});
		
		//modeTeam
		modeTeam.selectedToggleProperty().addListener((observable , oldToggle , newToggle) -> {
			if (newToggle == this.mainModeToggle)
				Main.topBar.selectTab(Main.topBar.getMainModeTab());
			else if (newToggle == this.djModeToggle)
				Main.topBar.selectTab(Main.topBar.getDjModeTab());
			else if (newToggle == this.userInfoToggle)
				Main.topBar.selectTab(Main.topBar.getUserModeTab());
			else if (newToggle == this.browserToggle)
				Main.topBar.selectTab(Main.topBar.getWebModeTab());
			else if (newToggle == this.moviesToggle)
				Main.topBar.selectTab(Main.topBar.getMoviesModeTab());
		});
		
		// StackView
		//this.maxWidthProperty().bind(imageView.fitWidthProperty())
		//this.maxHeightProperty().bind(imageView.fitHeightProperty())
		
		// ImageView
		userImageView.fitWidthProperty().bind(stackedFontIcon.heightProperty());
		userImageView.fitHeightProperty().bind(stackedFontIcon.heightProperty());
		userImageView.setOnMouseReleased(m -> Main.userInfoMode.getUser().changeUserImage());
		
		// Clip
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(stackedFontIcon.heightProperty());
		clip.heightProperty().bind(stackedFontIcon.heightProperty());
		clip.setArcWidth(90);
		clip.setArcHeight(90);
		userImageView.setClip(clip);
		
		//noImageStackedFontIcon
		noImageStackedFontIcon.visibleProperty().bind(userImageView.imageProperty().isNull());
		
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
		
		//Side Bar Volume Icons
		mainModeVolumeIcon.setVisible(false);
		djModeVolumeIcon.setVisible(false);
		browserVolumeIcon.setVisible(false);
	}
	
	/**
	 * Delete the previous and import a new Database to XR3Player
	 */
	public void importDatabase() {
		if (!zipper.isRunning() && !unZipper.isRunning() && ( Main.libraryMode.openedLibrariesViewer == null || Main.libraryMode.openedLibrariesViewer.isFree(true) )
				&& ActionTool.doQuestion(null,
						"Just to remind you : \n  After importing a new database to XR3Player \n  the old one will be permanently deleted \n  and you will continue with the fresh one :)\n\n                 ---------------------------- \n\nYou can always keep a backup of your current database if you wish ...",
						null, Main.window)) {
			
			File file = Main.specialChooser.selectDBFile(Main.window);
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
		if (!zipper.isRunning() && !unZipper.isRunning() && ( Main.libraryMode.openedLibrariesViewer == null || Main.libraryMode.openedLibrariesViewer.isFree(true) )) {
			
			File file = Main.specialChooser.exportDBFile(Main.window);
			if (file != null) {
				
				// Change the Scene View
				Main.updateScreen.setVisible(true);
				Main.updateScreen.getProgressBar().progressProperty().bind(zipper.progressProperty());
				
				// Export the database
				zipper.exportDataBase(file.getAbsolutePath(), InfoTool.getAbsoluteDatabasePathPlain());
			}
		}
	}
	
	/**
	 * Delete XR3Player Database
	 */
	public void deleteDatabase() {
		if (!zipper.isRunning() && !unZipper.isRunning() && ( Main.libraryMode.openedLibrariesViewer == null || Main.libraryMode.openedLibrariesViewer.isFree(true) )
				&& ActionTool.doQuestion(null,
						"ARE you sure you want to PERMANENTLY \nDELETE THE DATABASE?\n\n                 ---------------------------- \n\nYou can always keep a backup of your current database if you wish.\n\n                 ---------------------------- \n\nAfter that the application will automatically restart...",
						null, Main.window)) {
			
			// Close database connections
			if (Main.dbManager != null)
				Main.dbManager.manageConnection(Operation.CLOSE);
			
			// Clear the Previous database manager
			ActionTool.deleteFile(new File(InfoTool.getAbsoluteDatabasePathPlain()));
			
			// Show Update Screen
			Main.updateScreen.setVisible(true);
			Main.updateScreen.getProgressBar().progressProperty().unbind();
			Main.updateScreen.getProgressBar().setProgress(-1);
			Main.updateScreen.getLabel().setText("Restarting....");
			
			// Exit the application
			Main.canSaveData = false;
			Main.restartTheApplication(false);
			
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
	
	/**
	 * @return the moviesToggle
	 */
	public ToggleButton getMoviesToggle() {
		return moviesToggle;
	}
	
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
	 * @return the mainModeVolumeIcon
	 */
	public FontIcon getMainModeVolumeIcon() {
		return mainModeVolumeIcon;
	}
	
	/**
	 * @return the djModeVolumeIcon
	 */
	public FontIcon getDjModeVolumeIcon() {
		return djModeVolumeIcon;
	}
	
	/**
	 * @return the browserVolumeIcon
	 */
	public FontIcon getBrowserVolumeIcon() {
		return browserVolumeIcon;
	}
	
}
