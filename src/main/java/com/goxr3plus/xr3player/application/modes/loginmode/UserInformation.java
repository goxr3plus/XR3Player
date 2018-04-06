/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.modes.loginmode;

import java.io.IOException;
import java.util.logging.Level;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The Class LibrarySettings.
 *
 * @author GOXR3PLUS
 */
public class UserInformation extends StackPane {
	
	// --------------------------------------------------------------------
	
	@FXML
	private JFXButton goBack;
	
	@FXML
	private ImageView userImage;
	
	@FXML
	private Label userName;
	
	@FXML
	private JFXButton rename;
	
	@FXML
	private JFXButton delete;
	
	@FXML
	private Label dateCreated;
	
	@FXML
	private Label timeCreated;
	
	@FXML
	private Label librariesLabel;
	
	@FXML
	private JFXTextArea commentsArea;
	
	// --------------------------------------------------------------------
	
	public enum UserCategory {
		LOGGED_IN, NO_LOGGED_IN;
	}
	
	private User user;
	
	UserCategory userCategory;
	
	/**
	 * Constructor.
	 */
	public UserInformation(UserCategory userCategory) {
		this.userCategory = userCategory;
		
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
	public void show(User user) {
		this.user = user;
		
		//--UserName
		userName.textProperty().bindBidirectional(user.getNameField().textProperty());
		
		//--Date Label
		dateCreated.setText("Date Created : " + user.getDateCreated());
		
		//--Time Label		
		timeCreated.setText("Time Created : " + user.getTimeCreated());
		
		//--LibrariesLabel
		librariesLabel.setText("Total Libraries : " + user.getTotalLibrariesLabel().getText());
		
		//--Comments Area		
		commentsArea.setText(user.getDescriptionLabel().getText());
		
		//--rename
		rename.setOnAction(a -> user.renameUser(rename));
		
		//--delete
		delete.setOnAction(a -> Main.loginMode.teamViewer.getSelectedItem().deleteUser(delete));
		
		//--goBack
		goBack.setOnAction(a -> Main.loginMode.flipPane.flipToFront());
		
		//--imageView
		userImage.imageProperty().bind(user.getImageView().imageProperty());
		userImage.setOnMouseReleased(m -> user.changeUserImage());
		
		//User Category
		if (userCategory == UserCategory.NO_LOGGED_IN)
			Main.loginMode.flipPane.flipToBack();
		
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
		
	}
	
}
