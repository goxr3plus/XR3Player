/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.borderless;

import java.io.IOException;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

/**
 * Undecorated JavaFX Scene with implemented move, resize, minimize, maximize and Aero Snap.
 * 
 * Usage:
 * 
 * <pre>
 * {
 * 	&#64;code
*     //add the code here
 * }
 * </pre>
 * 
 * @version 1.0
 */
public class BorderlessScene extends Scene {
	
	/** The controller. */
	private BorderlessController controller;
	
	/** The root. */
	private AnchorPane root;
	
	/** The stage. */
	private Stage stage;
	
	/**
	 * The constructor.
	 * 
	 * @param stage
	 *            your stage.
	 * @param stageStyle
	 *            <b>Undecorated</b> and <b>Transparent</b> StageStyles are accepted or else the Transparent StageStyle will be set.
	 * @param sceneRoot
	 *            The root of the Scene
	 * @param minWidth
	 *            The minimum width that the Stage can have
	 * @param minHeight
	 *            The minimum height that the Stage can have
	 * 
	 */
	public BorderlessScene(Stage stage, StageStyle stageStyle, Parent sceneRoot, double minWidth, double minHeight) {
		super(new Pane());
		try {
			
			// Load the FXML
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(InfoTool.FXMLS + "Borderless.fxml"));
			this.root = loader.load();
			
			// Set Scene root
			setRoot(this.root);
			setContent(sceneRoot);
			
			// Initialize the Controller
			this.controller = loader.getController();
			this.controller.setStage(stage);
			
			// StageStyle
			stage.initStyle(stageStyle);
			this.stage = stage;
			
			// minSize
			stage.setMinWidth(minWidth);
			stage.setMinHeight(minHeight);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Change the content of the scene.
	 * 
	 * @param content
	 *            the root Parent of your new content.
	 */
	public void setContent(Parent content) {
		this.root.getChildren().remove(0);
		this.root.getChildren().add(0, content);
		AnchorPane.setLeftAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setTopAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setRightAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setBottomAnchor(content, Double.valueOf(0.0D));
	}
	
	/**
	 * Set a node that can be pressed and dragged to move the application around.
	 * 
	 * @param node
	 *            the node.
	 */
	public void setMoveControl(Node node) {
		this.controller.setMoveControl(node);
	}
	
	/**
	 * Toggle to maximize the application.
	 */
	public void maximizeStage() {
		controller.maximize();
	}
	
	/**
	 * Minimize the stage to the taskbar.
	 */
	public void minimizeStage() {
		controller.minimize();
	}
	
	/**
	 * Disable/enable the resizing of your stage. Enabled by default.
	 * 
	 * @param bool
	 *            false to disable, true to enable.
	 */
	public void setResizable(Boolean bool) {
		controller.setResizable(bool);
	}
	
	/**
	 * Check the maximized state of the application.
	 * 
	 * @return true if the window is maximized.
	 */
	public ReadOnlyBooleanProperty maximizedProperty() {
		return controller.maximizedProperty();
	}
	
	/**
	 * Returns the width and height of the application when windowed.
	 * 
	 * @return instance of Delta class. Delta.x = width, Delta.y = height.
	 */
	public Delta getWindowedSize() {
		if (controller.prevSize.x == null)
			controller.prevSize.x = stage.getWidth();
		if (controller.prevSize.y == null)
			controller.prevSize.y = stage.getHeight();
		return controller.prevSize;
	}
	
	/**
	 * Returns the x and y position of the application when windowed.
	 * 
	 * @return instance of Delta class. Use Delta.x and Delta.y.
	 */
	public Delta getWindowedPositon() {
		if (controller.prevPos.x == null)
			controller.prevPos.x = stage.getX();
		if (controller.prevPos.y == null)
			controller.prevPos.y = stage.getY();
		return controller.prevPos;
	}
}
