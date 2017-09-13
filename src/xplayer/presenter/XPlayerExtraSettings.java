/**
 * 
 */
package xplayer.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXTabPane;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

/**
 * The Class XPlayerSettingsController.
 *
 * @author GOXR3PLUS
 */
public class XPlayerExtraSettings extends BorderPane {
	
	// ------------------------
	
	@FXML
	private JFXTabPane tabPane;
	
	@FXML
	private Tab historyPlaylistTab;
	
	// ------------------------
	
	/** The x player UI. */
	XPlayerController xPlayerUI;
	
	/**
	 * Constructor.
	 *
	 * @param xPlayerUI
	 *            the x player UI
	 */
	public XPlayerExtraSettings(XPlayerController xPlayerUI) {
		
		this.xPlayerUI = xPlayerUI;
		
		// --------------------------FXMLLoader--------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "XPlayerExtraSettingsController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "XPlayerSettingsController FXML can't be loaded!", ex);
		}
		
	}
	
	/**
	 * As soon as fxml has been loaded then this method will be called 1)-constructor,2)-FXMLLOADER,3)-initialise();
	 */
	@FXML
	private void initialize() {
		
		// When this can be visible?
		this.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				xPlayerUI.getSettingsToggle().setSelected(false);
		});
		this.visibleProperty().bind(xPlayerUI.getSettingsToggle().selectedProperty());
		this.visibleProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue) // true?
				this.requestFocus();
		});
		
		// ----PlayListTab
		historyPlaylistTab.setContent(xPlayerUI.getxPlayerPlayList());
		
	}
	
	/**
	 * @return the historyPlaylistTab
	 */
	public Tab getHistoryPlaylistTab() {
		return historyPlaylistTab;
	}
	
	/**
	 * @return the tabPane
	 */
	public TabPane getTabPane() {
		return tabPane;
	}
	
}
