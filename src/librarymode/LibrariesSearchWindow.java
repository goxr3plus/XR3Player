/*
 * 
 */
package librarymode;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.InfoTool;

/**
 * The Class LibrariesSearchWindow.
 */
public class LibrariesSearchWindow {
	
	/** The stage. */
	public Stage stage;
	
	/** The scroll pane. */
	@FXML
	private ScrollPane scrollPane;
	
	/** The tile pane. */
	@FXML
	private TilePane tilePane;
	
	/** The results label. */
	@FXML
	private Label resultsLabel;
	
	/** The close. */
	@FXML
	private Button close;
	
	/**
	 * Called after the FXML layout is loaded.
	 */
	@FXML
	public void initialize() {
		
		// ...
		stage = new Stage();
		stage.initOwner(Main.window);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		stage.show();
		stage.close();
		
		// close
		close.setOnAction(a -> stage.close());
	}
	
	/**
	 * Add a Scene to the Window.
	 *
	 * @param scene the new scene
	 */
	public void setTheScene(Scene scene) {
		stage.setScene(scene);
		stage.getScene().getStylesheets()
		        .add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());
	}
	
	/**
	 * Clears all the items from the TilePane.
	 */
	public void clearContainer() {
		tilePane.getChildren().clear();
	}
	
	/**
	 * Adds the children.
	 *
	 * @param node the node
	 */
	public void addChildren(Node node) {
		tilePane.getChildren().add(node);
	}
	
	/**
	 * Changes the text of the Top Label of the Window.
	 *
	 * @param text the new label text
	 */
	public void setLabelText(String text) {
		resultsLabel.setText(text);
	}
}
