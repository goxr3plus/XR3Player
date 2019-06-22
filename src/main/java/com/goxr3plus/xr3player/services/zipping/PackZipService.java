/*
 * 
 */
package com.goxr3plus.xr3player.services.zipping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * This class is used as a Service which is exporting the applications database
 * as a zip folder.
 *
 * @author SuperGoliath
 */
public class PackZipService extends Service<Boolean> {

	/** The file list. */
	List<String> fileList = new ArrayList<>();

	/** The zip file. */
	String zipFile;

	/** The source folder. */
	String sourceFolder;

	/** The exception. */
	String exception;

	/**
	 * This method is using a Service to export the dataBase into a zip folder.
	 *
	 * @param zipFile      The Destination zip Folder
	 * @param sourceFolder The source Folder
	 */
	public void exportDataBase(final String zipFile, final String sourceFolder) {
		this.zipFile = zipFile;
		this.sourceFolder = sourceFolder;

		// Success
		setOnSucceeded(s -> {
			done();

			// Check the Value
			if (getValue()) {
				AlertTool.showNotification("Zip Service",
						"Successfully create requested zip file :\n [ " + zipFile + " ]", Duration.seconds(3),
						NotificationType.SUCCESS);
			} else
				showErrorNotification(exception);
		});

		// Failure
		setOnFailed(f -> {
			done();
			showErrorNotification("Zip Service Failed");
		});

		// Cancelled
		setOnCancelled(c -> done());

		// Set Cancel Action
		// Main.updateScreen.getCancelButton().setDisable(false);
		// Main.updateScreen.getCancelButton().setOnAction(a -> cancel());

		// Restart the Service
		restart();

	}

	private void showErrorNotification(final String reason) {
		AlertTool.showNotification("Failed",
				"Failed to export database :\n Reason [ " + (reason.isEmpty() ? "Unknown" : reason) + "]",
				Duration.seconds(3), NotificationType.ERROR);
	}

	/**
	 * Service done.
	 */
	private void done() {
		// Main.updateScreen.setVisible(false);
		// Main.updateScreen.getProgressBar().progressProperty().unbind();
		// Main.updateScreen.getCancelButton().setDisable(true);

	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<>() {
			@Override
			protected Boolean call() throws Exception {

				// Create a list with all the files and folders of the
				// sourceFolder
				fileList.clear();
				generateFileList(new File(sourceFolder), sourceFolder);
				final byte[] buffer = new byte[1024];

				final double total = fileList.size();
				double counter = 0;

				// GO
				try (FileOutputStream fos = new FileOutputStream(zipFile);
					 ZipOutputStream zos = new ZipOutputStream(fos)) {

					// Start
					for (final String file : fileList) {

						// Check if service is cancelled
						if (isCancelled())
							break;

						// Refresh the label Text
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("OUT:" + file));

						// Create zipEntry
						zos.putNextEntry(new ZipEntry(file));

						// Create File Input Stream
						try (FileInputStream in = new FileInputStream(sourceFolder + File.separator + file)) {

							// Copy byte by byte
							int len;
							while ((len = in.read(buffer)) > 0)
								zos.write(buffer, 0, len);

						} catch (final IOException ex) {
							ex.printStackTrace();
							exception = ex.getMessage();
						}

						// Update Progress
						updateProgress(++counter / total, 1);

					}

					// Close the motherFuckers
					zos.closeEntry();

				} catch (final IOException ex) {
					ex.printStackTrace();
					exception = ex.getMessage();
					return false;
				}

				// Delete the zip folder if cancelled
				if (isCancelled())
					new File(zipFile).delete();

				return true;
			}

		};
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList.
	 *
	 * @param f     the file
	 * @param file2 the file 2
	 */
	public void generateFileList(final File f, final String file2) {

		// add file only
		if (f.isFile())
			fileList.add(generateZipEntry(f.getAbsoluteFile() + "", file2));

		if (!f.isDirectory())
			return;
		final String[] subNote = f.list();
		if (subNote != null)
			for (final String filename : subNote)
				generateFileList(new File(f, filename), file2);

	}

	/**
	 * Format the file path for zip.
	 *
	 * @param file         file path
	 * @param sourceFolder the source folder
	 * @return Formatted file path
	 */
	private String generateZipEntry(final String file, final String sourceFolder) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}

}
