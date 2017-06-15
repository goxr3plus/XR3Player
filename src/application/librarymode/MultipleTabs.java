/*
 * 
 */
package application.librarymode;

import application.Main;
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
	
	/**
	 * Constructor.
	 */
	public MultipleTabs() {
		
		tabPane.setId("SpecialTabPane");
		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		addTab(new Tab("Opened Libraries", Main.libraryMode.multipleLibs));	
		addTab(new Tab("Radio"));
		tabPane.getSelectionModel().select(0);
		
		tabPane.getTabs().get(1).setDisable(true);
		
		this.setCenter(tabPane);
	}
	
	/**
	 * Adds a new Tab.
	 *
	 * @param tab
	 *        the tab
	 */
	public void addTab(Tab tab) {
		//tab.getStyleClass().add("sTab")
		tabPane.getTabs().add(tab);
		
	}
	
}
