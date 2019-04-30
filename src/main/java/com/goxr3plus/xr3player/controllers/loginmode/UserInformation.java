/*
 * 
 */
package com.goxr3plus.xr3player.controllers.loginmode;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.UserCategory;
import com.goxr3plus.xr3player.utils.general.InfoTool;

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
	private StackPane imageViewStackPane;

	@FXML
	private StackedFontIcon noImageStackedFontIcon;

	@FXML
	private ImageView userImage;

	@FXML
	private Label userName;

	@FXML
	private JFXButton delete;

	@FXML
	private JFXButton rename;

	@FXML
	private Label dateCreated;

	@FXML
	private Label librariesLabel;

	@FXML
	private Label commentsLabel;

	@FXML
	private TextArea commentsArea;

	// --------------------------------------------------------------------

	private User user;

	UserCategory userCategory;

	/**
	 * Constructor.
	 */
	public UserInformation(UserCategory userCategory) {
		this.userCategory = userCategory;

		// ----------------------------------FXMLLoader-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.USER_FXMLS + "UserInformation.fxml"));
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
	 * @param user The given user
	 */
	public void displayForUser(User user) {
		this.user = user;

		// --UserName
		userName.setText(user.getNameField().getText());

		// --Date Label
		dateCreated.setText("Created : " + user.getDateCreated() + " " + user.getTimeCreated());

		// --LibrariesLabel
		librariesLabel.setText("Libraries : " + user.getTotalLibrariesLabel().getText());

		// --Comments Area
		commentsArea.setText(user.getDescriptionLabel().getText());

		// --rename
		if (userCategory == UserCategory.NO_LOGGED_IN) {
			rename.setOnAction(a -> user.renameUser(rename));

			// --delete
			delete.setOnAction(a -> ((User) Main.loginMode.viewer.getSelectedItem()).deleteUser(delete));
		}

		// --delete
		if (userCategory == UserCategory.LOGGED_IN)
			delete.setVisible(false);

		// --goBack
		if (userCategory == UserCategory.NO_LOGGED_IN) {
			goBack.setOnAction(a -> Main.loginMode.flipPane.flipToFront());
		} else if (userCategory == UserCategory.LOGGED_IN) {
			goBack.setVisible(false);
			goBack.setMaxSize(0, 0);
			goBack.setMinSize(0, 0);
		}

		// --imageView
		userImage.setFitWidth(imageViewStackPane.getHeight());
		userImage.setFitHeight(imageViewStackPane.getHeight());
		userImage.imageProperty().bind(user.getImageView().imageProperty());
		userImage.setOnMouseReleased(m -> user.changeUserImage());

		// Clip
		Rectangle clip = new Rectangle();
		clip.widthProperty().set(imageViewStackPane.getHeight());
		clip.heightProperty().set(imageViewStackPane.getHeight());
		clip.setArcWidth(90);
		clip.setArcHeight(90);
		userImage.setClip(clip);

		// noImageStackedFontIcon
		noImageStackedFontIcon.visibleProperty().bind(userImage.imageProperty().isNull());

		// -- commentsLabel
		commentsLabel.textProperty().bind(commentsArea.textProperty().length().asString());

		// -- commentsArea
		Optional.ofNullable(user.getUserInformationDb().getProperty("User-Description"))
				.ifPresent(comment -> commentsArea.setText(comment));

		// User Category
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

		// ---------------- Comments Area----------------------------------
		commentsArea.hoverProperty().addListener(l -> commentsArea.requestFocus());
		commentsArea.focusedProperty().addListener(l -> {
			if (!commentsArea.isFocused()) {
				// System.out.println("Lost Focus");

				// User Description Label
				user.getDescriptionProperty().set(commentsArea.getText());

				// System.out.println("After seting Description");

				// Save on the properties file
				user.getUserInformationDb().updateProperty("User-Description", commentsArea.getText());
			}
		});
		commentsArea.textProperty().addListener(c -> {
			// User?=null
			if (user != null) {
				String text = commentsArea.getText();
				// Check
				if (!text.isEmpty() && text.length() > 2000)
					commentsArea.setText(text.substring(0, 2000));
			}
		});
	}

	/**
	 * @return the userName
	 */
	public Label getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(Label userName) {
		this.userName = userName;
	}

	/**
	 * @return the userImage
	 */
	public ImageView getUserImage() {
		return userImage;
	}

}
