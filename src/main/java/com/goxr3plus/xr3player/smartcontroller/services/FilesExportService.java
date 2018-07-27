package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.FilesMode;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

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
	private List<File> targetDirectories;
	
	private final SmartController smartController;
	
	/**
	 * Constructor
	 */
	public FilesExportService(SmartController smartController) {
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
	public void startOperation(List<File> targetDirectories , Operation operation , FilesMode filesToExport) {
		if (isRunning() || !smartController.isFree(true))
			ActionTool.showNotification("Message", "Export can't start", Duration.millis(2000), NotificationType.WARNING);
		else {
			this.targetDirectories = targetDirectories;
			this.operation = operation;
			this.filesToExport = filesToExport;
			
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
				
				//For each given target directory
				targetDirectories.forEach(targetDirectory -> {
					if (isCancelled())
						return;
					
					try {
						
						//Create the targetDirectory
						targetDirectory.mkdir();
						
						//Keep a counter for the process
						count = 0;
						
						//================Prepare based on the Files User want to Export=============
						
						if (filesToExport == FilesMode.CURRENT_PAGE) {  // CURRENT_PAGE
							
							//Count total files that will be exported
							total = smartController.getItemsObservableList().size();
							Platform.runLater(() -> smartController.getDescriptionArea().setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));
							
							// Stream
							Stream<Media> stream =
									//Is Filter Mode Selected?
									( !smartController.getFiltersModeTab().isSelected() ) ? smartController.getItemsObservableList().stream()
											: smartController.getFiltersMode().getMediaTableViewer().getTableView().getItems().stream();
							//For each item on stream
							stream.forEach(media -> {
								if (isCancelled())
									stream.close();
								else {
									passItem(media.getFilePath(), targetDirectory);
									
									//Update the progress
									updateProgress(++count, total);
								}
							});
							
						} else if (filesToExport == FilesMode.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE
							
							//Count total files that will be exported
							total = smartController.getNormalModeMediatTableViewer().getSelectionModel().getSelectedItems().size();
							Platform.runLater(() -> smartController.getDescriptionArea().setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));
							
							// Stream
							Stream<Media> stream =
									//Is Filter Mode Selected?
									( !smartController.getFiltersModeTab().isSelected() )
											? smartController.getNormalModeMediatTableViewer().getSelectionModel().getSelectedItems().stream()
											: smartController.getFiltersMode().getMediaTableViewer().getTableView().getSelectionModel().getSelectedItems().stream();
							//For each item on stream
							stream.forEach(media -> {
								if (isCancelled())
									stream.close();
								else {
									passItem(media.getFilePath(), targetDirectory);
									
									//Update the progress
									updateProgress(++count, total);
								}
							});
							
						} else if (filesToExport == FilesMode.EVERYTHING_ON_PLAYLIST && smartController.getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST
							
							//Count total files that will be exported
							total = smartController.getTotalInDataBase();
							Platform.runLater(() -> smartController.getDescriptionArea().setText("\n Exporting Media.... \n\t Total -> [ " + total + " ]\n"));
							
							// Stream
							String query = "SELECT* FROM '" + smartController.getDataBaseTableName() + "'";
							try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query);) {
								
								// Fetch the items from the database
								while (resultSet.next())
									if (isCancelled())
										break;
									else {
										passItem(resultSet.getString("PATH"), targetDirectory);
										
										//Update the progress
										updateProgress(++count, total);
									}
								
							} catch (Exception ex) {
								Main.logger.log(Level.WARNING, "", ex);
							}
							
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
				
				//Check if cancelled
				if (isCancelled())
					return false;
				else
					return true;
			}
			
			/**
			 * Pass current File absolute Path
			 *
			 * @param sourceFilePath
			 *            [ The Source File Absolute Path]
			 */
			private void passItem(String sourceFilePath , File destinationFolder) {
				if (!new File(sourceFilePath).exists())
					return;
				
				//Useful
				String fileName = InfoTool.getFileName(sourceFilePath);
				String destination = destinationFolder + File.separator + fileName;
				
				//Go
				if (operation == Operation.COPY) {
					Platform.runLater(() -> smartController.getDescriptionArea().appendText("\n Copying ->" + fileName));
					ActionTool.copy(sourceFilePath, destination);
				} else {
					Platform.runLater(() -> smartController.getDescriptionArea().appendText("\n Moving ->" + fileName));
					ActionTool.move(sourceFilePath, destination);
				}
			}
		};
	}
}
