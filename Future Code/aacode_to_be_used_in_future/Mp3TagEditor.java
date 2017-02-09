/*
 * 
 */
package aacode_to_be_used_in_future;

import java.io.IOException;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class Mp3TagEditor.
 */
public class Mp3TagEditor extends BorderPane{

	/** The title. */
	@FXML
	private TextField title;

	/** The subtitle. */
	@FXML
	private TextField subtitle;

	/** The comments. */
	@FXML
	private TextArea comments;

	/** The album artist. */
	@FXML
	private TextField albumArtist;

	/** The album title. */
	@FXML
	private TextField albumTitle;

	/** The save. */
	@FXML
	private Button save;

	/** The image view. */
	@FXML
	private ImageView imageView;

	/** The mp 3. */
	Mp3File mp3;

	/**
	 * Instantiates a new mp 3 tag editor.
	 */
	// Constructor
	public Mp3TagEditor() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls+"TagEditor.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show the window.
	 *
	 * @param path the path
	 * @param image the image
	 */
	public void setPath(String path, Image image) {
		try {
			mp3 = new Mp3File(path);

			if (mp3.hasId3v2Tag()) {
				ID3v2 tag = mp3.getId3v2Tag();

				// Description
				title.setText(tag.getTitle());
				comments.setText(tag.getComment());

				// Media
				albumArtist.setText(tag.getAlbumArtist());
				albumTitle.setText(tag.getAlbum());

				// Image
				if (image == null)
					imageView.setImage(InfoTool.getImageFromDocuments("noImage.png"));
				else
					imageView.setImage(image);

			}
		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			e.printStackTrace();
		}

	}
}
