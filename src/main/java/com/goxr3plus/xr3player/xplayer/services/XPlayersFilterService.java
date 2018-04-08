package main.java.com.goxr3plus.xr3player.xplayer.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.xplayer.presenter.XPlayerController;
import main.java.goxr3plus.javastreamplayer.stream.ThreadFactoryWithNamePrefix;

public class XPlayersFilterService {
	
	/**
	 * If is true then the Thread has stopped , so i restart it again...
	 */
	private final BooleanProperty threadStopped = new SimpleBooleanProperty(false);
	
	/**
	 * This executor service is used in order the playerState events to be executed in an order
	 */
	private final ExecutorService executors = Executors.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix("XPlayers Filter Service "));
	
	/**
	 * Start the Thread.
	 *
	 */
	public void start() {
		Runnable runnable = () -> {
			try {
				Platform.runLater(() -> threadStopped.set(false));
				
				//Run forever , except if i interrupt it ;)
				for (;; Thread.sleep(1000))
					Platform.runLater(() -> Main.xPlayersList.getList().stream()
							//Extra filtering
							.filter(xPlayerController -> {
								//If extended pass
								if (xPlayerController.isExtended())
									return true;
								//Or else check more through
								else {
									//For player 0
									if (xPlayerController.getKey() == 0 && Main.topBar.isTabSelected(Main.topBar.getMainModeTab()))
										return true;
									//For other players
									else if (xPlayerController.getKey() != 0 && Main.topBar.isTabSelected(Main.topBar.getDjModeTab()))
										return true;
								}
								return false;
							}).forEach(xPlayerController -> {
								
								//-------Set the appropriate image for the PlayPauseButton based on the status of the Player
								( (ImageView) xPlayerController.getPlayPauseButton().getGraphic() )
										.setImage(xPlayerController.getxPlayer().isPlaying() ? XPlayerController.pauseImage : XPlayerController.playImage);
								
								//-------Set the appropriate image for the PlayPauseButton based on the status of the Player
								( (ImageView) xPlayerController.getSmPlayPauseButton().getGraphic() )
										.setImage(xPlayerController.getxPlayer().isPlaying() ? XPlayerController.pauseImage : XPlayerController.playImage);
								
								// ---------Liked or disliked--------?
								xPlayerController.changeEmotionImage(Main.emotionListsController.getEmotionForMedia(xPlayerController.getxPlayerModel().songPathProperty().get()));
								
							}));
			} catch (Exception ex) {
				Main.logger.log(Level.INFO, "", ex);
			} finally {
				System.out.println("XPlayers Filter Service Thread exited!!!");
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
	
}
