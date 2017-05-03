/**
 * 
 */
package aa_test_code_for_future_updates;

import java.util.Arrays;
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
	
	
	//GEM CODE
	
	 // number of line segments to plot
        int n = 1000;//Integer.parseInt(args[0]);
        gc.setStroke(Color.BLACK);

        // the function y = sin(4x) + sin(20x), sampled at n+1 points
        // between x = 0 and x = pi
        double[] x = new double[n+1];
        double[] y = new double[n+1];
        for (int i = 0; i <= n; i++) {
            x[i] = (Math.PI * i / n);
            x[i]=x[i]*100;
            y[i] = Math.sin(4*x[i]) + Math.sin(20*x[i]);
            y[i]=y[i]*100;
        }
        
        System.out.println("X: "+Arrays.toString(x)+"\n Y: "+Arrays.toString(y));

        // rescale the coordinate system
       /// gc.scale(Math.PI,-2.0);
       /// StdDraw.setXscale(0, Math.PI);
       // StdDraw.setYscale(-2.0, +2.0);

        // plot the approximation to the function
        for (int i = 0; i < n; i++) {
            gc.strokeLine(5,5,100,100);
            gc.strokeLine(x[i], y[i], x[i+1], y[i+1]);
        }
        
        
        
        
        //Code for Circular With Lines
//	int points = 300; //number of points
//	//float pointAngle = 360 / points; //angle between points	
//	int half = 1000 / 2;
//	int r = 1000/3;
//	gc.setLineWidth(2);
//	
//	for (float angle = 0; angle < 360; angle++){
//	    System.out.println("Calculating");
//	    gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
//	    
//	    int px1 = (int) (half+Math.sin(Math.toRadians(angle))*r);
//	    int py1 = (int) (half+Math.cos(Math.toRadians(angle))*r);
//	    int px2 = (int) (half+Math.sin(Math.toRadians(angle))*(r+50));
//	    int py2 = (int) (half+Math.cos(Math.toRadians(angle))*(r+50));
//
//	    gc.strokeLine(px1,py1, px2, py2); //draw a line from each point back to the centre
//	    //draw line between (px1,py1) and (px2,py2)
//	}

        
        //Other Code
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
