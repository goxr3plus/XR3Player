package com.goxr3plus.xr3player.controllers.settings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.custom.Marquee;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.javafx.JavaFXTool;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * @author GOXR3PLUS
 *
 */
public class GeneralSettingsController extends BorderPane {

	/** -----------------------------------------------------. */

	@FXML
	private ToggleGroup sideBarPositionGroup;

	@FXML
	private ToggleGroup libraryModeUpsideDown;

	@FXML
	private ToggleGroup djModeUpsideDown;

	@FXML
	private JFXCheckBox animationsEnabled;

	@FXML
	private ToggleGroup notificationsPosition;

	@FXML
	private JFXToggleButton highGraphicsToggle;

	// -------------------------------------------------------------

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	public static Pos notificationPosition = Pos.BOTTOM_LEFT;

	/**
	 * Constructor.
	 */
	public GeneralSettingsController() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.SETTINGS_FXMLS + "GeneralSettingsController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * Called as soon as .fxml is initialized
	 */
	@FXML
	private void initialize() {

		// sideBarSideGroup
		sideBarPositionGroup.selectedToggleProperty().addListener(listener -> {

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("General-SideBarSide",
					Integer.toString(JavaFXTool.getIndexOfSelectedToggle(sideBarPositionGroup)));

			// Fix the side bar position
			Main.sideBar.changeSide(
					JavaFXTool.getIndexOfSelectedToggle(sideBarPositionGroup) == 0 ? NodeOrientation.LEFT_TO_RIGHT
							: NodeOrientation.RIGHT_TO_LEFT);
		});

//		//libraryModeUpsideDown
//		libraryModeUpsideDown.selectedToggleProperty().addListener(listener -> {
//			
//			//Update the properties file
//			Main.dbManager.getPropertiesDb().updateProperty("General-LibraryModeUpsideDown", Integer.toString(JavaFXTools.getIndexOfSelectedToggle(libraryModeUpsideDown)));
//			
//			//Turn Library Mode Upside Down or The Opposite
//			Main.libraryMode.turnUpsideDownSplitPane(JavaFXTools.getIndexOfSelectedToggle(libraryModeUpsideDown) == 0);
//			
//		});

		/*
		 * //djModeUpsideDown
		 * djModeUpsideDown.selectedToggleProperty().addListener(listener -> {
		 * 
		 * //Update the properties file
		 * Main.dbManager.getPropertiesDb().updateProperty("General-DjModeUpsideDown",
		 * Integer.toString(JavaFXTools.getIndexOfSelectedToggle(djModeUpsideDown)));
		 * 
		 * //Turn Library Mode Upside Down or The Opposite
		 * Main.djMode.turnUpsideDownSplitPane(JavaFXTools.getIndexOfSelectedToggle(
		 * djModeUpsideDown) != 0);
		 * 
		 * });
		 */

		// notificationsPosition
		notificationsPosition.selectedToggleProperty().addListener(listener -> {

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("General-NotificationsPosition",
					((Labeled) notificationsPosition.getSelectedToggle()).getText());

			// Turn Library Mode Upside Down or The Opposite
			determineNotificationBarPosition();

			// Show a small notification to user
			// ActionTool.showNotification("Notification Position", "Hi!!",
			// Duration.seconds(1), NotificationType.SIMPLE)

		});

		// highGraphicsToggle
		highGraphicsToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
			boolean value = newValue;
			// System.out.println("High graphics is :"+value)

			// Update the properties file
			Main.dbManager.getPropertiesDb().updateProperty("General-High-Graphics", String.valueOf(value));

			// Disable all Marquee from moving
			Main.xPlayersList.getList().forEach(controller -> {

				// Marquee
				controller.mediaFileMarquee.checkAnimationValidity(value);

				// getVisualizationsDisabledLabel
				controller.getVisualizationsDisabledLabel().setVisible(!value);

			});

			// For each Library
			Main.libraryMode.openedLibrariesViewer.getTabs().forEach(
					tab -> ((Marquee) ((HBox) tab.getGraphic()).getChildren().get(3)).checkAnimationValidity(value));

			// For the Web Browser
//			Main.webBrowser.setMovingTitlesEnabled(value);

			Main.topBar.getHighGraphics().setVisible(value);
			Main.topBar.getHighGraphics().setManaged(value);
		});

	}

	/**
	 * Determines the NotificationBarPosition based on the selected toggle text
	 */
	private void determineNotificationBarPosition() {
		switch (((Labeled) notificationsPosition.getSelectedToggle()).getText()) {

		case "TOP_LEFT":
			notificationPosition = Pos.TOP_LEFT;
			break;
		case "TOP_CENTER":
			notificationPosition = Pos.TOP_CENTER;
			break;
		case "TOP_RIGHT":
			notificationPosition = Pos.TOP_RIGHT;
			break;
		case "CENTER_LEFT":
			notificationPosition = Pos.CENTER_LEFT;
			break;
		case "CENTER":
			notificationPosition = Pos.CENTER;
			break;
		case "CENTER_RIGHT":
			notificationPosition = Pos.CENTER_RIGHT;
			break;
		case "BOTTOM_LEFT":
			notificationPosition = Pos.BOTTOM_LEFT;
			break;
		case "BOTTOM_CENTER":
			notificationPosition = Pos.BOTTOM_CENTER;
			break;
		case "BOTTOM_RIGHT":
			notificationPosition = Pos.BOTTOM_RIGHT;
			break;
		}
	}

	/**
	 * Restores all the settings that have to do with the category of the class
	 */
	public void restoreSettings() {

		// highGraphicsToggle
		highGraphicsToggle.setSelected(false);

		// notificationsPosition
		JavaFXTool.selectToogleWithText(notificationsPosition, "BOTTOM_LEFT");

		// sideBarSideGroup
		JavaFXTool.selectToggleOnIndex(sideBarPositionGroup, 0);

		// libraryModeUpsideDown
		JavaFXTool.selectToggleOnIndex(libraryModeUpsideDown, 0);

		// djModeUpsideDown
		JavaFXTool.selectToggleOnIndex(djModeUpsideDown, 0);

	}

	/**
	 * @return the sideBarSideGroup
	 */
	public ToggleGroup getSideBarSideGroup() {
		return sideBarPositionGroup;
	}

	/**
	 * @return the libraryModeUpsideDown
	 */
	public ToggleGroup getLibraryModeUpsideDown() {
		return libraryModeUpsideDown;
	}

	/**
	 * @return the djModeUpsideDown
	 */
	public ToggleGroup getDjModeUpsideDown() {
		return djModeUpsideDown;
	}

	/**
	 * @return the notificationsPosition
	 */
	public ToggleGroup getNotificationsPosition() {
		return notificationsPosition;
	}

	/**
	 * @return the highGraphicsToggle
	 */
	public JFXToggleButton getHighGraphicsToggle() {
		return highGraphicsToggle;
	}

}
