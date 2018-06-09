package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXToggleButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.SystemMonitor;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.SystemMonitor.Monitor;
import main.java.com.goxr3plus.xr3player.application.settings.ApplicationSettingsController.SettingsTab;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.windows.ConsoleWindowController.ConsoleTab;

/**
 * The BottomBar of the Application
 * 
 * @author GOXR3PLUS
 *
 */
public class BottomBar extends BorderPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private HBox hBox;
	
	@FXML
	private JFXToggleButton showHideSideBar;
	
	@FXML
	private JFXToggleButton keyBindings;
	
	@FXML
	private JFXToggleButton speechRecognitionToggle;
	
	@FXML
	private Label internetConnectionLabel;
	
	@FXML
	private Label internetConnectionDescriptionLabel;
	
	@FXML
	private Label currentTimeLabel;
	
	@FXML
	private Label runningTimeLabel;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * This Thread checks for Internet Connection
	 */
	private Thread internetConnectionThread;
	
	/**
	 * This Thread checks for System Time
	 */
	private Thread timeThread;
	
	private int minutes = -1;
	
	private boolean internetPreviousStatus = false;
	
	/**
	 * Constructor.
	 */
	public BottomBar() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "BottomBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Starts a Thread that continuously checks for internet connection
	 */
	public void startInternetCheckingThread() {
		
		if (internetConnectionThread != null)
			return;
		
		//Initialize
		internetConnectionThread = new Thread(() -> {
			boolean[] newStatus = { false };
			//Just to update the image for the firstTime
			boolean firstHack = true;
			
			//Run Forever
			while (true) {
				
				newStatus[0] = InfoTool.isReachableByPing("www.google.com");
				
				//Try to connect
				if (newStatus[0] != internetPreviousStatus || firstHack)
					Platform.runLater(() -> {
						internetConnectionLabel.setDisable(!newStatus[0]);
						internetConnectionDescriptionLabel.setText(newStatus[0] ? "Connected" : "Disconnected");
					});
				
				internetPreviousStatus = newStatus[0];
				firstHack = false;
				
				//Sleep sometime [ Don't lag the CPU]
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}, "Internet Connection Checker Thread");
		
		internetConnectionThread.setDaemon(true);
		internetConnectionThread.start();
	}
	
	/**
	 * Starts a Thread that is checking the current System Time and the application running Time
	 */
	public void startAppRunningTimeThread() {
		
		if (timeThread != null)
			return;
		
		//Initialize
		timeThread = new Thread(() -> {
			
			//Run Forever
			while (true) {
				
				Platform.runLater(() -> {
					currentTimeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")));
					runningTimeLabel.setText(++minutes + ( minutes == 1 ? " minute" : " minutes" ));
				});
				
				//Sleep sometime [ Don't lag the CPU]
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}, "App Running Time Checker Thread");
		
		timeThread.setDaemon(true);
		timeThread.start();
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//keyBindingsLabel
		keyBindings.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.SHORTCUTS));
		
		//SpeechRecognitionToggle
		speechRecognitionToggle.setOnAction(a -> Main.consoleWindow.showWindow(ConsoleTab.SPEECH_RECOGNITION));
		
		//showHideSideBar
		showHideSideBar.selectedProperty().addListener((observable , oldValue , newValue) -> Main.sideBar.toogleBar());
		
	}
	
	/**
	 * @return the keyBindings
	 */
	public JFXToggleButton getKeyBindings() {
		return keyBindings;
	}
	
	public JFXToggleButton getShowHideSideBar() {
		return showHideSideBar;
	}
	
	/**
	 * @return the speechRecognitionToggle
	 */
	public JFXToggleButton getSpeechRecognitionToggle() {
		return speechRecognitionToggle;
	}
	
}
