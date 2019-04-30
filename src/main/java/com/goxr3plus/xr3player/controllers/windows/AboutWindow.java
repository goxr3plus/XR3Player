/**
 * 
 */
package com.goxr3plus.xr3player.controllers.windows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.NetworkingTool;
import com.goxr3plus.xr3player.utils.io.IOInfo;

/**
 * @author GOXR3PLUS
 *
 */
public class AboutWindow extends BorderPane {

	// ---------------------------------------------

	@FXML
	private Label topLabel;

	@FXML
	private VBox centerVBox;

	@FXML
	private InlineCssTextArea cssTextArea;

	@FXML
	private JFXButton visitGithub;

	@FXML
	private JFXButton visitWebsite;

	@FXML
	private JFXButton reportBug;

	@FXML
	private JFXButton close;

	// -------------------------------------------------------------
	/** The logger. */
	private Logger logger = Logger.getLogger(getClass().getName());

	/** Window **/
	private Stage window = new Stage();

	/**
	 * Constructor
	 */
	public AboutWindow() {

		// ------------------------------------FXMLLOADER--------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.WINDOW_FXMLS + "AboutWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);

		}

		// --window
		window.setTitle("About-Report Bug");
		window.initStyle(StageStyle.UTILITY);
		window.getScene().setOnKeyReleased(k -> {
			if (k.getCode() == KeyCode.ESCAPE)
				window.close();
		});

	}

	@FXML
	private void initialize() {

		// Scene
		window.setScene(new Scene(this));
		window.getScene().getStylesheets()
				.add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());

		// InlineCssTextArea
		VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(cssTextArea);
		vsPane.setMinSize(300, 400);
		vsPane.setMaxWidth(Double.MAX_VALUE);
		vsPane.setMaxHeight(Double.MAX_VALUE);

		centerVBox.getChildren().remove(cssTextArea);
		centerVBox.getChildren().add(0, vsPane);

		// --Style
		String style = "-fx-font-weight:bold; -fx-font-size:14; -fx-fill:white;";

		// Information - Copyright
		String text = "A cross platform Java/JavaFX Media Player\n";
		cssTextArea.appendText(text);

		text = "Copyright (C) <2015-2350>  (www.goxr3plus.co.nf) . All rights reserved.\n\n";
		cssTextArea.appendText(text);
		cssTextArea.setStyle(0, cssTextArea.getLength() - 1, style);

		// Author-Version-Release Date-Home Page
		text = "Author :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style);
		cssTextArea.appendText("GOXR3Plus Studio\n");

		text = "Version :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style);
		cssTextArea.appendText(Main.APPLICATION_VERSION + "\n");

		text = "Release Date :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style);
		cssTextArea.appendText(Main.RELEASE_DATE + "\n");

		text = "Home Page :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style);
		cssTextArea.appendText("https://sourceforge.net/projects/xr3player\n\n");

		String style2 = style.replace("white", "green");
		// Java Version - Java Vendor - Java Home
		text = "Java Version :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style2);
		cssTextArea.appendText(System.getProperty("java.version") + "\n");

		text = "Java Vendor :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style2);
		cssTextArea.appendText(System.getProperty("java.vendor") + "\n");

		text = "Java Home :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style2);
		cssTextArea.appendText(System.getProperty("java.home") + "\n\n");

		String style3 = style.replace("white", "orange");
		// OS Name - Os Architecture - Os Version
		text = "Os Name :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style3);
		cssTextArea.appendText(System.getProperty("os.name") + "\n");

		text = "Os Arch :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style3);
		cssTextArea.appendText(System.getProperty("os.arch") + "\n");

		text = "Os Version :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style3);
		cssTextArea.appendText(System.getProperty("os.version") + "\n\n");

		String style4 = style.replace("white", "firebrick");
		// User Name - User Home - User directory
		text = "User Name :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style4);
		cssTextArea.appendText(System.getProperty("user.name") + "\n");

		text = "User Home :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style4);
		cssTextArea.appendText(System.getProperty("user.home") + "\n");

		text = "User Dir :\t";
		cssTextArea.appendText(text);
		// cssTextArea.setStyle(cssTextArea.getLength() - text.length(),
		// cssTextArea.getLength() - 1, style4);
		cssTextArea.appendText(IOInfo.getBasePathForClass(Main.class) + "\n");

		cssTextArea.setStyle(0, cssTextArea.getLength() - 1, style);

		// --close
		close.setOnAction(a -> window.close());

		// --visitWebsite
		visitWebsite.setOnAction(a -> NetworkingTool.openWebSite(InfoTool.WEBSITE_URL));

		// --visitGithub
		visitGithub.setOnAction(a -> NetworkingTool.openWebSite(InfoTool.GITHUB_URL));

		// --reportBug
		reportBug.setOnAction(a -> NetworkingTool.openWebSite("https://github.com/goxr3plus/XR3Player/issues"));

	}

	/**
	 * Shows the Window
	 */
	public void show() {
		window.sizeToScene();
		window.show();
	}

	/**
	 * Close the Window
	 */
	public void close() {
		window.close();
	}

	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}

}
