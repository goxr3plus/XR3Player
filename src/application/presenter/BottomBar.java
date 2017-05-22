package application.presenter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.settings.ApplicationSettingsController.SettingsTab;
import application.tools.InfoTool;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * The BottomBar of the Application
 * 
 * @author GOXR3PLUS
 *
 */
public class BottomBar extends HBox {
	
	//--------------------------------------------------------------
	
	@FXML
	private Label internetConnectionLabel;
	
	@FXML
	private Button keyBindingsLabel;
	
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
	
	private int minutes=-1;
	
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
						internetConnectionLabel.setText("ON");
						internetConnectionLabel.setTextFill(Color.GREEN);
					});
				} else {
					Platform.runLater(() -> {
						internetConnectionLabel.setText("OFF");
						internetConnectionLabel.setTextFill(Color.FIREBRICK);
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
	 * Starts a Thread that is checking the current System Time and the
	 * application running Time
	 */
	private void startTimingThread() {
		
		if (timeThread != null)
			return;
		
		//Initialise
		timeThread = new Thread(() -> {
			
			//Run Forever
			while (true) {
				
				Platform.runLater(() -> {
					LocalTime l = LocalTime.now();
					currentTimeLabel.setText(l.toString().substring(0,5));
					runningTimeLabel.setText(++minutes + ( minutes > 1 ? " minutes" : " minute" ));
				});
				
				//Sleep sometime [ Don't lag the CPU]
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}, "Internet Connection Checker Thread");
		
		timeThread.setDaemon(true);
		timeThread.start();
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//keyBindingsLabel
		keyBindingsLabel.setOnAction(a -> Main.settingsWindow.showWindow(SettingsTab.SHORTCUTS));
		
		//Start the Threads
		startInternetCheckingThread();
		startTimingThread();
	}
	
	/**
	 * @return the keyBindingsLabel
	 */
	public Button getKeyBindingsLabel() {
		return keyBindingsLabel;
	}
	
}
