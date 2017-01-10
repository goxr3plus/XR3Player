/*
 * 
 */
package xplayer.presenter;

import application.Main;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import radialmenu.RadialCheckMenuItem;
import radialmenu.RadialMenu;
import radialmenu.RadialMenuItem;
import tools.InfoTool;


/**
 * The Class XPlayerRadialMenu.
 */
public class XPlayerRadialMenu extends RadialMenu {
	
	/** The previous. */
	public RadialMenuItem previous;
	
	/** The next. */
	public RadialMenuItem next;
	
	/** The stop. */
	public RadialMenuItem stop;
	
	/** The resume or pause. */
	public RadialMenuItem resumeOrPause;
	
	/** The replay. */
	public RadialMenuItem replay;
	
	/** The mute. */
	public RadialCheckMenuItem mute;
	
	/** The play image view. */
	public final ImageView playImageView = InfoTool.getImageViewFromDocuments("play.png");
	
	/** The pause image view. */
	public final ImageView pauseImageView = InfoTool.getImageViewFromDocuments("pause.png");
	
	/** The next pressed. */
	private boolean nextPressed;
	
	/** The previous pressed. */
	private boolean previousPressed;
	
	/** The is showing. */
	private boolean isShowing;
	
	/** The size. */
	private int size = 360 / 6;
	
	/**
	 * Constructor.
	 *
	 * @param key the key
	 */
	public XPlayerRadialMenu(int key) {
		super(-32, 16, 60, 5, Color.rgb(0, 0, 0, 0.93), Color.rgb(0, 0, 0, 1), null, null, false,
		        CenterVisibility.ALWAYS, null);
		setCursor(Cursor.HAND);
		
		// PREVIOUS
		previous = new RadialMenuItem(size, "previous", InfoTool.getImageViewFromDocuments("previous.png"),
		        v -> goPrevious());
		
		// Play OR Pause
		resumeOrPause = new RadialMenuItem(size, "play/pause", playImageView,
		        v -> Main.xPlayersList.getXPlayerUI(key).reversePlayAndPause());
		
		// STOP
		stop = new RadialMenuItem(size, "stop", InfoTool.getImageViewFromDocuments("stop.png"),
		        v -> Main.xPlayersList.getXPlayer(key).stop());
		
		// NEXT
		next = new RadialMenuItem(size, "next", InfoTool.getImageViewFromDocuments("next.png"), v -> goNext());
		
		// Replay
		replay = new RadialMenuItem(size, "replay", InfoTool.getImageViewFromDocuments("replay.png"),
		        v -> Main.xPlayersList.getXPlayerUI(key).replaySong());
		
		// empty1
		mute = new RadialCheckMenuItem(size, InfoTool.getImageViewFromDocuments("unmute.png"), v -> {
		});
		mute.selectedProperty().addListener((observable , oldValue , newValue) -> {
			Main.xPlayersList.getXPlayer(key).setMute(mute.isSelected());
			mute.setGraphic(InfoTool.getImageViewFromDocuments(mute.isSelected() ? "mute.png" : "unmute.png"));
		});
		
		// FINNALLY
		addMenuItem(next);
		addMenuItem(resumeOrPause);
		addMenuItem(stop);
		addMenuItem(previous);
		addMenuItem(replay);
		addMenuItem(mute);
		
		hideRadialMenu();
		next.setDisable(true);
		previous.setDisable(true);
	}
	
	/**
	 * Checks if is showing.
	 *
	 * @return true, if is showing
	 */
	public boolean isShowing() {
		return isShowing;
	}
	
	/** The transparent. */
	Color transparent = Color.TRANSPARENT;
	
	/* (non-Javadoc)
	 * @see radialmenu.RadialMenu#hideRadialMenu() */
	@Override
	public void hideRadialMenu() {
		isShowing = false;
		super.hideRadialMenu();
		setBackgroundColor(transparent);
		setStrokeColor(transparent);
	}
	
	/* (non-Javadoc)
	 * @see radialmenu.RadialMenu#showRadialMenu() */
	@Override
	public void showRadialMenu() {
		isShowing = true;
		super.showRadialMenu();
		setBackgroundColor(Color.rgb(0, 0, 0, 0.93));
		setStrokeColor(Color.WHITE);
	}
	
	/**
	 * This method is used to go on next song.
	 */
	public void goNext() {
		if (!next.isDisable()) {
			nextPressed = true;
			previousPressed = false;
		}
	}
	
	/**
	 * This method is used to go on next song.
	 */
	public void goPrevious() {
		if (!previous.isDisable()) {
			nextPressed = false;
			previousPressed = true;
		}
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
