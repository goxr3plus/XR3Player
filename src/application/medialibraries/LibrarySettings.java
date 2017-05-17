/*
 * 
 */
package application.medialibraries;

import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import application.Main;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * The Class LibrarySettings.
 *
 * @author GOXR3PLUS
 */
public class LibrarySettings extends BorderPane {

    @FXML
    private TextArea commentsArea;

    @FXML
    private Label totalItems;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label starsLabel;

    @FXML
    private Label totalCharsLabel;

    // --------------------------------------------------------------------

    /** The library. */
    private Library library;

    /** The Constant popOver. */
    private final PopOver popOver = new PopOver();

    /**
     * Constructor.
     */
    public LibrarySettings() {

	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "LibrarySettings.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	popOver.setTitle("Information");
	popOver.getScene().setFill(Color.TRANSPARENT);
	popOver.setAutoFix(true);
	popOver.setArrowLocation(ArrowLocation.TOP_CENTER);
	popOver.setArrowSize(25);
	popOver.setDetachable(false);
	popOver.setAutoHide(true);
	popOver.setHeaderAlwaysVisible(true);
	popOver.setContentNode(this);
	popOver.showingProperty().addListener((observable, oldValue, newValue) -> {
	    if (library != null)
		library.updateDescription();
	});

	try {
	    loader.load();
	} catch (IOException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}

    }

    /**
     * Shows the window with the Library settings.
     *
     * @param library1
     *            the library
     */
    public void showWindow(Library library1) {
	this.library = library1;

	// Pass the current information
	dateLabel.setText(library1.getDateCreated());
	
	timeLabel.setText(library1.getTimeCreated());
	
	starsLabel.textProperty().bind(library.getRatingLabel().textProperty());
	
	totalItems.textProperty().bind(library.getTotalItemsLabel().textProperty());
	
	commentsArea.setText(library1.getDescription());
	
	popOver.show(library1.getImageView());
    }

    /**
     * @return True if the popover is showing of false if not
     */
    public boolean isShowing() {
	return popOver.isShowing();
    }
    /**
     * Updates the totalSongsLabel with the given text.
     * 
     * @param library
     *            The Library calling this method
     *
     */
    //    public void updateTotalItemsLabel(Library library) {
    //	if (this.library == library)
    //	    totalItems.setText("Total: [ " + Integer.toString(library.getTotalEntries()) + " ]");
    //    }

    /**
     * Checking if commentsArea is Focused.
     *
     * @return true, if is comments area focused
     */
    public boolean isCommentsAreaFocused() {
	return commentsArea.isFocused();

    }

    /**
     * Returns the StarLabel.
     *
     * @return the stars label
     */
    public Label getStarsLabel() {
	return starsLabel;
    }

    /**
     * Retuns Library that is on SettingsMode if any,else null.
     *
     * @return the library
     */
    public Library getLibrary() {
	return library;
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    public void initialize() {

	// GlyphsDude.setIcon(totalItems, FontAwesomeIcon.CLOUD, "1.5em")

	// starsLabel
	starsLabel.setOnMouseReleased(m -> library.updateLibraryStars(library));

	// totalCharsLabel
	totalCharsLabel.textProperty().bind(commentsArea.textProperty().length().asString());

	// commentsArea
	commentsArea.textProperty().addListener(c -> {
	    if (library != null)
		if (commentsArea.getText().length() <= 200)
		    library.setDescription(commentsArea.getText());
		else
		    commentsArea.setText(commentsArea.getText().substring(0, 200));
	});

	commentsArea.setOnMouseExited(exit -> {
	    if (library != null)
		library.updateDescription();
	});

	commentsArea.hoverProperty().addListener(l -> commentsArea.requestFocus());

	commentsArea.setOnKeyReleased(key -> {
	    if (key.getCode() == KeyCode.ESCAPE)
		popOver.hide();
	});

    }

}
