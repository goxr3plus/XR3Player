/*
 * 
 */
package application;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.scene.control.ToggleButton;
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

    @FXML
    private StackPane cpuStackPane;

    @FXML
    private Label cpuLabel;

    @FXML
    private Label xr3Label;

    @FXML
    private ToggleButton goLibrariesMode;

    @FXML
    private ToggleButton goDJMode;

    @FXML
    private Button restartButton;

    @FXML
    private Button minimize;

    @FXML
    private Button maxOrNormalize;

    @FXML
    private Button close;


    // ----------------------
    

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    CPUsage cpUsage = new CPUsage();

    /**
     * The current Window Mode that means if the application is on <b> LibraryMode </b> or in <b>DJMode </b>.
     */
    private WindowMode windowMode = WindowMode.LIBRARYMODE;

    /**
     * WindowMode.
     *
     * @author SuperGoliath
     */
    public enum WindowMode {

	/** The djmode. */
	DJMODE,
	/** The librarymode. */
	LIBRARYMODE;

    }

    /**
     * Constructor.
     */
    public TopBar() {
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
     * Called as soon as .fxml is initialized
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

	// goDJMode
	goDJMode.setOnMouseReleased(mouse -> {
	    if (windowMode != WindowMode.DJMODE && mouse.getButton() == MouseButton.PRIMARY) {

		// Work
		Main.djMode.getSplitPane().getItems().removeAll(Main.treeManager, Main.multipleTabs);
		Main.djMode.getSplitPane().getItems().addAll(Main.treeManager, Main.multipleTabs);
		Main.djMode.setDividerPositions();
		//Main.root.setCenter(Main.djMode)
		Main.mainModeFlipPane.flipToBack();

		// Update window Mode
		windowMode = WindowMode.DJMODE;

		// Marked
		changeMarks(true, false);
	    } else
		goDJMode.setSelected(true);
	});

	// goLibrariesMode
	goLibrariesMode.setSelected(true);
	goLibrariesMode.setOnMouseReleased(mouse -> {
	    if (windowMode != WindowMode.LIBRARYMODE && mouse.getButton() == MouseButton.PRIMARY) {

		Main.djMode.updateDividerArray();
		Main.libraryMode.add(Main.multipleTabs, 0, 1);
		//Main.root.setCenter(Main.libraryMode)
		Main.mainModeFlipPane.flipToFront();

		// Update window Mode
		windowMode = WindowMode.LIBRARYMODE;

		// Marked
		changeMarks(false, true);
	    } else
		goLibrariesMode.setSelected(true);
	});

    }

    /**
     * Add the binding to the xr3Label
     */
    public void addXR3LabelBinding() {
	// xr3Label
	StringBinding binding = Bindings.createStringBinding(
		() -> MessageFormat.format(">-XR3Player (BETA) V.{0} -<  Width=[{1}],Height=[{2}]", Main.currentVersion,
			Main.window.getWidth(), Main.window.getHeight()),
		Main.window.widthProperty(), Main.window.heightProperty());
	xr3Label.textProperty().bind(binding);
    }

    /**
     * Changes the marks of goDJMode,goSimpleMode,goLibraryMode.
     *
     * @param a
     *            the a
     * @param b
     *            the b
     */
    private void changeMarks(boolean a, boolean b) {
	goDJMode.setSelected(a);
	goLibrariesMode.setSelected(b);
    }

}
