package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class DropboxFileContextMenu extends ContextMenu {
	
	//--------------------------------------------------------------
	
	@FXML
	private MenuItem download;
	
	@FXML
	private MenuItem rename;
	
	@FXML
	private MenuItem delete;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private DropboxFile dropboxFile;
	private Node node;
	
	/**
	 * Constructor.
	 */
	public DropboxFileContextMenu() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "DropboxFileContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//delete
		delete.setOnAction(a -> Main.dropBoxViewer.deleteFile(dropboxFile, false));
		
		//download
		download.setOnAction(a -> Main.dropBoxViewer.downloadFile(dropboxFile));
		
		//rename
		rename.setOnAction(a -> Main.dropBoxViewer.renameFile(dropboxFile, node));
		
	}
	
	/**
	 * Show the ContextMenu
	 * 
	 * @param x
	 * @param y
	 * @param absoluteFilePath
	 */
	public void show(DropboxFile dropboxFile , double x , double y , Node node) {
		this.dropboxFile = dropboxFile;
		this.node = node;
		
		// Show it
		show(Main.window, x, y - 1);
		
		//Y axis
		double yIni = y + 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob , n , n1) -> super.setY(n1.doubleValue()));
		
		//Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames().addAll(new KeyFrame(Duration.seconds(0.25), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		//new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd, Interpolator.EASE_BOTH)))
		timeIn.play();
	}
	
}
