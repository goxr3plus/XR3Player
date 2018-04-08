/*
 * 
 */
package main.java.com.goxr3plus.xr3player.chromium.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.chromium.WebBrowserController;
import main.java.com.goxr3plus.xr3player.chromium.WebBrowserTabController;
import main.java.goxr3plus.javastreamplayer.stream.ThreadFactoryWithNamePrefix;

/**
 * The Class FileFilterThread.
 */
public class ChromiumUpdaterService {
	
	public static final Image mutedImage = InfoTool.getImageFromResourcesFolder("speakerMuted.png");
	public static final Image unMutedImage = InfoTool.getImageFromResourcesFolder("speaker.png");
	
	/**
	 * The name of the running Thread/s
	 */
	private final String threadName = "Chromium Updater Service ";
	
	/**
	 * If is true then the Thread has stopped , so i restart it again...
	 */
	private final BooleanProperty threadStopped = new SimpleBooleanProperty(false);
	
	/**
	 * This executor service is used in order the playerState events to be executed in an order
	 */
	private final ExecutorService executors = Executors.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix(threadName));
	
	/**
	 * The ChromiumWebBrowser controller
	 */
	private final WebBrowserController webBrowserController;
	
	/**
	 * Constructor
	 * 
	 * @param webBrowserController
	 */
	public ChromiumUpdaterService(WebBrowserController webBrowserController) {
		this.webBrowserController = webBrowserController;
	}
	
	/**
	 * Start the Thread.
	 *
	 */
	public void start() {
		Runnable runnable = () -> {
			try {
				Platform.runLater(() -> threadStopped.set(false));
				
				//Run forever , except if i interrupt it ;)
				for (;; Thread.sleep(1000)) {
					
					if (Main.topBar.isTabSelected(Main.topBar.getWebModeTab())) {
						checkTabsSound();
						//System.out.println(threadName + " entered if statement");
					}
					
				}
				
			} catch (Exception ex) {
				Main.logger.log(Level.INFO, "", ex);
			} finally {
				System.out.println(threadName + " exited !!!!");
				Platform.runLater(() -> threadStopped.set(true));
			}
		};
		executors.execute(runnable);
		
		//---Add this listener in case something bad happens to the thread above
		threadStopped.addListener((observable , oldValue , newValue) -> {
			//Restart it if it has stopped
			if (newValue)
				executors.execute(runnable);
		});
	}
	
	/**
	 * Checks the Tabs if the are muted , unmuted etc
	 */
	public void checkTabsSound() {
		webBrowserController.getTabPane().getTabs().forEach(tab -> {
			WebBrowserTabController tabController = (WebBrowserTabController) tab.getContent();
			
			//Is Audio Playing?
			if (tabController.getBrowser().isAudioPlaying()) {
				int width = 32;
				int height = 25;
				tabController.getAudioButton().setMinSize(width, height);
				tabController.getAudioButton().setPrefSize(width, height);
				tabController.getAudioButton().setMaxSize(width, height);
				tabController.getAudioButton().setVisible(true);
				
				//Is Audio Muted or Unmuted?
				( (ImageView) tabController.getAudioButton().getGraphic() ).setImage(tabController.getBrowser().isAudioMuted() ? mutedImage : unMutedImage);
			} else {
				int maxSize = 0;
				tabController.getAudioButton().setMinSize(maxSize, maxSize);
				tabController.getAudioButton().setPrefSize(maxSize, maxSize);
				tabController.getAudioButton().setMaxSize(maxSize, maxSize);
				tabController.getAudioButton().setVisible(false);
			}
			
			//Site is Loading
			tabController.getProgressIndicatorStackPane().setManaged(tabController.getBrowser().isLoading());
			tabController.getProgressIndicatorStackPane().setVisible(tabController.getBrowser().isLoading());
			
		});
	}
	
}
