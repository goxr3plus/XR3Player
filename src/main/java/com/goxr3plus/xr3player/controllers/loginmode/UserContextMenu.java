package com.goxr3plus.xr3player.controllers.loginmode;

import java.io.IOException;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import javafx.util.Duration;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.application.MainLoadUser;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * This is the Context Menu for every Library in the LibraryMode
 * 
 * @author GOXR3PLUS
 *
 */
public class UserContextMenu extends ContextMenu {

	// -------------------------------------------------------------

	@FXML
	private MenuItem login;

	@FXML
	private MenuItem rename;

	@FXML
	private MenuItem delete;

	@FXML
	private MenuItem changeImage;

	@FXML
	private MenuItem resetImage;

	@FXML
	private MenuItem exportImage;

	@FXML
	private MenuItem moreInfo;

	// -------------------------------------------------------------

	private User user;

	/**
	 * Instantiates a new library context menu.
	 */
	// Constructor
	public UserContextMenu() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.USER_FXMLS + "UserContextMenu.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {

		login.setOnAction(a -> MainLoadUser.startAppWithUser(user));

		rename.setOnAction(ac -> user.renameUser(user));

		changeImage.setOnAction(ac -> user.changeUserImage());

		resetImage.setOnAction(ac -> user.setDefaultImage());

		exportImage.setOnAction(a -> user.exportImage());

		delete.setOnAction(ac -> ((User) Main.loginMode.viewer.getSelectedItem()).deleteUser(user));

		moreInfo.setOnAction(a -> user.displayInformation());

	}

	/**
	 * Shows the LibraryContextMenu.
	 *
	 * @param window the window
	 * @param x      the x
	 * @param y      the y
	 * @param user   the user
	 */
	public void show(Window window, double x, double y, User user) {
		this.user = user;

		// customize the menu accordingly
		exportImage.setDisable(user.getAbsoluteImagePath() == null);
		resetImage.setDisable(exportImage.isDisable());

		// Fix first time show problem
		if (super.getWidth() == 0) {
			show(Main.window);
			hide();
		}

		// Show it
		show(window, x - 15 - super.getWidth() + super.getWidth() * 14 / 100, y - 1);

		// Y axis
		double yIni = y - 50;
		double yEnd = super.getY();
		super.setY(yIni);
		final DoubleProperty yProperty = new SimpleDoubleProperty(yIni);
		yProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// X axis
		// double xIni = screenX - super.getWidth() + super.getWidth() * 14 / 100 + 30;
		// double xEnd = screenX - super.getWidth() + super.getWidth() * 14 / 100;
		// super.setX(xIni);
		// final DoubleProperty xProperty = new SimpleDoubleProperty(xIni);
		// xProperty.addListener((ob, n, n1) -> super.setY(n1.doubleValue()));

		// Timeline
		Timeline timeIn = new Timeline();
		timeIn.getKeyFrames()
				.addAll(new KeyFrame(Duration.seconds(0.35), new KeyValue(yProperty, yEnd, Interpolator.EASE_BOTH)));
		// new KeyFrame(Duration.seconds(0.5), new KeyValue(xProperty, xEnd,
		// Interpolator.EASE_BOTH)))
		timeIn.play();

	}

}
