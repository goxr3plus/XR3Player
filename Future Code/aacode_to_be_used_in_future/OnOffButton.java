/*
 * 
 */
package aacode_to_be_used_in_future;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class OnOffButton.
 */
public class OnOffButton extends Label {

	/** The switched. */
	private SimpleBooleanProperty switched = new SimpleBooleanProperty();
	
	/** The button. */
	private Button button = new Button();

	/**
	 * Instantiates a new on off button.
	 *
	 * @param state the state
	 */
	// Constructor
	public OnOffButton(boolean state) {

		button.setStyle("-fx-background-color:white; -fx-background-radius:50px;");
		setGraphic(button);
		

		switched.addListener(l -> {
			if (switched.get()) {
				setText("ON");
				setStyle(
						"-fx-background-color: green;-fx-text-fill:white; -fx-background-radius:5px; -fx-effect: dropshadow( three-pass-box , orange , 5.0, 0.0 , 0.0 , 1.0 );");
				setContentDisplay(ContentDisplay.RIGHT);
			} else {
				setText("OFF");
				setStyle(
						"-fx-background-color: black;-fx-text-fill:white; -fx-background-radius:5px; -fx-effect: dropshadow( three-pass-box , orange , 5.0, 0.0 , 0.0 , 1.0 );");
				setContentDisplay(ContentDisplay.LEFT);
			}
		});

		setOn(true);
		setOn(state);
	}

	/**
	 * Add Listerner to the button.
	 *
	 * @param handler the handler
	 * @param handler2 the handler 2
	 */
	public void setOnAction(EventHandler<ActionEvent> handler, EventHandler<MouseEvent> handler2) {
		button.setOnAction(handler);
		setOnMouseClicked(handler2);
	}

	/**
	 * Return true if it is On.
	 *
	 * @return true, if is on
	 */
	public boolean isOn() {
		return switched.get() == true;
	}

	/**
	 * Switch between on and off.
	 *
	 * @param f the new on
	 */
	public void setOn(boolean f) {
		switched.set(f);
	}

}
