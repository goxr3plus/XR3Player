package main.java.com.goxr3plus.xr3player.utils.general;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.com.goxr3plus.xr3player.application.Main;

final class NetworkingTool {

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

}
