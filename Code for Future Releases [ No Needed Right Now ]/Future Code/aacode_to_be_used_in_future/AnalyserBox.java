/*
 * 
 */
package aacode_to_be_used_in_future;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;

// TODO: Auto-generated Javadoc
/**
 * The Class AnalyserBox.
 */
public final class AnalyserBox extends Canvas {

	/** The gc. */
	GraphicsContext gc = getGraphicsContext2D();
	
	/**
	 * Χρησιμοποιώ αυτόν τον αριθμό για να φαίνονται η αρχή και το τέλος δηλαδή
	 * το 0 και το 10 και να μην αποκρύπτονται.
	 */
	protected int vStart;
	
	/** The marks shown. */
	private int marksShown; 
	
	/** The marks distance. */
	private int marksDistance = 0;
	
	/** The milli per pixel. */
	private int milliPerPixel = 0;
	
	/** The current ms. */
	private long currentMs = 0;
	
	/** The total ms. */
	protected long totalMs = 0;

	/** Σχετικά με την επανάληψη εντός κάποιων χρονικών ορίων. */
	private int replayFrom = 0;
	
	/** The replay to. */
	private int replayTo = 0;
	
	/** The can replay. */
	private boolean canReplay = true;
	
	/** The marks. */
	private int[] marks;

	/**
	 * Instantiates a new analyser box.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public AnalyserBox(int width, int height) {

		// Εμφανισιακά
		setTranslateX(50);
		setTranslateY(100);
		setWidth(width);
		setHeight(height);

		// ’κρως απαραίτητα
		vStart = (int) getWidth() / 2;
		marksShown=12;

		// Συνέχεια
		gc.setFont(Font.loadFont(getClass().getResourceAsStream("Young.ttf"), 14));
		setCursor(Cursor.HAND);
		// repaintCanvas();

		setOnMouseDragged(m -> {
			// int x = (int) m.getX();
			if (m.getButton() == MouseButton.PRIMARY) {
				/*
				 * if (x <= vStart) timeLinePosition = vStart; else if (x >
				 * vStart && x < getMaximumTimeLinePx()) timeLinePosition = x;
				 * else if (x > getMaximumTimeLinePx()) timeLinePosition =
				 * getMaximumTimeLinePx();
				 */

				repaintCanvas();
			} else if (m.getButton() == MouseButton.SECONDARY) {
				// if (x > vStart && x < getMaximumTimeLinePx())
				// replayTo = x - replayFrom;
				// else if (x > getMaximumTimeLinePx())
				// replayTo = getMaximumTimeLinePx();
				repaintCanvas();
			}
		});

		setOnMousePressed(m -> {
			if (m.getButton() == MouseButton.SECONDARY) {
				// int x = (int) m.getX();
				// if (x > vStart && x < getMaximumTimeLinePx())
				// replayFrom = x;
			}
		});

		// Test Thread here!!!
//		new Thread(() -> {
//			initBoxMech(0, 10000, 50);
//			int currentTimeInMs = 0;
//			// Τρέξε μέχρι τα 10 δευτερόλεπτα
//			for (int i = 0; i < 100000; i++) {
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				setCurrentMs(currentTimeInMs += 50, false);
//				Platform.runLater(() -> {
//					repaintCanvas();
//				});
//			}
//		});//.start();

	}

	/**
	 * Repaints the canvas of analyserBox.
	 */
	public void repaintCanvas() {

		// gc.clearRect(0, 0, 0, 0);

		// background
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, getWidth(), getHeight());

		// If Replay Is Enabled
		if (canReplay) {
			gc.setFill(Color.rgb(0, 255, 0, 0.7));
			gc.fillRect(replayFrom, 20, replayTo, getHeight());
		}

		// draw Info line
		gc.setFill(Color.WHITE);
		gc.fillText("Time:" + getCurrentMs() / 1000 + "." + getCurrentMs() % 1000 + "  sec", 5, 15);

		// draw markers line
		gc.setLineWidth(1);	
		// int increase = getMarksDistance();
		// for (int i = 0, mark = vStart; i < secondsShown; i++, mark +=
		// increase)
		// gc.strokeLine(mark, 23, mark + 1, getHeight());
		gc.setStroke(Color.RED);
		for (int i = 0; i < marks.length; i++) {
			gc.strokeLine(--marks[i], 23, marks[i] + 1.00, getHeight());
			gc.setStroke(Color.AQUA);
		}
	}

	/////////////////////////////////////////////////////////
	// TODO setters
	/**
	 * Από πία μέχρι πιά χρονική στιγμή θα γίνει η επανάληψη.
	 *
	 * @param from the from
	 * @param to the to
	 */
	protected void setReplayFromTo(int from, int to) {
		replayFrom = from;
		replayTo = to;
	}

	/**
	 * Φτιάχνει τον μηχανισμό.
	 *
	 * @param ms <br>
	 *        Η χρονική στιγμή που βρίσκετε το τραγούδι τώρα (σε
	 *        milliseconds)
	 * @param totalMillis <br>
	 *        Ο συνολικός χρόνος του κομματιού (σε milliseconds)
	 * @param msPerPixel the ms per pixel
	 */
	public void initBoxMech(long ms, long totalMillis, int msPerPixel) {

		// msPerPixel,CurrentMs,TotalMs
		currentMs = ms;
		totalMs = totalMillis;
		setMsPerPixel(msPerPixel);

		// the marks
		int half = (int) getWidth()/2;
		marks = new int[marksShown];
		marks[0]=half;
		for(int i=1; i<marks.length; i++)
			marks[i]=marks[i-1]+getMarksDistance();
	}

	/**
	 * Ανανεώνει την χρονική στιγμή που βρίσκετε το τραγούδι τώρα (σε
	 * milliseconds).
	 *
	 * @param ms the ms
	 * @param repaint the repaint
	 */
	public void setCurrentMs(long ms, boolean repaint) {
		currentMs = ms;
		if (repaint)
			repaintCanvas();
	}

	/**
	 * Θέτει πόσα milliseconds θα αντιστοιχούν σε κάθε pixel που υπάρχουν μεταξύ
	 * 1νος δευτερολέπτου(πχ 1-2 sec).
	 *
	 * @param ms the new ms per pixel
	 */
	private void setMsPerPixel(int ms) {
		milliPerPixel = ms;
		marksDistance = 1000 / milliPerPixel;
	}

	////////////////////////////////////////////////////////////
	// TODO getter

	/**
	 * Πόσα milliseconds αναπαριστά κάθε px μεταξύ δύο marks(πχ μεταξύ 1 - 2
	 * sec).
	 *
	 * @return the ms per pixel
	 */
	public int getMsPerPixel() {
		return milliPerPixel;
	}

	/**
	 * Είναι μιά τιμή μεταξύ 0 ms και secondsShown*1000 ms.
	 *
	 * @return Σε ποιό millisecond βρίσκομαι
	 */
	public long getCurrentMs() {
		return currentMs;
	}

	/**
	 * Η απόσταση σε pixel μεταξύ κάθε mark δηλαδή από δευτερόλεπτο σε
	 * δευτερόλεπτο (πχ από το 1 μέχρι το 2).
	 *
	 * @return the marks distance
	 */
	public int getMarksDistance() {
		return marksDistance;
	}
	
	

}
