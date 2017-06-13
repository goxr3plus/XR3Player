/*
 * 
 */
package smartcontroller.media;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;

import application.Main;
import application.tools.ActionTool;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * Allows you to view informations about the selected song like the album image,to search for it on the web,to buy this song on iTunes,Amazon.
 *
 * @author SuperGoliath
 */
public class MediaInformation extends BorderPane implements Initializable {
	
	/** The image view. */
	@FXML
	private ImageView imageView;
	
	/** The title. */
	@FXML
	private Label title;
	
	/** The drive. */
	@FXML
	private Label drive;
	
	/** The stars. */
	@FXML
	private Label stars;
	
	/** The times played. */
	@FXML
	private Label timesPlayed;
	
	/** The duration. */
	@FXML
	private Label duration;
	
	/** The type. */
	@FXML
	private Label type;
	
	/** The year. */
	@FXML
	private Label year;
	
	/** The size. */
	@FXML
	private Label size;
	
	/** The comments. */
	@FXML
	private Label comments;
	
	/** The album. */
	@FXML
	private Label album;
	
	/** The media. */
	private Media media;
	
	/** The null image. */
	private final Image nullImage = InfoTool.getImageFromResourcesFolder("noImage.png");
	
	/**
	 * Constructor.
	 */
	public MediaInformation() {
		
		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MediaInformation.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "MediaInformation cannot be initialliazed...", ex);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0 , ResourceBundle arg1) {
		imageView.setOnDragDetected(drag -> {
			
			if (media != null) {
				
				/* Allow copy transfer mode */
				Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);
				
				/* Put a String into the dragBoard */
				ClipboardContent content = new ClipboardContent();
				content.putFiles(Arrays.asList(new File(media.getFilePath())));
				
				// width,height
				int fitWidth = (int) imageView.getFitWidth();
				int fitHeight = (int) imageView.getFitHeight();
				
				/* Set the DragView */
				if (imageView.getImage() == nullImage) {
					
					// ..
					WritableImage image = new WritableImage(fitWidth, fitHeight);
					Canvas canvas = new Canvas();
					canvas.setWidth(imageView.getFitWidth());
					canvas.setHeight(fitHeight);
					ActionTool.paintCanvas(canvas.getGraphicsContext2D(), media.getFileName(), fitWidth, fitHeight);
					
					// ..transparent
					SnapshotParameters params = new SnapshotParameters();
					params.setFill(Color.TRANSPARENT);
					
					// ..snapshot
					db.setDragView(canvas.snapshot(params, image), fitWidth / 2.00, 0);
				} else {
					WritableImage image = new WritableImage(fitWidth, fitHeight);
					db.setDragView(imageView.snapshot(null, image), fitWidth / 2.00, fitHeight / 2.00);
				}
				
				db.setContent(content);
			}
			drag.consume();
			
		});
		
	}
	
	/**
	 * Updates the image shown.
	 *
	 * @param media
	 *            the media
	 */
	public void updateInformation(Media media) {
		if (isVisible()) {
			this.media = media;
			
			// Set the image
			Image image = media.getAlbumImage();
			
			if (image != null)
				imageView.setImage(image);
			else
				imageView.setImage(nullImage);
			
			// title
			title.setText(media.getTitle());
			
			// duration
			duration.setText(InfoTool.getTimeEdited(media.getDuration()));
			
			// year
			
			// album
			
			// timesPlayed
			timesPlayed.setText(String.valueOf(media.getTimesPlayed()));
			
			// stars
			stars.setText(String.valueOf(media.getStars()));
			
			// drive
			drive.setText(media.getDrive());
			
			// type
			type.setText(media.getFileType());
			
			// size
			
			// comments
			
		}
		
	}
	
}
