/**
 * 
 */
package application.users;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * @author GOXR3PLUS
 *
 */
public class User extends StackPane {

    @FXML
    ImageView imageView;

    @FXML
    Label nameField;

    // --------------------------------------------

    /** The logger for this class */
    private static final Logger logger = Logger.getLogger(User.class.getName());

    /**
     * The position of the User into the List
     */
    private int position;
    private String userName;

    /** This InvalidationListener is used during the rename of a user */
    private final InvalidationListener renameInvalidator = new InvalidationListener() {
	@Override
	public void invalidated(Observable observable) {

	    // Remove the Listener
	    Main.renameWindow.showingProperty().removeListener(this);

	    // !Showing
	    if (!Main.renameWindow.isShowing()) {

		// old && new -> name
		String oldName = getUserName();
		String newName = Main.renameWindow.getUserInput();
		boolean success = false;

		// Remove Bindings
		nameField.textProperty().unbind();

		// !XPressed
		if (Main.renameWindow.wasAccepted()) {

		    // duplicate?
		    if (!Main.loginMode.userViewer.items.stream()
			    .anyMatch(user -> user != User.this && user.getUserName().equalsIgnoreCase(newName))
			    || newName.equalsIgnoreCase(oldName)) {

			File originalFolder = new File(InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + oldName);
			File outputFolder = new File(InfoTool.ABSOLUTE_DATABASE_PATH_WITH_SEPARATOR + newName);

			//Check if the Folder can be renamed
			if (originalFolder.renameTo(outputFolder)) { //Success
			    success = true;
			    setUserName(nameField.getText());
			    nameField.getTooltip().setText(getUserName());
			} else
			    ActionTool.showNotification("Error", "An error occured trying to rename the user",
				    Duration.seconds(2), NotificationType.ERROR);

		    }//This user already exists
		    else
			Notifications.create().title("Dublicate User")
				.text("This user already exists\nTry with a different name").darkStyle().showConfirm();
		}

		//Succeeded?
		if (!success)
		    resetTheName();

	    }  // !Showing
	}

	/**
	 * Resets the name if the user cancels the rename operation
	 */
	private void resetTheName() {
	    nameField.setText(getUserName());
	}
    };

    /**
     * Constructor
     * 
     * @param userName
     * @param position
     */
    public User(String userName, int position) {
	this.setUserName(userName);
	this.setPosition(position);

	// ----------------------------------FXMLLoader-------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "UserController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	// -------------Load the FXML-------------------------------
	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Called as soon as FXML file has been loaded
     */
    @FXML
    private void initialize() {

	// Clip
	Rectangle rect = new Rectangle();
	rect.widthProperty().bind(this.widthProperty());
	rect.heightProperty().bind(this.heightProperty());
	rect.setArcWidth(25);
	rect.setArcHeight(25);
	// rect.setEffect(new Reflection());

	// StackPane -> this
	this.setClip(rect);
	// Reflection reflection = new Reflection();
	// reflection.setInput(new DropShadow(4, Color.WHITE));
	// this.setEffect(reflection);

	//Name
	nameField.setText(getUserName());
	nameField.getTooltip().setText(getUserName());
    }

    /**
     * @return The Position of the user inside the list
     */
    public int getPosition() {
	return position;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
	return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
	if (nameField != null)
	    nameField.setText(userName);
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(int position) {
	this.position = position;
    }

    /**
     * Renames the current User.
     * 
     * @param node
     *            The node based on which the Rename Window will be position
     */
    public void renameUser(Node node) {

	// Open the Window
	Main.renameWindow.show(getUserName(), node);

	// Bind 
	nameField.textProperty().bind(Main.renameWindow.inputField.textProperty());

	Main.renameWindow.showingProperty().addListener(renameInvalidator);
    }
}
