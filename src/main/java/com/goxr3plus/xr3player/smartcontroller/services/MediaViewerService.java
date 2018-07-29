package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.MediaViewer;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

public class MediaViewerService extends Service<Void> {
	
	private final SmartController smartController;
	
	/**
	 * Constructor.
	 */
	public MediaViewerService(SmartController smartController) {
		this.smartController = smartController;
		
		setOnSucceeded(s -> done());
		setOnFailed(f -> done());
		setOnCancelled(c -> done());
	}
	
	public void startService() {
		
		//Delete all items
		smartController.getMediaViewer().deleteAllItems(true);
		
		//Restart
		restart();
	}
	
	/**
	 * Done.
	 */
	// Work done
	public void done() {
		
		try {
			//	double[] positions = smartController.getViewerSplitPane().getDividerPositions();
			//	positions[0] += 0.002;
			//	smartController.getViewerSplitPane().setDividerPositions(positions);
			
			//	positions[0] -= 0.005;
			//	smartController.getViewerSplitPane().setDividerPositions(positions);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				
				// counter
				int[] counter = { 0 };
				int total = smartController.getItemsObservableList().size();
				
				//Update Message
				updateMessage("Generating album views");
				
				//Update the Viewer
				List<Node> mediaList = smartController.getItemsObservableList().stream().map(media -> {
					
					//Update Progress
					updateProgress(++counter[0], total);
					
					//Create MediViewer
					MediaViewer mediaViewer = new MediaViewer(media);
					
					try {
						//Image can be null , remember.
						Image image = media.getAlbumImage();
						
						//Image exists?
						if (image != null) {
							mediaViewer.getImageView().setImage(image);
							mediaViewer.getNameLabel().setVisible(false);
						} else {
							mediaViewer.getNameLabel().setText(media.getTitle());
							mediaViewer.getNameLabel().setVisible(true);
						}
						
						// --Drag Detected
						mediaViewer.setOnDragDetected(event -> {
							
							/* allow copy transfer mode */
							Dragboard db = mediaViewer.startDragAndDrop(TransferMode.COPY, TransferMode.LINK);
							
							/* put a string on drag board */
							ClipboardContent content = new ClipboardContent();
							
							// PutFiles
							content.putFiles(Arrays.asList(new File(media.getFilePath())));
							
							//Set DragView
							JavaFXTools.setDragView(db, media.getAlbumImageFit(100, 100), media.getTitle());
							
							//Set Content
							db.setContent(content);
							
							event.consume();
						});
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
					return mediaViewer;
				}).collect(Collectors.toList());
				
				//Run on JavaFX Thread
				Platform.runLater(() -> smartController.getMediaViewer().addMultipleItems(mediaList));
				
				return null;
			}
		};
	}
}
