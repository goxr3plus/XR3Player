/*
 * 
 */
package aaaradio_not_used_yet;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;

// TODO: Auto-generated Javadoc
/**
 * The Class RadioGenre.
 */
public class RadioGenre extends Button {

	/**
	 * Instantiates a new radio genre.
	 *
	 * @param genre the genre
	 */
	public RadioGenre(String genre) {
		setBackground(null);
		this.setText(genre);
		this.setFocusTraversable(false);
		this.setStyle("-fx-background-color:transparent; -fx-text-fill:black;");
		this.setTooltip(new Tooltip(
				"Oldies is a commonly used term to describe a radio "
				+ "\nformat thats playlists focus primarily on popular "
				+ "\nmusic from a period of 15 to 55 years before the "
				+ "\npresent day. Commonly, the Oldies format includes "
				+ "\nclassic and popular Rock and Roll from the 1950s "
				+ "\nup to the 1980s ."));
		
		
		setOnMouseReleased( m->{
			if(m.getButton() == MouseButton.SECONDARY){
				
			}
		});
	}
	
}
