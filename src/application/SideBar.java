package application;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import smartcontroller.Operation;
import tools.ActionTool;
import tools.InfoTool;

/**
 * This class is used as the SideBar of the application.
 *
 * @author GOXR3PLUS
 */
public class SideBar extends BorderPane {

    /** The speech label. */
    @FXML
    private Label speechLabel;

    /** The internet label. */
    @FXML
    private Label internetLabel;

    /** The speech toggle. */
    @FXML
    private JFXToggleButton speechToggle;

    /** The speech progress indicator. */
    @FXML
    private ProgressIndicator speechProgressIndicator;

    /** The internet toggle. */
    @FXML
    private JFXToggleButton internetToggle;

    /** The internet progress indicator. */
    @FXML
    private ProgressIndicator internetProgressIndicator;

    @FXML Label userNameLabel;

    /** The xr 3 settings. */
    @FXML
    private MenuButton xr3Settings;

    /** The import data base. */
    @FXML
    private MenuItem importDataBase;

    /** The export data base. */
    @FXML
    private MenuItem exportDataBase;

    /** The delete data base. */
    @FXML
    private MenuItem deleteDataBase;

    /** The hide side bar. */
    @FXML
    private JFXButton hideSideBar;

    @FXML
    private ImageView userImageView;

    // -------------------------------------------------------------

    /** Translate Transition used to show/hide the bar. */
    private TranslateTransition tTrans;

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    /** The internet thread. */
    Thread internetThread;

    /**
     * Constructor.
     */
    public SideBar() {

	// ------------------------------------FXMLLOADER
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "SideBar.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}
	
    }

    /**
     * Gets the speech toggle button.
     *
     * @return the speech toggle button
     */
    public JFXToggleButton getSpeechToggleButton() {
	return speechToggle;
    }

    /**
     * Gets the speech progress indicator.
     *
     * @return the speech progress indicator
     */
    public ProgressIndicator getSpeechProgressIndicator() {
	return speechProgressIndicator;
    }

    /**
     * Shows the Bar.
     */
    public void showBar() {
	if (tTrans.getStatus() != Animation.Status.RUNNING && this.getTranslateX() == -this.getPrefWidth()) {
	    tTrans.setRate(1);
	    tTrans.playFromStart();
	}
    }

    /**
     * Hides the Bar.
     */
    public void hideBar() {
	if (tTrans.getStatus() != Animation.Status.RUNNING && this.getTranslateX() == 0) {
	    tTrans.setRate(-1);
	    tTrans.playFrom(tTrans.getTotalDuration());
	}
    }

    @FXML
    private void initialize() {

	

	// Translate Transition
	tTrans = new TranslateTransition(Duration.millis(200), this);
	tTrans.setFromX(-this.getPrefWidth());
	tTrans.setToX(0);

	this.setTranslateX(-this.getPrefWidth());

	// closeSideBar
	hideSideBar.setOnAction(a -> hideBar());

	// Clip
	Rectangle rect = new Rectangle();
	rect.widthProperty().bind(userImageView.fitWidthProperty());
	rect.heightProperty().bind(userImageView.fitHeightProperty());
	rect.setArcHeight(30);
	rect.setArcWidth(30);
	rect.setEffect(new Reflection());

	// StackPane -> this
	userImageView.setClip(rect);

	// importDataBase
	importDataBase.setOnAction(e -> {
	    if (!Main.dbManager.zipper.isRunning() && !Main.dbManager.unZipper.isRunning()
		    && Main.libraryMode.multipleLibs.isFree(true)) {

		File file = Main.specialChooser.prepareToImportDataBase(Main.window);
		if (file != null) {
		    if ("XR3DataBase.zip".equals(file.getName())) {

			// Close all the connections with database
			Main.dbManager.manageConnection(Operation.CLOSE);

			// Delete the previous database
			ActionTool.deleteFile(new File(InfoTool.ABSOLUTE_DATABASE_PATH_PLAIN));

			// Change the Scene View
			Main.updateScreen.setVisible(true);
			Main.updateScreen.progressBar.progressProperty()
				.bind(Main.dbManager.unZipper.progressProperty());

			// Import the new database
			Main.dbManager.unZipper.importDataBase(file.getAbsolutePath());

		    } else
			Notifications.create().title("Information").text("Please select the XR3DataBase.zip")
				.darkStyle().showWarning();
		}
	    }
	});

	// exportDataBase
	exportDataBase.setOnAction(a -> {
	    if (!Main.dbManager.zipper.isRunning() && !Main.dbManager.unZipper.isRunning()
		    && Main.libraryMode.multipleLibs.isFree(true)) {

		File file = Main.specialChooser.prepareForExportDataBase(Main.window);
		if (file != null) {

		    // Change the Scene View
		    Main.updateScreen.setVisible(true);
		    Main.updateScreen.progressBar.progressProperty().bind(Main.dbManager.zipper.progressProperty());

		    // Export the database
		    Main.dbManager.zipper.exportDataBase(file.getParent() + File.separator + "XR3DataBase.zip",
			    InfoTool.ABSOLUTE_DATABASE_PATH_PLAIN);
		}
	    }
	});

	// deleteDataBase
	deleteDataBase.setOnAction(a -> {
	    if (!Main.dbManager.zipper.isRunning() && !Main.dbManager.unZipper.isRunning()
		    && Main.libraryMode.multipleLibs.isFree(true) && ActionTool.doQuestion(
			    "You will delete the database of the application!\nAre you soore for that?\nThere is no coming back.\nAfter that the application will automatically restart...")) {

		// Close database connections
		Main.dbManager.manageConnection(Operation.CLOSE);

		// Clear the Previous database manager
		ActionTool.deleteFile(new File(InfoTool.ABSOLUTE_DATABASE_PATH_PLAIN));

		// Show Update Screen
		Main.updateScreen.setVisible(true);
		Main.updateScreen.progressBar.progressProperty().unbind();
		Main.updateScreen.progressBar.setProgress(-1);
		Main.updateScreen.label.setText("Restarting....");

		// Exit the application
		Main.canSaveData = false;
		Main.restartTheApplication(false);

	    }
	});

	// speechLabel and speechButton
	speechLabel.disableProperty().bind(speechToggle.selectedProperty().not());
	speechToggle.selectedProperty().addListener(l -> {
	    if (speechToggle.isSelected())
		Main.speechReader.startSpeechRecognizer();
	    else
		Main.speechReader.stopSpeechRec(true);
	});

	speechToggle.setDisable(true);
	// ------------------------------------About the Internet

	// Create the runnable only one time
	Runnable runnable = () -> {

	    // Disable the Progress Indicator
	    // Thread has started
	    Platform.runLater(() -> {
		internetProgressIndicator.setVisible(false);
		internetLabel.setText("");
	    });

	    // if internetToggle is still selected
	    while (internetToggle.isSelected()) {

		// Start a count down latch
		CountDownLatch latch = new CountDownLatch(1);

		// Decide
		if (InfoTool.isReachableByPing("www.google.com")) {

		    // System.out.println("Internet is reachable...")

		    // Enable internet label
		    Platform.runLater(() -> {
			internetLabel.setDisable(false);
			latch.countDown();
		    });

		} else {

		    // System.out.println("Internet not reachable...")

		    // Disable internet label
		    Platform.runLater(() -> {
			internetLabel.setDisable(true);
			latch.countDown();
		    });

		}
		try {
		    latch.await();
		    Thread.sleep(700);
		} catch (InterruptedException ex) {
		    internetThread.interrupt();
		    logger.log(Level.WARNING, "", ex);
		}
	    }

	    Platform.runLater(() -> {
		internetProgressIndicator.setVisible(false);
		internetLabel.setText("??");
	    });
	};

	internetToggle.selectedProperty().addListener(l -> {
	    if (internetToggle.isSelected()) {
		internetProgressIndicator.setVisible(true);
		internetThread = new Thread(runnable);
		internetThread.start();
	    } else {
		internetProgressIndicator.setVisible(true);
		internetLabel.setDisable(true);
	    }

	});

    }

}
