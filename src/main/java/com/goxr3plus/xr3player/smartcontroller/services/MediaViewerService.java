package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.util.stream.Collectors;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
			
				
				//Update the Viewer
				smartController.getMediaViewer().deleteAllItems();
				smartController.getMediaViewer().addMultipleItems(smartController.getItemsObservableList().stream().map(media -> {
					
					//Update Progress
					updateProgress(++counter[0], total);
					
					return new MediaViewer(media);				
				}).collect(Collectors.toList()));
				
				return null;
			}
		};
	}
}
