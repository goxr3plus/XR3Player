/**
 * 
 */
package xplayer.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXCheckBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;

/**
 * The Class XPlayerSettingsController.
 *
 * @author GOXR3PLUS
 */
public class XPlayerSettingsController extends BorderPane {

    @FXML
    JFXCheckBox showFPS;

    @FXML
    JFXCheckBox startImmediately;

    @FXML
    private JFXCheckBox showVisualizer;

    @FXML
    JFXCheckBox askSecurityQuestion;

    @FXML
    Tab equalizerTab;

    @FXML
    Tab playListTab;

    // ------------------------

    /** The x player UI. */
    XPlayerController xPlayerUI;

    /**
     * Constructor.
     *
     * @param xPlayerUI the x player UI
     */
    public XPlayerSettingsController(XPlayerController xPlayerUI) {

        this.xPlayerUI = xPlayerUI;

        // FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "XPlayerSettingsController.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName())
                .log(Level.SEVERE, "XPlayerSettingsController FXML can't be loaded!", ex);
        }

    }

    /**
     * As soon as fxml has been loaded then this method will be called
     * 1)-constructor,2)-FXMLLOADER,3)-initialize();
     */
    @FXML
    private void initialize() {

        // When this can be visible?
        this.setOnKeyReleased(key -> {
            if (key.getCode() == KeyCode.ESCAPE)
                xPlayerUI.settingsToggle.setSelected(false);
        });
        this.visibleProperty()
            .bind(xPlayerUI.settingsToggle.selectedProperty());
        this.visibleProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue) // true?
                    this.requestFocus();
            });

        // ShowFPS
        showFPS.setOnAction(a -> xPlayerUI.visualizer.setShowFPS(!xPlayerUI.visualizer.isShowingFPS()));

    }

}
