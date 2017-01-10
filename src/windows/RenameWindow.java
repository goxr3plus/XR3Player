/*
 * 
 */
package windows;

import java.io.IOException;

import org.controlsfx.control.Notifications;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

/**
 * The Class RenameWindow.
 */
public class RenameWindow extends HBox {
	
	public TextField inputField = TextFields.createClearableTextField();
	
	@FXML
    private Label charsField;

    @FXML
    private Button okButton;

    @FXML
    private Button closeButton;	
	// ----------------
	
	/**
	 * 
	 */
	public Stage window = new Stage();
	
	/** The x pressed. */
	private boolean xPressed = false;
	
	/** The not allow. */
	String[] notAllow = new String[]{ "/" , "\\" , ":" , "*" , "?" , "\"" , "<" , ">" , "|" , "'" };
	
	/**
	 * Constructor
	 */
	public RenameWindow() {
		
		// Window
		window.setTitle("Rename Window");
		window.setWidth(435);
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.TRANSPARENT);
		window.getIcons().add(InfoTool.getImageFromDocuments("icon.png"));
		window.centerOnScreen();
		window.setOnCloseRequest(ev -> xPressed = true);
		window.setAlwaysOnTop(true);
		
		// ----------------------------------FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "RenameWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// ----------------------------------Scene
		window.setScene(new Scene(this, Color.TRANSPARENT));
		getScene().getStylesheets()
		        .add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());
		getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE) {
				xPressed = true;
				window.close();
			}
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
		inputField.setPrefSize(290, 32);
		inputField
		        .setTooltip(new Tooltip("Not allowed:(<) (>) (:) (\") (/) (\\) (|) (?) (*) (') \n **Escape to Exit**"));
		inputField.setPromptText("Type Here...");
		inputField.setStyle("-fx-font-weight:bold; -fx-font-size:14;");
		
		inputField.textProperty().addListener((observable , oldValue , newValue) -> {
			
			if (newValue != null) {
				
				// Allow until 150 characters
				if (newValue.length() > 150)
					inputField.setText(newValue.substring(0, 150));
				
				// Strict Mode
				for (String character : notAllow)
					if (newValue.contains(character))
						inputField.setText(newValue.replace(character, ""));
			}
		});
		
		// Custom Event Handler
		EventHandler<ActionEvent> myHandler = e -> {
			
			// can pass?
			if (!inputField.getText().trim().isEmpty())
				window.close();
			else
				Notifications.create().text("You have to type something..").showWarning();
			
		};
		inputField.setOnAction(myHandler);
		getChildren().add(0, inputField);
		
		// okButton
		okButton.setOnAction(myHandler);

		// closeButton
		closeButton.setOnAction(action -> {
			xPressed = true;
			window.close();
		});
		
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
	 * Checks if is x pressed.
	 *
	 * @return true, if is x pressed
	 */
	public boolean isXPressed() {
		return xPressed;
	}
	
	/**
	 * Show Window with the given parameters.
	 *
	 * @param text the text
	 * @param node the node
	 */
	public void show(String text , Node node) {
		
		// Auto Calculate the position
		Bounds bounds = node.localToScreen(node.getBoundsInLocal());
		show(text, bounds.getMinX() + 5, bounds.getMaxY());
	}
	
	/**
	 * Show Window with the given parameters.
	 *
	 * @param text the text
	 * @param x the x
	 * @param y the y
	 */
	public void show(String text , double x , double y) {
		
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
		
		inputField.setText(text);
		inputField.end();
		xPressed = false;
		window.show();
	}
	
	/**
	 * @return Whether or not this {@code Stage} is showing (that is, open on
	 *         the
	 *         user's system). The Stage might be "showing", yet the user might
	 *         not
	 *         be able to see it due to the Stage being rendered behind another
	 *         window
	 *         or due to the Stage being positioned off the monitor.
	 * 
	 *
	 * @defaultValue false
	 */
	public ReadOnlyBooleanProperty showingProperty() {
		return window.showingProperty();
	}
	
	/**
	 * @return Whether or not this {@code Stage} is showing (that is, open on
	 *         the
	 *         user's system). The Stage might be "showing", yet the user might
	 *         not
	 *         be able to see it due to the Stage being rendered behind another
	 *         window
	 *         or due to the Stage being positioned off the monitor.
	 * 
	 */
	public boolean isShowing() {
		return showingProperty().get();
	}
	
}
