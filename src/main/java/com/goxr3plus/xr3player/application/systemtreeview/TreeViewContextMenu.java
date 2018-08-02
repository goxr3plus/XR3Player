package main.java.com.goxr3plus.xr3player.application.systemtreeview;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.PlayContextMenu;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.ShopContextMenu;
import main.java.com.goxr3plus.xr3player.smartcontroller.tags.TagTabCategory;

/**
 * The default context menu for song items of application.
 *
 * @author GOXR3PLUS
 */
public class TreeViewContextMenu extends ContextMenu {
	
	//--------------------------------------------------------------
	
	@FXML
	private Menu getInfoBuy;
	
	@FXML
	private SeparatorMenuItem separator1;
	
	@FXML
	private MenuItem copy;
	
	@FXML
	private MenuItem rename;
	
	@FXML
	private SeparatorMenuItem separator2;
	
	@FXML
	private MenuItem showFile;
	
	@FXML
	private MenuItem editFileInfo;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private String absoluteFilePath;
	
	/** ShopContextMenu */
	private final ShopContextMenu shopContextMenu = new ShopContextMenu();
	
	/** PlayContextMenu */
	private final PlayContextMenu playContextMenu = new PlayContextMenu();
	
	/**
	 * Constructor.
	 */
	public TreeViewContextMenu() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TreeViewContextMenu.fxml"));
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
		
		//PlayContextMenu
		getItems().addAll(0, playContextMenu.getItems());
		
		//getInfoBuy
		getInfoBuy.getItems().addAll(shopContextMenu.getItems());
	}
	
	/**
	 * Show the ContextMenu
	 * 
	 * @param x
	 * @param y
	 * @param absoluteFilePath
	 */
	public void show(String absoluteFilePath , double x , double y) {
		this.absoluteFilePath = absoluteFilePath;
		
		//Set all items visible
		getItems().forEach(item -> item.setVisible(true));
		
		//Keep this variables
		boolean isAudio = InfoTool.isAudio(absoluteFilePath);
		boolean isVideo = InfoTool.isVideo(absoluteFilePath);
		
		//Fix The Menu
		if (isVideo) {
			editFileInfo.setVisible(false);
		} else if (! ( isAudio || isVideo )) {
			playContextMenu.getStartPlayer().setVisible(false);
			playContextMenu.getStopPlayer().setVisible(false);
			editFileInfo.setVisible(false);
			separator1.setVisible(false);
			separator2.setVisible(false);
			getInfoBuy.setVisible(false);
		}
		
		//Update ShopContextMenu
		shopContextMenu.setMediaTitle(InfoTool.getFileTitle(absoluteFilePath));
		
		//Update PlayContextMenu
		playContextMenu.setAbsoluteMediaPath(absoluteFilePath);
		playContextMenu.updateItemsImages();
		
		// Show it
		show(Main.window, x + 8, y - 1);
		
		//Y axis
		double yIni = y - 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob , n , n1) -> super.setY(n1.doubleValue()));
		
		//Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames().addAll(new KeyFrame(Duration.seconds(0.35), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		//new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd, Interpolator.EASE_BOTH)))
		timeIn.play();
	}
	
	@FXML
	public void action(ActionEvent event) {
		Object source = event.getSource();
		
		//showFile
		if (source == showFile)
			ActionTool.openFileLocation(absoluteFilePath);
		else if (source == copy)
			JavaFXTools.setClipBoard(Arrays.asList(new File(absoluteFilePath)));
		else if (source == editFileInfo)
			Main.tagWindow.openAudio(absoluteFilePath, TagTabCategory.BASICINFO, true);
	}
	
}
