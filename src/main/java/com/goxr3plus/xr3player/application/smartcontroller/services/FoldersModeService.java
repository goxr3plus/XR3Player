package main.java.com.goxr3plus.xr3player.application.smartcontroller.services;

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
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.treeview.FileTreeItem;
import main.java.com.goxr3plus.xr3player.application.smartcontroller.enums.FilesMode;
import main.java.com.goxr3plus.xr3player.application.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.application.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.application.smartcontroller.modes.SmartControllerFoldersMode;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class FoldersModeService extends Service<Void> {
	
	/** A private instance of the SmartController it belongs */
	private final SmartControllerFoldersMode smartControllerFoldersMode;
	
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
	
	//A Special Thread used to count the Files of each Folder
	//private volatile Thread countingThread;
	//private volatile boolean stopCountingThread;
	
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
				
				//Create a new LinkedHashSet
				Set<String> set = new LinkedHashSet<>();
				
				//				//Stop the Previous Counting Thread if it is running
				//				stopCountingThread = true;
				//				
				//				//Check if counting Thread is still alive and wait until it finishes
				//				if (countingThread != null && stopCountingThread) {
				//					//System.out.println("Entered if");
				//					while (countingThread.isAlive())
				//						//System.out.println("Counting Thread is alive!");
				//						try {
				//							Thread.sleep(50);
				//						} catch (InterruptedException e) {
				//							e.printStackTrace();
				//						}
				//					
				//				}
				
				try {
					
					//Total and Count = 0
					progress = totalProgress = 0;
					
					//================Prepare based on the Files User want to Export=============
					
					if (filesMode == FilesMode.CURRENT_PAGE) {  // CURRENT_PAGE
						
						//Count total files that will be exported
						totalProgress = smartControllerFoldersMode.getSmartController().getItemsObservableList().size();
						
						// Stream
						Stream<Media> stream = smartControllerFoldersMode.getSmartController().getItemsObservableList().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								//Add item path to the set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());
								
								//Update the progress
								updateProgress(++progress, totalProgress);
							}
						});
						
					} else if (filesMode == FilesMode.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE
						
						//Count total files that will be exported
						totalProgress = smartControllerFoldersMode.getSmartController().getTableViewer().getSelectionModel().getSelectedItems().size();
						
						// Stream
						Stream<Media> stream = smartControllerFoldersMode.getSmartController().getTableViewer().getSelectionModel().getSelectedItems().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								//Add all the items to set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());
								
								//Update the progress
								updateProgress(++progress, totalProgress);
							}
						});
						
					} else if (filesMode == FilesMode.EVERYTHING_ON_PLAYLIST && smartControllerFoldersMode.getSmartController().getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST
						
						//Count total files that will be exported
						totalProgress = smartControllerFoldersMode.getSmartController().getTotalInDataBase();
						
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
									updateProgress(++progress, totalProgress);
								}
							
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
						}
						
					}
					
					//For each item on set
					Platform.runLater(() -> {
						
						//Add all the items
						set.forEach(filePath -> {
							FileTreeItem treeItem = new FileTreeItem(filePath);
							treeItem.setValue(treeItem.getValue() + " [ " + filePath + " ] ");
							
							//Add the item to the TreeView
							smartControllerFoldersMode.getRoot().getChildren().add(treeItem);
							
						});
						
						//Define if details label will be visible or not
						smartControllerFoldersMode.getDetailsLabel().setVisible(set.isEmpty());
						
						//---------------TO BE IMPLEMENTED IN FUTURE UPDATES :)------------------
						//						//Count how many files each folder has
						//						countingThread = new Thread(() -> {
						//							stopCountingThread = false;
						//							int[] innerCounter = { 0 };
						//							
						//							//Go for each children of the TreeView
						//							set.forEach(filePath -> {
						//								if (!stopCountingThread) {
						//									
						//									//Count the Files
						//									int countFiles = countFiles(new File( ( filePath )));
						//									
						//									//If it returns -1 it mean get the hell out of here and stop this Thread
						//									if (countFiles == -1) {
						//										stopCountingThread = true;
						//										return;
						//									}
						//									
						//									//Get the TreeItem
						//									//System.out.println("Max :" + smartControllerFoldersMode.getRoot().getChildren().size() + " Current : " + innerCounter[0]);
						//									FileTreeItem treeItem = (FileTreeItem) smartControllerFoldersMode.getRoot().getChildren().get(innerCounter[0]);
						//									++innerCounter[0];
						//									
						//									//Run this later
						//									Platform.runLater(
						//											() -> treeItem.setValue(treeItem.getValue() + " (" + InfoTool.getNumberWithDots(countFiles) + ") " + " [ " + filePath + " ]"));
						//									
						//								}
						//							});
						//							
						//							//Log the exit of Thread
						//							Main.logger.log(Level.WARNING, "FolderMode Counting Files Thread exited!\n");
						//						});
						//						
						//						//Daemon false and start it
						//						countingThread.setDaemon(false);
						//						countingThread.start();
						
					});
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return null;
			}
			
			/**
			 * Count files in a directory (including files in all sub directories)
			 * 
			 * @param directory
			 *            The full path of the directory
			 * @return Total number of files contained in this folder
			 */
			int countFiles(File dir) {
				int[] count = { 0 };
				
				//Folder exists?
				if (dir.exists())
					try {
						Files.walkFileTree(Paths.get(dir.getPath()), new HashSet<>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path file , BasicFileAttributes attrs) throws IOException {
								
								//System.out.println("It is symbolic link?"+Files.isSymbolicLink(file))
								
								if (InfoTool.isAudioSupported(file + ""))
									++count[0];
								
								if (isCancelled()) {// || stopCountingThread) {
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
								if (isCancelled()) {//|| stopCountingThread) {
									count[0] = -1;
									return FileVisitResult.TERMINATE;
								} else
									return FileVisitResult.CONTINUE;
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				//System.out.println("Total Files=" + count[0])
				return count[0];
			}
			
		};
	}
	
}
