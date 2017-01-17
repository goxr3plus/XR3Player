/**
 * 
 */
package smartcontroller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jfoenix.controls.JFXCheckBox;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class ExportWindowController extends ScrollPane {
	
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
	
	// ----------------------------------
	
	/**
	 * The Window of the ExportWindowController
	 */
	public Stage window = new Stage();
	
	/**
	 * The needed smartController
	 */
	private SmartController smartController;
	
	/**
	 * Constructor
	 */
	public ExportWindowController() {
		
		window.getIcons().add(InfoTool.getImageFromDocuments("icon.png"));
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		
		// -----------------------------------------FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "ExportWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		window.setScene(new Scene(this));
		window.getScene().getStylesheets()
		        .add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());
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
		okButton.setOnAction(a -> {
			if (!exportField1.getText().isEmpty())
				smartController.copyOrMoveService.startCopy(Arrays.asList(new File(exportField1.getText())));
			
			window.close();
		});
		
	}
	
	/**
	 * Opens the Export Window
	 * 
	 * @param smartController
	 */
	
	public void show(SmartController smartController) {
		this.smartController = smartController;
		window.show();
	}
	
}
