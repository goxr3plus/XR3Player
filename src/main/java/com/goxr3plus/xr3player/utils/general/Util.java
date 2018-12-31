package main.java.com.goxr3plus.xr3player.utils.general;

import javafx.application.Platform;
import main.java.com.goxr3plus.xr3player.application.Main;

public class Util {
	
	private static OperatingSystem os = null;
	private static final String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();
	
	public static OperatingSystem getOS() {
		if (os == null) {
			if (OPERATING_SYSTEM.contains("win"))
				os = OperatingSystem.WINDOWS;
			else if (OPERATING_SYSTEM.contains("nix") || OPERATING_SYSTEM.contains("nux") || OPERATING_SYSTEM.contains("aix")) {
				os = OperatingSystem.LINUX;
			} else if (OPERATING_SYSTEM.contains("mac")) {
				os = OperatingSystem.MAC;
			}else if (OPERATING_SYSTEM.contains("sunos")) {
				os = OperatingSystem.SOLARIS;
			}
		}
		return os;
	}
	
	public static boolean isWindows() {
		return OPERATING_SYSTEM.contains("win");
	}
	
	public static boolean isLinux() {
		return ( OPERATING_SYSTEM.contains("nix") || OPERATING_SYSTEM.contains("nux") || OPERATING_SYSTEM.contains("aix") );
	}
	
	public static boolean isMac() {
		return OPERATING_SYSTEM.contains("mac");
	}
	
	public static boolean isSolaris() {
		return OPERATING_SYSTEM.contains("sunos");
	}
	
	/**
	 * Use this code to terminate XR3Player
	 * 
	 * @param code
	 */
	public static void terminateXR3Player(final int code) {
		
		System.out.println("Dis All->" + Util.getOS());
		switch (Util.getOS()) {
			case WINDOWS:
				new Thread(() -> {
					//Disposing all Browsers...
					Main.webBrowser.disposeAllBrowsers();
					System.exit(code);
				}).start();
				break;
			case LINUX:
			case MAC:
				Platform.runLater(() -> {
					//Disposing all Browsers...
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
