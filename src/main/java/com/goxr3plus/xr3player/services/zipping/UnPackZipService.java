/*
 * 
 */
package com.goxr3plus.xr3player.services.zipping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.application.MainExit;
import com.goxr3plus.xr3player.database.DatabaseTool;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.Operation;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * This class is used to import an XR3Player database (as .zip folder)
 * 
 * @author SuperGoliath
 *
 */
public class UnPackZipService extends Service<Boolean> {

	/** The input zip. */
	private String inputZip;

	/** The out put folder. */
	private String outPutFolder = DatabaseTool.getAbsoluteDatabasePathPlain();

	/** The exception. */
	private String exception;

	/**
	 * Constructor.
	 */
	public UnPackZipService() {

		setOnSucceeded(s -> {
			// done()
			Main.canSaveData = false;

			// Check the value
			if (!getValue()) {
				AlertTool.showNotification("Unpack Zip Service", exception, Duration.seconds(2),
						NotificationType.ERROR);
				done();
			} else {
				AlertTool.showNotification("Database Import", "Successfully imported the database!",
						Duration.seconds(2), NotificationType.INFORMATION);

				// Restart XR3Player
				Main.updateScreen.getProgressBar().progressProperty().unbind();
				Main.updateScreen.getProgressBar().setProgress(-1);
				Main.updateScreen.getLabel().setText("Restarting....");
				MainExit.restartTheApplication(false);
			}

		});

		setOnFailed(failed -> {
			done();
			AlertTool.showNotification("Database Import", exception, Duration.seconds(2), NotificationType.ERROR);
		});

		setOnCancelled(c -> {
			done();
			AlertTool.showNotification("Database Import", exception, Duration.seconds(2), NotificationType.ERROR);

		});
	}

	/**
	 * Done.
	 */
	private static void done() {
		Main.updateScreen.setVisible(false);
		Main.updateScreen.getProgressBar().progressProperty().unbind();
	}

	/**
	 * Import the database from the zip folder.
	 *
	 * @param zipFolder the zip folder
	 */
	public void importDataBase(String zipFolder) {
		inputZip = zipFolder;
		reset();

		// Set Cancel Action
		Main.updateScreen.getCancelButton().setDisable(true);

		// Restart the Service
		restart();

	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<>() {
			@Override
			protected Boolean call() throws Exception {

				// Previous versions < Update 56 of XR3Player will be broken after this update
				// :( future is future

				// ----------------------Search for the signature
				// file-------------------------------
				try (ZipFile zis = new ZipFile(inputZip)) {

					// signature file
					String signatureFile = DatabaseTool.getDatabaseSignatureFile().getName();
					boolean found = zis.getEntry(signatureFile) != null;

					// Found it?
					if (!found) {
						exception = "Selected folder is not XR3Player database...";
						return false;
					}

				} catch (IOException ex) {
					exception = ex.getMessage();
					Main.logger.log(Level.WARNING, "", ex);
					return false;
				}

				// ----------------------Found the signature file so we can
				// procceeed-------------------------------

				// Close all the connections with database
				if (Main.dbManager != null)
					Main.dbManager.manageConnection(Operation.CLOSE);

				// Delete the previous database
				IOAction.deleteFile(new File(DatabaseTool.getAbsoluteDatabasePathPlain()));

				// ---------------------Move on Importing the
				// Database-----------------------------------------------

				// get the zip file content
				try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputZip))) {

					// create output directory is not exists
					File folder = new File(outPutFolder);
					if (!folder.exists())
						folder.mkdir();

					// get the zipped file list entry
					ZipEntry ze = zis.getNextEntry();

					// Count entries
					double counter = 0, total = 0;
					try (ZipFile zip = new ZipFile(inputZip)) {
						total = zip.size(); // total entries of the zip file
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					// We don't want division by 0
					if (total == 0)
						total = 1;

					// Start
					for (byte[] buffer = new byte[1024]; ze != null; ) {

						String fileName = ze.getName();
						File newFile = new File(outPutFolder + File.separator + fileName);

						// Refresh the dataLabel text
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("In:" + newFile.getName()));

						// create all non exists folders else you will hit FileNotFoundException for
						// compressed folder
						new File(newFile.getParent()).mkdirs();

						// Create File OutputStream
						try (FileOutputStream fos = new FileOutputStream(newFile)) {

							// Copy byte by byte
							int len;
							while ((len = zis.read(buffer)) > 0)
								fos.write(buffer, 0, len);

						} catch (IOException ex) {
							exception = ex.getMessage();
							Main.logger.log(Level.WARNING, "", ex);
						}

						// Get next entry
						ze = zis.getNextEntry();

						// Update the progress
						updateProgress(++counter / total, 1);
					}

					zis.closeEntry();
					zis.close();

				} catch (IOException ex) {
					exception = ex.getMessage();
					Main.logger.log(Level.WARNING, "", ex);
					return false;
				}

				return true;
			}

		};
	}
}
