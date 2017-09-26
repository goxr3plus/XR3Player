package smartcontroller.services;

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

import application.Main;
import application.presenter.treeview.TreeItemFile;
import application.tools.InfoTool;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import smartcontroller.enums.FilesExportMode;
import smartcontroller.enums.Genre;
import smartcontroller.media.Media;
import smartcontroller.modes.SmartControllerFoldersMode;

public class FoldersModeService extends Service<Void> {
	
	/** A private instance of the SmartController it belongs */
	private final SmartControllerFoldersMode smartControllerFoldersMode;
	
	FilesExportMode filesToExport = FilesExportMode.EVERYTHING_ON_PLAYLIST;
	
	private int count;
	private int total;
	private Thread thread;
	
	/**
	 * Constructor
	 * 
	 * @param smartController
	 */
	public FoldersModeService(SmartControllerFoldersMode smartControllerFoldersMode) {
		this.smartControllerFoldersMode = smartControllerFoldersMode;
		
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				//GO
				Set<String> set = new LinkedHashSet<>();
				
				try {
					
					//Total and Count = 0
					count = total = 0;
					
					//================Prepare based on the Files User want to Export=============
					
					if (filesToExport == FilesExportMode.CURRENT_PAGE) {  // CURRENT_PAGE
						
						//Count total files that will be exported
						total = smartControllerFoldersMode.getSmartController().getItemsObservableList().size();
						
						// Stream
						Stream<Media> stream = smartControllerFoldersMode.getSmartController().getItemsObservableList().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								//Add item path to the set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());
								
								//Update the progress
								updateProgress(++count, total);
								//System.out.println(count + " , " + total);
							}
						});
						
					} else if (filesToExport == FilesExportMode.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE
						
						//Count total files that will be exported
						total = smartControllerFoldersMode.getSmartController().getTableViewer().getSelectionModel().getSelectedItems().size();
						
						// Stream
						Stream<Media> stream = smartControllerFoldersMode.getSmartController().getTableViewer().getSelectionModel().getSelectedItems().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								//Add all the items to set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());
								
								//Update the progress
								updateProgress(++count, total);
								//System.out.println(count + " , " + total);
							}
						});
						
					} else if (filesToExport == FilesExportMode.EVERYTHING_ON_PLAYLIST && smartControllerFoldersMode.getSmartController().getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST
						
						//Count total files that will be exported
						total = smartControllerFoldersMode.getSmartController().getTotalInDataBase();
						
						// Stream
						String query = "SELECT* FROM '" + smartControllerFoldersMode.getSmartController().getDataBaseTableName() + "'";
						try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query);) {
							
							// Fetch the items from the database
							while (resultSet.next())
								if (isCancelled())
									break;
								else {
									//Add all the items to set
									set.add(new File(resultSet.getString("PATH")).getParentFile().getAbsolutePath());
									
									//Update the progress
									updateProgress(++count, total);
									//System.out.println(count + " , " + total);
								}
							
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
						}
						
					}
					
					//For each item on set
					Platform.runLater(() -> {
						
						//Add all the items
						set.forEach(item -> {
							TreeItemFile treeItem = new TreeItemFile(item);
							treeItem.setValue(treeItem.getValue());
							smartControllerFoldersMode.getRoot().getChildren().add(treeItem);
							
						});
						
						//Define if details label will be visible or not
						smartControllerFoldersMode.getDetailsLabel().setVisible(set.isEmpty());
						
						//Count how many files each folder has
						new Thread(() -> {
							//Trick to exit the Thread :)
							boolean[] exitThread = { false };
							
							smartControllerFoldersMode.getRoot().getChildren().forEach(treeItem -> {
								if (!exitThread[0]) {
									
									//Count the Files
									int countFiles = countFiles(new File( ( (TreeItemFile) treeItem ).getFullPath()));
									
									//If it returns -1 it mean get the hell out of here and stop this Thread
									if (countFiles == -1)
										exitThread[0] = true;
									
									//Run this later
									Platform.runLater(() -> treeItem.setValue(
											treeItem.getValue() + " (" + InfoTool.getNumberWithDots(countFiles) + ") " + " [ " + ( (TreeItemFile) treeItem ).getFullPath() + " ]"));
								}
							});
							
							System.out.println("FolderMode Counting Files Thread exited!");
						}).start();
						
					});
					
				} catch (Exception ex) {
					ex.printStackTrace();
					//return false;
				}
				
				return null;
			}
			
			/**
			 * Count files in a directory (including files in all sub directories)
			 * 
			 * @param directory
			 *            the directory to start in
			 * @return the total number of files
			 */
			int countFiles(File dir) {
				int[] count = { 0 };
				
				if (dir.exists())
					try {
						Files.walkFileTree(Paths.get(dir.getPath()), new HashSet<FileVisitOption>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE,
								new SimpleFileVisitor<Path>() {
									@Override
									public FileVisitResult visitFile(Path file , BasicFileAttributes attrs) throws IOException {
										
										//System.out.println("It is symbolic link?"+Files.isSymbolicLink(file));
										
										if (InfoTool.isAudioSupported(file + ""))
											++count[0];
										
										if (isCancelled() || Thread.interrupted()) {
											count[0] = -1;
											return FileVisitResult.TERMINATE;
										} else
											return FileVisitResult.CONTINUE;
									}
									
									@Override
									public FileVisitResult visitFileFailed(Path file , IOException e) throws IOException {
										System.err.printf("Visiting failed for %s\n", file);
										
										return FileVisitResult.SKIP_SUBTREE;
									}
									
									@Override
									public FileVisitResult preVisitDirectory(Path dir , BasicFileAttributes attrs) throws IOException {
										if (isCancelled() || Thread.interrupted()) {
											count[0] = -1;
											return FileVisitResult.TERMINATE;
										} else
											return FileVisitResult.CONTINUE;
									}
								});
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				//System.out.println("Total Files=" + count[0]);
				return count[0];
			}
			
		};
	}
	
}
