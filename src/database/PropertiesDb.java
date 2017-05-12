/**
 * 
 */
package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.util.Duration;
import tools.ActionTool;
import tools.InfoTool;
import tools.NotificationType;

/**
 * This class is saving all the XR3Player Settings to a property file
 * 
 * @author GOXR3PLUS
 *
 */
public class PropertiesDb {

    private final Properties properties;

    /** This executor does the commit job. */
    private static final ExecutorService updateExecutorService = Executors.newSingleThreadExecutor();

    DbManager localDbManager;

    /**
     * Constructor
     * 
     * @param localDbManager
     */
    public PropertiesDb(DbManager localDbManager) {
	this.localDbManager = localDbManager;
	properties = new Properties();

    }

    /**
     * Updates or Creates the given key
     * 
     * @param key
     * @param value
     */
    public void updateProperty(String key, String value) {
	String propertiesAbsolutePath = InfoTool.getAbsoluteDatabasePathWithSeparator() + "config.properties";

	//Check if exists [ Create if Not ] 
	if (!new File(propertiesAbsolutePath).exists())
	    try {
		new File(propertiesAbsolutePath).createNewFile();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }

	//Submit it to the executors Service
	updateExecutorService.submit(() -> {
	    try (InputStream inStream = new FileInputStream(propertiesAbsolutePath);
		    OutputStream outStream = new FileOutputStream(propertiesAbsolutePath)) {

		//load  properties
		properties.load(inStream);

		// set the properties value
		properties.setProperty(key, value);

		// save properties 
		properties.store(outStream, null);

	    } catch (IOException ex) {
		ex.printStackTrace();
	    } finally {
		//  if (showNotifications)
		ActionTool.showNotification("Properties Updated", "Changes saved successfully", Duration.millis(550), NotificationType.INFORMATION);
	    }
	});
    }

    /**
     * Loads the Properties
     */
    public void loadProperties() {
	String propertiesAbsolutePath = InfoTool.getAbsoluteDatabasePathWithSeparator() + "config.properties";

	//Check if exists [ Create if Not ] 
	if (!new File(propertiesAbsolutePath).exists())
	    try {
		new File(propertiesAbsolutePath).createNewFile();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }

	//Load the properties file
	try (InputStream inStream = new FileInputStream(propertiesAbsolutePath)) {

	    //load  properties
	    properties.load(inStream);

	} catch (IOException ex) {
	    ex.printStackTrace();
	}

    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
	return properties;
    }

}
