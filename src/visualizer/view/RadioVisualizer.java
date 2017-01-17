/*
 * 
 */
package visualizer.view;

import java.util.Map;

import aaaradio_not_used_yet.RadioPlayer;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import streamplayer.StreamPlayerEvent;
import streamplayer.StreamPlayerListener;
import visualizer.model.VisualizerModel;

// TODO: Auto-generated Javadoc
/**
 * The Class RadioVisualizer.
 */
public class RadioVisualizer extends Visualizer implements StreamPlayerListener {
	
	/** The radio player. */
	RadioPlayer radioPlayer;
	
	/**
	 * Instantiates a new radio visualizer.
	 *
	 * @param width the width
	 * @param height the height
	 * @param radioPlayer the radio player
	 */
	public RadioVisualizer(int width, int height, RadioPlayer radioPlayer) {
		super("RADIOPLAYER");
		this.radioPlayer = radioPlayer;
		
		radioPlayer.addStreamPlayerListener(this);
	//	resizeVisualizer(width, height);
		
		addMouseListener();
	}
	
	/* (non-Javadoc)
	 * @see streamplayer.StreamPlayerListener#opened(java.lang.Object, java.util.Map)
	 */
	@Override
	public void opened(Object dataSource , Map<String,Object> map) {
		// some code here
	}
	
	/* (non-Javadoc)
	 * @see streamplayer.StreamPlayerListener#progress(int, long, byte[], java.util.Map)
	 */
	@Override
	public void progress(int nEncodedBytes , long microsecondPosition , byte[] pcm , Map<String,Object> properties) {
		writeDSP(pcm);
		if (properties.containsKey("mp3.shoutcast.metadata.StreamTitle")) {
			String shoutTitle = ( (String) properties.get("mp3.shoutcast.metadata.StreamTitle") ).trim();
			System.out.println(shoutTitle);
		} else {
			// System.out.println("Not Containing
			// mp3.shoutcast.metadata.StreamTitle");
		}
		
		if (properties.containsKey("mp3.equalizer")) {
			// System.out.println("Contains mp3.equalizer");
		}
		
		// properties.keySet().forEach(key -> {
		// System.out.println(key + ":" + properties.get(key));
		// });
		System.out.println("\n\n\n\n\nProperties");
		
	}
	
	/* (non-Javadoc)
	 * @see streamplayer.StreamPlayerListener#statusUpdated(streamplayer.StreamPlayerEvent)
	 */
	@Override
	public void statusUpdated(StreamPlayerEvent event) {
		if (radioPlayer.isOpened() && radioPlayer.getSourceDataLine() != null) { // Situation==PLAYING
			
			setupDSP(radioPlayer.getSourceDataLine());
			startDSP(radioPlayer.getSourceDataLine());
			
			// Start Visualizer Animation
			super.startVisualizer();
			Platform.runLater(() -> {
				// extreme.show();
				// extreme.animation.start();
			});
			
		} else if (radioPlayer.isStopped()) { // Situation==STOPPED
			
			stopDSP();
			clear();
			
			// Stop visualizer Service
			super.stopVisualizer();
			// extreme.animation.stop();
			// xPlayer.playAList.next();
			
		}
	}
	
}
