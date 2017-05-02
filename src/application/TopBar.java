/*
 * 
 */
package application;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXTabPane;

import customnodes.SystemMonitor;
import customnodes.SystemMonitor.Monitor;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import tools.ActionTool;
import tools.InfoTool;

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
    private JFXTabPane jfxTabPane;

    @FXML
    private Tab mainModeTab;

    @FXML
    private Tab djModeTab;

    @FXML
    private Tab userModeTab;

    @FXML
    private Tab webModeTab;

    @FXML
    private Button restartButton;

    @FXML
    private Button minimize;

    @FXML
    private Button maxOrNormalize;

    @FXML
    private Button exitApplication;

    @FXML
    private Button changeBackground;

    @FXML
    private StackPane cpuStackPane;

    @FXML
    private Label cpuLabel;

    @FXML
    private StackPane ramStackPane;

    @FXML
    private Label ramLabel;

    // ----------------------------------------------

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    SystemMonitor cpuUsage = new SystemMonitor(Monitor.CPU);
    SystemMonitor ramUsage = new SystemMonitor(Monitor.RAM);

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
	WEBMODE;

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

	// ----------------------------cpuStackPane
	cpuStackPane.getChildren().add(0, cpuUsage);
	cpuStackPane.setOnMouseReleased(r -> {
	    if (cpuUsage.isRunning())
		cpuUsage.stopUpdater();
	    else
		cpuUsage.restartUpdater();
	});

	// cpuLabel
	cpuLabel.visibleProperty().bind(cpuUsage.getUpdateService().runningProperty().not());

	// cpuUsage
	cpuUsage.visibleProperty().bind(cpuLabel.visibleProperty().not());
	//cpUsage.restartUpdater()

	// ----------------------------RamStackPane
	ramStackPane.getChildren().add(0, ramUsage);
	ramStackPane.setOnMouseReleased(r -> {
	    if (ramUsage.isRunning())
		ramUsage.stopUpdater();
	    else
		ramUsage.restartUpdater();
	});

	// ramLabel
	ramLabel.visibleProperty().bind(ramUsage.getUpdateService().runningProperty().not());

	// cpuUsage
	ramUsage.visibleProperty().bind(ramLabel.visibleProperty().not());

	//---------------------------------------------------

	// restartButton
	restartButton.setOnAction(a -> {
	    if (ActionTool.doQuestion("Soore you want to restart the application?", restartButton))
		Main.restartTheApplication(true);
	});

	// minimize
	minimize.setOnAction(ac -> Main.window.setIconified(true));

	// maximize_normalize
	maxOrNormalize.setOnAction(ac -> Main.scene.maximizeStage());

	// close
	exitApplication.setOnAction(ac -> Main.exitQuestion());

	//changeBackground
	changeBackground.setOnAction(a -> Main.changeBackgroundImage());

	//----------------------------START: TABS---------------------------------

	mainModeTab.setOnSelectionChanged(l -> {
	    if (mainModeTab.isSelected()) {
		//System.out.println("MainMode Selected")

		if (windowMode != WindowMode.MAINMODE && !Main.libraryMode.getChildren().contains(Main.multipleTabs)) {

		    Main.djMode.updateDividerArray();
		    Main.libraryMode.add(Main.multipleTabs, 0, 1);

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

		if (windowMode != WindowMode.DJMODE && Main.libraryMode.getChildren().contains(Main.multipleTabs)) {

		    // Work
		    Main.djMode.getSplitPane().getItems().removeAll(Main.treeManager, Main.multipleTabs);
		    Main.djMode.getSplitPane().getItems().addAll(Main.treeManager, Main.multipleTabs);
		    Main.djMode.setDividerPositions();

		    // Update window Mode
		    windowMode = WindowMode.DJMODE;

		}
		//if (!Main.specialJFXTabPane.getTabs().get(1).isSelected())
		Main.specialJFXTabPane.getSelectionModel().select(1);

	    }
	});

	userModeTab.setOnSelectionChanged(l -> {
	    if (userModeTab.isSelected()) {
		//System.out.println("userModeTab Selected")

		//if (!Main.specialJFXTabPane.getTabs().get(2).isSelected())
		Main.specialJFXTabPane.getSelectionModel().select(2);

		// Update window Mode
		windowMode = WindowMode.USERMODE;
	    }
	});

	webModeTab.setOnSelectionChanged(l -> {
	    if (webModeTab.isSelected()) {
		//System.out.println("webModeTab Selected")

		//if (!Main.specialJFXTabPane.getTabs().get(3).isSelected())
		Main.specialJFXTabPane.getSelectionModel().select(3);

		// Update window Mode
		windowMode = WindowMode.WEBMODE;
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
	// xr3Label
	xr3Label.textProperty()
		.bind(Bindings.createStringBinding(() -> MessageFormat.format(">-XR3Player (BETA) V.{0} -<  Width=[{1}],Height=[{2}]",
			Main.currentVersion, Main.window.getWidth(), Main.window.getHeight()), Main.window.widthProperty(),
			Main.window.heightProperty()));
    }

    //    /**
    //     * Changes the marks of goDJMode,goSimpleMode,goLibraryMode.
    //     *
    //     * @param a
    //     *            the a
    //     * @param b
    //     *            the b
    //     */
    //    private void changeMarks(boolean a, boolean b) {
    //	goDJMode.setSelected(a);
    //	goLibrariesMode.setSelected(b);
    //    }

}
