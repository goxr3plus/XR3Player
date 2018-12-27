package main.java.com.goxr3plus.xr3player.services.dropbox;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.controllers.dropbox.DropboxViewer;
import main.java.com.goxr3plus.xr3player.services.dropbox.DropboxService.DropBoxOperation;

/**
 * The duty of this class is to Cache Dropbox Account Search so it is superior fast :)
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class SearchCacheService extends Service<Boolean> {
	
	/**
	 * ArrayList holding all the Dropbox user File paths
	 */
	private final List<Metadata> cachedList;
	private DbxClientV2 client;
	private DropboxViewer dropBoxViewer;
	
	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public SearchCacheService(DropboxViewer dropBoxViewer) {
		this.dropBoxViewer = dropBoxViewer;
		cachedList = new ArrayList<>();
	}
	
	/**
	 * Prepares the Cache Structure
	 * 
	 * @param client
	 */
	public void prepareCachedSearch(DbxClientV2 client) {
		this.client = client;
		cachedList.clear();
		
		//Restart
		restart();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			
			@Override
			protected Boolean call() throws Exception {
				boolean success = true;
				
				//Update Indicator Label
				Platform.runLater(() -> dropBoxViewer.getCachedSearchIndicator().getTooltip().setText("Preparing Cached Search"));
				
				try {
					if (cachedList.isEmpty()) // Check if cachedList is empty
						populateCachedList("");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				//Update progress
				updateProgress(1, 1);
				
				//Update Indicator Label
				Platform.runLater(() -> dropBoxViewer.getCachedSearchIndicator().getTooltip().setText("Superior Cached Search is Enabled"));
				
				System.out.println("Search Cache Service Exited");
				
				return success;
			}
			
			/**
			 * List all the Files inside DropboxAccount
			 * 
			 * @param path
			 *            The searting path
			 */
			private void populateCachedList(String path) throws DbxException {
				
				if (this.isCancelled())
					return;
				
				//System.out.println("Running...");
				
				//Exit if normal search is on progress , it will populate the 
				if (dropBoxViewer.getDropBoxService().getOperation() == DropBoxOperation.SEARCH) {
					//System.out.println("Search Cache Service exiting ....");
					
					//Cancel the Service and let the DropboxService search
					//to populate the cachedList
					this.cancel();
				} else if (dropBoxViewer.getDropBoxService().getOperation() == DropBoxOperation.REFRESH) {
					//System.out.println("Dropbox Service is running...");
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				ListFolderResult result = client.files().listFolder(path);
				
				//While true 
				while (true) {
					for (Metadata metadata : result.getEntries()) {
						
						//Check if cancelled
						if (this.isCancelled())
							break;
						
						//Check the metadata
						if (! ( metadata instanceof DeletedMetadata ))
							if (! ( metadata instanceof FolderMetadata )) {
								if (metadata instanceof FileMetadata)
									cachedList.add(metadata);
							} else {
								String folder = metadata.getPathLower();
								cachedList.add(metadata);
								populateCachedList(folder);
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
			
		};
	}
	
	/**
	 * @return the cachedList
	 */
	public List<Metadata> getCachedList() {
		return cachedList;
	}
	
}
