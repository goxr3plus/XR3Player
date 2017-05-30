/**
 * 
 */
package application.webbrowser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

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
		
		//tabPane
		tabPane.getTabs().clear();
		createAndAddNewTab();
		
		//addTab
		addTab.setOnAction(a -> createAndAddNewTab());
	}
	
	/**
	 * Creates a new tab for the web browser ->Directing to a specific web site
	 * [[SuppressWarningsSpartan]]
	 * 
	 * @param webSite
	 */
	public WebBrowserTabController createAndAddNewTab(String... webSite) {
		
		//Create
		WebBrowserTabController webBrowserTab = createNewTab(webSite);
		
		//Add the tab
		tabPane.getTabs().add(webBrowserTab.getTab());
		
		return webBrowserTab;
	}
	
	/**
	 * Creates a new tab for the web browser ->Directing to a specific web site
	 * [[SuppressWarningsSpartan]]
	 * 
	 * @param webSite
	 */
	public WebBrowserTabController createNewTab(String... webSite) {
		
		//Create
		Tab tab = new Tab("");
		WebBrowserTabController webBrowserTab = new WebBrowserTabController(this, tab, webSite.length == 0 ? null : webSite[0]);
		tab.setOnCloseRequest(c -> {
			
			//Check the tabs number
			if (tabPane.getTabs().size() == 1)
				createAndAddNewTab();
			
			// Delete cache for navigate back
			webBrowserTab.webEngine.load("about:blank");
			
			//Delete cookies  Experimental!!! 
			//java.net.CookieHandler.setDefault(new java.net.CookieManager())
			
		});
		
		return webBrowserTab;
	}
	
	/**
	 * @return the tabPane
	 */
	public TabPane getTabPane() {
		return tabPane;
	}
	
}
