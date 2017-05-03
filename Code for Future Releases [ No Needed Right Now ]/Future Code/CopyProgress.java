/*
 * 
 */
package services;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import tools.InfoTool;

/**
 * Get the progress of Copy Operation.
 * 
 * @author GOXR3PLUS
 *
 */
public class CopyProgress extends Service<Void> {

    /** The basic file. */
    File basicFile;

    /** The journal file. */
    File journalFile;

    /**
     * Starts the Vacuum Progress Service.
     *
     * @param basicFile
     *            the basic file
     * @param journalFile
     *            the journal file
     */
    public void start(File basicFile, File journalFile) {
	this.basicFile = basicFile;
	this.journalFile = journalFile;
	reset();
	start();
    }

    @Override
    protected Task<Void> createTask() {
	return new Task<Void>() {
	    @Override
	    protected Void call() throws Exception {

		updateMessage("Hold on(Service is running)...");

		long bfL = basicFile.length();
		long jfL;

		// Update Message
		updateMessage("Before:" + InfoTool.getFileSizeEdited(basicFile) + "  After:"
			+ InfoTool.getFileSizeEdited(journalFile));

		// Wait until it is created
		while (!journalFile.exists())
		    Thread.sleep(50);

		// creating process...
		while ((jfL = journalFile.length()) <= bfL) {

		    // Update Message
		    updateMessage("Before:" + InfoTool.getFileSizeEdited(basicFile) + "  After:"
			    + InfoTool.getFileSizeEdited(journalFile));

		    // Update Progress
		    updateProgress(jfL, bfL);

		    // Sleep
		    Thread.sleep(50);
		}

		// Update Message
		updateMessage("Terminating..");

		return null;
	    }

	};
    }

}