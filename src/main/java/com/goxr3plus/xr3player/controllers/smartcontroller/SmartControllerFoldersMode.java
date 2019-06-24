package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController.SettingsTab;
import com.goxr3plus.xr3player.controllers.systemtree.FileTreeItem;
import com.goxr3plus.xr3player.services.smartcontroller.FoldersModeService;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class SmartControllerFoldersMode extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private Label topLabel;

	@FXML
	private Button settings;

	@FXML
	private Button refresh;

	@FXML
	private TreeView<String> treeView;

	@FXML
	private Label detailsLabel;

	@FXML
	private Button backToMedia;

	@FXML
	private VBox indicatorVBox;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private Button collapseTree;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	// -------------------------------------------------------------

	private final FoldersModeService service = new FoldersModeService(this);

	/** A private instance of the SmartController it belongs */
	private final SmartController smartController;

	// -------------------------------------------------------------

	private final FileTreeItem root = new FileTreeItem("root");

	// -------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public SmartControllerFoldersMode(SmartController smartController) {
		this.smartController = smartController;

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "SmartControllerFoldersMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

		// --Drag Over
		super.setOnDragOver(dragOver -> {

			// The drag must come from source other than the owner
			if (dragOver.getGestureSource() != smartController.getNormalModeMediaTableViewer())// &&
																								// dragOver.getGestureSource()
																								// !=
																								// smartController.foldersMode)
				dragOver.acceptTransferModes(TransferMode.LINK);

		});

		// --Drag Dropped
		super.setOnDragDropped(drop -> {
			// Has Files? + isFree()?
			if (drop.getDragboard().hasFiles() && smartController.isFree(true))
				smartController.getInputService().start(drop.getDragboard().getFiles());

			drop.setDropCompleted(true);
		});

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		// TreeView
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// Mouse Released Event
		treeView.setOnMouseClicked(this::treeViewMouseClicked);

		// Drag Implementation
		treeView.setOnDragDetected(event -> {
			// FileTreeItem source = (FileTreeItem)
			// treeView.getSelectionModel().getSelectedItem();

			// The host is not allowed
			// if (source != null && source != root) {
			ObservableList<TreeItem<String>> selectedItems = treeView.getSelectionModel().getSelectedItems();
			if (!selectedItems.isEmpty()) {

				// Allow this transfer Mode
				Dragboard board = startDragAndDrop(TransferMode.LINK);

				// Put a String on DragBoard
				ClipboardContent content = new ClipboardContent();
				content.putFiles(selectedItems.stream()
						.map(treeItem -> new File(((FileTreeItem) treeItem).getAbsoluteFilePath()))
						.collect(Collectors.toList()));

				board.setContent(content);
				event.consume();
			}
		});

		// Custom Cell Factory
		treeView.setCellFactory(tv -> new TreeCell<>() {

			/**
			 * FontIcon
			 */
			private FontIcon icon = new FontIcon();

			{
				icon.setIconSize(24);
			}

			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				getStyleClass().remove("tree-cell-2");
				if (empty) {
					setGraphic(null);
					setText("");
				} else {
					setText(item);
					String absoluteFilePath = ((FileTreeItem) getTreeItem()).getAbsoluteFilePath();

					// We don't care about directories
					if (!((FileTreeItem) getTreeItem()).isDirectory()) {
						boolean existsInPlayList = smartController.containsFile(absoluteFilePath);

						// Check if the file exists inside the SmartController Playlist
						if (!existsInPlayList)
							getStyleClass().add("tree-cell-2");

						setGraphic(getTreeItem().getGraphic());
					} else {
						if (getTreeItem().isExpanded())
							setFontIcon("fas-folder-open", Color.web("#ddaa33"));
						else
							setFontIcon("fas-folder", Color.web("#ddaa33"));
					}

				}
			}

			/**
			 * Set Graphic Font Icon
			 *
			 * @param iconLiteral
			 * @param color
			 */
			private void setFontIcon(String iconLiteral, Color color) {
				icon.setIconLiteral(iconLiteral);
				icon.setIconColor(color);
				setGraphic(icon);
			}
		});

		// indicatorVBox
		indicatorVBox.visibleProperty().bind(service.runningProperty());

		// Progress Indicator
		progressIndicator.progressProperty().bind(service.progressProperty());

		// collapseTree
		collapseTree.setOnAction(a ->

		{
			// Trick for CPU based on this question ->
			// https://stackoverflow.com/questions/15490268/manually-expand-collapse-all-treeitems-memory-cost-javafx-2-2
			root.setExpanded(false);

			// Set not expanded all the children
			collapseTreeView(root, false);

			// Trick for CPU
			root.setExpanded(true);
		});

		// refresh
		refresh.setOnAction(a -> recreateTree());

		// settings
		settings.setOnAction(a -> {
			Main.settingsWindow.getPlayListsSettingsController().getInnerTabPane().getSelectionModel().select(1);
			Main.settingsWindow.showWindow(SettingsTab.PLAYLISTS);
		});

		// backToMedia
		backToMedia.setOnAction(a -> smartController.getModesTabPane().getSelectionModel().select(0));

	}

	/**
	 * Collapses the whole TreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(TreeItem<String> item, boolean expanded) {
		if (item == null || item.isLeaf())
			return;

		item.setExpanded(expanded);
		item.getChildren().forEach(child -> collapseTreeView(child, expanded));
	}

	/**
	 * Recreates the tree from the bottom based on the SmartController 1)Settings
	 * 2)Files
	 */
	public void recreateTree() {

		// Clear all the children
		root.getChildren().clear();

		// Start the Service
		service.restart();
	}

	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseClicked(MouseEvent mouseEvent) {
		// Get the selected item
		FileTreeItem source = (FileTreeItem) treeView.getSelectionModel().getSelectedItem();

		// host is not on the game
		if (source == null || source == root) {
			mouseEvent.consume();
			return;
		}

		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {

			// source is expanded
			if (!source.isExpanded() && source.getChildren().isEmpty()) {
				// if (source.isDirectory())
				// source.setGraphic(new ImageView(SystemRoot.openedFolderImage))

				// Check if the TreeItem has not children yet
				if (source.getChildren().isEmpty()) {

					// Main Path
					Path mainPath = Paths.get(source.getAbsoluteFilePath());

					// directory?
					if (mainPath.toFile().isDirectory()) {
						try (DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
							boolean showOnlyFilesThatExistToPlaylist = ((Control) Main.settingsWindow
									.getPlayListsSettingsController().getFilesToShowUnderFolders().getSelectedToggle())
											.getTooltip().getText().equals("1");

							// Run the Stream
							stream.forEach(path -> {

								// File or Directory is Hidden? + Directory or Accepted File
								if (!path.toFile().isHidden() && (path.toFile().isDirectory()
										|| ExtensionTool.isAudioSupported(path.toFile().getAbsolutePath()))) {

									// We don't care about directories
									if (!path.toFile().isDirectory()) {

										// showOnlyFilesThatExistToPlaylist ? if so -> check if the file exists inside
										// the Playlist
										if (showOnlyFilesThatExistToPlaylist) {
											if (smartController.containsFile(path.toFile().getAbsolutePath())) {

												// Create the TreeItem
												FileTreeItem treeNode = new FileTreeItem(path.toString());

												// Append
												source.getChildren().add(treeNode);
											}
										} else {

											// Create the TreeItem
											FileTreeItem treeNode = new FileTreeItem(path.toString());

											// Append
											source.getChildren().add(treeNode);

										}
									} else {

										// Create the TreeItem
										FileTreeItem treeNode = new FileTreeItem(path.toString());

										// Append
										source.getChildren().add(treeNode);
									}
								}

							});

						} catch (IOException x) {
							x.printStackTrace();
						}

						// Keep scroll position
						Platform.runLater(() -> treeView.scrollTo(treeView.getRow(source)));
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
			// Main.songsContextMenu.showContextMenu(row.itemProperty().get(),
			// smartController.getGenre(), mouseEvent.getScreenX(), mouseEvent.getScreenY(),
			// smartController, row);
			Main.treeViewContextMenu.show(source, mouseEvent.getScreenX(), mouseEvent.getScreenY());
		}
	}

	/**
	 * @return the smartController
	 */
	public SmartController getSmartController() {
		return smartController;
	}

	/**
	 * @return the root of the tree
	 */
	public FileTreeItem getRoot() {
		return root;
	}

	/**
	 * @return the progressIndicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}

	/**
	 * @param progressIndicator the progressIndicator to set
	 */
	public void setProgressIndicator(ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
	}

	/**
	 * @return the detailsLabel
	 */
	public Label getDetailsLabel() {
		return detailsLabel;
	}

	/**
	 * @return the topLabel
	 */
	public Label getTopLabel() {
		return topLabel;
	}

	/**
	 * @return the service
	 */
	public FoldersModeService getService() {
		return service;
	}

}
