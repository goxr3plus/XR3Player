/*
 * 
 */
package application.services;

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

/**
 * This class is used as a Service which is exporting the applications database as a zip folder.
 *
 * @author SuperGoliath
 */
public class CreateZipService extends Service<Boolean> {
	
	/** The file list. */
	List<String> fileList = new ArrayList<>();
	
	/** The zip file. */
	String zipFile;
	
	/** The source folder. */
	String sourceFolder;
	
	/** The success. */
	Notifications success = Notifications.create().title("Mission Completed").text("Successfully exported the database!");
	
	/** The fail. */
	Notifications fail = Notifications.create().title("Mission Failed").text("Failed to export the database!");
	
	/** The exception. */
	String exception;
	
	/**
	 * This method is using a Service to export the dataBase into a zip folder.
	 *
	 * @param zip
	 *            The Destination zip Folder
	 * @param sourceFolder1
	 *            The source Folder
	 */
	public void exportDataBase(String zip , String sourceFolder1) {
		
		// initialize these variables
		zipFile = zip;
		this.sourceFolder = sourceFolder1;
		
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
		Main.updateScreen.setVisible(false);
		Main.updateScreen.getProgressBar().progressProperty().unbind();
		
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
				
				double total = fileList.size() , counter = 0;
				
				// GO
				try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
					
					// Start
					for (String file : fileList) {
						
						// Refresh the label Text
						Platform.runLater(() -> Main.updateScreen.getLabel().setText("OUT:" + file));
						
						// Create zipEntry		
						zos.putNextEntry(new ZipEntry(file));
						
						//Create File Input Stream
						try (FileInputStream in = new FileInputStream(sourceFolder + File.separator + file)) {
							
							//Copy byte by byte
							int len;
							while ( ( len = in.read(buffer) ) > 0)
								zos.write(buffer, 0, len);
							
						} catch (IOException ex) {
							ex.printStackTrace();
							exception = ex.getMessage();
						}
						
						//Update Progress
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
	 * @param f
	 *            the file
	 * @param file2
	 *            the file 2
	 */
	public void generateFileList(File f , String file2) {
		
		// add file only
		if (f.isFile())
			fileList.add(generateZipEntry(f.getAbsoluteFile() + "", file2));
		
		if (!f.isDirectory())
			return;
		String[] subNote = f.list();
		if (subNote != null)
			for (String filename : subNote)
				generateFileList(new File(f, filename), file2);
			
	}
	
	/**
	 * Format the file path for zip.
	 *
	 * @param file
	 *            file path
	 * @param sourceFolder
	 *            the source folder
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file , String sourceFolder) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}
	
}
