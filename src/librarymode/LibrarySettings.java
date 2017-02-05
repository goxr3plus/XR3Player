/*
 * 
 */
package librarymode;

import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import tools.InfoTool;

/**
 * The Class LibrarySettings.
 *
 * @author GOXR3PLUS
 */
public class LibrarySettings extends GridPane {

    /** The total items. */
    @FXML
    private Label totalItems;

    /** The date label. */
    @FXML
    private Label dateLabel;

    /** The comments area. */
    @FXML
    private TextArea commentsArea;

    /** The time label. */
    @FXML
    private Label timeLabel;

    /** The stars label. */
    @FXML
    private Label starsLabel;

    /** The total chars label. */
    @FXML
    private Label totalCharsLabel;

    // --------------------------------------------------------------------

    /** The library. */
    public Library library;

    /** The Constant popOver. */
    public static final PopOver popOver = new PopOver();

    /**
     * Constructor.
     */
    public LibrarySettings() {

	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "LibrarySettings.fxml"));
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
     * @param library
     *            the library
     */
    public void showWindow(Library library) {
	this.library = library;

	// Pass the current information
	dateLabel.setText(library.getDateCreated());
	timeLabel.setText(library.getTimeCreated());
	starsLabel.setText(Double.toString(library.getStars()));
	totalItems.setText("Total: [ " + Integer.toString(library.getTotalEntries()) + " ]");
	commentsArea.setText(library.getDescription());
	popOver.show(library.getImageView());
    }

    /**
     * Updates the totalSongsLabel with the given text.
     * 
     * @param library
     *            The Library calling this method
     *
     */
    public void updateTotalItemsLabel(Library library) {
	if (this.library == library)
	    totalItems.setText("Total: [ " + Integer.toString(library.getTotalEntries()) + " ]");
    }

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
	starsLabel.setOnMouseReleased(m -> library.updateLibraryStars());

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
