package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.control.BreadCrumbBar;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DownloadService;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DropboxService;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DropboxService.DropBoxOperation;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.FileCategory;

public class DropBoxViewer extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private MenuButton topMenuButton;
	
	@FXML
	private MenuItem signOut;
	
	@FXML
	private Button refresh;
	
	@FXML
	private BreadCrumbBar<String> breadCrumbBar;
	
	@FXML
	private TreeView<String> treeView;
	
	@FXML
	private MenuButton deleteMenuButton;
	
	@FXML
	private Button createFolder;
	
	@FXML
	private MenuItem deleteFile;
	
	@FXML
	private MenuItem permanentlyDeleteFile;
	
	@FXML
	private Button renameFile;
	
	@FXML
	private Button downloadFile;
	
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
	private Button authorizationButton2;
	
	@FXML
	private VBox errorVBox;
	
	@FXML
	private JFXButton tryAgain;
	
	@FXML
	private ProgressIndicator tryAgainIndicator;
	
	@FXML
	private Label emptyFolderLabel;
	
	@FXML
	private Label dropBoxAccountsLabel;
	
	@FXML
	private VBox authorizationCodeVBox;
	
	@FXML
	private TextField authorizationCodeTextField;
	
	@FXML
	private Button authorizationCodeOkButton;
	
	@FXML
	private Button authorizationCodeCancelButton;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// -------------------------------------------------------------
	
	private final DropboxService dropBoxService = new DropboxService(this);
	
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
		treeView.setOnMouseClicked(this::mouseClicked);
		
		//refreshLabel
		refreshLabel.visibleProperty().bind(dropBoxService.runningProperty());
		
		//Progress Indicator
		progressIndicator.progressProperty().bind(dropBoxService.progressProperty());
		
		//refresh
		refresh.setOnAction(a -> recreateTree(dropBoxService.getCurrentPath()));
		
		// authorizationButton
		authorizationButton.setOnAction(a -> requestDropBoxAuthorization());
		
		//authorizationButton2
		authorizationButton2.setOnAction(a -> {
			
			//authorizationCodeVBox
			authorizationCodeVBox.setVisible(true);
			
			//Open the default external Browser
			ActionTool.openWebSite(authenticationBrowser.getAuthonticationRequestURL());
		});
		
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
				propertiesDb.updateProperty("DropBox-Access-Tokens",
						( propertiesDb.getProperty("DropBox-Access-Tokens") == null ? "" : propertiesDb.getProperty("DropBox-Access-Tokens") )
								+ ( savedAccountsListView.getItems().isEmpty() ? "" : "<>:<>" ) + accessToken);
				
				//loginVBox
				loginVBox.setVisible(false);
				
				//authorizationCodeVBox
				authorizationCodeVBox.setVisible(false);
				
				//authorizationCodeTextField
				authorizationCodeTextField.clear();
				
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
		
		//authorizationCodeVBox
		authorizationCodeVBox.setVisible(false);
		
		//authorizationCodeCancelButton
		authorizationCodeCancelButton.setOnAction(a -> authorizationCodeVBox.setVisible(false));
		
		//authorizationCodeOkButton
		authorizationCodeOkButton.disableProperty().bind(authorizationCodeTextField.textProperty().isEmpty());
		authorizationCodeOkButton.setOnAction(a -> authenticationBrowser.produceAccessToken(authorizationCodeTextField.getText().trim()));
		
		//authorizationCodeTextField
		authorizationCodeTextField.setOnAction(a -> authenticationBrowser.produceAccessToken(authorizationCodeTextField.getText().trim()));
		
		//signOut
		signOut.setOnAction(a -> {
			
			//cancel the service
			dropBoxService.cancel();
			
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
				
				//DropBoxAccountsLabel
				dropBoxAccountsLabel.setVisible(items.isEmpty());
			}
		});
		
		//tryAgain
		tryAgain.setOnAction(a -> checkForInternetConnection());
		
		//breadCrumbBar
		breadCrumbBar.setOnCrumbAction(event -> {
			
			//Recreate Tree
			String value = event.getSelectedCrumb().getValue();
			if ("DROPBOX ROOT".equals(value))
				recreateTree("");
			else
				recreateTree(dropBoxService.getCurrentPath().split(value)[0] + value);
			
		});
		
		//savedAccountsListView
		savedAccountsListView.setCellFactory(lv -> new ListCell<String>() {
			@Override
			public void updateItem(String item , boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					//String text = item.contains("<>:<>") ? item.split("<>:<>")[0] : item; // get text from item
					setText(item);
					setTooltip(new Tooltip(item));
					this.setGraphic(new ImageView(dropBoxImage));
				}
			}
		});
		savedAccountsListView.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ENTER && !savedAccountsListView.getItems().isEmpty()) {
				
				//AccessToken
				accessToken = savedAccountsListView.getSelectionModel().getSelectedItem();
				
				//Go Make It
				recreateTree("");
				
			}
		});
		
		//downloadFile
		downloadFile.disableProperty().bind(treeView.getSelectionModel().selectedItemProperty().isNull());
		downloadFile.setOnAction(a -> {
			
			//Get the selected file
			DropBoxFileTreeItem selectedItem = (DropBoxFileTreeItem) treeView.getSelectionModel().getSelectedItem();
			
			if (!selectedItem.isDirectory()) {
				
				//Show save dialog	
				File file = Main.specialChooser.showSaveDialog(selectedItem.getValue());
				if (file != null)
					new DownloadService(this).startService(selectedItem.getMetadata(), file.getAbsolutePath());
				
			} else //NOT SUPPORTED YET
				ActionTool.showNotification("No supported", "Folder download is not supported yet :) ", Duration.seconds(2), NotificationType.WARNING);
			
		});
		
		//deleteMenuButton
		deleteMenuButton.disableProperty().bind(treeView.getSelectionModel().selectedItemProperty().isNull());
		
		//deleteFile
		deleteFile.setOnAction(a -> {
			int selectedItems = treeView.getSelectionModel().getSelectedIndices().size();
			if (ActionTool.doQuestion("Delete",
					"Are you sure you want to delete "
							+ ( selectedItems != 1 ? " [ " + selectedItems + " ] items" : " [ " + treeView.getSelectionModel().getSelectedItem().getValue() + " ] " )
							+ " from your Dropbox?",
					deleteMenuButton, Main.window))
				this.dropBoxService.delete(DropBoxOperation.DELETE);
		});
		
		//permanentlyDeleteFile
		permanentlyDeleteFile.setOnAction(a -> {
			int selectedItems = treeView.getSelectionModel().getSelectedIndices().size();
			if (ActionTool.doQuestion("PERMANENT Delete",
					"Are you sure you want to delete "
							+ ( selectedItems != 1 ? " [ " + selectedItems + " ] items" : " [ " + treeView.getSelectionModel().getSelectedItem().getValue() + " ] " )
							+ " from your Dropbox PERMANENTLY?",
					deleteMenuButton, Main.window))
				this.dropBoxService.delete(DropBoxOperation.PERMANENTLY_DELETE);
		});
		
		//createFolder
		createFolder.setOnAction(a -> {
			
			//Show the window
			Main.renameWindow.show("", createFolder, "Create Dropbox Folder", FileCategory.FILE);
			
			// When the Rename Window is closed do the rename
			Main.renameWindow.showingProperty().addListener(new InvalidationListener() {
				
				@Override
				public void invalidated(Observable observable) {
					
					// Remove the Listener
					Main.renameWindow.showingProperty().removeListener(this);
					
					// !Showing
					if (!Main.renameWindow.isShowing()) {
						
						// !XPressed && // Old name != New name
						if (Main.renameWindow.wasAccepted()) {
							
							//Try to create
							dropBoxService.createFolder(dropBoxService.getCurrentPath() + "/" + Main.renameWindow.getInputField().getText());
							
						}
						
					} // RenameWindow is still showing
				}// invalidated
			});
			
		});
		
	}
	
	/**
	 * Refreshes the Saved Accounts Lists View
	 */
	public void refreshSavedAccounts() {
		
		//savedAccountsListView
		Optional.ofNullable(Main.userMode.getUser().getUserInformationDb().getProperty("DropBox-Access-Tokens")).ifPresent(accessTokens -> {
			if (accessTokens.contains("<>:<>")) //Check if we have multiple access tokens
				savedAccountsListView.setItems(Stream.of(accessTokens.split(Pattern.quote("<>:<>"))).collect(Collectors.toCollection(FXCollections::observableArrayList)));
			else if (!accessTokens.isEmpty()) //Check if we have one access token
				savedAccountsListView.setItems(Stream.of(accessTokens).collect(Collectors.toCollection(FXCollections::observableArrayList)));
		});
		
		//DropBoxAccountsLabel
		dropBoxAccountsLabel.setVisible(savedAccountsListView.getItems().isEmpty());
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
		
		//BreadCrumbBar
		if (path.isEmpty()) {
			
			//Build the Model
			TreeItem<String> model = BreadCrumbBar.buildTreeModel("DROPBOX ROOT");
			breadCrumbBar.setSelectedCrumb(model);
			
			//PRINT
			//System.out.println(Arrays.asList(path.split("/")))
		} else {
			
			//Build the ArrayList
			ArrayList<String> arrayList = new ArrayList<>();
			arrayList.add("DROPBOX ROOT");
			arrayList.addAll(Arrays.asList(path.replaceFirst(Pattern.quote("/"), "").split("/")));
			
			//Build the Model
			TreeItem<String> model = BreadCrumbBar.buildTreeModel((String[]) arrayList.toArray(new String[0]));
			
			//Add all the items to the model
			breadCrumbBar.setSelectedCrumb(model);
			
			//PRINT
			//System.out.println(Arrays.asList(path.split("/")))
			
		}
		
		//Start the Service
		dropBoxService.refresh(path);
	}
	
	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent
	 *            [[SuppressWarningsSpartan]]
	 */
	private void mouseClicked(MouseEvent mouseEvent) {
		//Get the selected item
		DropBoxFileTreeItem source = (DropBoxFileTreeItem) treeView.getSelectionModel().getSelectedItem();
		
		// host is not on the game
		if (source == null || source == root) {
			mouseEvent.consume();
			return;
		}
		
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
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
	
	/**
	 * @return the emptyFolderLabel
	 */
	public Label getEmptyFolderLabel() {
		return emptyFolderLabel;
	}
	
	/**
	 * @return the savedAccountsListView
	 */
	public ListView<String> getSavedAccountsListView() {
		return savedAccountsListView;
	}
	
}
