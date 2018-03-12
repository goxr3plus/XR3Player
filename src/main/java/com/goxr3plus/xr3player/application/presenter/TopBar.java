/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXTabPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;

/**
 * The Top bar of the application Window.
 *
 * @author GOXR3PLUS
 */
public class TopBar extends BorderPane {
	
	// ----------------------------------------------
	
	@FXML
	private Button restartButton;
	
	@FXML
	private Button minimize;
	
	@FXML
	private Button maxOrNormalize;
	
	@FXML
	private Button exitApplication;
	
	@FXML
	private MenuItem chooseBackground;
	
	@FXML
	private MenuItem resetBackground;
	
	@FXML
	private Label xr3Label;
	
	@FXML
	private ImageView highSpeed;
	
	@FXML
	private JFXTabPane jfxTabPane;
	
	@FXML
	private Tab mainModeTab;
	
	@FXML
	private Tab djModeTab;
	
	@FXML
	private Tab moviesModeTab;
	
	@FXML
	private Tab userModeTab;
	
	@FXML
	private Tab webModeTab;
	
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
		
		//---------------------------------------------------
		
		// restartButton
		restartButton.setOnAction(a -> {
			if (ActionTool.doQuestion("Restart", "Soore you want to restart the application?", restartButton, Main.window))
				Main.restartTheApplication(true);
		});
		
		// minimize
		minimize.setOnAction(ac -> Main.window.setIconified(true));
		
		// maximize_normalize
		maxOrNormalize.setOnAction(ac -> Main.scene.maximizeStage());
		
		// close
		exitApplication.setOnAction(ac -> Main.confirmApplicationExit());
		
		//chooseBackground
		chooseBackground.setOnAction(a -> Main.changeBackgroundImage());
		
		//resetBackground
		resetBackground.setOnAction(a -> Main.resetBackgroundImage());
		
		//----------------------------START: TABS---------------------------------
		
		//DummyStackPane technique to fix  Main Mode Disc Problems
		StackPane dummyStackPane = new StackPane();
		dummyStackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		mainModeTab.setOnSelectionChanged(l -> {
			if (mainModeTab.isSelected()) {
				//System.out.println("MainMode Selected")
				
				if (windowMode != WindowMode.MAINMODE && !Main.libraryMode.getTopSplitPane().getItems().contains(Main.playListModesSplitPane)) {
					
					//Update the djMode firstly
					Main.playListModesSplitPane.saveSplitPaneDivider();
					Main.djMode.saveBottomSplitPaneDivider();
					Main.djMode.getBottomSplitPane().getItems().clear();
					
					//Check firstly if the mode is upside down or not	
					Main.libraryMode.getTopSplitPane().getItems().remove(dummyStackPane);
					Main.libraryMode.getTopSplitPane().getItems().add(
							JavaFXTools.getIndexOfSelectedToggle(Main.settingsWindow.getGeneralSettingsController().getLibraryModeUpsideDown()) == 0 ? 1 : 0,
							Main.playListModesSplitPane);
					SplitPane.setResizableWithParent(Main.playListModesSplitPane, Boolean.FALSE);
					Main.libraryMode.updateTopSplitPaneDivider();
					//Main.libraryMode.getBottomSplitPane().getItems().clear()
					//Main.libraryMode.getBottomSplitPane().getItems().addAll(Main.multipleTabs, Main.xPlayersList.getXPlayerController(0))
					
					//Main.libraryMode.updateBottomSplitPaneDivider()
					//Main.multipleTabs.reverseSplitPaneItems()
					
					// Update window Mode
					windowMode = WindowMode.MAINMODE;
					
				}
				//if (!Main.specialJFXTabPane.getTabs().get(0).isSelected())
				Main.specialJFXTabPane.getSelectionModel().select(0);
			}
		});
		
		djModeTab.setOnSelectionChanged(l -> {
			if (djModeTab.isSelected()) {
				//System.out.println("djModeTab Selected")
				
				if (windowMode != WindowMode.DJMODE && Main.libraryMode.getTopSplitPane().getItems().contains(Main.playListModesSplitPane)) {
					
					//Update the libraryMode firstly
					Main.playListModesSplitPane.saveSplitPaneDivider();
					Main.libraryMode.saveTopSplitPaneDivider();
					//Main.libraryMode.saveBottomSplitPaneDivider()
					//Main.libraryMode.getBottomSplitPane().getItems().clear()	
					Main.libraryMode.getTopSplitPane().getItems().remove(Main.playListModesSplitPane);
					Main.libraryMode.getTopSplitPane().getItems().add(dummyStackPane);
					
					// Work
					Main.djMode.getBottomSplitPane().getItems().clear();
					Main.djMode.getBottomSplitPane().getItems().addAll(Main.treeManager, Main.playListModesSplitPane);
					SplitPane.setResizableWithParent(Main.playListModesSplitPane, Boolean.FALSE);
					SplitPane.setResizableWithParent(Main.treeManager, Boolean.FALSE);
					Main.djMode.updateBottomSplitPaneDivider();
					//Main.multipleTabs.reverseSplitPaneItems()
					
					// Update window Mode
					windowMode = WindowMode.DJMODE;
					
				}
				//if (!Main.specialJFXTabPane.getTabs().get(1).isSelected())
				Main.specialJFXTabPane.getSelectionModel().select(1);
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		moviesModeTab.setOnSelectionChanged(l -> {
			if (moviesModeTab.isSelected()) {
				//System.out.println("userModeTab Selected")
				
				//if (!Main.specialJFXTabPane.getTabs().get(2).isSelected())
				Main.specialJFXTabPane.getSelectionModel().select(2);
				
				// Update window Mode
				windowMode = WindowMode.MOVIEMODE;
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		userModeTab.setOnSelectionChanged(l -> {
			if (userModeTab.isSelected()) {
				//System.out.println("userModeTab Selected")
				
				//if (!Main.specialJFXTabPane.getTabs().get(2).isSelected())
				Main.specialJFXTabPane.getSelectionModel().select(3);
				
				// Update window Mode
				windowMode = WindowMode.USERMODE;
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		webModeTab.setOnSelectionChanged(l -> {
			if (webModeTab.isSelected()) {
				//System.out.println("webModeTab Selected")
				
				//if (!Main.specialJFXTabPane.getTabs().get(3).isSelected())
				Main.specialJFXTabPane.getSelectionModel().select(4);
				
				// Update window Mode
				windowMode = WindowMode.WEBMODE;
				
				//Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});
		
		//----------------------------END: TABS---------------------------------
		
	}
	
	/**
	 * Selects the tab from JFXTabPane in position {index}
	 * 
	 * @param index
	 */
	public void selectTab(int index) {
		jfxTabPane.getSelectionModel().select(index);
	}
	
	/**
	 * Checks if the tab from JFXTabPane in position {index} is selected
	 * 
	 * @param index
	 * @return True if the tab is selected or false if not
	 */
	public boolean isTabSelected(int index) {
		return jfxTabPane.getSelectionModel().isSelected(index);
	}
	
	/**
	 * Add the binding to the xr3Label
	 */
	public void addXR3LabelBinding() {
		
		// XR3Label
		xr3Label.setText(">- XR3Player Update " + Main.internalInformation.get("Version") + " (BETA) -<");
		
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
	public ImageView getHighSpeed() {
		return highSpeed;
	}
	
}
