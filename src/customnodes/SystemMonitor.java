package customnodes;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import visualizer.model.ResizableCanvas;

/**
 * This class is running a background class which updates every 1000 milliseconds the Canvas based on the CPULOAD.
 *
 * @author GOXR3PLUS
 */
public class SystemMonitor extends ResizableCanvas {

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
     * Colors to be used for paint
     */
    private final Color[] colors = { Color.BLACK, Color.FIREBRICK, Color.WHITE };

    /**
     * Constructor
     * 
     * @param monitor
     *            The category of Monitor
     */
    public SystemMonitor(Monitor monitor) {
	this.monitor = monitor;
	gc.setFont(Font.font("default", FontWeight.BOLD, 14));
	// startUpdater()
	repaint();
    }

    /**
     * Paint usage.
     */
    private void repaint() {
	gc.clearRect(0, 0, getWidth(), getHeight());

	// Outline
	gc.setFill(colors[0]);
	gc.fillRect(0, 0, getWidth(), getHeight());

	// Inner progress
	//gc.setStroke(Color.FIREBRICK);
	//if (monitor == Monitor.CPU)
	gc.setFill(colors[1]);
	//else if (monitor == Monitor.RAM)
	//     gc.setFill(Color.web("#FF4A00"));

	// usage=80
	//	if (usage % 2 == 0)
	//	    for (int a = 1; a <= usage + usage / 2; a += 2)
	//		gc.strokeRect(a, 3, 1, getHeight() - 6);
	//	else
	//	    for (int a = 1; a <= usage + usage / 2; a += 2) {
	//		if (a == usage + usage / 2)
	//		    gc.strokeRect(a, 3, 0.5, getHeight() - 6);
	//		else
	//		    gc.strokeRect(a, 3, 1, getHeight() - 6);
	//	    }

	//	float saColorScale = (float) VisualizerModel.spectrumAnalyserColors.length / (float) (getWidth()/1.2)  * 1.0f;
	//	float c = 0;
	//	for (int a = 1; a<(usageDouble/100.00)*(getWidth()-2); a++) {
	//	    c += saColorScale;
	//	    if (c < VisualizerModel.spectrumAnalyserColors.length)
	//		gc.setFill(VisualizerModel.spectrumAnalyserColors[(int) c]);
	//
	//	    gc.fillRect(a, 3, 1, getHeight() - 6);
	//	}

	gc.fillRect(1, 3, (usage / 100.00) * (getWidth() - 2), getHeight() - 6);
	//gc.fillRect(1, 3, (usageDouble/100.00)*(getWidth()-2), getHeight() - 6)

	// Show the Progress on String format
	gc.setFill(colors[2]);
	if (monitor == Monitor.CPU)
	    gc.fillText(String.format("CPU %d ", usage) + "%", getWidth() / 2 - 30, getHeight() / 2 + 5);
	else if (monitor == Monitor.RAM)
	    gc.fillText(String.format("RAM %d ", usage) + "%", getWidth() / 2 - 30, getHeight() / 2 + 5);

	//	gc.fillText(String.format("%.2f ", (usageDouble < 0) ? 0 : usageDouble) + "%", getWidth() / 2 - 15,
	//		getHeight() / 2 + 5)

    }

    /**
     * Starts the Background Update Service
     */
    public void restartUpdater() {
	run = true;
	getUpdateService().restart();
	Platform.runLater(SystemMonitor.this::repaint);
    }

    /**
     * Stops the Background Update Service
     */
    public void stopUpdater() {
	run = false;
	getUpdateService().cancel();
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

		    //Monitor the CPU
		    if (monitor == Monitor.CPU) {
			CpuTimes cpu;

			// Loop
			while (run) {

			    cpu = javaSysMon.cpuTimes();
			    Thread.sleep(999);
			    usageDouble = javaSysMon.cpuTimes().getCpuUsage(cpu) * 100.00;
			    // usageDouble = 100
			    usage = (int) usageDouble;
			    Platform.runLater(SystemMonitor.this::repaint);
			}

			System.out.println("CPU Update Service Exited");
		    }
		    //Monitor the RAM
		    else if (monitor == Monitor.RAM) {
			// Loop
			while (run) {

			    Thread.sleep(999);
			    // usageDouble = 
			    // usageDouble = 100
			    MemoryStats memory = javaSysMon.physical();
			    //System.out.println("Total: "+memory.getTotalBytes()+" ,Free: "+memory.getFreeBytes())
			    usage = 100 - (int) (((memory.getFreeBytes() * 100.00) / memory.getTotalBytes()));
			    Platform.runLater(SystemMonitor.this::repaint);
			}

			System.out.println("RAM Update Service Exited");
		    }

		    return null;
		}
	    };
	}

    }
}
