package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BrightnessManager {

    public static void main(String[] args) {
	try {
	    new BrightnessManager().setBrightness(60);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * Adjusts the brightness of the computer screen
     * 
     * @param brightness
     * @throws IOException
     */
    public static void setBrightness(int brightness) throws IOException {
	// Creates a powerShell command that will set the brightness to the
	// requested value (0-100), after the requested delay (in milliseconds)
	// has passed.
	String s = String.format("$brightness = %d;", brightness) + "$delay = 0;"
		+ "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
		+ "$myMonitor.wmisetbrightness($delay, $brightness)";
	String command = "powershell.exe  " + s;
	// Executing the command
	Process powerShellProcess = Runtime.getRuntime().exec(command);

	powerShellProcess.getOutputStream().close();

	// Report any error messages
	String line;

	BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
	line = stderr.readLine();
	if (line != null) {
	    System.err.println("Standard Error:");
	    do {
		System.err.println(line);
	    } while ((line = stderr.readLine()) != null);

	}
	stderr.close();

    }
}