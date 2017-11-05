package main.java.com.goxr3plus.xr3player.smartcontroller.tags.mp3;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class MP3Tagger {
	
	/**
	 * Using this Service as an external Thread which updates the Information
	 * based on the selected Media
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public class UpdateInformationService extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					return null;
				}
			};
		}
		
	}
}
