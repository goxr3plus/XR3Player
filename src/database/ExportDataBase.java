/*
 * 
 */
package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

// TODO: Auto-generated Javadoc
/**
 * This class is used as a Service which is exporting the applications database
 * as a zip folder.
 *
 * @author SuperGoliath
 */
public class ExportDataBase extends Service<Boolean> {
	
	/** The file list. */
	List<String> fileList = new ArrayList<>();
	
	/** The zip file. */
	String zipFile = null;
	
	/** The source folder. */
	String sourceFolder = null;
	
	/** The success. */
	Notifications success = Notifications.create().title("Mission Completed")
	        .text("Successfully exported the database!");
	
	/** The fail. */
	Notifications fail = Notifications.create().title("Mission Failed").text("Failed to export the database!");
	
	/** The exception. */
	String exception;
	
	/**
	 * This method is using a Service to export the dataBase into a zip folder.
	 *
	 * @param zip The Destination zip Folder
	 * @param sourceFolder The source Folder
	 */
	public void exportDataBase(String zip , String sourceFolder) {
		
		// initialize these variables
		zipFile = zip;
		this.sourceFolder = sourceFolder;
		
		setOnSucceeded(s -> {
			done();
			
			// Check the Value
			if (getValue())
				success.show();
			else
				fail.text(exception).showError();
		});
		
		setOnFailed(f -> {
			done();
			fail.text(exception).showError();
		});
		
		setOnCancelled(c -> {
			done();
			fail.showError();
		});
		
		// start the service
		reset();
		restart();
		
	}
	
	/**
	 * Service done.
	 */
	private void done() {
		Main.fixLayout();
		Main.updateScreen.progressBar.progressProperty().unbind();
		
	}
	
	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				
				// Create a list with all the files and folders of the
				// sourceFolder
				fileList.clear();
				generateFileList(new File(sourceFolder), sourceFolder);
				byte[] buffer = new byte[1024];
				
				double total = fileList.size();
				double counter = 0;
				
				// GO
				try (FileOutputStream fos = new FileOutputStream(zipFile);
				        ZipOutputStream zos = new ZipOutputStream(fos)) {
					
					// Start
					for (String file : fileList) {
						
						// Refresh the label Text
						Platform.runLater(() -> {
							Main.updateScreen.label.setText("OUT:" + file);
						});
						
						// Create zipEntry
						ZipEntry ze = new ZipEntry(file);
						zos.putNextEntry(ze);
						
						FileInputStream in = new FileInputStream(sourceFolder + File.separator + file);
						
						// Copy byte by byte
						int len;
						while ( ( len = in.read(buffer) ) > 0)
							zos.write(buffer, 0, len);
						
						in.close();
						updateProgress(++counter / total, 1);
					}
					
					// Close the motherFuckers
					zos.closeEntry();
					
				} catch (IOException ex) {
					ex.printStackTrace();
					exception = ex.getMessage();
					return false;
				}
				
				return true;
			}
			
		};
	}
	
	/**
	 * Traverse a directory and get all files, and add the file into fileList.
	 *
	 * @param file the file
	 * @param file2 the file 2
	 */
	public void generateFileList(File file , String file2) {
		
		// add file only
		if (file.isFile())
			fileList.add(generateZipEntry(file.getAbsoluteFile().toString(), file2));
		
		if (file.isDirectory()) {
			String[] subNote = file.list();
			if (subNote != null)
				for (String filename : subNote)
					generateFileList(new File(file, filename), file2);
				
		}
		
	}
	
	/**
	 * Format the file path for zip.
	 *
	 * @param file file path
	 * @param sourceFolder the source folder
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file , String sourceFolder) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}
	
}
