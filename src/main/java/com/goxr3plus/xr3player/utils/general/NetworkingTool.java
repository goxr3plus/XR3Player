package main.java.com.goxr3plus.xr3player.utils.general;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.com.goxr3plus.xr3player.application.Main;

public final class NetworkingTool {

	private NetworkingTool() {}

	/**
	 * Checks for Application Internet connection using Socket and InetSocketAddress
	 * I combine this method with reachableByPing to check if the Operating System
	 * is connected to the Internet and this method to check if the application can
	 * be connected to Internet,
	 * 
	 * Because the admin my have blocked internet connection for this Java
	 * application.
	 * 
	 * @param host the host
	 * @param port the port
	 * @return <b> true </b> if Connected on Internet,<b> false </b> if not.
	 */
	@Deprecated
	public static boolean isReachableUsingSocket(final String host, final int port) {
		final InetSocketAddress addr = new InetSocketAddress(host, port);
	
		// Check if it can be connected
		try (Socket sock = new Socket()) {
			sock.connect(addr, 2000);
			return true;
		} catch (final IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
			return false;
		}
	}

	/**
	 * Checks if a web site is reachable using ping command.
	 *
	 * @param host the host
	 * @return <b> true </b> if Connected on Internet,<b> false </b> if not.
	 */
	public static boolean isReachableByPing(final String host) {
		try {
	
			// Start a new Process
			final Process process = Runtime.getRuntime().exec("ping -"
					+ (System.getProperty("os.name").toLowerCase().startsWith("windows") ? "n" : "c") + " 1 " + host);
	
			// Wait for it to finish
			process.waitFor();
	
			// Check the return value
			return process.exitValue() == 0;
	
		} catch (final Exception ex) {
			Logger.getLogger(Main.class.getName()).log(Level.INFO, null, ex);
			return false;
		}
	}

}
