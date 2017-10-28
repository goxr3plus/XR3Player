/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.windows;

import java.io.IOException;

import org.controlsfx.control.textfield.TextFields;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;

/**
 * The Class RenameWindow.
 */
public class RenameWindow extends VBox {
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private Label charsField;
	
	@FXML
	private Button okButton;
	
	@FXML
	private Button closeButton;
	
	// ----------------
	
	/**
	 * The field inside the user writes the text
	 */
	public TextField inputField = TextFields.createClearableTextField();
	
	// Custom Event Handler
	EventHandler<ActionEvent> myHandler = e -> {
		
		// can pass?
		if (!inputField.getText().trim().isEmpty())
			close(true);
		else
			ActionTool.showNotification("Message", "You have to type something..", Duration.millis(1500), NotificationType.WARNING);
		
	};
	
	/** The window */
	private Stage window = new Stage();
	
	/** If it was accepted */
	private boolean accepted = false;
	
	/** The not allow. */
	String[] notAllow = new String[]{ "/" , "\\" , ":" , "*" , "?" , "\"" , "<" , ">" , "|" , "'" , "." };
	
	/**
	 * Constructor
	 */
	public RenameWindow() {
		
		// Window
		window.setTitle("Rename Window");
		window.setMinHeight(100);
		window.setMinWidth(300);
		window.setWidth(440);
		window.setHeight(80);
		//window.initModality(Modality.);
		window.initStyle(StageStyle.TRANSPARENT);
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		window.centerOnScreen();
		window.setOnCloseRequest(ev -> close(false));
		window.setAlwaysOnTop(true);
		
		// ----------------------------------FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "RenameWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// ----------------------------------Scene
		window.setScene(new Scene(this, Color.TRANSPARENT));
		//getScene().getStylesheets()
		//	.add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm())
		getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				close(false);
		});
		window.focusedProperty().addListener((observable , oldValue , newValue) -> {
			if (!newValue && window.isShowing())
				close(false);
		});
		
	}
	
	/**
	 * Called as soon as .fxml has been initialized
	 */
	@FXML
	private void initialize() {
		
		// CharsField
		charsField.textProperty().bind(inputField.textProperty().length().asString());
		
		// inputField
		getChildren().add(inputField);
		inputField.setMinSize(420, 32);
		inputField.setTooltip(new Tooltip("Not allowed:(<) (>) (:) (\") (/) (\\) (|) (?) (*) (') (.) \n **Press Escape to Exit**"));
		inputField.setPromptText("Type Here...");
		inputField.setStyle("-fx-font-weight:bold; -fx-font-size:14;");
		//inputField.setPrefColumnCount(200)
		//inputField.prefColumnCountProperty().bind(inputField.textProperty().length().add(1));
		inputField.textProperty().addListener((observable , oldValue , newValue) -> {
			//Check newValue
			if (newValue != null) {
				
				// Allow until 200 characters
				if (newValue.length() > 200)
					inputField.setText(newValue.substring(0, 200));
				
				// Strict Mode
				for (String character : notAllow)
					if (newValue.contains(character))
						inputField.setText(newValue.replace(character, ""));
			}
		});
		//---prefColumnCountProperty
		//	inputField.prefColumnCountProperty().addListener((observable, oldValue, newValue) -> {
		//	    if (inputField.getWidth() < 450)
		//		window.setWidth(inputField.getWidth() + 50);
		//	});
		inputField.setOnAction(myHandler);
		
		// okButton
		okButton.setOnAction(myHandler);
		
		// closeButton
		closeButton.setOnAction(action -> close(false));
		
		//window.show();
	}
	
	/**
	 * get the input that connectedUser Typed.
	 *
	 * @return the user input
	 */
	public String getUserInput() {
		return inputField.getText();
	}
	
	/**
	 * Checks if it was cancelled
	 *
	 * @return True if it was cancelled , false if not
	 */
	public boolean wasAccepted() {
		return accepted;
	}
	
	/**
	 * Close the Window.
	 *
	 * @param accepted1
	 *            True if accepted , False if not
	 */
	public void close(boolean accepted) {
		//	System.out.println("Rename Window Close called with accepted := " + accepted);
		this.accepted = accepted;
		window.close();
	}
	
	/**
	 * Show Window with the given parameters.
	 *
	 * @param text
	 *            the text
	 * @param n
	 *            the node
	 * @param title
	 *            The text if the title Label
	 */
	public void show(String text , Node n , String title) {
		
		// Auto Calculate the position
		Bounds bounds = n.localToScreen(n.getBoundsInLocal());
		//show(text, bounds.getMinX() + 5, bounds.getMaxY(), title);
		//System.out.println(bounds.getMinX() + " , " + getWidth() + " , " + bounds.getWidth() / 2);
		show(text, bounds.getMinX() - 440 / 2 + bounds.getWidth() / 2, bounds.getMaxY(), title);
		
		//System.out.println(bounds.getMinX() + " , " + getWidth() + " , " + bounds.getWidth() / 2);
	}
	
	/**
	 * Show Window with the given parameters.
	 *
	 * @param text
	 *            the text
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param title
	 *            The text if the title Label
	 */
	public void show(String text , double x , double y , String title) {
		
		titleLabel.setText(title);
		inputField.setText(text);
		accepted = true;
		
		//Set once
		window.setX(x);
		window.setY(y);
		
		window.show();
		
		//Set it again
		if (x <= -1 && y <= -1)
			window.centerOnScreen();
		else {
			if (x + getWidth() > InfoTool.getScreenWidth())
				x = InfoTool.getScreenWidth() - getWidth();
			else if (x < 0)
				x = 0;
			
			if (y + getHeight() > InfoTool.getScreenHeight())
				y = InfoTool.getScreenHeight() - getHeight();
			else if (y < 0)
				y = 0;
			
			window.setX(x);
			window.setY(y);
		}
		
		//	
		inputField.requestFocus();
		inputField.end();
	}
	
	/**
	 * @return Whether or not this {@code Stage} is showing (that is, open on the user's system). The Stage might be "showing", yet the user might not
	 *         be able to see it due to the Stage being rendered behind another window or due to the Stage being positioned off the monitor.
	 * 
	 *
	 * @defaultValue false
	 */
	public ReadOnlyBooleanProperty showingProperty() {
		return window.showingProperty();
	}
	
	/**
	 * @return Whether or not this {@code Stage} is showing (that is, open on the user's system). The Stage might be "showing", yet the user might not
	 *         be able to see it due to the Stage being rendered behind another window or due to the Stage being positioned off the monitor.
	 * 
	 */
	public boolean isShowing() {
		return showingProperty().get();
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
}
