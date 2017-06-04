package aaTesterOnlyCode;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import xplayer.visualizer.ResizableCanvas;

public class StackPaneResizer extends Application {
	
	ResizableCanvas canvas = new ResizableCanvas();
	StackPane stack2 = new StackPane(canvas);
	BorderPane borderPane = new BorderPane(stack2);
	StackPane stackPane = new StackPane(borderPane);
	
	
	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	public void start(Stage primary) {
		
		//BorderPane
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(stackPane);
		
		//StackPane
		stackPane.setStyle("-fx-background-color:cyan");
		stack2.setStyle("-fx-background-color:black");
		
		//Add Listeners the the StackPane
		stackPane.widthProperty().addListener((observable , oldValue , newValue) -> reCalculateCanvasSize());
		stackPane.heightProperty().addListener((observable , oldValue , newValue) -> reCalculateCanvasSize());
		
		//Scene
		Scene scene = new Scene(borderPane, 300, 300);
		primary.setScene(scene);
		primary.setOnCloseRequest(c -> System.exit(0));
		primary.show();
		canvas.redraw();
	}
	
	/**
	 * Recalculates the Canvas size to the preffered size
	 */
	public void reCalculateCanvasSize() {
		double size = Math.min(stackPane.getWidth(), stackPane.getHeight()) / 1.5;
		
		borderPane.setMinSize(size, size);
		borderPane.setMaxSize(size, size);
		borderPane.setPrefSize(size, size);
		canvas.redraw();
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
