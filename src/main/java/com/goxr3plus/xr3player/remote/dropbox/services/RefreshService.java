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
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.remote.dropbox.io.ProgressOutputStream;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropBoxViewer;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.RemoteFileTreeItem;

public class RefreshService extends Service<Void> {
	
	/**
	 * DropBoxViewer
	 */
	public DropBoxViewer dropBoxViewer;
	
	// Create Dropbox client
	DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	DbxClientV2 client;
	
	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public RefreshService(DropBoxViewer dropBoxViewer) {
		this.dropBoxViewer = dropBoxViewer;
		
		this.setOnSucceeded(s -> {
		});
		
		this.setOnFailed(f -> {
			
		});
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				try {
					
					//Create the Client
					if (client == null)
						client = new DbxClientV2(config, dropBoxViewer.getAccessToken());
					
					// Get current account info
					FullAccount account = client.users().getCurrentAccount();
					Platform.runLater(() -> dropBoxViewer.getTopLabel().setText(" " + account.getName().getDisplayName()));
					
					TreeMap<String,Metadata> children = new TreeMap<>();
					listAllFiles(client, "", children);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return null;
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
						for (Metadata md : result.getEntries()) {
							if (md instanceof DeletedMetadata) { //Deleted
								children.remove(md.getPathLower());
							} else if (md instanceof FolderMetadata) { //Folder
								String folder = md.getPathLower();
								String parent = new File(md.getPathLower()).getParent();
								children.put(folder, md);
								
								boolean subFileOfCurrentFolder = path.equals(parent.replace("\\", "/"));
								System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "Folder ->" + folder);
								
								//Add to TreeView						
								Platform.runLater(() -> {
									
									dropBoxViewer.getRoot().getChildren().add(new RemoteFileTreeItem(folder));
								});
								
								listAllFiles(client, folder, children);
							} else if (md instanceof FileMetadata) { //File
								String file = md.getPathLower();
								String parent = new File(md.getPathLower()).getParent();
								children.put(file, md);
								
								boolean subFileOfCurrentFolder = path.equals(parent.replace("\\", "/"));
								System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "File->" + file + " Media Info: " + InfoTool.isAudioSupported(file));
								
								//Add to TreeView
								Platform.runLater(() -> {
									dropBoxViewer.getRoot().getChildren().add(new RemoteFileTreeItem(file));
								});
							}
						}
						
						if (!result.getHasMore())
							break;
						
						try {
							result = client.files().listFolderContinue(result.getCursor());
							System.out.println("Entered result next");
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
		};
	}
	
}
