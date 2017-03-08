/*
 * 
 */
package borderless;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller implements window controls: maximize, minimize, drag, and Aero Snap.
 * 
 * @version 1.0
 */
public class BorderlessController {

    /** The stage. */
    private Stage stage;

    /** The prev size. */
    protected Delta prevSize;

    /** The prev pos. */
    protected Delta prevPos;

    /** The maximized. */
    private ReadOnlyBooleanWrapper maximized;

    /** The snapped. */
    private boolean snapped;

    /** The left pane. */
    @FXML
    private Pane leftPane;

    /** The right pane. */
    @FXML
    private Pane rightPane;

    /** The top pane. */
    @FXML
    private Pane topPane;

    /** The bottom pane. */
    @FXML
    private Pane bottomPane;

    /** The top left pane. */
    @FXML
    private Pane topLeftPane;

    /** The top right pane. */
    @FXML
    private Pane topRightPane;

    /** The bottom left pane. */
    @FXML
    private Pane bottomLeftPane;

    /** The bottom right pane. */
    @FXML
    private Pane bottomRightPane;

    /** The bottom. */
    String bottom = "bottom";

    /**
     * The constructor.
     */
    public BorderlessController() {
	prevSize = new Delta();
	prevPos = new Delta();
	maximized = new ReadOnlyBooleanWrapper(false);
	snapped = false;
    }

    /**
     * Maximized property.
     *
     * @return True is the window is maximized or False if it is not
     */
    public ReadOnlyBooleanProperty maximizedProperty() {
	return maximized.getReadOnlyProperty();
    }

    /**
     * Called after the FXML layout is loaded.
     */
    @FXML
    private void initialize() {

	setResizeControl(leftPane, "left");
	setResizeControl(rightPane, "right");
	setResizeControl(topPane, "top");
	setResizeControl(bottomPane, bottom);
	setResizeControl(topLeftPane, "top-left");
	setResizeControl(topRightPane, "top-right");
	setResizeControl(bottomLeftPane, bottom + "-left");
	setResizeControl(bottomRightPane, bottom + "-right");
    }

    /**
     * Set the Stage of the controller.
     *
     * @param primaryStage
     *            the new stage
     */
    protected void setStage(Stage primaryStage) {
	this.stage = primaryStage;
    }

    /**
     * Maximize on/off the application.
     */
    protected void maximize() {
	Rectangle2D screen;
	if (Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth() / 2, stage.getHeight() / 2)
		.isEmpty()) {
	    screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())
		    .get(0).getVisualBounds();
	} else {
	    screen = Screen
		    .getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth() / 2, stage.getHeight() / 2)
		    .get(0).getVisualBounds();
	}

	if (maximized.get()) {
	    stage.setWidth(prevSize.x);
	    stage.setHeight(prevSize.y);
	    stage.setX(prevPos.x);
	    stage.setY(prevPos.y);
	    setMaximized(false);
	} else {
	    // Record position and size, and maximize.
	    if (!snapped) {
		prevSize.x = stage.getWidth();
		prevSize.y = stage.getHeight();
		prevPos.x = stage.getX();
		prevPos.y = stage.getY();
	    } else if (!screen.contains(prevPos.x, prevPos.y)) {
		if (prevSize.x > screen.getWidth())
		    prevSize.x = screen.getWidth() - 20;

		if (prevSize.y > screen.getHeight())
		    prevSize.y = screen.getHeight() - 20;

		prevPos.x = screen.getMinX() + (screen.getWidth() - prevSize.x) / 2;
		prevPos.y = screen.getMinY() + (screen.getHeight() - prevSize.y) / 2;
	    }

	    stage.setX(screen.getMinX());
	    stage.setY(screen.getMinY());
	    stage.setWidth(screen.getWidth());
	    stage.setHeight(screen.getHeight());

	    setMaximized(true);
	}
    }

    /**
     * Minimize the application.
     */
    protected void minimize() {
	stage.setIconified(true);
    }

    /**
     * Set a node that can be pressed and dragged to move the application around.
     * 
     * @param node
     *            the node.
     */
    protected void setMoveControl(final Node node) {
	final Delta delta = new Delta();
	final Delta eventSource = new Delta();

	// Record drag deltas on press.
	node.setOnMousePressed(m -> {
	    if (m.isPrimaryButtonDown()) {
		delta.x = m.getSceneX(); //getX()
		delta.y = m.getSceneY(); //getY()

		if (maximized.get() || snapped) {
		    delta.x = prevSize.x * (m.getSceneX() / stage.getWidth());//(m.getX() / stage.getWidth())
		    delta.y = prevSize.y * (m.getSceneY() / stage.getHeight());//(m.getY() / stage.getHeight())
		} else {
		    prevSize.x = stage.getWidth();
		    prevSize.y = stage.getHeight();
		    prevPos.x = stage.getX();
		    prevPos.y = stage.getY();
		}

		eventSource.x = m.getScreenX();
		eventSource.y = node.prefHeight(stage.getHeight());
	    }
	});

	// Dragging moves the application around.
	node.setOnMouseDragged(m -> {
	    if (m.isPrimaryButtonDown()) {

		// Move x axis.
		stage.setX(m.getScreenX() - delta.x);

		if (snapped) {

		    // Aero Snap off.
		    Rectangle2D screen = Screen.getScreensForRectangle(m.getScreenX(), m.getScreenY(), 1, 1).get(0)
			    .getVisualBounds();

		    stage.setHeight(screen.getHeight());

		    if (m.getScreenY() > eventSource.y) {
			stage.setWidth(prevSize.x);
			stage.setHeight(prevSize.y);
			snapped = false;
		    }
		} else {
		    // Move y axis.
		    stage.setY(m.getScreenY() - delta.y);
		}

		// Aero Snap off.
		if (maximized.get()) {
		    stage.setWidth(prevSize.x);
		    stage.setHeight(prevSize.y);
		    setMaximized(false);
		}
	    }
	});

	// Maximize on double click.
	node.setOnMouseClicked(m -> {
	    if ((m.getButton().equals(MouseButton.PRIMARY)) && (m.getClickCount() == 2))
		maximize();
	});

	// Aero Snap on release.
	node.setOnMouseReleased(m -> {
	    if ((m.getButton().equals(MouseButton.PRIMARY)) && (m.getScreenX() != eventSource.x)) {
		Rectangle2D screen = Screen.getScreensForRectangle(m.getScreenX(), m.getScreenY(), 1, 1).get(0)
			.getVisualBounds();

		// Aero Snap Left.
		if (m.getScreenX() == screen.getMinX()) {
		    stage.setY(screen.getMinY());
		    stage.setHeight(screen.getHeight());

		    stage.setX(screen.getMinX());
		    if (screen.getWidth() / 2 < stage.getMinWidth()) {
			stage.setWidth(stage.getMinWidth());
		    } else {
			stage.setWidth(screen.getWidth() / 2);
		    }

		    snapped = true;
		}

		// Aero Snap Right.
		else if (m.getScreenX() == screen.getMaxX() - 1) {
		    stage.setY(screen.getMinY());
		    stage.setHeight(screen.getHeight());

		    if (screen.getWidth() / 2 < stage.getMinWidth()) {
			stage.setWidth(stage.getMinWidth());
		    } else {
			stage.setWidth(screen.getWidth() / 2);
		    }
		    stage.setX(screen.getMaxX() - stage.getWidth());

		    snapped = true;
		}

		// Aero Snap Top.
		else if (m.getScreenY() == screen.getMinY()) {
		    if (!screen.contains(prevPos.x, prevPos.y)) {
			if (prevSize.x > screen.getWidth())
			    prevSize.x = screen.getWidth() - 20;

			if (prevSize.y > screen.getHeight())
			    prevSize.y = screen.getHeight() - 20;

			prevPos.x = screen.getMinX() + (screen.getWidth() - prevSize.x) / 2;
			prevPos.y = screen.getMinY() + (screen.getHeight() - prevSize.y) / 2;
		    }

		    stage.setX(screen.getMinX());
		    stage.setY(screen.getMinY());
		    stage.setWidth(screen.getWidth());
		    stage.setHeight(screen.getHeight());
		    setMaximized(true);
		}
	    }
	});
    }

    /**
     * Set pane to resize application when pressed and dragged.
     * 
     * @param pane
     *            the pane the action is set to.
     * @param direction
     *            the resize direction. Diagonal: 'top' or 'bottom' + 'right' or 'left'.
     */
    private void setResizeControl(Pane pane, final String direction) {

	pane.setOnMouseDragged(m -> {
	    if (m.isPrimaryButtonDown()) {
		double width = stage.getWidth();
		double height = stage.getHeight();

		// Horizontal resize.
		if (direction.endsWith("left") && ((width > stage.getMinWidth()) || (m.getX() < 0))) {
		    stage.setWidth(width - m.getScreenX() + stage.getX());
		    stage.setX(m.getScreenX());
		} else if ((direction.endsWith("right")) && ((width > stage.getMinWidth()) || (m.getX() > 0))) {
		    stage.setWidth(width + m.getX());
		}

		// Vertical resize.
		if (direction.startsWith("top")) {
		    if (snapped) {
			stage.setHeight(prevSize.y);
			snapped = false;
		    } else if ((height > stage.getMinHeight()) || (m.getY() < 0)) {
			stage.setHeight(height - m.getScreenY() + stage.getY());
			stage.setY(m.getScreenY());
		    }
		} else if (direction.startsWith(bottom)) {
		    if (snapped) {
			stage.setY(prevPos.y);
			snapped = false;
		    } else if ((height > stage.getMinHeight()) || (m.getY() > 0))
			stage.setHeight(height + m.getY());
		}
	    }
	});

	// Record application height and y position.
	pane.setOnMousePressed(m -> {
	    if ((m.isPrimaryButtonDown()) && (!snapped)) {
		prevSize.y = stage.getHeight();
		prevPos.y = stage.getY();
	    }

	});

	// Aero Snap Resize.
	pane.setOnMouseReleased(m -> {
	    if ((m.getButton().equals(MouseButton.PRIMARY)) && (!snapped)) {
		Rectangle2D screen = Screen.getScreensForRectangle(m.getScreenX(), m.getScreenY(), 1, 1).get(0)
			.getVisualBounds();

		if ((stage.getY() <= screen.getMinY()) && (direction.startsWith("top"))) {
		    stage.setHeight(screen.getHeight());
		    stage.setY(screen.getMinY());
		    snapped = true;
		}

		if ((m.getScreenY() >= screen.getMaxY()) && (direction.startsWith(bottom))) {
		    stage.setHeight(screen.getHeight());
		    stage.setY(screen.getMinY());
		    snapped = true;
		}
	    }

	});

	// Aero Snap resize on double click.
	pane.setOnMouseClicked(m -> {
	    if ((m.getButton().equals(MouseButton.PRIMARY)) && (m.getClickCount() == 2)
		    && ("top".equals(direction) || bottom.equals(direction))) {
		Rectangle2D screen = Screen
			.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth() / 2, stage.getHeight() / 2)
			.get(0).getVisualBounds();

		if (snapped) {
		    stage.setHeight(prevSize.y);
		    stage.setY(prevPos.y);
		    snapped = false;
		} else {
		    prevSize.y = stage.getHeight();
		    prevPos.y = stage.getY();
		    stage.setHeight(screen.getHeight());
		    stage.setY(screen.getMinY());
		    snapped = true;
		}
	    }

	});
    }

    /**
     * Determines if the Window is maximized.
     *
     * @param maximized
     *            the new maximized
     */
    private void setMaximized(boolean maximized) {
	this.maximized.set(maximized);
	setResizable(!maximized);
    }

    /**
     * Disable/enable the resizing of your stage. Enabled by default.
     * 
     * @param bool
     *            false to disable, true to enable.
     */
    protected void setResizable(boolean bool) {
	leftPane.setDisable(!bool);
	rightPane.setDisable(!bool);
	topPane.setDisable(!bool);
	bottomPane.setDisable(!bool);
	topLeftPane.setDisable(!bool);
	topRightPane.setDisable(!bool);
	bottomLeftPane.setDisable(!bool);
	bottomRightPane.setDisable(!bool);
    }
}
