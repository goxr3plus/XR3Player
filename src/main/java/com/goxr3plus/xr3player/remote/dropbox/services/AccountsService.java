package main.java.com.goxr3plus.xr3player.remote.dropbox.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropboxClientTreeItem;
import main.java.com.goxr3plus.xr3player.remote.dropbox.presenter.DropboxViewer;

public class AccountsService extends Service<Boolean> {
	
	/**
	 * DropBoxViewer
	 */
	public DropboxViewer dropBoxViewer;
	
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	
	private final Color goldColor = Color.web("#f4c425");
	
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
								item -> item.getChildren()
										.add(produceTreeItem(InfoTool.getMinString(accessToken, 8), accessToken, JavaFXTools.getFontIcon("fas-key", goldColor, 20))),
								//Append this as a parent item which will have leafs	
								() -> dropBoxViewer.getTreeView().getRoot().getChildren()
										.add(produceTreeItem(email, accessToken, JavaFXTools.getFontIcon("fa-dropbox", DropboxViewer.FONT_ICON_COLOR, 32))));
						
					} catch (DbxException e) {
						e.printStackTrace();
					}
					
					updateProgress(++counter[0], totalSize[0]);
				});
				
				return true;
			}
			
			/**
			 * Fast method to produce TreeItems without duplicate code
			 * 
			 * @param value
			 * @return
			 */
			private TreeItem<String> produceTreeItem(String value , String accessToken , Node graphic) {
				DropboxClientTreeItem treeItem = new DropboxClientTreeItem(value, accessToken);
				treeItem.setGraphic(graphic);
				
				return treeItem;
			}
			
		};
		
	}
	
}
