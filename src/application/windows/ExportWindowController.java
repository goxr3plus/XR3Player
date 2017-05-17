/**
 * 
 */
package application.windows;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import smartcontroller.SmartController;

/**
 * @author GOXR3PLUS
 *
 */
public class ExportWindowController extends BorderPane {

    @FXML
    private TextField exportField1;

    @FXML
    private Button exportButton1;

    @FXML
    private JFXCheckBox copyBox;

    @FXML
    private JFXCheckBox moveBox;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    // ----------------------------------

    /**
     * The Window of the ExportWindowController
     */
    private Stage window = new Stage();

    /**
     * The needed smartController
     */
    private SmartController smartController;

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

	window.setTitle("Export Window");
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

	// exportButton1
	exportButton1.setOnAction(a -> {
	    File file = Main.specialChooser.showSaveDialog(smartController.getName());
	    if (file != null)
		exportField1.setText(file.getAbsolutePath());
	});

	// okButton
	okButton.disableProperty().bind(exportField1.textProperty().isEmpty());

	okButton.setOnAction(a -> {
	    if (!exportField1.getText().isEmpty())
		smartController.copyOrMoveService.startCopy(Arrays.asList(new File(exportField1.getText())));

	    window.close();
	});

	// cancelButton
	cancelButton.setOnAction(a -> window.close());

    }

    /**
     * Opens the Export Window
     * 
     * @param smartController1
     */

    public void show(SmartController smartController1) {
	this.smartController = smartController1;
	window.show();
    }

    /**
     * @return the window
     */
    public Stage getWindow() {
	return window;
    }

}
