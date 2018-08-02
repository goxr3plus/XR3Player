package main.java.com.goxr3plus.xr3player.application.systemtreeview.services;

import java.util.Optional;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import main.java.com.goxr3plus.xr3player.application.systemtreeview.TreeViewManager;

/**
 * The Class SearchService.
 */
public class TreeViewSearchService extends Service<Void> {
	
	/** The word. */
	private String searchText;
	
	private final TreeViewManager treeViewManager;
	
	/**
	 * Constructor.
	 */
	public TreeViewSearchService(TreeViewManager treeViewManager) {
		this.treeViewManager = treeViewManager;
		
	}
	
	/**
	 * You can start the search Service by calling this method.
	 */
	public void search(String searchText) {
		
		//Search word
		this.searchText = searchText.toLowerCase();
		
		//Restart
		restart();
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			protected Void call() throws Exception {
				
				//Go
				Optional.ofNullable(findElementMatching(treeViewManager.getRoot(), searchText)).ifPresent(item -> Platform.runLater(() -> {
					
					//Select an item
					treeViewManager.getTreeView().getSelectionModel().clearSelection();
					treeViewManager.getTreeView().getSelectionModel().select(item);
					
					//Scroll To
					treeViewManager.getTreeView().scrollTo(treeViewManager.getTreeView().getRow(item));
				}));
				
				return null;
			}
			
			/**
			 * Run all the children of given TreeItem and return back the one matching the value if any
			 * 
			 * @param item
			 * @param value
			 * @return
			 */
			private TreeItem<String> findElementMatching(TreeItem<String> item , String value) {
				if (item != null && item.getValue().toLowerCase().contains(value))
					return item;
				
				//Loop
				for (TreeItem<String> child : item.getChildren()) {
					TreeItem<String> s = findElementMatching(child, value);
					if (s != null)
						return s;
					
				}
				return null;
			}
			
		};
	}
	
}
