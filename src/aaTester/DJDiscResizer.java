package aaTester;

import disc.DJDisc;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DJDiscResizer extends Application {

    /**
     * Main Method
     * 
     * @param args
     */
    public static void main(String args[]) {
	launch(args);
    }

    public void start(Stage primary) {

	BorderPane borderPane = new BorderPane();
	StackPane stackPane  = new StackPane();

	stackPane.setStyle("-fx-background-color:cyan");
	DJDisc djDisc = new DJDisc(200, 200, Color.BLUE, 50, 150);
	stackPane.getChildren().add(djDisc);
	borderPane.setCenter(stackPane);

	
	stackPane.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
	    double size;
	    if (stackPane.getWidth() > stackPane.getHeight())
		size = stackPane.getHeight()/1.2;
	    else
		size = stackPane.getWidth()/1.2;
	    djDisc.resizeDisc(size, size);
	    
	    System.out.println("In Local Size:"+size);
	});
	
	//stackPane.size
	
	stackPane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
	    double size;
	    if (stackPane.getWidth() > stackPane.getHeight())
		size = stackPane.getHeight()/1.2;
	    else
		size = stackPane.getWidth()/1.2;
	    djDisc.resizeDisc(size, size);
	    
	    System.out.println("In Parent Size:"+size);
	});
	
//	stackPane.heightProperty().addListener((observable, oldValue, newValue) -> {
//	    double size;
//	    if (stackPane.getWidth() > stackPane.getHeight())
//		size = stackPane.getHeight()/1.2;
//	    else
//		size = stackPane.getWidth()/1.2;
//	    djDisc.resizeDisc(size, size);
//	    
//	    System.out.println("Entered");
//	});
//
//	stackPane.widthProperty().addListener((observable, oldValue, newValue) -> {
//	    double size;
//	    if (stackPane.getWidth() > stackPane.getHeight())
//		size = stackPane.getHeight()/1.2;
//	    else
//		size = stackPane.getWidth()/1.2;
//	    djDisc.resizeDisc(size, size);
//	    System.out.println("Entered");
//	});

	Scene scene = new Scene(borderPane);
	primary.setScene(scene);
	primary.setOnCloseRequest(c->System.exit(0));
	primary.show();
    }
}