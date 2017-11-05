package main.java.com.goxr3plus.xr3player.smartcontroller.tags;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXTabPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Audio;
import main.java.com.goxr3plus.xr3player.smartcontroller.tags.mp3.ID3v1;
import main.java.com.goxr3plus.xr3player.smartcontroller.tags.mp3.ID3v2;
import main.java.com.goxr3plus.xr3player.smartcontroller.tags.mp3.MP3BasicInfo;

/**
 * This window allows to modify Tags of various AudioFormats
 * 
 * @author GOXR3PLUS
 *
 */
public class TagWindow extends StackPane {
	
	//--------------------------------------------------------
	
	@FXML
	private JFXTabPane tabPane;
	
	@FXML
	private Tab basicInfoTab;
	
	@FXML
	private Tab artWorkTab;
	
	@FXML
	private Tab id3v1Tab;
	
	@FXML
	private Tab id3v2Tab;
	
	//--------------------------------------------------------
	
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/** The Window */
	private final Stage window = new Stage();
	
	//For MP3
	private final MP3BasicInfo mp3BasicInfo = new MP3BasicInfo();
	private final ArtWork artWork = new ArtWork();
	private final ID3v1 id3V1Controller = new ID3v1();
	private final ID3v2 id3V2Controller = new ID3v2();
	
	/**
	 * Constructor
	 */
	public TagWindow() {
		
		// ------------------------------------FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TagWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
		window.setTitle("Tag Window");
		window.initStyle(StageStyle.UTILITY);
		window.setScene(new Scene(this));
		window.getScene().setOnKeyReleased(k -> {
			if (k.getCode() == KeyCode.ESCAPE)
				window.close();
		});
	}
	
	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {
		
		//basicInfoTab
		basicInfoTab.setContent(mp3BasicInfo);
		
		//artWorkTab
		artWorkTab.setContent(artWork);
		//ImageView
		artWork.getImageView().fitWidthProperty().bind(window.widthProperty().subtract(20));
		artWork.getImageView().fitHeightProperty().bind(window.heightProperty().subtract(20));
		
		//id3v1Tab
		id3v1Tab.setContent(id3V1Controller);
		
		//id3v2Tab
		id3v2Tab.setContent(id3V2Controller);
		
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}
	
	/**
	 * Show the Window
	 */
	public void show() {
		if (!window.isShowing())
			window.show();
		else
			window.requestFocus();
	}
	
	/**
	 * Open the TagWindow based on the extension of the Audio
	 * 
	 * @param absolutePath
	 *            The absolute path of the file
	 * @param tabCategory
	 *            The tag tab category
	 */
	public void openAudio(String absolutePath , TagTabCategory tabCategory) {
		if (absolutePath != null) {
			
			//Find file extension
			String extension = InfoTool.getFileExtension(absolutePath);
			
			//Clear Tab Pane Tabs
			tabPane.getTabs().clear();
			
			//mp3?
			if (extension.equalsIgnoreCase(".mp3")) {
				
				//Add the tabs
				tabPane.getTabs().addAll(basicInfoTab, artWorkTab, id3v1Tab, id3v2Tab);
				
				//Check the tabCategory
				if (tabCategory == TagTabCategory.BASICINFO)
					tabPane.getSelectionModel().select(1);
				else if (tabCategory == TagTabCategory.ARTWORK)
					tabPane.getSelectionModel().select(2);
				
				//basicInfoTab
				mp3BasicInfo.updateInformation(new Audio(absolutePath, 0.0, 0, "", "", Genre.SEARCHWINDOW, -1));
				
				//artWorkTab
				artWork.showMediaFileImage(absolutePath);
				
				//id3v1Tab
				//id3v1Tab.setContent(id3V1Controller)
				
				//id3v2Tab
				//id3v2Tab.setContent(id3V2Controller)
				
			} else {
				
				//Add the tabs
				tabPane.getTabs().addAll(basicInfoTab);
				
				//basicInfoTab
				mp3BasicInfo.updateInformation(new Audio(absolutePath, 0.0, 0, "", "", Genre.SEARCHWINDOW, -1));
				
			}
		}
	}
	
}
