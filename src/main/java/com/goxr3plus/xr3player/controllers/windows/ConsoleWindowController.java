/**
 * OOUUUUUUUUU PARTYYYYYYYYYYYYYYYYYYYYY!
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


/**
 * @author GOXR3PLUS
 *
 */
public class ConsoleWindowController extends StackPane {

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private BorderPane borderPane;

//	@FXML
//	private JFXTextField commandTextField;

	@FXML
	private Button go;

	@FXML
	private Button close;

	@FXML
	private Button help;

	// --------------------------------------------------------

	private final InlineCssTextArea cssTextArea = new InlineCssTextArea();

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/** The Window */
	private final Stage window = new Stage();

	/**
	 * The Speech Recognition of the Application
	 */
	// private SpeechRecognition speechRecognition = new SpeechRecognition();

	/**
	 * @author GOXR3PLUS
	 *
	 */
	public enum ConsoleTab {
		CONSOLE, SPEECH_RECOGNITION;
	}

	/**
	 * Constructor
	 */
	public ConsoleWindowController() {

		// ------------------------------------FXMLLOADER
		final FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.WINDOW_FXMLS + "ConsoleWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

		window.setTitle("XR3Player Console");
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		this.focusedProperty().addListener((observable, oldValue, newValue) -> {
//			if (window.isFocused())
//				commandTextField.requestFocus();
		});
		window.getScene().setOnKeyReleased(k -> {
			if (k.getCode() == KeyCode.ESCAPE)
				window.close();
		});

	}

	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {

		// -- cssTextArea
		cssTextArea.setEditable(false);
		cssTextArea.setFocusTraversable(false);
		cssTextArea.getStyleClass().add("inline-css-text-area");

		final String t = "Click or type Help to open the app manual\n";
		cssTextArea.appendText(t);
		cssTextArea.setStyle(0, t.length(), "-fx-fill:yellow; -fx-font-weight:bold; -fx-font-size:15;");

		final VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(cssTextArea);
		vsPane.setMaxWidth(Double.MAX_VALUE);
		vsPane.setMaxHeight(Double.MAX_VALUE);
		borderPane.setCenter(vsPane);

		// commandTextField
//		commandTextField.setOnAction(a -> procceedCommand(commandTextField.getText()));

		// go
//		go.setOnAction(a -> procceedCommand(commandTextField.getText()));
//		go.disableProperty().bind(commandTextField.textProperty().isEmpty());

		// close
		close.setOnAction(a -> window.close());

		// help
		help.setOnAction(a -> IOAction
				.openFileInEditor(IOInfo.getBasePathForClass(ConsoleWindowController.class) + "XR3Player Manual.pdf"));

		// Add SpeechRecognition
		// tabPane.getTabs().get(1).setContent(this.speechRecognition);
	}

	Pattern pattern1 = Pattern.compile("player:[-+]?\\d+:\\w+");
	Pattern pattern2 = Pattern.compile("player:[-+]?\\d+:\\w+:[-+]?\\d+");
	Pattern pattern3 = Pattern.compile("player:[-+]?\\d+:\\w+:[-+]?\\d+:[smh]");

	/**
	 * This method is procceeding the commands for the ConsoleWindow
	 * 
	 * @param command
	 */
	public void procceedCommand(String command) {
		boolean success = false;

		// Print something to the user
		final String message = "Procceeding...->[" + command + "]";

		// Proceed the command
		command = command.trim().replaceAll("[ ]+", ":").toLowerCase();

		// Check if it is null
		if (command.isEmpty()) {
			AlertTool.showNotification("Message", "You have to type something...", Duration.millis(1500),
					NotificationType.WARNING);
			return;
		}

		// Player command?
		if (command.startsWith("player")) {
			System.out.println(command);
			final String[] array = command.split(":");

			// It must be 3 [format = "player"+"key"+"action"]
			if (array.length == 3 && pattern1.matcher(command).matches()) {
				System.out.println("Yes it does match format [\"player\"+\"key\"+\"action\"] ");
				final XPlayerController player = Main.xPlayersList.getXPlayerController(Integer.parseInt(array[1]));

				final String action = array[2];
				if ("stop".equals(action)) { // stop
					player.stop();
					success = true;
				} else if ("play".equals(action)) { // play
					player.playOrReplay();
					success = true;
				} else if ("resume".equals(action)) { // resume
					player.resume();
					success = true;
				} else if ("pause".equals(action)) { // pause
					player.pause();
					success = true;
				} else if ("replay".equals(action)) { // replay
					player.replay();
					success = true;
				} else if ("mute".equals(action)) { // replay
					player.setMute(true);
					success = true;
				} else if ("unmute".equals(action)) { // replay
					player.setMute(false);
					success = true;
				} else if ("open".equals(action)) { // open file chooser
					player.openFileChooser();
					success = true;
				}

			}

			// It must be 4 [format = "player"+"key"+"action"+"value"]
			else if (array.length == 4 && pattern2.matcher(command).matches()) {
				System.out.println("Yes it does match \"player\"+\"key\"+\"action\"+\"value\"] ");
				final XPlayerController player = Main.xPlayersList.getXPlayerController(Integer.parseInt(array[1]));

				final String action = array[2];
				final int value = Integer.parseInt(array[3]);

				if ("volume".equals(action)) { // adjust volume
					player.adjustVolume(value);
					success = true;
				} else if ("setvolume".equals(action)) { // setvolume
					player.setVolume(value);
					success = true;
				}
			}

			// It must be 4 [format = "player"+"key"+"action"+"value"+"s|m|h"]
			else if (array.length == 5 && pattern3.matcher(command).matches()) {
				System.out.println("Yes it does match \"player\"+\"key\"+\"action\"+\"value\"+\"s|m|h\"] ");
				final XPlayerController player = Main.xPlayersList.getXPlayerController(Integer.parseInt(array[1]));

				final String action = array[2];
				int value = Integer.parseInt(array[3]);

				if ("seek".equals(action)) { // seek
					// by default it will be seconds
					if ("m".equals(array[4])) // make it minutes
						value = value * 60;
					else if ("h".equals(array[4])) // make it hours
						value = value * 60 * 60;

					// Go seek
					player.seek(value);

					success = true;
				} else if ("seekto".equals(action)) { // goto a specific moment
					// by default it will be seconds
					if ("m".equals(array[4])) // make it minutes
						value = value * 60;
					else if ("h".equals(array[4])) // make it hours
						value = value * 60 * 60;

					// Then seek
					player.seekTo(value);

					success = true;
				}
			}

		} else if ("clear".equals(command) || "cls".equals(command)) { // Clear
			cssTextArea.clear();
			cssTextArea.clear();
			success = true;
		} else if ("help".equals(command)) { // help
			IOAction.openFileInEditor(IOInfo.getBasePathForClass(ConsoleWindowController.class) + "XR3Player Manual.pdf");
			success = true;
		} else if ("exit".equals(command) || "close".equals(command)) // close console
			window.close();

		System.out.println(command);

		// -----------------------------Message for the
		// user-------------------------------------------
		cssTextArea.appendText(message);
		cssTextArea.setStyle(cssTextArea.getText().length() - message.length(), cssTextArea.getLength(),
				"-fx-font-weight:bold; -fx-font-size:14; -fx-fill:white;");

		final String result = "  " + (success ? "Succeeded" : "Error");
		cssTextArea.appendText(result);
		cssTextArea.setStyle(cssTextArea.getText().length() - result.length() + 2, cssTextArea.getLength(),
				"-fx-font-weight:bold; -fx-font-size:14; -fx-fill:" + (success ? "green" : "red") + ";");
		cssTextArea.appendText("\n");

		cssTextArea.moveTo(cssTextArea.getLength());
		cssTextArea.requestFollowCaret();

	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

//	/**
//	 * @return the speechRecognition
//	 */
//	public SpeechRecognition getSpeechRecognition() {
//		return speechRecognition;
//	}

	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}

	/**
	 * Show the Window
	 */
	public void show() {
		if (!window.isShowing())
			window.show();
		else
			window.requestFocus();
	}

	/**
	 * Shows the Window.
	 * 
	 * @param consoleTab The default tab you want to be selected when the window is
	 *                    shown
	 */
	public void showWindow(final ConsoleTab consoleTab) {

		if (consoleTab == ConsoleTab.CONSOLE) {
			tabPane.getSelectionModel().select(0);
		} else if (consoleTab == ConsoleTab.SPEECH_RECOGNITION) {
			tabPane.getSelectionModel().select(1);

		}

		window.show();
	}

}
