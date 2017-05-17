/**
 * 
 */
package application.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import smartcontroller.Genre;
import smartcontroller.SmartController;

/**
 * @author GOXR3PLUS
 *
 */
public class SearchWindow extends BorderPane {

    //-----------------------------------------------------

    private SmartController smartController = new SmartController(Genre.SEARCHWINDOW, "Searching any Media", null);
    // -------------------------------------------------------------

    /** The logger. */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /** The Window */
    private Stage window = new Stage();

    /**
     * Constructor.
     */
    public SearchWindow() {

	// ------------------------------------FXMLLOADER ----------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SearchWindow.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}

	//Prepare the Window
	window.setTitle("Search any Media ever imported inside the XR3Player Application");
	window.initStyle(StageStyle.UTILITY);
	window.setScene(new Scene(this));
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
	setCenter(smartController);
    }

    /** Show the Window */
    public void show() {
	if (!window.isShowing())
	    window.show();
	else
	    window.requestFocus();
    }

    /** Hides/Closes the Window */
    public void close() {
	window.hide();
    }

    /**
     * Returns the Window of the Class
     * 
     * @return the window
     */
    public Stage getWindow() {
	return window;
    }

    /**
     * @return the smartController
     */
    public SmartController getSmartController() {
	return smartController;
    }

}
