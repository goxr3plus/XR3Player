/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.presenter.treeview;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The Class TreeViewManager.
 * 
 * @author GOXR3PLUS
 *
 */
public class TreeViewManager extends BorderPane {
	
	// -------------------------------------
	
	@FXML
	private TreeView<String> systemTreeView;
	
	//	@FXML
	//	private Button searchButton
	
	@FXML
	private Button collapseTree;
	
	// -------------------------------------
	
	/**
	 * The root element of the computer hard drive - drives
	 */
	private final SystemRoot systemRoot = new SystemRoot();
	
	/** The libraries tree. */
	public TreeItem<String> librariesTree = new TreeItem<>("Libraries", InfoTool.getImageViewFromResourcesFolder("folder.png"));
	
	/** The host name. */
	String hostName = "computer";
	
	/**
	 * Constructor.
	 */
	public TreeViewManager() {
		
		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TreeViewManager.fxml"));
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
		systemTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// Mouse Released Event
		systemTreeView.setOnMouseClicked(this::treeViewMouseClicked);
		
		// Drag Implementation
		systemTreeView.setOnDragDetected(event -> {
			//FileTreeItem source = (FileTreeItem) systemTreeView.getSelectionModel().getSelectedItem();
			
			//The host is not allowed
			//if (source != null && !source.getValue().equals(hostName)) {
			if (!systemTreeView.getSelectionModel().getSelectedItems().isEmpty()) {
				
				// Allow this transfer Mode
				Dragboard board = startDragAndDrop(TransferMode.LINK);
				
				// Put a String on DragBoard
				ClipboardContent content = new ClipboardContent();
				content.putFiles(systemTreeView.getSelectionModel().getSelectedItems().stream().map(treeItem -> new File( ( (FileTreeItem) treeItem ).getFullPath()))
						.collect(Collectors.toList()));
				
				board.setContent(content);
				event.consume();
			}
		});
		
		//searchButton
		//searchButton.setOnAction(a -> Main.specialChooser.prepareToImportSongFiles(Main.window));
		
		//collapseTree
		collapseTree.setOnAction(a -> {
			//Trick for CPU based on this question -> https://stackoverflow.com/questions/15490268/manually-expand-collapse-all-treeitems-memory-cost-javafx-2-2
			systemRoot.getRoot().setExpanded(false);
			
			//Set not expanded all the children
			collapseTreeView(systemRoot.getRoot(), false);
			
			//Trick for CPU
			systemRoot.getRoot().setExpanded(true);
		});
		
	}
	
	/**
	 * Collapses the whole TreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(TreeItem<String> item , boolean expanded) {
		if (item == null || item.isLeaf())
			return;
		
		item.setExpanded(expanded);
		item.getChildren().forEach(child -> collapseTreeView(child, expanded));
	}
	
	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent
	 *            [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseClicked(MouseEvent mouseEvent) {
		//Get the selected item
		FileTreeItem source = (FileTreeItem) systemTreeView.getSelectionModel().getSelectedItem();
		
		// host is not on the game
		if (source == null || source.getValue().equals(hostName)) {
			mouseEvent.consume();
			return;
		}
		
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
			
			// source is expanded
			if (!source.isExpanded() && source.getChildren().isEmpty()) {
				//if (source.isDirectory())
				//source.setGraphic(new ImageView(SystemRoot.openedFolderImage));
				
				//Check if the TreeItem has not children yet
				if (source.getChildren().isEmpty()) {
					
					//Main Path
					Path mainPath = Paths.get(source.getFullPath());
					
					// directory?				
					if (mainPath.toFile().isDirectory())
						try (DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
							
							//Run the Stream
							stream.forEach(path -> {
								
								// File or Directory is Hidden? + Directory or Accepted File
								if (!path.toFile().isHidden() && ( path.toFile().isDirectory() || InfoTool.isAudioSupported(path.toFile().getAbsolutePath()) )) {
									FileTreeItem treeNode = new FileTreeItem(path.toString());
									source.getChildren().add(treeNode);
								}
								
							});
							
						} catch (IOException x) {
							x.printStackTrace();
						}
					
				} else {
					// if you want to implement rescanning a
					// directory
					// for
					// changes this would be the place to do it
				}
				source.setExpanded(true);
			}
			
			// if (!source.isExpanded() && source.isDirectory())
			// source.setGraphic(new ImageView(SystemRoot.folderImage))
			
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			Main.treeViewContextMenu.show(source.getFullPath(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
		}
	}
	
}
