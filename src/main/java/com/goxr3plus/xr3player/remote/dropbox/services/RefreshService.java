package main.java.com.goxr3plus.xr3player.remote.dropbox.services;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropBoxFileTreeItem;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropBoxViewer;

public class RefreshService extends Service<Boolean> {
	
	public enum DropBoxOperation {
		REFRESH, CREATE_FOLDER, DELETE, PERMANENTLY_DELETE, RENAME;
	}
	
	/**
	 * DropBoxViewer
	 */
	public DropBoxViewer dropBoxViewer;
	
	// Create Dropbox client
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	private DbxClientV2 client;
	private String previousAccessToken;
	private String currentPath;
	
	/**
	 * This path is being used to delete files
	 */
	private DropBoxOperation operation;
	
	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public RefreshService(DropBoxViewer dropBoxViewer) {
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
		dropBoxViewer.getRoot().getChildren().clear();
		
		//Set LoginScreen not visible 
		dropBoxViewer.getLoginVBox().setVisible(false);
		
		//Restart
		super.restart();
	}
	
	/**
	 * After calling this method the Service will find the selected file or files and delete them from Dropbox Account
	 */
	public void delete(DropBoxOperation operation) {
		this.operation = operation;
		
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
						
						TreeMap<String,Metadata> children = new TreeMap<>();
						
						//List all the files brooooo!
						listAllFiles(client, currentPath, children);
						
						//Check if folder is empty
						Platform.runLater(() -> dropBoxViewer.getEmptyFolderLabel().setVisible(children.isEmpty()));
						
					} else if (operation == DropBoxOperation.DELETE) {
						dropBoxViewer.getTreeView().getSelectionModel().getSelectedItems().forEach(item -> delete( ( (DropBoxFileTreeItem) item ).getMetadata().getPathLower()));
						Platform.runLater(() -> refresh(currentPath));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					
					//Check if there is Internet Connection
					if (!InfoTool.isReachableByPing("www.google.com"))
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
			 */
			public void listAllFiles(DbxClientV2 client , String path , SortedMap<String,Metadata> children) {
				try {
					ListFolderResult result = null;
					try {
						result = client.files().listFolder(path);
					} catch (ListFolderErrorException ex) {
						ex.printStackTrace();
					}
					
					while (true) {
						for (Metadata metadata : result.getEntries()) {
							if (metadata instanceof DeletedMetadata) { // Deleted
								//	children.remove(metadata.getPathLower());
							} else if (metadata instanceof FolderMetadata) { // Folder
								String folder = metadata.getPathLower();
								String parent = new File(metadata.getPathLower()).getParent().replace("\\", "/");
								children.put(folder, metadata);
								
								//boolean subFileOfCurrentFolder = path.equals(parent);
								//System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "Folder ->" + folder);
								
								//Add to TreeView	
								Platform.runLater(() -> dropBoxViewer.getRoot().getChildren().add(new DropBoxFileTreeItem(metadata.getName(), metadata)));
								
								//listAllFiles(client, folder, children);
							} else if (metadata instanceof FileMetadata) { //File
								String file = metadata.getPathLower();
								String parent = new File(metadata.getPathLower()).getParent().replace("\\", "/");
								children.put(file, metadata);
								
								//boolean subFileOfCurrentFolder = path.equals(parent);
								//System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "File->" + file + " Media Info: " + InfoTool.isAudioSupported(file));
								
								Platform.runLater(() -> dropBoxViewer.getRoot().getChildren().add(new DropBoxFileTreeItem(metadata.getName(), metadata)));
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
				} catch (DbxException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/**
			 * Deletes the given file or folder from Dropbox Account
			 * 
			 * @param path
			 *            The path of the Dropbox File or Folder
			 */
			public void delete(String path) {
				try {
					if (operation == DropBoxOperation.DELETE)
						client.files().deleteV2(path);
					else
						client.files().permanentlyDelete(path);
				} catch (DbxException dbxe) {
					dbxe.printStackTrace();
				}
			}
			
			/**
			 * Renames the given file or folder from Dropbox Account
			 * 
			 * @param oldPath
			 * @param newPath
			 */
			public void rename(String oldPath , String newPath) {
				try {
					client.files().moveV2(oldPath, newPath);
				} catch (DbxException dbxe) {
					dbxe.printStackTrace();
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
