package com.goxr3plus.xr3player.controllers.smartcontroller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.goxr3plus.xr3player.services.smartcontroller.FiltersModeService;
import com.goxr3plus.xr3player.utils.general.InfoTool;

public class SmartControllerFiltersMode extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private MenuButton filterMenuButton;

	@FXML
	private ToggleGroup selectedFilter;

	@FXML
	private ListView<String> listView;

	@FXML
	private Label nothingFoundLabel;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Label detailsLabel;

	@FXML
	private Button backToMedia;

	@FXML
	private VBox loadingVBox;

	@FXML
	private Label loadingVBoxLabel;

	@FXML
	private ProgressBar loadingProgressBar;

	@FXML
	private JFXButton cancelButton;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	// -------------------------------------------------------------

	/** A private instance of the SmartController it belongs */
	private final SmartController smartController;

	private final MediaTableViewer mediaTableViewer;

	// -------------------------------------------------------------

	private final FiltersModeService service;

	// -------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public SmartControllerFiltersMode(SmartController smartController) {
		this.service = new FiltersModeService(smartController, this);
		this.smartController = smartController;
		this.mediaTableViewer = new MediaTableViewer(smartController, SmartControllerMode.FILTERS_MODE);

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SMARTCONTROLLER_FXMLS + "SmartControllerFiltersMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		// indicatorVBox
		loadingVBox.visibleProperty().bind(service.runningProperty());

		// progressIndicator
		loadingProgressBar.progressProperty().bind(service.progressProperty());

		// backToMedia
		backToMedia.setOnAction(a -> smartController.getModesTabPane().getSelectionModel().select(0));

		// listView
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				refreshTableView();
		});

		// listView - cellFactory
		listView.setCellFactory(lv -> new ListCell<>() {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
					setTooltip(null);
				} else {
					// String text = InfoTool.getFileName(item); // get text from item
					// setGraphic(new ImageView(artistImage))
					setText(item);
					setTooltip(new Tooltip(item));
				}
			}
		});

		// detailsLabel
		detailsLabel.setVisible(false);

		// borderPane
		borderPane.setCenter(mediaTableViewer);

		// selectedFilter
		selectedFilter.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// Regenerate
				regenerate();
				String text = ((MenuItem) newValue).getText();

				// FilterMenuButton
				filterMenuButton.setText("Filter : " + text);

				// Change Tab Label
				smartController.getFiltersModeTab().setText("Filter : " + text);

				// System.out.println("Selected Filter entered...")
			}
		});

		// cancelButton
		cancelButton.setOnAction(backToMedia.getOnAction());

	}

	/**
	 * Refreshes the whole artists mode [ Heavy procedure for many files ]
	 */
	public void regenerate() {
		service.regenerate();
	}

	/**
	 * Refreshes the TableView based on the current artist
	 */
	public void refreshTableView() {
		service.refreshTableView(listView.getSelectionModel().getSelectedItem());
	}

	/**
	 * @return the smartController
	 */
	public SmartController getSmartController() {
		return smartController;
	}

	/**
	 * @return the listView
	 */
	public ListView<String> getListView() {
		return listView;
	}

	/**
	 * @return the detailsLabel
	 */
	public Label getDetailsLabel() {
		return detailsLabel;
	}

	/**
	 * @return the mediaTableViewer
	 */
	public MediaTableViewer getMediaTableViewer() {
		return mediaTableViewer;
	}

	/**
	 * @return the progressLabel
	 */
	public Label getProgressLabel() {
		return loadingVBoxLabel;
	}

	/**
	 * @return the service
	 */
	public FiltersModeService getService() {
		return service;
	}

	/**
	 * @return the selectedFilter
	 */
	public ToggleGroup getSelectedFilter() {
		return selectedFilter;
	}

	/**
	 * @return the nothingFoundLabel
	 */
	public Label getNothingFoundLabel() {
		return nothingFoundLabel;
	}

}
