/*
 * 
 */
package application.librarymode;

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
public class LibraryInformation extends BorderPane {
	
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
	public LibraryInformation() {
		
		// ----------------------------------FXMLLoader-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "LibraryInformation.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		// -------------Load the FXML-------------------------------
		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
		
		// -------------Create the PopOver-------------------------------
		popOver.setTitle("Information");
		popOver.getScene().setFill(Color.TRANSPARENT);
		popOver.setAutoFix(true);
		popOver.setArrowLocation(ArrowLocation.TOP_CENTER);
		popOver.setArrowSize(25);
		popOver.setDetachable(false);
		popOver.setAutoHide(true);
		popOver.setHeaderAlwaysVisible(true);
		popOver.setContentNode(this);
		popOver.showingProperty().addListener((observable , oldValue , newValue) -> {
			if (library != null)
				library.updateDescription();
			if (!newValue) {  //on hidden
				//--Stars Label
				starsLabel.textProperty().unbind();
				//--Total Items
				totalItems.textProperty().unbind();
			}
			
		});
		
	}
	
	/**
	 * Shows the window with the Library settings.
	 *
	 * @param library
	 *            The given library
	 */
	public void showWindow(Library library) {
		this.library = library;
		
		//--Date Label
		dateLabel.setText(library.getDateCreated());
		//--Time Label
		timeLabel.setText(library.getTimeCreated());
		//--Stars Label
		starsLabel.textProperty().bind(library.getRatingLabel().textProperty());
		//--Total Items
		totalItems.textProperty().bind(library.getTotalItemsLabel().textProperty());
		//--Comments Area
		commentsArea.setText(library.getDescription());
		
		popOver.show(library.getImageView());
	}
	
	/**
	 * @return True if the popover is showing of false if not
	 */
	public boolean isShowing() {
		return popOver.isShowing();
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
	 * The library that lastly opened it's information's PopOver
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
		
		//-- Total Characters
		totalCharsLabel.textProperty().bind(commentsArea.textProperty().length().asString());
		
		//-- Comments Area
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
