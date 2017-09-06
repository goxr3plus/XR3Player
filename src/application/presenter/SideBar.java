package application.presenter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import application.Main;
import application.services.CreateZipService;
import application.services.ExportZipService;
import application.settings.ApplicationSettingsController.SettingsTab;
import application.tools.ActionTool;
import application.tools.InfoTool;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import smartcontroller.services.Operation;
import xr3capture.CaptureWindow;

/**
 * This class is used as the SideBar of the application.
 *
 * @author GOXR3PLUS
 */
public class SideBar extends BorderPane {
	
	//-----------------------------------------------------
	
	@FXML
	private JFXButton applicationUpdate;
	
	@FXML
	private MenuButton applicationDatabase;
	
	@FXML
	private MenuItem importDataBase;
	
	@FXML
	private MenuItem exportDataBase;
	
	@FXML
	private MenuItem deleteDataBase;
	
	@FXML
	private JFXButton applicationConsole;
	
	@FXML
	private JFXButton applicationSettings;
	
	@FXML
	private Button snapshot;
	
	@FXML
	private MenuItem downloadYoutubePlaylist;
	
	@FXML
	private MenuItem socialMediaToMP3;
	
	@FXML
	private MenuItem socialMediaToAnything;
	
	@FXML
	private MenuItem showWelcomeScreen;
	
	@FXML
	private MenuItem showApplicationInfo;
	
	@FXML
	private MenuItem showManual;
	
	@FXML
	private MenuItem donation;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/** The internet thread. */
	Thread internetThread;
	
	/** The zipper. */
	public final CreateZipService zipper = new CreateZipService();
	
	/** The un zipper. */
	public final ExportZipService unZipper = new ExportZipService();
	
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
			logger.log(Level.SEVERE, "", ex);
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
	
	String style = "-fx-background-radius: 15 0 0 15; -fx-background-color:black; -fx-border-width:0 4 0 0;";
	
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
		
		// showWelcomeScreen
		showWelcomeScreen.setOnAction(a -> Main.welcomeScreen.show());
		
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
		snapshot.setOnAction(a -> CaptureWindow.stage.show());
		
		//	ActionTool.showAlert("Snapshot Window", "Read the below.",
		//		"Hello BRO!\n\n FIRST\n\nEnable KeyBindings from Settings Window (Settings->Check KeyBindings CheckBox)\n\n THEN\n\n[ HOLD ALT KEY ] in order the snapshot window to be visible,then select an area of the screen with your mouse \n\n[RELEASE ALT KEY] or PRESS [ ESCAPE OR BACKSPACE ] to close the snapshot window \n\n FINALLY\n\nPress : [ ENTER OR SPACE ] to capture the selected area.");
		
		// importDataBase
		importDataBase.setOnAction(e -> {
			if (!zipper.isRunning() && !unZipper.isRunning() && ( Main.libraryMode.multipleLibs == null || Main.libraryMode.multipleLibs.isFree(true) )) {
				
				File file = Main.specialChooser.selectDBFile(Main.window);
				if (file != null) {
					// Change the Scene View
					Main.updateScreen.setVisible(true);
					Main.updateScreen.getProgressBar().progressProperty().bind(unZipper.progressProperty());
					
					// Import the new database
					unZipper.importDataBase(file.getAbsolutePath());
				}
			}
		});
		
		// exportDataBase
		exportDataBase.setOnAction(a -> {
			if (!zipper.isRunning() && !unZipper.isRunning() && ( Main.libraryMode.multipleLibs == null || Main.libraryMode.multipleLibs.isFree(true) )) {
				
				File file = Main.specialChooser.exportDBFile(Main.window);
				if (file != null) {
					
					// Change the Scene View
					Main.updateScreen.setVisible(true);
					Main.updateScreen.getProgressBar().progressProperty().bind(zipper.progressProperty());
					
					// Export the database
					zipper.exportDataBase(file.getAbsolutePath(), InfoTool.getAbsoluteDatabasePathPlain());
				}
			}
		});
		
		// deleteDataBase
		deleteDataBase.setOnAction(a -> {
			if (!zipper.isRunning() && !unZipper.isRunning() && ( Main.libraryMode.multipleLibs == null || Main.libraryMode.multipleLibs.isFree(true) ) && ActionTool.doQuestion(
					"Are you soore you want to delete the database?\nYou can keep a copy before deleting it by using export database functionality.\n\nAfter that the application will automatically restart...",
					Main.window)) {
				
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
		});
		
	}
	
}
