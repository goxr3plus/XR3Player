/**
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.enums.FilesMode;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.Operation;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * @author GOXR3PLUS
 *
 */
public class ExportWindowController extends BorderPane {

	// --------------------------------------------

	@FXML
	private VBox containerVBox;

	@FXML
	private ToggleGroup whatFilesToExportGroup;

	@FXML
	private ToggleGroup exportAsGroup;

	@FXML
	private VBox exportFoldersVBox;

	@FXML
	private JFXButton addFolder;

	@FXML
	private JFXButton okButton;

	@FXML
	private JFXButton cancelButton;

	// ----------------------------------

	/**
	 * The Window of the ExportWindowController
	 */
	private final Stage window = new Stage();

	private SmartController oldSmartController;

	/**
	 * The needed smartController
	 */
	private SmartController smartController;

	private FilesMode filesToExport;

	/**
	 * This class wraps an ObservableList
	 */
	private final SimpleListProperty<Media> itemsWrapperProperty = new SimpleListProperty<>();

	/**
	 * Constructor
	 */
	public ExportWindowController() {

		// -----------------------------------------FXMLLoader
		final FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.WINDOW_FXMLS + "ExportWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

		// Window
		window.setResizable(false);
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
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

		// okButton
		okButton.setOnAction(a -> {
			final List<File> foldersList = exportFoldersVBox.getChildren().stream()
					.map(box -> ((ExportWindowFolderHBox) box).getTextField().getText()).filter(text -> !text.isEmpty())
					.map(File::new).collect(Collectors.toList());

			// Check if folders List is empty
			if (foldersList.isEmpty()) {
				AlertTool.showNotification("Message", "You must select at least one folder to export the files",
						Duration.seconds(2), NotificationType.INFORMATION);
				return;
			}

			// Define the Operation
			final Operation operation = Operation.COPY;

			// Nailed it!
			smartController.getFilesExportService().startOperation(foldersList, operation, filesToExport,
					((Labeled) exportAsGroup.getSelectedToggle()).getText().equals("Folder") ? FileType.DIRECTORY
							: FileType.ZIP);

			window.close();
		});

		// cancelButton
		cancelButton.setOnAction(a -> window.close());

		// addFolder
		addFolder.setOnAction(a -> createFolderPickerBox());

		// exportAsGroup
		exportAsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

			// Null is not allowed!
			if (newValue == null) {
				exportAsGroup.selectToggle(oldValue);
				return;
			}

			// Check if the Folder Option is selected
			if (((Labeled) exportAsGroup.getSelectedToggle()).getText().equals("Folder")) {
				// Fix the value for each TextField
				exportFoldersVBox.getChildren().stream().map(box -> ((ExportWindowFolderHBox) box).getTextField())
						.filter(textField -> !textField.getText().isEmpty())
						.forEach(textField -> textField.setText(textField.getText().replaceAll(".zip", "")));
			} else {
				// Fix the value for each TextField
				exportFoldersVBox.getChildren().stream().map(box -> ((ExportWindowFolderHBox) box).getTextField())
						.filter(textField -> !textField.getText().isEmpty() && !textField.getText().contains(".zip"))
						.forEach(textField -> textField.appendText(".zip"));
			}
		});

		// exportAsGroup
		whatFilesToExportGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

			// Null is not allowed!
			if (newValue == null) {
				whatFilesToExportGroup.selectToggle(oldValue);
				return;
			}

			// Define
			defineFilesToExport();
		});

		// Create one Folder Picker at least
		createFolderPickerBox();
	}

	/**
	 * Creates a new Box that allows user to pick another folder too
	 */
	private void createFolderPickerBox() {
		final ExportWindowFolderHBox box = new ExportWindowFolderHBox();

		// Button
		box.getPickFolderButton().setOnAction(a -> pickFolder(box.getTextField()));

		// OKButton
		okButton.disableProperty().unbind();

		// Delete
		box.getDeleteBoxButton().setOnAction(a -> exportFoldersVBox.getChildren().remove(box));

		// exportFoldersVBox
		exportFoldersVBox.getChildren().add(box);
	}

	/**
	 * This is used by export buttons to pick an appropriate folder their export
	 * text field
	 * 
	 * @param exportField
	 */
	private void pickFolder(final TextField exportField) {
		final File file = Main.specialChooser.showSaveDialog(smartController.getName(),
				((Labeled) exportAsGroup.getSelectedToggle()).getText().equals("Folder") ? FileType.DIRECTORY
						: FileType.ZIP);

		// Selected any folder?
		if (file != null) {
			// We don't want the same folder to be selected 2 times or more
			if (exportFoldersVBox.getChildren().stream().map(box -> ((ExportWindowFolderHBox) box).getTextField())
					.filter(field -> exportField != field)
					.filter(field -> field.getText().equals(file.getAbsolutePath())).findAny().isPresent())
				AlertTool.showNotification("Duplicate Selection", "This folder has already been selected",
						Duration.seconds(2), NotificationType.INFORMATION);
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

		// Window Title Property
		window.titleProperty().unbind();
		final String common = "PlayList -> [ " + smartController.getName() + " ] , Total Media to export -> [ ";

		// define the variable using this switch statement
		switch (((Labeled) whatFilesToExportGroup.getSelectedToggle()).getText()) {
		case "Selected Items":
			itemsWrapperProperty
					.setValue(smartController.getNormalModeMediaTableViewer().getSelectionModel().getSelectedItems());
			window.titleProperty()
					.bind(Bindings.createStringBinding(() -> common + itemsWrapperProperty.sizeProperty().get() + " ]",
							itemsWrapperProperty.sizeProperty()));
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
	public void show(final SmartController smartController) {
		this.smartController = smartController;

		// Super
		super.disableProperty().unbind();
		super.disableProperty().bind(smartController.getFilesExportService().runningProperty());

		// Check which SmartController is calling
		if (oldSmartController != smartController)
			exportFoldersVBox.getChildren().forEach(box -> ((ExportWindowFolderHBox) box).getTextField().clear());

		// OldSmartController
		oldSmartController = smartController;

		// Disable or enable buttons
		if (smartController.getFiltersModeTab().isSelected()) {
			((Node) whatFilesToExportGroup.getToggles().get(2)).setDisable(true);
			JavaFXTool.selectToggleOnIndex(whatFilesToExportGroup, 1);
		} else {
			((Node) whatFilesToExportGroup.getToggles().get(2)).setDisable(false);
		}

		// Show the Window
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
