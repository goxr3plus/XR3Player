/*
 * 
 */
package application.librarymode;

import java.io.IOException;
import java.util.logging.Level;

import application.Main;
import application.presenter.custom.Marquee;
import application.tools.InfoTool;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import smartcontroller.SmartController;

/**
 * Mechanism of showing the opened libraries each opened library is represented by a Tab.
 *
 * @author GOXR3PLUS STUDIO
 */
public class MultipleLibraries extends StackPane {

    /** The tab pane. */
    @FXML
    private TabPane tabPane;

    /**
     * This Region is visible when no libraries are opened
     */
    @FXML
    private Region emptyLabelRegion;

    /**
     * This Label is visible when no libraries are opened
     */
    @FXML
    public Label emptyLabel;

    // -----------------------------------------------------------------------

    /** Custom pen cursor */
    //ImageCursor stylus = new ImageCursor(InfoTool.getImageFromDocuments("highlighter.png"), 0, 32)

    /** The hand. */
    Cursor hand = Cursor.HAND;

    private static final Image noItemsImage = InfoTool.getImageFromResourcesFolder("noMusic.png");

    /**
     * Constructor.
     */
    public MultipleLibraries() {

	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MultipleLibraries.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {

	tabPane.setId("MultipleLibrariesTabPane");

	// emptyLabel
	emptyLabel.setOnMouseReleased(m -> {
	    if (Main.libraryMode.teamViewer.getViewer().getItemsObservableList().isEmpty())
		Main.libraryMode.createNewLibrary(emptyLabel);
	    else
		Main.libraryMode.teamViewer.getViewer().getItemsObservableList().get(0).libraryOpenClose(true, false);
	});

	// emptyLabelRegion
	emptyLabelRegion.visibleProperty().bind(emptyLabel.visibleProperty());

	// TabPane
	//tabPane.setId("LibrariesTabPane");

	//	tabPane.setOnMouseMoved(m -> {
	//	    if (!m.isControlDown())
	//		resetCursor();
	//	    else
	//		setControlCursor();
	//	});	
    }

    //    /**
    //     * Resets the cursor to the default one.
    //     */
    //    public void resetCursor() {
    //	if (tabPane.getCursor() != hand)
    //	    tabPane.setCursor(hand);
    //    }
    //
    //    /**
    //     * Set the Cursor to control Cursor.
    //     */
    //    public void setControlCursor() {
    //	if (tabPane.getCursor() != stylus)
    //	    tabPane.setCursor(stylus);
    //    }

    /**
     * Returns true if all the controllers are free.
     *
     * @param showMessage
     *            the show message
     * @return true, if is free
     */
    public boolean isFree(boolean showMessage) {
	for (Tab tab : tabPane.getTabs())
	    if (!((SmartController) tab.getContent()).isFree(showMessage))
		return false;

	return true;
    }

    /**
     * Returns the selected library.
     *
     * @return The Selected Library if exists or <b> null </b> instead
     */
    public Library getSelectedLibrary() {

	// selection model is empty?
	return tabPane.getSelectionModel().isEmpty() ? null
		: Main.libraryMode.getLibraryWithName(tabPane.getSelectionModel().getSelectedItem().getTooltip().getText());
    }

    /**
     * Find a tab which contains that name.
     *
     * @param name
     *            the name
     * @return The tab with the given name
     */
    public Tab getTab(String name) {

	return tabPane.getTabs().stream().filter(tab -> tab.getTooltip().getText().equals(name)).findFirst().orElse(null);
    }

    /**
     * Find a tab which is in that position in the tab pane
     * 
     * @param index
     *
     * @return The tab with that index
     */
    public Tab getTab(int index) {
	return tabPane.getTabs().get(index);
    }

    /**
     * Returns a List of the TabPane Tabs
     * 
     * @return A List of the TabPane Tabs
     */
    public ObservableList<Tab> getTabs() {
	return tabPane.getTabs();
    }

    /**
     * Return the TabPane
     * 
     * @return The TabPane
     */
    public TabPane getTabPane() {
	return tabPane;
    }

    /**
     * Add a new Tab.
     *
     * @param library
     *            the library
     */
    public void insertTab(Library library) {
	emptyLabel.setVisible(false);

	// where is "" it must be
	// InfoTool.getMinString(library.getLibraryName(), 15)
	Tab tab = new Tab("", library.getSmartController());
	tab.setTooltip(new Tooltip(library.getLibraryName()));

	// Graphic
	StackPane stack = new StackPane();

	// indicator
	ProgressBar indicator = new ProgressBar();
	indicator.progressProperty().bind(library.getSmartController().getIndicator().progressProperty());
	indicator.setMaxSize(35, 15);

	// text
	Text text = new Text();
	text.setStyle("-fx-font-size:70%; -fx-fill:black;");
	text.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100.00).asString("%.02f %%"));
	// text.visibleProperty().bind(library.getSmartController().inputService.runningProperty())

	Marquee marquee = new Marquee();
	marquee.textProperty().bind(tab.getTooltip().textProperty());
	//marquee.setStyle("-fx-background-radius:0 0 0 0; -fx-background-color:rgb(255,255,255,0.5); -fx-border-color:transparent;")
	//tab.textProperty().bind(marquee.textProperty())

	stack.getChildren().addAll(indicator, text);
	stack.setManaged(false);
	stack.setVisible(false);

	//ImageView
	ImageView imageView = new ImageView(noItemsImage);
	imageView.visibleProperty().bind(library.getSmartController().totalInDataBaseProperty().isEqualTo(0));
	imageView.managedProperty().bind(imageView.visibleProperty());

	// HBOX
	HBox hBox = new HBox();
	hBox.getChildren().addAll(imageView, stack, marquee);

	// --Drag Over
	hBox.setOnDragOver(dragOver -> {
	    // The drag must come from source other than the owner
	    if (dragOver.getDragboard().hasFiles()) {
		//&& dragOver.getGestureSource() != library.getSmartController().tableViewer) 
		dragOver.acceptTransferModes(TransferMode.LINK);
		tabPane.getSelectionModel().select(tab);
	    }
	});

	// --Drag Dropped
	hBox.setOnDragDropped(drop -> {
	    // Has Files? + isFree()?
	    if (drop.getDragboard().hasFiles() && getSelectedLibrary().getSmartController().isFree(true)
		    && drop.getGestureSource() != library.getSmartController().getTableViewer())
		getSelectedLibrary().getSmartController().getInputService().start(drop.getDragboard().getFiles());

	    drop.setDropCompleted(true);
	});

	// stack
	library.getSmartController().getIndicatorVBox().visibleProperty().addListener((observable, oldValue, newValue) -> {
	    if (newValue) { //if it is visible
		stack.setManaged(true);
		stack.setVisible(true);
		// tab.setGraphic(hBox)
	    } else {
		stack.setManaged(false);
		stack.setVisible(false);
		// tab.setGraphic(null)
	    }
	});
	//library.getLibraryProgressIndicator().progressProperty().bind(indicator.progressProperty());
	//library.getLibraryProgressIndicator().visibleProperty().bind(stack.visibleProperty());

	tab.setOnCloseRequest(c -> {
	    if (library.getSmartController().isFree(true))
		library.libraryOpenClose(false, false);
	    else
		c.consume();
	});

	tab.setGraphic(hBox);
	tabPane.getTabs().add(tab);

	//ContextMenu
	ContextMenu contextMenu = new ContextMenu();

	//--findLibrary
	MenuItem findLibrary = new MenuItem("Go to Library");
	findLibrary.setOnAction(a -> Main.libraryMode.teamViewer.getViewer().setCenterIndex(library.getPosition()));
	contextMenu.getItems().add(findLibrary);

	tab.setContextMenu(contextMenu);
    }

    /**
     * Remove tab with that name.
     *
     * @param tabName
     *            the tab name
     */
    public void removeTab(String tabName) {

	tabPane.getTabs().removeIf(tab -> tab.getTooltip().getText().equals(tabName));

	// tabPane empty?
	if (tabPane.getTabs().isEmpty())
	    emptyLabel.setVisible(true);

    }

    /**
     * Rename the tab with old name to a tab with a new name.
     *
     * @param oldName
     *            the old name
     * @param newName
     *            the new name
     */
    public void renameTab(String oldName, String newName) {

	tabPane.getTabs().stream().forEach(tab -> {
	    if (tab.getTooltip().getText().equals(oldName)) {
		// tab.textProperty().unbind()
		tab.getTooltip().textProperty().unbind();
		// tab.setText(InfoTool.getMinString(newName, 15))
		tab.getTooltip().setText(newName);
	    }
	});
    }

}
