package com.goxr3plus.xr3player.controllers.dropbox;

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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.database.PropertiesDb;
import com.goxr3plus.xr3player.enums.DropBoxOperation;
import com.goxr3plus.xr3player.enums.FileCategory;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.services.dropbox.AccountsService;
import com.goxr3plus.xr3player.services.dropbox.DownloadService;
import com.goxr3plus.xr3player.services.dropbox.DropboxService;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.NetworkingTool;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

public class DropboxViewer extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private ProgressIndicator cachedSearchIndicator;

	@FXML
	private TextField searchField;

	@FXML
	private MenuButton topMenuButton;

	@FXML
	private MenuItem signOut;

	@FXML
	private BreadCrumbBar<String> breadCrumbBar;

	@FXML
	private Label searchResultsLabel;

	@FXML
	private Button openFolder;

	@FXML
	private Button createFolder;

	@FXML
	private MenuButton deleteMenuButton;

	@FXML
	private MenuItem deleteFile;

	@FXML
	private MenuItem permanentlyDeleteFile;

	@FXML
	private Button renameButton;

	@FXML
	private Button downloadFile;

	@FXML
	private Button refresh;

	@FXML
	private StackPane innerStackPane;

	@FXML
	private Label emptyFolderLabel;

	@FXML
	private VBox refreshVBox;

	@FXML
	private Label refreshLabel;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private Button cancelDropBoxService;

	@FXML
	private VBox loginVBox;

	@FXML
	private Button authorizationButton;

	@FXML
	private Button authorizationButton2;

	@FXML
	private Button refreshAccounts;

	@FXML
	private TreeView<String> treeView;

	@FXML
	private Button loginWithSavedAccount;

	@FXML
	private Button deleteSavedAccount;

	@FXML
	private Label dropBoxAccountsLabel;

	@FXML
	private VBox loadingAccountsVBox;

	@FXML
	private ProgressBar accountsProgressBar;

	@FXML
	private VBox authorizationCodeVBox;

	@FXML
	private TextField authorizationCodeTextField;

	@FXML
	private Button authorizationCodeOkButton;

	@FXML
	private Button authorizationCodeCancelButton;

	@FXML
	private VBox errorPane;

	@FXML
	private JFXButton tryAgain;

	@FXML
	private ProgressIndicator tryAgainIndicator;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	// -------------------------------------------------------------

	private final DropboxService dropBoxService = new DropboxService(this);
	private final AccountsService accountsService = new AccountsService(this);

	private final ObservableList<String> savedAccountsArray = FXCollections.observableArrayList();

	// -------------------------------------------------------------

	// -------------------------------------------------------------
	private String accessToken;

	private final DropboxAuthanticationBrowser authenticationBrowser;

	private final DropboxFilesTableViewer dropboxFilesTableViewer;

	private final DropboxFileContextMenu fileContextMenu = new DropboxFileContextMenu();

	public final static Color FONT_ICON_COLOR = Color.web("#25c1ff");

	/**
	 * Constructor.
	 */
	public DropboxViewer() {
		authenticationBrowser = new DropboxAuthanticationBrowser();
		dropboxFilesTableViewer = new DropboxFilesTableViewer();

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.DROPBOX_FXMLS + "DropboxViewer.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		// loadingAccountsVBox
		loadingAccountsVBox.visibleProperty().bind(accountsService.runningProperty());

		// accountsProgressBar
		accountsProgressBar.progressProperty().bind(accountsService.progressProperty());

		// treeView
		treeView.setShowRoot(false);
		treeView.setRoot(new DropboxClientTreeItem("Accounts", "no token", "no email"));
		treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			// Check for null
			if (newValue == null)
				return;

			// Check if it is leaf
			if (newValue.isLeaf()) {
				loginWithSavedAccount.setDisable(false);
				deleteSavedAccount.setDisable(false);
			} else {
				loginWithSavedAccount.setDisable(true);
				deleteSavedAccount.setDisable(true);
			}
		});
		treeView.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ENTER) // ENTER
				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem())
						.ifPresent(item -> connect(((DropboxClientTreeItem) item).getAccessToken()));
			else if (key.getCode() == KeyCode.DELETE) // DELETE
				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem())
						.ifPresent(item -> deleteSelectedAccount());
		});

		// DropboxFilesTableViewer
		innerStackPane.getChildren().add(dropboxFilesTableViewer);
		dropboxFilesTableViewer.toBack();

		// refreshVBox
		refreshVBox.visibleProperty().bind(getDropBoxService().runningProperty());

		// Progress Indicator
		progressIndicator.progressProperty().bind(getDropBoxService().progressProperty());

		// refresh
		refresh.setOnAction(a -> {
			if (!searchField.getText().isEmpty())
				search(searchField.getText());
			else
				recreateTableView(getDropBoxService().getCurrentPath());
		});

		// authorizationButton
		authorizationButton.setOnAction(a -> requestDropBoxAuthorization());

		// authorizationButton2
		authorizationButton2.setOnAction(a -> {

			// authorizationCodeVBox
			authorizationCodeVBox.setVisible(true);

			// Open the default external Browser
			NetworkingTool.openWebSite(authenticationBrowser.getAuthonticationRequestURL());
		});

		// Add binding to accessTokenProperty
		authenticationBrowser.accessTokenProperty().addListener((observable, oldValue, newValue) -> {
			// Check if empty
			if (!newValue.isEmpty()) {
				accessToken = newValue;

				// Show message to the User
				AlertTool.showNotification("Authantication", "Successfully authenticated to your Dropbox Account",
						Duration.millis(2000), NotificationType.SIMPLE,
						JavaFXTool.getFontIcon("fa-dropbox", FONT_ICON_COLOR, 64));

				// Save on the database
				final PropertiesDb propertiesDb = Main.userInfoMode.getUser().getUserInformationDb();
				propertiesDb.updateProperty("DropBox-Access-Tokens",
						(propertiesDb.getProperty("DropBox-Access-Tokens") == null ? ""
								: propertiesDb.getProperty("DropBox-Access-Tokens"))
								+ (savedAccountsArray.isEmpty() ? "" : "<>:<>") + accessToken);

				// loginVBox
				loginVBox.setVisible(false);

				// authorizationCodeVBox
				authorizationCodeVBox.setVisible(false);

				// authorizationCodeTextField
				authorizationCodeTextField.clear();

				// Connect
				connect(accessToken);

				// Refresh Saved Accounts
				refreshSavedAccounts();
			}
		});

		// errorVBox
		errorPane.setVisible(false);

		// loginVBox
		loginVBox.setVisible(true);

		// authorizationCodeVBox
		authorizationCodeVBox.setVisible(false);

		// openFolder
		openFolder.setOnAction(a -> {
			final DropboxFile selectedFile = dropboxFilesTableViewer.getTableView().getSelectionModel().getSelectedItem();
			if (selectedFile != null)
				recreateTableView(selectedFile.getMetadata().getPathLower());
		});

		// authorizationCodeCancelButton
		authorizationCodeCancelButton.setOnAction(a -> authorizationCodeVBox.setVisible(false));

		// authorizationCodeOkButton
		authorizationCodeOkButton.disableProperty().bind(authorizationCodeTextField.textProperty().isEmpty());
		authorizationCodeOkButton.setOnAction(
				a -> authenticationBrowser.produceAccessToken(authorizationCodeTextField.getText().trim()));

		// authorizationCodeTextField
		authorizationCodeTextField.setOnAction(
				a -> authenticationBrowser.produceAccessToken(authorizationCodeTextField.getText().trim()));

		// signOut
		signOut.setOnAction(a -> {

			// cancel the service
			getDropBoxService().cancel();

			// cancel cachedSearch Service
			getDropBoxService().getSearchCacheService().cancel();

			// loginVBox
			loginVBox.setVisible(true);

		});

		// loginWithSavedAccount
		loginWithSavedAccount.setOnAction(a -> connect(
				((DropboxClientTreeItem) treeView.getSelectionModel().getSelectedItem()).getAccessToken()));

		// deleteSavedAccount
		deleteSavedAccount.setOnAction(a -> deleteSelectedAccount());

		// tryAgain
		tryAgain.setOnAction(a -> checkForInternetConnection());

		// breadCrumbBar
		breadCrumbBar.setOnCrumbAction(event -> {

			// Recreate Tree
			final String value = event.getSelectedCrumb().getValue();
			if ("DROPBOX ROOT".equals(value))
				recreateTableView("");
			else
				recreateTableView(getDropBoxService().getCurrentPath().split(value)[0] + value);

		});

		// savedAccountsListView
		// savedAccountsListView.setItems(savedAccountsArray);
		// savedAccountsListView.setCellFactory(lv -> new ListCell<String>() {
		// @Override
		// public void updateItem(String item , boolean empty) {
		// super.updateItem(item, empty);
		// if (empty) {
		// setText(null);
		// } else {
		// //String text = item.contains("<>:<>") ? item.split("<>:<>")[0] : item; //
		// get text from item
		// setText(item);
		// setTooltip(new Tooltip(item));
		// setGraphic(JavaFXTools.getFontIcon("fa-dropbox", FONT_ICON_COLOR, 32));
		// }
		// }
		// });
		// savedAccountsListView.setOnKeyReleased(key -> {
		// if (key.getCode() == KeyCode.ENTER &&
		// !savedAccountsListView.getItems().isEmpty()) {
		//
		// //Connect
		// connect(savedAccountsListView.getSelectionModel().getSelectedItem());
		//
		// } else if (key.getCode() == KeyCode.DELETE &&
		// !savedAccountsListView.getItems().isEmpty())
		// deleteSelectedAccount();
		//
		// });

		// downloadFile
		downloadFile.disableProperty()
				.bind(dropboxFilesTableViewer.getSelectionModel().selectedItemProperty().isNull());
		downloadFile.setOnAction(a -> downloadFile(dropboxFilesTableViewer.getSelectionModel().getSelectedItem()));

		// deleteMenuButton
		deleteMenuButton.disableProperty()
				.bind(dropboxFilesTableViewer.getSelectionModel().selectedItemProperty().isNull());

		// deleteFile
		deleteFile.setOnAction(a -> deleteSelectedFiles(false));

		// permanentlyDeleteFile
		permanentlyDeleteFile.setOnAction(a -> deleteSelectedFiles(true));

		// renameButton
		renameButton.disableProperty().bind(deleteMenuButton.disabledProperty());
		renameButton.setOnAction(
				a -> renameFile(dropboxFilesTableViewer.getSelectionModel().getSelectedItem(), renameButton));

		// createFolder
		createFolder.setOnAction(a -> {

			// Show the window
			Main.renameWindow.show("", createFolder, "Create Dropbox Folder", FileCategory.FILE);

			// When the Rename Window is closed do the rename
			Main.renameWindow.showingProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(final Observable observable) {

					// Remove the Listener
					Main.renameWindow.showingProperty().removeListener(this);

					// !Showing
					if (!Main.renameWindow.isShowing()) {

						// !XPressed && // Old name != New name
						if (Main.renameWindow.wasAccepted()) {

							// Try to create
							getDropBoxService().createFolder(getDropBoxService().getCurrentPath() + "/"
									+ Main.renameWindow.getInputField().getText());

						}

					} // RenameWindow is still showing
				}// invalidated
			});

		});

		// cancelDropBoxService
		cancelDropBoxService.setOnAction(a -> {

			// It was on Search?
			if (dropBoxService.getOperation() == DropBoxOperation.SEARCH)
				dropBoxService.getSearchCacheService().prepareCachedSearch(dropBoxService.getClient());

			// Recreate
			recreateTableView("");
		});

		// searchResultsLabel
		searchResultsLabel.setVisible(false);

		// createFolder
		createFolder.disableProperty().bind(searchResultsLabel.visibleProperty());

		// searchField
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (searchField.getText().isEmpty())
				search("");
		});
		searchField.setOnAction(a -> search(searchField.getText()));

		// cachedSearchIndicator
		cachedSearchIndicator.progressProperty().bind(getDropBoxService().getSearchCacheService().progressProperty());

		// Trying to change the "Done" Text of ProgressIndicator from Cached Search
		// cachedSearchIndicator.progressProperty().addListener((observable , oldValue ,
		// newValue) -> {
		// // If progress is 100% then show Text
		// if (newValue.doubleValue() >= 1) {
		//
		// // Apply CSS so you can lookup the text
		// cachedSearchIndicator.applyCss();
		//
		// // This text replaces "Done"
		// //( (Text) cachedSearchIndicator.lookup(".text.percentage") ).setText("Cached
		// Search Ready");
		// Text text = ( (Text) cachedSearchIndicator.lookup(".percentage") );
		// text.setText("23");
		//
		// cachedSearchIndicator.applyCss();
		//
		// progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
		//
		// cachedSearchIndicator.applyCss();
		//
		// System.out.println("Cached Search Ready!!!!");
		// }
		//
		// });

		// refreshAccounts
		refreshAccounts.setOnAction(a -> accountsService.restartService());
	}

	/**
	 * Deletes the current selected dropbox account
	 */
	private void deleteSelectedAccount() {

		// Clear the selected item
		if (savedAccountsArray != null && treeView.getSelectionModel().getSelectedItem() != null) {

			if (AlertTool.doQuestion("Deleting Dropbox Account",
					"Are you soore you want to delete selected Dropbox Account ?", treeView, Main.window)) {

				// Remove the selected items
				savedAccountsArray.remove(
						((DropboxClientTreeItem) treeView.getSelectionModel().getSelectedItem()).getAccessToken());

				// Refresh the properties database
				Main.userInfoMode.getUser().getUserInformationDb().updateProperty("DropBox-Access-Tokens",
						savedAccountsArray.stream().collect(Collectors.joining("<>:<>")));

				// Start Accounts Service
				if (!savedAccountsArray.isEmpty())
					accountsService.restartService();
			}

			// DropBoxAccountsLabel
			dropBoxAccountsLabel.setVisible(savedAccountsArray.isEmpty());

		}

	}

	// ------------------------------------------------------------------------------------------

	/**
	 * Connect to the given user account
	 * 
	 * @param accessToken
	 */
	private void connect(final String accessToken) {
		this.accessToken = accessToken;

		// Clear CachedService Search
		getDropBoxService().getSearchCacheService().getCachedList().clear();

		// Create the TableView
		recreateTableView("");
	}

	// ------------------------------------------------------------------------------------------

	/**
	 * Prepare to delete selected file
	 * 
	 * @param permanent True = permanently , false = not permanently
	 */
	public void deleteSelectedFiles(final boolean permanent) {
		final int selectedItems = dropboxFilesTableViewer.getSelectionModel().getSelectedIndices().size();

		if (!permanent) {
			if (AlertTool.doQuestion("Delete",
					"Are you sure you want to delete " + (selectedItems != 1 ? " [ " + selectedItems + " ] items"
							: " [ " + dropboxFilesTableViewer.getSelectionModel().getSelectedItem().getTitle() + " ] ")
							+ " from your Dropbox?",
					deleteMenuButton, Main.window))
				this.getDropBoxService().delete(DropBoxOperation.DELETE);
		} else if (AlertTool.doQuestion("PERMANENT Delete",
				"Are you sure you want to delete " + (selectedItems != 1 ? " [ " + selectedItems + " ] items"
						: " [ " + dropboxFilesTableViewer.getSelectionModel().getSelectedItem().getTitle() + " ] ")
						+ " from your Dropbox PERMANENTLY?",
				deleteMenuButton, Main.window))
			this.getDropBoxService().delete(DropBoxOperation.PERMANENTLY_DELETE);

	}

	/**
	 * Prepare to delete selected file
	 * 
	 * @param permanent True = permanently , false = not permanently
	 */
	public void deleteFile(final DropboxFile dropboxFile, final boolean permanent) {

		if (!permanent) {
			if (AlertTool.doQuestion("Delete",
					"Are you sure you want to delete [ " + dropboxFile.getTitle() + " ]  from your Dropbox?",
					deleteMenuButton, Main.window))
				this.getDropBoxService().delete(DropBoxOperation.DELETE);
		} else if (AlertTool.doQuestion("PERMANENT Delete",
				"Are you sure you want to delete [" + dropboxFile.getTitle() + " ]  from your Dropbox PERMANENTLY?",
				deleteMenuButton, Main.window))
			this.getDropBoxService().delete(DropBoxOperation.PERMANENTLY_DELETE);

	}

	/**
	 * Prepare for downloading the selected file
	 */
	public DropboxDownloadedFile downloadFile(final DropboxFile dropboxFile) {

		// Simple File
		if (!dropboxFile.isDirectory()) {

			// Show save dialog
			final File file = Main.specialChooser.showSaveDialog(dropboxFile.getTitle(), FileType.FILE);
			return downloadFilePart2(dropboxFile, file);

			// Directory
		} else {

			// Show save dialog
			final File file = Main.specialChooser.showSaveDialog(dropboxFile.getTitle() + ".zip", FileType.DIRECTORY);
			return downloadFilePart2(dropboxFile, file);

		}
	}

	/**
	 * This method is called automatically by downloadFile method , dunno touch :)
	 * ATTENTION!!! FIRE !! hahah joking :)
	 */
	private DropboxDownloadedFile downloadFilePart2(final DropboxFile dropboxFile, final File file) {
		if (file == null)
			return null;

		// Create a new DropboxDownloadedFile
		final DownloadService downloadService = new DownloadService(this, dropboxFile, file.getAbsolutePath());
		final DropboxDownloadedFile dropboxDownloadedFile = new DropboxDownloadedFile(downloadService);

		// Append it to the TableViewer
		Main.dropboxDownloadsTableViewer.getObservableList().add(dropboxDownloadedFile);
		// Start the download Service
		dropboxDownloadedFile.getDownloadService().startService();

		// dropboxDownloadedFile
		return dropboxDownloadedFile;
	}

	/**
	 * Prepare to rename a DropboxFile
	 * 
	 * @param dropboxFile
	 */
	public void renameFile(final DropboxFile dropboxFile, final Node node) {

		// Show Rename Window
		Main.renameWindow.show(IOInfo.getFileTitle(dropboxFile.getMetadata().getName()), node, "Media Renaming",
				FileCategory.FILE);
		final String oldName = dropboxFile.getMetadata().getName();

		// Bind
		dropboxFile.titleProperty().bind(Main.renameWindow.getInputField().textProperty().concat(
				!dropboxFile.isFile() ? "" : "." + IOInfo.getFileExtension(dropboxFile.getMetadata().getName())));

		// When the Rename Window is closed do the rename
		Main.renameWindow.showingProperty().addListener(new InvalidationListener() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			public void invalidated(final Observable observable) {

				// Remove the Listener
				Main.renameWindow.showingProperty().removeListener(this);

				// !Showing
				if (!Main.renameWindow.isShowing()) {

					// Remove Binding
					dropboxFile.titleProperty().unbind();

					final String newName = Main.renameWindow.getInputField().getText() + (dropboxFile.isDirectory() ? ""
							: "." + IOInfo.getFileExtension(dropboxFile.getMetadata().getName()));

					// !XPressed && // Old name != New name
					if (Main.renameWindow.wasAccepted() && !oldName.equals(newName)) {
						final String parent = new File(dropboxFile.getMetadata().getPathLower()).getParent();

						// Try to do it
						getDropBoxService().rename(dropboxFile,
								parent.replace("\\", "/") + (parent.equals("\\") ? "" : "/") + newName);

						// System.out.println("Old Name: " + dropboxFile.getMetadata().getPathLower())
						//
						// System.out.println("New Name: " + parent.replace("\\", "/") + (
						// parent.equals("\\") ? "" : "/" ) + newName)
						//
					} else // X is pressed by user || // Old name == New name
						dropboxFile.titleProperty().set(oldName);

				} // RenameWindow is still showing
			}// invalidated
		});
		// }
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * Refreshes the Saved Accounts Lists View
	 */
	public void refreshSavedAccounts() {

		// Clear ArrayFirst
		savedAccountsArray.clear();

		// savedAccountsListView
		Optional.ofNullable(Main.userInfoMode.getUser().getUserInformationDb().getProperty("DropBox-Access-Tokens"))
				.ifPresent(accessTokens -> {
					if (accessTokens.contains("<>:<>")) // Check if we have multiple access tokens
						savedAccountsArray.addAll(Stream.of(accessTokens.split(Pattern.quote("<>:<>")))
								.collect(Collectors.toCollection(FXCollections::observableArrayList)));
					else if (!accessTokens.isEmpty()) // Check if we have one access token
						savedAccountsArray.addAll(Stream.of(accessTokens)
								.collect(Collectors.toCollection(FXCollections::observableArrayList)));
				});

		// DropBoxAccountsLabel
		dropBoxAccountsLabel.setVisible(savedAccountsArray.isEmpty());

		// Start Accounts Service
		if (!savedAccountsArray.isEmpty())
			accountsService.restartService();
	}

	/**
	 * Request XR3Player Authorization to have access to his/her DropBox Account
	 */
	public void requestDropBoxAuthorization() {

		// Show authentication browser
		authenticationBrowser.showAuthenticationWindow();

	}

	/**
	 * Starts the Dropbox Service Search functionality based on the given word
	 * 
	 * @param searchWord
	 */
	public void search(final String searchWord) {

		// Navigate back to root if searchWord is empty
		if (searchWord.isEmpty())
			recreateTableView("");
		else
			getDropBoxService().search(searchWord);
	}

	/**
	 * Recreates the TableView
	 */
	public void recreateTableView(final String path) {

		// BreadCrumbBar
		if (path.isEmpty()) {

			// Build the Model
			breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel("DROPBOX ROOT"));

			// PRINT
			// System.out.println(Arrays.asList(path.split("/")))
		} else {

			// Build the ArrayList
			final ArrayList<String> arrayList = new ArrayList<>();
			arrayList.add("DROPBOX ROOT");
			arrayList.addAll(Arrays.asList(path.replaceFirst(Pattern.quote("/"), "").split("/")));

			// Build the Model
			final TreeItem<String> model = BreadCrumbBar.buildTreeModel(arrayList.toArray(new String[0]));

			// Add all the items to the model
			breadCrumbBar.setSelectedCrumb(model);

			// PRINT
			// System.out.println(Arrays.asList(path.split("/")))

		}

		// Start the Service
		getDropBoxService().refresh(path);
	}

	/**
	 * Checks for internet connection
	 */
	void checkForInternetConnection() {

		// tryAgainIndicator
		tryAgainIndicator.setVisible(true);

		// Check for internet connection
		final Thread thread = new Thread(() -> {
			final boolean hasInternet = NetworkingTool.isReachableByPing("www.google.com");
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

	/**
	 * @return the treeView
	 */
	public TreeView<String> getTreeView() {
		return treeView;
	}

	/**
	 * @return the savedAccountsArray
	 */
	public ObservableList<String> getSavedAccountsArray() {
		return savedAccountsArray;
	}

}
