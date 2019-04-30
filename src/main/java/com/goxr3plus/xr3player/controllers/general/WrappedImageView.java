package com.goxr3plus.xr3player.controllers.general;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class WrappedImageView extends ImageView {
	WrappedImageView() {
		setPreserveRatio(false);
	}

	@Override
	public double minWidth(double height) {
		return 40;
	}

	@Override
	public double prefWidth(double height) {
		Image I = getImage();
		if (I == null)
			return minWidth(height);
		return I.getWidth();
	}

	@Override
	public double maxWidth(double height) {
		return 16384;
	}

	@Override
	public double minHeight(double width) {
		return 40;
	}

	@Override
	public double prefHeight(double width) {
		Image I = getImage();
		if (I == null)
			return minHeight(width);
		return I.getHeight();
	}

	@Override
	public double maxHeight(double width) {
		return 16384;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void resize(double width, double height) {
		setFitWidth(width);
		setFitHeight(height);
	}
}
