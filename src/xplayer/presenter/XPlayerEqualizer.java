/*
 * 
 */
package xplayer.presenter;

import disc.DJFilter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import xplayer.model.XPlayer;

// TODO: Auto-generated Javadoc
/**
 * The Class XPlayerEqualizer.
 */
public class XPlayerEqualizer extends BorderPane {
	
	/** The color. */
	// Color and key
	Color				color			= Color.BLUEVIOLET;
	
	/** The key. */
	int					key;
	
	/** The pan filter. */
	DJFilter			panFilter;
	
	/** The balance filter. */
	DJFilter			balanceFilter;
	
	/** The amplitude filter. */
	DJFilter			amplitudeFilter;
	
	/** The x player UI. */
	XPlayerController	xPlayerUI;
	
	/** The filters. */
	Filter[]			filters			= new Filter[32];
	
	/** The filter buttons. */
	FilterButton[]		filterButtons	= new FilterButton[]{
			new FilterButton("Normal", new double[]{ 0.0 , 0.0 , 0. , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 }) ,
			new FilterButton("Club", new double[]{ .2 , .2 , .35 , .34 , .34 , .34 , .42 , .50 , .50 , .50 }) ,
			new FilterButton("Dance", new double[]{ .26 , .2 , .46 , .50 , .50 , .66 , .70 , .70 , .50 , .50 }) ,
			new FilterButton("FullBass", new double[]{ 0.4 , .4 , .26 , .36 , .46 , .62 , .76 , .78 , .78 , .78 }) ,
			new FilterButton("FullBassTreble",
					new double[]{ .34 , .34 , .50 , .68 , .62 , .46 , .28 , .22 , .18 , .18 }) ,
			new FilterButton("Live", new double[]{ .2 , .2 , .40 , .36 , .34 , .34 , .40 , .42 , .42 , .42 }) ,
			new FilterButton("Party", new double[]{ .15 , .15 , .50 , .50 , .50 , .50 , .50 , .50 , .32 , .32 }) ,
			new FilterButton("Rock", new double[]{ .32 , .38 , .64 , .72 , .56 , .40 , .28 , .24 , .24 , .24 }) };
	
	/**
	 * Constructor.
	 *
	 * @param xPlayerUI the x player UI
	 */
	public XPlayerEqualizer(XPlayerController xPlayerUI) {
		
		this.xPlayerUI = xPlayerUI;
		color = xPlayerUI.getDiscColor();
		
		//setWidth(350);
		//setHeight(300);
		setStyle("-fx-background-color:transparent;");
		
		// -> CENTER
		TilePane tilePane = new TilePane();
		tilePane.setStyle("-fx-background-color:transparent;");
		tilePane.setPrefColumns(8);
		tilePane.setHgap(5);
		tilePane.setVgap(5);
		for (int counter = 0; counter < 32; counter++)
			filters[counter] = new Filter(36, 36, counter);
		
		tilePane.getChildren().addAll(filters);
		
		setCenter(tilePane);
		
		// -> BOTTOM
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		
		// reset
		Button reset = new Button("Reset");
		reset.setPrefSize(70, 25);
		reset.setTooltip(new Tooltip("Reset all to zero"));
		
		reset.setOnAction(action -> {
			for (int i = 0; i < 32; i++)
				xPlayerUI.xPlayerModel.getEqualizerArray()[i] = 0.0f;
			
			xPlayerUI.xPlayer.setEqualizer(xPlayerUI.xPlayerModel.getEqualizerArray(), 32);
			
			for (Filter comp : filters)
				comp.resetToZero();
		});
		
		grid.add(reset, 0, 0);
		
		// Effects
		Button effects = new Button("Effects");
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(filterButtons);
		
		effects.setOnMouseReleased(m -> {
			contextMenu.show(effects, m.getScreenX(), m.getScreenY());
		});
		
		grid.add(effects, 1, 0);
		
		// Extra Filters
		panFilter = new DJFilter(2, 2, 36, 36, Color.YELLOW);
		grid.add(panFilter, 2, 0);
		panFilter.setOnMouseDragged(drag -> {
			panFilter.onMouseDragged(drag);
			xPlayerUI.xPlayer.setPan(panFilter.getValue(200));
		});
		
		balanceFilter = new DJFilter(40, 2, 36, 36, Color.YELLOW);
		grid.add(balanceFilter, 3, 0);
		balanceFilter.setOnMouseDragged(drag -> {
			balanceFilter.onMouseDragged(drag);
			xPlayerUI.xPlayer.setBalance(balanceFilter.getValue(200));
		});
		
		amplitudeFilter = new DJFilter(40, 2, 36, 36, Color.YELLOW);
		grid.add(amplitudeFilter, 4, 0);
		amplitudeFilter.setOnMouseDragged(drag -> {
			amplitudeFilter.onMouseDragged(drag);
			
			float value = amplitudeFilter.getValue(200);
			// Πέρνα τις τιμές στον πίνακα
			xPlayerUI.xPlayerModel.getEqualizerArray()[0] = value;
			xPlayerUI.xPlayerModel.getEqualizerArray()[1] = -value;
			xPlayerUI.xPlayerModel.getEqualizerArray()[2] = value;
			xPlayerUI.xPlayerModel.getEqualizerArray()[3] = value;
			xPlayerUI.xPlayerModel.getEqualizerArray()[4] = -value;
			xPlayerUI.xPlayerModel.getEqualizerArray()[5] = value;
			
			// Εφάρμοσε το φίλτρο
			xPlayerUI.xPlayer.setEqualizer(xPlayerUI.xPlayerModel.getEqualizerArray(), 32);
			
			// Άλλαξε τις γωνίες στα φίλτρα του equalizer
			for (int i = 0; i <= 5; i++)
				filters[i].setAngle(xPlayerUI.xPlayerModel.getEqualizerArray()[i], 200);
			
		});
		
		setBottom(grid);
		
	}
	
	/**
	 * The Class Filter.
	 */
	// TODO Filter
	public class Filter extends DJFilter {
		
		/** The position. */
		int position;
		
		/**
		 * Constructor.
		 *
		 * @param width the width
		 * @param height the height
		 * @param position the position
		 */
		public Filter(int width, int height, int position) {
			super(width, height, color);
			this.position = position;
			setPadding(new Insets(5, 5, 5, 5));
			
			setOnMouseDragged(m -> {
				// Σχεδίαση του φίλτρου
				super.onMouseDragged(m);
				
				// Εφαρμογή του φίλτρου
				xPlayerUI.xPlayerModel.getEqualizerArray()[position] = getValue(200);
				xPlayerUI.xPlayer.setEqualizerKey(xPlayerUI.xPlayerModel.getEqualizerArray()[position], position);
			});
			
		}
		
		/** Επαναφορά σε 0.0f */
		public void resetToZero() {
			setAngle(100, 200);
		}
		
	}
	
	/**
	 * The Class FilterButton.
	 */
	// TODO FilterButton
	public class FilterButton extends MenuItem {
		
		/** The vars. */
		double[] vars;
		
		/**
		 * Instantiates a new filter button.
		 *
		 * @param text the text
		 * @param variables the variables
		 */
		public FilterButton(String text, double variables[]) {
			
			vars = variables;
			setText(text);
			
			setOnAction(action -> {
				// Πέρνα τις τιμές στον πίνακα
				for (int y = 0; y < 10; y++)
					xPlayerUI.xPlayerModel.getEqualizerArray()[y] = (float) vars[y];
				
				// Εφάρμοσε το φίλτρο
				xPlayerUI.xPlayer.setEqualizer(xPlayerUI.xPlayerModel.getEqualizerArray(), 32);
				
				// Άλλαξε τις γωνίες στα φίλτρα του equalizer
				for (int i = 0; i < 10; i++)
					filters[i].setAngle(xPlayerUI.xPlayerModel.getEqualizerArray()[i], 200);
				
			});
			
		}
		
	}
	
}
