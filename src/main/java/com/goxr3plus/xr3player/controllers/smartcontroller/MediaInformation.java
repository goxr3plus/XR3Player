/*
 * 
 */
package com.goxr3plus.xr3player.controllers.smartcontroller;

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

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.enums.FileLinkType;
import com.goxr3plus.xr3player.enums.Genre;
import com.goxr3plus.xr3player.enums.NotificationType;
import com.goxr3plus.xr3player.enums.TagTabCategory;
import com.goxr3plus.xr3player.models.smartcontroller.Audio;
import com.goxr3plus.xr3player.models.smartcontroller.Media;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.io.FileTypeAndAbsolutePath;
import com.goxr3plus.xr3player.utils.io.IOInfo;
import com.goxr3plus.xr3player.utils.javafx.AlertTool;

/**
 * Allows you to view informations about the selected song like the album
 * image,to search for it on the web,to buy this song on iTunes,Amazon.
 *
 * @author GOXR3PLUS STUDIO
 */
public class MediaInformation extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private Button mediaImageButton;

	@FXML
	private ImageView imageView;

	@FXML
	private Label title;

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

	@FXML
	private Button showMore;

	@FXML
	private Label dragAndDropLabel;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	private Media media;

	private final UpdateInformationService service = new UpdateInformationService();

	/**
	 * Constructor.
	 */
	public MediaInformation() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "MediaInformation.fxml"));
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
		mediaImageButton.setOnDragDetected(getOnDragDetected());
		setOnDragOver(event -> {
			// The drag must come from source other than the owner
			if (event.getGestureSource() != this)
				dragAndDropLabel.setVisible(true);
		});

		// dragAndDropLabel
		dragAndDropLabel.setVisible(false);
		dragAndDropLabel.setOnDragOver(event -> event.acceptTransferModes(TransferMode.LINK));
		dragAndDropLabel.setOnDragDropped(event -> {
			// File?
			for (File file : event.getDragboard().getFiles()) {

				// No directories allowed
				if (!file.isDirectory()) {

					// Get it
					FileTypeAndAbsolutePath ftaap = IOInfo.getRealPathFromFile(file.getAbsolutePath());

					// Check if File exists
					if (!new File(ftaap.getFileAbsolutePath()).exists()) {
						AlertTool.showNotification("File doesn't exist",
								(ftaap.getFileType() == FileLinkType.SYMBOLIC_LINK ? "Symbolic link" : "Windows Shortcut")
										+ " points to a file that doesn't exists anymore.",
								Duration.millis(2000), NotificationType.INFORMATION);
						return;
					}

					updateInformation(new Audio(ftaap.getFileAbsolutePath(), 0.0, 0, "", "", Genre.SEARCHWINDOW, -1));

					// break
					break;
				}
			}

			event.consume();
		});
		dragAndDropLabel.setOnDragExited(event -> {
			dragAndDropLabel.setVisible(false);
			event.consume();
		});

		// imageView
		// imageView.setOnDragDetected(getOnDragDetected())
		imageView.visibleProperty().bind(imageView.imageProperty().isNotNull());

		// mediaImageButton
		mediaImageButton.setOnAction(m -> Main.tagWindow.openAudio(media == null ? null : media.getFilePath(),
				TagTabCategory.ARTWORK, true));

		// showMore
		showMore.setOnAction(m -> Main.tagWindow.openAudio(media == null ? null : media.getFilePath(),
				TagTabCategory.BASICINFO, true));

	}

	/**
	 * Updates the image shown.
	 * 
	 * @param mediar the media [[SuppressWarningsSpartan]]
	 */
	public void updateInformation(Media mediar) {
		service.updateInformation(mediar);
	}

	/**
	 * Using this Service as an external Thread which updates the Information based
	 * on the selected Media
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public class UpdateInformationService extends Service<Void> {

		private String _sampleRate;
		private String _bitRate;
		private String _encoder;
		private String _Channel;
		private String _format;

		private String _mpegVersion;
		private String _mpegLayer;
		private String _totalFrames;
		private String _noOfSamples;
		private String _mp3StartByte;
		private String _empasis;
		private String _isVariableBitRate;
		private String _isOriginal;
		private String _isCopyrighted;
		private String _isPadding;
		private String _isProtected;
		private String _isPrivate;
		private Image image;

		/**
		 * Updates the image shown.
		 * 
		 * @param mediar the media [[SuppressWarningsSpartan]]
		 */
		public void updateInformation(Media mediar) {
			media = mediar;

			// We don't want thugs here
			if (media == null)
				return;

			// Restart the Service
			this.restart();

		}

		@Override
		protected Task<Void> createTask() {
			return new Task<>() {

				@Override
				protected Void call() throws Exception {

					// == image
					image = null;
					try {
						image = media.getAlbumImage();
					} catch (Exception ex) {
						// ex.printStackTrace()
					}

					Platform.runLater(() -> {
						imageView.setImage(image);

						// == title
						title.textProperty().bind(media.titleProperty());

						// == duration
						duration.setText(media.durationEditedProperty().get());

						// == stars
						stars.textProperty().bind(media.starsProperty().asString());

						// == drive
						drive.setText(media.getDrive());

						// == type
						type.setText(media.getFileType());

						// == size
						size.setText(media.fileSizeProperty().get());

					});

					// Try to get other information
					try {
						File file = new File(media.getFilePath());

						// ---------------------MP3--------------------------------------
						if ("mp3".equals(media.fileTypeProperty().get()) && file.exists() && file.length() != 0) {
							MP3AudioHeader mp3Header = new MP3File(file).getMP3AudioHeader();

							_sampleRate = mp3Header.getSampleRate();
							_bitRate = Long.toString(mp3Header.getBitRateAsNumber());
							_encoder = mp3Header.getEncoder();
							_Channel = mp3Header.getChannels();
							_format = mp3Header.getFormat();

							_mpegVersion = mp3Header.getMpegVersion();
							_mpegLayer = mp3Header.getMpegLayer();
							_totalFrames = Long.toString(mp3Header.getNumberOfFrames());
							_noOfSamples = Long.toString(mp3Header.getNoOfSamples());
							_mp3StartByte = Long.toString(mp3Header.getMp3StartByte());
							_empasis = mp3Header.isVariableBitRate() ? "yes" : "no";
							_isVariableBitRate = mp3Header.getEmphasis();
							_isOriginal = mp3Header.isOriginal() ? "yes" : "no";
							_isCopyrighted = mp3Header.isCopyrighted() ? "yes" : "no";
							_isPadding = mp3Header.isPadding() ? "yes" : "no";
							_isProtected = mp3Header.isProtected() ? "yes" : "no";
							_isPrivate = mp3Header.isPrivate() ? "yes" : "no";

							// Run it on JavaFX Thread
							Platform.runLater(() -> {
								sampleRate.setText(_sampleRate);
								bitRate.setText(_bitRate);
								encoder.setText(_encoder);
								channel.setText(_Channel);
								format.setText(_format);

								mpegVersion.setText(_mpegVersion);
								mpegLayer.setText(_mpegLayer);
								totalFrames.setText(_totalFrames);
								noOfSamples.setText(_noOfSamples);
								mp3StartByte.setText(_mp3StartByte);
								empasis.setText(_empasis);
								isVariableBitRate.setText(_isVariableBitRate);
								isOriginal.setText(_isOriginal);
								isCopyrighted.setText(_isCopyrighted);
								isPadding.setText(_isPadding);
								isProtected.setText(_isProtected);
								isPrivate.setText(_isPrivate);
							});

							// ------------------------OTHER FORMAT-------------------------
						} else {

							// Run it on JavaFX Thread
							Platform.runLater(() -> {
								sampleRate.setText("-");
								bitRate.setText("-");
								encoder.setText("-");
								channel.setText("-");
								format.setText("-");

								mpegVersion.setText("-");
								mpegLayer.setText("-");
								totalFrames.setText("-");
								noOfSamples.setText("-");
								mp3StartByte.setText("-");
								empasis.setText("-");
								isVariableBitRate.setText("-");
								isOriginal.setText("-");
								isCopyrighted.setText("-");
								isPadding.setText("-");
								isProtected.setText("-");
								isPrivate.setText("-");
							});
						}
					} catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException
							| CannotReadException ex) {
						// ex.printStackTrace();
					}

					return null;
				}
			};
		}

	}

}
