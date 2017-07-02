/**
 * 
 */
package application.loginmode;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.database.PropertiesDb;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.JavaFXTools;
import application.tools.NotificationType;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author GOXR3PLUS
 *
 */
public class User extends StackPane {
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label nameField;
	
	@FXML
	private Label descriptionLabel;
	
	@FXML
	private Label informationLabel;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private Label totalLibrariesLabel;
	
	// --------------------------------------------
	
	/** The logger for this class */
	private static final Logger logger = Logger.getLogger(User.class.getName());
	
	/**
	 * Here are stored all the informations about the user and other things like opened libraries etc.
	 */
	private PropertiesDb userInformationDb;
	
	/**
	 * The position of the User into the List
	 */
	private int position;
	private String userName;
	private LoginMode loginMode;
	
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
					if (!Main.loginMode.teamViewer.getItemsObservableList().stream().anyMatch(user -> user != User.this && user.getUserName().equalsIgnoreCase(newName))
							|| newName.equalsIgnoreCase(oldName)) {
						
						File originalFolder = new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + oldName);
						File outputFolder = new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + newName);
						
						//Check if the Folder can be renamed
						if (originalFolder.renameTo(outputFolder)) { //Success			
							success = true;
							setUserName(nameField.getText());
							nameField.getTooltip().setText(getUserName());
							
							//Change the absolute path of the UserInformation.properties file
							getUserInformationDb().setFileAbsolutePath(
									InfoTool.getAbsoluteDatabasePathWithSeparator() + userName + File.separator + "settings" + File.separator + "userInformation.properties");
							
							//Change Pie Data Name
							Main.loginMode.getLibrariesPieChartData().forEach(pieData -> {
								if (pieData.getName().equals(InfoTool.getMinString(oldName, 4)))
									pieData.setName(InfoTool.getMinString(newName, 4));
							});
						} else
							ActionTool.showNotification("Error", "An error occured trying to rename the user", Duration.seconds(2), NotificationType.ERROR);
						
					} //This user already exists
					else
						ActionTool.showNotification("Dublicate User", "Name->" + newName + " is already used from another User...", Duration.millis(2000),
								NotificationType.INFORMATION);
				}
				
				//Succeeded?
				if (!success)
					resetTheName();
				
			} // !Showing
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
	 * @param loginMode
	 */
	public User(String userName, int position, LoginMode loginMode) {
		this.setUserName(userName);
		this.setPosition(position);
		this.loginMode = loginMode;
		
		//Create the UserInformation DB
		userInformationDb = new PropertiesDb(
				InfoTool.getAbsoluteDatabasePathWithSeparator() + userName + File.separator + "settings" + File.separator + "userInformation.properties", false);
		
		// ----------------------------------FXMLLoader-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "User.fxml"));
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
		
		// --Key Listener
		setOnKeyReleased(this::onKeyReleased);
		
		// --Mouse Listener
		setOnMouseEntered(m -> {
			if (!isFocused())
				requestFocus();
		});
		
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
		
		//imageView
		String absoluteImagePath = JavaFXTools.getAbsoluteImagePath("userImage", InfoTool.getAbsoluteDatabasePathWithSeparator() + getUserName());
		imageView.setImage(absoluteImagePath == null ? null : new Image(new File(absoluteImagePath).toURI() + ""));
		
		//Name
		nameField.setText(getUserName());
		nameField.getTooltip().setText(getUserName());
		nameField.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.PRIMARY && m.getClickCount() == 2 && Main.loginMode.teamViewer.getTimeline().getStatus() != Status.RUNNING)
				renameUser(nameField);
		});
		
		// ----InformationLabel
		informationLabel.setOnMouseReleased(m -> Main.loginMode.userInformation.showWindow(this));
		
		// ----DescriptionLabel
		descriptionLabel.visibleProperty()
				.bind(descriptionLabel.textProperty().isEmpty().not().and(Main.settingsWindow.getLibrariesSettingsController().getShowWidgets().selectedProperty()));
		descriptionLabel.setOnMouseReleased(informationLabel.getOnMouseReleased());
		
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
	 * @return the totalLibrariesLabel
	 */
	public Label getTotalLibrariesLabel() {
		return totalLibrariesLabel;
	}
	
	/**
	 * @return the imageView
	 */
	public ImageView getImageView() {
		return imageView;
	}
	
	/**
	 * @return the nameField
	 */
	public Label getNameField() {
		return nameField;
	}
	
	/**
	 * @param nameField
	 *            the nameField to set
	 */
	public void setNameField(Label nameField) {
		this.nameField = nameField;
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
		Main.renameWindow.show(getUserName(), node, "User Renaming");
		
		// Bind 
		nameField.textProperty().bind(Main.renameWindow.inputField.textProperty());
		
		Main.renameWindow.showingProperty().addListener(renameInvalidator);
	}
	
	/**
	 * This method is called when a key is released.
	 *
	 * @param e
	 *            An event which indicates that a keystroke occurred in a javafx.scene.Node.
	 */
	public void onKeyReleased(KeyEvent e) {
		if (Main.loginMode.userInformation.isShowing() || getPosition() != loginMode.teamViewer.getCenterIndex())
			return;
		
		KeyCode code = e.getCode();
		if (code == KeyCode.R)
			renameUser(this);
		else if (code == KeyCode.DELETE || code == KeyCode.D)
			loginMode.deleteUser(this);
		else if (code == KeyCode.E)
			exportImage();
	}
	
	//----------------------------------------About Images---------------------------------------------------------------
	
	/**
	 * Reset's the user image back to the default
	 */
	public void setDefaultImage() {
		
		//Delete the Image inside the database
		deleteUserImage();
		
		//Set ImageView to null
		imageView.setImage(null);
	}
	
	/**
	 * The user has the ability to change the Library Image
	 *
	 */
	public void changeUserImage() {
		
		//Check the response
		JavaFXTools.selectAndSaveImage("userImage", InfoTool.getAbsoluteDatabasePathWithSeparator() + getUserName(), Main.specialChooser, Main.window)
				.ifPresent(imageFile -> imageView.setImage(new Image(imageFile.toURI() + "")));
	}
	
	/**
	 * Export the Library image.
	 */
	public void exportImage() {
		
		String absoluteImagePath = JavaFXTools.getAbsoluteImagePath("userImage", InfoTool.getAbsoluteDatabasePathWithSeparator() + getUserName());
		//Check if image exists
		if (absoluteImagePath == null)
			return;
		
		File file = Main.specialChooser.prepareToExportImage(Main.window, absoluteImagePath);
		
		//Check if user selected a folder for the image to be exported
		if (file != null)
			new Thread(() -> {
				if (!ActionTool.copy(absoluteImagePath, file.getAbsolutePath()))
					Platform.runLater(() -> ActionTool.showNotification("Exporting User Image", "Failed to export User image for \n User=[" + getUserName() + "]",
							Duration.millis(2500), NotificationType.SIMPLE));
			}).start();
		
	}
	
	/**
	 * Deletes the user background image
	 */
	private boolean deleteUserImage() {
		
		//Delete the User Image
		JavaFXTools.deleteAnyImageWithTitle("userImage", InfoTool.getAbsoluteDatabasePathWithSeparator() + getUserName());
		
		return true;
	}
	
	/**
	 * @return the userInformationDb
	 */
	public PropertiesDb getUserInformationDb() {
		return userInformationDb;
	}
	
	/**
	 * @param userInformationDb
	 *            the userInformationDb to set
	 */
	public void setUserInformationDb(PropertiesDb userInformationDb) {
		this.userInformationDb = userInformationDb;
	}
	
	/**
	 * Returns the date this user created based on the folder creation date
	 */
	public String getDateCreated() {
		return InfoTool.getFileCreationDate(new File(userInformationDb.getFileAbsolutePath()));
	}
	
	/**
	 * Returns the Time this user created based on the folder creation date
	 */
	public String getTimeCreated() {
		return InfoTool.getFileCreationTime(new File(userInformationDb.getFileAbsolutePath()));
	}
	
	/**
	 * @return the descriptionLabel
	 */
	public Label getDescriptionLabel() {
		return descriptionLabel;
	}
	
}
