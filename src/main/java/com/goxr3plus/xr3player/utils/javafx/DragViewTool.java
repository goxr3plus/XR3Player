package com.goxr3plus.xr3player.utils.javafx;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.paint.Color;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.models.smartcontroller.Media;

public final class DragViewTool {

	private DragViewTool() {
	}

	/**
	 * This method is used for the drag view of Media
	 * 
	 * @param dragBoard
	 * @param media
	 */
	public static void setDragView(final Dragboard dragBoard, final Media media) {
		final SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		dragBoard.setDragView(Main.dragViewer.updateMedia(media).snapshot(params, new WritableImage(150, 150)), 50, 0);
	}

	/**
	 * This view is used for plain text drag view
	 * 
	 * @param dragBoard
	 * @param title
	 */
	public static void setPlainTextDragView(final Dragboard dragBoard, final String title) {
		final SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		dragBoard.setDragView(Main.dragViewer.updateDropboxMedia(title).snapshot(params, new WritableImage(150, 150)),
				50, 0);
	}

}
