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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.modes.librarymode.Library;
import main.java.com.goxr3plus.xr3player.application.tools.ActionTool;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.application.tools.JavaFXTools;
import main.java.com.goxr3plus.xr3player.application.tools.NotificationType;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.FileCategory;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.PlayContextMenu;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.ShopContextMenu;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;
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
	
	private FileTreeItem treeItem;
	
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
	public void show(FileTreeItem treeItem , double x , double y) {
		this.treeItem = treeItem;
		
		//Set all items visible
		getItems().forEach(item -> item.setVisible(true));
		
		//Keep this variables
		boolean isAudio = InfoTool.isAudio(treeItem.getAbsoluteFilePath());
		boolean isVideo = InfoTool.isVideo(treeItem.getAbsoluteFilePath());
		
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
		shopContextMenu.setMediaTitle(InfoTool.getFileTitle(treeItem.getAbsoluteFilePath()));
		
		//Update PlayContextMenu
		playContextMenu.setAbsoluteMediaPath(treeItem.getAbsoluteFilePath());
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
			ActionTool.openFileLocation(treeItem.getAbsoluteFilePath());
		else if (source == copy)
			JavaFXTools.setClipBoard(Arrays.asList(new File(treeItem.getAbsoluteFilePath())));
		else if (source == editFileInfo)
			Main.tagWindow.openAudio(treeItem.getAbsoluteFilePath(), TagTabCategory.BASICINFO, true);
		else if (source == rename)
			rename(Main.topBar);
	}
	
	/**
	 * Rename the Media File.
	 * 
	 * @param controller
	 *            the controller
	 * @param node
	 *            The node based on which the Rename Window will be position [[SuppressWarningsSpartan]]
	 */
	public void rename(Node node) {
		
		// Open Window
		String extension = "." + InfoTool.getFileExtension(getAbsoluteFilePath());
		Main.renameWindow.show(InfoTool.getFileTitle(getAbsoluteFilePath()), node, "Media Renaming", FileCategory.FILE);
		String oldFilePath = getAbsoluteFilePath();
		
		// Bind
		treeItem.valueProperty().bind(Main.renameWindow.getInputField().textProperty().concat(!treeItem.isDirectory() ? extension : ""));
		
		// When the Rename Window is closed do the rename
		Main.renameWindow.showingProperty().addListener(new InvalidationListener() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			public void invalidated(Observable observable) {
				
				// Remove the Listener
				Main.renameWindow.showingProperty().removeListener(this);
				
				// !Showing
				if (!Main.renameWindow.isShowing()) {
					
					// Remove Binding
					treeItem.valueProperty().unbind();
					
					String newFilePath = new File(oldFilePath).getParent() + File.separator + Main.renameWindow.getInputField().getText()
							+ ( !treeItem.isDirectory() ? extension : "" );
					
					// !XPressed && // Old name != New name
					if (Main.renameWindow.wasAccepted() && !getAbsoluteFilePath().equals(newFilePath)) {
						
						try {
							
							// Check if that file already exists
							if (new File(newFilePath).exists()) {
								setAbsoluteFilePath(oldFilePath);
								ActionTool.showNotification("Rename Failed", "The action can not been completed:\nA file with that name already exists.", Duration.millis(1500),
										NotificationType.WARNING);
								//controller.renameWorking = false
								return;
							}
							
							// Check if it can be renamed
							if (!new File(getAbsoluteFilePath()).renameTo(new File(newFilePath))) {
								setAbsoluteFilePath(oldFilePath);
								ActionTool.showNotification("Rename Failed",
										"The action can not been completed(Possible Reasons):\n1) The file is opened by a program,close it and try again.\n2)It doesn't exist anymore..",
										Duration.millis(1500), NotificationType.WARNING);
								//controller.renameWorking = false
								return;
							}
							
							//Inform all Libraries SmartControllers 
							Main.libraryMode.viewer.getItemsObservableList().stream().map(library -> ( (Library) library ).getSmartController()).forEach(smartController -> {
								
								Media.internalDataBaseRename(smartController, newFilePath, oldFilePath);
								
							});
							
							//Inform all XPlayers SmartControllers
							Main.xPlayersList.getList().stream().map(xPlayerController -> xPlayerController.getxPlayerPlayList().getSmartController()).forEach(smartController -> {
								
								Media.internalDataBaseRename(smartController, newFilePath, oldFilePath);
								
							});
							
							//Update Emotion Lists SmartControllers
							Main.emotionsTabPane.getTabPane().getTabs().stream().map(tab -> (SmartController) tab.getContent()).forEach(smartController -> {
								
								Media.internalDataBaseRename(smartController, newFilePath, oldFilePath);
								
							});
							
							//Inform all XPlayers Models
							Main.xPlayersList.getList().stream().forEach(xPlayerController -> {
								if (oldFilePath.equals(xPlayerController.getxPlayerModel().songPathProperty().get())) {
									
									//filePath
									xPlayerController.getxPlayerModel().songPathProperty().set(newFilePath);
									
									//object
									xPlayerController.getPlayService().checkAudioTypeAndUpdateXPlayerModel(newFilePath);
									
									//change the text of Marquee
									xPlayerController.getMediaFileMarquee().setText(InfoTool.getFileName(newFilePath));
									
								}
							});
							
							// Inform Played Media List
							Main.playedSongs.renameMedia(oldFilePath, newFilePath, false);
							
							// Inform Hated Media List
							Main.emotionListsController.hatedMediaList.renameMedia(oldFilePath, newFilePath, false);
							// Inform Disliked Media List
							Main.emotionListsController.dislikedMediaList.renameMedia(oldFilePath, newFilePath, false);
							// Inform Liked Media List
							Main.emotionListsController.likedMediaList.renameMedia(oldFilePath, newFilePath, false);
							// Inform Loved Media List
							Main.emotionListsController.lovedMediaList.renameMedia(oldFilePath, newFilePath, false);
							
							//Update the SearchWindow
							Main.searchWindowSmartController.getItemsObservableList().forEach(media -> {
								if (media.getFilePath().equals(oldFilePath))
									media.setFilePath(newFilePath);
							});
							
							//Set new file path
							setAbsoluteFilePath(newFilePath);
							
							//Commit to the Database
							Main.dbManager.commit();
							
							ActionTool.showNotification("Success Message", "Successfully rename from :\n" + oldFilePath + " \nto\n" + newFilePath, Duration.millis(2000),
									NotificationType.SUCCESS);
							// Exception occurred
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
							setAbsoluteFilePath(oldFilePath);
							ActionTool.showNotification("Error Message", "Failed to rename the File:/n" + ex.getMessage(), Duration.millis(1500), NotificationType.ERROR);
						}
					} else // X is pressed by user || // Old name == New name
						setAbsoluteFilePath(oldFilePath);
					
				} // RenameWindow is still showing
			}// invalidated
		});
		//}
	}
	
	/**
	 * @return the absoluteFilePath
	 */
	public String getAbsoluteFilePath() {
		return treeItem.getAbsoluteFilePath();
	}
	
	/**
	 * @param absoluteFilePath
	 *            the absoluteFilePath to set
	 */
	public void setAbsoluteFilePath(String absoluteFilePath) {
		treeItem.setAbsoluteFilePath(absoluteFilePath);
	}
	
}
