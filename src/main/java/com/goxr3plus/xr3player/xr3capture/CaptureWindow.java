/*
 * 
 */
package main.java.com.goxr3plus.xr3player.xr3capture;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Class Main.
 *
 * @author GOXR3PLUS
 */
public class CaptureWindow {
	
	/** The daemon. */
	private Thread positionFixerThread;
	
	/** The stage. */
	public static Stage stage = new Stage();
	
	/** The main window controller. */
	public MainWindowController mainWindowController;
	
	/** The Capture Window of the application. */
	public CaptureWindowController captureWindowController;
	
	/** The settings window controller. */
	public SettingsWindowController settingsWindowController;
	
	/** Test to Speech using MaryTTS Libraries. */
	//public static TextToSpeech textToSpeech = new TextToSpeech()
	
	/**
	 * Constructor
	 */
	public CaptureWindow() {
		
		try {
			
			// stage
			stage.setTitle("XR3Capture Version 9!");
			//stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setAlwaysOnTop(true);
			
			// MainWindowController
			FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/xr3capture/MainWindowController.fxml"));
			loader1.load();
			mainWindowController = loader1.getController();
			
			// CaptureWindowController
			FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/xr3capture/CaptureWindowController.fxml"));
			loader2.load();
			captureWindowController = loader2.getController();
			
			// SettingsController
			FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/xr3capture/SettingsWindowController.fxml"));
			loader3.load();
			settingsWindowController = loader3.getController();
			
			// Add References between controllers
			mainWindowController.addControllerReferences(captureWindowController, settingsWindowController);
			captureWindowController.addControllerReferences(mainWindowController, settingsWindowController);
			settingsWindowController.addControllerReferences(mainWindowController, captureWindowController);
			
			// Load the dataBase
			//DataBase.loadDataBaseSettings(settingsWindowController)
			
			// Finally
			stage.setScene(new Scene(loader1.getRoot(), Color.TRANSPARENT));
			//stage.show()
			
			stage.setOnShown(s -> startPositionFixThread());
			stage.setOnHidden(h -> stopPositionFixThread());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//	DataBaseManager dataBaseManager = new DataBaseManager()
		//	dataBaseManager.retrieveJSonFileData()
		
		// Check MaryTTS
		// textToSpeech.speak("Hello my name is Mary!")
	}
	
	/**
	 * This method is starting a Thread which is running all the time and is fixing the position of the application on the screen.
	 */
	private void startPositionFixThread() {
		if (positionFixerThread != null && positionFixerThread.isAlive())
			return;
		
		// Check frequently for the Primary Screen Bounds
		positionFixerThread = new Thread(() -> {
			try {
				//Run until it is interrupted
				while (true) {
					
					// CountDownLatch
					CountDownLatch count = new CountDownLatch(1);
					
					// Get VisualBounds of the Primary Screen
					Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
					Platform.runLater(() -> {
						
						//Fix the window position
						stage.setX(mainWindowController.getRoot().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT ? bounds.getMinX() : bounds.getMaxX() - stage.getWidth());
						stage.setY(bounds.getMaxY() / 2 - stage.getHeight() / 2);
						count.countDown();
					});
					
					// Wait until the Platform.runLater has run
					count.await();
					// Sleep some time
					Thread.sleep(500);
					
				}
			} catch (@SuppressWarnings("unused") InterruptedException ex) {
				positionFixerThread.interrupt();
				//fuck dis error it is not fatal
				//Logger.getLogger(CaptureWindow.class.getName()).log(Level.WARNING, null, ex)
			}
			
			//System.out.println("XR3Positioning Thread exited")
		});
		
		positionFixerThread.setDaemon(true);
		positionFixerThread.start();
	}
	
	/**
	 * Stop thread positioning thread
	 */
	private void stopPositionFixThread() {
		if (positionFixerThread != null && positionFixerThread.isAlive())
			positionFixerThread.interrupt();
	}
	
}
