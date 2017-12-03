package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.modes.SmartControllerArtistsMode;

public class ArtistsModeService extends Service<Void> {
	
	/** A private instance of the SmartController it belongs */
	private final SmartControllerArtistsMode smartControllerArtistsMode;
	
	/**
	 * The operation to be done by the Service
	 */
	private Operation operation = Operation.UNKNOWN;
	/**
	 * The given artistName
	 */
	private String artistName = "";
	
	/**
	 * Service Progress
	 */
	private int progress;
	
	/**
	 * Service Total Progress
	 */
	private int totalProgress;
	
	/**
	 * Constructor
	 * 
	 * @param smartController
	 */
	public ArtistsModeService(SmartControllerArtistsMode smartControllerArtistsMode) {
		this.smartControllerArtistsMode = smartControllerArtistsMode;
		
		//Restart the Service
		this.restart();
	}
	
	/**
	 * Regenerates all the artists for the ArtistsMode
	 */
	public void regenerateArtists() {
		this.operation = Operation.REFRESH;
		
		//Clear List
		smartControllerArtistsMode.getListView().getItems().clear();
		
		//Restart the Service
		this.restart();
	}
	
	/**
	 * Refreshes the TableView based on the current artist
	 * 
	 * @param artistName
	 */
	public void refreshTableView(String artistName) {
		this.artistName = artistName;
		this.operation = Operation.UPDATE_TABLE_VIEW;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				//Create a new LinkedHashSet
				Set<String> set = new HashSet<>();
				
				try {
					
					//Total and Count = 0
					progress = totalProgress = 0;
					
					if (operation == Operation.REFRESH) {
						
						//================Prepare based on the Files User want to Export=============
						if (smartControllerArtistsMode.getSmartController().getGenre() == Genre.SEARCHWINDOW) {  // CURRENT_PAGE
							System.out.println("Entered for Search Window");
							
							//Count total files that will be exported
							totalProgress = smartControllerArtistsMode.getSmartController().getItemsObservableList().size();
							
							// Stream
							Stream<Media> stream = smartControllerArtistsMode.getSmartController().getItemsObservableList().stream();
							stream.forEach(media -> {
								if (isCancelled())
									stream.close();
								else {
									
									//Add the artist
									set.add(findArtistsFromAudioFile(new File(media.getFilePath())));
									
									//Update the progress
									updateProgress(++progress, totalProgress);
									
								}
							});
							
						} else if (smartControllerArtistsMode.getSmartController().getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST
							
							//Count total files that will be exported
							totalProgress = smartControllerArtistsMode.getSmartController().getTotalInDataBase();
							
							// Stream
							String query = "SELECT(PATH) FROM '" + smartControllerArtistsMode.getSmartController().getDataBaseTableName() + "'";
							try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query);) {
								
								// Fetch the items from the database
								while (resultSet.next())
									if (isCancelled())
										break;
									else {
										
										//Add the artist
										set.add(findArtistsFromAudioFile(new File(resultSet.getString("PATH"))));
										
										//Update the progress
										updateProgress(++progress, totalProgress);
									}
								
							} catch (Exception ex) {
								Main.logger.log(Level.WARNING, "", ex);
							}
							
						}
						
						//For each item on set
						set.remove("");
						ObservableList<String> observableList = set.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
						Platform.runLater(() -> {
							
							//Visibility of details label
							smartControllerArtistsMode.getDetailsLabel().setVisible(observableList.isEmpty());
							
							//Set list view items
							smartControllerArtistsMode.getListView().setItems(observableList);
						});
						
					} else if (operation == Operation.UPDATE_TABLE_VIEW) {
						
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return null;
				
			}
			
			/**
			 * Return the artist of the given audio file (mp3) actually
			 * 
			 * @param audioFile
			 * @return Return the artist of the given audio file (mp3) actually
			 */
			private String findArtistsFromAudioFile(File file) {
				
				//Check file existance , length and extension
				if (file.exists() && file.length() != 0 && "mp3".equals(InfoTool.getFileExtension(file.getAbsolutePath()))) {
					
					//MP3File
					MP3File mp3File;
					try {
						mp3File = new MP3File(file);
					} catch (IOException | TagException | ReadOnlyFileException | CannotReadException | InvalidAudioFrameException e) {
						//e.printStackTrace()
						return "";
					}
					
					//Does it have artist	
					if (mp3File.hasID3v2Tag()) {
						ID3v24Tag tag = mp3File.getID3v2TagAsv24();
						String artist = tag.getFirst(ID3v24FieldKey.ARTIST);
						
						//System.out.println("Artist : " + artist);// + " , Album Artist : " + tag.getFirst(ID3v24FieldKey.ALBUM_ARTIST))
						
						return artist;
					}
					
				}
				
				return "";
			}
			
		};
	}
	
}
