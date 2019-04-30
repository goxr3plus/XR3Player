/*
 * 
 */
package com.goxr3plus.xr3player.controllers.smartcontroller;

import org.controlsfx.control.textfield.TextFields;

import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.services.smartcontroller.SearchService;

/**
 * WARNING! I WAS DRUNK WHEN WRITING THIS CLASS ;) , REALLY! FUCKED UP
 * XOAXOAOXOAO
 * 
 * This class is used as a search Box for SmartController.
 *
 * @author GOXR3PLUS
 */
public class SmartControllerSearcher extends HBox {

	/** The search field. */
	private final TextField searchField = TextFields.createClearableTextField();

	/** The controller. */
	private SmartController smartController;

	/** The service. */
	private SearchService searchService;

	private boolean saveSettingBeforeSearch = true;

	/**
	 * Constructor.
	 *
	 * @param smartController The SmartController
	 */
	public SmartControllerSearcher(SmartController smartController) {
		this.smartController = smartController;
		searchService = new SearchService(smartController);

		// Super
		setAlignment(Pos.CENTER);
		getChildren().add(searchField);
		HBox.setHgrow(searchField, Priority.ALWAYS);
		getStyleClass().add("search-box");

		// searchField
		searchField.setMinWidth(100);
		searchField.setPrefWidth(400);
		searchField.setMaxWidth(Integer.MAX_VALUE);
		searchField.setPromptText("Search.....");
		searchField.textProperty().addListener((observable, newValue, oldValue) -> {

			// Check if the controller is free
			if (!smartController.isFree(false))
				return;

			if (searchField.getText().isEmpty()) {
				saveSettingBeforeSearch = true;

				// Reset Aphabetical Bar Letter
				smartController.getAlphabetBar().setLetterPressed(false);

				// Reset the page number to the default before search
				if (searchService.getPaneNumberBeforeSearch() <= smartController.getMaximumList())
					smartController.currentPageProperty().set(searchService.getPaneNumberBeforeSearch());

				// continue
				smartController.getNavigationHBox().setDisable(false);
				smartController.getLoadService().startService(false, false, false);

			} else if (Main.settingsWindow.getPlayListsSettingsController().getInstantSearch().isSelected()) {
				saveSettingsBeforeSearch();
				searchService.search(newValue);
			}
		});
		searchField.setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				searchField.clear();
		});
		searchField.editableProperty().bind(searchService.runningProperty().not());
		// searchField.disableProperty().bind(Main.advancedSearch.showingProperty())
		searchField.setOnAction(ac -> reSearch());

		// Override the default context menu
		searchField.setContextMenu(new ContextMenu());

	}

	/**
	 * This method fires a search again based on currently given parameters
	 */
	public void reSearch() {
		if (!smartController.isFree(false))
			return;
		saveSettingsBeforeSearch();
		searchService.search(searchField.getText());
	}

	/**
	 * //Save the Settings before the first search -> [ ScrollBar position and
	 * current page of the SmartController ]
	 */
	private void saveSettingsBeforeSearch() {

		// lock it so no override happens during the search
		if (!saveSettingBeforeSearch)
			return;

		searchService.setPageBeforeSearch(smartController.currentPageProperty().get());
		smartController.getVerticalScrollBar()
				.ifPresent(scrollBar -> smartController.setVerticalScrollValueWithoutSearch(scrollBar.getValue()));
		saveSettingBeforeSearch = false;
	}

	/**
	 * Returns true if the Search service is currently activated(if reset button is
	 * still visible).
	 *
	 * @return <b> True </b> if searchField is empty
	 */
	public boolean isActive() {
		return !searchField.getText().isEmpty();
	}

	/**
	 * @return the service
	 */
	public SearchService getService() {
		return searchService;
	}

	/**
	 * @return the searchField
	 */
	public TextField getSearchField() {
		return searchField;
	}

}
