/*
 * 
 */
package com.goxr3plus.xr3player.controllers.general;

import org.atteo.evo.inflector.English;
import org.controlsfx.control.textfield.TextFields;

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
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.librarymode.Library;
import com.goxr3plus.xr3player.controllers.loginmode.User;
import com.goxr3plus.xr3player.controllers.windows.SearchBoxWindow;

/**
 * Represents the Libraries Search.
 *
 * @author GOXR3PLUS
 */
public class SearchBox extends HBox {

	// -----------------------------------------

	private final SearchBoxWindow searchBoxWindow = new SearchBoxWindow();

	/** The Search Service */
	private final SearchService searchService = new SearchService();

	/** The SearchField */
	private final TextField searchField = TextFields.createClearableTextField();

	/** The region. */
	public final Region region = new Region();

	/** The search progress. */
	public final ProgressIndicator searchProgress = new ProgressIndicator();

	// -------------------------------------------------------------

	public enum SearchBoxType {

		LIBRARYSEARCHBOX, USERSSEARCHBOX;
	}

	private final SearchBoxType searchBoxType;

	/**
	 * Constructor.
	 */
	public SearchBox(SearchBoxType searchBoxType) {
		this.searchBoxType = searchBoxType;

		super.setAlignment(Pos.CENTER);
		getStyleClass().add("libraries-search-box");

		// Region
		region.setStyle("-fx-background-color:rgb(0,0,0,0.8)");

		// SearchProgress
		searchProgress.visibleProperty().bind(searchService.runningProperty());
		searchProgress.progressProperty().bind(searchService.progressProperty());
		region.visibleProperty().bind(searchService.runningProperty());

		// SearchField
		searchField.setFocusTraversable(false);
		searchField.setMinWidth(100);
		searchField.setPrefWidth(200);
		searchField.setMaxWidth(Integer.MAX_VALUE);
		if (searchBoxType == SearchBoxType.USERSSEARCHBOX)
			searchField.setPromptText("Search Users...");
		else if (searchBoxType == SearchBoxType.LIBRARYSEARCHBOX)
			searchField.setPromptText("Search Libraries...");
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!searchField.getText().isEmpty())
				searchService.startService();
			else {
				searchBoxWindow.clearItems();
				searchBoxWindow.setLabelText("Write something...");
			}
		});
		searchField.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				searchBoxWindow.getWindow().close();
		});
		searchField.setOnAction(a -> {
			if (!searchField.getText().isEmpty())
				searchService.startService();
			else {
				searchBoxWindow.clearItems();
				searchBoxWindow.setLabelText("Write something...");
			}
		});
		getChildren().add(searchField);

		// searchBoxWindow
		// searchBoxWindow.getWindow().setWidth(searchField.getPrefWidth());

	}

	/**
	 * @return the searchBoxWindow
	 */
	public SearchBoxWindow getSearchBoxWindow() {
		return searchBoxWindow;
	}

	/**
	 * Register the window listeners to the window so it follows the Main window of
	 * the application
	 * 
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

			getSearchBoxWindow().clearItems();
			word = searchField.getText();
			reset();
			start();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.concurrent.Service#createTask()
		 */
		@Override
		protected Task<Void> createTask() {
			return new Task<>() {
				@Override
				protected Void call() throws Exception {
					// Variables
					found = 0;
					word = word.toLowerCase();

					// matcher
					if (searchBoxType == SearchBoxType.USERSSEARCHBOX)
						Main.loginMode.viewer.getItemsObservableList().stream()
								.filter(user -> ((User) user).getName().toLowerCase().contains(word)).forEach(user -> {
							Platform.runLater(() -> getSearchBoxWindow().addItem(((User) user).getName(),
									ac -> Main.loginMode.viewer.setCenterItem(user)));
							++found;
						});
					else if (searchBoxType == SearchBoxType.LIBRARYSEARCHBOX)
						Main.libraryMode.viewer.getItemsObservableList().stream()
								.filter(library -> ((Library) library).getLibraryName().toLowerCase().contains(word))
								.forEach(library -> {
									Platform.runLater(
											() -> searchBoxWindow.addItem(((Library) library).getLibraryName(),
													ac -> Main.libraryMode.viewer.setCenterItem(library)));
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
			if (searchBoxType == SearchBoxType.USERSSEARCHBOX)
				getSearchBoxWindow().setLabelText("Found [ " + found + " ] " + English.plural("User", found));
			else if (searchBoxType == SearchBoxType.LIBRARYSEARCHBOX)
				searchBoxWindow.setLabelText("Found [ " + found + " ] " + English.plural("Library", found));

			// Show the Window
			getSearchBoxWindow().getWindow().show();
			getSearchBoxWindow().recalculateWindowPosition(searchField);
			Main.window.requestFocus();
			searchField.requestFocus();
		}
	}

}
