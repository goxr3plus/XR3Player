/**
 * 
 */
package application.settings.window;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;
import xplayer.presenter.XPlayerController;

/**
 * @author GOXR3PLUS
 *
 */
public class KeyBindingsController extends BorderPane {

    @FXML
    private Accordion accordion;

    @FXML
    private JFXCheckBox keyBindingsActive;
    //--------------------------------------------------------

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Constructor
     */
    public KeyBindingsController() {

	// Get the logger for "org.jnativehook" and set the level to off.
	Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
	logger.setLevel(Level.OFF);

	// Listen for all key events
	GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
	    public void nativeKeyPressed(NativeKeyEvent e) {
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		int keyCode = e.getKeyCode();

		//-------------SNAPSHOT WINDOW
		if (keyCode == NativeKeyEvent.ALT_L_MASK || keyCode == NativeKeyEvent.ALT_R_MASK
			|| keyCode == NativeKeyEvent.ALT_MASK || keyCode == NativeKeyEvent.VC_ALT) {
		    Main.snapShotWindow.prepareForCapture();
		}
	    }

	    //Key Released
	    public void nativeKeyReleased(NativeKeyEvent e) {
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		//Run in JavaFX Thread
		Platform.runLater(() -> {
		    XPlayerController xPlayer = Main.xPlayersList.getXPlayerController(0);

		    int keyCode = e.getKeyCode();

		    //WORK WORK WORK AAAA YEAH F F F FFUUUUUCK BITCH

		    //-----------PLAYERS
		    if (keyCode == NativeKeyEvent.VC_O && (e.getModifiers() == NativeKeyEvent.SHIFT_L_MASK
			    || e.getModifiers() == NativeKeyEvent.SHIFT_R_MASK
			    || e.getModifiers() == NativeKeyEvent.SHIFT_MASK
			    || e.getModifiers() == NativeKeyEvent.VC_SHIFT)) { //Open File Chooser
			xPlayer.openFileChooser();
			System.out.println("Opening File Chooser");

		    } else if (keyCode == NativeKeyEvent.VC_MEDIA_PLAY) { //PlayPause
			xPlayer.reversePlayAndPause();
		    } else if (keyCode == NativeKeyEvent.VC_R && (e.getModifiers() == NativeKeyEvent.SHIFT_L_MASK
			    || e.getModifiers() == NativeKeyEvent.SHIFT_R_MASK
			    || e.getModifiers() == NativeKeyEvent.SHIFT_MASK
			    || e.getModifiers() == NativeKeyEvent.VC_SHIFT)) { //Replay
			xPlayer.replaySong();

		    } else if (keyCode == NativeKeyEvent.VC_LEFT) {   //SEEK BACKWARD
			xPlayer.seek(-10);
		    } else if (keyCode == NativeKeyEvent.VC_RIGHT) {  //SEEK FORWARD
			xPlayer.seek(+10);

		    } else if (keyCode == NativeKeyEvent.VC_S && (e.getModifiers() == NativeKeyEvent.SHIFT_L_MASK
			    || e.getModifiers() == NativeKeyEvent.SHIFT_R_MASK
			    || e.getModifiers() == NativeKeyEvent.SHIFT_MASK
			    || e.getModifiers() == NativeKeyEvent.VC_SHIFT)) { //Open Media in Explorer
			xPlayer.openAudioInExplorer();
		    }

		    //-----------SNAPSHOT WINDOW
		    else if (keyCode == NativeKeyEvent.ALT_L_MASK || keyCode == NativeKeyEvent.ALT_R_MASK
			    || keyCode == NativeKeyEvent.ALT_MASK || keyCode == NativeKeyEvent.VC_ALT) {
			Main.snapShotWindow.hideWindow();
		    }

		});
	    }

	    public void nativeKeyTyped(NativeKeyEvent e) {
		System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

	    }
	});

	GlobalScreen.addNativeMouseListener(new NativeMouseListener() {

	    @Override
	    public void nativeMouseClicked(NativeMouseEvent e) {
		//info.setText(NativeKeyEvent.getKeyText(e.getKeyCode()));
	    }

	    @Override
	    public void nativeMousePressed(NativeMouseEvent e) {
		System.out.println("Mouse Pressed: " + NativeInputEvent.getModifiersText(e.getButton()));
		//info.setText(NativeInputEvent.getModifiersText(e.getButton()));//getKeyText(e.getgetKeyCode()));
	    }

	    @Override
	    public void nativeMouseReleased(NativeMouseEvent e) {
		//info.setText(NativeMouseEvent.getKeyText(e.getKeyCode()));
	    }

	});

	// ------------------------------------FXMLLOADER-------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "KeyBindingsController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}

    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {

	//KeyBindingsActive
	keyBindingsActive.selectedProperty().addListener((observable, oldValue, newValue) -> {

	    try {
		if (newValue) {
		    Alert alert = new Alert(AlertType.WARNING);
		    alert.initOwner(Main.window);
		    alert.setHeaderText("Please read this :) ");
		    alert.setContentText(
			    "I have added this functionallity for testing purposes. \n\n Specifically a KeyLogger is used to catch every key you type so it may has conflicts with other applications in case they have the same shortcuts  as XR3Player does . \n\n It is recommend to use it with care until i make it more mature\n\n Have a nice day!       GOXR3PLUS STUDIO");
		    alert.showAndWait();

		    // Add Global Listener for the Operating System
		    GlobalScreen.setEventDispatcher(new SwingDispatchService());
		    GlobalScreen.registerNativeHook();

		    keyBindingsActive.setText("Click here to De-activate KeyBindings");
		} else {
		    //Remove Global Listener from the Operating System
		    GlobalScreen.unregisterNativeHook();

		    keyBindingsActive.setText("Click here to Activate KeyBindings");
		}
	    } catch (NativeHookException ex) {
		//Log it
		logger.log(Level.WARNING,
			"\"Trying to register native hook for the operating system an error occured!\"", ex);

		//Show a notification to the user
		ActionTool.showNotification("Error with JNativeHook",
			"Trying to register native hook for the operating \n system an error occured!",
			Duration.seconds(2), NotificationType.ERROR);
	    }

	    ActionTool.showNotification("Notification",
		    "KeyBindings has been [ " + (newValue ? "activated" : "deactivated") + " ]", Duration.millis(800),
		    NotificationType.INFORMATION);
	});

	//accordion
	accordion.setExpandedPane(accordion.getPanes().get(0));

    }

}
