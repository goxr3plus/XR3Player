package main.java.com.goxr3plus.xr3player.smartcontroller.presenter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.richtext.InlineCssTextArea;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.application.windows.EmotionsWindow;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.modes.Mode;
import main.java.com.goxr3plus.xr3player.smartcontroller.tags.TagTabCategory;

/**
 * Representing the data of SmartController.
 *
 * @author GOXR3PLUS
 */
public class MediaTableViewer extends StackPane {
	
	@FXML
	private TableView<Media> tableView;
	
	@FXML
	private TableColumn<Media,Integer> number;
	
	/** The has been played. */
	@FXML
	private TableColumn<Media,Integer> playStatus;
	
	/** The media type. */
	@FXML
	private TableColumn<Media,ImageView> mediaType;
	
	/** The title. */
	@FXML
	private TableColumn<Media,String> title;
	
	@FXML
	private TableColumn<Media,Button> getInfoBuy;
	
	@FXML
	private TableColumn<Media,Button> emotions;
	
	/** The duration. */
	@FXML
	private TableColumn<Media,String> duration;
	
	/** The times played. */
	@FXML
	private TableColumn<Media,Integer> timesPlayed;
	
	/** The stars. */
	@FXML
	private TableColumn<Media,Button> stars;
	
	/** The hour imported. */
	@FXML
	private TableColumn<Media,String> hourImported;
	
	/** The date imported. */
	@FXML
	private TableColumn<Media,String> dateImported;
	
	/** The date that the file was created */
	@FXML
	private TableColumn<Media,String> dateFileCreated;
	
	/** The date that the file was last modified */
	@FXML
	private TableColumn<Media,String> dateFileModified;
	
	/** It is a remix? */
	@FXML
	private TableColumn<Media,?> remix;
	
	/** The album. */
	@FXML
	private TableColumn<Media,?> album;
	
	/** The composer. */
	@FXML
	private TableColumn<Media,?> composer;
	
	/** The comment. */
	@FXML
	private TableColumn<Media,?> comment;
	
	/** The genre. */
	@FXML
	private TableColumn<Media,?> genre;
	
	/** The bpm. */
	@FXML
	private TableColumn<Media,Integer> bpm;
	
	/** The key. */
	@FXML
	private TableColumn<Media,?> key;
	
	/** The harmonic. */
	@FXML
	private TableColumn<Media,?> harmonic;
	
	/** The bit rate. */
	@FXML
	private TableColumn<Media,Integer> bitRate;
	
	/** The year. */
	@FXML
	private TableColumn<Media,?> year;
	
	/** The drive. */
	@FXML
	private TableColumn<Media,String> drive;
	
	/** The file path. */
	@FXML
	private TableColumn<Media,String> filePath;
	
	/** The file name. */
	@FXML
	private TableColumn<Media,String> fileName;
	
	/** The file type. */
	@FXML
	private TableColumn<Media,String> fileType;
	
	/** The file size. */
	@FXML
	private TableColumn<Media,String> fileSize;
	
	/** The album art. */
	@FXML
	private TableColumn<Media,?> albumArt;
	
	/** The singer. */
	@FXML
	private TableColumn<Media,?> singer;
	
	@FXML
	private InlineCssTextArea detailCssTextArea;
	
	@FXML
	private Label quickSearchTextField;
	
	@FXML
	private Label dragAndDropLabel;
	
	//-------------------------------------------------
	
	/** The pause transition. */
	private final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
	private final StringProperty searchWord = new SimpleStringProperty("");
	
	/** The image. */
	private WritableImage image = new WritableImage(100, 100);
	
	/** The canvas. */
	private Canvas canvas = new Canvas();
	
	private final SmartController smartController;
	
	private int previousSelectedCount = 0;
	
	private final Mode mode;
	
	/**
	 * Constructor.
	 */
	public MediaTableViewer(SmartController smartController, Mode mode) {
		this.smartController = smartController;
		this.mode = mode;
		
		canvas.setWidth(100);
		canvas.setHeight(100);
		
		// FXMLLOADRE
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MediaTableViewer.fxml"));
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
		
		//------------------------------TableViewer---------------------------
		if (mode == Mode.MEDIA) {
			tableView.setItems(smartController.getItemsObservableList());
			
			//Add the place holder for the tableView
			Label placeHolderLabel = new Label();
			
			if (smartController.getGenre() == Genre.LIBRARYMEDIA) {
				placeHolderLabel.setText("Drag && Drop or Import/Paste Media...");
				placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-cursor:hand;");
				placeHolderLabel.setOnMouseReleased(m -> smartController.getToolsContextMenu().show(placeHolderLabel, m.getScreenX(), m.getScreenY()));
				placeHolderLabel.setGraphic(InfoTool.getImageViewFromResourcesFolder("import24.png"));
			} else if (smartController.getGenre() == Genre.SEARCHWINDOW) {
				placeHolderLabel.setText("Search Media from all the playlists...");
				placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; ");
			} else if (smartController.getGenre() == Genre.EMOTIONSMEDIA) {
				placeHolderLabel.setText("No Media in this emotions list ...");
				placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; ");
			}
			tableView.setPlaceholder(placeHolderLabel);
		}
		
		//--Allow Multiple Selection
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		tableView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) l -> {
			
			//Hold the Current Selected Count
			int currentSelectedCount = getSelectedCount();
			
			//Update the Label only if the current selected count != previousSelectedCount
			if (previousSelectedCount != currentSelectedCount) {
				previousSelectedCount = currentSelectedCount;
				smartController.updateLabel();
			}
			
		});
		
		//Update the Media Information when Selected Item changes
		tableView.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue != null)
				Main.mediaInformation.updateInformation(newValue);
		});
		
		//--KeyListener
		tableView.setOnKeyReleased(key -> {
			if ( ( key.isControlDown() || key.getCode() == KeyCode.COMMAND ) && key.getCode() == KeyCode.C) {
				System.out.println("Control+C was released");
				
				copySelectedMediaToClipBoard();
				
			} else if (smartController.getGenre() == Genre.LIBRARYMEDIA && ( ( key.isControlDown() || key.getCode() == KeyCode.COMMAND ) && key.getCode() == KeyCode.V )) {
				System.out.println("Control+V was released");
				
				pasteMediaFromClipBoard();
			}
		});
		
		//--Row Factory
		tableView.setRowFactory(rf -> {
			TableRow<Media> row = new TableRow<>();
			
			// use EasyBind to access the valueProperty of the itemProperty
			// of the cell:
			row.disableProperty().bind(
					// start at itemProperty of row
					EasyBind.select(row.itemProperty())
							// map to fileExistsProperty[a boolean] of item,
							// if item non-null
							.selectObject(Media::fileExistsProperty)
							// map to BooleanBinding checking if false
							.map(x -> !x.booleanValue())
							// value to use if item was null
							.orElse(false));
			
			//Mouse Listener
			row.setFocusTraversable(true);
			row.setOnMouseReleased(m -> {
				//We don't need null rows (rows without items)
				if (row.itemProperty().getValue() != null) {
					
					if (m.getButton() == MouseButton.SECONDARY && !row.isDisable())
						tableView.getSelectionModel().select(row.getIndex());
					
					//Primary
					if (m.getButton() == MouseButton.PRIMARY) {
						if (m.getClickCount() == 2)
							row.itemProperty().get().rename(row);
						
					} //Secondary
					else if (m.getButton() == MouseButton.SECONDARY && !tableView.getSelectionModel().getSelectedItems().isEmpty())
						Main.songsContextMenu.showContextMenu(row.itemProperty().get(), smartController.getGenre(), m.getScreenX(), m.getScreenY(), smartController, row);
				}
			});
			
			return row;
		});
		
		// --Drag Detected
		tableView.setOnDragDetected(event -> {
			if (getSelectedCount() != 0 && event.getScreenY() > tableView.localToScreen(tableView.getBoundsInLocal()).getMinY() + 30) {
				
				/* allow copy transfer mode */
				Dragboard db = tableView.startDragAndDrop(TransferMode.COPY, TransferMode.LINK);
				
				/* put a string on drag board */
				ClipboardContent content = new ClipboardContent();
				
				// PutFiles
				content.putFiles(tableView.getSelectionModel().getSelectedItems().stream().map(s -> new File(s.getFilePath())).collect(Collectors.toList()));
				
				// Single Drag and Drop ?
				if (content.getFiles().size() == 1)
					tableView.getSelectionModel().getSelectedItem().setDragView(db);
				// Multiple Drag and Drop ?
				else {
					ActionTool.paintCanvas(canvas.getGraphicsContext2D(), "(" + content.getFiles().size() + ")Items", 100, 100);
					db.setDragView(canvas.snapshot(null, image), 50, 0);
				}
				
				db.setContent(content);
			}
			event.consume();
		});
		
		// dragAndDropLabel
		dragAndDropLabel.setVisible(false);
		
		if (smartController.getGenre() == Genre.LIBRARYMEDIA && mode == Mode.MEDIA) {
			
			// --Drag Over
			tableView.setOnDragOver(dragOver -> {
				
				// The drag must come from source other than the owner
				if (dragOver.getDragboard().hasFiles() && dragOver.getGestureSource() != tableView)
					dragAndDropLabel.setVisible(true);
				
			});
			
			dragAndDropLabel.setOnDragOver(dragOver -> {
				
				// The drag must come from source other than the owner
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
		
		//--------------------------Other-----------------------------------
		String center = "-fx-alignment:CENTER-LEFT;";
		
		//getInfoBuy
		getInfoBuy.setCellValueFactory(new PropertyValueFactory<>("getInfoBuy"));
		
		// likeDislikeNeutral
		emotions.setCellValueFactory(new PropertyValueFactory<>("likeDislikeNeutral"));
		emotions.setComparator((button1 , button2) -> {
			if ( ( (ImageView) button1.getGraphic() ).getImage() == EmotionsWindow.neutralImage && ( (ImageView) button2.getGraphic() ).getImage() != EmotionsWindow.neutralImage)
				return 1;
			else if ( ( (ImageView) button1.getGraphic() ).getImage() != EmotionsWindow.neutralImage
					&& ( (ImageView) button2.getGraphic() ).getImage() == EmotionsWindow.neutralImage)
				return -1;
			else
				return 0;
		});
		
		// number
		number.setCellValueFactory(new PropertyValueFactory<>("number"));
		
		// hasBeenPlayed
		playStatus.setCellValueFactory(new PropertyValueFactory<>("playStatus"));
		playStatus.setCellFactory(col -> new TableCell<Media,Integer>() {
			private final ImageView imageView = new ImageView();
			
			{
				setGraphic(imageView);
				imageView.setFitWidth(24);
				imageView.setFitHeight(24);
				
			}
			
			@Override
			protected void updateItem(Integer item , boolean empty) {
				super.updateItem(item, empty);
				
				// set the image according to the play status		
				if (item != null)
					if (item == -2)
						imageView.setImage(null);
					else if (item == -1)
						imageView.setImage(Media.PLAYED_IMAGE);
					else
						imageView.setImage(item == 0 ? Media.PLAYING_IMAGE0 : item == 2 ? Media.PLAYING_IMAGE1 : Media.PLAYING_IMAGE2);
			}
			
		});
		
		// hasBeenPlayed
		mediaType.setCellValueFactory(new PropertyValueFactory<>("mediaType"));
		mediaType.setComparator((imageView1 , imageView2) -> {
			if (imageView1.getImage() == Media.SONG_MISSING_IMAGE && imageView2.getImage() != Media.SONG_MISSING_IMAGE)
				return 1;
			else if (imageView1.getImage() != Media.SONG_MISSING_IMAGE && imageView2.getImage() == Media.SONG_MISSING_IMAGE)
				return -1;
			else
				return 0;
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
		
		// stars
		stars.setCellValueFactory(new PropertyValueFactory<>("stars"));
		stars.setComparator((button1 , button2) -> {
			if (Double.parseDouble(button1.getText()) > Double.parseDouble(button2.getText()))
				return 1;
			else if (Double.parseDouble(button1.getText()) < Double.parseDouble(button2.getText()))
				return -1;
			else
				return 0;
		});
		
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
		
		//bitRate
		bitRate.setCellValueFactory(new PropertyValueFactory<>("bitRate"));
		
		//bpm
		bpm.setCellValueFactory(new PropertyValueFactory<>("bpm"));
		
		// PauseTransition
		pauseTransition.setOnFinished(f -> searchWord.set(""));
		
		// QuickSearchTextField
		quickSearchTextField.visibleProperty().bind(searchWord.isEmpty().not());
		quickSearchTextField.textProperty().bind(searchWord);
		
		// ------ centerStackPane
		setOnKeyReleased(key -> {
			KeyCode code = key.getCode();
			
			if (key.isControlDown() && code == KeyCode.LEFT && mode != Mode.ARTISTS)
				smartController.goPrevious();
			else if (key.isControlDown() && code == KeyCode.RIGHT && mode != Mode.ARTISTS)
				smartController.goNext();
			else if (key.getCode() == KeyCode.BACK_SPACE)
				searchWord.set("");
			else if (getSelectedCount() > 0) { // TableViewer
				
				if (code == KeyCode.DELETE && smartController.getGenre() != Genre.SEARCHWINDOW)
					smartController.prepareDelete(key.isShiftDown());
				else if (key.isControlDown()) { //Short Cuts
					if (code == KeyCode.F)
						ActionTool.openFileLocation(getSelectionModel().getSelectedItem().getFilePath());
					else if (code == KeyCode.Q)
						getSelectionModel().getSelectedItem().updateStars(getTableView());
					else if (code == KeyCode.R)
						getSelectionModel().getSelectedItem().rename(getTableView());
					else if (code == KeyCode.U) {
						Media media = getSelectionModel().getSelectedItem();
						if (!Main.playedSongs.containsFile(media.getFilePath()))
							Main.playedSongs.add(media.getFilePath(), true);
						else
							Main.playedSongs.remove(media.getFilePath(), true);
					} else if (code == KeyCode.ENTER)
						Main.xPlayersList.getXPlayerController(0).playSong(getSelectionModel().getSelectedItem().getFilePath());
					else if (key.isControlDown() && code == KeyCode.I)
						//More than 1 selected?
						if (getSelectedCount() > 1)
							Main.tagWindow.openMultipleAudioFiles(
									getSelectionModel().getSelectedItems().stream().map(Media::getFilePath).collect(Collectors.toCollection(FXCollections::observableArrayList)),
									getSelectionModel().getSelectedItem().getFilePath());
						//Only one file selected
						else
							Main.tagWindow.openAudio(getSelectionModel().getSelectedItem().getFilePath(), TagTabCategory.BASICINFO, true);
				}
				
			}
			
			//Local Search 
			if (!key.isControlDown() && ( key.getCode().isDigitKey() || key.getCode().isKeypadKey() || key.getCode().isLetterKey() || key.getCode() == KeyCode.SPACE )) {
				String keySmall = key.getText().toLowerCase();
				searchWord.set(searchWord.get() + keySmall);
				pauseTransition.playFromStart();
				
				//Check if searchWord is empty
				if (!searchWord.get().isEmpty()) {
					boolean[] found = { false };
					//Find the first matching item
					smartController.getItemsObservableList().forEach(media -> {
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
		//Get Native System ClipBoard
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		
		// PutFiles
		content.putFiles(tableView.getSelectionModel().getSelectedItems().stream().map(s -> new File(s.getFilePath())).collect(Collectors.toList()));
		
		//Set the Content
		clipboard.setContent(content);
		
		ActionTool.showNotification("Copied to Clipboard",
				"Files copied to clipboard,you can paste them anywhere on the your system.\nFor example in Windows with [CTRL+V], in Mac[COMMAND+V]", Duration.seconds(3.5),
				NotificationType.INFORMATION);
	}
	
	/**
	 * Past's all Native System ClipBoard content's to the smartcontroller
	 */
	public void pasteMediaFromClipBoard() {
		//Get Native System ClipBoard
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		
		// Has Files? + isFree()?
		if (clipboard.hasFiles() && smartController.isFree(true))
			smartController.getInputService().start(clipboard.getFiles());
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
	
}
