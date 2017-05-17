package customnodes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;

import application.tools.InfoTool;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

/**
 * This class is running a background class which updates every 1000 milliseconds the Canvas based on the CPULOAD.
 *
 * @author GOXR3PLUS
 */
public class SystemMonitor extends StackPane {

    //-----------------------------------------------------

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    // -------------------------------------------------------------

    /** The logger. */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * What to Monitor?
     * 
     * @author GOXR3PLUS
     *
     */
    public enum Monitor {
	/**
	 * Monitor the CPU
	 */
	CPU,
	/**
	 * Monitor the RAM
	 */
	RAM;
    }

    /** The monitor. */
    private Monitor monitor;

    /** The monitor. */
    private JavaSysMon javaSysMon = new JavaSysMon();

    /** The usage. */
    private int usage;

    /** The usage double. */
    private double usageDouble;

    /**
     * The Background Update Service Thread
     */
    private UpdateService updateService = new UpdateService();

    /**
     * When true the updateService can run;
     */
    private volatile boolean run;

    /**
     * Constructor
     * 
     * @param monitor
     *            The category of Monitor
     */
    public SystemMonitor(Monitor monitor) {
	this.monitor = monitor;

	// ------------------------------------FXMLLOADER ----------------------------------------
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SystemMonitor.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}

    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {

	//ProgressLabel
	progressLabel.setText("Click for " + (monitor == Monitor.CPU ? "CPU" : "RAM") + " Usage");
    }

    /**
     * Starts the Background Update Service
     */
    public void restartUpdater() {
	run = true;
	getUpdateService().restart();

	//ProgressLabel
	progressLabel.textProperty().bind(Bindings.createStringBinding(
		() -> String.format("%s %d %%", monitor == Monitor.CPU ? "CPU" : "RAM", progressBar.progressProperty().multiply(100.00).intValue()),
		progressBar.progressProperty()));
    }

    /**
     * Stops the Background Update Service
     */
    public void stopUpdater() {
	run = false;
	getUpdateService().cancel();

	//ProgressLabel
	progressLabel.textProperty().unbind();
	progressLabel.setText("Click for " + (monitor == Monitor.CPU ? "CPU" : "RAM") + " Usage");

	//ProgressBar
	progressBar.setProgress(0);
    }

    /**
     * True if the Updater is running or False if not
     * 
     * @return True if the Updater is running or False if not
     */
    public boolean isRunning() {
	return getUpdateService().isRunning();
    }

    /**
     * @return the updateService
     */
    public UpdateService getUpdateService() {
	return updateService;
    }

    /**
     * @return the progressLabel
     */
    public Label getProgressLabel() {
	return progressLabel;
    }

    /**
     * Updates the Canvas every 1 millisecond with the latest cpu load
     * 
     * @author GOXR3PLUS
     *
     */
    public class UpdateService extends Service<Void> {

	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {

		@Override
		protected Void call() throws Exception {

		    // Loop
		    while (run) {

			//Monitor the CPU
			if (monitor == Monitor.CPU) {
			    CpuTimes cpu;

			    cpu = javaSysMon.cpuTimes();
			    Thread.sleep(999);
			    usageDouble = javaSysMon.cpuTimes().getCpuUsage(cpu); //* 100.00;
			    usage = (int) usageDouble;
			    Platform.runLater(() -> progressBar.setProgress(usageDouble));

			    //System.out.println("CPU Update Service Exited");
			}
			//Monitor the RAM
			else if (monitor == Monitor.RAM) {

			    Thread.sleep(999);
			    MemoryStats memory = javaSysMon.physical();
			    //System.out.println("Total: "+memory.getTotalBytes()+" ,Free: "+memory.getFreeBytes())
			    //usage = 100 - (int) (((memory.getFreeBytes() * 100.00) / memory.getTotalBytes()));
			    usageDouble = 1.00 - ((memory.getFreeBytes() * 100.00) / memory.getTotalBytes()) / 100;
			    // System.out.println(usageDouble);
			    Platform.runLater(() -> progressBar.setProgress(usageDouble));
			}

		    }

		    System.out.println("Update Service Exited");
		    Platform.runLater(() -> progressBar.setProgress(0));

		    return null;
		}
	    };
	}

    }
}
