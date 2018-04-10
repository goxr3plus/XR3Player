/**
 * 
 */
package main.java.com.goxr3plus.xr3player.application.windows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.FilesMode;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;
import main.java.com.goxr3plus.xr3player.smartcontroller.services.Operation;

/**
 * @author GOXR3PLUS
 *
 */
public class ExportWindowController extends BorderPane {
	
	// --------------------------------------------
	
	@FXML
	private ToggleGroup exportProcedureGroup;
	
	@FXML
	private ToggleGroup whatFilesToExportGroup;
	
	@FXML
	private TextField exportField1;
	
	@FXML
	private Button clear1;
	
	@FXML
	private Button exportButton1;
	
	@FXML
	private TextField exportField2;
	
	@FXML
	private Button clear2;
	
	@FXML
	private Button exportButton2;
	
	@FXML
	private TextField exportField3;
	
	@FXML
	private Button clear3;
	
	@FXML
	private Button exportButton3;
	
	@FXML
	private Button okButton;
	
	@FXML
	private Button cancelButton;
	
	// ----------------------------------
	
	/**
	 * The Window of the ExportWindowController
	 */
	private Stage window = new Stage();
	
	private SmartController oldSmartController;
	
	/**
	 * The needed smartController
	 */
	private SmartController smartController;
	
	private FilesMode filesToExport;
	
	/**
	 * Constructor
	 */
	public ExportWindowController() {
		
		// -----------------------------------------FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "ExportWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// Window
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		window.getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				window.close();
		});
	}
	
	/**
	 * Called when FXML has been initialized
	 */
	@FXML
	private void initialize() {
		
		// exportButtons
		exportButton1.setOnAction(a -> pickFolder(exportField1));
		exportButton2.setOnAction(a -> pickFolder(exportField2));
		exportButton3.setOnAction(a -> pickFolder(exportField3));
		
		// clearButtons
		clear1.setOnAction(a -> exportField1.clear());
		clear2.setOnAction(a -> exportField2.clear());
		clear3.setOnAction(a -> exportField3.clear());
		
		// whatFilesToExportGroup
		whatFilesToExportGroup.selectedToggleProperty().addListener(l -> defineFilesToExport());
		
		// okButton
		okButton.disableProperty().bind(exportField1.textProperty().isEmpty().and(exportField2.textProperty().isEmpty()).and(exportField3.textProperty().isEmpty()));
		okButton.setOnAction(a -> {
			
			//Define the Operation
			Operation operation = "Copy".equalsIgnoreCase( ( (Labeled) exportProcedureGroup.getSelectedToggle() ).getText()) ? Operation.COPY : Operation.MOVE;
			
			//Create the directories for export !
			List<File> directories = new ArrayList<>();
			if (!exportField1.getText().isEmpty())
				directories.add(new File(exportField1.getText()));
			if (!exportField2.getText().isEmpty())
				directories.add(new File(exportField2.getText()));
			if (!exportField3.getText().isEmpty())
				directories.add(new File(exportField3.getText()));
			
			//Nailed it!
			smartController.getCopyOrMoveService().startOperation(directories, operation, filesToExport);
			
			window.close();
		});
		
		// cancelButton
		cancelButton.setOnAction(a -> window.close());
		
	}
	
	/**
	 * This is used by export buttons to pick an appropriate folder their export text field
	 * 
	 * @param exportField
	 */
	private void pickFolder(TextField exportField) {
		File file = Main.specialChooser.showSaveDialog(smartController.getName());
		
		//Selected any folder?
		if (file != null) {
			//We don't want the same folder to be selected 2 times or more
			if (Arrays.asList(exportField1, exportField2, exportField3).stream().filter(field -> exportField != field)
					.filter(field -> field.getText().equals(file.getAbsolutePath())).findAny().isPresent())
				ActionTool.showNotification("Duplicate Selection", "This folder has already been selected", Duration.seconds(2), NotificationType.INFORMATION);
			else
				exportField.setText(file.getAbsolutePath());
		}
	}
	
	/**
	 * Defines the FilesToExport variable , along with window title
	 */
	private void defineFilesToExport() {
		if (smartController == null)
			return;
		
		String common = "PlayList -> [ " + smartController.getName() + " ] , Total Media to export -> [ ";
		
		//define the variable using this switch statement
		switch ( ( (Labeled) whatFilesToExportGroup.getSelectedToggle() ).getText()) {
			case "Selected Items":
				window.setTitle(common + smartController.getNormalModeMediatTableViewer().getSelectionModel().getSelectedItems().size() + " ]");
				filesToExport = FilesMode.SELECTED_MEDIA;
				break;
			case "Current Page":
				window.setTitle(common + smartController.getItemsObservableList().size() + " ]");
				filesToExport = FilesMode.CURRENT_PAGE;
				break;
			case "Everything on Playlist":
				window.setTitle(common + smartController.getTotalInDataBase() + " ]");
				filesToExport = FilesMode.EVERYTHING_ON_PLAYLIST;
				break;
			default:
				filesToExport = FilesMode.CURRENT_PAGE;
		}
	}
	
	/**
	 * Opens the Export Window
	 * 
	 * @param smartController
	 */
	public void show(SmartController smartController) {
		this.smartController = smartController;
		
		if (oldSmartController != smartController) {
			exportField1.clear();
			exportField2.clear();
			exportField3.clear();
		}
		oldSmartController = smartController;
		
		//Disable or enable buttons
		if (smartController.getFiltersModeTab().isSelected()) {
			( (Node) whatFilesToExportGroup.getToggles().get(2) ).setDisable(true);
			JavaFXTools.selectToggleOnIndex(whatFilesToExportGroup, 1);
		} else {
			( (Node) whatFilesToExportGroup.getToggles().get(2) ).setDisable(false);
		}
		
		//Show the Window
		defineFilesToExport();
		window.show();
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
}
