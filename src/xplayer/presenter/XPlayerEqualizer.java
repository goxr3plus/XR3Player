/*
 * 
 */
package xplayer.presenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.presenter.custom.DJFilter;
import application.presenter.custom.DJFilterListener;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * The Class XPlayerEqualizer.
 */
public class XPlayerEqualizer extends BorderPane {
	
	@FXML
	private HBox bottomHBox;
	
	@FXML
	private VBox sideVBox;
	
	@FXML
	private Menu presets;
	
	@FXML
	private MenuItem resetFilters;
	
	@FXML
	private TilePane tilePane;
	
	//----------------------------------------------------
	
	/** The logger for this class */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private final XPlayerController xPlayerController;
	
	//	/** The pan filter. */
	//	private DJFilter panFilter;
	//	
	//	/** The balance filter. */
	//	private DJFilter balanceFilter;
	//	
	//	/** The amplitude filter. */
	//	private DJFilter amplitudeFilter;
	
	//================================================================================================
	
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
	 * An array list of available DJFilters
	 */
	List<CustomDJFilter> djFilters = new ArrayList<>();
	
	//================================================================================================
	
	/**
	 * Constructor.
	 *
	 * @param xPlayerController
	 *            The user Interface Controller of the Player
	 */
	public XPlayerEqualizer(XPlayerController xPlayerController) {
		this.xPlayerController = xPlayerController;
		
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
		
		//Add all the DJFilters		
		for (int i = 0; i < 32; i++) {
			//---VBOX------
			VBox vBox = new VBox();
			
			//---Label
			Label label = new Label("0.0");
			label.getStyleClass().add("applicationSettingsLabel2");
			label.setMinWidth(35);
			label.setMaxWidth(35);
			//label.textProperty()
			//.bind(Bindings.createStringBinding(() -> InfoTool.getMinString2(Double.toString(filter.currentValueProperty().get()), 4), filter.currentValueProperty()));
			
			//-----CustomDJFilter
			CustomDJFilter filter = new CustomDJFilter(30, 30, xPlayerController.getDiscArcColor(), 0.5, -1.0, 1.0, i, label);
			
			//Add the Children
			vBox.getChildren().addAll(filter, label);
			
			//Add VBox to TilePane
			tilePane.getChildren().add(vBox);
			
			//Add to djFilters
			djFilters.add(filter);
		}
		
		//-------------------------- Extra Filters--------------------------
		
		// --------------------Pan Filter---------------------------------
		//---VBOX------
		VBox vBox = new VBox();
		
		//---Label
		Label label = new Label("0.5");
		label.getStyleClass().add("applicationSettingsLabel2");
		label.setMinWidth(35);
		label.setMaxWidth(35);
		
		//-----CustomDJFilter
		DJFilter panFilter = new DJFilter(30, 30, Color.GOLD, 0.5, -1.0, 1.0);
		
		//Add the Children
		vBox.getChildren().addAll(panFilter, label);
		
		//Add VBox to TilePane
		panFilter.addEventFilter(MouseEvent.MOUSE_DRAGGED, d -> {
			label.setText(String.valueOf(panFilter.getValue()));
			xPlayerController.getxPlayer().setPan(panFilter.getValue());
		});
		
		// --------------------Balance Filter---------------------------------
		
		//---VBOX------
		VBox vBox2 = new VBox();
		
		//---Label
		Label label2 = new Label("0.5");
		label2.getStyleClass().add("applicationSettingsLabel2");
		label2.setMinWidth(35);
		label2.setMaxWidth(35);
		
		//-----CustomDJFilter
		DJFilter balanceFilter = new DJFilter(30, 30, Color.GOLD, 0.5, -1.0, 1.0);
		
		//Add the Children
		vBox2.getChildren().addAll(balanceFilter, label2);
		
		//Add VBox to TilePane
		balanceFilter.addEventFilter(MouseEvent.MOUSE_DRAGGED, d -> {
			label2.setText(String.valueOf(panFilter.getValue()));
			xPlayerController.getxPlayer().setBalance((float) panFilter.getValue());
		});
		
		sideVBox.getChildren().addAll(vBox, vBox2);
		vBox.setDisable(true);
		vBox2.setDisable(true);
		
		//resetFilers
		resetFilters.setOnAction(action -> {
			//Balance
//			panFilter.setValue(0.5);
//			label.setText(value);
//			xPlayerController.getxPlayer().setBalance((float) 0.0);
//			
//			//Pan
//			panFilter.setAngle(100, 200);
//			xPlayerController.getxPlayer().setPan(0.0);
			
			//Reset the equalizer
			for (int i = 0; i < 32; i++)
				xPlayerController.getxPlayerModel().getEqualizerArray()[i] = 0.0f;
			xPlayerController.getxPlayer().setEqualizer(xPlayerController.getxPlayerModel().getEqualizerArray(), 32);
			
			//Reset every  filter to it's default value 
			for (DJFilter filter : djFilters)
				filter.setValue(0.5);
			
		});
		
		// Add all
		presets.getItems().addAll(filterButtons);
		
	}
	
	/**
	 * @author GOXR3PLUS
	 *
	 */
	private class CustomDJFilter extends DJFilter implements DJFilterListener {
		
		private int position;
		private Label label;
		
		/**
		 * Constructor
		 * 
		 * @param width
		 * @param height
		 * @param arcColor
		 * @param currentValue
		 * @param maximumValue
		 * @param maximumValue
		 * @param label
		 */
		public CustomDJFilter(int width, int height, Color arcColor, double currentValue, double minimumValue, double maximumValue, int position, Label label) {
			super(width, height, arcColor, currentValue, minimumValue, maximumValue);
			
			//Position
			this.position = position;
			
			//Label
			this.label = label;
			
			//Add the DJFilterListener
			super.addDJDiscListener(this);
			
		}
		
		@Override
		public void valueChanged(double value) {
			
			//Add the filter
			double filterValue = 0.0;
			if (getValue() == 0.5)
				filterValue = 0.0;
			else if (getValue() < 0.5)
				filterValue = -1.0 + getValue() * 2.00;
			else if (getValue() > 0.5)
				filterValue = Math.abs(1.0 - getValue() * 2.00);
			
			//Set the Text 
			label.setText(InfoTool.getMinString2(Double.toString(filterValue), filterValue > 0 ? 4 : 5));
			
			xPlayerController.getxPlayerModel().getEqualizerArray()[position] = (float) filterValue;
			xPlayerController.getxPlayer().setEqualizerKey(xPlayerController.getxPlayerModel().getEqualizerArray()[position], position);
		}
		
	}
	
	/**
	 * The Class FilterButton.
	 */
	@Deprecated
	public class FilterButton extends MenuItem {
		
		double[] variables;
		
		/**
		 * Instantiates a new filter button.
		 *
		 * @param text
		 *            the text
		 * @param variables
		 *            the variables
		 */
		public FilterButton(String text, double[] variables) {
			this.variables = variables;
			
			//Continue
			setText(text);
			for (int i = 0; i < variables.length; i++)
				variables[i] = ( 100 - variables[i] ) / 100.00;
			
			// System.out.println(variables[0])
			
			setOnAction(action -> {
				// Pass the values to the array
				for (int y = 0; y < 10; y++)
					xPlayerController.getxPlayerModel().getEqualizerArray()[y] = (float) variables[y];
				
				// Set the filter
				xPlayerController.getxPlayer().setEqualizer(xPlayerController.getxPlayerModel().getEqualizerArray(), 32);
				
				// Change the angles on the filters
				for (int i = 0; i < 10; i++) {
					
					//Transform the value in order to be compatible with the DJFilter -1.0 .... 1.0
					double fakeValue = xPlayerController.getxPlayerModel().getEqualizerArray()[i];
					System.out.println(fakeValue);
					double transformedValue = 0.0;
					
					//Find the best transform
					if (fakeValue == 0.0)
						transformedValue = 0.5;
					else if (fakeValue < 0.0)
						transformedValue = Math.abs(1.0 + fakeValue);
					else if (fakeValue > 0.0)
						transformedValue = Math.abs(fakeValue);
					
					//Apply
					djFilters.get(i).setValue(transformedValue);
				}
				
			});
			
		}
		
	}
	
}
