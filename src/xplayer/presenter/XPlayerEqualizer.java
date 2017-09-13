/*
 * 
 */
package xplayer.presenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.presenter.custom.DJDisc2;
import application.presenter.custom.DJFilter;
import application.presenter.custom.DragAdjustableLabel;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

/**
 * The Class XPlayerEqualizer.
 */
public class XPlayerEqualizer extends BorderPane {
	
	@FXML
	private HBox bottomHBox;
	
	@FXML
	private Menu presets;
	
	@FXML
	private MenuItem resetFilters;
	
	@FXML
	private TilePane tilePane;
	
	//----------------------------------------------------
	
	/** The logger for this class */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	/** The color. */
	private Color discArcColor = Color.BLUEVIOLET;
	
	/** The pan filter. */
	private DJFilter panFilter;
	
	/** The balance filter. */
	private DJFilter balanceFilter;
	
	/** The amplitude filter. */
	private DJFilter amplitudeFilter;
	
	/** The x player UI. */
	private final XPlayerController xPlayerController;
	
	/** The filters. */
	private Filter[] filters = new Filter[32];
	
	private final double[] PRESET_NORMAL = { 50 , 50 , 50 , 50 , 50 , 50 , 50 , 50 , 50 , 50 };
	private final double[] PRESET_CLASSICAL = { 50 , 50 , 50 , 50 , 50 , 50 , 70 , 70 , 70 , 76 };
	private final double[] PRESET_CLUB = { 50 , 50 , 42 , 34 , 34 , 34 , 42 , 50 , 50 , 50 };
	private final double[] PRESET_DANCE = { 26 , 34 , 46 , 50 , 50 , 66 , 70 , 70 , 50 , 50 };
	private final double[] PRESET_FULLBASS = { 26 , 26 , 26 , 36 , 46 , 62 , 76 , 78 , 78 , 78 };
	private final double[] PRESET_FULLBASSTREBLE = { 34 , 34 , 50 , 68 , 62 , 46 , 28 , 22 , 18 , 18 };
	private final double[] PRESET_FULLTREBLE = { 78 , 78 , 78 , 62 , 42 , 24 , 8 , 8 , 8 , 8 };
	private final double[] PRESET_LAPTOP = { 38 , 22 , 36 , 60 , 58 , 46 , 38 , 24 , 16 , 14 };
	private final double[] PRESET_LIVE = { 66 , 50 , 40 , 36 , 34 , 34 , 40 , 42 , 42 , 42 };
	private final double[] PRESET_PARTY = { 32 , 32 , 50 , 50 , 50 , 50 , 50 , 50 , 32 , 32 };
	private final double[] PRESET_POP = { 56 , 38 , 32 , 30 , 38 , 54 , 56 , 56 , 54 , 54 };
	private final double[] PRESET_REGGAE = { 48 , 48 , 50 , 66 , 48 , 34 , 34 , 48 , 48 , 48 };
	private final double[] PRESET_ROCK = { 32 , 38 , 64 , 72 , 56 , 40 , 28 , 24 , 24 , 24 };
	private final double[] PRESET_TECHNO = { 30 , 34 , 48 , 66 , 64 , 48 , 30 , 24 , 24 , 28 };
	
	/** The filter buttons. */
	//where 0.0 is equivalent to 0.50 for FilterButton 
	private FilterButton[] filterButtons = new FilterButton[]{ new FilterButton("Normal", PRESET_NORMAL) , new FilterButton("Classical", PRESET_CLASSICAL) ,
			new FilterButton("Club", PRESET_CLUB) , new FilterButton("Dance", PRESET_DANCE) , new FilterButton("FullBass", PRESET_FULLBASS) ,
			new FilterButton("FullBassTreble", PRESET_FULLBASSTREBLE) , new FilterButton("FullTreble", PRESET_FULLTREBLE) , new FilterButton("Laptop", PRESET_LAPTOP) ,
			new FilterButton("Live", PRESET_LIVE) , new FilterButton("Party", PRESET_PARTY) , new FilterButton("Pop", PRESET_POP) , new FilterButton("Reggae", PRESET_REGGAE) ,
			new FilterButton("Rock", PRESET_ROCK) , new FilterButton("Techno", PRESET_TECHNO) };
	
	/**
	 * Constructor.
	 *
	 * @param xPlayerController
	 *            The user Interface Controller of the Player
	 */
	public XPlayerEqualizer(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		discArcColor = xPlayerController.getDiscColor();
		
		// ----------------------------------FXMLLoader-------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "XPlayerEqualizer.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		// -------------Load the FXML-------------------------------
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as FXML file has been loaded
	 */
	@FXML
	private void initialize() {
		
		// TilePane
		for (int counter = 0; counter < 32; counter++)
			filters[counter] = new Filter(36, 36, counter);
		//tilePane.getChildren().addAll(filters);
		
		List<DJDisc2> dragLabels = new ArrayList<>();
		for (int i = 0; i < 32; i++)
			dragLabels.add(new DJDisc2(30, 30, discArcColor));
		tilePane.getChildren().addAll(dragLabels);
		
		//resetFilers
		resetFilters.setOnAction(action -> {
			//Balance
			balanceFilter.setAngle(100, 200);
			xPlayerController.getxPlayer().setBalance((float) 0.0);
			
			//Pan
			panFilter.setAngle(100, 200);
			xPlayerController.getxPlayer().setPan(0.0);
			
			//Reset the equalizer
			for (int i = 0; i < 32; i++)
				xPlayerController.getxPlayerModel().getEqualizerArray()[i] = 0.0f;
			xPlayerController.getxPlayer().setEqualizer(xPlayerController.getxPlayerModel().getEqualizerArray(), 32);
			for (Filter filter : filters)
				filter.resetToZero();
		});
		
		// Add all
		presets.getItems().addAll(filterButtons);
		
		//-------------------------- Extra Filters--------------------------
		
		// -- panFilter
		panFilter = new DJFilter(36, 36, Color.WHITE, Color.GOLD, Color.BLACK);
		panFilter.setOnMouseDragged(drag -> {
			panFilter.onMouseDragged(drag);
			xPlayerController.getxPlayer().setPan(panFilter.getValue(200));
		});
		
		// --balanceFilter
		balanceFilter = new DJFilter(36, 36, Color.WHITE, Color.GOLD, Color.BLACK);
		balanceFilter.setOnMouseDragged(drag -> {
			balanceFilter.onMouseDragged(drag);
			xPlayerController.getxPlayer().setBalance(balanceFilter.getValue(200));
		});
		
		//tilePane.getChildren().add(0,balanceFilter);
		//tilePane.getChildren().add(0, panFilter);
	}
	
	/**
	 * @return the panFilter
	 */
	public DJFilter getPanFilter() {
		return panFilter;
	}
	
	/**
	 * @return the balanceFilter
	 */
	public DJFilter getBalanceFilter() {
		return balanceFilter;
	}
	
	/**
	 * The Class Filter.
	 */
	public class Filter extends DJFilter {
		
		/** The position. */
		int position;
		
		/**
		 * Constructor.
		 *
		 * @param width
		 *            the width
		 * @param height
		 *            the height
		 * @param position
		 *            the position
		 */
		public Filter(double width, double height, int position) {
			super(width, height, Color.BLACK, discArcColor, Color.WHITE);
			this.position = position;
			setPadding(new Insets(5, 5, 5, 5));
			
			setOnMouseDragged(m -> {
				//Draw the filter
				super.onMouseDragged(m);
				
				//Add the filter
				xPlayerController.getxPlayerModel().getEqualizerArray()[position] = getValue(200);
				xPlayerController.getxPlayer().setEqualizerKey(xPlayerController.getxPlayerModel().getEqualizerArray()[position], position);
			});
			
		}
		
		/** Reset to 0.0f */
		public void resetToZero() {
			setAngle(100, 200);
		}
		
	}
	
	/**
	 * The Class FilterButton.
	 */
	public class FilterButton extends MenuItem {
		
		/** The vars. */
		double[] vars;
		
		/**
		 * Instantiates a new filter button.
		 *
		 * @param text
		 *            the text
		 * @param variables
		 *            the variables
		 */
		public FilterButton(String text, double[] variables) {
			
			vars = variables;
			setText(text);
			for (int i = 0; i < variables.length; i++)
				variables[i] = variables[i] / 100.00 - 0.5;
			
			// System.out.println(variables[0])
			
			setOnAction(action -> {
				// Pass the values to the array
				for (int y = 0; y < 10; y++)
					xPlayerController.getxPlayerModel().getEqualizerArray()[y] = (float) vars[y];
				
				// Set the filter
				xPlayerController.getxPlayer().setEqualizer(xPlayerController.getxPlayerModel().getEqualizerArray(), 32);
				
				// Change the angles on the filters
				for (int i = 0; i < 10; i++)
					filters[i].setAngle(xPlayerController.getxPlayerModel().getEqualizerArray()[i], 200);
				
			});
			
		}
		
	}
	
}
