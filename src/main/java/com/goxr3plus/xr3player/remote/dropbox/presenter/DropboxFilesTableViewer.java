package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import java.io.IOException;
import java.util.logging.Level;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * Representing the data of SmartController.
 *
 * @author GOXR3PLUS
 */
public class DropboxFilesTableViewer extends StackPane {
	
	@FXML
	private TableView<DropboxFile> tableView;
	
	@FXML
	private TableColumn<DropboxFile,ImageView> fileThumbnail;
	
	@FXML
	private TableColumn<DropboxFile,String> title;
	
	@FXML
	private TableColumn<DropboxFile,Button> actionColumn;
	
	@FXML
	private InlineCssTextArea detailCssTextArea;
	
	@FXML
	private Label quickSearchTextField;
	
	@FXML
	private Label dragAndDropLabel;
	
	//-------------------------------------------------
	private int previousSelectedCount = 0;
	
	/**
	 * Constructor.
	 */
	public DropboxFilesTableViewer() {
		
		// FXMLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "DropboxFilesTableViewer.fxml"));
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
		
		//--Allow Multiple Selection
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		tableView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) l -> {
			
			//Hold the Current Selected Count
			int currentSelectedCount = getSelectedCount();
			
			//Update the Label only if the current selected count != previousSelectedCount
			if (previousSelectedCount != currentSelectedCount) {
				previousSelectedCount = currentSelectedCount;
				//smartController.updateLabel();
			}
			
		});
		
		//Update the Media Information when Selected Item changes
		tableView.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue) -> {
			//if (newValue != null)
			//Main.mediaInformation.updateInformation(newValue);
		});
		
		//--------------------------Other-----------------------------------
		String center = "-fx-alignment:CENTER-LEFT;";
		
		// title
		title.setStyle(center);
		title.setCellValueFactory(new PropertyValueFactory<>("title"));
		
		// fileType
		fileThumbnail.setCellValueFactory(new PropertyValueFactory<>("fileType"));
		
		// actionColumn
		actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionColumn"));
		
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
	public TableView<DropboxFile> getTableView() {
		return tableView;
	}
	
	public TableViewSelectionModel<DropboxFile> getSelectionModel() {
		return tableView.getSelectionModel();
	}
	
	/**
	 * @return the detailCssTextArea
	 */
	public InlineCssTextArea getDetailCssTextArea() {
		return detailCssTextArea;
	}
	
}
