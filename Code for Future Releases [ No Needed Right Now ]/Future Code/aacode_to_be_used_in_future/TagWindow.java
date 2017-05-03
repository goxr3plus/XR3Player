/*
 * 
 */
package aacode_to_be_used_in_future;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class TagWindow.
 */
public class TagWindow extends Stage {

	/** The mp 3 tag editor. */
	private Mp3TagEditor mp3TagEditor = new Mp3TagEditor();

	/**
	 * Instantiates a new tag window.
	 */
	// Constructor
	public TagWindow() {

		initModality(Modality.APPLICATION_MODAL);
		initStyle(StageStyle.UTILITY);
		setScene(new Scene(mp3TagEditor));
	}

	/**
	 * Shows the mp3TagEditor.
	 *
	 * @param path the path
	 * @param image the image
	 */
	public void showMp3TagEditor(String path,Image image) {
		mp3TagEditor.setPath(path,image);
		setTitle(InfoTool.getFileName(path));
		show();
	}

	/**
	 * Show ogg tag editor.
	 *
	 * @param path the path
	 */
	public void showOggTagEditor(String path) {

	}

	/**
	 * Show AAC tag editor.
	 *
	 * @param path the path
	 */
	public void showAACTagEditor(String path) {

	}

}
