/*
 * 
 */
package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.controlsfx.control.Notifications;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import smartcontroller.Operation;
import tools.ActionTool;
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
    String outPutFolder = InfoTool.getAbsoluteDatabasePathPlain();

    /** The success. */
    Notifications success = Notifications.create().title("Mission Completed").text("Successfully imported the database!");

    /** The fail. */
    Notifications fail = Notifications.create().title("Mission Failed").text("Failed to import the database!").hideAfter(Duration.seconds(15));

    /** The exception. */
    String exception;

    /**
     * Constructor.
     */
    public ImportDataBase() {

	setOnSucceeded(s -> {
	    // done()
	    Main.canSaveData = false;

	    //Check the value
	    if (!getValue()) {
		fail.text(exception).showError();
		done();
	    } else {
		success.showInformation();

		// Restart XR3Player
		Main.updateScreen.getProgressBar().progressProperty().unbind();
		Main.updateScreen.getProgressBar().setProgress(-1);
		Main.updateScreen.getLabel().setText("Restarting....");
		Main.restartTheApplication(false);
	    }

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
    private static void done() {
	Main.updateScreen.setVisible(false);
	Main.updateScreen.getProgressBar().progressProperty().unbind();
    }

    /**
     * Import the database from the zip folder.
     *
     * @param zipFolder
     *            the zip folder
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

		//Previous versions < Update 56 of XR3Player will be broken after this update :( future is future

		//----------------------Search for the signature file-------------------------------		
		try (ZipFile zis = new ZipFile(inputZip)) {

		    //signature file
		    String signatureFile = InfoTool.getDatabaseSignatureFile().getName();

		    //get all entries                      
		    Enumeration<? extends ZipEntry> e = zis.entries();
		    boolean found = false;

		    System.out.println("Trying to search [" + signatureFile + "] in ->" + zis.getName());

		    //Search every entry inside the zip folder
		    while (e.hasMoreElements())
			/*
			 * Here, normal compare would not work.
			 *
			 * Because zip might contain directories so the entry name will not
			 * match extactly with the file name we want to search.
			 *
			 * Additionally, there might be more than one file with the same
			 * name in different directories inside the zip archive.
			 *
			 * So approch here is to search using indexOf and not using
			 * equals or equalsIgnoreCase methods.
			 */
			//System.out.println(entry.getName())
			if (e.nextElement().getName().indexOf(signatureFile) != -1) {
			    found = true;
			    // System.out.println("Found " + entry.getName())

			    /*
			     * if you want to search only first instance, uncomment the
			     * following break statement.
			     */

			    break;
			}

		    //Found it?
		    if (!found) {
			exception = "Can't find the signature file [ " + signatureFile
				+ " ]  inside the given .zip folder\n After Update .56 every XR3Player database contains the above file.";
			return false;
		    }

		} catch (IOException ex) {
		    exception = ex.getMessage();
		    Main.logger.log(Level.WARNING, "", ex);
		    return false;
		}

		//----------------------Found the signature file so we can procceeed-------------------------------

		// Close all the connections with database
		if (Main.dbManager != null)
		    Main.dbManager.manageConnection(Operation.CLOSE);

		// Delete the previous database
		ActionTool.deleteFile(new File(InfoTool.getAbsoluteDatabasePathPlain()));

		//---------------------Move on Importing the Database-----------------------------------------------

		// get the zip file content
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputZip))) {

		    // create output directory is not exists
		    File folder = new File(outPutFolder);
		    if (!folder.exists())
			folder.mkdir();

		    // get the zipped file list entry
		    ZipEntry ze = zis.getNextEntry();

		    // Count entries
		    ZipFile zip = new ZipFile(inputZip);
		    double counter = 0, total = zip.size();

		    //Start
		    for (byte[] buffer = new byte[1024]; ze != null;) {

			String fileName = ze.getName();
			File newFile = new File(outPutFolder + File.separator + fileName);

			// Refresh the dataLabel text
			Platform.runLater(() -> Main.updateScreen.getLabel().setText("In:" + newFile.getName()));

			// create all non exists folders
			// else you will hit FileNotFoundException for
			// compressed folder
			new File(newFile.getParent()).mkdirs();

			//Create File OutputStream
			try (FileOutputStream fos = new FileOutputStream(newFile)) {

			    // Copy byte by byte
			    int len;
			    while ((len = zis.read(buffer)) > 0)
				fos.write(buffer, 0, len);

			} catch (IOException ex) {
			    exception = ex.getMessage();
			    Main.logger.log(Level.WARNING, "", ex);
			}

			//Get next entry
			ze = zis.getNextEntry();

			//Update the progress
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
