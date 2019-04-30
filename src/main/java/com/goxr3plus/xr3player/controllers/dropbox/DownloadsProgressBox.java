package com.goxr3plus.xr3player.controllers.dropbox;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.IOAction;

public class DownloadsProgressBox extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private ProgressBar downloadProgress;

	@FXML
	private JFXButton cancelDownload;

	@FXML
	private JFXButton deleteFile;

	@FXML
	private JFXButton openFileLocation;

	// -------------------------------------------------------------
	private final DropboxDownloadedFile dropBoxDownloadedFile;

	/**
	 * Constructor.
	 */
	public DownloadsProgressBox(DropboxDownloadedFile dropBoxDownloadedFile) {
		this.dropBoxDownloadedFile = dropBoxDownloadedFile;

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.DROPBOX_FXMLS + "DownloadsProgressBox.fxml"));
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

		// downloadProgress
		downloadProgress.progressProperty().bind(dropBoxDownloadedFile.getDownloadService().progressProperty());

		// cancelDownload
		cancelDownload.disableProperty().bind(dropBoxDownloadedFile.getDownloadService().runningProperty().not());
		cancelDownload.setOnAction(a -> {
			dropBoxDownloadedFile.getDownloadService().cancelDownload();

			// Remove from TableViewer
			Main.dropboxDownloadsTableViewer.getObservableList().remove(dropBoxDownloadedFile);

			// Delete from computer
			IOAction.deleteFile(new File(dropBoxDownloadedFile.getDownloadService().getLocalFileAbsolutePath()));
		});

		// deleteFile
		deleteFile.disableProperty().bind(dropBoxDownloadedFile.getDownloadService().runningProperty());
		deleteFile.setOnAction(action -> {
			List<Boolean> answers = Main.mediaDeleteWindow.doDeleteQuestion(false, dropBoxDownloadedFile.getTitle(), 1,
					Main.window);

			// Check if the user is sure he want's to go on delete action
			if (!answers.get(0))
				return;
			// Check if the delete will be finally permanent or not
			boolean permanent = answers.get(1);

			// Check if the delete is permanent
			if (permanent)
				IOAction.deleteFile(new File(dropBoxDownloadedFile.getDownloadService().getLocalFileAbsolutePath()));

			Main.dropboxDownloadsTableViewer.getObservableList().remove(dropBoxDownloadedFile);

		});

		// openFileLocation
		openFileLocation.setOnAction(a -> IOAction
				.openFileInExplorer(dropBoxDownloadedFile.getDownloadService().getLocalFileAbsolutePath()));
	}

}
