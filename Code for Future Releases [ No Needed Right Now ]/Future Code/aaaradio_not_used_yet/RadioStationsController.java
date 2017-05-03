/*
 * 
 */
package aaaradio_not_used_yet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import aacode_to_be_used_in_future.TagsBar;
import application.Main;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import smartcontroller.Genre;
import smartcontroller.SmartController;
import tools.InfoTool;

/**
 * The Class StationsInfostracture.
 */
public class RadioStationsController extends StackPane {
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private Button addStation;
	
	// ----------------------------------------------------
	
	/** The SmartController for managing RadioStations. */
	//SmartController controller;
	
	/** The station config. */
	//StationConfigurator stationConfig;
	
	/** The music genres. */
	// All the musicGenres
	public static List<String> musicGenres = Arrays.asList("50s", "60s", "70s", "80s", "90s", "Adult Contemporary",
	        "African", "Alternative", "Ambient", "Americana", "Baladas", "Bass", "Big Band", "Big Beat", "Bluegrass",
	        "Blues ", "Bollywood", "Breakbeat", "Breakcore", "Breaks", "Calypso", "Caribbean", "Celtic", "Chill",
	        "Chillout", "Chinese", "Christian", "Christmas", "Classic Rock", "Classical", "Comedy", "Community",
	        "Complextro", "Country", "Cumbia", "Dance", "Dancehall", "DarkWave", "Decades", "Deep House", "Disco",
	        "Doo Wop", "Downtempo", "Drone", "Drum And Bass", "Dub", "Dubstep", "Easy Listening", "Ebm", "Edm",
	        "Electro", "Electro House", "Electronic", "Electronica", "Emo", "Eurodance", "Europop", "Experimental",
	        "Fidget House", "Flamenco", "Folk", "Freestyle", "Funk", "Fusion", "Gabber", "Game", "Garage", "Glitch Hop",
	        "Goa", "Gospel", "Goth", "Greek", "Grime", "Grindcore", "Grunge", "Happy Hardcore", "Hard Trance",
	        "Hardcore", "HardStyle", "Hardtechno", "Heavy Metal", "Hindi", "Hip Hop", "House", "Idm", "Indian", "Indie",
	        "Industrial", "Irish", "Japanese", "Jazz", "Jewish", "Jpop", "Jumpstyle", "Jungle", "Kizomba", "Kpop",
	        "Latin", "Lounge", "Makina", "Manele", "Meditation", "Merengue", "Metal", "Minimal", "Moonbahton", "Mor",
	        "Nature", "Neurofunk", "Neurohop", "New Age", "New Wave", "News", "Noise", "Nu Jazz", "Oldies", "Opera",
	        "Orchestra", "Piano", "Polka", "Pop", "Progressive", "Psybient", "Psybreaks", "Psychedelic", "Psychobilly",
	        "Psytrance", "Punk", "Ragga", "Rap", "Rave", "Reggae", "Reggaeton", "Relaxation", "Religious", "Rnb",
	        "Rock", "Rockabilly", "Romania", "Romantic", "Roots", "Salsa", "Schlager", "Schranz", "Ska", "Smooth Jazz",
	        "Soca", "Soul", "Soundstracks", "Spanish", "Speedcore", "Sport", "Swing", "Synthpop", "Talk", "Tech House",
	        "Techno", "Tejano", "Tekno", "Thai", "Top 40", "Trance", "Trip Hop", "Turkish", "Urban", "Vocal",
	        "Western Swing", "World", "Zouk");
	
	/** The radio player. */
	// RadioPlayer
	//RadioPlayer radioPlayer = new RadioPlayer();
	
	/** The context menu. */
	// ContextMenu
	//public StationContextMenu contextMenu = new StationContextMenu();
	
	/**
	 * Constructor
	 */
	public RadioStationsController() {
		
		// ----------------------------------FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "RadioStationsController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		// -------------Load the FXML
		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
		
		// addStation("RadioAmi", "http://radio.flex.ru:8000/radionami");
		// addStation("Barique", "http://sc-baroque.1.fm:8045");
		// addStation("1.FM - Absolute TOP 40 Radio",
		// "http://185.33.21.112:11249/;?icy=http");
		// addStation("MegaFest FM",
		// "http://listen.radionomy.com/MegafestaFM?icy=http");
		
		// Bottom
		// setBottom(bottomBar);
		
	}
	
	/**
	 * Called as soon as fxml has been loaded
	 */
	@FXML
	public void initialize() {
//		System.out.println("StationsController initialized...");
//		
//		// Controller
//		controller = new SmartController(Genre.RADIOSTATION, "RadioController", InfoTool.radioStationsTable);
//		// setCenter(controller);
//		
//		// TagBar
//		TagsBar tagBar = new TagsBar();
//		tagBar.getEntries().addAll(musicGenres);
//		// setBottom(tagBar);
//		
//		// StationConfiguratior
//		stationConfig = new StationConfigurator();
//		
//		// addStation
//		addStation.setOnAction(e -> stationConfig.show(addStation));
		
	}
	
	/**
	 * The Class IOService.
	 */
	// TODO IOService
	public class IOService extends Service<Void> {
		
		/* (non-Javadoc)
		 * @see javafx.concurrent.Service#createTask() */
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					
					return null;
				}
			};
		}
		
	}
	
}
