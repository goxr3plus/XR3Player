package main.java.com.goxr3plus.xr3player.remote.dropbox.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.DownloadErrorException;
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
import main.java.com.goxr3plus.xr3player.remote.dropbox.io.ProgressOutputStream;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropBoxFileTreeItem;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropBoxViewer;

public class RefreshService extends Service<Boolean> {
	
	/**
	 * DropBoxViewer
	 */
	public DropBoxViewer dropBoxViewer;
	
	// Create Dropbox client
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	private DbxClientV2 client;
	private String previousAccessToken;
	private String startingPath;
	private boolean refreshSavedAccounts;
	
	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public RefreshService(DropBoxViewer dropBoxViewer) {
		this.dropBoxViewer = dropBoxViewer;
		
		//On Succesfuly exiting
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
	
	/**
	 * Restart the Service
	 * 
	 * @param path
	 *            The path to follow and open the Tree
	 */
	public void startService(String path) {
		this.startingPath = path;
		
		//Set LoginScreen not visible 
		dropBoxViewer.getLoginVBox().setVisible(false);
		
		//Restart
		super.restart();
	}
	
	/**
	 * This method checks any saved accounts and refreshes ListView to show Account mail etc .. instead of plaing Access_Tokens
	 */
	public void refreshSavedAccounts(boolean refreshAccounts) {
		refreshAccounts = true;
		
		//Restart
		super.restart();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				try {
					//Do we want to refreshSavedAccounts or list files for one particular connected account?
					if (!refreshSavedAccounts) {
						
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
						listAllFiles(client, startingPath, children);
						
						//Check if folder is empty
						Platform.runLater(() -> dropBoxViewer.getEmptyFolderLabel().setVisible(children.isEmpty()));
						
					} else { //Here refreshSavedAccounts
						
						///DbxClientV2 client 
						dropBoxViewer.getSavedAccountsListView().getItems().forEach(item -> {
							DbxClientV2 client = new DbxClientV2(config, item);
						});
						
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
			 * Download Dropbox File to Local Computer
			 * 
			 * @param client
			 *            Current connected client
			 * @param dropBoxFilePath
			 *            The file path on the Dropbox cloud server -> [/foldername/something.txt]
			 * @param localFileAbsolutePath
			 *            The absolute file path of the File on the Local File System
			 * @throws DbxException
			 * @throws DownloadErrorException
			 * @throws IOException
			 */
			public void downloadFile(DbxClientV2 client , String dropBoxFilePath , String localFileAbsolutePath) throws DownloadErrorException , DbxException , IOException {
				//Create DbxDownloader
				DbxDownloader<FileMetadata> dl = client.files().download(dropBoxFilePath);
				
				//FileOutputStream
				FileOutputStream fOut = new FileOutputStream(localFileAbsolutePath);
				System.out.println("Downloading .... " + dropBoxFilePath);
				
				//Add a progress Listener
				dl.download(new ProgressOutputStream(fOut, dl.getResult().getSize(), (long completed , long totalSize) -> {
					System.out.println( ( completed * 100 ) / totalSize + " %");
				}));
				
				//Fast way...
				//client.files().downloadBuilder(file).download(new FileOutputStream("downloads/" + md.getName()))
				
			}
			
			/**
			 * Collapses the whole TreeView THIS METHOD IS BUGGED AS FUCK...DAAADADAYYDUUMN MA BRO
			 * 
			 * @param item
			 */
			//			private DropBoxFileTreeItem getTreeViewItem(TreeItem<String> item , String value) {
			//				//System.out.println("Current Item Value: " + item.getValue() + " Search Value: " + value);
			//				if (item != null && item.getValue().equals(value))
			//					return (DropBoxFileTreeItem) item;
			//				
			//				//Check if it is leaf
			//				//if (!item.isLeaf())
			//				for (TreeItem<String> child : item.getChildren())
			//					getTreeViewItem(child, value);
			//				//else
			//				//	return getTreeViewItem(item, value);
			//				
			//				return null;
			//			}
		};
	}
	
	/**
	 * @return the client
	 */
	public DbxClientV2 getClient() {
		return client;
	}
	
	/**
	 * @return the startingPath
	 */
	public String getStartingPath() {
		return startingPath;
	}
	
}
