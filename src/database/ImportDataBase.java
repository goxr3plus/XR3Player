/*
 * 
 */
package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import tools.InfoTool;

/**
 * This class is used to import an XR3Player database (as .zip folder)
 * 
 * @author SuperGoliath
 *
 */
public class ImportDataBase extends Service<Boolean> {
	
	/** The input zip. */
	String inputZip;
	
	/** The out put folder. */
	String outPutFolder = InfoTool.ABSOLUTE_DATABASE_PATH_PLAIN;
	
	/** The zip. */
	ZipFile zip;
	
	/** The success. */
	Notifications success = Notifications.create().title("Mission Completed")
	        .text("Successfully imported the database!");
	
	/** The fail. */
	Notifications fail = Notifications.create().title("Mission Failed").text("Failed to import the database!");
	
	/** The exception. */
	String exception;
	
	/**
	 * Constructor.
	 */
	public ImportDataBase() {
		
		setOnSucceeded(s -> {
			// done()
			Main.canSaveData = false;
			
			// Check the Value
			if (getValue())
				success.showInformation();
			else
				fail.text(exception).showError();
			
			// Restart XR3Player
			Main.updateScreen.progressBar.progressProperty().unbind();
			Main.updateScreen.progressBar.setProgress(-1);
			Main.updateScreen.label.setText("Restarting....");
			Main.restartTheApplication(false);
			
		});
		
		setOnFailed(failed -> {
			done();
			fail.showError();
		});
		
		setOnCancelled(c -> {
			done();
			fail.showError();
			
		});
	}
	
	/**
	 * Done.
	 */
	private void done() {
		Main.fixLayout();
		Main.updateScreen.progressBar.progressProperty().unbind();
	}
	
	/**
	 * Import the database from the zip folder.
	 *
	 * @param zipFolder the zip folder
	 */
	public void importDataBase(String zipFolder) {
		inputZip = zipFolder;
		reset();
		restart();
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask() */
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				byte[] buffer = new byte[1024];
				
				try {
					
					// create output directory is not exists
					File folder = new File(outPutFolder);
					if (!folder.exists())
						folder.mkdir();
					
					// get the zip file content
					ZipInputStream zis = new ZipInputStream(new FileInputStream(inputZip));
					// get the zipped file list entry
					ZipEntry ze = zis.getNextEntry();
					
					// Count entries
					zip = new ZipFile(inputZip);
					double counter = 0;
					double total = zip.size();
					
					// Start
					while (ze != null) {
						
						String fileName = ze.getName();
						File newFile = new File(outPutFolder + File.separator + fileName);
						
						// Refresh the dataLabel text
						Platform.runLater(() -> Main.updateScreen.label.setText("In:" + newFile.getName()));
						
						// create all non exists folders
						// else you will hit FileNotFoundException for
						// compressed folder
						new File(newFile.getParent()).mkdirs();
						
						FileOutputStream fos = new FileOutputStream(newFile);
						
						// Copy byte by byte
						int len;
						while ( ( len = zis.read(buffer) ) > 0)
							fos.write(buffer, 0, len);
						
						fos.close();
						ze = zis.getNextEntry();
						updateProgress(++counter / total, 1);
					}
					
					zis.closeEntry();
					zis.close();
					zip.close();
					
				} catch (IOException ex) {
					exception = ex.getMessage();
					Main.logger.log(Level.WARNING, "", ex);
					return false;
				}
				
				return true;
			}
			
		};
	}
}
