package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v1FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;

/**
 * This Service tries to add the details to all Media inside the SmartController
 * 
 * So for example when the user loads the Playlist only the basic information will be added to the Media then this Service will be called and in the
 * background will add the information to all the Media one by one
 * 
 * @author GOXR3PLUSSTUDIO
 *
 */
public class AllDetailsService extends Service<Boolean> {
	
	/** SmartController instance reference */
	private ObservableList<Media> observableList;
	
	/**
	 * Constructor
	 * 
	 * @param smartController
	 */
	public AllDetailsService(ObservableList<Media> observableList) {
		this.observableList = observableList;
		
	}
	
	/**
	 * Restarts the Service
	 * 
	 * @param observableList
	 */
	public void restartService(ObservableList<Media> observableList) {
		//Check if null
		if (observableList == null)
			return;
		
		//Pass the instance
		this.observableList = observableList;
		
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
				
				//Try to do it
				observableList.stream().forEach(media -> {
					if (this.isCancelled())
						return;
					
					File file = new File(media.getFilePath());
					if (!file.exists())
						return;
					
					try {
						
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
						
						//if (file.exists() && localDuration != 0 && media.length() != 0 && "mp3".equals(fileType.get()))
						
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
						
					} catch (Exception e) {
						e.printStackTrace();
						success[0] = false;
					}
				});
				
				//Try to do it for art work this time
				observableList.stream().forEach(media -> {
					if (this.isCancelled())
						return;
					
					File file = new File(media.getFilePath());
					if (!file.exists())
						return;
					
					
					try {
						Image image = media.getAlbumImage();
						//Check if null
						if (image == null) {
							image = Media.NO_ARTWORK_IMAGE;
						}
						media.artworkProperty().get().setImage(image);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
				});
				
				return success[0];
			}
			
		};
	}
	
}
