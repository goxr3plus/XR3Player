/**
 * 
 */
package aaaatestcode;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * JavaFX Service which is Capable of Downloading Files from the Internet to the
 * LocalHost
 * 
 * @author GOXR3PLUS
 *
 */
public class DownloadService extends Service<Boolean> {

    // -----
    private long totalBytes;
    private boolean succeeded = false;
    private volatile boolean stopThread;

    // CopyThread
    private Thread copyThread = null;

    // ----
    private String urlString;
    private String destination;

    /**
     * The logger of the class
     */
    private static final Logger LOGGER = Logger.getLogger(DownloadService.class.getName());

    /**
     * Constructor
     */
    public DownloadService() {
	
	setOnSucceeded(s -> {
	    System.out.println("Succeeded with value: " + super.getValue()+" , Copy Thread is Alive? "+copyThread.isAlive());
	    done();
	});
	
	setOnFailed(f ->{
	    System.out.println("Failed with value: " + super.getValue()+" , Copy Thread is Alive? "+copyThread.isAlive());
	    done();
	});
	
	setOnCancelled(c ->  {
	    System.out.println("Cancelled with value: " + super.getValue()+" , Copy Thread is Alive? "+copyThread.isAlive());
	    done();
	});
    }
    
    /**The Service is done
     * @param value
     */
    private boolean done() {
	
	boolean fileDeleted = false;
	
	//Check if The Service Succeeded 
	if(!succeeded)
	   fileDeleted = new File(destination).delete();
	
	return fileDeleted;
    }

    /**
     * Start the Download Service
     * 
     * @param urlString
     *            The source File URL
     * @param destination
     *            The destination File
     */
    public void startDownload(String urlString, String destination) {
	if (!isRunning()) {
	    this.urlString = urlString;
	    this.destination = destination;
	    totalBytes = 0;
	    restart();
	}
    }

    @Override
    protected Task<Boolean> createTask() {
	return new Task<Boolean>() {
	    @Override
	    protected Boolean call() throws Exception {

		// Succeeded boolean
		succeeded = true;

		// URL and LocalFile
		URL urlFile = new URL(java.net.URLDecoder.decode(urlString, "UTF-8"));
		File destinationFile = new File(destination);

		try {
		    // Open the connection and get totalBytes
		    URLConnection connection = urlFile.openConnection();
		    totalBytes = Long.parseLong(connection.getHeaderField("Content-Length"));
		    
		    
		    
		    

		    // --------------------- Copy the File to External Thread-----------
		    copyThread = new Thread(() -> {

			// Start File Copy
			try (FileChannel zip = FileChannel.open(destinationFile.toPath(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {

			    zip.transferFrom(Channels.newChannel(connection.getInputStream()), 0, Long.MAX_VALUE);

			    
			    // Files.copy(dl.openStream(), fl.toPath(),StandardCopyOption.REPLACE_EXISTING)

			} catch (Exception ex) {
			    stopThread = true;
			    LOGGER.log(Level.WARNING, "DownloadService failed", ex);
			}

			System.out.println("Copy Thread exited...");
		    });
		    // Set to Daemon
		    copyThread.setDaemon(true);
		    // Start the Thread
		    copyThread.start();
		    // -------------------- End of Copy the File to External Thread-------
		    
		    
		    
		    
		    

		    // ---------------------------Check the %100 Progress--------------------
		    long outPutFileLength;
		    long previousLength = 0;
		    int failCounter = 0;
		    // While Loop
		    while ((outPutFileLength = destinationFile.length()) < totalBytes && !stopThread) {

			// Check the previous length
			if (previousLength != outPutFileLength) {
			    previousLength = outPutFileLength;
			    failCounter = 0;
			} else
			    ++failCounter;

			// 2 Seconds passed without response
			if (failCounter == 40 || stopThread)
			    break;

			// Update Progress
			super.updateProgress((outPutFileLength * 100) / totalBytes, 100);
			System.out.println("Current Bytes:" + outPutFileLength + " ,|, TotalBytes:" + totalBytes
				+ " ,|, Current Progress: " + (outPutFileLength * 100) / totalBytes + " %");

			// Sleep
			try {
			    Thread.sleep(50);
			} catch (InterruptedException ex) {
			    LOGGER.log(Level.WARNING, "", ex);
			}
		    }

		    // 2 Seconds passed without response
		    if (failCounter == 40)
			succeeded = false;
		   // --------------------------End of Check the %100 Progress--------------------

		} catch (Exception ex) {
		    succeeded = false;
		    // Stop the External Thread which is updating the %100
		    // progress
		    stopThread = true;
		    LOGGER.log(Level.WARNING, "DownloadService failed", ex);
		}
		
		
		
		
		
		
		
		//----------------------Finally------------------------------

		System.out.println("Trying to interrupt[shoot with an assault rifle] the copy Thread");

		// ---FORCE STOP COPY FILES
		if (!succeeded && copyThread != null && copyThread.isAlive()) {
		    copyThread.interrupt();
		    System.out.println("Done an interrupt to the copy Thread");

		    // Run a Looping checking if the copyThread has stopped...
		    while (copyThread.isAlive()) {
			System.out.println("Copy Thread is still Alive,refusing to die.");
			Thread.sleep(50);
		    }
		}

		System.out.println("Download Service exited:[Value=" + succeeded + "] Copy Thread is Alive? "
			+ (copyThread == null ? "" : copyThread.isAlive()));
		
		//---------------------- End of Finally------------------------------
		
		


		return succeeded;
	    }

	};
    }

}
