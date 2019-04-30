package com.goxr3plus.xr3player.controllers.settings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * @author GOXR3PLUS
 *
 */
public class KeyBindingsController extends BorderPane {

	// --------------------------------------------------------

	@FXML
	private Accordion accordion;

	@FXML
	private JFXCheckBox keyBindingsActive;

	@FXML
	private MenuButton choosedPlayerMenuButton;

	@FXML
	private ToggleGroup xPlayerSelected;

	// --------------------------------------------------------

	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Constructor.
	 */
	public KeyBindingsController() {

		addKeyListeners();

		// ------------------------------------FXMLLOADER-------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SETTINGS_FXMLS + "KeyBindingsController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {

		// KeyBindingsActive
		keyBindingsActive.selectedProperty().addListener((observable, oldValue, newValue) -> {

			try {
				if (newValue) {

					// Add Global Listener for the Operating System
					GlobalScreen.setEventDispatcher(new SwingDispatchService());
					GlobalScreen.registerNativeHook();

					keyBindingsActive.setText("Click here to De-activate KeyBindings");
				} else {
					// Remove Global Listener from the Operating System
					GlobalScreen.unregisterNativeHook();

					keyBindingsActive.setText("Click here to Activate KeyBindings");
				}

				// Update the properties file
				Main.dbManager.getPropertiesDb().updateProperty("ShortCuts-KeyBindings",
						String.valueOf(keyBindingsActive.isSelected()));
			} catch (NativeHookException ex) {
				// Log it
				logger.log(Level.WARNING,
						"\"Trying to register native hook for the operating system an error occured!\"", ex);

				// Show a notification to the user
				AlertTool.showNotification("Error with JNativeHook",
						"Trying to register native hook for the operating \n system an error occured!",
						Duration.seconds(2), NotificationType.ERROR);
			}

		});

		// xPlayerSelected
		xPlayerSelected.selectedToggleProperty().addListener(listener -> {
			// System.out.println(JavaFXTools.getIndexOfSelectedToggle(xPlayerSelected));
			int selectedIndex = JavaFXTool.getIndexOfSelectedToggle(xPlayerSelected);
			choosedPlayerMenuButton.setText("Choosed Player = { " + selectedIndex + " }");

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("ShortCuts-SelectedPlayer", String.valueOf(selectedIndex));
		});

		// accordion
		accordion.setExpandedPane(accordion.getPanes().get(0));

	}

	// ------------------------------------------------------------------------------------------------------

	private void addKeyListeners() {

		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Listen for all key events
		GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			public void nativeKeyPressed(NativeKeyEvent e) {
				// System.out.println("Key Pressed: " +
				// NativeKeyEvent.getKeyText(e.getKeyCode()));

				// Run in JavaFX Thread
				Platform.runLater(() -> decideForKeyPressed(e));
			}

			/** Key Released. */
			public void nativeKeyReleased(NativeKeyEvent e) {
				// System.out.println("Key Released: " +
				// NativeKeyEvent.getKeyText(e.getKeyCode()));

				// Run in JavaFX Thread
				Platform.runLater(() -> decideForKeyReleased(e));
			}

			public void nativeKeyTyped(NativeKeyEvent e) {
				// System.out.println("Key Typed: " +
				// NativeKeyEvent.getKeyText(e.getKeyCode()));

			}
		});

		// GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
		//
		// @Override
		// public void nativeMouseClicked(NativeMouseEvent e) {
		// //info.setText(NativeKeyEvent.getKeyText(e.getKeyCode()));
		// }
		//
		// @Override
		// public void nativeMousePressed(NativeMouseEvent e) {
		// System.out.println("Mouse Pressed: " +
		// NativeInputEvent.getModifiersText(e.getButton()));
		// //info.setText(NativeInputEvent.getModifiersText(e.getButton()));//getKeyText(e.getgetKeyCode()));
		// }
		//
		// @Override
		// public void nativeMouseReleased(NativeMouseEvent e) {
		// //info.setText(NativeMouseEvent.getKeyText(e.getKeyCode()));
		// }
		//
		// });

	}

	private void decideForKeyReleased(NativeKeyEvent e) {
		XPlayerController xPlayer = Main.xPlayersList
				.getXPlayerController(JavaFXTool.getIndexOfSelectedToggle(xPlayerSelected));

		int keyCode = e.getKeyCode();

		// OPEN FILE CHOOSER
		if (keyCode == NativeKeyEvent.VC_O && isShiftModifierPressed(e))
			xPlayer.openFileChooser();

		// PLAY/PAUSE
		else if (keyCode == NativeKeyEvent.VC_MEDIA_PLAY)
			xPlayer.reversePlayAndPause();

		// REPLAY
		else if (keyCode == NativeKeyEvent.VC_R && isShiftModifierPressed(e))
			xPlayer.replay();

		// SEEK BACKWARD
		else if (keyCode == NativeKeyEvent.VC_LEFT && isShiftModifierPressed(e))
			xPlayer.seek(-10);

		// SEEK FORWARD
		else if (keyCode == NativeKeyEvent.VC_RIGHT && isShiftModifierPressed(e))
			xPlayer.seek(10);

		// STOP
		else if (keyCode == NativeKeyEvent.VC_MEDIA_STOP)
			xPlayer.stop();

		// OPEN MEDIA ON FILE EXPLORER
		else if (keyCode == NativeKeyEvent.VC_S && isShiftModifierPressed(e))
			xPlayer.openAudioInExplorer();

	}

	private void decideForKeyPressed(NativeKeyEvent e) {
		XPlayerController xPlayer = Main.xPlayersList
				.getXPlayerController(JavaFXTool.getIndexOfSelectedToggle(xPlayerSelected));

		int keyCode = e.getKeyCode();

		// VOLUME +
		if (keyCode == NativeKeyEvent.VC_UP && isShiftModifierPressed(e))
			xPlayer.adjustVolume(2);

		// VOLUME -
		else if (keyCode == NativeKeyEvent.VC_DOWN && isShiftModifierPressed(e))
			xPlayer.adjustVolume(-2);

	}

	/**
	 * Detects if ShiftModifier is pressed
	 * 
	 * @param e
	 * @return True if yes , false if not
	 */
	private boolean isShiftModifierPressed(NativeKeyEvent e) {
		return e.getModifiers() == NativeKeyEvent.SHIFT_L_MASK || e.getModifiers() == NativeKeyEvent.SHIFT_R_MASK
				|| e.getModifiers() == NativeKeyEvent.SHIFT_MASK || e.getModifiers() == NativeKeyEvent.VC_SHIFT;
	}

	/**
	 * Restores all the settings that have to do with the category of the class
	 */
	public void restoreSettings() {

		// KeyBindingsActive
		keyBindingsActive.setSelected(false);

		// xPlayerSelected
		JavaFXTool.selectToggleOnIndex(xPlayerSelected, 0);
	}

	/**
	 * @return the keyBindingsActive
	 */
	public JFXCheckBox getKeyBindingsActive() {
		return keyBindingsActive;
	}

	/**
	 * @return the xPlayerSelected
	 */
	public ToggleGroup getxPlayerSelected() {
		return xPlayerSelected;
	}

}
