package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.util.stream.Collectors;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
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
				smartController.getMediaViewer().addMultipleItems(smartController.getItemsObservableList().stream().map(media -> {
					
					//Update Progress
					updateProgress(++counter[0], total);
					
					//Create MediViewer
					MediaViewer mediaViewer = new MediaViewer(media);
					
					try {
						//Image can be null , remember.
						Image image = media.getAlbumImage();
						
						if (image != null) {
							mediaViewer.getImageView().setImage(image);
							mediaViewer.getNameLabel().setVisible(false);
						} else {
							mediaViewer.getNameLabel().setText(media.getTitle());
							mediaViewer.getNameLabel().setVisible(true);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
					return mediaViewer;
				}).collect(Collectors.toList()));
				
				return null;
			}
		};
	}
}
