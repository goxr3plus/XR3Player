package application;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import application.database.ExportDataBase;
import application.database.ImportDataBase;
import application.settings.ApplicationSettingsController.SettingsTab;
import application.tools.ActionTool;
import application.tools.InfoTool;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import smartcontroller.Operation;

/**
 * This class is used as the SideBar of the application.
 *
 * @author GOXR3PLUS
 */
public class SideBar extends BorderPane {
	
	//-----------------------------------------------------
	
	@FXML
	private MenuButton applicationDatabase;
	
	@FXML
	private MenuItem importDataBase;
	
	@FXML
	private MenuItem exportDataBase;
	
	@FXML
	private MenuItem deleteDataBase;
	
	@FXML
	private JFXButton applicationSearch;
	
	@FXML
	private JFXButton snapshot;
	
	@FXML
	private JFXButton applicationConverter;
	
	@FXML
	private JFXButton applicationConsole;
	
	@FXML
	private JFXButton applicationUpdate;
	
	@FXML
	private JFXButton applicationSettings;
	
	@FXML
	private MenuItem aboutSection;
	
	@FXML
	private MenuItem help;
	
	@FXML
	private MenuItem donation;
	
	@FXML
	private JFXButton donateButton;
	
	// -------------------------------------------------------------
	
	/** Translate Transition used to show/hide the bar. */
	private TranslateTransition tTrans;
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/** The internet thread. */
	Thread internetThread;
	
	/** The zipper. */
	public final ExportDataBase zipper = new ExportDataBase();
	
	/** The un zipper. */
	public final ImportDataBase unZipper = new ImportDataBase();
	
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
	
	//	/**
	//	 * Gets the speech toggle button.
	//	 *
	//	 * @return the speech toggle button
	//	 */
	//	public JFXToggleButton getSpeechToggleButton() {
	//		return speechToggle;
	//	}
	//	
	//	/**
	//	 * Gets the speech progress indicator.
	//	 *
	//	 * @return the speech progress indicator
	//	 */
	//	public ProgressIndicator getSpeechProgressIndicator() {
	//		return speechProgressIndicator;
	//	}
	
	/**
	 * Shows the Bar.
	 */
	public void showBar() {
		if (tTrans.getStatus() != Animation.Status.RUNNING && this.getTranslateX() == -this.getPrefWidth()) {
			tTrans.setRate(1);
			tTrans.playFromStart();
		}
	}
	
	/**
	 * Hides the Bar.
	 */
	public void hideBar() {
		if (tTrans.getStatus() != Animation.Status.RUNNING && this.getTranslateX() == 0) {
			tTrans.setRate(-1);
			tTrans.playFrom(tTrans.getTotalDuration());
		}
	}
	
	/**
	 * Shows/Hides Side Bar
	 */
	public void toogleBar() {
		if (this.getTranslateX() == -this.getPrefWidth())
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
	
	//    /**
	//     * Goes to MainMode
	//     */
	//    public void goMainMode() {
	//	Main.rootFlipPane.flipToFront();
	//	goMainMode.setStyle("-fx-border-color:firebrick; " + style);
	//	goUserMode.setStyle("-fx-border-color:transparent; " + style);
	//    }
	//
	//    /**
	//     * Goes to UserMode
	//     */
	//    public void goUserMode() {
	//	Main.rootFlipPane.flipToBack();
	//	goMainMode.setStyle("-fx-border-color:transparent; " + style);
	//	goUserMode.setStyle("-fx-border-color:firebrick; " + style);
	//    }
	
	/**
	 * Prepares the SideBar to be shown for LoginMode
	 * 
	 * @param b
	 */
	public void prepareForLoginMode(boolean b) {
		if (b) {
			//goMainMode.setDisable(true);
			//goUserMode.setDisable(true);
			applicationSettings.setDisable(true);
			applicationConsole.setDisable(true);
			applicationSearch.setDisable(true);
			// applicationDatabase.setDisable(true);
			//snapshot.setDisable(true);
		} else {
			//goMainMode.setDisable(false);
			//goUserMode.setDisable(false);
			applicationSettings.setDisable(false);
			applicationConsole.setDisable(false);
			applicationSearch.setDisable(false);
			//applicationDatabase.setDisable(false);
			//snapshot.setDisable(false);
		}
	}
	
	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {
		
		//Prepare the Side Bar
		prepareForLoginMode(true);
		
		// Translate Transition
		tTrans = new TranslateTransition(Duration.millis(200), this);
		tTrans.setFromX(-this.getPrefWidth());
		tTrans.setToX(0);
		
		//this.setTranslateX(-this.getPrefWidth())
		//showBar()
		
		//---------UPDATE ------------------------------
		
		// checkForUpdates
		applicationUpdate.setOnAction(a -> Main.updateWindow.searchForUpdates(true));
		
		//help
		help.setOnAction(a -> ActionTool.openFile(InfoTool.getBasePathForClass(ActionTool.class) + "XR3Player Manual.pdf"));
		
		// aboutSection
		aboutSection.setOnAction(a -> Main.aboutWindow.showWindow());
		
		// donation
		donation.setOnAction(a -> ActionTool.openWebSite("https://www.paypal.me/GOXR3PLUSCOMPANY"));
		
		// donateButton
		donateButton.setOnAction(donation.getOnAction());
		
		//---------MODE ------------------------------	
		//	//goMainMode
		//	goMainMode.setOnAction(a -> goMainMode());
		//
		//	//goUserMode
		//	goUserMode.setOnAction(a -> goUserMode());
		//
		//	//theMovieDBMode
		//	browserMode.setOnAction(a -> {
		//
		//	});
		
		//-----------------------------------------
		
		// -- applicationSearch
		applicationSearch.setOnAction(a -> Main.searchWindow.show());
		
		//applicationSettings
		applicationSettings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.GENERERAL));
		
		//applicationConverter
		applicationConverter.setOnAction(a -> ActionTool.openWebSite("https://www.onlinevideoconverter.com/en/video-converter"));
		
		//applicationConsole
		applicationConsole.setOnAction(a -> Main.consoleWindow.show());
		
		//snapShot
		snapshot.setOnAction(a -> Main.captureWindow.stage.show());
		
		//	ActionTool.showAlert("Snapshot Window", "Read the below.",
		//		"Hello BRO!\n\n FIRST\n\nEnable KeyBindings from Settings Window (Settings->Check KeyBindings CheckBox)\n\n THEN\n\n[ HOLD ALT KEY ] in order the snapshot window to be visible,then select an area of the screen with your mouse \n\n[RELEASE ALT KEY] or PRESS [ ESCAPE OR BACKSPACE ] to close the snapshot window \n\n FINALLY\n\nPress : [ ENTER OR SPACE ] to capture the selected area.");
		
		// importDataBase
		importDataBase.setOnAction(e -> {
			if (!zipper.isRunning() && !unZipper.isRunning()
					&& ( Main.libraryMode.multipleLibs == null || Main.libraryMode.multipleLibs.isFree(true) )) {
				
				File file = Main.specialChooser.prepareToImportDataBase(Main.window);
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
			if (!zipper.isRunning() && !unZipper.isRunning()
					&& ( Main.libraryMode.multipleLibs == null || Main.libraryMode.multipleLibs.isFree(true) )) {
				
				File file = Main.specialChooser.prepareForExportDataBase(Main.window);
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
			if (!zipper.isRunning() && !unZipper.isRunning()
					&& ( Main.libraryMode.multipleLibs == null || Main.libraryMode.multipleLibs.isFree(true) ) && ActionTool.doQuestion(
							"Are you soore you want to delete the database?\nYou can keep a copy before deleting it by using export database functionality.\n\nAfter that the application will automatically restart...")) {
				
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
		
		//		// speechLabel and speechButton
		//		speechLabel.disableProperty().bind(speechToggle.selectedProperty().not());
		//		//	speechToggle.selectedProperty().addListener(l -> {
		//		//	    if (speechToggle.isSelected())
		//		//		Main.speechReader.startSpeechRecognizer();
		//		//	    else
		//		//		Main.speechReader.stopSpeechRec(true);
		//		//	});
		//		
		//		speechToggle.setDisable(true);
		//		// ------------------------------------About the Internet
		//		
		//		// Create the runnable only one time
		//		Runnable runnable = () -> {
		//			
		//			// Disable the Progress Indicator
		//			// Thread has started
		//			Platform.runLater(() -> {
		//				internetProgressIndicator.setVisible(false);
		//				internetLabel.setText("");
		//			});
		//			
		//			// if internetToggle is still selected
		//			while (internetToggle.isSelected()) {
		//				
		//				// Start a count down latch
		//				CountDownLatch latch = new CountDownLatch(1);
		//				
		//				// Decide
		//				if (InfoTool.isReachableByPing("www.google.com")) {
		//					
		//					// System.out.println("Internet is reachable...")
		//					
		//					// Enable internet label
		//					Platform.runLater(() -> {
		//						internetLabel.setDisable(false);
		//						latch.countDown();
		//					});
		//					
		//				} else {
		//					
		//					// System.out.println("Internet not reachable...")
		//					
		//					// Disable internet label
		//					Platform.runLater(() -> {
		//						internetLabel.setDisable(true);
		//						latch.countDown();
		//					});
		//					
		//				}
		//				try {
		//					latch.await();
		//					Thread.sleep(700);
		//				} catch (InterruptedException ex) {
		//					internetThread.interrupt();
		//					logger.log(Level.WARNING, "", ex);
		//				}
		//			}
		//			
		//			Platform.runLater(() -> {
		//				internetProgressIndicator.setVisible(false);
		//				internetLabel.setText("??");
		//			});
		//		};
		//		
		//		internetToggle.selectedProperty().addListener(l -> {
		//			if (internetToggle.isSelected()) {
		//				internetProgressIndicator.setVisible(true);
		//				internetThread = new Thread(runnable);
		//				internetThread.start();
		//			} else {
		//				internetProgressIndicator.setVisible(true);
		//				internetLabel.setDisable(true);
		//			}
		//			
		//		});
		
	}
	
}
