/*
 * 
 */
package application;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;

/**
 * Tabs System.
 *
 * @author GOXR3PLUS
 */
public class MultipleTabs extends BorderPane {

    /** The tab pane. */
    private TabPane tabPane = new TabPane();

    // private RadioStationsController radioStations = new
    // RadioStationsController()

    /**
     * Constructor.
     */
    public MultipleTabs() {

	tabPane.setId("SpecialTabPane");
	tabPane.setSide(Side.RIGHT);
	tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

	addTab(new Tab("Opened Libraries", Main.libraryMode.multipleLibs));
	// addTab(new Tab("Radio Stations", radioStations))
	tabPane.getSelectionModel().select(0);

	this.setCenter(tabPane);
    }

    /**
     * Adds a new Tab.
     *
     * @param tab
     *            the tab
     */
    public void addTab(Tab tab) {
	//tab.getStyleClass().add("STab");
	tabPane.getTabs().add(tab);

    }

    /**
     * Resize UI.
     *
     * @param width
     *            the width
     * @param height
     *            the height
     * @return the tab pane
     */
    public TabPane resizeUI(double width, double height) {
	setPrefSize(width, height);
	setMinHeight(height);
	return tabPane;
    }
}
