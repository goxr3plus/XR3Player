package main.java.com.goxr3plus.xr3player.remote.dropbox.downloads;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class DownloadsProgressBox extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private ProgressBar downloadProgress;
	
	@FXML
	private JFXButton cancelDownload;
	
	@FXML
	private JFXButton openFileLocation;
	
	// -------------------------------------------------------------
	private final DropboxDownloadedFile dropBoxDownloadedFile;
	
	/**
	 * Constructor.
	 */
	public DownloadsProgressBox(DropboxDownloadedFile dropBoxDownloadedFile) {
		this.dropBoxDownloadedFile = dropBoxDownloadedFile;
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.DROPBOX_FXMLS + "DownloadsProgressBox.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Called as soon as fxml is initialized
	 */
	@FXML
	private void initialize() {
		
		//downloadProgress
		downloadProgress.progressProperty().bind(dropBoxDownloadedFile.getDownloadService().progressProperty());
		
		//cancelDownload
		cancelDownload.disableProperty().bind(dropBoxDownloadedFile.getDownloadService().runningProperty());
		cancelDownload.setOnAction(a -> {
			dropBoxDownloadedFile.getDownloadService().cancelDownload();
			Main.dropboxDownloadsTableViewer.getObservableList().remove(dropBoxDownloadedFile);
		});
		
		//openFileLocation
		openFileLocation.disableProperty().bind(dropBoxDownloadedFile.getDownloadService().runningProperty());
		openFileLocation.setOnAction(a -> ActionTool.openFileLocation(dropBoxDownloadedFile.getDownloadService().getLocalFileAbsolutePath()));
	}
	
}
