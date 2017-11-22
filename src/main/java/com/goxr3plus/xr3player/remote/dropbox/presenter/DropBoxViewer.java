package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.BreadCrumbBar.BreadCrumbActionEvent;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.database.PropertiesDb;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.remote.dropbox.authorization.DropboxAuthenticationBrowser;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.RefreshService;

public class DropBoxViewer extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private MenuButton topMenuButton;
	
	@FXML
	private MenuItem signOut;
	
	@FXML
	private Button refresh;
	
	@FXML
	private Button collapseTree;
	
	@FXML
	private BreadCrumbBar<String> breadCrumbBar;
	
	@FXML
	private TreeView<String> treeView;
	
	@FXML
	private Label refreshLabel;
	
	@FXML
	private ProgressIndicator progressIndicator;
	
	@FXML
	private VBox loginVBox;
	
	@FXML
	private ListView<String> savedAccountsListView;
	
	@FXML
	private Button loginWithSavedAccount;
	
	@FXML
	private Button deleteSavedAccount;
	
	@FXML
	private Button authorizationButton;
	
	@FXML
	private VBox errorVBox;
	
	@FXML
	private JFXButton tryAgain;
	
	@FXML
	private ProgressIndicator tryAgainIndicator;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// -------------------------------------------------------------
	
	private final RefreshService refreshService = new RefreshService(this);
	
	// -------------------------------------------------------------
	
	//Create a fake root element
	private final DropBoxFileTreeItem root = new DropBoxFileTreeItem("", null);
	
	// -------------------------------------------------------------
	
	private final DropboxAuthenticationBrowser authenticationBrowser = new DropboxAuthenticationBrowser();
	
	private String accessToken;
	
	public static final Image dropBoxImage = InfoTool.getImageFromResourcesFolder("dropbox.png");
	
	/**
	 * Constructor.
	 */
	public DropBoxViewer() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "DropboxViewer.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//TreeView
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// Mouse Released Event
		treeView.setOnMouseReleased(this::treeViewMouseReleased);
		
		//refreshLabel
		refreshLabel.visibleProperty().bind(refreshService.runningProperty());
		
		//Progress Indicator
		progressIndicator.progressProperty().bind(refreshService.progressProperty());
		
		//collapseTree
		collapseTree.setOnAction(a -> {
			//Trick for CPU based on this question -> https://stackoverflow.com/questions/15490268/manually-expand-collapse-all-treeitems-memory-cost-javafx-2-2
			root.setExpanded(false);
			
			//Set not expanded all the children
			collapseTreeView(root, false);
			
			//Trick for CPU
			root.setExpanded(true);
		});
		
		//refresh
		refresh.setOnAction(a -> recreateTree(""));
		
		// authorizationButton
		authorizationButton.setOnAction(a -> requestDropBoxAuthorization());
		
		//Add binding to accessTokenProperty
		authenticationBrowser.accessTokenProperty().addListener((observable , oldValue , newValue) -> {
			//Check if empty
			if (!newValue.isEmpty()) {
				accessToken = newValue;
				
				//Show message to the User
				ActionTool.showNotification("Authantication", "Successfully authenticated to your Dropbox Account", Duration.millis(2000), NotificationType.SIMPLE,
						DropBoxViewer.dropBoxImage);
				
				//Save on the database
				PropertiesDb propertiesDb = Main.userMode.getUser().getUserInformationDb();
				propertiesDb.updateProperty("DropBox-Access-Tokens", propertiesDb.getProperty("DropBox-Access-Tokens") + "<>:<>" + accessToken);
				
				//loginVBox
				loginVBox.setVisible(false);
				
				//Go Make It
				recreateTree("");
				
				//Refresh Saved Accounts
				refreshSavedAccounts();
			}
		});
		
		//errorVBox
		errorVBox.setVisible(false);
		
		//loginVBox
		loginVBox.setVisible(true);
		
		//signOut
		signOut.setOnAction(a -> {
			
			//cancel the service
			refreshService.cancel();
			
			//loginVBox
			loginVBox.setVisible(true);
			
		});
		
		//loginWithSavedAccount
		loginWithSavedAccount.disableProperty().bind(savedAccountsListView.getSelectionModel().selectedItemProperty().isNull());
		loginWithSavedAccount.setOnAction(a -> {
			
			//AccessToken
			accessToken = savedAccountsListView.getSelectionModel().getSelectedItem();
			
			//Go Make It
			recreateTree("");
		});
		
		//deleteSavedAccount
		deleteSavedAccount.disableProperty().bind(loginWithSavedAccount.disabledProperty());
		deleteSavedAccount.setOnAction(a -> {
			
			//Clear the selected item
			ObservableList<String> items = savedAccountsListView.getItems();
			if (items != null) {
				items.remove(savedAccountsListView.getSelectionModel().getSelectedItem());
				Main.userMode.getUser().getUserInformationDb().updateProperty("DropBox-Access-Tokens", items.stream().collect(Collectors.joining("<>:<>")));
			}
		});
		
		//tryAgain
		tryAgain.setOnAction(a -> checkForInternetConnection());
		
		//breadCrumbBar
		breadCrumbBar.setOnCrumbAction(new EventHandler<BreadCrumbBar.BreadCrumbActionEvent<String>>() {
			@Override
			public void handle(BreadCrumbActionEvent<String> bae) {
				
				//Recreate Tree
				String value = breadCrumbBar.getSelectedCrumb().getValue();
				recreateTree(value.isEmpty() ? "" : "/" + value);
				
				System.out.println("Entered Bread Crumb Bar Action");			
			}
		});
		
	}
	
	/**
	 * Refreshes the Saved Accounts Lists View
	 */
	public void refreshSavedAccounts() {
		
		//savedAccountsListView
		Optional.ofNullable(Main.userMode.getUser().getUserInformationDb().getProperty("DropBox-Access-Tokens")).ifPresent(accessTokens -> savedAccountsListView
				.setItems(Stream.of(accessTokens.split(Pattern.quote("<>:<>"))).collect(Collectors.toCollection(FXCollections::observableArrayList))));
	}
	
	/**
	 * Request XR3Player Authorization to have access to his/her DropBox Account
	 */
	public void requestDropBoxAuthorization() {
		
		//Show authentication browser
		authenticationBrowser.showAuthenticationWindow();
		
	}
	
	/**
	 * Collapses the whole TreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(TreeItem<String> item , boolean expanded) {
		if (item == null || item.isLeaf())
			return;
		
		item.setExpanded(expanded);
		item.getChildren().forEach(child -> collapseTreeView(child, expanded));
	}
	
	/**
	 * Recreates the TreeView
	 */
	public void recreateTree(String path) {
		
		//Clear all the children
		root.getChildren().clear();
		
		//BreadCrumbBar
		if (path.isEmpty()) {
			
			//Build the Model
			TreeItem<String> model = BreadCrumbBar.buildTreeModel("");
			breadCrumbBar.setSelectedCrumb(model);
			
			//PRINT
			System.out.println(Arrays.asList(path.split("/")));
		} else {
			
			//Build the Model
			TreeItem<String> model = BreadCrumbBar.buildTreeModel((String[]) Arrays.asList(path.split("/")).toArray(new String[0]));
			
			//Add all the items to the model
			breadCrumbBar.setSelectedCrumb(model);
			
			//PRINT
			System.out.println(Arrays.asList(path.split("/")));
			
		}
		
		//Start the Service
		refreshService.startService(path);
	}
	
	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent
	 *            [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseReleased(MouseEvent mouseEvent) {
		//Get the selected item
		DropBoxFileTreeItem source = (DropBoxFileTreeItem) treeView.getSelectionModel().getSelectedItem();
		
		// host is not on the game
		if (source == null || source == root) {
			mouseEvent.consume();
			return;
		}
		
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
			if (source.isDirectory()) {
				recreateTree(source.getMetadata().getPathLower());
			}
			
		}
	}
	
	/**
	 * Checks for internet connection
	 */
	void checkForInternetConnection() {
		
		//tryAgainIndicator
		tryAgainIndicator.setVisible(true);
		
		//Check for internet connection
		Thread thread = new Thread(() -> {
			boolean hasInternet = InfoTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {
				errorVBox.setVisible(!hasInternet);
				tryAgainIndicator.setVisible(false);
			});
		}, "Internet Connection Tester Thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * @return the root of the tree
	 */
	public DropBoxFileTreeItem getRoot() {
		return root;
	}
	
	/**
	 * @return the progressIndicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}
	
	/**
	 * @return the authenticationBrowser
	 */
	public DropboxAuthenticationBrowser getAuthenticationBrowser() {
		return authenticationBrowser;
	}
	
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * @return the treeView
	 */
	public TreeView<String> getTreeView() {
		return treeView;
	}
	
	/**
	 * @return the refreshLabel
	 */
	public Label getRefreshLabel() {
		return refreshLabel;
	}
	
	/**
	 * @return the topMenuButton
	 */
	public MenuButton getTopMenuButton() {
		return topMenuButton;
	}
	
	/**
	 * @return the loginVBox
	 */
	public VBox getLoginVBox() {
		return loginVBox;
	}
	
	/**
	 * @return the errorVBox
	 */
	public VBox getErrorVBox() {
		return errorVBox;
	}
	
	/**
	 * @return the breadCrumbBar
	 */
	public BreadCrumbBar<String> getBreadCrumbBar() {
		return breadCrumbBar;
	}
	
}
