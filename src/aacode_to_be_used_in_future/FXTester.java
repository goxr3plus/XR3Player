/*
 * 
 */
package aacode_to_be_used_in_future;

import customNodes.DigitalClock;
import customNodes.DragAdjustableLabel;
import disc.Balancer;
import disc.DJDisc;
import disc.DJFilter;
import disc.VolumeSlider;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class FXTester.
 */
public class FXTester extends Application {

	/** The volumer. */
	VolumeSlider volumer = new VolumeSlider(50, 30, 20, 150, Orientation.VERTICAL, 10, 100);
	
	/** The balancer. */
	Balancer balancer = new Balancer(100, 50, 208, 20, 100, 200);
	
	/** The dj disc. */
	DJDisc djDisc = new DJDisc(140, 140, Color.RED, 15);
	
	/** The filter. */
	DJFilter filter = new DJFilter(150, 150, 36, 36, Color.BLUE);
	
	/** The clock. */
	// CPUsage cpu = new CPUsage(5, 5, 151, 16)
	DigitalClock clock = new DigitalClock(50, 30, 80, 24);
	
	/** The analyse box. */
	AnalyserBox analyseBox = new AnalyserBox(300, 100);
	
	/** The invisible slider. */
	DragAdjustableLabel invisibleSlider = new DragAdjustableLabel(10, 0, 100);

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {

		stage.centerOnScreen();
		stage.setWidth(500);
		stage.setHeight(500);

		Pane pane = new Pane(djDisc);
		pane.setStyle("-fx-background-color:transparent");
		djDisc.setTranslateX(70);
		djDisc.setTranslateY(50);
		// stage.setScene(new Scene(pane, Color.BLACK));

		stage.setScene(new Scene(new BorderPane(invisibleSlider)));

		stage.show();
		stage.setOnCloseRequest(request -> {
			System.exit(0);
		});

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
