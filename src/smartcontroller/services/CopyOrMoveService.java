package smartcontroller.services;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import application.tools.ActionTool;
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
	private List<File> directories;
	
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
	 * Copying process
	 * 
	 * @param directories1
	 */
	public void startCopy(List<File> directories1) {
		if (isRunning() || !smartController.isFree(true))
			ActionTool.showNotification("Message", "Copy can't start!!", Duration.millis(2000), NotificationType.WARNING);
		else {
			this.directories = directories1;
			commonOperations(Operation.COPY);
		}
	}
	
	/**
	 * Moving process
	 * 
	 * @param directories1
	 */
	public void startMoving(List<File> directories1) {
		if (isRunning() || !smartController.isFree(true))
			ActionTool.showNotification("Message", "Moving can't start!!", Duration.millis(2000), NotificationType.WARNING);
		else {
			this.directories = directories1;
			commonOperations(Operation.MOVE);
		}
	}
	
	/**
	 * Common operations on (move and copy) processes
	 */
	private void commonOperations(Operation operation1) {
		this.operation = operation1;
		
		// The choosen directories
		destinationFolder = directories.get(0);
		directories.forEach(File::mkdir);
		
		// Bindings
		smartController.getRegion().visibleProperty().bind(runningProperty());
		smartController.getIndicator().progressProperty().bind(progressProperty());
		smartController.getCancelButton().setText("Exporting...");
		smartController.getCancelButton().setDisable(false);
		smartController.getCancelButton().setOnAction(e -> {
			super.cancel();
			smartController.getCancelButton().setDisable(true);
		});
		smartController.getInformationTextArea().setText("\n Exporting Media from PlayList....");
		
		// start
		this.reset();
		this.start();
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
					count = 0;
					// total = (int) observableList.stream().filter(button
					// -> ( (Audio) button ).isMarked()).count();
					total = smartController.getItemsObservableList().size();
					
					// Multiple Items have been selected
					if (total > 0) {
						// Stream
						Stream<Media> stream = smartController.getItemsObservableList().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								passItem(media);
								updateProgress(++count, total);
							}
						});
						
						// User has pressed right click or a shortcut so one
						// item has passed
					}
					// } else {
					// passItem(Main.songsContextMenu.getM);
					//
					// // updateProgress
					// updateProgress(1, 1);
					// }
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
				return true;
			}
			
			/**
			 * Pass the media to the controller
			 *
			 * @param media
			 */
			private void passItem(Media media) {
				
				filePath = ( (Audio) media ).getFilePath();
				
				if (!new File(filePath).exists())
					return;
				
				//Useful
				String source = filePath;
				String destination = destinationFolder + File.separator + media.getFileName();
				
				//Go
				if (operation == Operation.COPY) {
					Platform.runLater(() -> smartController.getInformationTextArea().appendText("\n Copying ->" + media.getFileName()));
					ActionTool.copy(source, destination);
				} else {
					Platform.runLater(() -> smartController.getInformationTextArea().appendText("\n Moving ->" + media.getFileName()));
					ActionTool.move(source, destination);
				}
			}
		};
	}
}
