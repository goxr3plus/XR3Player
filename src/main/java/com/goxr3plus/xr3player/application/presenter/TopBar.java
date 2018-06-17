/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The Top bar of the application Window.
 *
 * @author GOXR3PLUS
 */
public class TopBar extends BorderPane {
	
	// ----------------------------------------------
	
	@FXML
	private Label xr3Label;
	
	@FXML
	private FontIcon highGraphics;
	
	@FXML
	private JFXButton showHideSideBar;
	
	@FXML
	private TextField searchField;
	
	@FXML
	private JFXTabPane jfxTabPane;
	
	@FXML
	private Tab mainModeTab;
	
	@FXML
	private Tab userModeTab;
	
	@FXML
	private Tab webModeTab;
	
	@FXML
	private Tab moviesModeTab;
	
	// ----------------------------------------------
	
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * The current Window Mode
	 * 
	 * @SEE WindowMode
	 */
	private WindowMode windowMode = WindowMode.MAINMODE;
	
	/**
	 * WindowMode.
	 *
	 * @author GOXR3PLUS
	 */
	public enum WindowMode {
		
		/**
		 * The Window is on LibraryMode
		 */
		MAINMODE,
		
		/**
		 * The window is on DJMode
		 */
		DJMODE,
		
		/**
		 * The window is on user settings mode
		 */
		USERMODE,
		
		/**
		 * The window is on web browser mode
		 */
		WEBMODE,
		
		/**
		 * The window is on movie mode
		 */
		MOVIEMODE;
		
	}
	
	/**
	 * Constructor.
	 */
	public TopBar() {
		
		//---------------------FXML LOADER---------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TopBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
		}
	}
	
	/**
	 * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {
		
		//Root
		this.setRight(new CloseAppBox());
		
		//----------------------------START: TABS---------------------------------
		
		//DummyStackPane technique to fix  Main Mode Disc Problems
		StackPane dummyStackPane = new StackPane();
		dummyStackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		mainModeTab.setOnSelectionChanged(l -> {
			if (mainModeTab.isSelected()) {
				
				if (windowMode != WindowMode.MAINMODE) {// && !Main.libraryMode.getTopSplitPane().getItems().contains(Main.playListModesSplitPane)) {
					
					// Update window Mode
					windowMode = WindowMode.MAINMODE;
					
				}
				Main.specialJFXTabPane.getSelectionModel().select(0);
			}
			
		});
		
		moviesModeTab.setOnSelectionChanged(l -> {
			if (moviesModeTab.isSelected()) {
				
				Main.specialJFXTabPane.getSelectionModel().select(1);
				Main.sideBar.getMoviesToggle().setSelected(true);
				
				// Update window Mode
				windowMode = WindowMode.MOVIEMODE;
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		userModeTab.setOnSelectionChanged(l -> {
			if (userModeTab.isSelected()) {
				
				Main.specialJFXTabPane.getSelectionModel().select(2);
				Main.sideBar.getUserInfoToggle().setSelected(true);
				
				// Update window Mode
				windowMode = WindowMode.USERMODE;
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		webModeTab.setOnSelectionChanged(l -> {
			if (webModeTab.isSelected()) {
				
				Main.specialJFXTabPane.getSelectionModel().select(3);
				Main.sideBar.getBrowserToggle().setSelected(true);
				
				// Update window Mode
				windowMode = WindowMode.WEBMODE;
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		//----------------------------END: TABS---------------------------------
		
		//showHideSideBar
		showHideSideBar.setOnAction(a -> {
			Main.bottomBar.getShowHideSideBar().setSelected(!Main.bottomBar.getShowHideSideBar().isSelected());
		});
	}
	
	/**
	 * Selects the tab from JFXTabPane in position {index}
	 * 
	 * @param index
	 */
	public void selectTab(Tab tab) {
		jfxTabPane.getSelectionModel().select(tab);
	}
	
	/**
	 * Checks if the tab from JFXTabPane in position {index} is selected
	 * 
	 * @param index
	 * @return True if the tab is selected or false if not
	 */
	public boolean isTabSelected(Tab tab) {
		return tab.isSelected();
	}
	
	/**
	 * Add the binding to the xr3Label
	 */
	public void addXR3LabelBinding() {
		
		// XR3Label
		xr3Label.setText("XR3Player V." + Main.APPLICATION_VERSION);
		
		//		xr3Label.textProperty().bind(Bindings.createStringBinding(() -> MessageFormat.format(">-XR3Player (BETA) V.{0} -<  Width=[{1}],Height=[{2}]",
		//				Main.internalInformation.get("Version"), Main.window.getWidth(), Main.window.getHeight()), Main.window.widthProperty(), Main.window.heightProperty()));
		
	}
	
	/**
	 * @return the xr3Label
	 */
	public Label getXr3Label() {
		return xr3Label;
	}
	
	/**
	 * @param xr3Label
	 *            the xr3Label to set
	 */
	public void setXr3Label(Label xr3Label) {
		this.xr3Label = xr3Label;
	}
	
	/**
	 * @return the windowMode
	 */
	public WindowMode getWindowMode() {
		return windowMode;
	}
	
	/**
	 * @return the highSpeed
	 */
	public FontIcon getHighGraphics() {
		return highGraphics;
	}
	
	/**
	 * @return the webModeTab
	 */
	public Tab getWebModeTab() {
		return webModeTab;
	}
	
	/**
	 * @return the mainModeTab
	 */
	public Tab getMainModeTab() {
		return mainModeTab;
	}
	
	/**
	 * @return the moviesModeTab
	 */
	public Tab getMoviesModeTab() {
		return moviesModeTab;
	}
	
	/**
	 * @return the userModeTab
	 */
	public Tab getUserModeTab() {
		return userModeTab;
	}
	
	/**
	 * @return the searchField
	 */
	public TextField getSearchField() {
		return searchField;
	}
	
}
