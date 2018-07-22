package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v1FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.MediaTableViewer;

/**
 * This Service tries to add the details to all Media inside the SmartController
 * 
 * So for example when the user loads the Playlist only the basic information will be added to the Media then this Service will be called and in the
 * background will add the information to all the Media one by one
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class MediaTagsService extends Service<Boolean> {
	
	/** MediaTableViewer instance reference */
	private MediaTableViewer mediaTableViewer;
	private AllDetailsServiceOperation operation = AllDetailsServiceOperation.ALL;
	
	public enum AllDetailsServiceOperation {
		
		ALL, TAGS, ARTWORK_COLUMN;
	}
	
	/**
	 * Constructor
	 */
	public MediaTagsService() {
		
		setOnSucceeded(s -> done());
		setOnFailed(f -> done());
	}
	
	private void done() {
		
		//Try to Sort the table
		mediaTableViewer.sortTable();
		
		//Refresh the Main Mode Table View
		mediaTableViewer.getSmartController().getNormalModeMediatTableViewer().getTableView().refresh();
	}
	
	/**
	 * Restarts the Service
	 * 
	 * @param observableList
	 */
	public void restartService(MediaTableViewer mediaTableViewer) {
		
		//Check if null	
		if (mediaTableViewer == null)
			return;
		
		//Pass the instance
		this.mediaTableViewer = mediaTableViewer;
		
		//Find the Operation
		if (mediaTableViewer.getArtworkColumn().isVisible())
			this.operation = AllDetailsServiceOperation.ALL;
		else
			this.operation = AllDetailsServiceOperation.TAGS;
		
		//Restart the Service
		restart();
	}
	
	/**
	 * Updates only the artwork column
	 * 
	 * @param mediaTableViewer
	 */
	public void updateOnlyArtWorkColumn(MediaTableViewer mediaTableViewer) {
		
		//Check if null	
		if (mediaTableViewer == null)
			return;
		
		//Pass the instance
		this.mediaTableViewer = mediaTableViewer;
		
		//Find the Operation 
		if (this.isRunning() && this.operation == AllDetailsServiceOperation.ALL)
			this.operation = AllDetailsServiceOperation.ALL;
		else
			this.operation = AllDetailsServiceOperation.ARTWORK_COLUMN;
		
		//Restart the Service
		restart();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			protected Boolean call() throws Exception {
				
				boolean[] success = { true };
				
				//Add all the Tags to the Media
				if (operation == AllDetailsServiceOperation.ALL || operation == AllDetailsServiceOperation.TAGS) {
					//System.out.println("Refreshing Tags")
					
					//Try to do it
					mediaTableViewer.getTableView().getItems().stream().forEach(media -> {
						if (this.isCancelled())
							return;
						
						//Check file
						File file = new File(media.getFilePath());
						if (!file.exists())
							return;
						
						//Fields
						int bitrate = -1;
						String bpm = "";
						String artist = "";
						String mood = "";
						String album = "";
						String composer = "";
						String comment = "";
						String tempo = "";
						String genre = "";
						String year = "";
						String key = "";
						String copyright = "";
						String track = "";
						String trackTotal = "";
						String remixer = "";
						String djMixer = "";
						String rating = "";
						String producer = "";
						String performer = "";
						String orchestra = "";
						String country = "";
						String lyricist = "";
						String conductor = "";
						String amazonID = "";
						String encoder = "";
						
						try {
							
							//--------------------MP3--------------------------------
							if ("mp3".equals(media.getFileType())) {
								
								MP3File mp3File = new MP3File(file);
								
								//-- BitRate 			
								bitrate = (int) mp3File.getMP3AudioHeader().getBitRateAsNumber();
								
								//--------------Check if it has ID3V2Tag-------------------
								if (mp3File.hasID3v2Tag()) {
									
									//Keep it
									ID3v24Tag tag = mp3File.getID3v2TagAsv24();
									
									//-- BPM
									bpm = tag.getFirst(ID3v24FieldKey.BPM).trim();
									media.bpmProperty().set(bpm.isEmpty() ? -1 : (int) Double.parseDouble(bpm));
									
									//-- Artist
									artist = tag.getFirst(ID3v24FieldKey.ARTIST).trim();
									
									//-- Mood
									mood = tag.getFirst(ID3v24FieldKey.MOOD).trim();
									
									//-- Album
									album = tag.getFirst(ID3v24FieldKey.ALBUM).trim();
									
									//-- Composer
									composer = tag.getFirst(ID3v24FieldKey.COMPOSER).trim();
									
									//-- Comment
									comment = tag.getFirst(ID3v24FieldKey.COMMENT).trim();
									
									//-- Genre
									genre = tag.getFirst(ID3v24FieldKey.GENRE).trim();
									
									//-- Tempo
									tempo = tag.getFirst(ID3v24FieldKey.TEMPO).trim();
									
									//-- Key
									key = tag.getFirst(ID3v24FieldKey.KEY).trim();
									
									//-- Year
									year = tag.getFirst(ID3v24FieldKey.YEAR).trim();
									
									//
									
									//-- COPYRIGHT
									copyright = tag.getFirst(ID3v24FieldKey.COPYRIGHT).trim();
									
									//-- TRACK
									track = tag.getFirst(ID3v24FieldKey.TRACK).trim();
									
									//-- TRACK_TOTAL
									trackTotal = tag.getFirst(ID3v24FieldKey.TRACK_TOTAL).trim();
									
									//-- REMIXER
									remixer = tag.getFirst(ID3v24FieldKey.REMIXER).trim();
									
									//-- DJMIXER
									djMixer = tag.getFirst(ID3v24FieldKey.DJMIXER).trim();
									
									//-- RATING
									rating = tag.getFirst(ID3v24FieldKey.RATING).trim();
									
									//-- PRODUCER
									producer = tag.getFirst(ID3v24FieldKey.PRODUCER).trim();
									
									//-- PERFORMER
									performer = tag.getFirst(ID3v24FieldKey.PERFORMER).trim();
									
									//-- ORCHESTRA
									orchestra = tag.getFirst(ID3v24FieldKey.ORCHESTRA).trim();
									
									//-- COUNTRY
									country = tag.getFirst(ID3v24FieldKey.COUNTRY).trim();
									
									//-- LYRICIST
									lyricist = tag.getFirst(ID3v24FieldKey.LYRICIST).trim();
									
									//-- CONDUCTOR
									conductor = tag.getFirst(ID3v24FieldKey.CONDUCTOR).trim();
									
									//-- AMAZON_ID
									amazonID = tag.getFirst(ID3v24FieldKey.AMAZON_ID).trim();
									
									//-- ENCODER
									encoder = tag.getFirst(ID3v24FieldKey.ENCODER).trim();
									
								}
								
								//--------------Check if it has ID3V1Tag-------------------
								else if (mp3File.hasID3v1Tag()) {
									
									ID3v1Tag tag = mp3File.getID3v1Tag();
									
									//-- Artist
									artist = tag.getFirst(ID3v1FieldKey.ARTIST.toString()).trim();
									
									//-- Album
									album = tag.getFirst(ID3v1FieldKey.ALBUM.toString()).trim();
									
									//-- Comment
									comment = tag.getFirst(ID3v1FieldKey.COMMENT.toString()).trim();
									
									//-- Genre
									genre = tag.getFirst(ID3v1FieldKey.GENRE.toString()).trim();
									
									//-- Year
									year = tag.getFirst(ID3v1FieldKey.YEAR.toString()).trim();
									
								}
								
							}
							
						} catch (Exception e) {
							System.err.println("MediaTagsService problem : " + e.getMessage());
							success[0] = false;
						}
						
						//----------------------Now fill all the Properties------------------------------
						String emptyWord = "-";
						
						//-- BitRate 			
						media.bitRateProperty().set(bitrate);
						
						//-- BPM
						media.bpmProperty().set(bpm.isEmpty() ? -1 : (int) Double.parseDouble(bpm));
						
						//-- Artist
						media.artistProperty().set(artist.isEmpty() ? emptyWord : artist);
						
						//-- Mood
						media.moodProperty().set(mood.isEmpty() ? emptyWord : mood);
						
						//-- Album
						media.albumProperty().set(album.isEmpty() ? emptyWord : album);
						
						//-- Composer
						media.composerProperty().set(composer.isEmpty() ? emptyWord : composer);
						
						//-- Comment
						media.commentProperty().set(comment.isEmpty() ? emptyWord : comment);
						
						//-- Genre
						media.genreProperty().set(genre.isEmpty() ? emptyWord : genre);
						
						//-- Tempo
						media.tempoProperty().set(tempo.isEmpty() ? emptyWord : tempo);
						
						//-- Key
						media.keyProperty().set(key.isEmpty() ? emptyWord : key);
						
						//-- Year
						media.yearProperty().set(year.isEmpty() ? emptyWord : year);
						
						//-- COPYRIGHT
						media.copyrightProperty().set(copyright.isEmpty() ? emptyWord : copyright);
						
						//-- TRACK
						media.trackProperty().set(track.isEmpty() ? emptyWord : track);
						
						//-- TRACK_TOTAL
						media.track_totalProperty().set(trackTotal.isEmpty() ? emptyWord : trackTotal);
						
						//-- REMIXER
						media.remixerProperty().set(remixer.isEmpty() ? emptyWord : remixer);
						
						//-- DJMIXER
						media.djMixerProperty().set(djMixer.isEmpty() ? emptyWord : djMixer);
						
						//-- RATING
						media.ratingProperty().set(rating.isEmpty() ? emptyWord : rating);
						
						//-- PRODUCER
						media.producerProperty().set(producer.isEmpty() ? emptyWord : producer);
						
						//-- PERFORMER
						media.performerProperty().set(performer.isEmpty() ? emptyWord : performer);
						
						//-- ORCHESTRA
						media.orchestraProperty().set(orchestra.isEmpty() ? emptyWord : orchestra);
						
						//-- COUNTRY
						media.countryProperty().set(country.isEmpty() ? emptyWord : country);
						
						//-- LYRICIST
						media.lyricistProperty().set(lyricist.isEmpty() ? emptyWord : lyricist);
						
						//-- CONDUCTOR
						media.conductorProperty().set(conductor.isEmpty() ? emptyWord : conductor);
						
						//-- AMAZON_ID
						media.amazonIDProperty().set(amazonID.isEmpty() ? emptyWord : amazonID);
						
						//-- ENCODER
						media.encoderProperty().set(encoder.isEmpty() ? emptyWord : encoder);
						
					});
				}
				
				//Try to do it for art work this time
				if (operation == AllDetailsServiceOperation.ALL || operation == AllDetailsServiceOperation.ARTWORK_COLUMN) {
					//System.out.println("Refreshing ArtWork")
					
					//For each
					mediaTableViewer.getTableView().getItems().stream().forEach(media -> {
						if (this.isCancelled())
							return;
						
						//Check file 
						File file = new File(media.getFilePath());
						if (!file.exists())
							return;
						
						try {
							//Image can be null , remember.
							Image image = media.getAlbumImage();
							
							( (ImageView) media.artworkProperty().get().getChildren().get(1) ).setImage(image);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					});
				}
				
				return success[0];
			}
			
		};
	}
	
}
