/*
 * 
 */
package com.goxr3plus.xr3player.services.chromium;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.goxr3plus.streamplayer.stream.ThreadFactoryWithNamePrefix;
import com.goxr3plus.xr3player.application.Main;
//import com.goxr3plus.xr3player.controllers.chromium.WebBrowserController;
//import com.goxr3plus.xr3player.controllers.chromium.WebBrowserTabController;
import com.goxr3plus.xr3player.controllers.general.TopBar.WindowMode;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * The Class FileFilterThread.
 */
public class ChromiumUpdaterService {

	/**
	 * The name of the running Thread/s
	 */
	private final String threadName = "Chromium Updater Service ";

	/**
	 * If is true then the Thread has stopped , so i restart it again...
	 */
	private final BooleanProperty threadStopped = new SimpleBooleanProperty(false);

	/**
	 * This executor service is used in order the playerState events to be executed
	 * in an order
	 */
	private final ExecutorService executors = Executors
			.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix(threadName));

	/**
	 * The ChromiumWebBrowser controller
	 */
//	private final WebBrowserController webBrowserController;

	/**
	 * Constructor
	 * 
	 * @param webBrowserController
	 */
//	public ChromiumUpdaterService(final WebBrowserController webBrowserController) {
//		this.webBrowserController = webBrowserController;
//	}

	/**
	 * Start the Thread.
	 *
	 */
	public void start() {
		final Runnable runnable = () -> {
			try {
				Platform.runLater(() -> threadStopped.set(false));

				// Run forever , except if i interrupt it ;)

				for (;; Thread.sleep(1000)) {

					if (Main.topBar.getWindowMode() == WindowMode.WEBMODE) {
						checkTabsSound();
						// System.out.println(threadName + " entered if statement")
					}

					// DropboxAuthenticationBrowser
					if (Main.dropBoxViewer.getAuthenticationBrowser().getWindow().isShowing()) {
//						Main.dropBoxViewer.getAuthenticationBrowser().getLoadingIndicator()
//								.setManaged(Main.dropBoxViewer.getAuthenticationBrowser().getBrowser().isLoading());
//						Main.dropBoxViewer.getAuthenticationBrowser().getLoadingIndicator()
//								.setVisible(Main.dropBoxViewer.getAuthenticationBrowser().getBrowser().isLoading());
					}

					// ----------------------------Check if volume is enabled
					// ---------------------------

					// Main Mode
					boolean muted = Main.xPlayersList.getXPlayerController(0).isMuteButtonSelected();
					Main.sideBar.getMainModeStackedFont().getChildren().get(1).setVisible(!muted);

					// DJ Mode
					muted = (Main.xPlayersList.getXPlayerController(1).isMuteButtonSelected())
							&& (Main.xPlayersList.getXPlayerController(2).isMuteButtonSelected());
					Main.sideBar.getDjModeStackedFont().getChildren().get(1).setVisible(!muted);

					// Browser Mode
//					final boolean notMuted = webBrowserController.getTabPane().getTabs().stream().filter(tab -> {
//						final WebBrowserTabController tabController = (WebBrowserTabController) tab.getContent();
//
//						// Is audio not muted?
//						return !tabController.getBrowser().isAudioMuted();
//					}).findFirst().isPresent();
//					Main.sideBar.getBrowserStackedFont().getChildren().get(1).setVisible(notMuted);

				}

			} catch (final Exception ex) {
//				Main.logger.log(Level.INFO, "", ex);
			} finally {
//				System.out.println(threadName + " exited !!!!");
				Platform.runLater(() -> threadStopped.set(true));
			}
		};
		executors.execute(runnable);

		// ---Add this listener in case something bad happens to the thread above
		threadStopped.addListener((observable, oldValue, newValue) -> {
			// Restart it if it has stopped
			if (newValue)
				executors.execute(runnable);
		});
	}

	/**
	 * Checks the Tabs if the are muted , unmuted etc
	 */
	public void checkTabsSound() {
//		webBrowserController.getTabPane().getTabs().forEach(tab -> {
//			final WebBrowserTabController tabController = (WebBrowserTabController) tab.getContent();
//
//			try {
//
//				// Is Audio Playing?
//				if (tabController.getBrowser().isAudioPlaying()) {
//					final int width = 32;
//					final int height = 25;
//					tabController.getAudioButton().setMinSize(width, height);
//					tabController.getAudioButton().setPrefSize(width, height);
//					tabController.getAudioButton().setMaxSize(width, height);
//					tabController.getAudioButton().setVisible(true);
//
//					// Is Audio Muted or unmuted?
//					if (tabController.getBrowser().isAudioMuted()) {
//						tabController.mutedImage.setVisible(true);
//						tabController.unmutedImage.setVisible(false);
//					} else {
//						tabController.mutedImage.setVisible(false);
//						tabController.unmutedImage.setVisible(true);
//					}
//				} else {
//					final int maxSize = 0;
//					tabController.getAudioButton().setMinSize(maxSize, maxSize);
//					tabController.getAudioButton().setPrefSize(maxSize, maxSize);
//					tabController.getAudioButton().setMaxSize(maxSize, maxSize);
//					tabController.getAudioButton().setVisible(false);
//				}
//
//				// Site is Loading
//				tabController.getProgressIndicatorStackPane().setManaged(tabController.getBrowser().isLoading());
//				tabController.getProgressIndicatorStackPane().setVisible(tabController.getBrowser().isLoading());
//			} catch (final Exception ex) {
//				ex.printStackTrace();
//			}
//		});
	}

}
