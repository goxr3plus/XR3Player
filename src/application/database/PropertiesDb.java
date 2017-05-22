/**
 * 
 */
package application.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.tools.ActionTool;
import application.tools.InfoTool;
import application.tools.NotificationType;
import javafx.util.Duration;

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
	
	/**
	 * Using this variable when i when to prevent update of properties happen
	 */
	private boolean updatePropertiesLocked = true;
	
	/**
	 * Constructor
	 * 
	 * @param localDbManager
	 */
	public PropertiesDb() {
		properties = new Properties();
	}
	
	/**
	 * Updates or Creates the given key , warning also updateProperty can be
	 * locked , if you want to unlock it or check if locked
	 * check the method is `isUpdatePropertyLocked()`
	 * 
	 * @param key
	 * @param value
	 */
	public void updateProperty(String key , String value) {
		if (updatePropertiesLocked)
			return;
		
		String propertiesAbsolutePath = InfoTool.getAbsoluteDatabasePathWithSeparator() + "config.properties";
		System.out.println("Updating Property!");
		
		//Check if exists [ Create if Not ] 
		if (!new File(propertiesAbsolutePath).exists())
			try {
				new File(propertiesAbsolutePath).createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		
		//Submit it to the executors Service
		updateExecutorService.submit(() -> {
			try (InputStream inStream = new FileInputStream(propertiesAbsolutePath); OutputStream outStream = new FileOutputStream(propertiesAbsolutePath)) {
				
				//load  properties
				properties.load(inStream);
				
				// set the properties value
				properties.setProperty(key, value);
				
				// save properties 
				properties.store(outStream, null);
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}
	
	/**
	 * Loads the Properties
	 */
	void loadProperties() {
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
	
	/**
	 * Check if properties update is locked
	 * 
	 * @return the canUpdateProperty
	 */
	public boolean isUpdatePropertiesLocked() {
		return updatePropertiesLocked;
	}
	
	/**
	 * Lock or unlock the update of properties
	 * 
	 * @param canUpdateProperty the canUpdateProperty to set
	 */
	public void setUpdatePropertiesLocked(boolean updatePropertiesLocked) {
		this.updatePropertiesLocked = updatePropertiesLocked;
	}
	
}
