package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.IOTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

/**
 * Manages the input operations of the SmartController.
 *
 * @author GOXR3PLUS
 */
public class InputService extends Service<Void> {
	
	/** The list. */
	private List<File> list;
	
	/** The job. */
	private String job;
	
	/** The counter. */
	private int progress;
	
	/** The total files. */
	private int totalFiles;
	
	private final SmartController smartController;
	
	/**
	 * Constructor.
	 */
	public InputService(SmartController smartController) {
		this.smartController = smartController;
		
		setOnSucceeded(s -> done());
		setOnCancelled(c -> done());
		setOnFailed(c -> done());
	}
	
	/**
	 * Start the Service.
	 *
	 * @param list1
	 *            the list
	 */
	public void start(List<File> list1) {
		//Check if can enter...
		if (!Platform.isFxApplicationThread() || !smartController.isFree(true) || isRunning())
			return;
		
		// Security
		job = "upload from system";
		
		// We need only directories or media files
		this.list = list1.stream()
				//Find real path for symbolic links etc
				.map(file -> new File(IOTool.getRealPathFromFile(file.getAbsolutePath()).getFileAbsolutePath()))
				//Filter only the files we want
				.filter(file -> file.isDirectory() || ( file.isFile() && InfoTool.isAudioSupported(file.getAbsolutePath()) ))
				//Collect everything to a list
				.collect(Collectors.toList());
		smartController.depositWorking = true;
		
		// System.out.println(this.list)
		
		//Clear the text of imformation text field
		smartController.getInformationTextArea().clear();
		
		// Binds
		smartController.getIndicatorVBox().visibleProperty().bind(runningProperty());
		smartController.getIndicator().progressProperty().bind(progressProperty());
		smartController.getCancelButton().setDisable(false);
		smartController.getCancelButton().setText("Counting...");
		smartController.getCancelButton().setOnAction(e -> {
			super.cancel();
			smartController.getCancelButton().setDisable(true);
		});
		
		// ....
		reset();
		start();
		
	}
	
	/**
	 * When the work is done.
	 */
	private void done() {
		list = null;
		smartController.unbind();
		smartController.getCancelButton().setDisable(true);
		smartController.depositWorking = false;
		smartController.getLoadService().startService(true, true, true);
		smartController.getFoldersMode().recreateTree();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			/*
			 * (non-Javadoc)
			 * @see javafx.concurrent.Task#call()
			 */
			@Override
			protected Void call() throws Exception {
				
				//Keep Important Variables here
				totalFiles = progress = 0;
				String date = InfoTool.getCurrentDate() , time = InfoTool.getLocalTime();
				
				// Update informationTextArea
				Platform.runLater(() -> smartController.getInformationTextArea().appendText("\nCounting files from ....\n"));
				
				//Initialize the prepared statement
				try (PreparedStatement preparedInsert = Main.dbManager.getConnection()
						.prepareStatement("INSERT OR IGNORE INTO '" + smartController.getDataBaseTableName() + "' (PATH,STARS,TIMESPLAYED,DATE,HOUR) VALUES (?,?,?,?,?)")) {
					
					// Start the insert work
					if ("upload from system".equals(job)) {
						
						//Count the total files to be inserted
						for (File file : list) {
							
							//File exists?
							if (!file.exists())
								continue;
							
							// Update informationTextArea
							Platform.runLater(() -> smartController.getInformationTextArea().appendText( ( !file.isDirectory() ? "File" : "Folder" ) + ": " + file.getName()));
							
							int previousTotal = totalFiles;
							// File or Folder exists?
							if (isCancelled())
								break;
							
							totalFiles += countFiles(file);
							
							// Update informationTextArea
							Platform.runLater(() -> smartController.getInformationTextArea().appendText("\n\t-> Total: [ " + ( totalFiles - previousTotal ) + " ]\n"));
							
						}
						
						// System.out.println("Total Files are->" + totalFiles)
						
						//Calculate the batch size
						//			if (totalFiles < 20_000)
						//			    batchSize = 1000;
						//			else if (totalFiles < 100_000)
						//			    batchSize = 5000;
						//			else
						//			    batchSize = 10_000;
						//			
						
						//			batchcount = 0;
						
						// Update informationTextArea and cancel button
						Platform.runLater(() -> {
							smartController.getCancelButton().setText("Inserting...");
							smartController.getInformationTextArea().appendText("\nInserting: [ " + totalFiles + " ] Files...\n");
						});
						
						//Add all Files absolute paths to the database table
						for (File file : list)
							if (file.exists() && !isCancelled())
								try {
									Files.walkFileTree(Paths.get(file.getPath()), new HashSet<FileVisitOption>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE,
											new SimpleFileVisitor<Path>() {
												@Override
												public FileVisitResult visitFile(Path filePath , BasicFileAttributes attrs) throws IOException {
													
													// System.out.println("Adding...."+s.toString())
													
													// supported?
													if (InfoTool.isAudioSupported(filePath + ""))
														insertMedia(filePath + "", 0, 0, date, time, preparedInsert);
													
													// update progress
													updateProgress(++progress, totalFiles);
													
													return isCancelled() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
												}
												
												@Override
												public FileVisitResult visitFileFailed(Path filePath , IOException e) throws IOException {
													System.err.printf("Visiting failed for %s\n", filePath);
													
													return FileVisitResult.SKIP_SUBTREE;
												}
												
												@Override
												public FileVisitResult preVisitDirectory(Path dir , BasicFileAttributes attrs) throws IOException {
													return isCancelled() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
												}
											});
								} catch (IOException e) {
									e.printStackTrace();
								}
					}
					
					saveInDataBase(preparedInsert);
					
				} catch (Exception ex) {
					ex.printStackTrace();
					//Platform.runLater(() -> ActionTool.showNotification("Error", ex.getMessage(), Duration.seconds(2), NotificationType.ERROR));
				}
				
				return null;
			}
			
			/**
			 * Save everything in database
			 */
			void saveInDataBase(PreparedStatement preparedInsert) {
				// Cancelled?
				if (isCancelled())
					return;
				
				//...
				updateProgress(-1, 0);
				final CountDownLatch latch = new CountDownLatch(1);
				Platform.runLater(() -> {
					smartController.getCancelButton().setDisable(true);
					smartController.getCancelButton().setText("Saving...");
					smartController.getInformationTextArea().appendText("Saving...");
					latch.countDown();
				});
				try {
					latch.await();
					
					//Insert the remaining
					preparedInsert.executeBatch();
					
					// Count how many items where added
					//--Below i need to know how many entries have been successfully added [ will be implemented better soon... :) ]
					// setTotalInDataBase((int) (getTotalInDataBase()
					//	    + Arrays.stream(preparedInsert.executeBatch()).filter(s -> s > 0).count()))
					final CountDownLatch latch2 = new CountDownLatch(1);
					Platform.runLater(() -> {
						smartController.setTotalInDataBase(0);
						latch2.countDown();
					});
					latch2.await();
					// Platform.runLater(() -> updateTotalLabel())
				} catch (SQLException | InterruptedException ex) {
					Main.logger.log(Level.WARNING, "", ex);
				}
				
			}
			
			/**
			 * Count files in a directory (including files in all sub directories)
			 * 
			 * @param directory
			 *            The Full path of the Directory
			 * @return Total number of files contained in this folder
			 */
			int countFiles(File dir) {
				int[] count = { 0 };
				
				//Folder exists?
				if (dir.exists())
					try {
						Files.walkFileTree(Paths.get(dir.getPath()), new HashSet<>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path filePath , BasicFileAttributes attrs) throws IOException {
								
								//System.out.println("It is symbolic link?"+Files.isSymbolicLink(file));
								
								if (InfoTool.isAudioSupported(filePath + ""))
									++count[0];
								
								return isCancelled() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
							}
							
							@Override
							public FileVisitResult visitFileFailed(Path filePath , IOException e) throws IOException {
								System.err.printf("Visiting failed for %s\n", filePath);
								
								return FileVisitResult.SKIP_SUBTREE;
							}
							
							@Override
							public FileVisitResult preVisitDirectory(Path dir , BasicFileAttributes attrs) throws IOException {
								return isCancelled() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				//System.out.println("Total Files=" + count[0]);
				return count[0];
			}
			
			/**
			 * Insert this song into the dataBase table
			 * 
			 * @param path
			 * @param stars
			 * @param timesPlayed
			 * @param dateCreated
			 * @param hourCreated
			 */
			void insertMedia(String path , double stars , int timesPlayed , String dateCreated , String hourCreated , PreparedStatement preparedInsert) {
				
				try {
					
					if (dateCreated == null || hourCreated == null)
						try {
							throw new Exception("DATE OR HOUR CREATED ARE NULL [LIBRARY INSERT SONG!]");
						} catch (Exception e) {
							e.printStackTrace();
						}
					
					// Save the Song in the appropriate database table
					preparedInsert.setString(1, path);
					preparedInsert.setDouble(2, stars);
					preparedInsert.setInt(3, timesPlayed);
					preparedInsert.setString(4, dateCreated);
					preparedInsert.setString(5, hourCreated);
					preparedInsert.addBatch();
				} catch (SQLException ex) {
					Main.logger.log(Level.WARNING, "", ex);
				}
				
			}
			
		};
	}
	
}
