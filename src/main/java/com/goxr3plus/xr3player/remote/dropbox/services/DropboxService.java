package main.java.com.goxr3plus.xr3player.remote.dropbox.services;

import java.util.List;
import java.util.stream.Collectors;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropboxFile;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropboxViewer;

public class DropboxService extends Service<Boolean> {
	
	public enum DropBoxOperation {
		REFRESH, SEARCH, CREATE_FOLDER, DELETE, PERMANENTLY_DELETE, RENAME;
	}
	
	/**
	 * DropBoxViewer
	 */
	public DropboxViewer dropBoxViewer;
	
	// Create Dropbox client
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	private DbxClientV2 client;
	private String previousAccessToken;
	private String currentPath;
	private String folderName;
	private String searchWord;
	
	//
	private DropboxFile dropboxFile;
	private String newPath;
	
	/**
	 * This path is being used to delete files
	 */
	private DropBoxOperation operation;
	
	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public DropboxService(DropboxViewer dropBoxViewer) {
		this.dropBoxViewer = dropBoxViewer;
		
		//On Successful exiting
		setOnSucceeded(s -> {
			
			//Check if failed
			if (!getValue()) {
				
				//Set Login Visible Again
				dropBoxViewer.getLoginVBox().setVisible(true);
				
				//Show message to the User
				ActionTool.showNotification("Authantication Failed",
						"Failed connecting in that Dropbox Account, try : \n1) Connect again with a new Dropbox Account \n2) Connect with another saved DropBox Account \n3) Delete this corrupted saved account",
						Duration.millis(3000), NotificationType.ERROR);
			}
		});
		
	}
	
	//	/**
	//	 * This method checks any saved accounts and refreshes ListView to show Account mail etc .. instead of plaing Access_Tokens
	//	 */
	//	@Deprecated
	//	private void refreshSavedAccounts(boolean refreshAccounts) {
	//		refreshAccounts = true;
	//		
	//		//Restart
	//		super.restart();
	//	}
	
	/**
	 * Restart the Service
	 * 
	 * @param path
	 *            The path to follow and open the Tree
	 */
	public void refresh(String path) {
		this.currentPath = path;
		this.operation = DropBoxOperation.REFRESH;
		
		//Clear all the children
		dropBoxViewer.getDropboxFilesTableViewer().getTableView().getItems().clear();
		
		//Set LoginScreen not visible 
		dropBoxViewer.getLoginVBox().setVisible(false);
		
		//RefreshLabel
		dropBoxViewer.getRefreshLabel().setText("Connecting to Server ...");
		
		//Restart
		super.restart();
	}
	
	/**
	 * Search whole Dropbox for the given word
	 * 
	 * @param searchWord
	 */
	public void search(String searchWord) {
		this.searchWord = searchWord.toLowerCase();
		this.operation = DropBoxOperation.SEARCH;
		
		//Clear all the children
		dropBoxViewer.getDropboxFilesTableViewer().getTableView().getItems().clear();
		
		//RefreshLabel
		dropBoxViewer.getRefreshLabel().setText("Searching for matching files ...");
		
		//Restart
		super.restart();
	}
	
	/**
	 * After calling this method the Service will find the selected file or files and delete them from Dropbox Account
	 */
	public void delete(DropBoxOperation operation) {
		this.operation = operation;
		
		//RefreshLabel
		dropBoxViewer.getRefreshLabel().setText("Deleting requested files ...");
		
		//Restart
		super.restart();
	}
	
	/**
	 * Create a new Folder with that name on Dropbox Account
	 * 
	 * @param folderName
	 *            The new folder name
	 */
	public void createFolder(String folderName) {
		this.folderName = folderName;
		this.operation = DropBoxOperation.CREATE_FOLDER;
		
		//RefreshLabel
		dropBoxViewer.getRefreshLabel().setText("Creating requested folder ...");
		
		//Restart                                                                   
		super.restart();
	}
	
	/**
	 * Renames a dropbox file
	 * 
	 * @param dropboxFile
	 *            The dropbox file that will receive this procedure
	 * @param newPath
	 *            newPath of file
	 * 
	 */
	public void rename(DropboxFile dropboxFile , String newPath) {
		this.dropboxFile = dropboxFile;
		this.newPath = newPath;
		this.operation = DropBoxOperation.RENAME;
		
		//RefreshLabel
		dropBoxViewer.getRefreshLabel().setText("Renaming requested file ...");
		
		//Restart                                                                   
		super.restart();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				try {
					
					//REFRESH?
					if (operation == DropBoxOperation.REFRESH) {
						
						//Create the Client
						if (client == null || previousAccessToken == null || !previousAccessToken.equals(dropBoxViewer.getAccessToken())) {
							previousAccessToken = dropBoxViewer.getAccessToken();
							client = new DbxClientV2(config, dropBoxViewer.getAccessToken());
						}
						
						// Get current account info
						FullAccount account = client.users().getCurrentAccount();
						Platform.runLater(() -> dropBoxViewer.getTopMenuButton().setText(" " + account.getName().getDisplayName()));
						
						//List all the files brooooo!
						ObservableList<DropboxFile> observableList = FXCollections.observableArrayList();
						listAllFiles(currentPath, observableList, false, true);
						
						//Check if folder is empty
						Platform.runLater(() -> {
							dropBoxViewer.getSearchResultsLabel().setVisible(false);
							dropBoxViewer.getEmptyFolderLabel().setVisible(observableList.isEmpty());
							
							//Set the items to TableView
							dropBoxViewer.getDropboxFilesTableViewer().getTableView().setItems(observableList);
							dropBoxViewer.getDropboxFilesTableViewer().updateLabel();
						});
						
					} else if (operation == DropBoxOperation.DELETE) {
						
						//Delete all the selected files and folders
						List<DropboxFile> list = dropBoxViewer.getDropboxFilesTableViewer().getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
						
						//Remove from the TreeView one by one
						list.forEach(item -> {
							if (delete(item.getMetadata().getPathLower()))
								Platform.runLater(() -> dropBoxViewer.getDropboxFilesTableViewer().getTableView().getItems().remove(item));
							
						});
						
						//Update the bottom label
						Platform.runLater(() -> dropBoxViewer.getDropboxFilesTableViewer().updateLabel());
						
					} else if (operation == DropBoxOperation.CREATE_FOLDER) {
						
						//Create Folder
						createFolder(folderName);
						
						//Refresh
						Platform.runLater(() -> refresh(currentPath));
						
					} else if (operation == DropBoxOperation.RENAME) {
						
						if (rename(dropboxFile.getMetadata().getPathLower(), newPath)) //check if not succeeded
							ActionTool.showNotification("Success Message", "Succesfully renamed file :)", Duration.millis(1500), NotificationType.SIMPLE,
									DropboxViewer.dropBoxImage, 0, 0);
						else {
							ActionTool.showNotification("Error Message",
									"Failed to rename the File:\n [ " + dropboxFile.getMetadata().getPathLower() + " ] to -> [ " + newPath + " ]", Duration.millis(1500),
									NotificationType.ERROR);
							dropboxFile.titleProperty().set(dropboxFile.getMetadata().getName());
						}
						
					} else if (operation == DropBoxOperation.SEARCH) {
						
						//Search Everything
						ObservableList<DropboxFile> observableList = FXCollections.observableArrayList();
						search("", observableList);
						
						Platform.runLater(() -> {
							
							//Set Label Visible
							dropBoxViewer.getSearchResultsLabel().setVisible(true);
							
							//Set the items to TableView
							dropBoxViewer.getDropboxFilesTableViewer().getTableView().setItems(observableList);
							
							//Set Label Visible
							dropBoxViewer.getSearchResultsLabel().setText("Total Found -> " + InfoTool.getNumberWithDots(observableList.size()));
							dropBoxViewer.getDropboxFilesTableViewer().updateLabel();
						});
						
					}
				} catch (ListFolderErrorException ex) {
					ex.printStackTrace();
					
					//Show to user about the error
					Platform.runLater(
							() -> ActionTool.showNotification("Missing Folder", "Folder : [ " + currentPath + " ] doesn't exist.", Duration.seconds(2), NotificationType.ERROR));
					
					//Check the Internet Connection
					checkConnection();
					
				} catch (Exception ex) {
					ex.printStackTrace();
					
					//Check the Internet Connection
					checkConnection();
					
					return false;
				}
				return true;
			}
			
			/**
			 * Check if there is Internet Connection
			 */
			private boolean checkConnection() {
				
				//Check if there is Internet Connection
				if (!InfoTool.isReachableByPing("www.google.com")) {
					Platform.runLater(() -> dropBoxViewer.getErrorVBox().setVisible(true));
					return false;
				}
				
				return true;
			}
			
			/**
			 * List all the Files inside DropboxAccount
			 * 
			 * @param client
			 * @param path
			 * @param children
			 * @param arrayList
			 * @throws DbxException
			 * @throws ListFolderErrorException
			 */
			public void listAllFiles(String path , ObservableList<DropboxFile> children , boolean recursive , boolean appendToMap) throws DbxException {
				
				ListFolderResult result = client.files().listFolder(path);
				
				while (true) {
					for (Metadata metadata : result.getEntries()) {
						if (metadata instanceof DeletedMetadata) { // Deleted
							//	children.remove(metadata.getPathLower())
						} else if (metadata instanceof FolderMetadata) { // Folder
							String folder = metadata.getPathLower();
							//String parent = new File(metadata.getPathLower()).getParent().replace("\\", "/")
							if (appendToMap)
								children.add(new DropboxFile(metadata));
							
							//boolean subFileOfCurrentFolder = path.equals(parent)
							//System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "Folder ->" + folder)
							
							if (recursive)
								listAllFiles(folder, children, recursive, appendToMap);
						} else if (metadata instanceof FileMetadata) { //File
							String file = metadata.getPathLower();
							//String parent = new File(metadata.getPathLower()).getParent().replace("\\", "/")
							if (appendToMap)
								children.add(new DropboxFile(metadata));
							
							//boolean subFileOfCurrentFolder = path.equals(parent)
							//System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "File->" + file + " Media Info: " + InfoTool.isAudioSupported(file))
						}
					}
					
					if (!result.getHasMore())
						break;
					
					try {
						result = client.files().listFolderContinue(result.getCursor());
						//System.out.println("Entered result next")
					} catch (ListFolderContinueErrorException ex) {
						ex.printStackTrace();
					}
				}
				
			}
			
			/**
			 * List all the Files inside DropboxAccount
			 * 
			 * @param client
			 * @param path
			 * @param children
			 * @param arrayList
			 * @throws DbxException
			 * @throws ListFolderErrorException
			 */
			public void search(String path , ObservableList<DropboxFile> children) throws DbxException {
				
				ListFolderResult result = client.files().listFolder(path);
				
				while (true) {
					for (Metadata metadata : result.getEntries()) {
						if (metadata instanceof DeletedMetadata) { // Deleted
							//	children.remove(metadata.getPathLower())
						} else if (metadata instanceof FolderMetadata) { // Folder
							String folder = metadata.getPathLower();
							if (metadata.getName().toLowerCase().contains(searchWord))
								children.add(new DropboxFile(metadata));
							
							//Run again
							search(folder, children);
						} else if (metadata instanceof FileMetadata) { //File
							if (metadata.getName().toLowerCase().contains(searchWord))
								children.add(new DropboxFile(metadata));
						}
					}
					
					if (!result.getHasMore())
						break;
					
					try {
						result = client.files().listFolderContinue(result.getCursor());
						//System.out.println("Entered result next")
					} catch (ListFolderContinueErrorException ex) {
						ex.printStackTrace();
					}
				}
				
			}
			
			/**
			 * Deletes the given file or folder from Dropbox Account
			 * 
			 * @param path
			 *            The path of the Dropbox File or Folder
			 */
			public boolean delete(String path) {
				try {
					if (operation == DropBoxOperation.DELETE)
						client.files().deleteV2(path);
					else
						client.files().permanentlyDelete(path); //SUPPORTED ONLY ON BUSINESS PLAN
						
					//Show message to the User
					Platform.runLater(() -> ActionTool.showNotification("Delete was successful", "Successfully deleted selected files/folders", Duration.millis(2000),
							NotificationType.SIMPLE,DropboxViewer.dropBoxImage, 0, 0));
					
					return true;
				} catch (DbxException dbxe) {
					dbxe.printStackTrace();
					
					//Show message to the User
					Platform.runLater(
							() -> ActionTool.showNotification("Failed deleting files", "Failed to delete selected files/folders", Duration.millis(2000), NotificationType.ERROR));
					
					return false;
				}
			}
			
			/**
			 * Renames the given file or folder from Dropbox Account
			 * 
			 * @param oldPath
			 * @param newPath
			 */
			public boolean rename(String oldPath , String newPath) {
				try {
					client.files().moveV2(oldPath, newPath);
					
					return true;
				} catch (DbxException dbxe) {
					dbxe.printStackTrace();
					
					return false;
				}
			}
			
			/**
			 * Create a folder from Dropbox Account
			 * 
			 * @param path
			 *            Folder name
			 */
			public boolean createFolder(String path) {
				try {
					
					//Create new folder
					CreateFolderResult result = client.files().createFolderV2(path, true);
					
					//Show message to the User
					Platform.runLater(() -> ActionTool.showNotification("New folder created", "Folder created with name :\n [ " + result.getMetadata().getName() + " ]",
							Duration.millis(2000), NotificationType.SIMPLE,DropboxViewer.dropBoxImage, 0, 0));
					
					return true;
				} catch (DbxException dbxe) {
					dbxe.printStackTrace();
					
					//Show message to the User
					Platform.runLater(() -> ActionTool.showNotification("Failed creating folder", "Folder was not created", Duration.millis(2000), NotificationType.ERROR));
					
					return false;
				}
			}
			
		};
	}
	
	/**
	 * The client
	 * 
	 * @return the client
	 */
	public DbxClientV2 getClient() {
		return client;
	}
	
	/**
	 * The Current Path on Dropbox Account
	 * 
	 * @return The Current Path on Dropbox Account
	 */
	public String getCurrentPath() {
		return currentPath;
	}
	
}
