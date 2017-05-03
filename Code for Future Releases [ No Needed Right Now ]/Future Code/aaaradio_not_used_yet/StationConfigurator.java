/*
 * 
 */
package aaaradio_not_used_yet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import aacode_to_be_used_in_future.TagsBar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class StationConfigurator.
 */
public class StationConfigurator extends GridPane implements Initializable {

	/** The pop over. */
	PopOver popOver = new PopOver();
	
	/** The tags bar. */
	TagsBar tagsBar = new TagsBar();

	/** The cancel. */
	@FXML
	private Button cancel;

	/** The save. */
	@FXML
	private Button save;

	/** The station name. */
	@FXML
	private JFXTextField stationName;

	/** The station url. */
	@FXML
	private JFXTextField stationUrl;

	/** The station creator. */
	@FXML
	private JFXTextField stationCreator;

	/** The station description. */
	@FXML
	private JFXTextArea stationDescription;

	/**
	 * Constructor.
	 */
	public StationConfigurator() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "StationConfigurator.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		getStyleClass().add("station-configurator");
		popOver.setArrowLocation(ArrowLocation.RIGHT_TOP);
		popOver.setDetachable(false);
		popOver.getScene().getStylesheets()
				.add(getClass().getResource(InfoTool.styLes +InfoTool.applicationCss).toExternalForm());
		popOver.setContentNode(this);
	}

	/**
	 * Show.
	 *
	 * @param station the station
	 * @param button the button
	 */
	public void show(RadioStation station, Button button) {
		popOver.show(button);
	}

	/**
	 * Show.
	 *
	 * @param button the button
	 */
	public void show(Button button) {
		tagsBar.getTags().clear();
		stationName.clear();
		stationUrl.clear();
		stationCreator.clear();
		stationDescription.clear();
		popOver.show(button);
	}

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// tagsBar
		tagsBar.getEntries().addAll(RadioStationsController.musicGenres);
		add(tagsBar,0, 0);

		// cancel
		cancel.setOnAction(e -> {
			popOver.hide();
		});

		// save
		save.setOnAction(e -> {
			if (tagsBar.getTags().size() > 0) {

			} else
				Notifications.create().text("You must give at least one genre for the station!").darkStyle()
						.showInformation();
		});

	}

}
