package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import main.java.com.goxr3plus.xr3player.application.presenter.OnlineMusicBoxController.OnlineMusicCategory;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * @author GOXR3PLUSSTUDIO
 *
 */
public class OnlineMusicController extends StackPane {
	
	// -------------------------------------------------------------
	
	@FXML
	private TilePane recommendationsView;
	
	@FXML
	private TilePane genresView;
	
	@FXML
	private TilePane activitiesView;
	
	@FXML
	private TilePane moodView;
	
	@FXML
	private TilePane chartsView;
	
	@FXML
	private TilePane editorsPickView;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * Constructor.
	 */
	public OnlineMusicController() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "OnlineMusic.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .fxml is initialised
	 */
	@FXML
	private void initialize() {
		
		//-----------------------------------recommendationsView---------------------------------------
		recommendationsView.getChildren().clear();
		
		//recommendationsView - children
		recommendationsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Top Songs", OnlineMusicCategory.RECOMMENDED));
		recommendationsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Latest Songs", OnlineMusicCategory.RECOMMENDED));
		
		//-----------------------------------genresView---------------------------------------
		genresView.getChildren().clear();
		
		//genresView - children
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Hip-Hop", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Country", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "R&B", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Pop", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Rock", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Jazz", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Soul", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Classical", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Punk", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Electronic", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Latin", OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Folk", OnlineMusicCategory.GENRES));
		
		//-----------------------------------activitiesView---------------------------------------
		activitiesView.getChildren().clear();
		
		//activitiesView - children
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Workout", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Morning", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Travel", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Pub", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Reading", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "High Tea", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Sleep", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Late Night", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Gaming", OnlineMusicCategory.ACTIVITIES));
		
		//-----------------------------------moodView---------------------------------------
		moodView.getChildren().clear();
		
		//moodView - children
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Happy", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Sweet", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Heart-broken", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Motivational", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Soft & Relaxing", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Lonely", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Romantic", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Miss You", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Refreshed", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Moved", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Reminiscene", OnlineMusicCategory.MOOD));
		moodView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Calm", OnlineMusicCategory.MOOD));
		
		//-----------------------------------chartsView---------------------------------------
		chartsView.getChildren().clear();
		
		//chartsView - children
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Billboard", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Spotify", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "iTunes", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Trap Nation", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Beatport", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "BBC UK", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Channel V", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Oricon J-pop", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Mnet K-pop", OnlineMusicCategory.CHARTS));
		chartsView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Hito Chinese", OnlineMusicCategory.CHARTS));
		
		//-----------------------------------editorsPickView---------------------------------------
		editorsPickView.getChildren().clear();
		
		//editorsPickView - children
		editorsPickView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Live Music", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Soft Noise", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Internet Radio", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "2017 Grammy Winners", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren().add(new OnlineMusicBoxController("https://www.youtube.com", "Youtube Covers", OnlineMusicCategory.EDITORSPICK));
	}
	
}
