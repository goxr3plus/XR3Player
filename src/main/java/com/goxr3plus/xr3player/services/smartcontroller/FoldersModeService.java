package com.goxr3plus.xr3player.services.smartcontroller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Control;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.FilesMode;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartControllerFoldersMode;
import com.goxr3plus.xr3player.controllers.systemtree.FileTreeItem;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.utils.general.ExtensionTool;

public class FoldersModeService extends Service<Void> {

	/** A private instance of the SmartController it belongs */
	private final SmartControllerFoldersMode smartControllerFoldersMode;

	private final FilesCounterService filesCounterService = new FilesCounterService();

	/**
	 * The selected files mode , based on user settings
	 */
	private FilesMode filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;

	/**
	 * Service Progress
	 */
	private int progress;
	/**
	 * Service Total Progress
	 */
	private int totalProgress;

	/**
	 * Constructor
	 * 
	 * @param smartControllerFoldersMode
	 */
	public FoldersModeService(SmartControllerFoldersMode smartControllerFoldersMode) {
		this.smartControllerFoldersMode = smartControllerFoldersMode;

	}

	@Override
	protected Task<Void> createTask() {
		return new Task<>() {
			@Override
			protected Void call() throws Exception {

				// Create a new LinkedHashSet
				Set<String> set = new LinkedHashSet<>();

				// Determine filesMode
				switch (((Control) Main.settingsWindow.getPlayListsSettingsController().getWhichFilesToShowGenerally()
						.getSelectedToggle()).getTooltip().getText()) {
					case "1":
						filesMode = FilesMode.SELECTED_MEDIA;
						break;
					case "2":
						filesMode = FilesMode.CURRENT_PAGE;
						break;
					case "3":
						filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;
						break;
					default:
						filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;
				}

				// Change Top Label Text
				Platform.runLater(() -> smartControllerFoldersMode.getTopLabel()
						.setText("Associated Folders Explorer -> " + filesMode.toString()));

				// Change Details Label Text
				Platform.runLater(() -> smartControllerFoldersMode.getDetailsLabel()
						.setText("No associated Folders found -> " + filesMode.toString()));

				try {

					// Total and Count = 0
					progress = totalProgress = 0;

					// ================Prepare based on the Files User want to Export=============

					if (filesMode == FilesMode.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE

						// Count total files that will be exported
						totalProgress = smartControllerFoldersMode.getSmartController().getNormalModeMediaTableViewer()
								.getSelectionModel().getSelectedItems().size();

						// Stream
						Stream<Media> stream = smartControllerFoldersMode.getSmartController()
								.getNormalModeMediaTableViewer().getSelectionModel().getSelectedItems().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								// Add all the items to set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());

								// Update the progress
								updateProgress(++progress, totalProgress);
							}
						});

					} else if (filesMode == FilesMode.CURRENT_PAGE
							|| smartControllerFoldersMode.getSmartController().getGenre() == Genre.SEARCHWINDOW) { // CURRENT_PAGE
						System.out.println("Entered for Search Window");

						// Count total files that will be exported
						totalProgress = smartControllerFoldersMode.getSmartController().getItemsObservableList().size();

						// Stream
						Stream<Media> stream = smartControllerFoldersMode.getSmartController().getItemsObservableList()
								.stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								// Add item path to the set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());

								// Update the progress
								updateProgress(++progress, totalProgress);
							}
						});

					} else if (filesMode == FilesMode.EVERYTHING_ON_PLAYLIST
							&& smartControllerFoldersMode.getSmartController().getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST

						// Count total files that will be exported
						totalProgress = smartControllerFoldersMode.getSmartController().getTotalInDataBase();

						// Stream
						String query = "SELECT(PATH) FROM '"
								+ smartControllerFoldersMode.getSmartController().getDataBaseTableName() + "'";
						try (ResultSet resultSet = Main.dbManager.getConnection().createStatement()
								.executeQuery(query)) {

							// Fetch the items from the database
							while (resultSet.next())
								if (isCancelled())
									break;
								else {
									// Add all the items to set
									set.add(new File(resultSet.getString("PATH")).getParentFile().getAbsolutePath());

									// Update the progress
									updateProgress(++progress, totalProgress);
								}

						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
						}

					}

					// For each item on set
					Platform.runLater(() -> {

						// Add all the items
						set.forEach(filePath -> {
							FileTreeItem treeItem = new FileTreeItem(filePath);
							treeItem.setValue(treeItem.getValue());

							// Add the item to the TreeView
							smartControllerFoldersMode.getRoot().getChildren().add(treeItem);

						});

						// Define if details label will be visible or not
						smartControllerFoldersMode.getDetailsLabel().setVisible(set.isEmpty());

						// Start FilesCounterService
						filesCounterService.restart();
					});

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return null;
			}

		};
	}

	/**
	 * Counts the files inside a folder or folders based on the implementation
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	private class FilesCounterService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {
			return new Task<>() {

				@Override
				protected Void call() throws Exception {

					// Append the total Files of each folder
					smartControllerFoldersMode.getRoot().getChildren().forEach(treeItem -> {
						int[] totalFiles = countFiles(new File(((FileTreeItem) treeItem).getAbsoluteFilePath()));
						String text = treeItem.getValue() + " [ " + totalFiles[1] + " / " + totalFiles[0] + " ]" + " [ "
								+ ((FileTreeItem) treeItem).getAbsoluteFilePath() + " ] ";

						Platform.runLater(() -> treeItem.setValue(text));
					});

					return null;
				}

				/**
				 * Count files in a directory (including files in all sub directories)
				 * @param dir The full path of the directory
				 * @return Position [0] Total number of files contained in this folder <br>
				 * Position [1] Total number of files contained in this folder && inside
				 * the Playlist Database <br>
				 */
				private int[] countFiles(File dir) {
					int[] count = {0, 0};

					// Folder exists?
					if (dir.exists())
						try {
							Files.walkFileTree(Paths.get(dir.getPath()),
									new HashSet<>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE,
									new SimpleFileVisitor<>() {
										@Override
										public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
												throws IOException {

											// System.out.println("It is symbolic link?"+Files.isSymbolicLink(file))

											if (ExtensionTool.isAudioSupported(file + ""))
												++count[0];
											if (smartControllerFoldersMode.getSmartController()
													.containsFile(file.toAbsolutePath().toString()))
												++count[1];

											if (isCancelled()) {
												return FileVisitResult.TERMINATE;
											} else
												return FileVisitResult.CONTINUE;
										}

										@Override
										public FileVisitResult visitFileFailed(Path file, IOException e)
												throws IOException {
											System.err.printf("Visiting failed for %s\n", file);

											return FileVisitResult.SKIP_SUBTREE;
										}

										@Override
										public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
												throws IOException {
											if (isCancelled()) {
												return FileVisitResult.TERMINATE;
											} else
												return FileVisitResult.CONTINUE;
										}
									});
						} catch (IOException e) {
							e.printStackTrace();
						}

					// System.out.println("Total Files=" + count[0])
					return count;
				}

			};

		}

	}

}
