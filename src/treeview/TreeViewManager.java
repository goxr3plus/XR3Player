/*
 * 
 */
package treeview;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;

/**
 * The Class TreeViewManager.
 */
public class TreeViewManager extends BorderPane {
	
	@FXML
	private TreeView<String> systemTreeView;
	
	// -------------------------------------
	
	/**
	 * The root element of the computer hard drive - drives
	 */
	SystemRoot systemRoot = new SystemRoot();
	
	/** The libraries tree. */
	public TreeItem<String> librariesTree = new TreeItem<>("Libraries",
	        InfoTool.getImageViewFromDocuments("folder.png"));
	
	/** The tree context menu. */
	public TreeViewContextMenu treeContextMenu = new TreeViewContextMenu();
	
	/** The host name. */
	String hostName = "computer";
	
	/**
	 * Constructor.
	 */
	public TreeViewManager() {
		
		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "TreeViewManager.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "TreeViewManager FXML can't be loaded!", ex);
		}
		
	}
	
	/** Called as soon as the .fxml has been loaded */
	@FXML
	private void initialize() {
		
		// ------------------------- TreeView ----------------------------------
		systemTreeView.setRoot(systemRoot.getRoot());
		
		systemTreeView.setOnMouseReleased(m -> {
			SystemFileItem source = (SystemFileItem) systemTreeView.getSelectionModel().getSelectedItem();
			System.out.println(source.getValue());
			
			// No source is Selected
			if (source == null)
				return;
			
			if (!source.getValue().equals(hostName)) // To host δεν ανακατεύεται
				if (m.getButton() == MouseButton.PRIMARY && m.getClickCount() == 2) {
					System.out.println(source.getValue());
					
					// Eάν δεν είναι ο φάκελος [expanded]
					if (!source.isExpanded() && source.getChildren().isEmpty()) {
						if (source.isDirectory())
							source.setGraphic(new ImageView(SystemRoot.openedFolderImage));
						
						try {
							if (source.getChildren().isEmpty()) {
								Path mainPath = Paths.get(source.getFullPath());
								if (Files.isDirectory(mainPath)) {
									DirectoryStream<Path> dir = Files.newDirectoryStream(mainPath);
									for (Path path : dir) {
										if (!path.toFile().isHidden()) { // hidden?
											if (path.toFile().isDirectory()) { // directory?
												SystemFileItem treeNode = new SystemFileItem(path.toString());
												source.getChildren().add(treeNode);
											} else if (InfoTool.isAudioSupported(path.toFile().getAbsolutePath())) { // file?
												SystemFileItem treeNode = new SystemFileItem(path.toString());
												source.getChildren().add(treeNode);
											}
										}
									}
								}
							} else {
								// if you want to implement rescanning a
								// directory
								// for
								// changes this would be the place to do it
							}
						} catch (IOException x) {
							x.printStackTrace();
						}
						
						source.setExpanded(true);
						// Εάν ο φάκελος δεν είναι expanded
					} // else if (!source.isExpanded() && source.isDirectory())
					  // {
					  // source.setGraphic(new
					  // ImageView(folderCollapseImage));
					  // }
					
				} else if (m.getButton() == MouseButton.SECONDARY) {
					// Main.treeManager.treeContextMenu.showMenu(Genre.SYSTEMFILE,
					// source.getFullPath(),
					// m.getScreenX(), m.getScreenY());
				}
		});
		
		// Drag Implementation
		systemTreeView.setOnDragDetected(event -> {
			SystemFileItem source = (SystemFileItem) systemTreeView.getSelectionModel().getSelectedItem();
			
			if (!source.getValue().equals(hostName)) { // To host δεν
			                                           // ανακατεύεται
				// Allow this transfer Mode
				Dragboard board = startDragAndDrop(TransferMode.LINK);
				
				// Put a String on DragBoard
				ClipboardContent content = new ClipboardContent();
				content.putFiles(Arrays.asList(new File(source.getFullPath())));
				
				board.setContent(content);
				event.consume();
			}
		});
		
		// ---------------------------LibraryTreeView--------------------------------------
		
		// TreeView<String> libraryTreeView = new TreeView<>(librariesTree);
		// libraryTreeView.setEditable(true);
		//
		// // Mouse Clicked Implementation
		// libraryTreeView.setOnMouseClicked(m -> {
		// TreeItem<String> selected =
		// libraryTreeView.getSelectionModel().getSelectedItem();
		//
		// if (!selected.getValue().equals(librariesTree.getValue())) {
		// // if (m.getButton() == MouseButton.SECONDARY) {
		// // treeContextMenu.showMenu(Genre.LIBRARY, selected.getValue(),
		// // m.getScreenX(), m.getSceneY());
		// // }
		// }
		// });
		//
		// // Drag Implementation
		// libraryTreeView.setOnDragDetected(event -> {
		// TreeItem<String> source =
		// libraryTreeView.getSelectionModel().getSelectedItem();
		//
		// if (!source.getValue().equals(librariesTree.getValue())) {
		//
		// // Allow this transfer Mode
		// Dragboard board = startDragAndDrop(TransferMode.LINK);
		//
		// // Put a String on DragBoard
		// ClipboardContent content = new ClipboardContent();
		// content.putString("!PATTERN LIBRARY!" + source.getValue());
		//
		// board.setContent(content);
		// event.consume();
		// }
		// });
	}
	
}
