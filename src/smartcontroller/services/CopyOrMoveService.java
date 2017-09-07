package smartcontroller.services;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.stream.Stream;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import smartcontroller.SmartController;
import smartcontroller.media.Audio;
import smartcontroller.media.Media;

/**
 * Copy or Move items
 *
 * @author GOXR3PLUS
 *
 */
public class CopyOrMoveService extends Service<Boolean> {
	
	private File destinationFolder;
	private int count;
	private int total;
	private String filePath;
	private Operation operation;
	private FilesToExport filesToExport;
	private List<File> targetDirectories;
	
	private final SmartController smartController;
	
	/**
	 * Constructor
	 */
	public CopyOrMoveService(SmartController smartController) {
		this.smartController = smartController;
		
		setOnSucceeded(s -> {
			done();
			ActionTool.showNotification("Message", operation + " successfully done for:\n\t" + smartController, Duration.millis(1500), NotificationType.SIMPLE);
		});
		
		setOnFailed(f -> {
			done();
			ActionTool.showNotification("Message", operation + " failed for:\n\t" + smartController, Duration.millis(1500), NotificationType.ERROR);
		});
		
		setOnCancelled(c -> done());
	}
	
	/**
	 * Start the operation based on the given parameters
	 * 
	 * @param targetDirectories
	 * @param operation
	 */
	public void startOperation(List<File> targetDirectories , Operation operation , FilesToExport filesToExport) {
		if (isRunning() || !smartController.isFree(true))
			ActionTool.showNotification("Message", "Export can't start", Duration.millis(2000), NotificationType.WARNING);
		else {
			this.targetDirectories = targetDirectories;
			this.operation = operation;
			this.filesToExport = filesToExport;
			
			// The choosen directories
			destinationFolder = targetDirectories.get(0);
			targetDirectories.forEach(File::mkdir);
			
			// Bindings
			smartController.getRegion().visibleProperty().bind(runningProperty());
			smartController.getIndicator().progressProperty().bind(progressProperty());
			smartController.getCancelButton().setText("Exporting...");
			smartController.getCancelButton().setDisable(false);
			smartController.getCancelButton().setOnAction(e -> {
				super.cancel();
				smartController.getCancelButton().setDisable(true);
			});
			
			// start
			this.reset();
			this.start();
		}
	}
	
	/**
	 * Process has been done
	 */
	private void done() {
		smartController.getCancelButton().setDisable(true);
		smartController.unbind();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			
			@Override
			protected Boolean call() throws Exception {
				
				try {
					//Keep a counter for the process
					count = 0;
					
					//================Prepare based on the Files User want to Export=============
					
					if (filesToExport == FilesToExport.CURRENT_PAGE) {  // CURRENT_PAGE
						
						//Count total files that will be exported
						total = smartController.getItemsObservableList().size();
						Platform.runLater(() -> smartController.getInformationTextArea().setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));
						
						// Stream
						Stream<Media> stream = smartController.getItemsObservableList().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								passItem(media.getFilePath());
								
								//Update the progress
								updateProgress(++count, total);
							}
						});
						
					} else if (filesToExport == FilesToExport.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE
						
						//Count total files that will be exported
						total = smartController.getTableViewer().getSelectionModel().getSelectedItems().size();
						Platform.runLater(() -> smartController.getInformationTextArea().setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));
						
						// Stream
						Stream<Media> stream = smartController.getTableViewer().getSelectionModel().getSelectedItems().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								passItem(media.getFilePath());
								
								//Update the progress
								updateProgress(++count, total);
							}
						});
						
					} else if (filesToExport == FilesToExport.EVERYTHING_ON_PLAYLIST) { // EVERYTHING_ON_PLAYLIST
						
						//Count total files that will be exported
						total = smartController.getTotalInDataBase();
						Platform.runLater(() -> smartController.getInformationTextArea().setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));
						
						// Stream
						String query = "SELECT* FROM '" + smartController.getDataBaseTableName() + "'";
						try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query);) {
							
							// Fetch the items from the database
							while (resultSet.next())
								if (isCancelled())
									break;
								else {
									passItem(resultSet.getString("PATH"));
									
									//Update the progress
									updateProgress(++count, total);
								}
							
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
						}
						
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
				return true;
			}
			
			/**
			 * Pass current File absolute Path
			 *
			 * @param sourceFilePath
			 *            [ The Source File Absolute Path]
			 */
			private void passItem(String sourceFilePath) {
				if (!new File(sourceFilePath).exists())
					return;
				
				//Useful
				String fileName = InfoTool.getFileName(sourceFilePath);
				String destination = destinationFolder + File.separator + fileName;
				
				//Go
				if (operation == Operation.COPY) {
					Platform.runLater(() -> smartController.getInformationTextArea().appendText("\n Copying ->" + fileName));
					ActionTool.copy(sourceFilePath, destination);
				} else {
					Platform.runLater(() -> smartController.getInformationTextArea().appendText("\n Moving ->" + fileName));
					ActionTool.move(sourceFilePath, destination);
				}
			}
		};
	}
}
