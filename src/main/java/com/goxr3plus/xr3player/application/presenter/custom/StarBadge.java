package main.java.com.goxr3plus.xr3player.application.presenter.custom;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.java.com.goxr3plus.xr3player.application.tools.general.InfoTool;

public class StarBadge extends Button {
	
	//--------------------------------------------------------------
	
	@FXML
	private Label label;
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public StarBadge(double stars) {
		setStars(stars);
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "StarBadge.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Called as soon as fxml is initialized
	 */
	@FXML
	private void initialize() {
		
	}
	
	/**
	 * Set the stars
	 * 
	 * @param stars
	 */
	public void setStars(double stars) {
		if (label != null)
			label.setText(String.valueOf(stars));
	}
	
	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}
	
}
