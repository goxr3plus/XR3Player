/*
 * 
 */
package application.presenter.treeview;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

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
	
	@FXML
	private Button searchButton;
	
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
		
		// Mouse Released Event
		systemTreeView.setOnMouseReleased(this::treeViewMouseReleased);
		
		// Drag Implementation
		systemTreeView.setOnDragDetected(event -> {
			TreeItemFile source = (TreeItemFile) systemTreeView.getSelectionModel().getSelectedItem();
			
			//The host is not allowed
			if (source != null && !source.getValue().equals(hostName)) {
				
				// Allow this transfer Mode
				Dragboard board = startDragAndDrop(TransferMode.LINK);
				
				// Put a String on DragBoard
				ClipboardContent content = new ClipboardContent();
				content.putFiles(Arrays.asList(new File(source.getFullPath())));
				
				board.setContent(content);
				event.consume();
			}
		});
		
		//searchButton
		searchButton.setOnAction(a -> Main.specialChooser.prepareToImportSongFiles(Main.window));
		
	}
	
	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent
	 *            [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseReleased(MouseEvent mouseEvent) {
		//Get the selected item
		TreeItemFile source = (TreeItemFile) systemTreeView.getSelectionModel().getSelectedItem();
		
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
									TreeItemFile treeNode = new TreeItemFile(path.toString());
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
			// Main.treeManager.treeContextMenu.showMenu(Genre.SYSTEMFILE,
			// source.getFullPath(),
			// m.getScreenX(), m.getScreenY());
		}
	}
	
}
