package aaTesterOnlyCode;

import application.presenter.custom.DJDisc;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DJDiscResizer extends Application {
	
	StackPane stackPane = new StackPane();
	DJDisc djDisc = new DJDisc(200, 200, Color.FIREBRICK, 50, 150);
	
	public void start(Stage primary) {
		
		//BorderPane
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(stackPane);
		
		//StackPane
		stackPane.setStyle("-fx-background-color:orange");
		stackPane.getChildren().add(djDisc);
		
		//Add Listeners the the StackPane
		//stackPane.boundsInParentProperty().addListener((observable , oldValue , newValue) -> reCalculateCanvasSize());
		stackPane.layoutBoundsProperty().addListener((observable , oldValue , newValue) -> reCalculateCanvasSize());
		//stackPane.heightProperty().addListener((observable , oldValue , newValue) -> reCalculateCanvasSize());
		
		//Scene
		Scene scene = new Scene(borderPane);
		primary.setScene(scene);
		primary.setOnCloseRequest(c -> System.exit(0));
		primary.show();
	}
	
	/**
	 * Recalculates the Canvas size to the preffered size
	 */
	public void reCalculateCanvasSize() {
		double size = Math.min(stackPane.getWidth(), stackPane.getHeight()) / 1.2;
		
		djDisc.resizeDisc(size, size);
		///djDisc.getCanvas().redraw();
		System.out.println("Redrawing canvas");
	}
	
	/**
	 * Main Method
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		launch(args);
	}
}
