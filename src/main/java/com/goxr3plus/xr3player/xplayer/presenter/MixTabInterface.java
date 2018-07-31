package main.java.com.goxr3plus.xr3player.xplayer.presenter;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class MixTabInterface extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private ProgressBar volumeProgress1;
	
	@FXML
	private ProgressBar volumeProgress2;
	
	@FXML
	private Slider masterVolumeSlider;
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public MixTabInterface() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.PLAYERS_FXMLS + "MixTabInterface.fxml"));
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
		
		//volumeProgress1
		
		//volumeProgress2
		
		//masterVolumeSlider
		masterVolumeSlider.valueProperty().addListener((observable , oldValue , newValue) -> {
			double value = newValue.doubleValue();
			double half = masterVolumeSlider.getMax() / 2;
			double masterVolumeSliderWidth = masterVolumeSlider.getWidth();
			System.out.println(volumeProgress1.getWidth() + volumeProgress2.getWidth());
			
			//Progress Max1
			volumeProgress1.setProgress(1);
			volumeProgress2.setProgress(1);
			
			//Below is mind tricks
			if ((int) value == (int) half) {
				volumeProgress1.setMinWidth(masterVolumeSliderWidth / 2);
				volumeProgress2.setMinWidth(masterVolumeSliderWidth / 2);
			} else if (value < half) {
				double progress = 1.0 - ( newValue.doubleValue() / half );
				double minimumWidth = masterVolumeSlider.getWidth() / 2 + ( masterVolumeSlider.getWidth() / 2 ) * ( progress );
				volumeProgress1.setMinWidth(masterVolumeSliderWidth - minimumWidth);
				volumeProgress1.setMaxWidth(masterVolumeSliderWidth - minimumWidth);
				volumeProgress2.setMinWidth(minimumWidth);
			} else if (value > half) {
				double progress = ( newValue.doubleValue() - half ) / half;
				double minimumWidth = masterVolumeSlider.getWidth() / 2 + ( masterVolumeSlider.getWidth() / 2 ) * ( progress );
				volumeProgress1.setMinWidth(minimumWidth);
				volumeProgress2.setMinWidth(masterVolumeSliderWidth - minimumWidth);
				volumeProgress2.setMaxWidth(masterVolumeSliderWidth - minimumWidth);
			}
			
		});
	}
	
}
