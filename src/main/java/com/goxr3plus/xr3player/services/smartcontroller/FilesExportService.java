package com.goxr3plus.xr3player.services.smartcontroller;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.zeroturnaround.zip.ZipUtil;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.FileType;
import com.goxr3plus.xr3player.enums.FilesMode;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.Operation;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController.WorkOnProgress;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.utils.io.IOAction;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * Copy or Move items
 *
 * @author GOXR3PLUS
 *
 */
public class FilesExportService extends Service<Boolean> {

	private int count;
	private int total;
	private Operation operation;
	private FilesMode filesToExport;
	private FileType fileType;
	private List<File> targetDirectories;

	private final SmartController smartController;

	/**
	 * Constructor
	 */
	public FilesExportService(final SmartController smartController) {
		this.smartController = smartController;

		final EventHandler<WorkerStateEvent> e1 = s -> {
			done();
			AlertTool.showNotification("Success Message", operation + " successfully done for:\n\t" + smartController,
					Duration.millis(1500), NotificationType.SUCCESS);
		};
		setOnSucceeded(e1);
		setOnFailed(f -> {
			done();
			AlertTool.showNotification("Error Message", operation + " failed for:\n\t" + smartController,
					Duration.millis(1500), NotificationType.ERROR);
		});

		setOnCancelled(c -> {
			done();
			AlertTool.showNotification("Information Message", operation + " cancelled for:\n\t" + smartController,
					Duration.millis(1500), NotificationType.INFORMATION);
		});
	}

	/**
	 * Start the operation based on the given parameters
	 * 
	 * @param targetDirectories
	 * @param operation
	 */
	public void startOperation(final List<File> targetDirectories, final Operation operation,
			final FilesMode filesToExport, final FileType fileType) {
		if (isRunning() || !smartController.isFree(true))
			return;

		// Security
		smartController.workOnProgress = WorkOnProgress.EXPORTING_FILES;

		// Variables
		this.targetDirectories = targetDirectories;
		this.operation = operation;
		this.filesToExport = filesToExport;
		this.fileType = fileType;

		// Bindings
		smartController.getIndicatorVBox().visibleProperty().bind(runningProperty());
		smartController.getIndicator().progressProperty().bind(progressProperty());
		smartController.getDescriptionLabel().setText("Exporting...");
		smartController.getCancelButton().setDisable(false);
		smartController.getCancelButton().setOnAction(e -> {
			super.cancel();
			smartController.getCancelButton().setDisable(true);
		});

		// start
		this.reset();
		this.start();

	}

	/**
	 * Process has been done
	 */
	private void done() {
		smartController.workOnProgress = WorkOnProgress.NONE;
		smartController.getCancelButton().setDisable(true);
		smartController.unbind();
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<>() {
			@Override
			protected Boolean call() throws Exception {

				// For each given target directory
				targetDirectories.forEach(targetDirectory -> {
					if (isCancelled())
						return;

					// Update SmartController Description Label
					Platform.runLater(() -> smartController.getDescriptionLabel()
							.setText("Producing : " + IOInfo.getFileName(targetDirectory.getAbsolutePath())));

					try {

						// Create the targetDirectory
						if (fileType == FileType.DIRECTORY)
							targetDirectory.mkdir();

						// Keep a counter for the process
						count = 0;

						// ================Prepare based on the Files User want to Export=============

						if (filesToExport == FilesMode.CURRENT_PAGE) { // CURRENT_PAGE

							// Count total files that will be exported
							total = smartController.getItemsObservableList().size();
							Platform.runLater(() -> smartController.getDescriptionArea()
									.setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));

							// Stream
							final Stream<String> stream =
									// Is Filter Mode Selected?
									!smartController.getFiltersModeTab().isSelected()
											? smartController.getItemsObservableList().stream().map(Media::getFilePath)
											: smartController.getFiltersMode().getMediaTableViewer().getTableView()
											.getItems().stream().map(Media::getFilePath);

							// Step 2
							proceedStream(stream, targetDirectory);

						} else if (filesToExport == FilesMode.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE

							// Count total files that will be exported
							total = smartController.getNormalModeMediaTableViewer().getSelectionModel()
									.getSelectedItems().size();
							Platform.runLater(() -> smartController.getDescriptionArea()
									.setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));

							// Stream
							final Stream<String> stream =
									// Is Filter Mode Selected?
									(!smartController.getFiltersModeTab().isSelected())
											? smartController.getNormalModeMediaTableViewer().getSelectionModel()
											.getSelectedItems().stream().map(Media::getFilePath)
											: smartController.getFiltersMode().getMediaTableViewer().getTableView()
											.getSelectionModel().getSelectedItems().stream()
											.map(Media::getFilePath);

							// Step 2
							proceedStream(stream, targetDirectory);

						} else if (filesToExport == FilesMode.EVERYTHING_ON_PLAYLIST
								&& smartController.getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST

							// Count total files that will be exported
							total = smartController.getTotalInDataBase();
							Platform.runLater(() -> smartController.getDescriptionArea()
									.setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));

							// Stream
							try (ResultSet resultSet = Main.dbManager.getConnection().createStatement()
									.executeQuery("SELECT* FROM '" + smartController.getDataBaseTableName() + "'");) {

								// Make a list of all the items
								final List<String> list = new ArrayList<>();

								// Fetch the items from the database
								while (resultSet.next())
									if (isCancelled())
										break;
									else {
										list.add(resultSet.getString("PATH"));
									}

								// Step 2
								proceedStream(list.stream(), targetDirectory);

							} catch (final Exception ex) {
								Main.logger.log(Level.WARNING, "", ex);
							}

						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				});

				// Check if cancelled
				return !isCancelled();
			}

			/**
			 * Proceed futher to complete the Service
			 *
			 * @param stream
			 */
			private void proceedStream(final Stream<String> stream, final File targetDirectory) {
				// DIRECTORY?
				if (fileType == FileType.DIRECTORY) {
					// For each item on stream
					stream.forEach(filePath -> {
						if (isCancelled())
							stream.close();
						else {
							passItem(filePath, targetDirectory);

							// Update the progress
							updateProgress(++count, total);
						}
					});
				} else if (fileType == FileType.ZIP) {
					try {
						ZipUtil.packEntries(stream.map(File::new).toArray(File[]::new), targetDirectory, fileName -> {
							if (isCancelled())
								return null;

							// Append Text to Description Area
							Platform.runLater(() -> smartController.getDescriptionArea()
									.appendText("\n Compressing ->" + fileName));

							// Update the progress
							updateProgress(++count, total);

							return fileName;
						});
					} catch (final Exception ex) {
						// Check if access is denied
						if (ex.getMessage().contains(
								"(The process cannot access the file because it is being used by another process)"))
							AlertTool.showNotification("Error Message", "[ "
											+ IOInfo.getFileName(targetDirectory.getAbsolutePath()) + " ]\n"
											+ "The process cannot access the file because it is being used by another process",
									Duration.seconds(4), NotificationType.ERROR);
						ex.printStackTrace();
					}
				}
			}

			/**
			 * Pass current File absolute Path
			 *
			 * @param sourceFilePath [ The Source File Absolute Path]
			 */
			private void passItem(final String sourceFilePath, final File destinationFolder) {
				if (!new File(sourceFilePath).exists())
					return;

				// Useful
				final String fileName = IOInfo.getFileName(sourceFilePath);
				final String destination = destinationFolder + File.separator + fileName;

				// Go
				if (operation == Operation.COPY) {
					Platform.runLater(
							() -> smartController.getDescriptionArea().appendText("\n Copying ->" + fileName));
					IOAction.copy(sourceFilePath, destination);
				} else {
					Platform.runLater(() -> smartController.getDescriptionArea().appendText("\n Moving ->" + fileName));
					IOAction.move(sourceFilePath, destination);
				}
			}
		};
	}
}
