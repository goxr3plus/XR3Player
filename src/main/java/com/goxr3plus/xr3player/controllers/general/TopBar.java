/*
 * 
 */
package com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;

/**
 * The Top bar of the application Window.
 *
 * @author GOXR3PLUS
 */
public class TopBar extends BorderPane {

	// ----------------------------------------------

	@FXML
	private Label xr3Label;

	@FXML
	private FontIcon highGraphics;

	@FXML
	private JFXButton showHideSideBar;

	@FXML
	private TextField searchField;

	// ----------------------------------------------

	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());

	private SimpleObjectProperty<WindowMode> windowModeProperty = new SimpleObjectProperty<>(WindowMode.MAINMODE);

	/**
	 * WindowMode.
	 *
	 * @author GOXR3PLUS
	 */
	public enum WindowMode {

		/**
		 * The Window is on LibraryMode
		 */
		MAINMODE,

		/**
		 * The window is on DJMode
		 */
		DJMODE,

		/**
		 * The window is on user settings mode
		 */
		USERMODE,

		/**
		 * The window is on web browser mode
		 */
		WEBMODE,

		/**
		 * The window is on movie mode
		 */
		MOVIEMODE;

	}

	/**
	 * Constructor.
	 */
	public TopBar() {

		// ---------------------FXML LOADER---------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "TopBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
		}
	}

	/**
	 * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {

		// Root
		this.setRight(new CloseAppBox());

		// Window Mode Property
		windowModeProperty.addListener((observable, oldValue, newValue) -> {

			// if (newValue == null)
			// return;

			// Hide all
			Main.libraryMode.setVisible(false);
			Main.userInfoMode.setVisible(false);
			Main.rootStackPane.getChildren().remove(Main.webBrowser);
			Main.movieModeController.setVisible(false);

			// Now decide which one to show
			if (newValue == WindowMode.MAINMODE) {
				Main.libraryMode.setVisible(true);

			} else if (newValue == WindowMode.DJMODE) {
				Main.libraryMode.setVisible(true);

			} else if (newValue == WindowMode.MOVIEMODE) {

				Main.movieModeController.setVisible(true);

			} else if (newValue == WindowMode.USERMODE) {
				Main.userInfoMode.setVisible(true);

			} else if (newValue == WindowMode.WEBMODE) {
				Main.rootStackPane.getChildren().add(Main.webBrowser);

			}

			// Extra config
			if (newValue == WindowMode.MOVIEMODE || newValue == WindowMode.USERMODE || newValue == WindowMode.WEBMODE) {

				// Hide the searchBox that is coming from LibraryMode
				Main.libraryMode.librariesSearcher.getSearchBoxWindow().close();
			}
		});

		// showHideSideBar
		showHideSideBar.setOnAction(a -> Main.bottomBar.getShowHideSideBar().setSelected(!Main.bottomBar.getShowHideSideBar().isSelected()));
	}

	/**
	 * Go to that Mode
	 * 
	 * @param mode
	 */
	public void goMode(WindowMode mode) {
		windowModeProperty.set(mode);
	}

	/**
	 * Add the binding to the xr3Label
	 */
	public void addXR3LabelBinding() {

		// XR3Label
		xr3Label.setText("XR3Player V." + Main.APPLICATION_VERSION);

		// xr3Label.textProperty().bind(Bindings.createStringBinding(() ->
		// MessageFormat.format(">-XR3Player (BETA) V.{0} -< Width=[{1}],Height=[{2}]",
		// Main.internalInformation.get("Version"), Main.window.getWidth(),
		// Main.window.getHeight()), Main.window.widthProperty(),
		// Main.window.heightProperty()));

	}

	/**
	 * @return the xr3Label
	 */
	public Label getXr3Label() {
		return xr3Label;
	}

	/**
	 * @param xr3Label the xr3Label to set
	 */
	public void setXr3Label(Label xr3Label) {
		this.xr3Label = xr3Label;
	}

	/**
	 * @return the highSpeed
	 */
	public FontIcon getHighGraphics() {
		return highGraphics;
	}

	/**
	 * @return the searchField
	 */
	public TextField getSearchField() {
		return searchField;
	}

	public SimpleObjectProperty<WindowMode> getWindowModeProperty() {
		return windowModeProperty;
	}

	public WindowMode getWindowMode() {
		return getWindowModeProperty().get();
	}

}
