package com.goxr3plus.xr3player.services.systemtree;

import java.util.Optional;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import com.goxr3plus.xr3player.enums.Operation;
import com.goxr3plus.xr3player.controllers.systemtree.FileTreeItem;
import com.goxr3plus.xr3player.controllers.systemtree.TreeViewManager;

/**
 * The Class TreeViewService.
 */
public class TreeViewService extends Service<Void> {

	/** The word. */
	private String searchText;
	private String oldFilePath;
	private String newFilePath;
	private Operation operation;

	private final TreeViewManager treeViewManager;

	/**
	 * Constructor.
	 */
	public TreeViewService(TreeViewManager treeViewManager) {
		this.treeViewManager = treeViewManager;

	}

	/**
	 * You can start the search Service by calling this method.
	 */
	public void search(String searchText) {

		// Variables
		this.searchText = searchText.toLowerCase();
		this.operation = Operation.SEARCH;
		treeViewManager.getSearchLabel().setText("Searching");

		// Restart
		restart();
	}

	public void rename(String oldFilePath, String newFilePath) {

		// Variables
		this.oldFilePath = oldFilePath;
		this.newFilePath = newFilePath;
		this.operation = Operation.RENAME;
		treeViewManager.getSearchLabel().setText("Renaming");

		// Restart
		restart();
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<>() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			protected Void call() throws Exception {

				if (operation == Operation.SEARCH) { // Operation.SEARCH

					// Go
					Optional.ofNullable(findElementMatching(treeViewManager.getRoot(), searchText))
							.ifPresent(item -> Platform.runLater(() -> {

								// Select an item
								treeViewManager.getTreeView().getSelectionModel().clearSelection();
								treeViewManager.getTreeView().getSelectionModel().select(item);

								// Scroll To
								treeViewManager.getTreeView().scrollTo(treeViewManager.getTreeView().getRow(item));
							}));

				} else if (operation == Operation.RENAME) {// Operation.RENAME

					// Search for it if exists in the Tree
					Optional.ofNullable(findElementMatching2(treeViewManager.getRoot(), oldFilePath)).ifPresent(
							// If present
							item -> Platform.runLater(() -> ((FileTreeItem) item).setAbsoluteFilePath(newFilePath)));

				}

				return null;
			}

			/**
			 * Run all the children of given TreeItem and return back the one matching the
			 * value if any
			 *
			 * @param item
			 * @param value
			 * @return
			 */
			private TreeItem<String> findElementMatching(TreeItem<String> item, String value) {
				if (item != null && item.getValue().toLowerCase().contains(value))
					return item;

				// Loop
				for (TreeItem<String> child : item.getChildren()) {
					TreeItem<String> s = findElementMatching(child, value);
					if (s != null)
						return s;

				}
				return null;
			}

			/**
			 * This method is the same with the above though i am using it for file renaming
			 *
			 * @param item
			 * @param fileAbsolutePath
			 * @return
			 */
			private TreeItem<String> findElementMatching2(TreeItem<String> item, String fileAbsolutePath) {
				if (item != null && ((FileTreeItem) item).getAbsoluteFilePath().equals(fileAbsolutePath))
					return item;

				// Loop
				for (TreeItem<String> child : item.getChildren()) {
					TreeItem<String> s = findElementMatching2(child, fileAbsolutePath);
					if (s != null)
						return s;

				}
				return null;
			}

		};
	}

}
