package main.java.com.goxr3plus.xr3player.remote.dropbox.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropboxViewer;

public class AccountsService extends Service<Boolean> {
	
	/**
	 * DropBoxViewer
	 */
	public DropboxViewer dropBoxViewer;
	
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	
	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public AccountsService(DropboxViewer dropBoxViewer) {
		this.dropBoxViewer = dropBoxViewer;
	}
	
	/**
	 * Restart the Service
	 */
	public void restartService() {
		//Clear TreeView
		dropBoxViewer.getTreeView().getRoot().getChildren().clear();
		
		//Restart
		super.restart();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				int[] counter = { 0 };
				int[] totalSize = { dropBoxViewer.getSavedAccountsArray().size() };
				
				//For each item on the list view
				dropBoxViewer.getSavedAccountsArray().forEach(accessToken -> {
					
					try {
						
						//Create the Client
						DbxClientV2 client = new DbxClientV2(config, accessToken);
						
						//Get informations for the client
						String email = client.users().getCurrentAccount().getEmail();
						
						//Here we append items to the treeView in a smart way
						dropBoxViewer.getTreeView().getRoot().getChildren().stream().
						//Filter
						filter(item -> item.getValue().equals(email)).findFirst().ifPresentOrElse(
								//Append AccessTokens to this  TreeItem
								item -> item.getChildren().add(new TreeItem<String>(accessToken)),
								//Append this as a parent item which will have leafs	
								() -> dropBoxViewer.getTreeView().getRoot().getChildren().add(new TreeItem<String>(email)));
						
					} catch (DbxException e) {
						e.printStackTrace();
					}
					
					updateProgress(++counter[0], totalSize[0]);
				});
				
				return true;
			}
			
		};
		
	}
	
}
