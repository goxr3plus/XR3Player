/*
 * 
 */
package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * The Top bar of the application Window.
 *
 * @author GOXR3PLUS
 */
public class TopBar extends BorderPane {

    /** The minimize. */
    @FXML
    private Button minimize;

    /** The max or normalize. */
    @FXML
    private Button maxOrNormalize;

    /** The close. */
    @FXML
    private Button close;

    /** The label. */
    @FXML
    private Label xr3Label;

    /** The show side bar. */
    @FXML
    private Button showSideBar;

    /** The go DJ mode. */
    @FXML
    private ToggleButton goDJMode;

    /** The go libraries mode. */
    @FXML
    private ToggleButton goLibrariesMode;

    @FXML
    private MenuItem checkForUpdates;

    @FXML
    private MenuItem restartButton;

    // ----------------------

    /**
     * The current Window Mode that means if the application is on <b>
     * LibraryMode </b> or in <b>DJMode </b>.
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "TopBar.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            Main.logger.log(Level.WARNING, "", ex);
        }
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {

        // showSideBar
        showSideBar.setOnAction(a -> Main.sideBar.showBar());

        checkForUpdates.setOnAction(a -> Main.checkForUpdates(true));

        // restartButton
        restartButton.setOnAction(a -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.initOwner(Main.window);

            alert.setContentText("Soore you want to restart the application?");
            ButtonType yes = new ButtonType("Yes");
            ButtonType cancel = new ButtonType("Cancel");
            ((Button) alert.getDialogPane()
                .lookupButton(ButtonType.CANCEL)).setDefaultButton(true);

            alert.getButtonTypes()
                .setAll(yes, cancel);
            alert.initStyle(StageStyle.TRANSPARENT);
            alert.showAndWait()
                .ifPresent(answer -> {
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
                Main.djMode.getSplitPane()
                    .getItems()
                    .removeAll(Main.treeManager, Main.multipleTabs);
                Main.djMode.getSplitPane()
                    .getItems()
                    .addAll(Main.treeManager, Main.multipleTabs);
                Main.djMode.setDividerPositions();
                Main.root.setCenter(Main.djMode);

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

                Main.libraryMode.add(Main.multipleTabs, 0, 1);
                Main.root.setCenter(Main.libraryMode);

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
        StringBinding binding = Bindings.createStringBinding(() -> MessageFormat.format(">-XR3Player-<  Width=[{0}],Height=[{1}]", Main.window.getWidth(), Main.window.getHeight()), Main.window.widthProperty(), Main.window.heightProperty());
        xr3Label.textProperty()
            .bind(binding);
    }

    /**
     * Changes the marks of goDJMode,goSimpleMode,goLibraryMode.
     *
     * @param a the a
     * @param b the b
     */
    private void changeMarks(boolean a, boolean b) {
        goDJMode.setSelected(a);
        goLibrariesMode.setSelected(b);
    }

}
