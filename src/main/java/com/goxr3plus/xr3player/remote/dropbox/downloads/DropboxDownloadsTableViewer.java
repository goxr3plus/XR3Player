package main.java.com.goxr3plus.xr3player.remote.dropbox.downloads;

import java.io.IOException;
import java.util.logging.Level;

import org.fxmisc.richtext.InlineCssTextArea;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.systemtreeview.FileTreeItem;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

/**
 * Representing the data of SmartController.
 *
 * @author GOXR3PLUS
 */
public class DropboxDownloadsTableViewer extends StackPane {
	
	@FXML
	private TableView<DropboxDownloadedFile> tableView;
	
	@FXML
	private TableColumn<DropboxDownloadedFile,String> fileThumbnail;
	
	@FXML
	private TableColumn<DropboxDownloadedFile,StackPane> progressBox;
	
	@FXML
	private TableColumn<DropboxDownloadedFile,String> title;
	
	@FXML
	private InlineCssTextArea detailCssTextArea;
	
	@FXML
	private Label quickSearchTextField;
	
	//-------------------------------------------------
	private int previousSelectedCount = 0;
	
	/** The pause transition. */
	private final PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
	private final StringProperty searchWord = new SimpleStringProperty("");
	
	private final ObservableList<DropboxDownloadedFile> observableList = FXCollections.observableArrayList();
	
	/**
	 * Constructor.
	 */
	public DropboxDownloadsTableViewer() {
		
		// FXMLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.DROPBOX_FXMLS + "DropboxDownloadsTableViewer.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "DropboxFilesTableViewer falied to initialize fxml..", ex);
		}
		
	}
	
	/**
	 * Called as soon as .fxml has been initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {
		
		//------------------------------TableViewer---------------------------
		
		//Set Items
		tableView.setItems(observableList);
		
		//--Allow Multiple Selection
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		tableView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) l -> {
			
			//Hold the Current Selected Count
			int currentSelectedCount = getSelectedCount();
			
			//Update the Label only if the current selected count != previousSelectedCount
			if (previousSelectedCount != currentSelectedCount) {
				previousSelectedCount = currentSelectedCount;
				updateLabel();
			}
			
			//Show/hide the action button 
			if (currentSelectedCount > 1)
				tableView.getItems().forEach(item -> item.getActionColumnButton().setVisible(false));
			else
				tableView.getItems().forEach(item -> item.getActionColumnButton().setVisible(true));
			
		});
		
		//Update the Media Information when Selected Item changes
		tableView.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue != null)
				Main.dropBoxViewer.getOpenFolder().setDisable(!newValue.isDirectory());
			else
				Main.dropBoxViewer.getOpenFolder().setDisable(true);
		});
		
		// PauseTransition
		pauseTransition.setOnFinished(f -> searchWord.set(""));
		
		// QuickSearchTextField
		quickSearchTextField.visibleProperty().bind(searchWord.isEmpty().not());
		quickSearchTextField.textProperty().bind(Bindings.concat("Search :> ").concat(searchWord));
		
		//--------------------------Other-----------------------------------
		String center = "-fx-alignment:CENTER-LEFT;";
		
		// title
		title.setStyle(center);
		title.setCellValueFactory(new PropertyValueFactory<>("title"));
		
		// fileType
		fileThumbnail.setCellValueFactory(new PropertyValueFactory<>("extension"));
		fileThumbnail.setCellFactory(cell -> new TableCell<DropboxDownloadedFile,String>() {
			//Icon FontIcon
			FontIcon icon = new FontIcon();
			
			{
				icon.setIconSize(24);
			}
			
			@Override
			protected void updateItem(String item , boolean empty) {
				super.updateItem(item, empty);
				
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					// set the image according to the play status		
					if (item != null && super.getTableRow().getItem() != null) {
						
						setText(null);
						setGraphic(icon);
						
						//It is directory?	
						if ( ( (DropboxDownloadedFile) super.getTableRow().getItem() ).isDirectory()) { //DIRECTORY
							JavaFXTools.setFontIcon(this, icon, "fas-folder", FileTreeItem.folderColor);
						} else {
							//Is it a music file?
							if (InfoTool.isAudioCheckExtension(item)) {           //AUDIO
								JavaFXTools.setFontIcon(this, icon, "fas-file-audio", FileTreeItem.audioColor);
							} else if (InfoTool.isVideoCheckExtension(item)) {    //VIDEO
								JavaFXTools.setFontIcon(this, icon, "fas-file-video", Color.WHITE);
							} else if (InfoTool.isImageCheckExtension(item)) {    //PICTURE
								JavaFXTools.setFontIcon(this, icon, "fas-file-image", Color.WHITE);
							} else if (InfoTool.isPdfCheckExtension(item)) {      //PDF
								JavaFXTools.setFontIcon(this, icon, "fas-file-pdf", FileTreeItem.pdfColor);
							} else if (InfoTool.isZipCheckExtension(item)) {      //ZIP
								JavaFXTools.setFontIcon(this, icon, "fas-file-archive", Color.WHITE);
							} else {                                              //FILE
								JavaFXTools.setFontIcon(this, icon, "fas-file", Color.WHITE);
							}
						}
					}
				}
			}
			
		});
		
		// download
		progressBox.setCellValueFactory(new PropertyValueFactory<>("progressBox"));
		
		//------------------------------------------------------------
		
		//--KeyListener
		tableView.setOnKeyReleased(key -> {
			
			//Find it
			if (key.getCode() == KeyCode.BACK_SPACE)
				searchWord.set("");
			else if (key.getCode() == KeyCode.DELETE)
				Main.dropBoxViewer.deleteSelectedFiles(false);
			
			//Local Search 
			if (!key.isControlDown() && ( key.getCode().isDigitKey() || key.getCode().isKeypadKey() || key.getCode().isLetterKey() || key.getCode() == KeyCode.SPACE )) {
				String keySmall = key.getText().toLowerCase();
				searchWord.set(searchWord.get() + keySmall);
				pauseTransition.playFromStart();
				
				//Check if searchWord is empty
				if (!searchWord.get().isEmpty()) {
					boolean[] found = { false };
					//Find the first matching item
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
	 * Updates the label of the smart controller. [[SuppressWarningsSpartan]]
	 */
	public void updateLabel() {
		
		//Clear the cssTextArea
		getDetailCssTextArea().clear();
		
		//Go madafucker ruuuuuuuuuuun n!!
		String total = "Total : ";
		String _total = InfoTool.getNumberWithDots(tableView.getItems().size());
		
		String selected = "Selected : ";
		String _selected = String.valueOf(getSelectedCount());
		
		//Now set the Text
		appendToDetails(getDetailCssTextArea(), total, _total, true, SmartController.style4);
		appendToDetails(getDetailCssTextArea(), selected, _selected, false, SmartController.style1);
		
	}
	
	/**
	 * This method is used from updateLabel() method to append Text to detailsCssTextArea
	 * 
	 * @param inlineCssTextArea
	 * @param text1
	 * @param text2
	 * @param appendComma
	 * @param style1
	 */
	private void appendToDetails(InlineCssTextArea inlineCssTextArea , String text1 , String text2 , boolean appendComma , String style1) {
		
		inlineCssTextArea.appendText(text1);
		inlineCssTextArea.setStyle(inlineCssTextArea.getLength() - text1.length(), inlineCssTextArea.getLength() - 1, style1);
		
		inlineCssTextArea.appendText(text2 + " " + ( !appendComma ? "" : ", " ));
		inlineCssTextArea.setStyle(inlineCssTextArea.getLength() - text2.length() - ( appendComma ? 3 : 1 ), inlineCssTextArea.getLength() - 1, SmartController.style2);
	}
	
	/**
	 * Sorts the Table
	 */
	public void sortTable() {
		if (!tableView.getSortOrder().isEmpty())
			tableView.sort();
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
	 * @return the tableView
	 */
	public TableView<DropboxDownloadedFile> getTableView() {
		return tableView;
	}
	
	public TableViewSelectionModel<DropboxDownloadedFile> getSelectionModel() {
		return tableView.getSelectionModel();
	}
	
	/**
	 * @return the detailCssTextArea
	 */
	public InlineCssTextArea getDetailCssTextArea() {
		return detailCssTextArea;
	}
	
	/**
	 * @return the observableList
	 */
	public ObservableList<DropboxDownloadedFile> getObservableList() {
		return observableList;
	}
	
}
