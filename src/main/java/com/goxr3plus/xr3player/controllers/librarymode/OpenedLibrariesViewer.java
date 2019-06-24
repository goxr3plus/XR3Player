/*
 * 
 */
package com.goxr3plus.xr3player.controllers.librarymode;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.custom.Marquee;
import com.goxr3plus.xr3player.controllers.librarymode.Library.LibraryStatus;
import com.goxr3plus.xr3player.controllers.smartcontroller.SmartController;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

/**
 * Mechanism of showing the opened libraries each opened library is represented
 * by a Tab.
 *
 * @author GOXR3PLUS STUDIO
 */
public class OpenedLibrariesViewer extends StackPane {

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private Button createFirstLibrary;

	// -----------------------------------------------------------------------

	/**
	 * This class wraps an ObservableList
	 */
	private SimpleListProperty<Tab> itemsWrapperProperty;

	/**
	 * Constructor.
	 */
	public OpenedLibrariesViewer() {

		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.LIBRARIES_FXMLS + "OpenedLibrariesViewer.fxml"));
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
		// tabPane.setId("MultipleLibrariesTabPane")

		// createFirstLibrary
		createFirstLibrary.setOnMouseReleased(m -> {
			if (Main.libraryMode.viewer.getItemsObservableList().isEmpty())
				Main.libraryMode.createNewLibrary(createFirstLibrary.getGraphic(), true, true);
			else
				((Library) Main.libraryMode.viewer.getItemsObservableList().get(0))
						.setLibraryStatus(LibraryStatus.OPENED, false);
		});

		// == emptyLabel
		itemsWrapperProperty = new SimpleListProperty<>(tabPane.getTabs());
		createFirstLibrary.visibleProperty().bind(itemsWrapperProperty.emptyProperty());

	}

	// /**
	// * Resets the cursor to the default one.
	// */
	// public void resetCursor() {
	// if (tabPane.getCursor() != hand)
	// tabPane.setCursor(hand);
	// }
	//
	// /**
	// * Set the Cursor to control Cursor.
	// */
	// public void setControlCursor() {
	// if (tabPane.getCursor() != stylus)
	// tabPane.setCursor(stylus);
	// }

	/**
	 * Returns true if all the controllers are free.
	 *
	 * @param showMessage the show message
	 * @return true, if is free
	 */
	public boolean isFree(boolean showMessage) {
		for (Tab tab : tabPane.getTabs())
			if (!((SmartController) tab.getContent()).isFree(showMessage))
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
			Main.libraryMode.getLibraryWithName(tabPane.getSelectionModel().getSelectedItem().getTooltip().getText())
					.ifPresent(library -> selectedLibrary = library);

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
	 * @param name the name
	 * @return The tab with the given name
	 */
	public Tab getTab(String name) {

		return tabPane.getTabs().stream().filter(tab -> tab.getTooltip().getText().equals(name)).findFirst()
				.orElse(null);
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
	 * @param library the library
	 */
	public void insertTab(Library library) {

		// Create the tab
		Tab tab = new Tab("", library.getSmartController());
		tab.setTooltip(new Tooltip(library.getLibraryName()));
		StackPane stack = new StackPane();
		ProgressBar indicator = new ProgressBar();
		indicator.progressProperty().bind(library.getSmartController().getIndicator().progressProperty());
		indicator.setMaxSize(35, 15);

		Label label = new Label();
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setAlignment(Pos.CENTER);
		label.setStyle(
				"-fx-font-weight:bold; -fx-text-fill: white; -fx-font-size:10; -fx-background-color: rgb(0,0,0,0.3);");
		label.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100).asString("%.00f %%"));
		Marquee marquee = new Marquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		// marquee.setStyle("-fx-background-radius:0 0 0 0;
		// -fx-background-color:rgb(255,255,255,0.5); -fx-border-color:transparent;")
		// tab.textProperty().bind(marquee.textProperty())
		marquee.checkAnimationValidity(
				Main.settingsWindow.getGeneralSettingsController().getHighGraphicsToggle().isSelected());

		stack.getChildren().addAll(indicator, label);
		stack.setManaged(false);
		stack.setVisible(false);

		// ImageView
		FontIcon fontIcon = JavaFXTool.getFontIcon("icm-radio-unchecked", Color.web("#d74418"), 20);
		fontIcon.visibleProperty().bind(library.getSmartController().totalInDataBaseProperty().isEqualTo(0));
		fontIcon.managedProperty().bind(fontIcon.visibleProperty());

		// X Button
		JFXButton closeButton = new JFXButton("X");
		closeButton.setFocusTraversable(false);
		int maxSize = 25;
		closeButton.setMinSize(maxSize, maxSize);
		closeButton.setPrefSize(maxSize, maxSize);
		closeButton.setMaxSize(maxSize, maxSize);
		closeButton.setStyle("-fx-background-radius:0; -fx-font-size:8px");
		closeButton.setOnAction(l -> removeTab(tab));

		// tabImage
		ImageView tabImage = new ImageView();
		tabImage.setFitWidth(24);
		tabImage.setFitHeight(24);
		tabImage.managedProperty().bind(tabImage.imageProperty().isNotNull());
		tabImage.imageProperty().bind(Bindings.createObjectBinding(() -> {
			if (library.getImage() != null)
				return library.getImage();
			else
				return null; // Media.NO_ARTWORK_IMAGE
		}, library.getImageView().imageProperty()));

		// HBOX
		HBox hBox = new HBox();
		hBox.setStyle("-fx-background-color:#101010;");
		hBox.setAlignment(Pos.CENTER);
		hBox.setOnMouseClicked(m -> {
			if (m.getButton() == MouseButton.MIDDLE)
				removeTab(tab);
		});
		hBox.getChildren().addAll(tabImage, fontIcon, stack, marquee, closeButton);

		// --Drag Events
		PauseTransition pauseTransition = new PauseTransition(Duration.millis(150));
		hBox.setOnDragExited(drag -> pauseTransition.stop());
		hBox.setOnDragEntered(dragOver -> {
			if (dragOver.getDragboard().hasFiles()) {
				// && dragOver.getGestureSource() != library.getSmartController().tableViewer)
				dragOver.acceptTransferModes(TransferMode.LINK);
				pauseTransition.playFromStart();
				pauseTransition.setOnFinished(f -> tabPane.getSelectionModel().select(tab));
			}
		});

		// --Drag Dropped
		hBox.setOnDragDropped(drop ->

		{
			// Has Files? + isFree()?
			if (drop.getDragboard().hasFiles() && getSelectedLibrary().get().getSmartController().isFree(true)
					&& drop.getGestureSource() != library.getSmartController().getNormalModeMediaTableViewer())
				getSelectedLibrary().get().getSmartController().getInputService().start(drop.getDragboard().getFiles());

			drop.setDropCompleted(true);
		});

		// stack
		library.getSmartController().getIndicatorVBox().visibleProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue) { // if it is visible
						stack.setManaged(true);
						stack.setVisible(true);
						// tab.setGraphic(hBox)
					} else {
						stack.setManaged(false);
						stack.setVisible(false);
						// tab.setGraphic(null)
					}
				});
		// library.getLibraryProgressIndicator().progressProperty().bind(indicator.progressProperty())
		// library.getLibraryProgressIndicator().visibleProperty().bind(stack.visibleProperty())

		tab.setOnCloseRequest(c -> {
			if (!library.getSmartController().isFree(true)) {
				if (c != null)
					c.consume();
			} else
				library.setLibraryStatus(LibraryStatus.CLOSED, false);
		});

		tab.setGraphic(hBox);
		tab.setContextMenu(new LibraryTabContextMenu(tab));

		// Add it
		tabPane.getTabs().add(tab);

	}

	/**
	 * Closes the tabs to the right of the given Tab
	 * 
	 * @param givenTab
	 */
	public void closeTabsToTheRight(Tab givenTab) {
		// Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;

		// The start
		int start = tabPane.getTabs().indexOf(givenTab);

		// Remove the appropriate items
		tabPane.getTabs().stream()
				// filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) > start)
				// Collect the all to a list
				.collect(Collectors.toList()).forEach(tab -> tab.getOnCloseRequest().handle(null));

	}

	/**
	 * Closes the tabs to the left of the given Tab
	 * 
	 * @param givenTab
	 */
	public void closeTabsToTheLeft(Tab givenTab) {
		// Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;

		// The start
		int start = tabPane.getTabs().indexOf(givenTab);

		// Remove the appropriate items
		tabPane.getTabs().stream()
				// filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) < start)
				// Collect the all to a list
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
	 * @param tabName the tab name
	 */
	public void removeTab(String tabName) {
		tabPane.getTabs().removeIf(tab -> tab.getTooltip().getText().equals(tabName));
	}

	/**
	 * Rename the tab with old name to a tab with a new name.
	 *
	 * @param oldName the old name
	 * @param newName the new name
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

	/**
	 * The label that indicates not libraries are opened or created
	 * 
	 * @return The emptyLabel
	 */
	public Button getEmptyLabel() {
		return createFirstLibrary;
	}

}
