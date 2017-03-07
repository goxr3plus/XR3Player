package snapshot;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFXNativeHookExample
 *
 */
public class JavaFXNativeHookExample extends Application {

	SnapshotWindowController captureWindow = new SnapshotWindowController();

	@Override
	public void start(final Stage primaryStage) {
		final StackPane root = new StackPane();
		final Scene scene = new Scene(root, 300, 250);

		// Clear previous logging configurations.
		LogManager.getLogManager().reset();

		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		final Text info = new Text();
		root.getChildren().add(info);

		// Add Global KeyListener for the Operating System
		try {
			GlobalScreen.setEventDispatcher(new SwingDispatchService());
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// Listen for all key events
		GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			public void nativeKeyPressed(NativeKeyEvent e) {
				info.setText(NativeKeyEvent.getKeyText(e.getKeyCode()));

				System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
				if (e.getKeyCode() == NativeKeyEvent.VC_ALT) {
					System.out.println("Showing Capture Window");
					captureWindow.prepareForCapture();

				}
			}

			public void nativeKeyReleased(NativeKeyEvent e) {
				System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
				if (e.getKeyCode() == NativeKeyEvent.VC_ALT) {
					Platform.runLater(() -> captureWindow.window.hide());

					System.out.println("Hided Capture Window");
				}
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
				info.setText(NativeInputEvent.getModifiersText(e.getButton()));//getKeyText(e.getgetKeyCode()));
			}

			@Override
			public void nativeMouseReleased(NativeMouseEvent e) {
				//info.setText(NativeMouseEvent.getKeyText(e.getKeyCode()));
			}
			
		});

		// Create the Primary Window
		Stage primaryWindow = new Stage();
		primaryWindow.setTitle("JavaFXNativeHook");
		primaryWindow.initStyle(StageStyle.UTILITY);
		primaryWindow.initModality(Modality.APPLICATION_MODAL);
		primaryWindow.setScene(scene);
		primaryWindow.show();
		
		Platform.setImplicitExit(false);
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		/* fix for osx */
		System.setProperty("javafx.macosx.embedded", "true");
		java.awt.Toolkit.getDefaultToolkit();

		/*
		 * In OSX you must activate accessibility.
		 * 
		 * To turn it on, type this in Terminal: > sudo touch
		 * /private/var/db/.AccessibilityAPIEnabled
		 * 
		 * To then disable it, type this: > sudo rm
		 * /private/var/db/.AccessibilityAPIEnabled
		 */

		/* start the app */
		launch(args);

	}
}