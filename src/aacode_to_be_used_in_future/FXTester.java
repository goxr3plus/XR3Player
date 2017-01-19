/*
 * 
 */
package aacode_to_be_used_in_future;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
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
import tools.InfoTool;


/**
 * The Class FXTester.
 */
public class FXTester extends Application {
	
	/** The volumer. */
	VolumeSlider volumer = new VolumeSlider(50, 30, 20, 150, Orientation.VERTICAL, 10, 100);
	
	/** The balancer. */
	Balancer balancer = new Balancer(100, 50, 208, 20, 100, 200);
	
	/** The dj disc. */
	DJDisc djDisc = new DJDisc(140, 140, Color.RED, 15,125);
	
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
	 * @see javafx.application.Application#start(javafx.stage.Stage) */
	@Override
	public void start(Stage stage) throws Exception {
		
		stage.centerOnScreen();
		stage.setWidth(500);
		stage.setHeight(500);
		
		Pane pane = new Pane(djDisc);
		pane.setStyle("-fx-background-color:transparent");
		djDisc.setTranslateX(70);
		djDisc.setTranslateY(50);
		stage.setScene(new Scene(pane, Color.ALICEBLUE));
		
		// stage.setScene(new Scene(new BorderPane(invisibleSlider)));
		
		stage.getScene().getStylesheets()
		        .add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());
		stage.show();
		stage.setOnCloseRequest(request -> {
			System.exit(0);
		});
		
		System.out.println(getBasePathForClass(this.getClass()));
		System.out.println(getCurrentDirectoryPath());
		
	}
	
	/**
	 * Returns the absolute path of the current directory in which the given
	 * class
	 * file is.
	 * 
	 * @param classs
	 * @return The absolute path of the current directory in which the class
	 *         file is.
	 * @author GOXR3PLUS[StackOverFlow user] + bachden [StackOverFlow user]
	 */
	public static final String getBasePathForClass(Class<?> classs) {
		
		// Local variables
		File file;
		String basePath = "";
		boolean failed = false;
		
		// Let's give a first try
		try {
			file = new File(classs.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			
			if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
				basePath = file.getParent();
			} else {
				basePath = file.getPath();
			}
		} catch (URISyntaxException ex) {
			failed = true;
			Logger.getLogger(classs.getName()).log(Level.WARNING,
			        "Cannot firgue out base path for class with way (1): ", ex);
		}
		
		// The above failed?
		if (failed) {
			try {
				file = new File(classs.getClassLoader().getResource("").toURI().getPath());
				basePath = file.getAbsolutePath();
				
				// the below is for testing purposes...
				// starts with File.separator?
				// String l = local.replaceFirst("[" + File.separator +
				// "/\\\\]", "")
			} catch (URISyntaxException ex) {
				Logger.getLogger(classs.getName()).log(Level.WARNING,
				        "Cannot firgue out base path for class with way (2): ", ex);
			}
		}
		
		// fix to run inside eclipse
		if (basePath.endsWith(File.separator + "lib") || basePath.endsWith(File.separator + "bin")
		        || basePath.endsWith("bin" + File.separator) || basePath.endsWith("lib" + File.separator)) {
			basePath = basePath.substring(0, basePath.length() - 4);
		}
		// fix to run inside netbeans
		if (basePath.endsWith(File.separator + "build" + File.separator + "classes")) {
			basePath = basePath.substring(0, basePath.length() - 14);
		}
		// end fix
		if (!basePath.endsWith(File.separator)) {
			basePath = basePath + File.separator;
		}
		return basePath;
	}
	
	/**
	 * @return The absolute path of the current directory
	 */
	public static String getCurrentDirectoryPath() {
		
		String local = null;
		try {
			local = InfoTool.class.getClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
		
		// the below is for testing purposes...
		// starts with File.separator?
		// String l = local.replaceFirst("[" + File.separator + "/\\\\]", "")
		
		return new File(local).getAbsolutePath();
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
