package customnodes;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import visualizer.model.ResizableCanvas;

/**
 * This class is running a background class which updates every 1000
 * milliseconds the Canvas based on the CPULOAD.
 *
 * @author GOXR3PLUS
 */
public class CPUsage extends ResizableCanvas {

    /** The monitor. */
    private JavaSysMon monitor = new JavaSysMon();

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
     */
    public CPUsage() {
	gc.setFont(Font.font("default", FontWeight.BOLD, 14));
	// startUpdater()

    }

    /**
     * Paint usage.
     */
    private void repaint() {
	gc.clearRect(0, 0, getWidth(), getHeight());

	// Outline
	gc.setFill(Color.BLACK);
	gc.fillRect(0, 0, getWidth(), getHeight());

	// Inner progress
	//gc.setStroke(Color.FIREBRICK);
	gc.setFill(Color.CORNFLOWERBLUE);

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
	
	gc.fillRect(1, 3, (usage/100.00)*(getWidth()-2), getHeight() - 6);
	//gc.fillRect(1, 3, (usageDouble/100.00)*(getWidth()-2), getHeight() - 6)

	// Show the Progress on String format
	gc.setFill(Color.WHITE);
	gc.fillText(String.format("%d ", usage) + "%", getWidth() / 2 - 15, getHeight() / 2 + 5);
	
//	gc.fillText(String.format("%.2f ", (usageDouble < 0) ? 0 : usageDouble) + "%", getWidth() / 2 - 15,
//		getHeight() / 2 + 5)

    }

    /**
     * Starts the Background Update Service
     */
    public void startUpdater() {
	run = true;
	updateService.restart();
    }

    /**
     * Stops the Background Update Service
     */
    public void stopUpdater() {
	run = false;
	updateService.cancel();
    }

    /**
     * Updates the Canvas every 1 millisecond with the latest cpu load
     * 
     * @author GOXR3PLUS
     *
     */
    private class UpdateService extends Service<Void> {

	@Override
	protected Task<Void> createTask() {
	    return new Task<Void>() {

		@Override
		protected Void call() throws Exception {

		    CpuTimes cpu;

		    // Loop
		    while (run) {

			cpu = monitor.cpuTimes();
			Thread.sleep(999);
			usageDouble = monitor.cpuTimes().getCpuUsage(cpu) * 100.00;
			// usageDouble = 100
			usage = (int) usageDouble;
			Platform.runLater(CPUsage.this::repaint);

		    }

		    System.out.println("CPU Update Service Exited");

		    return null;
		}
	    };
	}

    }
}
