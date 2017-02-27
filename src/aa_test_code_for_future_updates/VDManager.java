package aa_test_code_for_future_updates;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VDManager extends Application {

public static void main(String[] args) {
    launch(args);
}

@Override
public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Media");
    Group root = new Group();
    Media media = new Media(new File("C:\\Users\\GOXR3PLUS\\Desktop\\Twerking Dog.mp4").toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setCycleCount(50000);
    mediaPlayer.setRate(4);
    mediaPlayer.play();

    MediaView mediaView = new MediaView(mediaPlayer);

    root.getChildren().add(mediaView);
    Scene scene = new Scene(root,500,500,Color.WHITE);
    primaryStage.setScene(scene);
    primaryStage.show();
}

}