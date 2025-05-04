/**
 * 
 */
package com.goxr3plus.xr3player.xplayer.visualizer.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.xplayer.visualizer.core.VisualizerModel;
import com.jfoenix.controls.JFXButton;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * 
 * This class extends a StackPane which must have as a children a Visualizer
 * 
 * @author GOXR3PLUS
 *
 */
public class VisualizerStackController extends StackPane {

	@FXML
	private JFXButton next;

	@FXML
	private Label descriptionLabel;

	@FXML
	private JFXButton previous;

	// --------------------------------------

	private FadeTransition fadeTransition;

	/** The pause transition. */
	private PauseTransition pauseTransition = new PauseTransition(Duration.millis(900));

	/**
	 * Constructor
	 */
	public VisualizerStackController() {

		// ----------------- FXMLLoader----------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.VISUALIZERS_FXMLS + "VisualizerStackController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "VisualizerStackController FXML can't be loaded!",
					ex);
		}
	}

	/** Called as soon as the .fxml has been loaded */
	@FXML
	private void initialize() {

		// fadeTranstion
		fadeTransition = new FadeTransition(Duration.millis(1500), descriptionLabel);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);

		// descriptionLabel
		descriptionLabel.setOpacity(0);

		// --- MouseListeners
		addEventHandler(MouseEvent.MOUSE_MOVED, m -> {
			pauseTransition.playFromStart();
			previous.setVisible(true);
			next.setVisible(true);
		});

		// PauseTransition
		pauseTransition.setOnFinished(f -> {
			if (!previous.isHover() && !next.isHover()) {
				previous.setVisible(false);
				next.setVisible(false);
			}
		});

		// Fix start problem
		previous.setVisible(false);
		next.setVisible(false);
	}

	/**
	 * Replays the fade effect to show the new type of visualizer
	 * 
	 * @param text
	 */
	public void replayLabelEffect(String text) {
		descriptionLabel.setText(text);
		fadeTransition.playFromStart();
	}

	XPlayerController xPlayerController;

	/**
	 * Add the listeners to the Next and Previous Buttons
	 * 
	 * @param xPlayerController1
	 */
	public void addListenersToButtons(XPlayerController xPlayerController1) {
		this.xPlayerController = xPlayerController1;

		// previous
		previous.setOnAction(a -> previousSpectrumAnalyzer());
		// next
		next.setOnAction(a -> nextSpectrumAnalyzer());

		// -- KeyListeners
		setOnKeyReleased(key -> {
			System.out.println("Key RELEASED");
			if (key.getCode() == KeyCode.RIGHT)
				nextSpectrumAnalyzer();
			else if (key.getCode() == KeyCode.LEFT)
				previousSpectrumAnalyzer();
		});

		// --- Mouse Listeners
		setOnMouseEntered(m -> {
			if (!isFocused())
				requestFocus();
		});

		// --- Mouse Scroll Listeners
		setOnScroll(scroll -> {

			// Delta Y
			if (scroll.getDeltaY() > 0)
				xPlayerController.adjustVolume(1);
			else if (scroll.getDeltaY() < 0)
				xPlayerController.adjustVolume(-1);

			// Delta X
			if (scroll.getDeltaX() < 0)
				nextSpectrumAnalyzer();
			else if (scroll.getDeltaX() > 0)
				previousSpectrumAnalyzer();

			if (scroll.getDeltaY() != 0)
				replayLabelEffect("Vol: " + xPlayerController1.getVolume() + " %");
		});
	}

	/**
	 * Goes to the next Spectrum Analyzer
	 */
	public void nextSpectrumAnalyzer() {
		final SimpleIntegerProperty displayMode = xPlayerController.visualizer.displayMode;
		final int modeVal = displayMode.get();
		final int displaymodeMaximum = VisualizerModel.DISPLAYMODE_MAXIMUM;
		final int nextMode = modeVal + 1;
		displayMode.set((nextMode > displaymodeMaximum)
				? 0
				: nextMode);
	}

	/**
	 * Goes to the previous Spectrum Analyzer
	 */
	public void previousSpectrumAnalyzer() {
		final SimpleIntegerProperty displayMode = xPlayerController.visualizer.displayMode;
		final int modeVal = displayMode.get();
		final int displaymodeMaximum = VisualizerModel.DISPLAYMODE_MAXIMUM;
		final int nextMode = modeVal - 1;
		displayMode.set(nextMode >= 0
				? nextMode
				: displaymodeMaximum);
	}

}
