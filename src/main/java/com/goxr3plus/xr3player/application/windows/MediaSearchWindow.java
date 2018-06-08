/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.windows;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import main.java.com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * The Class LibrariesSearchWindow.
 */
public class MediaSearchWindow {
	
	// -------------------------------------------------------------
	
	/** The stage. */
	private Stage window;
	
	private BorderlessScene borderlessScene;
	
	// -------------------------------------------------------------
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public MediaSearchWindow() {
		
		//Window
		window = new Stage();
		borderlessScene = new BorderlessScene(window, StageStyle.UNDECORATED, Main.searchWindowSmartController, 400, 300);
		window.setScene(borderlessScene);
		window.focusedProperty().addListener(l -> {
			if (!window.isFocused())
				window.hide();
		});
	}
	
	/**
	 * Closes the window
	 */
	public void close() {
		window.close();
	}
	
	/**
	 * Shows the window
	 */
	public void show() {
		window.show();
	}
	
	/**
	 * Recalculates the position and shows the window
	 */
	public void recalculateAndshow(Node searchField) {
		recalculateWindowPosition(searchField);
		window.show();
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
	/**
	 * This method registers some listeners to the main window so when main windows changes his size or position then the Search Window recalculates it's
	 * position.
	 * 
	 * @param window1
	 * @param searchField
	 */
	public void registerListeners(Window window1 , Node searchField) {
		// Care so the Search Window is recalculating it's position
		Main.window.xProperty().addListener((observable , oldValue , newValue) -> recalculateWindowPosition(searchField));
		Main.window.yProperty().addListener((observable , oldValue , newValue) -> recalculateWindowPosition(searchField));
		Main.window.widthProperty().addListener((observable , oldValue , newValue) -> recalculateWindowPosition(searchField));
		Main.window.heightProperty().addListener((observable , oldValue , newValue) -> recalculateWindowPosition(searchField));
		window.initOwner(window1);
	}
	
	/**
	 * Recalculate window position.
	 */
	public void recalculateWindowPosition(Node searchField) {
		if (!window.isShowing())
			return;
		
		Bounds bounds = searchField.localToScreen(searchField.getBoundsInLocal());
		window.setX(bounds.getMinX());
		//Check here so the window doesn't go below screen height
		window.setY( ( window.getHeight() + bounds.getMaxY() + 10 < InfoTool.getVisualScreenHeight() ) ? bounds.getMaxY() + 10 : bounds.getMinY() - window.getHeight() - 10);
	}
	
}
