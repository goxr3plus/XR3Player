/*
 * 
 */
package aacode_to_be_used_in_future;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import javafx.scene.input.KeyEvent;

import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class SpaceIntercept.
 */
public class SpaceIntercept extends Application implements EventHandler<KeyEvent> {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		TextField textField = new TextField("asdf");
		Group root = new Group();
		Scene scene = new Scene(root, 200, 100);
		scene.addEventFilter(KeyEvent.ANY, this::handle);
		// root.addEventFilter(KeyEvent.ANY, event -> handle(event));
		// textField.addEventFilter(KeyEvent.ANY, event -> handle(event));
		root.getChildren().add(textField);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(KeyEvent event) {
		if (event.getCode() == KeyCode.SPACE || " ".equals(event.getCharacter())) {
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				System.out.println("Code that responds to SpaceBar");
			}
			event.consume();
		}
	}
}
