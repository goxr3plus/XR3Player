/**
 * 
 */
package xplayer.presenter;

import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.radialmenu.RadialMenu;
import eu.hansolo.enzo.radialmenu.RadialMenuBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuItem;
import eu.hansolo.enzo.radialmenu.RadialMenuItemBuilder;
import eu.hansolo.enzo.radialmenu.RadialMenuOptionsBuilder;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import tools.ActionTool;
import tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class XPlayerRadialMenu {

    /** The next pressed. */
    private boolean nextPressed;

    /** The previous pressed. */
    private boolean previousPressed;

    //
    private RadialMenu radialMenu;

    //
    RadialMenuItem stop = RadialMenuItemBuilder.create().symbol(SymbolType.STOP).tooltip("Stop").size(40).build();
    RadialMenuItem play = RadialMenuItemBuilder.create().symbol(SymbolType.PLAY).tooltip("Play").size(40).build();
    RadialMenuItem pause = RadialMenuItemBuilder.create().symbol(SymbolType.PAUSE).tooltip("Pause").size(40).build();
    RadialMenuItem mute = RadialMenuItemBuilder.create().selectable(true).selected(false)
	    .thumbnailImageName(getClass().getResource(InfoTool.images + "mute.png").toExternalForm()).tooltip("Mute")
	    .size(40).build();

    //
    RadialMenuItem refresh = RadialMenuItemBuilder.create().symbol(SymbolType.REFRESH).tooltip("Refresh").size(40)
	    .build();
    RadialMenuItem search = RadialMenuItemBuilder.create().symbol(SymbolType.SEARCH).tooltip("Search").size(40).build();

    //
    RadialMenuItem next = RadialMenuItemBuilder.create().symbol(SymbolType.FORWARD).tooltip("Next").size(40).build();
    RadialMenuItem previous = RadialMenuItemBuilder.create().symbol(SymbolType.REWIND).tooltip("Previous").size(40)
	    .build();

    XPlayerController xPlayerController;

    /**
     * Constructor
     * 
     * @param xPlayerController
     * 
     */
    public XPlayerRadialMenu(XPlayerController xPlayerController) {
	this.xPlayerController = xPlayerController;

	//----------------RadialMenu
	radialMenu = RadialMenuBuilder.create()
		.options(RadialMenuOptionsBuilder.create().degrees(180).buttonFillColor(Color.BLUE).offset(-140) //-135 with next and previous + 350 degrees //-160 with 360 degrees
			.radius(60).buttonSize(40).tooltipsEnabled(true).buttonHideOnSelect(false)
			.buttonHideOnClose(false).buttonAlpha(1.0).build())
		.items(
			//RadialMenuItemBuilder.create().thumbnailImageName(getClass().getResource("star.png").toExternalForm()).size(40).build(),
			stop, play, pause,
			//next, 
			refresh, mute, search
		//previous
		).build();

	//radialMenu.setStyle("-fx-background-color:magenta;")

	radialMenu.setPickOnBounds(false);
	radialMenu.setCursor(Cursor.HAND);
	if (xPlayerController.getKey() == 0)
	    radialMenu.open();

	//--disable the bitches
	//radialMenu.hide()
	next.setDisable(true);
	previous.setDisable(true);

	//--Click
	radialMenu.setOnItemClicked(clickEvent -> {

	    if (clickEvent.item == stop)
		xPlayerController.xPlayer.stop();
	    else if (clickEvent.item == play)
		xPlayerController.playOrReplay();
	    else if (clickEvent.item == pause)
		xPlayerController.pause();
	    if (clickEvent.item == refresh)
		xPlayerController.replaySong();
	    else if (clickEvent.item == search)
		xPlayerController.openAudioInExplorer();
	});

	radialMenu.setOnMenuOpenStarted(menuEvent -> System.out.println("Menu starts to open"));
	radialMenu.setOnMenuOpenFinished(menuEvent -> System.out.println("Menu finished to open"));
	radialMenu.setOnMenuCloseStarted(menuEvent -> System.out.println("Menu starts to close"));
	radialMenu.setOnMenuCloseFinished(menuEvent -> System.out.println("Menu finished to close"));
	radialMenu.setOnItemSelected(selectionEvent -> {
	    if (selectionEvent.item == mute)
		xPlayerController.xPlayer.setMute(true);
	});
	radialMenu.setOnItemDeselected(selectionEvent -> {
	    if (selectionEvent.item == mute)
		xPlayerController.xPlayer.setMute(false);
	});

    }

    /**
     * @return The RadialMenuButton
     */
    public RadialMenu getRadialMenuButton() {
	return radialMenu;
    }

    /**
     * @return True if the RadialMenu is Hidden or false if not
     */
    public boolean isHidden() {
	return radialMenu.getOpacity() == 0.0;
    }

    /**
     * This method is used to go on next song.
     */
    public void goNext() {
	//	if (!next.isDisable()) {
	//	    nextPressed = true;
	//	    previousPressed = false;
	//	}
    }

    /**
     * This method is used to go on next song.
     */
    public void goPrevious() {
	//	if (!previous.isDisable()) {
	//	    nextPressed = false;
	//	    previousPressed = true;
	//	}
    }

    /**
     * Determines if the next has been pressed.
     *
     * @return true, if successful
     */
    public boolean nextHasBeenPressed() {
	return nextPressed;
    }

    /**
     * Determines if the previous has been pressed.
     *
     * @return true, if successful
     */
    public boolean previousHasBeenPressed() {
	return previousPressed;
    }

    /**
     * Resets nextPressed and previousPressed to false.
     */
    public void resetPreviousAndNextIfPressed() {
	nextPressed = false;
	previousPressed = false;
    }

}
