/*
 * 
 */
package smartcontroller.media.Information;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import smartcontroller.Genre;
import smartcontroller.media.Audio;
import smartcontroller.media.Media;

/**
 * Allows you to view informations about the selected song like the album image,to search for it on the web,to buy this song on iTunes,Amazon.
 *
 * @author GOXR3PLUS STUDIO
 */
public class MediaInformation extends BorderPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label title;
	
	@FXML
	private Button moreButton;
	
	@FXML
	private Label drive;
	
	@FXML
	private Label stars;
	
	@FXML
	private Label duration;
	
	@FXML
	private Label type;
	
	@FXML
	private Label size;
	
	@FXML
	private Label bitRate;
	
	@FXML
	private Label sampleRate;
	
	@FXML
	private Label encoder;
	
	@FXML
	private Label channel;
	
	@FXML
	private Label format;
	
	@FXML
	private Label isPrivate;
	
	@FXML
	private Label isProtected;
	
	@FXML
	private Label isPadding;
	
	@FXML
	private Label isCopyrighted;
	
	@FXML
	private Label isOriginal;
	
	@FXML
	private Label isVariableBitRate;
	
	@FXML
	private Label empasis;
	
	@FXML
	private Label mp3StartByte;
	
	@FXML
	private Label totalFrames;
	
	@FXML
	private Label noOfSamples;
	
	@FXML
	private Label mpegLayer;
	
	@FXML
	private Label mpegVersion;
	
	// -------------------------------------------------------------
	
	/** The null image. */
	private final Image nullImage = InfoTool.getImageFromResourcesFolder("noAlbumImage.png");
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private Media media;
	
	/**
	 * Constructor.
	 */
	public MediaInformation() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MediaInformation.fxml"));
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
		setOnDragDetected(drag -> {
			if (media != null) {
				
				/* Allow copy transfer mode */
				Dragboard db = startDragAndDrop(TransferMode.COPY, TransferMode.LINK);
				
				/* Put a String into the dragBoard */
				ClipboardContent content = new ClipboardContent();
				content.putFiles(Arrays.asList(new File(media.getFilePath())));
				db.setContent(content);
				
				/* Set the DragView */
				media.setDragView(db);
				
			}
			drag.consume();
		});
		
		setOnDragOver(dragOver -> dragOver.acceptTransferModes(TransferMode.LINK));
		setOnDragDropped(drop -> {
			// Keeping the absolute path
			String absolutePath;
			
			// File?
			for (File file : drop.getDragboard().getFiles()) {
				absolutePath = file.getAbsolutePath();
				if (file.isFile() && InfoTool.isAudioSupported(absolutePath)) {
					updateInformation(new Audio(file.getAbsolutePath(), 0.0, 0, "", "", Genre.SEARCHWINDOW, -1));
					break;
				}
			}
		});
		
	}
	
	/**
	 * Updates the image shown.
	 * 
	 * @param media
	 *            the media [[SuppressWarningsSpartan]]
	 */
	public void updateInformation(Media media) {
		this.media = media;
		
		//We don't want thugs here
		if (this.media == null)
			return;
		
		//== image
		try {
			Image image = media.getAlbumImage();
			imageView.setImage(image != null ? image : nullImage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//== title
		title.textProperty().bind(media.titleProperty());
		
		//== duration
		duration.setText(media.durationEditedProperty().get());
		
		//== stars
		stars.textProperty().bind(media.starsProperty().get().textProperty());
		
		//== drive
		drive.setText(media.getDrive());
		
		//== type
		type.setText(media.getFileType());
		
		//== size
		size.setText(media.fileSizeProperty().get());
		
		try {
			File file = new File(media.getFilePath());
			//It is mp3?
			if ("mp3".equals(media.fileTypeProperty().get()) && file.exists()) {
				MP3AudioHeader mp3Header = new MP3File(file).getMP3AudioHeader();
				
				sampleRate.setText(mp3Header.getSampleRate());
				bitRate.setText(Long.toString(mp3Header.getBitRateAsNumber()));
				encoder.setText(mp3Header.getEncoder());
				channel.setText(mp3Header.getChannels());
				format.setText(mp3Header.getFormat());
				
				mpegVersion.setText(mp3Header.getMpegVersion());
				mpegLayer.setText(mp3Header.getMpegLayer());
				totalFrames.setText(Long.toString(mp3Header.getNumberOfFrames()));
				noOfSamples.setText(Long.toString(mp3Header.getNoOfSamples()));
				mp3StartByte.setText(Long.toString(mp3Header.getMp3StartByte()));
				empasis.setText(mp3Header.getEmphasis());
				isVariableBitRate.setText(mp3Header.isVariableBitRate() ? "yes" : "no");
				isOriginal.setText(mp3Header.isOriginal() ? "yes" : "no");
				isCopyrighted.setText(mp3Header.isCopyrighted() ? "yes" : "no");
				isPadding.setText(mp3Header.isPadding() ? "yes" : "no");
				isProtected.setText(mp3Header.isProtected() ? "yes" : "no");
				isPrivate.setText(mp3Header.isPrivate() ? "yes" : "no");
				
			} else {
				sampleRate.setText("-");
				bitRate.setText("-");
				encoder.setText("-");
				channel.setText("-");
				format.setText("-");
			}
		} catch (IOException | TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException ex) {
			ex.printStackTrace();
		}
		
	}
	
}
