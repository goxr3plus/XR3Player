package smartcontroller.services;

import java.io.File;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

import application.Main;
import application.presenter.treeview.TreeItemFile;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import smartcontroller.enums.FilesExportMode;
import smartcontroller.media.Media;
import smartcontroller.modes.SmartControllerFoldersMode;

public class FoldersModeService extends Service<Void> {
	
	/** A private instance of the SmartController it belongs */
	private final SmartControllerFoldersMode smartControllerFoldersMode;
	
	FilesExportMode filesToExport = FilesExportMode.EVERYTHING_ON_PLAYLIST;
	
	private int count;
	private int total;
	
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
						
					} else if (filesToExport == FilesExportMode.EVERYTHING_ON_PLAYLIST) { // EVERYTHING_ON_PLAYLIST
						
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
					Platform.runLater(() -> set.forEach(item -> {
						//System.out.println(item);
						//smartControllerFoldersMode.getRoot().getChildren().forEach(treeItem->{
						
						//});
						TreeItemFile treeItem = new TreeItemFile(item);
						treeItem.setValue(treeItem.getValue() + " [ " + item + " ]");
						smartControllerFoldersMode.getRoot().getChildren().add(treeItem);
						
					}));
					
				} catch (Exception ex) {
					ex.printStackTrace();
					//return false;
				}
				
				return null;
			}
			
		};
	}
	
}
