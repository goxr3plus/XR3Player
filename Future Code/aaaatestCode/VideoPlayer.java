/*
 * 
 */
package aa_test_code_for_future_updates;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import tools.InfoTool;

/**
 * The Class VideoPlayer.
 */
public class VideoPlayer extends StackPane {

    /** The media. */
    private Media media = new Media(new File("").toURI().toString());

    /** The video player. */
    private MediaPlayer videoPlayer = new MediaPlayer(media);

    /** The video view. */
    public MediaView videoView = new MediaView(videoPlayer);

    /**
     * Instantiates a new video player.
     */
    public VideoPlayer() {

	videoView.setStyle("-fx-background-color:black;");

	getChildren().addAll(videoView, makeControls());

	// Listener for drag&&drop
	setOnDragOver(drag -> {
	    drag.acceptTransferModes(TransferMode.LINK);
	});

	setOnDragDropped(drag -> {
	    for (File file : drag.getDragboard().getFiles()) {
		if (InfoTool.isVideoSupported(file.getAbsolutePath())) {
		    System.out.println(" Media Replaced");
		    videoPlayer.getMedia().getSource().replaceAll(media.getSource(), file.toURI().toString());
		}

	    }
	});

	videoPlayer.setOnError(new Runnable() {

	    @Override
	    public void run() {
		System.out.println("Error occured on Video Player");
		System.out.println(videoPlayer.getError().getMessage());
	    }

	});

    }

    /**
     * The Controls of the Video Player
     * 
     * @return A GridPane with the Controls
     */
    public GridPane makeControls() {

	GridPane grid = new GridPane();
	grid.setVgap(5);
	grid.setHgap(5);
	grid.setPadding(new Insets(5, 5, 5, 5));

	// TimeSlider
	Slider timeSlider = new Slider(0, 0, 0);
	timeSlider.setPrefWidth(400);
	timeSlider.setOnMouseDragged(drag -> {
	    videoPlayer.seek(Duration.seconds(timeSlider.getValue()));
	});
	grid.add(timeSlider, 0, 0);
	media.durationProperty().addListener(change -> {
	    timeSlider.setMax(media.getDuration().toSeconds());
	});
	videoPlayer.currentTimeProperty().addListener(c -> {
	    timeSlider.setValue(videoPlayer.getCurrentTime().toSeconds());
	});

	// PlayOrPause
	Button playOrPause = new Button("play/pause");
	playOrPause.setOnAction(action -> {
	    if (videoPlayer.getStatus() == Status.PLAYING)
		videoPlayer.pause();
	    else if (videoPlayer.getStatus() == Status.PAUSED || videoPlayer.getStatus() == Status.READY
		    || videoPlayer.getStatus() == Status.STOPPED)
		videoPlayer.play();
	});
	grid.add(playOrPause, 1, 0);

	// Stop
	Button stop = new Button("stop");
	stop.setOnAction(action -> {
	    if (videoPlayer.getStatus() == Status.PLAYING || videoPlayer.getStatus() == Status.PAUSED) {
		videoPlayer.stop();
		timeSlider.setValue(0);
	    }
	});
	grid.add(stop, 2, 0);

	return grid;
    }
}
