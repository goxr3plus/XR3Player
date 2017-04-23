/**
 * 
 */
package webBrowser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * @author GOXR3PLUS
 *
 */
public class WebBrowserController extends StackPane {

    /** The logger. */
    private final Logger logger = Logger.getLogger(getClass().getName());

    //------------------------------------------------------------

    @FXML
    private TabPane tabPane;

    @FXML
    private JFXButton addTab;

    @FXML
    private VBox errorPane;

    @FXML
    private Button tryAgain;

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
     * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
     */
    @FXML
    private void initialize() {

	//tryAgain
	tryAgain.setOnAction(a -> checkForInternetConnection());

	//tabPane
	tabPane.getTabs().clear();
	createNewTab("coz");

	//addTab
	addTab.setOnAction(a -> {

	    //Check tabs number
	    if (tabPane.getTabs().size() >= 4) {
		JFXDialog dialog = new JFXDialog();
		//Show Message
		Alert alert = new Alert(AlertType.WARNING,
			"Currently only 4 tabs are allowed , for performance reasons... \n\n If you can hack it without decompiling the code i will give you 5$ dollars via paypal ;)");
		alert.initOwner(Main.window);
		alert.initOwner(Main.window);
		alert.showAndWait();

		return;
	    }

	    //Create
	    createNewTab();
	});

	checkForInternetConnection();
    }

    /**
     * Creates a new tab for the webbrowser ->Directing to a specific website [[SuppressWarningsSpartan]]
     * 
     * @param webSite
     */
    public void createNewTab(String... webSite) {
	//Add new Tab
	Tab tab = new Tab("");
	WebBrowserTabController webBrowserTab = new WebBrowserTabController(tab, webSite.length == 0 ? null : webSite[0]);
	tab.setOnCloseRequest(c -> {

	    //Check the tabs number
	    if (tabPane.getTabs().size() == 1)
		createNewTab();

	    // Delete cache for navigate back
	    webBrowserTab.webEngine.load("about:blank");
	    webBrowserTab.webEngine.getLoadWorker().exceptionProperty().addListener(error -> {
		ActionTool.showNotification("Error Occured", "Trying to connect to a website error occured:\n\t["
			+ webBrowserTab.webEngine.getLoadWorker().getException().getMessage() + "]\nMaybe you don't have internet connection.",
			Duration.seconds(15), NotificationType.ERROR);

		checkForInternetConnection();
	    });

	    //Delete cookies  Experimental!!! 
	    //java.net.CookieHandler.setDefault(new java.net.CookieManager())

	});

	//Add the tab
	tabPane.getTabs().add(tab);
	//System.out.println(Arrays.asList(webSite))

    }

    /**
     * @return the errorPane
     */
    public VBox getErrorPane() {
	return errorPane;
    }

    /**
     * Checks for internet connection
     */
    private void checkForInternetConnection() {
	//Check for internet connection
	Thread thread = new Thread(() -> {
	    boolean hasInternet = InfoTool.isReachableByPing("https://www.google.com");
	    Platform.runLater(() -> errorPane.setVisible(!hasInternet));
	}, "Internet Connection Tester Thread");
	thread.setDaemon(true);
	thread.start();
    }

}
