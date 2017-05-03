import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MyApp extends Application {
    
    
    /**Main Method
     * @param args
     */
    public static void main(String args[]) {		
	launch(args);
    }

    public void start(Stage primary) {
	
	BorderPane borderPane = new BorderPane();
	
	//DJDisc djDisc = new DJDisc();
	
	
	Scene scene = new Scene(borderPane,400,400);
	primary.setScene(scene);
	primary.show();
    }
}