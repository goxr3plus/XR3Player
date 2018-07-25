package main.java.com.goxr3plus.xr3player.application.modes.loginmode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atteo.evo.inflector.English;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.modes.loginmode.UserInformation.UserCategory;
import main.java.com.goxr3plus.xr3player.application.modes.loginmode.services.UsersLoaderService;
import main.java.com.goxr3plus.xr3player.application.presenter.CloseAppBox;
import main.java.com.goxr3plus.xr3player.application.presenter.SearchBox;
import main.java.com.goxr3plus.xr3player.application.presenter.SearchBox.SearchBoxType;
import main.java.com.goxr3plus.xr3player.application.presenter.Viewer;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.flippane.FlipPanel;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool.FileType;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.FileCategory;

/**
 * @author GOXR3PLUS
 *
 */
public class LoginMode extends StackPane {
	
	//-------------------------------------
	
	@FXML
	private ImageView backgroundImageView;
	
	@FXML
	private Hyperlink youtubeTutorialsHyperLink;
	
	@FXML
	private Hyperlink visitCreatorHyperLink;
	
	@FXML
	private VBox downloadsVBox;
	
	@FXML
	private Label sourceForgeDownloadsLabel1;
	
	@FXML
	private Label sourceForgeDownloadsLabel;
	
	@FXML
	private Label gitHubDownloadsLabel;
	
	@FXML
	private BorderPane topBorderPane;
	
	@FXML
	private Label xr3PlayerLabel;
	
	@FXML
	private StackPane centerStackPane;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private StackPane usersStackView;
	
	@FXML
	private ScrollBar horizontalScrollBar;
	
	@FXML
	private Label quickSearchTextField;
	
	@FXML
	private GridPane topGrid;
	
	@FXML
	private JFXToggleButton selectionModeToggle;
	
	@FXML
	private HBox toolBarHBox;
	
	@FXML
	private Button deleteUser;
	
	@FXML
	private Button renameUser;
	
	@FXML
	private Button loginButton;
	
	@FXML
	private JFXButton previous;
	
	@FXML
	private JFXButton createUser;
	
	@FXML
	private JFXButton next;
	
	@FXML
	private Button openUserContextMenu;
	
	@FXML
	private ColorPicker colorPicker;
	
	@FXML
	private Label usersInfoLabel;
	
	@FXML
	private HBox botttomHBox;
	
	@FXML
	private Button createFirstUser;
	
	@FXML
	private Button importDatabase;
	
	@FXML
	private Button exportDatabase;
	
	@FXML
	private Button deleteDatabase;
	
	// --------------------------------------------
	
	//private final CategoryAxis xAxis = new CategoryAxis();
	//private final NumberAxis yAxis = new NumberAxis();
	//defining a series
	//XYChart.Series<String,Number> series = new XYChart.Series<>();
	//private final ObservableList<PieChart.Data> librariesPieChartData = FXCollections.observableArrayList()	
	
	//---
	
	/** The logger for this class */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Allows to see the users in a beautiful way
	 */
	public Viewer teamViewer;
	
	/**
	 * The Search Box of the LoginMode
	 */
	public SearchBox userSearchBox = new SearchBox(SearchBoxType.USERSSEARCHBOX);
	
	/** The context menu of the users */
	public UserContextMenu userContextMenu = new UserContextMenu(this);
	
	public final UserInformation userInformation = new UserInformation(UserCategory.NO_LOGGED_IN);
	
	/**
	 * Loads all the information about each user
	 */
	public UsersLoaderService usersLoaderService = new UsersLoaderService();
	
	public final FlipPanel flipPane = new FlipPanel(Orientation.HORIZONTAL);
	
	//---------------------------------------
	
	/** This InvalidationListener is used during the creation of a new user. */
	private final InvalidationListener userCreationInvalidator = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			
			// Remove the Listener
			Main.renameWindow.showingProperty().removeListener(this);
			
			// !Showing && !XPressed
			if (!Main.renameWindow.isShowing() && Main.renameWindow.wasAccepted()) {
				
				Main.window.requestFocus();
				
				// Check if this name already exists
				String newName = Main.renameWindow.getUserInput();
				
				// if can pass
				if (!teamViewer.getItemsObservableList().stream().anyMatch(user -> ( (User) user ).getUserName().equalsIgnoreCase(newName))) {
					
					if (new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + newName).mkdir()) {
						
						//Create the new user and add it 
						User user = new User(newName, teamViewer.getItemsObservableList().size(), LoginMode.this);
						teamViewer.addItem(user, true);
						
						//Add to PieChart
						//						librariesPieChartData.add(new PieChart.Data(newName, 0));
						//series.getData().add(new XYChart.Data<String,Number>(newName, 0));
						
						//Very well create the UsersInformationDb because it doesn't exist so on the next load it will exist
						ActionTool.createFileOrFolder(new File(InfoTool.getAbsoluteDatabasePathWithSeparator() + user.getUserName() + File.separator + "settings"),
								FileType.DIRECTORY);
						ActionTool.createFileOrFolder(new File(user.getUserInformationDb().getFileAbsolutePath()), FileType.FILE);
						
					} else
						ActionTool.showNotification("Error", "An error occured trying to create a new user", Duration.seconds(2), NotificationType.ERROR);
					
					// update the positions
					//updateUsersPosition()
				} else
					ActionTool.showNotification("Dublicate User", "Name->" + newName + " is already used from another User...", Duration.millis(2000),
							NotificationType.INFORMATION);
				
			}
		}
	};
	
	/**
	 * Constructor
	 */
	public LoginMode() {
		
		// ----------------------------------FXMLLoader-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.USER_FXMLS + "LoginMode.fxml"));
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
		
		//flipPane
		flipPane.setFlipTime(150);
		flipPane.getFront().getChildren().addAll(centerStackPane.getChildren());
		flipPane.getBack().getChildren().addAll(userInformation);
		
		//centerStackPane
		centerStackPane.getChildren().add(flipPane);
		
		//Initialize
		teamViewer = new Viewer(this, horizontalScrollBar);
		quickSearchTextField.visibleProperty().bind(teamViewer.searchWordProperty().isEmpty().not());
		quickSearchTextField.textProperty().bind(Bindings.concat("Search :> ").concat(teamViewer.searchWordProperty()));
		
		// -- botttomHBox
		botttomHBox.getChildren().add(userSearchBox);
		
		// createUser
		createUser.setOnAction(a -> createNewUser(createUser));
		
		//newUser
		createFirstUser.setOnAction(a -> createNewUser(createFirstUser.getGraphic(), true));
		createFirstUser.visibleProperty().bind(Bindings.size(teamViewer.getItemsObservableList()).isEqualTo(0));
		
		//loginButton
		loginButton.setOnAction(a -> Main.startAppWithUser((User) teamViewer.getSelectedItem()));
		
		//openUserContextMenu
		openUserContextMenu.setOnAction(a -> {
			User user = (User) teamViewer.getSelectedItem();
			Bounds bounds = user.localToScreen(user.getBoundsInLocal());
			userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3, bounds.getMinY() + bounds.getHeight() / 4, user);
		});
		
		//renameUser
		//renameUser.disableProperty().bind(deleteUser.disabledProperty())
		renameUser.setOnAction(a -> ( (User) teamViewer.getSelectedItem() ).renameUser(renameUser));
		
		//deleteUser
		//deleteUser.disableProperty().bind(newUser.visibleProperty())
		deleteUser.setOnAction(a -> ( (User) Main.loginMode.teamViewer.getSelectedItem() ).deleteUser(deleteUser));
		
		//topBorderPane
		topBorderPane.setRight(new CloseAppBox());
		
		// previous
		previous.setOnAction(a -> teamViewer.previous());
		
		// next
		next.setOnAction(a -> teamViewer.next());
		
		//Continue
		usersStackView.getChildren().add(teamViewer);
		teamViewer.toBack();
		
		//visitCreatorHyperLink
		visitCreatorHyperLink.setOnAction(a -> ActionTool.openWebSite(InfoTool.WEBSITE_URL));
		
		//youtubeTutorialsHyperLink
		youtubeTutorialsHyperLink.setOnAction(a -> ActionTool.openWebSite(InfoTool.TUTORIALS));
		
		//----usersInfoLabel
		usersInfoLabel.textProperty().bind(Bindings.createStringBinding(
				() -> "[ " + teamViewer.itemsWrapperProperty().sizeProperty().get() + " ] " + English.plural("User", teamViewer.itemsWrapperProperty().sizeProperty().get()),
				teamViewer.itemsWrapperProperty().sizeProperty()));
		
		//== exportDatabase
		exportDatabase.setOnAction(a -> Main.sideBar.exportDatabase());
		
		//== importDatabase
		importDatabase.setOnAction(a -> Main.sideBar.importDatabase());
		
		//== deleteDatabase
		deleteDatabase.setOnAction(a -> Main.sideBar.deleteDatabase());
		
		//== color picker
		String defaultWebColor = "#ef4949";
		colorPicker.setValue(Color.web(defaultWebColor));
		teamViewer.setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, " + defaultWebColor + " 87%);");
		colorPicker.setOnAction(a -> Main.applicationProperties.updateProperty("Users-Background-Color", JavaFXTools.colorToWebColor(colorPicker.getValue())));
		colorPicker.valueProperty().addListener((observable , oldColor , newColor) -> {
			
			//Format to WebColor
			String webColor = JavaFXTools.colorToWebColor(newColor);
			
			//Set the style
			this.teamViewer.setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, " + webColor + "  87%);");
		});
	}
	
	/**
	 * Used to create a new User
	 * 
	 * @param owner
	 */
	public void createNewUser(Node owner , boolean... exactPositioning) {
		
		// Open rename window
		Main.renameWindow.show("", owner, "Creating new User", FileCategory.DIRECTORY, exactPositioning);
		
		// Add the showing listener
		Main.renameWindow.showingProperty().addListener(userCreationInvalidator);
		
	}
	
	/**
	 * Gets the previous.
	 *
	 * @return the previous
	 */
	public Button getPrevious() {
		return previous;
	}
	
	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	public Button getNext() {
		return next;
	}
	
	/**
	 * @return the xr3PlayerLabel
	 */
	public Label getXr3PlayerLabel() {
		return xr3PlayerLabel;
	}
	
	/**
	 * @return the colorPicker
	 */
	public ColorPicker getColorPicker() {
		return colorPicker;
	}
	
	/**
	 * @return the centerStackPane
	 */
	public StackPane getCenterStackPane() {
		return centerStackPane;
	}
	
	/**
	 * @return the series
	 */
	//	public XYChart.Series<String,Number> getSeries() {
	//		return series;
	//	}
	
	/**
	 * @return the downloadsVBox
	 */
	public VBox getDownloadsVBox() {
		return downloadsVBox;
	}
	
	/**
	 * @return the sourceForgeDownloadsLabel
	 */
	public Label getSourceForgeDownloadsLabel() {
		return sourceForgeDownloadsLabel;
	}
	
	/**
	 * @return the gitHubDownloadsLabel
	 */
	public Label getGitHubDownloadsLabel() {
		return gitHubDownloadsLabel;
	}
	
	/**
	 * @return the backgroundImageView
	 */
	public ImageView getBackgroundImageView() {
		return backgroundImageView;
	}
	
}
