package application.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author GOXR3PLUS
 *
 */
public class WelcomeScreen extends BorderPane {
	
	//---------------------------------------------
	
	@FXML
	private VBox screen1;
	
	@FXML
	private Button close;
	
	@FXML
	private JFXCheckBox showOnStartUp;
	
	// -------------------------------------------------------------
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/** Window **/
	private Stage window = new Stage();
	
	/**
	 * Constructor
	 */
	public WelcomeScreen() {
		
		// ------------------------------------FXMLLOADER--------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "WelcomeScreen.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
			
		}
		
		// --window
		window.setTitle("Welcome");
		window.initStyle(StageStyle.UTILITY);
		window.setResizable(false);
		
	}
	
	@FXML
	private void initialize() {
		
		// Scene
		window.setScene(new Scene(this));
		window.getScene().getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		
		//close
		close.setOnAction(a -> close());
		
		//dontShowAgain
		showOnStartUp.selectedProperty()
				.addListener((observable , oldValue , newValue) -> Main.applicationProperties.updateProperty("Show-Welcome-Screen", String.valueOf(newValue.booleanValue())));
		
	}
	
	/**
	 * Shows the Window
	 */
	public void show() {
		window.sizeToScene();
		window.show();
	}
	
	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

	/**
	 * @return the showOnStartUp
	 */
	public JFXCheckBox getShowOnStartUp() {
		return showOnStartUp;
	}

	
}
