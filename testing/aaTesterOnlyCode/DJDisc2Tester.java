package aaTesterOnlyCode;

import application.presenter.custom.DJFilter;
import application.presenter.custom.DJFilter.DJFilterCategory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DJDisc2Tester extends Application {
	
	StackPane stackPane = new StackPane();
	DJFilter djDisc2 = new DJFilter(200, 200, Color.DEEPSKYBLUE, 0.5, -1.0, 1.0, DJFilterCategory.EQUALIZER_FILTER);
	
	public void start(Stage primary) {
		
		//BorderPane
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(stackPane);
		
		//StackPane
		stackPane.setStyle("-fx-background-color:#303030");
		stackPane.getChildren().add(djDisc2);
		
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
		
		djDisc2.resizeDisc(size, size);
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
