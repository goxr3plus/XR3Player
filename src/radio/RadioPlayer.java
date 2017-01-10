/*
 * 
 */
package radio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import streamplayer.StreamPlayer;
import streamplayer.StreamPlayerException;
import visualizer.view.RadioVisualizer;

// TODO: Auto-generated Javadoc
/**
 * The Class RadioPlayer.
 */
public class RadioPlayer extends StreamPlayer {

	/** The url connection. */
	private URLConnection urlConnection;

	/** The visualizer. */
	RadioVisualizer visualizer = new RadioVisualizer(200, 45, this);

	/**
	 * Stops the radio Stream.
	 */
	public void stopRadioStream() {
		super.stop();
	}

//	@Override
//	public boolean resume() {
//		super.resume();
//		visualizer.startVisualizer();
//	}
//
//	@Override
//	public void pause() {
//		super.pause();
//		visualizer.stopVisualizer();
//	}

	/**
 * Auto Controlls the RadioPlayer.
 *
 * @return the string
 */
	public String autoControllPlayer() {
		if (isPaused()) {
			resume();
			return "r";
		} else if (isPlaying()) {
			pause();
			return "p";
		}

		return "null";
	}

	/**
	 * Opens the radio Stream in a new Thread.
	 *
	 * @param url the url
	 */
	public void playRadioStream(URL url) {

		// MainWindow.stationsManager.bottomBar.indicator.setVisible(true);
		new Thread(() -> {
			try {
				// Connection
				// urlConnection = url.openConnection();

				// If you have proxy
				// Properties systemSettings = System.getProperties ();
				// systemSettings.put ( "proxySet", true );
				// systemSettings.put ( "http.proxyHost", "host" );
				// systemSettings.put ( "http.proxyPort", "port" );
				// If you have proxy auth
				// BASE64Encoder encoder = new BASE64Encoder ();
				// String encoded = encoder.encode ( ( "login:pass" ).getBytes
				// () );
				// urlConnection.setRequestProperty ( "Proxy-Authorization",
				// "Basic
				// " +
				// encoded );

				
				//Using URL
				// Connecting
				// urlConnection.connect();
				// Opening
				// open((urlConnection.getInputStream()));

				Socket connection = new Socket("50.7.98.106", 8398);

				String request = "GET / HTTP/1.1\n\n";
				OutputStream out = connection.getOutputStream();
				out.write(request.getBytes(StandardCharsets.US_ASCII));
				out.flush();

				InputStream response = connection.getInputStream();

				// Skip headers until we read a blank line.
				/*
				 * int lineLength; do { lineLength = 0; for (int b =
				 * response.read(); b >= 0 && b != '\n'; b = response.read()) {
				 * lineLength++; } } while (lineLength > 0);
				 */

				System.out.println("Opening response....");
				open(response);
				play();
				Platform.runLater(() -> {
					// MainWindow.stationsManager.bottomBar.setPauseGraphic();
				});
			} catch (StreamPlayerException | IOException e) {
				e.printStackTrace();
				stop();
				Platform.runLater(() -> {
					// MainWindow.stationsManager.bottomBar.setResumeGraphic();
					Alert alert = new Alert(AlertType.ERROR, e.getMessage());
					alert.showAndWait();
				});
			} finally {
				// MainWindow.stationsManager.bottomBar.indicator.setVisible(false);
			}

		}).start();
	}

}
