package main.java.com.goxr3plus.xr3player.smartcontroller.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.treeview.FileTreeItem;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.FilesMode;
import main.java.com.goxr3plus.xr3player.smartcontroller.enums.Genre;
import main.java.com.goxr3plus.xr3player.smartcontroller.media.Media;
import main.java.com.goxr3plus.xr3player.smartcontroller.modes.SmartControllerArtistsMode;

public class ArtistsModeService extends Service<Void> {
	
	/** A private instance of the SmartController it belongs */
	private final SmartControllerArtistsMode smartControllerArtistsMode;
	
	private final FilesCounterService filesCounterService = new FilesCounterService();
	
	/**
	 * The selected files mode , based on user settings
	 */
	private FilesMode filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;
	
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
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				//Create a new LinkedHashSet
				Set<String> set = new HashSet<>();
				filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;
				
				//Determine filesMode
				//				switch ( ( (Control) Main.settingsWindow.getPlayListsSettingsController().getWhichFilesToShowGenerally().getSelectedToggle() ).getTooltip().getText()) {
				//					case "1":
				//						filesMode = FilesMode.SELECTED_MEDIA;
				//						break;
				//					case "2":
				//						filesMode = FilesMode.CURRENT_PAGE;
				//						break;
				//					case "3":
				//						filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;
				//						break;
				//					default:
				//						filesMode = FilesMode.EVERYTHING_ON_PLAYLIST;
				//				}
				
				//Change Top Label Text
				//	Platform.runLater(() -> smartControllerArtistsMode.getTopLabel().setText("Associated Folders Explorer -> " + filesMode.toString()));
				
				//Change Details Label Text
				//	Platform.runLater(() -> smartControllerArtistsMode.getDetailsLabel().setText("No associated Folders found -> " + filesMode.toString()));
				
				try {
					
					//Total and Count = 0
					progress = totalProgress = 0;
					
					//================Prepare based on the Files User want to Export=============
					
					if (filesMode == FilesMode.SELECTED_MEDIA) { // SELECTED_FROM_CURRENT_PAGE
						
						//Count total files that will be exported
						totalProgress = smartControllerArtistsMode.getSmartController().getTableViewer().getSelectionModel().getSelectedItems().size();
						
						// Stream
						Stream<Media> stream = smartControllerArtistsMode.getSmartController().getTableViewer().getSelectionModel().getSelectedItems().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								//Add all the items to set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());
								
								//Update the progress
								updateProgress(++progress, totalProgress);
							}
						});
						
					} else if (filesMode == FilesMode.CURRENT_PAGE || smartControllerArtistsMode.getSmartController().getGenre() == Genre.SEARCHWINDOW) {  // CURRENT_PAGE
						System.out.println("Entered for Search Window");
						
						//Count total files that will be exported
						totalProgress = smartControllerArtistsMode.getSmartController().getItemsObservableList().size();
						
						// Stream
						Stream<Media> stream = smartControllerArtistsMode.getSmartController().getItemsObservableList().stream();
						stream.forEach(media -> {
							if (isCancelled())
								stream.close();
							else {
								//Add item path to the set
								set.add(new File(media.getFilePath()).getParentFile().getAbsolutePath());
								
								//Update the progress
								updateProgress(++progress, totalProgress);
							}
						});
						
					} else if (filesMode == FilesMode.EVERYTHING_ON_PLAYLIST && smartControllerArtistsMode.getSmartController().getGenre() != Genre.SEARCHWINDOW) { // EVERYTHING_ON_PLAYLIST
						
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
									File file = new File(resultSet.getString("PATH"));
									
									//exists ? + mp3 ?
									if (file.exists() && file.length() != 0 && "mp3".equals(InfoTool.getFileExtension(file.getAbsolutePath()))) {
										MP3File mp3File = new MP3File(file);
										
										String artist = "";
										
										if (mp3File.hasID3v2Tag()) {
											ID3v24Tag tag = mp3File.getID3v2TagAsv24();
											
											artist = tag.getFirst(ID3v24FieldKey.ARTIST);
											
											//System.out.println("Artist : " + artist);// + " , Album Artist : " + tag.getFirst(ID3v24FieldKey.ALBUM_ARTIST));
											
											set.add(artist);
										}
									}
									//Update the progress
									updateProgress(++progress, totalProgress);
								}
							
						} catch (Exception ex) {
							Main.logger.log(Level.WARNING, "", ex);
						}
						
					}
					
					//For each item on set
					ObservableList<String> observableList = set.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
					Platform.runLater(() -> smartControllerArtistsMode.getListView().setItems(observableList));
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return null;
			}
			
		};
	}
	
	/**
	 * Counts the files inside a folder or folders based on the implementation
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	private class FilesCounterService extends Service<Void> {
		
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				
				@Override
				protected Void call() throws Exception {
					
					//					//Append the total Files of each folder
					//					smartControllerArtistsMode.getRoot().getChildren().forEach(treeItem -> {
					//						int[] totalFiles = countFiles(new File( ( (FileTreeItem) treeItem ).getFullPath()));
					//						String text = treeItem.getValue() + " [ " + totalFiles[1] + " / " + totalFiles[0] + " ]" + " [ " + ( (FileTreeItem) treeItem ).getFullPath() + " ] ";
					//						
					//						Platform.runLater(() -> treeItem.setValue(text));
					//					});
					//					
					return null;
				}
				
				/**
				 * Count files in a directory (including files in all sub directories)
				 * 
				 * @param directory
				 *            The full path of the directory
				 * @return Position [0] Total number of files contained in this folder <br>
				 *         Position [1] Total number of files contained in this folder && inside the Playlist Database <br>
				 */
				private int[] countFiles(File dir) {
					int[] count = { 0 , 0 };
					
					//Folder exists?
					if (dir.exists())
						try {
							Files.walkFileTree(Paths.get(dir.getPath()), new HashSet<>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE,
									new SimpleFileVisitor<Path>() {
										@Override
										public FileVisitResult visitFile(Path file , BasicFileAttributes attrs) throws IOException {
											
											//System.out.println("It is symbolic link?"+Files.isSymbolicLink(file))
											
											if (InfoTool.isAudioSupported(file + ""))
												++count[0];
											if (smartControllerArtistsMode.getSmartController().containsFile(file.toAbsolutePath().toString()))
												++count[1];
											
											if (isCancelled()) {
												return FileVisitResult.TERMINATE;
											} else
												return FileVisitResult.CONTINUE;
										}
										
										@Override
										public FileVisitResult visitFileFailed(Path file , IOException e) throws IOException {
											System.err.printf("Visiting failed for %s\n", file);
											
											return FileVisitResult.SKIP_SUBTREE;
										}
										
										@Override
										public FileVisitResult preVisitDirectory(Path dir , BasicFileAttributes attrs) throws IOException {
											if (isCancelled()) {
												return FileVisitResult.TERMINATE;
											} else
												return FileVisitResult.CONTINUE;
										}
									});
						} catch (IOException e) {
							e.printStackTrace();
						}
					
					//System.out.println("Total Files=" + count[0])
					return count;
				}
				
			};
			
		}
		
	}
	
}
