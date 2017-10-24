/**
 * 
 */
package main.java.com.goxr3plus.xr3player.applicationmodes.loginmode;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class UserMode extends BorderPane {
	
	// ----------------------
	
	@FXML
	private Label nameLabel;
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label dateCreatedLabel;
	
	@FXML
	private Label timeCreatedLabel;
	
	// ----------------------
	
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	private User user;
	
	/**
	 * Constructor.
	 */
	public UserMode() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "UserMode.fxml"));
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
		
	}
	
	/**
	 * Returns the currently logged in user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * This method should be called after fxml has been initialized for this controller
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
		
		//--nameLabel
		nameLabel.textProperty().bind(Bindings.concat("Logged in as->[ ").concat(user.getNameField().textProperty()).concat(" ]"));
		
		//--imageView
		imageView.imageProperty().bind(user.getImageView().imageProperty());
		imageView.setOnMouseReleased(m -> user.changeUserImage());
		
		//--Date Label
		dateCreatedLabel.setText(user.getDateCreated());
		
		//--Time Label	
		timeCreatedLabel.setText(user.getTimeCreated());
	}
	
}
