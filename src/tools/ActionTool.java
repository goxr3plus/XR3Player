/*
 * 
 */
package tools;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Random;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * A class which has a lot of useful methods.
 *
 * @author GOXR3PLUS
 */
public final class ActionTool {

    /** The random. */
    static Random random = new Random();

    /** The question answer. */
    static boolean questionAnswer = false;

    /** The warning image. */
    static ImageView warningImage = InfoTool.getImageViewFromDocuments("warning.png");

    /** The question image. */
    static ImageView questionImage = InfoTool.getImageViewFromDocuments("question.png");

    /** The notification. */
    static Notifications notification = Notifications.create().darkStyle();

    /**
     * Private Constructor.
     */
    private ActionTool() {
    }

    /**
     * Opens the file with the System default file explorer.
     *
     * @param path
     *            the path
     */
    public static void openFileLocation(String path) {
	showNotification("Message", "Opening in System File Explorer...\n" + InfoTool.getFileName(path),
		Duration.millis(1500), NotificationType.INFORMATION);
	if (InfoTool.osName.toLowerCase().contains("win")) {
	    try {
		Runtime.getRuntime().exec("explorer.exe /select," + path);
	    } catch (IOException ex) {
		Main.logger.log(Level.WARNING, ex.getMessage(), ex);
		showNotification("Folder Explorer Fail", "Failed to open file explorer.", Duration.millis(1500),
			NotificationType.WARNING);
	    }
	} else {
	    showNotification("Not Supported",
		    "This function is only supported in Windows \n I am trying my best to implement it and on other operating systems :)",
		    Duration.millis(1500), NotificationType.WARNING);
	}

    }

    /**
     * Copy a file from source to destination.
     *
     * @param source
     *            the source
     * @param destination
     *            the destination
     */
    public static void copy(String source, String destination) {

	// Use bytes stream to support all file types
	try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destination)) {

	    byte[] buffer = new byte[1024];

	    int length;

	    // copy the file content in bytes
	    while ((length = in.read(buffer)) > 0)
		out.write(buffer, 0, length);

	} catch (Exception ex) {
	    Main.logger.log(Level.WARNING, "", ex);
	}

	// System.out.println("Copying ->" + source + "\n\tto ->" + destination)

    }

    /**
     * Moves a file to a different location.
     *
     * @param source
     *            the source
     * @param dest
     *            the dest
     * @return true, if successful
     */
    public static boolean move(String source, String dest) {
	copy(source, dest);
	return new File(source).delete();
    }

    /**
     * Deletes Directory of File.
     *
     * @param src
     *            the src
     * @return true, if successful
     */
    public static boolean deleteFile(File src) {

	if (src.isDirectory()) { // Directory
	    File[] list = src.listFiles();
	    if (list != null) { // If !returns null
		for (File subFile : list)
		    if (subFile != null)
			deleteFile(subFile);

		if (!src.delete()) {
		    Notifications.create().title("Error")
			    .text("Can't delete file:\n(" + src.getName() + ") cause is in use by a program.")
			    .darkStyle().showWarning();
		    return false;
		}
	    }
	} else if (src.isFile() && !src.delete()) { // File
	    Notifications.create().title("Error")
		    .text("Can't delete file:\n(" + src.getName() + ") cause is in use by a program.").darkStyle()
		    .showWarning();
	    return false;
	}

	return true;
    }

    /**
     * Calculates the creationTime of the File.
     *
     * @param path
     *            the path
     * @return A String in format <b> DD/MM/YYYY</b>
     */
    public static String getFileDateCreated(String path) {
	String[] dateCreatedF = getFileCreationTime(path).toString().split("-");

	return dateCreatedF[2].substring(0, 2) + "/" + dateCreatedF[1] + "/" + dateCreatedF[0];

    }

    /**
     * Calculates the creationTime of the File.
     *
     * @param path
     *            the path
     * @return FileTime
     */
    public static FileTime getFileCreationTime(String path) {
	try {
	    return Files.readAttributes(Paths.get(path), BasicFileAttributes.class).creationTime();
	} catch (IOException ex) {
	    Main.logger.log(Level.INFO, "", ex);
	}

	return null;
    }

    /**
     * Tries to open that URI on the default browser
     * 
     * @param uri
     * @return <b>True</b> if succeeded , <b>False</b> if not
     */
    public static boolean openWebSite(String uri) {
	

	// Open the Default Browser
	if (Desktop.isDesktopSupported()) {
	    Desktop desktop = Desktop.getDesktop();
	    try {
		desktop.browse(new URI(uri));
	    } catch (IOException | URISyntaxException ex) {
		Platform.runLater(() -> ActionTool.showNotification("Problem Occured",
			"Can't open default web browser at:\n[ https://sourceforge.net/projects/xr3player/ ]",
			Duration.millis(2500), NotificationType.INFORMATION));
		ex.printStackTrace();
		return false;
	    }
	    // Error?
	} else {
	    System.out.println("Error trying to open the default web browser.");
	    return false;
	}
	return true;
    }

    /**
     * Show a notification.
     *
     * @param title
     *            The notification title
     * @param text
     *            The notification text
     * @param duration
     *            The duration that notification will be visible
     * @param type
     *            The notification type
     */
    public static void showNotification(String title, String text, Duration duration, NotificationType type) {
	Notifications notification = Notifications.create().title(title).text(text);
	notification.hideAfter(duration);

	switch (type) {
	case INFORMATION:
	    notification.showInformation();
	    break;
	case WARNING:
	    notification.showWarning();
	    break;
	case ERROR:
	    notification.showError();
	    break;
	case CONFIRM:
	    notification.showConfirm();
	    break;
	case SIMPLE:
	    notification.show();
	    break;
	default:
	    break;

	}

    }

    /**
     * Makes a question to the user.
     *
     * @param text
     *            the text
     * @return true, if successful
     */
    public static boolean doQuestion(String text) {
	questionAnswer = false;

	// Show Alert
	Alert alert = new Alert(AlertType.CONFIRMATION);
	alert.initStyle(StageStyle.UTILITY);
	alert.initOwner(Main.window);
	alert.setGraphic(questionImage);
	alert.setHeaderText("Question");
	alert.setContentText(text);
	alert.showAndWait().ifPresent(answer -> {
	    if (answer == ButtonType.OK)
		questionAnswer = true;
	    else
		questionAnswer = false;
	});

	return questionAnswer;
    }

    /**
     * Makes a question to the user.
     *
     * @param text
     *            the text
     * @param node
     *            The node owner of the Alert
     * @return true, if successful
     */
    public static boolean doQuestion(String text, Node node) {
	questionAnswer = false;

	// Show Alert
	Alert alert = new Alert(AlertType.CONFIRMATION);
	alert.initStyle(StageStyle.UTILITY);
	alert.setGraphic(questionImage);
	alert.setHeaderText("Question");
	alert.setContentText(text);

	// Make sure that JavaFX doesn't cut the text with ...
	alert.getDialogPane().getChildren().stream().filter(item -> node instanceof Label)
		.forEach(item -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));

	// I noticed that height property is notified after width property
	// that's why i choose to add the listener here
	alert.heightProperty().addListener(l -> {

	    // Width and Height of the Alert
	    int alertWidth = (int) alert.getWidth();
	    int alertHeight = (int) alert.getHeight();

	    // Here it prints 0!!
	    System.out.println("Alert Width: " + alertWidth + " , Alert Height: " + alertHeight);

	    // Find the bounds of the node
	    Bounds bounds = node.localToScreen(node.getBoundsInLocal());
	    int x = (int) (bounds.getMinX() + bounds.getWidth() / 2 - alertWidth / 2);
	    int y = (int) (bounds.getMinY() + bounds.getHeight() / 2 - alertHeight / 2);

	    // Check if Alert goes out of the Screen on X Axis
	    if (x + alertWidth > InfoTool.getVisualScreenWidth())
		x = (int) (InfoTool.getVisualScreenWidth() - alertWidth);
	    else if (x < 0)
		x = 0;

	    // Check if Alert goes out of the Screen on Y AXIS
	    if (y + alertHeight > InfoTool.getVisualScreenHeight())
		y = (int) (InfoTool.getVisualScreenHeight() - alertHeight);
	    else if (y < 0)
		y = 0;

	    // Set the X and Y of the Alert
	    alert.setX(x);
	    alert.setY(y);
	});

	// Show the Alert
	alert.showAndWait().ifPresent(answer -> {
	    if (answer == ButtonType.OK)
		questionAnswer = true;
	    else
		questionAnswer = false;
	});

	return questionAnswer;
    }

    /**
     * Delete confirmation.
     *
     * @param permanent
     *            the permanent
     * @param text
     *            the text
     * @param i
     *            the i
     * @return true, if successful
     */
    public static boolean doDeleteQuestion(boolean permanent, String text, int i) {
	questionAnswer = false;
	String unique = "\n [" + text + "]";
	String multiple = "[" + text + " items]";

	Alert alert = new Alert(AlertType.CONFIRMATION);
	alert.initStyle(StageStyle.UTILITY);
	alert.initOwner(Main.window);
	alert.setGraphic(!permanent ? questionImage : warningImage);
	alert.setHeaderText(!permanent ? "Remove selection" + (i > 1 ? "s " + multiple : unique) + " from List?"
		: "Are you sure you want to permanently delete " + (i > 1 ? "these " + multiple : "this item " + unique)
			+ " ?");
	alert.setContentText(!permanent
		? "Are you sure you want to remove the selected " + (i > 1 ? "items" : "item") + " from the List?"
		: "If you delete the selection " + (i > 1 ? "s  they" : "it") + " will be permanenlty lost.");
	// LookUpButton
	((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setDefaultButton(false);
	((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);
	alert.showAndWait().ifPresent(answer -> {
	    if (answer == ButtonType.OK)
		questionAnswer = true;
	});

	return questionAnswer;
    }

    /**
     * Returns a Random Number from 0 to ...what i have choosen in method see
     * the doc
     *
     * @return the int
     */
    public static int returnRandom() {
	return random.nextInt(80000);
    }

    /**
     * Return random table name.
     *
     * @return Returns a RandomTableName for the database in format
     *         ("_"+randomNumber)
     */
    public static String returnRandomTableName() {
	return "_" + returnRandom();
    }

    /**
     * Paint the given text on the given graphics context.
     *
     * @param gc
     *            the gc
     * @param text
     *            the text
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public static void paintCanvas(GraphicsContext gc, String text, int width, int height) {

	// Clear
	gc.clearRect(0, 0, width, height);

	// Paint it
	gc.setLineWidth(2);
	gc.setLineDashes(3);
	gc.setFill(Color.WHITE);
	gc.fillRoundRect(0, 0, width, height, 15, 15);
	gc.setStroke(Color.BLACK);
	gc.strokeRoundRect(0, 0, width, height, 15, 15);
	gc.setFill(Color.BLACK);
	gc.setFont(Font.font(null, FontWeight.BOLD, 14));
	gc.fillText(text, 4, height / 2.00);

    }

}
