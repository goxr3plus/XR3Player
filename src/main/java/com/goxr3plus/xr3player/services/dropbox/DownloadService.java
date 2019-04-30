package com.goxr3plus.xr3player.services.dropbox;

import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.DownloadZipResult;
import com.dropbox.core.v2.files.FileMetadata;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxFile;
import com.goxr3plus.xr3player.controllers.dropbox.DropboxViewer;
import com.goxr3plus.xr3player.controllers.dropbox.ProgressOutputStream;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

public class DownloadService extends Service<Boolean> {

	private static final String FOLDER = "Folder";

	/**
	 * DropBoxViewer
	 */
	public DropboxViewer dropBoxViewer;

	// Create Dropbox client
	private final DbxRequestConfig config = new DbxRequestConfig("XR3Player");
	private DbxClientV2 client;
	private final DropboxFile dropboxFile;
	private final String localFileAbsolutePath;
	private DbxDownloader<FileMetadata> downloadFile;
	private DbxDownloader<DownloadZipResult> downloadFolder;

	/**
	 * Constructor
	 * 
	 * @param dropBoxViewer
	 */
	public DownloadService(final DropboxViewer dropBoxViewer, final DropboxFile dropboxFile,
			final String localFileAbsolutePath) {
		this.dropBoxViewer = dropBoxViewer;

		this.dropboxFile = dropboxFile;
		this.localFileAbsolutePath = localFileAbsolutePath;
	}

	/**
	 * Restart the Service
	 * 
	 */
	public void startService() {

		// Restart
		super.restart();
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {

				try {

					// Create the Client
					client = new DbxClientV2(config, dropBoxViewer.getAccessToken());

					// Try to download the File
					downloadFile(client, dropboxFile, localFileAbsolutePath);

					// Show message to the User
					Platform.runLater(() -> AlertTool.showNotification("Download completed",
							"Completed downloading " + (!dropboxFile.isDirectory() ? "File" : FOLDER) + " :\n[ "
									+ dropboxFile.getMetadata().getName() + " ]",
							Duration.millis(3000), NotificationType.SIMPLE,
							JavaFXTool.getFontIcon("fa-dropbox", DropboxViewer.FONT_ICON_COLOR, 64)));

					// Update the progress
					updateProgress(1, 1);

					return true;
				} catch (final Exception ex) {
					ex.printStackTrace();

					// Show message to the User
					Platform.runLater(() -> AlertTool.showNotification("Download Failed",
							"Failed to download " + (!dropboxFile.isDirectory() ? "File" : FOLDER) + ":\n[ "
									+ dropboxFile.getMetadata().getName() + " ]",
							Duration.millis(3000), NotificationType.ERROR));

					return false;
				}
			}

			/**
			 * Download Dropbox File to Local Computer
			 * 
			 * @param client                Current connected client
			 * @param dropboxFile           The file path on the Dropbox cloud server ->
			 *                              [/foldername/something.txt] or a Folder [/fuck]
			 * @param localFileAbsolutePath The absolute file path of the File on the Local
			 *                              File System
			 * @throws DbxException
			 * @throws DownloadErrorException
			 * @throws IOException
			 */
			public void downloadFile(final DbxClientV2 client, final DropboxFile dropboxFile,
					final String localFileAbsolutePath) throws DownloadErrorException, DbxException, IOException {
				final String dropBoxFilePath = dropboxFile.getMetadata().getPathLower();

				// Simple File
				if (!dropboxFile.isDirectory()) {
					// Create DbxDownloader
					downloadFile = client.files().download(dropBoxFilePath);
					try (// FileOutputStream
							FileOutputStream fOut = new FileOutputStream(localFileAbsolutePath);
							// ProgressOutPutStream
							ProgressOutputStream output = new ProgressOutputStream(fOut,
									downloadFile.getResult().getSize(), (long completed, long totalSize) -> {
										// System.out.println( ( completed * 100 ) / totalSize + " %")

										updateProgress((completed * 100), totalSize);
									})) {

						// FileOutputStream
						System.out.println("Downloading .... " + dropBoxFilePath);

						// Add a progress Listener
						downloadFile.download(output);

						// Fast way...
						// client.files().downloadBuilder(file).download(new
						// FileOutputStream("downloads/" + md.getName()))
						// DbxRawClientV2 rawClient = new
						// DbxRawClientV2(config,dropBoxViewer.getAccessToken())
						// DbxUserFilesRequests r = new DbxUserFilesRequests(client)
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					// Directory
				} else {
					// Create DbxDownloader
					downloadFolder = client.files().downloadZip(dropBoxFilePath);
					try (
							// FileOutputStream
							FileOutputStream fOut = new FileOutputStream(localFileAbsolutePath)) {

						// FileOutputStream
						System.out.println("Downloading .... " + dropBoxFilePath);

						// Add a progress Listener
						downloadFolder.download(fOut);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		};

	}

	/**
	 * This method attempts to cancel the download of the file
	 */
	public void cancelDownload() {
		// Cancel file download
		if (downloadFile != null) {
			downloadFile.close();
			// Cancel folder download
		} else if (downloadFolder != null) {
			downloadFolder.close();
		}

		// Show message to the User
		Platform.runLater(() -> AlertTool.showNotification(
				"Download Cancelled", "Download cancelled for" + (!dropboxFile.isDirectory() ? "File" : FOLDER)
						+ ":\n[ " + dropboxFile.getMetadata().getName() + " ]",
				Duration.millis(3000), NotificationType.WARNING));
	}

	/**
	 * @return the client
	 */
	public DbxClientV2 getClient() {
		return client;
	}

	/**
	 * @return the dropboxFile
	 */
	public DropboxFile getDropboxFile() {
		return dropboxFile;
	}

	/**
	 * @return the localFileAbsolutePath
	 */
	public String getLocalFileAbsolutePath() {
		return localFileAbsolutePath;
	}

}
