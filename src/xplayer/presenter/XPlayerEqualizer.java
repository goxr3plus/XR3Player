/*
 * 
 */
package xplayer.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import disc.DJFilter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import tools.InfoTool;

/**
 * The Class XPlayerEqualizer.
 */
public class XPlayerEqualizer extends BorderPane {

    @FXML
    private TilePane tilePane;

    @FXML
    private HBox bottomHBox;

    @FXML
    private Button resetFilers;

    @FXML
    private Button effects;

    //----------------------------------------------------

    /** The logger for this class */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /** The color. */
    Color color = Color.BLUEVIOLET;

    /** The pan filter. */
    DJFilter panFilter;

    /** The balance filter. */
    DJFilter balanceFilter;

    /** The amplitude filter. */
    DJFilter amplitudeFilter;

    /** The x player UI. */
    XPlayerController xPlayerUI;

    /** The filters. */
    Filter[] filters = new Filter[32];

    /** The filter buttons. */
    FilterButton[] filterButtons = new FilterButton[] {
	    new FilterButton("Normal", new double[] { 0.0, 0.0, 0., 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }),
	    new FilterButton("Club", new double[] { .2, .2, .35, .34, .34, .34, .42, .50, .50, .50 }),
	    new FilterButton("Dance", new double[] { .26, .2, .46, .50, .50, .66, .70, .70, .50, .50 }),
	    new FilterButton("FullBass", new double[] { 0.4, .4, .26, .36, .46, .62, .76, .78, .78, .78 }),
	    new FilterButton("FullBassTreble", new double[] { .34, .34, .50, .68, .62, .46, .28, .22, .18, .18 }),
	    new FilterButton("Live", new double[] { .2, .2, .40, .36, .34, .34, .40, .42, .42, .42 }),
	    new FilterButton("Party", new double[] { .15, .15, .50, .50, .50, .50, .50, .50, .32, .32 }),
	    new FilterButton("Rock", new double[] { .32, .38, .64, .72, .56, .40, .28, .24, .24, .24 }) };

    /**
     * Constructor.
     *
     * @param xPlayerUI
     *            the x player UI
     */
    public XPlayerEqualizer(XPlayerController xPlayerUI) {

	//Vars
	this.xPlayerUI = xPlayerUI;
	color = xPlayerUI.getDiscColor();

	// ----------------------------------FXMLLoader-------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "XPlayerEqualizer.fxml"));
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
	tilePane.getChildren().addAll(filters);

	//resetFilers
	resetFilers.setOnAction(action -> {
	    for (int i = 0; i < 32; i++)
		xPlayerUI.xPlayerModel.getEqualizerArray()[i] = 0.0f;

	    xPlayerUI.xPlayer.setEqualizer(xPlayerUI.xPlayerModel.getEqualizerArray(), 32);

	    for (Filter comp : filters)
		comp.resetToZero();
	    
	});

	// Effects
	ContextMenu contextMenu = new ContextMenu();
	contextMenu.getItems().addAll(filterButtons);
	effects.setOnMouseReleased(m -> contextMenu.show(effects, m.getScreenX(), m.getScreenY() - contextMenu.getHeight()));

	//-------------------------- Extra Filters--------------------------
	panFilter = new DJFilter(2, 2, 36, 36, Color.YELLOW);
	panFilter.setOnMouseDragged(drag -> {
	    panFilter.onMouseDragged(drag);
	    xPlayerUI.xPlayer.setPan(panFilter.getValue(200));
	});

	balanceFilter = new DJFilter(40, 2, 36, 36, Color.YELLOW);
	balanceFilter.setOnMouseDragged(drag -> {
	    balanceFilter.onMouseDragged(drag);
	    xPlayerUI.xPlayer.setBalance(balanceFilter.getValue(200));
	});

	//	amplitudeFilter = new DJFilter(40, 2, 36, 36, Color.YELLOW);
	//	amplitudeFilter.setOnMouseDragged(drag -> {
	//	    amplitudeFilter.onMouseDragged(drag);
	//
	//	    float value = amplitudeFilter.getValue(200);
	//	    // Pass the variables to the array
	//	    xPlayerUI.xPlayerModel.getEqualizerArray()[0] = value;
	//	    xPlayerUI.xPlayerModel.getEqualizerArray()[1] = -value;
	//	    xPlayerUI.xPlayerModel.getEqualizerArray()[2] = value;
	//	    xPlayerUI.xPlayerModel.getEqualizerArray()[3] = value;
	//	    xPlayerUI.xPlayerModel.getEqualizerArray()[4] = -value;
	//	    xPlayerUI.xPlayerModel.getEqualizerArray()[5] = value;
	//
	//	    // Add the filter
	//	    xPlayerUI.xPlayer.setEqualizer(xPlayerUI.xPlayerModel.getEqualizerArray(), 32);
	//
	//	    //Change the angles of equalizer filters
	//	    for (int i = 0; i <= 5; i++)
	//		filters[i].setAngle(xPlayerUI.xPlayerModel.getEqualizerArray()[i], 200);
	//
	//	});

	bottomHBox.getChildren().addAll(panFilter, balanceFilter);

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
	public Filter(int width, int height, int position) {
	    super(width, height, color);
	    this.position = position;
	    setPadding(new Insets(5, 5, 5, 5));

	    setOnMouseDragged(m -> {
		//Draw the filter
		super.onMouseDragged(m);

		//Add the filter
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
