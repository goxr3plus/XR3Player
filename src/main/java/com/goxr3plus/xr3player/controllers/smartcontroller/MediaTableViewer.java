package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.fxmisc.richtext.InlineCssTextArea;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.custom.StarBadge;
import com.goxr3plus.xr3player.controllers.windows.EmotionsWindow.Emotion;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.services.smartcontroller.MediaTagsService;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.DragViewTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Representing the data of SmartController.
 *
 * @author GOXR3PLUS
 */
public class MediaTableViewer extends StackPane {

	@FXML
	private TableView<Media> tableView;

	@FXML
	private TableColumn<Media, Integer> number;

	@FXML
	private TableColumn<Media, StackedFontIcon> artwork;

	/** The has been played. */
	@FXML
	private TableColumn<Media, Integer> playStatus;

	/** The media type. */
	@FXML
	private TableColumn<Media, Integer> mediaType;

	/** The title. */
	@FXML
	private TableColumn<Media, String> title;

	@FXML
	private TableColumn<Media, HBox> getInfoBuy;

	@FXML
	private TableColumn<Media, Integer> emotions;

	/** The duration. */
	@FXML
	private TableColumn<Media, String> duration;

	/** The times played. */
	@FXML
	private TableColumn<Media, Integer> timesPlayed;

	/** The stars. */
	@FXML
	private TableColumn<Media, Double> stars;

	/** The hour imported. */
	@FXML
	private TableColumn<Media, String> hourImported;

	/** The date imported. */
	@FXML
	private TableColumn<Media, String> dateImported;

	/** The date that the file was created */
	@FXML
	private TableColumn<Media, String> dateFileCreated;

	/** The date that the file was last modified */
	@FXML
	private TableColumn<Media, String> dateFileModified;

	@FXML
	private TableColumn<Media, String> artist;

	@FXML
	private TableColumn<Media, String> mood;

	@FXML
	private TableColumn<Media, String> album;

	@FXML
	private TableColumn<Media, String> composer;

	@FXML
	private TableColumn<Media, String> comment;

	/** The genre. */
	@FXML
	private TableColumn<Media, String> genre;

	@FXML
	private TableColumn<Media, String> copyright;

	@FXML
	private TableColumn<Media, String> track;

	@FXML
	private TableColumn<Media, String> track_total;

	@FXML
	private TableColumn<Media, String> remixer;

	@FXML
	private TableColumn<Media, String> djMixer;

	@FXML
	private TableColumn<Media, String> rating;

	@FXML
	private TableColumn<Media, String> producer;

	@FXML
	private TableColumn<Media, String> performer;

	@FXML
	private TableColumn<Media, String> orchestra;

	@FXML
	private TableColumn<Media, String> country;

	@FXML
	private TableColumn<Media, String> lyricist;

	@FXML
	private TableColumn<Media, String> conductor;

	@FXML
	private TableColumn<Media, String> amazonID;

	@FXML
	private TableColumn<Media, String> encoder;

	/** The bpm. */
	@FXML
	private TableColumn<Media, Integer> bpm;

	/** The bit rate. */
	@FXML
	private TableColumn<Media, Integer> bitRate;

	/** The drive. */
	@FXML
	private TableColumn<Media, String> drive;

	/** The file path. */
	@FXML
	private TableColumn<Media, String> filePath;

	/** The file name. */
	@FXML
	private TableColumn<Media, String> fileName;

	/** The file type. */
	@FXML
	private TableColumn<Media, String> fileType;

	/** The file size. */
	@FXML
	private TableColumn<Media, String> fileSize;

	/** The singer. */
	@FXML
	private TableColumn<Media, String> tempo;

	/** The year. */
	@FXML
	private TableColumn<Media, String> year;

	/** The key. */
	@FXML
	private TableColumn<Media, String> key;

	// ---------------------------------------------

	@FXML
	private InlineCssTextArea detailCssTextArea;

	@FXML
	private Label quickSearchTextField;

	@FXML
	private Label dragAndDropLabel;

	// -------------------------------------------------

	/** The pause transition. */
	private final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
	private final StringProperty searchWord = new SimpleStringProperty("");

	/** AllDetailsService */
	private final MediaTagsService allDetailsService = new MediaTagsService();

	private final SmartController smartController;

	private int previousSelectedCount = 0;

	private final SmartControllerMode mode;

	// Colors
	private Color lightGreen = Color.web("#ceff26");

	/**
	 * Constructor.
	 */
	public MediaTableViewer(SmartController smartController, SmartControllerMode mode) {
		this.smartController = smartController;
		this.mode = mode;

		// FXML Loader
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "MediaTableViewer.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "MediaTableViewer falied to initialize fxml..", ex);
		}

	}

	/**
	 * Called as soon as .fxml has been initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {

		// ArtWork FontIcon
		FontIcon openFolderIcon = new FontIcon("far-folder-open");
		openFolderIcon.setIconSize(30);
		openFolderIcon.setIconColor(Color.web("#ddaa33"));

		// ------------------------------TableViewer---------------------------
		if (mode == SmartControllerMode.MEDIA) {
			tableView.setItems(smartController.getItemsObservableList());

			// Add the place holder for the tableView
			Label placeHolderLabel = new Label();

			if (smartController.getGenre() == Genre.LIBRARYMEDIA) {
				placeHolderLabel.setText("Import Media ...");
				placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-cursor:hand;");
				placeHolderLabel.setOnMouseReleased(m -> smartController.getToolsContextMenu().show(placeHolderLabel,
						m.getScreenX(), m.getScreenY()));
				placeHolderLabel.setGraphic(openFolderIcon);
			} else if (smartController.getGenre() == Genre.SEARCHWINDOW) {
				placeHolderLabel.setText("Search Media from all the playlists...");
				placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; ");
			} else if (smartController.getGenre() == Genre.EMOTIONSMEDIA) {
				placeHolderLabel.setText("No Media in this emotions list ...");
				placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; ");
			}
			tableView.setPlaceholder(placeHolderLabel);
		}

		// --Allow Multiple Selection
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tableView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) l -> {

			// Hold the Current Selected Count
			int currentSelectedCount = getSelectedCount();

			// Update the Label only if the current selected count != previousSelectedCount
			if (previousSelectedCount != currentSelectedCount) {
				previousSelectedCount = currentSelectedCount;
				smartController.updateLabel();
			}

		});

		// Update the Media Information when Selected Item changes
		tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != newValue) {

				// Synchronize with Media Information
				Main.mediaInformation.updateInformation(newValue);

				// Check if selection is allowed
				if (Main.settingsWindow.getPlayListsSettingsController().getSelectMatchingMediaViewItem()
						.isSelected()) {
					String path = newValue.getFilePath();

					// Select
					smartController.getMediaViewer().getItemsObservableList().stream()
							.filter(media -> ((MediaViewer) media).getMedia().getFilePath().equals(path)).findFirst()
							.ifPresent(mediaViewer -> smartController.getMediaViewer().setCenterIndex(
									smartController.getMediaViewer().getItemsObservableList().indexOf(mediaViewer)));
				}

			}
		});

		// --Row Factory
		tableView.setRowFactory(rf -> {
			TableRow<Media> row = new TableRow<>();

			// // use EasyBind to access the valueProperty of the itemProperty
			// // of the cell:
			// row.disableProperty().bind(
			// // start at itemProperty of row
			// EasyBind.select(row.itemProperty())
			// // map to fileExistsProperty[a boolean] of item,
			// // if item non-null
			// .selectObject(Media::fileExistsProperty)
			// // map to BooleanBinding checking if false
			// .map(x -> !x.booleanValue())
			// // value to use if item was null
			// .orElse(false));

			// Mouse Listener
			row.setFocusTraversable(true);
			row.setOnMouseReleased(m -> {
				// We don't need null rows (rows without items)
				if (row.itemProperty().getValue() != null) {

					if (m.getButton() == MouseButton.SECONDARY && !row.isDisable())
						tableView.getSelectionModel().select(row.getIndex());

					// Primary
					if (m.getButton() == MouseButton.PRIMARY) {
						if (m.getClickCount() == 2)
							row.itemProperty().get().rename(row);

					} // Secondary
					else if (m.getButton() == MouseButton.SECONDARY
							&& !tableView.getSelectionModel().getSelectedItems().isEmpty())
						Main.songsContextMenu.showContextMenu(row.itemProperty().get(), smartController.getGenre(),
								m.getScreenX(), m.getScreenY(), smartController, row);
				}
			});

			return row;
		});

		// --Drag Detected
		tableView.setOnDragDetected(event -> {
			if (getSelectedCount() != 0
					&& event.getScreenY() > tableView.localToScreen(tableView.getBoundsInLocal()).getMinY() + 30) {

				/* allow copy transfer mode */
				Dragboard db = tableView.startDragAndDrop(TransferMode.COPY, TransferMode.LINK);

				/* put a string on drag board */
				ClipboardContent content = new ClipboardContent();

				// PutFiles
				content.putFiles(tableView.getSelectionModel().getSelectedItems().stream()
						.map(s -> new File(s.getFilePath())).collect(Collectors.toList()));

				// Single Drag and Drop ?
				if (content.getFiles().size() == 1)
					DragViewTool.setDragView(db, tableView.getSelectionModel().getSelectedItem());
				// Multiple Drag and Drop ?
				else {
					DragViewTool.setPlainTextDragView(db, "(" + content.getFiles().size() + ")Items");
				}

				db.setContent(content);
			}
			event.consume();
		});

		// dragAndDropLabel
		dragAndDropLabel.setVisible(false);

		// System.out.println(smartController.getGenre() + " , " +
		// SmartControllerMode.MEDIA);
		if (smartController.getGenre() == Genre.LIBRARYMEDIA && mode == SmartControllerMode.MEDIA) {

			// --Drag Over
			tableView.setOnDragOver(dragOver -> {

				// The drag must come from source other than the owner
				if (dragOver.getDragboard().hasFiles() && dragOver.getGestureSource() != tableView)
					dragAndDropLabel.setVisible(true);

			});

			dragAndDropLabel.setOnDragOver(dragOver -> {

				// The drag must come from source other than the owner
				// System.out.println(dragOver.getGestureSource());
				// if(dragOver.getGestureSource().toString().contains("MediaViewer") &&
				// dragOver.getGestureSource().)

				if (dragOver.getDragboard().hasFiles() && dragOver.getGestureSource() != tableView)
					dragOver.acceptTransferModes(TransferMode.LINK);

			});

			// --Drag Dropped
			dragAndDropLabel.setOnDragDropped(drop -> {
				// Has Files? + isFree()?
				if (drop.getDragboard().hasFiles() && smartController.isFree(true))
					smartController.getInputService().start(drop.getDragboard().getFiles());

				drop.setDropCompleted(true);
			});

			// Drag Exited
			dragAndDropLabel.setOnDragExited(drop -> dragAndDropLabel.setVisible(false));

		}

		// --------------------------Other-----------------------------------
		String center = "-fx-alignment:CENTER-LEFT;";

		// getInfoBuy
		getInfoBuy.setCellValueFactory(new PropertyValueFactory<>("getInfoBuy"));

		// emotion
		emotions.setCellValueFactory(new PropertyValueFactory<>("emotion"));
		emotions.setCellFactory(col -> new TableCell<>() {

			// Emotion Button
			Button emotionButton = new Button("");

			{

				emotionButton.getStyleClass().add("jfx-button2");
				emotionButton.setPrefSize(24, 24);
				emotionButton.setMinSize(24, 24);
				emotionButton.setMaxSize(24, 24);
				emotionButton.setStyle("-fx-cursor:hand");
				emotionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			}

			/**
			 * Update the emotion the user is feeling for this Media
			 */
			private void updateEmotion(Media media, Node node) {
				// Show the Window
				Main.emotionsWindow.show(media.getFileName(), node);// getFileName()

				// Listener
				Main.emotionsWindow.getWindow().showingProperty().addListener(new InvalidationListener() {
					/**
					 * [[SuppressWarningsSpartan]]
					 */
					@Override
					public void invalidated(Observable o) {

						// Remove the listener
						Main.emotionsWindow.getWindow().showingProperty().removeListener(this);

						// !showing?
						if (!Main.emotionsWindow.getWindow().isShowing() && Main.emotionsWindow.wasAccepted()) {

							// Add it the one of the emotions list
							new Thread(() -> Main.emotionListsController.makeEmotionDecisition(media.getFilePath(),
									Main.emotionsWindow.getEmotion())).start();

						}
					}
				});

			}

			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					int size = 24;
					Emotion emotion = Emotion.NEUTRAL;

					// Emotion Button
					emotionButton.setOnAction(a -> updateEmotion(getTableRow().getItem(), emotionButton));
					setGraphic(emotionButton);

					// set the image according to the play status
					if (item != null) {
						if (item == 1) {
							emotion = Emotion.HATE;
							size = 24;
						} else if (item == 2) {
							emotion = Emotion.DISLIKE;
							size = 20;
						} else if (item == 0) {
							emotion = Emotion.NEUTRAL;
							size = 28;
						} else if (item == 3) {
							emotion = Emotion.LIKE;
							size = 20;
						} else if (item == 4) {
							emotion = Emotion.LOVE;
							size = 20;
						}

						// Now set the graphic
						emotionButton.setGraphic(Main.emotionsWindow.getEmotionFontIcon(emotion, size));

					}

				}
			}

		});

		// stars
		stars.setCellValueFactory(new PropertyValueFactory<>("stars"));
		stars.setCellFactory(col -> new TableCell<>() {

			// Star Badge
			StarBadge starBadge = new StarBadge(0.0);

			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {

					// Check if item is null
					if (getTableRow().getItem() != null) {

						// Star Badge
						starBadge.setOnAction(a -> getTableRow().getItem().updateStars(starBadge));
						starBadge.setStars(getTableRow().getItem().getStars());

						setGraphic(starBadge);
					}
				}
			}

		});

		// number
		number.setCellValueFactory(new PropertyValueFactory<>("number"));

		// hasBeenPlayed
		playStatus.setCellValueFactory(new PropertyValueFactory<>("playStatus"));
		playStatus.setCellFactory(col -> new TableCell<>() {

			// Icon FontIcon
			// HBox flowPane = new HBox();

			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					// Clear the flowPane
					// flowPane.getChildren().clear();

					// set the image according to the play status
					if (item != null) {
						if (item == -2) // UNKNOWN
							setGraphic(null);
						else if (item == -1) { // Already played
							setGraphic(JavaFXTool.getFontIcon("fas-play-circle", lightGreen, 24));
						} else { // BEING PLAYED BY SOME PLAYERS
							//
							// TO BE FIXES - PROBLEM IF IS PLAYING IN MANY PLAYERS IT MUST SHOW MANY PLAYERS
							// NOT JUST ONE!!!
							setGraphic(JavaFXTool.getFontIcon("gmi-filter-" + (item + 1), Color.WHITE, 24));

							// String mediaPath = getTableRow().getItem().getFilePath();
							// Main.xPlayersList.getList().stream().sorted((o1 , o2) ->
							// Integer.valueOf(o1.getKey()).compareTo(o2.getKey())).forEach(controller -> {
							// String path = controller.getxPlayerModel().songPathProperty().get();
							// //Check if it matches
							// if (path != null && path.equals(mediaPath))
							// flowPane.getChildren().addAll(JavaFXTools.getFontIcon("gmi-filter-" + (
							// controller.getKey() + 1 ), Color.WHITE, 24));
							//
							// });
							// setGraphic(flowPane);
						}
					}
				}
			}

		});

		// mediaType
		mediaType.setCellValueFactory(new PropertyValueFactory<>("mediaType"));
		mediaType.setCellFactory(col -> new TableCell<>() {

			// Icon FontIcon
			FontIcon icon = new FontIcon();

			{
				icon.setIconSize(24);
			}

			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setGraphic(icon);

					// set the image according to the play status
					if (item != null)
						if (item == -1) { // Missing
							JavaFXTool.setFontIcon(this, icon, "fa-deaf", Color.WHITE);
						} else if (item == 0) { // Corrupted
							JavaFXTool.setFontIcon(this, icon, "fas-bug", Color.WHITE);
						} else if (item == 1) { // Okay
							JavaFXTool.setFontIcon(this, icon, "gmi-audiotrack", Color.WHITE);
						}
				}
			}

		});

		// artwork
		artwork.setCellValueFactory(new PropertyValueFactory<>("artwork"));
		artwork.setComparator((stackedFontIcon1, stackedFontIcon2) -> {
			ImageView imageView1 = (ImageView) stackedFontIcon1.getChildren().get(1);
			ImageView imageView2 = (ImageView) stackedFontIcon2.getChildren().get(1);
			if (imageView1.getImage() == null && imageView2.getImage() != null)
				return 1;
			else if (imageView1.getImage() != null && imageView2.getImage() == null)
				return 0;
			else
				return -1;
		});
		artwork.visibleProperty().addListener(l -> {
			if (artwork.isVisible())
				this.allDetailsService.updateOnlyArtWorkColumn(this);
		});

		// title
		title.setStyle(center);
		title.setCellValueFactory(new PropertyValueFactory<>("title"));

		// hourImported
		hourImported.setCellValueFactory(new PropertyValueFactory<>("hourImported"));

		// dateImported
		dateImported.setCellValueFactory(new PropertyValueFactory<>("dateImported"));

		// dateFileCreated
		dateFileCreated.setCellValueFactory(new PropertyValueFactory<>("dateFileCreated"));

		// dateFileCreated
		dateFileModified.setCellValueFactory(new PropertyValueFactory<>("dateFileModified"));

		// timesHeard
		timesPlayed.setCellValueFactory(new PropertyValueFactory<>("timesPlayed"));

		// duration
		duration.setCellValueFactory(new PropertyValueFactory<>("durationEdited"));

		// drive
		drive.setCellValueFactory(new PropertyValueFactory<>("drive"));

		// filePath
		filePath.setStyle(center);
		filePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));

		// fileName
		fileName.setStyle(center);
		fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));

		// fileType
		fileType.setCellValueFactory(new PropertyValueFactory<>("fileType"));

		// fileType
		fileSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));

		// artist
		artist.setCellValueFactory(new PropertyValueFactory<>("artist"));

		// mood
		mood.setCellValueFactory(new PropertyValueFactory<>("mood"));

		// album
		album.setCellValueFactory(new PropertyValueFactory<>("album"));

		// composer
		composer.setCellValueFactory(new PropertyValueFactory<>("composer"));

		// comment
		comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

		// genre
		genre.setCellValueFactory(new PropertyValueFactory<>("genre"));

		// bitRate
		bitRate.setCellValueFactory(new PropertyValueFactory<>("bitRate"));

		// bpm
		bpm.setCellValueFactory(new PropertyValueFactory<>("bpm"));

		// tempo
		tempo.setCellValueFactory(new PropertyValueFactory<>("tempo"));

		// key
		key.setCellValueFactory(new PropertyValueFactory<>("key"));

		// year
		year.setCellValueFactory(new PropertyValueFactory<>("year"));

		// .

		// copyright
		copyright.setCellValueFactory(new PropertyValueFactory<>("copyright"));

		// track
		track.setCellValueFactory(new PropertyValueFactory<>("track"));

		// track_total
		track_total.setCellValueFactory(new PropertyValueFactory<>("track_total"));

		// remixer
		remixer.setCellValueFactory(new PropertyValueFactory<>("remixer"));

		// djMixer
		djMixer.setCellValueFactory(new PropertyValueFactory<>("djMixer"));

		// rating
		rating.setCellValueFactory(new PropertyValueFactory<>("rating"));

		// producer
		producer.setCellValueFactory(new PropertyValueFactory<>("producer"));

		// performer
		performer.setCellValueFactory(new PropertyValueFactory<>("performer"));

		// orchestra
		orchestra.setCellValueFactory(new PropertyValueFactory<>("orchestra"));

		// country
		country.setCellValueFactory(new PropertyValueFactory<>("country"));

		// lyricist
		lyricist.setCellValueFactory(new PropertyValueFactory<>("lyricist"));

		// conductor
		conductor.setCellValueFactory(new PropertyValueFactory<>("conductor"));

		// amazonID
		amazonID.setCellValueFactory(new PropertyValueFactory<>("amazonID"));

		// encoder
		encoder.setCellValueFactory(new PropertyValueFactory<>("encoder"));

		// PauseTransition
		pauseTransition.setOnFinished(f -> searchWord.set(""));

		// QuickSearchTextField
		quickSearchTextField.visibleProperty().bind(searchWord.isEmpty().not());
		quickSearchTextField.textProperty().bind(Bindings.concat("Search :> ").concat(searchWord));

		// ------ centerStackPane
		setOnKeyReleased(key -> {
			KeyCode code = key.getCode();

			// Check this firstly
			if ((key.isControlDown() || key.getCode() == KeyCode.COMMAND) && key.getCode() == KeyCode.C) {

				copySelectedMediaToClipBoard();

				return;

			} else if (smartController.getGenre() == Genre.LIBRARYMEDIA
					&& ((key.isControlDown() || key.getCode() == KeyCode.COMMAND) && key.getCode() == KeyCode.V)) {

				pasteMediaFromClipBoard();

				return;
			}

			// Then this
			if (key.isControlDown() && code == KeyCode.LEFT && mode != SmartControllerMode.FILTERS_MODE)
				smartController.goPrevious();
			else if (key.isControlDown() && code == KeyCode.RIGHT && mode != SmartControllerMode.FILTERS_MODE)
				smartController.goNext();
			else if (key.getCode() == KeyCode.BACK_SPACE)
				searchWord.set("");
			else if (getSelectedCount() > 0) { // TableViewer

				if (code == KeyCode.DELETE && smartController.getGenre() != Genre.SEARCHWINDOW)
					smartController.prepareDelete(key.isShiftDown());
				else if (key.isControlDown()) { // Short Cuts
					if (code == KeyCode.F)
						IOAction.openFileInExplorer(getSelectionModel().getSelectedItem().getFilePath());
					else if (code == KeyCode.Q)
						getSelectionModel().getSelectedItem().updateStars(smartController.getSearchFieldStackPane());
					else if (code == KeyCode.R)
						getSelectionModel().getSelectedItem().rename(smartController.getSearchFieldStackPane());
					else if (code == KeyCode.U) {
						Media media = getSelectionModel().getSelectedItem();
						if (!Main.playedSongs.containsFile(media.getFilePath())) {
							Main.playedSongs.add(media.getFilePath(), true);
							Main.playedSongs.appendToTimesPlayed(media.getFilePath(), true);
						} else {
							if (Main.playedSongs.remove(media.getFilePath(), true))
								media.timesPlayedProperty().set(0);

						}
					} else if (code == KeyCode.ENTER)
						Main.xPlayersList.getXPlayerController(0)
								.playSong(getSelectionModel().getSelectedItem().getFilePath());
					else if (key.isControlDown() && code == KeyCode.I)
						// More than 1 selected?
						if (getSelectedCount() > 1)
							Main.tagWindow
									.openMultipleAudioFiles(
											getSelectionModel().getSelectedItems().stream().map(Media::getFilePath)
													.collect(Collectors
															.toCollection(FXCollections::observableArrayList)),
											getSelectionModel().getSelectedItem().getFilePath());
						// Only one file selected
						else
							Main.tagWindow.openAudio(getSelectionModel().getSelectedItem().getFilePath(),
									TagTabCategory.BASICINFO, true);
				}

			}

			// Local Search
			if (!key.isControlDown() && (key.getCode().isDigitKey() || key.getCode().isKeypadKey()
					|| key.getCode().isLetterKey() || key.getCode() == KeyCode.SPACE)) {
				String keySmall = key.getText().toLowerCase();
				searchWord.set(searchWord.get() + keySmall);
				pauseTransition.playFromStart();

				// Check if searchWord is empty
				if (!searchWord.get().isEmpty()) {
					boolean[] found = { false };
					// Find the first matching item
					tableView.getItems().forEach(media -> {
						if (media.getTitle().toLowerCase().contains(searchWord.get()) && !found[0]) {
							getSelectionModel().clearSelection();
							getSelectionModel().select(media);
							getTableView().scrollTo(media);
							found[0] = true;
						}
					});
				}
			}

		});
	}

	/**
	 * Calculates the selected items in the table.
	 *
	 * @return An int representing the total selected items in the table
	 */
	public int getSelectedCount() {
		return tableView.getSelectionModel().getSelectedItems().size();
	}

	/**
	 * Copies all the selected media files to the Native System ClipBoard
	 */
	public void copySelectedMediaToClipBoard() {
		JavaFXTool.setClipBoard(tableView.getSelectionModel().getSelectedItems().stream()
				.map(s -> new File(s.getFilePath())).collect(Collectors.toList()));
	}

	/**
	 * Past's all Native System ClipBoard content's to the smartcontroller
	 */
	public void pasteMediaFromClipBoard() {
		// Get Native System ClipBoard
		final Clipboard clipboard = Clipboard.getSystemClipboard();

		// Has Files? + isFree()?
		if (clipboard.hasFiles() && smartController.isFree(true))
			smartController.getInputService().start(clipboard.getFiles());
	}

	/**
	 * Sorts the Table
	 */
	public void sortTable() {
		if (!tableView.getSortOrder().isEmpty())
			tableView.sort();
	}

	/**
	 * @return the tableView
	 */
	public TableView<Media> getTableView() {
		return tableView;
	}

	public TableViewSelectionModel<Media> getSelectionModel() {
		return tableView.getSelectionModel();
	}

	/**
	 * @return the detailCssTextArea
	 */
	public InlineCssTextArea getDetailCssTextArea() {
		return detailCssTextArea;
	}

	/**
	 * @return the artwork
	 */
	public TableColumn<Media, StackedFontIcon> getArtworkColumn() {
		return artwork;
	}

	/**
	 * @return the allDetailsService
	 */
	public MediaTagsService getAllDetailsService() {
		return allDetailsService;
	}

	/**
	 * @return the smartController
	 */
	public SmartController getSmartController() {
		return smartController;
	}

}
