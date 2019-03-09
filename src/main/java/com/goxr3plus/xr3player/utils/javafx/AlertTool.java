package main.java.com.goxr3plus.xr3player.utils.javafx;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3player.enums.NotificationType;
import main.java.com.goxr3plus.xr3player.controllers.settings.GeneralSettingsController;

public final class AlertTool {

	private AlertTool() {}

	/**
	 * Makes a question to the user.
	 *
	 * @param text the text
	 * @param node The node owner of the Alert
	 * @return true, if successful
	 */
	public static boolean doQuestion(String headerText, String text, Node node, Stage window) {
		boolean[] questionAnswer = { false };
	
		// Show Alert
		Alert alert = JavaFXTool.createAlert(null, headerText, text, AlertType.CONFIRMATION, StageStyle.UTILITY,
				window, JavaFXTool.getFontIcon("fas-question-circle", Color.WHITE, 24));
	
		// Make sure that JavaFX doesn't cut the text with ...
		alert.getDialogPane().getChildren().stream().filter(item -> node instanceof Label)
				.forEach(item -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
	
		if (node != null) {
			// I noticed that height property is notified after width property
			// that's why i choose to add the listener here
			alert.heightProperty().addListener(l -> {
	
				// Width and Height of the Alert
				int alertWidth = (int) alert.getWidth();
				int alertHeight = (int) alert.getHeight();
	
				// Here it prints 0!!
				// System.out.println("Alert Width: " + alertWidth + " , Alert Height: " +
				// alertHeight)
	
				// Find the bounds of the node
				Bounds bounds = node.localToScreen(node.getBoundsInLocal());
				int x = (int) (bounds.getMinX() + bounds.getWidth() / 2 - alertWidth / 2);
				int y = (int) (bounds.getMinY() + bounds.getHeight() / 2 - alertHeight / 2);
	
				// Check if Alert goes out of the Screen on X Axis
				if (x + alertWidth > JavaFXTool.getVisualScreenWidth())
					x = (int) (JavaFXTool.getVisualScreenWidth() - alertWidth);
				else if (x < 0)
					x = 0;
	
				// Check if Alert goes out of the Screen on Y AXIS
				if (y + alertHeight > JavaFXTool.getVisualScreenHeight())
					y = (int) (JavaFXTool.getVisualScreenHeight() - alertHeight);
				else if (y < 0)
					y = 0;
	
				// Set the X and Y of the Alert
				alert.setX(x);
				alert.setY(y);
			});
		}
	
		// Show the Alert
		alert.showAndWait().ifPresent(answer -> questionAnswer[0] = (answer == ButtonType.OK));
	
		return questionAnswer[0];
	}

	/**
	 * Just a helper method for showNotification methods
	 */
	public static void showNotification2(String title, String text, Duration duration,
			NotificationType notificationType, Node graphic) {
		Notifications notification1;
	
		// Set graphic
		if (graphic == null)
			notification1 = Notifications.create().title(title).text(text).hideAfter(duration).darkStyle()
					.position(GeneralSettingsController.notificationPosition);
		else
			notification1 = Notifications.create().title(title).text(text).hideAfter(duration).darkStyle()
					.position(GeneralSettingsController.notificationPosition).graphic(graphic);
	
		// Show the notification
		switch (notificationType) {
		case CONFIRM:
			notification1.graphic(JavaFXTool.getFontIcon("fas-question-circle", Color.web("#ad14e2"), 32)).show();
			break;
		case ERROR:
			notification1.graphic(JavaFXTool.getFontIcon("fas-times", Color.web("#f83e3e"), 32)).show();
			break;
		case INFORMATION:
			notification1.graphic(JavaFXTool.getFontIcon("fas-info-circle", Color.web("#1496e5"), 32)).show();
			break;
		case SIMPLE:
			notification1.show();
			break;
		case WARNING:
			notification1.graphic(JavaFXTool.getFontIcon("fa-warning", Color.web("#d74418"), 32)).show();
			break;
		case SUCCESS:
			notification1.graphic(JavaFXTool.getFontIcon("fas-check", Color.web("#64ff41"), 32)).show();
			break;
		default:
			break;
		}
	}

	/**
	 * Show a notification.
	 *
	 * @param title            The notification title
	 * @param text             The notification text
	 * @param duration         The duration that notification will be visible
	 * @param notificationType The notification type
	 */
	public static void showNotification(String title, String text, Duration duration, NotificationType notificationType,
			Node graphic) {
	
		try {
	
			// Check if it is JavaFX Application Thread
			if (!Platform.isFxApplicationThread()) {
				Platform.runLater(() -> showNotification(title, text, duration, notificationType, graphic));
				return;
			}
	
			// Show the notification
			showNotification2(title, text, duration, notificationType, graphic);
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}

	/**
	 * Show a notification.
	 *
	 * @param title            The notification title
	 * @param text             The notification text
	 * @param duration         The duration that notification will be visible
	 * @param notificationType The notification type
	 */
	public static void showNotification(String title, String text, Duration duration,
			NotificationType notificationType) {
		Platform.runLater(() -> showNotification(title, text, duration, notificationType, null));
	}

}
