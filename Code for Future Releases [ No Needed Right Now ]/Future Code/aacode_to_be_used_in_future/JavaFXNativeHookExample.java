package aacode_to_be_used_in_future;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author barraq
 */
public class JavaFXNativeHookExample extends Application {

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

	try {
	    GlobalScreen.registerNativeHook();
	} catch (NativeHookException ex) {
	    System.err.println("There was a problem registering the native hook.");
	    System.err.println(ex.getMessage());
	    System.exit(1);
	}

	// Construct the example object and initialze native hook.
	GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
	    public void nativeKeyPressed(NativeKeyEvent e) {
		info.setText(NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
		    try {
			GlobalScreen.unregisterNativeHook();
		    } catch (NativeHookException ex) {
			ex.printStackTrace();
		    }
		}
	    }

	    public void nativeKeyReleased(NativeKeyEvent e) {
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	    }

	    public void nativeKeyTyped(NativeKeyEvent e) {
		System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	    }
	});

	Stage widget = new Stage();
	widget.setTitle("JavaFXNativeHook");
	widget.initStyle(StageStyle.UTILITY);
	widget.initModality(Modality.APPLICATION_MODAL);
	widget.setScene(scene);
	widget.show();
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