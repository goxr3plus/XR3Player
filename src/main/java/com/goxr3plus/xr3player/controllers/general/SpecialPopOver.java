package com.goxr3plus.xr3player.controllers.general;

import org.controlsfx.control.PopOver;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.util.Duration;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;

public class SpecialPopOver extends PopOver {

	/**
	 * Fixes the position of the PopOver so it doesn't appear on an annoying
	 * position for the user
	 * 
	 * @param node
	 */
	public void showPopOver(Node node) {
		// System.out.println(popOver.getWidth() + " , " + popOver.getHeight());
		if (this.getWidth() == 0) {
			this.show(node);

		}
		// System.out.println(popOver.getWidth() + " , " + popOver.getHeight());

		// Get Width and Height
		int popOverWidth = (int) this.getWidth();
		int popOverHeight = (int) this.getHeight();

		// Find the correct arrow location
		Bounds bounds = node.localToScreen(node.getBoundsInLocal());
		boolean fitOnTop = bounds.getMinY() - popOverHeight > 0; // top?
		boolean fitOnLeft = bounds.getMinX() - popOverWidth > 0; // left?
		boolean fitOnRight = bounds.getMaxX() + popOverWidth < JavaFXTool.getVisualScreenWidth();// right?
		boolean fitOnBottom = bounds.getMaxY() + popOverHeight < JavaFXTool.getVisualScreenHeight(); // bottom?

		if (fitOnTop)
			this.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		else if (fitOnBottom)
			this.setArrowLocation(ArrowLocation.TOP_CENTER);
		else if (fitOnLeft)
			this.setArrowLocation(ArrowLocation.RIGHT_CENTER);
		else if (fitOnRight)
			this.setArrowLocation(ArrowLocation.LEFT_CENTER);

		this.hide(Duration.ZERO);
		this.show(node);
	}

}
