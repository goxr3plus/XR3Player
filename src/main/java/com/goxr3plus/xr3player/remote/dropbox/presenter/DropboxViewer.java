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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.database.PropertiesDb;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.remote.dropbox.authorization.DropboxAuthanticationBrowser;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DownloadService;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DropboxService;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.DropboxService.DropBoxOperation;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.FileCategory;

public class DropboxViewer extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private ProgressIndicator cachedSearchIndicator;
	
	@FXML
	private StackPane innerStackPane;
	
	@FXML
	private TextField searchField;
	
	@FXML
	private MenuButton topMenuButton;
	
	@FXML
	private MenuItem signOut;
	
	@FXML
	private Button refresh;
	
	@FXML
	private BreadCrumbBar<String> breadCrumbBar;
	
	@FXML
	private Label searchResultsLabel;
	
	@FXML
	private MenuButton deleteMenuButton;
	
	@FXML
	private Button openFolder;
	
	@FXML
	private Button createFolder;
	
	@FXML
	private MenuItem deleteFile;
	
	@FXML
	private MenuItem permanentlyDeleteFile;
	
	@FXML
	private Button renameButton;
	
	@FXML
	private Button downloadFile;
	
	@FXML
	private Label refreshLabel;
	
	@FXML
	private ProgressIndicator progressIndicator;
	
	@FXML
	private Button cancelDropBoxService;
	
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
	private VBox errorPane;
	
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
	
	@FXML
	private VBox refreshVBox;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// -------------------------------------------------------------
	
	private final DropboxService dropBoxService = new DropboxService(this);
	
	// -------------------------------------------------------------
	
	// -------------------------------------------------------------
	
	public static final Image dropBoxImage = InfoTool.getImageFromResourcesFolder("dropbox.png");
	
	// -------------------------------------------------------------	
	private String accessToken;
	
	private final DropboxAuthanticationBrowser authenticationBrowser;
	
	private final DropboxFilesTableViewer dropboxFilesTableViewer;
	
	private final DropboxFileContextMenu fileContextMenu = new DropboxFileContextMenu();
	
	/**
	 * Constructor.
	 */
	public DropboxViewer() {
		authenticationBrowser = new DropboxAuthanticationBrowser();
		dropboxFilesTableViewer = new DropboxFilesTableViewer();
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.DROPBOX_FXMLS + "DropboxViewer.fxml"));
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
		
		//DropboxFilesTableViewer
		innerStackPane.getChildren().add(dropboxFilesTableViewer);
		dropboxFilesTableViewer.toBack();
		
		//refreshVBox
		refreshVBox.visibleProperty().bind(getDropBoxService().runningProperty());
		
		//Progress Indicator
		progressIndicator.progressProperty().bind(getDropBoxService().progressProperty());
		
		//refresh
		refresh.setOnAction(a -> {
			if (!searchField.getText().isEmpty())
				search(searchField.getText());
			else
				recreateTableView(getDropBoxService().getCurrentPath());
		});
		
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
						JavaFXTools.getImageView(DropboxViewer.dropBoxImage, -1, -1));
				
				//Save on the database
				PropertiesDb propertiesDb = Main.userInfoMode.getUser().getUserInformationDb();
				propertiesDb.updateProperty("DropBox-Access-Tokens",
						( propertiesDb.getProperty("DropBox-Access-Tokens") == null ? "" : propertiesDb.getProperty("DropBox-Access-Tokens") )
								+ ( savedAccountsListView.getItems().isEmpty() ? "" : "<>:<>" ) + accessToken);
				
				//loginVBox
				loginVBox.setVisible(false);
				
				//authorizationCodeVBox
				authorizationCodeVBox.setVisible(false);
				
				//authorizationCodeTextField
				authorizationCodeTextField.clear();
				
				//Connect
				connect(accessToken);
				
				//Refresh Saved Accounts
				refreshSavedAccounts();
			}
		});
		
		//errorVBox
		errorPane.setVisible(false);
		
		//loginVBox
		loginVBox.setVisible(true);
		
		//authorizationCodeVBox
		authorizationCodeVBox.setVisible(false);
		
		//openFolder
		openFolder.setOnAction(a -> {
			DropboxFile selectedFile = dropboxFilesTableViewer.getTableView().getSelectionModel().getSelectedItem();
			if (selectedFile != null)
				recreateTableView(selectedFile.getMetadata().getPathLower());
		});
		
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
			getDropBoxService().cancel();
			
			//cancel cachedSearch Service
			getDropBoxService().getSearchCacheService().cancel();
			
			//loginVBox
			loginVBox.setVisible(true);
			
		});
		
		//loginWithSavedAccount
		loginWithSavedAccount.disableProperty().bind(savedAccountsListView.getSelectionModel().selectedItemProperty().isNull());
		loginWithSavedAccount.setOnAction(a -> connect(savedAccountsListView.getSelectionModel().getSelectedItem()));
		
		//deleteSavedAccount
		deleteSavedAccount.disableProperty().bind(loginWithSavedAccount.disabledProperty());
		deleteSavedAccount.setOnAction(a -> deleteSelectedAccount());
		
		//tryAgain
		tryAgain.setOnAction(a -> checkForInternetConnection());
		
		//breadCrumbBar
		breadCrumbBar.setOnCrumbAction(event -> {
			
			//Recreate Tree
			String value = event.getSelectedCrumb().getValue();
			if ("DROPBOX ROOT".equals(value))
				recreateTableView("");
			else
				recreateTableView(getDropBoxService().getCurrentPath().split(value)[0] + value);
			
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
				
				//Connect
				connect(savedAccountsListView.getSelectionModel().getSelectedItem());
				
			} else if (key.getCode() == KeyCode.DELETE && !savedAccountsListView.getItems().isEmpty())
				deleteSelectedAccount();
			
		});
		
		//downloadFile
		downloadFile.disableProperty().bind(dropboxFilesTableViewer.getSelectionModel().selectedItemProperty().isNull());
		downloadFile.setOnAction(a -> downloadFile(dropboxFilesTableViewer.getSelectionModel().getSelectedItem()));
		
		//deleteMenuButton
		deleteMenuButton.disableProperty().bind(dropboxFilesTableViewer.getSelectionModel().selectedItemProperty().isNull());
		
		//deleteFile
		deleteFile.setOnAction(a -> deleteSelectedFiles(false));
		
		//permanentlyDeleteFile
		permanentlyDeleteFile.setOnAction(a -> deleteSelectedFiles(true));
		
		//renameButton
		renameButton.disableProperty().bind(deleteMenuButton.disabledProperty());
		renameButton.setOnAction(a -> this.renameFile(dropboxFilesTableViewer.getSelectionModel().getSelectedItem(), renameButton));
		
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
							getDropBoxService().createFolder(getDropBoxService().getCurrentPath() + "/" + Main.renameWindow.getInputField().getText());
							
						}
						
					} // RenameWindow is still showing
				}// invalidated
			});
			
		});
		
		//cancelDropBoxService
		cancelDropBoxService.setOnAction(a -> {
			
			//It was on Search?
			if (dropBoxService.getOperation() == DropBoxOperation.SEARCH)
				dropBoxService.getSearchCacheService().prepareCachedSearch(dropBoxService.getClient());
			
			//Recreate
			recreateTableView("");
		});
		
		//searchResultsLabel
		searchResultsLabel.setVisible(false);
		
		//createFolder
		createFolder.disableProperty().bind(searchResultsLabel.visibleProperty());
		
		//searchField
		searchField.textProperty().addListener((observable , oldValue , newValue) -> {
			if (searchField.getText().isEmpty())
				search("");
		});
		searchField.setOnAction(a -> search(searchField.getText()));
		
		//cachedSearchIndicator
		cachedSearchIndicator.progressProperty().bind(getDropBoxService().getSearchCacheService().progressProperty());
		
		//Trying to change the "Done" Text of ProgressIndicator from Cached Search
		//		cachedSearchIndicator.progressProperty().addListener((observable , oldValue , newValue) -> {
		//			// If progress is 100% then show Text
		//			if (newValue.doubleValue() >= 1) {
		//				
		//				// Apply CSS so you can lookup the text
		//				cachedSearchIndicator.applyCss();
		//				
		//				// This text replaces "Done"
		//				//( (Text) cachedSearchIndicator.lookup(".text.percentage") ).setText("Cached Search Ready");
		//				Text text = ( (Text) cachedSearchIndicator.lookup(".percentage") );
		//				text.setText("23");
		//				
		//				cachedSearchIndicator.applyCss();
		//				
		//				progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
		//				
		//				cachedSearchIndicator.applyCss();
		//				
		//				System.out.println("Cached Search Ready!!!!");
		//			}
		//			
		//		});
	}
	
	/**
	 * Deletes the current selected dropbox account
	 */
	private void deleteSelectedAccount() {
		
		//Clear the selected item
		ObservableList<String> items = savedAccountsListView.getItems();
		if (items != null) {
			
			if (ActionTool.doQuestion("Deleting Dropbox Account", "Are you soore you want to delete selected Dropbox Account ?", savedAccountsListView, Main.window)) {
				
				//Remove the selected items
				items.remove(savedAccountsListView.getSelectionModel().getSelectedIndex());
				
				//Refresh the properties database
				Main.userInfoMode.getUser().getUserInformationDb().updateProperty("DropBox-Access-Tokens", items.stream().collect(Collectors.joining("<>:<>")));
			}
			
			//DropBoxAccountsLabel
			dropBoxAccountsLabel.setVisible(items.isEmpty());
		}
	}
	
	//------------------------------------------------------------------------------------------
	
	/**
	 * Connect to the given user account
	 * 
	 * @param accessToken
	 */
	private void connect(String accessToken) {
		this.accessToken = accessToken;
		
		//Clear CachedService Search
		getDropBoxService().getSearchCacheService().getCachedList().clear();
		
		//Create the TableView
		recreateTableView("");
	}
	
	//------------------------------------------------------------------------------------------
	
	/**
	 * Prepare to delete selected file
	 * 
	 * @param permanent
	 *            True = permanently , false = not permanently
	 */
	public void deleteSelectedFiles(boolean permanent) {
		int selectedItems = dropboxFilesTableViewer.getSelectionModel().getSelectedIndices().size();
		
		if (!permanent) {
			if (ActionTool.doQuestion("Delete",
					"Are you sure you want to delete "
							+ ( selectedItems != 1 ? " [ " + selectedItems + " ] items" : " [ " + dropboxFilesTableViewer.getSelectionModel().getSelectedItem().getTitle() + " ] " )
							+ " from your Dropbox?",
					deleteMenuButton, Main.window))
				this.getDropBoxService().delete(DropBoxOperation.DELETE);
		} else if (ActionTool.doQuestion("PERMANENT Delete",
				"Are you sure you want to delete "
						+ ( selectedItems != 1 ? " [ " + selectedItems + " ] items" : " [ " + dropboxFilesTableViewer.getSelectionModel().getSelectedItem().getTitle() + " ] " )
						+ " from your Dropbox PERMANENTLY?",
				deleteMenuButton, Main.window))
			this.getDropBoxService().delete(DropBoxOperation.PERMANENTLY_DELETE);
		
	}
	
	/**
	 * Prepare to delete selected file
	 * 
	 * @param permanent
	 *            True = permanently , false = not permanently
	 */
	public void deleteFile(DropboxFile dropboxFile , boolean permanent) {
		
		if (!permanent) {
			if (ActionTool.doQuestion("Delete", "Are you sure you want to delete [ " + dropboxFile.getTitle() + " ]  from your Dropbox?", deleteMenuButton, Main.window))
				this.getDropBoxService().delete(DropBoxOperation.DELETE);
		} else if (ActionTool.doQuestion("PERMANENT Delete", "Are you sure you want to delete [" + dropboxFile.getTitle() + " ]  from your Dropbox PERMANENTLY?", deleteMenuButton,
				Main.window))
			this.getDropBoxService().delete(DropBoxOperation.PERMANENTLY_DELETE);
		
	}
	
	/**
	 * Prepare for downloading the selected file
	 */
	public void downloadFile(DropboxFile dropboxFile) {
		
		//Simple File
		if (!dropboxFile.isDirectory()) {
			
			//Show save dialog	
			File file = Main.specialChooser.showSaveDialog(dropboxFile.getTitle());
			if (file != null)
				new DownloadService(this).startService(dropboxFile, file.getAbsolutePath());
			
			//Directory
		} else {
			
			//Show save dialog	
			File file = Main.specialChooser.showSaveDialog(dropboxFile.getTitle() + ".zip");
			if (file != null)
				new DownloadService(this).startService(dropboxFile, file.getAbsolutePath());
		}
	}
	
	/**
	 * Prepare to rename a DropboxFile
	 * 
	 * @param dropboxFile
	 */
	public void renameFile(DropboxFile dropboxFile , Node node) {
		// Open Window
		Main.renameWindow.show(InfoTool.getFileTitle(dropboxFile.getMetadata().getName()), node, "Media Renaming", FileCategory.FILE);
		String oldName = dropboxFile.getMetadata().getName();
		
		// Bind
		dropboxFile.titleProperty().bind(Main.renameWindow.getInputField().textProperty());
		
		// When the Rename Window is closed do the rename
		Main.renameWindow.showingProperty().addListener(new InvalidationListener() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			public void invalidated(Observable observable) {
				
				// Remove the Listener
				Main.renameWindow.showingProperty().removeListener(this);
				
				// !Showing
				if (!Main.renameWindow.isShowing()) {
					
					// Remove Binding
					dropboxFile.titleProperty().unbind();
					
					String newName = Main.renameWindow.getInputField().getText()
							+ ( dropboxFile.isDirectory() ? "" : "." + InfoTool.getFileExtension(dropboxFile.getMetadata().getName()) );
					
					// !XPressed && // Old name != New name
					if (Main.renameWindow.wasAccepted() && !oldName.equals(newName)) {
						String parent = new File(dropboxFile.getMetadata().getPathLower()).getParent();
						
						//Try to do it
						getDropBoxService().rename(dropboxFile, parent.replace("\\", "/") + ( parent.equals("\\") ? "" : "/" ) + newName);
						
						//						System.out.println("Old Name: " + dropboxFile.getMetadata().getPathLower());
						//						
						//						System.out.println("New Name: " + parent.replace("\\", "/") + ( parent.equals("\\") ? "" : "/" ) + newName);
						//						
					} else // X is pressed by user || // Old name == New name
						dropboxFile.titleProperty().set(oldName);
					
				} // RenameWindow is still showing
			}// invalidated
		});
		//}
	}
	
	//------------------------------------------------------------------------------------------------------
	
	/**
	 * Refreshes the Saved Accounts Lists View
	 */
	public void refreshSavedAccounts() {
		
		//savedAccountsListView
		Optional.ofNullable(Main.userInfoMode.getUser().getUserInformationDb().getProperty("DropBox-Access-Tokens")).ifPresent(accessTokens -> {
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
	 * Starts the Dropbox Service Search functionality based on the given word
	 * 
	 * @param searchWord
	 */
	public void search(String searchWord) {
		
		//Navigate back to root if searchWord is empty
		if (searchWord.isEmpty())
			recreateTableView("");
		else
			getDropBoxService().search(searchWord);
	}
	
	/**
	 * Recreates the TableView
	 */
	public void recreateTableView(String path) {
		
		//BreadCrumbBar
		if (path.isEmpty()) {
			
			//Build the Model
			breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel("DROPBOX ROOT"));
			
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
		getDropBoxService().refresh(path);
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
				errorPane.setVisible(!hasInternet);
				tryAgainIndicator.setVisible(false);
			});
		}, "Internet Connection Tester Thread");
		thread.setDaemon(true);
		thread.start();
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
	public DropboxAuthanticationBrowser getAuthenticationBrowser() {
		return authenticationBrowser;
	}
	
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
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
		return errorPane;
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
	
	/**
	 * @return the searchResultsLabel
	 */
	public Label getSearchResultsLabel() {
		return searchResultsLabel;
	}
	
	/**
	 * @return the searchField
	 */
	public TextField getSearchField() {
		return searchField;
	}
	
	/**
	 * @return the dropboxFilesTableViewer
	 */
	public DropboxFilesTableViewer getDropboxFilesTableViewer() {
		return dropboxFilesTableViewer;
	}
	
	/**
	 * @return the fileContextMenu
	 */
	public DropboxFileContextMenu getFileContextMenu() {
		return fileContextMenu;
	}
	
	/**
	 * @return the openFolder
	 */
	public Button getOpenFolder() {
		return openFolder;
	}
	
	/**
	 * @return the cachedSearchIndicator
	 */
	public ProgressIndicator getCachedSearchIndicator() {
		return cachedSearchIndicator;
	}
	
	/**
	 * @return the dropBoxService
	 */
	public DropboxService getDropBoxService() {
		return dropBoxService;
	}
	
	/**
	 * @return the cancelDropBoxService
	 */
	public Button getCancelDropBoxService() {
		return cancelDropBoxService;
	}
	
}
