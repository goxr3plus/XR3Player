/**
 * 
 */
package aa_test_code_for_future_updates;

import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author GOXR3PLUS
 *
 */
public class RotCanvas extends Application {

    Random random = new Random();

    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

	Canvas canvas = new Canvas(1000, 1000);
	StackPane stackPane = new StackPane(canvas);

	//Draw
	GraphicsContext gc = canvas.getGraphicsContext2D();
	int points = 300; //number of points
	//float pointAngle = 360 / points; //angle between points	
	int half = 1000 / 2;
	int r = 1000/3;
	gc.setLineWidth(2);
	
	for (float angle = 0; angle < 360; angle++){
	    System.out.println("Calculating");
	    gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
	    
	    int px1 = (int) (half+Math.sin(Math.toRadians(angle))*r);
	    int py1 = (int) (half+Math.cos(Math.toRadians(angle))*r);
	    int px2 = (int) (half+Math.sin(Math.toRadians(angle))*(r+50));
	    int py2 = (int) (half+Math.cos(Math.toRadians(angle))*(r+50));

	    gc.strokeLine(px1,py1, px2, py2); //draw a line from each point back to the centre
	    //draw line between (px1,py1) and (px2,py2)
	}

//	for (float angle = 0; angle < 360; angle = angle + pointAngle) { //move round the circle to each point
//	    gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
//
//	    double x = Math.cos(Math.toRadians(angle)) * radius; //convert angle to radians for x and y coordinates
//	    double y = Math.sin(Math.toRadians(angle)) * radius;
//
//	   // gc.strokeLine(x + half, y + half, half, half); //draw a line from each point back to the centre
//
//	}

	primaryStage.setWidth(1000);
	primaryStage.setHeight(1000);
	primaryStage.setScene(new Scene(stackPane));
	primaryStage.show();

    }

    public static void main(String[] args) {
	launch(args);
    }

}
