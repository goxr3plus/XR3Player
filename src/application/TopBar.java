/*
 * 
 */
package application;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.TopBar.WindowMode;
import customnodes.CPUsage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import tools.InfoTool;

/**
 * The Top bar of the application Window.
 *
 * @author GOXR3PLUS
 */
public class TopBar extends BorderPane {

    // ----------------------------------------------

    @FXML
    private StackPane cpuStackPane;

    @FXML
    private Label cpuLabel;

    @FXML
    private Label xr3Label;

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
    private Button close;

    // ----------------------------------------------

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    CPUsage cpUsage = new CPUsage();

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

	// cpuStackPane
	cpuStackPane.getChildren().add(0, cpUsage);
	cpuStackPane.setOnMouseReleased(r -> {
	    if (cpUsage.isRunning())
		cpUsage.stopUpdater();
	    else
		cpUsage.restartUpdater();
	});

	// cpuLabel
	cpuLabel.visibleProperty().bind(cpUsage.getUpdateService().runningProperty().not());

	// cpuUsage
	cpUsage.visibleProperty().bind(cpuLabel.visibleProperty().not());
	//cpUsage.restartUpdater()

	// showSideBar
	//showSideBar.setOnAction(a -> Main.sideBar.toogleBar()

	// restartButton
	restartButton.setOnAction(a -> {
	    Alert alert = new Alert(AlertType.CONFIRMATION);
	    alert.initOwner(Main.window);

	    alert.setContentText("Soore you want to restart the application?");
	    ButtonType yes = new ButtonType("Yes", ButtonData.OK_DONE);
	    ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	    ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);

	    alert.getButtonTypes().setAll(yes, cancel);
	    alert.showAndWait().ifPresent(answer -> {
		if (answer == yes)
		    Main.restartTheApplication(true);
	    });
	});

	// minimize
	minimize.setOnAction(ac -> Main.window.setIconified(true));

	// maximize_normalize
	maxOrNormalize.setOnAction(ac -> Main.scene.maximizeStage());

	// close
	close.setOnAction(ac -> Main.exitQuestion());

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
		Main.specialJFXTabPane.getSelectionModel().select(1);

	    }
	});

	userModeTab.setOnSelectionChanged(l -> {
	    if (userModeTab.isSelected()) {
		//System.out.println("userModeTab Selected")

		Main.specialJFXTabPane.getSelectionModel().select(2);

		// Update window Mode
		windowMode = WindowMode.USERMODE;
	    }
	});

	webModeTab.setOnSelectionChanged(l -> {
	    if (webModeTab.isSelected()) {
		//System.out.println("webModeTab Selected")

		Main.specialJFXTabPane.getSelectionModel().select(3);

		// Update window Mode
		windowMode = WindowMode.WEBMODE;
	    }
	});

	//----------------------------END: TABS---------------------------------

    }

    /**
     * Add the binding to the xr3Label
     */
    public void addXR3LabelBinding() {
	// xr3Label
	xr3Label.textProperty()
		.bind(Bindings.createStringBinding(
			() -> MessageFormat.format(">-XR3Player (BETA) V.{0} -<  Width=[{1}],Height=[{2}]",
				Main.currentVersion, Main.window.getWidth(), Main.window.getHeight()),
			Main.window.widthProperty(), Main.window.heightProperty()));
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
