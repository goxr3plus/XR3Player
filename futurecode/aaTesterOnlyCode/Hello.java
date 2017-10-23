package aaTesterOnlyCode;
import com.jfoenix.controls.JFXTabPane;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Hello extends Application {
    
    
    /**Main Method
     * @param args
     */
    public static void main(String args[]) {
	launch(args);
    }

    public void start(Stage primaryStage) {
	
	JFXTabPane tabPane = new JFXTabPane();
	//Here trying to set the side
	tabPane.setSide(Side.LEFT);
	
	for(int i=0; i<4; i++)
	    tabPane.getTabs().add(new Tab("Tab "+i,new Label("Imagicon")));
	
	
	primaryStage.setScene(new Scene(new BorderPane(tabPane),400,400));
	primaryStage.show();
    }
}