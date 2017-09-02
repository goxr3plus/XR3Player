package aaTesterOnlyCode;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PopupDemoTester extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

	StackPane root = new StackPane();
	JFXButton button = new JFXButton("Press me boss!");
	
	
	JFXDialog content = new JFXDialog(root,new Label("Hei boss!"),JFXDialog.DialogTransition.LEFT);
	
	
	button.setOnAction(a->{
	   content.show();
	});
	
	root.getChildren().add(button);

	primaryStage.setTitle("JFX Popup Demo");
	primaryStage.setScene(new Scene(root, 400, 400));
	primaryStage.show();
    }

    public static void main(String[] args) {
	launch(args);
    }

}