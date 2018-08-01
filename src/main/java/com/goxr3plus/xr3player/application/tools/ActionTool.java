/*
 * 
 */
package main.java.com.goxr3plus.xr3player.application.tools;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
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
import main.java.com.goxr3plus.xr3player.application.settings.GeneralSettingsController;

/**
 * A class which has a lot of useful methods.
 *
 * @author GOXR3PLUS
 */
public final class ActionTool {
	
	/** The logger for this class */
	private static final Logger logger = Logger.getLogger(ActionTool.class.getName());
	
	/** The random. */
	private static Random random = new Random();
	
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
		
		// Open the Default Browser
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			showNotification("Message", "Opening in File Explorer:\n" + InfoTool.getFileName(path), Duration.millis(1500), NotificationType.INFORMATION);
			
			//START: --NEEDS TO BE FIXED!!!!!!----------------NOT WORKING WELL-----
			
			path = path.trim().replaceAll(" +", " ");
			String selectPath = "/select," + path;
			
			//START: Strip one SPACE among consecutive spaces
			LinkedList<String> list = new LinkedList<>();
			StringBuilder sb = new StringBuilder();
			boolean flag = true;
			
			for (int i = 0; i < selectPath.length(); i++) {
				if (i == 0) {
					sb.append(selectPath.charAt(i));
					continue;
				}
				
				if (selectPath.charAt(i) == ' ' && flag) {
					list.add(sb.toString());
					sb.setLength(0);
					flag = false;
					continue;
				}
				
				if (!flag && selectPath.charAt(i) != ' ')
					flag = true;
				
				sb.append(selectPath.charAt(i));
			}
			
			list.add(sb.toString());
			
			list.addFirst("explorer.exe");
			//END: Strip one SPACE among consecutive spaces
			
			//END: --NEEDS TO BE FIXED!!!!!!----------------NOT WORKING WELL-----
			
			try {
				//Open in Explorer and Highlight
				new ProcessBuilder(list).start();
			} catch (IOException ex) {
				logger.log(Level.WARNING, ex.getMessage(), ex);
				showNotification("Folder Explorer Fail", "Failed to open file explorer.", Duration.millis(1500), NotificationType.WARNING);
			}
		} else { //For MacOS and Linux
			try {
				Desktop.getDesktop().browseFileDirectory(new File(path));
			} catch (Exception ex) {
				ex.printStackTrace();
				showNotification("Not Supported", "This function is only supported in Windows \n I am trying my best to implement it and on other operating systems :)",
						Duration.millis(1500), NotificationType.WARNING);
			}
		}
		
	}
	
	/**
	 * Copy a file from source to destination.
	 *
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @return True if succeeded , False if not
	 */
	public static boolean copy(String source , String destination) {
		boolean succeess = true;
		
		//System.out.println("Copying ->" + source + "\n\tto ->" + destination)
		
		try {
			Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
			succeess = false;
		}
		
		return succeess;
		
	}
	
	/**
	 * Copy a file from source to destination.
	 *
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @return True if succeeded , False if not
	 */
	public static boolean copy(InputStream source , String destination) {
		boolean succeess = true;
		
		//System.out.println("Copying ->" + source + "\n\tto ->" + destination)
		
		try {
			System.out.println(Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING));
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
			succeess = false;
		}
		
		return succeess;
	}
	
	/**
	 * Moves a file to a different location.
	 *
	 * @param source
	 *            the source
	 * @param destination
	 *            the dest
	 * @return true, if successful
	 */
	public static boolean move(String source , String destination) {
		boolean succeess = true;
		
		//System.out.println("Moving ->" + source + "\n\tto ->" + destination)
		
		try {
			Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
			succeess = false;
		}
		
		return succeess;
	}
	
	/**
	 * Deletes Directory of File.
	 *
	 * @param source
	 *            The File to be deleted | either if it is directory or File
	 * @return true, if successful
	 */
	public static boolean deleteFile(File source) {
		
		if (source.isDirectory())  // Directory
			try {
				FileUtils.deleteDirectory(source);
			} catch (IOException ex) {
				logger.log(Level.INFO, "", ex);
			}
		else if (source.isFile() && !source.delete()) { // File
			showNotification("Message", "Can't delete file:\n(" + source.getName() + ") cause is in use by a program.", Duration.millis(2000), NotificationType.WARNING);
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
		
		FileTime creationTime = getFileCreationTime(path);
		
		//Be carefull for null pointer exception here
		if (creationTime == null)
			return "error occured";
		
		String[] dateCreatedF = creationTime.toString().split("-");
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
			logger.log(Level.INFO, "", ex);
		}
		
		return null;
	}
	
	/**
	 * Tries to open this File with the default system program
	 * 
	 * @param absolutePath
	 *            The absolute path of the File
	 * @return <b>True</b> if succeeded , <b>False</b> if not
	 */
	public static boolean openFile(String absolutePath) {
		
		try {
			//Check if Desktop is supported
			if (!Desktop.isDesktopSupported()) {
				ActionTool.showNotification("Problem Occured", "Can't open default File at:\n[" + absolutePath + " ]", Duration.millis(2500), NotificationType.INFORMATION);
				return false;
			}
			
			//Check File existance
			File file = new File(absolutePath);
			if (file.exists()) {
				ActionTool.showNotification("Opening file", "Opening in File Explorer :\n" + absolutePath, Duration.millis(1500), NotificationType.INFORMATION);
				Desktop.getDesktop().open(file);
			} else
				ActionTool.showNotification("Can't open file", "Can't open in File Explorer :\n" + absolutePath + " \n because it doesn't exists !", Duration.millis(1500),
						NotificationType.INFORMATION);
		} catch (IOException ex) {
			ActionTool.showNotification("Problem Occured", "Can't open default File at:\n[" + absolutePath + " ]", Duration.millis(2500), NotificationType.INFORMATION);
			logger.log(Level.INFO, "", ex);
			return false;
		}
		return true;
	}
	
	/**
	 * Tries to open that URI on the default browser
	 * 
	 * @param uri
	 * @return <b>True</b> if succeeded , <b>False</b> if not
	 */
	public static boolean openWebSite(String uri) {
		
		//Check if Desktop is supported
		if (!Desktop.isDesktopSupported()) {
			ActionTool.showNotification("Problem Occured", "Can't open default web browser at:\n[" + uri + " ]", Duration.millis(2500), NotificationType.INFORMATION);
			return false;
		}
		
		ActionTool.showNotification("Opening WebSite", "Opening on default Web Browser :\n" + uri, Duration.millis(1500), NotificationType.INFORMATION);
		
		//Start it to a new Thread , don't lag the JavaFX Application Thread
		new Thread(() -> {
			try {
				Desktop.getDesktop().browse(new URI(uri));
			} catch (IOException | URISyntaxException ex) {
				ActionTool.showNotification("Problem Occured", "Can't open default web browser at:\n[" + uri + " ]", Duration.millis(2500), NotificationType.INFORMATION);
				logger.log(Level.INFO, "", ex);
			}
		}).start();
		
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
	 * @param notificationType
	 *            The notification type
	 */
	public static void showNotification(String title , String text , Duration duration , NotificationType notificationType) {
		Platform.runLater(() -> showNotification(title, text, duration, notificationType, null));
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
	 * @param notificationType
	 *            The notification type
	 */
	public static void showNotification(String title , String text , Duration duration , NotificationType notificationType , Node graphic) {
		
		try {
			
			//Check if it is JavaFX Application Thread
			if (!Platform.isFxApplicationThread()) {
				Platform.runLater(() -> showNotification(title, text, duration, notificationType, graphic));
				return;
			}
			
			//Show the notification
			showNotification2(title, text, duration, notificationType, graphic);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Just a helper method for showNotification methods
	 */
	private static void showNotification2(String title , String text , Duration duration , NotificationType notificationType , Node graphic) {
		Notifications notification1;
		
		//Set graphic
		if (graphic == null)
			notification1 = Notifications.create().title(title).text(text).hideAfter(duration).darkStyle().position(GeneralSettingsController.notificationPosition);
		else
			notification1 = Notifications.create().title(title).text(text).hideAfter(duration).darkStyle().position(GeneralSettingsController.notificationPosition)
					.graphic(graphic);
		
		//Show the notification
		switch (notificationType) {
			case CONFIRM:
				notification1.graphic(JavaFXTools.getFontIcon("fas-question-circle", Color.web("#ad14e2"), 32)).show();
				break;
			case ERROR:
				notification1.graphic(JavaFXTools.getFontIcon("fas-times", Color.web("#f83e3e"), 32)).show();
				break;
			case INFORMATION:
				notification1.graphic(JavaFXTools.getFontIcon("fas-info-circle", Color.web("#1496e5"), 32)).show();
				break;
			case SIMPLE:
				notification1.show();
				break;
			case WARNING:
				notification1.graphic(JavaFXTools.getFontIcon("fa-warning", Color.web("#d74418"), 32)).show();
				break;
			case SUCCESS:
				notification1.graphic(JavaFXTools.getFontIcon("fas-check", Color.web("#64ff41"), 32)).show();
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
	 * @param node
	 *            The node owner of the Alert
	 * @return true, if successful
	 */
	public static boolean doQuestion(String headerText , String text , Node node , Stage window) {
		boolean[] questionAnswer = { false };
		
		// Show Alert
		Alert alert = JavaFXTools.createAlert(null, headerText, text, AlertType.CONFIRMATION, StageStyle.UTILITY, window, JavaFXTools.getFontIcon("fas-question-circle", Color.WHITE, 24));
		
		// Make sure that JavaFX doesn't cut the text with ...
		alert.getDialogPane().getChildren().stream().filter(item -> node instanceof Label).forEach(item -> ( (Label) node ).setMinHeight(Region.USE_PREF_SIZE));
		
		if (node != null) {
			// I noticed that height property is notified after width property
			// that's why i choose to add the listener here
			alert.heightProperty().addListener(l -> {
				
				// Width and Height of the Alert
				int alertWidth = (int) alert.getWidth();
				int alertHeight = (int) alert.getHeight();
				
				// Here it prints 0!!
				//System.out.println("Alert Width: " + alertWidth + " , Alert Height: " + alertHeight)
				
				// Find the bounds of the node
				Bounds bounds = node.localToScreen(node.getBoundsInLocal());
				int x = (int) ( bounds.getMinX() + bounds.getWidth() / 2 - alertWidth / 2 );
				int y = (int) ( bounds.getMinY() + bounds.getHeight() / 2 - alertHeight / 2 );
				
				// Check if Alert goes out of the Screen on X Axis
				if (x + alertWidth > InfoTool.getVisualScreenWidth())
					x = (int) ( InfoTool.getVisualScreenWidth() - alertWidth );
				else if (x < 0)
					x = 0;
				
				// Check if Alert goes out of the Screen on Y AXIS
				if (y + alertHeight > InfoTool.getVisualScreenHeight())
					y = (int) ( InfoTool.getVisualScreenHeight() - alertHeight );
				else if (y < 0)
					y = 0;
				
				// Set the X and Y of the Alert
				alert.setX(x);
				alert.setY(y);
			});
		}
		
		// Show the Alert
		alert.showAndWait().ifPresent(answer -> questionAnswer[0] = ( answer == ButtonType.OK ));
		
		return questionAnswer[0];
	}
	
	/**
	 * Returns a Random Number from 0 to ...what i have choosen in method see the doc
	 *
	 * @return A random integer
	 */
	public static int returnRandom() {
		return random.nextInt(80000);
	}
	
	/**
	 * Return random table name.
	 *
	 * @return Returns a RandomTableName for the database in format ("_"+randomNumber)
	 */
	public static String returnRandomTableName() {
		return "_" + returnRandom();
	}
	
	/**
	 * The Type of File
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public enum FileType {
		DIRECTORY, FILE, ZIP;
	}
	
	/**
	 * Creates the given File or Folder if not exists and returns the result
	 * 
	 * @param absoluteFilePath
	 *            The absolute path of the File|Folder
	 * @param fileType
	 *            Create DIRECTORY OR FILE ?
	 * @return True if exists or have been successfully created , otherwise false
	 */
	public static boolean createFileOrFolder(String absoluteFilePath , FileType fileType) {
		return createFileOrFolder(new File(absoluteFilePath), fileType);
	}
	
	/**
	 * Creates the given File or Folder if not exists and returns the result
	 * 
	 * @param absoluteFilePath
	 *            The absolute path of the File|Folder
	 * @param fileType
	 *            Create DIRECTORY OR FILE ?
	 * @return True if exists or have been successfully created , otherwise false
	 */
	public static boolean createFileOrFolder(File file , FileType fileType) {
		//Already exists?
		if (file.exists())
			return true;
		//Directory?
		if (fileType == FileType.DIRECTORY)
			return file.mkdir();
		//File?
		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
