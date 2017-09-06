package smartcontroller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import application.windows.EmotionsWindow;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;
import smartcontroller.media.Media;

/**
 * Representing the data of SmartController.
 *
 * @author GOXR3PLUS
 */
public class MediaTableViewer extends TableView<Media> {
	
	@FXML
	private TableColumn<Media,Integer> number;
	
	/** The has been played. */
	@FXML
	private TableColumn<Media,Boolean> hasBeenPlayed;
	
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
	private TableColumn<Media,?> bpm;
	
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
	
	//-------------------------------------------------
	
	/** The image. */
	private WritableImage image = new WritableImage(100, 100);
	
	/** The canvas. */
	private Canvas canvas = new Canvas();
	
	private final SmartController smartController;
	
	/**
	 * The selected row of the tableview , i need to impement this!
	 */
	//private TableRow<Media> selectedRow = new TableRow<>();
	
	/**
	 * Constructor.
	 */
	public MediaTableViewer(SmartController smartController) {
		this.smartController = smartController;
		
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
	 * Called as soon as .fxml has been initialized
	 */
	@FXML
	private void initialize() {
		
		//------------------------------TableViewer---------------------------
		setItems(smartController.getItemsObservableList());
		
		//Add the place holder for the tableView
		Label placeHolderLabel = new Label();
		
		if (smartController.getGenre() == Genre.LIBRARYMEDIA) {
			placeHolderLabel.setText("Drag && Drop or Import/Paste Media...");
			placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-cursor:hand;");
			placeHolderLabel.setOnMouseReleased(m -> smartController.getToolsContextMenu().show(placeHolderLabel, m.getScreenX(), m.getScreenY()));
		} else if (smartController.getGenre() == Genre.SEARCHWINDOW) {
			placeHolderLabel.setText("Search Media from all the playlists...");
			placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; ");
		} else if (smartController.getGenre() == Genre.EMOTIONSMEDIA) {
			placeHolderLabel.setText("No Media in this emotions list ...");
			placeHolderLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold; ");
		}
		setPlaceholder(placeHolderLabel);
		
		//--Selection Model
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) l -> smartController.updateLabel()); // Main.amazon.updateInformation((Media) newValue)
		getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue != null)
				Main.mediaInformation.updateInformation(newValue);
		});
		
		//--KeyListener
		setOnKeyReleased(key -> {
			if ( ( key.isControlDown() || key.getCode() == KeyCode.COMMAND ) && key.getCode() == KeyCode.C) {
				System.out.println("Control+C was released");
				
				copySelectedMediaToClipBoard();
				
			} else if (smartController.getGenre() == Genre.LIBRARYMEDIA && ( ( key.isControlDown() || key.getCode() == KeyCode.COMMAND ) && key.getCode() == KeyCode.V )) {
				System.out.println("Control+V was released");
				
				pasteMediaFromClipBoard();
			}
		});
		
		//--Row Factory
		setRowFactory(rf -> {
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
						smartController.getTableViewer().getSelectionModel().select(row.getIndex());
					
					//Primary
					if (m.getButton() == MouseButton.PRIMARY) {
						if (m.getClickCount() == 2)
							row.itemProperty().get().rename(row);
						
					} //Secondary
					else if (m.getButton() == MouseButton.SECONDARY && !smartController.getTableViewer().getSelectionModel().getSelectedItems().isEmpty())
						Main.songsContextMenu.showContextMenu(row.itemProperty().get(), smartController.getGenre(), m.getScreenX(), m.getScreenY(), smartController, row);
				}
			});
			
			//Needs fixing!!!
			//KeyListener
			//			row.setOnKeyReleased(k -> {
			//				System.out.println("Key Released....");
			//				KeyCode code = k.getCode();
			//				
			//				if (code == KeyCode.R)
			//					row.itemProperty().get().rename(smartController, row);
			//				else if (code == KeyCode.S)
			//					smartController.getTableViewer().getSelectionModel().getSelectedItem().updateStars(smartController, row);
			//			});
			
			// it's also possible to do this with the standard API, but
			// there are lots of
			// superfluous warnings sent to standard out:
			// row.setStyle("-fx-background-color:red");
			// row.disableProperty().bind(
			// Bindings.selectBoolean(row.itemProperty(),
			// "fileExists").not());
			
			return row;
		});
		
		// --Drag Detected
		setOnDragDetected(event -> {
			if (getSelectedCount() != 0 && event.getScreenY() > localToScreen(getBoundsInLocal()).getMinY() + 30) {
				
				/* allow copy transfer mode */
				Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);
				
				/* put a string on drag board */
				ClipboardContent content = new ClipboardContent();
				
				// PutFiles
				content.putFiles(getSelectionModel().getSelectedItems().stream().map(s -> new File(s.getFilePath())).collect(Collectors.toList()));
				
				// Single Drag and Drop ?
				if (content.getFiles().size() == 1)
					getSelectionModel().getSelectedItem().setDragView(db);
				// Multiple Drag and Drop ?
				else {
					ActionTool.paintCanvas(canvas.getGraphicsContext2D(), "(" + content.getFiles().size() + ")Items", 100, 100);
					db.setDragView(canvas.snapshot(null, image), 50, 0);
				}
				
				db.setContent(content);
			}
			event.consume();
		});
		
		if (smartController.getGenre() == Genre.LIBRARYMEDIA) {
			
			// --Drag Over
			setOnDragOver(dragOver -> {
				// System.out.println(over.getGestureSource() + "," +
				// controller.tableViewer)
				
				// // Check if the drag come from the same source
				// String gestureSourceString
				// if (over.getGestureSource() != null)
				// gestureSourceString = over.getGestureSource()
				// .toString()
				// else
				// gestureSourceString = "null"
				
				// The drag must come from source other than the owner
				if (dragOver.getDragboard().hasFiles() && dragOver.getGestureSource() != smartController.getTableViewer()) {
					dragOver.acceptTransferModes(TransferMode.LINK);
				}
			});
			
			//Drag Entered and Exited
			//setOnDragEntered(d -> navigationHBox.setVisible(false))
			//setOnDragExited(d -> navigationHBox.setVisible(true))
			
			// --Drag Dropped
			setOnDragDropped(drop -> {
				// Has Files? + isFree()?
				if (drop.getDragboard().hasFiles() && smartController.isFree(true))
					smartController.getInputService().start(drop.getDragboard().getFiles());
				
				drop.setDropCompleted(true);
			});
			
		}
		
		// setOnDragDone(d -> {
		// System.out.println(
		// "Drag Done,is drop completed?" + d.isDropCompleted() + " , is
		// accepted?" + d.isAccepted());
		// System.out.println("Accepted Mode:" +
		// d.getAcceptedTransferMode());
		// System.out.println(" Target:" + d.getTarget() + " Gesture
		// Target:" + d.getGestureTarget());
		// });
		
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
		hasBeenPlayed.setCellValueFactory(new PropertyValueFactory<>("hasBeenPlayed"));
		hasBeenPlayed.setCellFactory(col -> new TableCell<Media,Boolean>() {
			private final ImageView imageView = new ImageView();
			
			{
				setGraphic(imageView);
				//imageView.setFitWidth(24);
				//imageView.setFitHeight(24);
			}
			
			@Override
			protected void updateItem(Boolean item , boolean empty) {
				super.updateItem(item, empty);
				
				// set the image according to the played state
				imageView.setImage(item != null && item ? Media.PLAYED_IMAGE : null);
			}
			
		});
		
		// hasBeenPlayed
		mediaType.setCellValueFactory(new PropertyValueFactory<>("mediaType"));
		
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
		
		//		this.getColumns().addListener((ListChangeListener <? super TableColumn<Media, ?>>) ( c -> {
		//			c.getList().stream().forEach(column->{
		//				System.out.printf("%s ,",column.getText());
		//			});
		//			System.out.println();
		//		}));
		
	}
	
	/**
	 * Calculates the selected items in the table.
	 *
	 * @return An int representing the total selected items in the table
	 */
	public int getSelectedCount() {
		return getSelectionModel().getSelectedItems().size();
	}
	
	/**
	 * Copies all the selected media files to the Native System ClipBoard
	 */
	public void copySelectedMediaToClipBoard() {
		//Get Native System ClipBoard
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		
		// PutFiles
		content.putFiles(getSelectionModel().getSelectedItems().stream().map(s -> new File(s.getFilePath())).collect(Collectors.toList()));
		
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
}
