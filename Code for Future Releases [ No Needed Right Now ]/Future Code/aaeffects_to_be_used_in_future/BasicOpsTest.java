/*
 * 
 */
package aaeffects_to_be_used_in_future;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
// TODO: Auto-generated Javadoc
/**
 * The Class BasicOpsTest.
 */
public class BasicOpsTest extends Application {
 
    /**
	 * The main method.
	 *
	 * @param args the arguments
	 */
    public static void main(String[] args) {
        launch(args);
    }
 
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(300, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
	 * Draw shapes.
	 *
	 * @param gc the gc
	 */
    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
    }
}