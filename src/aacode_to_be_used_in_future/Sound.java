/*
 * 
 */
package aacode_to_be_used_in_future;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javazoom.jl.player.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class Sound.
 */
public class Sound {

	/**
	 * The main method.
	 *
	 * @param arg the arguments
	 */
	public static void main(String arg[]) {
		
		System.out.println("Preparing to start...");
		
		try (Socket connection = new Socket("50.7.98.106", 8398)) {
			
			String request = "GET / HTTP/1.1\n\n";
			OutputStream out = connection.getOutputStream();
			out.write(request.getBytes(StandardCharsets.US_ASCII));
			out.flush();

			InputStream response = connection.getInputStream();

			// Skip headers until we read a blank line.
			/*int lineLength;
			do {
				lineLength = 0;
				for (int b = response.read(); b >= 0 && b != '\n'; b = response.read()) {
					lineLength++;
				}
			} while (lineLength > 0);*/

			System.out.println("Opening response....");
			Player playMP3 = new Player(response);
			System.out.println("Preparing to play...");
			playMP3.play();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}