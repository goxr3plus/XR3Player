/*
 * 
 */
package com.goxr3plus.xr3player.controllers.systemtree;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.services.systemtree.TreeViewService;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * The Class TreeViewManager.
 * 
 * @author GOXR3PLUS
 *
 */
public class TreeViewManager extends StackPane {

	// -------------------------------------

	@FXML
	private VBox vBox;

	@FXML
	private Label topLabel;

	@FXML
	private Button collapseTree;

	@FXML
	private Label searchLabel;

	@FXML
	private TreeView<String> treeView;

	// -------------------------------------

	/** The search field. */
	private final TextField searchField = TextFields.createClearableTextField();

	/**
	 * The root element of the computer hard drive - drives
	 */
	private final SystemRoot systemRoot = new SystemRoot();

	/** The host name. */
	String hostName = "computer";

	private final TreeViewService service = new TreeViewService(this);

	/**
	 * Constructor.
	 */
	public TreeViewManager() {

		// FXMLLoader
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TreeViewManager.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "TreeViewManager FXML can't be loaded!", ex);
		}

	}

	/** Called as soon as the .fxml has been loaded */
	@FXML
	private void initialize() {

		// ------------------------- TreeView ----------------------------------
		treeView.setRoot(getRoot());
		treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treeView.setShowRoot(false);

		// Mouse Released Event
		treeView.setOnMouseClicked(this::treeViewMouseClicked);
		treeView.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ENTER) { // OPEN/CLOSE FOLDER

				// Any selected TreeItem ?
				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem()).ifPresent(item -> {
					item.setExpanded(!item.isExpanded());
					treeView.scrollTo(treeView.getRow(item));
				});

			}
//			else if (key.getCode() == KeyCode.R) { //RENAME
//				
//				//Any selected TreeItem ?
//				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem()).ifPresent(item -> ( (FileTreeItem) item ).rename(topLabel));
//				
//			}
			else if (key.getCode() == KeyCode.C) { // COPY

				// Any selected TreeItem ?
				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem()).ifPresent(item -> JavaFXTool
						.setClipBoard(Arrays.asList(new File(((FileTreeItem) item).getAbsoluteFilePath()))));

			} else if (key.getCode() == KeyCode.F) { // EXPLORER

				// Any selected TreeItem ?
				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem())
						.ifPresent(item -> IOAction.openFileInExplorer(((FileTreeItem) item).getAbsoluteFilePath()));

			} else if (key.getCode() == KeyCode.I) { // EXPLORER

				// Any selected TreeItem ?
				Optional.ofNullable(treeView.getSelectionModel().getSelectedItem()).ifPresent(item -> {
					final String absoluteFilePath = ((FileTreeItem) item).getAbsoluteFilePath();

					// If it is an audio file
					if (ExtensionTool.isAudio(absoluteFilePath))
						Main.tagWindow.openAudio(absoluteFilePath, TagTabCategory.BASICINFO, true);
				});
			}
		});

		// Drag Implementation
		treeView.setOnDragDetected(event -> {
			// FileTreeItem source = (FileTreeItem)
			// systemTreeView.getSelectionModel().getSelectedItem();

			// The host is not allowed
			// if (source != null && !source.getValue().equals(hostName)) {
			if (!treeView.getSelectionModel().getSelectedItems().isEmpty()) {

				// Allow this transfer Mode
				final Dragboard board = startDragAndDrop(TransferMode.LINK);

				// Put a String on DragBoard
				final ClipboardContent content = new ClipboardContent();
				content.putFiles(treeView.getSelectionModel().getSelectedItems().stream()
						.map(treeItem -> new File(((FileTreeItem) treeItem).getAbsoluteFilePath()))
						.collect(Collectors.toList()));

				board.setContent(content);
				event.consume();
			}
		});

		// collapseTree
		collapseTree.setOnAction(a -> {
			// Trick for CPU based on this question ->
			// https://stackoverflow.com/questions/15490268/manually-expand-collapse-all-treeitems-memory-cost-javafx-2-2
			getRoot().setExpanded(false);

			// Set not expanded all the children
			collapseTreeView(getRoot(), false);

			// Trick for CPU
			getRoot().setExpanded(true);
		});

		// searchField
		searchField.setPromptText("Search...");
		searchField.getStyleClass().add("dark-text-field-rectangle");
		searchField.setMinWidth(0);
		searchField.setMaxWidth(Integer.MAX_VALUE);
		searchField.textProperty().addListener(l -> service.search(searchField.getText()));
		vBox.getChildren().add(searchField);

		// searchLabel
		searchLabel.visibleProperty().bind(service.runningProperty());
	}

	/**
	 * The root of TreeView
	 * 
	 * @return The root of TreeView
	 */
	public FileTreeItem getRoot() {
		return systemRoot.getRoot();
	}

	/**
	 * Collapses the whole TreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(final TreeItem<String> item, final boolean expanded) {
		if (item == null || item.isLeaf())
			return;

		item.setExpanded(expanded);
		item.getChildren().forEach(child -> collapseTreeView(child, expanded));
	}

	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseClicked(final MouseEvent mouseEvent) {
		// Get the selected item
		final FileTreeItem source = (FileTreeItem) treeView.getSelectionModel().getSelectedItem();

		// host is not on the game
		if (source == null || source.getValue().equals(hostName)) {
			mouseEvent.consume();
			return;
		}

		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {

			// source is expanded
			if (!source.isExpanded() && source.getChildren().isEmpty()) {

				// Check if the TreeItem has not children yet
				if (source.getChildren().isEmpty()) {

					// Main Path
					final Path mainPath = Paths.get(source.getAbsoluteFilePath());

					// directory?
					if (mainPath.toFile().isDirectory())
						try (DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {

							// Run the Stream
							stream.forEach(path -> {

								// File or Directory is Hidden? + Directory or Accepted File
								if (!path.toFile().isHidden()) { // Future Filters ->>> && ( path.toFile().isDirectory()
																	// ||
																	// InfoTool.isAudioSupported(path.toFile().getAbsolutePath())
																	// )) {
									final FileTreeItem treeNode = new FileTreeItem(path.toString());
									source.getChildren().add(treeNode);
								}

							});

							// Keep scroll position
							Platform.runLater(() -> treeView.scrollTo(treeView.getRow(source)));
						} catch (final IOException x) {
							x.printStackTrace();
						}

					// Change Listener for Folders to change Icon
					source.expandedProperty().addListener((l, oldValue, newValue) -> {
						if (source.isDirectory())
							source.getIcon().setIconLiteral(newValue ? "fas-folder-open" : "fas-folder");
					}

					);
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
			Main.treeViewContextMenu.show(source, mouseEvent.getScreenX(), mouseEvent.getScreenY());
		}
	}

	/**
	 * @return the searchLabel
	 */
	public Label getSearchLabel() {
		return searchLabel;
	}

	/**
	 * @return the treeView
	 */
	public TreeView<String> getTreeView() {
		return treeView;
	}

	/**
	 * @return the searchService
	 */
	public TreeViewService getService() {
		return service;
	}

}
