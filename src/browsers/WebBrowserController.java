/**
 * 
 */
package browsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class WebBrowserController extends BorderPane {

    /** The logger. */
    private final Logger logger = Logger.getLogger(getClass().getName());

    //------------------------------------------------------------

    @FXML
    private TabPane tabPane;

    @FXML
    private JFXButton addTab;

    // -------------------------------------------------------------

    /**
     * Constructor
     */
    public WebBrowserController() {
	// ------------------------------------FXMLLOADER ----------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "WebBrowserController.fxml"));
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

	//tabPane
	tabPane.getTabs().clear();
	Tab tab = new Tab("");
	new WebBrowserTabController(tab);
	tabPane.getTabs().add(tab);

	//addTab
	addTab.setOnAction(a -> {

	    //Check tabs number
	    if (tabPane.getTabs().size() >= 4) {
		//Show Message
		Alert alert = new Alert(AlertType.ERROR,
			"Hello :)  Currently only 4 tabs are allowed , for performance reasons...");
		alert.initOwner(Main.window);
		alert.initOwner(Main.window);
		alert.showAndWait();

		return;
	    }

	    //Add new Tab
	    Tab t = new Tab("");
	    new WebBrowserTabController(t);
	    tabPane.getTabs().add(t);

	});
    }

}
