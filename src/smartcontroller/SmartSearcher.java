/*
 * 
 */
package smartcontroller;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.textfield.TextFields;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import media.Audio;
import tools.InfoTool;
import xplayer.presenter.AudioType;

/**
 * This class is used as a search Box for SmartController.
 *
 * @author GOXR3PLUS
 */
public class SmartSearcher extends HBox {

    /** The search field. */
    TextField searchField = TextFields.createClearableTextField();

    /** The controller. */
    // Variables
    SmartController controller;

    /** The service. */
    SearchService service = new SearchService();

    /**
     * Constructor.
     *
     * @param control
     *            the control
     */
    public SmartSearcher(SmartController control) {
	controller = control;

	getStyleClass().add("search-box");

	// searchField
	searchField.setMaxWidth(Integer.MAX_VALUE);
	searchField.setPromptText("Search.....");
	searchField.textProperty().addListener((observable, newValue, oldValue) -> {
	    if (searchField.getText().isEmpty()) {

		//Reset the page number to the default before search
		if (service.getPaneNumberBeforeSearch() <= controller.maximumList())
		    controller.listNumber.set(service.getPaneNumberBeforeSearch());

		//continue 
		controller.getNextButton().setDisable(false);
		controller.loadService.startService(false, false);
		//Main.advancedSearch.searchOnFlySelected()

	    } else if (controller.isFree(false) && control.instantSearch.isSelected())
		service.search();
	});
	searchField.editableProperty().bind(service.runningProperty().not());
	searchField.disableProperty().bind(Main.advancedSearch.showingProperty());
	searchField.setOnAction(ac -> {
	    if (controller.isFree(false))
		service.search();
	});
	searchField.focusedProperty().addListener(l -> {
	    if (!isActive()) {
		service.pageBeforeSearch = controller.listNumber.get();
		ScrollBar verticalBar = (ScrollBar) controller.tableViewer.lookup(".scroll-bar:vertical");
		service.previousVerticalScrollValue = verticalBar.getValue();
	    }
	});

	// searchField.setOnMouseClicked(m -> {
	// if (m.getButton() == MouseButton.SECONDARY)
	// Main.advancedSearch.show(searchField, controller);
	// });
	searchField.setContextMenu(new ContextMenu());
	getChildren().add(searchField);

    }

    /**
     * Returns true if the Search service is currently activated(if reset button is still visible).
     *
     * @return <b> True </b> if searchField is empty
     */
    public boolean isActive() {
	return !searchField.getText().isEmpty();
    }

    /**
     * @return The Vertical Scroll Bar position of SmartController TableViewer before the search activated
     */
    public double getVerticalScrollBarPosition() {
	return service.previousVerticalScrollValue;
    }

    /**
     * @return The Vertical Scroll Bar position of SmartController TableViewer before the search activated
     */
    public void setVerticalScrollBarPosition(double value) {
	service.previousVerticalScrollValue = value;
    }

    /**
     * The Class SearchService.
     */
    class SearchService extends Service<Void> {

	/** The word. */
	private String word;

	/** The counter. */
	int counter = 0;

	/** The page before search. */
	int pageBeforeSearch = 0;

	/**
	 * The previous value of the VerticalScroll bar in the table viewer
	 */
	double previousVerticalScrollValue = -1;

	/**
	 * Constructor.
	 */
	public SearchService() {
	    setOnSucceeded(s -> done());
	    setOnFailed(f -> {
		controller.getNextButton().setDisable(false);
		done();
	    });

	}

	/**
	 * You can start the search Service by calling this method.
	 */
	public void search() {
	    // advanced search?
	    // if (Main.advancedSearch.isShowing())
	    // word = Main.advancedSearch.getTextForSearching();
	    // else
	    word = searchField.getText();
	    controller.getRegion().visibleProperty().bind(runningProperty());
	    controller.getIndicator().progressProperty().bind(progressProperty());
	    controller.getCancelButton().setText("Searching...");
	    reset();
	    start();
	}

	/**
	 * When the Service is done.
	 */
	private void done() {
	    controller.updateList();
	    controller.unbind();

	}

	/**
	 * Returns the page that the SmartController was before the Search.
	 *
	 * @return the pane number before search
	 */
	private int getPaneNumberBeforeSearch() {
	    return pageBeforeSearch;
	}

	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {
		@Override
		protected Void call() throws Exception {

		    // Counter
		    counter = 0;

		    // Given Work
		    System.out.println("Searching for word:[" + word + "]");

		    controller.getNextButton().setDisable(true);
		    controller.observableList.clear();
		    controller.listNumber.set(pageBeforeSearch);

		    try (ResultSet set = Main.dbManager.connection1.createStatement().executeQuery("SELECT* FROM '"
			    + controller.getDataBaseTableName() + "' WHERE PATH LIKE '%" + word + "%'")) {

			Audio song = null;
			while (set.next()) {
			    song = new Audio(set.getString("PATH"),
				    InfoTool.durationInSeconds(set.getString("PATH"), AudioType.FILE),
				    set.getDouble("STARS"), set.getInt("TIMESPLAYED"), set.getString("DATE"),
				    set.getString("HOUR"), controller.genre);
			    controller.observableList.add(song);
			    ++counter;
			    if (counter >= controller.maximumPerList)
				break;

			}
		    } catch (Exception ex) {
			Main.logger.log(Level.WARNING, "", ex);
		    }

		    return null;
		}

	    };
	}

    }

    /**
     * This class contains more advanced search features.
     *
     * @author GOXR3PLUS
     */
    public static class AdvancedSearch extends BorderPane {

	/** The search field. */
	@FXML
	private JFXTextField searchField;

	/** The search on fly. */
	@FXML
	private JFXToggleButton searchOnFly;

	/** The case sensitive. */
	@FXML
	private JFXCheckBox caseSensitive;

	/** The bottom V box. */
	@FXML
	private VBox bottomVBox;

	/** The pop over. */
	private PopOver popOver = new PopOver();

	/** The controller. */
	SmartController controller;

	/**
	 * Constructor.
	 */
	public AdvancedSearch() {

	    // Load the f x m l file
	    FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "SearchSettings.fxml"));
	    loader.setController(this);
	    loader.setRoot(this);

	    try {
		loader.load();
	    } catch (IOException ex) {
		Main.logger.log(Level.WARNING, "", ex);
	    }

	}

	/**
	 * Called as soon as .fxml has been initialized
	 */
	@FXML
	private void initialize() {

	    // TagsBar
	    //			TagsBar tagsBar = new TagsBar();
	    //			tagsBar.setMaxWidth(InfoTool.getScreenWidth() / 2.5);
	    //			tagsBar.getEntries().addAll(RadioStationsController.musicGenres);
	    //			bottomVBox.getChildren().add(tagsBar);

	    // PopOver
	    popOver.setContentNode(this);
	    popOver.getScene().getStylesheets()
		    .add(getClass().getResource(InfoTool.styLes + InfoTool.applicationCss).toExternalForm());
	    popOver.setDetachable(false);
	    popOver.setAutoHide(true);
	    popOver.setArrowLocation(ArrowLocation.TOP_CENTER);
	    popOver.setOnHidden(h -> controller.searchService.searchField.textProperty().unbind());

	    // this
	    setOnMouseEntered(m -> requestFocus());

	    searchField.setContextMenu(new ContextMenu());

	}

	/**
	 * Search on fly selected.
	 *
	 * @return true, if successful
	 */
	public boolean searchOnFlySelected() {
	    return searchOnFly.isSelected();
	}

	/**
	 * Shows the Advanced Search with the given parameters.
	 *
	 * @param node
	 *            the node
	 * @param controller
	 *            the controller
	 */
	public void show(Node node, SmartController controller) {
	    searchField.editableProperty().bind(controller.searchService.service.runningProperty().not());
	    this.controller = controller;
	    searchField.setText(this.controller.searchService.searchField.getText());
	    this.controller.searchService.searchField.textProperty().bind(searchField.textProperty());

	    // Find the correct arrow location
	    double width = popOver.getWidth();
	    double height = popOver.getHeight();
	    Bounds bounds = controller.searchService.searchField
		    .localToScreen(controller.searchService.searchField.getBoundsInLocal());
	    boolean fitOnTop = bounds.getMinY() - height > 0; // top?
	    boolean fitOnLeft = bounds.getMinX() - width > 0; // left?
	    boolean fitOnRight = bounds.getMaxX() + width < InfoTool.getScreenWidth();// right?
	    boolean fitOnBottom = bounds.getMaxY() + height < InfoTool.getScreenHeight(); // bottom?

	    if (fitOnTop)
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
	    else if (fitOnBottom)
		popOver.setArrowLocation(ArrowLocation.TOP_CENTER);
	    else if (fitOnLeft)
		popOver.setArrowLocation(ArrowLocation.RIGHT_CENTER);
	    else if (fitOnRight)
		popOver.setArrowLocation(ArrowLocation.LEFT_CENTER);

	    popOver.show(node);
	    searchField.requestFocus();
	}

	/**
	 * Checks if is showing.
	 *
	 * @return true, if is showing
	 */
	public boolean isShowing() {
	    return popOver.isShowing();
	}

	/**
	 * Showing property.
	 *
	 * @return the read only boolean property
	 */
	public ReadOnlyBooleanProperty showingProperty() {
	    return popOver.showingProperty();
	}

	/**
	 * Gets the text for searching.
	 *
	 * @return the text for searching
	 */
	public String getTextForSearching() {
	    return searchField.getText();
	}

	/**
	 * Hide.
	 */
	public void hide() {
	    popOver.hide();
	}

    }

}
