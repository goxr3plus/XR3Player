package com.goxr3plus.xr3player.controllers.loginmode;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.xr3player.application.MainLoadUser;
import org.atteo.evo.inflector.English;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.FileCategory;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.UserCategory;
import com.goxr3plus.xr3player.controllers.custom.FlipPanel;
import com.goxr3plus.xr3player.controllers.general.CloseAppBox;
import com.goxr3plus.xr3player.controllers.general.SearchBox;
import com.goxr3plus.xr3player.controllers.general.SearchBox.SearchBoxType;
import com.goxr3plus.xr3player.controllers.general.Viewer;
import com.goxr3plus.xr3player.services.loginmode.UsersLoaderService;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.NetworkingTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * @author GOXR3PLUS
 *
 */
public class LoginMode extends StackPane {

	// -------------------------------------

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
	private HBox botttomHBox;

	@FXML
	private Label usersInfoLabel;

	@FXML
	private ToggleGroup sortByGroup;

	@FXML
	private Button createFirstUser;

	@FXML
	private Button importDatabase;

	@FXML
	private Button exportDatabase;

	@FXML
	private Button deleteDatabase;

	// --------------------------------------------

	// private final CategoryAxis xAxis = new CategoryAxis();
	// private final NumberAxis yAxis = new NumberAxis();
	// defining a series
	// XYChart.Series<String,Number> series = new XYChart.Series<>();
	// private final ObservableList<PieChart.Data> librariesPieChartData =
	// FXCollections.observableArrayList()

	// ---

	/** The logger for this class */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Allows to see the users in a beautiful way
	 */
	public Viewer viewer;

	/**
	 * The Search Box of the LoginMode
	 */
	public SearchBox userSearchBox = new SearchBox(SearchBoxType.USERSSEARCHBOX);

	/** The context menu of the users */
	public UserContextMenu userContextMenu = new UserContextMenu();

	public final UserInformation userInformation = new UserInformation(UserCategory.NO_LOGGED_IN);

	/**
	 * Loads all the information about each user
	 */
	public UsersLoaderService usersLoaderService = new UsersLoaderService();

	public final FlipPanel flipPane = new FlipPanel(Orientation.HORIZONTAL);

	// ---------------------------------------

	/** This InvalidationListener is used during the creation of a new user. */
	private final InvalidationListener userCreationInvalidator = new InvalidationListener() {
		@Override
		public void invalidated(final Observable observable) {

			// Remove the Listener
			Main.renameWindow.showingProperty().removeListener(this);

			// !Showing && !XPressed
			if (!Main.renameWindow.isShowing() && Main.renameWindow.wasAccepted()) {

				Main.window.requestFocus();

				// Check if this name already exists
				final String newName = Main.renameWindow.getUserInput();

				// if can pass
				if (!viewer.getItemsObservableList().stream()
						.anyMatch(user -> ((User) user).getName().equalsIgnoreCase(newName))) {

					if (new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator() + newName).mkdir()) {

						// Create the new user and add it
						final User user = new User(newName, viewer.getItemsObservableList().size(), LoginMode.this);
						viewer.addItem(user, true);

						// Add to PieChart
						// librariesPieChartData.add(new PieChart.Data(newName, 0));
						// series.getData().add(new XYChart.Data<String,Number>(newName, 0));

						// Very well create the UsersInformationDb because it doesn't exist so on the
						// next load it will exist
						IOAction.createFileOrFolder(new File(DatabaseTool.getAbsoluteDatabasePathWithSeparator()
								+ user.getName() + File.separator + "settings"), FileType.DIRECTORY);
						IOAction.createFileOrFolder(new File(user.getUserInformationDb().getFileAbsolutePath()),
								FileType.FILE);

					} else
						AlertTool.showNotification("Error", "An error occured trying to create a new user",
								Duration.seconds(2), NotificationType.ERROR);

					// update the positions
					// updateUsersPosition()
				} else
					AlertTool.showNotification("Dublicate User",
							"Name->" + newName + " is already used from another User...", Duration.millis(2000),
							NotificationType.INFORMATION);

			}
		}
	};

	/**
	 * Constructor
	 */
	public LoginMode() {

		// ----------------------------------FXMLLoader-------------------------------------
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.USER_FXMLS + "LoginMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		// -------------Load the FXML-------------------------------
		try {
			loader.load();
		} catch (final IOException ex) {
			logger.log(Level.WARNING, "", ex);
		}

	}

	/**
	 * Called as soon as FXML file has been loaded
	 */
	@FXML
	private void initialize() {

		// flipPane
		flipPane.setFlipTime(150);
		flipPane.getFront().getChildren().addAll(centerStackPane.getChildren());
		flipPane.getBack().getChildren().addAll(userInformation);

		// centerStackPane
		centerStackPane.getChildren().add(flipPane);

		// Initialize
		viewer = new Viewer(this, horizontalScrollBar);
		quickSearchTextField.visibleProperty().bind(viewer.searchWordProperty().isEmpty().not());
		quickSearchTextField.textProperty().bind(Bindings.concat("Search :> ").concat(viewer.searchWordProperty()));

		// -- botttomHBox
		botttomHBox.getChildren().add(userSearchBox);

		// createUser
		createUser.setOnAction(a -> createNewUser(createUser));

		// newUser
		createFirstUser.setOnAction(a -> createNewUser(createFirstUser.getGraphic(), true));
		createFirstUser.visibleProperty().bind(Bindings.size(viewer.getItemsObservableList()).isEqualTo(0));

		// loginButton
		loginButton.setOnAction(a -> MainLoadUser.startAppWithUser((User) viewer.getSelectedItem()));

		// openUserContextMenu
		openUserContextMenu.setOnAction(a -> {
			final User user = (User) viewer.getSelectedItem();
			final Bounds bounds = user.localToScreen(user.getBoundsInLocal());
			userContextMenu.show(Main.window, bounds.getMinX() + bounds.getWidth() / 3,
					bounds.getMinY() + bounds.getHeight() / 4, user);
		});

		// renameUser
		// renameUser.disableProperty().bind(deleteUser.disabledProperty())
		renameUser.setOnAction(a -> ((User) viewer.getSelectedItem()).renameUser(renameUser));

		// deleteUser
		// deleteUser.disableProperty().bind(newUser.visibleProperty())
		deleteUser.setOnAction(a -> ((User) Main.loginMode.viewer.getSelectedItem()).deleteUser(deleteUser));

		// topBorderPane
		topBorderPane.setRight(new CloseAppBox());

		// previous
		previous.setOnAction(a -> viewer.previous());

		// next
		next.setOnAction(a -> viewer.next());

		// Continue
		usersStackView.getChildren().add(viewer);
		viewer.toBack();

		// visitCreatorHyperLink
		visitCreatorHyperLink.setOnAction(a -> NetworkingTool.openWebSite(InfoTool.WEBSITE_URL));

		// youtubeTutorialsHyperLink
		youtubeTutorialsHyperLink.setOnAction(a -> NetworkingTool.openWebSite(InfoTool.TUTORIALS));

		// ----usersInfoLabel
		usersInfoLabel.textProperty()
				.bind(Bindings.createStringBinding(
						() -> "[ " + viewer.itemsWrapperProperty().sizeProperty().get() + " ] "
								+ English.plural("User", viewer.itemsWrapperProperty().sizeProperty().get()),
						viewer.itemsWrapperProperty().sizeProperty()));

		// == exportDatabase
		exportDatabase.setOnAction(a -> Main.sideBar.exportDatabase());

		// == importDatabase
		importDatabase.setOnAction(a -> Main.sideBar.importDatabase());

		// == deleteDatabase
		deleteDatabase.setOnAction(a -> Main.sideBar.deleteDatabase());

		// == color picker
		final String defaultWebColor = "#ef4949";
		colorPicker.setValue(Color.web(defaultWebColor));
		viewer.setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, "
				+ defaultWebColor + " 87%);");
		colorPicker.setOnAction(a -> Main.applicationProperties.updateProperty("Users-Background-Color",
				JavaFXTool.colorToWebColor(colorPicker.getValue())));
		colorPicker.valueProperty().addListener((observable, oldColor, newColor) -> {

			// Format to WebColor
			final String webColor = JavaFXTool.colorToWebColor(newColor);

			// Set the style
			this.viewer.setStyle("-fx-background-color: linear-gradient(to bottom,transparent 60,#141414 60.2%, "
					+ webColor + "  87%);");
		});
		// sortByGroup
		sortByGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null)
				return;

			// Create a custom comparator
			viewer.sortByComparator(getSortComparator());
		});
	}

	/**
	 * Return the text of the selected sort toggle
	 * 
	 * @return
	 */
	public String getSelectedSortToggleText() {
		return ((RadioMenuItem) sortByGroup.getSelectedToggle()).getText();
	}

	/**
	 * Get the sort comparator
	 * 
	 * @return
	 */
	public Comparator<Node> getSortComparator() {
		final String text = ((RadioMenuItem) sortByGroup.getSelectedToggle()).getText();

		if (text.equalsIgnoreCase("Name Ascendant")) {
			return (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(((User) a).getName(), ((User) b).getName());
		} else if (text.equalsIgnoreCase("Name Descendant")) {
			return (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(((User) b).getName(), ((User) a).getName());
		} else if (text.equalsIgnoreCase("Libraries  Ascendant")) {
			return (a, b) -> Double.compare(((User) b).getTotalLibraries(), ((User) a).getTotalLibraries());
		} else if (text.equalsIgnoreCase("Libraries  Descendant")) {
			return (a, b) -> Double.compare(((User) a).getTotalLibraries(), ((User) b).getTotalLibraries());
		} else if (text.equalsIgnoreCase("Dropbox Accounts  Ascendant")) {
			return (a, b) -> Double.compare(((User) b).getTotalDropboxAccounts(), ((User) a).getTotalDropboxAccounts());
		} else if (text.equalsIgnoreCase("Dropbox Accounts  Descendant")) {
			return (a, b) -> Double.compare(((User) a).getTotalDropboxAccounts(), ((User) b).getTotalDropboxAccounts());
		}

		return null;
	}

	/**
	 * Used to create a new User
	 * 
	 * @param owner
	 */
	public void createNewUser(final Node owner, final boolean... exactPositioning) {

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
	// public XYChart.Series<String,Number> getSeries() {
	// return series;
	// }

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
