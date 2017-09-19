package application.versionupdate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

/**
 * This class is an class FXML Prototype
 *
 * @author GOXR3PLUS
 */
public class GitHubRelease extends TitledPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private Label urlLabel;
	
	@FXML
	private Label downloadsLabel;
	
	@FXML
	private Label publishedAtLabel;
	
	@FXML
	private Label createdAtLabel;
	
	@FXML
	private Label downloadSizeLabel;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Constructor.
	 */
	public GitHubRelease() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "GitHubRelease.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Update the Labels with the Following Elements
	 * 
	 * @param url
	 * @param downloads
	 * @param publichedAt
	 * @param createdAt
	 * @param downloadSize
	 */
	public void updateLabels(String url , String downloads , String downloadSize , String publichedAt , String createdAt) {
		urlLabel.setText(url);
		downloadsLabel.setText(downloads);
		downloadSizeLabel.setText(downloadSize);
		publishedAtLabel.setText(publichedAt);
		createdAtLabel.setText(createdAt);
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
	}
	
}
