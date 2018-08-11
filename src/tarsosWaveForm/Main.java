package tarsosWaveForm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	WaveVisualization waveVisualization = new WaveVisualization(520, 32);
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			//Root
			BorderPane root = new BorderPane();
			root.setCenter(waveVisualization);
			root.boundsInLocalProperty().addListener(l -> {
				waveVisualization.setWidth(root.getWidth());
				waveVisualization.setHeight(root.getHeight());
			});
			
			//PrimaryStage
			primaryStage.setTitle("Dark Side");
			primaryStage.setOnCloseRequest(c -> System.exit(0));
			
			//Scene
			Scene scene = new Scene(root, 600, 150);
			primaryStage.setScene(scene);
			
			//Show
			primaryStage.show();
			
			//
			waveVisualization.getWaveService().startService("audioFiles/audio.mp3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
