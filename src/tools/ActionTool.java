/*
 * 
 */
package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Random;
import java.util.logging.Level;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
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
	
	/** The alert. */
	static Alert alert = new Alert(AlertType.CONFIRMATION);
	
	/** The warning image. */
	static ImageView warningImage = InfoTool.getImageViewFromDocuments("warning.png");
	
	/** The question image. */
	static ImageView questionImage = InfoTool.getImageViewFromDocuments("question.png");
	
	/** The notification. */
	static Notifications notification = Notifications.create().darkStyle();
	
	/**
	 * Private Constructor.
	 */
	private ActionTool() {}
	
	/**
	 * This method needs to be called from <b>JavaFX Thread</b> at least
	 * one time before this class is being used.That's because this class
	 * contains elements from <b>JavaFX</b> which needs to be initialized before
	 * the methods of this class are used from external threads . But mention
	 * that if you use a method which is accessing a JavaFX element from
	 * external Thread you have to use <b> Platform.runLater() </b>;
	 */
	public static void initInternalJavaFXElements() {
		alert.initStyle(StageStyle.UTILITY);
	}
	
	/**
	 * Opens the file with the System default file explorer.
	 *
	 * @param path the path
	 */
	public static void openFileLocation(String path) {
		showNotification("Message", "Opening in System File Explorer...\n" + InfoTool.getFileName(path),
		        NotificationType.INFORMATION);
		if (InfoTool.osName.toLowerCase().contains("win")) {
			try {
				Runtime.getRuntime().exec("explorer.exe /select," + path);
			} catch (IOException ex) {
				Main.logger.log(Level.WARNING, ex.getMessage(), ex);
				showNotification("Folder Explorer Fail", "Failed to open file explorer.", NotificationType.WARNING);
			}
		} else {
			showNotification("Not Supported",
			        "This function is only supported in Windows \n I am trying my best to implement it and on other operating systems :)",
			        NotificationType.WARNING);
		}
		
	}
	
	/**
	 * Copy a file from source to destination.
	 *
	 * @param source the source
	 * @param destination the destination
	 */
	public static void copy(String source , String destination) {
		
		// Use bytes stream to support all file types
		try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destination)) {
			
			byte[] buffer = new byte[1024];
			
			int length;
			
			// copy the file content in bytes
			while ( ( length = in.read(buffer) ) > 0)
				out.write(buffer, 0, length);
			
		} catch (Exception ex) {
			Main.logger.log(Level.WARNING, "", ex);
		}
		
		// System.out.println("Copying ->" + source + "\n\tto ->" + destination)
		
	}
	
	/**
	 * Moves a file to a different location.
	 *
	 * @param source the source
	 * @param dest the dest
	 * @return true, if successful
	 */
	public static boolean move(String source , String dest) {
		copy(source, dest);
		return new File(source).delete();
	}
	
	/**
	 * Deletes Directory of File.
	 *
	 * @param src the src
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
	 * @param path the path
	 * @return A String in format <b> DD/MM/YYYY</b>
	 */
	public static String getFileDateCreated(String path) {
		String[] dateCreatedF = getFileCreationTime(path).toString().split("-");
		
		return dateCreatedF[2].substring(0, 2) + "/" + dateCreatedF[1] + "/" + dateCreatedF[0];
		
	}
	
	/**
	 * Calculates the creationTime of the File.
	 *
	 * @param path the path
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
	 * Show a notification.
	 *
	 * @param title the title
	 * @param text the text
	 * @param type the type
	 */
	public static void showNotification(String title , String text , NotificationType type) {
		Notifications notification = Notifications.create().title(title).text(text).darkStyle();
		notification.hideAfter(Duration.millis(1500));
		
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
	 * @param text the text
	 * @return true, if successful
	 */
	public static boolean doQuestion(String text) {
		questionAnswer = false;
		
		// Show Alert
		alert.setGraphic(questionImage);
		alert.setHeaderText("");
		alert.setContentText(text);
		alert.showAndWait().ifPresent(answer -> {
			if (answer == ButtonType.OK)
				questionAnswer = true;
		});
		
		return questionAnswer;
	}
	
	/**
	 * Delete confirmation.
	 *
	 * @param permanent the permanent
	 * @param text the text
	 * @param i the i
	 * @return true, if successful
	 */
	public static boolean doDeleteQuestion(boolean permanent , String text , int i) {
		questionAnswer = false;
		String unique = "\n [" + text + "]";
		String multiple = "[" + text + " items]";
		
		alert.setGraphic(!permanent ? questionImage : warningImage);
		alert.setHeaderText(!permanent ? "Remove selection" + ( i > 1 ? "s " + multiple : unique ) + " from List?"
		        : "Are you sure you want to permanently delete "
		                + ( i > 1 ? "these " + multiple : "this item " + unique ) + " ?");
		alert.setContentText(!permanent
		        ? "Are you sure you want to remove the selected " + ( i > 1 ? "items" : "item" ) + " from the List?"
		        : "If you delete the selection " + ( i > 1 ? "s  they" : "it" ) + " will be permanenlty lost.");
		// LookUpButton
		( (Button) alert.getDialogPane().lookupButton(ButtonType.OK) ).setDefaultButton(false);
		( (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL) ).setDefaultButton(true);
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
	 * @param gc the gc
	 * @param text the text
	 * @param width the width
	 * @param height the height
	 */
	public static void paintCanvas(GraphicsContext gc , String text , int width , int height) {
		
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
