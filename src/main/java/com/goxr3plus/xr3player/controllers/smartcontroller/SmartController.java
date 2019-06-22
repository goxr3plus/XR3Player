/*
 * 
 */
package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.fxmisc.richtext.InlineCssTextArea;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTabPane;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.general.Viewer;
import com.goxr3plus.xr3player.controllers.settings.ApplicationSettingsController.SettingsTab;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.services.smartcontroller.FilesExportService;
import com.goxr3plus.xr3player.services.smartcontroller.InputService;
import com.goxr3plus.xr3player.services.smartcontroller.LoadService;
import com.goxr3plus.xr3player.services.smartcontroller.MediaViewerService;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * Used to control big amounts of Media using a TableViewer mechanism
 *
 * @author GOXR3PLUS
 */
public class SmartController extends StackPane {

	// ----------------------------------------------------------------

	@FXML
	private SplitPane viewerSplitPane;

	@FXML
	private StackPane viewerStackPane;

	@FXML
	private BorderPane viewerBorderPane;

	@FXML
	private Button viewerNext;

	@FXML
	private Button viewerPrevious;

	@FXML
	private Label noAlbumViewsLabel;

	@FXML
	private VBox mediaViewerVBox;

	@FXML
	private Label mediaViewerLabel;

	@FXML
	private ProgressBar mediaViewerProgress;

	@FXML
	private JFXTabPane modesTabPane;

	@FXML
	private Tab normalModeTab;

	@FXML
	private BorderPane mainBorder;

	@FXML
	private StackPane centerStackPane;

	@FXML
	private HBox alphabetBarBox;

	@FXML
	private HBox searchBarHBox;

	@FXML
	private JFXCheckBox instantSearch;

	@FXML
	private StackPane searchFieldStackPane;

	@FXML
	private JFXButton normalSearchButton;

	@FXML
	private HBox navigationHBox;

	@FXML
	private Button previous;

	@FXML
	private TextField pageField;

	@FXML
	private Button goToPage;

	@FXML
	private Button next;

	@FXML
	private Button settings;

	@FXML
	private MenuButton toolsMenuButton;

	@FXML
	private ContextMenu toolsContextMenu;

	@FXML
	private MenuItem importFolder;

	@FXML
	private MenuItem importFiles;

	@FXML
	private MenuItem exportFiles;

	@FXML
	private MenuItem clearAll;

	@FXML
	private Tab filtersModeTab;

	@FXML
	private Tab foldersModeTab;

	@FXML
	private VBox loadingVBox;

	@FXML
	private Label descriptionLabel;

	@FXML
	private ProgressBar loadingProgressBar;

	@FXML
	private Button cancelButton;

	@FXML
	private TextArea descriptionArea;

	@FXML
	private VBox reloadVBox;

	@FXML
	private Button reloadPlayListButton;

	// ----------------------------------------------------------

	private final Genre genre;

	/**
	 * The name of the database table (eg. @see ActionTool.returnRandomTableName())
	 */
	private final String dataBaseTableName;

	/** The name of the SmartController . */
	private String controllerName;

	/** Total items in database table . */
	private IntegerProperty totalInDataBase = new SimpleIntegerProperty(0);

	/**
	 * The last focus owner of the Scene , it is used by the pageField TextField
	 */
	private Node focusOwner;

	/** The table viewer. */
	private final MediaTableViewer normal_mode_mediaTableViewer;

	/** This list keeps all the Media Items from the TableViewer */
	private final ObservableList<Media> itemsObservableList = FXCollections.observableArrayList();

	/** The current page inside the TableViewer */
	private IntegerProperty currentPage = new SimpleIntegerProperty(0);

	/** Maximum items allowed per page. */
	public static final int DEFAULT_MAXIMUM_PER_PAGE = 50;
	private int maximumPerPage = DEFAULT_MAXIMUM_PER_PAGE;

	private final AlphabetBar alphabetBar = new AlphabetBar(this, Orientation.VERTICAL);

	/**
	 * This Viewer allows SmartController to display boxes with Media Album Images
	 */
	private final Viewer mediaViewer = new Viewer(this);

	// ---------Services--------------------------

	/** This Service is providing search functionality for the Playlist */
	private final SmartControllerSearcher searchService;

	/** This Service is used to reload/update the Playlist */
	private final LoadService loadService;

	/** This Service is used to insert Media into the Playlist */
	private final InputService inputService;

	/** This service is used to export files from Playlist */
	private final FilesExportService filesExportService;

	/** CopyOrMoveService */
	private final MediaViewerService mediaViewerService;

	// ---------Security---------------------------

	public enum WorkOnProgress {
		NONE, INSERTING_FILES, DELETE_FILES, RENAMING_LIBRARY, UPDATING_PLAYLIST, SEARCHING_FILES, EXPORTING_FILES;
	}

	public volatile WorkOnProgress workOnProgress = WorkOnProgress.NONE;

	// --------------------------------------------------

	private final SmartControllerFoldersMode foldersMode = new SmartControllerFoldersMode(this);

	private final SmartControllerFiltersMode filtersMode = new SmartControllerFiltersMode(this);

	// --------------------------------------------------

	/**
	 * The Vertical ScrollBar position of SmartController TableViewer without the
	 * search activated
	 */
	private double verticalScrollValueWithoutSearch = -1;

	/**
	 * The Vertical ScrollBar position of SmartController TableViewer when the the
	 * search activated
	 */
	private double verticalScrollValueWithSearch = -1;

	// --------------------------------------------------

	// private String previousCancelText = "";

	// ------------------------------------------------------------------------------------------------------------------------

	/**
	 * Instantiates a new smart controller.
	 *
	 * @param genre             .. @see Genre
	 * @param controllerName    The name of the SmartController
	 * @param dataBaseTableName The name of the database table <br>
	 *                          ..@see ActionTool.returnRandomTableName()
	 */
	public SmartController(Genre genre, String controllerName, String dataBaseTableName) {
		this.genre = genre;
		this.controllerName = controllerName;
		this.dataBaseTableName = dataBaseTableName;

		// Initialise
		normal_mode_mediaTableViewer = new MediaTableViewer(this, SmartControllerMode.MEDIA);
		searchService = new SmartControllerSearcher(this);
		loadService = new LoadService(this);
		inputService = new InputService(this);
		filesExportService = new FilesExportService(this);
		mediaViewerService = new MediaViewerService(this);

		// --------------------------------FXMLLoader---------------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "SmartController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}

	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Called as soon as FXML file has been loaded
	 */
	@FXML
	private void initialize() {

		// --alphabetBarBox
		// alphabetBarBox.getChildren().add(alphabetBar);
		this.mainBorder.setLeft(alphabetBar);

		// ------ tableViewer
		centerStackPane.getChildren().add(normal_mode_mediaTableViewer);
		normal_mode_mediaTableViewer.toBack();

		// ------ progress indicator
		loadingProgressBar.setVisible(true);
		// indicator.visibleProperty().bind(region.visibleProperty())
		loadingVBox.setVisible(false);

		// ------ cancel
		// cancelButton.hoverProperty().addListener((observable , oldValue , newValue)
		// -> cancelButton.setText(cancelButton.isHover() ? "cancel" :
		// previousCancelText));
		// cancelButton.textProperty().addListener((observable , oldValue , newValue) ->
		// {
		// if (!"cancel".equals(cancelButton.getText())) {
		// previousCancelText = cancelButton.getText();
		//
		// //Change it if it is hovered
		// if (cancelButton.isHover())
		// cancelButton.setText("cancel");
		// }
		// });
		// cancel.visibleProperty().bind(region.visibleProperty())
		cancelButton.setVisible(true);
		cancelButton.setDisable(true);

		// searchFieldStackPane
		searchFieldStackPane.getChildren().add(0, searchService);

		// normalSearchButton
		normalSearchButton.visibleProperty().bind(alphabetBar.letterPressedProperty());
		normalSearchButton.setOnAction(a -> {
			alphabetBar.setLetterPressed(false);
			// Do a reSearch
			getSearchService().reSearch();
		});

		// ------navigationHBox
		// navigationHBox.disableProperty().bind(this.totalInDataBase.isEqualTo(0))

		// ------ previous
		// previous.opacityProperty()
		// .bind(Bindings.when(previous.hoverProperty().or(next.hoverProperty())).then(1.0).otherwise(0.5))

		// previous.disableProperty().bind(next.disabledProperty())
		previous.disableProperty().bind(currentPage.isEqualTo(0));
		previous.setOnAction(a -> goPrevious());

		// ------- next
		// next.opacityProperty()
		// .bind(Bindings.when(next.hoverProperty().or(previous.hoverProperty())).then(1.0).otherwise(0.5))
		next.setDisable(true);
		next.setOnAction(a -> goNext());

		// Handler
		EventHandler<ActionEvent> handler = ac -> {
			if (!pageField.getText().isEmpty() && !loadService.isRunning() && !searchService.getService().isRunning()
					&& totalInDataBase.get() != 0) {
				int listNumber = Integer.parseInt(pageField.getText());
				if (listNumber <= getMaximumList()) {
					currentPage.set(listNumber);
					loadService.startService(false, true, false);
				} else {
					pageField.setText(Integer.toString(listNumber));
					pageField.selectEnd();
				}
			}
		};

		// -----goToPage
		goToPage.setOnAction(handler);
		// goToPage.disableProperty().bind(currentPage.isEqualTo(Integer.valueOf(pageField.getText())))

		// -------- pageField
		// pageField.opacityProperty()
		// .bind(Bindings.when(pageField.hoverProperty().or(next.hoverProperty()).or(previous.hoverProperty()))
		// .then(1.0).otherwise(0.03))

		// pageField.disableProperty().bind(next.disabledProperty())
		pageField.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!newValue.matches("\\d"))
				pageField.setText(newValue.replaceAll("\\D", ""));

			if (!pageField.getText().isEmpty()) {
				// System.out.println("Setting")
				int maximumPage = getMaximumList();
				// System.out.println("maximum Page:"+maximumPage)
				if (Integer.parseInt(pageField.getText()) > maximumPage)
					Platform.runLater(() -> {
						pageField.setText(Integer.toString(maximumPage));
						pageField.selectEnd();
					});

			}
		});

		pageField.setOnAction(handler);
		pageField.setOnScroll(scroll -> { // SCROLL

			// Calculate the Value
			int current = Integer.parseInt(pageField.getText());
			if (scroll.getDeltaY() > 0 && current < getMaximumList())
				++current;
			else if (scroll.getDeltaY() < 0 && current >= 1)
				--current;

			// Update pageField
			pageField.setText(String.valueOf(current));
			pageField.selectEnd();
			pageField.deselect();

		});

		// When the PageField is being hovered
		pageField.hoverProperty().addListener(l -> {
			if (!pageField.isHover())
				focusOwner.requestFocus();
			else {
				focusOwner = Main.window.getScene().getFocusOwner();
				pageField.requestFocus();
				pageField.selectEnd();
			}
		});

		// settings
		settings.setOnAction(a -> {
			Main.settingsWindow.getPlayListsSettingsController().getInnerTabPane().getSelectionModel().select(0);
			Main.settingsWindow.showWindow(SettingsTab.PLAYLISTS);
		});

		// importFolder
		importFolder.setOnAction(a -> {
			File file = Main.specialChooser.selectFolder(Main.window);
			if (file != null)
				inputService.start(Arrays.asList(file));
		});

		// importFiles
		importFiles.setOnAction(a -> {
			List<File> list = Main.specialChooser.prepareToImportSongFiles(Main.window);
			if (list != null && !list.isEmpty())
				inputService.start(list);
		});

		// exportFiles
		exportFiles.setOnAction(a -> Main.exportWindow.show(this));

		// Export
		// ...

		// clearAll
		clearAll.setOnAction(ac -> {
			if (AlertTool.doQuestion(null,
					"You want to remove all the Files from ->" + this
							+ "\n\nThis of course doesn't mean that they will be deleted from your computer",
					null, Main.window))
				clearDataBaseTable();
		});

		// == toolsMenuButton
		toolsMenuButton.setOnMouseReleased(m -> {
			Bounds bounds = toolsMenuButton.localToScreen(toolsMenuButton.getBoundsInLocal());
			toolsContextMenu.show(toolsMenuButton, bounds.getMaxX(), bounds.getMinY());
		});

		// -- refreshButton
		// refreshButton.setOnAction(e -> loadService.startService(false, true, false));

		// ---------------------Check the genre--------------------
		if (genre == Genre.SEARCHWINDOW) {
			navigationHBox.setVisible(false);
			toolsMenuButton.setVisible(false);
			navigationHBox.setManaged(false);
			toolsMenuButton.setManaged(false);
		}

		if (genre == Genre.EMOTIONSMEDIA) {
			importFolder.setVisible(false);
			importFiles.setVisible(false);
		}

		// ------------------------SMART CONTROLLER MODES-----------------
		foldersModeTab.setContent(foldersMode);
		filtersModeTab.setContent(filtersMode);

		// --Change Listener for Modes Tab Pane
		normalModeTab.setOnSelectionChanged(l -> {
			if (normalModeTab.isSelected()) {
				System.out.println("Normal Mode selected");
				filtersMode.getService().cancel();
				foldersMode.getService().cancel();
				filtersMode.getService().cancel();

				filtersModeSelected = false;
			}
		});
		foldersModeTab.setOnSelectionChanged(l -> {
			if (foldersModeTab.isSelected()) {
				System.out.println("Folders Mode selected");
				filtersMode.getService().cancel();
				foldersMode.recreateTree();
				filtersMode.getService().cancel();

				filtersModeSelected = false;
			}
		});
		filtersModeTab.setOnSelectionChanged(l -> {
			if (filtersModeTab.isSelected()) {
				System.out.println("Filters Mode selected");
				foldersMode.getService().cancel();
				filtersMode.regenerate();
				filtersModeSelected = true;
			} else {
				filtersModeSelected = false;
			}
		});

		// reloadVBox
		reloadVBox.setVisible(false);

		// reloadPlayListButton
		reloadPlayListButton.setOnAction(a -> {
			if (isFree(true)) {
				loadService.startService(false, true, false);
				reloadVBox.setVisible(false);
			}
		});
		// Automatically reload the playlist
		reloadPlayListButton.hoverProperty().addListener(l -> {
			if (isFree(true)) {
				loadService.startService(false, true, false);
				reloadVBox.setVisible(false);
			}
		});

		// viewerBorderPane
		((StackPane) viewerBorderPane.getCenter()).getChildren().add(0, mediaViewer);
		viewerNext.setOnAction(a -> mediaViewer.next());
		viewerPrevious.setOnAction(a -> mediaViewer.previous());

		// mediaViewerVBox
		mediaViewerVBox.visibleProperty().bind(mediaViewerService.runningProperty());
		mediaViewerProgress.progressProperty().bind(mediaViewerService.progressProperty());
		mediaViewerLabel.textProperty().bind(mediaViewerService.messageProperty());
	}

	public volatile boolean filtersModeSelected = false;

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							METHODS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Prepares the delete operation when more than one Media files will be deleted.
	 *
	 * @param permanent  <br>
	 *                   true->storage medium + (play list)/library<br>
	 *                   false->only from (play list)/library
	 */
	public void prepareDelete(boolean permanent) {
		int previousTotal = getTotalInDataBase();

		// Remove selected items
		removeSelected(permanent);

		// Update
		if (previousTotal != getTotalInDataBase())
			loadService.startService(true, true, true);

	}

	/**
	 * Removes the selected songs.
	 *
	 * @param permanent <br>
	 *                  true->storage medium + (play list)/library false->only from
	 *                  (play list)/library<br>
	 */
	private void removeSelected(boolean permanent) {

		// Free? && How many items are selected?+Question
		if (!isFree(true))
			return;

		List<Boolean> answers = Main.mediaDeleteWindow.doDeleteQuestion(permanent,
				normal_mode_mediaTableViewer.getSelectedCount() != 1
						? Integer.toString(normal_mode_mediaTableViewer.getSelectedCount())
						: normal_mode_mediaTableViewer.getSelectionModel().getSelectedItem().getFileName(),
				normal_mode_mediaTableViewer.getSelectedCount(), Main.window);

		// Check if the user is sure he want's to go on delete action
		if (!answers.get(0))
			return;
		// Check if the delete will be finally permanent or not
		boolean permanent1 = answers.get(1);

		// Remove selected items
		if (genre == Genre.SEARCHWINDOW) {

			if (modesTabPane.getSelectionModel().getSelectedItem() == filtersModeTab) {

				// Call the delete for each selected item
				filtersMode.getMediaTableViewer().getTableView().getSelectionModel().getSelectedItems().iterator()
						.forEachRemaining(r -> r.delete(permanent1, false, false, this, null));

				// Update artistsMode
				filtersMode.refreshTableView();
			} else
				// Call the delete for each selected item
				normal_mode_mediaTableViewer.getSelectionModel().getSelectedItems().iterator()
						.forEachRemaining(r -> r.delete(permanent1, false, false, this, null));

		} else
			try (PreparedStatement preparedDelete = Main.dbManager.getConnection()
					.prepareStatement("DELETE FROM '" + dataBaseTableName + "' WHERE PATH=?")) {

				if (modesTabPane.getSelectionModel().getSelectedItem() == filtersModeTab) {

					// Call the delete for each selected item
					filtersMode.getMediaTableViewer().getTableView().getSelectionModel().getSelectedItems().iterator()
							.forEachRemaining(r -> r.delete(permanent1, false, false, this, preparedDelete));

					// Update artistsMode
					filtersMode.refreshTableView();
				} else
					// Call the delete for each selected item
					normal_mode_mediaTableViewer.getSelectionModel().getSelectedItems().iterator()
							.forEachRemaining(r -> r.delete(permanent1, false, false, this, preparedDelete));

			} catch (Exception ex) {
				Main.logger.log(Level.WARNING, "", ex);
			}

	}

	/**
	 * Clears all the items from this library *.
	 */
	private void clearDataBaseTable() {
		if (!isFree(true))
			return;

		// Security Value
		workOnProgress = WorkOnProgress.DELETE_FILES;

		// Controller
		getIndicator().setProgress(-1);
		descriptionLabel.setText("Clearing...");
		loadingVBox.setVisible(true);

		// New Thread
		new Thread(() -> {
			try {
				Main.dbManager.getConnection().createStatement()
						.executeUpdate("DELETE FROM '" + dataBaseTableName + "'");
				Main.dbManager.commit();

				// Check if it is Emotion PlayList so clear the Internal List also babeeee!
				if (genre == Genre.EMOTIONSMEDIA)
					switch (getName()) {

					case "HatedMediaPlayList":
						Main.emotionListsController.hatedMediaList.getSet().clear();
						break;
					case "DislikedMediaPlayList":
						Main.emotionListsController.dislikedMediaList.getSet().clear();
						break;
					case "LikedMediaPlayList":
						Main.emotionListsController.likedMediaList.getSet().clear();
						break;
					case "LovedMediaPlayList":
						Main.emotionListsController.lovedMediaList.getSet().clear();
						break;
					}

				// Make the Region Disappear in the fog of hell ououou
				Platform.runLater(() -> {
					loadingVBox.setVisible(false);
					getCancelButton().setText("Cancel");
				});

			} catch (Exception ex) {
				Main.logger.log(Level.WARNING, "", ex);
			} finally {
				// Security Value
				workOnProgress = WorkOnProgress.NONE;
			}
		}).start();

		simpleClear();

		// Library?
		// if (genre == Genre.LIBRARYMEDIA)
		// Main.libraryMode.updateLibraryTotalLabel(controllerName);
	}

	public static final String style1 = "-fx-font-weight:bold; -fx-font-size:13; -fx-fill:#FF9000;";
	public static final String style2 = "-fx-font-weight:bold; -fx-font-size:13;  -fx-fill:white;";
	public static final String style3 = "-fx-font-weight:bold; -fx-font-size:13;  -fx-fill:#00BBFF;";
	public static final String style4 = "-fx-font-weight:bold; -fx-font-size:13;  -fx-fill:white;";

	/**
	 * Updates the label of the smart controller. [[SuppressWarningsSpartan]]
	 */
	public void updateLabel() {

		String total = "Total : ";
		String _total = InfoTool.getNumberWithDots(totalInDataBase.get());

		String selected = "Selected : ";
		String _selected = String.valueOf(normal_mode_mediaTableViewer.getSelectedCount());

		String maxPerPage = "MaxPerPage : ";
		String _maxPerPage = String.valueOf(maximumPerPage);

		// Search is Activated?
		if (searchService.isActive() || genre == Genre.SEARCHWINDOW) {

			// Go
			String found = "Found : ";
			String _found = String.valueOf(itemsObservableList.size());

			// Clear the text area
			normal_mode_mediaTableViewer.getDetailCssTextArea().clear();

			// Now set the Text
			appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), found, _found, true, style3);
			appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), total, _total, true, style1);
			appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), selected, _selected, true, style1);
			appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), maxPerPage, _maxPerPage, false,
					style1);

		} else {

			// Check if we are on artists mode
			if (modesTabPane.getSelectionModel().getSelectedItem() == filtersModeTab) {

				// Keep a reference
				MediaTableViewer artists_mode_MediaTableViewer = filtersMode.getMediaTableViewer();

				// Go
				_total = InfoTool.getNumberWithDots(artists_mode_MediaTableViewer.getTableView().getItems().size());
				_selected = String.valueOf(
						artists_mode_MediaTableViewer.getTableView().getSelectionModel().getSelectedIndices().size());

				// Clear the text area
				artists_mode_MediaTableViewer.getDetailCssTextArea().clear();

				// Now set the Text
				appendToDetails(artists_mode_MediaTableViewer.getDetailCssTextArea(), total, _total, true, style4);
				appendToDetails(artists_mode_MediaTableViewer.getDetailCssTextArea(), selected, _selected, false,
						style1);

			} else if (modesTabPane.getSelectionModel().getSelectedItem() == normalModeTab) {

				// Go
				String showing = "Showing : ";
				String _showing = InfoTool.getNumberWithDots(maximumPerPage * currentPage.get()) + " ... "
						+ InfoTool.getNumberWithDots(maximumPerPage * currentPage.get() + itemsObservableList.size());

				String maximumPage = "MaximumPage : ";
				String _maximumPage = InfoTool.getNumberWithDots(getMaximumList());

				// Clear the text area
				normal_mode_mediaTableViewer.getDetailCssTextArea().clear();

				// Now set the Text
				appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), total, _total, true, style3);
				appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), selected, _selected, true, style1);
				appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), showing, _showing, true, style1);
				appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), maxPerPage, _maxPerPage, true,
						style1);
				appendToDetails(normal_mode_mediaTableViewer.getDetailCssTextArea(), maximumPage, _maximumPage, false,
						style1);

			}

		}

		pageField.setText(Integer.toString(currentPage.get()));
	}

	/**
	 * This method is used from updateLabel() method to append Text to
	 * detailsCssTextArea
	 * 
	 * @param text1
	 * @param text2
	 * @param appendComma
	 */
	private void appendToDetails(InlineCssTextArea inlineCssTextArea, String text1, String text2, boolean appendComma,
			String style1) {

		inlineCssTextArea.appendText(text1);
		inlineCssTextArea.setStyle(inlineCssTextArea.getLength() - text1.length(), inlineCssTextArea.getLength() - 1,
				style1);

		inlineCssTextArea.appendText(text2 + " " + (!appendComma ? "" : ", "));
		inlineCssTextArea.setStyle(inlineCssTextArea.getLength() - text2.length() - (appendComma ? 3 : 1),
				inlineCssTextArea.getLength() - 1, style2);
	}

	/**
	 * Checks if any updates are on progress in the controller.
	 *
	 * @param showMessage the show message
	 * @return true->if yes<br>
	 *         false->if not
	 */
	public boolean isFree(boolean showMessage) {
		boolean isFree = (workOnProgress == WorkOnProgress.NONE);

		// Check if any work is already in progress
		if (!isFree && showMessage)
			showMessage(workOnProgress.toString());

		return isFree;
	}

	/**
	 * Show message.
	 *
	 * @param reason the reason
	 */
	private void showMessage(String reason) {
		AlertTool.showNotification("Message",
				"[" + reason + "] is working on:\n " + toString() + "\n\t retry as soon as it finish.",
				Duration.millis(2000), NotificationType.INFORMATION);
	}

	/**
	 * Unbind.
	 */
	public void unbind() {
		loadingVBox.visibleProperty().unbind();
		loadingVBox.setVisible(false);
		loadingProgressBar.progressProperty().unbind();
	}

	/**
	 * Goes on the Previous List.
	 */
	public void goPrevious() {
		if (SmartController.this.genre != Genre.SEARCHWINDOW && isFree(false) && !searchService.isActive()
				&& totalInDataBase.get() != 0 && currentPage.get() > 0) {
			currentPage.set(currentPage.get() - 1);
			loadService.startService(false, true, false);
		}
	}

	/**
	 * Goes on the Next List.
	 */
	public void goNext() {
		if (SmartController.this.genre != Genre.SEARCHWINDOW && isFree(false) && !searchService.isActive()
				&& totalInDataBase.get() != 0 && currentPage.get() < getMaximumList()) {
			currentPage.set(currentPage.get() + 1);
			loadService.startService(false, true, false);
		}
	}

	/**
	 * Updates the List.
	 */
	public void updateList() {

		if (totalInDataBase.get() != 0)
			next.setDisable(!(currentPage.isEqualTo(0).or(currentPage.lessThan(getMaximumList())).get()
					&& getMaximumList() != 0));
		else {
			next.setDisable(true);
			currentPage.set(0);
		}

		// update the label
		updateLabel();
		// refresh the tableViewer
		// normal_mode_mediaTableViewer.getTableView().refresh();
		// if (!normal_mode_mediaTableViewer.getTableView().getSortOrder().isEmpty())
		// normal_mode_mediaTableViewer.getTableView().sort();

		// Call MediaViewerService
		noAlbumViewsLabel.setVisible(getTotalInDataBase() == 0);
		if (getTotalInDataBase() != 0)
			mediaViewerService.startService();
	}

	/**
	 * Clear all the items from list.
	 */
	private void simpleClear() {
		itemsObservableList.clear();
		totalInDataBase.set(0);
		updateList();
	}

	/**
	 * Calculates the total entries in the database table [it MUST be called from
	 * external thread cause it may lag the application ]
	 */
	public synchronized void calculateTotalEntries() {
		// calculate the total entries
		if (getTotalInDataBase() == 0)
			try (ResultSet s = Main.dbManager.getConnection().createStatement()
					.executeQuery("SELECT COUNT(PATH) FROM '" + getDataBaseTableName() + "';")) {

				// Total items
				final int total = s.getInt(1);

				// Update the total
				final CountDownLatch latch = new CountDownLatch(1);
				Platform.runLater(() -> {
					setTotalInDataBase(total);

					// Count Down
					latch.countDown();
				});

				// Wait
				latch.await();
			} catch (SQLException | InterruptedException ex) {
				ex.printStackTrace();
			}
	}

	/**
	 * Checks if the Current SmartController database contains the file
	 * 
	 * @param absoluteFilePath The AbsoluteFilePath
	 * @return True if contains the file path or false if not
	 */
	public boolean containsFile(String absoluteFilePath) {
		if (genre == Genre.SEARCHWINDOW)
			return false;
		else {
			try (PreparedStatement pStatement = Main.dbManager.getConnection()
					.prepareStatement("SELECT COUNT(PATH) FROM '" + getDataBaseTableName() + "' WHERE PATH=?")) {

				// Total items
				pStatement.setString(1, absoluteFilePath);
				return pStatement.executeQuery().getInt(1) > 0;

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

			return false;
		}

	}

	@Override
	public String toString() {
		return "PlayList: <" + controllerName + ">";
	}

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							PROPERTIES
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Total in data base property.
	 *
	 * @return the integer property
	 */
	public IntegerProperty totalInDataBaseProperty() {
		return totalInDataBase;
	}

	/**
	 * @return the currentPage
	 */
	public IntegerProperty currentPageProperty() {
		return currentPage;
	}

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							SETTERS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Changes the MaximumPerPage
	 * 
	 * @param newMaximumPerPage
	 * @param updateSmartController If true the loadService will start (Memory
	 *                              consuming ;( ) use with great care
	 */
	public void setNewMaximumPerPage(int newMaximumPerPage, boolean updateSmartController) {
		if (maximumPerPage == newMaximumPerPage)
			return;

		// We need to know how much percent the newMaximumPerPage is to the
		// oldMaximumPerPage

		// ------------------------ I FOLLOW THE BELOW PRINCIPAL
		// -------------------------

		// [If you want to know what percent A is of B, you simple divide A by B,
		// then take that number and move the decimal place two spaces to the
		// right.That's your percentage!]
		// so...........

		double formula = (double) maximumPerPage / newMaximumPerPage;
		currentPage.set((int) (currentPage.get() * formula));
		// currentPage.set( ( maximumPerPage == 50 ) ? currentPage.get() / 2 :
		// currentPage.get() * 2 + ( currentPage.get() % 2 == 0 ? 0 : 1 ));
		maximumPerPage = newMaximumPerPage;
		reloadVBox.setVisible(true);
		// if (updateSmartController && isFree(false))
		// loadService.startService(false, true, false);
	}

	/**
	 * Sets the name.
	 *
	 * @param newName the new name
	 */
	public void setName(String newName) {
		controllerName = newName;
	}

	/**
	 * Sets the total in data base.
	 *
	 * @param totalInDataBase the new total in data base
	 */
	public void setTotalInDataBase(int totalInDataBase) {
		this.totalInDataBase.set(totalInDataBase);
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(IntegerProperty currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @param verticalScrollValueWithoutSearch the verticalScrollValueWithoutSearch
	 *                                         to set
	 */
	public void setVerticalScrollValueWithoutSearch(double verticalScrollValueWithoutSearch) {
		this.verticalScrollValueWithoutSearch = verticalScrollValueWithoutSearch;
	}

	/**
	 * @param verticalScrollValueWithSearch the verticalScrollValueWithSearch to set
	 */
	public void setVerticalScrollValueWithSearch(double verticalScrollValueWithSearch) {
		this.verticalScrollValueWithSearch = verticalScrollValueWithSearch;
	}

	/*-----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 * 							GETTERS
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 * 
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Returns Marked Songs.
	 *
	 * @return the selected items
	 */
	public ObservableList<Media> getSelectedItems() {
		return normal_mode_mediaTableViewer.getSelectionModel().getSelectedItems();
	}

	/**
	 * Gets the indicator.
	 *
	 * @return the indicator
	 */
	public ProgressBar getIndicator() {
		return loadingProgressBar;
	}

	/**
	 * Gets the cancel button.
	 *
	 * @return the cancel button
	 */
	public Button getCancelButton() {
		return cancelButton;
	}

	/**
	 * Gets the next button.
	 *
	 * @return the next button
	 */
	public HBox getNavigationHBox() {
		return navigationHBox;
	}

	/**
	 * Gets the next button.
	 *
	 * @return the next button
	 */
	public Button getNextButton() {
		return next;
	}

	/**
	 * Gets the previous button.
	 *
	 * @return the previous button
	 */
	public Button getPreviousButton() {
		return previous;
	}

	/**
	 * Gets the data base table name.
	 *
	 * @return the data base table name
	 */
	public String getDataBaseTableName() {
		return dataBaseTableName;
	}

	/**
	 * Returns the name of the smart controller
	 * 
	 * @return The name of the smartController
	 */
	public String getName() {
		return controllerName;
	}

	/**
	 * Return the number of the final List counting from <b>firstList->0
	 * SecondList->1 ....</b>
	 *
	 * @return the int
	 */
	public int getMaximumList() {
		return totalInDataBase.get() == 0 ? 0
				: (totalInDataBase.get() / maximumPerPage) + ((totalInDataBase.get() % maximumPerPage == 0) ? -1 : 0);
	}

	/**
	 * @return The Vertical ScrollBar of TableViewer
	 */
	public Optional<ScrollBar> getVerticalScrollBar() {

		return Optional
				.ofNullable((ScrollBar) getNormalModeMediaTableViewer().getTableView().lookup(".scroll-bar:vertical"));
	}

	/**
	 * @return the centerStackPane
	 */
	public StackPane getCenterStackPane() {
		return centerStackPane;
	}

	/**
	 * @return searchService
	 */
	public SmartControllerSearcher getSearchService() {
		return searchService;
	}

	/**
	 * @return loadService
	 */
	public LoadService getLoadService() {
		return loadService;
	}

	/**
	 * @return inputService
	 */
	public InputService getInputService() {
		return inputService;
	}

	/**
	 * @return filesExportService
	 */
	public FilesExportService getFilesExportService() {
		return filesExportService;
	}

	/**
	 * @return the maximumPerPage
	 */
	public int getMaximumPerPage() {
		return maximumPerPage;
	}

	/**
	 * @return the indicatorVBox
	 */
	public VBox getIndicatorVBox() {
		return loadingVBox;
	}

	/**
	 * @return the genre
	 */
	public Genre getGenre() {
		return genre;
	}

	/**
	 * @return the tableViewer
	 */
	public MediaTableViewer getNormalModeMediaTableViewer() {
		return normal_mode_mediaTableViewer;
	}

	/**
	 * @return the itemsObservableList
	 */
	public ObservableList<Media> getItemsObservableList() {
		return itemsObservableList;
	}

	/**
	 * @return the informationTextArea
	 */
	public TextArea getDescriptionArea() {
		return descriptionArea;
	}

	/**
	 * Gets the total in data base.
	 *
	 * @return the total in data base
	 */
	public int getTotalInDataBase() {
		return totalInDataBase.get();
	}

	/**
	 * @return the verticalScrollValueWithoutSearch
	 */
	public double getVerticalScrollValueWithoutSearch() {
		return verticalScrollValueWithoutSearch;
	}

	/**
	 * @return the verticalScrollValueWithSearch
	 */
	public double getVerticalScrollValueWithSearch() {
		return verticalScrollValueWithSearch;
	}

	/**
	 * @return the toolsContextMenu
	 */
	public ContextMenu getToolsContextMenu() {
		return toolsContextMenu;
	}

	/**
	 * @return the instantSearch
	 */
	public JFXCheckBox getInstantSearch() {
		return instantSearch;
	}

	/**
	 * @return the reloadVBox
	 */
	public VBox getReloadVBox() {
		return reloadVBox;
	}

	/**
	 * @return the modesTabPane
	 */
	public JFXTabPane getModesTabPane() {
		return modesTabPane;
	}

	/**
	 * @return the filtersModeTab
	 */
	public Tab getFiltersModeTab() {
		return filtersModeTab;
	}

	/**
	 * @return the normalModeTab
	 */
	public Tab getNormalModeTab() {
		return normalModeTab;
	}

	/**
	 * @return the filtersMode
	 */
	public SmartControllerFiltersMode getFiltersMode() {
		return filtersMode;
	}

	/**
	 * @return the foldersMode
	 */
	public SmartControllerFoldersMode getFoldersMode() {
		return foldersMode;
	}

	/**
	 * @return the alphabetBar
	 */
	public AlphabetBar getAlphabetBar() {
		return alphabetBar;
	}

	/**
	 * @return the searchFieldStackPane
	 */
	public StackPane getSearchFieldStackPane() {
		return searchFieldStackPane;
	}

	/**
	 * @return the descriptionLabel
	 */
	public Label getDescriptionLabel() {
		return descriptionLabel;
	}

	/**
	 * @return the mainBorder
	 */
	public BorderPane getMainBorder() {
		return mainBorder;
	}

	/**
	 * @return the viewer
	 */
	public Viewer getMediaViewer() {
		return mediaViewer;
	}

	/**
	 * @return the mediaViewerService
	 */
	public MediaViewerService getMediaViewerService() {
		return mediaViewerService;
	}

	/**
	 * @return the viewerSplitPane
	 */
	public SplitPane getViewerSplitPane() {
		return viewerSplitPane;
	}

	/**
	 * @return the noAlbumViewsLabel
	 */
	public Label getNoAlbumViewsLabel() {
		return noAlbumViewsLabel;
	}

	/**
	 * @return the foldersModeTab
	 */
	public Tab getFoldersModeTab() {
		return foldersModeTab;
	}

	public Tab getSelectedModeTab() {
		return modesTabPane.getSelectionModel().getSelectedItem();
	}

}
