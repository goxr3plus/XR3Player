package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import com.goxr3plus.xr3player.controllers.general.OnlineMusicBoxController.OnlineMusicCategory;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * @author GOXR3PLUSSTUDIO
 *
 */
public class OnlineMusicController extends StackPane {

	// -------------------------------------------------------------

	@FXML
	private FlowPane recommendationsView;

	@FXML
	private FlowPane editorsPickView;

	@FXML
	private FlowPane activitiesView;

	@FXML
	private FlowPane genresView;

	@FXML
	private FlowPane chartsView;

	@FXML
	private FlowPane moodView;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Constructor.
	 */
	public OnlineMusicController() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
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

		// -----------------------------------recommendationsView---------------------------------------
		recommendationsView.getChildren().clear();

		// recommendationsView - children
		recommendationsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=xpVfcZ0ZcFM&list=PLFgquLnL59alCl_2TQvOiD5Vgm1hCaGSI",
						"Top Songs", OnlineMusicCategory.RECOMMENDED));
		recommendationsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=oRArmtMA9AI&list=PLFgquLnL59alW3xmYiWRaoz0oM3H17Lth",
						"Latest Songs", OnlineMusicCategory.RECOMMENDED));

		// -----------------------------------genresView---------------------------------------
		genresView.getChildren().clear();

		// genresView - children
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=LJyin-yC0Gg&list=PLH6pfBXQXHEBElcVFl-gGewA2OaATF4xL",
						"Hip-Hop", OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=I1P7GoZcZpU&list=PLvLX2y1VZ-tHnQyOqyemaWjZjrJYr8ksp",
						"Country", OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=aMqB5V-UqOg&list=PLFRSDckdQc1vs7FJS-it5nCreCQ3UGQEp", "R&B",
						OnlineMusicCategory.GENRES));
		genresView.getChildren().add(new OnlineMusicBoxController(
				"https://www.youtube.com/watch?v=2Vv-BfVoq4g&list=RDQMVwMt5OuvNAM", "Pop", OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=7YAAyUFL1GQ&list=PLhd1HyMTk3f5S98HGlByL2eH1T3n6J-bR", "Rock",
						OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=21LGv8Cf0us&list=PLMcThd22goGYit-NKu2O8b4YMtwSTK9b9", "Jazz",
						OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=cPAbx5kgCJo&list=PLQog_FHUHAFUDDQPOTeAWSHwzFV1Zz5PZ", "Soul",
						OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=kSE15tLBdso&list=PLRb-5mC4V_Lop8KLXqSqMv4_mqw5M9jjW",
						"Classical", OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=wmC4Iw5ht6E&list=PLvP_6uwiamDS23WxoCfqY4LBOXF_yF1l9", "Punk",
						OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=NTe6-26-fHg&list=PLFPg_IUxqnZNTAbUMEZ76_snWd-ED5en7",
						"Electronic", OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=6DRkf3kZMXw&list=PLcfQmtiAG0X-fmM85dPlql5wfYbmFumzQ", "Latin",
						OnlineMusicCategory.GENRES));
		genresView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=D1HKBgoCWaM&list=PLx6QZyNaHQCxh4qw6r4hy68tGll9Oys9f", "Folk",
						OnlineMusicCategory.GENRES));

		// -----------------------------------activitiesView---------------------------------------
		activitiesView.getChildren().clear();

		// activitiesView - children
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=k2qgadSvNyU&list=PLChOO_ZAB22WuyDODJ3kjJiU0oQzWOTyb",
						"Workout", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=0yW7w8F2TVA&list=PLHwn8cKeb1J2TOechY-gogb9DZNwIwzIJ",
						"Morning", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=JQbjS0_ZfJ0&list=PLw-VjHDlEOgsqXyMWCuM-E7Ft-Fns_gn5", "Travel",
						OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController("https://www.youtube.com/watch?v=e82VE8UtW8A&list=PL127EBAC6281CC2E3",
						"Pub", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=5GZGUM6j9tQ&list=PLOzlQzU-yr65nTKt7tEbHBnnd0AiGJoqZ",
						"Reading", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=kSE15tLBdso&list=PLRb-5mC4V_Lop8KLXqSqMv4_mqw5M9jjW",
						"High Tea", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=pBjHZiU-2nQ&list=PLl28ky4zTcricJ6TA8trUGf7tmfGl1FFf", "Sleep",
						OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=RBumgq5yVrA&list=PLo3pNg0eiPc_JHZ-1jjCYbup7_rT3CBl8",
						"Late Night", OnlineMusicCategory.ACTIVITIES));
		activitiesView.getChildren()
				.add(new OnlineMusicBoxController("https://www.youtube.com/watch?v=4Q46xYqUwZQ&list=PL08B5FA401C541429",
						"Gaming", OnlineMusicCategory.ACTIVITIES));

		// -----------------------------------moodView---------------------------------------
		moodView.getChildren().clear();

		// moodView - children
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=NGLxoKOvzu4&list=PLinS5uF49IBo8HLKBDAjQaeiN4TjHi75Q", "Happy",
						OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=b-3BI9AspYc&list=PL8juZvZzhy3Z-NKva-HeiGyTz4-xur-V6", "Sweet",
						OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=YQHsXMglC9A&list=PL4duD-N02Jf0zQAsOHB34ybyRl-qMh8Zb",
						"Heart-broken", OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Motivational+music+playlist", "Motivational",
						OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Soft+and+Relaxing+music+playlist",
						"Soft & Relaxing", OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=987xHnVfRI4&list=PLGoCPtHY448ekqpLZmMT-AvA5aOKza4Go", "Lonely",
						OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=817P8W8-mGE&list=PLRNGf69jPtYLyoCALT1l1_5H6O2AaqkAH",
						"Romantic", OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=0G3_kG5FFfQ&list=PL6onCQ7SIRKas5oBUi0ylBiXKoypaKBW4",
						"Miss You", OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=papuvlVeZg8&list=PLNyF4u9mdcWo3Gy6k_UGxzzcwacLqF0Pi",
						"Refreshed", OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=UAWcs5H-qgQ&list=PLzzwfO_D01M4nNqJKR828zz6r2wGikC5a", "Moved",
						OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=OUz0T7ixeEk&list=PL2D1cxeX5H2_tf7ODlXYu9SJ6SO0y4ySW",
						"Reminiscene", OnlineMusicCategory.MOOD));
		moodView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/watch?v=8inJtTG_DuU&list=PLgKDEvAN_N2wF8bX1Cp-QtRkArhEytAx2", "Calm",
						OnlineMusicCategory.MOOD));

		// -----------------------------------chartsView---------------------------------------
		chartsView.getChildren().clear();

		// chartsView - children
		chartsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Billboard+Hot+100+%7C+Top+50+Songs+this+week",
						"Billboard", OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController("https://www.youtube.com/results?search_query=Spotify+Global+Top+50",
						"Spotify", OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController("https://www.youtube.com/results?search_query=Top+40+ITunes+Songs",
						"iTunes", OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Top+Hits+%7C+Trap+Nation", "Trap Nation",
						OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Top+100+Electro+House+Songs+", "Beatport",
						OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=The+Official+UK+Top+40+Singles+Chart", "BBC UK",
						OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Toip+40+Hits+Video+Hits+Channel", "Channel V",
						OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController("https://www.youtube.com/results?search_query=300+Top+Chinese+Songs",
						"Oricon J-pop", OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController("https://www.youtube.com/results?search_query=Melon+Dailty+Top+100",
						"Mnet K-pop", OnlineMusicCategory.CHARTS));
		chartsView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=2018+KKBOX+Chinese+POP+Weekly+Charts",
						"Hito Chinese", OnlineMusicCategory.CHARTS));

		// -----------------------------------editorsPickView---------------------------------------
		editorsPickView.getChildren().clear();

		// editorsPickView - children
		editorsPickView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Best+Live+Performance++-+Live+Music",
						"Live Music", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Nature+Relaxing+White+Noise", "Soft Noise",
						OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren().add(new OnlineMusicBoxController("https://www.internet-radio.com/",
				"Internet Radio", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Spotlight+on+Grammy+Winners",
						"2017 Grammy Winners", OnlineMusicCategory.EDITORSPICK));
		editorsPickView.getChildren()
				.add(new OnlineMusicBoxController(
						"https://www.youtube.com/results?search_query=Spotline+On%3A+Youtube+Covers+Music",
						"Youtube Covers", OnlineMusicCategory.EDITORSPICK));
	}

}
