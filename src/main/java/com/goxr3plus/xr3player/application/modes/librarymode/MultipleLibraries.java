/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.modes.librarymode;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.application.presenter.custom.Marquee;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.smartcontroller.presenter.SmartController;

/**
 * Mechanism of showing the opened libraries each opened library is represented
 * by a Tab.
 *
 * @author GOXR3PLUS STUDIO
 */
public class MultipleLibraries extends StackPane {
	
	@FXML
	private JFXTabPane tabPane;
	
	@FXML
	private JFXButton addNewLibrary;
	
	@FXML
	private Region emptyLabelRegion;
	
	@FXML
	private Label emptyLabel;
	
	// -----------------------------------------------------------------------
	
	private static final Image noItemsImage = InfoTool.getImageFromResourcesFolder("noMusic.png");
	
	/**
	 * This class wraps an ObservableList
	 */
	private SimpleListProperty<Tab> itemsWrapperProperty;
	
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
		
		tabPane.getTabs().clear();
		//tabPane.setId("MultipleLibrariesTabPane");
		
		// emptyLabel
		emptyLabel.setOnMouseReleased(m -> {
			if (Main.libraryMode.teamViewer.getViewer().getItemsObservableList().isEmpty())
				Main.libraryMode.createNewLibrary(emptyLabel, false);
			else
				Main.libraryMode.teamViewer.getViewer().getItemsObservableList().get(0).openLibrary(true, false);
		});
		
		//== emptyLabelRegion
		emptyLabelRegion.visibleProperty().bind(emptyLabel.visibleProperty());
		
		//== emptyLabel
		itemsWrapperProperty = new SimpleListProperty<>(tabPane.getTabs());
		emptyLabel.visibleProperty().bind(itemsWrapperProperty.emptyProperty());
		
		// TabPane
		//tabPane.setId("LibrariesTabPane");
		
		//	tabPane.setOnMouseMoved(m -> {
		//	    if (!m.isControlDown())
		//		resetCursor();
		//	    else
		//		setControlCursor();
		//	});	
		
		//== addTab
		addNewLibrary.setOnAction(a -> Main.libraryMode.createNewLibrary(addNewLibrary, true));
		
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
			if (! ( (SmartController) tab.getContent() ).isFree(showMessage))
				return false;
			
		return true;
	}
	
	/**
	 * Selects the tab with the given name
	 * 
	 * @param name
	 */
	public void selectTab(String name) {
		for (Tab tab : tabPane.getTabs())
			if (tab.getTooltip().getText().equals(name)) {
				tabPane.getSelectionModel().select(tab);
				break;
			}
	}
	
	private Library selectedLibrary;
	
	/**
	 * Returns the selected library.
	 *
	 * @return The Selected Library if exists or <b> null </b> instead
	 */
	public Optional<Library> getSelectedLibrary() {
		
		// selection model is empty?
		
		if (tabPane.getSelectionModel().isEmpty())
			selectedLibrary = null;
		else
			Main.libraryMode.getLibraryWithName(tabPane.getSelectionModel().getSelectedItem().getTooltip().getText()).ifPresent(library -> selectedLibrary = library);
		
		return Optional.ofNullable(selectedLibrary);
	}
	
	/**
	 * Returns the selected Tab or null instead
	 * 
	 * @return
	 */
	public Tab getSelectedTab() {
		return tabPane.getSelectionModel().getSelectedItem();
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
		
		// label
		Label label = new Label();
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setAlignment(Pos.CENTER);
		label.setStyle("-fx-font-weight:bold; -fx-text-fill: white; -fx-font-size:10; -fx-background-color: rgb(0,0,0,0.3);");
		label.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100).asString("%.00f %%"));
		//label.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100.00).asString("%.02f %%"))
		// text.visibleProperty().bind(library.getSmartController().inputService.runningProperty())
		
		Marquee marquee = new Marquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		//marquee.setStyle("-fx-background-radius:0 0 0 0; -fx-background-color:rgb(255,255,255,0.5); -fx-border-color:transparent;")
		//tab.textProperty().bind(marquee.textProperty())
		
		stack.getChildren().addAll(indicator, label);
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
			if (drop.getDragboard().hasFiles() && getSelectedLibrary().get().getSmartController().isFree(true)
					&& drop.getGestureSource() != library.getSmartController().getTableViewer())
				getSelectedLibrary().get().getSmartController().getInputService().start(drop.getDragboard().getFiles());
			
			drop.setDropCompleted(true);
		});
		
		// stack
		library.getSmartController().getIndicatorVBox().visibleProperty().addListener((observable , oldValue , newValue) -> {
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
			if (!library.getSmartController().isFree(true))
				c.consume();
			else
				library.openLibrary(false, false);
		});
		
		tab.setGraphic(hBox);
		tab.setContextMenu(new LibraryTabContextMenu(tab));
		
		//Add it
		tabPane.getTabs().add(tab);
	}
	
	/**
	 * Closes the tabs to the right of the given Tab
	 * 
	 * @param tab
	 */
	public void closeTabsToTheRight(Tab givenTab) {
		//Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;
		
		//The start
		int start = tabPane.getTabs().indexOf(givenTab);
		
		//Remove the appropriate items
		tabPane.getTabs().stream()
				//filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) > start)
				//Collect the all to a list
				.collect(Collectors.toList()).forEach(tab -> tab.getOnCloseRequest().handle(null));
		
	}
	
	/**
	 * Closes the tabs to the left of the given Tab
	 * 
	 * @param tab
	 */
	public void closeTabsToTheLeft(Tab givenTab) {
		//Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;
		
		//The start
		int start = tabPane.getTabs().indexOf(givenTab);
		
		//Remove the appropriate items
		tabPane.getTabs().stream()
				//filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) < start)
				//Collect the all to a list
				.collect(Collectors.toList()).forEach(tab -> tab.getOnCloseRequest().handle(null));
		
	}
	
	/**
	 * Removes this Tab from the TabPane
	 * 
	 * @param tab
	 */
	public void removeTab(Tab tab) {
		tab.getOnCloseRequest().handle(null);
	}
	
	/**
	 * Remove tab with that name.
	 *
	 * @param tabName
	 *            the tab name
	 */
	public void removeTab(String tabName) {
		tabPane.getTabs().removeIf(tab -> tab.getTooltip().getText().equals(tabName));
	}
	
	/**
	 * Rename the tab with old name to a tab with a new name.
	 *
	 * @param oldName
	 *            the old name
	 * @param newName
	 *            the new name
	 */
	public void renameTab(String oldName , String newName) {
		
		tabPane.getTabs().stream().forEach(tab -> {
			if (tab.getTooltip().getText().equals(oldName)) {
				// tab.textProperty().unbind()
				tab.getTooltip().textProperty().unbind();
				// tab.setText(InfoTool.getMinString(newName, 15))
				tab.getTooltip().setText(newName);
			}
		});
	}
	
	/**
	 * @return the emptyLabel
	 */
	public Label getEmptyLabel() {
		return emptyLabel;
	}
	
}
