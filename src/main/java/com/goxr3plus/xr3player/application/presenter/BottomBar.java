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
	
	//System Monitors for CPU + RAM
	private final SystemMonitor cpuMonitor = new SystemMonitor(Monitor.CPU);
	private final SystemMonitor ramMonitor = new SystemMonitor(Monitor.RAM);
	
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
	private void startInternetCheckingThread() {
		
		if (internetConnectionThread != null)
			return;
		
		//Initialise
		internetConnectionThread = new Thread(() -> {
			
			//Run Forever
			while (true) {
				
				//Try to connect
				if (InfoTool.isReachableByPing("www.google.com")) {
					Platform.runLater(() -> {
						internetConnectionLabel.setDisable(false);
						internetConnectionDescriptionLabel.setText("Connected");
					});
				} else {
					Platform.runLater(() -> {
						internetConnectionLabel.setDisable(true);
						internetConnectionDescriptionLabel.setText("Disconnected");
					});
				}
				
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
	private void startTimingThread() {
		
		if (timeThread != null)
			return;
		
		//Initialise
		timeThread = new Thread(() -> {
			
			//Run Forever
			while (true) {
				
				Platform.runLater(() -> {
					String currentTimeme = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
					currentTimeLabel.setText(currentTimeme);
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
		
		//Start the Threads
		startInternetCheckingThread();
		startTimingThread();
		
		//showHideSideBar
		showHideSideBar.selectedProperty().addListener((observable , oldValue , newValue) -> Main.sideBar.toogleBar());
		
		// ----------------------------cpuMonitor
		cpuMonitor.setOnMouseReleased(r -> {
			if (cpuMonitor.isRunning())
				cpuMonitor.stopUpdater();
			else
				cpuMonitor.restartUpdater();
		});
		
		// ----------------------------ramMonitor
		ramMonitor.setOnMouseReleased(r -> {
			if (ramMonitor.isRunning())
				ramMonitor.stopUpdater();
			else
				ramMonitor.restartUpdater();
		});
		
		hBox.getChildren().addAll(cpuMonitor, ramMonitor);
		
		// -- searchField
		//searchField.setOnMouseReleased(m -> Main.playListModesTabPane.selectTab(2));
	}
	
	/**
	 * @return the keyBindings
	 */
	public JFXToggleButton getKeyBindings() {
		return keyBindings;
	}
	
	/**
	 * @return the speechRecognitionToggle
	 */
	public JFXToggleButton getSpeechRecognitionToggle() {
		return speechRecognitionToggle;
	}
	
}
