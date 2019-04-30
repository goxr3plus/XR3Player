/*
 * 
 */
package com.goxr3plus.xr3player.services.database;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import com.goxr3plus.xr3player.utils.io.IOInfo;

/**
 * Get the progress of Vacuum Operation.
 * 
 * @author GOXR3PLUS
 *
 */
public class VacuumProgressService extends Service<Void> {

	/** The basic file. */
	private File basicFile;

	/** The journal file. */
	private File journalFile;

	/**
	 * Starts the Vacuum Progress Service.
	 *
	 * @param basicFile   the basic file
	 * @param journalFile the journal file
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

				updateMessage("Hold on(Vacuum running)...");

				long bfL = basicFile.length(), jfL;
				// Update Message
				updateMessage("Before:" + IOInfo.getFileSizeEdited(basicFile) + "  After:"
						+ IOInfo.getFileSizeEdited(journalFile));

				// Wait until it is created
				while (!journalFile.exists())
					Thread.sleep(50);

				// creating process...
				while ((jfL = journalFile.length()) < bfL) {

					// Update Message
					updateMessage("Before:" + IOInfo.getFileSizeEdited(basicFile) + "  After:"
							+ IOInfo.getFileSizeEdited(journalFile));

					// Update Progress
					updateProgress(jfL, bfL);

					// Sleep
					Thread.sleep(50);
				}

				// Update Message
				updateMessage("Terminating..");

				// System.out.println("Exited Vacuum Progress Service")
				return null;
			}

		};
	}

}
