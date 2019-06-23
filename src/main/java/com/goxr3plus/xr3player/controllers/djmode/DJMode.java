/*
 * 
 */
package com.goxr3plus.xr3player.controllers.djmode;

import java.io.IOException;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.controllers.xplayer.MixTabInterface;
import com.goxr3plus.xr3player.controllers.xplayer.XPlayerController;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.jfoenix.controls.JFXTabPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * The DJMode.
 *
 * @author GOXR3PLUS
 */
public class DJMode extends BorderPane {

	// -------------------------------------------------

	@FXML
	private HBox hBox;

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private Tab mixerTab;

	@FXML
	private Tab foldersModeTab;

	// --------------------------------------------------------------

	public final XPlayerController xPlayer0 = new XPlayerController(0);
	public final XPlayerController xPlayer1 = new XPlayerController(1);
	public final XPlayerController xPlayer2 = new XPlayerController(2);

	private final MixTabInterface mixTabInterface = new MixTabInterface();

	/**
	 * Constructor.
	 */
	public DJMode() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "DJMode.fxml"));
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
	public void initialize() {

		// XPlayer 0
		Main.xPlayersList.addXPlayerController(xPlayer0);
		xPlayer0.makeTheDisc(Color.rgb(255, 95, 0), 50, 0, 101, Side.LEFT);
		xPlayer0.makeTheVisualizer();

		// XPlayer 1
		Main.xPlayersList.addXPlayerController(xPlayer1);
		xPlayer1.makeTheDisc(Color.rgb(0, 144, 255), 50, 0, 101, Side.RIGHT);
		xPlayer1.makeTheVisualizer();
		hBox.getChildren().add(0, xPlayer1);
		HBox.setHgrow(xPlayer1, Priority.ALWAYS);

		// XPlayer 2
		Main.xPlayersList.addXPlayerController(xPlayer2);
		xPlayer2.makeTheDisc(Color.web("#fc4f4f"), 50, 0, 101, Side.LEFT);
		xPlayer2.makeTheVisualizer();
		hBox.getChildren().add(2, xPlayer2);
		HBox.setHgrow(xPlayer2, Priority.ALWAYS);

		// Mixer Tab
		mixerTab.setContent(mixTabInterface);

		// --------------------------mixTabInterface--------------------

		// --Volume Bar Box
		xPlayer1.getRootBorderPane().getChildren().remove(xPlayer1.getVolumeBarBox());
		mixTabInterface.getBorderPane().setLeft(xPlayer1.getVolumeBarBox());

		// --Dj Visualizer
		mixTabInterface.getCenterHBox().getChildren().add(xPlayer1.getDjVisualizer());
		HBox.setHgrow(xPlayer1.getDjVisualizer(), Priority.ALWAYS);

		// --Volume Bar Box
		xPlayer2.getRootBorderPane().getChildren().remove(xPlayer2.getVolumeBarBox());
		mixTabInterface.getBorderPane().setRight(xPlayer2.getVolumeBarBox());

		// --Dj Visualizer
		mixTabInterface.getCenterHBox().getChildren().add(xPlayer2.getDjVisualizer());
		HBox.setHgrow(xPlayer2.getDjVisualizer(), Priority.ALWAYS);

		// Add it to library mode
		Main.libraryMode.getDjModeStackPane().getChildren().add(hBox);
	}

	/**
	 * Adds the appropriate key listeners to the Parent.
	 */
	@Deprecated
	private void addKeyListeners() {

		// // Key Pressed Events
		// setOnKeyPressed(key -> {
		//
		// KeyCode keyCode = key.getCode();
		//
		// // -->Xplayer_1||XPlayer_2 Volume++
		// if (keyCode == KeyCode.W) {
		//
		// if (key.isShiftDown()) {
		// if (Main.xPlayersList.getXPlayerController(1).getVolume() < 101) {
		// Main.xPlayersList.getXPlayerController(1).setVolume(Main.xPlayersList.getXPlayerController(1).getVolume()
		// + 1);
		// }
		// } else if (key.isControlDown()) {
		// if (Main.xPlayersList.getXPlayerController(2).getVolume() < 101) {
		// Main.xPlayersList.getXPlayerController(2).setVolume(Main.xPlayersList.getXPlayerController(2).getVolume()
		// + 1);
		// }
		// }
		//
		// // -->Xplayer_1||XPlayer_2 Volume--
		// } else if (keyCode == KeyCode.Q) {
		//
		// if (key.isShiftDown()) {
		// if (Main.xPlayersList.getXPlayerController(1).getVolume() > -1) {
		// Main.xPlayersList.getXPlayerController(1).setVolume(Main.xPlayersList.getXPlayerController(1).getVolume()
		// - 1);
		// }
		// } else if (key.isControlDown()) {
		// if (Main.xPlayersList.getXPlayerController(2).getVolume() > -1) {
		// Main.xPlayersList.getXPlayerController(2).setVolume(Main.xPlayersList.getXPlayerController(2).getVolume()
		// - 1);
		// }
		// }
		// }
		//
		// });
		//
		// setOnKeyReleased(key -> {
		// KeyCode keyCode = key.getCode();
		//
		// // Xplayer_1||Xplayer2.Resume
		// if (keyCode == KeyCode.DIGIT1) {
		//
		// if (key.isShiftDown())
		// Main.xPlayersList.getXPlayer(1).resume();
		// else if (key.isControlDown())
		// Main.xPlayersList.getXPlayer(2).resume();
		//
		// // Xplayer_1||Xplayer_2.Pause
		// } else if (keyCode == KeyCode.DIGIT2) {
		//
		// if (key.isShiftDown())
		// Main.xPlayersList.getXPlayer(1).pause();
		// else if (key.isControlDown())
		// Main.xPlayersList.getXPlayer(2).pause();
		//
		// // Xplayer_1||Xplayer_2.Stop
		// } else if (keyCode == KeyCode.DIGIT3) {
		//
		// if (key.isShiftDown()) {
		// if (Main.xPlayersList.getXPlayer(1).isPausedOrPlaying())
		// Main.xPlayersList.getXPlayer(1).stop();
		// } else if (key.isControlDown() &&
		// Main.xPlayersList.getXPlayer(2).isPausedOrPlaying())
		// Main.xPlayersList.getXPlayer(2).stop();
		//
		// // DJBeats
		// }
		//
		// });

	}

	/**
	 * @return the mixTabInterface
	 */
	public MixTabInterface getMixTabInterface() {
		return mixTabInterface;
	}

	// Variables
	private double[] topSplitPaneDivider = { 0.45, 0.55 };

	// Variables
	private double[] bottomSplitPaneDivider = { 0.18, 0.83 };

	// /**
	// * Updates the values of array that holds DividerPositions of splitPane
	// */
	// public void updateTopSplitPaneDividerArray(double[] array) {
	// topSplitPaneDivider[0] = array[0];
	// topSplitPaneDivider[1] = array[1];
	// }
	//
	// /**
	// * Updates the values of array that holds DividerPositions of splitPane
	// */
	// public void updateBottomSplitPaneDividerArray(double[] array) {
	// bottomSplitPaneDivider[0] = array[0];
	// bottomSplitPaneDivider[1] = array[1];
	// }

	// ----------------------------

	/*	*//**
			 * Updates the SplitPane DividerPositions based on the saved array
			 */
	/*
	 * public void updateTopSplitPaneDivider() {
	 * topSplitPane.setDividerPositions(topSplitPaneDivider); }
	 *//**
		 * Updates the SplitPane DividerPositions based on the saved array
		 */
	/*
	 * public void updateBottomSplitPaneDivider() {
	 * bottomSplitPane.setDividerPositions(bottomSplitPaneDivider); }
	 * //----------------------------
	 *//**
		 * Saves current divider positions of SplitPane into an array
		 */
	/*
	 * public void saveTopSplitPaneDivider() { topSplitPaneDivider =
	 * topSplitPane.getDividerPositions(); }
	 *//**
		 * Saves current divider positions of SplitPane into an array
		 */
	/*
	 * public void saveBottomSplitPaneDivider() { bottomSplitPaneDivider =
	 * bottomSplitPane.getDividerPositions(); }
	 *//**
		 * Turns the Library Mode Upside Down or opposite
		 * 
		 * @param turnDown
		 */
	/*
	 * public void turnUpsideDownSplitPane(boolean turnDown) { //Check if it can
	 * enter based on the top hBox position if ( ( turnDown &&
	 * !topSplitPane.getItems().get(0).equals(hBox) ) || ( !turnDown &&
	 * topSplitPane.getItems().get(0).equals(hBox) )) return;
	 * //this.saveTopSplitPaneDivider() double temp = topSplitPaneDivider[0];
	 * topSplitPaneDivider[0] = topSplitPaneDivider[1]; topSplitPaneDivider[1] =
	 * temp; boolean libraryIsOnTop = topSplitPane.getItems().get(0).equals(hBox);
	 * topSplitPane.getItems().clear(); if (libraryIsOnTop)
	 * topSplitPane.getItems().addAll(bottomSplitPane, hBox); else
	 * topSplitPane.getItems().addAll(hBox, bottomSplitPane); //Fix layout problems
	 * SplitPane.setResizableWithParent(bottomSplitPane, Boolean.FALSE);
	 * SplitPane.setResizableWithParent(hBox, Boolean.FALSE);
	 * this.updateTopSplitPaneDivider(); } //----------------------------
	 *//**
		 * @return the topSplitPane
		 */
	/*
	 * public SplitPane getTopSplitPane() { return topSplitPane; }
	 *//**
		 * @return the bottomSplitPane
		 *//*
			 * public SplitPane getBottomSplitPane() { return bottomSplitPane; }
			 */

}
