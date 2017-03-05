/**
 * 
 */
package loginsystema;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class User extends StackPane {

    @FXML
    ImageView imageView;

    @FXML
    private Label nameField;

    // --------------------------------------------

    /** The logger for this class */
    private static final Logger logger = Logger.getLogger(User.class.getName());

    /**
     * The position of the User into the List
     */
    private int position;
    private String userName;

    /**
     * Constructor
     */
    public User(String userName, int position) {
	this.setUserName(userName);
	this.setPosition(position);

	// ----------------------------------FXMLLoader-------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "UserController.fxml"));
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
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
	this.position = position;
    }

}
