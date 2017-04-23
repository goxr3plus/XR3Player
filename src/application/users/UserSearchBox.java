/*
 * 
 */
package application.users;

import org.controlsfx.control.textfield.TextFields;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import librarysystema.SearchBoxWindow;

/**
 * Represents the Libraries Search.
 *
 * @author GOXR3PLUS
 */
public class UserSearchBox extends HBox {

    /** Libraries Search Window Controller */
    SearchBoxWindow searchBoxWindow = new SearchBoxWindow();

    /** The Search Service */
    private final SearchService service = new SearchService();

    /** The SearchField */
    private final TextField searchField = TextFields.createClearableTextField();

    /** The region. */
    public final Region region = new Region();

    /** The search progress. */
    public final ProgressIndicator searchProgress = new ProgressIndicator();

    // -------------------------------------------------------------

    /**
     * Constructor.
     */
    public UserSearchBox() {

	super.setAlignment(Pos.CENTER);
	getStyleClass().add("libraries-search-box");

	// Region
	region.setStyle("-fx-background-color:rgb(0,0,0,0.8)");

	// SearchProgress
	searchProgress.visibleProperty().bind(service.runningProperty());
	searchProgress.progressProperty().bind(service.progressProperty());
	region.visibleProperty().bind(service.runningProperty());

	// SearchField
	searchField.setMinWidth(280);
	searchField.setPrefWidth(280);
	searchField.setPromptText("Search Users...");
	searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	    if (!searchField.getText().isEmpty())
		service.startService();
	    else {
		searchBoxWindow.clearItems();
		searchBoxWindow.setLabelText("Type something bruh ;)");
	    }
	});
	searchField.setOnKeyReleased(key -> {
	    if (key.getCode() == KeyCode.ESCAPE)
		searchBoxWindow.getWindow().close();
	});
	searchField.setOnAction(a -> {
	    if (!searchField.getText().isEmpty())
		service.startService();
	    else {
		searchBoxWindow.clearItems();
		searchBoxWindow.setLabelText("Type something bruh ;)");
	    }
	});
	getChildren().add(searchField);

	// searchBoxWindow
	searchBoxWindow.getWindow().setWidth(searchField.getPrefWidth());

    }

    /**
     * @param window
     */
    public void registerListeners(Stage window) {
	searchBoxWindow.registerListeners(window, searchField);
    }

    /*-----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							SearchService
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */
    /**
     * This service is searching for Libraries with the given name.
     *
     * @author GOXR3PLUS
     */
    private class SearchService extends Service<Void> {

	/** The word. */
	String word;

	/** The found. */
	int found;

	/**
	 * Constructor.
	 */
	public SearchService() {
	    setOnSucceeded(s -> done());
	    setOnFailed(s -> done());
	    setOnCancelled(c -> done());
	}

	/**
	 * Start the Service.
	 */
	public void startService() {
	    if (isRunning())
		return;

	    searchBoxWindow.clearItems();
	    word = searchField.getText();
	    reset();
	    start();
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask() */
	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {
		@Override
		protected Void call() throws Exception {
		    // Variables
		    found = 0;
		    word = word.toLowerCase();

		    // matcher
		    Main.loginMode.userViewer.getItemsObservableList().stream().filter(user -> user.getUserName().toLowerCase().contains(word))
			    .forEach(user -> {
				Platform.runLater(() -> searchBoxWindow.addItem(user.getUserName(),
					ac -> Main.loginMode.userViewer.setCenterIndex(user.getPosition())));
				++found;
			    });
		    return null;
		}

	    };
	}

	/**
	 * When the SearchService is done.
	 */
	private void done() {
	    // Change Label text
	    searchBoxWindow.setLabelText("Found [ " + found + " ] " + (found == 1 ? "User" : " Users"));

	    // Show the Window
	    searchBoxWindow.getWindow().show();
	    searchBoxWindow.recalculateWindowPosition(searchField);
	    Main.window.requestFocus();
	    searchField.requestFocus();
	}
    }

}
