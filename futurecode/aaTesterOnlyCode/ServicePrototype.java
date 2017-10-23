/**
 * 
 */
package aaTesterOnlyCode;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * @author GOXR3PLUS
 *
 */
public class ServicePrototype extends Service<Boolean> {

    /**
     * Constructor
     */
    public ServicePrototype() {
	this.setOnSucceeded(s -> {

	});

	this.setOnFailed(f -> {

	});

	this.setOnCancelled(c -> {

	});

    }

    /**
     * Called when Service is done
     */
    private void done() {

    }

    /* (non-Javadoc)
     * @see javafx.concurrent.Service#createTask()
     */
    @Override
    protected Task<Boolean> createTask() {
	return new Task<Boolean>() {
	    @Override
	    protected Boolean call() throws Exception {

		return true;
	    }
	};
    }

}
