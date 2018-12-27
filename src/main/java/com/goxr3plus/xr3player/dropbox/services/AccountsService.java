package main.java.com.goxr3plus.xr3player.dropbox.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import main.java.com.goxr3plus.xr3player.application.tools.fx.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.general.InfoTool;
import main.java.com.goxr3plus.xr3player.dropbox.presenter.DropboxClientTreeItem;
import main.java.com.goxr3plus.xr3player.dropbox.presenter.DropboxViewer;

public class AccountsService extends Service<Boolean> {
	
	/**
	 * DropBoxViewer
	 */
	public DropboxViewer dropBoxViewer;
	
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	
	private final Color goldColor = Color.web("#f4c425");
	
	private List<String> expandedTreeItems;
	
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
		
		//Keep track of the previously expanded TreeItems
		expandedTreeItems = dropBoxViewer.getTreeView().getRoot().getChildren().stream().filter(TreeItem::isExpanded).map(item -> ( (DropboxClientTreeItem) item ).getEmail())
				.collect(Collectors.toList());
		
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
				
				//Create a multimap
				Map<String,List<String>> multimap = new HashMap<>();
				
				//For each item on the list view
				dropBoxViewer.getSavedAccountsArray().forEach(accessToken -> {
					
					try {
						
						//Create the Client
						DbxClientV2 client = new DbxClientV2(config, accessToken);
						
						//Get informations for the client
						String email = client.users().getCurrentAccount().getEmail();
						
						//Add to the map
						multimap.computeIfAbsent(email, k -> new ArrayList<>()).add(accessToken);
						
					} catch (InvalidAccessTokenException e) {
						System.err.println(e.getMessage());
					} catch (DbxException e) {
						e.printStackTrace();
					}
					
					updateProgress(++counter[0], totalSize[0]);
				});
				
				//Based on multimap add the values
				multimap.forEach((email , list) -> {
					
					//Check if list is empty
					if (!list.isEmpty()) {
						//Parent represents the client and it contains the accesstokens as leafs
						DropboxClientTreeItem parent = produceTreeItem(email + " [ " + list.size() + " ]", "no token", email,
								JavaFXTools.getFontIcon("fa-dropbox", DropboxViewer.FONT_ICON_COLOR, 32));
						
						//For each element on the list
						list.forEach(accessToken -> parent.getChildren()
								.add(produceTreeItem(InfoTool.getMinString(accessToken, 25), accessToken, email, JavaFXTools.getFontIcon("fas-key", goldColor, 20))));
						
						//Append to the treeview root item
						dropBoxViewer.getTreeView().getRoot().getChildren().add(parent);
						
						//DEcide if the parent will be expanded
						if (expandedTreeItems.contains(email))
							parent.setExpanded(true);
					}
				});
				
				return true;
			}
			
			/**
			 * Fast method to produce TreeItems without duplicate code
			 * 
			 * @param value
			 * @return
			 */
			private DropboxClientTreeItem produceTreeItem(String value , String accessToken , String email , Node graphic) {
				DropboxClientTreeItem treeItem = new DropboxClientTreeItem(value, accessToken, email);
				treeItem.setGraphic(graphic);
				
				return treeItem;
			}
			
		};
		
	}
	
}
