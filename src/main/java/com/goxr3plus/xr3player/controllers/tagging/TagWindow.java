package com.goxr3plus.xr3player.controllers.tagging;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.enums.FileLinkType;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.models.smartcontroller.Audio;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.FileTypeAndAbsolutePath;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * This window allows to modify Tags of various AudioFormats
 * 
 * @author GOXR3PLUS
 *
 */
public class TagWindow extends StackPane {

	// --------------------------------------------------------

	@FXML
	private SplitPane splitPane;

	@FXML
	private ListView<String> listView;

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private Tab basicInfoTab;

	@FXML
	private Tab artWorkTab;

	@FXML
	private Tab id3v1Tab;

	@FXML
	private Tab id3v2Tab;

	@FXML
	private JFXButton previous;

	@FXML
	private JFXButton next;

	@FXML
	private JFXButton closeButton;

	@FXML
	private Label dragAndDropLabel;

	// --------------------------------------------------------

	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());

	/** The Window */
	private final Stage window = new Stage();

	// For MP3
	private final MP3BasicInfo mp3BasicInfo = new MP3BasicInfo();
	private final ArtWorkController artWork = new ArtWorkController();
	private final ID3v1 id3V1Controller = new ID3v1();
	private final ID3v2 id3V2Controller = new ID3v2();

	/**
	 * Constructor
	 */
	public TagWindow() {

		// ------------------------------------FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.TAGS_FXMLS + "TagWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

		// Window
		window.setTitle("Tag Window");
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().setOnDragOver(event -> dragAndDropLabel.setVisible(true));
		window.getScene().setOnKeyReleased(k -> {
			if (k.getCode() == KeyCode.ESCAPE)
				window.close();
		});

	}

	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {

		// dragAndDropLabel
		dragAndDropLabel.setVisible(false);
		dragAndDropLabel.setOnDragOver(event -> event.acceptTransferModes(TransferMode.LINK));
		dragAndDropLabel.setOnDragDropped(event -> {
			// File?
			for (File file : event.getDragboard().getFiles()) {

				// No directories allowed
				if (!file.isDirectory()) {

					// Get it
					FileTypeAndAbsolutePath ftaap = IOInfo.getRealPathFromFile(file.getAbsolutePath());

					// Check if File exists
					if (!new File(ftaap.getFileAbsolutePath()).exists()) {
						AlertTool.showNotification("File doesn't exist",
								(ftaap.getFileType() == FileLinkType.SYMBOLIC_LINK ? "Symbolic link" : "Windows Shortcut")
										+ " points to a file that doesn't exists anymore.",
								Duration.millis(2000), NotificationType.INFORMATION);
						return;
					}

					openAudio(file.getAbsolutePath(), TagTabCategory.BASICINFO, true);

					// break
					break;
				}
			}

			event.consume();

		});
		dragAndDropLabel.setOnDragExited(event -> {
			dragAndDropLabel.setVisible(false);
			event.consume();
		});

		// basicInfoTab
		basicInfoTab.setContent(mp3BasicInfo);

		// artWorkTab
		artWorkTab.setContent(artWork);

		// ImageView
		artWork.getImageView().fitWidthProperty().bind(window.widthProperty().subtract(20));
		artWork.getImageView().fitHeightProperty().bind(window.heightProperty().subtract(145));

		// id3v1Tab
		id3v1Tab.setContent(id3V1Controller);

		// id3v2Tab
		id3v2Tab.setContent(id3V2Controller);

		// listView
		// listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
		listView.setCellFactory(lv -> new ListCell<>() {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setText(IOInfo.getFileName(item));
					setTooltip(new Tooltip(item));
				}
			}
		});
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				openAudio(newValue, TagTabCategory.CURRENT, false);
		});

		// Drag Implementation
		listView.setOnDragDetected(event -> {

			if (!listView.getSelectionModel().getSelectedItems().isEmpty()) {

				// Allow this transfer Mode
				Dragboard board = startDragAndDrop(TransferMode.LINK);

				// Put a String on DragBoard
				ClipboardContent content = new ClipboardContent();
				content.putFiles(listView.getSelectionModel().getSelectedItems().stream().map(item -> new File(item))
						.collect(Collectors.toList()));

				board.setContent(content);
				event.consume();
			}
		});

		// previous
		previous.setOnAction(a -> {
			// listView.getSelectionModel().clearSelection()

			// Rotation on list if you finish start
			if (listView.getSelectionModel().getSelectedIndex() != 0)
				listView.getSelectionModel().selectPrevious();
			else
				listView.getSelectionModel().selectLast();
		});

		// next
		next.setOnAction(a -> {
			// listView.getSelectionModel().clearSelection()

			// Rotation on list if you finish start
			if (listView.getSelectionModel().getSelectedIndex() != listView.getItems().size() - 1)
				listView.getSelectionModel().selectNext();
			else
				listView.getSelectionModel().selectFirst();
		});

		// TabPane
		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == id3v1Tab)
				id3V1Controller.populateTagFields(listView.getSelectionModel().getSelectedItem());
			else if (newValue == id3v2Tab)
				id3V2Controller.populateTagFields(listView.getSelectionModel().getSelectedItem());

		});

		// closeButton
		closeButton.setOnAction(a -> close());

	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}

	/**
	 * Show the Window
	 */
	public void show() {
		if (!window.isShowing())
			window.show();
		else
			window.requestFocus();
	}

	/**
	 * Opens multiple audio files at once ( based on the ListView that is created)
	 * 
	 * @param list         A given observable list containing all the absolute file
	 *                     paths
	 * @param selectedItem Not null if a specific file item must be selected first
	 */
	public void openMultipleAudioFiles(ObservableList<String> list, String selectedItem) {
		listView.setItems(list);
		if (selectedItem != null) {
			listView.getSelectionModel().select(selectedItem);
			listView.scrollTo(selectedItem);
		} else
			listView.getSelectionModel().select(0);
	}

	/**
	 * Open the TagWindow based on the extension of the Audio
	 * 
	 * @param absolutePath The absolute path of the file
	 * @param tabCategory  The tag tab category
	 */
	public void openAudio(String absolutePath, TagTabCategory tabCategory, boolean clearListView) {
		// Clear listView
		if (clearListView) {
			listView.setItems(Arrays.asList(absolutePath).stream()
					.collect(Collectors.toCollection(FXCollections::observableArrayList)));
			listView.getSelectionModel().select(0);
			previous.setDisable(true);
			next.setDisable(true);
		} else {
			previous.setDisable(false);
			next.setDisable(false);
		}

		// Check the absolutePath
		if (absolutePath != null) {

			// Find file extension
			String extension = IOInfo.getFileExtension(absolutePath);

			// Current Tab
			int currentTabSelected = tabPane.getSelectionModel().getSelectedIndex();

			// Clear Tab Pane Tabs
			tabPane.getTabs().clear();

			// mp3?
			if (extension.equalsIgnoreCase("mp3")) {

				// Add the tabs
				tabPane.getTabs().addAll(basicInfoTab, artWorkTab, id3v1Tab, id3v2Tab);

				// Check the tabCategory
				if (tabCategory == TagTabCategory.BASICINFO)
					tabPane.getSelectionModel().select(0);
				else if (tabCategory == TagTabCategory.ARTWORK)
					tabPane.getSelectionModel().select(1);
				else if (tabCategory == TagTabCategory.CURRENT)
					tabPane.getSelectionModel().select(currentTabSelected);

				// basicInfoTab
				mp3BasicInfo.updateInformation(new Audio(absolutePath, 0.0, 0, "", "", Genre.SEARCHWINDOW, -1));

				// artWorkTab
				artWork.showMediaFileImage(absolutePath);

				// id3v1Tab
				// id3v1Tab.setContent(id3V1Controller)

				// id3v2Tab
				// id3v2Tab.setContent(id3V2Controller)

			} else {

				// Add the tabs
				tabPane.getTabs().addAll(basicInfoTab);

				// basicInfoTab
				mp3BasicInfo.updateInformation(new Audio(absolutePath, 0.0, 0, "", "", Genre.SEARCHWINDOW, -1));

			}

			show();
		} else
			AlertTool.showNotification("No File", "No File has been selected ...", Duration.seconds(2),
					NotificationType.SIMPLE);
	}

}
