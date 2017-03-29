/*
 * 
 */
package remote_communication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * A Window that shows the results of speech recognizer.
 *
 * @author GOXR3PLUS
 */
public class SpeechWindow extends Stage {
	
	/** The container. */
	private Container container = new Container();
	
	/**
	 * Constructor.
	 */
	public SpeechWindow() {
		
		setTitle("SpeechResults");
		initOwner(Main.window);
		setScene(new Scene(container));
	}
	
	/**
	 * Append text.
	 *
	 * @param text the text
	 */
	public void appendText(String text) {
		container.appendText(text);
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		container.clear();
	}
	
	/**
	 * The Class Container.
	 *
	 * @author GOXR3PLUS
	 */
	private class Container extends BorderPane implements Initializable {
		
		/** The text area. */
		@FXML
		private TextArea textArea;
		
		/**
		 * Instantiates a new container.
		 */
		public Container() {
			// FXMLLOADER
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SpeechWindow.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			try {
				loader.load();
			} catch (IOException ex) {
				// throw as cause of RuntimeException
				throw new IllegalStateException(ex);
			}
		}
		
		/* (non-Javadoc)
		 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
		 */
		@Override
		public void initialize(URL location , ResourceBundle resources) {
			System.out.println("Speech_Window Container has been initialized..");
		}
		
		/**
		 * Append text.
		 *
		 * @param text the text
		 */
		public void appendText(String text) {
			Platform.runLater(() -> textArea.appendText(text + "\n"));
		}
		
		/**
		 * Clear.
		 */
		public void clear() {
			Platform.runLater(textArea::clear);
		}
		
	}
	
}
