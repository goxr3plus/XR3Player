/*
 * 
 */
package librarymode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import application.Main;
import customNodes.Marquee;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import services.FilesFilterService;
import smartcontroller.SmartController;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * Mechanism of showing the opened libraries each opened library is represented
 * by a Tab.
 *
 * @author SuperGoliath
 */
public class MultipleLibraries extends StackPane implements Initializable {
	
	/** The tab pane. */
	@FXML
	private TabPane tabPane;
	
	/** The empty label. */
	@FXML
	private Label emptyLabel;
	
	/** The stylus. */
	// Cursors
	ImageCursor stylus = new ImageCursor(InfoTool.getImageFromDocuments("highlighter.png"), 0, 32);
	
	/** The hand. */
	Cursor hand = Cursor.HAND;
	
	/**
	 * Constructor.
	 */
	public MultipleLibraries() {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "MultipleLibraries.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle) */
	@Override
	public void initialize(URL location , ResourceBundle resources) {
		
		// emptyLabel
		emptyLabel.setStyle("-fx-font-size:26px; -fx-background-color:white;");
		
		// TabPane
		tabPane.setId("LibrariesTabPane");
		
		// SelectionModel Listener
		tabPane.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue) -> {
			// Give a refresh to the newly selected
			if (!tabPane.getTabs().isEmpty() && ( (SmartController) newValue.getContent() ).isFree(false))
				( (SmartController) newValue.getContent() ).loadService.startService(false, true);
		});
		
		tabPane.setOnMouseMoved(m -> {
			if (!m.isControlDown())
				resetCursor();
			else
				setControlCursor();
		});
		
		// Filtering Thread
		new FilesFilterService().start(FilesFilterService.FilterMode.MULTIPLELIBS);
	}
	
	/**
	 * Resets the cursor to the default one.
	 */
	public void resetCursor() {
		if (tabPane.getCursor() != hand)
			tabPane.setCursor(hand);
	}
	
	/**
	 * Set the Cursor to control Cursor.
	 */
	public void setControlCursor() {
		if (tabPane.getCursor() != stylus)
			tabPane.setCursor(stylus);
	}
	
	/**
	 * Returns true if all the controllers are free.
	 *
	 * @param showMessage the show message
	 * @return true, if is free
	 */
	public boolean isFree(boolean showMessage) {
		for (Tab tab : tabPane.getTabs())
			if (! ( (SmartController) tab.getContent() ).isFree(showMessage))
				return false;
			
		return true;
	}
	
	/**
	 * Returns the selected library.
	 *
	 * @return the selected library
	 */
	public Library getSelectedLibrary() {
		
		// selection model is empty?
		return !tabPane.getSelectionModel().isEmpty() ? Main.libraryMode
		        .getLibraryWithName(tabPane.getSelectionModel().getSelectedItem().getTooltip().getText()) : null;
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
	 * Add a new Tab.
	 *
	 * @param library the library
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
		indicator.visibleProperty().bind(library.getSmartController().getIndicator().visibleProperty());
		indicator.setMaxSize(30, 5);
		
		// text
		Text text = new Text();
		text.setStyle("-fx-font-size:70%;");	
		text.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100.00).asString("%.02f %%"));
		//text.visibleProperty().bind(library.getSmartController().inputService.runningProperty())
		
		Marquee marquee = new Marquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		marquee.setStyle("-fx-background-radius:15 0 0 0; -fx-background-color:rgb(255,255,255,0.7); -fx-border-color:transparent;");
		
		stack.getChildren().addAll(indicator, text);
		stack.setManaged(false);
		stack.setVisible(false);
		
		// HBOX
		HBox hBox = new HBox();
		hBox.getChildren().addAll(stack, marquee);
		
		// stack
		library.getSmartController().getIndicator().visibleProperty().addListener(l -> {
			if (indicator.isVisible()) {
				stack.setManaged(true);
				stack.setVisible(true);
				// tab.setGraphic(hBox);
			} else {
				stack.setManaged(false);
				stack.setVisible(false);
				// tab.setGraphic(null);
			}
		});
		
		tab.setOnCloseRequest(c -> {
			if (library.getSmartController().isFree(true))
				library.libraryOpenClose(false, false);
			else
				c.consume();
		});
		
		tab.setGraphic(hBox);
		tabPane.getTabs().add(tab);
	}
	
	/**
	 * Remove tab with that name.
	 *
	 * @param tabName the tab name
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
	 * @param oldName the old name
	 * @param newName the new name
	 */
	public void renameTab(String oldName , String newName) {
		
		tabPane.getTabs().stream().forEach(tab -> {
			if (tab.getTooltip().getText().equals(oldName)) {
				// tab.textProperty().unbind();
				tab.getTooltip().textProperty().unbind();
				// tab.setText(InfoTool.getMinString(newName, 15));
				tab.getTooltip().setText(newName);
			}
		});
	}
	
}
