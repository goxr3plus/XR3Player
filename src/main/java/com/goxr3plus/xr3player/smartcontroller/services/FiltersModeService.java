package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.MenuItem;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Audio;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.modes.SmartControllerFiltersMode;

public class FiltersModeService extends Service<Void> {
	
	/** A private instance of the SmartController it belongs */
	private final SmartControllerFiltersMode smartControllerArtistsMode;
	
	private Filter filter = Filter.ARTIST;
	
	private enum Filter {
		
		ARTIST, ALBUM, GENRE, YEAR, BPM, KEY, COMPOSER, BIT_RATE;
	}
	
	/**
	 * The operation to be done by the Service
	 */
	private Operation operation = Operation.UNKNOWN;
	/**
	 * The given artistName
	 */
	private String filterValue = "";
	
	/**
	 * Service Progress
	 */
	private int progress;
	
	/**
	 * Service Total Progress
	 */
	private int totalProgress;
	
	private final MediaTagsService allDetailsService;
	
	/**
	 * Constructor
	 * 
	 * @param smartController
	 */
	public FiltersModeService(SmartControllerFiltersMode smartControllerArtistsMode) {
		this.smartControllerArtistsMode = smartControllerArtistsMode;
		this.allDetailsService = new MediaTagsService();
	}
	
	/**
	 * Regenerates all the artists for the ArtistsMode
	 */
	public void regenerate() {
		this.operation = Operation.REFRESH;
		
		//Clear List
		smartControllerArtistsMode.getListView().getItems().clear();
		
		//Progress Label
		smartControllerArtistsMode.getProgressLabel().setText("Generating ...");
		
		//determineFilter()
		determineFilter(false);
		
		//Restart the Service
		this.restart();
	}
	
	/**
	 * Refreshes the TableView based on the current artist
	 * 
	 * @param filterValue
	 */
	public void refreshTableView(String filterValue) {
		this.filterValue = filterValue;
		this.operation = Operation.UPDATE_TABLE_VIEW;
		
		//determineFilter()
		determineFilter(true);
		
		//Restart the Service
		this.restart();
	}
	
	private void determineFilter(boolean changeLabel) {
		switch ( ( (MenuItem) smartControllerArtistsMode.getSelectedFilter().getSelectedToggle() ).getText()) {
			case "Artist":
				filter = Filter.ARTIST;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs from artist [ " + filterValue + " ]");
				break;
			case "Album":
				filter = Filter.ALBUM;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs from album [ " + filterValue + " ]");
				break;
			case "Genre":
				filter = Filter.GENRE;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs with genre [ " + filterValue + " ]");
				break;
			case "Year":
				filter = Filter.YEAR;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs from year [ " + filterValue + " ]");
				break;
			case "BPM":
				filter = Filter.BPM;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs with bpm [ " + filterValue + " ]");
				break;
			case "Key":
				filter = Filter.KEY;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs with key [ " + filterValue + " ]");
				break;
			case "Composer":
				filter = Filter.COMPOSER;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs from composer [ " + filterValue + " ]");
				break;
			case "Bit Rate":
				filter = Filter.BIT_RATE;
				if (changeLabel)
					smartControllerArtistsMode.getProgressLabel().setText("Detecting songs with Bit Rate [ " + filterValue + " ]");
				break;
		}
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			/**
			 * [[SuppressWarningsSpartan]]
			 */
			@Override
			protected Void call() throws Exception {
				
				//Create a new LinkedHashSet
				Set<String> set = new HashSet<>();
				List<Media> matchingMediaList = new ArrayList<>();
				
				try {
					
					//Total and Count = 0
					progress = totalProgress = 0;
					
					//================Prepare based on the Files User want to Export=============
					if (smartControllerArtistsMode.getSmartController().getGenre() == Genre.SEARCHWINDOW) {  // CURRENT_PAGE
						//System.out.println("Entered for Search Window");
						
						//Count total files that will be exported
						totalProgress = smartControllerArtistsMode.getSmartController().getItemsObservableList().size();
						
						// Stream
						Stream<Media> stream = smartControllerArtistsMode.getSmartController().getItemsObservableList().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								
								if (operation == Operation.REFRESH)
									
									//Add the artist
									set.add(findTagFromAudioFile(new File(media.getFilePath()), media));
								
								else {
									
									//Find the artist Name
									String filterVal = findTagFromAudioFile(new File(media.getFilePath()), media);
									
									//If it equals
									if (filterVal.equals(filterValue))
										matchingMediaList.add(media);
									
								}
								
								//Update the progress
								updateProgress(++progress, totalProgress);
								
							}
						});
						
					} else if (smartControllerArtistsMode.getSmartController().getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST
						
						//Count total files that will be exported
						totalProgress = smartControllerArtistsMode.getSmartController().getTotalInDataBase();
						
						// Stream
						String query = "SELECT" + ( operation == Operation.UPDATE_TABLE_VIEW ? "*" : "(PATH)" ) + "FROM '"
								+ smartControllerArtistsMode.getSmartController().getDataBaseTableName() + "'";
						try (ResultSet resultSet = Main.dbManager.getConnection().createStatement().executeQuery(query);) {
							
							int counter = 0;
							// Fetch the items from the database
							while (resultSet.next()) {
								if (isCancelled())
									break;
								else {
									
									if (operation == Operation.REFRESH)
										
										//Add the artist
										set.add(findTagFromAudioFile(new File(resultSet.getString("PATH")), null));
									
									else {
										
										//Find the artist Name
										String filterVal = findTagFromAudioFile(new File(resultSet.getString("PATH")), null);
										
										//If it equals
										if (filterVal.equals(filterValue))
											//Add the Media
											matchingMediaList.add(new Audio(resultSet.getString("PATH"), resultSet.getDouble("STARS"), resultSet.getInt("TIMESPLAYED"),
													resultSet.getString("DATE"), resultSet.getString("HOUR"), smartControllerArtistsMode.getSmartController().getGenre(),
													++counter));
										
									}
									
									//Update the progress
									updateProgress(++progress, totalProgress);
								}
							}
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
						}
						
					}
					
					if (operation == Operation.REFRESH) {
						//For each item on set
						set.remove("");
						ObservableList<String> observableList = set.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
						Platform.runLater(() -> {
							
							//Details Label text
							if (smartControllerArtistsMode.getSmartController().getTotalInDataBase() == 0) {
								smartControllerArtistsMode.getDetailsLabel().setText("Playlist has no songs");
								smartControllerArtistsMode.getDetailsLabel().setVisible(true);
							} else if (observableList.isEmpty()) {
								smartControllerArtistsMode.getNothingFoundLabel().setVisible(true);
							} else {
								smartControllerArtistsMode.getDetailsLabel().setVisible(false);
								smartControllerArtistsMode.getNothingFoundLabel().setVisible(false);
							}
							
							//Set list view items
							smartControllerArtistsMode.getListView().setItems(observableList);
							if (!observableList.isEmpty())
								smartControllerArtistsMode.getListView().getSelectionModel().select(0);
							else
								//Empty the TableView
								smartControllerArtistsMode.getMediaTableViewer().getTableView().getItems().clear();
						});
					} else if (operation == Operation.UPDATE_TABLE_VIEW) {
						//For each item on set
						ObservableList<Media> observableList = matchingMediaList.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
						Platform.runLater(() -> {
							
							//Details Label text
							if (smartControllerArtistsMode.getSmartController().getTotalInDataBase() == 0) {
								smartControllerArtistsMode.getDetailsLabel().setText("Playlist has no songs");
								smartControllerArtistsMode.getDetailsLabel().setVisible(true);
								//							} else if (smartControllerArtistsMode.getListView().getItems().isEmpty()) {
								//								smartControllerArtistsMode.getDetailsLabel().setText("No artists found");
								//								smartControllerArtistsMode.getDetailsLabel().setVisible(true);
							} else {
								smartControllerArtistsMode.getDetailsLabel().setVisible(false);
							}
							
							//Check if any songs are containing this artist
							if (!observableList.isEmpty()) {
								
								//Set list view items					
								smartControllerArtistsMode.getMediaTableViewer().getTableView().setItems(observableList);
								smartControllerArtistsMode.getSmartController().updateLabel();
								
								//Populate Media Information
								allDetailsService.restartService(smartControllerArtistsMode.getMediaTableViewer());
							} else {
								
								//Remove the artist from the List
								smartControllerArtistsMode.getListView().getItems().remove(filterValue);
								
								//Empty the TableView
								smartControllerArtistsMode.getMediaTableViewer().getTableView().getItems().clear();
							}
							
						});
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return null;
				
			}
			
			/**
			 * Return the artist of the given audio file (mp3) actually
			 * 
			 * @param file
			 *            The audio File
			 * @param media
			 *            The audio File in case it is already a Media Class File
			 * @return Return the artist of the given audio file (mp3) actually
			 */
			private String findTagFromAudioFile(File file , Media media) {
				//System.out.println(file.getName());
				
				//Check file existence , length and extension
				if (file.exists() && "mp3".equals(media != null ? media.getFileType() : InfoTool.getFileExtension(file.getAbsolutePath())) && file.length() != 0) {
					
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
						
						if (filter == Filter.ARTIST)
							return tag.getFirst(ID3v24FieldKey.ARTIST);
						else if (filter == Filter.ALBUM)
							return tag.getFirst(ID3v24FieldKey.ALBUM);
						else if (filter == Filter.GENRE)
							return tag.getFirst(ID3v24FieldKey.GENRE);
						else if (filter == Filter.YEAR)
							return tag.getFirst(ID3v24FieldKey.YEAR);
						else if (filter == Filter.BPM)
							return tag.getFirst(ID3v24FieldKey.BPM);
						else if (filter == Filter.KEY)
							return tag.getFirst(ID3v24FieldKey.KEY);
						else if (filter == Filter.COMPOSER)
							return tag.getFirst(ID3v24FieldKey.COMPOSER);
						else if (filter == Filter.BIT_RATE)
							return mp3File.getMP3AudioHeader().getBitRate();
						
						//System.out.println("Artist : " + artist);// + " , Album Artist : " + tag.getFirst(ID3v24FieldKey.ALBUM_ARTIST))
						
					} else if (mp3File.hasID3v1Tag()) {
						
						ID3v1Tag tag = mp3File.getID3v1Tag();
						
						if (filter == Filter.ARTIST)
							return tag.getFirst(ID3v1FieldKey.ARTIST.toString());
						else if (filter == Filter.ALBUM)
							return tag.getFirst(ID3v1FieldKey.ALBUM.toString());
						else if (filter == Filter.GENRE)
							return tag.getFirst(ID3v1FieldKey.GENRE.toString());
						else if (filter == Filter.YEAR)
							return tag.getFirst(ID3v1FieldKey.YEAR.toString());
					}
					
				}
				
				return "";
			}
			
		};
	}
	
}
