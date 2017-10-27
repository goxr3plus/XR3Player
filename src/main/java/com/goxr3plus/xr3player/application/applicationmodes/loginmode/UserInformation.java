/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.applicationmodes.loginmode;

import java.io.IOException;
import java.util.logging.Level;

import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.SpecialPopOver;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The Class LibrarySettings.
 *
 * @author GOXR3PLUS
 */
public class UserInformation extends StackPane {
	
	@FXML
	private TextArea commentsArea;
	
	@FXML
	private Label totalLibraries;
	
	@FXML
	private Label dateLabel;
	
	@FXML
	private Label timeLabel;
	
	@FXML
	private Label totalCharsLabel;
	
	// --------------------------------------------------------------------
	
	private User user;
	
	/** The Constant popOver. */
	private SpecialPopOver popOver;
	
	/**
	 * Constructor.
	 */
	public UserInformation() {
		
		// ----------------------------------FXMLLoader-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "UserInformation.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		// -------------Load the FXML-------------------------------
		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
		
	}
	
	/**
	 * Shows the window with the Library settings.
	 *
	 * @param user
	 *            The given user
	 */
	public void showWindow(User user) {
		this.user = user;
		
		//--Date Label
		dateLabel.setText(user.getDateCreated());
		//--Time Label		
		timeLabel.setText(user.getTimeCreated());
		//--Total Libraries		
		totalLibraries.setText(user.getTotalLibrariesLabel().getText());
		//--Comments Area		
		commentsArea.setText(user.getDescriptionLabel().getText());
		
		//Show the PopOver
		popOver.showPopOver(user);
	}
	
	/**
	 * Check if the PopOver is Showing
	 * 
	 * @return True if showing , false if not
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
	 * Returns the user
	 *
	 * @return the library
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	public void initialize() {
		
		// -------------Create the PopOver-------------------------------
		popOver = new SpecialPopOver();
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
			//			if (!newValue)  //on hidden
			//				System.out.println("Closed...");
			//			
		});
		
		//-- Total Characters
		totalCharsLabel.textProperty().bind(commentsArea.textProperty().length().asString());
		
		//-- Comments Area
		commentsArea.textProperty().addListener(c -> {
			if (user != null)
				if (commentsArea.getText().length() <= 200)
					user.getDescriptionLabel().setText(commentsArea.getText());
				else
					commentsArea.setText(commentsArea.getText().substring(0, 200));
		});
		
		commentsArea.setOnMouseExited(exit -> {
			if (user != null)
				//Save on the properties file
				user.getUserInformationDb().updateProperty("User-Description", commentsArea.getText());
			
		});
		
		commentsArea.hoverProperty().addListener(l -> commentsArea.requestFocus());
		
		commentsArea.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				popOver.hide();
		});
		
	}
	
}
