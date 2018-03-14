package main.java.com.goxr3plus.xr3player.application.tools;

import com.teamdev.jxbrowser.chromium.Browser;

import javafx.application.Platform;
import main.java.com.goxr3plus.xr3player.application.Main;
import main.java.com.goxr3plus.xr3player.chromium.WebBrowserTabController;

public class Util {
	
	public enum OS {
		WINDOWS, LINUX, MAC, SOLARIS
	}// Operating systems.
	
	private static OS os = null;
	private static final String operSys = System.getProperty("os.name").toLowerCase();
	
	public static OS getOS() {
		if (os == null) {
			if (operSys.contains("win"))
				os = OS.WINDOWS;
			else if (operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix")) {
				os = OS.LINUX;
			} else if (operSys.contains("mac"))
				os = OS.MAC;
			else if (operSys.contains("sunos"))
				os = OS.SOLARIS;
		}
		return os;
	}
	
	public static boolean isWindows() {
		return operSys.contains("win");
	}
	
	public static boolean isLinux() {
		return ( operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix") );
	}
	
	public static boolean isMac() {
		return operSys.contains("mac");
	}
	
	public static boolean isSolaris() {
		return operSys.contains("sunos");
	}
	
	/**
	 * Use this code to terminate XR3Player
	 * 
	 * @param code
	 */
	public static void terminateXR3Player(int code) {
		
		System.out.println("Dis All->" + Util.getOS());
		switch (Util.getOS()) {
			case WINDOWS:
				new Thread(() -> {
					Main.webBrowser.disposeAllBrowsers();
					System.exit(code);
				}).start();
				break;
			case LINUX:
			case MAC:
				Platform.runLater(() -> {
					Main.webBrowser.disposeAllBrowsers();
					System.exit(code);
				});
				break;
			default:
				System.out.println("Can't dispose browser instance!!!");
				break;
		}
		
	}
}
