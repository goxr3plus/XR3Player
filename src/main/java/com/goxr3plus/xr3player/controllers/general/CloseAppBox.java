package main.java.com.goxr3plus.xr3player.controllers.general;

import java.io.IOException;

import org.kordamp.ikonli.javafx.StackedFontIcon;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.utils.general.ActionTool;
import main.java.com.goxr3plus.xr3player.utils.general.InfoTool;

public class CloseAppBox extends StackPane {

	// --------------------------------------------------------------

	@FXML
	private JFXButton donate;

	@FXML
	private JFXButton about;

	@FXML
	private MenuItem chooseBackground;

	@FXML
	private MenuItem resetBackground;

	@FXML
	private JFXButton restartButton;

	@FXML
	private JFXButton minimize;

	@FXML
	private JFXButton maxOrNormalize;

	@FXML
	private StackedFontIcon sizeStackedFontIcon;

	@FXML
	private JFXButton exitApplication;

	// -------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public CloseAppBox() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "CloseAppBox.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Called as soon as fxml is initialized
	 */
	@FXML
	private void initialize() {

		// restartButton
		restartButton.setOnAction(a -> {
			if (ActionTool.doQuestion("Restart", "Sure you want to restart the application?", restartButton,
					Main.window))
				Main.restartTheApplication(true);
		});

		// minimize
		minimize.setOnAction(ac -> Main.window.setIconified(true));

		// maximize_normalize
		maxOrNormalize.setOnAction(ac -> Main.borderlessScene.maximizeStage());

		// close
		exitApplication.setOnAction(ac -> Main.confirmApplicationExit());

		// donate
		donate.setOnAction(a -> ActionTool.openWebSite("https://www.paypal.me/GOXR3PLUSCOMPANY"));

		// about
		about.setOnAction(a -> Main.aboutWindow.show());

		// chooseBackground
		chooseBackground.setOnAction(a -> Main.changeBackgroundImage());

		// resetBackground
		resetBackground.setOnAction(a -> Main.resetBackgroundImage());

		// sizeStackedFontIcon
		Main.borderlessScene.maximizedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				sizeStackedFontIcon.getChildren().get(0).setVisible(true);
				sizeStackedFontIcon.getChildren().get(1).setVisible(false);
			} else {
				sizeStackedFontIcon.getChildren().get(1).setVisible(true);
				sizeStackedFontIcon.getChildren().get(0).setVisible(false);
			}
		});

	}

}
