package aaTesterOnlyCode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesTesting {
    Properties properties = new Properties();

    public PropertiesTesting() {
	writeSomething();
	writeSomething2();
    }

    public void writeSomething() {

	try (InputStream inStream = new FileInputStream("config.properties"); OutputStream outStream = new FileOutputStream("config.properties")) {

	    properties.load(inStream);

	    // set the properties value

	    properties.setProperty("database", "localhost");
	    properties.setProperty("dbuser", "mkyong");
	    properties.setProperty("dbpassword", "password");
	    
	    System.out.println(properties.getProperty("bitch"));

	    // save properties to project root folder
	    properties.store(outStream, null);

	} catch (IOException io) {
	    io.printStackTrace();
	}

	System.out.println("Write Something finished...");
    }

    public void writeSomething2() {
	try (InputStream inStream = new FileInputStream("config.properties"); OutputStream outStream = new FileOutputStream("config.properties")) {

	    properties.load(inStream);

	    // set the properties value

	    properties.setProperty("database2", "localhost");
	    properties.setProperty("dbuser", "O_O");
	    properties.setProperty("dbpassword3", "password");

	    // save properties to project root folder
	    properties.store(outStream, null);

	} catch (IOException io) {
	    io.printStackTrace();
	}

	System.out.println("Write Something 2 finished...");
    }

    public static void main(String[] args) {

	new PropertiesTesting();

    }

}