/*
 * 
 */
package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The Class UpdateScreen.
 */
public class MainLoadingScreen extends StackPane {

	@FXML
	private Rectangle leftRectangle;

	@FXML
	private Rectangle rightRectangle;

	@FXML
	private Rectangle rectangle;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label descriptionLabel;

	@FXML
	private JFXButton cancelButton;

	// -------------------------------------------------------------------------------------

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MainLoadingScreen.class.getName());

	TranslateTransition translate1;
	TranslateTransition translate2;

	/**
	 * Constructor.
	 */
	public MainLoadingScreen() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MainLoadingScreen.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Update Screen Can't be loaded", ex);
		}
	}

	/** Called as soon as the .fxml has been loaded */
	@FXML
	public void initialize() {

		// setStyle("-fx-background-image:url('/image/logo.jpg');
		// -fx-background-size:100% 100%; -fx-background-position: center center;
		// -fx-background-repeat: stretch;")

		// leftRectangle
		leftRectangle.widthProperty().bind(super.widthProperty().divide(2));
		leftRectangle.heightProperty().bind(super.heightProperty());

		rightRectangle.widthProperty().bind(leftRectangle.widthProperty());
		rightRectangle.heightProperty().bind(leftRectangle.heightProperty());

		translate1 = new TranslateTransition(Duration.millis(1000), leftRectangle);
		translate2 = new TranslateTransition(Duration.millis(1000), rightRectangle);

		translate1.setOnFinished(f -> rightRectangle.setVisible(false));
		translate2.setOnFinished(f -> leftRectangle.setVisible(false));
	}

	/**
	 * Makes the animation with rectangles when the application firstly opens
	 */
	public void closeUpdateScreen() {

		// Left Rectangle
		if (translate1.getStatus() != Animation.Status.RUNNING
				&& leftRectangle.getTranslateX() >= -leftRectangle.getWidth()) {
			translate1.setFromX(leftRectangle.getX());
			translate1.setToX(-leftRectangle.getWidth());
			translate1.playFromStart();
		}

		// Right Rectangle
		if (translate2.getStatus() != Animation.Status.RUNNING
				&& rightRectangle.getTranslateX() >= -rightRectangle.getWidth()) {
			translate2.setFromX(rightRectangle.getX());
			translate2.setToX(rightRectangle.getX() + rightRectangle.getWidth());
			translate2.playFromStart();
		}
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return descriptionLabel;
	}

	/**
	 * @return the progressBar
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @return the cancelButton
	 */
	public JFXButton getCancelButton() {
		return cancelButton;
	}

}
