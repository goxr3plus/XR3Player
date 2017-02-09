/*
 * 
 */
package aaaradio_not_used_yet;

import java.net.URL;

import application.Main;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class RadioStation.
 */
public class RadioStation extends Button {

	/** The station name. */
	private String stationName;
	
	/** The stream URL. */
	private URL streamURL;
	
	/** The stars. */
	private double stars;
	
	/** The station creator. */
	private String stationCreator;
	
	/**
	 * Tags are represented by a String which must be splitted by ("<:>") to get
	 * each tag.
	 */
	private String tags;

	/**
	 * Instantiates a new radio station.
	 *
	 * @param stationName the station name
	 * @param streamUrl the stream url
	 * @param tags the tags
	 * @param stars the stars
	 */
	// Constructor
	public RadioStation(String stationName, URL streamUrl, String tags, double stars) {
		this.setName(stationName);
		this.setStreamURL(streamUrl);
		this.setTags(tags);
		this.setStars(stars);

		// Graphics
		setId("button");
		setText(InfoTool.getMinString(stationName, 22));
		// setGraphic(InfoTool.getImageView("play.png"));
		setTooltip(new Tooltip(stationName));

//		setOnAction(a -> Main.stationsInfostructure.radioPlayer.playRadioStream(streamUrl));
//
//		setOnMouseReleased(m -> {
//			if (m.getButton() == MouseButton.SECONDARY)
//				Main.stationsInfostructure.contextMenu.show(streamUrl, m.getScreenX(), m.getScreenY());
//		});
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return stationName;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.stationName = name;
	}

	/**
	 * Gets the stream URL.
	 *
	 * @return the streamURL
	 */
	public URL getStreamURL() {
		return streamURL;
	}

	/**
	 * Sets the stream URL.
	 *
	 * @param streamURL the streamURL to set
	 */
	public void setStreamURL(URL streamURL) {
		this.streamURL = streamURL;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Sets the tags.
	 *
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * Gets the stars.
	 *
	 * @return the stars
	 */
	public double getStars() {
		return stars;
	}

	/**
	 * Sets the stars.
	 *
	 * @param stars the stars to set
	 */
	public void setStars(double stars) {
		this.stars = stars;
	}

}
