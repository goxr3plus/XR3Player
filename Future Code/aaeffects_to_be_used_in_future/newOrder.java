/*
 * 
 */
package aaeffects_to_be_used_in_future;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class newOrder.
 */
public class newOrder extends Application{

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	public void start(Stage theStage) 
	{
	    theStage.setTitle( "Timeline Example" );
	 
	    Group root = new Group();
	    Scene theScene = new Scene( root );
	    theStage.setScene( theScene );
	 
	    Canvas canvas = new Canvas( 512, 512 );
	    root.getChildren().add( canvas );
	 
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	 
	    Image earth = new Image( getClass().getResourceAsStream("sun.jpg") );
	    Image sun   = new Image( getClass().getResourceAsStream("sun.jpg") );
	   // Image space = new Image( "space.png" );
	 
	    final long startNanoTime = System.nanoTime();
	 
	    new AnimationTimer()
	    {
	        public void handle(long currentNanoTime)
	        {
	            double t = (currentNanoTime - startNanoTime) / 1000000000.0; 
	 
	            double x = 232 + 128 * Math.cos(t);
	            double y = 232 + 128 * Math.sin(t);
	 
	            // background image clears canvas
	            //gc.drawImage( space, 0, 0 );
	            gc.drawImage( earth, x, y );
	            gc.drawImage( sun, 196, 196 );
	            
	        }
	    }.start();
	 
	    theStage.show();
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		launch(args);
	}
}
